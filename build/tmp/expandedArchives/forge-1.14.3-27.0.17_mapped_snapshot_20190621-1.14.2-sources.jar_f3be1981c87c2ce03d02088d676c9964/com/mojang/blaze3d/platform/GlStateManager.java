package com.mojang.blaze3d.platform;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.util.LWJGLMemoryUntracker;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.system.MemoryUtil;

@OnlyIn(Dist.CLIENT)
public class GlStateManager {
   private static final int LIGHT_COUNT = 8;
   private static final int TEXTURE_COUNT = 8;
   private static final FloatBuffer MATRIX_BUFFER = GLX.make(MemoryUtil.memAllocFloat(16), (p_209238_0_) -> {
      LWJGLMemoryUntracker.untrack(MemoryUtil.memAddress(p_209238_0_));
   });
   private static final FloatBuffer COLOR_BUFFER = GLX.make(MemoryUtil.memAllocFloat(4), (p_209236_0_) -> {
      LWJGLMemoryUntracker.untrack(MemoryUtil.memAddress(p_209236_0_));
   });
   private static final GlStateManager.AlphaState ALPHA_TEST = new GlStateManager.AlphaState();
   private static final GlStateManager.BooleanState LIGHTING = new GlStateManager.BooleanState(2896);
   private static final GlStateManager.BooleanState[] LIGHT_ENABLE = IntStream.range(0, 8).mapToObj((p_199933_0_) -> {
      return new GlStateManager.BooleanState(16384 + p_199933_0_);
   }).toArray((p_199930_0_) -> {
      return new GlStateManager.BooleanState[p_199930_0_];
   });
   private static final GlStateManager.ColorMaterialState COLOR_MATERIAL = new GlStateManager.ColorMaterialState();
   private static final GlStateManager.BlendState BLEND = new GlStateManager.BlendState();
   private static final GlStateManager.DepthState DEPTH = new GlStateManager.DepthState();
   private static final GlStateManager.FogState FOG = new GlStateManager.FogState();
   private static final GlStateManager.CullState CULL = new GlStateManager.CullState();
   private static final GlStateManager.PolygonOffsetState POLY_OFFSET = new GlStateManager.PolygonOffsetState();
   private static final GlStateManager.ColorLogicState COLOR_LOGIC = new GlStateManager.ColorLogicState();
   private static final GlStateManager.TexGenState TEX_GEN = new GlStateManager.TexGenState();
   private static final GlStateManager.ClearState CLEAR = new GlStateManager.ClearState();
   private static final GlStateManager.StencilState STENCIL = new GlStateManager.StencilState();
   private static final GlStateManager.BooleanState NORMALIZE = new GlStateManager.BooleanState(2977);
   private static int activeTexture;
   private static final GlStateManager.TextureState[] TEXTURES = IntStream.range(0, 8).mapToObj((p_199931_0_) -> {
      return new GlStateManager.TextureState();
   }).toArray((p_199932_0_) -> {
      return new GlStateManager.TextureState[p_199932_0_];
   });
   private static int shadeModel = 7425;
   private static final GlStateManager.BooleanState RESCALE_NORMAL = new GlStateManager.BooleanState(32826);
   private static final GlStateManager.ColorMask COLOR_MASK = new GlStateManager.ColorMask();
   private static final GlStateManager.Color COLOR = new GlStateManager.Color();
   private static final float DEFAULTALPHACUTOFF = 0.1F;

   public static void pushLightingAttributes() {
      GL11.glPushAttrib(8256);
   }

   public static void pushTextureAttributes() {
      GL11.glPushAttrib(270336);
   }

   public static void popAttributes() {
      GL11.glPopAttrib();
   }

   public static void disableAlphaTest() {
      ALPHA_TEST.field_179208_a.func_179198_a();
   }

   public static void enableAlphaTest() {
      ALPHA_TEST.field_179208_a.func_179200_b();
   }

   public static void alphaFunc(int p_alphaFunc_0_, float p_alphaFunc_1_) {
      if (p_alphaFunc_0_ != ALPHA_TEST.field_179206_b || p_alphaFunc_1_ != ALPHA_TEST.field_179207_c) {
         ALPHA_TEST.field_179206_b = p_alphaFunc_0_;
         ALPHA_TEST.field_179207_c = p_alphaFunc_1_;
         GL11.glAlphaFunc(p_alphaFunc_0_, p_alphaFunc_1_);
      }

   }

   public static void enableLighting() {
      LIGHTING.func_179200_b();
   }

   public static void disableLighting() {
      LIGHTING.func_179198_a();
   }

   public static void enableLight(int p_enableLight_0_) {
      LIGHT_ENABLE[p_enableLight_0_].func_179200_b();
   }

   public static void disableLight(int p_disableLight_0_) {
      LIGHT_ENABLE[p_disableLight_0_].func_179198_a();
   }

   public static void enableColorMaterial() {
      COLOR_MATERIAL.field_179191_a.func_179200_b();
   }

   public static void disableColorMaterial() {
      COLOR_MATERIAL.field_179191_a.func_179198_a();
   }

   public static void colorMaterial(int p_colorMaterial_0_, int p_colorMaterial_1_) {
      if (p_colorMaterial_0_ != COLOR_MATERIAL.field_179189_b || p_colorMaterial_1_ != COLOR_MATERIAL.field_179190_c) {
         COLOR_MATERIAL.field_179189_b = p_colorMaterial_0_;
         COLOR_MATERIAL.field_179190_c = p_colorMaterial_1_;
         GL11.glColorMaterial(p_colorMaterial_0_, p_colorMaterial_1_);
      }

   }

   public static void light(int p_light_0_, int p_light_1_, FloatBuffer p_light_2_) {
      GL11.glLightfv(p_light_0_, p_light_1_, p_light_2_);
   }

   public static void lightModel(int p_lightModel_0_, FloatBuffer p_lightModel_1_) {
      GL11.glLightModelfv(p_lightModel_0_, p_lightModel_1_);
   }

   public static void normal3f(float p_normal3f_0_, float p_normal3f_1_, float p_normal3f_2_) {
      GL11.glNormal3f(p_normal3f_0_, p_normal3f_1_, p_normal3f_2_);
   }

   public static void disableDepthTest() {
      DEPTH.field_179052_a.func_179198_a();
   }

   public static void enableDepthTest() {
      DEPTH.field_179052_a.func_179200_b();
   }

