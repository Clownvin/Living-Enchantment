package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.ServerWorld;

public class GolemLastSeenSensor extends Sensor<LivingEntity> {
   public GolemLastSeenSensor() {
      this(200);
   }

   public GolemLastSeenSensor(int p_i51525_1_) {
      super(p_i51525_1_);
   }

   protected void update(ServerWorld p_212872_1_, LivingEntity p_212872_2_) {
      func_223545_a(p_212872_1_.getGameTime(), p_212872_2_);
   }

   public Set<MemoryModuleType<?>> getUsedMemories() {
      return ImmutableSet.of(MemoryModuleType.MOBS);
   }

   public static void func_223545_a(long p_223545_0_, LivingEntity p_223545_2_) {
      Brain<?> brain = p_223545_2_.getBrain();
      Optional<List<LivingEntity>> optional = brain.getMemory(MemoryModuleType.MOBS);
      if (optional.isPresent()) {
         boolean flag = optional.get().stream().anyMatch((p_223546_0_) -> {
            return p_223546_0_.getType().equals(EntityType.IRON_GOLEM);
         });
         if (flag) {
            brain.setMemory(MemoryModuleType.field_223542_x, p_223545_0_);
         }

      }
   }
}