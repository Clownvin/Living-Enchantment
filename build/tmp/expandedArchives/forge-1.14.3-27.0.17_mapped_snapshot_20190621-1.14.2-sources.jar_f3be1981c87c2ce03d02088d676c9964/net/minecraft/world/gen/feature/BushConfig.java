package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class BushConfig implements IFeatureConfig {
   public final BlockState state;

   public BushConfig(BlockState state) {
      this.state = state;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214634_1_) {
      return new Dynamic<>(p_214634_1_, p_214634_1_.createMap(ImmutableMap.of(p_214634_1_.createString("state"), BlockState.serialize(p_214634_1_, this.state).getValue())));
   }

   public static <T> BushConfig deserialize(Dynamic<T> p_214685_0_) {
      BlockState blockstate = p_214685_0_.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
      return new BushConfig(blockstate);
   }
}