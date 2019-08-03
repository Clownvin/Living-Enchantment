package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CodModel<T extends Entity> extends EntityModel<T> {
   private final RendererModel field_203723_a;
   private final RendererModel field_203724_b;
   private final RendererModel field_203725_c;
   private final RendererModel field_203726_d;
   private final RendererModel field_203727_e;
   private final RendererModel field_203728_f;
   private final RendererModel field_203729_g;

   public CodModel() {
      this.textureWidth = 32;
      this.textureHeight = 32;
      int i = 22;
      this.field_203723_a = new RendererModel(this, 0, 0);
      this.field_203723_a.addBox(-1.0F, -2.0F, 0.0F, 2, 4, 7);
      this.field_203723_a.setRotationPoint(0.0F, 22.0F, 0.0F);
      this.field_203725_c = new RendererModel(this, 11, 0);
      this.field_203725_c.addBox(-1.0F, -2.0F, -3.0F, 2, 4, 3);
      this.field_203725_c.setRotationPoint(0.0F, 22.0F, 0.0F);
      this.field_203726_d = new RendererModel(this, 0, 0);
      this.field_203726_d.addBox(-1.0F, -2.0F, -1.0F, 2, 3, 1);
      this.field_203726_d.setRotationPoint(0.0F, 22.0F, -3.0F);
      this.field_203727_e = new RendererModel(this, 22, 1);
      this.field_203727_e.addBox(-2.0F, 0.0F, -1.0F, 2, 0, 2);
      this.field_203727_e.setRotationPoint(-1.0F, 23.0F, 0.0F);
      this.field_203727_e.rotateAngleZ = (-(float)Math.PI / 4F);
      this.field_203728_f = new RendererModel(this, 22, 4);
      this.field_203728_f.addBox(0.0F, 0.0F, -1.0F, 2, 0, 2);
      this.field_203728_f.setRotationPoint(1.0F, 23.0F, 0.0F);
      this.field_203728_f.rotateAngleZ = ((float)Math.PI / 4F);
      this.field_203729_g = new RendererModel(this, 22, 3);
      this.field_203729_g.addBox(0.0F, -2.0F, 0.0F, 0, 4, 4);
      this.field_203729_g.setRotationPoint(0.0F, 22.0F, 7.0F);
      this.field_203724_b = new RendererModel(this, 20, -6);
      this.field_203724_b.addBox(0.0F, -1.0F, -1.0F, 0, 1, 6);
      this.field_203724_b.setRotationPoint(0.0F, 20.0F, 0.0F);
   }

   /**
    * Sets the models various rotation angles then renders the model.
    */
   public void render(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      this.field_203723_a.render(scale);
      this.field_203725_c.render(scale);
      this.field_203726_d.render(scale);
      this.field_203727_e.render(scale);
      this.field_203728_f.render(scale);
      this.field_203729_g.render(scale);
      this.field_203724_b.render(scale);
   }

   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
      float f = 1.0F;
      if (!entityIn.isInWater()) {
         f = 1.5F;
      }

      this.field_203729_g.rotateAngleY = -f * 0.45F * MathHelper.sin(0.6F * ageInTicks);
   }
}