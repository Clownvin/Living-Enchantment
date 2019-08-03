package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class Texture implements ITextureObject {
   protected int glTextureId = -1;
   protected boolean blur;
   protected boolean mipmap;
   protected boolean blurLast;
   protected boolean mipmapLast;

   public void setBlurMipmapDirect(boolean blurIn, boolean mipmapIn) {
      this.blur = blurIn;
      this.mipmap = mipmapIn;
      int i;
      int j;
      if (blurIn) {
         i = mipmapIn ? 9987 : 9729;
         j = 9729;
      } else {
         i = mipmapIn ? 9986 : 9728;
         j = 9728;
      }

      GlStateManager.texParameter(3553, 10241, i);
      GlStateManager.texParameter(3553, 10240, j);
   }

   public void setBlurMipmap(boolean blurIn, boolean mipmapIn) {
      this.blurLast = this.blur;
      this.mipmapLast = this.mipmap;
      this.setBlurMipmapDirect(blurIn, mipmapIn);
   }

   public void restoreLastBlurMipmap() {
      this.setBlurMipmapDirect(this.blurLast, this.mipmapLast);
   }

   public int getGlTextureId() {
      if (this.glTextureId == -1) {
         this.glTextureId = TextureUtil.generateTextureId();
      }

      return this.glTextureId;
   }

   public void deleteGlTexture() {
      if (this.glTextureId != -1) {
         TextureUtil.releaseTextureId(this.glTextureId);
         this.glTextureId = -1;
      }

   }
}