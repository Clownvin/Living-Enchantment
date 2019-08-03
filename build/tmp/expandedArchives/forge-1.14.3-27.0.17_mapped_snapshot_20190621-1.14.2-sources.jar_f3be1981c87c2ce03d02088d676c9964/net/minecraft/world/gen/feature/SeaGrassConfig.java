package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class SeaGrassConfig implements IFeatureConfig {
   public final int count;
   public final double tallProbability;

   public SeaGrassConfig(int count, double tallProbability) {
      this.count = count;
      this.tallProbability = tallProbability;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214634_1_) {
      return new Dynamic<>(p_214634_1_, p_214634_1_.createMap(ImmutableMap.of(p_214634_1_.createString("count"), p_214634_1_.createInt(this.count), p_214634_1_.createString("tall_seagrass_probability"), p_214634_1_.createDouble(this.tallProbability))));
   }

   public static <T> SeaGrassConfig deserialize(Dynamic<T> p_214659_0_) {
      int i = p_214659_0_.get("count").asInt(0);
      double d0 = p_214659_0_.get("tall_seagrass_probability").asDouble(0.0D);
      return new SeaGrassConfig(i, d0);
   }
}