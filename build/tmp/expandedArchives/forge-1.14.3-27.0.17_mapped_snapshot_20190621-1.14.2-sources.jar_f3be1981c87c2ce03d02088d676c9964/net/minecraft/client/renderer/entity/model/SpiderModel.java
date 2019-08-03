package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpiderModel<T extends Entity> extends EntityModel<T> {
   private final RendererModel field_78209_a;
   private final RendererModel field_78207_b;
   private final RendererModel field_78208_c;
   private final RendererModel field_78205_d;
   private final RendererModel field_78206_e;
   private final RendererModel field_78203_f;
   private final RendererModel field_78204_g;
   private final RendererModel field_78212_h;
   private final RendererModel field_78213_i;
   private final RendererModel field_78210_j;
   private final RendererModel field_78211_k;

   public SpiderModel() {
      float f = 0.0F;
      int i = 15;
      this.field_78209_a = new RendererModel(this, 32, 4);
      this.field_78209_a.addBox(-4.0F, -4.0F, -8.0F, 8, 8, 8, 0.0F);
      this.field_78209_a.setRotationPoint(0.0F, 15.0F, -3.0F);
      this.field_78207_b = new RendererModel(this, 0, 0);
      this.field_78207_b.addBox(-3.0F, -3.0F, -3.0F, 6, 6, 6, 0.0F);
      this.field_78207_b.setRotationPoint(0.0F, 15.0F, 0.0F);
      this.field_78208_c = new RendererModel(this, 0, 12);
      this.field_78208_c.addBox(-5.0F, -4.0F, -6.0F, 10, 8, 12, 0.0F);
      this.field_78208_c.setRotationPoint(0.0F, 15.0F, 9.0F);
      this.field_78205_d = new RendererModel(this, 18, 0);
      this.field_78205_d.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
      this.field_78205_d.setRotationPoint(-4.0F, 15.0F, 2.0F);
      this.field_78206_e = new RendererModel(this, 18, 0);
      this.field_78206_e.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
      this.field_78206_e.setRotationPoint(4.0F, 15.0F, 2.0F);
      this.field_78203_f = new RendererModel(this, 18, 0);
      this.field_78203_f.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
      this.field_78203_f.setRotationPoint(-4.0F, 15.0F, 1.0F);
      this.field_78204_g = new RendererModel(this, 18, 0);
      this.field_78204_g.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
      this.field_78204_g.setRotationPoint(4.0F, 15.0F, 1.0F);
      this.field_78212_h = new RendererModel(this, 18, 0);
      this.field_78212_h.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
      this.field_78212_h.setRotationPoint(-4.0F, 15.0F, 0.0F);
      this.field_78213_i = new RendererModel(this, 18, 0);
      this.field_78213_i.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
      this.field_78213_i.setRotationPoint(4.0F, 15.0F, 0.0F);
      this.field_78210_j = new RendererModel(this, 18, 0);
      this.field_78210_j.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
      this.field_78210_j.setRotationPoint(-4.0F, 15.0F, -1.0F);
      this.field_78211_k = new RendererModel(this, 18, 0);
      this.field_78211_k.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, 0.0F);
      this.field_78211_k.setRotationPoint(4.0F, 15.0F, -1.0F);
   }

   /**
    * Sets the models various rotation angles then renders the model.
    */
   public void render(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      this.field_78209_a.render(scale);
      this.field_78207_b.render(scale);
      this.field_78208_c.render(scale);
      this.field_78205_d.render(scale);
      this.field_78206_e.render(scale);
      this.field_78203_f.render(scale);
      this.field_78204_g.render(scale);
      this.field_78212_h.render(scale);
      this.field_78213_i.render(scale);
      this.field_78210_j.render(scale);
      this.field_78211_k.render(scale);
   }

   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
      this.field_78209_a.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
      this.field_78209_a.rotateAngleX = headPitch * ((float)Math.PI / 180F);
      float f = ((float)Math.PI / 4F);
      this.field_78205_d.rotateAngleZ = (-(float)Math.PI / 4F);
      this.field_78206_e.rotateAngleZ = ((float)Math.PI / 4F);
      this.field_78203_f.rotateAngleZ = -0.58119464F;
      this.field_78204_g.rotateAngleZ = 0.58119464F;
      this.field_78212_h.rotateAngleZ = -0.58119464F;
      this.field_78213_i.rotateAngleZ = 0.58119464F;
      this.field_78210_j.rotateAngleZ = (-(float)Math.PI / 4F);
      this.field_78211_k.rotateAngleZ = ((float)Math.PI / 4F);
      float f1 = -0.0F;
      float f2 = ((float)Math.PI / 8F);
      this.field_78205_d.rotateAngleY = ((float)Math.PI / 4F);
      this.field_78206_e.rotateAngleY = (-(float)Math.PI / 4F);
      this.field_78203_f.rotateAngleY = ((float)Math.PI / 8F);
      this.field_78204_g.rotateAngleY = (-(float)Math.PI / 8F);
      this.field_78212_h.rotateAngleY = (-(float)Math.PI / 8F);
      this.field_78213_i.rotateAngleY = ((float)Math.PI / 8F);
      this.field_78210_j.rotateAngleY = (-(float)Math.PI / 4F);
      this.field_78211_k.rotateAngleY = ((float)Math.PI / 4F);
      float f3 = -(MathHelper.cos(limbSwing * 0.6662F * 2.0F + 0.0F) * 0.4F) * limbSwingAmount;
      float f4 = -(MathHelper.cos(limbSwing * 0.6662F * 2.0F + (float)Math.PI) * 0.4F) * limbSwingAmount;
      float f5 = -(MathHelper.cos(limbSwing * 0.6662F * 2.0F + ((float)Math.PI / 2F)) * 0.4F) * limbSwingAmount;
      float f6 = -(MathHelper.cos(limbSwing * 0.6662F * 2.0F + ((float)Math.PI * 1.5F)) * 0.4F) * limbSwingAmount;
      float f7 = Math.abs(MathHelper.sin(limbSwing * 0.6662F + 0.0F) * 0.4F) * limbSwingAmount;
      float f8 = Math.abs(MathHelper.sin(limbSwing * 0.6662F + (float)Math.PI) * 0.4F) * limbSwingAmount;
      float f9 = Math.abs(MathHelper.sin(limbSwing * 0.6662F + ((float)Math.PI / 2F)) * 0.4F) * limbSwingAmount;
      float f10 = Math.abs(MathHelper.sin(limbSwing * 0.6662F + ((float)Math.PI * 1.5F)) * 0.4F) * limbSwingAmount;
      this.field_78205_d.rotateAngleY += f3;
      this.field_78206_e.rotateAngleY += -f3;
      this.field_78203_f.rotateAngleY += f4;
      this.field_78204_g.rotateAngleY += -f4;
      this.field_78212_h.rotateAngleY += f5;
      this.field_78213_i.rotateAngleY += -f5;
      this.field_78210_j.rotateAngleY += f6;
      this.field_78211_k.rotateAngleY += -f6;
      this.field_78205_d.rotateAngleZ += f7;
      this.field_78206_e.rotateAngleZ += -f7;
      this.field_78203_f.rotateAngleZ += f8;
      this.field_78204_g.rotateAngleZ += -f8;
      this.field_78212_h.rotateAngleZ += f9;
      this.field_78213_i.rotateAngleZ += -f9;
      this.field_78210_j.rotateAngleZ += f10;
      this.field_78211_k.rotateAngleZ += -f10;
   }
}