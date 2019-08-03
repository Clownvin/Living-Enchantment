package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SnowManModel<T extends Entity> extends EntityModel<T> {
   private final RendererModel body;
   private final RendererModel bottomBody;
   private final RendererModel head;
   private final RendererModel rightHand;
   private final RendererModel leftHand;

   public SnowManModel() {
      float f = 4.0F;
      float f1 = 0.0F;
      this.head = (new RendererModel(this, 0, 0)).setTextureSize(64, 64);
      this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, -0.5F);
      this.head.setRotationPoint(0.0F, 4.0F, 0.0F);
      this.rightHand = (new RendererModel(this, 32, 0)).setTextureSize(64, 64);
      this.rightHand.addBox(-1.0F, 0.0F, -1.0F, 12, 2, 2, -0.5F);
      this.rightHand.setRotationPoint(0.0F, 6.0F, 0.0F);
      this.leftHand = (new RendererModel(this, 32, 0)).setTextureSize(64, 64);
      this.leftHand.addBox(-1.0F, 0.0F, -1.0F, 12, 2, 2, -0.5F);
      this.leftHand.setRotationPoint(0.0F, 6.0F, 0.0F);
      this.body = (new RendererModel(this, 0, 16)).setTextureSize(64, 64);
      this.body.addBox(-5.0F, -10.0F, -5.0F, 10, 10, 10, -0.5F);
      this.body.setRotationPoint(0.0F, 13.0F, 0.0F);
      this.bottomBody = (new RendererModel(this, 0, 36)).setTextureSize(64, 64);
      this.bottomBody.addBox(-6.0F, -12.0F, -6.0F, 12, 12, 12, -0.5F);
      this.bottomBody.setRotationPoint(0.0F, 24.0F, 0.0F);
   }

   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
      super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
      this.head.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
      this.head.rotateAngleX = headPitch * ((float)Math.PI / 180F);
      this.body.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F) * 0.25F;
      float f = MathHelper.sin(this.body.rotateAngleY);
      float f1 = MathHelper.cos(this.body.rotateAngleY);
      this.rightHand.rotateAngleZ = 1.0F;
      this.leftHand.rotateAngleZ = -1.0F;
      this.rightHand.rotateAngleY = 0.0F + this.body.rotateAngleY;
      this.leftHand.rotateAngleY = (float)Math.PI + this.body.rotateAngleY;
      this.rightHand.rotationPointX = f1 * 5.0F;
      this.rightHand.rotationPointZ = -f * 5.0F;
      this.leftHand.rotationPointX = -f1 * 5.0F;
      this.leftHand.rotationPointZ = f * 5.0F;
   }

   /**
    * Sets the models various rotation angles then renders the model.
    */
   public void render(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      this.body.render(scale);
      this.bottomBody.render(scale);
      this.head.render(scale);
      this.rightHand.render(scale);
      this.leftHand.render(scale);
   }

   public RendererModel func_205070_a() {
      return this.head;
   }
}