package net.minecraft.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;
import java.util.BitSet;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.feature.ProbabilityConfig;

public class UnderwaterCaveWorldCarver extends CaveWorldCarver {
   public UnderwaterCaveWorldCarver(Function<Dynamic<?>, ? extends ProbabilityConfig> p_i49922_1_) {
      super(p_i49922_1_, 256);
      this.field_222718_j = ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.TERRACOTTA, Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA, Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA, Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.MYCELIUM, Blocks.SNOW, Blocks.SAND, Blocks.GRAVEL, Blocks.WATER, Blocks.LAVA, Blocks.OBSIDIAN, Blocks.AIR, Blocks.CAVE_AIR, Blocks.PACKED_ICE);
   }

   protected boolean func_222700_a(IChunk p_222700_1_, int p_222700_2_, int p_222700_3_, int p_222700_4_, int p_222700_5_, int p_222700_6_, int p_222700_7_, int p_222700_8_, int p_222700_9_) {
      return false;
   }

   protected boolean func_222703_a(IChunk chunkIn, BitSet p_222703_2_, Random p_222703_3_, BlockPos.MutableBlockPos p_222703_4_, BlockPos.MutableBlockPos p_222703_5_, BlockPos.MutableBlockPos p_222703_6_, int p_222703_7_, int p_222703_8_, int p_222703_9_, int p_222703_10_, int p_222703_11_, int p_222703_12_, int p_222703_13_, int p_222703_14_, AtomicBoolean p_222703_15_) {
      return func_222728_a(this, chunkIn, p_222703_2_, p_222703_3_, p_222703_4_, p_222703_7_, p_222703_8_, p_222703_9_, p_222703_10_, p_222703_11_, p_222703_12_, p_222703_13_, p_222703_14_);
   }

   protected static boolean func_222728_a(WorldCarver<?> p_222728_0_, IChunk p_222728_1_, BitSet p_222728_2_, Random p_222728_3_, BlockPos.MutableBlockPos p_222728_4_, int p_222728_5_, int p_222728_6_, int p_222728_7_, int p_222728_8_, int p_222728_9_, int p_222728_10_, int p_222728_11_, int p_222728_12_) {
      if (p_222728_11_ >= p_222728_5_) {
         return false;
      } else {
         int i = p_222728_10_ | p_222728_12_ << 4 | p_222728_11_ << 8;
         if (p_222728_2_.get(i)) {
            return false;
         } else {
            p_222728_2_.set(i);
            p_222728_4_.setPos(p_222728_8_, p_222728_11_, p_222728_9_);
            BlockState blockstate = p_222728_1_.getBlockState(p_222728_4_);
            if (!p_222728_0_.func_222706_a(blockstate)) {
               return false;
            } else if (p_222728_11_ == 10) {
               float f = p_222728_3_.nextFloat();
               if ((double)f < 0.25D) {
                  p_222728_1_.setBlockState(p_222728_4_, Blocks.MAGMA_BLOCK.getDefaultState(), false);
                  p_222728_1_.getBlocksToBeTicked().scheduleTick(p_222728_4_, Blocks.MAGMA_BLOCK, 0);
               } else {
                  p_222728_1_.setBlockState(p_222728_4_, Blocks.OBSIDIAN.getDefaultState(), false);
               }

               return true;
            } else if (p_222728_11_ < 10) {
               p_222728_1_.setBlockState(p_222728_4_, Blocks.LAVA.getDefaultState(), false);
               return false;
            } else {
               boolean flag = false;

               for(Direction direction : Direction.Plane.HORIZONTAL) {
                  int j = p_222728_8_ + direction.getXOffset();
                  int k = p_222728_9_ + direction.getZOffset();
                  if (j >> 4 != p_222728_6_ || k >> 4 != p_222728_7_ || p_222728_1_.getBlockState(p_222728_4_.setPos(j, p_222728_11_, k)).isAir()) {
                     p_222728_1_.setBlockState(p_222728_4_, WATER.getBlockState(), false);
                     p_222728_1_.getFluidsToBeTicked().scheduleTick(p_222728_4_, WATER.getFluid(), 0);
                     flag = true;
                     break;
                  }
               }

               p_222728_4_.setPos(p_222728_8_, p_222728_11_, p_222728_9_);
               if (!flag) {
                  p_222728_1_.setBlockState(p_222728_4_, WATER.getBlockState(), false);
                  return true;
               } else {
                  return true;
               }
            }
         }
      }
   }
}