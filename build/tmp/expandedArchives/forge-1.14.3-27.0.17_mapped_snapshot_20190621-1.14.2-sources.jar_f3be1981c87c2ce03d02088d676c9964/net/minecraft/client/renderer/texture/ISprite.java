package net.minecraft.client.renderer.texture;

import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ISprite {
   /**
    * @deprecated Use {@link #getState()} instead.
    */
   @Deprecated
   default ModelRotation getRotation() {
      return ModelRotation.X0_Y0;
   }

   default boolean isUvLock() {
      return false;
   }

   default net.minecraftforge.common.model.IModelState getState() { return getRotation(); }
}