package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.gen.Heightmap;

public class MoveToSkylightTask extends Task<LivingEntity> {
   private final float field_220494_a;

   public MoveToSkylightTask(float p_i50357_1_) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT));
      this.field_220494_a = p_i50357_1_;
   }

   protected void startExecuting(ServerWorld p_212831_1_, LivingEntity p_212831_2_, long p_212831_3_) {
      Optional<Vec3d> optional = Optional.ofNullable(this.func_220493_b(p_212831_1_, p_212831_2_));
      if (optional.isPresent()) {
         p_212831_2_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, optional.map((p_220492_1_) -> {
            return new WalkTarget(p_220492_1_, this.field_220494_a, 0);
         }));
      }

   }

   protected boolean shouldExecute(ServerWorld worldIn, LivingEntity owner) {
      return !worldIn.func_217337_f(new BlockPos(owner.posX, owner.getBoundingBox().minY, owner.posZ));
   }

   @Nullable
   private Vec3d func_220493_b(ServerWorld p_220493_1_, LivingEntity p_220493_2_) {
      Random random = p_220493_2_.getRNG();
      BlockPos blockpos = new BlockPos(p_220493_2_.posX, p_220493_2_.getBoundingBox().minY, p_220493_2_.posZ);

      for(int i = 0; i < 10; ++i) {
         BlockPos blockpos1 = blockpos.add(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);
         if (func_223015_b(p_220493_1_, p_220493_2_)) {
            return new Vec3d((double)blockpos1.getX(), (double)blockpos1.getY(), (double)blockpos1.getZ());
         }
      }

      return null;
   }

   public static boolean func_223015_b(ServerWorld p_223015_0_, LivingEntity p_223015_1_) {
      return p_223015_0_.func_217337_f(new BlockPos(p_223015_1_)) && (double)p_223015_0_.getHeight(Heightmap.Type.MOTION_BLOCKING, new BlockPos(p_223015_1_)).getY() <= p_223015_1_.posY;
   }
}