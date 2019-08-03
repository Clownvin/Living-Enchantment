package net.minecraft.world.gen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.gen.GenerationStage;

public class CaveEdgeConfig implements IPlacementConfig {
   protected final GenerationStage.Carving step;
   protected final float probability;

   public CaveEdgeConfig(GenerationStage.Carving step, float probability) {
      this.step = step;
      this.probability = probability;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214719_1_) {
      return new Dynamic<>(p_214719_1_, p_214719_1_.createMap(ImmutableMap.of(p_214719_1_.createString("step"), p_214719_1_.createString(this.step.toString()), p_214719_1_.createString("probability"), p_214719_1_.createFloat(this.probability))));
   }

   public static CaveEdgeConfig func_214720_a(Dynamic<?> p_214720_0_) {
      GenerationStage.Carving generationstage$carving = GenerationStage.Carving.valueOf(p_214720_0_.get("step").asString(""));
      float f = p_214720_0_.get("probability").asFloat(0.0F);
      return new CaveEdgeConfig(generationstage$carving, f);
   }
}