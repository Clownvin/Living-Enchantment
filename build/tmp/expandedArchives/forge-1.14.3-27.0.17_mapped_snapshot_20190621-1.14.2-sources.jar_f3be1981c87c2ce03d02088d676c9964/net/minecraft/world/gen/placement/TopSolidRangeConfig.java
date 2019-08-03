package net.minecraft.world.gen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class TopSolidRangeConfig implements IPlacementConfig {
   public final int min;
   public final int max;

   public TopSolidRangeConfig(int min, int max) {
      this.min = min;
      this.max = max;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214719_1_) {
      return new Dynamic<>(p_214719_1_, p_214719_1_.createMap(ImmutableMap.of(p_214719_1_.createString("min"), p_214719_1_.createInt(this.min), p_214719_1_.createString("max"), p_214719_1_.createInt(this.max))));
   }

   public static TopSolidRangeConfig deserialize(Dynamic<?> p_214725_0_) {
      int i = p_214725_0_.get("min").asInt(0);
      int j = p_214725_0_.get("max").asInt(0);
      return new TopSolidRangeConfig(i, j);
   }
}