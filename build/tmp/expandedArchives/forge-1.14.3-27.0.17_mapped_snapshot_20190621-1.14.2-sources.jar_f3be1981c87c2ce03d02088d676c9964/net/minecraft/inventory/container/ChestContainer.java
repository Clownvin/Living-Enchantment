package net.minecraft.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ChestContainer extends Container {
   private final IInventory lowerChestInventory;
   private final int numRows;

   private ChestContainer(ContainerType<?> type, int p_i50091_2_, PlayerInventory player, int rows) {
      this(type, p_i50091_2_, player, new Inventory(9 * rows), rows);
   }

   public static ChestContainer createGeneric9X1(int p_216986_0_, PlayerInventory p_216986_1_) {
      return new ChestContainer(ContainerType.GENERIC_9X1, p_216986_0_, p_216986_1_, 1);
   }

   public static ChestContainer createGeneric9X2(int p_216987_0_, PlayerInventory p_216987_1_) {
      return new ChestContainer(ContainerType.GENERIC_9X2, p_216987_0_, p_216987_1_, 2);
   }

   public static ChestContainer createGeneric9X3(int p_216988_0_, PlayerInventory p_216988_1_) {
      return new ChestContainer(ContainerType.GENERIC_9X3, p_216988_0_, p_216988_1_, 3);
   }

   public static ChestContainer createGeneric9X4(int p_216991_0_, PlayerInventory p_216991_1_) {
      return new ChestContainer(ContainerType.GENERIC_9X4, p_216991_0_, p_216991_1_, 4);
   }

   public static ChestContainer createGeneric9X5(int p_216989_0_, PlayerInventory p_216989_1_) {
      return new ChestContainer(ContainerType.GENERIC_9X5, p_216989_0_, p_216989_1_, 5);
   }

   public static ChestContainer createGeneric9X6(int p_216990_0_, PlayerInventory p_216990_1_) {
      return new ChestContainer(ContainerType.GENERIC_9X6, p_216990_0_, p_216990_1_, 6);
   }

   public static ChestContainer createGeneric9X3(int p_216992_0_, PlayerInventory p_216992_1_, IInventory p_216992_2_) {
      return new ChestContainer(ContainerType.GENERIC_9X3, p_216992_0_, p_216992_1_, p_216992_2_, 3);
   }

   public static ChestContainer createGeneric9X6(int p_216984_0_, PlayerInventory p_216984_1_, IInventory p_216984_2_) {
      return new ChestContainer(ContainerType.GENERIC_9X6, p_216984_0_, p_216984_1_, p_216984_2_, 6);
   }

   public ChestContainer(ContainerType<?> type, int id, PlayerInventory playerInventoryIn, IInventory p_i50092_4_, int rows) {
      super(type, id);
      assertInventorySize(p_i50092_4_, rows * 9);
      this.lowerChestInventory = p_i50092_4_;
      this.numRows = rows;
      p_i50092_4_.openInventory(playerInventoryIn.player);
      int i = (this.numRows - 4) * 18;

      for(int j = 0; j < this.numRows; ++j) {
         for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(p_i50092_4_, k + j * 9, 8 + k * 18, 18 + j * 18));
         }
      }

      for(int l = 0; l < 3; ++l) {
         for(int j1 = 0; j1 < 9; ++j1) {
            this.addSlot(new Slot(playerInventoryIn, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + i));
         }
      }

      for(int i1 = 0; i1 < 9; ++i1) {
         this.addSlot(new Slot(playerInventoryIn, i1, 8 + i1 * 18, 161 + i));
      }

   }

   /**
    * Determines whether supplied player can use this container
    */
   public boolean canInteractWith(PlayerEntity playerIn) {
      return this.lowerChestInventory.isUsableByPlayer(playerIn);
   }

   /**
    * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
    * inventory and the other inventory(s).
    */
   public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = this.inventorySlots.get(index);
      if (slot != null && slot.getHasStack()) {
         ItemStack itemstack1 = slot.getStack();
         itemstack = itemstack1.copy();
         if (index < this.numRows * 9) {
            if (!this.mergeItemStack(itemstack1, this.numRows * 9, this.inventorySlots.size(), true)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.mergeItemStack(itemstack1, 0, this.numRows * 9, false)) {
            return ItemStack.EMPTY;
         }

         if (itemstack1.isEmpty()) {
            slot.putStack(ItemStack.EMPTY);
         } else {
            slot.onSlotChanged();
         }
      }

      return itemstack;
   }

   /**
    * Called when the container is closed.
    */
   public void onContainerClosed(PlayerEntity playerIn) {
      super.onContainerClosed(playerIn);
      this.lowerChestInventory.closeInventory(playerIn);
   }

   /**
    * Gets the inventory associated with this chest container.
    *  
    * @see #field_75155_e
    */
   public IInventory getLowerChestInventory() {
      return this.lowerChestInventory;
   }

   @OnlyIn(Dist.CLIENT)
   public int getNumRows() {
      return this.numRows;
   }
}