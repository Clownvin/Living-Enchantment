package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RedstoneSide;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RedstoneWireBlock extends Block {
   public static final EnumProperty<RedstoneSide> NORTH = BlockStateProperties.REDSTONE_NORTH;
   public static final EnumProperty<RedstoneSide> EAST = BlockStateProperties.REDSTONE_EAST;
   public static final EnumProperty<RedstoneSide> SOUTH = BlockStateProperties.REDSTONE_SOUTH;
   public static final EnumProperty<RedstoneSide> WEST = BlockStateProperties.REDSTONE_WEST;
   public static final IntegerProperty POWER = BlockStateProperties.POWER_0_15;
   public static final Map<Direction, EnumProperty<RedstoneSide>> FACING_PROPERTY_MAP = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, NORTH, Direction.EAST, EAST, Direction.SOUTH, SOUTH, Direction.WEST, WEST));
   protected static final VoxelShape[] SHAPES = new VoxelShape[]{Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D), Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D), Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D), Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 13.0D, 1.0D, 16.0D), Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 16.0D, 1.0D, 16.0D), Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 16.0D, 1.0D, 13.0D), Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 13.0D), Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D)};
   private boolean canProvidePower = true;
   private final Set<BlockPos> blocksNeedingUpdate = Sets.newHashSet();

   public RedstoneWireBlock(Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(NORTH, RedstoneSide.NONE).with(EAST, RedstoneSide.NONE).with(SOUTH, RedstoneSide.NONE).with(WEST, RedstoneSide.NONE).with(POWER, Integer.valueOf(0)));
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return SHAPES[getAABBIndex(state)];
   }

   private static int getAABBIndex(BlockState state) {
      int i = 0;
      boolean flag = state.get(NORTH) != RedstoneSide.NONE;
      boolean flag1 = state.get(EAST) != RedstoneSide.NONE;
      boolean flag2 = state.get(SOUTH) != RedstoneSide.NONE;
      boolean flag3 = state.get(WEST) != RedstoneSide.NONE;
      if (flag || flag2 && !flag && !flag1 && !flag3) {
         i |= 1 << Direction.NORTH.getHorizontalIndex();
      }

      if (flag1 || flag3 && !flag && !flag1 && !flag2) {
         i |= 1 << Direction.EAST.getHorizontalIndex();
      }

      if (flag2 || flag && !flag1 && !flag2 && !flag3) {
         i |= 1 << Direction.SOUTH.getHorizontalIndex();
      }

      if (flag3 || flag1 && !flag && !flag2 && !flag3) {
         i |= 1 << Direction.WEST.getHorizontalIndex();
      }

      return i;
   }

   public BlockState getStateForPlacement(BlockItemUseContext context) {
      IBlockReader iblockreader = context.getWorld();
      BlockPos blockpos = context.getPos();
      return this.getDefaultState().with(WEST, this.getSide(iblockreader, blockpos, Direction.WEST)).with(EAST, this.getSide(iblockreader, blockpos, Direction.EAST)).with(NORTH, this.getSide(iblockreader, blockpos, Direction.NORTH)).with(SOUTH, this.getSide(iblockreader, blockpos, Direction.SOUTH));
   }

   /**
    * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
    * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
    * returns its solidified counterpart.
    * Note that this method should ideally consider only the specific face passed in.
    */
   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      if (facing == Direction.DOWN) {
         return stateIn;
      } else {
         return facing == Direction.UP ? stateIn.with(WEST, this.getSide(worldIn, currentPos, Direction.WEST)).with(EAST, this.getSide(worldIn, currentPos, Direction.EAST)).with(NORTH, this.getSide(worldIn, currentPos, Direction.NORTH)).with(SOUTH, this.getSide(worldIn, currentPos, Direction.SOUTH)) : stateIn.with(FACING_PROPERTY_MAP.get(facing), this.getSide(worldIn, currentPos, facing));
      }
   }

   /**
    * performs updates on diagonal neighbors of the target position and passes in the flags. The flags can be referenced
    * from the docs for {@link IWorldWriter#setBlockState(IBlockState, BlockPos, int)}.
    */
   public void updateDiagonalNeighbors(BlockState state, IWorld worldIn, BlockPos pos, int flags) {
      try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
         for(Direction direction : Direction.Plane.HORIZONTAL) {
            RedstoneSide redstoneside = state.get(FACING_PROPERTY_MAP.get(direction));
            if (redstoneside != RedstoneSide.NONE && worldIn.getBlockState(blockpos$pooledmutableblockpos.setPos(pos).move(direction)).getBlock() != this) {
               blockpos$pooledmutableblockpos.move(Direction.DOWN);
               BlockState blockstate = worldIn.getBlockState(blockpos$pooledmutableblockpos);
               if (blockstate.getBlock() != Blocks.OBSERVER) {
                  BlockPos blockpos = blockpos$pooledmutableblockpos.offset(direction.getOpposite());
                  BlockState blockstate1 = blockstate.updatePostPlacement(direction.getOpposite(), worldIn.getBlockState(blockpos), worldIn, blockpos$pooledmutableblockpos, blockpos);
                  replaceBlock(blockstate, blockstate1, worldIn, blockpos$pooledmutableblockpos, flags);
               }

               blockpos$pooledmutableblockpos.setPos(pos).move(direction).move(Direction.UP);
               BlockState blockstate3 = worldIn.getBlockState(blockpos$pooledmutableblockpos);
               if (blockstate3.getBlock() != Blocks.OBSERVER) {
                  BlockPos blockpos1 = blockpos$pooledmutableblockpos.offset(direction.getOpposite());
                  BlockState blockstate2 = blockstate3.updatePostPlacement(direction.getOpposite(), worldIn.getBlockState(blockpos1), worldIn, blockpos$pooledmutableblockpos, blockpos1);
                  replaceBlock(blockstate3, blockstate2, worldIn, blockpos$pooledmutableblockpos, flags);
               }
            }
         }
      }

   }

   private RedstoneSide getSide(IBlockReader worldIn, BlockPos pos, Direction face) {
      BlockPos blockpos = pos.offset(face);
      BlockState blockstate = worldIn.getBlockState(blockpos);
      BlockPos blockpos1 = pos.up();
      BlockState blockstate1 = worldIn.getBlockState(blockpos1);
      if (!blockstate1.isNormalCube(worldIn, blockpos1)) {
         boolean flag = Block.hasSolidSide(blockstate, worldIn, blockpos, Direction.UP) || blockstate.getBlock() == Blocks.HOPPER;
         if (flag && canConnectTo(worldIn.getBlockState(blockpos.up()), worldIn, blockpos.up(), null)) {
            if (isOpaque(blockstate.getCollisionShape(worldIn, blockpos))) {
               return RedstoneSide.UP;
            }

            return RedstoneSide.SIDE;
         }
      }

      return !canConnectTo(blockstate, worldIn, blockpos, face) && (blockstate.isNormalCube(worldIn, blockpos) || !canConnectTo(worldIn.getBlockState(blockpos.down()), worldIn, blockpos.down(), null)) ? RedstoneSide.NONE : RedstoneSide.SIDE;
   }

   public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
      BlockPos blockpos = pos.down();
      BlockState blockstate = worldIn.getBlockState(blockpos);
      return Block.hasSolidSide(blockstate, worldIn, blockpos, Direction.UP) || blockstate.getBlock() == Blocks.HOPPER;
   }

   private BlockState updateSurroundingRedstone(World worldIn, BlockPos pos, BlockState state) {
      state = this.func_212568_b(worldIn, pos, state);
      List<BlockPos> list = Lists.newArrayList(this.blocksNeedingUpdate);
      this.blocksNeedingUpdate.clear();

      for(BlockPos blockpos : list) {
         worldIn.notifyNeighborsOfStateChange(blockpos, this);
      }

      return state;
   }

   private BlockState func_212568_b(World p_212568_1_, BlockPos p_212568_2_, BlockState p_212568_3_) {
      BlockState blockstate = p_212568_3_;
      int i = p_212568_3_.get(POWER);
      this.canProvidePower = false;
      int j = p_212568_1_.getRedstonePowerFromNeighbors(p_212568_2_);
      this.canProvidePower = true;
      int k = 0;
      if (j < 15) {
         for(Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos blockpos = p_212568_2_.offset(direction);
            BlockState blockstate1 = p_212568_1_.getBlockState(blockpos);
            k = this.maxSignal(k, blockstate1);
            BlockPos blockpos1 = p_212568_2_.up();
            if (blockstate1.isNormalCube(p_212568_1_, blockpos) && !p_212568_1_.getBlockState(blockpos1).isNormalCube(p_212568_1_, blockpos1)) {
               k = this.maxSignal(k, p_212568_1_.getBlockState(blockpos.up()));
            } else if (!blockstate1.isNormalCube(p_212568_1_, blockpos)) {
               k = this.maxSignal(k, p_212568_1_.getBlockState(blockpos.down()));
            }
         }
      }

      int l = k - 1;
      if (j > l) {
         l = j;
      }

      if (i != l) {
         p_212568_3_ = p_212568_3_.with(POWER, Integer.valueOf(l));
         if (p_212568_1_.getBlockState(p_212568_2_) == blockstate) {
            p_212568_1_.setBlockState(p_212568_2_, p_212568_3_, 2);
         }

         this.blocksNeedingUpdate.add(p_212568_2_);

         for(Direction direction1 : Direction.values()) {
            this.blocksNeedingUpdate.add(p_212568_2_.offset(direction1));
         }
      }

      return p_212568_3_;
   }

   /**
    * Calls World.notifyNeighborsOfStateChange() for all neighboring blocks, but only if the given block is a redstone
    * wire.
    */
   private void notifyWireNeighborsOfStateChange(World worldIn, BlockPos pos) {
      if (worldIn.getBlockState(pos).getBlock() == this) {
         worldIn.notifyNeighborsOfStateChange(pos, this);

         for(Direction direction : Direction.values()) {
            worldIn.notifyNeighborsOfStateChange(pos.offset(direction), this);
         }

      }
   }

   public void onBlockAdded(BlockState p_220082_1_, World worldIn, BlockPos pos, BlockState p_220082_4_, boolean p_220082_5_) {
      if (p_220082_4_.getBlock() != p_220082_1_.getBlock() && !worldIn.isRemote) {
         this.updateSurroundingRedstone(worldIn, pos, p_220082_1_);

         for(Direction direction : Direction.Plane.VERTICAL) {
            worldIn.notifyNeighborsOfStateChange(pos.offset(direction), this);
         }

         for(Direction direction1 : Direction.Plane.HORIZONTAL) {
            this.notifyWireNeighborsOfStateChange(worldIn, pos.offset(direction1));
         }

         for(Direction direction2 : Direction.Plane.HORIZONTAL) {
            BlockPos blockpos = pos.offset(direction2);
            if (worldIn.getBlockState(blockpos).isNormalCube(worldIn, blockpos)) {
               this.notifyWireNeighborsOfStateChange(worldIn, blockpos.up());
            } else {
               this.notifyWireNeighborsOfStateChange(worldIn, blockpos.down());
            }
         }

      }
   }

   public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
      if (!isMoving && state.getBlock() != newState.getBlock()) {
         super.onReplaced(state, worldIn, pos, newState, isMoving);
         if (!worldIn.isRemote) {
            for(Direction direction : Direction.values()) {
               worldIn.notifyNeighborsOfStateChange(pos.offset(direction), this);
            }

            this.updateSurroundingRedstone(worldIn, pos, state);

            for(Direction direction1 : Direction.Plane.HORIZONTAL) {
               this.notifyWireNeighborsOfStateChange(worldIn, pos.offset(direction1));
            }

            for(Direction direction2 : Direction.Plane.HORIZONTAL) {
               BlockPos blockpos = pos.offset(direction2);
               if (worldIn.getBlockState(blockpos).isNormalCube(worldIn, blockpos)) {
                  this.notifyWireNeighborsOfStateChange(worldIn, blockpos.up());
               } else {
                  this.notifyWireNeighborsOfStateChange(worldIn, blockpos.down());
               }
            }

         }
      }
   }

   private int maxSignal(int existingSignal, BlockState neighbor) {
      if (neighbor.getBlock() != this) {
         return existingSignal;
      } else {
         int i = neighbor.get(POWER);
         return i > existingSignal ? i : existingSignal;
      }
   }

   public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean p_220069_6_) {
      if (!worldIn.isRemote) {
         if (state.isValidPosition(worldIn, pos)) {
            this.updateSurroundingRedstone(worldIn, pos, state);
         } else {
            spawnDrops(state, worldIn, pos);
            worldIn.removeBlock(pos, false);
         }

      }
   }

   /**
    * @deprecated call via {@link IBlockState#getStrongPower(IBlockAccess,BlockPos,EnumFacing)} whenever possible.
    * Implementing/overriding is fine.
    */
   public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
      return !this.canProvidePower ? 0 : blockState.getWeakPower(blockAccess, pos, side);
   }

   /**
    * @deprecated call via {@link IBlockState#getWeakPower(IBlockAccess,BlockPos,EnumFacing)} whenever possible.
    * Implementing/overriding is fine.
    */
   public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
      if (!this.canProvidePower) {
         return 0;
      } else {
         int i = blockState.get(POWER);
         if (i == 0) {
            return 0;
         } else if (side == Direction.UP) {
            return i;
         } else {
            EnumSet<Direction> enumset = EnumSet.noneOf(Direction.class);

            for(Direction direction : Direction.Plane.HORIZONTAL) {
               if (this.isPowerSourceAt(blockAccess, pos, direction)) {
                  enumset.add(direction);
               }
            }

            if (side.getAxis().isHorizontal() && enumset.isEmpty()) {
               return i;
            } else if (enumset.contains(side) && !enumset.contains(side.rotateYCCW()) && !enumset.contains(side.rotateY())) {
               return i;
            } else {
               return 0;
            }
         }
      }
   }

   private boolean isPowerSourceAt(IBlockReader worldIn, BlockPos pos, Direction side) {
      BlockPos blockpos = pos.offset(side);
      BlockState blockstate = worldIn.getBlockState(blockpos);
      boolean flag = blockstate.isNormalCube(worldIn, blockpos);
      BlockPos blockpos1 = pos.up();
      boolean flag1 = worldIn.getBlockState(blockpos1).isNormalCube(worldIn, blockpos1);
      if (!flag1 && flag && canConnectTo(worldIn.getBlockState(blockpos.up()), worldIn, blockpos.up(), null)) {
         return true;
      } else if (canConnectTo(blockstate, worldIn, blockpos, side)) {
         return true;
      } else if (blockstate.getBlock() == Blocks.REPEATER && blockstate.get(RedstoneDiodeBlock.POWERED) && blockstate.get(RedstoneDiodeBlock.HORIZONTAL_FACING) == side) {
         return true;
      } else {
         return !flag && canConnectTo(worldIn.getBlockState(blockpos.down()), worldIn, blockpos.down(), null);
      }
   }

   protected static boolean canConnectTo(BlockState blockState, IBlockReader world, BlockPos pos, @Nullable Direction side) {
      Block block = blockState.getBlock();
      if (block == Blocks.REDSTONE_WIRE) {
         return true;
      } else if (blockState.getBlock() == Blocks.REPEATER) {
         Direction direction = blockState.get(RepeaterBlock.HORIZONTAL_FACING);
         return direction == side || direction.getOpposite() == side;
      } else if (Blocks.OBSERVER == blockState.getBlock()) {
         return side == blockState.get(ObserverBlock.FACING);
      } else {
         return blockState.canConnectRedstone(world, pos, side) && side != null;
      }
   }

   /**
    * Can this block provide power. Only wire currently seems to have this change based on its state.
    * @deprecated call via {@link IBlockState#canProvidePower()} whenever possible. Implementing/overriding is fine.
    */
   public boolean canProvidePower(BlockState state) {
      return this.canProvidePower;
   }

   @OnlyIn(Dist.CLIENT)
   public static int colorMultiplier(int p_176337_0_) {
      float f = (float)p_176337_0_ / 15.0F;
      float f1 = f * 0.6F + 0.4F;
      if (p_176337_0_ == 0) {
         f1 = 0.3F;
      }

      float f2 = f * f * 0.7F - 0.5F;
      float f3 = f * f * 0.6F - 0.7F;
      if (f2 < 0.0F) {
         f2 = 0.0F;
      }

      if (f3 < 0.0F) {
         f3 = 0.0F;
      }

      int i = MathHelper.clamp((int)(f1 * 255.0F), 0, 255);
      int j = MathHelper.clamp((int)(f2 * 255.0F), 0, 255);
      int k = MathHelper.clamp((int)(f3 * 255.0F), 0, 255);
      return -16777216 | i << 16 | j << 8 | k;
   }

   /**
    * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
    * this method is unrelated to {@link randomTick} and {@link #needsRandomTick}, and will always be called regardless
    * of whether the block can receive random update ticks
    */
   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
      int i = stateIn.get(POWER);
      if (i != 0) {
         double d0 = (double)pos.getX() + 0.5D + ((double)rand.nextFloat() - 0.5D) * 0.2D;
         double d1 = (double)((float)pos.getY() + 0.0625F);
         double d2 = (double)pos.getZ() + 0.5D + ((double)rand.nextFloat() - 0.5D) * 0.2D;
         float f = (float)i / 15.0F;
         float f1 = f * 0.6F + 0.4F;
         float f2 = Math.max(0.0F, f * f * 0.7F - 0.5F);
         float f3 = Math.max(0.0F, f * f * 0.6F - 0.7F);
         worldIn.addParticle(new RedstoneParticleData(f1, f2, f3, 1.0F), d0, d1, d2, 0.0D, 0.0D, 0.0D);
      }
   }

   /**
    * Gets the render layer this block will render on. SOLID for solid blocks, CUTOUT or CUTOUT_MIPPED for on-off
    * transparency (glass, reeds), TRANSLUCENT for fully blended transparency (stained glass)
    */
   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
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

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(NORTH, EAST, SOUTH, WEST, POWER);
   }
}