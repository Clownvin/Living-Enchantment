package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CapeLayer extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {
   public CapeLayer(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> p_i50950_1_) {
      super(p_i50950_1_);
   }

   public void render(AbstractClientPlayerEntity entityIn, float p_212842_2_, float p_212842_3_, float p_212842_4_, float p_212842_5_, float p_212842_6_, float p_212842_7_, float p_212842_8_) {
      if (entityIn.hasPlayerInfo() && !entityIn.isInvisible() && entityIn.isWearing(PlayerModelPart.CAPE) && entityIn.getLocationCape() != null) {
         ItemStack itemstack = entityIn.getItemStackFromSlot(EquipmentSlotType.CHEST);
         if (itemstack.getItem() != Items.ELYTRA) {
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.bindTexture(entityIn.getLocationCape());
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0.0F, 0.0F, 0.125F);
            double d0 = MathHelper.lerp((double)p_212842_4_, entityIn.prevChasingPosX, entityIn.chasingPosX) - MathHelper.lerp((double)p_212842_4_, entityIn.prevPosX, entityIn.posX);
            double d1 = MathHelper.lerp((double)p_212842_4_, entityIn.prevChasingPosY, entityIn.chasingPosY) - MathHelper.lerp((double)p_212842_4_, entityIn.prevPosY, entityIn.posY);
            double d2 = MathHelper.lerp((double)p_212842_4_, entityIn.prevChasingPosZ, entityIn.chasingPosZ) - MathHelper.lerp((double)p_212842_4_, entityIn.prevPosZ, entityIn.posZ);
            float f = entityIn.prevRenderYawOffset + (entityIn.renderYawOffset - entityIn.prevRenderYawOffset);
            double d3 = (double)MathHelper.sin(f * ((float)Math.PI / 180F));
            double d4 = (double)(-MathHelper.cos(f * ((float)Math.PI / 180F)));
            float f1 = (float)d1 * 10.0F;
            f1 = MathHelper.clamp(f1, -6.0F, 32.0F);
            float f2 = (float)(d0 * d3 + d2 * d4) * 100.0F;
            f2 = MathHelper.clamp(f2, 0.0F, 150.0F);
            float f3 = (float)(d0 * d4 - d2 * d3) * 100.0F;
            f3 = MathHelper.clamp(f3, -20.0F, 20.0F);
            if (f2 < 0.0F) {
               f2 = 0.0F;
            }

            float f4 = MathHelper.lerp(p_212842_4_, entityIn.prevCameraYaw, entityIn.cameraYaw);
            f1 = f1 + MathHelper.sin(MathHelper.lerp(p_212842_4_, entityIn.prevDistanceWalkedModified, entityIn.distanceWalkedModified) * 6.0F) * 32.0F * f4;
            if (entityIn.func_213287_bg()) {
               f1 += 25.0F;
            }

            GlStateManager.rotatef(6.0F + f2 / 2.0F + f1, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(f3 / 2.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotatef(-f3 / 2.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
            this.getEntityModel().renderCape(0.0625F);
            GlStateManager.popMatrix();
         }
      }
   }

   public boolean shouldCombineTextures() {
      return false;
   }
}