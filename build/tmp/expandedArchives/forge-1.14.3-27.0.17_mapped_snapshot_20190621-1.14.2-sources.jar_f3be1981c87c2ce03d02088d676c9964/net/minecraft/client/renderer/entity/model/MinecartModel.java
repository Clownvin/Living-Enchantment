package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MinecartModel<T extends Entity> extends EntityModel<T> {
   private final RendererModel[] field_78154_a = new RendererModel[7];

   public MinecartModel() {
      this.field_78154_a[0] = new RendererModel(this, 0, 10);
      this.field_78154_a[1] = new RendererModel(this, 0, 0);
      this.field_78154_a[2] = new RendererModel(this, 0, 0);
      this.field_78154_a[3] = new RendererModel(this, 0, 0);
      this.field_78154_a[4] = new RendererModel(this, 0, 0);
      this.field_78154_a[5] = new RendererModel(this, 44, 10);
      int i = 20;
      int j = 8;
      int k = 16;
      int l = 4;
      this.field_78154_a[0].addBox(-10.0F, -8.0F, -1.0F, 20, 16, 2, 0.0F);
      this.field_78154_a[0].setRotationPoint(0.0F, 4.0F, 0.0F);
      this.field_78154_a[5].addBox(-9.0F, -7.0F, -1.0F, 18, 14, 1, 0.0F);
      this.field_78154_a[5].setRotationPoint(0.0F, 4.0F, 0.0F);
      this.field_78154_a[1].addBox(-8.0F, -9.0F, -1.0F, 16, 8, 2, 0.0F);
      this.field_78154_a[1].setRotationPoint(-9.0F, 4.0F, 0.0F);
      this.field_78154_a[2].addBox(-8.0F, -9.0F, -1.0F, 16, 8, 2, 0.0F);
      this.field_78154_a[2].setRotationPoint(9.0F, 4.0F, 0.0F);
      this.field_78154_a[3].addBox(-8.0F, -9.0F, -1.0F, 16, 8, 2, 0.0F);
      this.field_78154_a[3].setRotationPoint(0.0F, 4.0F, -7.0F);
      this.field_78154_a[4].addBox(-8.0F, -9.0F, -1.0F, 16, 8, 2, 0.0F);
      this.field_78154_a[4].setRotationPoint(0.0F, 4.0F, 7.0F);
      this.field_78154_a[0].rotateAngleX = ((float)Math.PI / 2F);
      this.field_78154_a[1].rotateAngleY = ((float)Math.PI * 1.5F);
      this.field_78154_a[2].rotateAngleY = ((float)Math.PI / 2F);
      this.field_78154_a[3].rotateAngleY = (float)Math.PI;
      this.field_78154_a[5].rotateAngleX = (-(float)Math.PI / 2F);
   }

   /**
    * Sets the models various rotation angles then renders the model.
    */
   public void render(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.field_78154_a[5].rotationPointY = 4.0F - ageInTicks;

      for(int i = 0; i < 6; ++i) {
         this.field_78154_a[i].render(scale);
      }

   }
}