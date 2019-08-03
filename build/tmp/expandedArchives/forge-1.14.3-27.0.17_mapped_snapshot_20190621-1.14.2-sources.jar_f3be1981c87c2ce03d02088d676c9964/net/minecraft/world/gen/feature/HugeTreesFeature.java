package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationBaseReader;
import net.minecraft.world.gen.IWorldGenerationReader;

public abstract class HugeTreesFeature<T extends IFeatureConfig> extends AbstractTreeFeature<T> {
   protected final int baseHeight;
   protected final BlockState trunk;
   protected final BlockState leaf;
   protected final int extraRandomHeight;

   public HugeTreesFeature(Function<Dynamic<?>, ? extends T> p_i51481_1_, boolean p_i51481_2_, int p_i51481_3_, int p_i51481_4_, BlockState p_i51481_5_, BlockState p_i51481_6_) {
      super(p_i51481_1_, p_i51481_2_);
      this.baseHeight = p_i51481_3_;
      this.extraRandomHeight = p_i51481_4_;
      this.trunk = p_i51481_5_;
      this.leaf = p_i51481_6_;
      setSapling((net.minecraftforge.common.IPlantable)net.minecraft.block.Blocks.OAK_SAPLING);
   }

   /**
    * calculates the height based on this trees base height and its extra random height
    */
   protected int getHeight(Random rand) {
      int i = rand.nextInt(3) + this.baseHeight;
      if (this.extraRandomHeight > 1) {
         i += rand.nextInt(this.extraRandomHeight);
      }

      return i;
   }

   /**
    * returns whether or not there is space for a tree to grow at a certain position
    */
   private boolean isSpaceAt(IWorldGenerationBaseReader worldIn, BlockPos leavesPos, int height) {
      boolean flag = true;
      if (leavesPos.getY() >= 1 && leavesPos.getY() + height + 1 <= worldIn.getMaxHeight()) {
         for(int i = 0; i <= 1 + height; ++i) {
            int j = 2;
            if (i == 0) {
               j = 1;
            } else if (i >= 1 + height - 2) {
               j = 2;
            }

            for(int k = -j; k <= j && flag; ++k) {
               for(int l = -j; l <= j && flag; ++l) {
                  if (leavesPos.getY() + i < 0 || leavesPos.getY() + i >= worldIn.getMaxHeight() || !func_214587_a(worldIn, leavesPos.add(k, i, l))) {
                     flag = false;
                  }
               }
            }
         }

         return flag;
      } else {
         return false;
      }
   }

   private boolean func_202405_b(IWorldGenerationReader p_202405_1_, BlockPos p_202405_2_) {
      BlockPos blockpos = p_202405_2_.down();

      if (isSoil(p_202405_1_, blockpos, getSapling()) && p_202405_2_.getY() >= 2) {
         setDirtAt(p_202405_1_, blockpos, p_202405_2_);
         setDirtAt(p_202405_1_, blockpos.east(), p_202405_2_);
         setDirtAt(p_202405_1_, blockpos.south(), p_202405_2_);
         setDirtAt(p_202405_1_, blockpos.south().east(), p_202405_2_);
         return true;
      } else {
         return false;
      }
   }

   protected boolean func_203427_a(IWorldGenerationReader p_203427_1_, BlockPos p_203427_2_, int p_203427_3_) {
      return this.isSpaceAt(p_203427_1_, p_203427_2_, p_203427_3_) && this.func_202405_b(p_203427_1_, p_203427_2_);
   }

   protected void func_222839_a(IWorldGenerationReader p_222839_1_, BlockPos p_222839_2_, int p_222839_3_, MutableBoundingBox p_222839_4_, Set<BlockPos> p_222839_5_) {
      int i = p_222839_3_ * p_222839_3_;

      for(int j = -p_222839_3_; j <= p_222839_3_ + 1; ++j) {
         for(int k = -p_222839_3_; k <= p_222839_3_ + 1; ++k) {
            int l = Math.min(Math.abs(j), Math.abs(j - 1));
            int i1 = Math.min(Math.abs(k), Math.abs(k - 1));
            if (l + i1 < 7 && l * l + i1 * i1 <= i) {
               BlockPos blockpos = p_222839_2_.add(j, 0, k);
               if (isAirOrLeaves(p_222839_1_, blockpos)) {
                  this.setLogState(p_222839_5_, p_222839_1_, blockpos, this.leaf, p_222839_4_);
               }
            }
         }
      }

   }

   protected void func_222838_b(IWorldGenerationReader p_222838_1_, BlockPos p_222838_2_, int p_222838_3_, MutableBoundingBox p_222838_4_, Set<BlockPos> p_222838_5_) {
      int i = p_222838_3_ * p_222838_3_;

      for(int j = -p_222838_3_; j <= p_222838_3_; ++j) {
         for(int k = -p_222838_3_; k <= p_222838_3_; ++k) {
            if (j * j + k * k <= i) {
               BlockPos blockpos = p_222838_2_.add(j, 0, k);
               if (isAirOrLeaves(p_222838_1_, blockpos)) {
                  this.setLogState(p_222838_5_, p_222838_1_, blockpos, this.leaf, p_222838_4_);
               }
            }
         }
      }

   }
}