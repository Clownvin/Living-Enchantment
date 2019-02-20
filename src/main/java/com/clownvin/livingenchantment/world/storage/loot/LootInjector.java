package com.clownvin.livingenchantment.world.storage.loot;

import com.clownvin.livingenchantment.LivingEnchantment;
import com.clownvin.livingenchantment.config.LivingConfig;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryTable;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Resource;

@Mod.EventBusSubscriber
public class LootInjector {

    private static LootEntry books;
    private static LootEntry armor;
    private static LootEntry uniques;

    public static void init() {
        books = new LootEntryTable(new ResourceLocation(LivingEnchantment.MODID + ":inject/living_book_loot"), 1, 3, new LootCondition[]{}, "living_book_loot");
        armor = new LootEntryTable(new ResourceLocation(LivingEnchantment.MODID + ":inject/unique_armor_loot"), 1, 3, new LootCondition[]{}, "unique_armor_loot");
        uniques = new LootEntryTable(new ResourceLocation(LivingEnchantment.MODID + ":inject/unique_weap_tool_loot"), 1, 3, new LootCondition[]{}, "unique_weap_tool_loot");
    }

    private static LootEntry[] createLoot(int type, int weight) {
        LootEntry empty;
        LootEntry[] loot;
        ResourceLocation emptyLocation = new ResourceLocation(LivingEnchantment.MODID + ":inject/empty");
        if (type == LivingEnchantment.JUST_BOOKS) {
            empty = new LootEntryTable(emptyLocation, weight - 1, 0, new LootCondition[]{}, "empty");
            loot = new LootEntry[]{books, empty};
        } else if (LivingConfig.general.allowArmor) { //Armor
            if (type == LivingEnchantment.JUST_UNIQUES) {
                empty = new LootEntryTable(emptyLocation, (weight - 1) * 2, 0, new LootCondition[]{}, "empty");
                loot = new LootEntry[]{armor, uniques, empty};
            } else {
                empty = new LootEntryTable(emptyLocation, (weight - 1) * 3, 0, new LootCondition[]{}, "empty");
                loot = new LootEntry[]{armor, books, uniques, empty};
            }
        } else { //No armor
            if (type == LivingEnchantment.JUST_UNIQUES) {
                empty = new LootEntryTable(emptyLocation, weight - 1, 0, new LootCondition[]{}, "empty");
                loot = new LootEntry[]{uniques, empty};
            } else {
                empty = new LootEntryTable(emptyLocation, (weight - 1) * 2, 0, new LootCondition[]{}, "empty");
                loot = new LootEntry[]{books, uniques, empty};
            }
        }
        return loot;
    }

    @SubscribeEvent
    public static void loadLootEvent(LootTableLoadEvent event) {
        if (LivingConfig.loot.fishingLoot && event.getName().toString().equals("minecraft:gameplay/fishing")) {
            LootPool pool = new LootPool(createLoot(LivingConfig.loot.fishingLootType, LivingConfig.loot.fishingLootChance), new LootCondition[]{}, new RandomValueRange(1, 1), new RandomValueRange(0, 0), "living_fishing_loot");
            event.getTable().addPool(pool);
        } else if (LivingConfig.loot.chestLoot && event.getName().toString().contains("chests/")) {
            LootPool pool = new LootPool(createLoot(LivingConfig.loot.chestLootType, LivingConfig.loot.chestLootChance), new LootCondition[]{}, new RandomValueRange(1, 2), new RandomValueRange(0, 6), "living_chest_loot");
            event.getTable().addPool(pool);
        }
    }
}
