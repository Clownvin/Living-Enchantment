package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class FillLayerFeature extends Feature<FillLayerConfig> {
   public FillLayerFeature(Function<Dynamic<?>, ? extends FillLayerConfig> p_i49877_1_) {
      super(p_i49877_1_);
   }

   public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, FillLayerConfig config) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int i = 0; i < 16; ++i) {
         for(int j = 0; j < 16; ++j) {
            int k = pos.getX() + i;
            int l = pos.getZ() + j;
            int i1 = config.height;
            blockpos$mutableblockpos.setPos(k, i1, l);
            if (worldIn.getBlockState(blockpos$mutableblockpos).isAir()) {
               worldIn.setBlockState(blockpos$mutableblockpos, config.state, 2);
            }
         }
      }

      return true;
   }
}