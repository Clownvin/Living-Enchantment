package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.monster.VexEntity;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VexModel extends BipedModel<VexEntity> {
   private final RendererModel leftWing;
   private final RendererModel rightWing;

   public VexModel() {
      this(0.0F);
   }

   public VexModel(float p_i47224_1_) {
      super(p_i47224_1_, 0.0F, 64, 64);
      this.bipedLeftLeg.showModel = false;
      this.bipedHeadwear.showModel = false;
      this.bipedRightLeg = new RendererModel(this, 32, 0);
      this.bipedRightLeg.addBox(-1.0F, -1.0F, -2.0F, 6, 10, 4, 0.0F);
      this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
      this.rightWing = new RendererModel(this, 0, 32);
      this.rightWing.addBox(-20.0F, 0.0F, 0.0F, 20, 12, 1);
      this.leftWing = new RendererModel(this, 0, 32);
      this.leftWing.mirror = true;
      this.leftWing.addBox(0.0F, 0.0F, 0.0F, 20, 12, 1);
   }

   /**
    * Sets the models various rotation angles then renders the model.
    */
   public void render(VexEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      this.rightWing.render(scale);
      this.leftWing.render(scale);
   }

   public void setRotationAngles(VexEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
      super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
      if (entityIn.isCharging()) {
         if (entityIn.getPrimaryHand() == HandSide.RIGHT) {
            this.bipedRightArm.rotateAngleX = 3.7699115F;
         } else {
            this.bipedLeftArm.rotateAngleX = 3.7699115F;
         }
      }

      this.bipedRightLeg.rotateAngleX += ((float)Math.PI / 5F);
      this.rightWing.rotationPointZ = 2.0F;
      this.leftWing.rotationPointZ = 2.0F;
      this.rightWing.rotationPointY = 1.0F;
      this.leftWing.rotationPointY = 1.0F;
      this.rightWing.rotateAngleY = 0.47123894F + MathHelper.cos(ageInTicks * 0.8F) * (float)Math.PI * 0.05F;
      this.leftWing.rotateAngleY = -this.rightWing.rotateAngleY;
      this.leftWing.rotateAngleZ = -0.47123894F;
      this.leftWing.rotateAngleX = 0.47123894F;
      this.rightWing.rotateAngleX = 0.47123894F;
      this.rightWing.rotateAngleZ = 0.47123894F;
   }
}