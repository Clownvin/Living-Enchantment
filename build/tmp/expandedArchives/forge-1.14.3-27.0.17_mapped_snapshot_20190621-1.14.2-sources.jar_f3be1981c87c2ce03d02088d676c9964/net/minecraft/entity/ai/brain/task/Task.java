package net.minecraft.entity.ai.brain.task;

import java.util.Map;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.ServerWorld;

public abstract class Task<E extends LivingEntity> {
   private final Map<MemoryModuleType<?>, MemoryModuleStatus> requiredMemoryState;
   private Task.Status status = Task.Status.STOPPED;
   private long stopTime;
   private final int durationMin;
   private final int durationMax;

   public Task(Map<MemoryModuleType<?>, MemoryModuleStatus> p_i51504_1_) {
      this(p_i51504_1_, 60);
   }

   public Task(Map<MemoryModuleType<?>, MemoryModuleStatus> p_i51505_1_, int p_i51505_2_) {
      this(p_i51505_1_, p_i51505_2_, p_i51505_2_);
   }

   public Task(Map<MemoryModuleType<?>, MemoryModuleStatus> p_i51506_1_, int p_i51506_2_, int p_i51506_3_) {
      this.durationMin = p_i51506_2_;
      this.durationMax = p_i51506_3_;
      this.requiredMemoryState = p_i51506_1_;
   }

   public Task.Status getStatus() {
      return this.status;
   }

   public final boolean start(ServerWorld worldIn, E owner, long gameTime) {
      if (this.hasRequiredMemories(owner) && this.shouldExecute(worldIn, owner)) {
         this.status = Task.Status.RUNNING;
         int i = this.durationMin + worldIn.getRandom().nextInt(this.durationMax + 1 - this.durationMin);
         this.stopTime = gameTime + (long)i;
         this.startExecuting(worldIn, owner, gameTime);
         return true;
      } else {
         return false;
      }
   }

   protected void startExecuting(ServerWorld p_212831_1_, E p_212831_2_, long p_212831_3_) {
   }

   public final void tick(ServerWorld p_220377_1_, E p_220377_2_, long gameTime) {
      if (!this.isTimedOut(gameTime) && this.shouldContinueExecuting(p_220377_1_, p_220377_2_, gameTime)) {
         this.updateTask(p_220377_1_, p_220377_2_, gameTime);
      } else {
         this.stop(p_220377_1_, p_220377_2_, gameTime);
      }

   }

   protected void updateTask(ServerWorld worldIn, E owner, long gameTime) {
   }

   public final void stop(ServerWorld p_220380_1_, E p_220380_2_, long p_220380_3_) {
      this.status = Task.Status.STOPPED;
      this.resetTask(p_220380_1_, p_220380_2_, p_220380_3_);
   }

   protected void resetTask(ServerWorld p_212835_1_, E p_212835_2_, long p_212835_3_) {
   }

   protected boolean shouldContinueExecuting(ServerWorld p_212834_1_, E p_212834_2_, long p_212834_3_) {
      return false;
   }

   protected boolean isTimedOut(long gameTime) {
      return gameTime > this.stopTime;
   }

   protected boolean shouldExecute(ServerWorld worldIn, E owner) {
      return true;
   }

   public String toString() {
      return this.getClass().getSimpleName();
   }

   private boolean hasRequiredMemories(E owner) {
      return this.requiredMemoryState.entrySet().stream().allMatch((p_220379_1_) -> {
         MemoryModuleType<?> memorymoduletype = p_220379_1_.getKey();
         MemoryModuleStatus memorymodulestatus = p_220379_1_.getValue();
         return owner.getBrain().hasMemory(memorymoduletype, memorymodulestatus);
      });
   }

   public static enum Status {
      STOPPED,
      RUNNING;
   }
}