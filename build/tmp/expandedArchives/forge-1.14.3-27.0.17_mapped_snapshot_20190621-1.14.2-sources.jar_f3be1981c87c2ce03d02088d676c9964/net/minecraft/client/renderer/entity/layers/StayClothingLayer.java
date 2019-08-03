package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.SkeletonModel;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StayClothingLayer<T extends MobEntity & IRangedAttackMob, M extends EntityModel<T>> extends LayerRenderer<T, M> {
   private static final ResourceLocation STRAY_CLOTHES_TEXTURES = new ResourceLocation("textures/entity/skeleton/stray_overlay.png");
   private final SkeletonModel<T> layerModel = new SkeletonModel<>(0.25F, true);

   public StayClothingLayer(IEntityRenderer<T, M> p_i50919_1_) {
      super(p_i50919_1_);
   }

   public void render(T entityIn, float p_212842_2_, float p_212842_3_, float p_212842_4_, float p_212842_5_, float p_212842_6_, float p_212842_7_, float p_212842_8_) {
      this.getEntityModel().setModelAttributes(this.layerModel);
      this.layerModel.setLivingAnimations(entityIn, p_212842_2_, p_212842_3_, p_212842_4_);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.bindTexture(STRAY_CLOTHES_TEXTURES);
      this.layerModel.render(entityIn, p_212842_2_, p_212842_3_, p_212842_5_, p_212842_6_, p_212842_7_, p_212842_8_);
   }

   public boolean shouldCombineTextures() {
      return true;
   }
}