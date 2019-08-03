package net.minecraft.world.chunk.listener;

import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.util.concurrent.DelegatedTaskExecutor;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChainedChunkStatusListener implements IChunkStatusListener {
   private final IChunkStatusListener field_219519_a;
   private final DelegatedTaskExecutor<Runnable> field_219520_b;

   public ChainedChunkStatusListener(IChunkStatusListener p_i50696_1_, Executor p_i50696_2_) {
      this.field_219519_a = p_i50696_1_;
      this.field_219520_b = DelegatedTaskExecutor.create(p_i50696_2_, "progressListener");
   }

   public void func_219509_a(ChunkPos p_219509_1_) {
      this.field_219520_b.enqueue(() -> {
         this.field_219519_a.func_219509_a(p_219509_1_);
      });
   }

   public void func_219508_a(ChunkPos p_219508_1_, @Nullable ChunkStatus p_219508_2_) {
      this.field_219520_b.enqueue(() -> {
         this.field_219519_a.func_219508_a(p_219508_1_, p_219508_2_);
      });
   }

   public void func_219510_b() {
      this.field_219520_b.enqueue(this.field_219519_a::func_219510_b);
   }
}