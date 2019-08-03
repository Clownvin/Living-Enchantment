package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class MineshaftConfig implements IFeatureConfig {
   public final double probability;
   public final MineshaftStructure.Type type;

   public MineshaftConfig(double probability, MineshaftStructure.Type type) {
      this.probability = probability;
      this.type = type;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214634_1_) {
      return new Dynamic<>(p_214634_1_, p_214634_1_.createMap(ImmutableMap.of(p_214634_1_.createString("probability"), p_214634_1_.createDouble(this.probability), p_214634_1_.createString("type"), p_214634_1_.createString(this.type.func_214714_a()))));
   }

   public static <T> MineshaftConfig deserialize(Dynamic<T> p_214638_0_) {
      float f = p_214638_0_.get("probability").asFloat(0.0F);
      MineshaftStructure.Type mineshaftstructure$type = MineshaftStructure.Type.func_214715_a(p_214638_0_.get("type").asString(""));
      return new MineshaftConfig((double)f, mineshaftstructure$type);
   }
}