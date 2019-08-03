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

public class AtSurfaceWithChance extends Placement<ChanceConfig> {
   public AtSurfaceWithChance(Function<Dynamic<?>, ? extends ChanceConfig> p_i51395_1_) {
      super(p_i51395_1_);
   }

   public Stream<BlockPos> getPositions(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> p_212848_2_, Random random, ChanceConfig p_212848_4_, BlockPos pos) {
      if (random.nextFloat() < 1.0F / (float)p_212848_4_.chance) {
         int i = random.nextInt(16);
         int j = random.nextInt(16);
         BlockPos blockpos = worldIn.getHeight(Heightmap.Type.MOTION_BLOCKING, pos.add(i, 0, j));
         return Stream.of(blockpos);
      } else {
         return Stream.empty();
      }
   }
}