package net.minecraft.tileentity;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.SmokerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class SmokerTileEntity extends AbstractFurnaceTileEntity {
   public SmokerTileEntity() {
      super(TileEntityType.SMOKER, IRecipeType.SMOKING);
   }

   protected ITextComponent getDefaultName() {
      return new TranslationTextComponent("container.smoker");
   }

   protected int getBurnTime(ItemStack p_213997_1_) {
      return super.getBurnTime(p_213997_1_) / 2;
   }

   protected Container createMenu(int p_213906_1_, PlayerInventory p_213906_2_) {
      return new SmokerContainer(p_213906_1_, p_213906_2_, this, this.field_214013_b);
   }
}