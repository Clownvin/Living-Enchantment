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

public class SurfacePlus32 extends Placement<FrequencyConfig> {
   public SurfacePlus32(Function<Dynamic<?>, ? extends FrequencyConfig> p_i51382_1_) {
      super(p_i51382_1_);
   }

   public Stream<BlockPos> getPositions(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> p_212848_2_, Random random, FrequencyConfig p_212848_4_, BlockPos pos) {
      return IntStream.range(0, p_212848_4_.count).mapToObj((p_215047_3_) -> {
         int i = random.nextInt(16);
         int j = random.nextInt(16);
         int k = worldIn.getHeight(Heightmap.Type.MOTION_BLOCKING, pos.add(i, 0, j)).getY() + 32;
         if (k <= 0) {
            return null;
         } else {
            int l = random.nextInt(k);
            return pos.add(i, l, j);
         }
      }).filter(Objects::nonNull);
   }
}