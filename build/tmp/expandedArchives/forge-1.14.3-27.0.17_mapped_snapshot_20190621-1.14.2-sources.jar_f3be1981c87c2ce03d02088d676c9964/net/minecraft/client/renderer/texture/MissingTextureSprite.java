package net.minecraft.client.renderer.texture;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.util.LazyLoadBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class MissingTextureSprite extends TextureAtlasSprite {
   private static final ResourceLocation LOCATION = new ResourceLocation("missingno");
   @Nullable
   private static DynamicTexture dynamicTexture;
   private static final LazyLoadBase<NativeImage> IMAGE = new LazyLoadBase<>(() -> {
      NativeImage nativeimage = new NativeImage(16, 16, false);
      int i = -16777216;
      int j = -524040;

      for(int k = 0; k < 16; ++k) {
         for(int l = 0; l < 16; ++l) {
            if (k < 8 ^ l < 8) {
               nativeimage.setPixelRGBA(l, k, -524040);
            } else {
               nativeimage.setPixelRGBA(l, k, -16777216);
            }
         }
      }

      nativeimage.untrack();
      return nativeimage;
   });

   private MissingTextureSprite() {
      super(LOCATION, 16, 16);
      this.frames = new NativeImage[]{IMAGE.getValue()};
   }

   public static MissingTextureSprite func_217790_a() {
      return new MissingTextureSprite();
   }

   public static ResourceLocation getLocation() {
      return LOCATION;
   }

   public void clearFramesTextureData() {
      for(int i = 1; i < this.frames.length; ++i) {
         this.frames[i].close();
      }

      this.frames = new NativeImage[]{IMAGE.getValue()};
   }

   public static DynamicTexture getDynamicTexture() {
      if (dynamicTexture == null) {
         dynamicTexture = new DynamicTexture(IMAGE.getValue());
         Minecraft.getInstance().getTextureManager().loadTexture(LOCATION, dynamicTexture);
      }

      return dynamicTexture;
   }
}