package net.minecraft.client.renderer.texture;

import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class SpriteUploader extends ReloadListener<AtlasTexture.SheetData> implements AutoCloseable {
   private final AtlasTexture textureAtlas;

   public SpriteUploader(TextureManager p_i50905_1_, ResourceLocation atlasTextureLocation, String p_i50905_3_) {
      this.textureAtlas = new AtlasTexture(p_i50905_3_);
      p_i50905_1_.loadTickableTexture(atlasTextureLocation, this.textureAtlas);
   }

   protected abstract Iterable<ResourceLocation> getKnownKeys();

   /**
    * Gets a sprite associated with the passed resource location.
    */
   protected TextureAtlasSprite getSprite(ResourceLocation p_215282_1_) {
      return this.textureAtlas.getSprite(p_215282_1_);
   }

   /**
    * Performs any reloading that can be done off-thread, such as file IO
    */
   protected AtlasTexture.SheetData prepare(IResourceManager p_212854_1_, IProfiler p_212854_2_) {
      p_212854_2_.startTick();
      p_212854_2_.startSection("stitching");
      AtlasTexture.SheetData atlastexture$sheetdata = this.textureAtlas.stitch(p_212854_1_, this.getKnownKeys(), p_212854_2_);
      p_212854_2_.endSection();
      p_212854_2_.endTick();
      return atlastexture$sheetdata;
   }

   /**
    * Performs any reloading that must be done on the main thread, such as uploading textures to the GPU or touching
    * non-threadsafe data
    */
   protected void apply(AtlasTexture.SheetData p_212853_1_, IResourceManager p_212853_2_, IProfiler p_212853_3_) {
      p_212853_3_.startTick();
      p_212853_3_.startSection("upload");
      this.textureAtlas.upload(p_212853_1_);
      p_212853_3_.endSection();
      p_212853_3_.endTick();
   }

   public void close() {
      this.textureAtlas.clear();
   }
}