   public static void depthFunc(int p_depthFunc_0_) {
      if (p_depthFunc_0_ != DEPTH.field_179051_c) {
         DEPTH.field_179051_c = p_depthFunc_0_;
         GL11.glDepthFunc(p_depthFunc_0_);
      }

   }

   public static void depthMask(boolean p_depthMask_0_) {
      if (p_depthMask_0_ != DEPTH.field_179050_b) {
         DEPTH.field_179050_b = p_depthMask_0_;
         GL11.glDepthMask(p_depthMask_0_);
      }

   }

   public static void disableBlend() {
      BLEND.field_179213_a.func_179198_a();
   }

   public static void enableBlend() {
      BLEND.field_179213_a.func_179200_b();
   }

   public static void blendFunc(GlStateManager.SourceFactor p_blendFunc_0_, GlStateManager.DestFactor p_blendFunc_1_) {
      blendFunc(p_blendFunc_0_.value, p_blendFunc_1_.value);
   }

   public static void blendFunc(int p_blendFunc_0_, int p_blendFunc_1_) {
      if (p_blendFunc_0_ != BLEND.field_179211_b || p_blendFunc_1_ != BLEND.field_179212_c) {
         BLEND.field_179211_b = p_blendFunc_0_;
         BLEND.field_179212_c = p_blendFunc_1_;
         GL11.glBlendFunc(p_blendFunc_0_, p_blendFunc_1_);
      }

   }

   public static void blendFuncSeparate(GlStateManager.SourceFactor p_blendFuncSeparate_0_, GlStateManager.DestFactor p_blendFuncSeparate_1_, GlStateManager.SourceFactor p_blendFuncSeparate_2_, GlStateManager.DestFactor p_blendFuncSeparate_3_) {
      blendFuncSeparate(p_blendFuncSeparate_0_.value, p_blendFuncSeparate_1_.value, p_blendFuncSeparate_2_.value, p_blendFuncSeparate_3_.value);
   }

   public static void blendFuncSeparate(int p_blendFuncSeparate_0_, int p_blendFuncSeparate_1_, int p_blendFuncSeparate_2_, int p_blendFuncSeparate_3_) {
      if (p_blendFuncSeparate_0_ != BLEND.field_179211_b || p_blendFuncSeparate_1_ != BLEND.field_179212_c || p_blendFuncSeparate_2_ != BLEND.field_179209_d || p_blendFuncSeparate_3_ != BLEND.field_179210_e) {
         BLEND.field_179211_b = p_blendFuncSeparate_0_;
         BLEND.field_179212_c = p_blendFuncSeparate_1_;
         BLEND.field_179209_d = p_blendFuncSeparate_2_;
         BLEND.field_179210_e = p_blendFuncSeparate_3_;
         GLX.glBlendFuncSeparate(p_blendFuncSeparate_0_, p_blendFuncSeparate_1_, p_blendFuncSeparate_2_, p_blendFuncSeparate_3_);
      }

   }

   public static void blendEquation(int p_blendEquation_0_) {
      GL14.glBlendEquation(p_blendEquation_0_);
   }

   public static void setupSolidRenderingTextureCombine(int p_setupSolidRenderingTextureCombine_0_) {
      COLOR_BUFFER.put(0, (float)(p_setupSolidRenderingTextureCombine_0_ >> 16 & 255) / 255.0F);
      COLOR_BUFFER.put(1, (float)(p_setupSolidRenderingTextureCombine_0_ >> 8 & 255) / 255.0F);
      COLOR_BUFFER.put(2, (float)(p_setupSolidRenderingTextureCombine_0_ >> 0 & 255) / 255.0F);
      COLOR_BUFFER.put(3, (float)(p_setupSolidRenderingTextureCombine_0_ >> 24 & 255) / 255.0F);
      texEnv(8960, 8705, COLOR_BUFFER);
      texEnv(8960, 8704, 34160);
      texEnv(8960, 34161, 7681);
      texEnv(8960, 34176, 34166);
      texEnv(8960, 34192, 768);
      texEnv(8960, 34162, 7681);
      texEnv(8960, 34184, 5890);
      texEnv(8960, 34200, 770);
   }

   public static void tearDownSolidRenderingTextureCombine() {
      texEnv(8960, 8704, 8448);
      texEnv(8960, 34161, 8448);
      texEnv(8960, 34162, 8448);
      texEnv(8960, 34176, 5890);
      texEnv(8960, 34184, 5890);
      texEnv(8960, 34192, 768);
      texEnv(8960, 34200, 770);
   }

   public static void enableFog() {
      FOG.field_179049_a.func_179200_b();
   }

   public static void disableFog() {
      FOG.field_179049_a.func_179198_a();
   }

   public static void fogMode(GlStateManager.FogMode p_fogMode_0_) {
      fogMode(p_fogMode_0_.field_187351_d);
   }

   private static void fogMode(int p_fogMode_0_) {
      if (p_fogMode_0_ != FOG.field_179047_b) {
         FOG.field_179047_b = p_fogMode_0_;
         GL11.glFogi(2917, p_fogMode_0_);
      }

   }

   public static void fogDensity(float p_fogDensity_0_) {
      if (p_fogDensity_0_ != FOG.field_179048_c) {
         FOG.field_179048_c = p_fogDensity_0_;
         GL11.glFogf(2914, p_fogDensity_0_);
      }

   }

   public static void fogStart(float p_fogStart_0_) {
      if (p_fogStart_0_ != FOG.field_179045_d) {
         FOG.field_179045_d = p_fogStart_0_;
         GL11.glFogf(2915, p_fogStart_0_);
      }

   }

   public static void fogEnd(float p_fogEnd_0_) {
      if (p_fogEnd_0_ != FOG.field_179046_e) {
         FOG.field_179046_e = p_fogEnd_0_;
         GL11.glFogf(2916, p_fogEnd_0_);
      }

   }

   public static void fog(int p_fog_0_, FloatBuffer p_fog_1_) {
      GL11.glFogfv(p_fog_0_, p_fog_1_);
   }

   public static void fogi(int p_fogi_0_, int p_fogi_1_) {
      GL11.glFogi(p_fogi_0_, p_fogi_1_);
   }

   public static void enableCull() {
      CULL.field_179054_a.func_179200_b();
   }

   public static void disableCull() {
      CULL.field_179054_a.func_179198_a();
   }

   public static void cullFace(GlStateManager.CullFace p_cullFace_0_) {
      cullFace(p_cullFace_0_.field_187328_d);
   }

   private static void cullFace(int p_cullFace_0_) {
      if (p_cullFace_0_ != CULL.field_179053_b) {
         CULL.field_179053_b = p_cullFace_0_;
         GL11.glCullFace(p_cullFace_0_);
      }

   }

