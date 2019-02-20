package com.clownvin.livingenchantment.enchantment;

import com.clownvin.livingenchantment.LivingEnchantment;
import com.clownvin.livingenchantment.config.Config;
import net.minecraft.enchantment.*;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class EnchantmentLiving extends Enchantment {

    public static Enchantment LIVING_ENCHANTMENT;

    public EnchantmentLiving(Rarity rarityIn, EnumEnchantmentType typeIn, EntityEquipmentSlot[] slots) {
        super(rarityIn, typeIn, slots);
        this.setRegistryName(LivingEnchantment.MODID, "living");
    }

    @Override
    public boolean canApply(ItemStack stack) {
        return (Config.COMMON.allowArmor.get() && stack.getItem() instanceof ItemArmor) || stack.getItem() instanceof ItemTool || stack.getItem() instanceof ItemHoe || stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemBow;
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
        if (Config.COMMON.allowForbiddenEnchantments.get())
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
