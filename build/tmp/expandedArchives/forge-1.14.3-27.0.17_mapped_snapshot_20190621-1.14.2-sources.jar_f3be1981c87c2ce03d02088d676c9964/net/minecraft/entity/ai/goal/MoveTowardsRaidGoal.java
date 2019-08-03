package net.minecraft.entity.ai.goal;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.raid.RaidManager;

public class MoveTowardsRaidGoal<T extends AbstractRaiderEntity> extends Goal {
   private final T field_220744_a;

   public MoveTowardsRaidGoal(T p_i50323_1_) {
      this.field_220744_a = p_i50323_1_;
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
   }

   /**
    * Returns whether the EntityAIBase should begin execution.
    */
   public boolean shouldExecute() {
      return this.field_220744_a.getAttackTarget() == null && !this.field_220744_a.isBeingRidden() && this.field_220744_a.isRaidActive() && !this.field_220744_a.getRaid().func_221319_a() && !((ServerWorld)this.field_220744_a.world).func_217483_b_(new BlockPos(this.field_220744_a));
   }

   /**
    * Returns whether an in-progress EntityAIBase should continue executing
    */
   public boolean shouldContinueExecuting() {
      return this.field_220744_a.isRaidActive() && !this.field_220744_a.getRaid().func_221319_a() && this.field_220744_a.world instanceof ServerWorld && !((ServerWorld)this.field_220744_a.world).func_217483_b_(new BlockPos(this.field_220744_a));
   }

   /**
    * Keep ticking a continuous task that has already been started
    */
   public void tick() {
      if (this.field_220744_a.isRaidActive()) {
         Raid raid = this.field_220744_a.getRaid();
         if (this.field_220744_a.ticksExisted % 20 == 0) {
            this.func_220743_a(raid);
         }

         if (!this.field_220744_a.hasPath()) {
            Vec3d vec3d = new Vec3d(raid.func_221304_t());
            Vec3d vec3d1 = new Vec3d(this.field_220744_a.posX, this.field_220744_a.posY, this.field_220744_a.posZ);
            Vec3d vec3d2 = vec3d1.subtract(vec3d);
            vec3d = vec3d2.scale(0.4D).add(vec3d);
            Vec3d vec3d3 = vec3d.subtract(vec3d1).normalize().scale(10.0D).add(vec3d1);
            BlockPos blockpos = new BlockPos(vec3d3);
            blockpos = this.field_220744_a.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockpos);
            if (!this.field_220744_a.getNavigator().tryMoveToXYZ((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), 1.0D)) {
               this.func_220741_g();
            }
         }
      }

   }

   private void func_220743_a(Raid p_220743_1_) {
      if (p_220743_1_.isActive()) {
         Set<AbstractRaiderEntity> set = Sets.newHashSet();
         List<AbstractRaiderEntity> list = this.field_220744_a.world.getEntitiesWithinAABB(AbstractRaiderEntity.class, this.field_220744_a.getBoundingBox().grow(16.0D), (p_220742_1_) -> {
            return !p_220742_1_.isRaidActive() && RaidManager.func_215165_a(p_220742_1_, p_220743_1_);
         });
         set.addAll(list);

         for(AbstractRaiderEntity abstractraiderentity : set) {
            p_220743_1_.func_221317_a(p_220743_1_.func_221315_l(), abstractraiderentity, (BlockPos)null, true);
         }
      }

   }

   private void func_220741_g() {
      Random random = this.field_220744_a.getRNG();
      BlockPos blockpos = this.field_220744_a.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (new BlockPos(this.field_220744_a)).add(-8 + random.nextInt(16), 0, -8 + random.nextInt(16)));
      this.field_220744_a.getNavigator().tryMoveToXYZ((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), 1.0D);
   }
}