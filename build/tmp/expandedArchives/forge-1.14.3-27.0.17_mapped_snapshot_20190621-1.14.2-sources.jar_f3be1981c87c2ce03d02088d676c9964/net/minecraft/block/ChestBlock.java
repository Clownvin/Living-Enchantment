package net.minecraft.block;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.DoubleSidedInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.ChestType;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ChestBlock extends ContainerBlock implements IWaterLoggable {
   public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
   public static final EnumProperty<ChestType> TYPE = BlockStateProperties.CHEST_TYPE;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape field_196316_c = Block.makeCuboidShape(1.0D, 0.0D, 0.0D, 15.0D, 14.0D, 15.0D);
   protected static final VoxelShape field_196317_y = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 16.0D);
   protected static final VoxelShape field_196318_z = Block.makeCuboidShape(0.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
   protected static final VoxelShape field_196313_A = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 16.0D, 14.0D, 15.0D);
   protected static final VoxelShape field_196315_B = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
   private static final ChestBlock.InventoryFactory<IInventory> field_220109_i = new ChestBlock.InventoryFactory<IInventory>() {
      public IInventory forDouble(ChestTileEntity p_212855_1_, ChestTileEntity p_212855_2_) {
         return new DoubleSidedInventory(p_212855_1_, p_212855_2_);
      }

      public IInventory forSingle(ChestTileEntity p_212856_1_) {
         return p_212856_1_;
      }
   };
   private static final ChestBlock.InventoryFactory<INamedContainerProvider> field_220110_j = new ChestBlock.InventoryFactory<INamedContainerProvider>() {
      public INamedContainerProvider forDouble(final ChestTileEntity p_212855_1_, final ChestTileEntity p_212855_2_) {
         final IInventory iinventory = new DoubleSidedInventory(p_212855_1_, p_212855_2_);
         return new INamedContainerProvider() {
            @Nullable
            public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
               if (p_212855_1_.canOpen(p_createMenu_3_) && p_212855_2_.canOpen(p_createMenu_3_)) {
                  p_212855_1_.fillWithLoot(p_createMenu_2_.player);
                  p_212855_2_.fillWithLoot(p_createMenu_2_.player);
                  return ChestContainer.createGeneric9X6(p_createMenu_1_, p_createMenu_2_, iinventory);
               } else {
                  return null;
               }
            }

            public ITextComponent getDisplayName() {
               if (p_212855_1_.hasCustomName()) {
                  return p_212855_1_.getDisplayName();
               } else {
                  return (ITextComponent)(p_212855_2_.hasCustomName() ? p_212855_2_.getDisplayName() : new TranslationTextComponent("container.chestDouble"));
               }
            }
         };
      }

      public INamedContainerProvider forSingle(ChestTileEntity p_212856_1_) {
         return p_212856_1_;
      }
   };

   protected ChestBlock(Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(TYPE, ChestType.SINGLE).with(WATERLOGGED, Boolean.valueOf(false)));
   }

   /**
    * @deprecated call via {@link IBlockState#hasCustomBreakingProgress()} whenever possible. Implementing/overriding is
    * fine.
    */
   @OnlyIn(Dist.CLIENT)
   public boolean hasCustomBreakingProgress(BlockState state) {
      return true;
   }

   /**
    * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
    * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
    * @deprecated call via {@link IBlockState#getRenderType()} whenever possible. Implementing/overriding is fine.
    */
   public BlockRenderType getRenderType(BlockState state) {
      return BlockRenderType.ENTITYBLOCK_ANIMATED;
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

      if (facingState.getBlock() == this && facing.getAxis().isHorizontal()) {
         ChestType chesttype = facingState.get(TYPE);
         if (stateIn.get(TYPE) == ChestType.SINGLE && chesttype != ChestType.SINGLE && stateIn.get(FACING) == facingState.get(FACING) && getDirectionToAttached(facingState) == facing.getOpposite()) {
            return stateIn.with(TYPE, chesttype.opposite());
         }
      } else if (getDirectionToAttached(stateIn) == facing) {
         return stateIn.with(TYPE, ChestType.SINGLE);
      }

      return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      if (state.get(TYPE) == ChestType.SINGLE) {
         return field_196315_B;
      } else {
         switch(getDirectionToAttached(state)) {
         case NORTH:
         default:
            return field_196316_c;
         case SOUTH:
            return field_196317_y;
         case WEST:
            return field_196318_z;
         case EAST:
            return field_196313_A;
         }
      }
   }

   /**
    * Returns a facing pointing from the given state to its attached double chest
    */
   public static Direction getDirectionToAttached(BlockState state) {
      Direction direction = state.get(FACING);
      return state.get(TYPE) == ChestType.LEFT ? direction.rotateY() : direction.rotateYCCW();
   }

   public BlockState getStateForPlacement(BlockItemUseContext context) {
      ChestType chesttype = ChestType.SINGLE;
      Direction direction = context.getPlacementHorizontalFacing().getOpposite();
      IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
      boolean flag = context.isPlacerSneaking();
      Direction direction1 = context.getFace();
      if (direction1.getAxis().isHorizontal() && flag) {
         Direction direction2 = this.getDirectionToAttach(context, direction1.getOpposite());
         if (direction2 != null && direction2.getAxis() != direction1.getAxis()) {
            direction = direction2;
            chesttype = direction2.rotateYCCW() == direction1.getOpposite() ? ChestType.RIGHT : ChestType.LEFT;
         }
      }

      if (chesttype == ChestType.SINGLE && !flag) {
         if (direction == this.getDirectionToAttach(context, direction.rotateY())) {
            chesttype = ChestType.LEFT;
         } else if (direction == this.getDirectionToAttach(context, direction.rotateYCCW())) {
            chesttype = ChestType.RIGHT;
         }
      }

      return this.getDefaultState().with(FACING, direction).with(TYPE, chesttype).with(WATERLOGGED, Boolean.valueOf(ifluidstate.getFluid() == Fluids.WATER));
   }

   public IFluidState getFluidState(BlockState state) {
      return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
   }

   /**
    * Returns facing pointing to a chest to form a double chest with, null otherwise
    */
   @Nullable
   private Direction getDirectionToAttach(BlockItemUseContext p_196312_1_, Direction p_196312_2_) {
      BlockState blockstate = p_196312_1_.getWorld().getBlockState(p_196312_1_.getPos().offset(p_196312_2_));
      return blockstate.getBlock() == this && blockstate.get(TYPE) == ChestType.SINGLE ? blockstate.get(FACING) : null;
   }

   /**
    * Called by ItemBlocks after a block is set in the world, to allow post-place logic
    */
   public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
      if (stack.hasDisplayName()) {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof ChestTileEntity) {
            ((ChestTileEntity)tileentity).setCustomName(stack.getDisplayName());
         }
      }

   }

   public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
      if (state.getBlock() != newState.getBlock()) {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof IInventory) {
            InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory)tileentity);
            worldIn.updateComparatorOutputLevel(pos, this);
         }

         super.onReplaced(state, worldIn, pos, newState, isMoving);
      }
   }

   public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
      if (worldIn.isRemote) {
         return true;
      } else {
         INamedContainerProvider inamedcontainerprovider = this.getContainer(state, worldIn, pos);
         if (inamedcontainerprovider != null) {
            player.openContainer(inamedcontainerprovider);
            player.addStat(this.getOpenStat());
         }

         return true;
      }
   }

   protected Stat<ResourceLocation> getOpenStat() {
      return Stats.CUSTOM.get(Stats.OPEN_CHEST);
   }

   @Nullable
   public static <T> T func_220106_a(BlockState p_220106_0_, IWorld p_220106_1_, BlockPos p_220106_2_, boolean allowBlocked, ChestBlock.InventoryFactory<T> p_220106_4_) {
      TileEntity tileentity = p_220106_1_.getTileEntity(p_220106_2_);
      if (!(tileentity instanceof ChestTileEntity)) {
         return (T)null;
      } else if (!allowBlocked && isBlocked(p_220106_1_, p_220106_2_)) {
         return (T)null;
      } else {
         ChestTileEntity chesttileentity = (ChestTileEntity)tileentity;
         ChestType chesttype = p_220106_0_.get(TYPE);
         if (chesttype == ChestType.SINGLE) {
            return p_220106_4_.forSingle(chesttileentity);
         } else {
            BlockPos blockpos = p_220106_2_.offset(getDirectionToAttached(p_220106_0_));
            BlockState blockstate = p_220106_1_.getBlockState(blockpos);
            if (blockstate.getBlock() == p_220106_0_.getBlock()) {
               ChestType chesttype1 = blockstate.get(TYPE);
               if (chesttype1 != ChestType.SINGLE && chesttype != chesttype1 && blockstate.get(FACING) == p_220106_0_.get(FACING)) {
                  if (!allowBlocked && isBlocked(p_220106_1_, blockpos)) {
                     return (T)null;
                  }

                  TileEntity tileentity1 = p_220106_1_.getTileEntity(blockpos);
                  if (tileentity1 instanceof ChestTileEntity) {
                     ChestTileEntity chesttileentity1 = chesttype == ChestType.RIGHT ? chesttileentity : (ChestTileEntity)tileentity1;
                     ChestTileEntity chesttileentity2 = chesttype == ChestType.RIGHT ? (ChestTileEntity)tileentity1 : chesttileentity;
                     return p_220106_4_.forDouble(chesttileentity1, chesttileentity2);
                  }
               }
            }

            return p_220106_4_.forSingle(chesttileentity);
         }
      }
   }

   @Nullable
   public static IInventory getInventory(BlockState p_220105_0_, World p_220105_1_, BlockPos p_220105_2_, boolean allowBlocked) {
      return func_220106_a(p_220105_0_, p_220105_1_, p_220105_2_, allowBlocked, field_220109_i);
   }

   @Nullable
   public INamedContainerProvider getContainer(BlockState state, World worldIn, BlockPos pos) {
      return func_220106_a(state, worldIn, pos, false, field_220110_j);
   }

   public TileEntity createNewTileEntity(IBlockReader worldIn) {
      return new ChestTileEntity();
   }

   private static boolean isBlocked(IWorld p_220108_0_, BlockPos p_220108_1_) {
      return isBelowSolidBlock(p_220108_0_, p_220108_1_) || isCatSittingOn(p_220108_0_, p_220108_1_);
   }

   private static boolean isBelowSolidBlock(IBlockReader p_176456_0_, BlockPos worldIn) {
      BlockPos blockpos = worldIn.up();
      return p_176456_0_.getBlockState(blockpos).isNormalCube(p_176456_0_, blockpos);
   }

   private static boolean isCatSittingOn(IWorld p_220107_0_, BlockPos p_220107_1_) {
      List<CatEntity> list = p_220107_0_.getEntitiesWithinAABB(CatEntity.class, new AxisAlignedBB((double)p_220107_1_.getX(), (double)(p_220107_1_.getY() + 1), (double)p_220107_1_.getZ(), (double)(p_220107_1_.getX() + 1), (double)(p_220107_1_.getY() + 2), (double)(p_220107_1_.getZ() + 1)));
      if (!list.isEmpty()) {
         for(CatEntity catentity : list) {
            if (catentity.isSitting()) {
               return true;
            }
         }
      }

      return false;
   }

   /**
    * @deprecated call via {@link IBlockState#hasComparatorInputOverride()} whenever possible. Implementing/overriding
    * is fine.
    */
   public boolean hasComparatorInputOverride(BlockState state) {
      return true;
   }

   /**
    * @deprecated call via {@link IBlockState#getComparatorInputOverride(World,BlockPos)} whenever possible.
    * Implementing/overriding is fine.
    */
   public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
      return Container.calcRedstoneFromInventory(getInventory(blockState, worldIn, pos, false));
   }

   /**
    * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
    * fine.
    */
   public BlockState rotate(BlockState state, Rotation rot) {
      return state.with(FACING, rot.rotate(state.get(FACING)));
   }

   /**
    * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withMirror(Mirror)} whenever possible. Implementing/overriding is fine.
    */
   public BlockState mirror(BlockState state, Mirror mirrorIn) {
      return state.rotate(mirrorIn.toRotation(state.get(FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(FACING, TYPE, WATERLOGGED);
   }

   public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return false;
   }

   interface InventoryFactory<T> {
      T forDouble(ChestTileEntity p_212855_1_, ChestTileEntity p_212855_2_);

      T forSingle(ChestTileEntity p_212856_1_);
   }
}