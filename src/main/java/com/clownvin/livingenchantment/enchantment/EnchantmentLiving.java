package com.clownvin.livingenchantment.enchantment;

import com.clownvin.livingenchantment.LivingEnchantment;
import com.clownvin.livingenchantment.config.LivingConfig;
import net.minecraft.enchantment.*;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class EnchantmentLiving extends Enchantment {

    public static Enchantment LIVING_ENCHANTMENT;

    public EnchantmentLiving(Rarity rarityIn, EnumEnchantmentType typeIn, EntityEquipmentSlot[] slots) {
        super(rarityIn, typeIn, slots);
        this.setName("living");
        this.setRegistryName(LivingEnchantment.MODID, getName());
    }

    @Override
    public boolean canApply(ItemStack stack) {
        return (LivingConfig.general.allowArmor && stack.getItem() instanceof ItemArmor) || stack.getItem() instanceof ItemTool || stack.getItem() instanceof ItemHoe || stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemBow;
    }

    @Override
    public boolean isTreasureEnchantment() {
        return true;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel) {
        return 40;
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        return 50;
    }

    @Override
    public boolean canApplyTogether(Enchantment ench) {
        if (ench == null)
            return true;
        if (LivingConfig.general.allowDamageEnchantments)
            return true;
        if (ench instanceof EnchantmentArrowDamage || ench instanceof EnchantmentDamage || ench instanceof EnchantmentDigging || ench instanceof EnchantmentProtection)
            return false;
        return true;
    }

    @SubscribeEvent
    public static void registerEnchantment(RegistryEvent.Register<Enchantment> event) {
        event.getRegistry().register(LIVING_ENCHANTMENT);
    }
}
