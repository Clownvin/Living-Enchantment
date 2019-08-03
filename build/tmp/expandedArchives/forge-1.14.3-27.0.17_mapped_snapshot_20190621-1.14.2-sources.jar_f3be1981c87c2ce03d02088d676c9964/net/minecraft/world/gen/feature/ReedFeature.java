package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Blocks;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class ReedFeature extends Feature<NoFeatureConfig> {
   public ReedFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51446_1_) {
      super(p_i51446_1_);
   }

   public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config) {
      int i = 0;

      for(int j = 0; j < 20; ++j) {
         BlockPos blockpos = pos.add(rand.nextInt(4) - rand.nextInt(4), 0, rand.nextInt(4) - rand.nextInt(4));
         if (worldIn.isAirBlock(blockpos)) {
            BlockPos blockpos1 = blockpos.down();
            if (worldIn.getFluidState(blockpos1.west()).isTagged(FluidTags.WATER) || worldIn.getFluidState(blockpos1.east()).isTagged(FluidTags.WATER) || worldIn.getFluidState(blockpos1.north()).isTagged(FluidTags.WATER) || worldIn.getFluidState(blockpos1.south()).isTagged(FluidTags.WATER)) {
               int k = 2 + rand.nextInt(rand.nextInt(3) + 1);

               for(int l = 0; l < k; ++l) {
                  if (Blocks.SUGAR_CANE.getDefaultState().isValidPosition(worldIn, blockpos)) {
                     worldIn.setBlockState(blockpos.up(l), Blocks.SUGAR_CANE.getDefaultState(), 2);
                     ++i;
                  }
               }
            }
         }
      }

      return i > 0;
   }
}