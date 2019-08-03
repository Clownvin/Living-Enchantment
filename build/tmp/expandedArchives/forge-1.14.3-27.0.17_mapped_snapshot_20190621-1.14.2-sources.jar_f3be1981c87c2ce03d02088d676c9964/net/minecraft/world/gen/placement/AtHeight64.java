package net.minecraft.world.gen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class AtHeight64 extends Placement<FrequencyConfig> {
   public AtHeight64(Function<Dynamic<?>, ? extends FrequencyConfig> p_i51383_1_) {
      super(p_i51383_1_);
   }

   public Stream<BlockPos> getPositions(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> p_212848_2_, Random random, FrequencyConfig p_212848_4_, BlockPos pos) {
      return IntStream.range(0, p_212848_4_.count).mapToObj((p_215048_2_) -> {
         int i = random.nextInt(16);
         int j = 64;
         int k = random.nextInt(16);
         return pos.add(i, 64, k);
      });
   }
}