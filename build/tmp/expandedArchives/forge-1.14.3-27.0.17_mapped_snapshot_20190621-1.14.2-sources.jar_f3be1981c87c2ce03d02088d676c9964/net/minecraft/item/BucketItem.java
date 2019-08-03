package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BucketItem extends Item {
   private final Fluid containedBlock;

   public BucketItem(Fluid containedFluidIn, Item.Properties builder) {
      super(builder);
      this.containedBlock = containedFluidIn;
   }

   /**
    * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
    * {@link #onItemUse}.
    */
   public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
      ItemStack itemstack = playerIn.getHeldItem(handIn);
      RayTraceResult raytraceresult = rayTrace(worldIn, playerIn, this.containedBlock == Fluids.EMPTY ? RayTraceContext.FluidMode.SOURCE_ONLY : RayTraceContext.FluidMode.NONE);
      ActionResult<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onBucketUse(playerIn, worldIn, itemstack, raytraceresult);
      if (ret != null) return ret;
      if (raytraceresult.getType() == RayTraceResult.Type.MISS) {
         return new ActionResult<>(ActionResultType.PASS, itemstack);
      } else if (raytraceresult.getType() != RayTraceResult.Type.BLOCK) {
         return new ActionResult<>(ActionResultType.PASS, itemstack);
      } else {
         BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult)raytraceresult;
         BlockPos blockpos = blockraytraceresult.getPos();
         if (worldIn.isBlockModifiable(playerIn, blockpos) && playerIn.canPlayerEdit(blockpos, blockraytraceresult.getFace(), itemstack)) {
            if (this.containedBlock == Fluids.EMPTY) {
               BlockState blockstate1 = worldIn.getBlockState(blockpos);
               if (blockstate1.getBlock() instanceof IBucketPickupHandler) {
                  Fluid fluid = ((IBucketPickupHandler)blockstate1.getBlock()).pickupFluid(worldIn, blockpos, blockstate1);
                  if (fluid != Fluids.EMPTY) {
                     playerIn.addStat(Stats.ITEM_USED.get(this));
                     playerIn.playSound(fluid.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_FILL_LAVA : SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
                     ItemStack itemstack1 = this.fillBucket(itemstack, playerIn, fluid.getFilledBucket());
                     if (!worldIn.isRemote) {
                        CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayerEntity)playerIn, new ItemStack(fluid.getFilledBucket()));
                     }

                     return new ActionResult<>(ActionResultType.SUCCESS, itemstack1);
                  }
               }

               return new ActionResult<>(ActionResultType.FAIL, itemstack);
            } else {
               BlockState blockstate = worldIn.getBlockState(blockpos);
               BlockPos blockpos1 = blockstate.getBlock() instanceof ILiquidContainer && this.containedBlock == Fluids.WATER ? blockpos : blockraytraceresult.getPos().offset(blockraytraceresult.getFace());
               if (this.tryPlaceContainedLiquid(playerIn, worldIn, blockpos1, blockraytraceresult)) {
                  this.onLiquidPlaced(worldIn, itemstack, blockpos1);
                  if (playerIn instanceof ServerPlayerEntity) {
                     CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)playerIn, blockpos1, itemstack);
                  }

                  playerIn.addStat(Stats.ITEM_USED.get(this));
                  return new ActionResult<>(ActionResultType.SUCCESS, this.emptyBucket(itemstack, playerIn));
               } else {
                  return new ActionResult<>(ActionResultType.FAIL, itemstack);
               }
            }
         } else {
            return new ActionResult<>(ActionResultType.FAIL, itemstack);
         }
      }
   }

   protected ItemStack emptyBucket(ItemStack p_203790_1_, PlayerEntity p_203790_2_) {
      return !p_203790_2_.abilities.isCreativeMode ? new ItemStack(Items.BUCKET) : p_203790_1_;
   }

   public void onLiquidPlaced(World worldIn, ItemStack p_203792_2_, BlockPos pos) {
   }

   private ItemStack fillBucket(ItemStack emptyBuckets, PlayerEntity player, Item fullBucket) {
      if (player.abilities.isCreativeMode) {
         return emptyBuckets;
      } else {
         emptyBuckets.shrink(1);
         if (emptyBuckets.isEmpty()) {
            return new ItemStack(fullBucket);
         } else {
            if (!player.inventory.addItemStackToInventory(new ItemStack(fullBucket))) {
               player.dropItem(new ItemStack(fullBucket), false);
            }

            return emptyBuckets;
         }
      }
   }

   public boolean tryPlaceContainedLiquid(@Nullable PlayerEntity player, World worldIn, BlockPos posIn, @Nullable BlockRayTraceResult p_180616_4_) {
      if (!(this.containedBlock instanceof FlowingFluid)) {
         return false;
      } else {
         BlockState blockstate = worldIn.getBlockState(posIn);
         Material material = blockstate.getMaterial();
         boolean flag = !material.isSolid();
         boolean flag1 = material.isReplaceable();
         if (worldIn.isAirBlock(posIn) || flag || flag1 || blockstate.getBlock() instanceof ILiquidContainer && ((ILiquidContainer)blockstate.getBlock()).canContainFluid(worldIn, posIn, blockstate, this.containedBlock)) {
            if (worldIn.dimension.doesWaterVaporize() && this.containedBlock.isIn(FluidTags.WATER)) {
               int i = posIn.getX();
               int j = posIn.getY();
               int k = posIn.getZ();
               worldIn.playSound(player, posIn, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8F);

               for(int l = 0; l < 8; ++l) {
                  worldIn.addParticle(ParticleTypes.LARGE_SMOKE, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0D, 0.0D, 0.0D);
               }
            } else if (blockstate.getBlock() instanceof ILiquidContainer && this.containedBlock == Fluids.WATER) {
               if (((ILiquidContainer)blockstate.getBlock()).receiveFluid(worldIn, posIn, blockstate, ((FlowingFluid)this.containedBlock).getStillFluidState(false))) {
                  this.playEmptySound(player, worldIn, posIn);
               }
            } else {
               if (!worldIn.isRemote && (flag || flag1) && !material.isLiquid()) {
                  worldIn.destroyBlock(posIn, true);
               }

               this.playEmptySound(player, worldIn, posIn);
               worldIn.setBlockState(posIn, this.containedBlock.getDefaultState().getBlockState(), 11);
            }

            return true;
         } else {
            return p_180616_4_ == null ? false : this.tryPlaceContainedLiquid(player, worldIn, p_180616_4_.getPos().offset(p_180616_4_.getFace()), (BlockRayTraceResult)null);
         }
      }
   }

   protected void playEmptySound(@Nullable PlayerEntity player, IWorld worldIn, BlockPos pos) {
      SoundEvent soundevent = this.containedBlock.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_EMPTY_LAVA : SoundEvents.ITEM_BUCKET_EMPTY;
      worldIn.playSound(player, pos, soundevent, SoundCategory.BLOCKS, 1.0F, 1.0F);
   }

   @Override
   public net.minecraftforge.common.capabilities.ICapabilityProvider initCapabilities(ItemStack stack, @Nullable net.minecraft.nbt.CompoundNBT nbt) {
      if (this.getClass() == BucketItem.class)
         return new net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper(stack);
      else
         return super.initCapabilities(stack, nbt);
   }
}