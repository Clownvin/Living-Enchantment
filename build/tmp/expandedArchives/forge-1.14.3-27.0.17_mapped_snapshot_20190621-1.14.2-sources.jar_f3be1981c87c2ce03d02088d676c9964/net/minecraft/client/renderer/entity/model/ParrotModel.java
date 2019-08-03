package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParrotModel extends EntityModel<ParrotEntity> {
   private final RendererModel body;
   private final RendererModel tail;
   private final RendererModel wingLeft;
   private final RendererModel wingRight;
   private final RendererModel head;
   private final RendererModel head2;
   private final RendererModel beak1;
   private final RendererModel beak2;
   private final RendererModel feather;
   private final RendererModel legLeft;
   private final RendererModel legRight;

   public ParrotModel() {
      this.textureWidth = 32;
      this.textureHeight = 32;
      this.body = new RendererModel(this, 2, 8);
      this.body.addBox(-1.5F, 0.0F, -1.5F, 3, 6, 3);
      this.body.setRotationPoint(0.0F, 16.5F, -3.0F);
      this.tail = new RendererModel(this, 22, 1);
      this.tail.addBox(-1.5F, -1.0F, -1.0F, 3, 4, 1);
      this.tail.setRotationPoint(0.0F, 21.07F, 1.16F);
      this.wingLeft = new RendererModel(this, 19, 8);
      this.wingLeft.addBox(-0.5F, 0.0F, -1.5F, 1, 5, 3);
      this.wingLeft.setRotationPoint(1.5F, 16.94F, -2.76F);
      this.wingRight = new RendererModel(this, 19, 8);
      this.wingRight.addBox(-0.5F, 0.0F, -1.5F, 1, 5, 3);
      this.wingRight.setRotationPoint(-1.5F, 16.94F, -2.76F);
      this.head = new RendererModel(this, 2, 2);
      this.head.addBox(-1.0F, -1.5F, -1.0F, 2, 3, 2);
      this.head.setRotationPoint(0.0F, 15.69F, -2.76F);
      this.head2 = new RendererModel(this, 10, 0);
      this.head2.addBox(-1.0F, -0.5F, -2.0F, 2, 1, 4);
      this.head2.setRotationPoint(0.0F, -2.0F, -1.0F);
      this.head.addChild(this.head2);
      this.beak1 = new RendererModel(this, 11, 7);
      this.beak1.addBox(-0.5F, -1.0F, -0.5F, 1, 2, 1);
      this.beak1.setRotationPoint(0.0F, -0.5F, -1.5F);
      this.head.addChild(this.beak1);
      this.beak2 = new RendererModel(this, 16, 7);
      this.beak2.addBox(-0.5F, 0.0F, -0.5F, 1, 2, 1);
      this.beak2.setRotationPoint(0.0F, -1.75F, -2.45F);
      this.head.addChild(this.beak2);
      this.feather = new RendererModel(this, 2, 18);
      this.feather.addBox(0.0F, -4.0F, -2.0F, 0, 5, 4);
      this.feather.setRotationPoint(0.0F, -2.15F, 0.15F);
      this.head.addChild(this.feather);
      this.legLeft = new RendererModel(this, 14, 18);
      this.legLeft.addBox(-0.5F, 0.0F, -0.5F, 1, 2, 1);
      this.legLeft.setRotationPoint(1.0F, 22.0F, -1.05F);
      this.legRight = new RendererModel(this, 14, 18);
      this.legRight.addBox(-0.5F, 0.0F, -0.5F, 1, 2, 1);
      this.legRight.setRotationPoint(-1.0F, 22.0F, -1.05F);
   }

   /**
    * Sets the models various rotation angles then renders the model.
    */
   public void render(ParrotEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.func_217159_a(scale);
   }

   public void setRotationAngles(ParrotEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
      this.func_217162_a(func_217158_a(entityIn), entityIn.ticksExisted, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
   }

   public void setLivingAnimations(ParrotEntity entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
      this.func_217160_a(func_217158_a(entityIn));
   }

   public void func_217161_a(float p_217161_1_, float p_217161_2_, float p_217161_3_, float p_217161_4_, float p_217161_5_, int p_217161_6_) {
      this.func_217160_a(ParrotModel.State.ON_SHOULDER);
      this.func_217162_a(ParrotModel.State.ON_SHOULDER, p_217161_6_, p_217161_1_, p_217161_2_, 0.0F, p_217161_3_, p_217161_4_);
      this.func_217159_a(p_217161_5_);
   }

   private void func_217159_a(float p_217159_1_) {
      this.body.render(p_217159_1_);
      this.wingLeft.render(p_217159_1_);
      this.wingRight.render(p_217159_1_);
      this.tail.render(p_217159_1_);
      this.head.render(p_217159_1_);
      this.legLeft.render(p_217159_1_);
      this.legRight.render(p_217159_1_);
   }

   private void func_217162_a(ParrotModel.State p_217162_1_, int p_217162_2_, float p_217162_3_, float p_217162_4_, float p_217162_5_, float p_217162_6_, float p_217162_7_) {
      this.head.rotateAngleX = p_217162_7_ * ((float)Math.PI / 180F);
      this.head.rotateAngleY = p_217162_6_ * ((float)Math.PI / 180F);
      this.head.rotateAngleZ = 0.0F;
      this.head.rotationPointX = 0.0F;
      this.body.rotationPointX = 0.0F;
      this.tail.rotationPointX = 0.0F;
      this.wingRight.rotationPointX = -1.5F;
      this.wingLeft.rotationPointX = 1.5F;
      switch(p_217162_1_) {
      case SITTING:
         break;
      case PARTY:
         float f = MathHelper.cos((float)p_217162_2_);
         float f1 = MathHelper.sin((float)p_217162_2_);
         this.head.rotationPointX = f;
         this.head.rotationPointY = 15.69F + f1;
         this.head.rotateAngleX = 0.0F;
         this.head.rotateAngleY = 0.0F;
         this.head.rotateAngleZ = MathHelper.sin((float)p_217162_2_) * 0.4F;
         this.body.rotationPointX = f;
         this.body.rotationPointY = 16.5F + f1;
         this.wingLeft.rotateAngleZ = -0.0873F - p_217162_5_;
         this.wingLeft.rotationPointX = 1.5F + f;
         this.wingLeft.rotationPointY = 16.94F + f1;
         this.wingRight.rotateAngleZ = 0.0873F + p_217162_5_;
         this.wingRight.rotationPointX = -1.5F + f;
         this.wingRight.rotationPointY = 16.94F + f1;
         this.tail.rotationPointX = f;
         this.tail.rotationPointY = 21.07F + f1;
         break;
      case STANDING:
         this.legLeft.rotateAngleX += MathHelper.cos(p_217162_3_ * 0.6662F) * 1.4F * p_217162_4_;
         this.legRight.rotateAngleX += MathHelper.cos(p_217162_3_ * 0.6662F + (float)Math.PI) * 1.4F * p_217162_4_;
      case FLYING:
      case ON_SHOULDER:
      default:
         float f2 = p_217162_5_ * 0.3F;
         this.head.rotationPointY = 15.69F + f2;
         this.tail.rotateAngleX = 1.015F + MathHelper.cos(p_217162_3_ * 0.6662F) * 0.3F * p_217162_4_;
         this.tail.rotationPointY = 21.07F + f2;
         this.body.rotationPointY = 16.5F + f2;
         this.wingLeft.rotateAngleZ = -0.0873F - p_217162_5_;
         this.wingLeft.rotationPointY = 16.94F + f2;
         this.wingRight.rotateAngleZ = 0.0873F + p_217162_5_;
         this.wingRight.rotationPointY = 16.94F + f2;
         this.legLeft.rotationPointY = 22.0F + f2;
         this.legRight.rotationPointY = 22.0F + f2;
      }

   }

   private void func_217160_a(ParrotModel.State p_217160_1_) {
      this.feather.rotateAngleX = -0.2214F;
      this.body.rotateAngleX = 0.4937F;
      this.wingLeft.rotateAngleX = -0.6981F;
      this.wingLeft.rotateAngleY = -(float)Math.PI;
      this.wingRight.rotateAngleX = -0.6981F;
      this.wingRight.rotateAngleY = -(float)Math.PI;
      this.legLeft.rotateAngleX = -0.0299F;
      this.legRight.rotateAngleX = -0.0299F;
      this.legLeft.rotationPointY = 22.0F;
      this.legRight.rotationPointY = 22.0F;
      this.legLeft.rotateAngleZ = 0.0F;
      this.legRight.rotateAngleZ = 0.0F;
      switch(p_217160_1_) {
      case SITTING:
         float f = 1.9F;
         this.head.rotationPointY = 17.59F;
         this.tail.rotateAngleX = 1.5388988F;
         this.tail.rotationPointY = 22.97F;
         this.body.rotationPointY = 18.4F;
         this.wingLeft.rotateAngleZ = -0.0873F;
         this.wingLeft.rotationPointY = 18.84F;
         this.wingRight.rotateAngleZ = 0.0873F;
         this.wingRight.rotationPointY = 18.84F;
         ++this.legLeft.rotationPointY;
         ++this.legRight.rotationPointY;
         ++this.legLeft.rotateAngleX;
         ++this.legRight.rotateAngleX;
         break;
      case PARTY:
         this.legLeft.rotateAngleZ = -0.34906584F;
         this.legRight.rotateAngleZ = 0.34906584F;
      case STANDING:
      case ON_SHOULDER:
      default:
         break;
      case FLYING:
         this.legLeft.rotateAngleX += 0.6981317F;
         this.legRight.rotateAngleX += 0.6981317F;
      }

   }

   private static ParrotModel.State func_217158_a(ParrotEntity p_217158_0_) {
      if (p_217158_0_.isPartying()) {
         return ParrotModel.State.PARTY;
      } else if (p_217158_0_.isSitting()) {
         return ParrotModel.State.SITTING;
      } else {
         return p_217158_0_.isFlying() ? ParrotModel.State.FLYING : ParrotModel.State.STANDING;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum State {
      FLYING,
      STANDING,
      SITTING,
      PARTY,
      ON_SHOULDER;
   }
}