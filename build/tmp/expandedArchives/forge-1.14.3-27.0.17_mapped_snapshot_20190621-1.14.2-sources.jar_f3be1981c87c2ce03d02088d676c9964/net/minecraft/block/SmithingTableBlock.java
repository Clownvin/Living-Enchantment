package net.minecraft.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public class SmithingTableBlock extends CraftingTableBlock {
   protected SmithingTableBlock(Block.Properties p_i49974_1_) {
      super(p_i49974_1_);
   }

   public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
      return false;
   }
}