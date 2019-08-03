package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ServerWorld;

public class FleeTask extends Task<CreatureEntity> {
   private final MemoryModuleType<? extends Entity> field_220541_a;
   private final float field_220542_b;

   public FleeTask(MemoryModuleType<? extends Entity> p_i50346_1_, float p_i50346_2_) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT, p_i50346_1_, MemoryModuleStatus.VALUE_PRESENT));
      this.field_220541_a = p_i50346_1_;
      this.field_220542_b = p_i50346_2_;
   }

   protected boolean shouldExecute(ServerWorld worldIn, CreatureEntity owner) {
      Entity entity = owner.getBrain().getMemory(this.field_220541_a).get();
      return owner.getDistanceSq(entity) < 36.0D;
   }

   protected void startExecuting(ServerWorld p_212831_1_, CreatureEntity p_212831_2_, long p_212831_3_) {
      Entity entity = p_212831_2_.getBrain().getMemory(this.field_220541_a).get();
      func_220540_a(p_212831_2_, entity, this.field_220542_b);
   }

   public static void func_220540_a(CreatureEntity p_220540_0_, Entity p_220540_1_, float p_220540_2_) {
      for(int i = 0; i < 10; ++i) {
         Vec3d vec3d = new Vec3d(p_220540_1_.posX, p_220540_1_.posY, p_220540_1_.posZ);
         Vec3d vec3d1 = RandomPositionGenerator.func_223548_b(p_220540_0_, 16, 7, vec3d);
         if (vec3d1 != null) {
            p_220540_0_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3d1, p_220540_2_, 0));
            return;
         }
      }

   }
}