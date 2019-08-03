package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.ServerWorld;

public class UpdateActivityTask extends Task<LivingEntity> {
   public UpdateActivityTask() {
      super(ImmutableMap.of());
   }

   protected void startExecuting(ServerWorld p_212831_1_, LivingEntity p_212831_2_, long p_212831_3_) {
      p_212831_2_.getBrain().updateActivity(p_212831_1_.getDayTime(), p_212831_1_.getGameTime());
   }
}