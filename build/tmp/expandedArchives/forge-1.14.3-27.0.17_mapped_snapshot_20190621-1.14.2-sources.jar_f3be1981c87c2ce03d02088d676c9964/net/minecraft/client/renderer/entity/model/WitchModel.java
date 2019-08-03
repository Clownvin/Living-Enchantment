package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WitchModel<T extends Entity> extends VillagerModel<T> {
   private boolean holdingItem;
   private final RendererModel mole = (new RendererModel(this)).setTextureSize(64, 128);

   public WitchModel(float scale) {
      super(scale, 64, 128);
      this.mole.setRotationPoint(0.0F, -2.0F, 0.0F);
      this.mole.setTextureOffset(0, 0).addBox(0.0F, 3.0F, -6.75F, 1, 1, 1, -0.25F);
      this.villagerNose.addChild(this.mole);
      this.villagerHead.func_217179_c(this.field_217151_b);
      this.field_217151_b = (new RendererModel(this)).setTextureSize(64, 128);
      this.field_217151_b.setRotationPoint(-5.0F, -10.03125F, -5.0F);
      this.field_217151_b.setTextureOffset(0, 64).addBox(0.0F, 0.0F, 0.0F, 10, 2, 10);
      this.villagerHead.addChild(this.field_217151_b);
      RendererModel renderermodel = (new RendererModel(this)).setTextureSize(64, 128);
      renderermodel.setRotationPoint(1.75F, -4.0F, 2.0F);
      renderermodel.setTextureOffset(0, 76).addBox(0.0F, 0.0F, 0.0F, 7, 4, 7);
      renderermodel.rotateAngleX = -0.05235988F;
      renderermodel.rotateAngleZ = 0.02617994F;
      this.field_217151_b.addChild(renderermodel);
      RendererModel renderermodel1 = (new RendererModel(this)).setTextureSize(64, 128);
      renderermodel1.setRotationPoint(1.75F, -4.0F, 2.0F);
      renderermodel1.setTextureOffset(0, 87).addBox(0.0F, 0.0F, 0.0F, 4, 4, 4);
      renderermodel1.rotateAngleX = -0.10471976F;
      renderermodel1.rotateAngleZ = 0.05235988F;
      renderermodel.addChild(renderermodel1);
      RendererModel renderermodel2 = (new RendererModel(this)).setTextureSize(64, 128);
      renderermodel2.setRotationPoint(1.75F, -2.0F, 2.0F);
      renderermodel2.setTextureOffset(0, 95).addBox(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.25F);
      renderermodel2.rotateAngleX = -0.20943952F;
      renderermodel2.rotateAngleZ = 0.10471976F;
      renderermodel1.addChild(renderermodel2);
   }

   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
      super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
      this.villagerNose.offsetX = 0.0F;
      this.villagerNose.offsetY = 0.0F;
      this.villagerNose.offsetZ = 0.0F;
      float f = 0.01F * (float)(entityIn.getEntityId() % 10);
      this.villagerNose.rotateAngleX = MathHelper.sin((float)entityIn.ticksExisted * f) * 4.5F * ((float)Math.PI / 180F);
      this.villagerNose.rotateAngleY = 0.0F;
      this.villagerNose.rotateAngleZ = MathHelper.cos((float)entityIn.ticksExisted * f) * 2.5F * ((float)Math.PI / 180F);
      if (this.holdingItem) {
         this.villagerNose.rotateAngleX = -0.9F;
         this.villagerNose.offsetZ = -0.09375F;
         this.villagerNose.offsetY = 0.1875F;
      }

   }

   public RendererModel func_205073_b() {
      return this.villagerNose;
   }

   public void func_205074_a(boolean p_205074_1_) {
      this.holdingItem = p_205074_1_;
   }
}