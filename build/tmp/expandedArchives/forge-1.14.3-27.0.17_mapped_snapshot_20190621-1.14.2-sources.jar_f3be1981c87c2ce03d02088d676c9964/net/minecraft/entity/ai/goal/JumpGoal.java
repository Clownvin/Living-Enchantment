package net.minecraft.entity.ai.goal;

import java.util.EnumSet;

public abstract class JumpGoal extends Goal {
   public JumpGoal() {
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
   }

   protected float updateRotation(float p_205147_1_, float p_205147_2_, float p_205147_3_) {
      float f;
      for(f = p_205147_2_ - p_205147_1_; f < -180.0F; f += 360.0F) {
         ;
      }

      while(f >= 180.0F) {
         f -= 360.0F;
      }

      return p_205147_1_ + p_205147_3_ * f;
   }
}