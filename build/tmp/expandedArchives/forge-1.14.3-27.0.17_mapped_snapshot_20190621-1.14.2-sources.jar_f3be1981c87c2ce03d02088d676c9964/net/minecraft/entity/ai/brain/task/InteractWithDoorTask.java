package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.pathfinding.Path;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.ServerWorld;

public class InteractWithDoorTask extends Task<LivingEntity> {
   public InteractWithDoorTask() {
      super(ImmutableMap.of(MemoryModuleType.PATH, MemoryModuleStatus.VALUE_PRESENT, MemoryModuleType.INTERACTABLE_DOORS, MemoryModuleStatus.VALUE_PRESENT));
   }

   protected void startExecuting(ServerWorld p_212831_1_, LivingEntity p_212831_2_, long p_212831_3_) {
      Brain<?> brain = p_212831_2_.getBrain();
      Path path = brain.getMemory(MemoryModuleType.PATH).get();
      List<GlobalPos> list = brain.getMemory(MemoryModuleType.INTERACTABLE_DOORS).get();
      List<BlockPos> list1 = path.func_215746_d().stream().map((p_220435_0_) -> {
         return new BlockPos(p_220435_0_.x, p_220435_0_.y, p_220435_0_.z);
      }).collect(Collectors.toList());
      Set<BlockPos> set = this.func_220436_a(p_212831_1_, list, list1);
      int i = path.getCurrentPathIndex() - 1;
      this.func_220434_a(p_212831_1_, list1, set, i);
   }

   private Set<BlockPos> func_220436_a(ServerWorld p_220436_1_, List<GlobalPos> p_220436_2_, List<BlockPos> p_220436_3_) {
      return p_220436_2_.stream().filter((p_220432_1_) -> {
         return p_220432_1_.getDimension() == p_220436_1_.getDimension().getType();
      }).map(GlobalPos::getPos).filter(p_220436_3_::contains).collect(Collectors.toSet());
   }

   private void func_220434_a(ServerWorld p_220434_1_, List<BlockPos> p_220434_2_, Set<BlockPos> p_220434_3_, int p_220434_4_) {
      p_220434_3_.forEach((p_220433_3_) -> {
         int i = p_220434_2_.indexOf(p_220433_3_);
         BlockState blockstate = p_220434_1_.getBlockState(p_220433_3_);
         Block block = blockstate.getBlock();
         if (BlockTags.WOODEN_DOORS.contains(block) && block instanceof DoorBlock) {
            ((DoorBlock)block).toggleDoor(p_220434_1_, p_220433_3_, i >= p_220434_4_);
         }

      });
   }
}