package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.monster.AbstractIllagerEntity;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IllagerModel<T extends AbstractIllagerEntity> extends EntityModel<T> implements IHasArm, IHasHead {
   protected final RendererModel head;
   private final RendererModel hat;
   protected final RendererModel body;
   protected final RendererModel arms;
   protected final RendererModel field_217143_g;
   protected final RendererModel field_217144_h;
   private final RendererModel nose;
   protected final RendererModel rightArm;
   protected final RendererModel leftArm;
   private float field_217145_m;

   public IllagerModel(float scaleFactor, float p_i47227_2_, int textureWidthIn, int textureHeightIn) {
      this.head = (new RendererModel(this)).setTextureSize(textureWidthIn, textureHeightIn);
      this.head.setRotationPoint(0.0F, 0.0F + p_i47227_2_, 0.0F);
      this.head.setTextureOffset(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8, 10, 8, scaleFactor);
      this.hat = (new RendererModel(this, 32, 0)).setTextureSize(textureWidthIn, textureHeightIn);
      this.hat.addBox(-4.0F, -10.0F, -4.0F, 8, 12, 8, scaleFactor + 0.45F);
      this.head.addChild(this.hat);
      this.hat.showModel = false;
      this.nose = (new RendererModel(this)).setTextureSize(textureWidthIn, textureHeightIn);
      this.nose.setRotationPoint(0.0F, p_i47227_2_ - 2.0F, 0.0F);
      this.nose.setTextureOffset(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2, 4, 2, scaleFactor);
      this.head.addChild(this.nose);
      this.body = (new RendererModel(this)).setTextureSize(textureWidthIn, textureHeightIn);
      this.body.setRotationPoint(0.0F, 0.0F + p_i47227_2_, 0.0F);
      this.body.setTextureOffset(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8, 12, 6, scaleFactor);
      this.body.setTextureOffset(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8, 18, 6, scaleFactor + 0.5F);
      this.arms = (new RendererModel(this)).setTextureSize(textureWidthIn, textureHeightIn);
      this.arms.setRotationPoint(0.0F, 0.0F + p_i47227_2_ + 2.0F, 0.0F);
      this.arms.setTextureOffset(44, 22).addBox(-8.0F, -2.0F, -2.0F, 4, 8, 4, scaleFactor);
      RendererModel renderermodel = (new RendererModel(this, 44, 22)).setTextureSize(textureWidthIn, textureHeightIn);
      renderermodel.mirror = true;
      renderermodel.addBox(4.0F, -2.0F, -2.0F, 4, 8, 4, scaleFactor);
      this.arms.addChild(renderermodel);
      this.arms.setTextureOffset(40, 38).addBox(-4.0F, 2.0F, -2.0F, 8, 4, 4, scaleFactor);
      this.field_217143_g = (new RendererModel(this, 0, 22)).setTextureSize(textureWidthIn, textureHeightIn);
      this.field_217143_g.setRotationPoint(-2.0F, 12.0F + p_i47227_2_, 0.0F);
      this.field_217143_g.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scaleFactor);
      this.field_217144_h = (new RendererModel(this, 0, 22)).setTextureSize(textureWidthIn, textureHeightIn);
      this.field_217144_h.mirror = true;
      this.field_217144_h.setRotationPoint(2.0F, 12.0F + p_i47227_2_, 0.0F);
      this.field_217144_h.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, scaleFactor);
      this.rightArm = (new RendererModel(this, 40, 46)).setTextureSize(textureWidthIn, textureHeightIn);
      this.rightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, scaleFactor);
      this.rightArm.setRotationPoint(-5.0F, 2.0F + p_i47227_2_, 0.0F);
      this.leftArm = (new RendererModel(this, 40, 46)).setTextureSize(textureWidthIn, textureHeightIn);
      this.leftArm.mirror = true;
      this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, scaleFactor);
      this.leftArm.setRotationPoint(5.0F, 2.0F + p_i47227_2_, 0.0F);
   }

   /**
    * Sets the models various rotation angles then renders the model.
    */
   public void render(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      this.head.render(scale);
      this.body.render(scale);
      this.field_217143_g.render(scale);
      this.field_217144_h.render(scale);
      if (entityIn.getArmPose() == AbstractIllagerEntity.ArmPose.CROSSED) {
         this.arms.render(scale);
      } else {
         this.rightArm.render(scale);
         this.leftArm.render(scale);
      }

   }

   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
      this.head.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
      this.head.rotateAngleX = headPitch * ((float)Math.PI / 180F);
      this.arms.rotationPointY = 3.0F;
      this.arms.rotationPointZ = -1.0F;
      this.arms.rotateAngleX = -0.75F;
      if (this.isSitting) {
         this.rightArm.rotateAngleX = (-(float)Math.PI / 5F);
         this.rightArm.rotateAngleY = 0.0F;
         this.rightArm.rotateAngleZ = 0.0F;
         this.leftArm.rotateAngleX = (-(float)Math.PI / 5F);
         this.leftArm.rotateAngleY = 0.0F;
         this.leftArm.rotateAngleZ = 0.0F;
         this.field_217143_g.rotateAngleX = -1.4137167F;
         this.field_217143_g.rotateAngleY = ((float)Math.PI / 10F);
         this.field_217143_g.rotateAngleZ = 0.07853982F;
         this.field_217144_h.rotateAngleX = -1.4137167F;
         this.field_217144_h.rotateAngleY = (-(float)Math.PI / 10F);
         this.field_217144_h.rotateAngleZ = -0.07853982F;
      } else {
         this.rightArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F;
         this.rightArm.rotateAngleY = 0.0F;
         this.rightArm.rotateAngleZ = 0.0F;
         this.leftArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
         this.leftArm.rotateAngleY = 0.0F;
         this.leftArm.rotateAngleZ = 0.0F;
         this.field_217143_g.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
         this.field_217143_g.rotateAngleY = 0.0F;
         this.field_217143_g.rotateAngleZ = 0.0F;
         this.field_217144_h.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount * 0.5F;
         this.field_217144_h.rotateAngleY = 0.0F;
         this.field_217144_h.rotateAngleZ = 0.0F;
      }

      AbstractIllagerEntity.ArmPose abstractillagerentity$armpose = entityIn.getArmPose();
      if (abstractillagerentity$armpose == AbstractIllagerEntity.ArmPose.ATTACKING) {
         float f = MathHelper.sin(this.swingProgress * (float)Math.PI);
         float f1 = MathHelper.sin((1.0F - (1.0F - this.swingProgress) * (1.0F - this.swingProgress)) * (float)Math.PI);
         this.rightArm.rotateAngleZ = 0.0F;
         this.leftArm.rotateAngleZ = 0.0F;
         this.rightArm.rotateAngleY = 0.15707964F;
         this.leftArm.rotateAngleY = -0.15707964F;
         if (entityIn.getPrimaryHand() == HandSide.RIGHT) {
            this.rightArm.rotateAngleX = -1.8849558F + MathHelper.cos(ageInTicks * 0.09F) * 0.15F;
            this.leftArm.rotateAngleX = -0.0F + MathHelper.cos(ageInTicks * 0.19F) * 0.5F;
            this.rightArm.rotateAngleX += f * 2.2F - f1 * 0.4F;
            this.leftArm.rotateAngleX += f * 1.2F - f1 * 0.4F;
         } else {
            this.rightArm.rotateAngleX = -0.0F + MathHelper.cos(ageInTicks * 0.19F) * 0.5F;
            this.leftArm.rotateAngleX = -1.8849558F + MathHelper.cos(ageInTicks * 0.09F) * 0.15F;
            this.rightArm.rotateAngleX += f * 1.2F - f1 * 0.4F;
            this.leftArm.rotateAngleX += f * 2.2F - f1 * 0.4F;
         }

         this.rightArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
         this.leftArm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
         this.rightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
         this.leftArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
      } else if (abstractillagerentity$armpose == AbstractIllagerEntity.ArmPose.SPELLCASTING) {
         this.rightArm.rotationPointZ = 0.0F;
         this.rightArm.rotationPointX = -5.0F;
         this.leftArm.rotationPointZ = 0.0F;
         this.leftArm.rotationPointX = 5.0F;
         this.rightArm.rotateAngleX = MathHelper.cos(ageInTicks * 0.6662F) * 0.25F;
         this.leftArm.rotateAngleX = MathHelper.cos(ageInTicks * 0.6662F) * 0.25F;
         this.rightArm.rotateAngleZ = 2.3561945F;
         this.leftArm.rotateAngleZ = -2.3561945F;
         this.rightArm.rotateAngleY = 0.0F;
         this.leftArm.rotateAngleY = 0.0F;
      } else if (abstractillagerentity$armpose == AbstractIllagerEntity.ArmPose.BOW_AND_ARROW) {
         this.rightArm.rotateAngleY = -0.1F + this.head.rotateAngleY;
         this.rightArm.rotateAngleX = (-(float)Math.PI / 2F) + this.head.rotateAngleX;
         this.leftArm.rotateAngleX = -0.9424779F + this.head.rotateAngleX;
         this.leftArm.rotateAngleY = this.head.rotateAngleY - 0.4F;
         this.leftArm.rotateAngleZ = ((float)Math.PI / 2F);
      } else if (abstractillagerentity$armpose == AbstractIllagerEntity.ArmPose.CROSSBOW_HOLD) {
         this.rightArm.rotateAngleY = -0.3F + this.head.rotateAngleY;
         this.leftArm.rotateAngleY = 0.6F + this.head.rotateAngleY;
         this.rightArm.rotateAngleX = (-(float)Math.PI / 2F) + this.head.rotateAngleX + 0.1F;
         this.leftArm.rotateAngleX = -1.5F + this.head.rotateAngleX;
      } else if (abstractillagerentity$armpose == AbstractIllagerEntity.ArmPose.CROSSBOW_CHARGE) {
         this.rightArm.rotateAngleY = -0.8F;
         this.rightArm.rotateAngleX = -0.97079635F;
         this.leftArm.rotateAngleX = -0.97079635F;
         float f2 = MathHelper.clamp(this.field_217145_m, 0.0F, 25.0F);
         this.leftArm.rotateAngleY = MathHelper.lerp(f2 / 25.0F, 0.4F, 0.85F);
         this.leftArm.rotateAngleX = MathHelper.lerp(f2 / 25.0F, this.leftArm.rotateAngleX, (-(float)Math.PI / 2F));
      } else if (abstractillagerentity$armpose == AbstractIllagerEntity.ArmPose.CELEBRATING) {
         this.rightArm.rotationPointZ = 0.0F;
         this.rightArm.rotationPointX = -5.0F;
         this.rightArm.rotateAngleX = MathHelper.cos(ageInTicks * 0.6662F) * 0.05F;
         this.rightArm.rotateAngleZ = 2.670354F;
         this.rightArm.rotateAngleY = 0.0F;
         this.leftArm.rotationPointZ = 0.0F;
         this.leftArm.rotationPointX = 5.0F;
         this.leftArm.rotateAngleX = MathHelper.cos(ageInTicks * 0.6662F) * 0.05F;
         this.leftArm.rotateAngleZ = -2.3561945F;
         this.leftArm.rotateAngleY = 0.0F;
      }

   }

   public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
      this.field_217145_m = (float)entityIn.getItemInUseMaxCount();
      super.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);
   }

   private RendererModel getArm(HandSide p_191216_1_) {
      return p_191216_1_ == HandSide.LEFT ? this.leftArm : this.rightArm;
   }

   public RendererModel func_205062_a() {
      return this.hat;
   }

   public RendererModel func_205072_a() {
      return this.head;
   }

   public void postRenderArm(float scale, HandSide side) {
      this.getArm(side).postRender(0.0625F);
   }
}