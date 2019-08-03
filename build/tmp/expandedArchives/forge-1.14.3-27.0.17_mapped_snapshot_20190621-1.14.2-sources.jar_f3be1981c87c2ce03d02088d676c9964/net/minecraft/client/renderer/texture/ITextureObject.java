package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.GlStateManager;
import java.io.IOException;
import java.util.concurrent.Executor;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ITextureObject {
   void setBlurMipmap(boolean blurIn, boolean mipmapIn);

   void restoreLastBlurMipmap();

   void loadTexture(IResourceManager manager) throws IOException;

   int getGlTextureId();

   default void bindTexture() {
      GlStateManager.bindTexture(this.getGlTextureId());
   }

   default void func_215244_a(TextureManager p_215244_1_, IResourceManager p_215244_2_, ResourceLocation p_215244_3_, Executor p_215244_4_) {
      p_215244_1_.loadTexture(p_215244_3_, this);
   }
}