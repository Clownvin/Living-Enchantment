package net.minecraft.world.gen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class HeightWithChanceConfig implements IPlacementConfig {
   public final int count;
   public final float chance;

   public HeightWithChanceConfig(int count, float chance) {
      this.count = count;
      this.chance = chance;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214719_1_) {
      return new Dynamic<>(p_214719_1_, p_214719_1_.createMap(ImmutableMap.of(p_214719_1_.createString("count"), p_214719_1_.createInt(this.count), p_214719_1_.createString("chance"), p_214719_1_.createFloat(this.chance))));
   }

   public static HeightWithChanceConfig deserialize(Dynamic<?> p_214724_0_) {
      int i = p_214724_0_.get("count").asInt(0);
      float f = p_214724_0_.get("chance").asFloat(0.0F);
      return new HeightWithChanceConfig(i, f);
   }
}