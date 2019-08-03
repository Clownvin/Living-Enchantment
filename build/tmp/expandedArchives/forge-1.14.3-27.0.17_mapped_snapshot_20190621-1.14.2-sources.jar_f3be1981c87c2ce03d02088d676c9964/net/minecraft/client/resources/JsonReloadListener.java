package net.minecraft.client.resources;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class JsonReloadListener extends ReloadListener<Map<ResourceLocation, JsonObject>> {
   private static final Logger field_223380_a = LogManager.getLogger();
   private static final int field_223381_b = ".json".length();
   private final Gson field_223382_c;
   private final String field_223383_d;

   public JsonReloadListener(Gson p_i51536_1_, String p_i51536_2_) {
      this.field_223382_c = p_i51536_1_;
      this.field_223383_d = p_i51536_2_;
   }

   /**
    * Performs any reloading that can be done off-thread, such as file IO
    */
   protected Map<ResourceLocation, JsonObject> prepare(IResourceManager p_212854_1_, IProfiler p_212854_2_) {
      Map<ResourceLocation, JsonObject> map = Maps.newHashMap();
      int i = this.field_223383_d.length() + 1;

      for(ResourceLocation resourcelocation : p_212854_1_.getAllResourceLocations(this.field_223383_d, (p_223379_0_) -> {
         return p_223379_0_.endsWith(".json");
      })) {
         String s = resourcelocation.getPath();
         ResourceLocation resourcelocation1 = new ResourceLocation(resourcelocation.getNamespace(), s.substring(i, s.length() - field_223381_b));

         try (
            IResource iresource = p_212854_1_.getResource(resourcelocation);
            InputStream inputstream = iresource.getInputStream();
            Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));
         ) {
            JsonObject jsonobject = JSONUtils.fromJson(this.field_223382_c, reader, JsonObject.class);
            if (jsonobject != null) {
               JsonObject jsonobject1 = map.put(resourcelocation1, jsonobject);
               if (jsonobject1 != null) {
                  throw new IllegalStateException("Duplicate data file ignored with ID " + resourcelocation1);
               }
            } else {
               field_223380_a.error("Couldn't load data file {} from {} as it's null or empty", resourcelocation1, resourcelocation);
            }
         } catch (IllegalArgumentException | IOException | JsonParseException jsonparseexception) {
            field_223380_a.error("Couldn't parse data file {} from {}", resourcelocation1, resourcelocation, jsonparseexception);
         }
      }

      return map;
   }

   protected ResourceLocation getPreparedPath(ResourceLocation rl) {
       return new ResourceLocation(rl.getNamespace(), this.field_223383_d + "/" + rl.getPath() + ".json");
   }
}