package net.minecraft.world.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.advancements.criterion.NBTPredicate;
import net.minecraft.command.arguments.NBTPathArgument;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

public class CopyNbt extends LootFunction {
   private final CopyNbt.Source field_215887_a;
   private final List<CopyNbt.Operation> field_215888_c;
   private static final Function<Entity, INBT> field_215889_d = NBTPredicate::writeToNBTWithSelectedItem;
   private static final Function<TileEntity, INBT> field_215890_e = (p_215882_0_) -> {
      return p_215882_0_.write(new CompoundNBT());
   };

   private CopyNbt(ILootCondition[] p_i51240_1_, CopyNbt.Source p_i51240_2_, List<CopyNbt.Operation> p_i51240_3_) {
      super(p_i51240_1_);
      this.field_215887_a = p_i51240_2_;
      this.field_215888_c = ImmutableList.copyOf(p_i51240_3_);
   }

   private static NBTPathArgument.NBTPath func_215880_b(String p_215880_0_) {
      try {
         return (new NBTPathArgument()).parse(new StringReader(p_215880_0_));
      } catch (CommandSyntaxException commandsyntaxexception) {
         throw new IllegalArgumentException("Failed to parse path " + p_215880_0_, commandsyntaxexception);
      }
   }

   public Set<LootParameter<?>> getRequiredParameters() {
      return ImmutableSet.of(this.field_215887_a.field_216225_f);
   }

   public ItemStack doApply(ItemStack stack, LootContext context) {
      INBT inbt = this.field_215887_a.field_216226_g.apply(context);
      if (inbt != null) {
         this.field_215888_c.forEach((p_215885_2_) -> {
            p_215885_2_.func_216216_a(stack::getOrCreateTag, inbt);
         });
      }

      return stack;
   }

   public static CopyNbt.Builder func_215881_a(CopyNbt.Source p_215881_0_) {
      return new CopyNbt.Builder(p_215881_0_);
   }

   public static enum Action {
      REPLACE("replace") {
         public void func_216227_a(INBT p_216227_1_, NBTPathArgument.NBTPath p_216227_2_, List<INBT> p_216227_3_) throws CommandSyntaxException {
            INBT inbt = Iterables.getLast(p_216227_3_);
            p_216227_2_.func_218076_b(p_216227_1_, inbt::copy);
         }
      },
      APPEND("append") {
         public void func_216227_a(INBT p_216227_1_, NBTPathArgument.NBTPath p_216227_2_, List<INBT> p_216227_3_) throws CommandSyntaxException {
            List<INBT> list = p_216227_2_.func_218073_a(p_216227_1_, ListNBT::new);
            list.forEach((p_216232_1_) -> {
               if (p_216232_1_ instanceof ListNBT) {
                  p_216227_3_.forEach((p_216231_1_) -> {
                     ((ListNBT)p_216232_1_).add(p_216231_1_.copy());
                  });
               }

            });
         }
      },
      MERGE("merge") {
         public void func_216227_a(INBT p_216227_1_, NBTPathArgument.NBTPath p_216227_2_, List<INBT> p_216227_3_) throws CommandSyntaxException {
            List<INBT> list = p_216227_2_.func_218073_a(p_216227_1_, CompoundNBT::new);
            list.forEach((p_216234_1_) -> {
               if (p_216234_1_ instanceof CompoundNBT) {
                  p_216227_3_.forEach((p_216233_1_) -> {
                     if (p_216233_1_ instanceof CompoundNBT) {
                        ((CompoundNBT)p_216234_1_).merge((CompoundNBT)p_216233_1_);
                     }

                  });
               }

            });
         }
      };

      private final String field_216230_d;

      public abstract void func_216227_a(INBT p_216227_1_, NBTPathArgument.NBTPath p_216227_2_, List<INBT> p_216227_3_) throws CommandSyntaxException;

      private Action(String p_i50670_3_) {
         this.field_216230_d = p_i50670_3_;
      }

      public static CopyNbt.Action func_216229_a(String p_216229_0_) {
         for(CopyNbt.Action copynbt$action : values()) {
            if (copynbt$action.field_216230_d.equals(p_216229_0_)) {
               return copynbt$action;
            }
         }

         throw new IllegalArgumentException("Invalid merge strategy" + p_216229_0_);
      }
   }

   public static class Builder extends LootFunction.Builder<CopyNbt.Builder> {
      private final CopyNbt.Source field_216057_a;
      private final List<CopyNbt.Operation> field_216058_b = Lists.newArrayList();

      private Builder(CopyNbt.Source p_i50675_1_) {
         this.field_216057_a = p_i50675_1_;
      }

      public CopyNbt.Builder func_216055_a(String p_216055_1_, String p_216055_2_, CopyNbt.Action p_216055_3_) {
         this.field_216058_b.add(new CopyNbt.Operation(p_216055_1_, p_216055_2_, p_216055_3_));
         return this;
      }

