package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.ServerWorld;

public class ExpirePOITask extends Task<LivingEntity> {
   private final MemoryModuleType<GlobalPos> field_220591_a;
   private final Predicate<PointOfInterestType> field_220592_b;

   public ExpirePOITask(PointOfInterestType p_i50338_1_, MemoryModuleType<GlobalPos> p_i50338_2_) {
      super(ImmutableMap.of(p_i50338_2_, MemoryModuleStatus.VALUE_PRESENT));
      this.field_220592_b = p_i50338_1_.func_221045_c();
      this.field_220591_a = p_i50338_2_;
   }

   protected boolean shouldExecute(ServerWorld worldIn, LivingEntity owner) {
      GlobalPos globalpos = owner.getBrain().getMemory(this.field_220591_a).get();
      return Objects.equals(worldIn.getDimension().getType(), globalpos.getDimension()) && globalpos.getPos().withinDistance(owner.getPositionVec(), 5.0D);
   }

   protected void startExecuting(ServerWorld p_212831_1_, LivingEntity p_212831_2_, long p_212831_3_) {
      Brain<?> brain = p_212831_2_.getBrain();
      GlobalPos globalpos = brain.getMemory(this.field_220591_a).get();
      ServerWorld serverworld = p_212831_1_.getServer().getWorld(globalpos.getDimension());
      if (this.func_223020_a(serverworld, globalpos.getPos()) || this.func_223019_a(serverworld, globalpos.getPos(), p_212831_2_)) {
         brain.removeMemory(this.field_220591_a);
      }

   }

   private boolean func_223019_a(ServerWorld p_223019_1_, BlockPos p_223019_2_, LivingEntity p_223019_3_) {
      BlockState blockstate = p_223019_1_.getBlockState(p_223019_2_);
      return blockstate.getBlock().isIn(BlockTags.BEDS) && blockstate.get(BedBlock.OCCUPIED) && !p_223019_3_.isSleeping();
   }

   private boolean func_223020_a(ServerWorld p_223020_1_, BlockPos p_223020_2_) {
      return !p_223020_1_.func_217443_B().func_219138_a(p_223020_2_, this.field_220592_b);
   }
}