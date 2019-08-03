package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PufferFishSmallModel<T extends Entity> extends EntityModel<T> {
   private final RendererModel field_203754_a;
   private final RendererModel field_203755_b;
   private final RendererModel field_203756_c;
   private final RendererModel field_203757_d;
   private final RendererModel field_203758_e;
   private final RendererModel field_203759_f;

   public PufferFishSmallModel() {
      this.textureWidth = 32;
      this.textureHeight = 32;
      int i = 23;
      this.field_203754_a = new RendererModel(this, 0, 27);
      this.field_203754_a.addBox(-1.5F, -2.0F, -1.5F, 3, 2, 3);
      this.field_203754_a.setRotationPoint(0.0F, 23.0F, 0.0F);
      this.field_203755_b = new RendererModel(this, 24, 6);
      this.field_203755_b.addBox(-1.5F, 0.0F, -1.5F, 1, 1, 1);
      this.field_203755_b.setRotationPoint(0.0F, 20.0F, 0.0F);
      this.field_203756_c = new RendererModel(this, 28, 6);
      this.field_203756_c.addBox(0.5F, 0.0F, -1.5F, 1, 1, 1);
      this.field_203756_c.setRotationPoint(0.0F, 20.0F, 0.0F);
      this.field_203759_f = new RendererModel(this, -3, 0);
      this.field_203759_f.addBox(-1.5F, 0.0F, 0.0F, 3, 0, 3);
      this.field_203759_f.setRotationPoint(0.0F, 22.0F, 1.5F);
      this.field_203757_d = new RendererModel(this, 25, 0);
      this.field_203757_d.addBox(-1.0F, 0.0F, 0.0F, 1, 0, 2);
      this.field_203757_d.setRotationPoint(-1.5F, 22.0F, -1.5F);
      this.field_203758_e = new RendererModel(this, 25, 0);
      this.field_203758_e.addBox(0.0F, 0.0F, 0.0F, 1, 0, 2);
      this.field_203758_e.setRotationPoint(1.5F, 22.0F, -1.5F);
   }

   /**
    * Sets the models various rotation angles then renders the model.
    */
   public void render(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      this.field_203754_a.render(scale);
      this.field_203755_b.render(scale);
      this.field_203756_c.render(scale);
      this.field_203759_f.render(scale);
      this.field_203757_d.render(scale);
      this.field_203758_e.render(scale);
   }

   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
      this.field_203757_d.rotateAngleZ = -0.2F + 0.4F * MathHelper.sin(ageInTicks * 0.2F);
      this.field_203758_e.rotateAngleZ = 0.2F - 0.4F * MathHelper.sin(ageInTicks * 0.2F);
   }
}