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
import net.minecraft.world.gen.Heightmap;

public class AtSurfaceRandomCount extends Placement<FrequencyConfig> {
   public AtSurfaceRandomCount(Function<Dynamic<?>, ? extends FrequencyConfig> p_i51370_1_) {
      super(p_i51370_1_);
   }

   public Stream<BlockPos> getPositions(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> p_212848_2_, Random random, FrequencyConfig p_212848_4_, BlockPos pos) {
      int i = random.nextInt(p_212848_4_.count);
      return IntStream.range(0, i).mapToObj((p_215053_3_) -> {
         int j = random.nextInt(16);
         int k = random.nextInt(16);
         return worldIn.getHeight(Heightmap.Type.MOTION_BLOCKING, pos.add(j, 0, k));
      });
   }
}