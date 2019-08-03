package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CreeperModel<T extends Entity> extends EntityModel<T> {
   private final RendererModel field_78135_a;
   private final RendererModel creeperArmor;
   private final RendererModel field_78134_c;
   private final RendererModel field_78131_d;
   private final RendererModel field_78132_e;
   private final RendererModel field_78129_f;
   private final RendererModel field_78130_g;

   public CreeperModel() {
      this(0.0F);
   }

   public CreeperModel(float p_i46366_1_) {
      int i = 6;
      this.field_78135_a = new RendererModel(this, 0, 0);
      this.field_78135_a.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, p_i46366_1_);
      this.field_78135_a.setRotationPoint(0.0F, 6.0F, 0.0F);
      this.creeperArmor = new RendererModel(this, 32, 0);
      this.creeperArmor.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, p_i46366_1_ + 0.5F);
      this.creeperArmor.setRotationPoint(0.0F, 6.0F, 0.0F);
      this.field_78134_c = new RendererModel(this, 16, 16);
      this.field_78134_c.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, p_i46366_1_);
      this.field_78134_c.setRotationPoint(0.0F, 6.0F, 0.0F);
      this.field_78131_d = new RendererModel(this, 0, 16);
      this.field_78131_d.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, p_i46366_1_);
      this.field_78131_d.setRotationPoint(-2.0F, 18.0F, 4.0F);
      this.field_78132_e = new RendererModel(this, 0, 16);
      this.field_78132_e.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, p_i46366_1_);
      this.field_78132_e.setRotationPoint(2.0F, 18.0F, 4.0F);
      this.field_78129_f = new RendererModel(this, 0, 16);
      this.field_78129_f.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, p_i46366_1_);
      this.field_78129_f.setRotationPoint(-2.0F, 18.0F, -4.0F);
      this.field_78130_g = new RendererModel(this, 0, 16);
      this.field_78130_g.addBox(-2.0F, 0.0F, -2.0F, 4, 6, 4, p_i46366_1_);
      this.field_78130_g.setRotationPoint(2.0F, 18.0F, -4.0F);
   }

   /**
    * Sets the models various rotation angles then renders the model.
    */
   public void render(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      this.field_78135_a.render(scale);
      this.field_78134_c.render(scale);
      this.field_78131_d.render(scale);
      this.field_78132_e.render(scale);
      this.field_78129_f.render(scale);
      this.field_78130_g.render(scale);
   }

   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
      this.field_78135_a.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
      this.field_78135_a.rotateAngleX = headPitch * ((float)Math.PI / 180F);
      this.field_78131_d.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
      this.field_78132_e.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
      this.field_78129_f.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
      this.field_78130_g.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
   }
}