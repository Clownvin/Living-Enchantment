package net.minecraft.client.renderer.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FoxModel<T extends FoxEntity> extends EntityModel<T> {
   public final RendererModel field_217115_a;
   private final RendererModel field_217116_b;
   private final RendererModel field_217117_f;
   private final RendererModel field_217118_g;
   private final RendererModel field_217119_h;
   private final RendererModel field_217120_i;
   private final RendererModel field_217121_j;
   private final RendererModel field_217122_k;
   private final RendererModel field_217123_l;
   private final RendererModel field_217124_m;
   private float field_217125_n;

   public FoxModel() {
      this.textureWidth = 48;
      this.textureHeight = 32;
      this.field_217115_a = new RendererModel(this, 1, 5);
      this.field_217115_a.addBox(-3.0F, -2.0F, -5.0F, 8, 6, 6);
      this.field_217115_a.setRotationPoint(-1.0F, 16.5F, -3.0F);
      this.field_217116_b = new RendererModel(this, 8, 1);
      this.field_217116_b.addBox(-3.0F, -4.0F, -4.0F, 2, 2, 1);
      this.field_217117_f = new RendererModel(this, 15, 1);
      this.field_217117_f.addBox(3.0F, -4.0F, -4.0F, 2, 2, 1);
      this.field_217118_g = new RendererModel(this, 6, 18);
      this.field_217118_g.addBox(-1.0F, 2.01F, -8.0F, 4, 2, 3);
      this.field_217115_a.addChild(this.field_217116_b);
      this.field_217115_a.addChild(this.field_217117_f);
      this.field_217115_a.addChild(this.field_217118_g);
      this.field_217119_h = new RendererModel(this, 24, 15);
      this.field_217119_h.addBox(-3.0F, 3.999F, -3.5F, 6, 11, 6);
      this.field_217119_h.setRotationPoint(0.0F, 16.0F, -6.0F);
      float f = 0.001F;
      this.field_217120_i = new RendererModel(this, 13, 24);
      this.field_217120_i.addBox(2.0F, 0.5F, -1.0F, 2, 6, 2, 0.001F);
      this.field_217120_i.setRotationPoint(-5.0F, 17.5F, 7.0F);
      this.field_217121_j = new RendererModel(this, 4, 24);
      this.field_217121_j.addBox(2.0F, 0.5F, -1.0F, 2, 6, 2, 0.001F);
      this.field_217121_j.setRotationPoint(-1.0F, 17.5F, 7.0F);
      this.field_217122_k = new RendererModel(this, 13, 24);
      this.field_217122_k.addBox(2.0F, 0.5F, -1.0F, 2, 6, 2, 0.001F);
      this.field_217122_k.setRotationPoint(-5.0F, 17.5F, 0.0F);
      this.field_217123_l = new RendererModel(this, 4, 24);
      this.field_217123_l.addBox(2.0F, 0.5F, -1.0F, 2, 6, 2, 0.001F);
      this.field_217123_l.setRotationPoint(-1.0F, 17.5F, 0.0F);
      this.field_217124_m = new RendererModel(this, 30, 0);
      this.field_217124_m.addBox(2.0F, 0.0F, -1.0F, 4, 9, 5);
      this.field_217124_m.setRotationPoint(-4.0F, 15.0F, -1.0F);
      this.field_217119_h.addChild(this.field_217124_m);
   }

   public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
      this.field_217119_h.rotateAngleX = ((float)Math.PI / 2F);
      this.field_217124_m.rotateAngleX = -0.05235988F;
      this.field_217120_i.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
      this.field_217121_j.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
      this.field_217122_k.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
      this.field_217123_l.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
      this.field_217115_a.setRotationPoint(-1.0F, 16.5F, -3.0F);
      this.field_217115_a.rotateAngleY = 0.0F;
      this.field_217115_a.rotateAngleZ = entityIn.func_213475_v(partialTick);
      this.field_217120_i.showModel = true;
      this.field_217121_j.showModel = true;
      this.field_217122_k.showModel = true;
      this.field_217123_l.showModel = true;
      this.field_217119_h.setRotationPoint(0.0F, 16.0F, -6.0F);
      this.field_217119_h.rotateAngleZ = 0.0F;
      this.field_217120_i.setRotationPoint(-5.0F, 17.5F, 7.0F);
      this.field_217121_j.setRotationPoint(-1.0F, 17.5F, 7.0F);
      if (entityIn.isCrouching()) {
         this.field_217119_h.rotateAngleX = 1.6755161F;
         float f = entityIn.func_213503_w(partialTick);
         this.field_217119_h.setRotationPoint(0.0F, 16.0F + entityIn.func_213503_w(partialTick), -6.0F);
         this.field_217115_a.setRotationPoint(-1.0F, 16.5F + f, -3.0F);
         this.field_217115_a.rotateAngleY = 0.0F;
      } else if (entityIn.isSleeping()) {
         this.field_217119_h.rotateAngleZ = (-(float)Math.PI / 2F);
         this.field_217119_h.setRotationPoint(0.0F, 21.0F, -6.0F);
         this.field_217124_m.rotateAngleX = -2.6179938F;
         if (this.isChild) {
            this.field_217124_m.rotateAngleX = -2.1816616F;
            this.field_217119_h.setRotationPoint(0.0F, 21.0F, -2.0F);
         }

         this.field_217115_a.setRotationPoint(1.0F, 19.49F, -3.0F);
         this.field_217115_a.rotateAngleX = 0.0F;
         this.field_217115_a.rotateAngleY = -2.0943952F;
         this.field_217115_a.rotateAngleZ = 0.0F;
         this.field_217120_i.showModel = false;
         this.field_217121_j.showModel = false;
         this.field_217122_k.showModel = false;
         this.field_217123_l.showModel = false;
      } else if (entityIn.isSitting()) {
         this.field_217119_h.rotateAngleX = ((float)Math.PI / 6F);
         this.field_217119_h.setRotationPoint(0.0F, 9.0F, -3.0F);
         this.field_217124_m.rotateAngleX = ((float)Math.PI / 4F);
         this.field_217124_m.setRotationPoint(-4.0F, 15.0F, -2.0F);
         this.field_217115_a.setRotationPoint(-1.0F, 10.0F, -0.25F);
         this.field_217115_a.rotateAngleX = 0.0F;
         this.field_217115_a.rotateAngleY = 0.0F;
         if (this.isChild) {
            this.field_217115_a.setRotationPoint(-1.0F, 13.0F, -3.75F);
         }

         this.field_217120_i.rotateAngleX = -1.3089969F;
         this.field_217120_i.setRotationPoint(-5.0F, 21.5F, 6.75F);
         this.field_217121_j.rotateAngleX = -1.3089969F;
         this.field_217121_j.setRotationPoint(-1.0F, 21.5F, 6.75F);
         this.field_217122_k.rotateAngleX = -0.2617994F;
         this.field_217123_l.rotateAngleX = -0.2617994F;
      }

   }

   /**
    * Sets the models various rotation angles then renders the model.
    */
   public void render(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      this.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      if (this.isChild) {
         GlStateManager.pushMatrix();
         float f = 0.75F;
         GlStateManager.scalef(0.75F, 0.75F, 0.75F);
         GlStateManager.translatef(0.0F, 8.0F * scale, 3.35F * scale);
         this.field_217115_a.render(scale);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         float f1 = 0.5F;
         GlStateManager.scalef(0.5F, 0.5F, 0.5F);
         GlStateManager.translatef(0.0F, 24.0F * scale, 0.0F);
         this.field_217119_h.render(scale);
         this.field_217120_i.render(scale);
         this.field_217121_j.render(scale);
         this.field_217122_k.render(scale);
         this.field_217123_l.render(scale);
         GlStateManager.popMatrix();
      } else {
         GlStateManager.pushMatrix();
         this.field_217115_a.render(scale);
         this.field_217119_h.render(scale);
         this.field_217120_i.render(scale);
         this.field_217121_j.render(scale);
         this.field_217122_k.render(scale);
         this.field_217123_l.render(scale);
         GlStateManager.popMatrix();
      }

   }

   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
      super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
      if (!entityIn.isSleeping() && !entityIn.func_213472_dX() && !entityIn.isCrouching()) {
         this.field_217115_a.rotateAngleX = headPitch * ((float)Math.PI / 180F);
         this.field_217115_a.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
      }

      if (entityIn.isSleeping()) {
         this.field_217115_a.rotateAngleX = 0.0F;
         this.field_217115_a.rotateAngleY = -2.0943952F;
         this.field_217115_a.rotateAngleZ = MathHelper.cos(ageInTicks * 0.027F) / 22.0F;
      }

      if (entityIn.isCrouching()) {
         float f = MathHelper.cos(ageInTicks) * 0.01F;
         this.field_217119_h.rotateAngleY = f;
         this.field_217120_i.rotateAngleZ = f;
         this.field_217121_j.rotateAngleZ = f;
         this.field_217122_k.rotateAngleZ = f / 2.0F;
         this.field_217123_l.rotateAngleZ = f / 2.0F;
      }

      if (entityIn.func_213472_dX()) {
         float f1 = 0.1F;
         this.field_217125_n += 0.67F;
         this.field_217120_i.rotateAngleX = MathHelper.cos(this.field_217125_n * 0.4662F) * 0.1F;
         this.field_217121_j.rotateAngleX = MathHelper.cos(this.field_217125_n * 0.4662F + (float)Math.PI) * 0.1F;
         this.field_217122_k.rotateAngleX = MathHelper.cos(this.field_217125_n * 0.4662F + (float)Math.PI) * 0.1F;
         this.field_217123_l.rotateAngleX = MathHelper.cos(this.field_217125_n * 0.4662F) * 0.1F;
      }

   }
}