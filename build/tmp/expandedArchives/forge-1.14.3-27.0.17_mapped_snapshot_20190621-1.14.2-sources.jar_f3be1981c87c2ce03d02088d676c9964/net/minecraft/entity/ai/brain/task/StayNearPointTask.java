package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.ServerWorld;

public class StayNearPointTask extends Task<VillagerEntity> {
   private final MemoryModuleType<GlobalPos> field_220548_a;
   private final float field_220549_b;
   private final int field_220550_c;
   private final int field_220551_d;
   private final int field_223018_e;

   public StayNearPointTask(MemoryModuleType<GlobalPos> p_i51501_1_, float p_i51501_2_, int p_i51501_3_, int p_i51501_4_, int p_i51501_5_) {
      super(ImmutableMap.of(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleStatus.REGISTERED, MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT, p_i51501_1_, MemoryModuleStatus.VALUE_PRESENT));
      this.field_220548_a = p_i51501_1_;
      this.field_220549_b = p_i51501_2_;
      this.field_220550_c = p_i51501_3_;
      this.field_220551_d = p_i51501_4_;
      this.field_223018_e = p_i51501_5_;
   }

   protected void startExecuting(ServerWorld p_212831_1_, VillagerEntity p_212831_2_, long p_212831_3_) {
      Brain<?> brain = p_212831_2_.getBrain();
      brain.getMemory(this.field_220548_a).ifPresent((p_220545_6_) -> {
         if (!this.func_220546_a(p_212831_1_, p_212831_2_, p_220545_6_) && !this.func_223017_a(p_212831_1_, p_212831_2_)) {
            if (!this.func_220547_b(p_212831_1_, p_212831_2_, p_220545_6_)) {
               brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(p_220545_6_.getPos(), this.field_220549_b, this.field_220550_c));
            }
         } else {
            p_212831_2_.func_213742_a(this.field_220548_a);
            brain.removeMemory(this.field_220548_a);
            brain.setMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, p_212831_3_);
         }

      });
   }

   private boolean func_223017_a(ServerWorld p_223017_1_, VillagerEntity p_223017_2_) {
      Optional<Long> optional = p_223017_2_.getBrain().getMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
      if (optional.isPresent()) {
         return p_223017_1_.getGameTime() - optional.get() > (long)this.field_223018_e;
      } else {
         return false;
      }
   }

   private boolean func_220546_a(ServerWorld p_220546_1_, VillagerEntity p_220546_2_, GlobalPos p_220546_3_) {
      return p_220546_3_.getDimension() != p_220546_1_.getDimension().getType() || p_220546_3_.getPos().manhattanDistance(new BlockPos(p_220546_2_)) > this.field_220551_d;
   }

   private boolean func_220547_b(ServerWorld p_220547_1_, VillagerEntity p_220547_2_, GlobalPos p_220547_3_) {
      return p_220547_3_.getDimension() == p_220547_1_.getDimension().getType() && p_220547_3_.getPos().manhattanDistance(new BlockPos(p_220547_2_)) <= this.field_220550_c;
   }
}