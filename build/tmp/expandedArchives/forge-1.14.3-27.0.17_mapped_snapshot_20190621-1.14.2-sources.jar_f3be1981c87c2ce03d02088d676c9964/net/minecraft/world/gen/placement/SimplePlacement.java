package net.minecraft.world.gen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public abstract class SimplePlacement<DC extends IPlacementConfig> extends Placement<DC> {
   public SimplePlacement(Function<Dynamic<?>, ? extends DC> p_i51362_1_) {
      super(p_i51362_1_);
   }

   public final Stream<BlockPos> getPositions(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> p_212848_2_, Random random, DC p_212848_4_, BlockPos pos) {
      return this.func_212852_a_(random, p_212848_4_, pos);
   }

   protected abstract Stream<BlockPos> func_212852_a_(Random p_212852_1_, DC p_212852_2_, BlockPos p_212852_3_);
}