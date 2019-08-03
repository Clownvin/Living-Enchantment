package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.layers.PhantomEyesLayer;
import net.minecraft.client.renderer.entity.model.PhantomModel;
import net.minecraft.entity.monster.PhantomEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PhantomRenderer extends MobRenderer<PhantomEntity, PhantomModel<PhantomEntity>> {
   private static final ResourceLocation PHANTOM_LOCATION = new ResourceLocation("textures/entity/phantom.png");

   public PhantomRenderer(EntityRendererManager p_i48829_1_) {
      super(p_i48829_1_, new PhantomModel<>(), 0.75F);
      this.addLayer(new PhantomEyesLayer<>(this));
   }

   /**
    * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
    */
   protected ResourceLocation getEntityTexture(PhantomEntity entity) {
      return PHANTOM_LOCATION;
   }

   /**
    * Allows the render to do state modifications necessary before the model is rendered.
    */
   protected void preRenderCallback(PhantomEntity entitylivingbaseIn, float partialTickTime) {
      int i = entitylivingbaseIn.getPhantomSize();
      float f = 1.0F + 0.15F * (float)i;
      GlStateManager.scalef(f, f, f);
      GlStateManager.translatef(0.0F, 1.3125F, 0.1875F);
   }

   protected void applyRotations(PhantomEntity entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
      GlStateManager.rotatef(entityLiving.rotationPitch, 1.0F, 0.0F, 0.0F);
   }
}