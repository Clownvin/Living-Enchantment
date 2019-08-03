package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class BlockWithContextConfig implements IFeatureConfig {
   protected final BlockState toPlace;
   protected final List<BlockState> placeOn;
   protected final List<BlockState> placeIn;
   protected final List<BlockState> placeUnder;

   public BlockWithContextConfig(BlockState toPlace, List<BlockState> placeOn, List<BlockState> placeIn, List<BlockState> placeUnder) {
      this.toPlace = toPlace;
      this.placeOn = placeOn;
      this.placeIn = placeIn;
      this.placeUnder = placeUnder;
   }

   public BlockWithContextConfig(BlockState state, BlockState[] placeOn, BlockState[] placeIn, BlockState[] placeUnder) {
      this(state, Lists.newArrayList(placeOn), Lists.newArrayList(placeIn), Lists.newArrayList(placeUnder));
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214634_1_) {
      T t = BlockState.serialize(p_214634_1_, this.toPlace).getValue();
      T t1 = p_214634_1_.createList(this.placeOn.stream().map((p_214662_1_) -> {
         return BlockState.serialize(p_214634_1_, p_214662_1_).getValue();
      }));
      T t2 = p_214634_1_.createList(this.placeIn.stream().map((p_214661_1_) -> {
         return BlockState.serialize(p_214634_1_, p_214661_1_).getValue();
      }));
      T t3 = p_214634_1_.createList(this.placeUnder.stream().map((p_214660_1_) -> {
         return BlockState.serialize(p_214634_1_, p_214660_1_).getValue();
      }));
      return new Dynamic<>(p_214634_1_, p_214634_1_.createMap(ImmutableMap.of(p_214634_1_.createString("to_place"), t, p_214634_1_.createString("place_on"), t1, p_214634_1_.createString("place_in"), t2, p_214634_1_.createString("place_under"), t3)));
   }

   public static <T> BlockWithContextConfig deserialize(Dynamic<T> p_214663_0_) {
      BlockState blockstate = p_214663_0_.get("to_place").map(BlockState::deserialize).orElse(Blocks.AIR.getDefaultState());
      List<BlockState> list = p_214663_0_.get("place_on").asList(BlockState::deserialize);
      List<BlockState> list1 = p_214663_0_.get("place_in").asList(BlockState::deserialize);
      List<BlockState> list2 = p_214663_0_.get("place_under").asList(BlockState::deserialize);
      return new BlockWithContextConfig(blockstate, list, list1, list2);
   }
}