   public static void polygonMode(int p_polygonMode_0_, int p_polygonMode_1_) {
      GL11.glPolygonMode(p_polygonMode_0_, p_polygonMode_1_);
   }

   public static void enablePolygonOffset() {
      POLY_OFFSET.field_179044_a.func_179200_b();
   }

   public static void disablePolygonOffset() {
      POLY_OFFSET.field_179044_a.func_179198_a();
   }

   public static void enableLineOffset() {
      POLY_OFFSET.field_179042_b.func_179200_b();
   }

   public static void disableLineOffset() {
      POLY_OFFSET.field_179042_b.func_179198_a();
   }

   public static void polygonOffset(float p_polygonOffset_0_, float p_polygonOffset_1_) {
      if (p_polygonOffset_0_ != POLY_OFFSET.field_179043_c || p_polygonOffset_1_ != POLY_OFFSET.field_179041_d) {
         POLY_OFFSET.field_179043_c = p_polygonOffset_0_;
         POLY_OFFSET.field_179041_d = p_polygonOffset_1_;
         GL11.glPolygonOffset(p_polygonOffset_0_, p_polygonOffset_1_);
      }

   }

   public static void enableColorLogicOp() {
      COLOR_LOGIC.field_179197_a.func_179200_b();
   }

   public static void disableColorLogicOp() {
      COLOR_LOGIC.field_179197_a.func_179198_a();
   }

   public static void logicOp(GlStateManager.LogicOp p_logicOp_0_) {
      logicOp(p_logicOp_0_.field_187370_q);
   }

   public static void logicOp(int p_logicOp_0_) {
      if (p_logicOp_0_ != COLOR_LOGIC.field_179196_b) {
         COLOR_LOGIC.field_179196_b = p_logicOp_0_;
         GL11.glLogicOp(p_logicOp_0_);
      }

   }

   public static void enableTexGen(GlStateManager.TexGen p_enableTexGen_0_) {
      getTexGen(p_enableTexGen_0_).field_179067_a.func_179200_b();
   }

   public static void disableTexGen(GlStateManager.TexGen p_disableTexGen_0_) {
      getTexGen(p_disableTexGen_0_).field_179067_a.func_179198_a();
   }

   public static void texGenMode(GlStateManager.TexGen p_texGenMode_0_, int p_texGenMode_1_) {
      GlStateManager.TexGenCoord glstatemanager$texgencoord = getTexGen(p_texGenMode_0_);
      if (p_texGenMode_1_ != glstatemanager$texgencoord.field_179066_c) {
         glstatemanager$texgencoord.field_179066_c = p_texGenMode_1_;
         GL11.glTexGeni(glstatemanager$texgencoord.field_179065_b, 9472, p_texGenMode_1_);
      }

   }

   public static void texGenParam(GlStateManager.TexGen p_texGenParam_0_, int p_texGenParam_1_, FloatBuffer p_texGenParam_2_) {
      GL11.glTexGenfv(getTexGen(p_texGenParam_0_).field_179065_b, p_texGenParam_1_, p_texGenParam_2_);
   }

   private static GlStateManager.TexGenCoord getTexGen(GlStateManager.TexGen p_getTexGen_0_) {
      switch(p_getTexGen_0_) {
      case S:
         return TEX_GEN.field_179064_a;
      case T:
         return TEX_GEN.field_179062_b;
      case R:
         return TEX_GEN.field_179063_c;
      case Q:
         return TEX_GEN.field_179061_d;
      default:
         return TEX_GEN.field_179064_a;
      }
   }

   public static void activeTexture(int p_activeTexture_0_) {
      if (activeTexture != p_activeTexture_0_ - GLX.GL_TEXTURE0) {
         activeTexture = p_activeTexture_0_ - GLX.GL_TEXTURE0;
         GLX.glActiveTexture(p_activeTexture_0_);
      }

   }

   public static void enableTexture() {
      TEXTURES[activeTexture].field_179060_a.func_179200_b();
   }

   public static void disableTexture() {
      TEXTURES[activeTexture].field_179060_a.func_179198_a();
   }

   public static void texEnv(int p_texEnv_0_, int p_texEnv_1_, FloatBuffer p_texEnv_2_) {
      GL11.glTexEnvfv(p_texEnv_0_, p_texEnv_1_, p_texEnv_2_);
   }

   public static void texEnv(int p_texEnv_0_, int p_texEnv_1_, int p_texEnv_2_) {
      GL11.glTexEnvi(p_texEnv_0_, p_texEnv_1_, p_texEnv_2_);
   }

   public static void texEnv(int p_texEnv_0_, int p_texEnv_1_, float p_texEnv_2_) {
      GL11.glTexEnvf(p_texEnv_0_, p_texEnv_1_, p_texEnv_2_);
   }

   public static void texParameter(int p_texParameter_0_, int p_texParameter_1_, float p_texParameter_2_) {
      GL11.glTexParameterf(p_texParameter_0_, p_texParameter_1_, p_texParameter_2_);
   }

   public static void texParameter(int p_texParameter_0_, int p_texParameter_1_, int p_texParameter_2_) {
      GL11.glTexParameteri(p_texParameter_0_, p_texParameter_1_, p_texParameter_2_);
   }

   public static int getTexLevelParameter(int p_getTexLevelParameter_0_, int p_getTexLevelParameter_1_, int p_getTexLevelParameter_2_) {
      return GL11.glGetTexLevelParameteri(p_getTexLevelParameter_0_, p_getTexLevelParameter_1_, p_getTexLevelParameter_2_);
   }

   public static int genTexture() {
      return GL11.glGenTextures();
   }

   public static void deleteTexture(int p_deleteTexture_0_) {
      GL11.glDeleteTextures(p_deleteTexture_0_);

      for(GlStateManager.TextureState glstatemanager$texturestate : TEXTURES) {
         if (glstatemanager$texturestate.field_179059_b == p_deleteTexture_0_) {
            glstatemanager$texturestate.field_179059_b = -1;
         }
      }

   }

   public static void bindTexture(int p_bindTexture_0_) {
      if (p_bindTexture_0_ != TEXTURES[activeTexture].field_179059_b) {
         TEXTURES[activeTexture].field_179059_b = p_bindTexture_0_;
         GL11.glBindTexture(3553, p_bindTexture_0_);
      }

   }

