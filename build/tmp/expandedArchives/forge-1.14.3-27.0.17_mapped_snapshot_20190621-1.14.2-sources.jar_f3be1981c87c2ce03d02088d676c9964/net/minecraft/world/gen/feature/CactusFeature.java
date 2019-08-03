package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class CactusFeature extends Feature<NoFeatureConfig> {
   public CactusFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> configFactoryIn) {
      super(configFactoryIn);
   }

   public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config) {
      for(int i = 0; i < 10; ++i) {
         BlockPos blockpos = pos.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
         if (worldIn.isAirBlock(blockpos)) {
            int j = 1 + rand.nextInt(rand.nextInt(3) + 1);

            for(int k = 0; k < j; ++k) {
               if (Blocks.CACTUS.getDefaultState().isValidPosition(worldIn, blockpos)) {
                  worldIn.setBlockState(blockpos.up(k), Blocks.CACTUS.getDefaultState(), 2);
               }
            }
         }
      }

      return true;
   }
}