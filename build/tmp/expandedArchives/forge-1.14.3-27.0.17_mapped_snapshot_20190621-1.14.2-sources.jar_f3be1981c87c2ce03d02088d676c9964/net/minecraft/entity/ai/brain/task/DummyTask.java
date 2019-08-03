package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.ServerWorld;

public class DummyTask extends Task<LivingEntity> {
   public DummyTask(int p_i50369_1_, int p_i50369_2_) {
      super(ImmutableMap.of(), p_i50369_1_, p_i50369_2_);
   }

   protected boolean shouldContinueExecuting(ServerWorld p_212834_1_, LivingEntity p_212834_2_, long p_212834_3_) {
      return true;
   }
}