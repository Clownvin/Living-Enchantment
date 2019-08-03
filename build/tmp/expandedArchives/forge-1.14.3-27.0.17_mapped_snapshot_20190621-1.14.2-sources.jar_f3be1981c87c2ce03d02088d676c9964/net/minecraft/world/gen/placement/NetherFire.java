package net.minecraft.world.gen.placement;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class NetherFire extends SimplePlacement<FrequencyConfig> {
   public NetherFire(Function<Dynamic<?>, ? extends FrequencyConfig> p_i51356_1_) {
      super(p_i51356_1_);
   }

   public Stream<BlockPos> func_212852_a_(Random p_212852_1_, FrequencyConfig p_212852_2_, BlockPos p_212852_3_) {
      List<BlockPos> list = Lists.newArrayList();

      for(int i = 0; i < p_212852_1_.nextInt(p_212852_1_.nextInt(p_212852_2_.count) + 1) + 1; ++i) {
         int j = p_212852_1_.nextInt(16);
         int k = p_212852_1_.nextInt(120) + 4;
         int l = p_212852_1_.nextInt(16);
         list.add(p_212852_3_.add(j, k, l));
      }

      return list.stream();
   }
}