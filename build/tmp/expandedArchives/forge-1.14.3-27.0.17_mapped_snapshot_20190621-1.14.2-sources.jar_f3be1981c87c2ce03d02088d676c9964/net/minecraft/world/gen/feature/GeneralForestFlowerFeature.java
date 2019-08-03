package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

public class GeneralForestFlowerFeature extends FlowersFeature {
   public GeneralForestFlowerFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i49872_1_) {
      super(p_i49872_1_);
   }

   public BlockState getRandomFlower(Random p_202355_1_, BlockPos p_202355_2_) {
      return Blocks.LILY_OF_THE_VALLEY.getDefaultState();
   }
}