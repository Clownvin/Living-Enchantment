package net.minecraft.tileentity;

import javax.annotation.Nullable;
import net.minecraft.block.LecternBlock;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IClearable;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.LecternContainer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IIntArray;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.ServerWorld;

public class LecternTileEntity extends TileEntity implements IClearable, INamedContainerProvider {
   private final IInventory field_214048_a = new IInventory() {
      /**
       * Returns the number of slots in the inventory.
       */
      public int getSizeInventory() {
         return 1;
      }

      public boolean isEmpty() {
         return LecternTileEntity.this.book.isEmpty();
      }

      /**
       * Returns the stack in the given slot.
       */
      public ItemStack getStackInSlot(int index) {
         return index == 0 ? LecternTileEntity.this.book : ItemStack.EMPTY;
      }

      /**
       * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
       */
      public ItemStack decrStackSize(int index, int count) {
         if (index == 0) {
            ItemStack itemstack = LecternTileEntity.this.book.split(count);
            if (LecternTileEntity.this.book.isEmpty()) {
               LecternTileEntity.this.func_214042_s();
            }

            return itemstack;
         } else {
            return ItemStack.EMPTY;
         }
      }

      /**
       * Removes a stack from the given slot and returns it.
       */
      public ItemStack removeStackFromSlot(int index) {
         if (index == 0) {
            ItemStack itemstack = LecternTileEntity.this.book;
            LecternTileEntity.this.book = ItemStack.EMPTY;
            LecternTileEntity.this.func_214042_s();
            return itemstack;
         } else {
            return ItemStack.EMPTY;
         }
      }

      /**
       * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
       */
      public void setInventorySlotContents(int index, ItemStack stack) {
      }

      /**
       * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended.
       */
      public int getInventoryStackLimit() {
         return 1;
      }

      /**
       * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think
       * it hasn't changed and skip it.
       */
      public void markDirty() {
         LecternTileEntity.this.markDirty();
      }

      /**
       * Don't rename this method to canInteractWith due to conflicts with Container
       */
      public boolean isUsableByPlayer(PlayerEntity player) {
         if (LecternTileEntity.this.world.getTileEntity(LecternTileEntity.this.pos) != LecternTileEntity.this) {
            return false;
         } else {
            return player.getDistanceSq((double)LecternTileEntity.this.pos.getX() + 0.5D, (double)LecternTileEntity.this.pos.getY() + 0.5D, (double)LecternTileEntity.this.pos.getZ() + 0.5D) > 64.0D ? false : LecternTileEntity.this.func_214046_f();
         }
      }

      /**
       * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot. For
       * guis use Slot.isItemValid
       */
      public boolean isItemValidForSlot(int index, ItemStack stack) {
         return false;
      }

      public void clear() {
      }
   };
   private final IIntArray field_214049_b = new IIntArray() {
      public int get(int index) {
         return index == 0 ? LecternTileEntity.this.page : 0;
      }

      public void set(int index, int value) {
         if (index == 0) {
            LecternTileEntity.this.func_214035_a(value);
         }

      }

      public int size() {
         return 1;
      }
   };
   private ItemStack book = ItemStack.EMPTY;
   private int page;
   private int pages;

   public LecternTileEntity() {
      super(TileEntityType.LECTERN);
   }

   public ItemStack func_214033_c() {
      return this.book;
   }

   public boolean func_214046_f() {
      Item item = this.book.getItem();
      return item == Items.WRITABLE_BOOK || item == Items.WRITTEN_BOOK;
   }

   public void func_214045_a(ItemStack p_214045_1_) {
      this.func_214040_a(p_214045_1_, (PlayerEntity)null);
   }

   private void func_214042_s() {
      this.page = 0;
      this.pages = 0;
      LecternBlock.setHasBook(this.getWorld(), this.getPos(), this.getBlockState(), false);
   }

   public void func_214040_a(ItemStack p_214040_1_, @Nullable PlayerEntity p_214040_2_) {
      this.book = this.func_214047_b(p_214040_1_, p_214040_2_);
      this.page = 0;
      this.pages = WrittenBookItem.func_220049_j(this.book);
      this.markDirty();
   }

   private void func_214035_a(int p_214035_1_) {
      int i = MathHelper.clamp(p_214035_1_, 0, this.pages - 1);
      if (i != this.page) {
         this.page = i;
         this.markDirty();
         LecternBlock.pulse(this.getWorld(), this.getPos(), this.getBlockState());
      }

   }

   public int func_214041_g() {
      return this.page;
   }

   public int func_214034_r() {
      float f = this.pages > 1 ? (float)this.func_214041_g() / ((float)this.pages - 1.0F) : 1.0F;
      return MathHelper.floor(f * 14.0F) + (this.func_214046_f() ? 1 : 0);
   }

   private ItemStack func_214047_b(ItemStack p_214047_1_, @Nullable PlayerEntity p_214047_2_) {
      if (this.world instanceof ServerWorld && p_214047_1_.getItem() == Items.WRITTEN_BOOK) {
         WrittenBookItem.func_220050_a(p_214047_1_, this.func_214039_a(p_214047_2_), p_214047_2_);
      }

      return p_214047_1_;
   }

   private CommandSource func_214039_a(@Nullable PlayerEntity p_214039_1_) {
      String s;
      ITextComponent itextcomponent;
      if (p_214039_1_ == null) {
         s = "Lectern";
         itextcomponent = new StringTextComponent("Lectern");
      } else {
         s = p_214039_1_.getName().getString();
         itextcomponent = p_214039_1_.getDisplayName();
      }

      Vec3d vec3d = new Vec3d((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D);
      return new CommandSource(ICommandSource.field_213139_a_, vec3d, Vec2f.ZERO, (ServerWorld)this.world, 2, s, itextcomponent, this.world.getServer(), p_214039_1_);
   }

   public void read(CompoundNBT compound) {
      super.read(compound);
      if (compound.contains("Book", 10)) {
         this.book = this.func_214047_b(ItemStack.read(compound.getCompound("Book")), (PlayerEntity)null);
      } else {
         this.book = ItemStack.EMPTY;
      }

      this.pages = WrittenBookItem.func_220049_j(this.book);
      this.page = MathHelper.clamp(compound.getInt("Page"), 0, this.pages - 1);
   }

   public CompoundNBT write(CompoundNBT compound) {
      super.write(compound);
      if (!this.func_214033_c().isEmpty()) {
         compound.put("Book", this.func_214033_c().write(new CompoundNBT()));
         compound.putInt("Page", this.page);
      }

      return compound;
   }

   public void clear() {
      this.func_214045_a(ItemStack.EMPTY);
   }

   public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
      return new LecternContainer(p_createMenu_1_, this.field_214048_a, this.field_214049_b);
   }

   public ITextComponent getDisplayName() {
      return new TranslationTextComponent("container.lectern");
   }
}