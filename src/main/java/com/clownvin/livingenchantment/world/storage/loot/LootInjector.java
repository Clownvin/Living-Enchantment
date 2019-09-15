package com.clownvin.livingenchantment.world.storage.loot;

import com.clownvin.livingenchantment.LivingEnchantment;
import com.clownvin.livingenchantment.config.Config;
import com.clownvin.livingenchantment.world.storage.loot.functions.EnchantLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraft.world.storage.loot.functions.ILootFunction;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LootInjector {

    private static final Logger LOGGER = LogManager.getLogger(LootInjector.class);

    private static LootPool fishingPool;
    private static LootPool chestPool;

    public static void init() {
        int fishingWeight = Config.COMMON.fishingLootChance.get();
        fishingPool = LootPool.builder().addEntry(ItemLootEntry.func_216168_a(Items.BOOK).weight(fishingWeight).acceptFunction(EnchantLiving.getBuilder())).addEntry(EmptyLootEntry.func_216167_a().weight(fishingWeight - 1)).rolls(ConstantRange.func_215835_a(1)).build();
        int chestWeight = Config.COMMON.chestLootChance.get();
        chestPool = LootPool.builder().addEntry(ItemLootEntry.func_216168_a(Items.BOOK).weight(chestWeight).acceptFunction(EnchantLiving.getBuilder())).addEntry(EmptyLootEntry.func_216167_a().weight(chestWeight - 1)).rolls(ConstantRange.func_215835_a(1)).build();
    }

    @SubscribeEvent
    public static void lootload(LootTableLoadEvent event) {
        LOGGER.debug("Recieved LootTableLoadEvent: "+event.getName());
        if (Config.COMMON.fishingLoot.get() && event.getName().toString().contains("minecraft:gameplay/fishing")) {
            //injectFishingLoot(event.getTable());
            event.getTable().addPool(fishingPool);
            LOGGER.debug("Automatically injected fishing loot into: " + event.getName());
        } else if (Config.COMMON.chestLoot.get() && event.getName().toString().contains("chests/")) {
            //injectChestLoot(event.getTable());
            event.getTable().addPool(LootPool.builder().addEntry(ItemLootEntry.func_216168_a(Items.BOOK).weight(100).acceptFunction(EnchantLiving.getBuilder())).addEntry(EmptyLootEntry.func_216167_a().weight(0)).build());
            LOGGER.debug("Automatically injected chest loot into: "+event.getName());
        }
    }

//    public static void injectChestLoot(LootTable table) {
//        //System.out.println("Injected loot into: "+table.)
//        LootPool.builder().addEntry(ItemLootEntry.func_216168_a())
//        LootPool pool = new LootPool(createLoot(Config.COMMON.chestLootType.get(), Config.COMMON.chestLootChance.get()), new ILootCondition[]{}, new RandomValueRange(1, 2), new RandomValueRange(0, 6), "living_chest_loot");
//        table.addPool(pool);
//    }
//
//    public static void injectFishingLoot(LootTable table) {
//        LootPool pool = new LootPool(createLoot(Config.COMMON.fishingLootType.get(), Config.COMMON.fishingLootChance.get()), new ILootCondition[]{}, new RandomValueRange(1, 1), new RandomValueRange(0, 0), "living_fishing_loot");
//        table.addPool(pool);
//    }
//
//    private static LootEntry[] createLoot(int type, int weight) {
//        LootEntry empty;
//        LootEntry[] loot;
//        ResourceLocation emptyLocation = new ResourceLocation(LivingEnchantment.MODID + ":inject/empty");
//        if (type == LivingEnchantment.JUST_BOOKS) {
//            empty = new LootEntryTable(emptyLocation, weight - 1, 0, new LootCondition[]{}, "empty");
//            loot = new LootEntry[]{books, empty};
//        } else if (Config.COMMON.allowArmor.get()) { //Armor
//            if (type == LivingEnchantment.JUST_UNIQUES) {
//                empty = new LootEntryTable(emptyLocation, (weight - 1) * 2, 0, new LootCondition[]{}, "empty");
//                loot = new LootEntry[]{armor, uniques, empty};
//            } else {
//                empty = new LootEntryTable(emptyLocation, (weight - 1) * 3, 0, new LootCondition[]{}, "empty");
//                loot = new LootEntry[]{armor, books, uniques, empty};
//            }
//        } else { //No armor
//            if (type == LivingEnchantment.JUST_UNIQUES) {
//                empty = new LootEntryTable(emptyLocation, weight - 1, 0, new LootCondition[]{}, "empty");
//                loot = new LootEntry[]{uniques, empty};
//            } else {
//                empty = new LootEntryTable(emptyLocation, (weight - 1) * 2, 0, new LootCondition[]{}, "empty");
//                loot = new LootEntry[]{books, uniques, empty};
//            }
//        }
//        return loot;
//    }
}