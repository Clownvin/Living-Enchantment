package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;

public class LiquidsConfig implements IFeatureConfig {
   public final IFluidState state;

   public LiquidsConfig(IFluidState state) {
      this.state = state;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214634_1_) {
      return new Dynamic<>(p_214634_1_, p_214634_1_.createMap(ImmutableMap.of(p_214634_1_.createString("state"), IFluidState.serialize(p_214634_1_, this.state).getValue())));
   }

   public static <T> LiquidsConfig deserialize(Dynamic<T> p_214677_0_) {
      IFluidState ifluidstate = p_214677_0_.get("state").map(IFluidState::deserialize).orElse(Fluids.EMPTY.getDefaultState());
      return new LiquidsConfig(ifluidstate);
   }
}