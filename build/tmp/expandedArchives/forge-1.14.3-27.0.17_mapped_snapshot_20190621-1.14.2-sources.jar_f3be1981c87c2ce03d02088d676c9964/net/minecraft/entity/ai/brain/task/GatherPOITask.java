package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.ServerWorld;

public class GatherPOITask extends Task<LivingEntity> {
   private final PointOfInterestType field_220604_a;
   private final MemoryModuleType<GlobalPos> field_220605_b;
   private final boolean field_220606_c;
   private long field_220607_d;
   private final Long2LongMap field_223013_e = new Long2LongOpenHashMap();
   private int field_223014_f;

   public GatherPOITask(PointOfInterestType p_i50374_1_, MemoryModuleType<GlobalPos> p_i50374_2_, boolean p_i50374_3_) {
      super(ImmutableMap.of(p_i50374_2_, MemoryModuleStatus.VALUE_ABSENT));
      this.field_220604_a = p_i50374_1_;
      this.field_220605_b = p_i50374_2_;
      this.field_220606_c = p_i50374_3_;
   }

   protected boolean shouldExecute(ServerWorld worldIn, LivingEntity owner) {
      if (this.field_220606_c && owner.isChild()) {
         return false;
      } else {
         return worldIn.getGameTime() - this.field_220607_d >= 20L;
      }
   }

   protected void startExecuting(ServerWorld p_212831_1_, LivingEntity p_212831_2_, long p_212831_3_) {
      this.field_223014_f = 0;
      this.field_220607_d = p_212831_1_.getGameTime() + (long)p_212831_1_.getRandom().nextInt(20);
      CreatureEntity creatureentity = (CreatureEntity)p_212831_2_;
      PointOfInterestManager pointofinterestmanager = p_212831_1_.func_217443_B();
      Predicate<BlockPos> predicate = (p_220603_3_) -> {
         long i = p_220603_3_.toLong();
         if (this.field_223013_e.containsKey(i)) {
            return false;
         } else if (++this.field_223014_f >= 5) {
            return false;
         } else {
            BlockPos blockpos1;
            if (this.field_220604_a == PointOfInterestType.MEETING) {
               BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(p_220603_3_);
               this.func_223012_a(p_212831_1_, blockpos$mutableblockpos);
               blockpos$mutableblockpos.move(Direction.UP);
               blockpos1 = blockpos$mutableblockpos;
            } else {
               blockpos1 = p_220603_3_;
            }

            if (creatureentity.getBoundingBox().grow(2.0D).contains(new Vec3d(blockpos1))) {
               return true;
            } else {
               Path path = creatureentity.getNavigator().getPathToPos(blockpos1);
               boolean flag = path != null && path.func_222862_a(blockpos1);
               if (!flag) {
                  this.field_223013_e.put(i, this.field_220607_d + 40L);
               }

               return flag;
            }
         }
      };
      Optional<BlockPos> optional = pointofinterestmanager.func_219155_b(this.field_220604_a.func_221045_c(), predicate, new BlockPos(p_212831_2_), 48);
      if (optional.isPresent()) {
         BlockPos blockpos = optional.get();
         p_212831_2_.getBrain().setMemory(this.field_220605_b, GlobalPos.of(p_212831_1_.getDimension().getType(), blockpos));
         DebugPacketSender.func_218801_c(p_212831_1_, blockpos);
      } else if (this.field_223014_f < 5) {
         this.field_223013_e.long2LongEntrySet().removeIf((p_223011_1_) -> {
            return p_223011_1_.getLongValue() < this.field_220607_d;
         });
      }

   }

   private void func_223012_a(ServerWorld p_223012_1_, BlockPos.MutableBlockPos p_223012_2_) {
      while(true) {
         p_223012_2_.move(Direction.DOWN);
         if (!p_223012_1_.getBlockState(p_223012_2_).getCollisionShape(p_223012_1_, p_223012_2_).isEmpty() || p_223012_2_.getY() <= 0) {
            break;
         }
      }

   }
}