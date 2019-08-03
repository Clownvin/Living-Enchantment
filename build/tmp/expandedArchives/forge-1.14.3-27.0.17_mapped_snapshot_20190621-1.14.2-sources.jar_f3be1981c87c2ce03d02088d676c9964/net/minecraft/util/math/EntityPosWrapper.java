package net.minecraft.util.math;

import java.util.List;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;

public class EntityPosWrapper implements IPosWrapper {
   private final LivingEntity entity;

   public EntityPosWrapper(LivingEntity entity) {
      this.entity = entity;
   }

   public BlockPos func_220608_a() {
      return new BlockPos(this.entity);
   }

   public Vec3d func_220609_b() {
      return new Vec3d(this.entity.posX, this.entity.posY + (double)this.entity.getEyeHeight(), this.entity.posZ);
   }

   public boolean func_220610_a(LivingEntity p_220610_1_) {
      Optional<List<LivingEntity>> optional = p_220610_1_.getBrain().getMemory(MemoryModuleType.VISIBLE_MOBS);
      return this.entity.isAlive() && optional.isPresent() && optional.get().contains(this.entity);
   }

   public String toString() {
      return "EntityPosWrapper for " + this.entity;
   }
}