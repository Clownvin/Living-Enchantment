package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.INameable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BannerTileEntity extends TileEntity implements INameable {
   private ITextComponent name;
   private DyeColor baseColor = DyeColor.WHITE;
   private ListNBT patterns;
   private boolean patternDataSet;
   private List<BannerPattern> patternList;
   private List<DyeColor> colorList;
   private String patternResourceLocation;

   public BannerTileEntity() {
      super(TileEntityType.BANNER);
   }

   public BannerTileEntity(DyeColor p_i47731_1_) {
      this();
      this.baseColor = p_i47731_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public void loadFromItemStack(ItemStack p_195534_1_, DyeColor p_195534_2_) {
      this.patterns = null;
      CompoundNBT compoundnbt = p_195534_1_.getChildTag("BlockEntityTag");
      if (compoundnbt != null && compoundnbt.contains("Patterns", 9)) {
         this.patterns = compoundnbt.getList("Patterns", 10).copy();
      }

      this.baseColor = p_195534_2_;
      this.patternList = null;
      this.colorList = null;
      this.patternResourceLocation = "";
      this.patternDataSet = true;
      this.name = p_195534_1_.hasDisplayName() ? p_195534_1_.getDisplayName() : null;
   }

   public ITextComponent getName() {
      return (ITextComponent)(this.name != null ? this.name : new TranslationTextComponent("block.minecraft.banner"));
   }

   @Nullable
   public ITextComponent getCustomName() {
      return this.name;
   }

   public void func_213136_a(ITextComponent p_213136_1_) {
      this.name = p_213136_1_;
   }

   public CompoundNBT write(CompoundNBT compound) {
      super.write(compound);
      if (this.patterns != null) {
         compound.put("Patterns", this.patterns);
      }

      if (this.name != null) {
         compound.putString("CustomName", ITextComponent.Serializer.toJson(this.name));
      }

      return compound;
   }

   public void read(CompoundNBT compound) {
      super.read(compound);
      if (compound.contains("CustomName", 8)) {
         this.name = ITextComponent.Serializer.fromJson(compound.getString("CustomName"));
      }

      if (this.hasWorld()) {
         this.baseColor = ((AbstractBannerBlock)this.getBlockState().getBlock()).getColor();
      } else {
         this.baseColor = null;
      }

      this.patterns = compound.getList("Patterns", 10);
      this.patternList = null;
      this.colorList = null;
      this.patternResourceLocation = null;
      this.patternDataSet = true;
   }

   /**
    * Retrieves packet to send to the client whenever this Tile Entity is resynced via World.notifyBlockUpdate. For
    * modded TE's, this packet comes back to you clientside in {@link #onDataPacket}
    */
   @Nullable
   public SUpdateTileEntityPacket getUpdatePacket() {
      return new SUpdateTileEntityPacket(this.pos, 6, this.getUpdateTag());
   }

   /**
    * Get an NBT compound to sync to the client with SPacketChunkData, used for initial loading of the chunk or when
    * many blocks change at once. This compound comes back to you clientside in {@link handleUpdateTag}
    */
   public CompoundNBT getUpdateTag() {
      return this.write(new CompoundNBT());
   }

   /**
    * Retrieves the amount of patterns stored on an ItemStack. If the tag does not exist this value will be 0.
    */
   public static int getPatterns(ItemStack stack) {
      CompoundNBT compoundnbt = stack.getChildTag("BlockEntityTag");
      return compoundnbt != null && compoundnbt.contains("Patterns") ? compoundnbt.getList("Patterns", 10).size() : 0;
   }

   /**
    * Retrieves the list of patterns for this tile entity. The banner data will be initialized/refreshed before this
    * happens.
    */
   @OnlyIn(Dist.CLIENT)
   public List<BannerPattern> getPatternList() {
      this.initializeBannerData();
      return this.patternList;
   }

   /**
    * Retrieves the list of colors for this tile entity. The banner data will be initialized/refreshed before this
    * happens.
    */
   @OnlyIn(Dist.CLIENT)
   public List<DyeColor> getColorList() {
      this.initializeBannerData();
      return this.colorList;
   }

   @OnlyIn(Dist.CLIENT)
   public String getPatternResourceLocation() {
      this.initializeBannerData();
      return this.patternResourceLocation;
   }

   /**
    * Establishes all of the basic properties for the banner. This will also apply the data from the tile entities nbt
    * tag compounds.
    */
   @OnlyIn(Dist.CLIENT)
   private void initializeBannerData() {
      if (this.patternList == null || this.colorList == null || this.patternResourceLocation == null) {
         if (!this.patternDataSet) {
            this.patternResourceLocation = "";
         } else {
            this.patternList = Lists.newArrayList();
            this.colorList = Lists.newArrayList();
            DyeColor dyecolor = this.getBaseColor(this::getBlockState);
            if (dyecolor == null) {
               this.patternResourceLocation = "banner_missing";
            } else {
               this.patternList.add(BannerPattern.BASE);
               this.colorList.add(dyecolor);
               this.patternResourceLocation = "b" + dyecolor.getId();
               if (this.patterns != null) {
                  for(int i = 0; i < this.patterns.size(); ++i) {
                     CompoundNBT compoundnbt = this.patterns.getCompound(i);
                     BannerPattern bannerpattern = BannerPattern.byHash(compoundnbt.getString("Pattern"));
                     if (bannerpattern != null) {
                        this.patternList.add(bannerpattern);
                        int j = compoundnbt.getInt("Color");
                        this.colorList.add(DyeColor.byId(j));
                        this.patternResourceLocation = this.patternResourceLocation + bannerpattern.getHashname() + j;
                     }
                  }
               }
            }

         }
      }
   }

   /**
    * Removes all the banner related data from a provided instance of ItemStack.
    */
   public static void removeBannerData(ItemStack stack) {
      CompoundNBT compoundnbt = stack.getChildTag("BlockEntityTag");
      if (compoundnbt != null && compoundnbt.contains("Patterns", 9)) {
         ListNBT listnbt = compoundnbt.getList("Patterns", 10);
         if (!listnbt.isEmpty()) {
            listnbt.remove(listnbt.size() - 1);
            if (listnbt.isEmpty()) {
               stack.removeChildTag("BlockEntityTag");
            }

         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getItem(BlockState p_190615_1_) {
      ItemStack itemstack = new ItemStack(BannerBlock.forColor(this.getBaseColor(() -> {
         return p_190615_1_;
      })));
      if (this.patterns != null && !this.patterns.isEmpty()) {
         itemstack.getOrCreateChildTag("BlockEntityTag").put("Patterns", this.patterns.copy());
      }

      if (this.name != null) {
         itemstack.setDisplayName(this.name);
      }

      return itemstack;
   }

   public DyeColor getBaseColor(Supplier<BlockState> p_195533_1_) {
      if (this.baseColor == null) {
         this.baseColor = ((AbstractBannerBlock)p_195533_1_.get().getBlock()).getColor();
      }

      return this.baseColor;
   }
}