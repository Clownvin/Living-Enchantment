package net.minecraft.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;
import java.util.BitSet;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.feature.ProbabilityConfig;

public class NetherCaveWorldCarver extends CaveWorldCarver {
   public NetherCaveWorldCarver(Function<Dynamic<?>, ? extends ProbabilityConfig> p_i49927_1_) {
      super(p_i49927_1_, 128);
      this.field_222718_j = ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.NETHERRACK);
      this.field_222719_k = ImmutableSet.of(Fluids.LAVA, Fluids.WATER);
   }

   protected int func_222724_a() {
      return 10;
   }

   protected float func_222722_a(Random p_222722_1_) {
      return (p_222722_1_.nextFloat() * 2.0F + p_222722_1_.nextFloat()) * 2.0F;
   }

   protected double func_222725_b() {
      return 5.0D;
   }

   protected int func_222726_b(Random p_222726_1_) {
      return p_222726_1_.nextInt(this.field_222720_l);
   }

   protected boolean func_222703_a(IChunk chunkIn, BitSet p_222703_2_, Random p_222703_3_, BlockPos.MutableBlockPos p_222703_4_, BlockPos.MutableBlockPos p_222703_5_, BlockPos.MutableBlockPos p_222703_6_, int p_222703_7_, int p_222703_8_, int p_222703_9_, int p_222703_10_, int p_222703_11_, int p_222703_12_, int p_222703_13_, int p_222703_14_, AtomicBoolean p_222703_15_) {
      int i = p_222703_12_ | p_222703_14_ << 4 | p_222703_13_ << 8;
      if (p_222703_2_.get(i)) {
         return false;
      } else {
         p_222703_2_.set(i);
         p_222703_4_.setPos(p_222703_10_, p_222703_13_, p_222703_11_);
         if (this.func_222706_a(chunkIn.getBlockState(p_222703_4_))) {
            BlockState blockstate;
            if (p_222703_13_ <= 31) {
               blockstate = LAVA.getBlockState();
            } else {
               blockstate = CAVE_AIR;
            }

            chunkIn.setBlockState(p_222703_4_, blockstate, false);
            return true;
         } else {
            return false;
         }
      }
   }
}