package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.ServerWorld;

public class NearestBedSensor extends Sensor<MobEntity> {
   public NearestBedSensor() {
      super(200);
   }

   public Set<MemoryModuleType<?>> getUsedMemories() {
      return ImmutableSet.of(MemoryModuleType.NEAREST_BED);
   }

   protected void update(ServerWorld p_212872_1_, MobEntity p_212872_2_) {
      if (p_212872_2_.isChild()) {
         p_212872_2_.getBrain().setMemory(MemoryModuleType.NEAREST_BED, this.func_220977_b(p_212872_1_, p_212872_2_));
      }
   }

   private Optional<BlockPos> func_220977_b(ServerWorld p_220977_1_, MobEntity p_220977_2_) {
      PointOfInterestManager pointofinterestmanager = p_220977_1_.func_217443_B();
      Predicate<BlockPos> predicate = (p_220978_1_) -> {
         if (p_220978_1_.equals(new BlockPos(p_220977_2_))) {
            return true;
         } else {
            Path path = p_220977_2_.getNavigator().getPathToPos(p_220978_1_);
            return path != null && path.func_222862_a(p_220978_1_);
         }
      };
      return pointofinterestmanager.func_219147_b(PointOfInterestType.HOME.func_221045_c(), predicate, new BlockPos(p_220977_2_), 16, PointOfInterestManager.Status.ANY);
   }
}