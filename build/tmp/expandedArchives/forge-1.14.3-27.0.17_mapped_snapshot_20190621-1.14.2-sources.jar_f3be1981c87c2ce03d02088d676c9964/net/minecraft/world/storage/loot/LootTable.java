package net.minecraft.world.storage.loot;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.loot.functions.ILootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootTable {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final LootTable EMPTY_LOOT_TABLE = new LootTable(LootParameterSets.EMPTY, new LootPool[0], new ILootFunction[0]);
   public static final LootParameterSet DEFAULT_PARAMETER_SET = LootParameterSets.GENERIC;
   private final LootParameterSet parameterSet;
   private final List<LootPool> pools;
   private final ILootFunction[] functions;
   private final BiFunction<ItemStack, LootContext, ItemStack> combinedFunctions;

   private LootTable(LootParameterSet p_i51265_1_, LootPool[] p_i51265_2_, ILootFunction[] p_i51265_3_) {
      this.parameterSet = p_i51265_1_;
      this.pools = Lists.newArrayList(p_i51265_2_);
      this.functions = p_i51265_3_;
      this.combinedFunctions = LootFunctionManager.combine(p_i51265_3_);
   }

   public static Consumer<ItemStack> capStackSizes(Consumer<ItemStack> p_216124_0_) {
      return (p_216125_1_) -> {
         if (p_216125_1_.getCount() < p_216125_1_.getMaxStackSize()) {
            p_216124_0_.accept(p_216125_1_);
         } else {
            int i = p_216125_1_.getCount();

            while(i > 0) {
               ItemStack itemstack = p_216125_1_.copy();
               itemstack.setCount(Math.min(p_216125_1_.getMaxStackSize(), i));
               i -= itemstack.getCount();
               p_216124_0_.accept(itemstack);
            }
         }

      };
   }

   public void recursiveGenerate(LootContext p_216114_1_, Consumer<ItemStack> p_216114_2_) {
      if (p_216114_1_.addLootTable(this)) {
         Consumer<ItemStack> consumer = ILootFunction.func_215858_a(this.combinedFunctions, p_216114_2_, p_216114_1_);

         for(LootPool lootpool : this.pools) {
            lootpool.generate(consumer, p_216114_1_);
         }

         p_216114_1_.removeLootTable(this);
      } else {
         LOGGER.warn("Detected infinite loop in loot tables");
      }

   }

   public void generate(LootContext p_216120_1_, Consumer<ItemStack> p_216120_2_) {
      this.recursiveGenerate(p_216120_1_, capStackSizes(p_216120_2_));
   }

   public List<ItemStack> generate(LootContext p_216113_1_) {
      List<ItemStack> list = Lists.newArrayList();
      this.generate(p_216113_1_, list::add);
      return list;
   }

   public LootParameterSet func_216122_a() {
      return this.parameterSet;
   }

   public void func_216117_a(ValidationResults p_216117_1_, Function<ResourceLocation, LootTable> p_216117_2_, Set<ResourceLocation> p_216117_3_, LootParameterSet p_216117_4_) {
      for(int i = 0; i < this.pools.size(); ++i) {
         this.pools.get(i).func_216099_a(p_216117_1_.descend(".pools[" + i + "]"), p_216117_2_, p_216117_3_, p_216117_4_);
      }

      for(int j = 0; j < this.functions.length; ++j) {
         this.functions[j].func_215856_a(p_216117_1_.descend(".functions[" + j + "]"), p_216117_2_, p_216117_3_, p_216117_4_);
      }

   }

   public void fillInventory(IInventory p_216118_1_, LootContext p_216118_2_) {
      List<ItemStack> list = this.generate(p_216118_2_);
      Random random = p_216118_2_.getRandom();
      List<Integer> list1 = this.getEmptySlotsRandomized(p_216118_1_, random);
      this.shuffleItems(list, list1.size(), random);

      for(ItemStack itemstack : list) {
         if (list1.isEmpty()) {
            LOGGER.warn("Tried to over-fill a container");
            return;
         }

         if (itemstack.isEmpty()) {
            p_216118_1_.setInventorySlotContents(list1.remove(list1.size() - 1), ItemStack.EMPTY);
         } else {
            p_216118_1_.setInventorySlotContents(list1.remove(list1.size() - 1), itemstack);
         }
      }

   }

   /**
    * shuffles items by changing their order and splitting stacks
    */
   private void shuffleItems(List<ItemStack> stacks, int p_186463_2_, Random rand) {
      List<ItemStack> list = Lists.newArrayList();
      Iterator<ItemStack> iterator = stacks.iterator();

      while(iterator.hasNext()) {
         ItemStack itemstack = iterator.next();
         if (itemstack.isEmpty()) {
            iterator.remove();
         } else if (itemstack.getCount() > 1) {
            list.add(itemstack);
            iterator.remove();
         }
      }

      while(p_186463_2_ - stacks.size() - list.size() > 0 && !list.isEmpty()) {
         ItemStack itemstack2 = list.remove(MathHelper.nextInt(rand, 0, list.size() - 1));
         int i = MathHelper.nextInt(rand, 1, itemstack2.getCount() / 2);
         ItemStack itemstack1 = itemstack2.split(i);
         if (itemstack2.getCount() > 1 && rand.nextBoolean()) {
            list.add(itemstack2);
         } else {
            stacks.add(itemstack2);
         }

         if (itemstack1.getCount() > 1 && rand.nextBoolean()) {
            list.add(itemstack1);
         } else {
            stacks.add(itemstack1);
         }
      }

      stacks.addAll(list);
      Collections.shuffle(stacks, rand);
   }

   private List<Integer> getEmptySlotsRandomized(IInventory inventory, Random rand) {
      List<Integer> list = Lists.newArrayList();

      for(int i = 0; i < inventory.getSizeInventory(); ++i) {
         if (inventory.getStackInSlot(i).isEmpty()) {
            list.add(i);
         }
      }

      Collections.shuffle(list, rand);
      return list;
   }

   public static LootTable.Builder builder() {
      return new LootTable.Builder();
   }

   public static class Builder implements ILootFunctionConsumer<LootTable.Builder> {
      private final List<LootPool> field_216041_a = Lists.newArrayList();
      private final List<ILootFunction> field_216042_b = Lists.newArrayList();
      private LootParameterSet field_216043_c = LootTable.DEFAULT_PARAMETER_SET;

      public LootTable.Builder func_216040_a(LootPool.Builder p_216040_1_) {
         this.field_216041_a.add(p_216040_1_.build());
         return this;
      }

      public LootTable.Builder func_216039_a(LootParameterSet p_216039_1_) {
         this.field_216043_c = p_216039_1_;
         return this;
      }

      public LootTable.Builder acceptFunction(ILootFunction.IBuilder functionBuilder) {
         this.field_216042_b.add(functionBuilder.build());
         return this;
      }

      public LootTable.Builder cast() {
         return this;
      }

      public LootTable func_216038_b() {
         return new LootTable(this.field_216043_c, this.field_216041_a.toArray(new LootPool[0]), this.field_216042_b.toArray(new ILootFunction[0]));
      }
   }

   //======================== FORGE START =============================================
   private boolean isFrozen = false;
   public void freeze() {
      this.isFrozen = true;
      this.pools.forEach(LootPool::freeze);
   }
   public boolean isFrozen(){ return this.isFrozen; }
   private void checkFrozen() {
      if (this.isFrozen())
         throw new RuntimeException("Attempted to modify LootTable after being finalized!");
   }

   public LootPool getPool(String name) {
      return pools.stream().filter(e -> name.equals(e.getName())).findFirst().orElse(null);
   }

   public LootPool removePool(String name) {
      checkFrozen();
      for (LootPool pool : this.pools) {
         if (name.equals(pool.getName())) {
            this.pools.remove(pool);
            return pool;
         }
      }
      return null;
   }

   public void addPool(LootPool pool) {
      checkFrozen();
      if (pools.stream().anyMatch(e -> e == pool || e.getName().equals(pool.getName())))
         throw new RuntimeException("Attempted to add a duplicate pool to loot table: " + pool.getName());
      this.pools.add(pool);
   }
   //======================== FORGE END ===============================================

   public static class Serializer implements JsonDeserializer<LootTable>, JsonSerializer<LootTable> {
      public LootTable deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject jsonobject = JSONUtils.getJsonObject(p_deserialize_1_, "loot table");
         LootPool[] alootpool = JSONUtils.deserializeClass(jsonobject, "pools", new LootPool[0], p_deserialize_3_, LootPool[].class);
         LootParameterSet lootparameterset = null;
         if (jsonobject.has("type")) {
            String s = JSONUtils.getString(jsonobject, "type");
            lootparameterset = LootParameterSets.getValue(new ResourceLocation(s));
         }

         ILootFunction[] ailootfunction = JSONUtils.deserializeClass(jsonobject, "functions", new ILootFunction[0], p_deserialize_3_, ILootFunction[].class);
         return new LootTable(lootparameterset != null ? lootparameterset : LootParameterSets.GENERIC, alootpool, ailootfunction);
      }

      public JsonElement serialize(LootTable p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         JsonObject jsonobject = new JsonObject();
         if (p_serialize_1_.parameterSet != LootTable.DEFAULT_PARAMETER_SET) {
            ResourceLocation resourcelocation = LootParameterSets.getKey(p_serialize_1_.parameterSet);
            if (resourcelocation != null) {
               jsonobject.addProperty("type", resourcelocation.toString());
            } else {
               LootTable.LOGGER.warn("Failed to find id for param set " + p_serialize_1_.parameterSet);
            }
         }

         if (!p_serialize_1_.pools.isEmpty()) {
            jsonobject.add("pools", p_serialize_3_.serialize(p_serialize_1_.pools));
         }

         if (!ArrayUtils.isEmpty((Object[])p_serialize_1_.functions)) {
            jsonobject.add("functions", p_serialize_3_.serialize(p_serialize_1_.functions));
         }

         return jsonobject;
      }
   }
}