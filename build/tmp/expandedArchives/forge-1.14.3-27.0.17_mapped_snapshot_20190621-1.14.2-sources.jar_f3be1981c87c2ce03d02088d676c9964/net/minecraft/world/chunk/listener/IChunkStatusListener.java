package net.minecraft.world.chunk.listener;

import javax.annotation.Nullable;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;

public interface IChunkStatusListener {
   void func_219509_a(ChunkPos p_219509_1_);

   void func_219508_a(ChunkPos p_219508_1_, @Nullable ChunkStatus p_219508_2_);

   void func_219510_b();
}