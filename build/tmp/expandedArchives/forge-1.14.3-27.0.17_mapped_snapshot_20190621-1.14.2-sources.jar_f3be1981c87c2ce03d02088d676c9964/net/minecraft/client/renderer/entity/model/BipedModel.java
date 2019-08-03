package net.minecraft.client.renderer.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BipedModel<T extends LivingEntity> extends EntityModel<T> implements IHasArm, IHasHead {
   public RendererModel field_78116_c;
   public RendererModel bipedHeadwear;
   public RendererModel field_78115_e;
   public RendererModel bipedRightArm;
   public RendererModel bipedLeftArm;
   public RendererModel bipedRightLeg;
   public RendererModel bipedLeftLeg;
   public BipedModel.ArmPose leftArmPose = BipedModel.ArmPose.EMPTY;
   public BipedModel.ArmPose rightArmPose = BipedModel.ArmPose.EMPTY;
   public boolean isSneak;
   public float field_205061_a;
   private float field_217149_a;

   public BipedModel() {
      this(0.0F);
   }

   public BipedModel(float modelSize) {
      this(modelSize, 0.0F, 64, 32);
   }

   public BipedModel(float modelSize, float p_i1149_2_, int textureWidthIn, int textureHeightIn) {
      this.textureWidth = textureWidthIn;
      this.textureHeight = textureHeightIn;
      this.field_78116_c = new RendererModel(this, 0, 0);
      this.field_78116_c.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, modelSize);
      this.field_78116_c.setRotationPoint(0.0F, 0.0F + p_i1149_2_, 0.0F);
      this.bipedHeadwear = new RendererModel(this, 32, 0);
      this.bipedHeadwear.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, modelSize + 0.5F);
      this.bipedHeadwear.setRotationPoint(0.0F, 0.0F + p_i1149_2_, 0.0F);
      this.field_78115_e = new RendererModel(this, 16, 16);
      this.field_78115_e.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, modelSize);
      this.field_78115_e.setRotationPoint(0.0F, 0.0F + p_i1149_2_, 0.0F);
      this.bipedRightArm = new RendererModel(this, 40, 16);
      this.bipedRightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, modelSize);
      this.bipedRightArm.setRotationPoint(-5.0F, 2.0F + p_i1149_2_, 0.0F);
      this.bipedLeftArm = new RendererModel(this, 40, 16);
      this.bipedLeftArm.mirror = true;
      this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, modelSize);
      this.bipedLeftArm.setRotationPoint(5.0F, 2.0F + p_i1149_2_, 0.0F);
      this.bipedRightLeg = new RendererModel(this, 0, 16);
      this.bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize);
      this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F + p_i1149_2_, 0.0F);
      this.bipedLeftLeg = new RendererModel(this, 0, 16);
      this.bipedLeftLeg.mirror = true;
      this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, modelSize);
      this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F + p_i1149_2_, 0.0F);
   }

   /**
    * Sets the models various rotation angles then renders the model.
    */
   public void render(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      GlStateManager.pushMatrix();
      if (this.isChild) {
         float f = 2.0F;
         GlStateManager.scalef(0.75F, 0.75F, 0.75F);
         GlStateManager.translatef(0.0F, 16.0F * scale, 0.0F);
         this.field_78116_c.render(scale);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.scalef(0.5F, 0.5F, 0.5F);
         GlStateManager.translatef(0.0F, 24.0F * scale, 0.0F);
         this.field_78115_e.render(scale);
         this.bipedRightArm.render(scale);
         this.bipedLeftArm.render(scale);
         this.bipedRightLeg.render(scale);
         this.bipedLeftLeg.render(scale);
         this.bipedHeadwear.render(scale);
      } else {
         if (entityIn.func_213287_bg()) {
            GlStateManager.translatef(0.0F, 0.2F, 0.0F);
         }

         this.field_78116_c.render(scale);
         this.field_78115_e.render(scale);
         this.bipedRightArm.render(scale);
         this.bipedLeftArm.render(scale);
         this.bipedRightLeg.render(scale);
         this.bipedLeftLeg.render(scale);
         this.bipedHeadwear.render(scale);
      }

      GlStateManager.popMatrix();
   }

   public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
      this.field_205061_a = entityIn.getSwimAnimation(partialTick);
      this.field_217149_a = (float)entityIn.getItemInUseMaxCount();
      super.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);
   }

   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
      boolean flag = entityIn.getTicksElytraFlying() > 4;
      boolean flag1 = entityIn.func_213314_bj();
      this.field_78116_c.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
      if (flag) {
         this.field_78116_c.rotateAngleX = (-(float)Math.PI / 4F);
      } else if (this.field_205061_a > 0.0F) {
         if (flag1) {
            this.field_78116_c.rotateAngleX = this.func_205060_a(this.field_78116_c.rotateAngleX, (-(float)Math.PI / 4F), this.field_205061_a);
         } else {
            this.field_78116_c.rotateAngleX = this.func_205060_a(this.field_78116_c.rotateAngleX, headPitch * ((float)Math.PI / 180F), this.field_205061_a);
         }
      } else {
         this.field_78116_c.rotateAngleX = headPitch * ((float)Math.PI / 180F);
      }

      this.field_78115_e.rotateAngleY = 0.0F;
      this.bipedRightArm.rotationPointZ = 0.0F;
      this.bipedRightArm.rotationPointX = -5.0F;
      this.bipedLeftArm.rotationPointZ = 0.0F;
      this.bipedLeftArm.rotationPointX = 5.0F;
      float f = 1.0F;
      if (flag) {
         f = (float)entityIn.getMotion().lengthSquared();
         f = f / 0.2F;
         f = f * f * f;
      }

      if (f < 1.0F) {
         f = 1.0F;
      }

      this.bipedRightArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F / f;
      this.bipedLeftArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F / f;
      this.bipedRightArm.rotateAngleZ = 0.0F;
      this.bipedLeftArm.rotateAngleZ = 0.0F;
      this.bipedRightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount / f;
      this.bipedLeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount / f;
      this.bipedRightLeg.rotateAngleY = 0.0F;
      this.bipedLeftLeg.rotateAngleY = 0.0F;
      this.bipedRightLeg.rotateAngleZ = 0.0F;
      this.bipedLeftLeg.rotateAngleZ = 0.0F;
      if (this.isSitting) {
         this.bipedRightArm.rotateAngleX += (-(float)Math.PI / 5F);
         this.bipedLeftArm.rotateAngleX += (-(float)Math.PI / 5F);
         this.bipedRightLeg.rotateAngleX = -1.4137167F;
         this.bipedRightLeg.rotateAngleY = ((float)Math.PI / 10F);
         this.bipedRightLeg.rotateAngleZ = 0.07853982F;
         this.bipedLeftLeg.rotateAngleX = -1.4137167F;
         this.bipedLeftLeg.rotateAngleY = (-(float)Math.PI / 10F);
         this.bipedLeftLeg.rotateAngleZ = -0.07853982F;
      }

      this.bipedRightArm.rotateAngleY = 0.0F;
      this.bipedRightArm.rotateAngleZ = 0.0F;
      switch(this.leftArmPose) {
      case EMPTY:
         this.bipedLeftArm.rotateAngleY = 0.0F;
         break;
      case BLOCK:
         this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5F - 0.9424779F;
         this.bipedLeftArm.rotateAngleY = ((float)Math.PI / 6F);
         break;
      case ITEM:
         this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5F - ((float)Math.PI / 10F);
         this.bipedLeftArm.rotateAngleY = 0.0F;
      }

      switch(this.rightArmPose) {
      case EMPTY:
         this.bipedRightArm.rotateAngleY = 0.0F;
         break;
      case BLOCK:
         this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F - 0.9424779F;
         this.bipedRightArm.rotateAngleY = (-(float)Math.PI / 6F);
         break;
      case ITEM:
         this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F - ((float)Math.PI / 10F);
         this.bipedRightArm.rotateAngleY = 0.0F;
         break;
      case THROW_SPEAR:
         this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F - (float)Math.PI;
         this.bipedRightArm.rotateAngleY = 0.0F;
      }

      if (this.leftArmPose == BipedModel.ArmPose.THROW_SPEAR && this.rightArmPose != BipedModel.ArmPose.BLOCK && this.rightArmPose != BipedModel.ArmPose.THROW_SPEAR && this.rightArmPose != BipedModel.ArmPose.BOW_AND_ARROW) {
         this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5F - (float)Math.PI;
         this.bipedLeftArm.rotateAngleY = 0.0F;
      }

      if (this.swingProgress > 0.0F) {
         HandSide handside = this.func_217147_a(entityIn);
         RendererModel renderermodel = this.getArmForSide(handside);
         float f1 = this.swingProgress;
         this.field_78115_e.rotateAngleY = MathHelper.sin(MathHelper.sqrt(f1) * ((float)Math.PI * 2F)) * 0.2F;
         if (handside == HandSide.LEFT) {
            this.field_78115_e.rotateAngleY *= -1.0F;
         }

         this.bipedRightArm.rotationPointZ = MathHelper.sin(this.field_78115_e.rotateAngleY) * 5.0F;
         this.bipedRightArm.rotationPointX = -MathHelper.cos(this.field_78115_e.rotateAngleY) * 5.0F;
         this.bipedLeftArm.rotationPointZ = -MathHelper.sin(this.field_78115_e.rotateAngleY) * 5.0F;
         this.bipedLeftArm.rotationPointX = MathHelper.cos(this.field_78115_e.rotateAngleY) * 5.0F;
         this.bipedRightArm.rotateAngleY += this.field_78115_e.rotateAngleY;
         this.bipedLeftArm.rotateAngleY += this.field_78115_e.rotateAngleY;
         this.bipedLeftArm.rotateAngleX += this.field_78115_e.rotateAngleY;
         f1 = 1.0F - this.swingProgress;
         f1 = f1 * f1;
         f1 = f1 * f1;
         f1 = 1.0F - f1;
         float f2 = MathHelper.sin(f1 * (float)Math.PI);
         float f3 = MathHelper.sin(this.swingProgress * (float)Math.PI) * -(this.field_78116_c.rotateAngleX - 0.7F) * 0.75F;
         renderermodel.rotateAngleX = (float)((double)renderermodel.rotateAngleX - ((double)f2 * 1.2D + (double)f3));
         renderermodel.rotateAngleY += this.field_78115_e.rotateAngleY * 2.0F;
         renderermodel.rotateAngleZ += MathHelper.sin(this.swingProgress * (float)Math.PI) * -0.4F;
      }

      if (this.isSneak) {
         this.field_78115_e.rotateAngleX = 0.5F;
         this.bipedRightArm.rotateAngleX += 0.4F;
         this.bipedLeftArm.rotateAngleX += 0.4F;
         this.bipedRightLeg.rotationPointZ = 4.0F;
         this.bipedLeftLeg.rotationPointZ = 4.0F;
         this.bipedRightLeg.rotationPointY = 9.0F;
         this.bipedLeftLeg.rotationPointY = 9.0F;
         this.field_78116_c.rotationPointY = 1.0F;
      } else {
         this.field_78115_e.rotateAngleX = 0.0F;
         this.bipedRightLeg.rotationPointZ = 0.1F;
         this.bipedLeftLeg.rotationPointZ = 0.1F;
         this.bipedRightLeg.rotationPointY = 12.0F;
         this.bipedLeftLeg.rotationPointY = 12.0F;
         this.field_78116_c.rotationPointY = 0.0F;
      }

      this.bipedRightArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
      this.bipedLeftArm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
      this.bipedRightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
      this.bipedLeftArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
      if (this.rightArmPose == BipedModel.ArmPose.BOW_AND_ARROW) {
         this.bipedRightArm.rotateAngleY = -0.1F + this.field_78116_c.rotateAngleY;
         this.bipedLeftArm.rotateAngleY = 0.1F + this.field_78116_c.rotateAngleY + 0.4F;
         this.bipedRightArm.rotateAngleX = (-(float)Math.PI / 2F) + this.field_78116_c.rotateAngleX;
         this.bipedLeftArm.rotateAngleX = (-(float)Math.PI / 2F) + this.field_78116_c.rotateAngleX;
      } else if (this.leftArmPose == BipedModel.ArmPose.BOW_AND_ARROW && this.rightArmPose != BipedModel.ArmPose.THROW_SPEAR && this.rightArmPose != BipedModel.ArmPose.BLOCK) {
         this.bipedRightArm.rotateAngleY = -0.1F + this.field_78116_c.rotateAngleY - 0.4F;
         this.bipedLeftArm.rotateAngleY = 0.1F + this.field_78116_c.rotateAngleY;
         this.bipedRightArm.rotateAngleX = (-(float)Math.PI / 2F) + this.field_78116_c.rotateAngleX;
         this.bipedLeftArm.rotateAngleX = (-(float)Math.PI / 2F) + this.field_78116_c.rotateAngleX;
      }

      float f4 = (float)CrossbowItem.func_220026_e(entityIn.getActiveItemStack());
      if (this.rightArmPose == BipedModel.ArmPose.CROSSBOW_CHARGE) {
         this.bipedRightArm.rotateAngleY = -0.8F;
         this.bipedRightArm.rotateAngleX = -0.97079635F;
         this.bipedLeftArm.rotateAngleX = -0.97079635F;
         float f5 = MathHelper.clamp(this.field_217149_a, 0.0F, f4);
         this.bipedLeftArm.rotateAngleY = MathHelper.lerp(f5 / f4, 0.4F, 0.85F);
         this.bipedLeftArm.rotateAngleX = MathHelper.lerp(f5 / f4, this.bipedLeftArm.rotateAngleX, (-(float)Math.PI / 2F));
      } else if (this.leftArmPose == BipedModel.ArmPose.CROSSBOW_CHARGE) {
         this.bipedLeftArm.rotateAngleY = 0.8F;
         this.bipedRightArm.rotateAngleX = -0.97079635F;
         this.bipedLeftArm.rotateAngleX = -0.97079635F;
         float f6 = MathHelper.clamp(this.field_217149_a, 0.0F, f4);
         this.bipedRightArm.rotateAngleY = MathHelper.lerp(f6 / f4, -0.4F, -0.85F);
         this.bipedRightArm.rotateAngleX = MathHelper.lerp(f6 / f4, this.bipedRightArm.rotateAngleX, (-(float)Math.PI / 2F));
      }

      if (this.rightArmPose == BipedModel.ArmPose.CROSSBOW_HOLD && this.swingProgress <= 0.0F) {
         this.bipedRightArm.rotateAngleY = -0.3F + this.field_78116_c.rotateAngleY;
         this.bipedLeftArm.rotateAngleY = 0.6F + this.field_78116_c.rotateAngleY;
         this.bipedRightArm.rotateAngleX = (-(float)Math.PI / 2F) + this.field_78116_c.rotateAngleX + 0.1F;
         this.bipedLeftArm.rotateAngleX = -1.5F + this.field_78116_c.rotateAngleX;
      } else if (this.leftArmPose == BipedModel.ArmPose.CROSSBOW_HOLD) {
         this.bipedRightArm.rotateAngleY = -0.6F + this.field_78116_c.rotateAngleY;
         this.bipedLeftArm.rotateAngleY = 0.3F + this.field_78116_c.rotateAngleY;
         this.bipedRightArm.rotateAngleX = -1.5F + this.field_78116_c.rotateAngleX;
         this.bipedLeftArm.rotateAngleX = (-(float)Math.PI / 2F) + this.field_78116_c.rotateAngleX + 0.1F;
      }

      if (this.field_205061_a > 0.0F) {
         float f7 = limbSwing % 26.0F;
         float f8 = this.swingProgress > 0.0F ? 0.0F : this.field_205061_a;
         if (f7 < 14.0F) {
            this.bipedLeftArm.rotateAngleX = this.func_205060_a(this.bipedLeftArm.rotateAngleX, 0.0F, this.field_205061_a);
            this.bipedRightArm.rotateAngleX = MathHelper.lerp(f8, this.bipedRightArm.rotateAngleX, 0.0F);
            this.bipedLeftArm.rotateAngleY = this.func_205060_a(this.bipedLeftArm.rotateAngleY, (float)Math.PI, this.field_205061_a);
            this.bipedRightArm.rotateAngleY = MathHelper.lerp(f8, this.bipedRightArm.rotateAngleY, (float)Math.PI);
            this.bipedLeftArm.rotateAngleZ = this.func_205060_a(this.bipedLeftArm.rotateAngleZ, (float)Math.PI + 1.8707964F * this.func_203068_a(f7) / this.func_203068_a(14.0F), this.field_205061_a);
            this.bipedRightArm.rotateAngleZ = MathHelper.lerp(f8, this.bipedRightArm.rotateAngleZ, (float)Math.PI - 1.8707964F * this.func_203068_a(f7) / this.func_203068_a(14.0F));
         } else if (f7 >= 14.0F && f7 < 22.0F) {
            float f10 = (f7 - 14.0F) / 8.0F;
            this.bipedLeftArm.rotateAngleX = this.func_205060_a(this.bipedLeftArm.rotateAngleX, ((float)Math.PI / 2F) * f10, this.field_205061_a);
            this.bipedRightArm.rotateAngleX = MathHelper.lerp(f8, this.bipedRightArm.rotateAngleX, ((float)Math.PI / 2F) * f10);
            this.bipedLeftArm.rotateAngleY = this.func_205060_a(this.bipedLeftArm.rotateAngleY, (float)Math.PI, this.field_205061_a);
            this.bipedRightArm.rotateAngleY = MathHelper.lerp(f8, this.bipedRightArm.rotateAngleY, (float)Math.PI);
            this.bipedLeftArm.rotateAngleZ = this.func_205060_a(this.bipedLeftArm.rotateAngleZ, 5.012389F - 1.8707964F * f10, this.field_205061_a);
            this.bipedRightArm.rotateAngleZ = MathHelper.lerp(f8, this.bipedRightArm.rotateAngleZ, 1.2707963F + 1.8707964F * f10);
         } else if (f7 >= 22.0F && f7 < 26.0F) {
            float f9 = (f7 - 22.0F) / 4.0F;
            this.bipedLeftArm.rotateAngleX = this.func_205060_a(this.bipedLeftArm.rotateAngleX, ((float)Math.PI / 2F) - ((float)Math.PI / 2F) * f9, this.field_205061_a);
            this.bipedRightArm.rotateAngleX = MathHelper.lerp(f8, this.bipedRightArm.rotateAngleX, ((float)Math.PI / 2F) - ((float)Math.PI / 2F) * f9);
            this.bipedLeftArm.rotateAngleY = this.func_205060_a(this.bipedLeftArm.rotateAngleY, (float)Math.PI, this.field_205061_a);
            this.bipedRightArm.rotateAngleY = MathHelper.lerp(f8, this.bipedRightArm.rotateAngleY, (float)Math.PI);
            this.bipedLeftArm.rotateAngleZ = this.func_205060_a(this.bipedLeftArm.rotateAngleZ, (float)Math.PI, this.field_205061_a);
            this.bipedRightArm.rotateAngleZ = MathHelper.lerp(f8, this.bipedRightArm.rotateAngleZ, (float)Math.PI);
         }

         float f11 = 0.3F;
         float f12 = 0.33333334F;
         this.bipedLeftLeg.rotateAngleX = MathHelper.lerp(this.field_205061_a, this.bipedLeftLeg.rotateAngleX, 0.3F * MathHelper.cos(limbSwing * 0.33333334F + (float)Math.PI));
         this.bipedRightLeg.rotateAngleX = MathHelper.lerp(this.field_205061_a, this.bipedRightLeg.rotateAngleX, 0.3F * MathHelper.cos(limbSwing * 0.33333334F));
      }

      this.bipedHeadwear.func_217177_a(this.field_78116_c);
   }

   protected float func_205060_a(float p_205060_1_, float p_205060_2_, float p_205060_3_) {
      float f = (p_205060_2_ - p_205060_1_) % ((float)Math.PI * 2F);
      if (f < -(float)Math.PI) {
         f += ((float)Math.PI * 2F);
      }

      if (f >= (float)Math.PI) {
         f -= ((float)Math.PI * 2F);
      }

      return p_205060_1_ + p_205060_3_ * f;
   }

   private float func_203068_a(float p_203068_1_) {
      return -65.0F * p_203068_1_ + p_203068_1_ * p_203068_1_;
   }

   public void func_217148_a(BipedModel<T> p_217148_1_) {
      super.setModelAttributes(p_217148_1_);
      p_217148_1_.leftArmPose = this.leftArmPose;
      p_217148_1_.rightArmPose = this.rightArmPose;
      p_217148_1_.isSneak = this.isSneak;
   }

   public void setVisible(boolean visible) {
      this.field_78116_c.showModel = visible;
      this.bipedHeadwear.showModel = visible;
      this.field_78115_e.showModel = visible;
      this.bipedRightArm.showModel = visible;
      this.bipedLeftArm.showModel = visible;
      this.bipedRightLeg.showModel = visible;
      this.bipedLeftLeg.showModel = visible;
   }

   public void postRenderArm(float scale, HandSide side) {
      this.getArmForSide(side).postRender(scale);
   }

   protected RendererModel getArmForSide(HandSide side) {
      return side == HandSide.LEFT ? this.bipedLeftArm : this.bipedRightArm;
   }

   public RendererModel func_205072_a() {
      return this.field_78116_c;
   }

   protected HandSide func_217147_a(T p_217147_1_) {
      HandSide handside = p_217147_1_.getPrimaryHand();
      return p_217147_1_.swingingHand == Hand.MAIN_HAND ? handside : handside.opposite();
   }

   @OnlyIn(Dist.CLIENT)
   public static enum ArmPose {
      EMPTY,
      ITEM,
      BLOCK,
      BOW_AND_ARROW,
      THROW_SPEAR,
      CROSSBOW_CHARGE,
      CROSSBOW_HOLD;
   }
}