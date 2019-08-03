package net.minecraft.client.resources;

import java.io.IOException;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.FoliageColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FoliageColorReloadListener extends ReloadListener<int[]> {
   private static final ResourceLocation FOLIAGE_LOCATION = new ResourceLocation("textures/colormap/foliage.png");

   /**
    * Performs any reloading that can be done off-thread, such as file IO
    */
   protected int[] prepare(IResourceManager p_212854_1_, IProfiler p_212854_2_) {
      try {
         return ColorMapLoader.func_217820_a(p_212854_1_, FOLIAGE_LOCATION);
      } catch (IOException ioexception) {
         throw new IllegalStateException("Failed to load foliage color texture", ioexception);
      }
   }

   /**
    * Performs any reloading that must be done on the main thread, such as uploading textures to the GPU or touching
    * non-threadsafe data
    */
   protected void apply(int[] p_212853_1_, IResourceManager p_212853_2_, IProfiler p_212853_3_) {
      FoliageColors.setFoliageBiomeColorizer(p_212853_1_);
   }

   //@Override //Forge: TODO: Filtered resource reloading
   public net.minecraftforge.resource.IResourceType getResourceType() {
      return net.minecraftforge.resource.VanillaResourceType.TEXTURES;
   }
}