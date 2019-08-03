package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.util.Direction;
import net.minecraft.world.IWorld;

public class HayBlockPileFeature extends BlockPileFeature {
   public HayBlockPileFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i49867_1_) {
      super(p_i49867_1_);
   }

   protected BlockState func_214620_a(IWorld p_214620_1_) {
      Direction.Axis direction$axis = Direction.Axis.func_218393_a(p_214620_1_.getRandom());
      return Blocks.HAY_BLOCK.getDefaultState().with(RotatedPillarBlock.AXIS, direction$axis);
   }
}