package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

public class DefaultFlowersFeature extends FlowersFeature {
   public DefaultFlowersFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i49889_1_) {
      super(p_i49889_1_);
   }

   public BlockState getRandomFlower(Random p_202355_1_, BlockPos p_202355_2_) {
      return p_202355_1_.nextFloat() > 0.6666667F ? Blocks.DANDELION.getDefaultState() : Blocks.POPPY.getDefaultState();
   }
}