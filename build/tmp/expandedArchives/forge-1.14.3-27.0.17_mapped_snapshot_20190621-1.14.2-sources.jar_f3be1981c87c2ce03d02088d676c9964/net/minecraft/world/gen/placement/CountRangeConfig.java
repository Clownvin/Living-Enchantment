package net.minecraft.world.gen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class CountRangeConfig implements IPlacementConfig {
   public final int count;
   public final int bottomOffset;
   public final int topOffset;
   public final int maximum;

   public CountRangeConfig(int count, int bottomOffset, int topOffset, int maximum) {
      this.count = count;
      this.bottomOffset = bottomOffset;
      this.topOffset = topOffset;
      this.maximum = maximum;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214719_1_) {
      return new Dynamic<>(p_214719_1_, p_214719_1_.createMap(ImmutableMap.of(p_214719_1_.createString("count"), p_214719_1_.createInt(this.count), p_214719_1_.createString("bottom_offset"), p_214719_1_.createInt(this.bottomOffset), p_214719_1_.createString("top_offset"), p_214719_1_.createInt(this.topOffset), p_214719_1_.createString("maximum"), p_214719_1_.createInt(this.maximum))));
   }

   public static CountRangeConfig deserialize(Dynamic<?> p_214733_0_) {
      int i = p_214733_0_.get("count").asInt(0);
      int j = p_214733_0_.get("bottom_offset").asInt(0);
      int k = p_214733_0_.get("top_offset").asInt(0);
      int l = p_214733_0_.get("maximum").asInt(0);
      return new CountRangeConfig(i, j, k, l);
   }
}