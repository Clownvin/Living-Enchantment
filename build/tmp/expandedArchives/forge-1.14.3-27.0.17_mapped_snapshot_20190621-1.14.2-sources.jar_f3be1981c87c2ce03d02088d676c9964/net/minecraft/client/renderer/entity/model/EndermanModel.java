package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EndermanModel<T extends LivingEntity> extends BipedModel<T> {
   public boolean isCarrying;
   public boolean isAttacking;

   public EndermanModel(float scale) {
      super(0.0F, -14.0F, 64, 32);
      float f = -14.0F;
      this.bipedHeadwear = new RendererModel(this, 0, 16);
      this.bipedHeadwear.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, scale - 0.5F);
      this.bipedHeadwear.setRotationPoint(0.0F, -14.0F, 0.0F);
      this.field_78115_e = new RendererModel(this, 32, 16);
      this.field_78115_e.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, scale);
      this.field_78115_e.setRotationPoint(0.0F, -14.0F, 0.0F);
      this.bipedRightArm = new RendererModel(this, 56, 0);
      this.bipedRightArm.addBox(-1.0F, -2.0F, -1.0F, 2, 30, 2, scale);
      this.bipedRightArm.setRotationPoint(-3.0F, -12.0F, 0.0F);
      this.bipedLeftArm = new RendererModel(this, 56, 0);
      this.bipedLeftArm.mirror = true;
      this.bipedLeftArm.addBox(-1.0F, -2.0F, -1.0F, 2, 30, 2, scale);
      this.bipedLeftArm.setRotationPoint(5.0F, -12.0F, 0.0F);
      this.bipedRightLeg = new RendererModel(this, 56, 0);
      this.bipedRightLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 30, 2, scale);
      this.bipedRightLeg.setRotationPoint(-2.0F, -2.0F, 0.0F);
      this.bipedLeftLeg = new RendererModel(this, 56, 0);
      this.bipedLeftLeg.mirror = true;
      this.bipedLeftLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 30, 2, scale);
      this.bipedLeftLeg.setRotationPoint(2.0F, -2.0F, 0.0F);
   }

   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
      super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
      this.field_78116_c.showModel = true;
      float f = -14.0F;
      this.field_78115_e.rotateAngleX = 0.0F;
      this.field_78115_e.rotationPointY = -14.0F;
      this.field_78115_e.rotationPointZ = -0.0F;
      this.bipedRightLeg.rotateAngleX -= 0.0F;
      this.bipedLeftLeg.rotateAngleX -= 0.0F;
      this.bipedRightArm.rotateAngleX = (float)((double)this.bipedRightArm.rotateAngleX * 0.5D);
      this.bipedLeftArm.rotateAngleX = (float)((double)this.bipedLeftArm.rotateAngleX * 0.5D);
      this.bipedRightLeg.rotateAngleX = (float)((double)this.bipedRightLeg.rotateAngleX * 0.5D);
      this.bipedLeftLeg.rotateAngleX = (float)((double)this.bipedLeftLeg.rotateAngleX * 0.5D);
      float f1 = 0.4F;
      if (this.bipedRightArm.rotateAngleX > 0.4F) {
         this.bipedRightArm.rotateAngleX = 0.4F;
      }

      if (this.bipedLeftArm.rotateAngleX > 0.4F) {
         this.bipedLeftArm.rotateAngleX = 0.4F;
      }

      if (this.bipedRightArm.rotateAngleX < -0.4F) {
         this.bipedRightArm.rotateAngleX = -0.4F;
      }

      if (this.bipedLeftArm.rotateAngleX < -0.4F) {
         this.bipedLeftArm.rotateAngleX = -0.4F;
      }

      if (this.bipedRightLeg.rotateAngleX > 0.4F) {
         this.bipedRightLeg.rotateAngleX = 0.4F;
      }

      if (this.bipedLeftLeg.rotateAngleX > 0.4F) {
         this.bipedLeftLeg.rotateAngleX = 0.4F;
      }

      if (this.bipedRightLeg.rotateAngleX < -0.4F) {
         this.bipedRightLeg.rotateAngleX = -0.4F;
      }

      if (this.bipedLeftLeg.rotateAngleX < -0.4F) {
         this.bipedLeftLeg.rotateAngleX = -0.4F;
      }

      if (this.isCarrying) {
         this.bipedRightArm.rotateAngleX = -0.5F;
         this.bipedLeftArm.rotateAngleX = -0.5F;
         this.bipedRightArm.rotateAngleZ = 0.05F;
         this.bipedLeftArm.rotateAngleZ = -0.05F;
      }

      this.bipedRightArm.rotationPointZ = 0.0F;
      this.bipedLeftArm.rotationPointZ = 0.0F;
      this.bipedRightLeg.rotationPointZ = 0.0F;
      this.bipedLeftLeg.rotationPointZ = 0.0F;
      this.bipedRightLeg.rotationPointY = -5.0F;
      this.bipedLeftLeg.rotationPointY = -5.0F;
      this.field_78116_c.rotationPointZ = -0.0F;
      this.field_78116_c.rotationPointY = -13.0F;
      this.bipedHeadwear.rotationPointX = this.field_78116_c.rotationPointX;
      this.bipedHeadwear.rotationPointY = this.field_78116_c.rotationPointY;
      this.bipedHeadwear.rotationPointZ = this.field_78116_c.rotationPointZ;
      this.bipedHeadwear.rotateAngleX = this.field_78116_c.rotateAngleX;
      this.bipedHeadwear.rotateAngleY = this.field_78116_c.rotateAngleY;
      this.bipedHeadwear.rotateAngleZ = this.field_78116_c.rotateAngleZ;
      if (this.isAttacking) {
         float f2 = 1.0F;
         this.field_78116_c.rotationPointY -= 5.0F;
      }

   }
}