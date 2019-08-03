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

public class TopSolidRange extends Placement<TopSolidRangeConfig> {
   public TopSolidRange(Function<Dynamic<?>, ? extends TopSolidRangeConfig> p_i51359_1_) {
      super(p_i51359_1_);
   }

   public Stream<BlockPos> getPositions(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> p_212848_2_, Random random, TopSolidRangeConfig p_212848_4_, BlockPos pos) {
      int i = random.nextInt(p_212848_4_.max - p_212848_4_.min) + p_212848_4_.min;
      return IntStream.range(0, i).mapToObj((p_215064_3_) -> {
         int j = random.nextInt(16);
         int k = random.nextInt(16);
         int l = worldIn.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, pos.getX() + j, pos.getZ() + k);
         return new BlockPos(pos.getX() + j, l, pos.getZ() + k);
      });
   }
}