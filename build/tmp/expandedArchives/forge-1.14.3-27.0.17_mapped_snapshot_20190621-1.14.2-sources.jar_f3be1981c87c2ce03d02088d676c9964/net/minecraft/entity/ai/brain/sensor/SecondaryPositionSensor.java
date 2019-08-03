package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.dimension.DimensionType;

public class SecondaryPositionSensor extends Sensor<VillagerEntity> {
   public SecondaryPositionSensor() {
      super(40);
   }

   protected void update(ServerWorld p_212872_1_, VillagerEntity p_212872_2_) {
      DimensionType dimensiontype = p_212872_1_.getDimension().getType();
      BlockPos blockpos = new BlockPos(p_212872_2_);
      List<GlobalPos> list = Lists.newArrayList();
      int i = 4;

      for(int j = -4; j <= 4; ++j) {
         for(int k = -2; k <= 2; ++k) {
            for(int l = -4; l <= 4; ++l) {
               BlockPos blockpos1 = blockpos.add(j, k, l);
               if (p_212872_2_.getVillagerData().getProfession().func_221150_d().contains(p_212872_1_.getBlockState(blockpos1).getBlock())) {
                  list.add(GlobalPos.of(dimensiontype, blockpos1));
               }
            }
         }
      }

      Brain<?> brain = p_212872_2_.getBrain();
      if (!list.isEmpty()) {
         brain.setMemory(MemoryModuleType.SECONDARY_JOB_SITE, list);
      } else {
         brain.removeMemory(MemoryModuleType.SECONDARY_JOB_SITE);
      }

   }

   public Set<MemoryModuleType<?>> getUsedMemories() {
      return ImmutableSet.of(MemoryModuleType.SECONDARY_JOB_SITE);
   }
}