package net.minecraft.client.renderer.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OcelotModel<T extends Entity> extends EntityModel<T> {
   protected final RendererModel ocelotBackLeftLeg;
   protected final RendererModel ocelotBackRightLeg;
   protected final RendererModel ocelotFrontLeftLeg;
   protected final RendererModel ocelotFrontRightLeg;
   protected final RendererModel ocelotTail;
   protected final RendererModel ocelotTail2;
   protected final RendererModel ocelotHead;
   protected final RendererModel ocelotBody;
   protected int state = 1;

   public OcelotModel(float p_i51064_1_) {
      this.ocelotHead = new RendererModel(this, "head");
      this.ocelotHead.func_217178_a("main", -2.5F, -2.0F, -3.0F, 5, 4, 5, p_i51064_1_, 0, 0);
      this.ocelotHead.func_217178_a("nose", -1.5F, 0.0F, -4.0F, 3, 2, 2, p_i51064_1_, 0, 24);
      this.ocelotHead.func_217178_a("ear1", -2.0F, -3.0F, 0.0F, 1, 1, 2, p_i51064_1_, 0, 10);
      this.ocelotHead.func_217178_a("ear2", 1.0F, -3.0F, 0.0F, 1, 1, 2, p_i51064_1_, 6, 10);
      this.ocelotHead.setRotationPoint(0.0F, 15.0F, -9.0F);
      this.ocelotBody = new RendererModel(this, 20, 0);
      this.ocelotBody.addBox(-2.0F, 3.0F, -8.0F, 4, 16, 6, p_i51064_1_);
      this.ocelotBody.setRotationPoint(0.0F, 12.0F, -10.0F);
      this.ocelotTail = new RendererModel(this, 0, 15);
      this.ocelotTail.addBox(-0.5F, 0.0F, 0.0F, 1, 8, 1, p_i51064_1_);
      this.ocelotTail.rotateAngleX = 0.9F;
      this.ocelotTail.setRotationPoint(0.0F, 15.0F, 8.0F);
      this.ocelotTail2 = new RendererModel(this, 4, 15);
      this.ocelotTail2.addBox(-0.5F, 0.0F, 0.0F, 1, 8, 1, p_i51064_1_);
      this.ocelotTail2.setRotationPoint(0.0F, 20.0F, 14.0F);
      this.ocelotBackLeftLeg = new RendererModel(this, 8, 13);
      this.ocelotBackLeftLeg.addBox(-1.0F, 0.0F, 1.0F, 2, 6, 2, p_i51064_1_);
      this.ocelotBackLeftLeg.setRotationPoint(1.1F, 18.0F, 5.0F);
      this.ocelotBackRightLeg = new RendererModel(this, 8, 13);
      this.ocelotBackRightLeg.addBox(-1.0F, 0.0F, 1.0F, 2, 6, 2, p_i51064_1_);
      this.ocelotBackRightLeg.setRotationPoint(-1.1F, 18.0F, 5.0F);
      this.ocelotFrontLeftLeg = new RendererModel(this, 40, 0);
      this.ocelotFrontLeftLeg.addBox(-1.0F, 0.0F, 0.0F, 2, 10, 2, p_i51064_1_);
      this.ocelotFrontLeftLeg.setRotationPoint(1.2F, 14.1F, -5.0F);
      this.ocelotFrontRightLeg = new RendererModel(this, 40, 0);
      this.ocelotFrontRightLeg.addBox(-1.0F, 0.0F, 0.0F, 2, 10, 2, p_i51064_1_);
      this.ocelotFrontRightLeg.setRotationPoint(-1.2F, 14.1F, -5.0F);
   }

   /**
    * Sets the models various rotation angles then renders the model.
    */
   public void render(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      if (this.isChild) {
         float f = 2.0F;
         GlStateManager.pushMatrix();
         GlStateManager.scalef(0.75F, 0.75F, 0.75F);
         GlStateManager.translatef(0.0F, 10.0F * scale, 4.0F * scale);
         this.ocelotHead.render(scale);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         GlStateManager.scalef(0.5F, 0.5F, 0.5F);
         GlStateManager.translatef(0.0F, 24.0F * scale, 0.0F);
         this.ocelotBody.render(scale);
         this.ocelotBackLeftLeg.render(scale);
         this.ocelotBackRightLeg.render(scale);
         this.ocelotFrontLeftLeg.render(scale);
         this.ocelotFrontRightLeg.render(scale);
         this.ocelotTail.render(scale);
         this.ocelotTail2.render(scale);
         GlStateManager.popMatrix();
      } else {
         this.ocelotHead.render(scale);
         this.ocelotBody.render(scale);
         this.ocelotTail.render(scale);
         this.ocelotTail2.render(scale);
         this.ocelotBackLeftLeg.render(scale);
         this.ocelotBackRightLeg.render(scale);
         this.ocelotFrontLeftLeg.render(scale);
         this.ocelotFrontRightLeg.render(scale);
      }

   }

   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
      this.ocelotHead.rotateAngleX = headPitch * ((float)Math.PI / 180F);
      this.ocelotHead.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
      if (this.state != 3) {
         this.ocelotBody.rotateAngleX = ((float)Math.PI / 2F);
         if (this.state == 2) {
            this.ocelotBackLeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * limbSwingAmount;
            this.ocelotBackRightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + 0.3F) * limbSwingAmount;
            this.ocelotFrontLeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI + 0.3F) * limbSwingAmount;
            this.ocelotFrontRightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * limbSwingAmount;
            this.ocelotTail2.rotateAngleX = 1.7278761F + ((float)Math.PI / 10F) * MathHelper.cos(limbSwing) * limbSwingAmount;
         } else {
            this.ocelotBackLeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * limbSwingAmount;
            this.ocelotBackRightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * limbSwingAmount;
            this.ocelotFrontLeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * limbSwingAmount;
            this.ocelotFrontRightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * limbSwingAmount;
            if (this.state == 1) {
               this.ocelotTail2.rotateAngleX = 1.7278761F + ((float)Math.PI / 4F) * MathHelper.cos(limbSwing) * limbSwingAmount;
            } else {
               this.ocelotTail2.rotateAngleX = 1.7278761F + 0.47123894F * MathHelper.cos(limbSwing) * limbSwingAmount;
            }
         }
      }

   }

   public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
      this.ocelotBody.rotationPointY = 12.0F;
      this.ocelotBody.rotationPointZ = -10.0F;
      this.ocelotHead.rotationPointY = 15.0F;
      this.ocelotHead.rotationPointZ = -9.0F;
      this.ocelotTail.rotationPointY = 15.0F;
      this.ocelotTail.rotationPointZ = 8.0F;
      this.ocelotTail2.rotationPointY = 20.0F;
      this.ocelotTail2.rotationPointZ = 14.0F;
      this.ocelotFrontLeftLeg.rotationPointY = 14.1F;
      this.ocelotFrontLeftLeg.rotationPointZ = -5.0F;
      this.ocelotFrontRightLeg.rotationPointY = 14.1F;
      this.ocelotFrontRightLeg.rotationPointZ = -5.0F;
      this.ocelotBackLeftLeg.rotationPointY = 18.0F;
      this.ocelotBackLeftLeg.rotationPointZ = 5.0F;
      this.ocelotBackRightLeg.rotationPointY = 18.0F;
      this.ocelotBackRightLeg.rotationPointZ = 5.0F;
      this.ocelotTail.rotateAngleX = 0.9F;
      if (entityIn.isSneaking()) {
         ++this.ocelotBody.rotationPointY;
         this.ocelotHead.rotationPointY += 2.0F;
         ++this.ocelotTail.rotationPointY;
         this.ocelotTail2.rotationPointY += -4.0F;
         this.ocelotTail2.rotationPointZ += 2.0F;
         this.ocelotTail.rotateAngleX = ((float)Math.PI / 2F);
         this.ocelotTail2.rotateAngleX = ((float)Math.PI / 2F);
         this.state = 0;
      } else if (entityIn.isSprinting()) {
         this.ocelotTail2.rotationPointY = this.ocelotTail.rotationPointY;
         this.ocelotTail2.rotationPointZ += 2.0F;
         this.ocelotTail.rotateAngleX = ((float)Math.PI / 2F);
         this.ocelotTail2.rotateAngleX = ((float)Math.PI / 2F);
         this.state = 2;
      } else {
         this.state = 1;
      }

   }
}