package net.minecraft.entity.ai.goal;

import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class WaterAvoidingRandomFlyingGoal extends WaterAvoidingRandomWalkingGoal {
   public WaterAvoidingRandomFlyingGoal(CreatureEntity p_i47413_1_, double p_i47413_2_) {
      super(p_i47413_1_, p_i47413_2_);
   }

   @Nullable
   protected Vec3d getPosition() {
      Vec3d vec3d = null;
      if (this.field_75457_a.isInWater()) {
         vec3d = RandomPositionGenerator.getLandPos(this.field_75457_a, 15, 15);
      }

      if (this.field_75457_a.getRNG().nextFloat() >= this.probability) {
         vec3d = this.getTreePos();
      }

      return vec3d == null ? super.getPosition() : vec3d;
   }

   @Nullable
   private Vec3d getTreePos() {
      BlockPos blockpos = new BlockPos(this.field_75457_a);
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      BlockPos.MutableBlockPos blockpos$mutableblockpos1 = new BlockPos.MutableBlockPos();
      Iterable<BlockPos> iterable = BlockPos.getAllInBoxMutable(MathHelper.floor(this.field_75457_a.posX - 3.0D), MathHelper.floor(this.field_75457_a.posY - 6.0D), MathHelper.floor(this.field_75457_a.posZ - 3.0D), MathHelper.floor(this.field_75457_a.posX + 3.0D), MathHelper.floor(this.field_75457_a.posY + 6.0D), MathHelper.floor(this.field_75457_a.posZ + 3.0D));
      Iterator iterator = iterable.iterator();

      BlockPos blockpos1;
      while(true) {
         if (!iterator.hasNext()) {
            return null;
         }

         blockpos1 = (BlockPos)iterator.next();
         if (!blockpos.equals(blockpos1)) {
            Block block = this.field_75457_a.world.getBlockState(blockpos$mutableblockpos1.setPos(blockpos1).move(Direction.DOWN)).getBlock();
            boolean flag = block instanceof LeavesBlock || block.isIn(BlockTags.LOGS);
            if (flag && this.field_75457_a.world.isAirBlock(blockpos1) && this.field_75457_a.world.isAirBlock(blockpos$mutableblockpos.setPos(blockpos1).move(Direction.UP))) {
               break;
            }
         }
      }

      return new Vec3d((double)blockpos1.getX(), (double)blockpos1.getY(), (double)blockpos1.getZ());
   }
}