package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.util.LongSerializable;
import net.minecraft.util.math.BlockPosWrapper;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.ServerWorld;

public class SpawnGolemTask extends Task<VillagerEntity> {
   private int field_220600_a;
   private boolean field_220601_b;

   public SpawnGolemTask() {
      super(ImmutableMap.of(MemoryModuleType.JOB_SITE, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.LOOK_TARGET, MemoryModuleStatus.REGISTERED));
   }

   protected boolean shouldExecute(ServerWorld worldIn, VillagerEntity owner) {
      return this.func_220599_a(worldIn.getDayTime() % 24000L, owner.func_213763_er());
   }

   protected void resetTask(ServerWorld p_212835_1_, VillagerEntity p_212835_2_, long p_212835_3_) {
      this.field_220601_b = false;
      this.field_220600_a = 0;
      p_212835_2_.getBrain().removeMemory(MemoryModuleType.LOOK_TARGET);
   }

   protected void updateTask(ServerWorld worldIn, VillagerEntity owner, long gameTime) {
      Brain<VillagerEntity> brain = owner.getBrain();
      brain.setMemory(MemoryModuleType.field_223544_z, LongSerializable.func_223463_a(gameTime));
      if (!this.field_220601_b) {
         owner.func_213766_ei();
         this.field_220601_b = true;
         owner.playWorkstationSound();
         brain.getMemory(MemoryModuleType.JOB_SITE).ifPresent((p_220598_1_) -> {
            brain.setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosWrapper(p_220598_1_.getPos()));
         });
      }

      ++this.field_220600_a;
   }

   protected boolean shouldContinueExecuting(ServerWorld p_212834_1_, VillagerEntity p_212834_2_, long p_212834_3_) {
      Optional<GlobalPos> optional = p_212834_2_.getBrain().getMemory(MemoryModuleType.JOB_SITE);
      if (!optional.isPresent()) {
         return false;
      } else {
         GlobalPos globalpos = optional.get();
         return this.field_220600_a < 100 && Objects.equals(globalpos.getDimension(), p_212834_1_.getDimension().getType()) && globalpos.getPos().withinDistance(p_212834_2_.getPositionVec(), 1.73D);
      }
   }

   private boolean func_220599_a(long p_220599_1_, long p_220599_3_) {
      return p_220599_3_ == 0L || p_220599_1_ < p_220599_3_ || p_220599_1_ > p_220599_3_ + 3500L;
   }
}