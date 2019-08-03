package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.ServerWorld;

public class WalkToHouseTask extends Task<LivingEntity> {
   private final float field_220524_a;
   private long field_220525_b;

   public WalkToHouseTask(float p_i50353_1_) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT, MemoryModuleType.HOME, MemoryModuleStatus.VALUE_ABSENT));
      this.field_220524_a = p_i50353_1_;
   }

   protected boolean shouldExecute(ServerWorld worldIn, LivingEntity owner) {
      if (worldIn.getGameTime() - this.field_220525_b < 40L) {
         return false;
      } else {
         CreatureEntity creatureentity = (CreatureEntity)owner;
         PointOfInterestManager pointofinterestmanager = worldIn.func_217443_B();
         Optional<BlockPos> optional = pointofinterestmanager.func_219147_b(PointOfInterestType.HOME.func_221045_c(), (p_220522_0_) -> {
            return true;
         }, new BlockPos(owner), 48, PointOfInterestManager.Status.ANY);
         return optional.isPresent() && !(optional.get().distanceSq(new Vec3i(creatureentity.posX, creatureentity.posY, creatureentity.posZ)) <= 4.0D);
      }
   }

   protected void startExecuting(ServerWorld p_212831_1_, LivingEntity p_212831_2_, long p_212831_3_) {
      this.field_220525_b = p_212831_1_.getGameTime();
      CreatureEntity creatureentity = (CreatureEntity)p_212831_2_;
      PointOfInterestManager pointofinterestmanager = p_212831_1_.func_217443_B();
      Predicate<BlockPos> predicate = (p_220523_2_) -> {
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(p_220523_2_);
         if (p_212831_1_.getBlockState(p_220523_2_.down()).isAir()) {
            blockpos$mutableblockpos.move(Direction.DOWN);
         }

         while(p_212831_1_.getBlockState(blockpos$mutableblockpos).isAir() && blockpos$mutableblockpos.getY() >= 0) {
            blockpos$mutableblockpos.move(Direction.DOWN);
         }

         Path path = creatureentity.getNavigator().getPathToPos(blockpos$mutableblockpos);
         return path != null && path.func_222862_a(blockpos$mutableblockpos);
      };
      pointofinterestmanager.func_219147_b(PointOfInterestType.HOME.func_221045_c(), predicate, new BlockPos(p_212831_2_), 48, PointOfInterestManager.Status.ANY).ifPresent((p_220521_3_) -> {
         p_212831_2_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(p_220521_3_, this.field_220524_a, 1));
         DebugPacketSender.func_218801_c(p_212831_1_, p_220521_3_);
      });
   }
}