package net.minecraft.world.gen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;

public class TopSolidOnce extends Placement<NoPlacementConfig> {
   public TopSolidOnce(Function<Dynamic<?>, ? extends NoPlacementConfig> p_i51361_1_) {
      super(p_i51361_1_);
   }

   public Stream<BlockPos> getPositions(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> p_212848_2_, Random random, NoPlacementConfig p_212848_4_, BlockPos pos) {
      int i = random.nextInt(16);
      int j = random.nextInt(16);
      int k = worldIn.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, pos.getX() + i, pos.getZ() + j);
      return Stream.of(new BlockPos(pos.getX() + i, k, pos.getZ() + j));
   }
}