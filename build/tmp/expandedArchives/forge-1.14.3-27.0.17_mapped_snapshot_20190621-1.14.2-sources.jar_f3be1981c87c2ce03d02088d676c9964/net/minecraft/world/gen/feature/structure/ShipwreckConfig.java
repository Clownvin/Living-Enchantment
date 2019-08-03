package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class ShipwreckConfig implements IFeatureConfig {
   public final boolean isBeached;

   public ShipwreckConfig(boolean p_i48900_1_) {
      this.isBeached = p_i48900_1_;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214634_1_) {
      return new Dynamic<>(p_214634_1_, p_214634_1_.createMap(ImmutableMap.of(p_214634_1_.createString("is_beached"), p_214634_1_.createBoolean(this.isBeached))));
   }

   public static <T> ShipwreckConfig deserialize(Dynamic<T> p_214658_0_) {
      boolean flag = p_214658_0_.get("is_beached").asBoolean(false);
      return new ShipwreckConfig(flag);
   }
}