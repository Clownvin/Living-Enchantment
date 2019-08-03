package net.minecraft.client.renderer.model;

import java.util.Map;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelManager extends ReloadListener<ModelBakery> {
   private Map<ResourceLocation, IBakedModel> modelRegistry = new java.util.HashMap<>();
   private final AtlasTexture texMap;
   private final BlockModelShapes modelProvider;
   private IBakedModel defaultModel;

   public ModelManager(AtlasTexture textures) {
      this.texMap = textures;
      this.modelProvider = new BlockModelShapes(this);
   }

   public IBakedModel getModel(ModelResourceLocation modelLocation) {
      return this.modelRegistry.getOrDefault(modelLocation, this.defaultModel);
   }

   public IBakedModel getMissingModel() {
      return this.defaultModel;
   }

   public BlockModelShapes getBlockModelShapes() {
      return this.modelProvider;
   }

   // TODO
   //@Override
   public net.minecraftforge.resource.IResourceType getResourceType() {
      return net.minecraftforge.resource.VanillaResourceType.MODELS;
   }

   /**
    * Performs any reloading that can be done off-thread, such as file IO
    */
   protected ModelBakery prepare(IResourceManager p_212854_1_, IProfiler p_212854_2_) {
      p_212854_2_.startTick();
      net.minecraftforge.client.model.ModelLoader modelbakery = new net.minecraftforge.client.model.ModelLoader(p_212854_1_, this.texMap, p_212854_2_);
      p_212854_2_.endTick();
      return modelbakery;
   }

   /**
    * Performs any reloading that must be done on the main thread, such as uploading textures to the GPU or touching
    * non-threadsafe data
    */
   protected void apply(ModelBakery p_212853_1_, IResourceManager p_212853_2_, IProfiler p_212853_3_) {
      p_212853_3_.startTick();
      p_212853_3_.startSection("upload");
      p_212853_1_.func_217844_a(p_212853_3_);
      this.modelRegistry = p_212853_1_.func_217846_a();
      this.defaultModel = this.modelRegistry.get(ModelBakery.MODEL_MISSING);
      net.minecraftforge.client.ForgeHooksClient.onModelBake(this, this.modelRegistry, (net.minecraftforge.client.model.ModelLoader) p_212853_1_);
      p_212853_3_.endStartSection("cache");
      this.modelProvider.reloadModels();
      p_212853_3_.endSection();
      p_212853_3_.endTick();
   }
}