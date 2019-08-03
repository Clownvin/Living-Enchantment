package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.primitives.Doubles;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.client.renderer.VertexBufferUploader;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ChunkRenderDispatcher {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ThreadFactory THREAD_FACTORY = (new ThreadFactoryBuilder()).setNameFormat("Chunk Batcher %d").setDaemon(true).setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER)).build();
   private final int countRenderBuilders;
   private final List<Thread> listWorkerThreads = Lists.newArrayList();
   private final List<ChunkRenderWorker> listThreadedWorkers = Lists.newArrayList();
   private final PriorityBlockingQueue<ChunkRenderTask> queueChunkUpdates = Queues.newPriorityBlockingQueue();
   private final BlockingQueue<RegionRenderCacheBuilder> queueFreeRenderBuilders;
   private final WorldVertexBufferUploader worldVertexUploader = new WorldVertexBufferUploader();
   private final VertexBufferUploader vertexUploader = new VertexBufferUploader();
   private final Queue<ChunkRenderDispatcher.PendingUpload> queueChunkUploads = Queues.newPriorityQueue();
   private final ChunkRenderWorker renderWorker;
   private Vec3d field_217672_l = Vec3d.ZERO;

   public ChunkRenderDispatcher(boolean p_i51518_1_) {
      this(p_i51518_1_, -1);
   }

   public ChunkRenderDispatcher(boolean p_i51518_1_, int countRenderBuilders) {
      int i = Math.max(1, (int)((double)Runtime.getRuntime().maxMemory() * 0.3D) / 10485760 - 1);
      int j = Runtime.getRuntime().availableProcessors();
      int k = p_i51518_1_ ? j : Math.min(j, 4);
      int l = countRenderBuilders < 0 ? Math.max(1, Math.min(k * 2, i)) : countRenderBuilders;
      this.renderWorker = new ChunkRenderWorker(this, new RegionRenderCacheBuilder());
      List<RegionRenderCacheBuilder> list = Lists.newArrayListWithExpectedSize(l);

      try {
         for(int i1 = 0; i1 < l; ++i1) {
            list.add(new RegionRenderCacheBuilder());
         }
      } catch (OutOfMemoryError var11) {
         LOGGER.warn("Allocated only {}/{} buffers", list.size(), l);
         int j1 = list.size() * 2 / 3;

         for(int k1 = 0; k1 < j1; ++k1) {
            list.remove(list.size() - 1);
         }

         System.gc();
      }

      this.countRenderBuilders = list.size();
      this.queueFreeRenderBuilders = Queues.newArrayBlockingQueue(this.countRenderBuilders);
      this.queueFreeRenderBuilders.addAll(list);
      int l1 = Math.min(k, this.countRenderBuilders);
      if (l1 > 1) {
         for(int i2 = 0; i2 < l1; ++i2) {
            ChunkRenderWorker chunkrenderworker = new ChunkRenderWorker(this);
            Thread thread = THREAD_FACTORY.newThread(chunkrenderworker);
            thread.start();
            this.listThreadedWorkers.add(chunkrenderworker);
            this.listWorkerThreads.add(thread);
         }
      }

   }

   public String getDebugInfo() {
      return this.listWorkerThreads.isEmpty() ? String.format("pC: %03d, single-threaded", this.queueChunkUpdates.size()) : String.format("pC: %03d, pU: %02d, aB: %02d", this.queueChunkUpdates.size(), this.queueChunkUploads.size(), this.queueFreeRenderBuilders.size());
   }

   public void func_217669_a(Vec3d p_217669_1_) {
      this.field_217672_l = p_217669_1_;
   }

   public Vec3d func_217671_b() {
      return this.field_217672_l;
   }

   public boolean runChunkUploads(long finishTimeNano) {
      boolean flag = false;

      while(true) {
         boolean flag1 = false;
         if (this.listWorkerThreads.isEmpty()) {
            ChunkRenderTask chunkrendertask = this.queueChunkUpdates.poll();
            if (chunkrendertask != null) {
               try {
                  this.renderWorker.processTask(chunkrendertask);
                  flag1 = true;
               } catch (InterruptedException var9) {
                  LOGGER.warn("Skipped task due to interrupt");
               }
            }
         }

         int i = 0;
         synchronized(this.queueChunkUploads) {
            while(i < 10) {
               ChunkRenderDispatcher.PendingUpload chunkrenderdispatcher$pendingupload = this.queueChunkUploads.poll();
               if (chunkrenderdispatcher$pendingupload == null) {
                  break;
               }

               if (!chunkrenderdispatcher$pendingupload.uploadTask.isDone()) {
                  chunkrenderdispatcher$pendingupload.uploadTask.run();
                  flag1 = true;
                  flag = true;
                  ++i;
               }
            }
         }

         if (finishTimeNano == 0L || !flag1 || finishTimeNano < Util.nanoTime()) {
            break;
         }
      }

      return flag;
   }

   public boolean updateChunkLater(ChunkRender chunkRenderer) {
      chunkRenderer.getLockCompileTask().lock();

      boolean flag1;
      try {
         ChunkRenderTask chunkrendertask = chunkRenderer.makeCompileTaskChunk();
         chunkrendertask.addFinishRunnable(() -> {
            this.queueChunkUpdates.remove(chunkrendertask);
         });
         boolean flag = this.queueChunkUpdates.offer(chunkrendertask);
         if (!flag) {
            chunkrendertask.finish();
         }

         flag1 = flag;
      } finally {
         chunkRenderer.getLockCompileTask().unlock();
      }

      return flag1;
   }

   public boolean updateChunkNow(ChunkRender chunkRenderer) {
      chunkRenderer.getLockCompileTask().lock();

      boolean flag;
      try {
         ChunkRenderTask chunkrendertask = chunkRenderer.makeCompileTaskChunk();

         try {
            this.renderWorker.processTask(chunkrendertask);
         } catch (InterruptedException var7) {
            ;
         }

         flag = true;
      } finally {
         chunkRenderer.getLockCompileTask().unlock();
      }

      return flag;
   }

   public void stopChunkUpdates() {
      this.clearChunkUpdates();
      List<RegionRenderCacheBuilder> list = Lists.newArrayList();

      while(list.size() != this.countRenderBuilders) {
         this.runChunkUploads(Long.MAX_VALUE);

         try {
            list.add(this.allocateRenderBuilder());
         } catch (InterruptedException var3) {
            ;
         }
      }

      this.queueFreeRenderBuilders.addAll(list);
   }

   public void freeRenderBuilder(RegionRenderCacheBuilder builder) {
      this.queueFreeRenderBuilders.add(builder);
   }

   public RegionRenderCacheBuilder allocateRenderBuilder() throws InterruptedException {
      return this.queueFreeRenderBuilders.take();
   }

   public ChunkRenderTask getNextChunkUpdate() throws InterruptedException {
      return this.queueChunkUpdates.take();
   }

   public boolean updateTransparencyLater(ChunkRender chunkRenderer) {
      chunkRenderer.getLockCompileTask().lock();

      boolean flag;
      try {
         ChunkRenderTask chunkrendertask = chunkRenderer.makeCompileTaskTransparency();
         if (chunkrendertask == null) {
            flag = true;
            return flag;
         }

         chunkrendertask.addFinishRunnable(() -> {
            this.queueChunkUpdates.remove(chunkrendertask);
         });
         flag = this.queueChunkUpdates.offer(chunkrendertask);
      } finally {
         chunkRenderer.getLockCompileTask().unlock();
      }

      return flag;
   }

   public ListenableFuture<Void> uploadChunk(BlockRenderLayer layerIn, BufferBuilder builderIn, ChunkRender renderChunkIn, CompiledChunk compiledChunkIn, double distanceSqIn) {
      if (Minecraft.getInstance().isOnExecutionThread()) {
         if (GLX.useVbo()) {
            this.uploadVertexBuffer(builderIn, renderChunkIn.getVertexBufferByLayer(layerIn.ordinal()));
         } else {
            this.func_217670_a(builderIn, ((ListedChunkRender)renderChunkIn).getDisplayList(layerIn, compiledChunkIn));
         }

         builderIn.setTranslation(0.0D, 0.0D, 0.0D);
         return Futures.immediateFuture((Void)null);
      } else {
         ListenableFutureTask<Void> listenablefuturetask = ListenableFutureTask.create(() -> {
            this.uploadChunk(layerIn, builderIn, renderChunkIn, compiledChunkIn, distanceSqIn);
         }, (Void)null);
         synchronized(this.queueChunkUploads) {
            this.queueChunkUploads.add(new ChunkRenderDispatcher.PendingUpload(listenablefuturetask, distanceSqIn));
            return listenablefuturetask;
         }
      }
   }

   private void func_217670_a(BufferBuilder p_217670_1_, int p_217670_2_) {
      GlStateManager.newList(p_217670_2_, 4864);
      this.worldVertexUploader.draw(p_217670_1_);
      GlStateManager.endList();
   }

   private void uploadVertexBuffer(BufferBuilder bufferBuilderIn, VertexBuffer vertexBufferIn) {
      this.vertexUploader.setVertexBuffer(vertexBufferIn);
      this.vertexUploader.draw(bufferBuilderIn);
   }

   public void clearChunkUpdates() {
      while(!this.queueChunkUpdates.isEmpty()) {
         ChunkRenderTask chunkrendertask = this.queueChunkUpdates.poll();
         if (chunkrendertask != null) {
            chunkrendertask.finish();
         }
      }

   }

   public boolean hasNoChunkUpdates() {
      return this.queueChunkUpdates.isEmpty() && this.queueChunkUploads.isEmpty();
   }

   public void stopWorkerThreads() {
      this.clearChunkUpdates();

      for(ChunkRenderWorker chunkrenderworker : this.listThreadedWorkers) {
         chunkrenderworker.notifyToStop();
      }

      for(Thread thread : this.listWorkerThreads) {
         try {
            thread.interrupt();
            thread.join();
         } catch (InterruptedException interruptedexception) {
            LOGGER.warn("Interrupted whilst waiting for worker to die", (Throwable)interruptedexception);
         }
      }

      this.queueFreeRenderBuilders.clear();
   }

   @OnlyIn(Dist.CLIENT)
   class PendingUpload implements Comparable<ChunkRenderDispatcher.PendingUpload> {
      private final ListenableFutureTask<Void> uploadTask;
      private final double distanceSq;

      public PendingUpload(ListenableFutureTask<Void> uploadTaskIn, double distanceSqIn) {
         this.uploadTask = uploadTaskIn;
         this.distanceSq = distanceSqIn;
      }

      public int compareTo(ChunkRenderDispatcher.PendingUpload p_compareTo_1_) {
         return Doubles.compare(this.distanceSq, p_compareTo_1_.distanceSq);
      }
   }
}