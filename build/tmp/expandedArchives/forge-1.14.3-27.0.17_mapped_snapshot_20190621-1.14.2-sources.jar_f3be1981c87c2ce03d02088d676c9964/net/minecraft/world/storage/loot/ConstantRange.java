package net.minecraft.world.storage.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Random;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public final class ConstantRange implements IRandomRange {
   private final int value;

   public ConstantRange(int value) {
      this.value = value;
   }

   public int generateInt(Random rand) {
      return this.value;
   }

   public ResourceLocation func_215830_a() {
      return CONSTANT;
   }

   public static ConstantRange func_215835_a(int p_215835_0_) {
      return new ConstantRange(p_215835_0_);
   }

   public static class Serializer implements JsonDeserializer<ConstantRange>, JsonSerializer<ConstantRange> {
      public ConstantRange deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         return new ConstantRange(JSONUtils.getInt(p_deserialize_1_, "value"));
      }

      public JsonElement serialize(ConstantRange p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         return new JsonPrimitive((int)p_serialize_1_.value);
      }
   }
}