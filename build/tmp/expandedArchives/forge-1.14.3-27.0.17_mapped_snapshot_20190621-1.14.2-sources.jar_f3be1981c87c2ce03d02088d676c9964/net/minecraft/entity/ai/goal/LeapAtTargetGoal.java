package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.Vec3d;

public class LeapAtTargetGoal extends Goal {
   private final MobEntity field_75328_a;
   private LivingEntity field_75326_b;
   private final float leapMotionY;

   public LeapAtTargetGoal(MobEntity leapingEntity, float leapMotionYIn) {
      this.field_75328_a = leapingEntity;
      this.leapMotionY = leapMotionYIn;
      this.setMutexFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
   }

   /**
    * Returns whether the EntityAIBase should begin execution.
    */
   public boolean shouldExecute() {
      if (this.field_75328_a.isBeingRidden()) {
         return false;
      } else {
         this.field_75326_b = this.field_75328_a.getAttackTarget();
         if (this.field_75326_b == null) {
            return false;
         } else {
            double d0 = this.field_75328_a.getDistanceSq(this.field_75326_b);
            if (!(d0 < 4.0D) && !(d0 > 16.0D)) {
               if (!this.field_75328_a.onGround) {
                  return false;
               } else {
                  return this.field_75328_a.getRNG().nextInt(5) == 0;
               }
            } else {
               return false;
            }
         }
      }
   }

   /**
    * Returns whether an in-progress EntityAIBase should continue executing
    */
   public boolean shouldContinueExecuting() {
      return !this.field_75328_a.onGround;
   }

   /**
    * Execute a one shot task or start executing a continuous task
    */
   public void startExecuting() {
      Vec3d vec3d = this.field_75328_a.getMotion();
      Vec3d vec3d1 = new Vec3d(this.field_75326_b.posX - this.field_75328_a.posX, 0.0D, this.field_75326_b.posZ - this.field_75328_a.posZ);
      if (vec3d1.lengthSquared() > 1.0E-7D) {
         vec3d1 = vec3d1.normalize().scale(0.4D).add(vec3d.scale(0.2D));
      }

      this.field_75328_a.setMotion(vec3d1.x, (double)this.leapMotionY, vec3d1.y);
   }
}