package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;

public class JungleTreeFeature extends TreeFeature {
   public JungleTreeFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51488_1_, boolean p_i51488_2_, int p_i51488_3_, BlockState p_i51488_4_, BlockState p_i51488_5_, boolean p_i51488_6_) {
      super(p_i51488_1_, p_i51488_2_, p_i51488_3_, p_i51488_4_, p_i51488_5_, p_i51488_6_);
      setSapling((net.minecraftforge.common.IPlantable)net.minecraft.block.Blocks.JUNGLE_SAPLING);
   }

   protected int func_208534_a(Random p_208534_1_) {
      return this.minTreeHeight + p_208534_1_.nextInt(7);
   }
}