package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ChunkRenderWorker implements Runnable {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ChunkRenderDispatcher chunkRenderDispatcher;
   private final RegionRenderCacheBuilder regionRenderCacheBuilder;
   private boolean shouldRun = true;

   public ChunkRenderWorker(ChunkRenderDispatcher chunkRenderDispatcherIn) {
      this(chunkRenderDispatcherIn, (RegionRenderCacheBuilder)null);
   }

   public ChunkRenderWorker(ChunkRenderDispatcher chunkRenderDispatcherIn, @Nullable RegionRenderCacheBuilder regionRenderCacheBuilderIn) {
      this.chunkRenderDispatcher = chunkRenderDispatcherIn;
      this.regionRenderCacheBuilder = regionRenderCacheBuilderIn;
   }

   public void run() {
      while(this.shouldRun) {
         try {
            this.processTask(this.chunkRenderDispatcher.getNextChunkUpdate());
         } catch (InterruptedException var3) {
            LOGGER.debug("Stopping chunk worker due to interrupt");
            return;
         } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Batching chunks");
            Minecraft.getInstance().crashed(Minecraft.getInstance().addGraphicsAndWorldToCrashReport(crashreport));
            return;
         }
      }

   }

   void processTask(final ChunkRenderTask generator) throws InterruptedException {
      generator.getLock().lock();

      try {
         if (!func_223453_b(generator, ChunkRenderTask.Status.PENDING)) {
            return;
         }

         if (!generator.getRenderChunk().func_217674_b()) {
            generator.finish();
            return;
         }

         generator.setStatus(ChunkRenderTask.Status.COMPILING);
      } finally {
         generator.getLock().unlock();
      }

      final RegionRenderCacheBuilder lvt_2_1_ = this.getRegionRenderCacheBuilder();
      generator.getLock().lock();

      try {
         if (!func_223453_b(generator, ChunkRenderTask.Status.COMPILING)) {
            this.func_223450_a(lvt_2_1_);
            return;
         }
      } finally {
         generator.getLock().unlock();
      }

      generator.setRegionRenderCacheBuilder(lvt_2_1_);
      Vec3d lvt_3_1_ = this.chunkRenderDispatcher.func_217671_b();
      float lvt_4_1_ = (float)lvt_3_1_.x;
      float lvt_5_1_ = (float)lvt_3_1_.y;
      float lvt_6_1_ = (float)lvt_3_1_.z;
      ChunkRenderTask.Type lvt_7_1_ = generator.getType();
      if (lvt_7_1_ == ChunkRenderTask.Type.REBUILD_CHUNK) {
         generator.getRenderChunk().rebuildChunk(lvt_4_1_, lvt_5_1_, lvt_6_1_, generator);
      } else if (lvt_7_1_ == ChunkRenderTask.Type.RESORT_TRANSPARENCY) {
         generator.getRenderChunk().resortTransparency(lvt_4_1_, lvt_5_1_, lvt_6_1_, generator);
      }

      generator.getLock().lock();

      try {
         if (!func_223453_b(generator, ChunkRenderTask.Status.COMPILING)) {
            this.func_223450_a(lvt_2_1_);
            return;
         }

         generator.setStatus(ChunkRenderTask.Status.UPLOADING);
      } finally {
         generator.getLock().unlock();
      }

      final CompiledChunk lvt_8_1_ = generator.getCompiledChunk();
      ArrayList lvt_9_1_ = Lists.newArrayList();
      if (lvt_7_1_ == ChunkRenderTask.Type.REBUILD_CHUNK) {
         for(BlockRenderLayer blockrenderlayer : BlockRenderLayer.values()) {
            if (lvt_8_1_.isLayerStarted(blockrenderlayer)) {
               lvt_9_1_.add(this.chunkRenderDispatcher.uploadChunk(blockrenderlayer, generator.getRegionRenderCacheBuilder().getBuilder(blockrenderlayer), generator.getRenderChunk(), lvt_8_1_, generator.getDistanceSq()));
            }
         }
      } else if (lvt_7_1_ == ChunkRenderTask.Type.RESORT_TRANSPARENCY) {
         lvt_9_1_.add(this.chunkRenderDispatcher.uploadChunk(BlockRenderLayer.TRANSLUCENT, generator.getRegionRenderCacheBuilder().getBuilder(BlockRenderLayer.TRANSLUCENT), generator.getRenderChunk(), lvt_8_1_, generator.getDistanceSq()));
      }

      ListenableFuture<List<Void>> listenablefuture = Futures.allAsList(lvt_9_1_);
      generator.addFinishRunnable(() -> {
         listenablefuture.cancel(false);
      });
      Futures.addCallback(listenablefuture, new FutureCallback<List<Void>>() {
         public void onSuccess(@Nullable List<Void> p_onSuccess_1_) {
            ChunkRenderWorker.this.func_223450_a(lvt_2_1_);
            generator.getLock().lock();

            label38: {
               try {
                  if (ChunkRenderWorker.func_223453_b(generator, ChunkRenderTask.Status.UPLOADING)) {
                     generator.setStatus(ChunkRenderTask.Status.DONE);
                     break label38;
                  }
               } finally {
                  generator.getLock().unlock();
               }

               return;
            }

            generator.getRenderChunk().setCompiledChunk(lvt_8_1_);
         }

         public void onFailure(Throwable p_onFailure_1_) {
            ChunkRenderWorker.this.func_223450_a(lvt_2_1_);
            if (!(p_onFailure_1_ instanceof CancellationException) && !(p_onFailure_1_ instanceof InterruptedException)) {
               Minecraft.getInstance().crashed(CrashReport.makeCrashReport(p_onFailure_1_, "Rendering chunk"));
            }

         }
      });
   }

   private static boolean func_223453_b(ChunkRenderTask p_223453_0_, ChunkRenderTask.Status p_223453_1_) {
      if (p_223453_0_.getStatus() != p_223453_1_) {
         if (!p_223453_0_.isFinished()) {
            LOGGER.warn("Chunk render task was {} when I expected it to be {}; ignoring task", p_223453_0_.getStatus(), p_223453_1_);
         }

         return false;
      } else {
         return true;
      }
   }

   private RegionRenderCacheBuilder getRegionRenderCacheBuilder() throws InterruptedException {
      return this.regionRenderCacheBuilder != null ? this.regionRenderCacheBuilder : this.chunkRenderDispatcher.allocateRenderBuilder();
   }

   private void func_223450_a(RegionRenderCacheBuilder p_223450_1_) {
      if (p_223450_1_ != this.regionRenderCacheBuilder) {
         this.chunkRenderDispatcher.freeRenderBuilder(p_223450_1_);
      }

   }

   public void notifyToStop() {
      this.shouldRun = false;
   }
}