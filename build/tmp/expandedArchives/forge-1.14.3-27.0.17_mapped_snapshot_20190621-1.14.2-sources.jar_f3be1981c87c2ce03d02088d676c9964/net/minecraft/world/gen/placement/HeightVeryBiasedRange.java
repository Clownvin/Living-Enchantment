package net.minecraft.world.gen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class HeightVeryBiasedRange extends SimplePlacement<CountRangeConfig> {
   public HeightVeryBiasedRange(Function<Dynamic<?>, ? extends CountRangeConfig> p_i51379_1_) {
      super(p_i51379_1_);
   }

   public Stream<BlockPos> func_212852_a_(Random p_212852_1_, CountRangeConfig p_212852_2_, BlockPos p_212852_3_) {
      return IntStream.range(0, p_212852_2_.count).mapToObj((p_215059_3_) -> {
         int i = p_212852_1_.nextInt(16);
         int j = p_212852_1_.nextInt(16);
         int k = p_212852_1_.nextInt(p_212852_1_.nextInt(p_212852_1_.nextInt(p_212852_2_.maximum - p_212852_2_.topOffset) + p_212852_2_.bottomOffset) + p_212852_2_.bottomOffset);
         return p_212852_3_.add(i, k, j);
      });
   }
}