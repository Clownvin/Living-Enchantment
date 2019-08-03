package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.TextureUtil;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.StitcherException;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class AtlasTexture extends Texture implements ITickableTextureObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final ResourceLocation LOCATION_BLOCKS_TEXTURE = new ResourceLocation("textures/atlas/blocks.png");
   public static final ResourceLocation LOCATION_PARTICLES_TEXTURE = new ResourceLocation("textures/atlas/particles.png");
   public static final ResourceLocation LOCATION_PAINTINGS_TEXTURE = new ResourceLocation("textures/atlas/paintings.png");
   public static final ResourceLocation LOCATION_EFFECTS_TEXTURE = new ResourceLocation("textures/atlas/mob_effects.png");
   private final List<TextureAtlasSprite> listAnimatedSprites = Lists.newArrayList();
   private final Set<ResourceLocation> sprites = Sets.newHashSet();
   private final Map<ResourceLocation, TextureAtlasSprite> mapUploadedSprites = Maps.newHashMap();
   private final String basePath;
   private final int field_215265_o;
   private int mipmapLevels;
   private final TextureAtlasSprite missingImage = MissingTextureSprite.func_217790_a();

   public AtlasTexture(String basePathIn) {
      this.basePath = basePathIn;
      this.field_215265_o = Minecraft.getGLMaximumTextureSize();
   }

   public void loadTexture(IResourceManager manager) throws IOException {
   }

   public void upload(AtlasTexture.SheetData p_215260_1_) {
      this.sprites.clear();
      this.sprites.addAll(p_215260_1_.field_217805_a);
      LOGGER.info("Created: {}x{} {}-atlas", p_215260_1_.field_217806_b, p_215260_1_.field_217807_c, this.basePath);
      TextureUtil.prepareImage(this.getGlTextureId(), this.mipmapLevels, p_215260_1_.field_217806_b, p_215260_1_.field_217807_c);
      this.clear();

      for(TextureAtlasSprite textureatlassprite : p_215260_1_.field_217808_d) {
         this.mapUploadedSprites.put(textureatlassprite.getName(), textureatlassprite);

         try {
            textureatlassprite.uploadMipmaps();
         } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Stitching texture atlas");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Texture being stitched together");
            crashreportcategory.addDetail("Atlas path", this.basePath);
            crashreportcategory.addDetail("Sprite", textureatlassprite);
            throw new ReportedException(crashreport);
         }

         if (textureatlassprite.hasAnimationMetadata()) {
            this.listAnimatedSprites.add(textureatlassprite);
         }
      }

      net.minecraftforge.client.ForgeHooksClient.onTextureStitchedPost(this);
   }

   public AtlasTexture.SheetData stitch(IResourceManager p_215254_1_, Iterable<ResourceLocation> p_215254_2_, IProfiler p_215254_3_) {
      Set<ResourceLocation> set = Sets.newHashSet();
      net.minecraftforge.client.ForgeHooksClient.onTextureStitchedPre(this);
      p_215254_3_.startSection("preparing");
      p_215254_2_.forEach((p_215253_1_) -> {
         if (p_215253_1_ == null) {
            throw new IllegalArgumentException("Location cannot be null!");
         } else {
            set.add(p_215253_1_);
         }
      });
      int i = this.field_215265_o;
      Stitcher stitcher = new Stitcher(i, i, this.mipmapLevels);
      int j = Integer.MAX_VALUE;
      int k = 1 << this.mipmapLevels;
      p_215254_3_.endStartSection("extracting_frames");

      for(TextureAtlasSprite textureatlassprite : this.func_215256_a(p_215254_1_, set)) {
         j = Math.min(j, Math.min(textureatlassprite.getWidth(), textureatlassprite.getHeight()));
         int l = Math.min(Integer.lowestOneBit(textureatlassprite.getWidth()), Integer.lowestOneBit(textureatlassprite.getHeight()));
         if (l < k) {
            LOGGER.warn("Texture {} with size {}x{} limits mip level from {} to {}", textureatlassprite.getName(), textureatlassprite.getWidth(), textureatlassprite.getHeight(), MathHelper.log2(k), MathHelper.log2(l));
            k = l;
         }

         stitcher.addSprite(textureatlassprite);
      }

      int i1 = Math.min(j, k);
      int j1 = MathHelper.log2(i1);
      if (false) // FORGE: do not lower the mipmap level
      if (j1 < this.mipmapLevels) {
         LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", this.basePath, this.mipmapLevels, j1, i1);
         this.mipmapLevels = j1;
      }

      p_215254_3_.endStartSection("mipmapping");
      this.missingImage.generateMipmaps(this.mipmapLevels);
      p_215254_3_.endStartSection("register");
      stitcher.addSprite(this.missingImage);
      p_215254_3_.endStartSection("stitching");

      try {
         stitcher.doStitch();
      } catch (StitcherException stitcherexception) {
         throw stitcherexception;
      }

      p_215254_3_.endStartSection("loading");
      List<TextureAtlasSprite> list = this.func_215259_a(p_215254_1_, stitcher);
      p_215254_3_.endSection();
      return new AtlasTexture.SheetData(set, stitcher.getCurrentWidth(), stitcher.getCurrentHeight(), list);
   }

   private Collection<TextureAtlasSprite> func_215256_a(IResourceManager p_215256_1_, Set<ResourceLocation> p_215256_2_) {
      List<CompletableFuture<?>> list = new ArrayList<>();
      ConcurrentLinkedQueue<TextureAtlasSprite> concurrentlinkedqueue = new ConcurrentLinkedQueue<>();

      for(ResourceLocation resourcelocation : p_215256_2_) {
         if (!this.missingImage.getName().equals(resourcelocation)) {
            list.add(CompletableFuture.runAsync(() -> {
               ResourceLocation resourcelocation1 = this.getSpritePath(resourcelocation);

               TextureAtlasSprite textureatlassprite;
               try (IResource iresource = p_215256_1_.getResource(resourcelocation1)) {
                  PngSizeInfo pngsizeinfo = new PngSizeInfo(iresource.toString(), iresource.getInputStream());
                  AnimationMetadataSection animationmetadatasection = iresource.getMetadata(AnimationMetadataSection.SERIALIZER);
                  textureatlassprite = new TextureAtlasSprite(resourcelocation, pngsizeinfo, animationmetadatasection);
               } catch (RuntimeException runtimeexception) {
                  LOGGER.error("Unable to parse metadata from {} : {}", resourcelocation1, runtimeexception);
                  return;
               } catch (IOException ioexception) {
                  LOGGER.error("Using missing texture, unable to load {} : {}", resourcelocation1, ioexception);
                  return;
               }

               concurrentlinkedqueue.add(textureatlassprite);
            }, Util.getServerExecutor()));
         }
      }

      CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).join();
      return concurrentlinkedqueue;
   }

   private List<TextureAtlasSprite> func_215259_a(IResourceManager p_215259_1_, Stitcher p_215259_2_) {
      ConcurrentLinkedQueue<TextureAtlasSprite> concurrentlinkedqueue = new ConcurrentLinkedQueue<>();
      List<CompletableFuture<?>> list = new ArrayList<>();

      for(TextureAtlasSprite textureatlassprite : p_215259_2_.getStichSlots()) {
         if (textureatlassprite == this.missingImage) {
            concurrentlinkedqueue.add(textureatlassprite);
         } else {
            list.add(CompletableFuture.runAsync(() -> {
               if (this.loadSprite(p_215259_1_, textureatlassprite)) {
                  concurrentlinkedqueue.add(textureatlassprite);
               }

            }, Util.getServerExecutor()));
         }
      }

      CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).join();
      return new ArrayList<>(concurrentlinkedqueue);
   }

   private boolean loadSprite(IResourceManager manager, TextureAtlasSprite sprite) {
      ResourceLocation resourcelocation = this.getSpritePath(sprite.getName());
      IResource iresource = null;

      label62: {
         boolean flag;
         if (sprite.hasCustomLoader(manager, resourcelocation)) break label62;
         try {
            iresource = manager.getResource(resourcelocation);
            sprite.loadSpriteFrames(iresource, this.mipmapLevels + 1);
            break label62;
         } catch (RuntimeException runtimeexception) {
            LOGGER.error("Unable to parse metadata from {}", resourcelocation, runtimeexception);
            flag = false;
         } catch (IOException ioexception) {
            LOGGER.error("Using missing texture, unable to load {}", resourcelocation, ioexception);
            flag = false;
            return flag;
         } finally {
            IOUtils.closeQuietly((Closeable)iresource);
         }

         return flag;
      }

      try {
         sprite.generateMipmaps(this.mipmapLevels);
         return true;
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Applying mipmap");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Sprite being mipmapped");
         crashreportcategory.addDetail("Sprite name", () -> {
            return sprite.getName().toString();
         });
         crashreportcategory.addDetail("Sprite size", () -> {
            return sprite.getWidth() + " x " + sprite.getHeight();
         });
         crashreportcategory.addDetail("Sprite frames", () -> {
            return sprite.getFrameCount() + " frames";
         });
         crashreportcategory.addDetail("Mipmap levels", this.mipmapLevels);
         throw new ReportedException(crashreport);
      }
   }

   private ResourceLocation getSpritePath(ResourceLocation location) {
      return new ResourceLocation(location.getNamespace(), String.format("%s/%s%s", this.basePath, location.getPath(), ".png"));
   }

   public TextureAtlasSprite getAtlasSprite(String iconName) {
      return this.getSprite(new ResourceLocation(iconName));
   }

   public void updateAnimations() {
      this.bindTexture();

      for(TextureAtlasSprite textureatlassprite : this.listAnimatedSprites) {
         textureatlassprite.updateAnimation();
      }

   }

   public void tick() {
      this.updateAnimations();
   }

   public void setMipmapLevels(int mipmapLevelsIn) {
      this.mipmapLevels = mipmapLevelsIn;
   }

   public TextureAtlasSprite getSprite(ResourceLocation location) {
      TextureAtlasSprite textureatlassprite = this.mapUploadedSprites.get(location);
      return textureatlassprite == null ? this.missingImage : textureatlassprite;
   }

   public void clear() {
      for(TextureAtlasSprite textureatlassprite : this.mapUploadedSprites.values()) {
         textureatlassprite.clearFramesTextureData();
      }

      this.mapUploadedSprites.clear();
      this.listAnimatedSprites.clear();
   }
   
   //===================================================================================================
   //                                           Forge Start
   //===================================================================================================

   private final java.util.Deque<ResourceLocation> loadingSprites = new java.util.ArrayDeque<>();
   private final java.util.Set<ResourceLocation> loadedSprites = new java.util.HashSet<>();

   public String getBasePath()
   {
       return basePath;
   }

   public int getMipmapLevels()
   {
       return mipmapLevels;
   }

   private int loadTexture(Stitcher stitcher, IResourceManager manager, ResourceLocation resourcelocation, int j, int k)
   {
      if (loadedSprites.contains(resourcelocation))
      {
         return j;
      }
      TextureAtlasSprite textureatlassprite;
      ResourceLocation resourcelocation1 = this.getSpritePath(resourcelocation);
      for (ResourceLocation loading : loadingSprites)
      {
         if (resourcelocation1.equals(loading))
         {
            final String error = "circular model dependencies, stack: [" + com.google.common.base.Joiner.on(", ").join(loadingSprites) + "]";
            net.minecraftforge.fml.client.ClientHooks.trackBrokenTexture(resourcelocation, error);
         }
      }
      loadingSprites.addLast(resourcelocation1);
      try (IResource iresource = manager.getResource(resourcelocation1))
      {
         PngSizeInfo pngsizeinfo = new PngSizeInfo(iresource.toString(), iresource.getInputStream());
         AnimationMetadataSection animationmetadatasection = iresource.getMetadata(AnimationMetadataSection.SERIALIZER);
         textureatlassprite = new TextureAtlasSprite(resourcelocation, pngsizeinfo, animationmetadatasection);

         for (ResourceLocation dependency : textureatlassprite.getDependencies())
         {
            if (!sprites.contains(dependency))
            {
               this.sprites.add(dependency);
            }
            j = loadTexture(stitcher, manager, dependency, j, k);
         }
         if (textureatlassprite.hasCustomLoader(manager, resourcelocation))
         {
            if (textureatlassprite.load(manager, resourcelocation, mapUploadedSprites::get))
            {
               return j;
            }
         }
         j = Math.min(j, Math.min(textureatlassprite.getWidth(), textureatlassprite.getHeight()));
         int j1 = Math.min(Integer.lowestOneBit(textureatlassprite.getWidth()), Integer.lowestOneBit(textureatlassprite.getHeight()));
         if (j1 < k)
         {
            // FORGE: do not lower the mipmap level, just log the problematic textures
            LOGGER.warn("Texture {} with size {}x{} will have visual artifacts at mip level {}, it can only support level {}." +
                    "Please report to the mod author that the texture should be some multiple of 16x16.",
                    resourcelocation1, textureatlassprite.getWidth(), textureatlassprite.getHeight(), MathHelper.log2(k), MathHelper.log2(j1));
         }
         if (loadSprite(manager, textureatlassprite))
         {
            stitcher.addSprite(textureatlassprite);
         }
         return j;
      }
      catch (RuntimeException runtimeexception)
      {
         net.minecraftforge.fml.client.ClientHooks.trackBrokenTexture(resourcelocation, runtimeexception.getMessage());
         return j;
      }
      catch (IOException ioexception)
      {
         net.minecraftforge.fml.client.ClientHooks.trackMissingTexture(resourcelocation);
         return j;
      }
      finally
      {
         loadingSprites.removeLast();
         sprites.add(resourcelocation1);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class SheetData {
      final Set<ResourceLocation> field_217805_a;
      final int field_217806_b;
      final int field_217807_c;
      final List<TextureAtlasSprite> field_217808_d;

      public SheetData(Set<ResourceLocation> p_i49874_1_, int p_i49874_2_, int p_i49874_3_, List<TextureAtlasSprite> p_i49874_4_) {
         this.field_217805_a = p_i49874_1_;
         this.field_217806_b = p_i49874_2_;
         this.field_217807_c = p_i49874_3_;
         this.field_217808_d = p_i49874_4_;
      }
   }
}