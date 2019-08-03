package net.minecraft.entity.ai.goal;

import net.minecraft.block.BlockState;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;

public class FollowOwnerFlyingGoal extends FollowOwnerGoal {
   public FollowOwnerFlyingGoal(TameableEntity tameableIn, double followSpeedIn, float minDistIn, float maxDistIn) {
      super(tameableIn, followSpeedIn, minDistIn, maxDistIn);
   }

   protected boolean canTeleportToBlock(BlockPos pos) {
      BlockState blockstate = this.world.getBlockState(pos);
      return (blockstate.func_215682_a(this.world, pos, this.tameable) || blockstate.isIn(BlockTags.LEAVES)) && this.world.isAirBlock(pos.up()) && this.world.isAirBlock(pos.up(2));
   }
}