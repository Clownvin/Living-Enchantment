package net.minecraft.advancements.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class VillagerTradeTrigger implements ICriterionTrigger<VillagerTradeTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("villager_trade");
   private final Map<PlayerAdvancements, VillagerTradeTrigger.Listeners> listeners = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<VillagerTradeTrigger.Instance> listener) {
      VillagerTradeTrigger.Listeners villagertradetrigger$listeners = this.listeners.get(playerAdvancementsIn);
      if (villagertradetrigger$listeners == null) {
         villagertradetrigger$listeners = new VillagerTradeTrigger.Listeners(playerAdvancementsIn);
         this.listeners.put(playerAdvancementsIn, villagertradetrigger$listeners);
      }

      villagertradetrigger$listeners.add(listener);
   }

   public void removeListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<VillagerTradeTrigger.Instance> listener) {
      VillagerTradeTrigger.Listeners villagertradetrigger$listeners = this.listeners.get(playerAdvancementsIn);
      if (villagertradetrigger$listeners != null) {
         villagertradetrigger$listeners.remove(listener);
         if (villagertradetrigger$listeners.isEmpty()) {
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
   public VillagerTradeTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      EntityPredicate entitypredicate = EntityPredicate.deserialize(json.get("villager"));
      ItemPredicate itempredicate = ItemPredicate.deserialize(json.get("item"));
      return new VillagerTradeTrigger.Instance(entitypredicate, itempredicate);
   }

   public void func_215114_a(ServerPlayerEntity p_215114_1_, AbstractVillagerEntity p_215114_2_, ItemStack p_215114_3_) {
      VillagerTradeTrigger.Listeners villagertradetrigger$listeners = this.listeners.get(p_215114_1_.getAdvancements());
      if (villagertradetrigger$listeners != null) {
         villagertradetrigger$listeners.func_218029_a(p_215114_1_, p_215114_2_, p_215114_3_);
      }

   }

   public static class Instance extends CriterionInstance {
      private final EntityPredicate villager;
      private final ItemPredicate item;

      public Instance(EntityPredicate villager, ItemPredicate item) {
         super(VillagerTradeTrigger.ID);
         this.villager = villager;
         this.item = item;
      }

      public static VillagerTradeTrigger.Instance any() {
         return new VillagerTradeTrigger.Instance(EntityPredicate.ANY, ItemPredicate.ANY);
      }

      public boolean func_215125_a(ServerPlayerEntity p_215125_1_, AbstractVillagerEntity p_215125_2_, ItemStack p_215125_3_) {
         if (!this.villager.test(p_215125_1_, p_215125_2_)) {
            return false;
         } else {
            return this.item.test(p_215125_3_);
         }
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("item", this.item.serialize());
         jsonobject.add("villager", this.villager.serialize());
         return jsonobject;
      }
   }

   static class Listeners {
      private final PlayerAdvancements playerAdvancements;
      private final Set<ICriterionTrigger.Listener<VillagerTradeTrigger.Instance>> listeners = Sets.newHashSet();

      public Listeners(PlayerAdvancements playerAdvancementsIn) {
         this.playerAdvancements = playerAdvancementsIn;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void add(ICriterionTrigger.Listener<VillagerTradeTrigger.Instance> listener) {
         this.listeners.add(listener);
      }

      public void remove(ICriterionTrigger.Listener<VillagerTradeTrigger.Instance> listener) {
         this.listeners.remove(listener);
      }

      public void func_218029_a(ServerPlayerEntity p_218029_1_, AbstractVillagerEntity p_218029_2_, ItemStack p_218029_3_) {
         List<ICriterionTrigger.Listener<VillagerTradeTrigger.Instance>> list = null;

         for(ICriterionTrigger.Listener<VillagerTradeTrigger.Instance> listener : this.listeners) {
            if (listener.getCriterionInstance().func_215125_a(p_218029_1_, p_218029_2_, p_218029_3_)) {
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(ICriterionTrigger.Listener<VillagerTradeTrigger.Instance> listener1 : list) {
               listener1.grantCriterion(this.playerAdvancements);
            }
         }

      }
   }
}