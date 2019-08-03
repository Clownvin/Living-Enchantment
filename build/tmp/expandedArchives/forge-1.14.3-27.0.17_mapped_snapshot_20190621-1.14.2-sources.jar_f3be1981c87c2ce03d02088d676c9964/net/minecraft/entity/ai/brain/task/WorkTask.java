package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ServerWorld;

public class WorkTask extends Task<CreatureEntity> {
   private final MemoryModuleType<GlobalPos> field_220565_a;
   private long field_220566_b;
   private final int field_220567_c;

   public WorkTask(MemoryModuleType<GlobalPos> p_i50342_1_, int p_i50342_2_) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.REGISTERED, p_i50342_1_, MemoryModuleStatus.VALUE_PRESENT));
      this.field_220565_a = p_i50342_1_;
      this.field_220567_c = p_i50342_2_;
   }

   protected boolean shouldExecute(ServerWorld worldIn, CreatureEntity owner) {
      Optional<GlobalPos> optional = owner.getBrain().getMemory(this.field_220565_a);
      return optional.isPresent() && Objects.equals(worldIn.getDimension().getType(), optional.get().getDimension()) && optional.get().getPos().withinDistance(owner.getPositionVec(), (double)this.field_220567_c);
   }

   protected void startExecuting(ServerWorld p_212831_1_, CreatureEntity p_212831_2_, long p_212831_3_) {
      if (p_212831_3_ > this.field_220566_b) {
         Optional<Vec3d> optional = Optional.ofNullable(RandomPositionGenerator.getLandPos(p_212831_2_, 8, 6));
         p_212831_2_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, optional.map((p_220564_0_) -> {
            return new WalkTarget(p_220564_0_, 0.4F, 1);
         }));
         this.field_220566_b = p_212831_3_ + 180L;
      }

   }
}