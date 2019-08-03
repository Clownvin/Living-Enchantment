package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VillagerModel<T extends Entity> extends EntityModel<T> implements IHasHead, IHeadToggle {
   protected final RendererModel villagerHead;
   protected RendererModel field_217151_b;
   protected final RendererModel field_217152_f;
   protected final RendererModel villagerBody;
   protected final RendererModel field_217153_h;
   protected final RendererModel villagerArms;
   protected final RendererModel rightVillagerLeg;
   protected final RendererModel leftVillagerLeg;
   protected final RendererModel villagerNose;

   public VillagerModel(float scale) {
      this(scale, 64, 64);
   }

   public VillagerModel(float p_i51059_1_, int p_i51059_2_, int p_i51059_3_) {
      float f = 0.5F;
      this.villagerHead = (new RendererModel(this)).setTextureSize(p_i51059_2_, p_i51059_3_);
      this.villagerHead.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.villagerHead.setTextureOffset(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8, 10, 8, p_i51059_1_);
      this.field_217151_b = (new RendererModel(this)).setTextureSize(p_i51059_2_, p_i51059_3_);
      this.field_217151_b.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.field_217151_b.setTextureOffset(32, 0).addBox(-4.0F, -10.0F, -4.0F, 8, 10, 8, p_i51059_1_ + 0.5F);
      this.villagerHead.addChild(this.field_217151_b);
      this.field_217152_f = (new RendererModel(this)).setTextureSize(p_i51059_2_, p_i51059_3_);
      this.field_217152_f.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.field_217152_f.setTextureOffset(30, 47).addBox(-8.0F, -8.0F, -6.0F, 16, 16, 1, p_i51059_1_);
      this.field_217152_f.rotateAngleX = (-(float)Math.PI / 2F);
      this.field_217151_b.addChild(this.field_217152_f);
      this.villagerNose = (new RendererModel(this)).setTextureSize(p_i51059_2_, p_i51059_3_);
      this.villagerNose.setRotationPoint(0.0F, -2.0F, 0.0F);
      this.villagerNose.setTextureOffset(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2, 4, 2, p_i51059_1_);
      this.villagerHead.addChild(this.villagerNose);
      this.villagerBody = (new RendererModel(this)).setTextureSize(p_i51059_2_, p_i51059_3_);
      this.villagerBody.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.villagerBody.setTextureOffset(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8, 12, 6, p_i51059_1_);
      this.field_217153_h = (new RendererModel(this)).setTextureSize(p_i51059_2_, p_i51059_3_);
      this.field_217153_h.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.field_217153_h.setTextureOffset(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8, 18, 6, p_i51059_1_ + 0.5F);
      this.villagerBody.addChild(this.field_217153_h);
      this.villagerArms = (new RendererModel(this)).setTextureSize(p_i51059_2_, p_i51059_3_);
      this.villagerArms.setRotationPoint(0.0F, 2.0F, 0.0F);
      this.villagerArms.setTextureOffset(44, 22).addBox(-8.0F, -2.0F, -2.0F, 4, 8, 4, p_i51059_1_);
      this.villagerArms.setTextureOffset(44, 22).addBox(4.0F, -2.0F, -2.0F, 4, 8, 4, p_i51059_1_, true);
      this.villagerArms.setTextureOffset(40, 38).addBox(-4.0F, 2.0F, -2.0F, 8, 4, 4, p_i51059_1_);
      this.rightVillagerLeg = (new RendererModel(this, 0, 22)).setTextureSize(p_i51059_2_, p_i51059_3_);
      this.rightVillagerLeg.setRotationPoint(-2.0F, 12.0F, 0.0F);
      this.rightVillagerLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, p_i51059_1_);
      this.leftVillagerLeg = (new RendererModel(this, 0, 22)).setTextureSize(p_i51059_2_, p_i51059_3_);
      this.leftVillagerLeg.mirror = true;
      this.leftVillagerLeg.setRotationPoint(2.0F, 12.0F, 0.0F);
      this.leftVillagerLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, p_i51059_1_);
   }

   /**
    * Sets the models various rotation angles then renders the model.
    */
   public void render(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      this.villagerHead.render(scale);
      this.villagerBody.render(scale);
      this.rightVillagerLeg.render(scale);
      this.leftVillagerLeg.render(scale);
      this.villagerArms.render(scale);
   }

   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
      boolean flag = false;
      if (entityIn instanceof AbstractVillagerEntity) {
         flag = ((AbstractVillagerEntity)entityIn).getShakeHeadTicks() > 0;
      }

      this.villagerHead.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
      this.villagerHead.rotateAngleX = headPitch * ((float)Math.PI / 180F);
      if (flag) {
         this.villagerHead.rotateAngleZ = 0.3F * MathHelper.sin(0.45F * ageInTicks);
         this.villagerHead.rotateAngleX = 0.4F;
      } else {
         this.villagerHead.rotateAngleZ = 0.0F;
      }

      this.villagerArms.rotationPointY = 3.0F;
      this.villagerArms.rotationPointZ = -1.0F;
      this.villagerArms.rotateAngleX = -0.75F;
      this.rightVillagerLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
      this.leftVillagerLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount * 0.5F;
      this.rightVillagerLeg.rotateAngleY = 0.0F;
      this.leftVillagerLeg.rotateAngleY = 0.0F;
   }

   public RendererModel func_205072_a() {
      return this.villagerHead;
   }

   public void func_217146_a(boolean p_217146_1_) {
      this.villagerHead.showModel = p_217146_1_;
      this.field_217151_b.showModel = p_217146_1_;
      this.field_217152_f.showModel = p_217146_1_;
   }
}