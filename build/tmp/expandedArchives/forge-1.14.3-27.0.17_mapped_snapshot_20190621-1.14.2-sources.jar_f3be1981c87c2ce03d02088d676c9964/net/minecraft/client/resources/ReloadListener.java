package net.minecraft.client.resources;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;

public abstract class ReloadListener<T> implements IFutureReloadListener {
   public final CompletableFuture<Void> reload(IFutureReloadListener.IStage stage, IResourceManager resourceManager, IProfiler preparationsProfiler, IProfiler reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
      return CompletableFuture.supplyAsync(() -> {
         return (T)this.prepare(resourceManager, preparationsProfiler);
      }, backgroundExecutor).thenCompose(stage::markCompleteAwaitingOthers).thenAcceptAsync((p_215269_3_) -> {
         this.apply(p_215269_3_, resourceManager, reloadProfiler);
      }, gameExecutor);
   }

   /**
    * Performs any reloading that can be done off-thread, such as file IO
    */
   protected abstract T prepare(IResourceManager p_212854_1_, IProfiler p_212854_2_);

   /**
    * Performs any reloading that must be done on the main thread, such as uploading textures to the GPU or touching
    * non-threadsafe data
    */
   protected abstract void apply(T p_212853_1_, IResourceManager p_212853_2_, IProfiler p_212853_3_);
}