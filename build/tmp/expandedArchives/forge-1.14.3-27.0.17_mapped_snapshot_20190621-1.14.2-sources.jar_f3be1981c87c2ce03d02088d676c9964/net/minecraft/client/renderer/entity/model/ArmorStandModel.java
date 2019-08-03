package net.minecraft.client.renderer.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.util.HandSide;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ArmorStandModel extends ArmorStandArmorModel {
   private final RendererModel standRightSide;
   private final RendererModel standLeftSide;
   private final RendererModel standWaist;
   private final RendererModel standBase;

   public ArmorStandModel() {
      this(0.0F);
   }

   public ArmorStandModel(float modelSize) {
      super(modelSize, 64, 64);
      this.field_78116_c = new RendererModel(this, 0, 0);
      this.field_78116_c.addBox(-1.0F, -7.0F, -1.0F, 2, 7, 2, modelSize);
      this.field_78116_c.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.field_78115_e = new RendererModel(this, 0, 26);
      this.field_78115_e.addBox(-6.0F, 0.0F, -1.5F, 12, 3, 3, modelSize);
      this.field_78115_e.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.bipedRightArm = new RendererModel(this, 24, 0);
      this.bipedRightArm.addBox(-2.0F, -2.0F, -1.0F, 2, 12, 2, modelSize);
      this.bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
      this.bipedLeftArm = new RendererModel(this, 32, 16);
      this.bipedLeftArm.mirror = true;
      this.bipedLeftArm.addBox(0.0F, -2.0F, -1.0F, 2, 12, 2, modelSize);
      this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
      this.bipedRightLeg = new RendererModel(this, 8, 0);
      this.bipedRightLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 11, 2, modelSize);
      this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
      this.bipedLeftLeg = new RendererModel(this, 40, 16);
      this.bipedLeftLeg.mirror = true;
      this.bipedLeftLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 11, 2, modelSize);
      this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
      this.standRightSide = new RendererModel(this, 16, 0);
      this.standRightSide.addBox(-3.0F, 3.0F, -1.0F, 2, 7, 2, modelSize);
      this.standRightSide.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.standRightSide.showModel = true;
      this.standLeftSide = new RendererModel(this, 48, 16);
      this.standLeftSide.addBox(1.0F, 3.0F, -1.0F, 2, 7, 2, modelSize);
      this.standLeftSide.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.standWaist = new RendererModel(this, 0, 48);
      this.standWaist.addBox(-4.0F, 10.0F, -1.0F, 8, 2, 2, modelSize);
      this.standWaist.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.standBase = new RendererModel(this, 0, 32);
      this.standBase.addBox(-6.0F, 11.0F, -6.0F, 12, 1, 12, modelSize);
      this.standBase.setRotationPoint(0.0F, 12.0F, 0.0F);
      this.bipedHeadwear.showModel = false;
   }

   public void setRotationAngles(ArmorStandEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
      super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
      this.bipedLeftArm.showModel = entityIn.getShowArms();
      this.bipedRightArm.showModel = entityIn.getShowArms();
      this.standBase.showModel = !entityIn.hasNoBasePlate();
      this.bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
      this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
      this.standRightSide.rotateAngleX = ((float)Math.PI / 180F) * entityIn.getBodyRotation().getX();
      this.standRightSide.rotateAngleY = ((float)Math.PI / 180F) * entityIn.getBodyRotation().getY();
      this.standRightSide.rotateAngleZ = ((float)Math.PI / 180F) * entityIn.getBodyRotation().getZ();
      this.standLeftSide.rotateAngleX = ((float)Math.PI / 180F) * entityIn.getBodyRotation().getX();
      this.standLeftSide.rotateAngleY = ((float)Math.PI / 180F) * entityIn.getBodyRotation().getY();
      this.standLeftSide.rotateAngleZ = ((float)Math.PI / 180F) * entityIn.getBodyRotation().getZ();
      this.standWaist.rotateAngleX = ((float)Math.PI / 180F) * entityIn.getBodyRotation().getX();
      this.standWaist.rotateAngleY = ((float)Math.PI / 180F) * entityIn.getBodyRotation().getY();
      this.standWaist.rotateAngleZ = ((float)Math.PI / 180F) * entityIn.getBodyRotation().getZ();
      this.standBase.rotateAngleX = 0.0F;
      this.standBase.rotateAngleY = ((float)Math.PI / 180F) * -entityIn.rotationYaw;
      this.standBase.rotateAngleZ = 0.0F;
   }

   /**
    * Sets the models various rotation angles then renders the model.
    */
   public void render(ArmorStandEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      GlStateManager.pushMatrix();
      if (this.isChild) {
         float f = 2.0F;
         GlStateManager.scalef(0.5F, 0.5F, 0.5F);
         GlStateManager.translatef(0.0F, 24.0F * scale, 0.0F);
         this.standRightSide.render(scale);
         this.standLeftSide.render(scale);
         this.standWaist.render(scale);
         this.standBase.render(scale);
      } else {
         if (entityIn.isSneaking()) {
            GlStateManager.translatef(0.0F, 0.2F, 0.0F);
         }

         this.standRightSide.render(scale);
         this.standLeftSide.render(scale);
         this.standWaist.render(scale);
         this.standBase.render(scale);
      }

      GlStateManager.popMatrix();
   }

   public void postRenderArm(float scale, HandSide side) {
      RendererModel renderermodel = this.getArmForSide(side);
      boolean flag = renderermodel.showModel;
      renderermodel.showModel = true;
      super.postRenderArm(scale, side);
      renderermodel.showModel = flag;
   }
}