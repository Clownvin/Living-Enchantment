package net.minecraft.item;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.world.IWorldReader;

public class WallOrFloorItem extends BlockItem {
   protected final Block wallBlock;

   public WallOrFloorItem(Block floorBlock, Block wallBlockIn, Item.Properties p_i48462_3_) {
      super(floorBlock, p_i48462_3_);
      this.wallBlock = wallBlockIn;
   }

   @Nullable
   protected BlockState getStateForPlacement(BlockItemUseContext context) {
      BlockState blockstate = this.wallBlock.getStateForPlacement(context);
      BlockState blockstate1 = null;
      IWorldReader iworldreader = context.getWorld();
      BlockPos blockpos = context.getPos();

      for(Direction direction : context.getNearestLookingDirections()) {
         if (direction != Direction.UP) {
            BlockState blockstate2 = direction == Direction.DOWN ? this.getBlock().getStateForPlacement(context) : blockstate;
            if (blockstate2 != null && blockstate2.isValidPosition(iworldreader, blockpos)) {
               blockstate1 = blockstate2;
               break;
            }
         }
      }

      return blockstate1 != null && iworldreader.func_217350_a(blockstate1, blockpos, ISelectionContext.dummy()) ? blockstate1 : null;
   }

   public void addToBlockToItemMap(Map<Block, Item> blockToItemMap, Item itemIn) {
      super.addToBlockToItemMap(blockToItemMap, itemIn);
      blockToItemMap.put(this.wallBlock, itemIn);
   }
}