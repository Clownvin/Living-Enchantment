package net.minecraft.world.gen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class ChanceRangeConfig implements IPlacementConfig {
   public final float chance;
   public final int bottomOffset;
   public final int topOffset;
   public final int top;

   public ChanceRangeConfig(float chance, int bottomOffset, int topOffset, int top) {
      this.chance = chance;
      this.bottomOffset = bottomOffset;
      this.topOffset = topOffset;
      this.top = top;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214719_1_) {
      return new Dynamic<>(p_214719_1_, p_214719_1_.createMap(ImmutableMap.of(p_214719_1_.createString("chance"), p_214719_1_.createFloat(this.chance), p_214719_1_.createString("bottom_offset"), p_214719_1_.createInt(this.bottomOffset), p_214719_1_.createString("top_offset"), p_214719_1_.createInt(this.topOffset), p_214719_1_.createString("top"), p_214719_1_.createInt(this.top))));
   }

   public static ChanceRangeConfig deserialize(Dynamic<?> p_214732_0_) {
      float f = p_214732_0_.get("chance").asFloat(0.0F);
      int i = p_214732_0_.get("bottom_offset").asInt(0);
      int j = p_214732_0_.get("top_offset").asInt(0);
      int k = p_214732_0_.get("top").asInt(0);
      return new ChanceRangeConfig(f, i, j, k);
   }
}