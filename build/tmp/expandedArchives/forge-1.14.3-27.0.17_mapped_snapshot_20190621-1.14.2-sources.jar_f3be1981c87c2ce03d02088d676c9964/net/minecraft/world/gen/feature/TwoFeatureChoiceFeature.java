package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class TwoFeatureChoiceFeature extends Feature<TwoFeatureChoiceConfig> {
   public TwoFeatureChoiceFeature(Function<Dynamic<?>, ? extends TwoFeatureChoiceConfig> p_i51457_1_) {
      super(p_i51457_1_);
   }

   public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, TwoFeatureChoiceConfig config) {
      boolean flag = rand.nextBoolean();
      return flag ? config.trueFeature.place(worldIn, generator, rand, pos) : config.falseFeature.place(worldIn, generator, rand, pos);
   }
}