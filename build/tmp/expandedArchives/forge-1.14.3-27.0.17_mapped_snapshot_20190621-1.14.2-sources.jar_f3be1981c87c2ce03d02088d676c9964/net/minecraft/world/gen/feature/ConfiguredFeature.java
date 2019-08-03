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

public class ConfiguredFeature<FC extends IFeatureConfig> {
   public final Feature<FC> feature;
   public final FC config;

   public ConfiguredFeature(Feature<FC> featureIn, FC configIn) {
      this.feature = featureIn;
      this.config = configIn;
   }

   public ConfiguredFeature(Feature<FC> p_i49901_1_, Dynamic<?> dynamicIn) {
      this(p_i49901_1_, p_i49901_1_.createConfig(dynamicIn));
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> opsIn) {
      return new Dynamic<>(opsIn, opsIn.createMap(ImmutableMap.of(opsIn.createString("name"), opsIn.createString(Registry.FEATURE.getKey(this.feature).toString()), opsIn.createString("config"), this.config.serialize(opsIn).getValue())));
   }

   public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos) {
      return this.feature.place(worldIn, generator, rand, pos, this.config);
   }

   public static <T> ConfiguredFeature<?> deserialize(Dynamic<T> p_222736_0_) {
      Feature<? extends IFeatureConfig> feature = Registry.FEATURE.getOrDefault(new ResourceLocation(p_222736_0_.get("name").asString("")));
      return new ConfiguredFeature<>(feature, p_222736_0_.get("config").orElseEmptyMap());
   }
}