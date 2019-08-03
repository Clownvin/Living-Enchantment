package net.minecraft.world.storage.loot;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.ILootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootTableManager extends JsonReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON_INSTANCE = (new GsonBuilder()).registerTypeAdapter(RandomValueRange.class, new RandomValueRange.Serializer()).registerTypeAdapter(BinomialRange.class, new BinomialRange.Serializer()).registerTypeAdapter(ConstantRange.class, new ConstantRange.Serializer()).registerTypeAdapter(IntClamper.class, new IntClamper.Serializer()).registerTypeAdapter(LootPool.class, new LootPool.Serializer()).registerTypeAdapter(LootTable.class, new LootTable.Serializer()).registerTypeHierarchyAdapter(LootEntry.class, new LootEntryManager.Serializer()).registerTypeHierarchyAdapter(ILootFunction.class, new LootFunctionManager.Serializer()).registerTypeHierarchyAdapter(ILootCondition.class, new LootConditionManager.Serializer()).registerTypeHierarchyAdapter(LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer()).create();
   private Map<ResourceLocation, LootTable> registeredLootTables = ImmutableMap.of();

   public LootTableManager() {
      super(GSON_INSTANCE, "loot_tables");
   }

   public LootTable getLootTableFromLocation(ResourceLocation ressources) {
      return this.registeredLootTables.getOrDefault(ressources, LootTable.EMPTY_LOOT_TABLE);
   }

   /**
    * Performs any reloading that must be done on the main thread, such as uploading textures to the GPU or touching
    * non-threadsafe data
    */
   protected void apply(Map<ResourceLocation, JsonObject> p_212853_1_, IResourceManager p_212853_2_, IProfiler p_212853_3_) {
      Builder<ResourceLocation, LootTable> builder = ImmutableMap.builder();
      JsonObject jsonobject = p_212853_1_.remove(LootTables.EMPTY);
      if (jsonobject != null) {
         LOGGER.warn("Datapack tried to redefine {} loot table, ignoring", (Object)LootTables.EMPTY);
      }

      p_212853_1_.forEach((p_223385_1_, p_223385_2_) -> {
         try {
            net.minecraft.resources.IResource res = p_212853_2_.getResource(getPreparedPath(p_223385_1_));
            LootTable loottable = net.minecraftforge.common.ForgeHooks.loadLootTable(GSON_INSTANCE, p_223385_1_, p_223385_2_, res == null || !res.getPackName().equals("Default"), this);
            builder.put(p_223385_1_, loottable);
         } catch (Exception exception) {
            LOGGER.error("Couldn't parse loot table {}", p_223385_1_, exception);
         }

      });
      builder.put(LootTables.EMPTY, LootTable.EMPTY_LOOT_TABLE);
      ImmutableMap<ResourceLocation, LootTable> immutablemap = builder.build();
      ValidationResults validationresults = new ValidationResults();
      immutablemap.forEach((p_215305_2_, p_215305_3_) -> {
         func_215302_a(validationresults, p_215305_2_, p_215305_3_, immutablemap::get);
      });
      validationresults.getProblems().forEach((p_215303_0_, p_215303_1_) -> {
         LOGGER.warn("Found validation problem in " + p_215303_0_ + ": " + p_215303_1_);
      });
      this.registeredLootTables = immutablemap;
   }

   public static void func_215302_a(ValidationResults p_215302_0_, ResourceLocation p_215302_1_, LootTable p_215302_2_, Function<ResourceLocation, LootTable> p_215302_3_) {
      Set<ResourceLocation> set = ImmutableSet.of(p_215302_1_);
      p_215302_2_.func_216117_a(p_215302_0_.descend("{" + p_215302_1_.toString() + "}"), p_215302_3_, set, p_215302_2_.func_216122_a());
   }

   public static JsonElement func_215301_a(LootTable p_215301_0_) {
      return GSON_INSTANCE.toJsonTree(p_215301_0_);
   }

   public Set<ResourceLocation> func_215304_a() {
      return this.registeredLootTables.keySet();
   }
}