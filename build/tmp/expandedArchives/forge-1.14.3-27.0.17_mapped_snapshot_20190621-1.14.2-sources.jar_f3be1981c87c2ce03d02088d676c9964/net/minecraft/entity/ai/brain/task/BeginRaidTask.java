package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.raid.Raid;

public class BeginRaidTask extends Task<LivingEntity> {
   public BeginRaidTask() {
      super(ImmutableMap.of());
   }

   protected boolean shouldExecute(ServerWorld worldIn, LivingEntity owner) {
      return worldIn.rand.nextInt(20) == 0;
   }

   protected void startExecuting(ServerWorld p_212831_1_, LivingEntity p_212831_2_, long p_212831_3_) {
      Brain<?> brain = p_212831_2_.getBrain();
      Raid raid = p_212831_1_.findRaid(new BlockPos(p_212831_2_));
      if (raid != null) {
         if (raid.func_221297_c() && !raid.func_221334_b()) {
            brain.setFallbackActivity(Activity.RAID);
            brain.switchTo(Activity.RAID);
         } else {
            brain.setFallbackActivity(Activity.PRE_RAID);
            brain.switchTo(Activity.PRE_RAID);
         }
      }

   }
}