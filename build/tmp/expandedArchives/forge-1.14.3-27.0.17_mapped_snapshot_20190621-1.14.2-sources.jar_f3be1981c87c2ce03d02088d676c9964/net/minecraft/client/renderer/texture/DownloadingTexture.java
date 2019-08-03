package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.TextureUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class DownloadingTexture extends SimpleTexture {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final AtomicInteger TEXTURE_DOWNLOADER_THREAD_ID = new AtomicInteger(0);
   @Nullable
   private final File cacheFile;
   private final String imageUrl;
   @Nullable
   private final IImageBuffer imageBuffer;
   @Nullable
   private Thread imageThread;
   private volatile boolean textureUploaded;

   public DownloadingTexture(@Nullable File cacheFileIn, String imageUrlIn, ResourceLocation textureResourceLocation, @Nullable IImageBuffer imageBufferIn) {
      super(textureResourceLocation);
      this.cacheFile = cacheFileIn;
      this.imageUrl = imageUrlIn;
      this.imageBuffer = imageBufferIn;
   }

   private void uploadImage(NativeImage nativeImageIn) {
      TextureUtil.prepareImage(this.getGlTextureId(), nativeImageIn.getWidth(), nativeImageIn.getHeight());
      nativeImageIn.uploadTextureSub(0, 0, 0, false);
   }

   public void setImage(NativeImage nativeImageIn) {
      if (this.imageBuffer != null) {
         this.imageBuffer.skinAvailable();
      }

      synchronized(this) {
         this.uploadImage(nativeImageIn);
         this.textureUploaded = true;
      }
   }

   public void loadTexture(IResourceManager manager) throws IOException {
      if (!this.textureUploaded) {
         synchronized(this) {
            super.loadTexture(manager);
            this.textureUploaded = true;
         }
      }

      if (this.imageThread == null) {
         if (this.cacheFile != null && this.cacheFile.isFile()) {
            LOGGER.debug("Loading http texture from local cache ({})", (Object)this.cacheFile);
            NativeImage nativeimage = null;

            try {
               nativeimage = NativeImage.read(new FileInputStream(this.cacheFile));
               if (this.imageBuffer != null) {
                  nativeimage = this.imageBuffer.parseUserSkin(nativeimage);
               }

               this.setImage(nativeimage);
            } catch (IOException ioexception) {
               LOGGER.error("Couldn't load skin {}", this.cacheFile, ioexception);
               this.loadTextureFromServer();
            } finally {
               if (nativeimage != null) {
                  nativeimage.close();
               }

            }
         } else {
            this.loadTextureFromServer();
         }
      }

   }

   protected void loadTextureFromServer() {
      this.imageThread = new Thread("Texture Downloader #" + TEXTURE_DOWNLOADER_THREAD_ID.incrementAndGet()) {
         public void run() {
            HttpURLConnection httpurlconnection = null;
            DownloadingTexture.LOGGER.debug("Downloading http texture from {} to {}", DownloadingTexture.this.imageUrl, DownloadingTexture.this.cacheFile);

            try {
               httpurlconnection = (HttpURLConnection)(new URL(DownloadingTexture.this.imageUrl)).openConnection(Minecraft.getInstance().getProxy());
               httpurlconnection.setDoInput(true);
               httpurlconnection.setDoOutput(false);
               httpurlconnection.connect();
               if (httpurlconnection.getResponseCode() / 100 == 2) {
                  InputStream inputstream;
                  if (DownloadingTexture.this.cacheFile != null) {
                     FileUtils.copyInputStreamToFile(httpurlconnection.getInputStream(), DownloadingTexture.this.cacheFile);
                     inputstream = new FileInputStream(DownloadingTexture.this.cacheFile);
                  } else {
                     inputstream = httpurlconnection.getInputStream();
                  }

                  Minecraft.getInstance().execute(() -> {
                     NativeImage nativeimage = null;

                     try {
                        nativeimage = NativeImage.read(inputstream);
                        if (DownloadingTexture.this.imageBuffer != null) {
                           nativeimage = DownloadingTexture.this.imageBuffer.parseUserSkin(nativeimage);
                        }

                        DownloadingTexture.this.setImage(nativeimage);
                     } catch (IOException ioexception) {
                        DownloadingTexture.LOGGER.warn("Error while loading the skin texture", (Throwable)ioexception);
                     } finally {
                        if (nativeimage != null) {
                           nativeimage.close();
                        }

                        IOUtils.closeQuietly(inputstream);
                     }

                  });
                  return;
               }
            } catch (Exception exception) {
               DownloadingTexture.LOGGER.error("Couldn't download http texture", (Throwable)exception);
               return;
            } finally {
               if (httpurlconnection != null) {
                  httpurlconnection.disconnect();
               }

            }

         }
      };
      this.imageThread.setDaemon(true);
      this.imageThread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
      this.imageThread.start();
   }
}