package net.minecraft.tileentity;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.PistonType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PistonTileEntity extends TileEntity implements ITickableTileEntity {
   private BlockState pistonState;
   private Direction pistonFacing;
   private boolean extending;
   private boolean shouldHeadBeRendered;
   private static final ThreadLocal<Direction> MOVING_ENTITY = new ThreadLocal<Direction>() {
      protected Direction initialValue() {
         return null;
      }
   };
   private float progress;
   private float lastProgress;
   private long lastTicked;

   public PistonTileEntity() {
      super(TileEntityType.PISTON);
   }

   public PistonTileEntity(BlockState pistonStateIn, Direction pistonFacingIn, boolean extendingIn, boolean shouldHeadBeRenderedIn) {
      this();
      this.pistonState = pistonStateIn;
      this.pistonFacing = pistonFacingIn;
      this.extending = extendingIn;
      this.shouldHeadBeRendered = shouldHeadBeRenderedIn;
   }

   /**
    * Get an NBT compound to sync to the client with SPacketChunkData, used for initial loading of the chunk or when
    * many blocks change at once. This compound comes back to you clientside in {@link handleUpdateTag}
    */
   public CompoundNBT getUpdateTag() {
      return this.write(new CompoundNBT());
   }

   /**
    * Returns true if a piston is extending
    */
   public boolean isExtending() {
      return this.extending;
   }

   public Direction getFacing() {
      return this.pistonFacing;
   }

   public boolean shouldPistonHeadBeRendered() {
      return this.shouldHeadBeRendered;
   }

   /**
    * Get interpolated progress value (between lastProgress and progress) given the fractional time between ticks as an
    * argument
    */
   public float getProgress(float ticks) {
      if (ticks > 1.0F) {
         ticks = 1.0F;
      }

      return MathHelper.lerp(ticks, this.lastProgress, this.progress);
   }

   @OnlyIn(Dist.CLIENT)
   public float getOffsetX(float ticks) {
      return (float)this.pistonFacing.getXOffset() * this.getExtendedProgress(this.getProgress(ticks));
   }

   @OnlyIn(Dist.CLIENT)
   public float getOffsetY(float ticks) {
      return (float)this.pistonFacing.getYOffset() * this.getExtendedProgress(this.getProgress(ticks));
   }

   @OnlyIn(Dist.CLIENT)
   public float getOffsetZ(float ticks) {
      return (float)this.pistonFacing.getZOffset() * this.getExtendedProgress(this.getProgress(ticks));
   }

   private float getExtendedProgress(float p_184320_1_) {
      return this.extending ? p_184320_1_ - 1.0F : 1.0F - p_184320_1_;
   }

   private BlockState getCollisionRelatedBlockState() {
      return !this.isExtending() && this.shouldPistonHeadBeRendered() && this.pistonState.getBlock() instanceof PistonBlock ? Blocks.PISTON_HEAD.getDefaultState().with(PistonHeadBlock.TYPE, this.pistonState.getBlock() == Blocks.STICKY_PISTON ? PistonType.STICKY : PistonType.DEFAULT).with(PistonHeadBlock.FACING, this.pistonState.get(PistonBlock.FACING)) : this.pistonState;
   }

   private void moveCollidedEntities(float p_184322_1_) {
      Direction direction = this.getMotionDirection();
      double d0 = (double)(p_184322_1_ - this.progress);
      VoxelShape voxelshape = this.getCollisionRelatedBlockState().getCollisionShape(this.world, this.getPos());
      if (!voxelshape.isEmpty()) {
         List<AxisAlignedBB> list = voxelshape.toBoundingBoxList();
         AxisAlignedBB axisalignedbb = this.moveByPositionAndProgress(this.getMinMaxPiecesAABB(list));
         List<Entity> list1 = this.world.getEntitiesWithinAABBExcludingEntity((Entity)null, this.getMovementArea(axisalignedbb, direction, d0).union(axisalignedbb));
         if (!list1.isEmpty()) {
            boolean flag = this.pistonState.getBlock().isStickyBlock(this.pistonState);

            for(int i = 0; i < list1.size(); ++i) {
               Entity entity = list1.get(i);
               if (entity.getPushReaction() != PushReaction.IGNORE) {
                  if (flag) {
                     Vec3d vec3d = entity.getMotion();
                     double d1 = vec3d.x;
                     double d2 = vec3d.y;
                     double d3 = vec3d.z;
                     switch(direction.getAxis()) {
                     case X:
                        d1 = (double)direction.getXOffset();
                        break;
                     case Y:
                        d2 = (double)direction.getYOffset();
                        break;
                     case Z:
                        d3 = (double)direction.getZOffset();
                     }

                     entity.setMotion(d1, d2, d3);
                  }

                  double d4 = 0.0D;

                  for(int j = 0; j < list.size(); ++j) {
                     AxisAlignedBB axisalignedbb2 = this.getMovementArea(this.moveByPositionAndProgress(list.get(j)), direction, d0);
                     AxisAlignedBB axisalignedbb1 = entity.getBoundingBox();
                     if (axisalignedbb2.intersects(axisalignedbb1)) {
                        d4 = Math.max(d4, this.getMovement(axisalignedbb2, direction, axisalignedbb1));
                        if (d4 >= d0) {
                           break;
                        }
                     }
                  }

                  if (!(d4 <= 0.0D)) {
                     d4 = Math.min(d4, d0) + 0.01D;
                     MOVING_ENTITY.set(direction);
                     entity.move(MoverType.PISTON, new Vec3d(d4 * (double)direction.getXOffset(), d4 * (double)direction.getYOffset(), d4 * (double)direction.getZOffset()));
                     MOVING_ENTITY.set((Direction)null);
                     if (!this.extending && this.shouldHeadBeRendered) {
                        this.fixEntityWithinPistonBase(entity, direction, d0);
                     }
                  }
               }
            }

         }
      }
   }

   public Direction getMotionDirection() {
      return this.extending ? this.pistonFacing : this.pistonFacing.getOpposite();
   }

   private AxisAlignedBB getMinMaxPiecesAABB(List<AxisAlignedBB> p_191515_1_) {
      double d0 = 0.0D;
      double d1 = 0.0D;
      double d2 = 0.0D;
      double d3 = 1.0D;
      double d4 = 1.0D;
      double d5 = 1.0D;

      for(AxisAlignedBB axisalignedbb : p_191515_1_) {
         d0 = Math.min(axisalignedbb.minX, d0);
         d1 = Math.min(axisalignedbb.minY, d1);
         d2 = Math.min(axisalignedbb.minZ, d2);
         d3 = Math.max(axisalignedbb.maxX, d3);
         d4 = Math.max(axisalignedbb.maxY, d4);
         d5 = Math.max(axisalignedbb.maxZ, d5);
      }

      return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
   }

   private double getMovement(AxisAlignedBB p_190612_1_, Direction facing, AxisAlignedBB p_190612_3_) {
      switch(facing.getAxis()) {
      case X:
         return getDeltaX(p_190612_1_, facing, p_190612_3_);
      case Y:
      default:
         return getDeltaY(p_190612_1_, facing, p_190612_3_);
      case Z:
         return getDeltaZ(p_190612_1_, facing, p_190612_3_);
      }
   }

   private AxisAlignedBB moveByPositionAndProgress(AxisAlignedBB p_190607_1_) {
      double d0 = (double)this.getExtendedProgress(this.progress);
      return p_190607_1_.offset((double)this.pos.getX() + d0 * (double)this.pistonFacing.getXOffset(), (double)this.pos.getY() + d0 * (double)this.pistonFacing.getYOffset(), (double)this.pos.getZ() + d0 * (double)this.pistonFacing.getZOffset());
   }

   private AxisAlignedBB getMovementArea(AxisAlignedBB p_190610_1_, Direction p_190610_2_, double p_190610_3_) {
      double d0 = p_190610_3_ * (double)p_190610_2_.getAxisDirection().getOffset();
      double d1 = Math.min(d0, 0.0D);
      double d2 = Math.max(d0, 0.0D);
      switch(p_190610_2_) {
      case WEST:
         return new AxisAlignedBB(p_190610_1_.minX + d1, p_190610_1_.minY, p_190610_1_.minZ, p_190610_1_.minX + d2, p_190610_1_.maxY, p_190610_1_.maxZ);
      case EAST:
         return new AxisAlignedBB(p_190610_1_.maxX + d1, p_190610_1_.minY, p_190610_1_.minZ, p_190610_1_.maxX + d2, p_190610_1_.maxY, p_190610_1_.maxZ);
      case DOWN:
         return new AxisAlignedBB(p_190610_1_.minX, p_190610_1_.minY + d1, p_190610_1_.minZ, p_190610_1_.maxX, p_190610_1_.minY + d2, p_190610_1_.maxZ);
      case UP:
      default:
         return new AxisAlignedBB(p_190610_1_.minX, p_190610_1_.maxY + d1, p_190610_1_.minZ, p_190610_1_.maxX, p_190610_1_.maxY + d2, p_190610_1_.maxZ);
      case NORTH:
         return new AxisAlignedBB(p_190610_1_.minX, p_190610_1_.minY, p_190610_1_.minZ + d1, p_190610_1_.maxX, p_190610_1_.maxY, p_190610_1_.minZ + d2);
      case SOUTH:
         return new AxisAlignedBB(p_190610_1_.minX, p_190610_1_.minY, p_190610_1_.maxZ + d1, p_190610_1_.maxX, p_190610_1_.maxY, p_190610_1_.maxZ + d2);
      }
   }

   private void fixEntityWithinPistonBase(Entity p_190605_1_, Direction p_190605_2_, double p_190605_3_) {
      AxisAlignedBB axisalignedbb = p_190605_1_.getBoundingBox();
      AxisAlignedBB axisalignedbb1 = VoxelShapes.fullCube().getBoundingBox().offset(this.pos);
      if (axisalignedbb.intersects(axisalignedbb1)) {
         Direction direction = p_190605_2_.getOpposite();
         double d0 = this.getMovement(axisalignedbb1, direction, axisalignedbb) + 0.01D;
         double d1 = this.getMovement(axisalignedbb1, direction, axisalignedbb.intersect(axisalignedbb1)) + 0.01D;
         if (Math.abs(d0 - d1) < 0.01D) {
            d0 = Math.min(d0, p_190605_3_) + 0.01D;
            MOVING_ENTITY.set(p_190605_2_);
            p_190605_1_.move(MoverType.PISTON, new Vec3d(d0 * (double)direction.getXOffset(), d0 * (double)direction.getYOffset(), d0 * (double)direction.getZOffset()));
            MOVING_ENTITY.set((Direction)null);
         }
      }

   }

   private static double getDeltaX(AxisAlignedBB p_190611_0_, Direction facing, AxisAlignedBB p_190611_2_) {
      return facing.getAxisDirection() == Direction.AxisDirection.POSITIVE ? p_190611_0_.maxX - p_190611_2_.minX : p_190611_2_.maxX - p_190611_0_.minX;
   }

   private static double getDeltaY(AxisAlignedBB p_190608_0_, Direction facing, AxisAlignedBB p_190608_2_) {
      return facing.getAxisDirection() == Direction.AxisDirection.POSITIVE ? p_190608_0_.maxY - p_190608_2_.minY : p_190608_2_.maxY - p_190608_0_.minY;
   }

   private static double getDeltaZ(AxisAlignedBB p_190604_0_, Direction facing, AxisAlignedBB p_190604_2_) {
      return facing.getAxisDirection() == Direction.AxisDirection.POSITIVE ? p_190604_0_.maxZ - p_190604_2_.minZ : p_190604_2_.maxZ - p_190604_0_.minZ;
   }

   public BlockState getPistonState() {
      return this.pistonState;
   }

   /**
    * removes a piston's tile entity (and if the piston is moving, stops it)
    */
   public void clearPistonTileEntity() {
      if (this.lastProgress < 1.0F && this.world != null) {
         this.progress = 1.0F;
         this.lastProgress = this.progress;
         this.world.removeTileEntity(this.pos);
         this.remove();
         if (this.world.getBlockState(this.pos).getBlock() == Blocks.MOVING_PISTON) {
            BlockState blockstate;
            if (this.shouldHeadBeRendered) {
               blockstate = Blocks.AIR.getDefaultState();
            } else {
               blockstate = Block.getValidBlockForPosition(this.pistonState, this.world, this.pos);
            }

            this.world.setBlockState(this.pos, blockstate, 3);
            this.world.neighborChanged(this.pos, blockstate.getBlock(), this.pos);
         }
      }

   }

   public void tick() {
      this.lastTicked = this.world.getGameTime();
      this.lastProgress = this.progress;
      if (this.lastProgress >= 1.0F) {
         this.world.removeTileEntity(this.pos);
         this.remove();
         if (this.pistonState != null && this.world.getBlockState(this.pos).getBlock() == Blocks.MOVING_PISTON) {
            BlockState blockstate = Block.getValidBlockForPosition(this.pistonState, this.world, this.pos);
            if (blockstate.isAir()) {
               this.world.setBlockState(this.pos, this.pistonState, 84);
               Block.replaceBlock(this.pistonState, blockstate, this.world, this.pos, 3);
            } else {
               if (blockstate.has(BlockStateProperties.WATERLOGGED) && blockstate.get(BlockStateProperties.WATERLOGGED)) {
                  blockstate = blockstate.with(BlockStateProperties.WATERLOGGED, Boolean.valueOf(false));
               }

               this.world.setBlockState(this.pos, blockstate, 67);
               this.world.neighborChanged(this.pos, blockstate.getBlock(), this.pos);
            }
         }

      } else {
         float f = this.progress + 0.5F;
         this.moveCollidedEntities(f);
         this.progress = f;
         if (this.progress >= 1.0F) {
            this.progress = 1.0F;
         }

      }
   }

   public void read(CompoundNBT compound) {
      super.read(compound);
      this.pistonState = NBTUtil.readBlockState(compound.getCompound("blockState"));
      this.pistonFacing = Direction.byIndex(compound.getInt("facing"));
      this.progress = compound.getFloat("progress");
      this.lastProgress = this.progress;
      this.extending = compound.getBoolean("extending");
      this.shouldHeadBeRendered = compound.getBoolean("source");
   }

   public CompoundNBT write(CompoundNBT compound) {
      super.write(compound);
      compound.put("blockState", NBTUtil.writeBlockState(this.pistonState));
      compound.putInt("facing", this.pistonFacing.getIndex());
      compound.putFloat("progress", this.lastProgress);
      compound.putBoolean("extending", this.extending);
      compound.putBoolean("source", this.shouldHeadBeRendered);
      return compound;
   }

   public VoxelShape getCollisionShape(IBlockReader p_195508_1_, BlockPos p_195508_2_) {
      VoxelShape voxelshape;
      if (!this.extending && this.shouldHeadBeRendered) {
         voxelshape = this.pistonState.with(PistonBlock.EXTENDED, Boolean.valueOf(true)).getCollisionShape(p_195508_1_, p_195508_2_);
      } else {
         voxelshape = VoxelShapes.empty();
      }

      Direction direction = MOVING_ENTITY.get();
      if ((double)this.progress < 1.0D && direction == this.getMotionDirection()) {
         return voxelshape;
      } else {
         BlockState blockstate;
         if (this.shouldPistonHeadBeRendered()) {
            blockstate = Blocks.PISTON_HEAD.getDefaultState().with(PistonHeadBlock.FACING, this.pistonFacing).with(PistonHeadBlock.SHORT, Boolean.valueOf(this.extending != 1.0F - this.progress < 4.0F));
         } else {
            blockstate = this.pistonState;
         }

         float f = this.getExtendedProgress(this.progress);
         double d0 = (double)((float)this.pistonFacing.getXOffset() * f);
         double d1 = (double)((float)this.pistonFacing.getYOffset() * f);
         double d2 = (double)((float)this.pistonFacing.getZOffset() * f);
         return VoxelShapes.or(voxelshape, blockstate.getCollisionShape(p_195508_1_, p_195508_2_).withOffset(d0, d1, d2));
      }
   }

   public long getLastTicked() {
      return this.lastTicked;
   }
}