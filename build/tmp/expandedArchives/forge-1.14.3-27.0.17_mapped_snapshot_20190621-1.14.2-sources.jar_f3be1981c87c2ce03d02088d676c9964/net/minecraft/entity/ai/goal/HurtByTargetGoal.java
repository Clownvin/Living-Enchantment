package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.math.AxisAlignedBB;

public class HurtByTargetGoal extends TargetGoal {
   private static final EntityPredicate field_220795_a = (new EntityPredicate()).setLineOfSiteRequired().setUseInvisibilityCheck();
   private boolean entityCallsForHelp;
   private int revengeTimerOld;
   private final Class<?>[] excludedReinforcementTypes;
   private Class<?>[] field_220797_i;

   public HurtByTargetGoal(CreatureEntity p_i50317_1_, Class<?>... p_i50317_2_) {
      super(p_i50317_1_, true);
      this.excludedReinforcementTypes = p_i50317_2_;
      this.setMutexFlags(EnumSet.of(Goal.Flag.TARGET));
   }

   /**
    * Returns whether the EntityAIBase should begin execution.
    */
   public boolean shouldExecute() {
      int i = this.field_75299_d.getRevengeTimer();
      LivingEntity livingentity = this.field_75299_d.getRevengeTarget();
      if (i != this.revengeTimerOld && livingentity != null) {
         for(Class<?> oclass : this.excludedReinforcementTypes) {
            if (oclass.isAssignableFrom(livingentity.getClass())) {
               return false;
            }
         }

         return this.func_220777_a(livingentity, field_220795_a);
      } else {
         return false;
      }
   }

   public HurtByTargetGoal setCallsForHelp(Class<?>... p_220794_1_) {
      this.entityCallsForHelp = true;
      this.field_220797_i = p_220794_1_;
      return this;
   }

   /**
    * Execute a one shot task or start executing a continuous task
    */
   public void startExecuting() {
      this.field_75299_d.setAttackTarget(this.field_75299_d.getRevengeTarget());
      this.target = this.field_75299_d.getAttackTarget();
      this.revengeTimerOld = this.field_75299_d.getRevengeTimer();
      this.unseenMemoryTicks = 300;
      if (this.entityCallsForHelp) {
         this.alertOthers();
      }

      super.startExecuting();
   }

   protected void alertOthers() {
      double d0 = this.getTargetDistance();
      List<MobEntity> list = this.field_75299_d.world.getEntitiesWithinAABB(this.field_75299_d.getClass(), (new AxisAlignedBB(this.field_75299_d.posX, this.field_75299_d.posY, this.field_75299_d.posZ, this.field_75299_d.posX + 1.0D, this.field_75299_d.posY + 1.0D, this.field_75299_d.posZ + 1.0D)).grow(d0, 10.0D, d0));
      Iterator iterator = list.iterator();

      while(true) {
         MobEntity mobentity;
         while(true) {
            if (!iterator.hasNext()) {
               return;
            }

            mobentity = (MobEntity)iterator.next();
            if (this.field_75299_d != mobentity && mobentity.getAttackTarget() == null && (!(this.field_75299_d instanceof TameableEntity) || ((TameableEntity)this.field_75299_d).getOwner() == ((TameableEntity)mobentity).getOwner()) && !mobentity.isOnSameTeam(this.field_75299_d.getRevengeTarget())) {
               if (this.field_220797_i == null) {
                  break;
               }

               boolean flag = false;

               for(Class<?> oclass : this.field_220797_i) {
                  if (mobentity.getClass() == oclass) {
                     flag = true;
                     break;
                  }
               }

               if (!flag) {
                  break;
               }
            }
         }

         this.setAttackTarget(mobentity, this.field_75299_d.getRevengeTarget());
      }
   }

   protected void setAttackTarget(MobEntity mobIn, LivingEntity targetIn) {
      mobIn.setAttackTarget(targetIn);
   }
}