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
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

public class ShotCrossbowTrigger implements ICriterionTrigger<ShotCrossbowTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("shot_crossbow");
   private final Map<PlayerAdvancements, ShotCrossbowTrigger.Listeners> field_215113_b = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<ShotCrossbowTrigger.Instance> listener) {
      ShotCrossbowTrigger.Listeners shotcrossbowtrigger$listeners = this.field_215113_b.get(playerAdvancementsIn);
      if (shotcrossbowtrigger$listeners == null) {
         shotcrossbowtrigger$listeners = new ShotCrossbowTrigger.Listeners(playerAdvancementsIn);
         this.field_215113_b.put(playerAdvancementsIn, shotcrossbowtrigger$listeners);
      }

      shotcrossbowtrigger$listeners.func_218025_a(listener);
   }

   public void removeListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<ShotCrossbowTrigger.Instance> listener) {
      ShotCrossbowTrigger.Listeners shotcrossbowtrigger$listeners = this.field_215113_b.get(playerAdvancementsIn);
      if (shotcrossbowtrigger$listeners != null) {
         shotcrossbowtrigger$listeners.func_218023_b(listener);
         if (shotcrossbowtrigger$listeners.func_218024_a()) {
            this.field_215113_b.remove(playerAdvancementsIn);
         }
      }

   }

   public void removeAllListeners(PlayerAdvancements playerAdvancementsIn) {
      this.field_215113_b.remove(playerAdvancementsIn);
   }

   /**
    * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
    */
   public ShotCrossbowTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      ItemPredicate itempredicate = ItemPredicate.deserialize(json.get("item"));
      return new ShotCrossbowTrigger.Instance(itempredicate);
   }

   public void func_215111_a(ServerPlayerEntity p_215111_1_, ItemStack p_215111_2_) {
      ShotCrossbowTrigger.Listeners shotcrossbowtrigger$listeners = this.field_215113_b.get(p_215111_1_.getAdvancements());
      if (shotcrossbowtrigger$listeners != null) {
         shotcrossbowtrigger$listeners.func_218026_a(p_215111_2_);
      }

   }

   public static class Instance extends CriterionInstance {
      private final ItemPredicate field_215123_a;

      public Instance(ItemPredicate p_i50604_1_) {
         super(ShotCrossbowTrigger.ID);
         this.field_215123_a = p_i50604_1_;
      }

      public static ShotCrossbowTrigger.Instance func_215122_a(IItemProvider p_215122_0_) {
         return new ShotCrossbowTrigger.Instance(ItemPredicate.Builder.create().item(p_215122_0_).build());
      }

      public boolean func_215121_a(ItemStack p_215121_1_) {
         return this.field_215123_a.test(p_215121_1_);
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("item", this.field_215123_a.serialize());
         return jsonobject;
      }
   }

   static class Listeners {
      private final PlayerAdvancements field_218027_a;
      private final Set<ICriterionTrigger.Listener<ShotCrossbowTrigger.Instance>> field_218028_b = Sets.newHashSet();

      public Listeners(PlayerAdvancements p_i50606_1_) {
         this.field_218027_a = p_i50606_1_;
      }

      public boolean func_218024_a() {
         return this.field_218028_b.isEmpty();
      }

      public void func_218025_a(ICriterionTrigger.Listener<ShotCrossbowTrigger.Instance> p_218025_1_) {
         this.field_218028_b.add(p_218025_1_);
      }

      public void func_218023_b(ICriterionTrigger.Listener<ShotCrossbowTrigger.Instance> p_218023_1_) {
         this.field_218028_b.remove(p_218023_1_);
      }

      public void func_218026_a(ItemStack p_218026_1_) {
         List<ICriterionTrigger.Listener<ShotCrossbowTrigger.Instance>> list = null;

         for(ICriterionTrigger.Listener<ShotCrossbowTrigger.Instance> listener : this.field_218028_b) {
            if (listener.getCriterionInstance().func_215121_a(p_218026_1_)) {
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(ICriterionTrigger.Listener<ShotCrossbowTrigger.Instance> listener1 : list) {
               listener1.grantCriterion(this.field_218027_a);
            }
         }

      }
   }
}