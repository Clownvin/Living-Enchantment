package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ArmorStandArmorModel extends BipedModel<ArmorStandEntity> {
   public ArmorStandArmorModel() {
      this(0.0F);
   }

   public ArmorStandArmorModel(float modelSize) {
      this(modelSize, 64, 32);
   }

   protected ArmorStandArmorModel(float modelSize, int textureWidthIn, int textureHeightIn) {
      super(modelSize, 0.0F, textureWidthIn, textureHeightIn);
   }

   public void setRotationAngles(ArmorStandEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
      this.field_78116_c.rotateAngleX = ((float)Math.PI / 180F) * entityIn.getHeadRotation().getX();
      this.field_78116_c.rotateAngleY = ((float)Math.PI / 180F) * entityIn.getHeadRotation().getY();
      this.field_78116_c.rotateAngleZ = ((float)Math.PI / 180F) * entityIn.getHeadRotation().getZ();
      this.field_78116_c.setRotationPoint(0.0F, 1.0F, 0.0F);
      this.field_78115_e.rotateAngleX = ((float)Math.PI / 180F) * entityIn.getBodyRotation().getX();
      this.field_78115_e.rotateAngleY = ((float)Math.PI / 180F) * entityIn.getBodyRotation().getY();
      this.field_78115_e.rotateAngleZ = ((float)Math.PI / 180F) * entityIn.getBodyRotation().getZ();
      this.bipedLeftArm.rotateAngleX = ((float)Math.PI / 180F) * entityIn.getLeftArmRotation().getX();
      this.bipedLeftArm.rotateAngleY = ((float)Math.PI / 180F) * entityIn.getLeftArmRotation().getY();
      this.bipedLeftArm.rotateAngleZ = ((float)Math.PI / 180F) * entityIn.getLeftArmRotation().getZ();
      this.bipedRightArm.rotateAngleX = ((float)Math.PI / 180F) * entityIn.getRightArmRotation().getX();
      this.bipedRightArm.rotateAngleY = ((float)Math.PI / 180F) * entityIn.getRightArmRotation().getY();
      this.bipedRightArm.rotateAngleZ = ((float)Math.PI / 180F) * entityIn.getRightArmRotation().getZ();
      this.bipedLeftLeg.rotateAngleX = ((float)Math.PI / 180F) * entityIn.getLeftLegRotation().getX();
      this.bipedLeftLeg.rotateAngleY = ((float)Math.PI / 180F) * entityIn.getLeftLegRotation().getY();
      this.bipedLeftLeg.rotateAngleZ = ((float)Math.PI / 180F) * entityIn.getLeftLegRotation().getZ();
      this.bipedLeftLeg.setRotationPoint(1.9F, 11.0F, 0.0F);
      this.bipedRightLeg.rotateAngleX = ((float)Math.PI / 180F) * entityIn.getRightLegRotation().getX();
      this.bipedRightLeg.rotateAngleY = ((float)Math.PI / 180F) * entityIn.getRightLegRotation().getY();
      this.bipedRightLeg.rotateAngleZ = ((float)Math.PI / 180F) * entityIn.getRightLegRotation().getZ();
      this.bipedRightLeg.setRotationPoint(-1.9F, 11.0F, 0.0F);
      this.bipedHeadwear.func_217177_a(this.field_78116_c);
   }
}