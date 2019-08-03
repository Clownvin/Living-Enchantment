package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.model.HorseModel;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractHorseRenderer<T extends AbstractHorseEntity, M extends HorseModel<T>> extends MobRenderer<T, M> {
   private final float scale;

   public AbstractHorseRenderer(EntityRendererManager p_i50975_1_, M p_i50975_2_, float p_i50975_3_) {
      super(p_i50975_1_, p_i50975_2_, 0.75F);
      this.scale = p_i50975_3_;
   }

   /**
    * Allows the render to do state modifications necessary before the model is rendered.
    */
   protected void preRenderCallback(T entitylivingbaseIn, float partialTickTime) {
      GlStateManager.scalef(this.scale, this.scale, this.scale);
      super.preRenderCallback(entitylivingbaseIn, partialTickTime);
   }
}