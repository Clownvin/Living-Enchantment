package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;

public class RespirationEnchantment extends Enchantment {
   public RespirationEnchantment(Enchantment.Rarity rarityIn, EquipmentSlotType... slots) {
      super(rarityIn, EnchantmentType.ARMOR_HEAD, slots);
   }

   /**
    * Returns the minimal value of enchantability needed on the enchantment level passed.
    */
   public int getMinEnchantability(int enchantmentLevel) {
      return 10 * enchantmentLevel;
   }

   public int func_223551_b(int p_223551_1_) {
      return this.getMinEnchantability(p_223551_1_) + 30;
   }

   /**
    * Returns the maximum level that the enchantment can have.
    */
   public int getMaxLevel() {
      return 3;
   }
}