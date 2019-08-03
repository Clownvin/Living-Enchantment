package net.minecraft.client.renderer.entity.model;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IHasHead {
   RendererModel func_205072_a();

   default void func_217142_c(float p_217142_1_) {
      this.func_205072_a().postRender(p_217142_1_);
   }
}