package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class LayerRenderer<T extends Entity, M extends EntityModel<T>> {
   private final IEntityRenderer<T, M> entityRenderer;

   public LayerRenderer(IEntityRenderer<T, M> entityRendererIn) {
      this.entityRenderer = entityRendererIn;
   }

   public M getEntityModel() {
      return this.entityRenderer.getEntityModel();
   }

   public void bindTexture(ResourceLocation p_215333_1_) {
      this.entityRenderer.bindTexture(p_215333_1_);
   }

   public void func_215334_a(T p_215334_1_) {
      this.entityRenderer.func_217758_e(p_215334_1_);
   }

   public abstract void render(T entityIn, float p_212842_2_, float p_212842_3_, float p_212842_4_, float p_212842_5_, float p_212842_6_, float p_212842_7_, float p_212842_8_);

   public abstract boolean shouldCombineTextures();
}