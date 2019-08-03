package net.minecraft.client.resources;

import java.io.IOException;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ColorMapLoader {
   @Deprecated
   public static int[] func_217820_a(IResourceManager p_217820_0_, ResourceLocation p_217820_1_) throws IOException {
      Object object;
      try (
         IResource iresource = p_217820_0_.getResource(p_217820_1_);
         NativeImage nativeimage = NativeImage.read(iresource.getInputStream());
      ) {
         object = nativeimage.makePixelArray();
      }

      return (int[])object;
   }
}