   public static void texImage2D(int p_texImage2D_0_, int p_texImage2D_1_, int p_texImage2D_2_, int p_texImage2D_3_, int p_texImage2D_4_, int p_texImage2D_5_, int p_texImage2D_6_, int p_texImage2D_7_, @Nullable IntBuffer p_texImage2D_8_) {
      GL11.glTexImage2D(p_texImage2D_0_, p_texImage2D_1_, p_texImage2D_2_, p_texImage2D_3_, p_texImage2D_4_, p_texImage2D_5_, p_texImage2D_6_, p_texImage2D_7_, p_texImage2D_8_);
   }

   public static void texSubImage2D(int p_texSubImage2D_0_, int p_texSubImage2D_1_, int p_texSubImage2D_2_, int p_texSubImage2D_3_, int p_texSubImage2D_4_, int p_texSubImage2D_5_, int p_texSubImage2D_6_, int p_texSubImage2D_7_, long p_texSubImage2D_8_) {
      GL11.glTexSubImage2D(p_texSubImage2D_0_, p_texSubImage2D_1_, p_texSubImage2D_2_, p_texSubImage2D_3_, p_texSubImage2D_4_, p_texSubImage2D_5_, p_texSubImage2D_6_, p_texSubImage2D_7_, p_texSubImage2D_8_);
   }

   public static void copyTexSubImage2D(int p_copyTexSubImage2D_0_, int p_copyTexSubImage2D_1_, int p_copyTexSubImage2D_2_, int p_copyTexSubImage2D_3_, int p_copyTexSubImage2D_4_, int p_copyTexSubImage2D_5_, int p_copyTexSubImage2D_6_, int p_copyTexSubImage2D_7_) {
      GL11.glCopyTexSubImage2D(p_copyTexSubImage2D_0_, p_copyTexSubImage2D_1_, p_copyTexSubImage2D_2_, p_copyTexSubImage2D_3_, p_copyTexSubImage2D_4_, p_copyTexSubImage2D_5_, p_copyTexSubImage2D_6_, p_copyTexSubImage2D_7_);
   }

   public static void getTexImage(int p_getTexImage_0_, int p_getTexImage_1_, int p_getTexImage_2_, int p_getTexImage_3_, long p_getTexImage_4_) {
      GL11.glGetTexImage(p_getTexImage_0_, p_getTexImage_1_, p_getTexImage_2_, p_getTexImage_3_, p_getTexImage_4_);
   }

   public static void enableNormalize() {
      NORMALIZE.func_179200_b();
   }

   public static void disableNormalize() {
      NORMALIZE.func_179198_a();
   }

   public static void shadeModel(int p_shadeModel_0_) {
      if (p_shadeModel_0_ != shadeModel) {
         shadeModel = p_shadeModel_0_;
         GL11.glShadeModel(p_shadeModel_0_);
      }

   }

   public static void enableRescaleNormal() {
      RESCALE_NORMAL.func_179200_b();
   }

   public static void disableRescaleNormal() {
      RESCALE_NORMAL.func_179198_a();
   }

   public static void viewport(int p_viewport_0_, int p_viewport_1_, int p_viewport_2_, int p_viewport_3_) {
      GlStateManager.Viewport.INSTANCE.field_199289_b = p_viewport_0_;
      GlStateManager.Viewport.INSTANCE.field_199290_c = p_viewport_1_;
      GlStateManager.Viewport.INSTANCE.field_199291_d = p_viewport_2_;
      GlStateManager.Viewport.INSTANCE.field_199292_e = p_viewport_3_;
      GL11.glViewport(p_viewport_0_, p_viewport_1_, p_viewport_2_, p_viewport_3_);
   }

   public static void colorMask(boolean p_colorMask_0_, boolean p_colorMask_1_, boolean p_colorMask_2_, boolean p_colorMask_3_) {
      if (p_colorMask_0_ != COLOR_MASK.field_179188_a || p_colorMask_1_ != COLOR_MASK.field_179186_b || p_colorMask_2_ != COLOR_MASK.field_179187_c || p_colorMask_3_ != COLOR_MASK.field_179185_d) {
         COLOR_MASK.field_179188_a = p_colorMask_0_;
         COLOR_MASK.field_179186_b = p_colorMask_1_;
         COLOR_MASK.field_179187_c = p_colorMask_2_;
         COLOR_MASK.field_179185_d = p_colorMask_3_;
         GL11.glColorMask(p_colorMask_0_, p_colorMask_1_, p_colorMask_2_, p_colorMask_3_);
      }

   }

   public static void stencilFunc(int p_stencilFunc_0_, int p_stencilFunc_1_, int p_stencilFunc_2_) {
      if (p_stencilFunc_0_ != STENCIL.field_179078_a.field_179081_a || p_stencilFunc_0_ != STENCIL.field_179078_a.field_212902_b || p_stencilFunc_0_ != STENCIL.field_179078_a.field_179080_c) {
         STENCIL.field_179078_a.field_179081_a = p_stencilFunc_0_;
         STENCIL.field_179078_a.field_212902_b = p_stencilFunc_1_;
         STENCIL.field_179078_a.field_179080_c = p_stencilFunc_2_;
         GL11.glStencilFunc(p_stencilFunc_0_, p_stencilFunc_1_, p_stencilFunc_2_);
      }

   }

   public static void stencilMask(int p_stencilMask_0_) {
      if (p_stencilMask_0_ != STENCIL.field_179076_b) {
         STENCIL.field_179076_b = p_stencilMask_0_;
         GL11.glStencilMask(p_stencilMask_0_);
      }

   }

   public static void stencilOp(int p_stencilOp_0_, int p_stencilOp_1_, int p_stencilOp_2_) {
      if (p_stencilOp_0_ != STENCIL.field_179077_c || p_stencilOp_1_ != STENCIL.field_179074_d || p_stencilOp_2_ != STENCIL.field_179075_e) {
         STENCIL.field_179077_c = p_stencilOp_0_;
         STENCIL.field_179074_d = p_stencilOp_1_;
         STENCIL.field_179075_e = p_stencilOp_2_;
         GL11.glStencilOp(p_stencilOp_0_, p_stencilOp_1_, p_stencilOp_2_);
      }

   }

   public static void clearDepth(double p_clearDepth_0_) {
      if (p_clearDepth_0_ != CLEAR.field_179205_a) {
         CLEAR.field_179205_a = p_clearDepth_0_;
         GL11.glClearDepth(p_clearDepth_0_);
      }

   }

