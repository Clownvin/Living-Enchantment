package net.minecraft.world.storage.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.LootParameterSet;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.ValidationResults;

public class Inverted implements ILootCondition {
   private final ILootCondition term;

   private Inverted(ILootCondition term) {
      this.term = term;
   }

   public final boolean test(LootContext p_test_1_) {
      return !this.term.test(p_test_1_);
   }

   public Set<LootParameter<?>> getRequiredParameters() {
      return this.term.getRequiredParameters();
   }

   public void func_215856_a(ValidationResults p_215856_1_, Function<ResourceLocation, LootTable> p_215856_2_, Set<ResourceLocation> p_215856_3_, LootParameterSet p_215856_4_) {
      ILootCondition.super.func_215856_a(p_215856_1_, p_215856_2_, p_215856_3_, p_215856_4_);
      this.term.func_215856_a(p_215856_1_, p_215856_2_, p_215856_3_, p_215856_4_);
   }

   public static ILootCondition.IBuilder builder(ILootCondition.IBuilder p_215979_0_) {
      Inverted inverted = new Inverted(p_215979_0_.build());
      return () -> {
         return inverted;
      };
   }

   public static class Serializer extends ILootCondition.AbstractSerializer<Inverted> {
      public Serializer() {
         super(new ResourceLocation("inverted"), Inverted.class);
      }

      public void serialize(JsonObject json, Inverted value, JsonSerializationContext context) {
         json.add("term", context.serialize(value.term));
      }

      public Inverted deserialize(JsonObject json, JsonDeserializationContext context) {
         ILootCondition ilootcondition = JSONUtils.deserializeClass(json, "term", context, ILootCondition.class);
         return new Inverted(ilootcondition);
      }
   }
}