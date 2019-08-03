package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;

public class PunchEnchantment extends Enchantment {
   public PunchEnchantment(Enchantment.Rarity rarityIn, EquipmentSlotType... slots) {
      super(rarityIn, EnchantmentType.BOW, slots);
   }

   /**
    * Returns the minimal value of enchantability needed on the enchantment level passed.
    */
   public int getMinEnchantability(int enchantmentLevel) {
      return 12 + (enchantmentLevel - 1) * 20;
   }

   public int func_223551_b(int p_223551_1_) {
      return this.getMinEnchantability(p_223551_1_) + 25;
   }

   /**
    * Returns the maximum level that the enchantment can have.
    */
   public int getMaxLevel() {
      return 2;
   }
}