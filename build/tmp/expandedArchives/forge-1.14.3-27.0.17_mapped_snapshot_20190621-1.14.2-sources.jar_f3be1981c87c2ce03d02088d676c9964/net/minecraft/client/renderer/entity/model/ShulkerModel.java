package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.monster.ShulkerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ShulkerModel<T extends ShulkerEntity> extends EntityModel<T> {
   private final RendererModel base;
   private final RendererModel lid;
   private final RendererModel head;

   public ShulkerModel() {
      this.textureHeight = 64;
      this.textureWidth = 64;
      this.lid = new RendererModel(this);
      this.base = new RendererModel(this);
      this.head = new RendererModel(this);
      this.lid.setTextureOffset(0, 0).addBox(-8.0F, -16.0F, -8.0F, 16, 12, 16);
      this.lid.setRotationPoint(0.0F, 24.0F, 0.0F);
      this.base.setTextureOffset(0, 28).addBox(-8.0F, -8.0F, -8.0F, 16, 8, 16);
      this.base.setRotationPoint(0.0F, 24.0F, 0.0F);
      this.head.setTextureOffset(0, 52).addBox(-3.0F, 0.0F, -3.0F, 6, 6, 6);
      this.head.setRotationPoint(0.0F, 12.0F, 0.0F);
   }

   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
      float f = ageInTicks - (float)entityIn.ticksExisted;
      float f1 = (0.5F + entityIn.getClientPeekAmount(f)) * (float)Math.PI;
      float f2 = -1.0F + MathHelper.sin(f1);
      float f3 = 0.0F;
      if (f1 > (float)Math.PI) {
         f3 = MathHelper.sin(ageInTicks * 0.1F) * 0.7F;
      }

      this.lid.setRotationPoint(0.0F, 16.0F + MathHelper.sin(f1) * 8.0F + f3, 0.0F);
      if (entityIn.getClientPeekAmount(f) > 0.3F) {
         this.lid.rotateAngleY = f2 * f2 * f2 * f2 * (float)Math.PI * 0.125F;
      } else {
         this.lid.rotateAngleY = 0.0F;
      }

      this.head.rotateAngleX = headPitch * ((float)Math.PI / 180F);
      this.head.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
   }

   /**
    * Sets the models various rotation angles then renders the model.
    */
   public void render(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.base.render(scale);
      this.lid.render(scale);
   }

   public RendererModel getBase() {
      return this.base;
   }

   public RendererModel getLid() {
      return this.lid;
   }

   public RendererModel getHead() {
      return this.head;
   }
}