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

public class ScatteredPlantFeature extends Feature<NoFeatureConfig> {
   protected final BlockState field_214623_a;

   public ScatteredPlantFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i49906_1_, BlockState p_i49906_2_) {
      super(p_i49906_1_);
      this.field_214623_a = p_i49906_2_;
   }

   public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config) {
      int i = 0;

      for(int j = 0; j < 64; ++j) {
         BlockPos blockpos = pos.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
         if (worldIn.isAirBlock(blockpos) && worldIn.getBlockState(blockpos.down()).getBlock() == Blocks.GRASS_BLOCK) {
            worldIn.setBlockState(blockpos, this.field_214623_a, 2);
            ++i;
         }
      }

      return i > 0;
   }
}