   public static void clearColor(float p_clearColor_0_, float p_clearColor_1_, float p_clearColor_2_, float p_clearColor_3_) {
      if (p_clearColor_0_ != CLEAR.field_179203_b.field_179195_a || p_clearColor_1_ != CLEAR.field_179203_b.field_179193_b || p_clearColor_2_ != CLEAR.field_179203_b.field_179194_c || p_clearColor_3_ != CLEAR.field_179203_b.field_179192_d) {
         CLEAR.field_179203_b.field_179195_a = p_clearColor_0_;
         CLEAR.field_179203_b.field_179193_b = p_clearColor_1_;
         CLEAR.field_179203_b.field_179194_c = p_clearColor_2_;
         CLEAR.field_179203_b.field_179192_d = p_clearColor_3_;
         GL11.glClearColor(p_clearColor_0_, p_clearColor_1_, p_clearColor_2_, p_clearColor_3_);
      }

   }

   public static void clearStencil(int p_clearStencil_0_) {
      if (p_clearStencil_0_ != CLEAR.field_212901_c) {
         CLEAR.field_212901_c = p_clearStencil_0_;
         GL11.glClearStencil(p_clearStencil_0_);
      }

   }

   public static void clear(int p_clear_0_, boolean p_clear_1_) {
      GL11.glClear(p_clear_0_);
      if (p_clear_1_) {
         getError();
      }

   }

   public static void matrixMode(int p_matrixMode_0_) {
      GL11.glMatrixMode(p_matrixMode_0_);
   }

   public static void loadIdentity() {
      GL11.glLoadIdentity();
   }

   public static void pushMatrix() {
      GL11.glPushMatrix();
   }

   public static void popMatrix() {
      GL11.glPopMatrix();
   }

   public static void getMatrix(int p_getMatrix_0_, FloatBuffer p_getMatrix_1_) {
      GL11.glGetFloatv(p_getMatrix_0_, p_getMatrix_1_);
   }

   public static Matrix4f getMatrix4f(int p_getMatrix4f_0_) {
      GL11.glGetFloatv(p_getMatrix4f_0_, MATRIX_BUFFER);
      MATRIX_BUFFER.rewind();
      Matrix4f matrix4f = new Matrix4f();
      matrix4f.read(MATRIX_BUFFER);
      MATRIX_BUFFER.rewind();
      return matrix4f;
   }

   public static void ortho(double p_ortho_0_, double p_ortho_2_, double p_ortho_4_, double p_ortho_6_, double p_ortho_8_, double p_ortho_10_) {
      GL11.glOrtho(p_ortho_0_, p_ortho_2_, p_ortho_4_, p_ortho_6_, p_ortho_8_, p_ortho_10_);
   }

   public static void rotatef(float p_rotatef_0_, float p_rotatef_1_, float p_rotatef_2_, float p_rotatef_3_) {
      GL11.glRotatef(p_rotatef_0_, p_rotatef_1_, p_rotatef_2_, p_rotatef_3_);
   }

   public static void rotated(double p_rotated_0_, double p_rotated_2_, double p_rotated_4_, double p_rotated_6_) {
      GL11.glRotated(p_rotated_0_, p_rotated_2_, p_rotated_4_, p_rotated_6_);
   }

   public static void scalef(float p_scalef_0_, float p_scalef_1_, float p_scalef_2_) {
      GL11.glScalef(p_scalef_0_, p_scalef_1_, p_scalef_2_);
   }

   public static void scaled(double p_scaled_0_, double p_scaled_2_, double p_scaled_4_) {
      GL11.glScaled(p_scaled_0_, p_scaled_2_, p_scaled_4_);
   }

   public static void translatef(float p_translatef_0_, float p_translatef_1_, float p_translatef_2_) {
      GL11.glTranslatef(p_translatef_0_, p_translatef_1_, p_translatef_2_);
   }

   public static void translated(double p_translated_0_, double p_translated_2_, double p_translated_4_) {
      GL11.glTranslated(p_translated_0_, p_translated_2_, p_translated_4_);
   }

   public static void multMatrix(FloatBuffer p_multMatrix_0_) {
      GL11.glMultMatrixf(p_multMatrix_0_);
   }

   public static void multMatrix(Matrix4f p_multMatrix_0_) {
      p_multMatrix_0_.write(MATRIX_BUFFER);
      MATRIX_BUFFER.rewind();
      GL11.glMultMatrixf(MATRIX_BUFFER);
   }

   public static void color4f(float p_color4f_0_, float p_color4f_1_, float p_color4f_2_, float p_color4f_3_) {
      if (p_color4f_0_ != COLOR.field_179195_a || p_color4f_1_ != COLOR.field_179193_b || p_color4f_2_ != COLOR.field_179194_c || p_color4f_3_ != COLOR.field_179192_d) {
         COLOR.field_179195_a = p_color4f_0_;
         COLOR.field_179193_b = p_color4f_1_;
         COLOR.field_179194_c = p_color4f_2_;
         COLOR.field_179192_d = p_color4f_3_;
         GL11.glColor4f(p_color4f_0_, p_color4f_1_, p_color4f_2_, p_color4f_3_);
      }

   }

   public static void color3f(float p_color3f_0_, float p_color3f_1_, float p_color3f_2_) {
      color4f(p_color3f_0_, p_color3f_1_, p_color3f_2_, 1.0F);
   }

   public static void texCoord2f(float p_texCoord2f_0_, float p_texCoord2f_1_) {
      GL11.glTexCoord2f(p_texCoord2f_0_, p_texCoord2f_1_);
   }

   public static void vertex3f(float p_vertex3f_0_, float p_vertex3f_1_, float p_vertex3f_2_) {
      GL11.glVertex3f(p_vertex3f_0_, p_vertex3f_1_, p_vertex3f_2_);
   }

   public static void clearCurrentColor() {
      COLOR.field_179195_a = -1.0F;
      COLOR.field_179193_b = -1.0F;
      COLOR.field_179194_c = -1.0F;
      COLOR.field_179192_d = -1.0F;
   }

   public static void normalPointer(int p_normalPointer_0_, int p_normalPointer_1_, int p_normalPointer_2_) {
      GL11.glNormalPointer(p_normalPointer_0_, p_normalPointer_1_, (long)p_normalPointer_2_);
   }

   public static void normalPointer(int p_normalPointer_0_, int p_normalPointer_1_, ByteBuffer p_normalPointer_2_) {
      GL11.glNormalPointer(p_normalPointer_0_, p_normalPointer_1_, p_normalPointer_2_);
   }

