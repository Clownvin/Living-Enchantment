package net.minecraft.client.shader;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import java.nio.IntBuffer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Framebuffer {
   public int framebufferTextureWidth;
   public int framebufferTextureHeight;
   public int framebufferWidth;
   public int framebufferHeight;
   public final boolean useDepth;
   public int framebufferObject;
   public int framebufferTexture;
   public int depthBuffer;
   public final float[] framebufferColor;
   public int framebufferFilter;

   public Framebuffer(int p_i51175_1_, int p_i51175_2_, boolean p_i51175_3_, boolean p_i51175_4_) {
      this.useDepth = p_i51175_3_;
      this.framebufferObject = -1;
      this.framebufferTexture = -1;
      this.depthBuffer = -1;
      this.framebufferColor = new float[4];
      this.framebufferColor[0] = 1.0F;
      this.framebufferColor[1] = 1.0F;
      this.framebufferColor[2] = 1.0F;
      this.framebufferColor[3] = 0.0F;
      this.func_216491_a(p_i51175_1_, p_i51175_2_, p_i51175_4_);
   }

   public void func_216491_a(int p_216491_1_, int p_216491_2_, boolean p_216491_3_) {
      if (!GLX.isUsingFBOs()) {
         this.framebufferWidth = p_216491_1_;
         this.framebufferHeight = p_216491_2_;
      } else {
         GlStateManager.enableDepthTest();
         if (this.framebufferObject >= 0) {
            this.deleteFramebuffer();
         }

         this.func_216492_b(p_216491_1_, p_216491_2_, p_216491_3_);
         GLX.glBindFramebuffer(GLX.GL_FRAMEBUFFER, 0);
      }
   }

   public void deleteFramebuffer() {
      if (GLX.isUsingFBOs()) {
         this.unbindFramebufferTexture();
         this.unbindFramebuffer();
         if (this.depthBuffer > -1) {
            GLX.glDeleteRenderbuffers(this.depthBuffer);
            this.depthBuffer = -1;
         }

         if (this.framebufferTexture > -1) {
            TextureUtil.releaseTextureId(this.framebufferTexture);
            this.framebufferTexture = -1;
         }

         if (this.framebufferObject > -1) {
            GLX.glBindFramebuffer(GLX.GL_FRAMEBUFFER, 0);
            GLX.glDeleteFramebuffers(this.framebufferObject);
            this.framebufferObject = -1;
         }

      }
   }

   public void func_216492_b(int p_216492_1_, int p_216492_2_, boolean p_216492_3_) {
      this.framebufferWidth = p_216492_1_;
      this.framebufferHeight = p_216492_2_;
      this.framebufferTextureWidth = p_216492_1_;
      this.framebufferTextureHeight = p_216492_2_;
      if (!GLX.isUsingFBOs()) {
         this.func_216493_b(p_216492_3_);
      } else {
         this.framebufferObject = GLX.glGenFramebuffers();
         this.framebufferTexture = TextureUtil.generateTextureId();
         if (this.useDepth) {
            this.depthBuffer = GLX.glGenRenderbuffers();
         }

         this.setFramebufferFilter(9728);
         GlStateManager.bindTexture(this.framebufferTexture);
         GlStateManager.texImage2D(3553, 0, 32856, this.framebufferTextureWidth, this.framebufferTextureHeight, 0, 6408, 5121, (IntBuffer)null);
         GLX.glBindFramebuffer(GLX.GL_FRAMEBUFFER, this.framebufferObject);
         GLX.glFramebufferTexture2D(GLX.GL_FRAMEBUFFER, GLX.GL_COLOR_ATTACHMENT0, 3553, this.framebufferTexture, 0);
         if (this.useDepth) {
            GLX.glBindRenderbuffer(GLX.GL_RENDERBUFFER, this.depthBuffer);
            GLX.glRenderbufferStorage(GLX.GL_RENDERBUFFER, 33190, this.framebufferTextureWidth, this.framebufferTextureHeight);
            GLX.glFramebufferRenderbuffer(GLX.GL_FRAMEBUFFER, GLX.GL_DEPTH_ATTACHMENT, GLX.GL_RENDERBUFFER, this.depthBuffer);
         }

         this.checkFramebufferComplete();
         this.func_216493_b(p_216492_3_);
         this.unbindFramebufferTexture();
      }
   }

   public void setFramebufferFilter(int framebufferFilterIn) {
      if (GLX.isUsingFBOs()) {
         this.framebufferFilter = framebufferFilterIn;
         GlStateManager.bindTexture(this.framebufferTexture);
         GlStateManager.texParameter(3553, 10241, framebufferFilterIn);
         GlStateManager.texParameter(3553, 10240, framebufferFilterIn);
         GlStateManager.texParameter(3553, 10242, 10496);
         GlStateManager.texParameter(3553, 10243, 10496);
         GlStateManager.bindTexture(0);
      }

   }

   public void checkFramebufferComplete() {
      int i = GLX.glCheckFramebufferStatus(GLX.GL_FRAMEBUFFER);
      if (i != GLX.GL_FRAMEBUFFER_COMPLETE) {
         if (i == GLX.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
         } else if (i == GLX.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
         } else if (i == GLX.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");
         } else if (i == GLX.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER");
         } else {
            throw new RuntimeException("glCheckFramebufferStatus returned unknown status:" + i);
         }
      }
   }

   public void bindFramebufferTexture() {
      if (GLX.isUsingFBOs()) {
         GlStateManager.bindTexture(this.framebufferTexture);
      }

   }

   public void unbindFramebufferTexture() {
      if (GLX.isUsingFBOs()) {
         GlStateManager.bindTexture(0);
      }

   }

   public void bindFramebuffer(boolean p_147610_1_) {
      if (GLX.isUsingFBOs()) {
         GLX.glBindFramebuffer(GLX.GL_FRAMEBUFFER, this.framebufferObject);
         if (p_147610_1_) {
            GlStateManager.viewport(0, 0, this.framebufferWidth, this.framebufferHeight);
         }
      }

   }

   public void unbindFramebuffer() {
      if (GLX.isUsingFBOs()) {
         GLX.glBindFramebuffer(GLX.GL_FRAMEBUFFER, 0);
      }

   }

   public void setFramebufferColor(float red, float green, float blue, float alpha) {
      this.framebufferColor[0] = red;
      this.framebufferColor[1] = green;
      this.framebufferColor[2] = blue;
      this.framebufferColor[3] = alpha;
   }

   public void framebufferRender(int width, int height) {
      this.framebufferRenderExt(width, height, true);
   }

   public void framebufferRenderExt(int width, int height, boolean p_178038_3_) {
      if (GLX.isUsingFBOs()) {
         GlStateManager.colorMask(true, true, true, false);
         GlStateManager.disableDepthTest();
         GlStateManager.depthMask(false);
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         GlStateManager.ortho(0.0D, (double)width, (double)height, 0.0D, 1000.0D, 3000.0D);
         GlStateManager.matrixMode(5888);
         GlStateManager.loadIdentity();
         GlStateManager.translatef(0.0F, 0.0F, -2000.0F);
         GlStateManager.viewport(0, 0, width, height);
         GlStateManager.enableTexture();
         GlStateManager.disableLighting();
         GlStateManager.disableAlphaTest();
         if (p_178038_3_) {
            GlStateManager.disableBlend();
            GlStateManager.enableColorMaterial();
         }

         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.bindFramebufferTexture();
         float f = (float)width;
         float f1 = (float)height;
         float f2 = (float)this.framebufferWidth / (float)this.framebufferTextureWidth;
         float f3 = (float)this.framebufferHeight / (float)this.framebufferTextureHeight;
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder bufferbuilder = tessellator.getBuffer();
         bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         bufferbuilder.pos(0.0D, (double)f1, 0.0D).tex(0.0D, 0.0D).color(255, 255, 255, 255).endVertex();
         bufferbuilder.pos((double)f, (double)f1, 0.0D).tex((double)f2, 0.0D).color(255, 255, 255, 255).endVertex();
         bufferbuilder.pos((double)f, 0.0D, 0.0D).tex((double)f2, (double)f3).color(255, 255, 255, 255).endVertex();
         bufferbuilder.pos(0.0D, 0.0D, 0.0D).tex(0.0D, (double)f3).color(255, 255, 255, 255).endVertex();
         tessellator.draw();
         this.unbindFramebufferTexture();
         GlStateManager.depthMask(true);
         GlStateManager.colorMask(true, true, true, true);
      }
   }

   public void func_216493_b(boolean p_216493_1_) {
      this.bindFramebuffer(true);
      GlStateManager.clearColor(this.framebufferColor[0], this.framebufferColor[1], this.framebufferColor[2], this.framebufferColor[3]);
      int i = 16384;
      if (this.useDepth) {
         GlStateManager.clearDepth(1.0D);
         i |= 256;
      }

      GlStateManager.clear(i, p_216493_1_);
      this.unbindFramebuffer();
   }
}