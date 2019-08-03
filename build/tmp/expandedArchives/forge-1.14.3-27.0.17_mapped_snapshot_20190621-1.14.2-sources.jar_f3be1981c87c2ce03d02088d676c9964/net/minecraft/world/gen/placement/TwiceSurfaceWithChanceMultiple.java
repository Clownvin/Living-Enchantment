package net.minecraft.world.gen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;

public class TwiceSurfaceWithChanceMultiple extends Placement<HeightWithChanceConfig> {
   public TwiceSurfaceWithChanceMultiple(Function<Dynamic<?>, ? extends HeightWithChanceConfig> p_i51386_1_) {
      super(p_i51386_1_);
   }

   public Stream<BlockPos> getPositions(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> p_212848_2_, Random random, HeightWithChanceConfig p_212848_4_, BlockPos pos) {
      return IntStream.range(0, p_212848_4_.count).filter((p_215045_2_) -> {
         return random.nextFloat() < p_212848_4_.chance;
      }).mapToObj((p_215044_3_) -> {
         int i = random.nextInt(16);
         int j = random.nextInt(16);
         int k = worldIn.getHeight(Heightmap.Type.MOTION_BLOCKING, pos.add(i, 0, j)).getY() * 2;
         if (k <= 0) {
            return null;
         } else {
            int l = random.nextInt(k);
            return pos.add(i, l, j);
         }
      }).filter(Objects::nonNull);
   }
}