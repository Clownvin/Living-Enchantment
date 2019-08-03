package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.passive.horse.AbstractChestedHorseEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HorseArmorChestsModel<T extends AbstractChestedHorseEntity> extends HorseModel<T> {
   private final RendererModel field_199057_c = new RendererModel(this, 26, 21);
   private final RendererModel field_199058_d;

   public HorseArmorChestsModel(float p_i51068_1_) {
      super(p_i51068_1_);
      this.field_199057_c.addBox(-4.0F, 0.0F, -2.0F, 8, 8, 3);
      this.field_199058_d = new RendererModel(this, 26, 21);
      this.field_199058_d.addBox(-4.0F, 0.0F, -2.0F, 8, 8, 3);
      this.field_199057_c.rotateAngleY = (-(float)Math.PI / 2F);
      this.field_199058_d.rotateAngleY = ((float)Math.PI / 2F);
      this.field_199057_c.setRotationPoint(6.0F, -8.0F, 0.0F);
      this.field_199058_d.setRotationPoint(-6.0F, -8.0F, 0.0F);
      this.field_217127_a.addChild(this.field_199057_c);
      this.field_217127_a.addChild(this.field_199058_d);
   }

   protected void func_199047_a(RendererModel p_199047_1_) {
      RendererModel renderermodel = new RendererModel(this, 0, 12);
      renderermodel.addBox(-1.0F, -7.0F, 0.0F, 2, 7, 1);
      renderermodel.setRotationPoint(1.25F, -10.0F, 4.0F);
      RendererModel renderermodel1 = new RendererModel(this, 0, 12);
      renderermodel1.addBox(-1.0F, -7.0F, 0.0F, 2, 7, 1);
      renderermodel1.setRotationPoint(-1.25F, -10.0F, 4.0F);
      renderermodel.rotateAngleX = 0.2617994F;
      renderermodel.rotateAngleZ = 0.2617994F;
      renderermodel1.rotateAngleX = 0.2617994F;
      renderermodel1.rotateAngleZ = -0.2617994F;
      p_199047_1_.addChild(renderermodel);
      p_199047_1_.addChild(renderermodel1);
   }

   /**
    * Sets the models various rotation angles then renders the model.
    */
   public void render(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      if (entityIn.hasChest()) {
         this.field_199057_c.showModel = true;
         this.field_199058_d.showModel = true;
      } else {
         this.field_199057_c.showModel = false;
         this.field_199058_d.showModel = false;
      }

      super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
   }
}