package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.world.ServerWorld;

public class ClearHurtTask extends Task<VillagerEntity> {
   public ClearHurtTask() {
      super(ImmutableMap.of());
   }

   protected void startExecuting(ServerWorld p_212831_1_, VillagerEntity p_212831_2_, long p_212831_3_) {
      boolean flag = PanicTask.func_220512_b(p_212831_2_) || PanicTask.func_220513_a(p_212831_2_) || func_220394_a(p_212831_2_);
      if (!flag) {
         p_212831_2_.getBrain().removeMemory(MemoryModuleType.HURT_BY);
         p_212831_2_.getBrain().removeMemory(MemoryModuleType.HURT_BY_ENTITY);
         p_212831_2_.getBrain().updateActivity(p_212831_1_.getDayTime(), p_212831_1_.getGameTime());
      }

   }

   private static boolean func_220394_a(VillagerEntity p_220394_0_) {
      return p_220394_0_.getBrain().getMemory(MemoryModuleType.HURT_BY_ENTITY).filter((p_223523_1_) -> {
         return p_223523_1_.getDistanceSq(p_220394_0_) <= 36.0D;
      }).isPresent();
   }
}