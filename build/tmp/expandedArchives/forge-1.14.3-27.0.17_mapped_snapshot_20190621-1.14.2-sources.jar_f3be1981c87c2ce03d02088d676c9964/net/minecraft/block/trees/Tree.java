package net.minecraft.block.trees;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public abstract class Tree {
   @Nullable
   protected abstract AbstractTreeFeature<NoFeatureConfig> getTreeFeature(Random random);

   public boolean spawn(IWorld worldIn, BlockPos pos, BlockState blockUnder, Random random) {
      AbstractTreeFeature<NoFeatureConfig> abstracttreefeature = this.getTreeFeature(random);
      if (abstracttreefeature == null) {
         return false;
      } else {
         worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 4);
         if (abstracttreefeature.place(worldIn, worldIn.getChunkProvider().getChunkGenerator(), random, pos, IFeatureConfig.NO_FEATURE_CONFIG)) {
            return true;
         } else {
            worldIn.setBlockState(pos, blockUnder, 4);
            return false;
         }
      }
   }
}