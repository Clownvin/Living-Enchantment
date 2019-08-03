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

public class NetherMagma extends Placement<FrequencyConfig> {
   public NetherMagma(Function<Dynamic<?>, ? extends FrequencyConfig> p_i51354_1_) {
      super(p_i51354_1_);
   }

   public Stream<BlockPos> getPositions(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> p_212848_2_, Random random, FrequencyConfig p_212848_4_, BlockPos pos) {
      int i = worldIn.getSeaLevel() / 2 + 1;
      return IntStream.range(0, p_212848_4_.count).mapToObj((p_215066_3_) -> {
         int j = random.nextInt(16);
         int k = i - 5 + random.nextInt(10);
         int l = random.nextInt(16);
         return pos.add(j, k, l);
      });
   }
}