package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class HellLavaConfig implements IFeatureConfig {
   public final boolean insideRock;

   public HellLavaConfig(boolean insideRock) {
      this.insideRock = insideRock;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214634_1_) {
      return new Dynamic<>(p_214634_1_, p_214634_1_.createMap(ImmutableMap.of(p_214634_1_.createString("inside_rock"), p_214634_1_.createBoolean(this.insideRock))));
   }

   public static <T> HellLavaConfig deserialize(Dynamic<T> p_214709_0_) {
      boolean flag = p_214709_0_.get("inside_rock").asBoolean(false);
      return new HellLavaConfig(flag);
   }
}