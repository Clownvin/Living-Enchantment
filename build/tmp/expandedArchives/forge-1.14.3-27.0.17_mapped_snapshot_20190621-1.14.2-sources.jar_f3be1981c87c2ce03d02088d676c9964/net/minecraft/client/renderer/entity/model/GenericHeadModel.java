package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.model.Model;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GenericHeadModel extends Model {
   protected final RendererModel field_217105_a;

   public GenericHeadModel() {
      this(0, 35, 64, 64);
   }

   public GenericHeadModel(int p_i51060_1_, int p_i51060_2_, int p_i51060_3_, int p_i51060_4_) {
      this.textureWidth = p_i51060_3_;
      this.textureHeight = p_i51060_4_;
      this.field_217105_a = new RendererModel(this, p_i51060_1_, p_i51060_2_);
      this.field_217105_a.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
      this.field_217105_a.setRotationPoint(0.0F, 0.0F, 0.0F);
   }

   public void func_217104_a(float p_217104_1_, float p_217104_2_, float p_217104_3_, float p_217104_4_, float p_217104_5_, float p_217104_6_) {
      this.field_217105_a.rotateAngleY = p_217104_4_ * ((float)Math.PI / 180F);
      this.field_217105_a.rotateAngleX = p_217104_5_ * ((float)Math.PI / 180F);
      this.field_217105_a.render(p_217104_6_);
   }
}