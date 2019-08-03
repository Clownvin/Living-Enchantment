package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public abstract class BlockPileFeature extends Feature<NoFeatureConfig> {
   public BlockPileFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i49914_1_) {
      super(p_i49914_1_);
   }

   public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config) {
      if (pos.getY() < 5) {
         return false;
      } else {
         int i = 2 + rand.nextInt(2);
         int j = 2 + rand.nextInt(2);

         for(BlockPos blockpos : BlockPos.getAllInBoxMutable(pos.add(-i, 0, -j), pos.add(i, 1, j))) {
            int k = pos.getX() - blockpos.getX();
            int l = pos.getZ() - blockpos.getZ();
            if ((float)(k * k + l * l) <= rand.nextFloat() * 10.0F - rand.nextFloat() * 6.0F) {
               this.func_214622_b(worldIn, blockpos, rand);
            } else if ((double)rand.nextFloat() < 0.031D) {
               this.func_214622_b(worldIn, blockpos, rand);
            }
         }

         return true;
      }
   }

   private boolean func_214621_a(IWorld p_214621_1_, BlockPos p_214621_2_, Random p_214621_3_) {
      BlockPos blockpos = p_214621_2_.down();
      BlockState blockstate = p_214621_1_.getBlockState(blockpos);
      return blockstate.getBlock() == Blocks.GRASS_PATH ? p_214621_3_.nextBoolean() : Block.hasSolidSide(blockstate, p_214621_1_, blockpos, Direction.UP);
   }

   private void func_214622_b(IWorld p_214622_1_, BlockPos p_214622_2_, Random p_214622_3_) {
      if (p_214622_1_.isAirBlock(p_214622_2_) && this.func_214621_a(p_214622_1_, p_214622_2_, p_214622_3_)) {
         p_214622_1_.setBlockState(p_214622_2_, this.func_214620_a(p_214622_1_), 4);
      }

   }

   protected abstract BlockState func_214620_a(IWorld p_214620_1_);
}