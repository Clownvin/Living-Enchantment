package net.minecraft.client.renderer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IWindowEventListener {
   void setGameFocused(boolean focused);

   void func_213227_b(boolean p_213227_1_);

   void func_213226_a();
}