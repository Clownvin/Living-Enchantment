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

public class TopSolid extends Placement<FrequencyConfig> {
   public TopSolid(Function<Dynamic<?>, ? extends FrequencyConfig> p_i51380_1_) {
      super(p_i51380_1_);
   }

   public Stream<BlockPos> getPositions(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> p_212848_2_, Random random, FrequencyConfig p_212848_4_, BlockPos pos) {
      return IntStream.range(0, p_212848_4_.count).mapToObj((p_215049_3_) -> {
         int i = random.nextInt(16) + pos.getX();
         int j = random.nextInt(16) + pos.getZ();
         return new BlockPos(i, worldIn.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, i, j), j);
      });
   }
}