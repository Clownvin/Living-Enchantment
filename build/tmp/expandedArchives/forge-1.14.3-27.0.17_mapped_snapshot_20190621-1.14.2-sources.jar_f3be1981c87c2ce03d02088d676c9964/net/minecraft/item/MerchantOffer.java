package net.minecraft.item;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.MathHelper;

public class MerchantOffer {
   /** The first input for this offer. */
   private final ItemStack buyingStackFirst;
   /** The second input for this offer. */
   private final ItemStack buyingStackSecond;
   /** The output of this offer. */
   private final ItemStack sellingStack;
   private int uses;
   private final int maxUses;
   private boolean doesRewardEXP = true;
   private int specialPrice;
   private int demand;
   private float field_222231_i;
   private int givenEXP = 1;

   public MerchantOffer(CompoundNBT dataTag) {
      this.buyingStackFirst = ItemStack.read(dataTag.getCompound("buy"));
      this.buyingStackSecond = ItemStack.read(dataTag.getCompound("buyB"));
      this.sellingStack = ItemStack.read(dataTag.getCompound("sell"));
      this.uses = dataTag.getInt("uses");
      if (dataTag.contains("maxUses", 99)) {
         this.maxUses = dataTag.getInt("maxUses");
      } else {
         this.maxUses = 4;
      }

      if (dataTag.contains("rewardExp", 1)) {
         this.doesRewardEXP = dataTag.getBoolean("rewardExp");
      }

      if (dataTag.contains("xp", 3)) {
         this.givenEXP = dataTag.getInt("xp");
      }

      if (dataTag.contains("priceMultiplier", 5)) {
         this.field_222231_i = dataTag.getFloat("priceMultiplier");
      }

      this.specialPrice = dataTag.getInt("specialPrice");
      this.demand = dataTag.getInt("demand");
   }

   public MerchantOffer(ItemStack p_i50013_1_, ItemStack p_i50013_2_, int p_i50013_3_, int p_i50013_4_, float p_i50013_5_) {
      this(p_i50013_1_, ItemStack.EMPTY, p_i50013_2_, p_i50013_3_, p_i50013_4_, p_i50013_5_);
   }

   public MerchantOffer(ItemStack p_i50014_1_, ItemStack p_i50014_2_, ItemStack p_i50014_3_, int p_i50014_4_, int p_i50014_5_, float p_i50014_6_) {
      this(p_i50014_1_, p_i50014_2_, p_i50014_3_, 0, p_i50014_4_, p_i50014_5_, p_i50014_6_);
   }

   public MerchantOffer(ItemStack p_i50015_1_, ItemStack p_i50015_2_, ItemStack p_i50015_3_, int p_i50015_4_, int p_i50015_5_, int p_i50015_6_, float p_i50015_7_) {
      this.buyingStackFirst = p_i50015_1_;
      this.buyingStackSecond = p_i50015_2_;
      this.sellingStack = p_i50015_3_;
      this.uses = p_i50015_4_;
      this.maxUses = p_i50015_5_;
      this.givenEXP = p_i50015_6_;
      this.field_222231_i = p_i50015_7_;
   }

   public ItemStack func_222218_a() {
      return this.buyingStackFirst;
   }

   public ItemStack func_222205_b() {
      int i = this.buyingStackFirst.getCount();
      ItemStack itemstack = this.buyingStackFirst.copy();
      int j = Math.max(0, MathHelper.floor((float)(i * this.demand) * this.field_222231_i));
      itemstack.setCount(MathHelper.clamp(i + j + this.specialPrice, 1, this.buyingStackFirst.getItem().getMaxStackSize()));
      return itemstack;
   }

   public ItemStack func_222202_c() {
      return this.buyingStackSecond;
   }

   public ItemStack func_222200_d() {
      return this.sellingStack;
   }

   public void func_222222_e() {
      this.demand = this.demand + this.uses - (this.maxUses - this.uses);
   }

   public ItemStack func_222206_f() {
      return this.sellingStack.copy();
   }

   public int func_222213_g() {
      return this.uses;
   }

   public void func_222203_h() {
      this.uses = 0;
   }

   public int func_222214_i() {
      return this.maxUses;
   }

   public void func_222219_j() {
      ++this.uses;
   }

   public void func_222207_a(int p_222207_1_) {
      this.specialPrice += p_222207_1_;
   }

   public void func_222220_k() {
      this.specialPrice = 0;
   }

   public int func_222212_l() {
      return this.specialPrice;
   }

   public void func_222209_b(int p_222209_1_) {
      this.specialPrice = p_222209_1_;
   }

   public float func_222211_m() {
      return this.field_222231_i;
   }

   public int func_222210_n() {
      return this.givenEXP;
   }

   public boolean func_222217_o() {
      return this.uses >= this.maxUses;
   }

   public void func_222216_p() {
      this.uses = this.maxUses;
   }

   public boolean func_222221_q() {
      return this.doesRewardEXP;
   }

   public CompoundNBT func_222208_r() {
      CompoundNBT compoundnbt = new CompoundNBT();
      compoundnbt.put("buy", this.buyingStackFirst.write(new CompoundNBT()));
      compoundnbt.put("sell", this.sellingStack.write(new CompoundNBT()));
      compoundnbt.put("buyB", this.buyingStackSecond.write(new CompoundNBT()));
      compoundnbt.putInt("uses", this.uses);
      compoundnbt.putInt("maxUses", this.maxUses);
      compoundnbt.putBoolean("rewardExp", this.doesRewardEXP);
      compoundnbt.putInt("xp", this.givenEXP);
      compoundnbt.putFloat("priceMultiplier", this.field_222231_i);
      compoundnbt.putInt("specialPrice", this.specialPrice);
      compoundnbt.putInt("demand", this.demand);
      return compoundnbt;
   }

   public boolean func_222204_a(ItemStack p_222204_1_, ItemStack p_222204_2_) {
      return this.func_222201_c(p_222204_1_, this.func_222205_b()) && p_222204_1_.getCount() >= this.func_222205_b().getCount() && this.func_222201_c(p_222204_2_, this.buyingStackSecond) && p_222204_2_.getCount() >= this.buyingStackSecond.getCount();
   }

   private boolean func_222201_c(ItemStack p_222201_1_, ItemStack p_222201_2_) {
      if (p_222201_2_.isEmpty() && p_222201_1_.isEmpty()) {
         return true;
      } else {
         ItemStack itemstack = p_222201_1_.copy();
         if (itemstack.getItem().isDamageable()) {
            itemstack.setDamage(itemstack.getDamage());
         }

         return ItemStack.areItemsEqual(itemstack, p_222201_2_) && (!p_222201_2_.hasTag() || itemstack.hasTag() && NBTUtil.areNBTEquals(p_222201_2_.getTag(), itemstack.getTag(), false));
      }
   }

   public boolean func_222215_b(ItemStack p_222215_1_, ItemStack p_222215_2_) {
      if (!this.func_222204_a(p_222215_1_, p_222215_2_)) {
         return false;
      } else {
         p_222215_1_.shrink(this.func_222205_b().getCount());
         if (!this.func_222202_c().isEmpty()) {
            p_222215_2_.shrink(this.func_222202_c().getCount());
         }

         return true;
      }
   }
}