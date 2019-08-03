package net.minecraft.client.renderer.tileentity.model;

import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SignModel extends Model {
   private final RendererModel field_78166_a = new RendererModel(this, 0, 0);
   private final RendererModel field_78165_b;

   public SignModel() {
      this.field_78166_a.addBox(-12.0F, -14.0F, -1.0F, 24, 12, 2, 0.0F);
      this.field_78165_b = new RendererModel(this, 0, 14);
      this.field_78165_b.addBox(-1.0F, -2.0F, -1.0F, 2, 14, 2, 0.0F);
   }

   /**
    * Renders the sign model through TileEntitySignRenderer
    */
   public void renderSign() {
      this.field_78166_a.render(0.0625F);
      this.field_78165_b.render(0.0625F);
   }

   public RendererModel getSignStick() {
      return this.field_78165_b;
   }
}