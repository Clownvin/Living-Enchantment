package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChunkRenderTask implements Comparable<ChunkRenderTask> {
   private final ChunkRender renderChunk;
   private final ReentrantLock lock = new ReentrantLock();
   private final List<Runnable> listFinishRunnables = Lists.newArrayList();
   private final ChunkRenderTask.Type type;
   private final double distanceSq;
   @Nullable
   private ChunkRenderCache field_217668_f;
   private RegionRenderCacheBuilder regionRenderCacheBuilder;
   private CompiledChunk compiledChunk;
   private ChunkRenderTask.Status status = ChunkRenderTask.Status.PENDING;
   private boolean finished;
   private java.util.Map<net.minecraft.util.math.BlockPos, net.minecraftforge.client.model.data.IModelData> modelData;

   public ChunkRenderTask(ChunkRender p_i50980_1_, ChunkRenderTask.Type p_i50980_2_, double p_i50980_3_, @Nullable ChunkRenderCache p_i50980_5_) {
      this.renderChunk = p_i50980_1_;
      this.type = p_i50980_2_;
      this.distanceSq = p_i50980_3_;
      this.field_217668_f = p_i50980_5_;

      modelData = net.minecraftforge.client.model.ModelDataManager.getModelData(net.minecraft.client.Minecraft.getInstance().world, new net.minecraft.util.math.ChunkPos(p_i50980_1_.getPosition()));
   }

   public ChunkRenderTask.Status getStatus() {
      return this.status;
   }

   public ChunkRender getRenderChunk() {
      return this.renderChunk;
   }

   @Nullable
   public ChunkRenderCache func_217667_c() {
      ChunkRenderCache chunkrendercache = this.field_217668_f;
      this.field_217668_f = null;
      return chunkrendercache;
   }

   public CompiledChunk getCompiledChunk() {
      return this.compiledChunk;
   }

   public void setCompiledChunk(CompiledChunk compiledChunkIn) {
      this.compiledChunk = compiledChunkIn;
   }

   public RegionRenderCacheBuilder getRegionRenderCacheBuilder() {
      return this.regionRenderCacheBuilder;
   }

   public void setRegionRenderCacheBuilder(RegionRenderCacheBuilder regionRenderCacheBuilderIn) {
      this.regionRenderCacheBuilder = regionRenderCacheBuilderIn;
   }

   public void setStatus(ChunkRenderTask.Status statusIn) {
      this.lock.lock();

      try {
         this.status = statusIn;
      } finally {
         this.lock.unlock();
      }

   }

   public void finish() {
      this.lock.lock();

      try {
         this.field_217668_f = null;
         if (this.type == ChunkRenderTask.Type.REBUILD_CHUNK && this.status != ChunkRenderTask.Status.DONE) {
            this.renderChunk.setNeedsUpdate(false);
         }

         this.finished = true;
         this.status = ChunkRenderTask.Status.DONE;

         for(Runnable runnable : this.listFinishRunnables) {
            runnable.run();
         }
      } finally {
         this.lock.unlock();
      }

   }

   public void addFinishRunnable(Runnable runnable) {
      this.lock.lock();

      try {
         this.listFinishRunnables.add(runnable);
         if (this.finished) {
            runnable.run();
         }
      } finally {
         this.lock.unlock();
      }

   }

   public ReentrantLock getLock() {
      return this.lock;
   }

   public ChunkRenderTask.Type getType() {
      return this.type;
   }

   public boolean isFinished() {
      return this.finished;
   }

   public int compareTo(ChunkRenderTask p_compareTo_1_) {
      return Doubles.compare(this.distanceSq, p_compareTo_1_.distanceSq);
   }

   public double getDistanceSq() {
      return this.distanceSq;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Status {
      PENDING,
      COMPILING,
      UPLOADING,
      DONE;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Type {
      REBUILD_CHUNK,
      RESORT_TRANSPARENCY;
   }
   
   public net.minecraftforge.client.model.data.IModelData getModelData(net.minecraft.util.math.BlockPos pos) {
       return modelData.getOrDefault(pos, net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
   }
}