package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class TwoFeatureChoiceConfig implements IFeatureConfig {
   public final ConfiguredFeature<?> trueFeature;
   public final ConfiguredFeature<?> falseFeature;

   public TwoFeatureChoiceConfig(ConfiguredFeature<?> p_i51459_1_, ConfiguredFeature<?> p_i51459_2_) {
      this.trueFeature = p_i51459_1_;
      this.falseFeature = p_i51459_2_;
   }

   public TwoFeatureChoiceConfig(Feature<?> trueFeature, IFeatureConfig trueConfig, Feature<?> falseFeature, IFeatureConfig falseConfig) {
      this(func_214646_a(trueFeature, trueConfig), func_214646_a(falseFeature, falseConfig));
   }

   private static <FC extends IFeatureConfig> ConfiguredFeature<FC> func_214646_a(Feature<FC> p_214646_0_, IFeatureConfig p_214646_1_) {
      return new ConfiguredFeature<>(p_214646_0_, (FC)p_214646_1_);
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214634_1_) {
      return new Dynamic<>(p_214634_1_, p_214634_1_.createMap(ImmutableMap.of(p_214634_1_.createString("feature_true"), this.trueFeature.serialize(p_214634_1_).getValue(), p_214634_1_.createString("feature_false"), this.falseFeature.serialize(p_214634_1_).getValue())));
   }

   public static <T> TwoFeatureChoiceConfig deserialize(Dynamic<T> p_214647_0_) {
      ConfiguredFeature<?> configuredfeature = ConfiguredFeature.deserialize(p_214647_0_.get("feature_true").orElseEmptyMap());
      ConfiguredFeature<?> configuredfeature1 = ConfiguredFeature.deserialize(p_214647_0_.get("feature_false").orElseEmptyMap());
      return new TwoFeatureChoiceConfig(configuredfeature, configuredfeature1);
   }
}