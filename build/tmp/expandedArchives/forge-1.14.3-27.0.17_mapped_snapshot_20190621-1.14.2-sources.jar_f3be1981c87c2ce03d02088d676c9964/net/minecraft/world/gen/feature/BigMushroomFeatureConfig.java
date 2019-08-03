package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class BigMushroomFeatureConfig implements IFeatureConfig {
   public final boolean planted;

   public BigMushroomFeatureConfig(boolean planted) {
      this.planted = planted;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214634_1_) {
      return new Dynamic<>(p_214634_1_, p_214634_1_.createMap(ImmutableMap.of(p_214634_1_.createString("planted"), p_214634_1_.createBoolean(this.planted))));
   }

   public static <T> BigMushroomFeatureConfig deserialize(Dynamic<T> p_222853_0_) {
      boolean flag = p_222853_0_.get("planted").asBoolean(false);
      return new BigMushroomFeatureConfig(flag);
   }
}