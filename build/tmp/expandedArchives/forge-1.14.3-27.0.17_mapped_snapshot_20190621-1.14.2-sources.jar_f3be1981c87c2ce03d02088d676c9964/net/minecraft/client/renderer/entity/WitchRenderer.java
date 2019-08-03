package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.layers.WitchHeldItemLayer;
import net.minecraft.client.renderer.entity.model.WitchModel;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WitchRenderer extends MobRenderer<WitchEntity, WitchModel<WitchEntity>> {
   private static final ResourceLocation WITCH_TEXTURES = new ResourceLocation("textures/entity/witch.png");

   public WitchRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new WitchModel<>(0.0F), 0.5F);
      this.addLayer(new WitchHeldItemLayer<>(this));
   }

   /**
    * Renders the desired {@code T} type Entity.
    */
   public void doRender(WitchEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
      this.field_77045_g.func_205074_a(!entity.getHeldItemMainhand().isEmpty());
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
   }

   /**
    * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
    */
   protected ResourceLocation getEntityTexture(WitchEntity entity) {
      return WITCH_TEXTURES;
   }

   /**
    * Allows the render to do state modifications necessary before the model is rendered.
    */
   protected void preRenderCallback(WitchEntity entitylivingbaseIn, float partialTickTime) {
      float f = 0.9375F;
      GlStateManager.scalef(0.9375F, 0.9375F, 0.9375F);
   }
}