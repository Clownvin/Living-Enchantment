package net.minecraft.client.renderer.tileentity.model;

import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BellModel extends Model {
   private final RendererModel field_217100_a;
   private final RendererModel field_217101_b;

   public BellModel() {
      this.textureWidth = 32;
      this.textureHeight = 32;
      this.field_217100_a = new RendererModel(this, 0, 0);
      this.field_217100_a.addBox(-3.0F, -6.0F, -3.0F, 6, 7, 6);
      this.field_217100_a.setRotationPoint(8.0F, 12.0F, 8.0F);
      this.field_217101_b = new RendererModel(this, 0, 13);
      this.field_217101_b.addBox(4.0F, 4.0F, 4.0F, 8, 2, 8);
      this.field_217101_b.setRotationPoint(-8.0F, -12.0F, -8.0F);
      this.field_217100_a.addChild(this.field_217101_b);
   }

   public void func_217099_a(float p_217099_1_, float p_217099_2_, float p_217099_3_) {
      this.field_217100_a.rotateAngleX = p_217099_1_;
      this.field_217100_a.rotateAngleZ = p_217099_2_;
      this.field_217100_a.render(p_217099_3_);
   }
}