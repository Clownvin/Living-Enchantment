package net.minecraft.world.gen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class TopSolidWithNoise extends Placement<TopSolidWithNoiseConfig> {
   public TopSolidWithNoise(Function<Dynamic<?>, ? extends TopSolidWithNoiseConfig> p_i51360_1_) {
      super(p_i51360_1_);
   }

   public Stream<BlockPos> getPositions(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> p_212848_2_, Random random, TopSolidWithNoiseConfig p_212848_4_, BlockPos pos) {
      double d0 = Biome.INFO_NOISE.getValue((double)pos.getX() / p_212848_4_.noiseFactor, (double)pos.getZ() / p_212848_4_.noiseFactor);
      int i = (int)Math.ceil((d0 + p_212848_4_.noiseOffset) * (double)p_212848_4_.noiseToCountRatio);
      return IntStream.range(0, i).mapToObj((p_215065_4_) -> {
         int j = random.nextInt(16);
         int k = random.nextInt(16);
         int l = worldIn.getHeight(p_212848_4_.heightmap, pos.getX() + j, pos.getZ() + k);
         return new BlockPos(pos.getX() + j, l, pos.getZ() + k);
      });
   }
}