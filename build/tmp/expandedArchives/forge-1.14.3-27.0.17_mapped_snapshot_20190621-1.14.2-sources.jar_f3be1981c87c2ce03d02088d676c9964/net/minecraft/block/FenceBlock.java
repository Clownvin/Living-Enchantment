package net.minecraft.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.LeadItem;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class FenceBlock extends FourWayBlock {
   private final VoxelShape[] renderShapes;

   public FenceBlock(Block.Properties properties) {
      super(2.0F, 2.0F, 16.0F, 16.0F, 24.0F, properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(NORTH, Boolean.valueOf(false)).with(EAST, Boolean.valueOf(false)).with(SOUTH, Boolean.valueOf(false)).with(WEST, Boolean.valueOf(false)).with(WATERLOGGED, Boolean.valueOf(false)));
      this.renderShapes = this.makeShapes(2.0F, 1.0F, 16.0F, 6.0F, 15.0F);
   }

   public VoxelShape getRenderShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
      return this.renderShapes[this.getIndex(state)];
   }

   public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return false;
   }

   public boolean func_220111_a(BlockState p_220111_1_, boolean p_220111_2_, Direction p_220111_3_) {
      Block block = p_220111_1_.getBlock();
      boolean flag = block.isIn(BlockTags.FENCES) && p_220111_1_.getMaterial() == this.material;
      boolean flag1 = block instanceof FenceGateBlock && FenceGateBlock.isParallel(p_220111_1_, p_220111_3_);
      return !cannotAttach(block) && p_220111_2_ || flag || flag1;
   }

   public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
      if (!worldIn.isRemote) {
         return LeadItem.attachToFence(player, worldIn, pos);
      } else {
         ItemStack itemstack = player.getHeldItem(handIn);
         return itemstack.getItem() == Items.LEAD || itemstack.isEmpty();
      }
   }

   public BlockState getStateForPlacement(BlockItemUseContext context) {
      IBlockReader iblockreader = context.getWorld();
      BlockPos blockpos = context.getPos();
      IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
      BlockPos blockpos1 = blockpos.north();
      BlockPos blockpos2 = blockpos.east();
      BlockPos blockpos3 = blockpos.south();
      BlockPos blockpos4 = blockpos.west();
      BlockState blockstate = iblockreader.getBlockState(blockpos1);
      BlockState blockstate1 = iblockreader.getBlockState(blockpos2);
      BlockState blockstate2 = iblockreader.getBlockState(blockpos3);
      BlockState blockstate3 = iblockreader.getBlockState(blockpos4);
      return super.getStateForPlacement(context).with(NORTH, Boolean.valueOf(this.func_220111_a(blockstate, Block.hasSolidSide(blockstate, iblockreader, blockpos1, Direction.SOUTH), Direction.SOUTH))).with(EAST, Boolean.valueOf(this.func_220111_a(blockstate1, Block.hasSolidSide(blockstate1, iblockreader, blockpos2, Direction.WEST), Direction.WEST))).with(SOUTH, Boolean.valueOf(this.func_220111_a(blockstate2, Block.hasSolidSide(blockstate2, iblockreader, blockpos3, Direction.NORTH), Direction.NORTH))).with(WEST, Boolean.valueOf(this.func_220111_a(blockstate3, Block.hasSolidSide(blockstate3, iblockreader, blockpos4, Direction.EAST), Direction.EAST))).with(WATERLOGGED, Boolean.valueOf(ifluidstate.getFluid() == Fluids.WATER));
   }

   /**
    * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
    * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
    * returns its solidified counterpart.
    * Note that this method should ideally consider only the specific face passed in.
    */
   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      if (stateIn.get(WATERLOGGED)) {
         worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
      }

      return facing.getAxis().getPlane() == Direction.Plane.HORIZONTAL ? stateIn.with(FACING_TO_PROPERTY_MAP.get(facing), Boolean.valueOf(this.func_220111_a(facingState, Block.hasSolidSide(facingState, worldIn, facingPos, facing.getOpposite()), facing.getOpposite()))) : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED);
   }
}