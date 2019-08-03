package net.minecraft.world.storage.loot;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.ILootFunction;
import org.apache.commons.lang3.ArrayUtils;

public abstract class LootFunction implements ILootFunction {
   protected final ILootCondition[] conditions;
   private final Predicate<LootContext> combinedConditions;

   protected LootFunction(ILootCondition[] conditionsIn) {
      this.conditions = conditionsIn;
      this.combinedConditions = LootConditionManager.and(conditionsIn);
   }

   public final ItemStack apply(ItemStack p_apply_1_, LootContext p_apply_2_) {
      return this.combinedConditions.test(p_apply_2_) ? this.doApply(p_apply_1_, p_apply_2_) : p_apply_1_;
   }

   protected abstract ItemStack doApply(ItemStack stack, LootContext context);

   public void func_215856_a(ValidationResults p_215856_1_, Function<ResourceLocation, LootTable> p_215856_2_, Set<ResourceLocation> p_215856_3_, LootParameterSet p_215856_4_) {
      ILootFunction.super.func_215856_a(p_215856_1_, p_215856_2_, p_215856_3_, p_215856_4_);

      for(int i = 0; i < this.conditions.length; ++i) {
         this.conditions[i].func_215856_a(p_215856_1_.descend(".conditions[" + i + "]"), p_215856_2_, p_215856_3_, p_215856_4_);
      }

   }

   protected static LootFunction.Builder<?> builder(Function<ILootCondition[], ILootFunction> p_215860_0_) {
      return new LootFunction.SimpleBuilder(p_215860_0_);
   }

   public abstract static class Builder<T extends LootFunction.Builder<T>> implements ILootFunction.IBuilder, ILootConditionConsumer<T> {
      private final List<ILootCondition> conditions = Lists.newArrayList();

      public T acceptCondition(ILootCondition.IBuilder conditionBuilder) {
         this.conditions.add(conditionBuilder.build());
         return (T)this.doCast();
      }

      public final T cast() {
         return (T)this.doCast();
      }

      protected abstract T doCast();

      protected ILootCondition[] getConditions() {
         return this.conditions.toArray(new ILootCondition[0]);
      }
   }

   public abstract static class Serializer<T extends LootFunction> extends ILootFunction.Serializer<T> {
      public Serializer(ResourceLocation p_i50228_1_, Class<T> p_i50228_2_) {
         super(p_i50228_1_, p_i50228_2_);
      }

      public void serialize(JsonObject object, T functionClazz, JsonSerializationContext serializationContext) {
         if (!ArrayUtils.isEmpty((Object[])functionClazz.conditions)) {
            object.add("conditions", serializationContext.serialize(functionClazz.conditions));
         }

      }

      public final T deserialize(JsonObject p_212870_1_, JsonDeserializationContext p_212870_2_) {
         ILootCondition[] ailootcondition = JSONUtils.deserializeClass(p_212870_1_, "conditions", new ILootCondition[0], p_212870_2_, ILootCondition[].class);
         return (T)this.deserialize(p_212870_1_, p_212870_2_, ailootcondition);
      }

      public abstract T deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn);
   }

   static final class SimpleBuilder extends LootFunction.Builder<LootFunction.SimpleBuilder> {
      private final Function<ILootCondition[], ILootFunction> function;

      public SimpleBuilder(Function<ILootCondition[], ILootFunction> p_i50229_1_) {
         this.function = p_i50229_1_;
      }

      protected LootFunction.SimpleBuilder doCast() {
         return this;
      }

      public ILootFunction build() {
         return this.function.apply(this.getConditions());
      }
   }
}