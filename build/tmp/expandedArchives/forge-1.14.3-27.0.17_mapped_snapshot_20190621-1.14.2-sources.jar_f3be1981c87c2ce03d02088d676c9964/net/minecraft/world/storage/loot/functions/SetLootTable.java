package net.minecraft.world.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.LootParameterSet;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.ValidationResults;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

public class SetLootTable extends LootFunction {
   private final ResourceLocation field_215928_a;
   private final long field_215929_c;

   private SetLootTable(ILootCondition[] p_i51224_1_, ResourceLocation p_i51224_2_, long p_i51224_3_) {
      super(p_i51224_1_);
      this.field_215928_a = p_i51224_2_;
      this.field_215929_c = p_i51224_3_;
   }

   public ItemStack doApply(ItemStack stack, LootContext context) {
      if (stack.isEmpty()) {
         return stack;
      } else {
         CompoundNBT compoundnbt = new CompoundNBT();
         compoundnbt.putString("LootTable", this.field_215928_a.toString());
         if (this.field_215929_c != 0L) {
            compoundnbt.putLong("LootTableSeed", this.field_215929_c);
         }

         stack.getOrCreateTag().put("BlockEntityTag", compoundnbt);
         return stack;
      }
   }

   public void func_215856_a(ValidationResults p_215856_1_, Function<ResourceLocation, LootTable> p_215856_2_, Set<ResourceLocation> p_215856_3_, LootParameterSet p_215856_4_) {
      if (p_215856_3_.contains(this.field_215928_a)) {
         p_215856_1_.addProblem("Table " + this.field_215928_a + " is recursively called");
      } else {
         super.func_215856_a(p_215856_1_, p_215856_2_, p_215856_3_, p_215856_4_);
         LootTable loottable = p_215856_2_.apply(this.field_215928_a);
         if (loottable == null) {
            p_215856_1_.addProblem("Unknown loot table called " + this.field_215928_a);
         } else {
            Set<ResourceLocation> set = ImmutableSet.<ResourceLocation>builder().addAll(p_215856_3_).add(this.field_215928_a).build();
            loottable.func_216117_a(p_215856_1_.descend("->{" + this.field_215928_a + "}"), p_215856_2_, set, p_215856_4_);
         }

      }
   }

   public static class Serializer extends LootFunction.Serializer<SetLootTable> {
      protected Serializer() {
         super(new ResourceLocation("set_loot_table"), SetLootTable.class);
      }

      public void serialize(JsonObject object, SetLootTable functionClazz, JsonSerializationContext serializationContext) {
         super.serialize(object, functionClazz, serializationContext);
         object.addProperty("name", functionClazz.field_215928_a.toString());
         if (functionClazz.field_215929_c != 0L) {
            object.addProperty("seed", functionClazz.field_215929_c);
         }

      }

      public SetLootTable deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn) {
         ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(object, "name"));
         long i = JSONUtils.func_219796_a(object, "seed", 0L);
         return new SetLootTable(conditionsIn, resourcelocation, i);
      }
   }
}