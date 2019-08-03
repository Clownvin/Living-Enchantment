package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TropicalFishBModel<T extends Entity> extends EntityModel<T> {
   private final RendererModel field_204240_a;
   private final RendererModel field_204241_b;
   private final RendererModel field_204242_c;
   private final RendererModel field_204243_d;
   private final RendererModel field_204244_e;
   private final RendererModel field_204245_f;

   public TropicalFishBModel() {
      this(0.0F);
   }

   public TropicalFishBModel(float p_i48891_1_) {
      this.textureWidth = 32;
      this.textureHeight = 32;
      int i = 19;
      this.field_204240_a = new RendererModel(this, 0, 20);
      this.field_204240_a.addBox(-1.0F, -3.0F, -3.0F, 2, 6, 6, p_i48891_1_);
      this.field_204240_a.setRotationPoint(0.0F, 19.0F, 0.0F);
      this.field_204241_b = new RendererModel(this, 21, 16);
      this.field_204241_b.addBox(0.0F, -3.0F, 0.0F, 0, 6, 5, p_i48891_1_);
      this.field_204241_b.setRotationPoint(0.0F, 19.0F, 3.0F);
      this.field_204242_c = new RendererModel(this, 2, 16);
      this.field_204242_c.addBox(-2.0F, 0.0F, 0.0F, 2, 2, 0, p_i48891_1_);
      this.field_204242_c.setRotationPoint(-1.0F, 20.0F, 0.0F);
      this.field_204242_c.rotateAngleY = ((float)Math.PI / 4F);
      this.field_204243_d = new RendererModel(this, 2, 12);
      this.field_204243_d.addBox(0.0F, 0.0F, 0.0F, 2, 2, 0, p_i48891_1_);
      this.field_204243_d.setRotationPoint(1.0F, 20.0F, 0.0F);
      this.field_204243_d.rotateAngleY = (-(float)Math.PI / 4F);
      this.field_204244_e = new RendererModel(this, 20, 11);
      this.field_204244_e.addBox(0.0F, -4.0F, 0.0F, 0, 4, 6, p_i48891_1_);
      this.field_204244_e.setRotationPoint(0.0F, 16.0F, -3.0F);
      this.field_204245_f = new RendererModel(this, 20, 21);
      this.field_204245_f.addBox(0.0F, 0.0F, 0.0F, 0, 4, 6, p_i48891_1_);
      this.field_204245_f.setRotationPoint(0.0F, 22.0F, -3.0F);
   }

   /**
    * Sets the models various rotation angles then renders the model.
    */
   public void render(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      this.field_204240_a.render(scale);
      this.field_204241_b.render(scale);
      this.field_204242_c.render(scale);
      this.field_204243_d.render(scale);
      this.field_204244_e.render(scale);
      this.field_204245_f.render(scale);
   }

   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
      float f = 1.0F;
      if (!entityIn.isInWater()) {
         f = 1.5F;
      }

      this.field_204241_b.rotateAngleY = -f * 0.45F * MathHelper.sin(0.6F * ageInTicks);
   }
}