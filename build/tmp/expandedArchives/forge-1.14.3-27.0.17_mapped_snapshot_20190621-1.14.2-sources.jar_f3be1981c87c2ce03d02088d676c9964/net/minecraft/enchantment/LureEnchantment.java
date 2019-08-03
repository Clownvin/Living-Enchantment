package net.minecraft.enchantment;

import net.minecraft.inventory.EquipmentSlotType;

public class LureEnchantment extends Enchantment {
   protected LureEnchantment(Enchantment.Rarity rarityIn, EnchantmentType typeIn, EquipmentSlotType... slots) {
      super(rarityIn, typeIn, slots);
   }

   /**
    * Returns the minimal value of enchantability needed on the enchantment level passed.
    */
   public int getMinEnchantability(int enchantmentLevel) {
      return 15 + (enchantmentLevel - 1) * 9;
   }

   public int func_223551_b(int p_223551_1_) {
      return super.getMinEnchantability(p_223551_1_) + 50;
   }

   /**
    * Returns the maximum level that the enchantment can have.
    */
   public int getMaxLevel() {
      return 3;
   }
}