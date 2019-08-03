package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.function.Predicate;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.Vec3d;

public class AvoidEntityGoal<T extends LivingEntity> extends Goal {
   protected final CreatureEntity entity;
   private final double farSpeed;
   private final double nearSpeed;
   protected T field_75376_d;
   protected final float avoidDistance;
   protected Path path;
   protected final PathNavigator navigation;
   protected final Class<T> classToAvoid;
   protected final Predicate<LivingEntity> avoidTargetSelector;
   protected final Predicate<LivingEntity> field_203784_k;
   private final EntityPredicate field_220872_k;

   public AvoidEntityGoal(CreatureEntity entityIn, Class<T> classToAvoidIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn) {
      this(entityIn, classToAvoidIn, (p_200828_0_) -> {
         return true;
      }, avoidDistanceIn, farSpeedIn, nearSpeedIn, EntityPredicates.CAN_AI_TARGET::test);
   }

   public AvoidEntityGoal(CreatureEntity p_i48859_1_, Class<T> p_i48859_2_, Predicate<LivingEntity> p_i48859_3_, float p_i48859_4_, double p_i48859_5_, double p_i48859_7_, Predicate<LivingEntity> p_i48859_9_) {
      this.entity = p_i48859_1_;
      this.classToAvoid = p_i48859_2_;
      this.avoidTargetSelector = p_i48859_3_;
      this.avoidDistance = p_i48859_4_;
      this.farSpeed = p_i48859_5_;
      this.nearSpeed = p_i48859_7_;
      this.field_203784_k = p_i48859_9_;
      this.navigation = p_i48859_1_.getNavigator();
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
      this.field_220872_k = (new EntityPredicate()).setDistance((double)p_i48859_4_).setCustomPredicate(p_i48859_9_.and(p_i48859_3_));
   }

   public AvoidEntityGoal(CreatureEntity p_i48860_1_, Class<T> p_i48860_2_, float p_i48860_3_, double p_i48860_4_, double p_i48860_6_, Predicate<LivingEntity> p_i48860_8_) {
      this(p_i48860_1_, p_i48860_2_, (p_203782_0_) -> {
         return true;
      }, p_i48860_3_, p_i48860_4_, p_i48860_6_, p_i48860_8_);
   }

   /**
    * Returns whether the EntityAIBase should begin execution.
    */
   public boolean shouldExecute() {
      this.field_75376_d = this.entity.world.func_217360_a(this.classToAvoid, this.field_220872_k, this.entity, this.entity.posX, this.entity.posY, this.entity.posZ, this.entity.getBoundingBox().grow((double)this.avoidDistance, 3.0D, (double)this.avoidDistance));
      if (this.field_75376_d == null) {
         return false;
      } else {
         Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.entity, 16, 7, new Vec3d(this.field_75376_d.posX, this.field_75376_d.posY, this.field_75376_d.posZ));
         if (vec3d == null) {
            return false;
         } else if (this.field_75376_d.getDistanceSq(vec3d.x, vec3d.y, vec3d.z) < this.field_75376_d.getDistanceSq(this.entity)) {
            return false;
         } else {
            this.path = this.navigation.getPathToXYZ(vec3d.x, vec3d.y, vec3d.z);
            return this.path != null;
         }
      }
   }

   /**
    * Returns whether an in-progress EntityAIBase should continue executing
    */
   public boolean shouldContinueExecuting() {
      return !this.navigation.noPath();
   }

   /**
    * Execute a one shot task or start executing a continuous task
    */
   public void startExecuting() {
      this.navigation.setPath(this.path, this.farSpeed);
   }

   /**
    * Reset the task's internal state. Called when this task is interrupted by another one
    */
   public void resetTask() {
      this.field_75376_d = null;
   }

   /**
    * Keep ticking a continuous task that has already been started
    */
   public void tick() {
      if (this.entity.getDistanceSq(this.field_75376_d) < 49.0D) {
         this.entity.getNavigator().setSpeed(this.nearSpeed);
      } else {
         this.entity.getNavigator().setSpeed(this.farSpeed);
      }

   }
}