package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.raid.Raid;

public class ForgetRaidTask extends Task<LivingEntity> {
   public ForgetRaidTask() {
      super(ImmutableMap.of());
   }

   protected boolean shouldExecute(ServerWorld worldIn, LivingEntity owner) {
      return worldIn.rand.nextInt(20) == 0;
   }

   protected void startExecuting(ServerWorld p_212831_1_, LivingEntity p_212831_2_, long p_212831_3_) {
      Brain<?> brain = p_212831_2_.getBrain();
      Raid raid = p_212831_1_.findRaid(new BlockPos(p_212831_2_));
      if (raid == null || raid.isStopped() || raid.isLoss()) {
         brain.setFallbackActivity(Activity.IDLE);
         brain.updateActivity(p_212831_1_.getDayTime(), p_212831_1_.getGameTime());
      }

   }
}