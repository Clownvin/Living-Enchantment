package net.minecraft.world.storage.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;

public class LocationCheck implements ILootCondition {
   private final LocationPredicate predicate;

   private LocationCheck(LocationPredicate predicate) {
      this.predicate = predicate;
   }

   public boolean test(LootContext p_test_1_) {
      BlockPos blockpos = p_test_1_.get(LootParameters.POSITION);
      return blockpos != null && this.predicate.test(p_test_1_.getWorld(), (float)blockpos.getX(), (float)blockpos.getY(), (float)blockpos.getZ());
   }

   public static ILootCondition.IBuilder builder(LocationPredicate.Builder p_215975_0_) {
      return () -> {
         return new LocationCheck(p_215975_0_.build());
      };
   }

   public static class Serializer extends ILootCondition.AbstractSerializer<LocationCheck> {
      public Serializer() {
         super(new ResourceLocation("location_check"), LocationCheck.class);
      }

      public void serialize(JsonObject json, LocationCheck value, JsonSerializationContext context) {
         json.add("predicate", value.predicate.serialize());
      }

      public LocationCheck deserialize(JsonObject json, JsonDeserializationContext context) {
         LocationPredicate locationpredicate = LocationPredicate.deserialize(json.get("predicate"));
         return new LocationCheck(locationpredicate);
      }
   }
}