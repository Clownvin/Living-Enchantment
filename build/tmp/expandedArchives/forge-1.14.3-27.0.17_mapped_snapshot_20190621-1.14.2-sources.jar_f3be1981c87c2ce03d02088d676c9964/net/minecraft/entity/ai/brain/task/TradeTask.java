package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.EntityPosWrapper;
import net.minecraft.world.ServerWorld;

public class TradeTask extends Task<VillagerEntity> {
   private final float field_220476_a;

   public TradeTask(float p_i50359_1_) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED), Integer.MAX_VALUE);
      this.field_220476_a = p_i50359_1_;
   }

   protected boolean shouldExecute(ServerWorld worldIn, VillagerEntity owner) {
      PlayerEntity playerentity = owner.getCustomer();
      return owner.isAlive() && playerentity != null && !owner.isInWater() && !owner.velocityChanged && owner.getDistanceSq(playerentity) <= 16.0D && playerentity.openContainer != null;
   }

   protected boolean shouldContinueExecuting(ServerWorld p_212834_1_, VillagerEntity p_212834_2_, long p_212834_3_) {
      return this.shouldExecute(p_212834_1_, p_212834_2_);
   }

   protected void startExecuting(ServerWorld p_212831_1_, VillagerEntity p_212831_2_, long p_212831_3_) {
      this.func_220475_a(p_212831_2_);
   }

   protected void resetTask(ServerWorld p_212835_1_, VillagerEntity p_212835_2_, long p_212835_3_) {
      Brain<?> brain = p_212835_2_.getBrain();
      brain.removeMemory(MemoryModuleType.WALK_TARGET);
      brain.removeMemory(MemoryModuleType.LOOK_TARGET);
   }

   protected void updateTask(ServerWorld worldIn, VillagerEntity owner, long gameTime) {
      this.func_220475_a(owner);
   }

   protected boolean isTimedOut(long gameTime) {
      return false;
   }

   private void func_220475_a(VillagerEntity p_220475_1_) {
      EntityPosWrapper entityposwrapper = new EntityPosWrapper(p_220475_1_.getCustomer());
      Brain<?> brain = p_220475_1_.getBrain();
      brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(entityposwrapper, this.field_220476_a, 2));
      brain.setMemory(MemoryModuleType.LOOK_TARGET, entityposwrapper);
   }
}