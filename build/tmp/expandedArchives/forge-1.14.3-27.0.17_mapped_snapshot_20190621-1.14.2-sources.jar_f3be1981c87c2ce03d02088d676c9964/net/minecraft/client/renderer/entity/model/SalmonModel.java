package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SalmonModel<T extends Entity> extends EntityModel<T> {
   private final RendererModel field_203761_a;
   private final RendererModel field_203762_b;
   private final RendererModel field_203763_c;
   private final RendererModel field_203764_d;
   private final RendererModel field_203765_e;
   private final RendererModel field_203766_f;
   private final RendererModel field_203767_g;
   private final RendererModel field_203768_h;

   public SalmonModel() {
      this.textureWidth = 32;
      this.textureHeight = 32;
      int i = 20;
      this.field_203761_a = new RendererModel(this, 0, 0);
      this.field_203761_a.addBox(-1.5F, -2.5F, 0.0F, 3, 5, 8);
      this.field_203761_a.setRotationPoint(0.0F, 20.0F, 0.0F);
      this.field_203762_b = new RendererModel(this, 0, 13);
      this.field_203762_b.addBox(-1.5F, -2.5F, 0.0F, 3, 5, 8);
      this.field_203762_b.setRotationPoint(0.0F, 20.0F, 8.0F);
      this.field_203763_c = new RendererModel(this, 22, 0);
      this.field_203763_c.addBox(-1.0F, -2.0F, -3.0F, 2, 4, 3);
      this.field_203763_c.setRotationPoint(0.0F, 20.0F, 0.0F);
      this.field_203766_f = new RendererModel(this, 20, 10);
      this.field_203766_f.addBox(0.0F, -2.5F, 0.0F, 0, 5, 6);
      this.field_203766_f.setRotationPoint(0.0F, 0.0F, 8.0F);
      this.field_203762_b.addChild(this.field_203766_f);
      this.field_203764_d = new RendererModel(this, 2, 1);
      this.field_203764_d.addBox(0.0F, 0.0F, 0.0F, 0, 2, 3);
      this.field_203764_d.setRotationPoint(0.0F, -4.5F, 5.0F);
      this.field_203761_a.addChild(this.field_203764_d);
      this.field_203765_e = new RendererModel(this, 0, 2);
      this.field_203765_e.addBox(0.0F, 0.0F, 0.0F, 0, 2, 4);
      this.field_203765_e.setRotationPoint(0.0F, -4.5F, -1.0F);
      this.field_203762_b.addChild(this.field_203765_e);
      this.field_203767_g = new RendererModel(this, -4, 0);
      this.field_203767_g.addBox(-2.0F, 0.0F, 0.0F, 2, 0, 2);
      this.field_203767_g.setRotationPoint(-1.5F, 21.5F, 0.0F);
      this.field_203767_g.rotateAngleZ = (-(float)Math.PI / 4F);
      this.field_203768_h = new RendererModel(this, 0, 0);
      this.field_203768_h.addBox(0.0F, 0.0F, 0.0F, 2, 0, 2);
      this.field_203768_h.setRotationPoint(1.5F, 21.5F, 0.0F);
      this.field_203768_h.rotateAngleZ = ((float)Math.PI / 4F);
   }

   /**
    * Sets the models various rotation angles then renders the model.
    */
   public void render(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      this.field_203761_a.render(scale);
      this.field_203762_b.render(scale);
      this.field_203763_c.render(scale);
      this.field_203767_g.render(scale);
      this.field_203768_h.render(scale);
   }

   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
      float f = 1.0F;
      float f1 = 1.0F;
      if (!entityIn.isInWater()) {
         f = 1.3F;
         f1 = 1.7F;
      }

      this.field_203762_b.rotateAngleY = -f * 0.25F * MathHelper.sin(f1 * 0.6F * ageInTicks);
   }
}