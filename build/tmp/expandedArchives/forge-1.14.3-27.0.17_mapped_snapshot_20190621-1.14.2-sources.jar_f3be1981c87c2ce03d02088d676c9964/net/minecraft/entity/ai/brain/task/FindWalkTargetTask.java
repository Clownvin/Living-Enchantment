package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ServerWorld;

public class FindWalkTargetTask extends Task<CreatureEntity> {
   private final float field_220597_a;
   private final int field_223525_b;
   private final int field_223526_c;

   public FindWalkTargetTask(float p_i50336_1_) {
      this(p_i50336_1_, 10, 7);
   }

   public FindWalkTargetTask(float p_i51526_1_, int p_i51526_2_, int p_i51526_3_) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT));
      this.field_220597_a = p_i51526_1_;
      this.field_223525_b = p_i51526_2_;
      this.field_223526_c = p_i51526_3_;
   }

   protected void startExecuting(ServerWorld p_212831_1_, CreatureEntity p_212831_2_, long p_212831_3_) {
      BlockPos blockpos = new BlockPos(p_212831_2_);
      if (p_212831_1_.func_217483_b_(blockpos)) {
         this.func_220593_a(p_212831_2_);
      } else {
         SectionPos sectionpos = SectionPos.from(blockpos);
         SectionPos sectionpos1 = BrainUtil.func_220617_a(p_212831_1_, sectionpos, 2);
         if (sectionpos1 != sectionpos) {
            this.func_220594_a(p_212831_2_, sectionpos1);
         } else {
            this.func_220593_a(p_212831_2_);
         }
      }

   }

   private void func_220594_a(CreatureEntity p_220594_1_, SectionPos p_220594_2_) {
      BlockPos blockpos = p_220594_2_.getCenter();
      Optional<Vec3d> optional = Optional.ofNullable(RandomPositionGenerator.findRandomTargetBlockTowards(p_220594_1_, this.field_223525_b, this.field_223526_c, new Vec3d((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ())));
      p_220594_1_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, optional.map((p_220596_1_) -> {
         return new WalkTarget(p_220596_1_, this.field_220597_a, 0);
      }));
   }

   private void func_220593_a(CreatureEntity p_220593_1_) {
      Optional<Vec3d> optional = Optional.ofNullable(RandomPositionGenerator.getLandPos(p_220593_1_, this.field_223525_b, this.field_223526_c));
      p_220593_1_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, optional.map((p_220595_1_) -> {
         return new WalkTarget(p_220595_1_, this.field_220597_a, 0);
      }));
   }
}