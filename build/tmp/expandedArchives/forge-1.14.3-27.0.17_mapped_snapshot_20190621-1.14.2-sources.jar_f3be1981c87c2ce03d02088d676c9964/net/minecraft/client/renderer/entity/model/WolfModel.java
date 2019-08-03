package net.minecraft.client.renderer.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WolfModel<T extends WolfEntity> extends EntityModel<T> {
   private final RendererModel field_78185_a;
   private final RendererModel field_78183_b;
   private final RendererModel field_78184_c;
   private final RendererModel field_78181_d;
   private final RendererModel field_78182_e;
   private final RendererModel field_78179_f;
   private final RendererModel field_78180_g;
   private final RendererModel field_78186_h;

   public WolfModel() {
      float f = 0.0F;
      float f1 = 13.5F;
      this.field_78185_a = new RendererModel(this, 0, 0);
      this.field_78185_a.addBox(-2.0F, -3.0F, -2.0F, 6, 6, 4, 0.0F);
      this.field_78185_a.setRotationPoint(-1.0F, 13.5F, -7.0F);
      this.field_78183_b = new RendererModel(this, 18, 14);
      this.field_78183_b.addBox(-3.0F, -2.0F, -3.0F, 6, 9, 6, 0.0F);
      this.field_78183_b.setRotationPoint(0.0F, 14.0F, 2.0F);
      this.field_78186_h = new RendererModel(this, 21, 0);
      this.field_78186_h.addBox(-3.0F, -3.0F, -3.0F, 8, 6, 7, 0.0F);
      this.field_78186_h.setRotationPoint(-1.0F, 14.0F, 2.0F);
      this.field_78184_c = new RendererModel(this, 0, 18);
      this.field_78184_c.addBox(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
      this.field_78184_c.setRotationPoint(-2.5F, 16.0F, 7.0F);
      this.field_78181_d = new RendererModel(this, 0, 18);
      this.field_78181_d.addBox(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
      this.field_78181_d.setRotationPoint(0.5F, 16.0F, 7.0F);
      this.field_78182_e = new RendererModel(this, 0, 18);
      this.field_78182_e.addBox(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
      this.field_78182_e.setRotationPoint(-2.5F, 16.0F, -4.0F);
      this.field_78179_f = new RendererModel(this, 0, 18);
      this.field_78179_f.addBox(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
      this.field_78179_f.setRotationPoint(0.5F, 16.0F, -4.0F);
      this.field_78180_g = new RendererModel(this, 9, 18);
      this.field_78180_g.addBox(0.0F, 0.0F, -1.0F, 2, 8, 2, 0.0F);
      this.field_78180_g.setRotationPoint(-1.0F, 12.0F, 8.0F);
      this.field_78185_a.setTextureOffset(16, 14).addBox(-2.0F, -5.0F, 0.0F, 2, 2, 1, 0.0F);
      this.field_78185_a.setTextureOffset(16, 14).addBox(2.0F, -5.0F, 0.0F, 2, 2, 1, 0.0F);
      this.field_78185_a.setTextureOffset(0, 10).addBox(-0.5F, 0.0F, -5.0F, 3, 3, 4, 0.0F);
   }

   /**
    * Sets the models various rotation angles then renders the model.
    */
   public void render(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      this.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      if (this.isChild) {
         float f = 2.0F;
         GlStateManager.pushMatrix();
         GlStateManager.translatef(0.0F, 5.0F * scale, 2.0F * scale);
         this.field_78185_a.renderWithRotation(scale);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.scalef(0.5F, 0.5F, 0.5F);
         GlStateManager.translatef(0.0F, 24.0F * scale, 0.0F);
         this.field_78183_b.render(scale);
         this.field_78184_c.render(scale);
         this.field_78181_d.render(scale);
         this.field_78182_e.render(scale);
         this.field_78179_f.render(scale);
         this.field_78180_g.renderWithRotation(scale);
         this.field_78186_h.render(scale);
         GlStateManager.popMatrix();
      } else {
         this.field_78185_a.renderWithRotation(scale);
         this.field_78183_b.render(scale);
         this.field_78184_c.render(scale);
         this.field_78181_d.render(scale);
         this.field_78182_e.render(scale);
         this.field_78179_f.render(scale);
         this.field_78180_g.renderWithRotation(scale);
         this.field_78186_h.render(scale);
      }

   }

   public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
      if (entityIn.isAngry()) {
         this.field_78180_g.rotateAngleY = 0.0F;
      } else {
         this.field_78180_g.rotateAngleY = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
      }

      if (entityIn.isSitting()) {
         this.field_78186_h.setRotationPoint(-1.0F, 16.0F, -3.0F);
         this.field_78186_h.rotateAngleX = 1.2566371F;
         this.field_78186_h.rotateAngleY = 0.0F;
         this.field_78183_b.setRotationPoint(0.0F, 18.0F, 0.0F);
         this.field_78183_b.rotateAngleX = ((float)Math.PI / 4F);
         this.field_78180_g.setRotationPoint(-1.0F, 21.0F, 6.0F);
         this.field_78184_c.setRotationPoint(-2.5F, 22.0F, 2.0F);
         this.field_78184_c.rotateAngleX = ((float)Math.PI * 1.5F);
         this.field_78181_d.setRotationPoint(0.5F, 22.0F, 2.0F);
         this.field_78181_d.rotateAngleX = ((float)Math.PI * 1.5F);
         this.field_78182_e.rotateAngleX = 5.811947F;
         this.field_78182_e.setRotationPoint(-2.49F, 17.0F, -4.0F);
         this.field_78179_f.rotateAngleX = 5.811947F;
         this.field_78179_f.setRotationPoint(0.51F, 17.0F, -4.0F);
      } else {
         this.field_78183_b.setRotationPoint(0.0F, 14.0F, 2.0F);
         this.field_78183_b.rotateAngleX = ((float)Math.PI / 2F);
         this.field_78186_h.setRotationPoint(-1.0F, 14.0F, -3.0F);
         this.field_78186_h.rotateAngleX = this.field_78183_b.rotateAngleX;
         this.field_78180_g.setRotationPoint(-1.0F, 12.0F, 8.0F);
         this.field_78184_c.setRotationPoint(-2.5F, 16.0F, 7.0F);
         this.field_78181_d.setRotationPoint(0.5F, 16.0F, 7.0F);
         this.field_78182_e.setRotationPoint(-2.5F, 16.0F, -4.0F);
         this.field_78179_f.setRotationPoint(0.5F, 16.0F, -4.0F);
         this.field_78184_c.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
         this.field_78181_d.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
         this.field_78182_e.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
         this.field_78179_f.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
      }

      this.field_78185_a.rotateAngleZ = entityIn.getInterestedAngle(partialTick) + entityIn.getShakeAngle(partialTick, 0.0F);
      this.field_78186_h.rotateAngleZ = entityIn.getShakeAngle(partialTick, -0.08F);
      this.field_78183_b.rotateAngleZ = entityIn.getShakeAngle(partialTick, -0.16F);
      this.field_78180_g.rotateAngleZ = entityIn.getShakeAngle(partialTick, -0.2F);
   }

   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
      super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
      this.field_78185_a.rotateAngleX = headPitch * ((float)Math.PI / 180F);
      this.field_78185_a.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
      this.field_78180_g.rotateAngleX = ageInTicks;
   }
}