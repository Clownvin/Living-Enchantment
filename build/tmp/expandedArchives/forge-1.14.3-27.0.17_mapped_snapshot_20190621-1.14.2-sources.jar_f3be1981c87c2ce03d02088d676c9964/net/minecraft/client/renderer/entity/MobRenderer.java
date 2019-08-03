package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.HangingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class MobRenderer<T extends MobEntity, M extends EntityModel<T>> extends LivingRenderer<T, M> {
   public MobRenderer(EntityRendererManager p_i50961_1_, M p_i50961_2_, float p_i50961_3_) {
      super(p_i50961_1_, p_i50961_2_, p_i50961_3_);
   }

   protected boolean canRenderName(T entity) {
      return super.canRenderName(entity) && (entity.getAlwaysRenderNameTagForRender() || entity.hasCustomName() && entity == this.renderManager.pointedEntity);
   }

   public boolean shouldRender(T livingEntity, ICamera camera, double camX, double camY, double camZ) {
      if (super.shouldRender(livingEntity, camera, camX, camY, camZ)) {
         return true;
      } else {
         Entity entity = livingEntity.getLeashHolder();
         return entity != null ? camera.isBoundingBoxInFrustum(entity.getRenderBoundingBox()) : false;
      }
   }

   /**
    * Renders the desired {@code T} type Entity.
    */
   public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
      if (!this.renderOutlines) {
         this.renderLeash(entity, x, y, z, entityYaw, partialTicks);
      }

   }

   protected void renderLeash(T entityLivingIn, double x, double y, double z, float entityYaw, float partialTicks) {
      Entity entity = entityLivingIn.getLeashHolder();
      if (entity != null) {
         y = y - (1.6D - (double)entityLivingIn.getHeight()) * 0.5D;
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder bufferbuilder = tessellator.getBuffer();
         double d0 = (double)(MathHelper.lerp(partialTicks * 0.5F, entity.rotationYaw, entity.prevRotationYaw) * ((float)Math.PI / 180F));
         double d1 = (double)(MathHelper.lerp(partialTicks * 0.5F, entity.rotationPitch, entity.prevRotationPitch) * ((float)Math.PI / 180F));
         double d2 = Math.cos(d0);
         double d3 = Math.sin(d0);
         double d4 = Math.sin(d1);
         if (entity instanceof HangingEntity) {
            d2 = 0.0D;
            d3 = 0.0D;
            d4 = -1.0D;
         }

         double d5 = Math.cos(d1);
         double d6 = MathHelper.lerp((double)partialTicks, entity.prevPosX, entity.posX) - d2 * 0.7D - d3 * 0.5D * d5;
         double d7 = MathHelper.lerp((double)partialTicks, entity.prevPosY + (double)entity.getEyeHeight() * 0.7D, entity.posY + (double)entity.getEyeHeight() * 0.7D) - d4 * 0.5D - 0.25D;
         double d8 = MathHelper.lerp((double)partialTicks, entity.prevPosZ, entity.posZ) - d3 * 0.7D + d2 * 0.5D * d5;
         double d9 = (double)(MathHelper.lerp(partialTicks, entityLivingIn.renderYawOffset, entityLivingIn.prevRenderYawOffset) * ((float)Math.PI / 180F)) + (Math.PI / 2D);
         d2 = Math.cos(d9) * (double)entityLivingIn.getWidth() * 0.4D;
         d3 = Math.sin(d9) * (double)entityLivingIn.getWidth() * 0.4D;
         double d10 = MathHelper.lerp((double)partialTicks, entityLivingIn.prevPosX, entityLivingIn.posX) + d2;
         double d11 = MathHelper.lerp((double)partialTicks, entityLivingIn.prevPosY, entityLivingIn.posY);
         double d12 = MathHelper.lerp((double)partialTicks, entityLivingIn.prevPosZ, entityLivingIn.posZ) + d3;
         x = x + d2;
         z = z + d3;
         double d13 = (double)((float)(d6 - d10));
         double d14 = (double)((float)(d7 - d11));
         double d15 = (double)((float)(d8 - d12));
         GlStateManager.disableTexture();
         GlStateManager.disableLighting();
         GlStateManager.disableCull();
         int i = 24;
         double d16 = 0.025D;
         bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);

         for(int j = 0; j <= 24; ++j) {
            float f = 0.5F;
            float f1 = 0.4F;
            float f2 = 0.3F;
            if (j % 2 == 0) {
               f *= 0.7F;
               f1 *= 0.7F;
               f2 *= 0.7F;
            }

            float f3 = (float)j / 24.0F;
            bufferbuilder.pos(x + d13 * (double)f3 + 0.0D, y + d14 * (double)(f3 * f3 + f3) * 0.5D + (double)((24.0F - (float)j) / 18.0F + 0.125F), z + d15 * (double)f3).color(f, f1, f2, 1.0F).endVertex();
            bufferbuilder.pos(x + d13 * (double)f3 + 0.025D, y + d14 * (double)(f3 * f3 + f3) * 0.5D + (double)((24.0F - (float)j) / 18.0F + 0.125F) + 0.025D, z + d15 * (double)f3).color(f, f1, f2, 1.0F).endVertex();
         }

         tessellator.draw();
         bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);

         for(int k = 0; k <= 24; ++k) {
            float f4 = 0.5F;
            float f5 = 0.4F;
            float f6 = 0.3F;
            if (k % 2 == 0) {
               f4 *= 0.7F;
               f5 *= 0.7F;
               f6 *= 0.7F;
            }

            float f7 = (float)k / 24.0F;
            bufferbuilder.pos(x + d13 * (double)f7 + 0.0D, y + d14 * (double)(f7 * f7 + f7) * 0.5D + (double)((24.0F - (float)k) / 18.0F + 0.125F) + 0.025D, z + d15 * (double)f7).color(f4, f5, f6, 1.0F).endVertex();
            bufferbuilder.pos(x + d13 * (double)f7 + 0.025D, y + d14 * (double)(f7 * f7 + f7) * 0.5D + (double)((24.0F - (float)k) / 18.0F + 0.125F), z + d15 * (double)f7 + 0.025D).color(f4, f5, f6, 1.0F).endVertex();
         }

         tessellator.draw();
         GlStateManager.enableLighting();
         GlStateManager.enableTexture();
         GlStateManager.enableCull();
      }
   }
}