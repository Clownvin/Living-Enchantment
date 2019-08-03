package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class EntityModel<T extends Entity> extends Model {
   public float swingProgress;
   public boolean isSitting;
   public boolean isChild = true;

   /**
    * Sets the models various rotation angles then renders the model.
    */
   public void render(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
   }

   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
   }

   public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
   }

   public void setModelAttributes(EntityModel<T> p_217111_1_) {
      p_217111_1_.swingProgress = this.swingProgress;
      p_217111_1_.isSitting = this.isSitting;
      p_217111_1_.isChild = this.isChild;
   }
}