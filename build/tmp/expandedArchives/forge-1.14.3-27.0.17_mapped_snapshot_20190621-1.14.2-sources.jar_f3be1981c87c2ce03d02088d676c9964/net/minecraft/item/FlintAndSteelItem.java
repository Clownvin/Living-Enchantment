package net.minecraft.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class FlintAndSteelItem extends Item {
   public FlintAndSteelItem(Item.Properties builder) {
      super(builder);
   }

   /**
    * Called when this item is used when targetting a Block
    */
   public ActionResultType onItemUse(ItemUseContext context) {
      PlayerEntity playerentity = context.getPlayer();
      IWorld iworld = context.getWorld();
      BlockPos blockpos = context.getPos();
      BlockPos blockpos1 = blockpos.offset(context.getFace());
      if (func_219996_a(iworld.getBlockState(blockpos1), iworld, blockpos1)) {
         iworld.playSound(playerentity, blockpos1, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);
         BlockState blockstate1 = ((FireBlock)Blocks.FIRE).getStateForPlacement(iworld, blockpos1);
         iworld.setBlockState(blockpos1, blockstate1, 11);
         ItemStack itemstack = context.getItem();
         if (playerentity instanceof ServerPlayerEntity) {
            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)playerentity, blockpos1, itemstack);
            itemstack.damageItem(1, playerentity, (p_219999_1_) -> {
               p_219999_1_.sendBreakAnimation(context.getHand());
            });
         }

         return ActionResultType.SUCCESS;
      } else {
         BlockState blockstate = iworld.getBlockState(blockpos);
         if (func_219997_a(blockstate)) {
            iworld.playSound(playerentity, blockpos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);
            iworld.setBlockState(blockpos, blockstate.with(BlockStateProperties.LIT, Boolean.valueOf(true)), 11);
            if (playerentity != null) {
               context.getItem().damageItem(1, playerentity, (p_219998_1_) -> {
                  p_219998_1_.sendBreakAnimation(context.getHand());
               });
            }

            return ActionResultType.SUCCESS;
         } else {
            return ActionResultType.FAIL;
         }
      }
   }

   public static boolean func_219997_a(BlockState p_219997_0_) {
      return p_219997_0_.getBlock() == Blocks.CAMPFIRE && !p_219997_0_.get(BlockStateProperties.WATERLOGGED) && !p_219997_0_.get(BlockStateProperties.LIT);
   }

   public static boolean func_219996_a(BlockState p_219996_0_, IWorld p_219996_1_, BlockPos p_219996_2_) {
      BlockState blockstate = ((FireBlock)Blocks.FIRE).getStateForPlacement(p_219996_1_, p_219996_2_);
      boolean flag = false;

      for(Direction direction : Direction.Plane.HORIZONTAL) {
         if (p_219996_1_.getBlockState(p_219996_2_.offset(direction)).getBlock() == Blocks.OBSIDIAN && ((NetherPortalBlock)Blocks.NETHER_PORTAL).isPortal(p_219996_1_, p_219996_2_) != null) {
            flag = true;
         }
      }

      return p_219996_0_.isAir() && (blockstate.isValidPosition(p_219996_1_, p_219996_2_) || flag);
   }
}