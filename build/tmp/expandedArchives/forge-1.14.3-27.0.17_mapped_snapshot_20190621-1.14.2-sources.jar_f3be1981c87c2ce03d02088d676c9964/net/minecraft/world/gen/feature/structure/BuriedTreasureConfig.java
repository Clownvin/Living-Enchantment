package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class BuriedTreasureConfig implements IFeatureConfig {
   public final float probability;

   public BuriedTreasureConfig(float probability) {
      this.probability = probability;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214634_1_) {
      return new Dynamic<>(p_214634_1_, p_214634_1_.createMap(ImmutableMap.of(p_214634_1_.createString("probability"), p_214634_1_.createFloat(this.probability))));
   }

   public static <T> BuriedTreasureConfig deserialize(Dynamic<T> p_214684_0_) {
      float f = p_214684_0_.get("probability").asFloat(0.0F);
      return new BuriedTreasureConfig(f);
   }
}