package net.minecraft.client.renderer.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TurtleModel<T extends TurtleEntity> extends QuadrupedModel<T> {
   private final RendererModel field_203078_i;

   public TurtleModel(float p_i48834_1_) {
      super(12, p_i48834_1_);
      this.textureWidth = 128;
      this.textureHeight = 64;
      this.headModel = new RendererModel(this, 3, 0);
      this.headModel.addBox(-3.0F, -1.0F, -3.0F, 6, 5, 6, 0.0F);
      this.headModel.setRotationPoint(0.0F, 19.0F, -10.0F);
      this.field_78148_b = new RendererModel(this);
      this.field_78148_b.setTextureOffset(7, 37).addBox(-9.5F, 3.0F, -10.0F, 19, 20, 6, 0.0F);
      this.field_78148_b.setTextureOffset(31, 1).addBox(-5.5F, 3.0F, -13.0F, 11, 18, 3, 0.0F);
      this.field_78148_b.setRotationPoint(0.0F, 11.0F, -10.0F);
      this.field_203078_i = new RendererModel(this);
      this.field_203078_i.setTextureOffset(70, 33).addBox(-4.5F, 3.0F, -14.0F, 9, 18, 1, 0.0F);
      this.field_203078_i.setRotationPoint(0.0F, 11.0F, -10.0F);
      int i = 1;
      this.field_78149_c = new RendererModel(this, 1, 23);
      this.field_78149_c.addBox(-2.0F, 0.0F, 0.0F, 4, 1, 10, 0.0F);
      this.field_78149_c.setRotationPoint(-3.5F, 22.0F, 11.0F);
      this.field_78146_d = new RendererModel(this, 1, 12);
      this.field_78146_d.addBox(-2.0F, 0.0F, 0.0F, 4, 1, 10, 0.0F);
      this.field_78146_d.setRotationPoint(3.5F, 22.0F, 11.0F);
      this.field_78147_e = new RendererModel(this, 27, 30);
      this.field_78147_e.addBox(-13.0F, 0.0F, -2.0F, 13, 1, 5, 0.0F);
      this.field_78147_e.setRotationPoint(-5.0F, 21.0F, -4.0F);
      this.field_78144_f = new RendererModel(this, 27, 24);
      this.field_78144_f.addBox(0.0F, 0.0F, -2.0F, 13, 1, 5, 0.0F);
      this.field_78144_f.setRotationPoint(5.0F, 21.0F, -4.0F);
   }

   /**
    * Sets the models various rotation angles then renders the model.
    */
   public void render(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      if (this.isChild) {
         float f = 6.0F;
         GlStateManager.pushMatrix();
         GlStateManager.scalef(0.16666667F, 0.16666667F, 0.16666667F);
         GlStateManager.translatef(0.0F, 120.0F * scale, 0.0F);
         this.headModel.render(scale);
         this.field_78148_b.render(scale);
         this.field_78149_c.render(scale);
         this.field_78146_d.render(scale);
         this.field_78147_e.render(scale);
         this.field_78144_f.render(scale);
         GlStateManager.popMatrix();
      } else {
         GlStateManager.pushMatrix();
         if (entityIn.hasEgg()) {
            GlStateManager.translatef(0.0F, -0.08F, 0.0F);
         }

         this.headModel.render(scale);
         this.field_78148_b.render(scale);
         GlStateManager.pushMatrix();
         this.field_78149_c.render(scale);
         this.field_78146_d.render(scale);
         GlStateManager.popMatrix();
         this.field_78147_e.render(scale);
         this.field_78144_f.render(scale);
         if (entityIn.hasEgg()) {
            this.field_203078_i.render(scale);
         }

         GlStateManager.popMatrix();
      }

   }

   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
      super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
      this.field_78149_c.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F * 0.6F) * 0.5F * limbSwingAmount;
      this.field_78146_d.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F * 0.6F + (float)Math.PI) * 0.5F * limbSwingAmount;
      this.field_78147_e.rotateAngleZ = MathHelper.cos(limbSwing * 0.6662F * 0.6F + (float)Math.PI) * 0.5F * limbSwingAmount;
      this.field_78144_f.rotateAngleZ = MathHelper.cos(limbSwing * 0.6662F * 0.6F) * 0.5F * limbSwingAmount;
      this.field_78147_e.rotateAngleX = 0.0F;
      this.field_78144_f.rotateAngleX = 0.0F;
      this.field_78147_e.rotateAngleY = 0.0F;
      this.field_78144_f.rotateAngleY = 0.0F;
      this.field_78149_c.rotateAngleY = 0.0F;
      this.field_78146_d.rotateAngleY = 0.0F;
      this.field_203078_i.rotateAngleX = ((float)Math.PI / 2F);
      if (!entityIn.isInWater() && entityIn.onGround) {
         float f = entityIn.isDigging() ? 4.0F : 1.0F;
         float f1 = entityIn.isDigging() ? 2.0F : 1.0F;
         float f2 = 5.0F;
         this.field_78147_e.rotateAngleY = MathHelper.cos(f * limbSwing * 5.0F + (float)Math.PI) * 8.0F * limbSwingAmount * f1;
         this.field_78147_e.rotateAngleZ = 0.0F;
         this.field_78144_f.rotateAngleY = MathHelper.cos(f * limbSwing * 5.0F) * 8.0F * limbSwingAmount * f1;
         this.field_78144_f.rotateAngleZ = 0.0F;
         this.field_78149_c.rotateAngleY = MathHelper.cos(limbSwing * 5.0F + (float)Math.PI) * 3.0F * limbSwingAmount;
         this.field_78149_c.rotateAngleX = 0.0F;
         this.field_78146_d.rotateAngleY = MathHelper.cos(limbSwing * 5.0F) * 3.0F * limbSwingAmount;
         this.field_78146_d.rotateAngleX = 0.0F;
      }

   }
}