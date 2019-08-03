package net.minecraft.world.gen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class ChanceRange extends SimplePlacement<ChanceRangeConfig> {
   public ChanceRange(Function<Dynamic<?>, ? extends ChanceRangeConfig> p_i51358_1_) {
      super(p_i51358_1_);
   }

   public Stream<BlockPos> func_212852_a_(Random p_212852_1_, ChanceRangeConfig p_212852_2_, BlockPos p_212852_3_) {
      if (p_212852_1_.nextFloat() < p_212852_2_.chance) {
         int i = p_212852_1_.nextInt(16);
         int j = p_212852_1_.nextInt(p_212852_2_.top - p_212852_2_.topOffset) + p_212852_2_.bottomOffset;
         int k = p_212852_1_.nextInt(16);
         return Stream.of(p_212852_3_.add(i, j, k));
      } else {
         return Stream.empty();
      }
   }
}