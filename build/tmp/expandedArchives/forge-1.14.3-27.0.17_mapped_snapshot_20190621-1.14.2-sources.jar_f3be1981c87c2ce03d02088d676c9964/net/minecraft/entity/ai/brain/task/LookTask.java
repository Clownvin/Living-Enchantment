package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.ServerWorld;

public class LookTask extends Task<MobEntity> {
   public LookTask(int p_i50358_1_, int p_i50358_2_) {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.VALUE_PRESENT), p_i50358_1_, p_i50358_2_);
   }

   protected boolean shouldContinueExecuting(ServerWorld p_212834_1_, MobEntity p_212834_2_, long p_212834_3_) {
      return p_212834_2_.getBrain().getMemory(MemoryModuleType.LOOK_TARGET).filter((p_220485_1_) -> {
         return p_220485_1_.func_220610_a(p_212834_2_);
      }).isPresent();
   }

   protected void resetTask(ServerWorld p_212835_1_, MobEntity p_212835_2_, long p_212835_3_) {
      p_212835_2_.getBrain().removeMemory(MemoryModuleType.LOOK_TARGET);
   }

   protected void updateTask(ServerWorld worldIn, MobEntity owner, long gameTime) {
      owner.getBrain().getMemory(MemoryModuleType.LOOK_TARGET).ifPresent((p_220484_1_) -> {
         owner.getLookController().func_220674_a(p_220484_1_.func_220609_b());
      });
   }
}