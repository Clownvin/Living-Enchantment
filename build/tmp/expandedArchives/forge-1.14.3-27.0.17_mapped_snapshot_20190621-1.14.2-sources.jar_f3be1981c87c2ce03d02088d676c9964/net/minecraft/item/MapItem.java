package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class MapItem extends AbstractMapItem {
   public MapItem(Item.Properties builder) {
      super(builder);
   }

   /**
    * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
    * {@link #onItemUse}.
    */
   public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
      ItemStack itemstack = FilledMapItem.setupNewMap(worldIn, MathHelper.floor(playerIn.posX), MathHelper.floor(playerIn.posZ), (byte)0, true, false);
      ItemStack itemstack1 = playerIn.getHeldItem(handIn);
      if (!playerIn.abilities.isCreativeMode) {
         itemstack1.shrink(1);
      }

      if (itemstack1.isEmpty()) {
         return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
      } else {
         if (!playerIn.inventory.addItemStackToInventory(itemstack.copy())) {
            playerIn.dropItem(itemstack, false);
         }

         playerIn.addStat(Stats.ITEM_USED.get(this));
         return new ActionResult<>(ActionResultType.SUCCESS, itemstack1);
      }
   }
}