package net.minecraft.client.renderer.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.HandSide;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlayerModel<T extends LivingEntity> extends BipedModel<T> {
   public final RendererModel bipedLeftArmwear;
   public final RendererModel bipedRightArmwear;
   public final RendererModel bipedLeftLegwear;
   public final RendererModel bipedRightLegwear;
   public final RendererModel bipedBodyWear;
   private final RendererModel bipedCape;
   private final RendererModel bipedDeadmau5Head;
   private final boolean smallArms;

   public PlayerModel(float modelSize, boolean smallArmsIn) {
      super(modelSize, 0.0F, 64, 64);
      this.smallArms = smallArmsIn;
      this.bipedDeadmau5Head = new RendererModel(this, 24, 0);
      this.bipedDeadmau5Head.addBox(-3.0F, -6.0F, -1.0F, 6, 6, 1, modelSize);
      this.bipedCape = new RendererModel(this, 0, 0);
      this.bipedCape.setTextureSize(64, 32);
      this.bipedCape.addBox(-5.0F, 0.0F, -1.0F, 10, 16, 1, modelSize);
      if (smallArmsIn) {
         this.bipedLeftArm = new RendererModel(this, 32, 48);
         this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, modelSize);
         this.bipedLeftArm.setRotationPoint(5.0F, 2.5F, 0.0F);
         this.bipedRightArm = new RendererModel(this, 40, 16);
         this.bipedRightArm.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, modelSize);
         this.bipedRightArm.setRotationPoint(-5.0F, 2.5F, 0.0F);
         this.bipedLeftArmwear = new RendererModel(this, 48, 48);
         this.bipedLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, modelSize + 0.25F);
         this.bipedLeftArmwear.setRotationPoint(5.0F, 2.5F, 0.0F);
         this.bipedRightArmwear = new RendererModel(this, 40, 32);
         this.bipedRightArmwear.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, modelSize + 0.25F);
         this.bipedRightArmwear.setRotationPoint(-5.0F, 2.5F, 10.0F);
      } else {
         this.bipedLeftArm = new RendererModel(this, 32, 48);
         this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, modelSize);
         this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
         this.bipedLeftArmwear = new RendererModel(this, 48, 48);
         this.bipedLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, modelSize + 0.25F);
         this.bipedLeftArmwear.setRotationPoint(5.0F, 2.0F, 0.0F);
         this.bipedRightArmwear = new RendererModel(this, 40, 32);
         this.bipedRightArmwear.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, modelSize + 0.25F);
         this.bipedRightArmwear.setRotationPoint(-5.0F, 2.0F, 10.0F);
      }

      this.bipedLeftLeg = new RendererModel(this, 16, 48);
      this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize);
      this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
      this.bipedLeftLegwear = new RendererModel(this, 0, 48);
      this.bipedLeftLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize + 0.25F);
      this.bipedLeftLegwear.setRotationPoint(1.9F, 12.0F, 0.0F);
      this.bipedRightLegwear = new RendererModel(this, 0, 32);
      this.bipedRightLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize + 0.25F);
      this.bipedRightLegwear.setRotationPoint(-1.9F, 12.0F, 0.0F);
      this.bipedBodyWear = new RendererModel(this, 16, 32);
      this.bipedBodyWear.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, modelSize + 0.25F);
      this.bipedBodyWear.setRotationPoint(0.0F, 0.0F, 0.0F);
   }

   /**
    * Sets the models various rotation angles then renders the model.
    */
   public void render(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      GlStateManager.pushMatrix();
      if (this.isChild) {
         float f = 2.0F;
         GlStateManager.scalef(0.5F, 0.5F, 0.5F);
         GlStateManager.translatef(0.0F, 24.0F * scale, 0.0F);
         this.bipedLeftLegwear.render(scale);
         this.bipedRightLegwear.render(scale);
         this.bipedLeftArmwear.render(scale);
         this.bipedRightArmwear.render(scale);
         this.bipedBodyWear.render(scale);
      } else {
         if (entityIn.func_213287_bg()) {
            GlStateManager.translatef(0.0F, 0.2F, 0.0F);
         }

         this.bipedLeftLegwear.render(scale);
         this.bipedRightLegwear.render(scale);
         this.bipedLeftArmwear.render(scale);
         this.bipedRightArmwear.render(scale);
         this.bipedBodyWear.render(scale);
      }

      GlStateManager.popMatrix();
   }

   public void renderDeadmau5Head(float scale) {
      this.bipedDeadmau5Head.func_217177_a(this.field_78116_c);
      this.bipedDeadmau5Head.rotationPointX = 0.0F;
      this.bipedDeadmau5Head.rotationPointY = 0.0F;
      this.bipedDeadmau5Head.render(scale);
   }

   public void renderCape(float scale) {
      this.bipedCape.render(scale);
   }

   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
      super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
      this.bipedLeftLegwear.func_217177_a(this.bipedLeftLeg);
      this.bipedRightLegwear.func_217177_a(this.bipedRightLeg);
      this.bipedLeftArmwear.func_217177_a(this.bipedLeftArm);
      this.bipedRightArmwear.func_217177_a(this.bipedRightArm);
      this.bipedBodyWear.func_217177_a(this.field_78115_e);
      if (entityIn.func_213287_bg()) {
         this.bipedCape.rotationPointY = 2.0F;
      } else {
         this.bipedCape.rotationPointY = 0.0F;
      }

   }

   public void setVisible(boolean visible) {
      super.setVisible(visible);
      this.bipedLeftArmwear.showModel = visible;
      this.bipedRightArmwear.showModel = visible;
      this.bipedLeftLegwear.showModel = visible;
      this.bipedRightLegwear.showModel = visible;
      this.bipedBodyWear.showModel = visible;
      this.bipedCape.showModel = visible;
      this.bipedDeadmau5Head.showModel = visible;
   }

   public void postRenderArm(float scale, HandSide side) {
      RendererModel renderermodel = this.getArmForSide(side);
      if (this.smallArms) {
         float f = 0.5F * (float)(side == HandSide.RIGHT ? 1 : -1);
         renderermodel.rotationPointX += f;
         renderermodel.postRender(scale);
         renderermodel.rotationPointX -= f;
      } else {
         renderermodel.postRender(scale);
      }

   }
}