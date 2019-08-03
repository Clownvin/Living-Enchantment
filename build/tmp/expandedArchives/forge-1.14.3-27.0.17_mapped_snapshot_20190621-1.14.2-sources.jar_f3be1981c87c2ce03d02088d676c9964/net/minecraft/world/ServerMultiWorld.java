package net.minecraft.world;

import java.util.concurrent.Executor;
import net.minecraft.profiler.IProfiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.border.IBorderListener;
import net.minecraft.world.chunk.listener.IChunkStatusListener;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.DerivedWorldInfo;
import net.minecraft.world.storage.SaveHandler;

public class ServerMultiWorld extends ServerWorld {
   private final ServerWorld delegate;
   private final IBorderListener borderListener;
   public ServerMultiWorld(ServerWorld p_i50708_1_, MinecraftServer p_i50708_2_, Executor p_i50708_3_, SaveHandler p_i50708_4_, DimensionType p_i50708_5_, IProfiler p_i50708_6_, IChunkStatusListener p_i50708_7_) {
      super(p_i50708_2_, p_i50708_3_, p_i50708_4_, new DerivedWorldInfo(p_i50708_1_.getWorldInfo()), p_i50708_5_, p_i50708_6_, p_i50708_7_);
      this.delegate = p_i50708_1_;
      this.borderListener = new IBorderListener.Impl(this.getWorldBorder());
      p_i50708_1_.getWorldBorder().addListener(this.borderListener);
   }

   @Override
   public void close() throws java.io.IOException {
      super.close();
      this.delegate.getWorldBorder().removeListener(this.borderListener); // Unlink ourselves, to prevent world leak.
   }
}