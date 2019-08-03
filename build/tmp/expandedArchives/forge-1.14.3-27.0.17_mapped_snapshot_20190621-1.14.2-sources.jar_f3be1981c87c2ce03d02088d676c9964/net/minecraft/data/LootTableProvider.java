package net.minecraft.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.data.loot.ChestLootTables;
import net.minecraft.data.loot.EntityLootTables;
import net.minecraft.data.loot.FishingLootTables;
import net.minecraft.data.loot.GiftLootTables;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootParameterSet;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraft.world.storage.loot.LootTables;
import net.minecraft.world.storage.loot.ValidationResults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootTableProvider implements IDataProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
   private final DataGenerator field_218443_d;
   private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> field_218444_e = ImmutableList.of(Pair.of(FishingLootTables::new, LootParameterSets.FISHING), Pair.of(ChestLootTables::new, LootParameterSets.CHEST), Pair.of(EntityLootTables::new, LootParameterSets.ENTITY), Pair.of(BlockLootTables::new, LootParameterSets.BLOCK), Pair.of(GiftLootTables::new, LootParameterSets.GIFT));

   public LootTableProvider(DataGenerator p_i50789_1_) {
      this.field_218443_d = p_i50789_1_;
   }

   /**
    * Performs this provider's action.
    */
   public void act(DirectoryCache cache) {
      Path path = this.field_218443_d.getOutputFolder();
      Map<ResourceLocation, LootTable> map = Maps.newHashMap();
      this.field_218444_e.forEach((p_218438_1_) -> {
         p_218438_1_.getFirst().get().accept((p_218437_2_, p_218437_3_) -> {
            if (map.put(p_218437_2_, p_218437_3_.func_216039_a(p_218438_1_.getSecond()).func_216038_b()) != null) {
               throw new IllegalStateException("Duplicate loot table " + p_218437_2_);
            }
         });
      });
      ValidationResults validationresults = new ValidationResults();

      for(ResourceLocation resourcelocation : Sets.difference(LootTables.func_215796_a(), map.keySet())) {
         validationresults.addProblem("Missing built-in table: " + resourcelocation);
      }

      map.forEach((p_218436_2_, p_218436_3_) -> {
         LootTableManager.func_215302_a(validationresults, p_218436_2_, p_218436_3_, map::get);
      });
      Multimap<String, String> multimap = validationresults.getProblems();
      if (!multimap.isEmpty()) {
         multimap.forEach((p_218435_0_, p_218435_1_) -> {
            LOGGER.warn("Found validation problem in " + p_218435_0_ + ": " + p_218435_1_);
         });
         throw new IllegalStateException("Failed to validate loot tables, see logs");
      } else {
         map.forEach((p_218440_2_, p_218440_3_) -> {
            Path path1 = func_218439_a(path, p_218440_2_);

            try {
               IDataProvider.func_218426_a(GSON, cache, LootTableManager.func_215301_a(p_218440_3_), path1);
            } catch (IOException ioexception) {
               LOGGER.error("Couldn't save loot table {}", path1, ioexception);
            }

         });
      }
   }

   private static Path func_218439_a(Path p_218439_0_, ResourceLocation p_218439_1_) {
      return p_218439_0_.resolve("data/" + p_218439_1_.getNamespace() + "/loot_tables/" + p_218439_1_.getPath() + ".json");
   }

   /**
    * Gets a name for this provider, to use in logging.
    */
   public String getName() {
      return "LootTables";
   }
}