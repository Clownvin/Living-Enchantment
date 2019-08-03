package net.minecraft.client.renderer.tileentity;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Arrays;
import java.util.Comparator;
import java.util.UUID;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BannerTextures;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.model.ShieldModel;
import net.minecraft.client.renderer.entity.model.TridentModel;
import net.minecraft.item.BannerItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.BannerTileEntity;
import net.minecraft.tileentity.BedTileEntity;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.ConduitTileEntity;
import net.minecraft.tileentity.EnderChestTileEntity;
import net.minecraft.tileentity.ShulkerBoxTileEntity;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.tileentity.TrappedChestTileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;

@OnlyIn(Dist.CLIENT)
public class ItemStackTileEntityRenderer {
   private static final ShulkerBoxTileEntity[] SHULKER_BOXES = Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map(ShulkerBoxTileEntity::new).toArray((p_199929_0_) -> {
      return new ShulkerBoxTileEntity[p_199929_0_];
   });
   private static final ShulkerBoxTileEntity SHULKER_BOX = new ShulkerBoxTileEntity((DyeColor)null);
   public static final ItemStackTileEntityRenderer instance = new ItemStackTileEntityRenderer();
   private final ChestTileEntity chestBasic = new ChestTileEntity();
   private final ChestTileEntity chestTrap = new TrappedChestTileEntity();
   private final EnderChestTileEntity enderChest = new EnderChestTileEntity();
   private final BannerTileEntity banner = new BannerTileEntity();
   private final BedTileEntity bed = new BedTileEntity();
   private final SkullTileEntity skull = new SkullTileEntity();
   private final ConduitTileEntity conduit = new ConduitTileEntity();
   private final ShieldModel modelShield = new ShieldModel();
   private final TridentModel trident = new TridentModel();

   public void renderByItem(ItemStack itemStackIn) {
      Item item = itemStackIn.getItem();
      if (item instanceof BannerItem) {
         this.banner.loadFromItemStack(itemStackIn, ((BannerItem)item).getColor());
         TileEntityRendererDispatcher.instance.renderAsItem(this.banner);
      } else if (item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof BedBlock) {
         this.bed.setColor(((BedBlock)((BlockItem)item).getBlock()).getColor());
         TileEntityRendererDispatcher.instance.renderAsItem(this.bed);
      } else if (item == Items.SHIELD) {
         if (itemStackIn.getChildTag("BlockEntityTag") != null) {
            this.banner.loadFromItemStack(itemStackIn, ShieldItem.getColor(itemStackIn));
            Minecraft.getInstance().getTextureManager().bindTexture(BannerTextures.SHIELD_DESIGNS.getResourceLocation(this.banner.getPatternResourceLocation(), this.banner.getPatternList(), this.banner.getColorList()));
         } else {
            Minecraft.getInstance().getTextureManager().bindTexture(BannerTextures.SHIELD_BASE_TEXTURE);
         }

         GlStateManager.pushMatrix();
         GlStateManager.scalef(1.0F, -1.0F, -1.0F);
         this.modelShield.render();
         if (itemStackIn.hasEffect()) {
            this.renderEffect(this.modelShield::render);
         }

         GlStateManager.popMatrix();
      } else if (item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof AbstractSkullBlock) {
         GameProfile gameprofile = null;
         if (itemStackIn.hasTag()) {
            CompoundNBT compoundnbt = itemStackIn.getTag();
            if (compoundnbt.contains("SkullOwner", 10)) {
               gameprofile = NBTUtil.readGameProfile(compoundnbt.getCompound("SkullOwner"));
            } else if (compoundnbt.contains("SkullOwner", 8) && !StringUtils.isBlank(compoundnbt.getString("SkullOwner"))) {
               GameProfile gameprofile1 = new GameProfile((UUID)null, compoundnbt.getString("SkullOwner"));
               gameprofile = SkullTileEntity.updateGameProfile(gameprofile1);
               compoundnbt.remove("SkullOwner");
               compoundnbt.put("SkullOwner", NBTUtil.writeGameProfile(new CompoundNBT(), gameprofile));
            }
         }

         if (SkullTileEntityRenderer.instance != null) {
            GlStateManager.pushMatrix();
            GlStateManager.disableCull();
            SkullTileEntityRenderer.instance.render(0.0F, 0.0F, 0.0F, (Direction)null, 180.0F, ((AbstractSkullBlock)((BlockItem)item).getBlock()).getSkullType(), gameprofile, -1, 0.0F);
            GlStateManager.enableCull();
            GlStateManager.popMatrix();
         }
      } else if (item == Items.TRIDENT) {
         Minecraft.getInstance().getTextureManager().bindTexture(TridentModel.TEXTURE_LOCATION);
         GlStateManager.pushMatrix();
         GlStateManager.scalef(1.0F, -1.0F, -1.0F);
         this.trident.renderer();
         if (itemStackIn.hasEffect()) {
            this.renderEffect(this.trident::renderer);
         }

         GlStateManager.popMatrix();
      } else if (item instanceof BlockItem && ((BlockItem)item).getBlock() == Blocks.CONDUIT) {
         TileEntityRendererDispatcher.instance.renderAsItem(this.conduit);
      } else if (item == Blocks.ENDER_CHEST.asItem()) {
         TileEntityRendererDispatcher.instance.renderAsItem(this.enderChest);
      } else if (item == Blocks.TRAPPED_CHEST.asItem()) {
         TileEntityRendererDispatcher.instance.renderAsItem(this.chestTrap);
      } else if (Block.getBlockFromItem(item) instanceof ShulkerBoxBlock) {
         DyeColor dyecolor = ShulkerBoxBlock.getColorFromItem(item);
         if (dyecolor == null) {
            TileEntityRendererDispatcher.instance.renderAsItem(SHULKER_BOX);
         } else {
            TileEntityRendererDispatcher.instance.renderAsItem(SHULKER_BOXES[dyecolor.getId()]);
         }
      } else {
         TileEntityRendererDispatcher.instance.renderAsItem(this.chestBasic);
      }

   }

   private void renderEffect(Runnable renderModelFunction) {
      GlStateManager.color3f(0.5019608F, 0.2509804F, 0.8F);
      Minecraft.getInstance().getTextureManager().bindTexture(ItemRenderer.RES_ITEM_GLINT);
      ItemRenderer.renderEffect(Minecraft.getInstance().getTextureManager(), renderModelFunction, 1);
   }
}