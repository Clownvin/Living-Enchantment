package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class FillLayerConfig implements IFeatureConfig {
   public final int height;
   public final BlockState state;

   public FillLayerConfig(int height, BlockState state) {
      this.height = height;
      this.state = state;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214634_1_) {
      return new Dynamic<>(p_214634_1_, p_214634_1_.createMap(ImmutableMap.of(p_214634_1_.createString("height"), p_214634_1_.createInt(this.height), p_214634_1_.createString("state"), BlockState.serialize(p_214634_1_, this.state).getValue())));
   }

   public static <T> FillLayerConfig deserialize(Dynamic<T> p_214635_0_) {
      int i = p_214635_0_.get("height").asInt(0);
      BlockState blockstate = p_214635_0_.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
      return new FillLayerConfig(i, blockstate);
   }
}