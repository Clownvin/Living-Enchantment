package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;

public class PrioritizedGoal extends Goal {
   private final Goal inner;
   private final int priority;
   private boolean running;

   public PrioritizedGoal(int p_i50318_1_, Goal p_i50318_2_) {
      this.priority = p_i50318_1_;
      this.inner = p_i50318_2_;
   }

   public boolean isPreemptedBy(PrioritizedGoal p_220771_1_) {
      return this.isPreemptible() && p_220771_1_.getPriority() < this.getPriority();
   }

   /**
    * Returns whether the EntityAIBase should begin execution.
    */
   public boolean shouldExecute() {
      return this.inner.shouldExecute();
   }

   /**
    * Returns whether an in-progress EntityAIBase should continue executing
    */
   public boolean shouldContinueExecuting() {
      return this.inner.shouldContinueExecuting();
   }

   public boolean isPreemptible() {
      return this.inner.isPreemptible();
   }

   /**
    * Execute a one shot task or start executing a continuous task
    */
   public void startExecuting() {
      if (!this.running) {
         this.running = true;
         this.inner.startExecuting();
      }
   }

   /**
    * Reset the task's internal state. Called when this task is interrupted by another one
    */
   public void resetTask() {
      if (this.running) {
         this.running = false;
         this.inner.resetTask();
      }
   }

   /**
    * Keep ticking a continuous task that has already been started
    */
   public void tick() {
      this.inner.tick();
   }

   public void setMutexFlags(EnumSet<Goal.Flag> p_220684_1_) {
      this.inner.setMutexFlags(p_220684_1_);
   }

   public EnumSet<Goal.Flag> getMutexFlags() {
      return this.inner.getMutexFlags();
   }

   public boolean isRunning() {
      return this.running;
   }

   public int getPriority() {
      return this.priority;
   }

   public Goal func_220772_j() {
      return this.inner;
   }

   public boolean equals(@Nullable Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ != null && this.getClass() == p_equals_1_.getClass() ? this.inner.equals(((PrioritizedGoal)p_equals_1_).inner) : false;
      }
   }

   public int hashCode() {
      return this.inner.hashCode();
   }
}