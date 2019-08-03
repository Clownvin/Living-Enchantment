package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class BlockBlobConfig implements IFeatureConfig {
   public final BlockState state;
   public final int startRadius;

   public BlockBlobConfig(BlockState state, int startRadius) {
      this.state = state;
      this.startRadius = startRadius;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214634_1_) {
      return new Dynamic<>(p_214634_1_, p_214634_1_.createMap(ImmutableMap.of(p_214634_1_.createString("state"), BlockState.serialize(p_214634_1_, this.state).getValue(), p_214634_1_.createString("start_radius"), p_214634_1_.createInt(this.startRadius))));
   }

   public static <T> BlockBlobConfig deserialize(Dynamic<T> p_214682_0_) {
      BlockState blockstate = p_214682_0_.get("state").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
      int i = p_214682_0_.get("start_radius").asInt(0);
      return new BlockBlobConfig(blockstate, i);
   }
}