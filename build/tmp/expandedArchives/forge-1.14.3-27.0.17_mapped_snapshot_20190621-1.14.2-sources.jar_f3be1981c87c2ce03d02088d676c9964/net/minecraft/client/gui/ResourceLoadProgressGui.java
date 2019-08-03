package net.minecraft.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.resources.IAsyncReloader;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.VanillaPack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ResourceLoadProgressGui extends LoadingGui {
   private static final ResourceLocation MOJANG_LOGO_TEXTURE = new ResourceLocation("textures/gui/title/mojang.png");
   private final Minecraft mc;
   private final IAsyncReloader asyncReloader;
   private final Runnable completedCallback;
   private final boolean reloading;
   private float field_212978_f;
   private long field_212979_g = -1L;
   private long field_212980_h = -1L;

   public ResourceLoadProgressGui(Minecraft mc, IAsyncReloader p_i51112_2_, Runnable completedCallback, boolean reloading) {
      this.mc = mc;
      this.asyncReloader = p_i51112_2_;
      this.completedCallback = completedCallback;
      this.reloading = reloading;
   }

   public static void loadLogoTexture(Minecraft mc) {
      mc.getTextureManager().loadTexture(MOJANG_LOGO_TEXTURE, new ResourceLoadProgressGui.MojangLogoTexture());
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      int i = this.mc.mainWindow.getScaledWidth();
      int j = this.mc.mainWindow.getScaledHeight();
      long k = Util.milliTime();
      if (this.reloading && (this.asyncReloader.asyncPartDone() || this.mc.currentScreen != null) && this.field_212980_h == -1L) {
         this.field_212980_h = k;
      }

      float f = this.field_212979_g > -1L ? (float)(k - this.field_212979_g) / 1000.0F : -1.0F;
      float f1 = this.field_212980_h > -1L ? (float)(k - this.field_212980_h) / 500.0F : -1.0F;
      float f2;
      if (f >= 1.0F) {
         if (this.mc.currentScreen != null) {
            this.mc.currentScreen.render(0, 0, p_render_3_);
         }

         int l = MathHelper.ceil((1.0F - MathHelper.clamp(f - 1.0F, 0.0F, 1.0F)) * 255.0F);
         fill(0, 0, i, j, 16777215 | l << 24);
         f2 = 1.0F - MathHelper.clamp(f - 1.0F, 0.0F, 1.0F);
      } else if (this.reloading) {
         if (this.mc.currentScreen != null && f1 < 1.0F) {
            this.mc.currentScreen.render(p_render_1_, p_render_2_, p_render_3_);
         }

         int j1 = MathHelper.ceil(MathHelper.clamp((double)f1, 0.15D, 1.0D) * 255.0D);
         fill(0, 0, i, j, 16777215 | j1 << 24);
         f2 = MathHelper.clamp(f1, 0.0F, 1.0F);
      } else {
         fill(0, 0, i, j, -1);
         f2 = 1.0F;
      }

      int k1 = (this.mc.mainWindow.getScaledWidth() - 256) / 2;
      int i1 = (this.mc.mainWindow.getScaledHeight() - 256) / 2;
      this.mc.getTextureManager().bindTexture(MOJANG_LOGO_TEXTURE);
      GlStateManager.enableBlend();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, f2);
      this.blit(k1, i1, 0, 0, 256, 256);
      float f3 = this.asyncReloader.estimateExecutionSpeed();
      this.field_212978_f = this.field_212978_f * 0.95F + f3 * 0.050000012F;
      net.minecraftforge.fml.client.ClientModLoader.renderProgressText();
      if (f < 1.0F) {
         this.func_212972_a(i / 2 - 150, j / 4 * 3, i / 2 + 150, j / 4 * 3 + 10, this.field_212978_f, 1.0F - MathHelper.clamp(f, 0.0F, 1.0F));
      }

      if (f >= 2.0F) {
         this.mc.setLoadingGui((LoadingGui)null);
      }

      if (this.field_212979_g == -1L && this.asyncReloader.fullyDone() && (!this.reloading || f1 >= 2.0F)) {
         this.asyncReloader.join();
         this.field_212979_g = Util.milliTime();
         this.completedCallback.run();
         if (this.mc.currentScreen != null) {
            this.mc.currentScreen.init(this.mc, this.mc.mainWindow.getScaledWidth(), this.mc.mainWindow.getScaledHeight());
         }
      }

   }

   private void func_212972_a(int left, int top, int right, int bottom, float progress, float colormod) {
      int i = MathHelper.ceil((float)(right - left - 2) * progress);
      fill(left - 1, top - 1, right + 1, bottom + 1, -16777216 | Math.round((1.0F - colormod) * 255.0F) << 16 | Math.round((1.0F - colormod) * 255.0F) << 8 | Math.round((1.0F - colormod) * 255.0F));
      fill(left, top, right, bottom, -1);
      fill(left + 1, top + 1, left + i, bottom - 1, -16777216 | (int)MathHelper.lerp(1.0F - colormod, 226.0F, 255.0F) << 16 | (int)MathHelper.lerp(1.0F - colormod, 40.0F, 255.0F) << 8 | (int)MathHelper.lerp(1.0F - colormod, 55.0F, 255.0F));
   }

   public boolean isPauseScreen() {
      return true;
   }

   @OnlyIn(Dist.CLIENT)
   static class MojangLogoTexture extends SimpleTexture {
      public MojangLogoTexture() {
         super(ResourceLoadProgressGui.MOJANG_LOGO_TEXTURE);
      }

      protected SimpleTexture.TextureData func_215246_b(IResourceManager resourceManager) {
         Minecraft minecraft = Minecraft.getInstance();
         VanillaPack vanillapack = minecraft.getPackFinder().getVanillaPack();

         try (InputStream inputstream = vanillapack.getResourceStream(ResourcePackType.CLIENT_RESOURCES, ResourceLoadProgressGui.MOJANG_LOGO_TEXTURE)) {
            SimpleTexture.TextureData simpletexture$texturedata = new SimpleTexture.TextureData((TextureMetadataSection)null, NativeImage.read(inputstream));
            return simpletexture$texturedata;
         } catch (IOException ioexception) {
            return new SimpleTexture.TextureData(ioexception);
         }
      }
   }
}