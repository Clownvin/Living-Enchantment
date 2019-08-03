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

public class TopSolidWithChance extends Placement<ChanceConfig> {
   public TopSolidWithChance(Function<Dynamic<?>, ? extends ChanceConfig> p_i51392_1_) {
      super(p_i51392_1_);
   }

   public Stream<BlockPos> getPositions(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> p_212848_2_, Random random, ChanceConfig p_212848_4_, BlockPos pos) {
      if (random.nextFloat() < 1.0F / (float)p_212848_4_.chance) {
         int i = random.nextInt(16);
         int j = random.nextInt(16);
         int k = worldIn.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, pos.getX() + i, pos.getZ() + j);
         return Stream.of(new BlockPos(pos.getX() + i, k, pos.getZ() + j));
      } else {
         return Stream.empty();
      }
   }
}