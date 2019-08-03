package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class JungleGrassFeature extends Feature<NoFeatureConfig> {
   public JungleGrassFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51490_1_) {
      super(p_i51490_1_);
   }

   public BlockState func_202357_a(Random p_202357_1_) {
      return p_202357_1_.nextInt(4) == 0 ? Blocks.FERN.getDefaultState() : Blocks.GRASS.getDefaultState();
   }

   public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config) {
      BlockState blockstate = this.func_202357_a(rand);

      for(BlockState blockstate1 = worldIn.getBlockState(pos); (blockstate1.isAir(worldIn, pos) || blockstate1.isIn(BlockTags.LEAVES)) && pos.getY() > 0; blockstate1 = worldIn.getBlockState(pos)) {
         pos = pos.down();
      }

      int i = 0;

      for(int j = 0; j < 128; ++j) {
         BlockPos blockpos = pos.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
         if (worldIn.isAirBlock(blockpos) && worldIn.getBlockState(blockpos.down()).getBlock() != Blocks.PODZOL && blockstate.isValidPosition(worldIn, blockpos)) {
            worldIn.setBlockState(blockpos, blockstate, 2);
            ++i;
         }
      }

      return i > 0;
   }
}