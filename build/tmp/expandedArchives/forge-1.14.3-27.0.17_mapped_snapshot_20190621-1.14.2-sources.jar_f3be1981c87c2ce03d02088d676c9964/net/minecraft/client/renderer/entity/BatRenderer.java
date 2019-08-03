package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.model.BatModel;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BatRenderer extends MobRenderer<BatEntity, BatModel> {
   private static final ResourceLocation BAT_TEXTURES = new ResourceLocation("textures/entity/bat.png");

   public BatRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new BatModel(), 0.25F);
   }

   /**
    * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
    */
   protected ResourceLocation getEntityTexture(BatEntity entity) {
      return BAT_TEXTURES;
   }

   /**
    * Allows the render to do state modifications necessary before the model is rendered.
    */
   protected void preRenderCallback(BatEntity entitylivingbaseIn, float partialTickTime) {
      GlStateManager.scalef(0.35F, 0.35F, 0.35F);
   }

   protected void applyRotations(BatEntity entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      if (entityLiving.getIsBatHanging()) {
         GlStateManager.translatef(0.0F, -0.1F, 0.0F);
      } else {
         GlStateManager.translatef(0.0F, MathHelper.cos(ageInTicks * 0.3F) * 0.1F, 0.0F);
      }

      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}