package net.minecraft.block;

import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class VineBlock extends Block implements net.minecraftforge.common.IShearable {
   public static final BooleanProperty UP = SixWayBlock.UP;
   public static final BooleanProperty NORTH = SixWayBlock.NORTH;
   public static final BooleanProperty EAST = SixWayBlock.EAST;
   public static final BooleanProperty SOUTH = SixWayBlock.SOUTH;
   public static final BooleanProperty WEST = SixWayBlock.WEST;
   public static final Map<Direction, BooleanProperty> FACING_TO_PROPERTY_MAP = SixWayBlock.FACING_TO_PROPERTY_MAP.entrySet().stream().filter((p_199782_0_) -> {
      return p_199782_0_.getKey() != Direction.DOWN;
   }).collect(Util.toMapCollector());
   protected static final VoxelShape UP_AABB = Block.makeCuboidShape(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape WEST_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);
   protected static final VoxelShape EAST_AABB = Block.makeCuboidShape(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape NORTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);
   protected static final VoxelShape SOUTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);

   public VineBlock(Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(UP, Boolean.valueOf(false)).with(NORTH, Boolean.valueOf(false)).with(EAST, Boolean.valueOf(false)).with(SOUTH, Boolean.valueOf(false)).with(WEST, Boolean.valueOf(false)));
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      VoxelShape voxelshape = VoxelShapes.empty();
      if (state.get(UP)) {
         voxelshape = VoxelShapes.or(voxelshape, UP_AABB);
      }

      if (state.get(NORTH)) {
         voxelshape = VoxelShapes.or(voxelshape, NORTH_AABB);
      }

      if (state.get(EAST)) {
         voxelshape = VoxelShapes.or(voxelshape, EAST_AABB);
      }

      if (state.get(SOUTH)) {
         voxelshape = VoxelShapes.or(voxelshape, SOUTH_AABB);
      }

      if (state.get(WEST)) {
         voxelshape = VoxelShapes.or(voxelshape, WEST_AABB);
      }

      return voxelshape;
   }

   public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
      return this.func_196543_i(this.func_196545_h(state, worldIn, pos));
   }

   private boolean func_196543_i(BlockState p_196543_1_) {
      return this.func_208496_w(p_196543_1_) > 0;
   }

   private int func_208496_w(BlockState p_208496_1_) {
      int i = 0;

      for(BooleanProperty booleanproperty : FACING_TO_PROPERTY_MAP.values()) {
         if (p_208496_1_.get(booleanproperty)) {
            ++i;
         }
      }

      return i;
   }

   private boolean func_196541_a(IBlockReader p_196541_1_, BlockPos p_196541_2_, Direction p_196541_3_) {
      if (p_196541_3_ == Direction.DOWN) {
         return false;
      } else {
         BlockPos blockpos = p_196541_2_.offset(p_196541_3_);
         if (canAttachTo(p_196541_1_, blockpos, p_196541_3_)) {
            return true;
         } else if (p_196541_3_.getAxis() == Direction.Axis.Y) {
            return false;
         } else {
            BooleanProperty booleanproperty = FACING_TO_PROPERTY_MAP.get(p_196541_3_);
            BlockState blockstate = p_196541_1_.getBlockState(p_196541_2_.up());
            return blockstate.getBlock() == this && blockstate.get(booleanproperty);
         }
      }
   }

   public static boolean canAttachTo(IBlockReader p_196542_0_, BlockPos worldIn, Direction neighborPos) {
      BlockState blockstate = p_196542_0_.getBlockState(worldIn);
      return Block.doesSideFillSquare(blockstate.getCollisionShape(p_196542_0_, worldIn), neighborPos.getOpposite());
   }

   private BlockState func_196545_h(BlockState p_196545_1_, IBlockReader p_196545_2_, BlockPos p_196545_3_) {
      BlockPos blockpos = p_196545_3_.up();
      if (p_196545_1_.get(UP)) {
         p_196545_1_ = p_196545_1_.with(UP, Boolean.valueOf(canAttachTo(p_196545_2_, blockpos, Direction.DOWN)));
      }

      BlockState blockstate = null;

      for(Direction direction : Direction.Plane.HORIZONTAL) {
         BooleanProperty booleanproperty = getPropertyFor(direction);
         if (p_196545_1_.get(booleanproperty)) {
            boolean flag = this.func_196541_a(p_196545_2_, p_196545_3_, direction);
            if (!flag) {
               if (blockstate == null) {
                  blockstate = p_196545_2_.getBlockState(blockpos);
               }

               flag = blockstate.getBlock() == this && blockstate.get(booleanproperty);
            }

            p_196545_1_ = p_196545_1_.with(booleanproperty, Boolean.valueOf(flag));
         }
      }

      return p_196545_1_;
   }

   /**
    * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
    * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
    * returns its solidified counterpart.
    * Note that this method should ideally consider only the specific face passed in.
    */
   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      if (facing == Direction.DOWN) {
         return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
      } else {
         BlockState blockstate = this.func_196545_h(stateIn, worldIn, currentPos);
         return !this.func_196543_i(blockstate) ? Blocks.AIR.getDefaultState() : blockstate;
      }
   }

   public void tick(BlockState state, World worldIn, BlockPos pos, Random random) {
      if (!worldIn.isRemote) {
         BlockState blockstate = this.func_196545_h(state, worldIn, pos);
         if (blockstate != state) {
            if (this.func_196543_i(blockstate)) {
               worldIn.setBlockState(pos, blockstate, 2);
            } else {
               spawnDrops(state, worldIn, pos);
               worldIn.removeBlock(pos, false);
            }

         } else if (worldIn.rand.nextInt(4) == 0 && worldIn.isAreaLoaded(pos, 4)) { // Forge: check area to prevent loading unloaded chunks
            Direction direction = Direction.random(random);
            BlockPos blockpos = pos.up();
            if (direction.getAxis().isHorizontal() && !state.get(getPropertyFor(direction))) {
               if (this.func_196539_a(worldIn, pos)) {
                  BlockPos blockpos4 = pos.offset(direction);
                  BlockState blockstate5 = worldIn.getBlockState(blockpos4);
                  if (blockstate5.isAir()) {
                     Direction direction3 = direction.rotateY();
                     Direction direction4 = direction.rotateYCCW();
                     boolean flag = state.get(getPropertyFor(direction3));
                     boolean flag1 = state.get(getPropertyFor(direction4));
                     BlockPos blockpos2 = blockpos4.offset(direction3);
                     BlockPos blockpos3 = blockpos4.offset(direction4);
                     if (flag && canAttachTo(worldIn, blockpos2, direction3)) {
                        worldIn.setBlockState(blockpos4, this.getDefaultState().with(getPropertyFor(direction3), Boolean.valueOf(true)), 2);
                     } else if (flag1 && canAttachTo(worldIn, blockpos3, direction4)) {
                        worldIn.setBlockState(blockpos4, this.getDefaultState().with(getPropertyFor(direction4), Boolean.valueOf(true)), 2);
                     } else {
                        Direction direction1 = direction.getOpposite();
                        if (flag && worldIn.isAirBlock(blockpos2) && canAttachTo(worldIn, pos.offset(direction3), direction1)) {
                           worldIn.setBlockState(blockpos2, this.getDefaultState().with(getPropertyFor(direction1), Boolean.valueOf(true)), 2);
                        } else if (flag1 && worldIn.isAirBlock(blockpos3) && canAttachTo(worldIn, pos.offset(direction4), direction1)) {
                           worldIn.setBlockState(blockpos3, this.getDefaultState().with(getPropertyFor(direction1), Boolean.valueOf(true)), 2);
                        } else if ((double)worldIn.rand.nextFloat() < 0.05D && canAttachTo(worldIn, blockpos4.up(), Direction.UP)) {
                           worldIn.setBlockState(blockpos4, this.getDefaultState().with(UP, Boolean.valueOf(true)), 2);
                        }
                     }
                  } else if (canAttachTo(worldIn, blockpos4, direction)) {
                     worldIn.setBlockState(pos, state.with(getPropertyFor(direction), Boolean.valueOf(true)), 2);
                  }

               }
            } else {
               if (direction == Direction.UP && pos.getY() < 255) {
                  if (this.func_196541_a(worldIn, pos, direction)) {
                     worldIn.setBlockState(pos, state.with(UP, Boolean.valueOf(true)), 2);
                     return;
                  }

                  if (worldIn.isAirBlock(blockpos)) {
                     if (!this.func_196539_a(worldIn, pos)) {
                        return;
                     }

                     BlockState blockstate4 = state;

                     for(Direction direction2 : Direction.Plane.HORIZONTAL) {
                        if (random.nextBoolean() || !canAttachTo(worldIn, blockpos.offset(direction2), Direction.UP)) {
                           blockstate4 = blockstate4.with(getPropertyFor(direction2), Boolean.valueOf(false));
                        }
                     }

                     if (this.func_196540_x(blockstate4)) {
                        worldIn.setBlockState(blockpos, blockstate4, 2);
                     }

                     return;
                  }
               }

               if (pos.getY() > 0) {
                  BlockPos blockpos1 = pos.down();
                  BlockState blockstate1 = worldIn.getBlockState(blockpos1);
                  if (blockstate1.isAir() || blockstate1.getBlock() == this) {
                     BlockState blockstate2 = blockstate1.isAir() ? this.getDefaultState() : blockstate1;
                     BlockState blockstate3 = this.func_196544_a(state, blockstate2, random);
                     if (blockstate2 != blockstate3 && this.func_196540_x(blockstate3)) {
                        worldIn.setBlockState(blockpos1, blockstate3, 2);
                     }
                  }
               }

            }
         }
      }
   }

   private BlockState func_196544_a(BlockState p_196544_1_, BlockState p_196544_2_, Random p_196544_3_) {
      for(Direction direction : Direction.Plane.HORIZONTAL) {
         if (p_196544_3_.nextBoolean()) {
            BooleanProperty booleanproperty = getPropertyFor(direction);
            if (p_196544_1_.get(booleanproperty)) {
               p_196544_2_ = p_196544_2_.with(booleanproperty, Boolean.valueOf(true));
            }
         }
      }

      return p_196544_2_;
   }

   private boolean func_196540_x(BlockState p_196540_1_) {
      return p_196540_1_.get(NORTH) || p_196540_1_.get(EAST) || p_196540_1_.get(SOUTH) || p_196540_1_.get(WEST);
   }

   private boolean func_196539_a(IBlockReader p_196539_1_, BlockPos p_196539_2_) {
      int i = 4;
      Iterable<BlockPos> iterable = BlockPos.getAllInBoxMutable(p_196539_2_.getX() - 4, p_196539_2_.getY() - 1, p_196539_2_.getZ() - 4, p_196539_2_.getX() + 4, p_196539_2_.getY() + 1, p_196539_2_.getZ() + 4);
      int j = 5;

      for(BlockPos blockpos : iterable) {
         if (p_196539_1_.getBlockState(blockpos).getBlock() == this) {
            --j;
            if (j <= 0) {
               return false;
            }
         }
      }

      return true;
   }

   public boolean isReplaceable(BlockState state, BlockItemUseContext useContext) {
      BlockState blockstate = useContext.getWorld().getBlockState(useContext.getPos());
      if (blockstate.getBlock() == this) {
         return this.func_208496_w(blockstate) < FACING_TO_PROPERTY_MAP.size();
      } else {
         return super.isReplaceable(state, useContext);
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext context) {
      BlockState blockstate = context.getWorld().getBlockState(context.getPos());
      boolean flag = blockstate.getBlock() == this;
      BlockState blockstate1 = flag ? blockstate : this.getDefaultState();

      for(Direction direction : context.getNearestLookingDirections()) {
         if (direction != Direction.DOWN) {
            BooleanProperty booleanproperty = getPropertyFor(direction);
            boolean flag1 = flag && blockstate.get(booleanproperty);
            if (!flag1 && this.func_196541_a(context.getWorld(), context.getPos(), direction)) {
               return blockstate1.with(booleanproperty, Boolean.valueOf(true));
            }
         }
      }

      return flag ? blockstate1 : null;
   }

   /**
    * Gets the render layer this block will render on. SOLID for solid blocks, CUTOUT or CUTOUT_MIPPED for on-off
    * transparency (glass, reeds), TRANSLUCENT for fully blended transparency (stained glass)
    */
   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(UP, NORTH, EAST, SOUTH, WEST);
   }

   /**
    * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
    * fine.
    */
   public BlockState rotate(BlockState state, Rotation rot) {
      switch(rot) {
      case CLOCKWISE_180:
         return state.with(NORTH, state.get(SOUTH)).with(EAST, state.get(WEST)).with(SOUTH, state.get(NORTH)).with(WEST, state.get(EAST));
      case COUNTERCLOCKWISE_90:
         return state.with(NORTH, state.get(EAST)).with(EAST, state.get(SOUTH)).with(SOUTH, state.get(WEST)).with(WEST, state.get(NORTH));
      case CLOCKWISE_90:
         return state.with(NORTH, state.get(WEST)).with(EAST, state.get(NORTH)).with(SOUTH, state.get(EAST)).with(WEST, state.get(SOUTH));
      default:
         return state;
      }
   }

   /**
    * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withMirror(Mirror)} whenever possible. Implementing/overriding is fine.
    */
   public BlockState mirror(BlockState state, Mirror mirrorIn) {
      switch(mirrorIn) {
      case LEFT_RIGHT:
         return state.with(NORTH, state.get(SOUTH)).with(SOUTH, state.get(NORTH));
      case FRONT_BACK:
         return state.with(EAST, state.get(WEST)).with(WEST, state.get(EAST));
      default:
         return super.mirror(state, mirrorIn);
      }
   }

   public static BooleanProperty getPropertyFor(Direction side) {
      return FACING_TO_PROPERTY_MAP.get(side);
   }

   @Override public boolean isLadder(BlockState state, IWorldReader world, BlockPos pos, net.minecraft.entity.LivingEntity entity) { return true; }
}