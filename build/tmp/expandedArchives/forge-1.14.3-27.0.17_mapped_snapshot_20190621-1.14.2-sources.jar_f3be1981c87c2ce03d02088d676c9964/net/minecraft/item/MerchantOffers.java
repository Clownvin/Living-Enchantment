package net.minecraft.item;

import java.util.ArrayList;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;

public class MerchantOffers extends ArrayList<MerchantOffer> {
   public MerchantOffers() {
   }

   public MerchantOffers(CompoundNBT p_i50011_1_) {
      ListNBT listnbt = p_i50011_1_.getList("Recipes", 10);

      for(int i = 0; i < listnbt.size(); ++i) {
         this.add(new MerchantOffer(listnbt.getCompound(i)));
      }

   }

   @Nullable
   public MerchantOffer func_222197_a(ItemStack p_222197_1_, ItemStack p_222197_2_, int p_222197_3_) {
      if (p_222197_3_ > 0 && p_222197_3_ < this.size()) {
         MerchantOffer merchantoffer1 = this.get(p_222197_3_);
         return merchantoffer1.func_222204_a(p_222197_1_, p_222197_2_) ? merchantoffer1 : null;
      } else {
         for(int i = 0; i < this.size(); ++i) {
            MerchantOffer merchantoffer = this.get(i);
            if (merchantoffer.func_222204_a(p_222197_1_, p_222197_2_)) {
               return merchantoffer;
            }
         }

         return null;
      }
   }

   public void func_222196_a(PacketBuffer p_222196_1_) {
      p_222196_1_.writeByte((byte)(this.size() & 255));

      for(int i = 0; i < this.size(); ++i) {
         MerchantOffer merchantoffer = this.get(i);
         p_222196_1_.writeItemStack(merchantoffer.func_222218_a());
         p_222196_1_.writeItemStack(merchantoffer.func_222200_d());
         ItemStack itemstack = merchantoffer.func_222202_c();
         p_222196_1_.writeBoolean(!itemstack.isEmpty());
         if (!itemstack.isEmpty()) {
            p_222196_1_.writeItemStack(itemstack);
         }

         p_222196_1_.writeBoolean(merchantoffer.func_222217_o());
         p_222196_1_.writeInt(merchantoffer.func_222213_g());
         p_222196_1_.writeInt(merchantoffer.func_222214_i());
         p_222196_1_.writeInt(merchantoffer.func_222210_n());
         p_222196_1_.writeInt(merchantoffer.func_222212_l());
         p_222196_1_.writeFloat(merchantoffer.func_222211_m());
      }

   }

   public static MerchantOffers func_222198_b(PacketBuffer p_222198_0_) {
      MerchantOffers merchantoffers = new MerchantOffers();
      int i = p_222198_0_.readByte() & 255;

      for(int j = 0; j < i; ++j) {
         ItemStack itemstack = p_222198_0_.readItemStack();
         ItemStack itemstack1 = p_222198_0_.readItemStack();
         ItemStack itemstack2 = ItemStack.EMPTY;
         if (p_222198_0_.readBoolean()) {
            itemstack2 = p_222198_0_.readItemStack();
         }

         boolean flag = p_222198_0_.readBoolean();
         int k = p_222198_0_.readInt();
         int l = p_222198_0_.readInt();
         int i1 = p_222198_0_.readInt();
         int j1 = p_222198_0_.readInt();
         float f = p_222198_0_.readFloat();
         MerchantOffer merchantoffer = new MerchantOffer(itemstack, itemstack2, itemstack1, k, l, i1, f);
         if (flag) {
            merchantoffer.func_222216_p();
         }

         merchantoffer.func_222209_b(j1);
         merchantoffers.add(merchantoffer);
      }

      return merchantoffers;
   }

   public CompoundNBT func_222199_a() {
      CompoundNBT compoundnbt = new CompoundNBT();
      ListNBT listnbt = new ListNBT();

      for(int i = 0; i < this.size(); ++i) {
         MerchantOffer merchantoffer = this.get(i);
         listnbt.add(merchantoffer.func_222208_r());
      }

      compoundnbt.put("Recipes", listnbt);
      return compoundnbt;
   }
}