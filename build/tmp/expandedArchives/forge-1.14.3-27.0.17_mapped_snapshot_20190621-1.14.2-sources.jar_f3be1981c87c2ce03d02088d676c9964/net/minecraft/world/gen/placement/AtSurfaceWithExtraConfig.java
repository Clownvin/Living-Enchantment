package net.minecraft.world.gen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class AtSurfaceWithExtraConfig implements IPlacementConfig {
   public final int count;
   public final float extraChance;
   public final int extraCount;

   public AtSurfaceWithExtraConfig(int count, float extraChanceIn, int extraCountIn) {
      this.count = count;
      this.extraChance = extraChanceIn;
      this.extraCount = extraCountIn;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214719_1_) {
      return new Dynamic<>(p_214719_1_, p_214719_1_.createMap(ImmutableMap.of(p_214719_1_.createString("count"), p_214719_1_.createInt(this.count), p_214719_1_.createString("extra_chance"), p_214719_1_.createFloat(this.extraChance), p_214719_1_.createString("extra_count"), p_214719_1_.createInt(this.extraCount))));
   }

   public static AtSurfaceWithExtraConfig deserialize(Dynamic<?> p_214723_0_) {
      int i = p_214723_0_.get("count").asInt(0);
      float f = p_214723_0_.get("extra_chance").asFloat(0.0F);
      int j = p_214723_0_.get("extra_count").asInt(0);
      return new AtSurfaceWithExtraConfig(i, f, j);
   }
}