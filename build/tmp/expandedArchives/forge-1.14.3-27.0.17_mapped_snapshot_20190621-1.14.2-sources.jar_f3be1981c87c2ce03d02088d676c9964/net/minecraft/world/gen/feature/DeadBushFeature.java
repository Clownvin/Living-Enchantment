package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DeadBushBlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class DeadBushFeature extends Feature<NoFeatureConfig> {
   private static final DeadBushBlock DEAD_BUSH_BLOCK = (DeadBushBlock)Blocks.DEAD_BUSH;

   public DeadBushFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i49894_1_) {
      super(p_i49894_1_);
   }

   public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config) {
      for(BlockState blockstate = worldIn.getBlockState(pos); (blockstate.isAir(worldIn, pos) || blockstate.isIn(BlockTags.LEAVES)) && pos.getY() > 0; blockstate = worldIn.getBlockState(pos)) {
         pos = pos.down();
      }

      BlockState blockstate1 = DEAD_BUSH_BLOCK.getDefaultState();

      for(int i = 0; i < 4; ++i) {
         BlockPos blockpos = pos.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
         if (worldIn.isAirBlock(blockpos) && blockstate1.isValidPosition(worldIn, blockpos)) {
            worldIn.setBlockState(blockpos, blockstate1, 2);
         }
      }

      return true;
   }
}