package net.minecraft.world.storage.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

public abstract class ParentedLootEntry extends LootEntry {
   protected final LootEntry[] field_216147_c;
   private final ILootEntry field_216148_e;

   protected ParentedLootEntry(LootEntry[] p_i51262_1_, ILootCondition[] p_i51262_2_) {
      super(p_i51262_2_);
      this.field_216147_c = p_i51262_1_;
      this.field_216148_e = this.combineChildren(p_i51262_1_);
   }

   public void func_216142_a(ValidationResults p_216142_1_, Function<ResourceLocation, LootTable> p_216142_2_, Set<ResourceLocation> p_216142_3_, LootParameterSet p_216142_4_) {
      super.func_216142_a(p_216142_1_, p_216142_2_, p_216142_3_, p_216142_4_);
      if (this.field_216147_c.length == 0) {
         p_216142_1_.addProblem("Empty children list");
      }

      for(int i = 0; i < this.field_216147_c.length; ++i) {
         this.field_216147_c[i].func_216142_a(p_216142_1_.descend(".entry[" + i + "]"), p_216142_2_, p_216142_3_, p_216142_4_);
      }

   }

   protected abstract ILootEntry combineChildren(ILootEntry[] p_216146_1_);

   public final boolean expand(LootContext p_expand_1_, Consumer<ILootGenerator> p_expand_2_) {
      return !this.func_216141_a(p_expand_1_) ? false : this.field_216148_e.expand(p_expand_1_, p_expand_2_);
   }

   public static <T extends ParentedLootEntry> ParentedLootEntry.AbstractSerializer<T> func_216145_a(ResourceLocation p_216145_0_, Class<T> p_216145_1_, final ParentedLootEntry.IFactory<T> p_216145_2_) {
      return new ParentedLootEntry.AbstractSerializer<T>(p_216145_0_, p_216145_1_) {
         protected T func_216186_a(JsonObject p_216186_1_, JsonDeserializationContext p_216186_2_, LootEntry[] p_216186_3_, ILootCondition[] p_216186_4_) {
            return p_216145_2_.create(p_216186_3_, p_216186_4_);
         }
      };
   }

   public abstract static class AbstractSerializer<T extends ParentedLootEntry> extends LootEntry.Serializer<T> {
      public AbstractSerializer(ResourceLocation p_i50490_1_, Class<T> p_i50490_2_) {
         super(p_i50490_1_, p_i50490_2_);
      }

      public void func_212830_a_(JsonObject p_212830_1_, T p_212830_2_, JsonSerializationContext p_212830_3_) {
         p_212830_1_.add("children", p_212830_3_.serialize(p_212830_2_.field_216147_c));
      }

      public final T func_212865_b_(JsonObject p_212865_1_, JsonDeserializationContext p_212865_2_, ILootCondition[] p_212865_3_) {
         LootEntry[] alootentry = JSONUtils.deserializeClass(p_212865_1_, "children", p_212865_2_, LootEntry[].class);
         return (T)this.func_216186_a(p_212865_1_, p_212865_2_, alootentry, p_212865_3_);
      }

      protected abstract T func_216186_a(JsonObject p_216186_1_, JsonDeserializationContext p_216186_2_, LootEntry[] p_216186_3_, ILootCondition[] p_216186_4_);
   }

   @FunctionalInterface
   public interface IFactory<T extends ParentedLootEntry> {
      T create(LootEntry[] p_create_1_, ILootCondition[] p_create_2_);
   }
}