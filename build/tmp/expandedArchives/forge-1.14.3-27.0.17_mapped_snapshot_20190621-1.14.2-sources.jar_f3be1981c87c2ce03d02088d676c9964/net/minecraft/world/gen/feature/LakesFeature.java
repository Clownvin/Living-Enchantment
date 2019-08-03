package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class LakesFeature extends Feature<LakesConfig> {
   private static final BlockState field_205188_a = Blocks.CAVE_AIR.getDefaultState();

   public LakesFeature(Function<Dynamic<?>, ? extends LakesConfig> p_i51485_1_) {
      super(p_i51485_1_);
   }

   public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, LakesConfig config) {
      while(pos.getY() > 5 && worldIn.isAirBlock(pos)) {
         pos = pos.down();
      }

      if (pos.getY() <= 4) {
         return false;
      } else {
         pos = pos.down(4);
         ChunkPos chunkpos = new ChunkPos(pos);
         if (!worldIn.getChunk(chunkpos.x, chunkpos.z, ChunkStatus.STRUCTURE_REFERENCES).getStructureReferences(Feature.VILLAGE.getStructureName()).isEmpty()) {
            return false;
         } else {
            boolean[] aboolean = new boolean[2048];
            int i = rand.nextInt(4) + 4;

            for(int j = 0; j < i; ++j) {
               double d0 = rand.nextDouble() * 6.0D + 3.0D;
               double d1 = rand.nextDouble() * 4.0D + 2.0D;
               double d2 = rand.nextDouble() * 6.0D + 3.0D;
               double d3 = rand.nextDouble() * (16.0D - d0 - 2.0D) + 1.0D + d0 / 2.0D;
               double d4 = rand.nextDouble() * (8.0D - d1 - 4.0D) + 2.0D + d1 / 2.0D;
               double d5 = rand.nextDouble() * (16.0D - d2 - 2.0D) + 1.0D + d2 / 2.0D;

               for(int l = 1; l < 15; ++l) {
                  for(int i1 = 1; i1 < 15; ++i1) {
                     for(int j1 = 1; j1 < 7; ++j1) {
                        double d6 = ((double)l - d3) / (d0 / 2.0D);
                        double d7 = ((double)j1 - d4) / (d1 / 2.0D);
                        double d8 = ((double)i1 - d5) / (d2 / 2.0D);
                        double d9 = d6 * d6 + d7 * d7 + d8 * d8;
                        if (d9 < 1.0D) {
                           aboolean[(l * 16 + i1) * 8 + j1] = true;
                        }
                     }
                  }
               }
            }

            for(int k1 = 0; k1 < 16; ++k1) {
               for(int l2 = 0; l2 < 16; ++l2) {
                  for(int k = 0; k < 8; ++k) {
                     boolean flag = !aboolean[(k1 * 16 + l2) * 8 + k] && (k1 < 15 && aboolean[((k1 + 1) * 16 + l2) * 8 + k] || k1 > 0 && aboolean[((k1 - 1) * 16 + l2) * 8 + k] || l2 < 15 && aboolean[(k1 * 16 + l2 + 1) * 8 + k] || l2 > 0 && aboolean[(k1 * 16 + (l2 - 1)) * 8 + k] || k < 7 && aboolean[(k1 * 16 + l2) * 8 + k + 1] || k > 0 && aboolean[(k1 * 16 + l2) * 8 + (k - 1)]);
                     if (flag) {
                        Material material = worldIn.getBlockState(pos.add(k1, k, l2)).getMaterial();
                        if (k >= 4 && material.isLiquid()) {
                           return false;
                        }

                        if (k < 4 && !material.isSolid() && worldIn.getBlockState(pos.add(k1, k, l2)) != config.state) {
                           return false;
                        }
                     }
                  }
               }
            }

            for(int l1 = 0; l1 < 16; ++l1) {
               for(int i3 = 0; i3 < 16; ++i3) {
                  for(int i4 = 0; i4 < 8; ++i4) {
                     if (aboolean[(l1 * 16 + i3) * 8 + i4]) {
                        worldIn.setBlockState(pos.add(l1, i4, i3), i4 >= 4 ? field_205188_a : config.state, 2);
                     }
                  }
               }
            }

            for(int i2 = 0; i2 < 16; ++i2) {
               for(int j3 = 0; j3 < 16; ++j3) {
                  for(int j4 = 4; j4 < 8; ++j4) {
                     if (aboolean[(i2 * 16 + j3) * 8 + j4]) {
                        BlockPos blockpos = pos.add(i2, j4 - 1, j3);
                        if (Block.isDirt(worldIn.getBlockState(blockpos).getBlock()) && worldIn.getLightFor(LightType.SKY, pos.add(i2, j4, j3)) > 0) {
                           Biome biome = worldIn.getBiome(blockpos);
                           if (biome.getSurfaceBuilderConfig().getTop().getBlock() == Blocks.MYCELIUM) {
                              worldIn.setBlockState(blockpos, Blocks.MYCELIUM.getDefaultState(), 2);
                           } else {
                              worldIn.setBlockState(blockpos, Blocks.GRASS_BLOCK.getDefaultState(), 2);
                           }
                        }
                     }
                  }
               }
            }

            if (config.state.getMaterial() == Material.LAVA) {
               for(int j2 = 0; j2 < 16; ++j2) {
                  for(int k3 = 0; k3 < 16; ++k3) {
                     for(int k4 = 0; k4 < 8; ++k4) {
                        boolean flag1 = !aboolean[(j2 * 16 + k3) * 8 + k4] && (j2 < 15 && aboolean[((j2 + 1) * 16 + k3) * 8 + k4] || j2 > 0 && aboolean[((j2 - 1) * 16 + k3) * 8 + k4] || k3 < 15 && aboolean[(j2 * 16 + k3 + 1) * 8 + k4] || k3 > 0 && aboolean[(j2 * 16 + (k3 - 1)) * 8 + k4] || k4 < 7 && aboolean[(j2 * 16 + k3) * 8 + k4 + 1] || k4 > 0 && aboolean[(j2 * 16 + k3) * 8 + (k4 - 1)]);
                        if (flag1 && (k4 < 4 || rand.nextInt(2) != 0) && worldIn.getBlockState(pos.add(j2, k4, k3)).getMaterial().isSolid()) {
                           worldIn.setBlockState(pos.add(j2, k4, k3), Blocks.STONE.getDefaultState(), 2);
                        }
                     }
                  }
               }
            }

            if (config.state.getMaterial() == Material.WATER) {
               for(int k2 = 0; k2 < 16; ++k2) {
                  for(int l3 = 0; l3 < 16; ++l3) {
                     int l4 = 4;
                     BlockPos blockpos1 = pos.add(k2, 4, l3);
                     if (worldIn.getBiome(blockpos1).doesWaterFreeze(worldIn, blockpos1, false)) {
                        worldIn.setBlockState(blockpos1, Blocks.ICE.getDefaultState(), 2);
                     }
                  }
               }
            }

            return true;
         }
      }
   }
}