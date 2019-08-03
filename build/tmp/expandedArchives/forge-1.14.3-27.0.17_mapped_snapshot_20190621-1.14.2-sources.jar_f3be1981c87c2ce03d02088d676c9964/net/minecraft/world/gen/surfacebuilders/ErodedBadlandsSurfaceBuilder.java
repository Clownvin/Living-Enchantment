package net.minecraft.world.gen.surfacebuilders;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public class ErodedBadlandsSurfaceBuilder extends BadlandsSurfaceBuilder {
   private static final BlockState WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.getDefaultState();
   private static final BlockState ORANGE_TERRACOTTA = Blocks.ORANGE_TERRACOTTA.getDefaultState();
   private static final BlockState TERRACOTTA = Blocks.TERRACOTTA.getDefaultState();

   public ErodedBadlandsSurfaceBuilder(Function<Dynamic<?>, ? extends SurfaceBuilderConfig> p_i51314_1_) {
      super(p_i51314_1_);
   }

   public void buildSurface(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config) {
      double d0 = 0.0D;
      double d1 = Math.min(Math.abs(noise), this.field_215435_c.getValue((double)x * 0.25D, (double)z * 0.25D));
      if (d1 > 0.0D) {
         double d2 = 0.001953125D;
         double d3 = Math.abs(this.field_215437_d.getValue((double)x * 0.001953125D, (double)z * 0.001953125D));
         d0 = d1 * d1 * 2.5D;
         double d4 = Math.ceil(d3 * 50.0D) + 14.0D;
         if (d0 > d4) {
            d0 = d4;
         }

         d0 = d0 + 64.0D;
      }

      int l = x & 15;
      int i = z & 15;
      BlockState blockstate2 = WHITE_TERRACOTTA;
      BlockState blockstate = biomeIn.getSurfaceBuilderConfig().getUnder();
      int i1 = (int)(noise / 3.0D + 3.0D + random.nextDouble() * 0.25D);
      boolean flag = Math.cos(noise / 3.0D * Math.PI) > 0.0D;
      int j = -1;
      boolean flag1 = false;
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int k = Math.max(startHeight, (int)d0 + 1); k >= 0; --k) {
         blockpos$mutableblockpos.setPos(l, k, i);
         if (chunkIn.getBlockState(blockpos$mutableblockpos).isAir() && k < (int)d0) {
            chunkIn.setBlockState(blockpos$mutableblockpos, defaultBlock, false);
         }

         BlockState blockstate1 = chunkIn.getBlockState(blockpos$mutableblockpos);
         if (blockstate1.isAir()) {
            j = -1;
         } else if (blockstate1.getBlock() == defaultBlock.getBlock()) {
            if (j == -1) {
               flag1 = false;
               if (i1 <= 0) {
                  blockstate2 = Blocks.AIR.getDefaultState();
                  blockstate = defaultBlock;
               } else if (k >= seaLevel - 4 && k <= seaLevel + 1) {
                  blockstate2 = WHITE_TERRACOTTA;
                  blockstate = biomeIn.getSurfaceBuilderConfig().getUnder();
               }

               if (k < seaLevel && (blockstate2 == null || blockstate2.isAir())) {
                  blockstate2 = defaultFluid;
               }

               j = i1 + Math.max(0, k - seaLevel);
               if (k >= seaLevel - 1) {
                  if (k <= seaLevel + 3 + i1) {
                     chunkIn.setBlockState(blockpos$mutableblockpos, biomeIn.getSurfaceBuilderConfig().getTop(), false);
                     flag1 = true;
                  } else {
                     BlockState blockstate3;
                     if (k >= 64 && k <= 127) {
                        if (flag) {
                           blockstate3 = TERRACOTTA;
                        } else {
                           blockstate3 = this.func_215431_a(x, k, z);
                        }
                     } else {
                        blockstate3 = ORANGE_TERRACOTTA;
                     }

                     chunkIn.setBlockState(blockpos$mutableblockpos, blockstate3, false);
                  }
               } else {
                  chunkIn.setBlockState(blockpos$mutableblockpos, blockstate, false);
                  Block block = blockstate.getBlock();
                  if (block == Blocks.WHITE_TERRACOTTA || block == Blocks.ORANGE_TERRACOTTA || block == Blocks.MAGENTA_TERRACOTTA || block == Blocks.LIGHT_BLUE_TERRACOTTA || block == Blocks.YELLOW_TERRACOTTA || block == Blocks.LIME_TERRACOTTA || block == Blocks.PINK_TERRACOTTA || block == Blocks.GRAY_TERRACOTTA || block == Blocks.LIGHT_GRAY_TERRACOTTA || block == Blocks.CYAN_TERRACOTTA || block == Blocks.PURPLE_TERRACOTTA || block == Blocks.BLUE_TERRACOTTA || block == Blocks.BROWN_TERRACOTTA || block == Blocks.GREEN_TERRACOTTA || block == Blocks.RED_TERRACOTTA || block == Blocks.BLACK_TERRACOTTA) {
                     chunkIn.setBlockState(blockpos$mutableblockpos, ORANGE_TERRACOTTA, false);
                  }
               }
            } else if (j > 0) {
               --j;
               if (flag1) {
                  chunkIn.setBlockState(blockpos$mutableblockpos, ORANGE_TERRACOTTA, false);
               } else {
                  chunkIn.setBlockState(blockpos$mutableblockpos, this.func_215431_a(x, k, z), false);
               }
            }
         }
      }

   }
}