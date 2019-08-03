package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ICrossbowUser;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.FireworkRocketEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CrossbowItem extends ShootableItem {
   private boolean field_220034_c = false;
   private boolean field_220035_d = false;

   public CrossbowItem(Item.Properties p_i50052_1_) {
      super(p_i50052_1_);
      this.addPropertyOverride(new ResourceLocation("pull"), (p_220022_1_, p_220022_2_, p_220022_3_) -> {
         if (p_220022_3_ != null && p_220022_1_.getItem() == this) {
            return isCharged(p_220022_1_) ? 0.0F : (float)(p_220022_1_.getUseDuration() - p_220022_3_.getItemInUseCount()) / (float)func_220026_e(p_220022_1_);
         } else {
            return 0.0F;
         }
      });
      this.addPropertyOverride(new ResourceLocation("pulling"), (p_220033_0_, p_220033_1_, p_220033_2_) -> {
         return p_220033_2_ != null && p_220033_2_.isHandActive() && p_220033_2_.getActiveItemStack() == p_220033_0_ && !isCharged(p_220033_0_) ? 1.0F : 0.0F;
      });
      this.addPropertyOverride(new ResourceLocation("charged"), (p_220030_0_, p_220030_1_, p_220030_2_) -> {
         return p_220030_2_ != null && isCharged(p_220030_0_) ? 1.0F : 0.0F;
      });
      this.addPropertyOverride(new ResourceLocation("firework"), (p_220020_0_, p_220020_1_, p_220020_2_) -> {
         return p_220020_2_ != null && isCharged(p_220020_0_) && hasChargedProjectile(p_220020_0_, Items.FIREWORK_ROCKET) ? 1.0F : 0.0F;
      });
   }

   public Predicate<ItemStack> getAmmoPredicate() {
      return ARROWS_OR_FIREWORKS;
   }

   /**
    * Get the predicate to match ammunition when searching the player's inventory, not their main/offhand
    */
   public Predicate<ItemStack> getInventoryAmmoPredicate() {
      return ARROWS;
   }

   /**
    * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
    * {@link #onItemUse}.
    */
   public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
      ItemStack itemstack = playerIn.getHeldItem(handIn);
      if (isCharged(itemstack)) {
         fireProjectiles(worldIn, playerIn, handIn, itemstack, func_220013_l(itemstack), 1.0F);
         setCharged(itemstack, false);
         return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
      } else if (!playerIn.func_213356_f(itemstack).isEmpty()) {
         if (!isCharged(itemstack)) {
            this.field_220034_c = false;
            this.field_220035_d = false;
            playerIn.setActiveHand(handIn);
         }

         return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
      } else {
         return new ActionResult<>(ActionResultType.FAIL, itemstack);
      }
   }

   /**
    * Called when the player stops using an Item (stops holding the right mouse button).
    */
   public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
      int i = this.getUseDuration(stack) - timeLeft;
      float f = func_220031_a(i, stack);
      if (f >= 1.0F && !isCharged(stack) && func_220021_a(entityLiving, stack)) {
         setCharged(stack, true);
         SoundCategory soundcategory = entityLiving instanceof PlayerEntity ? SoundCategory.PLAYERS : SoundCategory.HOSTILE;
         worldIn.playSound((PlayerEntity)null, entityLiving.posX, entityLiving.posY, entityLiving.posZ, SoundEvents.ITEM_CROSSBOW_LOADING_END, soundcategory, 1.0F, 1.0F / (random.nextFloat() * 0.5F + 1.0F) + 0.2F);
      }

   }

   private static boolean func_220021_a(LivingEntity p_220021_0_, ItemStack p_220021_1_) {
      int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.MULTISHOT, p_220021_1_);
      int j = i == 0 ? 1 : 3;
      boolean flag = p_220021_0_ instanceof PlayerEntity && ((PlayerEntity)p_220021_0_).abilities.isCreativeMode;
      ItemStack itemstack = p_220021_0_.func_213356_f(p_220021_1_);
      ItemStack itemstack1 = itemstack.copy();

      for(int k = 0; k < j; ++k) {
         if (k > 0) {
            itemstack = itemstack1.copy();
         }

         if (itemstack.isEmpty() && flag) {
            itemstack = new ItemStack(Items.ARROW);
            itemstack1 = itemstack.copy();
         }

         if (!func_220023_a(p_220021_0_, p_220021_1_, itemstack, k > 0, flag)) {
            return false;
         }
      }

      return true;
   }

   private static boolean func_220023_a(LivingEntity p_220023_0_, ItemStack p_220023_1_, ItemStack p_220023_2_, boolean p_220023_3_, boolean p_220023_4_) {
      if (p_220023_2_.isEmpty()) {
         return false;
      } else {
         boolean flag = p_220023_4_ && p_220023_2_.getItem() instanceof ArrowItem;
         ItemStack itemstack;
         if (!flag && !p_220023_4_ && !p_220023_3_) {
            itemstack = p_220023_2_.split(1);
            if (p_220023_2_.isEmpty() && p_220023_0_ instanceof PlayerEntity) {
               ((PlayerEntity)p_220023_0_).inventory.deleteStack(p_220023_2_);
            }
         } else {
            itemstack = p_220023_2_.copy();
         }

         addChargedProjectile(p_220023_1_, itemstack);
         return true;
      }
   }

   public static boolean isCharged(ItemStack p_220012_0_) {
      CompoundNBT compoundnbt = p_220012_0_.getTag();
      return compoundnbt != null && compoundnbt.getBoolean("Charged");
   }

   public static void setCharged(ItemStack p_220011_0_, boolean p_220011_1_) {
      CompoundNBT compoundnbt = p_220011_0_.getOrCreateTag();
      compoundnbt.putBoolean("Charged", p_220011_1_);
   }

   private static void addChargedProjectile(ItemStack crossbow, ItemStack projectile) {
      CompoundNBT compoundnbt = crossbow.getOrCreateTag();
      ListNBT listnbt;
      if (compoundnbt.contains("ChargedProjectiles", 9)) {
         listnbt = compoundnbt.getList("ChargedProjectiles", 10);
      } else {
         listnbt = new ListNBT();
      }

      CompoundNBT compoundnbt1 = new CompoundNBT();
      projectile.write(compoundnbt1);
      listnbt.add(compoundnbt1);
      compoundnbt.put("ChargedProjectiles", listnbt);
   }

   private static List<ItemStack> getChargedProjectiles(ItemStack p_220018_0_) {
      List<ItemStack> list = Lists.newArrayList();
      CompoundNBT compoundnbt = p_220018_0_.getTag();
      if (compoundnbt != null && compoundnbt.contains("ChargedProjectiles", 9)) {
         ListNBT listnbt = compoundnbt.getList("ChargedProjectiles", 10);
         if (listnbt != null) {
            for(int i = 0; i < listnbt.size(); ++i) {
               CompoundNBT compoundnbt1 = listnbt.getCompound(i);
               list.add(ItemStack.read(compoundnbt1));
            }
         }
      }

      return list;
   }

   private static void clearProjectiles(ItemStack p_220027_0_) {
      CompoundNBT compoundnbt = p_220027_0_.getTag();
      if (compoundnbt != null) {
         ListNBT listnbt = compoundnbt.getList("ChargedProjectiles", 9);
         listnbt.clear();
         compoundnbt.put("ChargedProjectiles", listnbt);
      }

   }

   private static boolean hasChargedProjectile(ItemStack p_220019_0_, Item p_220019_1_) {
      return getChargedProjectiles(p_220019_0_).stream().anyMatch((p_220010_1_) -> {
         return p_220010_1_.getItem() == p_220019_1_;
      });
   }

   private static void func_220016_a(World p_220016_0_, LivingEntity p_220016_1_, Hand p_220016_2_, ItemStack p_220016_3_, ItemStack p_220016_4_, float p_220016_5_, boolean p_220016_6_, float p_220016_7_, float p_220016_8_, float p_220016_9_) {
      if (!p_220016_0_.isRemote) {
         boolean flag = p_220016_4_.getItem() == Items.FIREWORK_ROCKET;
         IProjectile iprojectile;
         if (flag) {
            iprojectile = new FireworkRocketEntity(p_220016_0_, p_220016_4_, p_220016_1_.posX, p_220016_1_.posY + (double)p_220016_1_.getEyeHeight() - (double)0.15F, p_220016_1_.posZ, true);
         } else {
            iprojectile = func_220024_a(p_220016_0_, p_220016_1_, p_220016_3_, p_220016_4_);
            if (p_220016_6_ || p_220016_9_ != 0.0F) {
               ((AbstractArrowEntity)iprojectile).pickupStatus = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
            }
         }

         if (p_220016_1_ instanceof ICrossbowUser) {
            ICrossbowUser icrossbowuser = (ICrossbowUser)p_220016_1_;
            icrossbowuser.shoot(icrossbowuser.getAttackTarget(), p_220016_3_, iprojectile, p_220016_9_);
         } else {
            Vec3d vec3d1 = p_220016_1_.func_213286_i(1.0F);
            Quaternion quaternion = new Quaternion(new Vector3f(vec3d1), p_220016_9_, true);
            Vec3d vec3d = p_220016_1_.getLook(1.0F);
            Vector3f vector3f = new Vector3f(vec3d);
            vector3f.func_214905_a(quaternion);
            iprojectile.shoot((double)vector3f.getX(), (double)vector3f.getY(), (double)vector3f.getZ(), p_220016_7_, p_220016_8_);
         }

         p_220016_3_.damageItem(flag ? 3 : 1, p_220016_1_, (p_220017_1_) -> {
            p_220017_1_.sendBreakAnimation(p_220016_2_);
         });
         p_220016_0_.addEntity((Entity)iprojectile);
         p_220016_0_.playSound((PlayerEntity)null, p_220016_1_.posX, p_220016_1_.posY, p_220016_1_.posZ, SoundEvents.ITEM_CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1.0F, p_220016_5_);
      }
   }

   private static AbstractArrowEntity func_220024_a(World p_220024_0_, LivingEntity p_220024_1_, ItemStack p_220024_2_, ItemStack p_220024_3_) {
      ArrowItem arrowitem = (ArrowItem)(p_220024_3_.getItem() instanceof ArrowItem ? p_220024_3_.getItem() : Items.ARROW);
      AbstractArrowEntity abstractarrowentity = arrowitem.createArrow(p_220024_0_, p_220024_3_, p_220024_1_);
      if (p_220024_1_ instanceof PlayerEntity) {
         abstractarrowentity.setIsCritical(true);
      }

      abstractarrowentity.func_213869_a(SoundEvents.ITEM_CROSSBOW_HIT);
      abstractarrowentity.func_213865_o(true);
      int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.PIERCING, p_220024_2_);
      if (i > 0) {
         abstractarrowentity.func_213872_b((byte)i);
      }

      return abstractarrowentity;
   }

   public static void fireProjectiles(World p_220014_0_, LivingEntity p_220014_1_, Hand p_220014_2_, ItemStack p_220014_3_, float p_220014_4_, float p_220014_5_) {
      List<ItemStack> list = getChargedProjectiles(p_220014_3_);
      float[] afloat = func_220028_a(p_220014_1_.getRNG());

      for(int i = 0; i < list.size(); ++i) {
         ItemStack itemstack = list.get(i);
         boolean flag = p_220014_1_ instanceof PlayerEntity && ((PlayerEntity)p_220014_1_).abilities.isCreativeMode;
         if (!itemstack.isEmpty()) {
            if (i == 0) {
               func_220016_a(p_220014_0_, p_220014_1_, p_220014_2_, p_220014_3_, itemstack, afloat[i], flag, p_220014_4_, p_220014_5_, 0.0F);
            } else if (i == 1) {
               func_220016_a(p_220014_0_, p_220014_1_, p_220014_2_, p_220014_3_, itemstack, afloat[i], flag, p_220014_4_, p_220014_5_, -10.0F);
            } else if (i == 2) {
               func_220016_a(p_220014_0_, p_220014_1_, p_220014_2_, p_220014_3_, itemstack, afloat[i], flag, p_220014_4_, p_220014_5_, 10.0F);
            }
         }
      }

      func_220015_a(p_220014_0_, p_220014_1_, p_220014_3_);
   }

   private static float[] func_220028_a(Random p_220028_0_) {
      boolean flag = p_220028_0_.nextBoolean();
      return new float[]{1.0F, func_220032_a(flag), func_220032_a(!flag)};
   }

   private static float func_220032_a(boolean p_220032_0_) {
      float f = p_220032_0_ ? 0.63F : 0.43F;
      return 1.0F / (random.nextFloat() * 0.5F + 1.8F) + f;
   }

   private static void func_220015_a(World p_220015_0_, LivingEntity p_220015_1_, ItemStack p_220015_2_) {
      if (p_220015_1_ instanceof ServerPlayerEntity) {
         ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)p_220015_1_;
         if (!p_220015_0_.isRemote) {
            CriteriaTriggers.SHOT_CROSSBOW.func_215111_a(serverplayerentity, p_220015_2_);
         }

         serverplayerentity.addStat(Stats.ITEM_USED.get(p_220015_2_.getItem()));
      }

      clearProjectiles(p_220015_2_);
   }

   public void func_219972_a(World worldIn, LivingEntity livingEntityIn, ItemStack stack, int p_219972_4_) {
      if (!worldIn.isRemote) {
         int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.QUICK_CHARGE, stack);
         SoundEvent soundevent = this.func_220025_a(i);
         SoundEvent soundevent1 = i == 0 ? SoundEvents.ITEM_CROSSBOW_LOADING_MIDDLE : null;
         float f = (float)(stack.getUseDuration() - p_219972_4_) / (float)func_220026_e(stack);
         if (f < 0.2F) {
            this.field_220034_c = false;
            this.field_220035_d = false;
         }

         if (f >= 0.2F && !this.field_220034_c) {
            this.field_220034_c = true;
            worldIn.playSound((PlayerEntity)null, livingEntityIn.posX, livingEntityIn.posY, livingEntityIn.posZ, soundevent, SoundCategory.PLAYERS, 0.5F, 1.0F);
         }

         if (f >= 0.5F && soundevent1 != null && !this.field_220035_d) {
            this.field_220035_d = true;
            worldIn.playSound((PlayerEntity)null, livingEntityIn.posX, livingEntityIn.posY, livingEntityIn.posZ, soundevent1, SoundCategory.PLAYERS, 0.5F, 1.0F);
         }
      }

   }

   /**
    * How long it takes to use or consume an item
    */
   public int getUseDuration(ItemStack stack) {
      return func_220026_e(stack) + 3;
   }

   public static int func_220026_e(ItemStack p_220026_0_) {
      int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.QUICK_CHARGE, p_220026_0_);
      return i == 0 ? 25 : 25 - 5 * i;
   }

   /**
    * returns the action that specifies what animation to play when the items is being used
    */
   public UseAction getUseAction(ItemStack stack) {
      return UseAction.CROSSBOW;
   }

   private SoundEvent func_220025_a(int p_220025_1_) {
      switch(p_220025_1_) {
      case 1:
         return SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_1;
      case 2:
         return SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_2;
      case 3:
         return SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_3;
      default:
         return SoundEvents.ITEM_CROSSBOW_LOADING_START;
      }
   }

   private static float func_220031_a(int p_220031_0_, ItemStack p_220031_1_) {
      float f = (float)p_220031_0_ / (float)func_220026_e(p_220031_1_);
      if (f > 1.0F) {
         f = 1.0F;
      }

      return f;
   }

   /**
    * allows items to add custom lines of information to the mouseover description
    */
   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      List<ItemStack> list = getChargedProjectiles(stack);
      if (isCharged(stack) && !list.isEmpty()) {
         ItemStack itemstack = list.get(0);
         tooltip.add((new TranslationTextComponent("item.minecraft.crossbow.projectile")).appendText(" ").appendSibling(itemstack.getTextComponent()));
         if (flagIn.isAdvanced() && itemstack.getItem() == Items.FIREWORK_ROCKET) {
            List<ITextComponent> list1 = Lists.newArrayList();
            Items.FIREWORK_ROCKET.addInformation(itemstack, worldIn, list1, flagIn);
            if (!list1.isEmpty()) {
               for(int i = 0; i < list1.size(); ++i) {
                  list1.set(i, (new StringTextComponent("  ")).appendSibling(list1.get(i)).applyTextStyle(TextFormatting.GRAY));
               }

               tooltip.addAll(list1);
            }
         }

      }
   }

   private static float func_220013_l(ItemStack p_220013_0_) {
      return p_220013_0_.getItem() == Items.CROSSBOW && hasChargedProjectile(p_220013_0_, Items.FIREWORK_ROCKET) ? 1.6F : 3.15F;
   }
}