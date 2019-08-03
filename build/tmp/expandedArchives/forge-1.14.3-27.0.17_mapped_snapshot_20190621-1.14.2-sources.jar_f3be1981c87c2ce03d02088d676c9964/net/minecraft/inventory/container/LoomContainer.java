package net.minecraft.inventory.container;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.BannerPatternItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LoomContainer extends Container {
   private final IWorldPosCallable field_217033_c;
   private final IntReferenceHolder field_217034_d = IntReferenceHolder.single();
   private Runnable field_217035_e = () -> {
   };
   private final Slot field_217036_f;
   private final Slot field_217037_g;
   private final Slot field_217038_h;
   private final Slot field_217039_i;
   private final IInventory field_217040_j = new Inventory(3) {
      /**
       * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think
       * it hasn't changed and skip it.
       */
      public void markDirty() {
         super.markDirty();
         LoomContainer.this.onCraftMatrixChanged(this);
         LoomContainer.this.field_217035_e.run();
      }
   };
   private final IInventory field_217041_k = new Inventory(1) {
      /**
       * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think
       * it hasn't changed and skip it.
       */
      public void markDirty() {
         super.markDirty();
         LoomContainer.this.field_217035_e.run();
      }
   };

   public LoomContainer(int p_i50073_1_, PlayerInventory p_i50073_2_) {
      this(p_i50073_1_, p_i50073_2_, IWorldPosCallable.DUMMY);
   }

   public LoomContainer(int p_i50074_1_, PlayerInventory p_i50074_2_, final IWorldPosCallable p_i50074_3_) {
      super(ContainerType.LOOM, p_i50074_1_);
      this.field_217033_c = p_i50074_3_;
      this.field_217036_f = this.addSlot(new Slot(this.field_217040_j, 0, 13, 26) {
         /**
          * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
          */
         public boolean isItemValid(ItemStack stack) {
            return stack.getItem() instanceof BannerItem;
         }
      });
      this.field_217037_g = this.addSlot(new Slot(this.field_217040_j, 1, 33, 26) {
         /**
          * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
          */
         public boolean isItemValid(ItemStack stack) {
            return stack.getItem() instanceof DyeItem;
         }
      });
      this.field_217038_h = this.addSlot(new Slot(this.field_217040_j, 2, 23, 45) {
         /**
          * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
          */
         public boolean isItemValid(ItemStack stack) {
            return stack.getItem() instanceof BannerPatternItem;
         }
      });
      this.field_217039_i = this.addSlot(new Slot(this.field_217041_k, 0, 143, 58) {
         /**
          * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
          */
         public boolean isItemValid(ItemStack stack) {
            return false;
         }

         public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack) {
            LoomContainer.this.field_217036_f.decrStackSize(1);
            LoomContainer.this.field_217037_g.decrStackSize(1);
            if (!LoomContainer.this.field_217036_f.getHasStack() || !LoomContainer.this.field_217037_g.getHasStack()) {
               LoomContainer.this.field_217034_d.set(0);
            }

            p_i50074_3_.consume((p_216951_0_, p_216951_1_) -> {
               p_216951_0_.playSound((PlayerEntity)null, p_216951_1_, SoundEvents.UI_LOOM_TAKE_RESULT, SoundCategory.BLOCKS, 1.0F, 1.0F);
            });
            return super.onTake(thePlayer, stack);
         }
      });

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 9; ++j) {
            this.addSlot(new Slot(p_i50074_2_, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for(int k = 0; k < 9; ++k) {
         this.addSlot(new Slot(p_i50074_2_, k, 8 + k * 18, 142));
      }

      this.func_216958_a(this.field_217034_d);
   }

   @OnlyIn(Dist.CLIENT)
   public int func_217023_e() {
      return this.field_217034_d.get();
   }

   /**
    * Determines whether supplied player can use this container
    */
   public boolean canInteractWith(PlayerEntity playerIn) {
      return isWithinUsableDistance(this.field_217033_c, playerIn, Blocks.LOOM);
   }

   /**
    * Handles the given Button-click on the server, currently only used by enchanting. Name is for legacy.
    */
   public boolean enchantItem(PlayerEntity playerIn, int id) {
      if (id > 0 && id <= BannerPattern.field_222481_P) {
         this.field_217034_d.set(id);
         this.func_217031_j();
         return true;
      } else {
         return false;
      }
   }

   /**
    * Callback for when the crafting matrix is changed.
    */
   public void onCraftMatrixChanged(IInventory inventoryIn) {
      ItemStack itemstack = this.field_217036_f.getStack();
      ItemStack itemstack1 = this.field_217037_g.getStack();
      ItemStack itemstack2 = this.field_217038_h.getStack();
      ItemStack itemstack3 = this.field_217039_i.getStack();
      if (itemstack3.isEmpty() || !itemstack.isEmpty() && !itemstack1.isEmpty() && this.field_217034_d.get() > 0 && (this.field_217034_d.get() < BannerPattern.field_222480_O - 5 || !itemstack2.isEmpty())) {
         if (!itemstack2.isEmpty() && itemstack2.getItem() instanceof BannerPatternItem) {
            CompoundNBT compoundnbt = itemstack.getOrCreateChildTag("BlockEntityTag");
            boolean flag = compoundnbt.contains("Patterns", 9) && !itemstack.isEmpty() && compoundnbt.getList("Patterns", 10).size() >= 6;
            if (flag) {
               this.field_217034_d.set(0);
            } else {
               this.field_217034_d.set(((BannerPatternItem)itemstack2.getItem()).func_219980_b().ordinal());
            }
         }
      } else {
         this.field_217039_i.putStack(ItemStack.EMPTY);
         this.field_217034_d.set(0);
      }

      this.func_217031_j();
      this.detectAndSendChanges();
   }

   @OnlyIn(Dist.CLIENT)
   public void func_217020_a(Runnable p_217020_1_) {
      this.field_217035_e = p_217020_1_;
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
         if (index == this.field_217039_i.slotNumber) {
            if (!this.mergeItemStack(itemstack1, 4, 40, true)) {
               return ItemStack.EMPTY;
            }

            slot.onSlotChange(itemstack1, itemstack);
         } else if (index != this.field_217037_g.slotNumber && index != this.field_217036_f.slotNumber && index != this.field_217038_h.slotNumber) {
            if (itemstack1.getItem() instanceof BannerItem) {
               if (!this.mergeItemStack(itemstack1, this.field_217036_f.slotNumber, this.field_217036_f.slotNumber + 1, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (itemstack1.getItem() instanceof DyeItem) {
               if (!this.mergeItemStack(itemstack1, this.field_217037_g.slotNumber, this.field_217037_g.slotNumber + 1, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (itemstack1.getItem() instanceof BannerPatternItem) {
               if (!this.mergeItemStack(itemstack1, this.field_217038_h.slotNumber, this.field_217038_h.slotNumber + 1, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (index >= 4 && index < 31) {
               if (!this.mergeItemStack(itemstack1, 31, 40, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (index >= 31 && index < 40 && !this.mergeItemStack(itemstack1, 4, 31, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.mergeItemStack(itemstack1, 4, 40, false)) {
            return ItemStack.EMPTY;
         }

         if (itemstack1.isEmpty()) {
            slot.putStack(ItemStack.EMPTY);
         } else {
            slot.onSlotChanged();
         }

         if (itemstack1.getCount() == itemstack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(playerIn, itemstack1);
      }

      return itemstack;
   }

   /**
    * Called when the container is closed.
    */
   public void onContainerClosed(PlayerEntity playerIn) {
      super.onContainerClosed(playerIn);
      this.field_217033_c.consume((p_217028_2_, p_217028_3_) -> {
         this.clearContainer(playerIn, playerIn.world, this.field_217040_j);
      });
   }

   private void func_217031_j() {
      if (this.field_217034_d.get() > 0) {
         ItemStack itemstack = this.field_217036_f.getStack();
         ItemStack itemstack1 = this.field_217037_g.getStack();
         ItemStack itemstack2 = ItemStack.EMPTY;
         if (!itemstack.isEmpty() && !itemstack1.isEmpty()) {
            itemstack2 = itemstack.copy();
            itemstack2.setCount(1);
            BannerPattern bannerpattern = BannerPattern.values()[this.field_217034_d.get()];
            DyeColor dyecolor = ((DyeItem)itemstack1.getItem()).getDyeColor();
            CompoundNBT compoundnbt = itemstack2.getOrCreateChildTag("BlockEntityTag");
            ListNBT listnbt;
            if (compoundnbt.contains("Patterns", 9)) {
               listnbt = compoundnbt.getList("Patterns", 10);
            } else {
               listnbt = new ListNBT();
               compoundnbt.put("Patterns", listnbt);
            }

            CompoundNBT compoundnbt1 = new CompoundNBT();
            compoundnbt1.putString("Pattern", bannerpattern.getHashname());
            compoundnbt1.putInt("Color", dyecolor.getId());
            listnbt.add(compoundnbt1);
         }

         if (!ItemStack.areItemStacksEqual(itemstack2, this.field_217039_i.getStack())) {
            this.field_217039_i.putStack(itemstack2);
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public Slot func_217024_f() {
      return this.field_217036_f;
   }

   @OnlyIn(Dist.CLIENT)
   public Slot func_217022_g() {
      return this.field_217037_g;
   }

   @OnlyIn(Dist.CLIENT)
   public Slot func_217025_h() {
      return this.field_217038_h;
   }

   @OnlyIn(Dist.CLIENT)
   public Slot func_217026_i() {
      return this.field_217039_i;
   }
}