package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PufferFishMediumModel<T extends Entity> extends EntityModel<T> {
   private final RendererModel field_203730_a;
   private final RendererModel field_203731_b;
   private final RendererModel field_203732_c;
   private final RendererModel field_203733_d;
   private final RendererModel field_203734_e;
   private final RendererModel field_203735_f;
   private final RendererModel field_203736_g;
   private final RendererModel field_203737_h;
   private final RendererModel field_203738_i;
   private final RendererModel field_203739_j;
   private final RendererModel field_203740_k;

   public PufferFishMediumModel() {
      this.textureWidth = 32;
      this.textureHeight = 32;
      int i = 22;
      this.field_203730_a = new RendererModel(this, 12, 22);
      this.field_203730_a.addBox(-2.5F, -5.0F, -2.5F, 5, 5, 5);
      this.field_203730_a.setRotationPoint(0.0F, 22.0F, 0.0F);
      this.field_203731_b = new RendererModel(this, 24, 0);
      this.field_203731_b.addBox(-2.0F, 0.0F, 0.0F, 2, 0, 2);
      this.field_203731_b.setRotationPoint(-2.5F, 17.0F, -1.5F);
      this.field_203732_c = new RendererModel(this, 24, 3);
      this.field_203732_c.addBox(0.0F, 0.0F, 0.0F, 2, 0, 2);
      this.field_203732_c.setRotationPoint(2.5F, 17.0F, -1.5F);
      this.field_203733_d = new RendererModel(this, 15, 16);
      this.field_203733_d.addBox(-2.5F, -1.0F, 0.0F, 5, 1, 1);
      this.field_203733_d.setRotationPoint(0.0F, 17.0F, -2.5F);
      this.field_203733_d.rotateAngleX = ((float)Math.PI / 4F);
      this.field_203734_e = new RendererModel(this, 10, 16);
      this.field_203734_e.addBox(-2.5F, -1.0F, -1.0F, 5, 1, 1);
      this.field_203734_e.setRotationPoint(0.0F, 17.0F, 2.5F);
      this.field_203734_e.rotateAngleX = (-(float)Math.PI / 4F);
      this.field_203735_f = new RendererModel(this, 8, 16);
      this.field_203735_f.addBox(-1.0F, -5.0F, 0.0F, 1, 5, 1);
      this.field_203735_f.setRotationPoint(-2.5F, 22.0F, -2.5F);
      this.field_203735_f.rotateAngleY = (-(float)Math.PI / 4F);
      this.field_203736_g = new RendererModel(this, 8, 16);
      this.field_203736_g.addBox(-1.0F, -5.0F, 0.0F, 1, 5, 1);
      this.field_203736_g.setRotationPoint(-2.5F, 22.0F, 2.5F);
      this.field_203736_g.rotateAngleY = ((float)Math.PI / 4F);
      this.field_203737_h = new RendererModel(this, 4, 16);
      this.field_203737_h.addBox(0.0F, -5.0F, 0.0F, 1, 5, 1);
      this.field_203737_h.setRotationPoint(2.5F, 22.0F, 2.5F);
      this.field_203737_h.rotateAngleY = (-(float)Math.PI / 4F);
      this.field_203738_i = new RendererModel(this, 0, 16);
      this.field_203738_i.addBox(0.0F, -5.0F, 0.0F, 1, 5, 1);
      this.field_203738_i.setRotationPoint(2.5F, 22.0F, -2.5F);
      this.field_203738_i.rotateAngleY = ((float)Math.PI / 4F);
      this.field_203739_j = new RendererModel(this, 8, 22);
      this.field_203739_j.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1);
      this.field_203739_j.setRotationPoint(0.5F, 22.0F, 2.5F);
      this.field_203739_j.rotateAngleX = ((float)Math.PI / 4F);
      this.field_203740_k = new RendererModel(this, 17, 21);
      this.field_203740_k.addBox(-2.5F, 0.0F, 0.0F, 5, 1, 1);
      this.field_203740_k.setRotationPoint(0.0F, 22.0F, -2.5F);
      this.field_203740_k.rotateAngleX = (-(float)Math.PI / 4F);
   }

   /**
    * Sets the models various rotation angles then renders the model.
    */
   public void render(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      this.field_203730_a.render(scale);
      this.field_203731_b.render(scale);
      this.field_203732_c.render(scale);
      this.field_203733_d.render(scale);
      this.field_203734_e.render(scale);
      this.field_203735_f.render(scale);
      this.field_203736_g.render(scale);
      this.field_203737_h.render(scale);
      this.field_203738_i.render(scale);
      this.field_203739_j.render(scale);
      this.field_203740_k.render(scale);
   }

   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
      this.field_203731_b.rotateAngleZ = -0.2F + 0.4F * MathHelper.sin(ageInTicks * 0.2F);
      this.field_203732_c.rotateAngleZ = 0.2F - 0.4F * MathHelper.sin(ageInTicks * 0.2F);
   }
}