package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SquidModel<T extends Entity> extends EntityModel<T> {
   private final RendererModel field_78202_a;
   private final RendererModel[] field_78201_b = new RendererModel[8];

   public SquidModel() {
      int i = -16;
      this.field_78202_a = new RendererModel(this, 0, 0);
      this.field_78202_a.addBox(-6.0F, -8.0F, -6.0F, 12, 16, 12);
      this.field_78202_a.rotationPointY += 8.0F;

      for(int j = 0; j < this.field_78201_b.length; ++j) {
         this.field_78201_b[j] = new RendererModel(this, 48, 0);
         double d0 = (double)j * Math.PI * 2.0D / (double)this.field_78201_b.length;
         float f = (float)Math.cos(d0) * 5.0F;
         float f1 = (float)Math.sin(d0) * 5.0F;
         this.field_78201_b[j].addBox(-1.0F, 0.0F, -1.0F, 2, 18, 2);
         this.field_78201_b[j].rotationPointX = f;
         this.field_78201_b[j].rotationPointZ = f1;
         this.field_78201_b[j].rotationPointY = 15.0F;
         d0 = (double)j * Math.PI * -2.0D / (double)this.field_78201_b.length + (Math.PI / 2D);
         this.field_78201_b[j].rotateAngleY = (float)d0;
      }

   }

   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
      for(RendererModel renderermodel : this.field_78201_b) {
         renderermodel.rotateAngleX = ageInTicks;
      }

   }

   /**
    * Sets the models various rotation angles then renders the model.
    */
   public void render(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      this.field_78202_a.render(scale);

      for(RendererModel renderermodel : this.field_78201_b) {
         renderermodel.render(scale);
      }

   }
}