package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.monster.RavagerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RavagerModel extends EntityModel<RavagerEntity> {
   private final RendererModel field_217168_a;
   private final RendererModel field_217169_b;
   private final RendererModel field_217170_f;
   private final RendererModel field_217171_g;
   private final RendererModel field_217172_h;
   private final RendererModel field_217173_i;
   private final RendererModel field_217174_j;
   private final RendererModel field_217175_k;

   public RavagerModel() {
      this.textureWidth = 128;
      this.textureHeight = 128;
      int i = 16;
      float f = 0.0F;
      this.field_217175_k = new RendererModel(this);
      this.field_217175_k.setRotationPoint(0.0F, -7.0F, -1.5F);
      this.field_217175_k.setTextureOffset(68, 73).addBox(-5.0F, -1.0F, -18.0F, 10, 10, 18, 0.0F);
      this.field_217168_a = new RendererModel(this);
      this.field_217168_a.setRotationPoint(0.0F, 16.0F, -17.0F);
      this.field_217168_a.setTextureOffset(0, 0).addBox(-8.0F, -20.0F, -14.0F, 16, 20, 16, 0.0F);
      this.field_217168_a.setTextureOffset(0, 0).addBox(-2.0F, -6.0F, -18.0F, 4, 8, 4, 0.0F);
      RendererModel renderermodel = new RendererModel(this);
      renderermodel.setRotationPoint(-10.0F, -14.0F, -8.0F);
      renderermodel.setTextureOffset(74, 55).addBox(0.0F, -14.0F, -2.0F, 2, 14, 4, 0.0F);
      renderermodel.rotateAngleX = 1.0995574F;
      this.field_217168_a.addChild(renderermodel);
      RendererModel renderermodel1 = new RendererModel(this);
      renderermodel1.mirror = true;
      renderermodel1.setRotationPoint(8.0F, -14.0F, -8.0F);
      renderermodel1.setTextureOffset(74, 55).addBox(0.0F, -14.0F, -2.0F, 2, 14, 4, 0.0F);
      renderermodel1.rotateAngleX = 1.0995574F;
      this.field_217168_a.addChild(renderermodel1);
      this.field_217169_b = new RendererModel(this);
      this.field_217169_b.setRotationPoint(0.0F, -2.0F, 2.0F);
      this.field_217169_b.setTextureOffset(0, 36).addBox(-8.0F, 0.0F, -16.0F, 16, 3, 16, 0.0F);
      this.field_217168_a.addChild(this.field_217169_b);
      this.field_217175_k.addChild(this.field_217168_a);
      this.field_217170_f = new RendererModel(this);
      this.field_217170_f.setTextureOffset(0, 55).addBox(-7.0F, -10.0F, -7.0F, 14, 16, 20, 0.0F);
      this.field_217170_f.setTextureOffset(0, 91).addBox(-6.0F, 6.0F, -7.0F, 12, 13, 18, 0.0F);
      this.field_217170_f.setRotationPoint(0.0F, 1.0F, 2.0F);
      this.field_217171_g = new RendererModel(this, 96, 0);
      this.field_217171_g.addBox(-4.0F, 0.0F, -4.0F, 8, 37, 8, 0.0F);
      this.field_217171_g.setRotationPoint(-8.0F, -13.0F, 18.0F);
      this.field_217172_h = new RendererModel(this, 96, 0);
      this.field_217172_h.mirror = true;
      this.field_217172_h.addBox(-4.0F, 0.0F, -4.0F, 8, 37, 8, 0.0F);
      this.field_217172_h.setRotationPoint(8.0F, -13.0F, 18.0F);
      this.field_217173_i = new RendererModel(this, 64, 0);
      this.field_217173_i.addBox(-4.0F, 0.0F, -4.0F, 8, 37, 8, 0.0F);
      this.field_217173_i.setRotationPoint(-8.0F, -13.0F, -5.0F);
      this.field_217174_j = new RendererModel(this, 64, 0);
      this.field_217174_j.mirror = true;
      this.field_217174_j.addBox(-4.0F, 0.0F, -4.0F, 8, 37, 8, 0.0F);
      this.field_217174_j.setRotationPoint(8.0F, -13.0F, -5.0F);
   }

   /**
    * Sets the models various rotation angles then renders the model.
    */
   public void render(RavagerEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      this.field_217175_k.render(scale);
      this.field_217170_f.render(scale);
      this.field_217171_g.render(scale);
      this.field_217172_h.render(scale);
      this.field_217173_i.render(scale);
      this.field_217174_j.render(scale);
   }

   public void setRotationAngles(RavagerEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
      this.field_217168_a.rotateAngleX = headPitch * ((float)Math.PI / 180F);
      this.field_217168_a.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
      this.field_217170_f.rotateAngleX = ((float)Math.PI / 2F);
      float f = 0.4F * limbSwingAmount;
      this.field_217171_g.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * f;
      this.field_217172_h.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * f;
      this.field_217173_i.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * f;
      this.field_217174_j.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * f;
   }

   public void setLivingAnimations(RavagerEntity entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
      super.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);
      int i = entityIn.func_213684_dX();
      int j = entityIn.func_213687_eg();
      int k = 20;
      int l = entityIn.func_213683_l();
      int i1 = 10;
      if (l > 0) {
         float f = this.func_217167_a((float)l - partialTick, 10.0F);
         float f1 = (1.0F + f) * 0.5F;
         float f2 = f1 * f1 * f1 * 12.0F;
         float f3 = f2 * MathHelper.sin(this.field_217175_k.rotateAngleX);
         this.field_217175_k.rotationPointZ = -6.5F + f2;
         this.field_217175_k.rotationPointY = -7.0F - f3;
         float f4 = MathHelper.sin(((float)l - partialTick) / 10.0F * (float)Math.PI * 0.25F);
         this.field_217169_b.rotateAngleX = ((float)Math.PI / 2F) * f4;
         if (l > 5) {
            this.field_217169_b.rotateAngleX = MathHelper.sin(((float)(-4 + l) - partialTick) / 4.0F) * (float)Math.PI * 0.4F;
         } else {
            this.field_217169_b.rotateAngleX = 0.15707964F * MathHelper.sin((float)Math.PI * ((float)l - partialTick) / 10.0F);
         }
      } else {
         float f5 = -1.0F;
         float f6 = -1.0F * MathHelper.sin(this.field_217175_k.rotateAngleX);
         this.field_217175_k.rotationPointX = 0.0F;
         this.field_217175_k.rotationPointY = -7.0F - f6;
         this.field_217175_k.rotationPointZ = 5.5F;
         boolean flag = i > 0;
         this.field_217175_k.rotateAngleX = flag ? 0.21991149F : 0.0F;
         this.field_217169_b.rotateAngleX = (float)Math.PI * (flag ? 0.05F : 0.01F);
         if (flag) {
            double d0 = (double)i / 40.0D;
            this.field_217175_k.rotationPointX = (float)Math.sin(d0 * 10.0D) * 3.0F;
         } else if (j > 0) {
            float f7 = MathHelper.sin(((float)(20 - j) - partialTick) / 20.0F * (float)Math.PI * 0.25F);
            this.field_217169_b.rotateAngleX = ((float)Math.PI / 2F) * f7;
         }
      }

   }

   private float func_217167_a(float p_217167_1_, float p_217167_2_) {
      return (Math.abs(p_217167_1_ % p_217167_2_ - p_217167_2_ * 0.5F) - p_217167_2_ * 0.25F) / (p_217167_2_ * 0.25F);
   }
}