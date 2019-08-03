package com.mojang.blaze3d.platform;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

@OnlyIn(Dist.CLIENT)
public class TextureUtil {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final int MIN_MIPMAP_LEVEL = 0;
   private static final int DEFAULT_IMAGE_BUFFER_SIZE = 8192;

   public static int generateTextureId() {
      return GlStateManager.genTexture();
   }

   public static void releaseTextureId(int p_releaseTextureId_0_) {
      GlStateManager.deleteTexture(p_releaseTextureId_0_);
   }

   public static void prepareImage(int p_prepareImage_0_, int p_prepareImage_1_, int p_prepareImage_2_) {
      prepareImage(NativeImage.PixelFormatGLCode.RGBA, p_prepareImage_0_, 0, p_prepareImage_1_, p_prepareImage_2_);
   }

   public static void prepareImage(NativeImage.PixelFormatGLCode p_prepareImage_0_, int p_prepareImage_1_, int p_prepareImage_2_, int p_prepareImage_3_) {
      prepareImage(p_prepareImage_0_, p_prepareImage_1_, 0, p_prepareImage_2_, p_prepareImage_3_);
   }

   public static void prepareImage(int p_prepareImage_0_, int p_prepareImage_1_, int p_prepareImage_2_, int p_prepareImage_3_) {
      prepareImage(NativeImage.PixelFormatGLCode.RGBA, p_prepareImage_0_, p_prepareImage_1_, p_prepareImage_2_, p_prepareImage_3_);
   }

   public static void prepareImage(NativeImage.PixelFormatGLCode p_prepareImage_0_, int p_prepareImage_1_, int p_prepareImage_2_, int p_prepareImage_3_, int p_prepareImage_4_) {
      bind(p_prepareImage_1_);
      if (p_prepareImage_2_ >= 0) {
         GlStateManager.texParameter(3553, 33085, p_prepareImage_2_);
         GlStateManager.texParameter(3553, 33082, 0);
         GlStateManager.texParameter(3553, 33083, p_prepareImage_2_);
         GlStateManager.texParameter(3553, 34049, 0.0F);
      }

      for(int i = 0; i <= p_prepareImage_2_; ++i) {
         GlStateManager.texImage2D(3553, i, p_prepareImage_0_.getGlFormat(), p_prepareImage_3_ >> i, p_prepareImage_4_ >> i, 0, 6408, 5121, (IntBuffer)null);
      }

   }

   private static void bind(int p_bind_0_) {
      GlStateManager.bindTexture(p_bind_0_);
   }

   public static ByteBuffer readResource(InputStream p_readResource_0_) throws IOException {
      ByteBuffer bytebuffer;
      if (p_readResource_0_ instanceof FileInputStream) {
         FileInputStream fileinputstream = (FileInputStream)p_readResource_0_;
         FileChannel filechannel = fileinputstream.getChannel();
         bytebuffer = MemoryUtil.memAlloc((int)filechannel.size() + 1);

         while(filechannel.read(bytebuffer) != -1) {
            ;
         }
      } else {
         bytebuffer = MemoryUtil.memAlloc(8192);
         ReadableByteChannel readablebytechannel = Channels.newChannel(p_readResource_0_);

         while(readablebytechannel.read(bytebuffer) != -1) {
            if (bytebuffer.remaining() == 0) {
               bytebuffer = MemoryUtil.memRealloc(bytebuffer, bytebuffer.capacity() * 2);
            }
         }
      }

      return bytebuffer;
   }

   public static String readResourceAsString(InputStream p_readResourceAsString_0_) {
      ByteBuffer bytebuffer = null;

      try {
         bytebuffer = readResource(p_readResourceAsString_0_);
         int i = bytebuffer.position();
         bytebuffer.rewind();
         String s = MemoryUtil.memASCII(bytebuffer, i);
         return s;
      } catch (IOException var7) {
         ;
      } finally {
         if (bytebuffer != null) {
            MemoryUtil.memFree(bytebuffer);
         }

      }

      return null;
   }

   public static void writeAsPNG(String p_writeAsPNG_0_, int p_writeAsPNG_1_, int p_writeAsPNG_2_, int p_writeAsPNG_3_, int p_writeAsPNG_4_) {
      bind(p_writeAsPNG_1_);

      for(int i = 0; i <= p_writeAsPNG_2_; ++i) {
         String s = p_writeAsPNG_0_ + "_" + i + ".png";
         int j = p_writeAsPNG_3_ >> i;
         int k = p_writeAsPNG_4_ >> i;

         try (NativeImage nativeimage = new NativeImage(j, k, false)) {
            nativeimage.downloadFromTexture(i, false);
            nativeimage.func_216510_a(s);
            LOGGER.debug("Exported png to: {}", (Object)(new File(s)).getAbsolutePath());
         } catch (IOException ioexception) {
            LOGGER.debug("Unable to write: ", (Throwable)ioexception);
         }
      }

   }

   public static void initTexture(IntBuffer p_initTexture_0_, int p_initTexture_1_, int p_initTexture_2_) {
      GL11.glPixelStorei(3312, 0);
      GL11.glPixelStorei(3313, 0);
      GL11.glPixelStorei(3314, 0);
      GL11.glPixelStorei(3315, 0);
      GL11.glPixelStorei(3316, 0);
      GL11.glPixelStorei(3317, 4);
      GL11.glTexImage2D(3553, 0, 6408, p_initTexture_1_, p_initTexture_2_, 0, 32993, 33639, p_initTexture_0_);
      GL11.glTexParameteri(3553, 10242, 10497);
      GL11.glTexParameteri(3553, 10243, 10497);
      GL11.glTexParameteri(3553, 10240, 9728);
      GL11.glTexParameteri(3553, 10241, 9729);
   }
}