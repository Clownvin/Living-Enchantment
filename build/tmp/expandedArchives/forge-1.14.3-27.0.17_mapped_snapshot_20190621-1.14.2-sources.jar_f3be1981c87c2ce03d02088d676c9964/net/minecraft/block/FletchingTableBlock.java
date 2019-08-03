package net.minecraft.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public class FletchingTableBlock extends CraftingTableBlock {
   protected FletchingTableBlock(Block.Properties p_i49985_1_) {
      super(p_i49985_1_);
   }

   public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
      return false;
   }
}