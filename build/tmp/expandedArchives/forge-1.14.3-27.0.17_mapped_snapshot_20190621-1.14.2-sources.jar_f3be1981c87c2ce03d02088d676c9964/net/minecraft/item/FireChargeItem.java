package net.minecraft.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FireChargeItem extends Item {
   public FireChargeItem(Item.Properties builder) {
      super(builder);
   }

   /**
    * Called when this item is used when targetting a Block
    */
   public ActionResultType onItemUse(ItemUseContext context) {
      World world = context.getWorld();
      if (world.isRemote) {
         return ActionResultType.SUCCESS;
      } else {
         BlockPos blockpos = context.getPos();
         BlockState blockstate = world.getBlockState(blockpos);
         if (blockstate.getBlock() == Blocks.CAMPFIRE) {
            if (!blockstate.get(CampfireBlock.LIT) && !blockstate.get(CampfireBlock.WATERLOGGED)) {
               this.func_219995_a(world, blockpos);
               world.setBlockState(blockpos, blockstate.with(CampfireBlock.LIT, Boolean.valueOf(true)));
            }
         } else {
            blockpos = blockpos.offset(context.getFace());
            if (world.getBlockState(blockpos).isAir()) {
               this.func_219995_a(world, blockpos);
               world.setBlockState(blockpos, ((FireBlock)Blocks.FIRE).getStateForPlacement(world, blockpos));
            }
         }

         context.getItem().shrink(1);
         return ActionResultType.SUCCESS;
      }
   }

   private void func_219995_a(World p_219995_1_, BlockPos p_219995_2_) {
      p_219995_1_.playSound((PlayerEntity)null, p_219995_2_, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
   }
}