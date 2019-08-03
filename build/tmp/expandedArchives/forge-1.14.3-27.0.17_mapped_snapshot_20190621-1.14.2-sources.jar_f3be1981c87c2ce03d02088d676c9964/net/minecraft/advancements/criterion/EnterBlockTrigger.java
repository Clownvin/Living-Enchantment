package net.minecraft.advancements.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

public class EnterBlockTrigger implements ICriterionTrigger<EnterBlockTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("enter_block");
   private final Map<PlayerAdvancements, EnterBlockTrigger.Listeners> listeners = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<EnterBlockTrigger.Instance> listener) {
      EnterBlockTrigger.Listeners enterblocktrigger$listeners = this.listeners.get(playerAdvancementsIn);
      if (enterblocktrigger$listeners == null) {
         enterblocktrigger$listeners = new EnterBlockTrigger.Listeners(playerAdvancementsIn);
         this.listeners.put(playerAdvancementsIn, enterblocktrigger$listeners);
      }

      enterblocktrigger$listeners.add(listener);
   }

   public void removeListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<EnterBlockTrigger.Instance> listener) {
      EnterBlockTrigger.Listeners enterblocktrigger$listeners = this.listeners.get(playerAdvancementsIn);
      if (enterblocktrigger$listeners != null) {
         enterblocktrigger$listeners.remove(listener);
         if (enterblocktrigger$listeners.isEmpty()) {
            this.listeners.remove(playerAdvancementsIn);
         }
      }

   }

   public void removeAllListeners(PlayerAdvancements playerAdvancementsIn) {
      this.listeners.remove(playerAdvancementsIn);
   }

   /**
    * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
    */
   public EnterBlockTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      Block block = null;
      if (json.has("block")) {
         ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(json, "block"));
         block = Registry.BLOCK.getValue(resourcelocation).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown block type '" + resourcelocation + "'");
         });
      }

      Map<IProperty<?>, Object> map = null;
      if (json.has("state")) {
         if (block == null) {
            throw new JsonSyntaxException("Can't define block state without a specific block type");
         }

         StateContainer<Block, BlockState> statecontainer = block.getStateContainer();

         for(Entry<String, JsonElement> entry : JSONUtils.getJsonObject(json, "state").entrySet()) {
            IProperty<?> iproperty = statecontainer.getProperty(entry.getKey());
            if (iproperty == null) {
               throw new JsonSyntaxException("Unknown block state property '" + (String)entry.getKey() + "' for block '" + Registry.BLOCK.getKey(block) + "'");
            }

            String s = JSONUtils.getString(entry.getValue(), entry.getKey());
            Optional<?> optional = iproperty.parseValue(s);
            if (!optional.isPresent()) {
               throw new JsonSyntaxException("Invalid block state value '" + s + "' for property '" + (String)entry.getKey() + "' on block '" + Registry.BLOCK.getKey(block) + "'");
            }

            if (map == null) {
               map = Maps.newHashMap();
            }

            map.put(iproperty, optional.get());
         }
      }

      return new EnterBlockTrigger.Instance(block, map);
   }

   public void trigger(ServerPlayerEntity player, BlockState state) {
      EnterBlockTrigger.Listeners enterblocktrigger$listeners = this.listeners.get(player.getAdvancements());
      if (enterblocktrigger$listeners != null) {
         enterblocktrigger$listeners.trigger(state);
      }

   }

   public static class Instance extends CriterionInstance {
      private final Block block;
      private final Map<IProperty<?>, Object> properties;

      public Instance(@Nullable Block blockIn, @Nullable Map<IProperty<?>, Object> propertiesIn) {
         super(EnterBlockTrigger.ID);
         this.block = blockIn;
         this.properties = propertiesIn;
      }

      public static EnterBlockTrigger.Instance forBlock(Block p_203920_0_) {
         return new EnterBlockTrigger.Instance(p_203920_0_, (Map<IProperty<?>, Object>)null);
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         if (this.block != null) {
            jsonobject.addProperty("block", Registry.BLOCK.getKey(this.block).toString());
            if (this.properties != null && !this.properties.isEmpty()) {
               JsonObject jsonobject1 = new JsonObject();

               for(Entry<IProperty<?>, ?> entry : this.properties.entrySet()) {
                  jsonobject1.addProperty(entry.getKey().getName(), Util.getValueName(entry.getKey(), entry.getValue()));
               }

               jsonobject.add("state", jsonobject1);
            }
         }

         return jsonobject;
      }

      public boolean test(BlockState state) {
         if (this.block != null && state.getBlock() != this.block) {
            return false;
         } else {
            if (this.properties != null) {
               for(Entry<IProperty<?>, Object> entry : this.properties.entrySet()) {
                  if (state.get(entry.getKey()) != entry.getValue()) {
                     return false;
                  }
               }
            }

            return true;
         }
      }
   }

   static class Listeners {
      private final PlayerAdvancements playerAdvancements;
      private final Set<ICriterionTrigger.Listener<EnterBlockTrigger.Instance>> listeners = Sets.newHashSet();

      public Listeners(PlayerAdvancements playerAdvancementsIn) {
         this.playerAdvancements = playerAdvancementsIn;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void add(ICriterionTrigger.Listener<EnterBlockTrigger.Instance> listener) {
         this.listeners.add(listener);
      }

      public void remove(ICriterionTrigger.Listener<EnterBlockTrigger.Instance> listener) {
         this.listeners.remove(listener);
      }

      public void trigger(BlockState state) {
         List<ICriterionTrigger.Listener<EnterBlockTrigger.Instance>> list = null;

         for(ICriterionTrigger.Listener<EnterBlockTrigger.Instance> listener : this.listeners) {
            if (listener.getCriterionInstance().test(state)) {
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(ICriterionTrigger.Listener<EnterBlockTrigger.Instance> listener1 : list) {
               listener1.grantCriterion(this.playerAdvancements);
            }
         }

      }
   }
}