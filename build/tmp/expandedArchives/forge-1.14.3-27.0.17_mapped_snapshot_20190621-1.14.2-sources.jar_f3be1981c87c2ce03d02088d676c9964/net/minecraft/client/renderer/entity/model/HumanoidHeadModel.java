package net.minecraft.client.renderer.entity.model;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HumanoidHeadModel extends GenericHeadModel {
   private final RendererModel head = new RendererModel(this, 32, 0);

   public HumanoidHeadModel() {
      super(0, 0, 64, 64);
      this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.25F);
      this.head.setRotationPoint(0.0F, 0.0F, 0.0F);
   }

   public void func_217104_a(float p_217104_1_, float p_217104_2_, float p_217104_3_, float p_217104_4_, float p_217104_5_, float p_217104_6_) {
      super.func_217104_a(p_217104_1_, p_217104_2_, p_217104_3_, p_217104_4_, p_217104_5_, p_217104_6_);
      this.head.rotateAngleY = this.field_217105_a.rotateAngleY;
      this.head.rotateAngleX = this.field_217105_a.rotateAngleX;
      this.head.render(p_217104_6_);
   }
}