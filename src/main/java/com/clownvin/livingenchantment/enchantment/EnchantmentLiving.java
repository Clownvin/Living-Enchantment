package com.clownvin.livingenchantment.enchantment;

import com.clownvin.livingenchantment.LivingEnchantment;
import com.clownvin.livingenchantment.config.Config;
import net.minecraft.enchantment.*;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class EnchantmentLiving extends Enchantment {

    public static Enchantment LIVING_ENCHANTMENT;

    public EnchantmentLiving(Rarity rarityIn, EnchantmentType typeIn, EquipmentSlotType[] slots) {
        super(rarityIn, typeIn, slots);
        this.setRegistryName(LivingEnchantment.MODID, "living");
    }

    @Override
    public boolean canApply(ItemStack stack) {
        return (Config.COMMON.allowArmor.get() && stack.getItem() instanceof ArmorItem) || stack.getItem() instanceof ToolItem || stack.getItem() instanceof HoeItem || stack.getItem() instanceof SwordItem || stack.getItem() instanceof BowItem;
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
    public boolean canApplyTogether(Enchantment ench) {
        if (ench == null)
            return true;
        if (Config.COMMON.allowForbiddenEnchantments.get())
            return true;
        if (ench instanceof PowerEnchantment || ench instanceof DamageEnchantment || ench instanceof EfficiencyEnchantment || ench instanceof ProtectionEnchantment)
            return false;
        return true;
    }

    @SubscribeEvent
    public static void registerEnchantment(RegistryEvent.Register<Enchantment> event) {
        event.getRegistry().register(LIVING_ENCHANTMENT);
    }
}
