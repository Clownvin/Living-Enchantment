package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BookModel extends Model {
   private final RendererModel field_78102_a = (new RendererModel(this)).setTextureOffset(0, 0).addBox(-6.0F, -5.0F, 0.0F, 6, 10, 0);
   private final RendererModel field_78100_b = (new RendererModel(this)).setTextureOffset(16, 0).addBox(0.0F, -5.0F, 0.0F, 6, 10, 0);
   private final RendererModel field_78101_c;
   private final RendererModel field_78098_d;
   private final RendererModel field_78099_e;
   private final RendererModel field_78096_f;
   private final RendererModel field_78097_g = (new RendererModel(this)).setTextureOffset(12, 0).addBox(-1.0F, -5.0F, 0.0F, 2, 10, 0);

   public BookModel() {
      this.field_78101_c = (new RendererModel(this)).setTextureOffset(0, 10).addBox(0.0F, -4.0F, -0.99F, 5, 8, 1);
      this.field_78098_d = (new RendererModel(this)).setTextureOffset(12, 10).addBox(0.0F, -4.0F, -0.01F, 5, 8, 1);
      this.field_78099_e = (new RendererModel(this)).setTextureOffset(24, 10).addBox(0.0F, -4.0F, 0.0F, 5, 8, 0);
      this.field_78096_f = (new RendererModel(this)).setTextureOffset(24, 10).addBox(0.0F, -4.0F, 0.0F, 5, 8, 0);
      this.field_78102_a.setRotationPoint(0.0F, 0.0F, -1.0F);
      this.field_78100_b.setRotationPoint(0.0F, 0.0F, 1.0F);
      this.field_78097_g.rotateAngleY = ((float)Math.PI / 2F);
   }

   public void func_217103_a(float p_217103_1_, float p_217103_2_, float p_217103_3_, float p_217103_4_, float p_217103_5_, float p_217103_6_) {
      this.func_217102_b(p_217103_1_, p_217103_2_, p_217103_3_, p_217103_4_, p_217103_5_, p_217103_6_);
      this.field_78102_a.render(p_217103_6_);
      this.field_78100_b.render(p_217103_6_);
      this.field_78097_g.render(p_217103_6_);
      this.field_78101_c.render(p_217103_6_);
      this.field_78098_d.render(p_217103_6_);
      this.field_78099_e.render(p_217103_6_);
      this.field_78096_f.render(p_217103_6_);
   }

   private void func_217102_b(float p_217102_1_, float p_217102_2_, float p_217102_3_, float p_217102_4_, float p_217102_5_, float p_217102_6_) {
      float f = (MathHelper.sin(p_217102_1_ * 0.02F) * 0.1F + 1.25F) * p_217102_4_;
      this.field_78102_a.rotateAngleY = (float)Math.PI + f;
      this.field_78100_b.rotateAngleY = -f;
      this.field_78101_c.rotateAngleY = f;
      this.field_78098_d.rotateAngleY = -f;
      this.field_78099_e.rotateAngleY = f - f * 2.0F * p_217102_2_;
      this.field_78096_f.rotateAngleY = f - f * 2.0F * p_217102_3_;
      this.field_78101_c.rotationPointX = MathHelper.sin(f);
      this.field_78098_d.rotationPointX = MathHelper.sin(f);
      this.field_78099_e.rotationPointX = MathHelper.sin(f);
      this.field_78096_f.rotationPointX = MathHelper.sin(f);
   }
}