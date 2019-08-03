package net.minecraft.server.management;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CommandBlockBlock;
import net.minecraft.block.JigsawBlock;
import net.minecraft.block.StructureBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.network.play.server.SChangeBlockPacket;
import net.minecraft.network.play.server.SPlayerListItemPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.GameType;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.World;

public class PlayerInteractionManager {
   public ServerWorld world;
   public ServerPlayerEntity player;
   private GameType gameType = GameType.NOT_SET;
   private boolean isDestroyingBlock;
   private int initialDamage;
   private BlockPos destroyPos = BlockPos.ZERO;
   private int ticks;
   private boolean receivedFinishDiggingPacket;
   private BlockPos delayedDestroyPos = BlockPos.ZERO;
   private int initialBlockDamage;
   private int durabilityRemainingOnBlock = -1;

   public PlayerInteractionManager(ServerWorld p_i50702_1_) {
      this.world = p_i50702_1_;
   }

   public void setGameType(GameType type) {
      this.gameType = type;
      type.configurePlayerCapabilities(this.player.abilities);
      this.player.sendPlayerAbilities();
      this.player.server.getPlayerList().sendPacketToAllPlayers(new SPlayerListItemPacket(SPlayerListItemPacket.Action.UPDATE_GAME_MODE, this.player));
      this.world.updateAllPlayersSleepingFlag();
   }

   public GameType getGameType() {
      return this.gameType;
   }

   public boolean survivalOrAdventure() {
      return this.gameType.isSurvivalOrAdventure();
   }

   /**
    * Get if we are in creative game mode.
    */
   public boolean isCreative() {
      return this.gameType.isCreative();
   }

   /**
    * if the gameType is currently NOT_SET then change it to par1
    */
   public void initializeGameType(GameType type) {
      if (this.gameType == GameType.NOT_SET) {
         this.gameType = type;
      }

      this.setGameType(this.gameType);
   }

   public void tick() {
      ++this.ticks;
      if (this.receivedFinishDiggingPacket) {
         int i = this.ticks - this.initialBlockDamage;
         BlockState blockstate = this.world.getBlockState(this.delayedDestroyPos);
         if (blockstate.isAir(world, delayedDestroyPos)) {
            this.receivedFinishDiggingPacket = false;
         } else {
            float f = blockstate.getPlayerRelativeBlockHardness(this.player, this.player.world, this.delayedDestroyPos) * (float)(i + 1);
            int j = (int)(f * 10.0F);
            if (j != this.durabilityRemainingOnBlock) {
               this.world.sendBlockBreakProgress(this.player.getEntityId(), this.delayedDestroyPos, j);
               this.durabilityRemainingOnBlock = j;
            }

            if (f >= 1.0F) {
               this.receivedFinishDiggingPacket = false;
               this.tryHarvestBlock(this.delayedDestroyPos);
            }
         }
      } else if (this.isDestroyingBlock) {
         BlockState blockstate1 = this.world.getBlockState(this.destroyPos);
         if (blockstate1.isAir(world, destroyPos)) {
            this.world.sendBlockBreakProgress(this.player.getEntityId(), this.destroyPos, -1);
            this.durabilityRemainingOnBlock = -1;
            this.isDestroyingBlock = false;
         } else {
            int k = this.ticks - this.initialDamage;
            float f1 = blockstate1.getPlayerRelativeBlockHardness(this.player, this.player.world, this.destroyPos) * (float)(k + 1); // Forge: Fix network break progress using wrong position
            int l = (int)(f1 * 10.0F);
            if (l != this.durabilityRemainingOnBlock) {
               this.world.sendBlockBreakProgress(this.player.getEntityId(), this.destroyPos, l);
               this.durabilityRemainingOnBlock = l;
            }
         }
      }

   }

