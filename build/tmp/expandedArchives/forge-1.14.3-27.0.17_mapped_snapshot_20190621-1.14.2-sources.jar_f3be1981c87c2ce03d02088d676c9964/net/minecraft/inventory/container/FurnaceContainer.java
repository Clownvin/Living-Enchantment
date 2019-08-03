package net.minecraft.inventory.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.IIntArray;

public class FurnaceContainer extends AbstractFurnaceContainer {
   public FurnaceContainer(int p_i50082_1_, PlayerInventory p_i50082_2_) {
      super(ContainerType.FURNACE, IRecipeType.SMELTING, p_i50082_1_, p_i50082_2_);
   }

   public FurnaceContainer(int p_i50083_1_, PlayerInventory p_i50083_2_, IInventory p_i50083_3_, IIntArray p_i50083_4_) {
      super(ContainerType.FURNACE, IRecipeType.SMELTING, p_i50083_1_, p_i50083_2_, p_i50083_3_, p_i50083_4_);
   }
}