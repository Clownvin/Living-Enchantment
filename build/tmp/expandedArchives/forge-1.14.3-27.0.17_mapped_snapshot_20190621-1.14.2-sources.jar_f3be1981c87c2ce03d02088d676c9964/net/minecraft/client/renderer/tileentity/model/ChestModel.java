package net.minecraft.client.renderer.tileentity.model;

import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChestModel extends Model {
   protected RendererModel field_78234_a = (new RendererModel(this, 0, 0)).setTextureSize(64, 64);
   protected RendererModel field_78232_b;
   protected RendererModel field_78233_c;

   public ChestModel() {
      this.field_78234_a.addBox(0.0F, -5.0F, -14.0F, 14, 5, 14, 0.0F);
      this.field_78234_a.rotationPointX = 1.0F;
      this.field_78234_a.rotationPointY = 7.0F;
      this.field_78234_a.rotationPointZ = 15.0F;
      this.field_78233_c = (new RendererModel(this, 0, 0)).setTextureSize(64, 64);
      this.field_78233_c.addBox(-1.0F, -2.0F, -15.0F, 2, 4, 1, 0.0F);
      this.field_78233_c.rotationPointX = 8.0F;
      this.field_78233_c.rotationPointY = 7.0F;
      this.field_78233_c.rotationPointZ = 15.0F;
      this.field_78232_b = (new RendererModel(this, 0, 19)).setTextureSize(64, 64);
      this.field_78232_b.addBox(0.0F, 0.0F, 0.0F, 14, 10, 14, 0.0F);
      this.field_78232_b.rotationPointX = 1.0F;
      this.field_78232_b.rotationPointY = 6.0F;
      this.field_78232_b.rotationPointZ = 1.0F;
   }

   /**
    * This method renders out all parts of the chest model.
    */
   public void renderAll() {
      this.field_78233_c.rotateAngleX = this.field_78234_a.rotateAngleX;
      this.field_78234_a.render(0.0625F);
      this.field_78233_c.render(0.0625F);
      this.field_78232_b.render(0.0625F);
   }

   public RendererModel getLid() {
      return this.field_78234_a;
   }
}