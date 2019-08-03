package net.minecraft.client.renderer.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnderDragonModel extends EntityModel<EnderDragonEntity> {
   private final RendererModel field_78221_a;
   private final RendererModel spine;
   private final RendererModel field_78220_c;
   private final RendererModel field_78217_d;
   private final RendererModel field_78218_e;
   private final RendererModel field_78215_f;
   private final RendererModel field_78216_g;
   private final RendererModel field_78226_h;
   private final RendererModel field_78227_i;
   private final RendererModel field_78224_j;
   private final RendererModel field_78225_k;
   private final RendererModel field_78222_l;
   private float partialTicks;

   public EnderDragonModel(float p_i46360_1_) {
      this.textureWidth = 256;
      this.textureHeight = 256;
      float f = -16.0F;
      this.field_78221_a = new RendererModel(this, "head");
      this.field_78221_a.func_217178_a("upperlip", -6.0F, -1.0F, -24.0F, 12, 5, 16, p_i46360_1_, 176, 44);
      this.field_78221_a.func_217178_a("upperhead", -8.0F, -8.0F, -10.0F, 16, 16, 16, p_i46360_1_, 112, 30);
      this.field_78221_a.mirror = true;
      this.field_78221_a.func_217178_a("scale", -5.0F, -12.0F, -4.0F, 2, 4, 6, p_i46360_1_, 0, 0);
      this.field_78221_a.func_217178_a("nostril", -5.0F, -3.0F, -22.0F, 2, 2, 4, p_i46360_1_, 112, 0);
      this.field_78221_a.mirror = false;
      this.field_78221_a.func_217178_a("scale", 3.0F, -12.0F, -4.0F, 2, 4, 6, p_i46360_1_, 0, 0);
      this.field_78221_a.func_217178_a("nostril", 3.0F, -3.0F, -22.0F, 2, 2, 4, p_i46360_1_, 112, 0);
      this.field_78220_c = new RendererModel(this, "jaw");
      this.field_78220_c.setRotationPoint(0.0F, 4.0F, -8.0F);
      this.field_78220_c.func_217178_a("jaw", -6.0F, 0.0F, -16.0F, 12, 4, 16, p_i46360_1_, 176, 65);
      this.field_78221_a.addChild(this.field_78220_c);
      this.spine = new RendererModel(this, "neck");
      this.spine.func_217178_a("box", -5.0F, -5.0F, -5.0F, 10, 10, 10, p_i46360_1_, 192, 104);
      this.spine.func_217178_a("scale", -1.0F, -9.0F, -3.0F, 2, 4, 6, p_i46360_1_, 48, 0);
      this.field_78217_d = new RendererModel(this, "body");
      this.field_78217_d.setRotationPoint(0.0F, 4.0F, 8.0F);
      this.field_78217_d.func_217178_a("body", -12.0F, 0.0F, -16.0F, 24, 24, 64, p_i46360_1_, 0, 0);
      this.field_78217_d.func_217178_a("scale", -1.0F, -6.0F, -10.0F, 2, 6, 12, p_i46360_1_, 220, 53);
      this.field_78217_d.func_217178_a("scale", -1.0F, -6.0F, 10.0F, 2, 6, 12, p_i46360_1_, 220, 53);
      this.field_78217_d.func_217178_a("scale", -1.0F, -6.0F, 30.0F, 2, 6, 12, p_i46360_1_, 220, 53);
      this.field_78225_k = new RendererModel(this, "wing");
      this.field_78225_k.setRotationPoint(-12.0F, 5.0F, 2.0F);
      this.field_78225_k.func_217178_a("bone", -56.0F, -4.0F, -4.0F, 56, 8, 8, p_i46360_1_, 112, 88);
      this.field_78225_k.func_217178_a("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56, p_i46360_1_, -56, 88);
      this.field_78222_l = new RendererModel(this, "wingtip");
      this.field_78222_l.setRotationPoint(-56.0F, 0.0F, 0.0F);
      this.field_78222_l.func_217178_a("bone", -56.0F, -2.0F, -2.0F, 56, 4, 4, p_i46360_1_, 112, 136);
      this.field_78222_l.func_217178_a("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56, p_i46360_1_, -56, 144);
      this.field_78225_k.addChild(this.field_78222_l);
      this.field_78215_f = new RendererModel(this, "frontleg");
      this.field_78215_f.setRotationPoint(-12.0F, 20.0F, 2.0F);
      this.field_78215_f.func_217178_a("main", -4.0F, -4.0F, -4.0F, 8, 24, 8, p_i46360_1_, 112, 104);
      this.field_78226_h = new RendererModel(this, "frontlegtip");
      this.field_78226_h.setRotationPoint(0.0F, 20.0F, -1.0F);
      this.field_78226_h.func_217178_a("main", -3.0F, -1.0F, -3.0F, 6, 24, 6, p_i46360_1_, 226, 138);
      this.field_78215_f.addChild(this.field_78226_h);
      this.field_78224_j = new RendererModel(this, "frontfoot");
      this.field_78224_j.setRotationPoint(0.0F, 23.0F, 0.0F);
      this.field_78224_j.func_217178_a("main", -4.0F, 0.0F, -12.0F, 8, 4, 16, p_i46360_1_, 144, 104);
      this.field_78226_h.addChild(this.field_78224_j);
      this.field_78218_e = new RendererModel(this, "rearleg");
      this.field_78218_e.setRotationPoint(-16.0F, 16.0F, 42.0F);
      this.field_78218_e.func_217178_a("main", -8.0F, -4.0F, -8.0F, 16, 32, 16, p_i46360_1_, 0, 0);
      this.field_78216_g = new RendererModel(this, "rearlegtip");
      this.field_78216_g.setRotationPoint(0.0F, 32.0F, -4.0F);
      this.field_78216_g.func_217178_a("main", -6.0F, -2.0F, 0.0F, 12, 32, 12, p_i46360_1_, 196, 0);
      this.field_78218_e.addChild(this.field_78216_g);
      this.field_78227_i = new RendererModel(this, "rearfoot");
      this.field_78227_i.setRotationPoint(0.0F, 31.0F, 4.0F);
      this.field_78227_i.func_217178_a("main", -9.0F, 0.0F, -20.0F, 18, 6, 24, p_i46360_1_, 112, 0);
      this.field_78216_g.addChild(this.field_78227_i);
   }

   public void setLivingAnimations(EnderDragonEntity entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
      this.partialTicks = partialTick;
   }

   /**
    * Sets the models various rotation angles then renders the model.
    */
   public void render(EnderDragonEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      GlStateManager.pushMatrix();
      float f = MathHelper.lerp(this.partialTicks, entityIn.prevAnimTime, entityIn.animTime);
      this.field_78220_c.rotateAngleX = (float)(Math.sin((double)(f * ((float)Math.PI * 2F))) + 1.0D) * 0.2F;
      float f1 = (float)(Math.sin((double)(f * ((float)Math.PI * 2F) - 1.0F)) + 1.0D);
      f1 = (f1 * f1 + f1 * 2.0F) * 0.05F;
      GlStateManager.translatef(0.0F, f1 - 2.0F, -3.0F);
      GlStateManager.rotatef(f1 * 2.0F, 1.0F, 0.0F, 0.0F);
      float f2 = 0.0F;
      float f3 = 20.0F;
      float f4 = -12.0F;
      float f5 = 1.5F;
      double[] adouble = entityIn.getMovementOffsets(6, this.partialTicks);
      float f6 = this.updateRotations(entityIn.getMovementOffsets(5, this.partialTicks)[0] - entityIn.getMovementOffsets(10, this.partialTicks)[0]);
      float f7 = this.updateRotations(entityIn.getMovementOffsets(5, this.partialTicks)[0] + (double)(f6 / 2.0F));
      float f8 = f * ((float)Math.PI * 2F);

      for(int i = 0; i < 5; ++i) {
         double[] adouble1 = entityIn.getMovementOffsets(5 - i, this.partialTicks);
         float f9 = (float)Math.cos((double)((float)i * 0.45F + f8)) * 0.15F;
         this.spine.rotateAngleY = this.updateRotations(adouble1[0] - adouble[0]) * ((float)Math.PI / 180F) * 1.5F;
         this.spine.rotateAngleX = f9 + entityIn.getHeadPartYOffset(i, adouble, adouble1) * ((float)Math.PI / 180F) * 1.5F * 5.0F;
         this.spine.rotateAngleZ = -this.updateRotations(adouble1[0] - (double)f7) * ((float)Math.PI / 180F) * 1.5F;
         this.spine.rotationPointY = f3;
         this.spine.rotationPointZ = f4;
         this.spine.rotationPointX = f2;
         f3 = (float)((double)f3 + Math.sin((double)this.spine.rotateAngleX) * 10.0D);
         f4 = (float)((double)f4 - Math.cos((double)this.spine.rotateAngleY) * Math.cos((double)this.spine.rotateAngleX) * 10.0D);
         f2 = (float)((double)f2 - Math.sin((double)this.spine.rotateAngleY) * Math.cos((double)this.spine.rotateAngleX) * 10.0D);
         this.spine.render(scale);
      }

      this.field_78221_a.rotationPointY = f3;
      this.field_78221_a.rotationPointZ = f4;
      this.field_78221_a.rotationPointX = f2;
      double[] adouble2 = entityIn.getMovementOffsets(0, this.partialTicks);
      this.field_78221_a.rotateAngleY = this.updateRotations(adouble2[0] - adouble[0]) * ((float)Math.PI / 180F);
      this.field_78221_a.rotateAngleX = this.updateRotations((double)entityIn.getHeadPartYOffset(6, adouble, adouble2)) * ((float)Math.PI / 180F) * 1.5F * 5.0F;
      this.field_78221_a.rotateAngleZ = -this.updateRotations(adouble2[0] - (double)f7) * ((float)Math.PI / 180F);
      this.field_78221_a.render(scale);
      GlStateManager.pushMatrix();
      GlStateManager.translatef(0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(-f6 * 1.5F, 0.0F, 0.0F, 1.0F);
      GlStateManager.translatef(0.0F, -1.0F, 0.0F);
      this.field_78217_d.rotateAngleZ = 0.0F;
      this.field_78217_d.render(scale);

      for(int j = 0; j < 2; ++j) {
         GlStateManager.enableCull();
         float f11 = f * ((float)Math.PI * 2F);
         this.field_78225_k.rotateAngleX = 0.125F - (float)Math.cos((double)f11) * 0.2F;
         this.field_78225_k.rotateAngleY = 0.25F;
         this.field_78225_k.rotateAngleZ = (float)(Math.sin((double)f11) + 0.125D) * 0.8F;
         this.field_78222_l.rotateAngleZ = -((float)(Math.sin((double)(f11 + 2.0F)) + 0.5D)) * 0.75F;
         this.field_78218_e.rotateAngleX = 1.0F + f1 * 0.1F;
         this.field_78216_g.rotateAngleX = 0.5F + f1 * 0.1F;
         this.field_78227_i.rotateAngleX = 0.75F + f1 * 0.1F;
         this.field_78215_f.rotateAngleX = 1.3F + f1 * 0.1F;
         this.field_78226_h.rotateAngleX = -0.5F - f1 * 0.1F;
         this.field_78224_j.rotateAngleX = 0.75F + f1 * 0.1F;
         this.field_78225_k.render(scale);
         this.field_78215_f.render(scale);
         this.field_78218_e.render(scale);
         GlStateManager.scalef(-1.0F, 1.0F, 1.0F);
         if (j == 0) {
            GlStateManager.cullFace(GlStateManager.CullFace.FRONT);
         }
      }

      GlStateManager.popMatrix();
      GlStateManager.cullFace(GlStateManager.CullFace.BACK);
      GlStateManager.disableCull();
      float f10 = -((float)Math.sin((double)(f * ((float)Math.PI * 2F)))) * 0.0F;
      f8 = f * ((float)Math.PI * 2F);
      f3 = 10.0F;
      f4 = 60.0F;
      f2 = 0.0F;
      adouble = entityIn.getMovementOffsets(11, this.partialTicks);

      for(int k = 0; k < 12; ++k) {
         adouble2 = entityIn.getMovementOffsets(12 + k, this.partialTicks);
         f10 = (float)((double)f10 + Math.sin((double)((float)k * 0.45F + f8)) * (double)0.05F);
         this.spine.rotateAngleY = (this.updateRotations(adouble2[0] - adouble[0]) * 1.5F + 180.0F) * ((float)Math.PI / 180F);
         this.spine.rotateAngleX = f10 + (float)(adouble2[1] - adouble[1]) * ((float)Math.PI / 180F) * 1.5F * 5.0F;
         this.spine.rotateAngleZ = this.updateRotations(adouble2[0] - (double)f7) * ((float)Math.PI / 180F) * 1.5F;
         this.spine.rotationPointY = f3;
         this.spine.rotationPointZ = f4;
         this.spine.rotationPointX = f2;
         f3 = (float)((double)f3 + Math.sin((double)this.spine.rotateAngleX) * 10.0D);
         f4 = (float)((double)f4 - Math.cos((double)this.spine.rotateAngleY) * Math.cos((double)this.spine.rotateAngleX) * 10.0D);
         f2 = (float)((double)f2 - Math.sin((double)this.spine.rotateAngleY) * Math.cos((double)this.spine.rotateAngleX) * 10.0D);
         this.spine.render(scale);
      }

      GlStateManager.popMatrix();
   }

   /**
    * Updates the rotations in the parameters for rotations greater than 180 degrees or less than -180 degrees. It adds
    * or subtracts 360 degrees, so that the appearance is the same, although the numbers are then simplified to range -
    * 180 to 180
    */
   private float updateRotations(double p_78214_1_) {
      while(p_78214_1_ >= 180.0D) {
         p_78214_1_ -= 360.0D;
      }

      while(p_78214_1_ < -180.0D) {
         p_78214_1_ += 360.0D;
      }

      return (float)p_78214_1_;
   }
}