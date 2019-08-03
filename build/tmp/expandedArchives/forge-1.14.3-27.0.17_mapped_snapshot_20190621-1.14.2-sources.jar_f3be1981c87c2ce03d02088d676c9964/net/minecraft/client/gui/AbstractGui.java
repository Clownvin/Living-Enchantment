package net.minecraft.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractGui {
   public static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation("textures/gui/options_background.png");
   public static final ResourceLocation STATS_ICON_LOCATION = new ResourceLocation("textures/gui/container/stats_icons.png");
   public static final ResourceLocation GUI_ICONS_LOCATION = new ResourceLocation("textures/gui/icons.png");
   protected int blitOffset;

   protected void hLine(int p_hLine_1_, int p_hLine_2_, int p_hLine_3_, int p_hLine_4_) {
      if (p_hLine_2_ < p_hLine_1_) {
         int i = p_hLine_1_;
         p_hLine_1_ = p_hLine_2_;
         p_hLine_2_ = i;
      }

      fill(p_hLine_1_, p_hLine_3_, p_hLine_2_ + 1, p_hLine_3_ + 1, p_hLine_4_);
   }

   protected void vLine(int p_vLine_1_, int p_vLine_2_, int p_vLine_3_, int p_vLine_4_) {
      if (p_vLine_3_ < p_vLine_2_) {
         int i = p_vLine_2_;
         p_vLine_2_ = p_vLine_3_;
         p_vLine_3_ = i;
      }

      fill(p_vLine_1_, p_vLine_2_ + 1, p_vLine_1_ + 1, p_vLine_3_, p_vLine_4_);
   }

   public static void fill(int p_fill_0_, int p_fill_1_, int p_fill_2_, int p_fill_3_, int p_fill_4_) {
      if (p_fill_0_ < p_fill_2_) {
         int i = p_fill_0_;
         p_fill_0_ = p_fill_2_;
         p_fill_2_ = i;
      }

      if (p_fill_1_ < p_fill_3_) {
         int j = p_fill_1_;
         p_fill_1_ = p_fill_3_;
         p_fill_3_ = j;
      }

      float f3 = (float)(p_fill_4_ >> 24 & 255) / 255.0F;
      float f = (float)(p_fill_4_ >> 16 & 255) / 255.0F;
      float f1 = (float)(p_fill_4_ >> 8 & 255) / 255.0F;
      float f2 = (float)(p_fill_4_ & 255) / 255.0F;
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      GlStateManager.enableBlend();
      GlStateManager.disableTexture();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.color4f(f, f1, f2, f3);
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
      bufferbuilder.pos((double)p_fill_0_, (double)p_fill_3_, 0.0D).endVertex();
      bufferbuilder.pos((double)p_fill_2_, (double)p_fill_3_, 0.0D).endVertex();
      bufferbuilder.pos((double)p_fill_2_, (double)p_fill_1_, 0.0D).endVertex();
      bufferbuilder.pos((double)p_fill_0_, (double)p_fill_1_, 0.0D).endVertex();
      tessellator.draw();
      GlStateManager.enableTexture();
      GlStateManager.disableBlend();
   }

   protected void fillGradient(int p_fillGradient_1_, int p_fillGradient_2_, int p_fillGradient_3_, int p_fillGradient_4_, int p_fillGradient_5_, int p_fillGradient_6_) {
      float f = (float)(p_fillGradient_5_ >> 24 & 255) / 255.0F;
      float f1 = (float)(p_fillGradient_5_ >> 16 & 255) / 255.0F;
      float f2 = (float)(p_fillGradient_5_ >> 8 & 255) / 255.0F;
      float f3 = (float)(p_fillGradient_5_ & 255) / 255.0F;
      float f4 = (float)(p_fillGradient_6_ >> 24 & 255) / 255.0F;
      float f5 = (float)(p_fillGradient_6_ >> 16 & 255) / 255.0F;
      float f6 = (float)(p_fillGradient_6_ >> 8 & 255) / 255.0F;
      float f7 = (float)(p_fillGradient_6_ & 255) / 255.0F;
      GlStateManager.disableTexture();
      GlStateManager.enableBlend();
      GlStateManager.disableAlphaTest();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.shadeModel(7425);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
      bufferbuilder.pos((double)p_fillGradient_3_, (double)p_fillGradient_2_, (double)this.blitOffset).color(f1, f2, f3, f).endVertex();
      bufferbuilder.pos((double)p_fillGradient_1_, (double)p_fillGradient_2_, (double)this.blitOffset).color(f1, f2, f3, f).endVertex();
      bufferbuilder.pos((double)p_fillGradient_1_, (double)p_fillGradient_4_, (double)this.blitOffset).color(f5, f6, f7, f4).endVertex();
      bufferbuilder.pos((double)p_fillGradient_3_, (double)p_fillGradient_4_, (double)this.blitOffset).color(f5, f6, f7, f4).endVertex();
      tessellator.draw();
      GlStateManager.shadeModel(7424);
      GlStateManager.disableBlend();
      GlStateManager.enableAlphaTest();
      GlStateManager.enableTexture();
   }

   public void drawCenteredString(FontRenderer p_drawCenteredString_1_, String p_drawCenteredString_2_, int p_drawCenteredString_3_, int p_drawCenteredString_4_, int p_drawCenteredString_5_) {
      p_drawCenteredString_1_.drawStringWithShadow(p_drawCenteredString_2_, (float)(p_drawCenteredString_3_ - p_drawCenteredString_1_.getStringWidth(p_drawCenteredString_2_) / 2), (float)p_drawCenteredString_4_, p_drawCenteredString_5_);
   }

   public void drawRightAlignedString(FontRenderer p_drawRightAlignedString_1_, String p_drawRightAlignedString_2_, int p_drawRightAlignedString_3_, int p_drawRightAlignedString_4_, int p_drawRightAlignedString_5_) {
      p_drawRightAlignedString_1_.drawStringWithShadow(p_drawRightAlignedString_2_, (float)(p_drawRightAlignedString_3_ - p_drawRightAlignedString_1_.getStringWidth(p_drawRightAlignedString_2_)), (float)p_drawRightAlignedString_4_, p_drawRightAlignedString_5_);
   }

   public void drawString(FontRenderer p_drawString_1_, String p_drawString_2_, int p_drawString_3_, int p_drawString_4_, int p_drawString_5_) {
      p_drawString_1_.drawStringWithShadow(p_drawString_2_, (float)p_drawString_3_, (float)p_drawString_4_, p_drawString_5_);
   }

   public static void blit(int p_blit_0_, int p_blit_1_, int p_blit_2_, int p_blit_3_, int p_blit_4_, TextureAtlasSprite p_blit_5_) {
      innerBlit(p_blit_0_, p_blit_0_ + p_blit_3_, p_blit_1_, p_blit_1_ + p_blit_4_, p_blit_2_, p_blit_5_.getMinU(), p_blit_5_.getMaxU(), p_blit_5_.getMinV(), p_blit_5_.getMaxV());
   }

   public void blit(int p_blit_1_, int p_blit_2_, int p_blit_3_, int p_blit_4_, int p_blit_5_, int p_blit_6_) {
      blit(p_blit_1_, p_blit_2_, this.blitOffset, (float)p_blit_3_, (float)p_blit_4_, p_blit_5_, p_blit_6_, 256, 256);
   }

   public static void blit(int p_blit_0_, int p_blit_1_, int p_blit_2_, float p_blit_3_, float p_blit_4_, int p_blit_5_, int p_blit_6_, int p_blit_7_, int p_blit_8_) {
      innerBlit(p_blit_0_, p_blit_0_ + p_blit_5_, p_blit_1_, p_blit_1_ + p_blit_6_, p_blit_2_, p_blit_5_, p_blit_6_, p_blit_3_, p_blit_4_, p_blit_8_, p_blit_7_);
   }

   public static void blit(int p_blit_0_, int p_blit_1_, int p_blit_2_, int p_blit_3_, float p_blit_4_, float p_blit_5_, int p_blit_6_, int p_blit_7_, int p_blit_8_, int p_blit_9_) {
      innerBlit(p_blit_0_, p_blit_0_ + p_blit_2_, p_blit_1_, p_blit_1_ + p_blit_3_, 0, p_blit_6_, p_blit_7_, p_blit_4_, p_blit_5_, p_blit_8_, p_blit_9_);
   }

   public static void blit(int p_blit_0_, int p_blit_1_, float p_blit_2_, float p_blit_3_, int p_blit_4_, int p_blit_5_, int p_blit_6_, int p_blit_7_) {
      blit(p_blit_0_, p_blit_1_, p_blit_4_, p_blit_5_, p_blit_2_, p_blit_3_, p_blit_4_, p_blit_5_, p_blit_6_, p_blit_7_);
   }

   private static void innerBlit(int p_innerBlit_0_, int p_innerBlit_1_, int p_innerBlit_2_, int p_innerBlit_3_, int p_innerBlit_4_, int p_innerBlit_5_, int p_innerBlit_6_, float p_innerBlit_7_, float p_innerBlit_8_, int p_innerBlit_9_, int p_innerBlit_10_) {
      innerBlit(p_innerBlit_0_, p_innerBlit_1_, p_innerBlit_2_, p_innerBlit_3_, p_innerBlit_4_, (p_innerBlit_7_ + 0.0F) / (float)p_innerBlit_9_, (p_innerBlit_7_ + (float)p_innerBlit_5_) / (float)p_innerBlit_9_, (p_innerBlit_8_ + 0.0F) / (float)p_innerBlit_10_, (p_innerBlit_8_ + (float)p_innerBlit_6_) / (float)p_innerBlit_10_);
   }

   protected static void innerBlit(int p_innerBlit_0_, int p_innerBlit_1_, int p_innerBlit_2_, int p_innerBlit_3_, int p_innerBlit_4_, float p_innerBlit_5_, float p_innerBlit_6_, float p_innerBlit_7_, float p_innerBlit_8_) {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
      bufferbuilder.pos((double)p_innerBlit_0_, (double)p_innerBlit_3_, (double)p_innerBlit_4_).tex((double)p_innerBlit_5_, (double)p_innerBlit_8_).endVertex();
      bufferbuilder.pos((double)p_innerBlit_1_, (double)p_innerBlit_3_, (double)p_innerBlit_4_).tex((double)p_innerBlit_6_, (double)p_innerBlit_8_).endVertex();
      bufferbuilder.pos((double)p_innerBlit_1_, (double)p_innerBlit_2_, (double)p_innerBlit_4_).tex((double)p_innerBlit_6_, (double)p_innerBlit_7_).endVertex();
      bufferbuilder.pos((double)p_innerBlit_0_, (double)p_innerBlit_2_, (double)p_innerBlit_4_).tex((double)p_innerBlit_5_, (double)p_innerBlit_7_).endVertex();
      tessellator.draw();
   }
}