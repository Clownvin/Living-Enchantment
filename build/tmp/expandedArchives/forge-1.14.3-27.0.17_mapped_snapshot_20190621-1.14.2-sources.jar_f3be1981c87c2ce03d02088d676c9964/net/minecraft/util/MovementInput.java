package net.minecraft.util;

import net.minecraft.util.math.Vec2f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MovementInput {
   public float moveStrafe;
   public float moveForward;
   public boolean forwardKeyDown;
   public boolean backKeyDown;
   public boolean leftKeyDown;
   public boolean rightKeyDown;
   public boolean jump;
   public boolean sneak;

   public void func_217607_a(boolean p_217607_1_, boolean p_217607_2_) {
   }

   public Vec2f getMoveVector() {
      return new Vec2f(this.moveStrafe, this.moveForward);
   }

   public boolean func_223135_b() {
      return this.moveForward > 1.0E-5F;
   }
}