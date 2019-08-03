package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;

public class SweepingEnchantment extends Enchantment {
   public SweepingEnchantment(Enchantment.Rarity rarityIn, EquipmentSlotType... slots) {
      super(rarityIn, EnchantmentType.WEAPON, slots);
   }

   /**
    * Returns the minimal value of enchantability needed on the enchantment level passed.
    */
   public int getMinEnchantability(int enchantmentLevel) {
      return 5 + (enchantmentLevel - 1) * 9;
   }

   public int func_223551_b(int p_223551_1_) {
      return this.getMinEnchantability(p_223551_1_) + 15;
   }

   /**
    * Returns the maximum level that the enchantment can have.
    */
   public int getMaxLevel() {
      return 3;
   }

   public static float getSweepingDamageRatio(int p_191526_0_) {
      return 1.0F - 1.0F / (float)(p_191526_0_ + 1);
   }
}