      public CopyNbt.Builder func_216056_a(String p_216056_1_, String p_216056_2_) {
         return this.func_216055_a(p_216056_1_, p_216056_2_, CopyNbt.Action.REPLACE);
      }

      protected CopyNbt.Builder doCast() {
         return this;
      }

      public ILootFunction build() {
         return new CopyNbt(this.getConditions(), this.field_216057_a, this.field_216058_b);
      }
   }

   static class Operation {
      private final String field_216217_a;
      private final NBTPathArgument.NBTPath field_216218_b;
      private final String field_216219_c;
      private final NBTPathArgument.NBTPath field_216220_d;
      private final CopyNbt.Action field_216221_e;

      private Operation(String p_i50673_1_, String p_i50673_2_, CopyNbt.Action p_i50673_3_) {
         this.field_216217_a = p_i50673_1_;
         this.field_216218_b = CopyNbt.func_215880_b(p_i50673_1_);
         this.field_216219_c = p_i50673_2_;
         this.field_216220_d = CopyNbt.func_215880_b(p_i50673_2_);
         this.field_216221_e = p_i50673_3_;
      }

      public void func_216216_a(Supplier<INBT> p_216216_1_, INBT p_216216_2_) {
         try {
            List<INBT> list = this.field_216218_b.func_218071_a(p_216216_2_);
            if (!list.isEmpty()) {
               this.field_216221_e.func_216227_a(p_216216_1_.get(), this.field_216220_d, list);
            }
         } catch (CommandSyntaxException var4) {
            ;
         }

      }

      public JsonObject func_216214_a() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.addProperty("source", this.field_216217_a);
         jsonobject.addProperty("target", this.field_216219_c);
         jsonobject.addProperty("op", this.field_216221_e.field_216230_d);
         return jsonobject;
      }

      public static CopyNbt.Operation func_216215_a(JsonObject p_216215_0_) {
         String s = JSONUtils.getString(p_216215_0_, "source");
         String s1 = JSONUtils.getString(p_216215_0_, "target");
         CopyNbt.Action copynbt$action = CopyNbt.Action.func_216229_a(JSONUtils.getString(p_216215_0_, "op"));
         return new CopyNbt.Operation(s, s1, copynbt$action);
      }
   }

   public static class Serializer extends LootFunction.Serializer<CopyNbt> {
      public Serializer() {
         super(new ResourceLocation("copy_nbt"), CopyNbt.class);
      }

      public void serialize(JsonObject object, CopyNbt functionClazz, JsonSerializationContext serializationContext) {
         super.serialize(object, functionClazz, serializationContext);
         object.addProperty("source", functionClazz.field_215887_a.field_216224_e);
         JsonArray jsonarray = new JsonArray();
         functionClazz.field_215888_c.stream().map(CopyNbt.Operation::func_216214_a).forEach(jsonarray::add);
         object.add("ops", jsonarray);
      }

      public CopyNbt deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn) {
         CopyNbt.Source copynbt$source = CopyNbt.Source.func_216223_a(JSONUtils.getString(object, "source"));
         List<CopyNbt.Operation> list = Lists.newArrayList();

         for(JsonElement jsonelement : JSONUtils.getJsonArray(object, "ops")) {
            JsonObject jsonobject = JSONUtils.getJsonObject(jsonelement, "op");
            list.add(CopyNbt.Operation.func_216215_a(jsonobject));
         }

         return new CopyNbt(conditionsIn, copynbt$source, list);
      }
   }

   public static enum Source {
      THIS("this", LootParameters.THIS_ENTITY, CopyNbt.field_215889_d),
      KILLER("killer", LootParameters.KILLER_ENTITY, CopyNbt.field_215889_d),
      KILLER_PLAYER("killer_player", LootParameters.LAST_DAMAGE_PLAYER, CopyNbt.field_215889_d),
      BLOCK_ENTITY("block_entity", LootParameters.BLOCK_ENTITY, CopyNbt.field_215890_e);

      public final String field_216224_e;
      public final LootParameter<?> field_216225_f;
      public final Function<LootContext, INBT> field_216226_g;

      private <T> Source(String p_i50672_3_, LootParameter<T> p_i50672_4_, Function<? super T, INBT> p_i50672_5_) {
         this.field_216224_e = p_i50672_3_;
         this.field_216225_f = p_i50672_4_;
         this.field_216226_g = (p_216222_2_) -> {
            T t = p_216222_2_.get(p_i50672_4_);
            return t != null ? p_i50672_5_.apply(t) : null;
         };
      }

      public static CopyNbt.Source func_216223_a(String p_216223_0_) {
         for(CopyNbt.Source copynbt$source : values()) {
            if (copynbt$source.field_216224_e.equals(p_216223_0_)) {
               return copynbt$source;
            }
         }

         throw new IllegalArgumentException("Invalid tag source " + p_216223_0_);
      }
   }
}