   /**
    * If not creative, it calls sendBlockBreakProgress until the block is broken first. tryHarvestBlock can also be the
    * result of this call.
    */
   public void startDestroyBlock(BlockPos pos, Direction side) {
      net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock event = net.minecraftforge.common.ForgeHooks.onLeftClickBlock(player, pos, side);
      if (event.isCanceled()) { // Restore block and te data
         player.connection.sendPacket(new SChangeBlockPacket(world, pos));
         world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
         return;
      }
      if (this.isCreative()) {
         if (!this.world.extinguishFire((PlayerEntity)null, pos, side)) {
            this.tryHarvestBlock(pos);
         }

      } else {
         if (this.gameType.hasLimitedInteractions()) {
            if (this.gameType == GameType.SPECTATOR) {
               return;
            }

            if (!this.player.isAllowEdit()) {
               ItemStack itemstack = this.player.getHeldItemMainhand();
               if (itemstack.isEmpty()) {
                  return;
               }

               CachedBlockInfo cachedblockinfo = new CachedBlockInfo(this.world, pos, false);
               if (!itemstack.canDestroy(this.world.getTags(), cachedblockinfo)) {
                  return;
               }
            }
         }

         this.world.extinguishFire((PlayerEntity)null, pos, side);
         this.initialDamage = this.ticks;
         float f = 1.0F;
         BlockState blockstate = this.world.getBlockState(pos);
         if (!blockstate.isAir(world, pos)) {
            if (event.getUseBlock() != net.minecraftforge.eventbus.api.Event.Result.DENY) {
            blockstate.onBlockClicked(this.world, pos, this.player);
               this.world.extinguishFire((PlayerEntity)null, pos, side);
            } else { // Restore block and te data
               player.connection.sendPacket(new SChangeBlockPacket(world, pos));
               world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
            }
            f = blockstate.getPlayerRelativeBlockHardness(this.player, this.player.world, pos);
         }

         if (event.getUseItem() == net.minecraftforge.eventbus.api.Event.Result.DENY) {
            if (f >= 1.0F) { // Restore block and te data
               player.connection.sendPacket(new SChangeBlockPacket(world, pos));
               world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
            }
            return;
         }

         if (!blockstate.isAir(world, pos) && f >= 1.0F) {
            this.tryHarvestBlock(pos);
         } else {
            this.isDestroyingBlock = true;
            this.destroyPos = pos;
            int i = (int)(f * 10.0F);
            this.world.sendBlockBreakProgress(this.player.getEntityId(), pos, i);
            this.player.connection.sendPacket(new SChangeBlockPacket(this.world, pos));
            this.durabilityRemainingOnBlock = i;
         }

      }
   }

   public void stopDestroyBlock(BlockPos pos) {
      if (pos.equals(this.destroyPos)) {
         int i = this.ticks - this.initialDamage;
         BlockState blockstate = this.world.getBlockState(pos);
         if (!blockstate.isAir(world, pos)) {
            float f = blockstate.getPlayerRelativeBlockHardness(this.player, this.player.world, pos) * (float)(i + 1);
            if (f >= 0.7F) {
               this.isDestroyingBlock = false;
               this.world.sendBlockBreakProgress(this.player.getEntityId(), pos, -1);
               this.tryHarvestBlock(pos);
            } else if (!this.receivedFinishDiggingPacket) {
               this.isDestroyingBlock = false;
               this.receivedFinishDiggingPacket = true;
               this.delayedDestroyPos = pos;
               this.initialBlockDamage = this.initialDamage;
            }
         }
      }

   }

   /**
    * Stops the block breaking process
    */
   public void abortDestroyBlock() {
      this.isDestroyingBlock = false;
      this.world.sendBlockBreakProgress(this.player.getEntityId(), this.destroyPos, -1);
   }

   /**
    * Removes a block and triggers the appropriate events
    */
   private boolean removeBlock(BlockPos pos) {
      return removeBlock(pos, false);
   }
   private boolean removeBlock(BlockPos pos, boolean canHarvest) {
      BlockState blockstate = this.world.getBlockState(pos);
      boolean flag = blockstate.removedByPlayer(world, pos, player, canHarvest, world.getFluidState(pos));
      if (flag) {
         blockstate.getBlock().onPlayerDestroy(this.world, pos, blockstate);
      }

      return flag;
   }

