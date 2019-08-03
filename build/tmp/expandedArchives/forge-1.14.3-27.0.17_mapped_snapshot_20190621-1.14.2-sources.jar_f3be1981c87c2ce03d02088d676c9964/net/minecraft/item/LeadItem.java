package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.LeashKnotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LeadItem extends Item {
   public LeadItem(Item.Properties builder) {
      super(builder);
   }

   /**
    * Called when this item is used when targetting a Block
    */
   public ActionResultType onItemUse(ItemUseContext context) {
      World world = context.getWorld();
      BlockPos blockpos = context.getPos();
      Block block = world.getBlockState(blockpos).getBlock();
      if (block.isIn(BlockTags.FENCES)) {
         PlayerEntity playerentity = context.getPlayer();
         if (!world.isRemote && playerentity != null) {
            attachToFence(playerentity, world, blockpos);
         }

         return ActionResultType.SUCCESS;
      } else {
         return ActionResultType.PASS;
      }
   }

   public static boolean attachToFence(PlayerEntity player, World worldIn, BlockPos fence) {
      LeashKnotEntity leashknotentity = null;
      boolean flag = false;
      double d0 = 7.0D;
      int i = fence.getX();
      int j = fence.getY();
      int k = fence.getZ();

      for(MobEntity mobentity : worldIn.getEntitiesWithinAABB(MobEntity.class, new AxisAlignedBB((double)i - 7.0D, (double)j - 7.0D, (double)k - 7.0D, (double)i + 7.0D, (double)j + 7.0D, (double)k + 7.0D))) {
         if (mobentity.getLeashHolder() == player) {
            if (leashknotentity == null) {
               leashknotentity = LeashKnotEntity.create(worldIn, fence);
            }

            mobentity.setLeashHolder(leashknotentity, true);
            flag = true;
         }
      }

      return flag;
   }
}