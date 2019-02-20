package com.clownvin.livingenchantment.world.storage.loot;

import com.clownvin.livingenchantment.LivingEnchantment;
import com.clownvin.livingenchantment.config.Config;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LootInjector {

    private static final Logger LOGGER = LogManager.getLogger(LootInjector.class);

    private static LootEntry books;
    private static LootEntry armor;
    private static LootEntry uniques;

    public static void init() {
        books = new LootEntryTable(new ResourceLocation(LivingEnchantment.MODID + ":inject/living_book_loot"), 1, 3, new LootCondition[]{}, "living_book_loot");
        armor = new LootEntryTable(new ResourceLocation(LivingEnchantment.MODID + ":inject/unique_armor_loot"), 1, 3, new LootCondition[]{}, "unique_armor_loot");
        uniques = new LootEntryTable(new ResourceLocation(LivingEnchantment.MODID + ":inject/unique_weap_tool_loot"), 1, 3, new LootCondition[]{}, "unique_weap_tool_loot");
    }

    @SubscribeEvent
    public static void lootload(LootTableLoadEvent event) {
        LOGGER.debug("Recieved LootTableLoadEvent: "+event.getName());
        if (Config.COMMON.fishingLoot.get() && event.getName().toString().equals("minecraft:loot_tables/gameplay/fishing.json")) {
            injectFishingLoot(event.getTable());
            LOGGER.debug("Automatically injected fishing loot into: " + event.getName());
        } else if (Config.COMMON.chestLoot.get() && event.getName().toString().contains("chests/")) {
            injectChestLoot(event.getTable());
            LOGGER.debug("Automatically injected chest loot into: "+event.getName());
        }
    }

    public static void injectChestLoot(LootTable table) {
        //System.out.println("Injected loot into: "+table.)
        LootPool pool = new LootPool(createLoot(Config.COMMON.chestLootType.get(), Config.COMMON.chestLootChance.get()), new LootCondition[]{}, new RandomValueRange(1, 2), new RandomValueRange(0, 6), "living_chest_loot");
        table.addPool(pool);
    }

    public static void injectFishingLoot(LootTable table) {
        LootPool pool = new LootPool(createLoot(Config.COMMON.fishingLootType.get(), Config.COMMON.fishingLootChance.get()), new LootCondition[]{}, new RandomValueRange(1, 1), new RandomValueRange(0, 0), "living_fishing_loot");
        table.addPool(pool);
    }

    private static LootEntry[] createLoot(int type, int weight) {
        LootEntry empty;
        LootEntry[] loot;
        ResourceLocation emptyLocation = new ResourceLocation(LivingEnchantment.MODID + ":inject/empty");
        if (type == LivingEnchantment.JUST_BOOKS) {
            empty = new LootEntryTable(emptyLocation, weight - 1, 0, new LootCondition[]{}, "empty");
            loot = new LootEntry[]{books, empty};
        } else if (Config.COMMON.allowArmor.get()) { //Armor
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
}
