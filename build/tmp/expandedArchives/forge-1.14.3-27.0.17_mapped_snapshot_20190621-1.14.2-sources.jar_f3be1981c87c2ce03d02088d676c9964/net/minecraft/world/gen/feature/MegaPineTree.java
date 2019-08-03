package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;

public class MegaPineTree extends HugeTreesFeature<NoFeatureConfig> {
   private static final BlockState TRUNK = Blocks.SPRUCE_LOG.getDefaultState();
   private static final BlockState LEAF = Blocks.SPRUCE_LEAVES.getDefaultState();
   private static final BlockState PODZOL = Blocks.PODZOL.getDefaultState();
   private final boolean useBaseHeight;

   public MegaPineTree(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51482_1_, boolean p_i51482_2_, boolean p_i51482_3_) {
      super(p_i51482_1_, p_i51482_2_, 13, 15, TRUNK, LEAF);
      this.useBaseHeight = p_i51482_3_;
      setSapling((net.minecraftforge.common.IPlantable)Blocks.SPRUCE_SAPLING);
   }

   public boolean place(Set<BlockPos> changedBlocks, IWorldGenerationReader worldIn, Random rand, BlockPos position, MutableBoundingBox p_208519_5_) {
      int i = this.getHeight(rand);
      if (!this.func_203427_a(worldIn, position, i)) {
         return false;
      } else {
         this.func_214596_a(worldIn, position.getX(), position.getZ(), position.getY() + i, 0, rand, p_208519_5_, changedBlocks);

         for(int j = 0; j < i; ++j) {
            if (isAirOrLeaves(worldIn, position.up(j))) {
               this.setLogState(changedBlocks, worldIn, position.up(j), this.trunk, p_208519_5_);
            }

            if (j < i - 1) {
               if (isAirOrLeaves(worldIn, position.add(1, j, 0))) {
                  this.setLogState(changedBlocks, worldIn, position.add(1, j, 0), this.trunk, p_208519_5_);
               }

               if (isAirOrLeaves(worldIn, position.add(1, j, 1))) {
                  this.setLogState(changedBlocks, worldIn, position.add(1, j, 1), this.trunk, p_208519_5_);
               }

               if (isAirOrLeaves(worldIn, position.add(0, j, 1))) {
                  this.setLogState(changedBlocks, worldIn, position.add(0, j, 1), this.trunk, p_208519_5_);
               }
            }
         }

         this.generateSaplings(worldIn, rand, position);
         return true;
      }
   }

   private void func_214596_a(IWorldGenerationReader p_214596_1_, int p_214596_2_, int p_214596_3_, int p_214596_4_, int p_214596_5_, Random p_214596_6_, MutableBoundingBox p_214596_7_, Set<BlockPos> p_214596_8_) {
      int i = p_214596_6_.nextInt(5) + (this.useBaseHeight ? this.baseHeight : 3);
      int j = 0;

      for(int k = p_214596_4_ - i; k <= p_214596_4_; ++k) {
         int l = p_214596_4_ - k;
         int i1 = p_214596_5_ + MathHelper.floor((float)l / (float)i * 3.5F);
         this.func_222839_a(p_214596_1_, new BlockPos(p_214596_2_, k, p_214596_3_), i1 + (l > 0 && i1 == j && (k & 1) == 0 ? 1 : 0), p_214596_7_, p_214596_8_);
         j = i1;
      }

   }

   public void generateSaplings(IWorldGenerationReader worldIn, Random random, BlockPos pos) {
      this.placePodzolCircle(worldIn, pos.west().north());
      this.placePodzolCircle(worldIn, pos.east(2).north());
      this.placePodzolCircle(worldIn, pos.west().south(2));
      this.placePodzolCircle(worldIn, pos.east(2).south(2));

      for(int i = 0; i < 5; ++i) {
         int j = random.nextInt(64);
         int k = j % 8;
         int l = j / 8;
         if (k == 0 || k == 7 || l == 0 || l == 7) {
            this.placePodzolCircle(worldIn, pos.add(-3 + k, 0, -3 + l));
         }
      }

   }

   private void placePodzolCircle(IWorldGenerationReader worldIn, BlockPos center) {
      for(int i = -2; i <= 2; ++i) {
         for(int j = -2; j <= 2; ++j) {
            if (Math.abs(i) != 2 || Math.abs(j) != 2) {
               this.placePodzolAt(worldIn, center.add(i, 0, j));
            }
         }
      }

   }

   private void placePodzolAt(IWorldGenerationReader worldIn, BlockPos pos) {
      for(int i = 2; i >= -3; --i) {
         BlockPos blockpos = pos.up(i);
         if (isSoil(worldIn, blockpos, getSapling())) {
            this.setBlockState(worldIn, blockpos, PODZOL);
            break;
         }

         if (!isAir(worldIn, blockpos) && i < 0) {
            break;
         }
      }

   }
}