package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;

public class NearestAttackableTargetGoal<T extends LivingEntity> extends TargetGoal {
   protected final Class<T> targetClass;
   protected final int targetChance;
   protected LivingEntity field_75309_a;
   protected EntityPredicate field_220779_d;

   public NearestAttackableTargetGoal(MobEntity p_i50313_1_, Class<T> p_i50313_2_, boolean p_i50313_3_) {
      this(p_i50313_1_, p_i50313_2_, p_i50313_3_, false);
   }

   public NearestAttackableTargetGoal(MobEntity p_i50314_1_, Class<T> p_i50314_2_, boolean p_i50314_3_, boolean p_i50314_4_) {
      this(p_i50314_1_, p_i50314_2_, 10, p_i50314_3_, p_i50314_4_, (Predicate<LivingEntity>)null);
   }

   public NearestAttackableTargetGoal(MobEntity p_i50315_1_, Class<T> p_i50315_2_, int p_i50315_3_, boolean p_i50315_4_, boolean p_i50315_5_, @Nullable Predicate<LivingEntity> p_i50315_6_) {
      super(p_i50315_1_, p_i50315_4_, p_i50315_5_);
      this.targetClass = p_i50315_2_;
      this.targetChance = p_i50315_3_;
      this.setMutexFlags(EnumSet.of(Goal.Flag.TARGET));
      this.field_220779_d = (new EntityPredicate()).setDistance(this.getTargetDistance()).setCustomPredicate(p_i50315_6_);
   }

   /**
    * Returns whether the EntityAIBase should begin execution.
    */
   public boolean shouldExecute() {
      if (this.targetChance > 0 && this.field_75299_d.getRNG().nextInt(this.targetChance) != 0) {
         return false;
      } else {
         this.func_220778_g();
         return this.field_75309_a != null;
      }
   }

   protected AxisAlignedBB getTargetableArea(double targetDistance) {
      return this.field_75299_d.getBoundingBox().grow(targetDistance, 4.0D, targetDistance);
   }

   protected void func_220778_g() {
      if (this.targetClass != PlayerEntity.class && this.targetClass != ServerPlayerEntity.class) {
         this.field_75309_a = this.field_75299_d.world.<T>func_217360_a(this.targetClass, this.field_220779_d, this.field_75299_d, this.field_75299_d.posX, this.field_75299_d.posY + (double)this.field_75299_d.getEyeHeight(), this.field_75299_d.posZ, this.getTargetableArea(this.getTargetDistance()));
      } else {
         this.field_75309_a = this.field_75299_d.world.func_217372_a(this.field_220779_d, this.field_75299_d, this.field_75299_d.posX, this.field_75299_d.posY + (double)this.field_75299_d.getEyeHeight(), this.field_75299_d.posZ);
      }

   }

   /**
    * Execute a one shot task or start executing a continuous task
    */
   public void startExecuting() {
      this.field_75299_d.setAttackTarget(this.field_75309_a);
      super.startExecuting();
   }
}