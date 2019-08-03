package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class MeleeAttackGoal extends Goal {
   protected final CreatureEntity field_75441_b;
   protected int attackTick;
   private final double speedTowardsTarget;
   private final boolean longMemory;
   private Path path;
   private int delayCounter;
   private double targetX;
   private double targetY;
   private double targetZ;
   protected final int attackInterval = 20;
   private long field_220720_k;
   private int failedPathFindingPenalty = 0;
   private boolean canPenalize = false;

   public MeleeAttackGoal(CreatureEntity creature, double speedIn, boolean useLongMemory) {
      this.field_75441_b = creature;
      this.speedTowardsTarget = speedIn;
      this.longMemory = useLongMemory;
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
   }

   /**
    * Returns whether the EntityAIBase should begin execution.
    */
   public boolean shouldExecute() {
      long i = this.field_75441_b.world.getGameTime();
      if (i - this.field_220720_k < 20L) {
         return false;
      } else {
         this.field_220720_k = i;
         LivingEntity livingentity = this.field_75441_b.getAttackTarget();
         if (livingentity == null) {
            return false;
         } else if (!livingentity.isAlive()) {
            return false;
         } else {
           if (canPenalize) {
               if (--this.delayCounter <= 0) {
                  this.path = this.field_75441_b.getNavigator().getPathToEntityLiving(livingentity);
                  this.delayCounter = 4 + this.field_75441_b.getRNG().nextInt(7);
                  return this.path != null;
               } else {
                  return true;
               }
            }
            this.path = this.field_75441_b.getNavigator().getPathToEntityLiving(livingentity);
            if (this.path != null) {
               return true;
            } else {
               return this.getAttackReachSqr(livingentity) >= this.field_75441_b.getDistanceSq(livingentity.posX, livingentity.getBoundingBox().minY, livingentity.posZ);
            }
         }
      }
   }

   /**
    * Returns whether an in-progress EntityAIBase should continue executing
    */
   public boolean shouldContinueExecuting() {
      LivingEntity livingentity = this.field_75441_b.getAttackTarget();
      if (livingentity == null) {
         return false;
      } else if (!livingentity.isAlive()) {
         return false;
      } else if (!this.longMemory) {
         return !this.field_75441_b.getNavigator().noPath();
      } else if (!this.field_75441_b.isWithinHomeDistanceFromPosition(new BlockPos(livingentity))) {
         return false;
      } else {
         return !(livingentity instanceof PlayerEntity) || !livingentity.isSpectator() && !((PlayerEntity)livingentity).isCreative();
      }
   }

   /**
    * Execute a one shot task or start executing a continuous task
    */
   public void startExecuting() {
      this.field_75441_b.getNavigator().setPath(this.path, this.speedTowardsTarget);
      this.field_75441_b.setAggroed(true);
      this.delayCounter = 0;
   }

   /**
    * Reset the task's internal state. Called when this task is interrupted by another one
    */
   public void resetTask() {
      LivingEntity livingentity = this.field_75441_b.getAttackTarget();
      if (!EntityPredicates.CAN_AI_TARGET.test(livingentity)) {
         this.field_75441_b.setAttackTarget((LivingEntity)null);
      }

      this.field_75441_b.setAggroed(false);
      this.field_75441_b.getNavigator().clearPath();
   }

   /**
    * Keep ticking a continuous task that has already been started
    */
   public void tick() {
      LivingEntity livingentity = this.field_75441_b.getAttackTarget();
      this.field_75441_b.getLookController().setLookPositionWithEntity(livingentity, 30.0F, 30.0F);
      double d0 = this.field_75441_b.getDistanceSq(livingentity.posX, livingentity.getBoundingBox().minY, livingentity.posZ);
      --this.delayCounter;
      if ((this.longMemory || this.field_75441_b.getEntitySenses().canSee(livingentity)) && this.delayCounter <= 0 && (this.targetX == 0.0D && this.targetY == 0.0D && this.targetZ == 0.0D || livingentity.getDistanceSq(this.targetX, this.targetY, this.targetZ) >= 1.0D || this.field_75441_b.getRNG().nextFloat() < 0.05F)) {
         this.targetX = livingentity.posX;
         this.targetY = livingentity.getBoundingBox().minY;
         this.targetZ = livingentity.posZ;
         this.delayCounter = 4 + this.field_75441_b.getRNG().nextInt(7);
         if (this.canPenalize) {
            this.delayCounter += failedPathFindingPenalty;
            if (this.field_75441_b.getNavigator().getPath() != null) {
               net.minecraft.pathfinding.PathPoint finalPathPoint = this.field_75441_b.getNavigator().getPath().getFinalPathPoint();
               if (finalPathPoint != null && livingentity.getDistanceSq(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < 1)
                  failedPathFindingPenalty = 0;
               else
                  failedPathFindingPenalty += 10;
            } else {
               failedPathFindingPenalty += 10;
            }
         }
         if (d0 > 1024.0D) {
            this.delayCounter += 10;
         } else if (d0 > 256.0D) {
            this.delayCounter += 5;
         }

         if (!this.field_75441_b.getNavigator().tryMoveToEntityLiving(livingentity, this.speedTowardsTarget)) {
            this.delayCounter += 15;
         }
      }

      this.attackTick = Math.max(this.attackTick - 1, 0);
      this.checkAndPerformAttack(livingentity, d0);
   }

   protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
      double d0 = this.getAttackReachSqr(enemy);
      if (distToEnemySqr <= d0 && this.attackTick <= 0) {
         this.attackTick = 20;
         this.field_75441_b.swingArm(Hand.MAIN_HAND);
         this.field_75441_b.attackEntityAsMob(enemy);
      }

   }

   protected double getAttackReachSqr(LivingEntity attackTarget) {
      return (double)(this.field_75441_b.getWidth() * 2.0F * this.field_75441_b.getWidth() * 2.0F + attackTarget.getWidth());
   }
}