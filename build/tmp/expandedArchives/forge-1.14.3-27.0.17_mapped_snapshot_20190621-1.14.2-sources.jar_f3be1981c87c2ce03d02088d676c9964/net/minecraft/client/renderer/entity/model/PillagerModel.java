package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.monster.AbstractIllagerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PillagerModel<T extends AbstractIllagerEntity> extends IllagerModel<T> {
   public PillagerModel(float p_i51062_1_, float p_i51062_2_, int p_i51062_3_, int p_i51062_4_) {
      super(p_i51062_1_, p_i51062_2_, p_i51062_3_, p_i51062_4_);
   }

   /**
    * Sets the models various rotation angles then renders the model.
    */
   public void render(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      this.head.render(scale);
      this.body.render(scale);
      this.field_217143_g.render(scale);
      this.field_217144_h.render(scale);
      this.rightArm.render(scale);
      this.leftArm.render(scale);
   }
}