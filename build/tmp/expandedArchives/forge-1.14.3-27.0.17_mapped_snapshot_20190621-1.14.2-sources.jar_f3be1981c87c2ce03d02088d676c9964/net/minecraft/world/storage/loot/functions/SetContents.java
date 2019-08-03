package net.minecraft.world.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.LootParameterSet;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.ValidationResults;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

public class SetContents extends LootFunction {
   private final List<LootEntry> field_215924_a;

   private SetContents(ILootCondition[] p_i51226_1_, List<LootEntry> p_i51226_2_) {
      super(p_i51226_1_);
      this.field_215924_a = ImmutableList.copyOf(p_i51226_2_);
   }

   public ItemStack doApply(ItemStack stack, LootContext context) {
      if (stack.isEmpty()) {
         return stack;
      } else {
         NonNullList<ItemStack> nonnulllist = NonNullList.create();
         this.field_215924_a.forEach((p_215921_2_) -> {
            p_215921_2_.expand(context, (p_215922_2_) -> {
               p_215922_2_.func_216188_a(LootTable.capStackSizes(nonnulllist::add), context);
            });
         });
         CompoundNBT compoundnbt = new CompoundNBT();
         ItemStackHelper.saveAllItems(compoundnbt, nonnulllist);
         CompoundNBT compoundnbt1 = stack.getOrCreateTag();
         compoundnbt1.put("BlockEntityTag", compoundnbt.merge(compoundnbt1.getCompound("BlockEntityTag")));
         return stack;
      }
   }

   public void func_215856_a(ValidationResults p_215856_1_, Function<ResourceLocation, LootTable> p_215856_2_, Set<ResourceLocation> p_215856_3_, LootParameterSet p_215856_4_) {
      super.func_215856_a(p_215856_1_, p_215856_2_, p_215856_3_, p_215856_4_);

      for(int i = 0; i < this.field_215924_a.size(); ++i) {
         this.field_215924_a.get(i).func_216142_a(p_215856_1_.descend(".entry[" + i + "]"), p_215856_2_, p_215856_3_, p_215856_4_);
      }

   }

   public static SetContents.Builder func_215920_b() {
      return new SetContents.Builder();
   }

   public static class Builder extends LootFunction.Builder<SetContents.Builder> {
      private final List<LootEntry> field_216076_a = Lists.newArrayList();

      protected SetContents.Builder doCast() {
         return this;
      }

      public SetContents.Builder func_216075_a(LootEntry.Builder<?> p_216075_1_) {
         this.field_216076_a.add(p_216075_1_.func_216081_b());
         return this;
      }

      public ILootFunction build() {
         return new SetContents(this.getConditions(), this.field_216076_a);
      }
   }

   public static class Serializer extends LootFunction.Serializer<SetContents> {
      protected Serializer() {
         super(new ResourceLocation("set_contents"), SetContents.class);
      }

      public void serialize(JsonObject object, SetContents functionClazz, JsonSerializationContext serializationContext) {
         super.serialize(object, functionClazz, serializationContext);
         object.add("entries", serializationContext.serialize(functionClazz.field_215924_a));
      }

      public SetContents deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn) {
         LootEntry[] alootentry = JSONUtils.deserializeClass(object, "entries", deserializationContext, LootEntry[].class);
         return new SetContents(conditionsIn, Arrays.asList(alootentry));
      }
   }
}