   /**
    * Attempts to harvest a block
    */
   public boolean tryHarvestBlock(BlockPos pos) {
      BlockState blockstate = this.world.getBlockState(pos);
      int exp = net.minecraftforge.common.ForgeHooks.onBlockBreakEvent(world, gameType, player, pos);
      if (exp == -1) {
         return false;
      } else {
         TileEntity tileentity = this.world.getTileEntity(pos);
         Block block = blockstate.getBlock();
         if ((block instanceof CommandBlockBlock || block instanceof StructureBlock || block instanceof JigsawBlock) && !this.player.canUseCommandBlock()) {
            this.world.notifyBlockUpdate(pos, blockstate, blockstate, 3);
            return false;
         } else {
            ItemStack stack = player.getHeldItemMainhand();
            if (stack.onBlockStartBreak(pos, player)) {
               return false;
            }

            if (this.gameType.hasLimitedInteractions()) {
               if (this.gameType == GameType.SPECTATOR) {
                  return false;
               }

               if (!this.player.isAllowEdit()) {
                  ItemStack itemstack = this.player.getHeldItemMainhand();
                  if (itemstack.isEmpty()) {
                     return false;
                  }

                  CachedBlockInfo cachedblockinfo = new CachedBlockInfo(this.world, pos, false);
                  if (!itemstack.canDestroy(this.world.getTags(), cachedblockinfo)) {
                     return false;
                  }
               }
            }

            boolean flag1 = false;
            if (!this.isCreative()) {
               ItemStack itemstack2 = this.player.getHeldItemMainhand();
               ItemStack copy = itemstack2.copy();
               boolean flag = blockstate.canHarvestBlock(world, pos, player);
               itemstack2.onBlockDestroyed(this.world, blockstate, pos, this.player);
               if (itemstack2.isEmpty() && !copy.isEmpty()) {
                  net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(this.player, copy, Hand.MAIN_HAND);
               }
               flag1 = this.removeBlock(pos, flag);
               if (flag1 && flag) {
                  ItemStack itemstack1 = itemstack2.isEmpty() ? ItemStack.EMPTY : itemstack2.copy();
                  blockstate.getBlock().harvestBlock(this.world, this.player, pos, blockstate, tileentity, itemstack1);
               }
            } else {
               flag1 = this.removeBlock(pos);
            }

            if (!this.isCreative() && flag1 && exp > 0) { // Drop experience
               blockstate.getBlock().dropXpOnBlockBreak(world, pos, exp);
            }

            return flag1;
         }
      }
   }

