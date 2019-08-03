package net.minecraft.client.renderer.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SlimeModel<T extends Entity> extends EntityModel<T> {
   private final RendererModel field_78200_a;
   private final RendererModel field_78198_b;
   private final RendererModel field_78199_c;
   private final RendererModel field_78197_d;

   public SlimeModel(int slimeBodyTexOffY) {
      if (slimeBodyTexOffY > 0) {
         this.field_78200_a = new RendererModel(this, 0, slimeBodyTexOffY);
         this.field_78200_a.addBox(-3.0F, 17.0F, -3.0F, 6, 6, 6);
         this.field_78198_b = new RendererModel(this, 32, 0);
         this.field_78198_b.addBox(-3.25F, 18.0F, -3.5F, 2, 2, 2);
         this.field_78199_c = new RendererModel(this, 32, 4);
         this.field_78199_c.addBox(1.25F, 18.0F, -3.5F, 2, 2, 2);
         this.field_78197_d = new RendererModel(this, 32, 8);
         this.field_78197_d.addBox(0.0F, 21.0F, -3.5F, 1, 1, 1);
      } else {
         this.field_78200_a = new RendererModel(this, 0, slimeBodyTexOffY);
         this.field_78200_a.addBox(-4.0F, 16.0F, -4.0F, 8, 8, 8);
         this.field_78198_b = null;
         this.field_78199_c = null;
         this.field_78197_d = null;
      }

   }

   /**
    * Sets the models various rotation angles then renders the model.
    */
   public void render(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      GlStateManager.translatef(0.0F, 0.001F, 0.0F);
      this.field_78200_a.render(scale);
      if (this.field_78198_b != null) {
         this.field_78198_b.render(scale);
         this.field_78199_c.render(scale);
         this.field_78197_d.render(scale);
      }

   }
}