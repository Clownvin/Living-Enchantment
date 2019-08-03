package net.minecraft.client.renderer.tileentity.model;

import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LargeChestModel extends ChestModel {
   public LargeChestModel() {
      this.field_78234_a = (new RendererModel(this, 0, 0)).setTextureSize(128, 64);
      this.field_78234_a.addBox(0.0F, -5.0F, -14.0F, 30, 5, 14, 0.0F);
      this.field_78234_a.rotationPointX = 1.0F;
      this.field_78234_a.rotationPointY = 7.0F;
      this.field_78234_a.rotationPointZ = 15.0F;
      this.field_78233_c = (new RendererModel(this, 0, 0)).setTextureSize(128, 64);
      this.field_78233_c.addBox(-1.0F, -2.0F, -15.0F, 2, 4, 1, 0.0F);
      this.field_78233_c.rotationPointX = 16.0F;
      this.field_78233_c.rotationPointY = 7.0F;
      this.field_78233_c.rotationPointZ = 15.0F;
      this.field_78232_b = (new RendererModel(this, 0, 19)).setTextureSize(128, 64);
      this.field_78232_b.addBox(0.0F, 0.0F, 0.0F, 30, 10, 14, 0.0F);
      this.field_78232_b.rotationPointX = 1.0F;
      this.field_78232_b.rotationPointY = 6.0F;
      this.field_78232_b.rotationPointZ = 1.0F;
   }
}