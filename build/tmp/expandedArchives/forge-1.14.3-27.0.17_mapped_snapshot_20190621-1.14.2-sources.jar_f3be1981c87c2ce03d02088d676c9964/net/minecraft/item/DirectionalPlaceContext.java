package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DirectionalPlaceContext extends BlockItemUseContext {
   private final Direction field_221537_g;

   public DirectionalPlaceContext(World p_i50051_1_, BlockPos p_i50051_2_, Direction p_i50051_3_, ItemStack p_i50051_4_, Direction p_i50051_5_) {
      super(p_i50051_1_, (PlayerEntity)null, Hand.MAIN_HAND, p_i50051_4_, new BlockRayTraceResult(new Vec3d((double)p_i50051_2_.getX() + 0.5D, (double)p_i50051_2_.getY(), (double)p_i50051_2_.getZ() + 0.5D), p_i50051_5_, p_i50051_2_, false));
      this.field_221537_g = p_i50051_3_;
   }

   public BlockPos getPos() {
      return this.rayTraceResult.getPos();
   }

   public boolean canPlace() {
      return this.world.getBlockState(this.rayTraceResult.getPos()).isReplaceable(this);
   }

   public boolean replacingClickedOnBlock() {
      return this.canPlace();
   }

   public Direction getNearestLookingDirection() {
      return Direction.DOWN;
   }

   public Direction[] getNearestLookingDirections() {
      switch(this.field_221537_g) {
      case DOWN:
      default:
         return new Direction[]{Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP};
      case UP:
         return new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
      case NORTH:
         return new Direction[]{Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.SOUTH};
      case SOUTH:
         return new Direction[]{Direction.DOWN, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.NORTH};
      case WEST:
         return new Direction[]{Direction.DOWN, Direction.WEST, Direction.SOUTH, Direction.UP, Direction.NORTH, Direction.EAST};
      case EAST:
         return new Direction[]{Direction.DOWN, Direction.EAST, Direction.SOUTH, Direction.UP, Direction.NORTH, Direction.WEST};
      }
   }

   public Direction getPlacementHorizontalFacing() {
      return this.field_221537_g.getAxis() == Direction.Axis.Y ? Direction.NORTH : this.field_221537_g;
   }

   public boolean isPlacerSneaking() {
      return false;
   }

   public float getPlacementYaw() {
      return (float)(this.field_221537_g.getHorizontalIndex() * 90);
   }
}