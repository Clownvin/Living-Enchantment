package net.minecraft.client.gui.fonts;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.fonts.providers.DefaultGlyphProvider;
import net.minecraft.client.gui.fonts.providers.GlyphProviderTypes;
import net.minecraft.client.gui.fonts.providers.IGlyphProvider;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.EmptyProfiler;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class FontResourceManager implements AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map<ResourceLocation, FontRenderer> fontRenderers = Maps.newHashMap();
   private final Set<IGlyphProvider> field_216888_c = Sets.newHashSet();
   private final TextureManager textureManager;
   private boolean forceUnicodeFont;
   private final IFutureReloadListener field_216889_f = new ReloadListener<Map<ResourceLocation, List<IGlyphProvider>>>() {
      /**
       * Performs any reloading that can be done off-thread, such as file IO
       */
      protected Map<ResourceLocation, List<IGlyphProvider>> prepare(IResourceManager p_212854_1_, IProfiler p_212854_2_) {
         p_212854_2_.startTick();
         Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
         Map<ResourceLocation, List<IGlyphProvider>> map = Maps.newHashMap();

         for(ResourceLocation resourcelocation : p_212854_1_.getAllResourceLocations("font", (p_215274_0_) -> {
            return p_215274_0_.endsWith(".json");
         })) {
            String s = resourcelocation.getPath();
            ResourceLocation resourcelocation1 = new ResourceLocation(resourcelocation.getNamespace(), s.substring("font/".length(), s.length() - ".json".length()));
            List<IGlyphProvider> list = map.computeIfAbsent(resourcelocation1, (p_215272_0_) -> {
               return Lists.newArrayList(new DefaultGlyphProvider());
            });
            p_212854_2_.startSection(resourcelocation1::toString);

            try {
               for(IResource iresource : p_212854_1_.getAllResources(resourcelocation)) {
                  p_212854_2_.startSection(iresource::getPackName);

                  try (
                     InputStream inputstream = iresource.getInputStream();
                     Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));
                  ) {
                     p_212854_2_.startSection("reading");
                     JsonArray jsonarray = JSONUtils.getJsonArray(JSONUtils.fromJson(gson, reader, JsonObject.class), "providers");
                     p_212854_2_.endStartSection("parsing");

                     for(int i = jsonarray.size() - 1; i >= 0; --i) {
                        JsonObject jsonobject = JSONUtils.getJsonObject(jsonarray.get(i), "providers[" + i + "]");

                        try {
                           String s1 = JSONUtils.getString(jsonobject, "type");
                           GlyphProviderTypes glyphprovidertypes = GlyphProviderTypes.byName(s1);
                           if (!FontResourceManager.this.forceUnicodeFont || glyphprovidertypes == GlyphProviderTypes.LEGACY_UNICODE || !resourcelocation1.equals(Minecraft.DEFAULT_FONT_RENDERER_NAME)) {
                              p_212854_2_.startSection(s1);
                              list.add(glyphprovidertypes.getFactory(jsonobject).create(p_212854_1_));
                              p_212854_2_.endSection();
                           }
                        } catch (RuntimeException runtimeexception) {
                           FontResourceManager.LOGGER.warn("Unable to read definition '{}' in fonts.json in resourcepack: '{}': {}", resourcelocation1, iresource.getPackName(), runtimeexception.getMessage());
                        }
                     }

                     p_212854_2_.endSection();
                  } catch (RuntimeException runtimeexception1) {
                     FontResourceManager.LOGGER.warn("Unable to load font '{}' in fonts.json in resourcepack: '{}': {}", resourcelocation1, iresource.getPackName(), runtimeexception1.getMessage());
                  }

                  p_212854_2_.endSection();
               }
            } catch (IOException ioexception) {
               FontResourceManager.LOGGER.warn("Unable to load font '{}' in fonts.json: {}", resourcelocation1, ioexception.getMessage());
            }

            p_212854_2_.startSection("caching");

            for(char c0 = 0; c0 < '\uffff'; ++c0) {
               if (c0 != ' ') {
                  for(IGlyphProvider iglyphprovider : Lists.reverse(list)) {
                     if (iglyphprovider.func_212248_a(c0) != null) {
                        break;
                     }
                  }
               }
            }

            p_212854_2_.endSection();
            p_212854_2_.endSection();
         }

         p_212854_2_.endTick();
         return map;
      }

      /**
       * Performs any reloading that must be done on the main thread, such as uploading textures to the GPU or touching
       * non-threadsafe data
       */
      protected void apply(Map<ResourceLocation, List<IGlyphProvider>> p_212853_1_, IResourceManager p_212853_2_, IProfiler p_212853_3_) {
         p_212853_3_.startTick();
         p_212853_3_.startSection("reloading");
         Stream.concat(FontResourceManager.this.fontRenderers.keySet().stream(), p_212853_1_.keySet().stream()).distinct().forEach((p_215271_2_) -> {
            List<IGlyphProvider> list = p_212853_1_.getOrDefault(p_215271_2_, Collections.emptyList());
            Collections.reverse(list);
            FontResourceManager.this.fontRenderers.computeIfAbsent(p_215271_2_, (p_215273_1_) -> {
               return new FontRenderer(FontResourceManager.this.textureManager, new Font(FontResourceManager.this.textureManager, p_215273_1_));
            }).setGlyphProviders(list);
         });
         Collection<List<IGlyphProvider>> collection = p_212853_1_.values();
         Set set = FontResourceManager.this.field_216888_c;
         collection.forEach(set::addAll);
         p_212853_3_.endSection();
         p_212853_3_.endTick();
      }
   };

   public FontResourceManager(TextureManager textureManagerIn, boolean forceUnicodeFontIn) {
      this.textureManager = textureManagerIn;
      this.forceUnicodeFont = forceUnicodeFontIn;
   }

   @Nullable
   public FontRenderer getFontRenderer(ResourceLocation id) {
      return this.fontRenderers.computeIfAbsent(id, (p_212318_1_) -> {
         FontRenderer fontrenderer = new FontRenderer(this.textureManager, new Font(this.textureManager, p_212318_1_));
         fontrenderer.setGlyphProviders(Lists.newArrayList(new DefaultGlyphProvider()));
         return fontrenderer;
      });
   }

   public void func_216883_a(boolean p_216883_1_, Executor p_216883_2_, Executor p_216883_3_) {
      if (p_216883_1_ != this.forceUnicodeFont) {
         this.forceUnicodeFont = p_216883_1_;
         IResourceManager iresourcemanager = Minecraft.getInstance().getResourceManager();
         IFutureReloadListener.IStage ifuturereloadlistener$istage = new IFutureReloadListener.IStage() {
            public <T> CompletableFuture<T> markCompleteAwaitingOthers(T backgroundResult) {
               return CompletableFuture.completedFuture(backgroundResult);
            }
         };
         this.field_216889_f.reload(ifuturereloadlistener$istage, iresourcemanager, EmptyProfiler.INSTANCE, EmptyProfiler.INSTANCE, p_216883_2_, p_216883_3_);
      }
   }

   public IFutureReloadListener func_216884_a() {
      return this.field_216889_f;
   }

   public void close() {
      this.fontRenderers.values().forEach(FontRenderer::close);
      this.field_216888_c.forEach(IGlyphProvider::close);
   }
}