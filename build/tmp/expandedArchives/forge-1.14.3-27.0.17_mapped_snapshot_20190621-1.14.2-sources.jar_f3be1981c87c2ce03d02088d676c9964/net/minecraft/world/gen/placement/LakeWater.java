package net.minecraft.world.gen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class LakeWater extends Placement<LakeChanceConfig> {
   public LakeWater(Function<Dynamic<?>, ? extends LakeChanceConfig> p_i51367_1_) {
      super(p_i51367_1_);
   }

   public Stream<BlockPos> getPositions(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> p_212848_2_, Random random, LakeChanceConfig p_212848_4_, BlockPos pos) {
      if (random.nextInt(p_212848_4_.chance) == 0) {
         int i = random.nextInt(16);
         int j = random.nextInt(p_212848_2_.getMaxHeight());
         int k = random.nextInt(16);
         return Stream.of(pos.add(i, j, k));
      } else {
         return Stream.empty();
      }
   }
}