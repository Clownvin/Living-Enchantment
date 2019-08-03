package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.FireworkRocketEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FireworkRocketItem extends Item {
   public FireworkRocketItem(Item.Properties builder) {
      super(builder);
   }

   /**
    * Called when this item is used when targetting a Block
    */
   public ActionResultType onItemUse(ItemUseContext context) {
      World world = context.getWorld();
      if (!world.isRemote) {
         ItemStack itemstack = context.getItem();
         Vec3d vec3d = context.getHitVec();
         FireworkRocketEntity fireworkrocketentity = new FireworkRocketEntity(world, vec3d.x, vec3d.y, vec3d.z, itemstack);
         world.addEntity(fireworkrocketentity);
         itemstack.shrink(1);
      }

      return ActionResultType.SUCCESS;
   }

   /**
    * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
    * {@link #onItemUse}.
    */
   public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
      if (playerIn.isElytraFlying()) {
         ItemStack itemstack = playerIn.getHeldItem(handIn);
         if (!worldIn.isRemote) {
            worldIn.addEntity(new FireworkRocketEntity(worldIn, itemstack, playerIn));
            if (!playerIn.abilities.isCreativeMode) {
               itemstack.shrink(1);
            }
         }

         return new ActionResult<>(ActionResultType.SUCCESS, playerIn.getHeldItem(handIn));
      } else {
         return new ActionResult<>(ActionResultType.PASS, playerIn.getHeldItem(handIn));
      }
   }

   /**
    * allows items to add custom lines of information to the mouseover description
    */
   @OnlyIn(Dist.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      CompoundNBT compoundnbt = stack.getChildTag("Fireworks");
      if (compoundnbt != null) {
         if (compoundnbt.contains("Flight", 99)) {
            tooltip.add((new TranslationTextComponent("item.minecraft.firework_rocket.flight")).appendText(" ").appendText(String.valueOf((int)compoundnbt.getByte("Flight"))).applyTextStyle(TextFormatting.GRAY));
         }

         ListNBT listnbt = compoundnbt.getList("Explosions", 10);
         if (!listnbt.isEmpty()) {
            for(int i = 0; i < listnbt.size(); ++i) {
               CompoundNBT compoundnbt1 = listnbt.getCompound(i);
               List<ITextComponent> list = Lists.newArrayList();
               FireworkStarItem.func_195967_a(compoundnbt1, list);
               if (!list.isEmpty()) {
                  for(int j = 1; j < list.size(); ++j) {
                     list.set(j, (new StringTextComponent("  ")).appendSibling(list.get(j)).applyTextStyle(TextFormatting.GRAY));
                  }

                  tooltip.addAll(list);
               }
            }
         }

      }
   }

   public static enum Shape {
      SMALL_BALL(0, "small_ball"),
      LARGE_BALL(1, "large_ball"),
      STAR(2, "star"),
      CREEPER(3, "creeper"),
      BURST(4, "burst");

      private static final FireworkRocketItem.Shape[] field_196077_f = Arrays.stream(values()).sorted(Comparator.comparingInt((p_199796_0_) -> {
         return p_199796_0_.field_196078_g;
      })).toArray((p_199797_0_) -> {
         return new FireworkRocketItem.Shape[p_199797_0_];
      });
      private final int field_196078_g;
      private final String field_196079_h;

      private Shape(int p_i47931_3_, String p_i47931_4_) {
         this.field_196078_g = p_i47931_3_;
         this.field_196079_h = p_i47931_4_;
      }

      public int func_196071_a() {
         return this.field_196078_g;
      }

      @OnlyIn(Dist.CLIENT)
      public String func_196068_b() {
         return this.field_196079_h;
      }

      @OnlyIn(Dist.CLIENT)
      public static FireworkRocketItem.Shape func_196070_a(int p_196070_0_) {
         return p_196070_0_ >= 0 && p_196070_0_ < field_196077_f.length ? field_196077_f[p_196070_0_] : SMALL_BALL;
      }
   }
}