   public static void texCoordPointer(int p_texCoordPointer_0_, int p_texCoordPointer_1_, int p_texCoordPointer_2_, int p_texCoordPointer_3_) {
      GL11.glTexCoordPointer(p_texCoordPointer_0_, p_texCoordPointer_1_, p_texCoordPointer_2_, (long)p_texCoordPointer_3_);
   }

   public static void texCoordPointer(int p_texCoordPointer_0_, int p_texCoordPointer_1_, int p_texCoordPointer_2_, ByteBuffer p_texCoordPointer_3_) {
      GL11.glTexCoordPointer(p_texCoordPointer_0_, p_texCoordPointer_1_, p_texCoordPointer_2_, p_texCoordPointer_3_);
   }

   public static void vertexPointer(int p_vertexPointer_0_, int p_vertexPointer_1_, int p_vertexPointer_2_, int p_vertexPointer_3_) {
      GL11.glVertexPointer(p_vertexPointer_0_, p_vertexPointer_1_, p_vertexPointer_2_, (long)p_vertexPointer_3_);
   }

   public static void vertexPointer(int p_vertexPointer_0_, int p_vertexPointer_1_, int p_vertexPointer_2_, ByteBuffer p_vertexPointer_3_) {
      GL11.glVertexPointer(p_vertexPointer_0_, p_vertexPointer_1_, p_vertexPointer_2_, p_vertexPointer_3_);
   }

   public static void colorPointer(int p_colorPointer_0_, int p_colorPointer_1_, int p_colorPointer_2_, int p_colorPointer_3_) {
      GL11.glColorPointer(p_colorPointer_0_, p_colorPointer_1_, p_colorPointer_2_, (long)p_colorPointer_3_);
   }

   public static void colorPointer(int p_colorPointer_0_, int p_colorPointer_1_, int p_colorPointer_2_, ByteBuffer p_colorPointer_3_) {
      GL11.glColorPointer(p_colorPointer_0_, p_colorPointer_1_, p_colorPointer_2_, p_colorPointer_3_);
   }

   public static void disableClientState(int p_disableClientState_0_) {
      GL11.glDisableClientState(p_disableClientState_0_);
   }

   public static void enableClientState(int p_enableClientState_0_) {
      GL11.glEnableClientState(p_enableClientState_0_);
   }

   public static void begin(int p_begin_0_) {
      GL11.glBegin(p_begin_0_);
   }

   public static void end() {
      GL11.glEnd();
   }

   public static void drawArrays(int p_drawArrays_0_, int p_drawArrays_1_, int p_drawArrays_2_) {
      GL11.glDrawArrays(p_drawArrays_0_, p_drawArrays_1_, p_drawArrays_2_);
   }

   public static void lineWidth(float p_lineWidth_0_) {
      GL11.glLineWidth(p_lineWidth_0_);
   }

   public static void callList(int p_callList_0_) {
      GL11.glCallList(p_callList_0_);
   }

   public static void deleteLists(int p_deleteLists_0_, int p_deleteLists_1_) {
      GL11.glDeleteLists(p_deleteLists_0_, p_deleteLists_1_);
   }

   public static void newList(int p_newList_0_, int p_newList_1_) {
      GL11.glNewList(p_newList_0_, p_newList_1_);
   }

   public static void endList() {
      GL11.glEndList();
   }

   public static int genLists(int p_genLists_0_) {
      return GL11.glGenLists(p_genLists_0_);
   }

   public static void pixelStore(int p_pixelStore_0_, int p_pixelStore_1_) {
      GL11.glPixelStorei(p_pixelStore_0_, p_pixelStore_1_);
   }

   public static void pixelTransfer(int p_pixelTransfer_0_, float p_pixelTransfer_1_) {
      GL11.glPixelTransferf(p_pixelTransfer_0_, p_pixelTransfer_1_);
   }

   public static void readPixels(int p_readPixels_0_, int p_readPixels_1_, int p_readPixels_2_, int p_readPixels_3_, int p_readPixels_4_, int p_readPixels_5_, ByteBuffer p_readPixels_6_) {
      GL11.glReadPixels(p_readPixels_0_, p_readPixels_1_, p_readPixels_2_, p_readPixels_3_, p_readPixels_4_, p_readPixels_5_, p_readPixels_6_);
   }

   public static void readPixels(int p_readPixels_0_, int p_readPixels_1_, int p_readPixels_2_, int p_readPixels_3_, int p_readPixels_4_, int p_readPixels_5_, long p_readPixels_6_) {
      GL11.glReadPixels(p_readPixels_0_, p_readPixels_1_, p_readPixels_2_, p_readPixels_3_, p_readPixels_4_, p_readPixels_5_, p_readPixels_6_);
   }

   public static int getError() {
      return GL11.glGetError();
   }

   public static String getString(int p_getString_0_) {
      return GL11.glGetString(p_getString_0_);
   }

   public static void getInteger(int p_getInteger_0_, IntBuffer p_getInteger_1_) {
      GL11.glGetIntegerv(p_getInteger_0_, p_getInteger_1_);
   }

   public static int getInteger(int p_getInteger_0_) {
      return GL11.glGetInteger(p_getInteger_0_);
   }

   public static void setProfile(GlStateManager.Profile p_setProfile_0_) {
      p_setProfile_0_.func_187373_a();
   }

   public static void unsetProfile(GlStateManager.Profile p_unsetProfile_0_) {
      p_unsetProfile_0_.func_187374_b();
   }

   @OnlyIn(Dist.CLIENT)
   static class AlphaState {
      public final GlStateManager.BooleanState field_179208_a = new GlStateManager.BooleanState(3008);
      public int field_179206_b = 519;
      public float field_179207_c = -1.0F;

