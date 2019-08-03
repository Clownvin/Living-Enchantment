package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.TextureUtil;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class TextureManager implements ITickable, IFutureReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final ResourceLocation RESOURCE_LOCATION_EMPTY = new ResourceLocation("");
   private final Map<ResourceLocation, ITextureObject> mapTextureObjects = Maps.newHashMap();
   private final List<ITickable> listTickables = Lists.newArrayList();
   private final Map<String, Integer> mapTextureCounters = Maps.newHashMap();
   private final IResourceManager resourceManager;

   public TextureManager(IResourceManager resourceManager) {
      this.resourceManager = resourceManager;
   }

   public void bindTexture(ResourceLocation resource) {
      ITextureObject itextureobject = this.mapTextureObjects.get(resource);
      if (itextureobject == null) {
         itextureobject = new SimpleTexture(resource);
         this.loadTexture(resource, itextureobject);
      }

      itextureobject.bindTexture();
   }

   public boolean loadTickableTexture(ResourceLocation textureLocation, ITickableTextureObject textureObj) {
      if (this.loadTexture(textureLocation, textureObj)) {
         this.listTickables.add(textureObj);
         return true;
      } else {
         return false;
      }
   }

   public boolean loadTexture(ResourceLocation textureLocation, ITextureObject textureObj) {
      boolean flag = true;

      try {
         textureObj.loadTexture(this.resourceManager);
      } catch (IOException ioexception) {
         if (textureLocation != RESOURCE_LOCATION_EMPTY) {
            LOGGER.warn("Failed to load texture: {}", textureLocation, ioexception);
         }

         textureObj = MissingTextureSprite.getDynamicTexture();
         this.mapTextureObjects.put(textureLocation, textureObj);
         flag = false;
      } catch (Throwable throwable) {
         ITextureObject p_110579_2_f = textureObj;
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Registering texture");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Resource location being registered");
         crashreportcategory.addDetail("Resource location", textureLocation);
         crashreportcategory.addDetail("Texture object class", () -> {
            return p_110579_2_f.getClass().getName();
         });
         throw new ReportedException(crashreport);
      }

      this.mapTextureObjects.put(textureLocation, textureObj);
      return flag;
   }

   public ITextureObject getTexture(ResourceLocation textureLocation) {
      return this.mapTextureObjects.get(textureLocation);
   }

   public ResourceLocation getDynamicTextureLocation(String name, DynamicTexture texture) {
      Integer integer = this.mapTextureCounters.get(name);
      if (integer == null) {
         integer = 1;
      } else {
         integer = integer + 1;
      }

      this.mapTextureCounters.put(name, integer);
      ResourceLocation resourcelocation = new ResourceLocation(String.format("dynamic/%s_%d", name, integer));
      this.loadTexture(resourcelocation, texture);
      return resourcelocation;
   }

   public CompletableFuture<Void> loadAsync(ResourceLocation textureLocation, Executor executor) {
      if (!this.mapTextureObjects.containsKey(textureLocation)) {
         PreloadedTexture preloadedtexture = new PreloadedTexture(this.resourceManager, textureLocation, executor);
         this.mapTextureObjects.put(textureLocation, preloadedtexture);
         return preloadedtexture.func_215248_a().thenRunAsync(() -> {
            this.loadTexture(textureLocation, preloadedtexture);
         }, Minecraft.getInstance());
      } else {
         return CompletableFuture.completedFuture((Void)null);
      }
   }

   public void tick() {
      for(ITickable itickable : this.listTickables) {
         itickable.tick();
      }

   }

   public void deleteTexture(ResourceLocation textureLocation) {
      ITextureObject itextureobject = this.getTexture(textureLocation);
      if (itextureobject != null) {
         this.mapTextureObjects.remove(textureLocation); // Forge: fix MC-98707
         TextureUtil.releaseTextureId(itextureobject.getGlTextureId());
      }

   }

   public CompletableFuture<Void> reload(IFutureReloadListener.IStage stage, IResourceManager resourceManager, IProfiler preparationsProfiler, IProfiler reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
      return CompletableFuture.allOf(MainMenuScreen.func_213097_a(this, backgroundExecutor), this.loadAsync(Widget.WIDGETS_LOCATION, backgroundExecutor)).<Void>thenCompose(stage::markCompleteAwaitingOthers).thenAcceptAsync((p_215266_3_) -> {
         MissingTextureSprite.getDynamicTexture();
         Iterator<Entry<ResourceLocation, ITextureObject>> iterator = this.mapTextureObjects.entrySet().iterator();

         while(iterator.hasNext()) {
            Entry<ResourceLocation, ITextureObject> entry = iterator.next();
            ResourceLocation resourcelocation = entry.getKey();
            ITextureObject itextureobject = entry.getValue();
            if (itextureobject == MissingTextureSprite.getDynamicTexture() && !resourcelocation.equals(MissingTextureSprite.getLocation())) {
               iterator.remove();
            } else {
               itextureobject.func_215244_a(this, resourceManager, resourcelocation, gameExecutor);
            }
         }

      }, gameExecutor);
   }
}