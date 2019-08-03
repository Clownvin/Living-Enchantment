package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class NoFeatureConfig implements IFeatureConfig {
   public <T> Dynamic<T> serialize(DynamicOps<T> p_214634_1_) {
      return new Dynamic<>(p_214634_1_, p_214634_1_.emptyMap());
   }

   public static <T> NoFeatureConfig deserialize(Dynamic<T> p_214639_0_) {
      return IFeatureConfig.NO_FEATURE_CONFIG;
   }
}