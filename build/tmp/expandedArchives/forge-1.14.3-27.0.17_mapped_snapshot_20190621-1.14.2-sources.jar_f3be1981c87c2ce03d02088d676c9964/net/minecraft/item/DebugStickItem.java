package net.minecraft.item;

import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DebugStickItem extends Item {
   public DebugStickItem(Item.Properties builder) {
      super(builder);
   }

   /**
    * Returns true if this item has an enchantment glint. By default, this returns <code>stack.isItemEnchanted()</code>,
    * but other items can override it (for instance, written books always return true).
    *  
    * Note that if you override this method, you generally want to also call the super version (on {@link Item}) to get
    * the glint for enchanted items. Of course, that is unnecessary if the overwritten version always returns true.
    */
   @OnlyIn(Dist.CLIENT)
   public boolean hasEffect(ItemStack stack) {
      return true;
   }

   public boolean canPlayerBreakBlockWhileHolding(BlockState state, World worldIn, BlockPos pos, PlayerEntity player) {
      if (!worldIn.isRemote) {
         this.handleClick(player, state, worldIn, pos, false, player.getHeldItem(Hand.MAIN_HAND));
      }

      return false;
   }

   /**
    * Called when this item is used when targetting a Block
    */
   public ActionResultType onItemUse(ItemUseContext context) {
      PlayerEntity playerentity = context.getPlayer();
      World world = context.getWorld();
      if (!world.isRemote && playerentity != null) {
         BlockPos blockpos = context.getPos();
         this.handleClick(playerentity, world.getBlockState(blockpos), world, blockpos, true, context.getItem());
      }

      return ActionResultType.SUCCESS;
   }

   private void handleClick(PlayerEntity p_195958_1_, BlockState p_195958_2_, IWorld p_195958_3_, BlockPos p_195958_4_, boolean rightClick, ItemStack p_195958_6_) {
      if (p_195958_1_.canUseCommandBlock()) {
         Block block = p_195958_2_.getBlock();
         StateContainer<Block, BlockState> statecontainer = block.getStateContainer();
         Collection<IProperty<?>> collection = statecontainer.getProperties();
         String s = Registry.BLOCK.getKey(block).toString();
         if (collection.isEmpty()) {
            sendMessage(p_195958_1_, new TranslationTextComponent(this.getTranslationKey() + ".empty", s));
         } else {
            CompoundNBT compoundnbt = p_195958_6_.getOrCreateChildTag("DebugProperty");
            String s1 = compoundnbt.getString(s);
            IProperty<?> iproperty = statecontainer.getProperty(s1);
            if (rightClick) {
               if (iproperty == null) {
                  iproperty = collection.iterator().next();
               }

               BlockState blockstate = cycleProperty(p_195958_2_, iproperty, p_195958_1_.isSneaking());
               p_195958_3_.setBlockState(p_195958_4_, blockstate, 18);
               sendMessage(p_195958_1_, new TranslationTextComponent(this.getTranslationKey() + ".update", iproperty.getName(), func_195957_a(blockstate, iproperty)));
            } else {
               iproperty = getAdjacentValue(collection, iproperty, p_195958_1_.isSneaking());
               String s2 = iproperty.getName();
               compoundnbt.putString(s, s2);
               sendMessage(p_195958_1_, new TranslationTextComponent(this.getTranslationKey() + ".select", s2, func_195957_a(p_195958_2_, iproperty)));
            }

         }
      }
   }

   private static <T extends Comparable<T>> BlockState cycleProperty(BlockState p_195960_0_, IProperty<T> p_195960_1_, boolean backwards) {
      return p_195960_0_.with(p_195960_1_, (T)(getAdjacentValue(p_195960_1_.getAllowedValues(), p_195960_0_.get(p_195960_1_), backwards)));
   }

   private static <T> T getAdjacentValue(Iterable<T> p_195959_0_, @Nullable T p_195959_1_, boolean p_195959_2_) {
      return (T)(p_195959_2_ ? Util.getElementBefore(p_195959_0_, p_195959_1_) : Util.getElementAfter(p_195959_0_, p_195959_1_));
   }

   private static void sendMessage(PlayerEntity p_195956_0_, ITextComponent p_195956_1_) {
      ((ServerPlayerEntity)p_195956_0_).sendMessage(p_195956_1_, ChatType.GAME_INFO);
   }

   private static <T extends Comparable<T>> String func_195957_a(BlockState p_195957_0_, IProperty<T> p_195957_1_) {
      return p_195957_1_.getName(p_195957_0_.get(p_195957_1_));
   }
}