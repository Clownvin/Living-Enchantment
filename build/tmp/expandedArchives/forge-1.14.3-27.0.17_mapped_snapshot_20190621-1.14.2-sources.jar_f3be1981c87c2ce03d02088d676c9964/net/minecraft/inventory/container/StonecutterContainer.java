package net.minecraft.inventory.container;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.StonecuttingRecipe;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class StonecutterContainer extends Container {
   static final ImmutableList<Item> field_217084_c = ImmutableList.of(Items.STONE, Items.SANDSTONE, Items.RED_SANDSTONE, Items.QUARTZ_BLOCK, Items.COBBLESTONE, Items.STONE_BRICKS, Items.BRICKS, Items.NETHER_BRICKS, Items.RED_NETHER_BRICKS, Items.PURPUR_BLOCK, Items.PRISMARINE, Items.PRISMARINE_BRICKS, Items.DARK_PRISMARINE, Items.ANDESITE, Items.POLISHED_ANDESITE, Items.GRANITE, Items.POLISHED_GRANITE, Items.DIORITE, Items.POLISHED_DIORITE, Items.MOSSY_STONE_BRICKS, Items.MOSSY_COBBLESTONE, Items.SMOOTH_SANDSTONE, Items.SMOOTH_RED_SANDSTONE, Items.SMOOTH_QUARTZ, Items.END_STONE, Items.END_STONE_BRICKS, Items.SMOOTH_STONE, Items.CUT_SANDSTONE, Items.CUT_RED_SANDSTONE);
   private final IWorldPosCallable field_217088_g;
   private final IntReferenceHolder field_217089_h = IntReferenceHolder.single();
   private final World field_217090_i;
   private List<StonecuttingRecipe> field_217091_j = Lists.newArrayList();
   private ItemStack field_217092_k = ItemStack.EMPTY;
   private long field_217093_l;
   final Slot field_217085_d;
   final Slot field_217086_e;
   private Runnable field_217094_m = () -> {
   };
   public final IInventory field_217087_f = new Inventory(1) {
      /**
       * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think
       * it hasn't changed and skip it.
       */
      public void markDirty() {
         super.markDirty();
         StonecutterContainer.this.onCraftMatrixChanged(this);
         StonecutterContainer.this.field_217094_m.run();
      }
   };
   private final CraftResultInventory field_217095_n = new CraftResultInventory();

   public StonecutterContainer(int p_i50059_1_, PlayerInventory p_i50059_2_) {
      this(p_i50059_1_, p_i50059_2_, IWorldPosCallable.DUMMY);
   }

   public StonecutterContainer(int p_i50060_1_, PlayerInventory p_i50060_2_, final IWorldPosCallable p_i50060_3_) {
      super(ContainerType.STONECUTTER, p_i50060_1_);
      this.field_217088_g = p_i50060_3_;
      this.field_217090_i = p_i50060_2_.player.world;
      this.field_217085_d = this.addSlot(new Slot(this.field_217087_f, 0, 20, 33));
      this.field_217086_e = this.addSlot(new Slot(this.field_217095_n, 1, 143, 33) {
         /**
          * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
          */
         public boolean isItemValid(ItemStack stack) {
            return false;
         }

         public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack) {
            ItemStack itemstack = StonecutterContainer.this.field_217085_d.decrStackSize(1);
            if (!itemstack.isEmpty()) {
               StonecutterContainer.this.func_217082_i();
            }

            stack.getItem().onCreated(stack, thePlayer.world, thePlayer);
            p_i50060_3_.consume((p_216954_1_, p_216954_2_) -> {
               long l = p_216954_1_.getGameTime();
               if (StonecutterContainer.this.field_217093_l != l) {
                  p_216954_1_.playSound((PlayerEntity)null, p_216954_2_, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                  StonecutterContainer.this.field_217093_l = l;
               }

            });
            return super.onTake(thePlayer, stack);
         }
      });

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 9; ++j) {
            this.addSlot(new Slot(p_i50060_2_, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for(int k = 0; k < 9; ++k) {
         this.addSlot(new Slot(p_i50060_2_, k, 8 + k * 18, 142));
      }

      this.func_216958_a(this.field_217089_h);
   }

   @OnlyIn(Dist.CLIENT)
   public int func_217073_e() {
      return this.field_217089_h.get();
   }

   @OnlyIn(Dist.CLIENT)
   public List<StonecuttingRecipe> func_217076_f() {
      return this.field_217091_j;
   }

   @OnlyIn(Dist.CLIENT)
   public int func_217075_g() {
      return this.field_217091_j.size();
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_217083_h() {
      return this.field_217085_d.getHasStack() && !this.field_217091_j.isEmpty();
   }

   /**
    * Determines whether supplied player can use this container
    */
   public boolean canInteractWith(PlayerEntity playerIn) {
      return isWithinUsableDistance(this.field_217088_g, playerIn, Blocks.STONECUTTER);
   }

   /**
    * Handles the given Button-click on the server, currently only used by enchanting. Name is for legacy.
    */
   public boolean enchantItem(PlayerEntity playerIn, int id) {
      if (id >= 0 && id < this.field_217091_j.size()) {
         this.field_217089_h.set(id);
         this.func_217082_i();
      }

      return true;
   }

   /**
    * Callback for when the crafting matrix is changed.
    */
   public void onCraftMatrixChanged(IInventory inventoryIn) {
      ItemStack itemstack = this.field_217085_d.getStack();
      if (itemstack.getItem() != this.field_217092_k.getItem()) {
         this.field_217092_k = itemstack.copy();
         this.func_217074_a(inventoryIn, itemstack);
      }

   }

   private void func_217074_a(IInventory p_217074_1_, ItemStack p_217074_2_) {
      this.field_217091_j.clear();
      this.field_217089_h.set(-1);
      this.field_217086_e.putStack(ItemStack.EMPTY);
      if (!p_217074_2_.isEmpty()) {
         this.field_217091_j = this.field_217090_i.getRecipeManager().getRecipes(IRecipeType.STONECUTTING, p_217074_1_, this.field_217090_i);
      }

   }

   private void func_217082_i() {
      if (!this.field_217091_j.isEmpty()) {
         StonecuttingRecipe stonecuttingrecipe = this.field_217091_j.get(this.field_217089_h.get());
         this.field_217086_e.putStack(stonecuttingrecipe.getCraftingResult(this.field_217087_f));
      } else {
         this.field_217086_e.putStack(ItemStack.EMPTY);
      }

      this.detectAndSendChanges();
   }

   public ContainerType<?> getType() {
      return ContainerType.STONECUTTER;
   }

   @OnlyIn(Dist.CLIENT)
   public void func_217071_a(Runnable p_217071_1_) {
      this.field_217094_m = p_217071_1_;
   }

   /**
    * Called to determine if the current slot is valid for the stack merging (double-click) code. The stack passed in is
    * null for the initial slot that was double-clicked.
    */
   public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
      return false;
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
         Item item = itemstack1.getItem();
         itemstack = itemstack1.copy();
         if (index == 1) {
            item.onCreated(itemstack1, playerIn.world, playerIn);
            if (!this.mergeItemStack(itemstack1, 2, 38, true)) {
               return ItemStack.EMPTY;
            }

            slot.onSlotChange(itemstack1, itemstack);
         } else if (index == 0) {
            if (!this.mergeItemStack(itemstack1, 2, 38, false)) {
               return ItemStack.EMPTY;
            }
         } else if (field_217084_c.contains(item)) {
            if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
               return ItemStack.EMPTY;
            }
         } else if (index >= 2 && index < 29) {
            if (!this.mergeItemStack(itemstack1, 29, 38, false)) {
               return ItemStack.EMPTY;
            }
         } else if (index >= 29 && index < 38 && !this.mergeItemStack(itemstack1, 2, 29, false)) {
            return ItemStack.EMPTY;
         }

         if (itemstack1.isEmpty()) {
            slot.putStack(ItemStack.EMPTY);
         }

         slot.onSlotChanged();
         if (itemstack1.getCount() == itemstack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(playerIn, itemstack1);
         this.detectAndSendChanges();
      }

      return itemstack;
   }

   /**
    * Called when the container is closed.
    */
   public void onContainerClosed(PlayerEntity playerIn) {
      super.onContainerClosed(playerIn);
      this.field_217095_n.removeStackFromSlot(1);
      this.field_217088_g.consume((p_217079_2_, p_217079_3_) -> {
         this.clearContainer(playerIn, playerIn.world, this.field_217087_f);
      });
   }
}