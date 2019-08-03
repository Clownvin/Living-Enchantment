package net.minecraft.block.trees;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public abstract class BigTree extends Tree {
   public boolean spawn(IWorld worldIn, BlockPos pos, BlockState blockUnder, Random random) {
      for(int i = 0; i >= -1; --i) {
         for(int j = 0; j >= -1; --j) {
            if (canBigTreeSpawnAt(blockUnder, worldIn, pos, i, j)) {
               return this.spawnBigTree(worldIn, pos, blockUnder, random, i, j);
            }
         }
      }

      return super.spawn(worldIn, pos, blockUnder, random);
   }

   @Nullable
   protected abstract AbstractTreeFeature<NoFeatureConfig> getBigTreeFeature(Random random);

   public boolean spawnBigTree(IWorld worldIn, BlockPos pos, BlockState blockUnder, Random random, int xOffset, int zOffset) {
      AbstractTreeFeature<NoFeatureConfig> abstracttreefeature = this.getBigTreeFeature(random);
      if (abstracttreefeature == null) {
         return false;
      } else {
         BlockState blockstate = Blocks.AIR.getDefaultState();
         worldIn.setBlockState(pos.add(xOffset, 0, zOffset), blockstate, 4);
         worldIn.setBlockState(pos.add(xOffset + 1, 0, zOffset), blockstate, 4);
         worldIn.setBlockState(pos.add(xOffset, 0, zOffset + 1), blockstate, 4);
         worldIn.setBlockState(pos.add(xOffset + 1, 0, zOffset + 1), blockstate, 4);
         if (abstracttreefeature.place(worldIn, worldIn.getChunkProvider().getChunkGenerator(), random, pos.add(xOffset, 0, zOffset), IFeatureConfig.NO_FEATURE_CONFIG)) {
            return true;
         } else {
            worldIn.setBlockState(pos.add(xOffset, 0, zOffset), blockUnder, 4);
            worldIn.setBlockState(pos.add(xOffset + 1, 0, zOffset), blockUnder, 4);
            worldIn.setBlockState(pos.add(xOffset, 0, zOffset + 1), blockUnder, 4);
            worldIn.setBlockState(pos.add(xOffset + 1, 0, zOffset + 1), blockUnder, 4);
            return false;
         }
      }
   }

   public static boolean canBigTreeSpawnAt(BlockState blockUnder, IBlockReader worldIn, BlockPos pos, int xOffset, int zOffset) {
      Block block = blockUnder.getBlock();
      return block == worldIn.getBlockState(pos.add(xOffset, 0, zOffset)).getBlock() && block == worldIn.getBlockState(pos.add(xOffset + 1, 0, zOffset)).getBlock() && block == worldIn.getBlockState(pos.add(xOffset, 0, zOffset + 1)).getBlock() && block == worldIn.getBlockState(pos.add(xOffset + 1, 0, zOffset + 1)).getBlock();
   }
}