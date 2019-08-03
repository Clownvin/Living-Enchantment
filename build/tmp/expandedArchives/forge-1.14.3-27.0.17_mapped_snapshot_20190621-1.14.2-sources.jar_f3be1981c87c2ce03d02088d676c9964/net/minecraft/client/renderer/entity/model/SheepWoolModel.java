package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.passive.SheepEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SheepWoolModel<T extends SheepEntity> extends QuadrupedModel<T> {
   private float headRotationAngleX;

   public SheepWoolModel() {
      super(12, 0.0F);
      this.headModel = new RendererModel(this, 0, 0);
      this.headModel.addBox(-3.0F, -4.0F, -4.0F, 6, 6, 6, 0.6F);
      this.headModel.setRotationPoint(0.0F, 6.0F, -8.0F);
      this.field_78148_b = new RendererModel(this, 28, 8);
      this.field_78148_b.addBox(-4.0F, -10.0F, -7.0F, 8, 16, 6, 1.75F);
      this.field_78148_b.setRotationPoint(0.0F, 5.0F, 2.0F);
      float f = 0.5F;
      this.field_78149_c = new RendererModel(this, 0, 16);
      this.field_78149_c.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.5F);
      this.field_78149_c.setRotationPoint(-3.0F, 12.0F, 7.0F);
      this.field_78146_d = new RendererModel(this, 0, 16);
      this.field_78146_d.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.5F);
      this.field_78146_d.setRotationPoint(3.0F, 12.0F, 7.0F);
      this.field_78147_e = new RendererModel(this, 0, 16);
      this.field_78147_e.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.5F);
      this.field_78147_e.setRotationPoint(-3.0F, 12.0F, -5.0F);
      this.field_78144_f = new RendererModel(this, 0, 16);
      this.field_78144_f.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, 0.5F);
      this.field_78144_f.setRotationPoint(3.0F, 12.0F, -5.0F);
   }

   public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
      super.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);
      this.headModel.rotationPointY = 6.0F + entityIn.getHeadRotationPointY(partialTick) * 9.0F;
      this.headRotationAngleX = entityIn.getHeadRotationAngleX(partialTick);
   }

   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
      super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
      this.headModel.rotateAngleX = this.headRotationAngleX;
   }
}