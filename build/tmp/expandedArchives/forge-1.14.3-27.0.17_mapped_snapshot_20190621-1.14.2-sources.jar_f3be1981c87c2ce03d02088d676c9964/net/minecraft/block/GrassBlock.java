package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.FlowersFeature;

public class GrassBlock extends SpreadableSnowyDirtBlock implements IGrowable {
   public GrassBlock(Block.Properties properties) {
      super(properties);
   }

   /**
    * Whether this IGrowable can grow
    */
   public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
      return worldIn.getBlockState(pos.up()).isAir();
   }

   public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
      return true;
   }

   public void grow(World worldIn, Random rand, BlockPos pos, BlockState state) {
      BlockPos blockpos = pos.up();
      BlockState blockstate = Blocks.GRASS.getDefaultState();

      for(int i = 0; i < 128; ++i) {
         BlockPos blockpos1 = blockpos;
         int j = 0;

         while(true) {
            if (j >= i / 16) {
               BlockState blockstate2 = worldIn.getBlockState(blockpos1);
               if (blockstate2.getBlock() == blockstate.getBlock() && rand.nextInt(10) == 0) {
                  ((IGrowable)blockstate.getBlock()).grow(worldIn, rand, blockpos1, blockstate2);
               }

               if (!blockstate2.isAir()) {
                  break;
               }

               BlockState blockstate1;
               if (rand.nextInt(8) == 0) {
                  List<ConfiguredFeature<?>> list = worldIn.getBiome(blockpos1).getFlowers();
                  if (list.isEmpty()) {
                     break;
                  }

                  blockstate1 = ((FlowersFeature)((DecoratedFeatureConfig)(list.get(0)).config).feature.feature).getRandomFlower(rand, blockpos1);
               } else {
                  blockstate1 = blockstate;
               }

               if (blockstate1.isValidPosition(worldIn, blockpos1)) {
                  worldIn.setBlockState(blockpos1, blockstate1, 3);
               }
               break;
            }

            blockpos1 = blockpos1.add(rand.nextInt(3) - 1, (rand.nextInt(3) - 1) * rand.nextInt(3) / 2, rand.nextInt(3) - 1);
            if (worldIn.getBlockState(blockpos1.down()).getBlock() != this || isOpaque(worldIn.getBlockState(blockpos1).getCollisionShape(worldIn, blockpos1))) {
               break;
            }

            ++j;
         }
      }

   }

   public boolean isSolid(BlockState state) {
      return true;
   }

   /**
    * Gets the render layer this block will render on. SOLID for solid blocks, CUTOUT or CUTOUT_MIPPED for on-off
    * transparency (glass, reeds), TRANSLUCENT for fully blended transparency (stained glass)
    */
   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT_MIPPED;
   }
}