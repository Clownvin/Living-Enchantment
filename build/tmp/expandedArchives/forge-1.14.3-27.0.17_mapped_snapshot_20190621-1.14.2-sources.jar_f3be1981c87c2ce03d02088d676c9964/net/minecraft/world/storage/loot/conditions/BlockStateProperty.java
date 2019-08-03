package net.minecraft.world.storage.loot.conditions;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.LootParameters;

public class BlockStateProperty implements ILootCondition {
   private final Block block;
   private final Map<IProperty<?>, Object> properties;
   private final Predicate<BlockState> predicate;

   private BlockStateProperty(Block p_i51198_1_, Map<IProperty<?>, Object> p_i51198_2_) {
      this.block = p_i51198_1_;
      this.properties = ImmutableMap.copyOf(p_i51198_2_);
      this.predicate = buildPredicate(p_i51198_1_, p_i51198_2_);
   }

   private static Predicate<BlockState> buildPredicate(Block p_215984_0_, Map<IProperty<?>, Object> p_215984_1_) {
      int i = p_215984_1_.size();
      if (i == 0) {
         return (p_215983_1_) -> {
            return p_215983_1_.getBlock() == p_215984_0_;
         };
      } else if (i == 1) {
         Entry<IProperty<?>, Object> entry1 = p_215984_1_.entrySet().iterator().next();
         IProperty<?> iproperty1 = entry1.getKey();
         Object object1 = entry1.getValue();
         return (p_215987_3_) -> {
            return p_215987_3_.getBlock() == p_215984_0_ && object1.equals(p_215987_3_.get(iproperty1));
         };
      } else {
         Predicate<BlockState> predicate = (p_215986_1_) -> {
            return p_215986_1_.getBlock() == p_215984_0_;
         };

         for(Entry<IProperty<?>, Object> entry : p_215984_1_.entrySet()) {
            IProperty<?> iproperty = entry.getKey();
            Object object = entry.getValue();
            predicate = predicate.and((p_215982_2_) -> {
               return object.equals(p_215982_2_.get(iproperty));
            });
         }

         return predicate;
      }
   }

   public Set<LootParameter<?>> getRequiredParameters() {
      return ImmutableSet.of(LootParameters.BLOCK_STATE);
   }

   public boolean test(LootContext p_test_1_) {
      BlockState blockstate = p_test_1_.get(LootParameters.BLOCK_STATE);
      return blockstate != null && this.predicate.test(blockstate);
   }

   public static BlockStateProperty.Builder builder(Block p_215985_0_) {
      return new BlockStateProperty.Builder(p_215985_0_);
   }

   public static class Builder implements ILootCondition.IBuilder {
      private final Block block;
      private final Set<IProperty<?>> allowedProperties;
      private final Map<IProperty<?>, Object> desiredProperties = Maps.newHashMap();

      public Builder(Block p_i50576_1_) {
         this.block = p_i50576_1_;
         this.allowedProperties = Sets.newIdentityHashSet();
         this.allowedProperties.addAll(p_i50576_1_.getStateContainer().getProperties());
      }

      public <T extends Comparable<T>> BlockStateProperty.Builder with(IProperty<T> p_216299_1_, T p_216299_2_) {
         if (!this.allowedProperties.contains(p_216299_1_)) {
            throw new IllegalArgumentException("Block " + Registry.BLOCK.getKey(this.block) + " does not have property '" + p_216299_1_ + "'");
         } else if (!p_216299_1_.getAllowedValues().contains(p_216299_2_)) {
            throw new IllegalArgumentException("Block " + Registry.BLOCK.getKey(this.block) + " property '" + p_216299_1_ + "' does not have value '" + p_216299_2_ + "'");
         } else {
            this.desiredProperties.put(p_216299_1_, p_216299_2_);
            return this;
         }
      }

      public ILootCondition build() {
         return new BlockStateProperty(this.block, this.desiredProperties);
      }
   }

   public static class Serializer extends ILootCondition.AbstractSerializer<BlockStateProperty> {
      private static <T extends Comparable<T>> String func_216294_a(IProperty<T> p_216294_0_, Object p_216294_1_) {
         return p_216294_0_.getName((T)(p_216294_1_));
      }

      protected Serializer() {
         super(new ResourceLocation("block_state_property"), BlockStateProperty.class);
      }

      public void serialize(JsonObject json, BlockStateProperty value, JsonSerializationContext context) {
         json.addProperty("block", Registry.BLOCK.getKey(value.block).toString());
         JsonObject jsonobject = new JsonObject();
         value.properties.forEach((p_216295_1_, p_216295_2_) -> {
            jsonobject.addProperty(p_216295_1_.getName(), func_216294_a(p_216295_1_, p_216295_2_));
         });
         json.add("properties", jsonobject);
      }

      public BlockStateProperty deserialize(JsonObject json, JsonDeserializationContext context) {
         ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(json, "block"));
         Block block = Registry.BLOCK.getValue(resourcelocation).orElseThrow(() -> {
            return new IllegalArgumentException("Can't find block " + resourcelocation);
         });
         StateContainer<Block, BlockState> statecontainer = block.getStateContainer();
         Map<IProperty<?>, Object> map = Maps.newHashMap();
         if (json.has("properties")) {
            JsonObject jsonobject = JSONUtils.getJsonObject(json, "properties");
            jsonobject.entrySet().forEach((p_216292_3_) -> {
               String s = p_216292_3_.getKey();
               IProperty<?> iproperty = statecontainer.getProperty(s);
               if (iproperty == null) {
                  throw new IllegalArgumentException("Block " + Registry.BLOCK.getKey(block) + " does not have property '" + s + "'");
               } else {
                  String s1 = JSONUtils.getString(p_216292_3_.getValue(), "value");
                  Object object = iproperty.parseValue(s1).orElseThrow(() -> {
                     return new IllegalArgumentException("Block " + Registry.BLOCK.getKey(block) + " property '" + s + "' does not have value '" + s1 + "'");
                  });
                  map.put(iproperty, object);
               }
            });
         }

         return new BlockStateProperty(block, map);
      }
   }
}