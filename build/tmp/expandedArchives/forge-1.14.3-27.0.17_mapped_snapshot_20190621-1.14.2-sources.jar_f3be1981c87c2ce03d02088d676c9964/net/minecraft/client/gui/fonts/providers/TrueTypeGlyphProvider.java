package net.minecraft.client.gui.fonts.providers;

import it.unimi.dsi.fastutil.chars.CharArraySet;
import it.unimi.dsi.fastutil.chars.CharSet;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.fonts.IGlyphInfo;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;

@OnlyIn(Dist.CLIENT)
public class TrueTypeGlyphProvider implements IGlyphProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private final STBTTFontinfo fontInfo;
   private final float oversample;
   private final CharSet chars = new CharArraySet();
   private final float shiftX;
   private final float shiftY;
   private final float scale;
   private final float ascent;

   public TrueTypeGlyphProvider(STBTTFontinfo info, float size, float oversampleIn, float shiftXIn, float shiftYIn, String charsIn) {
      this.fontInfo = info;
      this.oversample = oversampleIn;
      charsIn.chars().forEach((p_211614_1_) -> {
         this.chars.add((char)(p_211614_1_ & '\uffff'));
      });
      this.shiftX = shiftXIn * oversampleIn;
      this.shiftY = shiftYIn * oversampleIn;
      this.scale = STBTruetype.stbtt_ScaleForPixelHeight(info, size * oversampleIn);

      try (MemoryStack memorystack = MemoryStack.stackPush()) {
         IntBuffer intbuffer = memorystack.mallocInt(1);
         IntBuffer intbuffer1 = memorystack.mallocInt(1);
         IntBuffer intbuffer2 = memorystack.mallocInt(1);
         STBTruetype.stbtt_GetFontVMetrics(info, intbuffer, intbuffer1, intbuffer2);
         this.ascent = (float)intbuffer.get(0) * this.scale;
      }

   }

   @Nullable
   public TrueTypeGlyphProvider.GlpyhInfo func_212248_a(char p_212248_1_) {
      if (this.chars.contains(p_212248_1_)) {
         return null;
      } else {
         Object lvt_9_1_;
         try (MemoryStack memorystack = MemoryStack.stackPush()) {
            IntBuffer intbuffer = memorystack.mallocInt(1);
            IntBuffer intbuffer1 = memorystack.mallocInt(1);
            IntBuffer intbuffer2 = memorystack.mallocInt(1);
            IntBuffer intbuffer3 = memorystack.mallocInt(1);
            int i = STBTruetype.stbtt_FindGlyphIndex(this.fontInfo, p_212248_1_);
            if (i != 0) {
               STBTruetype.stbtt_GetGlyphBitmapBoxSubpixel(this.fontInfo, i, this.scale, this.scale, this.shiftX, this.shiftY, intbuffer, intbuffer1, intbuffer2, intbuffer3);
               int k = intbuffer2.get(0) - intbuffer.get(0);
               int j = intbuffer3.get(0) - intbuffer1.get(0);
               if (k != 0 && j != 0) {
                  IntBuffer intbuffer5 = memorystack.mallocInt(1);
                  IntBuffer intbuffer4 = memorystack.mallocInt(1);
                  STBTruetype.stbtt_GetGlyphHMetrics(this.fontInfo, i, intbuffer5, intbuffer4);
                  TrueTypeGlyphProvider.GlpyhInfo truetypeglyphprovider$glpyhinfo = new TrueTypeGlyphProvider.GlpyhInfo(intbuffer.get(0), intbuffer2.get(0), -intbuffer1.get(0), -intbuffer3.get(0), (float)intbuffer5.get(0) * this.scale, (float)intbuffer4.get(0) * this.scale, i);
                  return truetypeglyphprovider$glpyhinfo;
               }

               Object lvt_11_1_ = null;
               return (TrueTypeGlyphProvider.GlpyhInfo)lvt_11_1_;
            }

            lvt_9_1_ = null;
         }

         return (TrueTypeGlyphProvider.GlpyhInfo)lvt_9_1_;
      }
   }

   public static STBTTFontinfo func_216485_a(ByteBuffer p_216485_0_) throws IOException {
      STBTTFontinfo stbttfontinfo = STBTTFontinfo.create();
      if (!STBTruetype.stbtt_InitFont(stbttfontinfo, p_216485_0_)) {
         throw new IOException("Invalid ttf");
      } else {
         return stbttfontinfo;
      }
   }

   @OnlyIn(Dist.CLIENT)
   class GlpyhInfo implements IGlyphInfo {
      private final int width;
      private final int height;
      private final float field_212464_d;
      private final float field_212465_e;
      private final float advanceWidth;
      private final int glyphIndex;

      private GlpyhInfo(int p_i49751_2_, int p_i49751_3_, int p_i49751_4_, int p_i49751_5_, float p_i49751_6_, float p_i49751_7_, int p_i49751_8_) {
         this.width = p_i49751_3_ - p_i49751_2_;
         this.height = p_i49751_4_ - p_i49751_5_;
         this.advanceWidth = p_i49751_6_ / TrueTypeGlyphProvider.this.oversample;
         this.field_212464_d = (p_i49751_7_ + (float)p_i49751_2_ + TrueTypeGlyphProvider.this.shiftX) / TrueTypeGlyphProvider.this.oversample;
         this.field_212465_e = (TrueTypeGlyphProvider.this.ascent - (float)p_i49751_4_ + TrueTypeGlyphProvider.this.shiftY) / TrueTypeGlyphProvider.this.oversample;
         this.glyphIndex = p_i49751_8_;
      }

      public int getWidth() {
         return this.width;
      }

      public int getHeight() {
         return this.height;
      }

      public float getOversample() {
         return TrueTypeGlyphProvider.this.oversample;
      }

      public float getAdvance() {
         return this.advanceWidth;
      }

      public float func_223273_a_() {
         return this.field_212464_d;
      }

      public float func_223277_l_() {
         return this.field_212465_e;
      }

      public void uploadGlyph(int xOffset, int yOffset) {
         try (NativeImage nativeimage = new NativeImage(NativeImage.PixelFormat.LUMINANCE, this.width, this.height, false)) {
            nativeimage.renderGlyph(TrueTypeGlyphProvider.this.fontInfo, this.glyphIndex, this.width, this.height, TrueTypeGlyphProvider.this.scale, TrueTypeGlyphProvider.this.scale, TrueTypeGlyphProvider.this.shiftX, TrueTypeGlyphProvider.this.shiftY, 0, 0);
            nativeimage.uploadTextureSub(0, xOffset, yOffset, 0, 0, this.width, this.height, false);
         }

      }

      public boolean isColored() {
         return false;
      }
   }
}