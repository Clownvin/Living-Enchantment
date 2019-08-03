package net.minecraft.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.Vec3d;

public class WaterAvoidingRandomWalkingGoal extends RandomWalkingGoal {
   protected final float probability;

   public WaterAvoidingRandomWalkingGoal(CreatureEntity creature, double speedIn) {
      this(creature, speedIn, 0.001F);
   }

   public WaterAvoidingRandomWalkingGoal(CreatureEntity creature, double speedIn, float probabilityIn) {
      super(creature, speedIn);
      this.probability = probabilityIn;
   }

   @Nullable
   protected Vec3d getPosition() {
      if (this.field_75457_a.isInWaterOrBubbleColumn()) {
         Vec3d vec3d = RandomPositionGenerator.getLandPos(this.field_75457_a, 15, 7);
         return vec3d == null ? super.getPosition() : vec3d;
      } else {
         return this.field_75457_a.getRNG().nextFloat() >= this.probability ? RandomPositionGenerator.getLandPos(this.field_75457_a, 10, 7) : super.getPosition();
      }
   }
}