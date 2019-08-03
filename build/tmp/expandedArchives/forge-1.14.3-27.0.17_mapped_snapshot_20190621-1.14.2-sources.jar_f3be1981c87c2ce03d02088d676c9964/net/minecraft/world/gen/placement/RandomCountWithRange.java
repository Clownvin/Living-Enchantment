package net.minecraft.world.gen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class RandomCountWithRange extends SimplePlacement<CountRangeConfig> {
   public RandomCountWithRange(Function<Dynamic<?>, ? extends CountRangeConfig> p_i51353_1_) {
      super(p_i51353_1_);
   }

   public Stream<BlockPos> func_212852_a_(Random p_212852_1_, CountRangeConfig p_212852_2_, BlockPos p_212852_3_) {
      int i = p_212852_1_.nextInt(Math.max(p_212852_2_.count, 1));
      return IntStream.range(0, i).mapToObj((p_215063_3_) -> {
         int j = p_212852_1_.nextInt(16);
         int k = p_212852_1_.nextInt(p_212852_2_.maximum - p_212852_2_.topOffset) + p_212852_2_.bottomOffset;
         int l = p_212852_1_.nextInt(16);
         return p_212852_3_.add(j, k, l);
      });
   }
}