package net.minecraft.client.renderer.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PandaModel<T extends PandaEntity> extends QuadrupedModel<T> {
   private float field_217164_l;
   private float field_217165_m;
   private float field_217166_n;

   public PandaModel(int p_i51063_1_, float p_i51063_2_) {
      super(p_i51063_1_, p_i51063_2_);
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.headModel = new RendererModel(this, 0, 6);
      this.headModel.addBox(-6.5F, -5.0F, -4.0F, 13, 10, 9);
      this.headModel.setRotationPoint(0.0F, 11.5F, -17.0F);
      this.headModel.setTextureOffset(45, 16).addBox(-3.5F, 0.0F, -6.0F, 7, 5, 2);
      this.headModel.setTextureOffset(52, 25).addBox(-8.5F, -8.0F, -1.0F, 5, 4, 1);
      this.headModel.setTextureOffset(52, 25).addBox(3.5F, -8.0F, -1.0F, 5, 4, 1);
      this.field_78148_b = new RendererModel(this, 0, 25);
      this.field_78148_b.addBox(-9.5F, -13.0F, -6.5F, 19, 26, 13);
      this.field_78148_b.setRotationPoint(0.0F, 10.0F, 0.0F);
      int i = 9;
      int j = 6;
      this.field_78149_c = new RendererModel(this, 40, 0);
      this.field_78149_c.addBox(-3.0F, 0.0F, -3.0F, 6, 9, 6);
      this.field_78149_c.setRotationPoint(-5.5F, 15.0F, 9.0F);
      this.field_78146_d = new RendererModel(this, 40, 0);
      this.field_78146_d.addBox(-3.0F, 0.0F, -3.0F, 6, 9, 6);
      this.field_78146_d.setRotationPoint(5.5F, 15.0F, 9.0F);
      this.field_78147_e = new RendererModel(this, 40, 0);
      this.field_78147_e.addBox(-3.0F, 0.0F, -3.0F, 6, 9, 6);
      this.field_78147_e.setRotationPoint(-5.5F, 15.0F, -9.0F);
      this.field_78144_f = new RendererModel(this, 40, 0);
      this.field_78144_f.addBox(-3.0F, 0.0F, -3.0F, 6, 9, 6);
      this.field_78144_f.setRotationPoint(5.5F, 15.0F, -9.0F);
   }

   public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
      super.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);
      this.field_217164_l = entityIn.func_213561_v(partialTick);
      this.field_217165_m = entityIn.func_213583_w(partialTick);
      this.field_217166_n = entityIn.isChild() ? 0.0F : entityIn.func_213591_x(partialTick);
   }

   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
      super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
      boolean flag = entityIn.func_213544_dV() > 0;
      boolean flag1 = entityIn.func_213539_dW();
      int i = entityIn.func_213585_ee();
      boolean flag2 = entityIn.func_213578_dZ();
      boolean flag3 = entityIn.func_213566_eo();
      if (flag) {
         this.headModel.rotateAngleY = 0.35F * MathHelper.sin(0.6F * ageInTicks);
         this.headModel.rotateAngleZ = 0.35F * MathHelper.sin(0.6F * ageInTicks);
         this.field_78147_e.rotateAngleX = -0.75F * MathHelper.sin(0.3F * ageInTicks);
         this.field_78144_f.rotateAngleX = 0.75F * MathHelper.sin(0.3F * ageInTicks);
      } else {
         this.headModel.rotateAngleZ = 0.0F;
      }

      if (flag1) {
         if (i < 15) {
            this.headModel.rotateAngleX = (-(float)Math.PI / 4F) * (float)i / 14.0F;
         } else if (i < 20) {
            float f = (float)((i - 15) / 5);
            this.headModel.rotateAngleX = (-(float)Math.PI / 4F) + ((float)Math.PI / 4F) * f;
         }
      }

      if (this.field_217164_l > 0.0F) {
         this.field_78148_b.rotateAngleX = this.func_217163_a(this.field_78148_b.rotateAngleX, 1.7407963F, this.field_217164_l);
         this.headModel.rotateAngleX = this.func_217163_a(this.headModel.rotateAngleX, ((float)Math.PI / 2F), this.field_217164_l);
         this.field_78147_e.rotateAngleZ = -0.27079642F;
         this.field_78144_f.rotateAngleZ = 0.27079642F;
         this.field_78149_c.rotateAngleZ = 0.5707964F;
         this.field_78146_d.rotateAngleZ = -0.5707964F;
         if (flag2) {
            this.headModel.rotateAngleX = ((float)Math.PI / 2F) + 0.2F * MathHelper.sin(ageInTicks * 0.6F);
            this.field_78147_e.rotateAngleX = -0.4F - 0.2F * MathHelper.sin(ageInTicks * 0.6F);
            this.field_78144_f.rotateAngleX = -0.4F - 0.2F * MathHelper.sin(ageInTicks * 0.6F);
         }

         if (flag3) {
            this.headModel.rotateAngleX = 2.1707964F;
            this.field_78147_e.rotateAngleX = -0.9F;
            this.field_78144_f.rotateAngleX = -0.9F;
         }
      } else {
         this.field_78149_c.rotateAngleZ = 0.0F;
         this.field_78146_d.rotateAngleZ = 0.0F;
         this.field_78147_e.rotateAngleZ = 0.0F;
         this.field_78144_f.rotateAngleZ = 0.0F;
      }

      if (this.field_217165_m > 0.0F) {
         this.field_78149_c.rotateAngleX = -0.6F * MathHelper.sin(ageInTicks * 0.15F);
         this.field_78146_d.rotateAngleX = 0.6F * MathHelper.sin(ageInTicks * 0.15F);
         this.field_78147_e.rotateAngleX = 0.3F * MathHelper.sin(ageInTicks * 0.25F);
         this.field_78144_f.rotateAngleX = -0.3F * MathHelper.sin(ageInTicks * 0.25F);
         this.headModel.rotateAngleX = this.func_217163_a(this.headModel.rotateAngleX, ((float)Math.PI / 2F), this.field_217165_m);
      }

      if (this.field_217166_n > 0.0F) {
         this.headModel.rotateAngleX = this.func_217163_a(this.headModel.rotateAngleX, 2.0561945F, this.field_217166_n);
         this.field_78149_c.rotateAngleX = -0.5F * MathHelper.sin(ageInTicks * 0.5F);
         this.field_78146_d.rotateAngleX = 0.5F * MathHelper.sin(ageInTicks * 0.5F);
         this.field_78147_e.rotateAngleX = 0.5F * MathHelper.sin(ageInTicks * 0.5F);
         this.field_78144_f.rotateAngleX = -0.5F * MathHelper.sin(ageInTicks * 0.5F);
      }

   }

   protected float func_217163_a(float p_217163_1_, float p_217163_2_, float p_217163_3_) {
      float f;
      for(f = p_217163_2_ - p_217163_1_; f < -(float)Math.PI; f += ((float)Math.PI * 2F)) {
         ;
      }

      while(f >= (float)Math.PI) {
         f -= ((float)Math.PI * 2F);
      }

      return p_217163_1_ + p_217163_3_ * f;
   }

   /**
    * Sets the models various rotation angles then renders the model.
    */
   public void render(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      if (this.isChild) {
         float f = 3.0F;
         GlStateManager.pushMatrix();
         GlStateManager.translatef(0.0F, this.childYOffset * scale, this.childZOffset * scale);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         float f1 = 0.6F;
         GlStateManager.scalef(0.5555555F, 0.5555555F, 0.5555555F);
         GlStateManager.translatef(0.0F, 23.0F * scale, 0.3F);
         this.headModel.render(scale);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.scalef(0.33333334F, 0.33333334F, 0.33333334F);
         GlStateManager.translatef(0.0F, 49.0F * scale, 0.0F);
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
}