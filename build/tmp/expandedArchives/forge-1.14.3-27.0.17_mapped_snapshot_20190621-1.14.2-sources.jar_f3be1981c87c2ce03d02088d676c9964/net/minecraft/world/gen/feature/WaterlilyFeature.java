package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class WaterlilyFeature extends Feature<NoFeatureConfig> {
   public WaterlilyFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51416_1_) {
      super(p_i51416_1_);
   }

   public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config) {
      BlockPos blockpos1;
      for(BlockPos blockpos = pos; blockpos.getY() > 0; blockpos = blockpos1) {
         blockpos1 = blockpos.down();
         if (!worldIn.isAirBlock(blockpos1)) {
            break;
         }
      }

      for(int i = 0; i < 10; ++i) {
         BlockPos blockpos2 = pos.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
         BlockState blockstate = Blocks.LILY_PAD.getDefaultState();
         if (worldIn.isAirBlock(blockpos2) && blockstate.isValidPosition(worldIn, blockpos2)) {
            worldIn.setBlockState(blockpos2, blockstate, 2);
         }
      }

      return true;
   }
}