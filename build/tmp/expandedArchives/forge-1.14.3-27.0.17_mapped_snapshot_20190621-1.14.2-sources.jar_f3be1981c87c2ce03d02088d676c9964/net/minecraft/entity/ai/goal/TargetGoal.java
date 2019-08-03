package net.minecraft.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public abstract class TargetGoal extends Goal {
   protected final MobEntity field_75299_d;
   protected final boolean shouldCheckSight;
   private final boolean nearbyOnly;
   private int targetSearchStatus;
   private int targetSearchDelay;
   private int targetUnseenTicks;
   protected LivingEntity target;
   protected int unseenMemoryTicks = 60;

   public TargetGoal(MobEntity p_i50308_1_, boolean p_i50308_2_) {
      this(p_i50308_1_, p_i50308_2_, false);
   }

   public TargetGoal(MobEntity p_i50309_1_, boolean p_i50309_2_, boolean p_i50309_3_) {
      this.field_75299_d = p_i50309_1_;
      this.shouldCheckSight = p_i50309_2_;
      this.nearbyOnly = p_i50309_3_;
   }

   /**
    * Returns whether an in-progress EntityAIBase should continue executing
    */
   public boolean shouldContinueExecuting() {
      LivingEntity livingentity = this.field_75299_d.getAttackTarget();
      if (livingentity == null) {
         livingentity = this.target;
      }

      if (livingentity == null) {
         return false;
      } else if (!livingentity.isAlive()) {
         return false;
      } else {
         Team team = this.field_75299_d.getTeam();
         Team team1 = livingentity.getTeam();
         if (team != null && team1 == team) {
            return false;
         } else {
            double d0 = this.getTargetDistance();
            if (this.field_75299_d.getDistanceSq(livingentity) > d0 * d0) {
               return false;
            } else {
               if (this.shouldCheckSight) {
                  if (this.field_75299_d.getEntitySenses().canSee(livingentity)) {
                     this.targetUnseenTicks = 0;
                  } else if (++this.targetUnseenTicks > this.unseenMemoryTicks) {
                     return false;
                  }
               }

               if (livingentity instanceof PlayerEntity && ((PlayerEntity)livingentity).abilities.disableDamage) {
                  return false;
               } else {
                  this.field_75299_d.setAttackTarget(livingentity);
                  return true;
               }
            }
         }
      }
   }

   protected double getTargetDistance() {
      IAttributeInstance iattributeinstance = this.field_75299_d.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
      return iattributeinstance == null ? 16.0D : iattributeinstance.getValue();
   }

   /**
    * Execute a one shot task or start executing a continuous task
    */
   public void startExecuting() {
      this.targetSearchStatus = 0;
      this.targetSearchDelay = 0;
      this.targetUnseenTicks = 0;
   }

   /**
    * Reset the task's internal state. Called when this task is interrupted by another one
    */
   public void resetTask() {
      this.field_75299_d.setAttackTarget((LivingEntity)null);
      this.target = null;
   }

   protected boolean func_220777_a(@Nullable LivingEntity p_220777_1_, EntityPredicate p_220777_2_) {
      if (p_220777_1_ == null) {
         return false;
      } else if (!p_220777_2_.canTarget(this.field_75299_d, p_220777_1_)) {
         return false;
      } else if (!this.field_75299_d.isWithinHomeDistanceFromPosition(new BlockPos(p_220777_1_))) {
         return false;
      } else {
         if (this.nearbyOnly) {
            if (--this.targetSearchDelay <= 0) {
               this.targetSearchStatus = 0;
            }

            if (this.targetSearchStatus == 0) {
               this.targetSearchStatus = this.canEasilyReach(p_220777_1_) ? 1 : 2;
            }

            if (this.targetSearchStatus == 2) {
               return false;
            }
         }

         return true;
      }
   }

   /**
    * Checks to see if this entity can find a short path to the given target.
    */
   private boolean canEasilyReach(LivingEntity target) {
      this.targetSearchDelay = 10 + this.field_75299_d.getRNG().nextInt(5);
      Path path = this.field_75299_d.getNavigator().getPathToEntityLiving(target);
      if (path == null) {
         return false;
      } else {
         PathPoint pathpoint = path.getFinalPathPoint();
         if (pathpoint == null) {
            return false;
         } else {
            int i = pathpoint.x - MathHelper.floor(target.posX);
            int j = pathpoint.z - MathHelper.floor(target.posZ);
            return (double)(i * i + j * j) <= 2.25D;
         }
      }
   }

   public TargetGoal setUnseenMemoryTicks(int p_190882_1_) {
      this.unseenMemoryTicks = p_190882_1_;
      return this;
   }
}