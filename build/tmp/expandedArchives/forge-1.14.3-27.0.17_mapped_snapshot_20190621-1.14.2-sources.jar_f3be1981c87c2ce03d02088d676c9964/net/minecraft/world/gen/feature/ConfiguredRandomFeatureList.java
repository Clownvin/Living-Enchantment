package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class ConfiguredRandomFeatureList<FC extends IFeatureConfig> {
   public final Feature<FC> feature;
   public final FC config;
   public final Float chance;

   public ConfiguredRandomFeatureList(Feature<FC> feature, FC config, Float chance) {
      this.feature = feature;
      this.config = config;
      this.chance = chance;
   }

   public ConfiguredRandomFeatureList(Feature<FC> p_i51415_1_, Dynamic<?> p_i51415_2_, float p_i51415_3_) {
      this(p_i51415_1_, p_i51415_1_.createConfig(p_i51415_2_), Float.valueOf(p_i51415_3_));
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214841_1_) {
      return new Dynamic<>(p_214841_1_, p_214841_1_.createMap(ImmutableMap.of(p_214841_1_.createString("name"), p_214841_1_.createString(Registry.FEATURE.getKey(this.feature).toString()), p_214841_1_.createString("config"), this.config.serialize(p_214841_1_).getValue(), p_214841_1_.createString("chance"), p_214841_1_.createFloat(this.chance))));
   }

   public boolean place(IWorld p_214839_1_, ChunkGenerator<? extends GenerationSettings> p_214839_2_, Random p_214839_3_, BlockPos p_214839_4_) {
      return this.feature.place(p_214839_1_, p_214839_2_, p_214839_3_, p_214839_4_, this.config);
   }

   public static <T> ConfiguredRandomFeatureList<?> func_214840_a(Dynamic<T> p_214840_0_) {
      Feature<? extends IFeatureConfig> feature = Registry.FEATURE.getOrDefault(new ResourceLocation(p_214840_0_.get("name").asString("")));
      return new ConfiguredRandomFeatureList<>(feature, p_214840_0_.get("config").orElseEmptyMap(), p_214840_0_.get("chance").asFloat(0.0F));
   }
}