package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorld;

public class WalkRandomlyTask extends Task<CreatureEntity> {
   private final float field_220431_a;

   public WalkRandomlyTask(float p_i50364_1_) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT));
      this.field_220431_a = p_i50364_1_;
   }

   protected boolean shouldExecute(ServerWorld worldIn, CreatureEntity owner) {
      return !worldIn.func_217337_f(new BlockPos(owner));
   }

   protected void startExecuting(ServerWorld p_212831_1_, CreatureEntity p_212831_2_, long p_212831_3_) {
      BlockPos blockpos = new BlockPos(p_212831_2_);
      List<BlockPos> list = BlockPos.getAllInBox(blockpos.add(-1, -1, -1), blockpos.add(1, 1, 1)).map(BlockPos::toImmutable).collect(Collectors.toList());
      Collections.shuffle(list);
      Optional<BlockPos> optional = list.stream().filter((p_220428_1_) -> {
         return !p_212831_1_.func_217337_f(p_220428_1_);
      }).filter((p_220427_2_) -> {
         return p_212831_1_.func_217400_a(p_220427_2_, p_212831_2_);
      }).filter((p_220429_2_) -> {
         return p_212831_1_.areCollisionShapesEmpty(p_212831_2_);
      }).findFirst();
      optional.ifPresent((p_220430_2_) -> {
         p_212831_2_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(p_220430_2_, this.field_220431_a, 0));
      });
   }
}