package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.Vec3d;

public class RandomWalkingGoal extends Goal {
   protected final CreatureEntity field_75457_a;
   protected double x;
   protected double y;
   protected double z;
   protected final double speed;
   protected int executionChance;
   protected boolean mustUpdate;

   public RandomWalkingGoal(CreatureEntity creatureIn, double speedIn) {
      this(creatureIn, speedIn, 120);
   }

   public RandomWalkingGoal(CreatureEntity creatureIn, double speedIn, int chance) {
      this.field_75457_a = creatureIn;
      this.speed = speedIn;
      this.executionChance = chance;
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
   }

   /**
    * Returns whether the EntityAIBase should begin execution.
    */
   public boolean shouldExecute() {
      if (this.field_75457_a.isBeingRidden()) {
         return false;
      } else {
         if (!this.mustUpdate) {
            if (this.field_75457_a.getIdleTime() >= 100) {
               return false;
            }

            if (this.field_75457_a.getRNG().nextInt(this.executionChance) != 0) {
               return false;
            }
         }

         Vec3d vec3d = this.getPosition();
         if (vec3d == null) {
            return false;
         } else {
            this.x = vec3d.x;
            this.y = vec3d.y;
            this.z = vec3d.z;
            this.mustUpdate = false;
            return true;
         }
      }
   }

   @Nullable
   protected Vec3d getPosition() {
      return RandomPositionGenerator.findRandomTarget(this.field_75457_a, 10, 7);
   }

   /**
    * Returns whether an in-progress EntityAIBase should continue executing
    */
   public boolean shouldContinueExecuting() {
      return !this.field_75457_a.getNavigator().noPath();
   }

   /**
    * Execute a one shot task or start executing a continuous task
    */
   public void startExecuting() {
      this.field_75457_a.getNavigator().tryMoveToXYZ(this.x, this.y, this.z, this.speed);
   }

   /**
    * Makes task to bypass chance
    */
   public void makeUpdate() {
      this.mustUpdate = true;
   }

   /**
    * Changes task random possibility for execution
    */
   public void setExecutionChance(int newchance) {
      this.executionChance = newchance;
   }
}