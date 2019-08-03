package net.minecraft.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ServerWorld;

public class MoveTowardsVillageGoal extends RandomWalkingGoal {
   public MoveTowardsVillageGoal(CreatureEntity p_i50325_1_, double p_i50325_2_) {
      super(p_i50325_1_, p_i50325_2_, 10);
   }

   /**
    * Returns whether the EntityAIBase should begin execution.
    */
   public boolean shouldExecute() {
      ServerWorld serverworld = (ServerWorld)this.field_75457_a.world;
      BlockPos blockpos = new BlockPos(this.field_75457_a);
      return serverworld.func_217483_b_(blockpos) ? false : super.shouldExecute();
   }

   @Nullable
   protected Vec3d getPosition() {
      ServerWorld serverworld = (ServerWorld)this.field_75457_a.world;
      BlockPos blockpos = new BlockPos(this.field_75457_a);
      SectionPos sectionpos = SectionPos.from(blockpos);
      SectionPos sectionpos1 = BrainUtil.func_220617_a(serverworld, sectionpos, 2);
      if (sectionpos1 != sectionpos) {
         BlockPos blockpos1 = sectionpos1.getCenter();
         return RandomPositionGenerator.findRandomTargetBlockTowards(this.field_75457_a, 10, 7, new Vec3d((double)blockpos1.getX(), (double)blockpos1.getY(), (double)blockpos1.getZ()));
      } else {
         return null;
      }
   }
}