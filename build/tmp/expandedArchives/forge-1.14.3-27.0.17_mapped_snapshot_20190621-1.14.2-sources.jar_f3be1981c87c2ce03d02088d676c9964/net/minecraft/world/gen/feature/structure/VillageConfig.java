package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class VillageConfig implements IFeatureConfig {
   public final ResourceLocation startPool;
   public final int size;

   public VillageConfig(String startPool, int size) {
      this.startPool = new ResourceLocation(startPool);
      this.size = size;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214634_1_) {
      return new Dynamic<>(p_214634_1_, p_214634_1_.createMap(ImmutableMap.of(p_214634_1_.createString("start_pool"), p_214634_1_.createString(this.startPool.toString()), p_214634_1_.createString("size"), p_214634_1_.createInt(this.size))));
   }

   public static <T> VillageConfig deserialize(Dynamic<T> p_214679_0_) {
      String s = p_214679_0_.get("start_pool").asString("");
      int i = p_214679_0_.get("size").asInt(6);
      return new VillageConfig(s, i);
   }
}