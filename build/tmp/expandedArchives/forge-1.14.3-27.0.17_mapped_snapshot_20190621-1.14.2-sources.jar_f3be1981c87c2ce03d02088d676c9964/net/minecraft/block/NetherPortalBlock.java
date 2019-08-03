package net.minecraft.block;

import com.google.common.cache.LoadingCache;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class NetherPortalBlock extends Block {
   public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
   protected static final VoxelShape X_AABB = Block.makeCuboidShape(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
   protected static final VoxelShape Z_AABB = Block.makeCuboidShape(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);

   public NetherPortalBlock(Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(AXIS, Direction.Axis.X));
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      switch((Direction.Axis)state.get(AXIS)) {
      case Z:
         return Z_AABB;
      case X:
      default:
         return X_AABB;
      }
   }

   public void tick(BlockState state, World worldIn, BlockPos pos, Random random) {
      if (worldIn.dimension.isSurfaceWorld() && worldIn.getGameRules().func_223586_b(GameRules.field_223601_d) && random.nextInt(2000) < worldIn.getDifficulty().getId()) {
         while(worldIn.getBlockState(pos).getBlock() == this) {
            pos = pos.down();
         }

         if (worldIn.getBlockState(pos).canEntitySpawn(worldIn, pos, EntityType.ZOMBIE_PIGMAN)) {
            Entity entity = EntityType.ZOMBIE_PIGMAN.spawn(worldIn, (CompoundNBT)null, (ITextComponent)null, (PlayerEntity)null, pos.up(), SpawnReason.STRUCTURE, false, false);
            if (entity != null) {
               entity.timeUntilPortal = entity.getPortalCooldown();
            }
         }
      }

   }

   public boolean trySpawnPortal(IWorld worldIn, BlockPos pos) {
      NetherPortalBlock.Size netherportalblock$size = this.isPortal(worldIn, pos);
      if (netherportalblock$size != null && !net.minecraftforge.event.ForgeEventFactory.onTrySpawnPortal(worldIn, pos, netherportalblock$size)) {
         netherportalblock$size.placePortalBlocks();
         return true;
      } else {
         return false;
      }
   }

   @Nullable
   public NetherPortalBlock.Size isPortal(IWorld p_201816_1_, BlockPos p_201816_2_) {
      NetherPortalBlock.Size netherportalblock$size = new NetherPortalBlock.Size(p_201816_1_, p_201816_2_, Direction.Axis.X);
      if (netherportalblock$size.isValid() && netherportalblock$size.portalBlockCount == 0) {
         return netherportalblock$size;
      } else {
         NetherPortalBlock.Size netherportalblock$size1 = new NetherPortalBlock.Size(p_201816_1_, p_201816_2_, Direction.Axis.Z);
         return netherportalblock$size1.isValid() && netherportalblock$size1.portalBlockCount == 0 ? netherportalblock$size1 : null;
      }
   }

   /**
    * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
    * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
    * returns its solidified counterpart.
    * Note that this method should ideally consider only the specific face passed in.
    */
   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      Direction.Axis direction$axis = facing.getAxis();
      Direction.Axis direction$axis1 = stateIn.get(AXIS);
      boolean flag = direction$axis1 != direction$axis && direction$axis.isHorizontal();
      return !flag && facingState.getBlock() != this && !(new NetherPortalBlock.Size(worldIn, currentPos, direction$axis1)).func_208508_f() ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   /**
    * Gets the render layer this block will render on. SOLID for solid blocks, CUTOUT or CUTOUT_MIPPED for on-off
    * transparency (glass, reeds), TRANSLUCENT for fully blended transparency (stained glass)
    */
   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.TRANSLUCENT;
   }

   public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
      if (!entityIn.isPassenger() && !entityIn.isBeingRidden() && entityIn.isNonBoss()) {
         entityIn.setPortal(pos);
      }

   }

   /**
    * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
    * this method is unrelated to {@link randomTick} and {@link #needsRandomTick}, and will always be called regardless
    * of whether the block can receive random update ticks
    */
   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
      if (rand.nextInt(100) == 0) {
         worldIn.playSound((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.BLOCKS, 0.5F, rand.nextFloat() * 0.4F + 0.8F, false);
      }

      for(int i = 0; i < 4; ++i) {
         double d0 = (double)((float)pos.getX() + rand.nextFloat());
         double d1 = (double)((float)pos.getY() + rand.nextFloat());
         double d2 = (double)((float)pos.getZ() + rand.nextFloat());
         double d3 = ((double)rand.nextFloat() - 0.5D) * 0.5D;
         double d4 = ((double)rand.nextFloat() - 0.5D) * 0.5D;
         double d5 = ((double)rand.nextFloat() - 0.5D) * 0.5D;
         int j = rand.nextInt(2) * 2 - 1;
         if (worldIn.getBlockState(pos.west()).getBlock() != this && worldIn.getBlockState(pos.east()).getBlock() != this) {
            d0 = (double)pos.getX() + 0.5D + 0.25D * (double)j;
            d3 = (double)(rand.nextFloat() * 2.0F * (float)j);
         } else {
            d2 = (double)pos.getZ() + 0.5D + 0.25D * (double)j;
            d5 = (double)(rand.nextFloat() * 2.0F * (float)j);
         }

         worldIn.addParticle(ParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
      return ItemStack.EMPTY;
   }

   /**
    * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
    * fine.
    */
   public BlockState rotate(BlockState state, Rotation rot) {
      switch(rot) {
      case COUNTERCLOCKWISE_90:
      case CLOCKWISE_90:
         switch((Direction.Axis)state.get(AXIS)) {
         case Z:
            return state.with(AXIS, Direction.Axis.X);
         case X:
            return state.with(AXIS, Direction.Axis.Z);
         default:
            return state;
         }
      default:
         return state;
      }
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(AXIS);
   }

   public BlockPattern.PatternHelper createPatternHelper(IWorld worldIn, BlockPos p_181089_2_) {
      Direction.Axis direction$axis = Direction.Axis.Z;
      NetherPortalBlock.Size netherportalblock$size = new NetherPortalBlock.Size(worldIn, p_181089_2_, Direction.Axis.X);
      LoadingCache<BlockPos, CachedBlockInfo> loadingcache = BlockPattern.createLoadingCache(worldIn, true);
      if (!netherportalblock$size.isValid()) {
         direction$axis = Direction.Axis.X;
         netherportalblock$size = new NetherPortalBlock.Size(worldIn, p_181089_2_, Direction.Axis.Z);
      }

      if (!netherportalblock$size.isValid()) {
         return new BlockPattern.PatternHelper(p_181089_2_, Direction.NORTH, Direction.UP, loadingcache, 1, 1, 1);
      } else {
         int[] aint = new int[Direction.AxisDirection.values().length];
         Direction direction = netherportalblock$size.rightDir.rotateYCCW();
         BlockPos blockpos = netherportalblock$size.bottomLeft.up(netherportalblock$size.getHeight() - 1);

         for(Direction.AxisDirection direction$axisdirection : Direction.AxisDirection.values()) {
            BlockPattern.PatternHelper blockpattern$patternhelper = new BlockPattern.PatternHelper(direction.getAxisDirection() == direction$axisdirection ? blockpos : blockpos.offset(netherportalblock$size.rightDir, netherportalblock$size.getWidth() - 1), Direction.getFacingFromAxis(direction$axisdirection, direction$axis), Direction.UP, loadingcache, netherportalblock$size.getWidth(), netherportalblock$size.getHeight(), 1);

            for(int i = 0; i < netherportalblock$size.getWidth(); ++i) {
               for(int j = 0; j < netherportalblock$size.getHeight(); ++j) {
                  CachedBlockInfo cachedblockinfo = blockpattern$patternhelper.translateOffset(i, j, 1);
                  if (!cachedblockinfo.getBlockState().isAir()) {
                     ++aint[direction$axisdirection.ordinal()];
                  }
               }
            }
         }

         Direction.AxisDirection direction$axisdirection1 = Direction.AxisDirection.POSITIVE;

         for(Direction.AxisDirection direction$axisdirection2 : Direction.AxisDirection.values()) {
            if (aint[direction$axisdirection2.ordinal()] < aint[direction$axisdirection1.ordinal()]) {
               direction$axisdirection1 = direction$axisdirection2;
            }
         }

         return new BlockPattern.PatternHelper(direction.getAxisDirection() == direction$axisdirection1 ? blockpos : blockpos.offset(netherportalblock$size.rightDir, netherportalblock$size.getWidth() - 1), Direction.getFacingFromAxis(direction$axisdirection1, direction$axis), Direction.UP, loadingcache, netherportalblock$size.getWidth(), netherportalblock$size.getHeight(), 1);
      }
   }

   public static class Size {
      private final IWorld world;
      private final Direction.Axis axis;
      private final Direction rightDir;
      private final Direction leftDir;
      private int portalBlockCount;
      @Nullable
      private BlockPos bottomLeft;
      private int height;
      private int width;

      public Size(IWorld p_i48740_1_, BlockPos p_i48740_2_, Direction.Axis p_i48740_3_) {
         this.world = p_i48740_1_;
         this.axis = p_i48740_3_;
         if (p_i48740_3_ == Direction.Axis.X) {
            this.leftDir = Direction.EAST;
            this.rightDir = Direction.WEST;
         } else {
            this.leftDir = Direction.NORTH;
            this.rightDir = Direction.SOUTH;
         }

         for(BlockPos blockpos = p_i48740_2_; p_i48740_2_.getY() > blockpos.getY() - 21 && p_i48740_2_.getY() > 0 && this.func_196900_a(p_i48740_1_.getBlockState(p_i48740_2_.down())); p_i48740_2_ = p_i48740_2_.down()) {
            ;
         }

         int i = this.getDistanceUntilEdge(p_i48740_2_, this.leftDir) - 1;
         if (i >= 0) {
            this.bottomLeft = p_i48740_2_.offset(this.leftDir, i);
            this.width = this.getDistanceUntilEdge(this.bottomLeft, this.rightDir);
            if (this.width < 2 || this.width > 21) {
               this.bottomLeft = null;
               this.width = 0;
            }
         }

         if (this.bottomLeft != null) {
            this.height = this.calculatePortalHeight();
         }

      }

      protected int getDistanceUntilEdge(BlockPos p_180120_1_, Direction p_180120_2_) {
         int i;
         for(i = 0; i < 22; ++i) {
            BlockPos blockpos = p_180120_1_.offset(p_180120_2_, i);
            if (!this.func_196900_a(this.world.getBlockState(blockpos)) || this.world.getBlockState(blockpos.down()).getBlock() != Blocks.OBSIDIAN) {
               break;
            }
         }

         Block block = this.world.getBlockState(p_180120_1_.offset(p_180120_2_, i)).getBlock();
         return block == Blocks.OBSIDIAN ? i : 0;
      }

      public int getHeight() {
         return this.height;
      }

      public int getWidth() {
         return this.width;
      }

      protected int calculatePortalHeight() {
         label56:
         for(this.height = 0; this.height < 21; ++this.height) {
            for(int i = 0; i < this.width; ++i) {
               BlockPos blockpos = this.bottomLeft.offset(this.rightDir, i).up(this.height);
               BlockState blockstate = this.world.getBlockState(blockpos);
               if (!this.func_196900_a(blockstate)) {
                  break label56;
               }

               Block block = blockstate.getBlock();
               if (block == Blocks.NETHER_PORTAL) {
                  ++this.portalBlockCount;
               }

               if (i == 0) {
                  block = this.world.getBlockState(blockpos.offset(this.leftDir)).getBlock();
                  if (block != Blocks.OBSIDIAN) {
                     break label56;
                  }
               } else if (i == this.width - 1) {
                  block = this.world.getBlockState(blockpos.offset(this.rightDir)).getBlock();
                  if (block != Blocks.OBSIDIAN) {
                     break label56;
                  }
               }
            }
         }

         for(int j = 0; j < this.width; ++j) {
            if (this.world.getBlockState(this.bottomLeft.offset(this.rightDir, j).up(this.height)).getBlock() != Blocks.OBSIDIAN) {
               this.height = 0;
               break;
            }
         }

         if (this.height <= 21 && this.height >= 3) {
            return this.height;
         } else {
            this.bottomLeft = null;
            this.width = 0;
            this.height = 0;
            return 0;
         }
      }

      protected boolean func_196900_a(BlockState p_196900_1_) {
         Block block = p_196900_1_.getBlock();
         return p_196900_1_.isAir() || block == Blocks.FIRE || block == Blocks.NETHER_PORTAL;
      }

      public boolean isValid() {
         return this.bottomLeft != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
      }

      public void placePortalBlocks() {
         for(int i = 0; i < this.width; ++i) {
            BlockPos blockpos = this.bottomLeft.offset(this.rightDir, i);

            for(int j = 0; j < this.height; ++j) {
               this.world.setBlockState(blockpos.up(j), Blocks.NETHER_PORTAL.getDefaultState().with(NetherPortalBlock.AXIS, this.axis), 18);
            }
         }

      }

      private boolean func_196899_f() {
         return this.portalBlockCount >= this.width * this.height;
      }

      public boolean func_208508_f() {
         return this.isValid() && this.func_196899_f();
      }
   }
}