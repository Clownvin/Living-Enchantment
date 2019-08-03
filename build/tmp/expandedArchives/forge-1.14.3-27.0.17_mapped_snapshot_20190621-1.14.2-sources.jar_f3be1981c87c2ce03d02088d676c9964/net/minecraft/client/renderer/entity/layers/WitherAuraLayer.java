package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.WitherModel;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WitherAuraLayer extends LayerRenderer<WitherEntity, WitherModel<WitherEntity>> {
   private static final ResourceLocation WITHER_ARMOR = new ResourceLocation("textures/entity/wither/wither_armor.png");
   private final WitherModel<WitherEntity> witherModel = new WitherModel<>(0.5F);

   public WitherAuraLayer(IEntityRenderer<WitherEntity, WitherModel<WitherEntity>> p_i50915_1_) {
      super(p_i50915_1_);
   }

   public void render(WitherEntity entityIn, float p_212842_2_, float p_212842_3_, float p_212842_4_, float p_212842_5_, float p_212842_6_, float p_212842_7_, float p_212842_8_) {
      if (entityIn.isArmored()) {
         GlStateManager.depthMask(!entityIn.isInvisible());
         this.bindTexture(WITHER_ARMOR);
         GlStateManager.matrixMode(5890);
         GlStateManager.loadIdentity();
         float f = (float)entityIn.ticksExisted + p_212842_4_;
         float f1 = MathHelper.cos(f * 0.02F) * 3.0F;
         float f2 = f * 0.01F;
         GlStateManager.translatef(f1, f2, 0.0F);
         GlStateManager.matrixMode(5888);
         GlStateManager.enableBlend();
         float f3 = 0.5F;
         GlStateManager.color4f(0.5F, 0.5F, 0.5F, 1.0F);
         GlStateManager.disableLighting();
         GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
         this.witherModel.setLivingAnimations(entityIn, p_212842_2_, p_212842_3_, p_212842_4_);
         this.getEntityModel().setModelAttributes(this.witherModel);
         GameRenderer gamerenderer = Minecraft.getInstance().gameRenderer;
         gamerenderer.setupFogColor(true);
         this.witherModel.render(entityIn, p_212842_2_, p_212842_3_, p_212842_5_, p_212842_6_, p_212842_7_, p_212842_8_);
         gamerenderer.setupFogColor(false);
         GlStateManager.matrixMode(5890);
         GlStateManager.loadIdentity();
         GlStateManager.matrixMode(5888);
         GlStateManager.enableLighting();
         GlStateManager.disableBlend();
         GlStateManager.depthMask(true);
      }
   }

   public boolean shouldCombineTextures() {
      return false;
   }
}