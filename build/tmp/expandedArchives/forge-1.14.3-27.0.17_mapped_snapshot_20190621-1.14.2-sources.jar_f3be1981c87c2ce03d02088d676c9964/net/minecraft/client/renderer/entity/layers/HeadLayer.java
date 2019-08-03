package net.minecraft.client.renderer.entity.layers;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.UUID;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.SkullTileEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.ZombieVillagerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;

@OnlyIn(Dist.CLIENT)
public class HeadLayer<T extends LivingEntity, M extends EntityModel<T> & IHasHead> extends LayerRenderer<T, M> {
   public HeadLayer(IEntityRenderer<T, M> p_i50946_1_) {
      super(p_i50946_1_);
   }

   public void render(T entityIn, float p_212842_2_, float p_212842_3_, float p_212842_4_, float p_212842_5_, float p_212842_6_, float p_212842_7_, float p_212842_8_) {
      ItemStack itemstack = entityIn.getItemStackFromSlot(EquipmentSlotType.HEAD);
      if (!itemstack.isEmpty()) {
         Item item = itemstack.getItem();
         GlStateManager.pushMatrix();
         if (entityIn.func_213287_bg()) {
            GlStateManager.translatef(0.0F, 0.2F, 0.0F);
         }

         boolean flag = entityIn instanceof VillagerEntity || entityIn instanceof ZombieVillagerEntity;
         if (entityIn.isChild() && !(entityIn instanceof VillagerEntity)) {
            float f = 2.0F;
            float f1 = 1.4F;
            GlStateManager.translatef(0.0F, 0.5F * p_212842_8_, 0.0F);
            GlStateManager.scalef(0.7F, 0.7F, 0.7F);
            GlStateManager.translatef(0.0F, 16.0F * p_212842_8_, 0.0F);
         }

         ((IHasHead)this.getEntityModel()).func_217142_c(0.0625F);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         if (item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof AbstractSkullBlock) {
            float f3 = 1.1875F;
            GlStateManager.scalef(1.1875F, -1.1875F, -1.1875F);
            if (flag) {
               GlStateManager.translatef(0.0F, 0.0625F, 0.0F);
            }

            GameProfile gameprofile = null;
            if (itemstack.hasTag()) {
               CompoundNBT compoundnbt = itemstack.getTag();
               if (compoundnbt.contains("SkullOwner", 10)) {
                  gameprofile = NBTUtil.readGameProfile(compoundnbt.getCompound("SkullOwner"));
               } else if (compoundnbt.contains("SkullOwner", 8)) {
                  String s = compoundnbt.getString("SkullOwner");
                  if (!StringUtils.isBlank(s)) {
                     gameprofile = SkullTileEntity.updateGameProfile(new GameProfile((UUID)null, s));
                     compoundnbt.put("SkullOwner", NBTUtil.writeGameProfile(new CompoundNBT(), gameprofile));
                  }
               }
            }

            SkullTileEntityRenderer.instance.render(-0.5F, 0.0F, -0.5F, (Direction)null, 180.0F, ((AbstractSkullBlock)((BlockItem)item).getBlock()).getSkullType(), gameprofile, -1, p_212842_2_);
         } else if (!(item instanceof ArmorItem) || ((ArmorItem)item).getEquipmentSlot() != EquipmentSlotType.HEAD) {
            float f2 = 0.625F;
            GlStateManager.translatef(0.0F, -0.25F, 0.0F);
            GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.scalef(0.625F, -0.625F, -0.625F);
            if (flag) {
               GlStateManager.translatef(0.0F, 0.1875F, 0.0F);
            }

            Minecraft.getInstance().getFirstPersonRenderer().renderItem(entityIn, itemstack, ItemCameraTransforms.TransformType.HEAD);
         }

         GlStateManager.popMatrix();
      }
   }

   public boolean shouldCombineTextures() {
      return false;
   }
}