package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.FoxModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FoxHeldItemLayer extends LayerRenderer<FoxEntity, FoxModel<FoxEntity>> {
   public FoxHeldItemLayer(IEntityRenderer<FoxEntity, FoxModel<FoxEntity>> p_i50938_1_) {
      super(p_i50938_1_);
   }

   public void render(FoxEntity entityIn, float p_212842_2_, float p_212842_3_, float p_212842_4_, float p_212842_5_, float p_212842_6_, float p_212842_7_, float p_212842_8_) {
      ItemStack itemstack = entityIn.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
      if (!itemstack.isEmpty()) {
         boolean flag = entityIn.isSleeping();
         boolean flag1 = entityIn.isChild();
         GlStateManager.pushMatrix();
         if (flag1) {
            float f = 0.75F;
            GlStateManager.scalef(0.75F, 0.75F, 0.75F);
            GlStateManager.translatef(0.0F, 8.0F * p_212842_8_, 3.35F * p_212842_8_);
         }

         GlStateManager.translatef((this.getEntityModel()).field_217115_a.rotationPointX / 16.0F, (this.getEntityModel()).field_217115_a.rotationPointY / 16.0F, (this.getEntityModel()).field_217115_a.rotationPointZ / 16.0F);
         float f1 = entityIn.func_213475_v(p_212842_4_) * (180F / (float)Math.PI);
         GlStateManager.rotatef(f1, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotatef(p_212842_6_, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(p_212842_7_, 1.0F, 0.0F, 0.0F);
         if (entityIn.isChild()) {
            if (flag) {
               GlStateManager.translatef(0.4F, 0.26F, 0.15F);
            } else {
               GlStateManager.translatef(0.06F, 0.26F, -0.5F);
            }
         } else if (flag) {
            GlStateManager.translatef(0.46F, 0.26F, 0.22F);
         } else {
            GlStateManager.translatef(0.06F, 0.27F, -0.5F);
         }

         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         if (flag) {
            GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
         }

         Minecraft.getInstance().getItemRenderer().renderItem(itemstack, entityIn, ItemCameraTransforms.TransformType.GROUND, false);
         GlStateManager.popMatrix();
      }
   }

   public boolean shouldCombineTextures() {
      return false;
   }
}