package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.model.IllagerModel;
import net.minecraft.entity.monster.AbstractIllagerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class IllagerRenderer<T extends AbstractIllagerEntity> extends MobRenderer<T, IllagerModel<T>> {
   protected IllagerRenderer(EntityRendererManager p_i50966_1_, IllagerModel<T> p_i50966_2_, float p_i50966_3_) {
      super(p_i50966_1_, p_i50966_2_, p_i50966_3_);
      this.addLayer(new HeadLayer<>(this));
   }

   public IllagerRenderer(EntityRendererManager p_i50967_1_) {
      super(p_i50967_1_, new IllagerModel<>(0.0F, 0.0F, 64, 64), 0.5F);
      this.addLayer(new HeadLayer<>(this));
   }

   /**
    * Allows the render to do state modifications necessary before the model is rendered.
    */
   protected void preRenderCallback(T entitylivingbaseIn, float partialTickTime) {
      float f = 0.9375F;
      GlStateManager.scalef(0.9375F, 0.9375F, 0.9375F);
   }
}