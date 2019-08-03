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

public class DarkOakTreePlacement extends Placement<NoPlacementConfig> {
   public DarkOakTreePlacement(Function<Dynamic<?>, ? extends NoPlacementConfig> p_i51377_1_) {
      super(p_i51377_1_);
   }

   public Stream<BlockPos> getPositions(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> p_212848_2_, Random random, NoPlacementConfig p_212848_4_, BlockPos pos) {
      return IntStream.range(0, 16).mapToObj((p_215052_3_) -> {
         int i = p_215052_3_ / 4;
         int j = p_215052_3_ % 4;
         int k = i * 4 + 1 + random.nextInt(3);
         int l = j * 4 + 1 + random.nextInt(3);
         return worldIn.getHeight(Heightmap.Type.MOTION_BLOCKING, pos.add(k, 0, l));
      });
   }
}