      private AlphaState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class BlendState {
      public final GlStateManager.BooleanState field_179213_a = new GlStateManager.BooleanState(3042);
      public int field_179211_b = 1;
      public int field_179212_c = 0;
      public int field_179209_d = 1;
      public int field_179210_e = 0;

      private BlendState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class BooleanState {
      private final int field_179202_a;
      private boolean field_179201_b;

      public BooleanState(int p_i50871_1_) {
         this.field_179202_a = p_i50871_1_;
      }

      public void func_179198_a() {
         this.func_179199_a(false);
      }

      public void func_179200_b() {
         this.func_179199_a(true);
      }

      public void func_179199_a(boolean p_179199_1_) {
         if (p_179199_1_ != this.field_179201_b) {
            this.field_179201_b = p_179199_1_;
            if (p_179199_1_) {
               GL11.glEnable(this.field_179202_a);
            } else {
               GL11.glDisable(this.field_179202_a);
            }
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   static class ClearState {
      public double field_179205_a = 1.0D;
      public final GlStateManager.Color field_179203_b = new GlStateManager.Color(0.0F, 0.0F, 0.0F, 0.0F);
      public int field_212901_c;

      private ClearState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class Color {
      public float field_179195_a = 1.0F;
      public float field_179193_b = 1.0F;
      public float field_179194_c = 1.0F;
      public float field_179192_d = 1.0F;

      public Color() {
         this(1.0F, 1.0F, 1.0F, 1.0F);
      }

      public Color(float p_i50869_1_, float p_i50869_2_, float p_i50869_3_, float p_i50869_4_) {
         this.field_179195_a = p_i50869_1_;
         this.field_179193_b = p_i50869_2_;
         this.field_179194_c = p_i50869_3_;
         this.field_179192_d = p_i50869_4_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class ColorLogicState {
      public final GlStateManager.BooleanState field_179197_a = new GlStateManager.BooleanState(3058);
      public int field_179196_b = 5379;

      private ColorLogicState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class ColorMask {
      public boolean field_179188_a = true;
      public boolean field_179186_b = true;
      public boolean field_179187_c = true;
      public boolean field_179185_d = true;

      private ColorMask() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class ColorMaterialState {
      public final GlStateManager.BooleanState field_179191_a = new GlStateManager.BooleanState(2903);
      public int field_179189_b = 1032;
      public int field_179190_c = 5634;

      private ColorMaterialState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum CullFace {
      FRONT(1028),
      BACK(1029),
      FRONT_AND_BACK(1032);

      public final int field_187328_d;

      private CullFace(int p_i50865_3_) {
         this.field_187328_d = p_i50865_3_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class CullState {
      public final GlStateManager.BooleanState field_179054_a = new GlStateManager.BooleanState(2884);
      public int field_179053_b = 1029;

      private CullState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class DepthState {
      public final GlStateManager.BooleanState field_179052_a = new GlStateManager.BooleanState(2929);
      public boolean field_179050_b = true;
      public int field_179051_c = 513;

      private DepthState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum DestFactor {
      CONSTANT_ALPHA(32771),
      CONSTANT_COLOR(32769),
      DST_ALPHA(772),
      DST_COLOR(774),
      ONE(1),
      ONE_MINUS_CONSTANT_ALPHA(32772),
      ONE_MINUS_CONSTANT_COLOR(32770),
      ONE_MINUS_DST_ALPHA(773),
      ONE_MINUS_DST_COLOR(775),
      ONE_MINUS_SRC_ALPHA(771),
      ONE_MINUS_SRC_COLOR(769),
      SRC_ALPHA(770),
      SRC_COLOR(768),
      ZERO(0);

      public final int value;

      private DestFactor(int p_i51106_3_) {
         this.value = p_i51106_3_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum FogMode {
      LINEAR(9729),
      EXP(2048),
      EXP2(2049);

      public final int field_187351_d;

      private FogMode(int p_i50862_3_) {
         this.field_187351_d = p_i50862_3_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class FogState {
      public final GlStateManager.BooleanState field_179049_a = new GlStateManager.BooleanState(2912);
      public int field_179047_b = 2048;
      public float field_179048_c = 1.0F;
      public float field_179045_d;
      public float field_179046_e = 1.0F;

      private FogState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum LogicOp {
      AND(5377),
      AND_INVERTED(5380),
      AND_REVERSE(5378),
      CLEAR(5376),
      COPY(5379),
      COPY_INVERTED(5388),
      EQUIV(5385),
      INVERT(5386),
      NAND(5390),
      NOOP(5381),
      NOR(5384),
      OR(5383),
      OR_INVERTED(5389),
      OR_REVERSE(5387),
      SET(5391),
      XOR(5382);

      public final int field_187370_q;

      private LogicOp(int p_i50860_3_) {
         this.field_187370_q = p_i50860_3_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class PolygonOffsetState {
      public final GlStateManager.BooleanState field_179044_a = new GlStateManager.BooleanState(32823);
      public final GlStateManager.BooleanState field_179042_b = new GlStateManager.BooleanState(10754);
      public float field_179043_c;
      public float field_179041_d;

      private PolygonOffsetState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Profile {
      DEFAULT {
         public void func_187373_a() {
            GlStateManager.disableAlphaTest();
            GlStateManager.alphaFunc(519, 0.0F);
            GlStateManager.disableLighting();
            GlStateManager.lightModel(2899, RenderHelper.setColorBuffer(0.2F, 0.2F, 0.2F, 1.0F));

            for(int i = 0; i < 8; ++i) {
               GlStateManager.disableLight(i);
               GlStateManager.light(16384 + i, 4608, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
               GlStateManager.light(16384 + i, 4611, RenderHelper.setColorBuffer(0.0F, 0.0F, 1.0F, 0.0F));
               if (i == 0) {
                  GlStateManager.light(16384 + i, 4609, RenderHelper.setColorBuffer(1.0F, 1.0F, 1.0F, 1.0F));
                  GlStateManager.light(16384 + i, 4610, RenderHelper.setColorBuffer(1.0F, 1.0F, 1.0F, 1.0F));
               } else {
                  GlStateManager.light(16384 + i, 4609, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                  GlStateManager.light(16384 + i, 4610, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
               }
            }

            GlStateManager.disableColorMaterial();
            GlStateManager.colorMaterial(1032, 5634);
            GlStateManager.disableDepthTest();
            GlStateManager.depthFunc(513);
            GlStateManager.depthMask(true);
            GlStateManager.disableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendEquation(32774);
            GlStateManager.disableFog();
            GlStateManager.fogi(2917, 2048);
            GlStateManager.fogDensity(1.0F);
            GlStateManager.fogStart(0.0F);
            GlStateManager.fogEnd(1.0F);
            GlStateManager.fog(2918, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 0.0F));
            if (GL.getCapabilities().GL_NV_fog_distance) {
               GlStateManager.fogi(2917, 34140);
            }

            GlStateManager.polygonOffset(0.0F, 0.0F);
            GlStateManager.disableColorLogicOp();
            GlStateManager.logicOp(5379);
            GlStateManager.disableTexGen(GlStateManager.TexGen.S);
            GlStateManager.texGenMode(GlStateManager.TexGen.S, 9216);
            GlStateManager.texGenParam(GlStateManager.TexGen.S, 9474, RenderHelper.setColorBuffer(1.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.texGenParam(GlStateManager.TexGen.S, 9217, RenderHelper.setColorBuffer(1.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.disableTexGen(GlStateManager.TexGen.T);
            GlStateManager.texGenMode(GlStateManager.TexGen.T, 9216);
            GlStateManager.texGenParam(GlStateManager.TexGen.T, 9474, RenderHelper.setColorBuffer(0.0F, 1.0F, 0.0F, 0.0F));
            GlStateManager.texGenParam(GlStateManager.TexGen.T, 9217, RenderHelper.setColorBuffer(0.0F, 1.0F, 0.0F, 0.0F));
            GlStateManager.disableTexGen(GlStateManager.TexGen.R);
            GlStateManager.texGenMode(GlStateManager.TexGen.R, 9216);
            GlStateManager.texGenParam(GlStateManager.TexGen.R, 9474, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.texGenParam(GlStateManager.TexGen.R, 9217, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.disableTexGen(GlStateManager.TexGen.Q);
            GlStateManager.texGenMode(GlStateManager.TexGen.Q, 9216);
            GlStateManager.texGenParam(GlStateManager.TexGen.Q, 9474, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.texGenParam(GlStateManager.TexGen.Q, 9217, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.activeTexture(0);
            GlStateManager.texParameter(3553, 10240, 9729);
            GlStateManager.texParameter(3553, 10241, 9986);
            GlStateManager.texParameter(3553, 10242, 10497);
            GlStateManager.texParameter(3553, 10243, 10497);
            GlStateManager.texParameter(3553, 33085, 1000);
            GlStateManager.texParameter(3553, 33083, 1000);
            GlStateManager.texParameter(3553, 33082, -1000);
            GlStateManager.texParameter(3553, 34049, 0.0F);
            GlStateManager.texEnv(8960, 8704, 8448);
            GlStateManager.texEnv(8960, 8705, RenderHelper.setColorBuffer(0.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.texEnv(8960, 34161, 8448);
            GlStateManager.texEnv(8960, 34162, 8448);
            GlStateManager.texEnv(8960, 34176, 5890);
            GlStateManager.texEnv(8960, 34177, 34168);
            GlStateManager.texEnv(8960, 34178, 34166);
            GlStateManager.texEnv(8960, 34184, 5890);
            GlStateManager.texEnv(8960, 34185, 34168);
            GlStateManager.texEnv(8960, 34186, 34166);
            GlStateManager.texEnv(8960, 34192, 768);
            GlStateManager.texEnv(8960, 34193, 768);
            GlStateManager.texEnv(8960, 34194, 770);
            GlStateManager.texEnv(8960, 34200, 770);
            GlStateManager.texEnv(8960, 34201, 770);
            GlStateManager.texEnv(8960, 34202, 770);
            GlStateManager.texEnv(8960, 34163, 1.0F);
            GlStateManager.texEnv(8960, 3356, 1.0F);
            GlStateManager.disableNormalize();
            GlStateManager.shadeModel(7425);
            GlStateManager.disableRescaleNormal();
            GlStateManager.colorMask(true, true, true, true);
            GlStateManager.clearDepth(1.0D);
            GlStateManager.lineWidth(1.0F);
            GlStateManager.normal3f(0.0F, 0.0F, 1.0F);
            GlStateManager.polygonMode(1028, 6914);
            GlStateManager.polygonMode(1029, 6914);
         }

         public void func_187374_b() {
         }
      },
      PLAYER_SKIN {
         public void func_187373_a() {
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(770, 771, 1, 0);
         }

         public void func_187374_b() {
            GlStateManager.disableBlend();
         }
      },
      TRANSPARENT_MODEL {
         public void func_187373_a() {
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.15F);
            GlStateManager.depthMask(false);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.alphaFunc(516, 0.003921569F);
         }

         public void func_187374_b() {
            GlStateManager.disableBlend();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.depthMask(true);
         }
      };

      private Profile() {
      }

      public abstract void func_187373_a();

      public abstract void func_187374_b();
   }

   @OnlyIn(Dist.CLIENT)
   public static enum SourceFactor {
      CONSTANT_ALPHA(32771),
      CONSTANT_COLOR(32769),
      DST_ALPHA(772),
      DST_COLOR(774),
      ONE(1),
      ONE_MINUS_CONSTANT_ALPHA(32772),
      ONE_MINUS_CONSTANT_COLOR(32770),
      ONE_MINUS_DST_ALPHA(773),
      ONE_MINUS_DST_COLOR(775),
      ONE_MINUS_SRC_ALPHA(771),
      ONE_MINUS_SRC_COLOR(769),
      SRC_ALPHA(770),
      SRC_ALPHA_SATURATE(776),
      SRC_COLOR(768),
      ZERO(0);

      public final int value;

      private SourceFactor(int p_i50328_3_) {
         this.value = p_i50328_3_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class StencilFunc {
      public int field_179081_a = 519;
      public int field_212902_b;
      public int field_179080_c = -1;

      private StencilFunc() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class StencilState {
      public final GlStateManager.StencilFunc field_179078_a = new GlStateManager.StencilFunc();
      public int field_179076_b = -1;
      public int field_179077_c = 7680;
      public int field_179074_d = 7680;
      public int field_179075_e = 7680;

      private StencilState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum TexGen {
      S,
      T,
      R,
      Q;
   }

   @OnlyIn(Dist.CLIENT)
   static class TexGenCoord {
      public final GlStateManager.BooleanState field_179067_a;
      public final int field_179065_b;
      public int field_179066_c = -1;

      public TexGenCoord(int p_i50853_1_, int p_i50853_2_) {
         this.field_179065_b = p_i50853_1_;
         this.field_179067_a = new GlStateManager.BooleanState(p_i50853_2_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class TexGenState {
      public final GlStateManager.TexGenCoord field_179064_a = new GlStateManager.TexGenCoord(8192, 3168);
      public final GlStateManager.TexGenCoord field_179062_b = new GlStateManager.TexGenCoord(8193, 3169);
      public final GlStateManager.TexGenCoord field_179063_c = new GlStateManager.TexGenCoord(8194, 3170);
      public final GlStateManager.TexGenCoord field_179061_d = new GlStateManager.TexGenCoord(8195, 3171);

      private TexGenState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class TextureState {
      public final GlStateManager.BooleanState field_179060_a = new GlStateManager.BooleanState(3553);
      public int field_179059_b;

      private TextureState() {
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Viewport {
      INSTANCE;

      protected int field_199289_b;
      protected int field_199290_c;
      protected int field_199291_d;
      protected int field_199292_e;
   }
}