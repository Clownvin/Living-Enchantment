package net.minecraft.data.loot;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.storage.loot.ConstantRange;
import net.minecraft.world.storage.loot.ItemLootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTables;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.StandaloneLootEntry;
import net.minecraft.world.storage.loot.TableLootEntry;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraft.world.storage.loot.conditions.LocationCheck;
import net.minecraft.world.storage.loot.functions.EnchantWithLevels;
import net.minecraft.world.storage.loot.functions.SetCount;
import net.minecraft.world.storage.loot.functions.SetDamage;
import net.minecraft.world.storage.loot.functions.SetNBT;

public class FishingLootTables implements Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> {
   public static final ILootCondition.IBuilder IN_JUNGLE = LocationCheck.builder((new LocationPredicate.Builder()).biome(Biomes.JUNGLE));
   public static final ILootCondition.IBuilder IN_JUNGLE_HILLS = LocationCheck.builder((new LocationPredicate.Builder()).biome(Biomes.JUNGLE_HILLS));
   public static final ILootCondition.IBuilder IN_JUNGLE_EDGE = LocationCheck.builder((new LocationPredicate.Builder()).biome(Biomes.JUNGLE_EDGE));
   public static final ILootCondition.IBuilder IN_BAMBOO_JUNGLE = LocationCheck.builder((new LocationPredicate.Builder()).biome(Biomes.BAMBOO_JUNGLE));
   public static final ILootCondition.IBuilder IN_MODIFIED_JUNGLE = LocationCheck.builder((new LocationPredicate.Builder()).biome(Biomes.MODIFIED_JUNGLE));
   public static final ILootCondition.IBuilder IN_MODIFIED_JUNGLE_EDGE = LocationCheck.builder((new LocationPredicate.Builder()).biome(Biomes.MODIFIED_JUNGLE_EDGE));
   public static final ILootCondition.IBuilder IN_BAMBOO_JUNGLE_HILLS = LocationCheck.builder((new LocationPredicate.Builder()).biome(Biomes.BAMBOO_JUNGLE_HILLS));

   public void accept(BiConsumer<ResourceLocation, LootTable.Builder> p_accept_1_) {
      p_accept_1_.accept(LootTables.GAMEPLAY_FISHING, LootTable.builder().func_216040_a(LootPool.builder().rolls(ConstantRange.func_215835_a(1)).addEntry(TableLootEntry.func_216171_a(LootTables.GAMEPLAY_FISHING_JUNK).weight(10).quality(-2)).addEntry(TableLootEntry.func_216171_a(LootTables.GAMEPLAY_FISHING_TREASURE).weight(5).quality(2)).addEntry(TableLootEntry.func_216171_a(LootTables.GAMEPLAY_FISHING_FISH).weight(85).quality(-1))));
      p_accept_1_.accept(LootTables.GAMEPLAY_FISHING_FISH, LootTable.builder().func_216040_a(LootPool.builder().addEntry(ItemLootEntry.func_216168_a(Items.COD).weight(60)).addEntry(ItemLootEntry.func_216168_a(Items.SALMON).weight(25)).addEntry(ItemLootEntry.func_216168_a(Items.TROPICAL_FISH).weight(2)).addEntry(ItemLootEntry.func_216168_a(Items.PUFFERFISH).weight(13))));
      p_accept_1_.accept(LootTables.GAMEPLAY_FISHING_JUNK, LootTable.builder().func_216040_a(LootPool.builder().addEntry(ItemLootEntry.func_216168_a(Items.LEATHER_BOOTS).weight(10).acceptFunction(SetDamage.func_215931_a(RandomValueRange.func_215837_a(0.0F, 0.9F)))).addEntry(ItemLootEntry.func_216168_a(Items.LEATHER).weight(10)).addEntry(ItemLootEntry.func_216168_a(Items.BONE).weight(10)).addEntry(ItemLootEntry.func_216168_a(Items.POTION).weight(10).acceptFunction(SetNBT.func_215952_a(Util.make(new CompoundNBT(), (p_218588_0_) -> {
         p_218588_0_.putString("Potion", "minecraft:water");
      })))).addEntry(ItemLootEntry.func_216168_a(Items.STRING).weight(5)).addEntry(ItemLootEntry.func_216168_a(Items.FISHING_ROD).weight(2).acceptFunction(SetDamage.func_215931_a(RandomValueRange.func_215837_a(0.0F, 0.9F)))).addEntry(ItemLootEntry.func_216168_a(Items.BOWL).weight(10)).addEntry(ItemLootEntry.func_216168_a(Items.STICK).weight(5)).addEntry(ItemLootEntry.func_216168_a(Items.INK_SAC).weight(1).acceptFunction(SetCount.func_215932_a(ConstantRange.func_215835_a(10)))).addEntry(ItemLootEntry.func_216168_a(Blocks.TRIPWIRE_HOOK).weight(10)).addEntry(ItemLootEntry.func_216168_a(Items.ROTTEN_FLESH).weight(10)).addEntry(((StandaloneLootEntry.Builder)ItemLootEntry.func_216168_a(Blocks.BAMBOO).acceptCondition(IN_JUNGLE.alternative(IN_JUNGLE_HILLS).alternative(IN_JUNGLE_EDGE).alternative(IN_BAMBOO_JUNGLE).alternative(IN_MODIFIED_JUNGLE).alternative(IN_MODIFIED_JUNGLE_EDGE).alternative(IN_BAMBOO_JUNGLE_HILLS))).weight(10))));
      p_accept_1_.accept(LootTables.GAMEPLAY_FISHING_TREASURE, LootTable.builder().func_216040_a(LootPool.builder().addEntry(ItemLootEntry.func_216168_a(Blocks.LILY_PAD)).addEntry(ItemLootEntry.func_216168_a(Items.NAME_TAG)).addEntry(ItemLootEntry.func_216168_a(Items.SADDLE)).addEntry(ItemLootEntry.func_216168_a(Items.BOW).acceptFunction(SetDamage.func_215931_a(RandomValueRange.func_215837_a(0.0F, 0.25F))).acceptFunction(EnchantWithLevels.func_215895_a(ConstantRange.func_215835_a(30)).func_216059_e())).addEntry(ItemLootEntry.func_216168_a(Items.FISHING_ROD).acceptFunction(SetDamage.func_215931_a(RandomValueRange.func_215837_a(0.0F, 0.25F))).acceptFunction(EnchantWithLevels.func_215895_a(ConstantRange.func_215835_a(30)).func_216059_e())).addEntry(ItemLootEntry.func_216168_a(Items.BOOK).acceptFunction(EnchantWithLevels.func_215895_a(ConstantRange.func_215835_a(30)).func_216059_e())).addEntry(ItemLootEntry.func_216168_a(Items.NAUTILUS_SHELL))));
   }
}