package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.IWorld;

public class SnowBlockPileFeature extends BlockPileFeature {
   public SnowBlockPileFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51434_1_) {
      super(p_i51434_1_);
   }

   protected BlockState func_214620_a(IWorld p_214620_1_) {
      return Blocks.SNOW_BLOCK.getDefaultState();
   }
}