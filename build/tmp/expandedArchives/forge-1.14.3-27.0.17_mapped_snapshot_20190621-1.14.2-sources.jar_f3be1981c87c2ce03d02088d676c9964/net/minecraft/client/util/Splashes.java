package net.minecraft.client.util;

import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Splashes extends ReloadListener<List<String>> {
   private static final ResourceLocation field_215278_a = new ResourceLocation("texts/splashes.txt");
   private static final Random RANDOM = new Random();
   private final List<String> field_215280_c = Lists.newArrayList();
   private final Session field_215281_d;

   public Splashes(Session p_i50906_1_) {
      this.field_215281_d = p_i50906_1_;
   }

   /**
    * Performs any reloading that can be done off-thread, such as file IO
    */
   protected List<String> prepare(IResourceManager p_212854_1_, IProfiler p_212854_2_) {
      try (
         IResource iresource = Minecraft.getInstance().getResourceManager().getResource(field_215278_a);
         BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8));
      ) {
         Object object = bufferedreader.lines().map(String::trim).filter((p_215277_0_) -> {
            return p_215277_0_.hashCode() != 125780783;
         }).collect(Collectors.toList());
         return (List<String>)object;
      } catch (IOException var36) {
         return Collections.emptyList();
      }
   }

   /**
    * Performs any reloading that must be done on the main thread, such as uploading textures to the GPU or touching
    * non-threadsafe data
    */
   protected void apply(List<String> p_212853_1_, IResourceManager p_212853_2_, IProfiler p_212853_3_) {
      this.field_215280_c.clear();
      this.field_215280_c.addAll(p_212853_1_);
   }

   @Nullable
   public String func_215276_a() {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(new Date());
      if (calendar.get(2) + 1 == 12 && calendar.get(5) == 24) {
         return "Merry X-mas!";
      } else if (calendar.get(2) + 1 == 1 && calendar.get(5) == 1) {
         return "Happy new year!";
      } else if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31) {
         return "OOoooOOOoooo! Spooky!";
      } else if (this.field_215280_c.isEmpty()) {
         return null;
      } else {
         return this.field_215281_d != null && RANDOM.nextInt(this.field_215280_c.size()) == 42 ? this.field_215281_d.getUsername().toUpperCase(Locale.ROOT) + " IS YOU" : this.field_215280_c.get(RANDOM.nextInt(this.field_215280_c.size()));
      }
   }
}