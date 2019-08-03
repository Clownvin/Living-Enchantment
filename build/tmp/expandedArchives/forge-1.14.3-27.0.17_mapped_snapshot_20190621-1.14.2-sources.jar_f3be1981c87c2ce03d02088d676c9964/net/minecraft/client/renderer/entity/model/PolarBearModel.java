package net.minecraft.client.renderer.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PolarBearModel<T extends PolarBearEntity> extends QuadrupedModel<T> {
   public PolarBearModel() {
      super(12, 0.0F);
      this.textureWidth = 128;
      this.textureHeight = 64;
      this.headModel = new RendererModel(this, 0, 0);
      this.headModel.addBox(-3.5F, -3.0F, -3.0F, 7, 7, 7, 0.0F);
      this.headModel.setRotationPoint(0.0F, 10.0F, -16.0F);
      this.headModel.setTextureOffset(0, 44).addBox(-2.5F, 1.0F, -6.0F, 5, 3, 3, 0.0F);
      this.headModel.setTextureOffset(26, 0).addBox(-4.5F, -4.0F, -1.0F, 2, 2, 1, 0.0F);
      RendererModel renderermodel = this.headModel.setTextureOffset(26, 0);
      renderermodel.mirror = true;
      renderermodel.addBox(2.5F, -4.0F, -1.0F, 2, 2, 1, 0.0F);
      this.field_78148_b = new RendererModel(this);
      this.field_78148_b.setTextureOffset(0, 19).addBox(-5.0F, -13.0F, -7.0F, 14, 14, 11, 0.0F);
      this.field_78148_b.setTextureOffset(39, 0).addBox(-4.0F, -25.0F, -7.0F, 12, 12, 10, 0.0F);
      this.field_78148_b.setRotationPoint(-2.0F, 9.0F, 12.0F);
      int i = 10;
      this.field_78149_c = new RendererModel(this, 50, 22);
      this.field_78149_c.addBox(-2.0F, 0.0F, -2.0F, 4, 10, 8, 0.0F);
      this.field_78149_c.setRotationPoint(-3.5F, 14.0F, 6.0F);
      this.field_78146_d = new RendererModel(this, 50, 22);
      this.field_78146_d.addBox(-2.0F, 0.0F, -2.0F, 4, 10, 8, 0.0F);
      this.field_78146_d.setRotationPoint(3.5F, 14.0F, 6.0F);
      this.field_78147_e = new RendererModel(this, 50, 40);
      this.field_78147_e.addBox(-2.0F, 0.0F, -2.0F, 4, 10, 6, 0.0F);
      this.field_78147_e.setRotationPoint(-2.5F, 14.0F, -7.0F);
      this.field_78144_f = new RendererModel(this, 50, 40);
      this.field_78144_f.addBox(-2.0F, 0.0F, -2.0F, 4, 10, 6, 0.0F);
      this.field_78144_f.setRotationPoint(2.5F, 14.0F, -7.0F);
      --this.field_78149_c.rotationPointX;
      ++this.field_78146_d.rotationPointX;
      this.field_78149_c.rotationPointZ += 0.0F;
      this.field_78146_d.rotationPointZ += 0.0F;
      --this.field_78147_e.rotationPointX;
      ++this.field_78144_f.rotationPointX;
      --this.field_78147_e.rotationPointZ;
      --this.field_78144_f.rotationPointZ;
      this.childZOffset += 2.0F;
   }

   /**
    * Sets the models various rotation angles then renders the model.
    */
   public void render(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      if (this.isChild) {
         float f = 2.0F;
         this.childYOffset = 16.0F;
         this.childZOffset = 4.0F;
         GlStateManager.pushMatrix();
         GlStateManager.scalef(0.6666667F, 0.6666667F, 0.6666667F);
         GlStateManager.translatef(0.0F, this.childYOffset * scale, this.childZOffset * scale);
         this.headModel.render(scale);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.scalef(0.5F, 0.5F, 0.5F);
         GlStateManager.translatef(0.0F, 24.0F * scale, 0.0F);
         this.field_78148_b.render(scale);
         this.field_78149_c.render(scale);
         this.field_78146_d.render(scale);
         this.field_78147_e.render(scale);
         this.field_78144_f.render(scale);
         GlStateManager.popMatrix();
      } else {
         this.headModel.render(scale);
         this.field_78148_b.render(scale);
         this.field_78149_c.render(scale);
         this.field_78146_d.render(scale);
         this.field_78147_e.render(scale);
         this.field_78144_f.render(scale);
      }

   }

   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
      super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
      float f = ageInTicks - (float)entityIn.ticksExisted;
      float f1 = entityIn.getStandingAnimationScale(f);
      f1 = f1 * f1;
      float f2 = 1.0F - f1;
      this.field_78148_b.rotateAngleX = ((float)Math.PI / 2F) - f1 * (float)Math.PI * 0.35F;
      this.field_78148_b.rotationPointY = 9.0F * f2 + 11.0F * f1;
      this.field_78147_e.rotationPointY = 14.0F * f2 - 6.0F * f1;
      this.field_78147_e.rotationPointZ = -8.0F * f2 - 4.0F * f1;
      this.field_78147_e.rotateAngleX -= f1 * (float)Math.PI * 0.45F;
      this.field_78144_f.rotationPointY = this.field_78147_e.rotationPointY;
      this.field_78144_f.rotationPointZ = this.field_78147_e.rotationPointZ;
      this.field_78144_f.rotateAngleX -= f1 * (float)Math.PI * 0.45F;
      if (this.isChild) {
         this.headModel.rotationPointY = 10.0F * f2 - 9.0F * f1;
         this.headModel.rotationPointZ = -16.0F * f2 - 7.0F * f1;
      } else {
         this.headModel.rotationPointY = 10.0F * f2 - 14.0F * f1;
         this.headModel.rotationPointZ = -16.0F * f2 - 3.0F * f1;
      }

      this.headModel.rotateAngleX += f1 * (float)Math.PI * 0.15F;
   }
}