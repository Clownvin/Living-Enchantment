package net.minecraft.world.gen.feature.template;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class BlockIgnoreStructureProcessor extends StructureProcessor {
   public static final BlockIgnoreStructureProcessor STRUCTURE_BLOCK = new BlockIgnoreStructureProcessor(ImmutableList.of(Blocks.STRUCTURE_BLOCK));
   public static final BlockIgnoreStructureProcessor AIR = new BlockIgnoreStructureProcessor(ImmutableList.of(Blocks.AIR));
   public static final BlockIgnoreStructureProcessor AIR_AND_STRUCTURE_BLOCK = new BlockIgnoreStructureProcessor(ImmutableList.of(Blocks.AIR, Blocks.STRUCTURE_BLOCK));
   private final ImmutableList<Block> blocks;

   public BlockIgnoreStructureProcessor(List<Block> blocks) {
      this.blocks = ImmutableList.copyOf(blocks);
   }

   public BlockIgnoreStructureProcessor(Dynamic<?> p_i51337_1_) {
      this(p_i51337_1_.get("blocks").asList((p_215203_0_) -> {
         return BlockState.deserialize(p_215203_0_).getBlock();
      }));
   }

   @Nullable
   public Template.BlockInfo process(IWorldReader p_215194_1_, BlockPos p_215194_2_, Template.BlockInfo p_215194_3_, Template.BlockInfo p_215194_4_, PlacementSettings p_215194_5_) {
      return this.blocks.contains(p_215194_4_.state.getBlock()) ? null : p_215194_4_;
   }

   protected IStructureProcessorType getType() {
      return IStructureProcessorType.BLOCK_IGNORE;
   }

   protected <T> Dynamic<T> serialize0(DynamicOps<T> p_215193_1_) {
      return new Dynamic<>(p_215193_1_, p_215193_1_.createMap(ImmutableMap.of(p_215193_1_.createString("blocks"), p_215193_1_.createList(this.blocks.stream().map((p_215202_1_) -> {
         return BlockState.serialize(p_215193_1_, p_215202_1_.getDefaultState()).getValue();
      })))));
   }
}