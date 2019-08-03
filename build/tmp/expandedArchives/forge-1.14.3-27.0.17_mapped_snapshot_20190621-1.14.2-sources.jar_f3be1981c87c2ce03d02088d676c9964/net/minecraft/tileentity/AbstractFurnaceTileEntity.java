package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IRecipeHelperPopulator;
import net.minecraft.inventory.IRecipeHolder;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public abstract class AbstractFurnaceTileEntity extends LockableTileEntity implements ISidedInventory, IRecipeHolder, IRecipeHelperPopulator, ITickableTileEntity {
   private static final int[] field_214015_g = new int[]{0};
   private static final int[] field_214016_h = new int[]{2, 1};
   private static final int[] field_214017_i = new int[]{1};
   protected NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
   private int burnTime;
   private int recipesUsed;
   private int cookTime;
   private int cookTimeTotal;
   protected final IIntArray field_214013_b = new IIntArray() {
      public int get(int index) {
         switch(index) {
         case 0:
            return AbstractFurnaceTileEntity.this.burnTime;
         case 1:
            return AbstractFurnaceTileEntity.this.recipesUsed;
         case 2:
            return AbstractFurnaceTileEntity.this.cookTime;
         case 3:
            return AbstractFurnaceTileEntity.this.cookTimeTotal;
         default:
            return 0;
         }
      }

      public void set(int index, int value) {
         switch(index) {
         case 0:
            AbstractFurnaceTileEntity.this.burnTime = value;
            break;
         case 1:
            AbstractFurnaceTileEntity.this.recipesUsed = value;
            break;
         case 2:
            AbstractFurnaceTileEntity.this.cookTime = value;
            break;
         case 3:
            AbstractFurnaceTileEntity.this.cookTimeTotal = value;
         }

      }

      public int size() {
         return 4;
      }
   };
   private final Map<ResourceLocation, Integer> field_214022_n = Maps.newHashMap();
   protected final IRecipeType<? extends AbstractCookingRecipe> recipeType;

   protected AbstractFurnaceTileEntity(TileEntityType<?> p_i49964_1_, IRecipeType<? extends AbstractCookingRecipe> p_i49964_2_) {
      super(p_i49964_1_);
      this.recipeType = p_i49964_2_;
   }

   public static Map<Item, Integer> getBurnTimes() {
      Map<Item, Integer> map = Maps.newLinkedHashMap();
      func_213996_a(map, Items.LAVA_BUCKET, 20000);
      func_213996_a(map, Blocks.COAL_BLOCK, 16000);
      func_213996_a(map, Items.BLAZE_ROD, 2400);
      func_213996_a(map, Items.COAL, 1600);
      func_213996_a(map, Items.CHARCOAL, 1600);
      func_213992_a(map, ItemTags.LOGS, 300);
      func_213992_a(map, ItemTags.PLANKS, 300);
      func_213992_a(map, ItemTags.WOODEN_STAIRS, 300);
      func_213992_a(map, ItemTags.WOODEN_SLABS, 150);
      func_213992_a(map, ItemTags.WOODEN_TRAPDOORS, 300);
      func_213992_a(map, ItemTags.WOODEN_PRESSURE_PLATES, 300);
      func_213996_a(map, Blocks.OAK_FENCE, 300);
      func_213996_a(map, Blocks.BIRCH_FENCE, 300);
      func_213996_a(map, Blocks.SPRUCE_FENCE, 300);
      func_213996_a(map, Blocks.JUNGLE_FENCE, 300);
      func_213996_a(map, Blocks.DARK_OAK_FENCE, 300);
      func_213996_a(map, Blocks.ACACIA_FENCE, 300);
      func_213996_a(map, Blocks.OAK_FENCE_GATE, 300);
      func_213996_a(map, Blocks.BIRCH_FENCE_GATE, 300);
      func_213996_a(map, Blocks.SPRUCE_FENCE_GATE, 300);
      func_213996_a(map, Blocks.JUNGLE_FENCE_GATE, 300);
      func_213996_a(map, Blocks.DARK_OAK_FENCE_GATE, 300);
      func_213996_a(map, Blocks.ACACIA_FENCE_GATE, 300);
      func_213996_a(map, Blocks.NOTE_BLOCK, 300);
      func_213996_a(map, Blocks.BOOKSHELF, 300);
      func_213996_a(map, Blocks.LECTERN, 300);
      func_213996_a(map, Blocks.JUKEBOX, 300);
      func_213996_a(map, Blocks.CHEST, 300);
      func_213996_a(map, Blocks.TRAPPED_CHEST, 300);
      func_213996_a(map, Blocks.CRAFTING_TABLE, 300);
      func_213996_a(map, Blocks.DAYLIGHT_DETECTOR, 300);
      func_213992_a(map, ItemTags.BANNERS, 300);
      func_213996_a(map, Items.BOW, 300);
      func_213996_a(map, Items.FISHING_ROD, 300);
      func_213996_a(map, Blocks.LADDER, 300);
      func_213992_a(map, ItemTags.SIGNS, 200);
      func_213996_a(map, Items.WOODEN_SHOVEL, 200);
      func_213996_a(map, Items.WOODEN_SWORD, 200);
      func_213996_a(map, Items.WOODEN_HOE, 200);
      func_213996_a(map, Items.WOODEN_AXE, 200);
      func_213996_a(map, Items.WOODEN_PICKAXE, 200);
      func_213992_a(map, ItemTags.WOODEN_DOORS, 200);
      func_213992_a(map, ItemTags.BOATS, 200);
      func_213992_a(map, ItemTags.WOOL, 100);
      func_213992_a(map, ItemTags.WOODEN_BUTTONS, 100);
      func_213996_a(map, Items.STICK, 100);
      func_213992_a(map, ItemTags.SAPLINGS, 100);
      func_213996_a(map, Items.BOWL, 100);
      func_213992_a(map, ItemTags.CARPETS, 67);
      func_213996_a(map, Blocks.DRIED_KELP_BLOCK, 4001);
      func_213996_a(map, Items.CROSSBOW, 300);
      func_213996_a(map, Blocks.BAMBOO, 50);
      func_213996_a(map, Blocks.DEAD_BUSH, 100);
      func_213996_a(map, Blocks.SCAFFOLDING, 50);
      func_213996_a(map, Blocks.LOOM, 300);
      func_213996_a(map, Blocks.BARREL, 300);
      func_213996_a(map, Blocks.CARTOGRAPHY_TABLE, 300);
      func_213996_a(map, Blocks.FLETCHING_TABLE, 300);
      func_213996_a(map, Blocks.SMITHING_TABLE, 300);
      func_213996_a(map, Blocks.COMPOSTER, 300);
      return map;
   }

   private static void func_213992_a(Map<Item, Integer> p_213992_0_, Tag<Item> p_213992_1_, int p_213992_2_) {
      for(Item item : p_213992_1_.getAllElements()) {
         p_213992_0_.put(item, p_213992_2_);
      }

   }

   private static void func_213996_a(Map<Item, Integer> p_213996_0_, IItemProvider p_213996_1_, int p_213996_2_) {
      p_213996_0_.put(p_213996_1_.asItem(), p_213996_2_);
   }

   private boolean func_214006_r() {
      return this.burnTime > 0;
   }

   public void read(CompoundNBT compound) {
      super.read(compound);
      this.items = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
      ItemStackHelper.loadAllItems(compound, this.items);
      this.burnTime = compound.getInt("BurnTime");
      this.cookTime = compound.getInt("CookTime");
      this.cookTimeTotal = compound.getInt("CookTimeTotal");
      this.recipesUsed = this.getBurnTime(this.items.get(1));
      int i = compound.getShort("RecipesUsedSize");

      for(int j = 0; j < i; ++j) {
         ResourceLocation resourcelocation = new ResourceLocation(compound.getString("RecipeLocation" + j));
         int k = compound.getInt("RecipeAmount" + j);
         this.field_214022_n.put(resourcelocation, k);
      }

   }

   public CompoundNBT write(CompoundNBT compound) {
      super.write(compound);
      compound.putInt("BurnTime", this.burnTime);
      compound.putInt("CookTime", this.cookTime);
      compound.putInt("CookTimeTotal", this.cookTimeTotal);
      ItemStackHelper.saveAllItems(compound, this.items);
      compound.putShort("RecipesUsedSize", (short)this.field_214022_n.size());
      int i = 0;

      for(Entry<ResourceLocation, Integer> entry : this.field_214022_n.entrySet()) {
         compound.putString("RecipeLocation" + i, entry.getKey().toString());
         compound.putInt("RecipeAmount" + i, entry.getValue());
         ++i;
      }

      return compound;
   }

   public void tick() {
      boolean flag = this.func_214006_r();
      boolean flag1 = false;
      if (this.func_214006_r()) {
         --this.burnTime;
      }

      if (!this.world.isRemote) {
         ItemStack itemstack = this.items.get(1);
         if (this.func_214006_r() || !itemstack.isEmpty() && !this.items.get(0).isEmpty()) {
            IRecipe<?> irecipe = this.world.getRecipeManager().getRecipe((IRecipeType<AbstractCookingRecipe>)this.recipeType, this, this.world).orElse(null);
            if (!this.func_214006_r() && this.func_214008_b(irecipe)) {
               this.burnTime = this.getBurnTime(itemstack);
               this.recipesUsed = this.burnTime;
               if (this.func_214006_r()) {
                  flag1 = true;
                  if (itemstack.hasContainerItem())
                      this.items.set(1, itemstack.getContainerItem());
                  else
                  if (!itemstack.isEmpty()) {
                     Item item = itemstack.getItem();
                     itemstack.shrink(1);
                     if (itemstack.isEmpty()) {
                        this.items.set(1, itemstack.getContainerItem());
                     }
                  }
               }
            }

            if (this.func_214006_r() && this.func_214008_b(irecipe)) {
               ++this.cookTime;
               if (this.cookTime == this.cookTimeTotal) {
                  this.cookTime = 0;
                  this.cookTimeTotal = this.func_214005_h();
                  this.func_214007_c(irecipe);
                  flag1 = true;
               }
            } else {
               this.cookTime = 0;
            }
         } else if (!this.func_214006_r() && this.cookTime > 0) {
            this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.cookTimeTotal);
         }

         if (flag != this.func_214006_r()) {
            flag1 = true;
            this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(AbstractFurnaceBlock.field_220091_b, Boolean.valueOf(this.func_214006_r())), 3);
         }
      }

      if (flag1) {
         this.markDirty();
      }

   }

   protected boolean func_214008_b(@Nullable IRecipe<?> p_214008_1_) {
      if (!this.items.get(0).isEmpty() && p_214008_1_ != null) {
         ItemStack itemstack = p_214008_1_.getRecipeOutput();
         if (itemstack.isEmpty()) {
            return false;
         } else {
            ItemStack itemstack1 = this.items.get(2);
            if (itemstack1.isEmpty()) {
               return true;
            } else if (!itemstack1.isItemEqual(itemstack)) {
               return false;
            } else if (itemstack1.getCount() + itemstack.getCount() <= this.getInventoryStackLimit() && itemstack1.getCount() + itemstack.getCount() <= itemstack1.getMaxStackSize()) { // Forge fix: make furnace respect stack sizes in furnace recipes
               return true;
            } else {
               return itemstack1.getCount() + itemstack.getCount() <= itemstack.getMaxStackSize(); // Forge fix: make furnace respect stack sizes in furnace recipes
            }
         }
      } else {
         return false;
      }
   }

   private void func_214007_c(@Nullable IRecipe<?> p_214007_1_) {
      if (p_214007_1_ != null && this.func_214008_b(p_214007_1_)) {
         ItemStack itemstack = this.items.get(0);
         ItemStack itemstack1 = p_214007_1_.getRecipeOutput();
         ItemStack itemstack2 = this.items.get(2);
         if (itemstack2.isEmpty()) {
            this.items.set(2, itemstack1.copy());
         } else if (itemstack2.getItem() == itemstack1.getItem()) {
            itemstack2.grow(itemstack1.getCount());
         }

         if (!this.world.isRemote) {
            this.setRecipeUsed(p_214007_1_);
         }

         if (itemstack.getItem() == Blocks.WET_SPONGE.asItem() && !this.items.get(1).isEmpty() && this.items.get(1).getItem() == Items.BUCKET) {
            this.items.set(1, new ItemStack(Items.WATER_BUCKET));
         }

         itemstack.shrink(1);
      }
   }

   protected int getBurnTime(ItemStack p_213997_1_) {
      if (p_213997_1_.isEmpty()) {
         return 0;
      } else {
         Item item = p_213997_1_.getItem();
         int ret = p_213997_1_.getBurnTime();
         return net.minecraftforge.event.ForgeEventFactory.getItemBurnTime(p_213997_1_, ret == -1 ? getBurnTimes().getOrDefault(item, 0) : ret);
      }
   }

   protected int func_214005_h() {
      return this.world.getRecipeManager().getRecipe((IRecipeType<AbstractCookingRecipe>)this.recipeType, this, this.world).map(AbstractCookingRecipe::getCookTime).orElse(200);
   }

   public static boolean isFuel(ItemStack p_213991_0_) {
      int ret = p_213991_0_.getBurnTime();
      return net.minecraftforge.event.ForgeEventFactory.getItemBurnTime(p_213991_0_, ret == -1 ? getBurnTimes().getOrDefault(p_213991_0_.getItem(), 0) : ret) > 0;
   }

   public int[] getSlotsForFace(Direction side) {
      if (side == Direction.DOWN) {
         return field_214016_h;
      } else {
         return side == Direction.UP ? field_214015_g : field_214017_i;
      }
   }

   /**
    * Returns true if automation can insert the given item in the given slot from the given side.
    */
   public boolean canInsertItem(int index, ItemStack itemStackIn, @Nullable Direction direction) {
      return this.isItemValidForSlot(index, itemStackIn);
   }

   /**
    * Returns true if automation can extract the given item in the given slot from the given side.
    */
   public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
      if (direction == Direction.DOWN && index == 1) {
         Item item = stack.getItem();
         if (item != Items.WATER_BUCKET && item != Items.BUCKET) {
            return false;
         }
      }

      return true;
   }

   /**
    * Returns the number of slots in the inventory.
    */
   public int getSizeInventory() {
      return this.items.size();
   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.items) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   /**
    * Returns the stack in the given slot.
    */
   public ItemStack getStackInSlot(int index) {
      return this.items.get(index);
   }

   /**
    * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
    */
   public ItemStack decrStackSize(int index, int count) {
      return ItemStackHelper.getAndSplit(this.items, index, count);
   }

   /**
    * Removes a stack from the given slot and returns it.
    */
   public ItemStack removeStackFromSlot(int index) {
      return ItemStackHelper.getAndRemove(this.items, index);
   }

   /**
    * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
    */
   public void setInventorySlotContents(int index, ItemStack stack) {
      ItemStack itemstack = this.items.get(index);
      boolean flag = !stack.isEmpty() && stack.isItemEqual(itemstack) && ItemStack.areItemStackTagsEqual(stack, itemstack);
      this.items.set(index, stack);
      if (stack.getCount() > this.getInventoryStackLimit()) {
         stack.setCount(this.getInventoryStackLimit());
      }

      if (index == 0 && !flag) {
         this.cookTimeTotal = this.func_214005_h();
         this.cookTime = 0;
         this.markDirty();
      }

   }

   /**
    * Don't rename this method to canInteractWith due to conflicts with Container
    */
   public boolean isUsableByPlayer(PlayerEntity player) {
      if (this.world.getTileEntity(this.pos) != this) {
         return false;
      } else {
         return player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
      }
   }

   /**
    * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot. For
    * guis use Slot.isItemValid
    */
   public boolean isItemValidForSlot(int index, ItemStack stack) {
      if (index == 2) {
         return false;
      } else if (index != 1) {
         return true;
      } else {
         ItemStack itemstack = this.items.get(1);
         return isFuel(stack) || stack.getItem() == Items.BUCKET && itemstack.getItem() != Items.BUCKET;
      }
   }

   public void clear() {
      this.items.clear();
   }

   public void setRecipeUsed(@Nullable IRecipe<?> recipe) {
      if (recipe != null) {
         this.field_214022_n.compute(recipe.getId(), (p_214004_0_, p_214004_1_) -> {
            return 1 + (p_214004_1_ == null ? 0 : p_214004_1_);
         });
      }

   }

   @Nullable
   public IRecipe<?> getRecipeUsed() {
      return null;
   }

   public void onCrafting(PlayerEntity player) {
   }

   public void func_213995_d(PlayerEntity p_213995_1_) {
      List<IRecipe<?>> list = Lists.newArrayList();

      for(Entry<ResourceLocation, Integer> entry : this.field_214022_n.entrySet()) {
         p_213995_1_.world.getRecipeManager().getRecipe(entry.getKey()).ifPresent((p_213993_3_) -> {
            list.add(p_213993_3_);
            func_214003_a(p_213995_1_, entry.getValue(), ((AbstractCookingRecipe)p_213993_3_).getExperience());
         });
      }

      p_213995_1_.unlockRecipes(list);
      this.field_214022_n.clear();
   }

   private static void func_214003_a(PlayerEntity p_214003_0_, int p_214003_1_, float p_214003_2_) {
      if (p_214003_2_ == 0.0F) {
         p_214003_1_ = 0;
      } else if (p_214003_2_ < 1.0F) {
         int i = MathHelper.floor((float)p_214003_1_ * p_214003_2_);
         if (i < MathHelper.ceil((float)p_214003_1_ * p_214003_2_) && Math.random() < (double)((float)p_214003_1_ * p_214003_2_ - (float)i)) {
            ++i;
         }

         p_214003_1_ = i;
      }

      while(p_214003_1_ > 0) {
         int j = ExperienceOrbEntity.getXPSplit(p_214003_1_);
         p_214003_1_ -= j;
         p_214003_0_.world.addEntity(new ExperienceOrbEntity(p_214003_0_.world, p_214003_0_.posX, p_214003_0_.posY + 0.5D, p_214003_0_.posZ + 0.5D, j));
      }

   }

   public void fillStackedContents(RecipeItemHelper helper) {
      for(ItemStack itemstack : this.items) {
         helper.accountStack(itemstack);
      }

   }

   net.minecraftforge.common.util.LazyOptional<? extends net.minecraftforge.items.IItemHandler>[] handlers =
           net.minecraftforge.items.wrapper.SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);

   @Override
   public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable Direction facing) {
      if (!this.removed && facing != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
         if (facing == Direction.UP)
            return handlers[0].cast();
         else if (facing == Direction.DOWN)
            return handlers[1].cast();
         else
            return handlers[2].cast();
      }
      return super.getCapability(capability, facing);
   }

   /**
    * invalidates a tile entity
    */
   @Override
   public void remove() {
      super.remove();
      for (int x = 0; x < handlers.length; x++)
        handlers[x].invalidate();
   }
}