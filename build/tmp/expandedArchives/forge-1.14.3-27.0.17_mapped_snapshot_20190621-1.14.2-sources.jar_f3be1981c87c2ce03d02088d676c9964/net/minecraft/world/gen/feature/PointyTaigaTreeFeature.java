package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;

public class PointyTaigaTreeFeature extends AbstractTreeFeature<NoFeatureConfig> {
   private static final BlockState TRUNK = Blocks.SPRUCE_LOG.getDefaultState();
   private static final BlockState LEAF = Blocks.SPRUCE_LEAVES.getDefaultState();

   public PointyTaigaTreeFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51469_1_) {
      super(p_i51469_1_, false);
      setSapling((net.minecraftforge.common.IPlantable)Blocks.SPRUCE_SAPLING);
   }

   public boolean place(Set<BlockPos> changedBlocks, IWorldGenerationReader worldIn, Random rand, BlockPos position, MutableBoundingBox p_208519_5_) {
      int i = rand.nextInt(5) + 7;
      int j = i - rand.nextInt(2) - 3;
      int k = i - j;
      int l = 1 + rand.nextInt(k + 1);
      if (position.getY() >= 1 && position.getY() + i + 1 <= worldIn.getMaxHeight()) {
         boolean flag = true;

         for(int i1 = position.getY(); i1 <= position.getY() + 1 + i && flag; ++i1) {
            int j1 = 1;
            if (i1 - position.getY() < j) {
               j1 = 0;
            } else {
               j1 = l;
            }

            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

            for(int k1 = position.getX() - j1; k1 <= position.getX() + j1 && flag; ++k1) {
               for(int l1 = position.getZ() - j1; l1 <= position.getZ() + j1 && flag; ++l1) {
                  if (i1 >= 0 && i1 < worldIn.getMaxHeight()) {
                     if (!func_214587_a(worldIn, blockpos$mutableblockpos.setPos(k1, i1, l1))) {
                        flag = false;
                     }
                  } else {
                     flag = false;
                  }
               }
            }
         }

         if (!flag) {
            return false;
         } else if (isSoil(worldIn, position.down(), getSapling()) && position.getY() < worldIn.getMaxHeight() - i - 1) {
            this.setDirtAt(worldIn, position.down(), position);
            int j2 = 0;

            for(int k2 = position.getY() + i; k2 >= position.getY() + j; --k2) {
               for(int i3 = position.getX() - j2; i3 <= position.getX() + j2; ++i3) {
                  int j3 = i3 - position.getX();

                  for(int k3 = position.getZ() - j2; k3 <= position.getZ() + j2; ++k3) {
                     int i2 = k3 - position.getZ();
                     if (Math.abs(j3) != j2 || Math.abs(i2) != j2 || j2 <= 0) {
                        BlockPos blockpos = new BlockPos(i3, k2, k3);
                        if (isAirOrLeaves(worldIn, blockpos)) {
                           this.setLogState(changedBlocks, worldIn, blockpos, LEAF, p_208519_5_);
                        }
                     }
                  }
               }

               if (j2 >= 1 && k2 == position.getY() + j + 1) {
                  --j2;
               } else if (j2 < l) {
                  ++j2;
               }
            }

            for(int l2 = 0; l2 < i - 1; ++l2) {
               if (isAirOrLeaves(worldIn, position.up(l2))) {
                  this.setLogState(changedBlocks, worldIn, position.up(l2), TRUNK, p_208519_5_);
               }
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
}