   public ActionResultType processRightClick(PlayerEntity player, World worldIn, ItemStack stack, Hand hand) {
      if (this.gameType == GameType.SPECTATOR) {
         return ActionResultType.PASS;
      } else if (player.getCooldownTracker().hasCooldown(stack.getItem())) {
         return ActionResultType.PASS;
      } else {
         ActionResultType cancelResult = net.minecraftforge.common.ForgeHooks.onItemRightClick(player, hand);
         if (cancelResult != null) return cancelResult;
         int i = stack.getCount();
         int j = stack.getDamage();
         ItemStack copyBeforeUse = stack.copy();
         ActionResult<ItemStack> actionresult = stack.useItemRightClick(worldIn, player, hand);
         ItemStack itemstack = actionresult.getResult();
         if (itemstack == stack && itemstack.getCount() == i && itemstack.getUseDuration() <= 0 && itemstack.getDamage() == j) {
            return actionresult.getType();
         } else if (actionresult.getType() == ActionResultType.FAIL && itemstack.getUseDuration() > 0 && !player.isHandActive()) {
            return actionresult.getType();
         } else {
            player.setHeldItem(hand, itemstack);
            if (this.isCreative()) {
               itemstack.setCount(i);
               if (itemstack.isDamageable()) {
                  itemstack.setDamage(j);
               }
            }

            if (itemstack.isEmpty()) {
               net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, copyBeforeUse, hand);
               player.setHeldItem(hand, ItemStack.EMPTY);
            }

            if (!player.isHandActive()) {
               ((ServerPlayerEntity)player).sendContainerToPlayer(player.container);
            }

            return actionresult.getType();
         }
      }
   }

   public ActionResultType func_219441_a(PlayerEntity p_219441_1_, World p_219441_2_, ItemStack p_219441_3_, Hand p_219441_4_, BlockRayTraceResult p_219441_5_) {
      BlockPos blockpos = p_219441_5_.getPos();
      BlockState blockstate = p_219441_2_.getBlockState(blockpos);
      if (this.gameType == GameType.SPECTATOR) {
         INamedContainerProvider inamedcontainerprovider = blockstate.getContainer(p_219441_2_, blockpos);
         if (inamedcontainerprovider != null) {
            p_219441_1_.openContainer(inamedcontainerprovider);
            return ActionResultType.SUCCESS;
         } else {
            return ActionResultType.PASS;
         }
      } else {
         net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock event = net.minecraftforge.common.ForgeHooks.onRightClickBlock(player, p_219441_4_, blockpos, p_219441_5_.getFace());
         if (event.isCanceled()) return event.getCancellationResult();

         ActionResultType result = ActionResultType.PASS;
         if (event.getUseItem() != net.minecraftforge.eventbus.api.Event.Result.DENY) {
            result = p_219441_3_.onItemUseFirst(new ItemUseContext(p_219441_1_, p_219441_4_, p_219441_5_));
            if (result != ActionResultType.PASS) {
               return result;
            }
         }

         boolean flag = !(player.getHeldItemMainhand().doesSneakBypassUse(p_219441_2_, blockpos, p_219441_1_) && player.getHeldItemOffhand().doesSneakBypassUse(p_219441_2_, blockpos, p_219441_1_));
         boolean flag1 = p_219441_1_.isSneaking() && flag;
         if ((!flag1 || event.getUseBlock() == net.minecraftforge.eventbus.api.Event.Result.ALLOW) &&
               event.getUseBlock() != net.minecraftforge.eventbus.api.Event.Result.DENY &&
               blockstate.onBlockActivated(p_219441_2_, p_219441_1_, p_219441_4_, p_219441_5_)) {
            result = ActionResultType.SUCCESS;
         }

         if (!p_219441_3_.isEmpty() && !p_219441_1_.getCooldownTracker().hasCooldown(p_219441_3_.getItem())) {
            ItemUseContext itemusecontext = new ItemUseContext(p_219441_1_, p_219441_4_, p_219441_5_);
            if (this.isCreative()) {
               int i = p_219441_3_.getCount();
               if ((result != ActionResultType.SUCCESS && event.getUseItem() != net.minecraftforge.eventbus.api.Event.Result.DENY) ||
                   (result == ActionResultType.SUCCESS && event.getUseItem() == net.minecraftforge.eventbus.api.Event.Result.ALLOW)) {
               ActionResultType actionresulttype = p_219441_3_.onItemUse(itemusecontext);
               p_219441_3_.setCount(i);
               return actionresulttype;
               } else return result;
            } else {
               if ((result != ActionResultType.SUCCESS && event.getUseItem() != net.minecraftforge.eventbus.api.Event.Result.DENY) ||
                   (result == ActionResultType.SUCCESS && event.getUseItem() == net.minecraftforge.eventbus.api.Event.Result.ALLOW)) {
                  ItemStack copyBeforeUse = p_219441_3_.copy();
                  result = p_219441_3_.onItemUse(itemusecontext);
                  if (p_219441_3_.isEmpty()) {
                     net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, copyBeforeUse, p_219441_4_);
                  }
               }
               return result;
            }
         } else {
            return ActionResultType.PASS;
         }
      }
   }

   /**
    * Sets the world instance.
    */
   public void setWorld(ServerWorld serverWorld) {
      this.world = serverWorld;
   }
}