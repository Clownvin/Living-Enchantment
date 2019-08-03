package com.mojang.blaze3d.platform;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import net.minecraft.client.MainWindow;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.IDataHolder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.EXTBlendFuncSeparate;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryUtil;
import oshi.SystemInfo;
import oshi.hardware.Processor;

@OnlyIn(Dist.CLIENT)
public class GLX {
   private static final Logger LOGGER = LogManager.getLogger();
   public static boolean isNvidia;
   public static boolean isAmd;
   public static int GL_FRAMEBUFFER;
   public static int GL_RENDERBUFFER;
   public static int GL_COLOR_ATTACHMENT0;
   public static int GL_DEPTH_ATTACHMENT;
   public static int GL_FRAMEBUFFER_COMPLETE;
   public static int GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT;
   public static int GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT;
   public static int GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER;
   public static int GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER;
   private static GLX.FboMode fboMode;
   public static final boolean useFbo = true;
   private static boolean hasShaders;
   private static boolean useShaderArb;
   public static int GL_LINK_STATUS;
   public static int GL_COMPILE_STATUS;
   public static int GL_VERTEX_SHADER;
   public static int GL_FRAGMENT_SHADER;
   private static boolean useMultitextureArb;
   public static int GL_TEXTURE0;
   public static int GL_TEXTURE1;
   public static int GL_TEXTURE2;
   private static boolean useTexEnvCombineArb;
   public static int GL_COMBINE;
   public static int GL_INTERPOLATE;
   public static int GL_PRIMARY_COLOR;
   public static int GL_CONSTANT;
   public static int GL_PREVIOUS;
   public static int GL_COMBINE_RGB;
   public static int GL_SOURCE0_RGB;
   public static int GL_SOURCE1_RGB;
   public static int GL_SOURCE2_RGB;
   public static int GL_OPERAND0_RGB;
   public static int GL_OPERAND1_RGB;
   public static int GL_OPERAND2_RGB;
   public static int GL_COMBINE_ALPHA;
   public static int GL_SOURCE0_ALPHA;
   public static int GL_SOURCE1_ALPHA;
   public static int GL_SOURCE2_ALPHA;
   public static int GL_OPERAND0_ALPHA;
   public static int GL_OPERAND1_ALPHA;
   public static int GL_OPERAND2_ALPHA;
   private static boolean separateBlend;
   public static boolean useSeparateBlendExt;
   public static boolean isOpenGl21;
   public static boolean usePostProcess;
   private static String capsString = "";
   private static String cpuInfo;
   public static final boolean useVbo = true;
   public static boolean needVbo;
   private static boolean useVboArb;
   public static int GL_ARRAY_BUFFER;
   public static int GL_STATIC_DRAW;
   private static final Map<Integer, String> LOOKUP_MAP = make(Maps.newHashMap(), (p_212906_0_) -> {
      p_212906_0_.put(0, "No error");
      p_212906_0_.put(1280, "Enum parameter is invalid for this function");
      p_212906_0_.put(1281, "Parameter is invalid for this function");
      p_212906_0_.put(1282, "Current state is invalid for this function");
      p_212906_0_.put(1283, "Stack overflow");
      p_212906_0_.put(1284, "Stack underflow");
      p_212906_0_.put(1285, "Out of memory");
      p_212906_0_.put(1286, "Operation on incomplete framebuffer");
      p_212906_0_.put(1286, "Operation on incomplete framebuffer");
   });

   /* Stores the last values sent into glMultiTexCoord2f */
   public static float lastBrightnessX = 0.0f;
   public static float lastBrightnessY = 0.0f;

   public static void populateSnooperWithOpenGL(IDataHolder p_populateSnooperWithOpenGL_0_) {
      p_populateSnooperWithOpenGL_0_.setFixedData("opengl_version", GlStateManager.getString(7938));
      p_populateSnooperWithOpenGL_0_.setFixedData("opengl_vendor", GlStateManager.getString(7936));
      GLCapabilities glcapabilities = GL.getCapabilities();
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_arrays_of_arrays]", glcapabilities.GL_ARB_arrays_of_arrays);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_base_instance]", glcapabilities.GL_ARB_base_instance);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_blend_func_extended]", glcapabilities.GL_ARB_blend_func_extended);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_clear_buffer_object]", glcapabilities.GL_ARB_clear_buffer_object);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_color_buffer_float]", glcapabilities.GL_ARB_color_buffer_float);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_compatibility]", glcapabilities.GL_ARB_compatibility);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_compressed_texture_pixel_storage]", glcapabilities.GL_ARB_compressed_texture_pixel_storage);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_compute_shader]", glcapabilities.GL_ARB_compute_shader);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_copy_buffer]", glcapabilities.GL_ARB_copy_buffer);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_copy_image]", glcapabilities.GL_ARB_copy_image);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_depth_buffer_float]", glcapabilities.GL_ARB_depth_buffer_float);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_compute_shader]", glcapabilities.GL_ARB_compute_shader);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_copy_buffer]", glcapabilities.GL_ARB_copy_buffer);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_copy_image]", glcapabilities.GL_ARB_copy_image);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_depth_buffer_float]", glcapabilities.GL_ARB_depth_buffer_float);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_depth_clamp]", glcapabilities.GL_ARB_depth_clamp);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_depth_texture]", glcapabilities.GL_ARB_depth_texture);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_draw_buffers]", glcapabilities.GL_ARB_draw_buffers);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_draw_buffers_blend]", glcapabilities.GL_ARB_draw_buffers_blend);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_draw_elements_base_vertex]", glcapabilities.GL_ARB_draw_elements_base_vertex);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_draw_indirect]", glcapabilities.GL_ARB_draw_indirect);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_draw_instanced]", glcapabilities.GL_ARB_draw_instanced);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_explicit_attrib_location]", glcapabilities.GL_ARB_explicit_attrib_location);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_explicit_uniform_location]", glcapabilities.GL_ARB_explicit_uniform_location);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_fragment_layer_viewport]", glcapabilities.GL_ARB_fragment_layer_viewport);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_fragment_program]", glcapabilities.GL_ARB_fragment_program);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_fragment_shader]", glcapabilities.GL_ARB_fragment_shader);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_fragment_program_shadow]", glcapabilities.GL_ARB_fragment_program_shadow);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_framebuffer_object]", glcapabilities.GL_ARB_framebuffer_object);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_framebuffer_sRGB]", glcapabilities.GL_ARB_framebuffer_sRGB);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_geometry_shader4]", glcapabilities.GL_ARB_geometry_shader4);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_gpu_shader5]", glcapabilities.GL_ARB_gpu_shader5);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_half_float_pixel]", glcapabilities.GL_ARB_half_float_pixel);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_half_float_vertex]", glcapabilities.GL_ARB_half_float_vertex);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_instanced_arrays]", glcapabilities.GL_ARB_instanced_arrays);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_map_buffer_alignment]", glcapabilities.GL_ARB_map_buffer_alignment);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_map_buffer_range]", glcapabilities.GL_ARB_map_buffer_range);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_multisample]", glcapabilities.GL_ARB_multisample);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_multitexture]", glcapabilities.GL_ARB_multitexture);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_occlusion_query2]", glcapabilities.GL_ARB_occlusion_query2);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_pixel_buffer_object]", glcapabilities.GL_ARB_pixel_buffer_object);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_seamless_cube_map]", glcapabilities.GL_ARB_seamless_cube_map);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_shader_objects]", glcapabilities.GL_ARB_shader_objects);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_shader_stencil_export]", glcapabilities.GL_ARB_shader_stencil_export);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_shader_texture_lod]", glcapabilities.GL_ARB_shader_texture_lod);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_shadow]", glcapabilities.GL_ARB_shadow);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_shadow_ambient]", glcapabilities.GL_ARB_shadow_ambient);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_stencil_texturing]", glcapabilities.GL_ARB_stencil_texturing);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_sync]", glcapabilities.GL_ARB_sync);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_tessellation_shader]", glcapabilities.GL_ARB_tessellation_shader);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_texture_border_clamp]", glcapabilities.GL_ARB_texture_border_clamp);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_texture_buffer_object]", glcapabilities.GL_ARB_texture_buffer_object);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_texture_cube_map]", glcapabilities.GL_ARB_texture_cube_map);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_texture_cube_map_array]", glcapabilities.GL_ARB_texture_cube_map_array);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_texture_non_power_of_two]", glcapabilities.GL_ARB_texture_non_power_of_two);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_uniform_buffer_object]", glcapabilities.GL_ARB_uniform_buffer_object);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_vertex_blend]", glcapabilities.GL_ARB_vertex_blend);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_vertex_buffer_object]", glcapabilities.GL_ARB_vertex_buffer_object);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_vertex_program]", glcapabilities.GL_ARB_vertex_program);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_vertex_shader]", glcapabilities.GL_ARB_vertex_shader);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[EXT_bindable_uniform]", glcapabilities.GL_EXT_bindable_uniform);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[EXT_blend_equation_separate]", glcapabilities.GL_EXT_blend_equation_separate);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[EXT_blend_func_separate]", glcapabilities.GL_EXT_blend_func_separate);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[EXT_blend_minmax]", glcapabilities.GL_EXT_blend_minmax);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[EXT_blend_subtract]", glcapabilities.GL_EXT_blend_subtract);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[EXT_draw_instanced]", glcapabilities.GL_EXT_draw_instanced);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[EXT_framebuffer_multisample]", glcapabilities.GL_EXT_framebuffer_multisample);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[EXT_framebuffer_object]", glcapabilities.GL_EXT_framebuffer_object);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[EXT_framebuffer_sRGB]", glcapabilities.GL_EXT_framebuffer_sRGB);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[EXT_geometry_shader4]", glcapabilities.GL_EXT_geometry_shader4);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[EXT_gpu_program_parameters]", glcapabilities.GL_EXT_gpu_program_parameters);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[EXT_gpu_shader4]", glcapabilities.GL_EXT_gpu_shader4);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[EXT_packed_depth_stencil]", glcapabilities.GL_EXT_packed_depth_stencil);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[EXT_separate_shader_objects]", glcapabilities.GL_EXT_separate_shader_objects);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[EXT_shader_image_load_store]", glcapabilities.GL_EXT_shader_image_load_store);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[EXT_shadow_funcs]", glcapabilities.GL_EXT_shadow_funcs);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[EXT_shared_texture_palette]", glcapabilities.GL_EXT_shared_texture_palette);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[EXT_stencil_clear_tag]", glcapabilities.GL_EXT_stencil_clear_tag);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[EXT_stencil_two_side]", glcapabilities.GL_EXT_stencil_two_side);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[EXT_stencil_wrap]", glcapabilities.GL_EXT_stencil_wrap);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[EXT_texture_array]", glcapabilities.GL_EXT_texture_array);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[EXT_texture_buffer_object]", glcapabilities.GL_EXT_texture_buffer_object);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[EXT_texture_integer]", glcapabilities.GL_EXT_texture_integer);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[EXT_texture_sRGB]", glcapabilities.GL_EXT_texture_sRGB);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[ARB_vertex_shader]", glcapabilities.GL_ARB_vertex_shader);
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[gl_max_vertex_uniforms]", GlStateManager.getInteger(35658));
      GlStateManager.getError();
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[gl_max_fragment_uniforms]", GlStateManager.getInteger(35657));
      GlStateManager.getError();
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[gl_max_vertex_attribs]", GlStateManager.getInteger(34921));
      GlStateManager.getError();
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[gl_max_vertex_texture_image_units]", GlStateManager.getInteger(35660));
      GlStateManager.getError();
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[gl_max_texture_image_units]", GlStateManager.getInteger(34930));
      GlStateManager.getError();
      p_populateSnooperWithOpenGL_0_.setFixedData("gl_caps[gl_max_array_texture_layers]", GlStateManager.getInteger(35071));
      GlStateManager.getError();
   }

   public static String getOpenGLVersionString() {
      return GLFW.glfwGetCurrentContext() == 0L ? "NO CONTEXT" : GlStateManager.getString(7937) + " GL version " + GlStateManager.getString(7938) + ", " + GlStateManager.getString(7936);
   }

   public static int getRefreshRate(MainWindow p_getRefreshRate_0_) {
      long i = GLFW.glfwGetWindowMonitor(p_getRefreshRate_0_.getHandle());
      if (i == 0L) {
         i = GLFW.glfwGetPrimaryMonitor();
      }

      GLFWVidMode glfwvidmode = i == 0L ? null : GLFW.glfwGetVideoMode(i);
      return glfwvidmode == null ? 0 : glfwvidmode.refreshRate();
   }

   public static String getLWJGLVersion() {
      return Version.getVersion();
   }

   public static LongSupplier initGlfw() {
      MainWindow.checkGlfwError((p_212905_0_, p_212905_1_) -> {
         throw new IllegalStateException(String.format("GLFW error before init: [0x%X]%s", p_212905_0_, p_212905_1_));
      });
      List<String> list = Lists.newArrayList();
      GLFWErrorCallback glfwerrorcallback = GLFW.glfwSetErrorCallback((p_212908_1_, p_212908_2_) -> {
         list.add(String.format("GLFW error during init: [0x%X]%s", p_212908_1_, p_212908_2_));
      });
      if (!GLFW.glfwInit()) {
         throw new IllegalStateException("Failed to initialize GLFW, errors: " + Joiner.on(",").join(list));
      } else {
         LongSupplier longsupplier = () -> {
            return (long)(GLFW.glfwGetTime() * 1.0E9D);
         };

         for(String s : list) {
            LOGGER.error("GLFW error collected during initialization: {}", (Object)s);
         }

         setGlfwErrorCallback(glfwerrorcallback);
         return longsupplier;
      }
   }

   public static void setGlfwErrorCallback(GLFWErrorCallbackI p_setGlfwErrorCallback_0_) {
      GLFW.glfwSetErrorCallback(p_setGlfwErrorCallback_0_).free();
   }

   public static boolean shouldClose(MainWindow p_shouldClose_0_) {
      return GLFW.glfwWindowShouldClose(p_shouldClose_0_.getHandle());
   }

   public static void pollEvents() {
      GLFW.glfwPollEvents();
   }

   public static String getOpenGLVersion() {
      return GlStateManager.getString(7938);
   }

   public static String getRenderer() {
      return GlStateManager.getString(7937);
   }

   public static String getVendor() {
      return GlStateManager.getString(7936);
   }

   public static void setupNvFogDistance() {
      if (GL.getCapabilities().GL_NV_fog_distance) {
         GlStateManager.fogi(34138, 34139);
      }

   }

   public static boolean supportsOpenGL2() {
      return GL.getCapabilities().OpenGL20;
   }

   public static void withTextureRestore(Runnable p_withTextureRestore_0_) {
      GL11.glPushAttrib(270336);

      try {
         p_withTextureRestore_0_.run();
      } finally {
         GL11.glPopAttrib();
      }

   }

   public static ByteBuffer allocateMemory(int p_allocateMemory_0_) {
      return MemoryUtil.memAlloc(p_allocateMemory_0_);
   }

   public static void freeMemory(Buffer p_freeMemory_0_) {
      MemoryUtil.memFree(p_freeMemory_0_);
   }

   public static void init() {
      GLCapabilities glcapabilities = GL.getCapabilities();
      useMultitextureArb = glcapabilities.GL_ARB_multitexture && !glcapabilities.OpenGL13;
      useTexEnvCombineArb = glcapabilities.GL_ARB_texture_env_combine && !glcapabilities.OpenGL13;
      if (useMultitextureArb) {
         capsString = capsString + "Using ARB_multitexture.\n";
         GL_TEXTURE0 = 33984;
         GL_TEXTURE1 = 33985;
         GL_TEXTURE2 = 33986;
      } else {
         capsString = capsString + "Using GL 1.3 multitexturing.\n";
         GL_TEXTURE0 = 33984;
         GL_TEXTURE1 = 33985;
         GL_TEXTURE2 = 33986;
      }

      if (useTexEnvCombineArb) {
         capsString = capsString + "Using ARB_texture_env_combine.\n";
         GL_COMBINE = 34160;
         GL_INTERPOLATE = 34165;
         GL_PRIMARY_COLOR = 34167;
         GL_CONSTANT = 34166;
         GL_PREVIOUS = 34168;
         GL_COMBINE_RGB = 34161;
         GL_SOURCE0_RGB = 34176;
         GL_SOURCE1_RGB = 34177;
         GL_SOURCE2_RGB = 34178;
         GL_OPERAND0_RGB = 34192;
         GL_OPERAND1_RGB = 34193;
         GL_OPERAND2_RGB = 34194;
         GL_COMBINE_ALPHA = 34162;
         GL_SOURCE0_ALPHA = 34184;
         GL_SOURCE1_ALPHA = 34185;
         GL_SOURCE2_ALPHA = 34186;
         GL_OPERAND0_ALPHA = 34200;
         GL_OPERAND1_ALPHA = 34201;
         GL_OPERAND2_ALPHA = 34202;
      } else {
         capsString = capsString + "Using GL 1.3 texture combiners.\n";
         GL_COMBINE = 34160;
         GL_INTERPOLATE = 34165;
         GL_PRIMARY_COLOR = 34167;
         GL_CONSTANT = 34166;
         GL_PREVIOUS = 34168;
         GL_COMBINE_RGB = 34161;
         GL_SOURCE0_RGB = 34176;
         GL_SOURCE1_RGB = 34177;
         GL_SOURCE2_RGB = 34178;
         GL_OPERAND0_RGB = 34192;
         GL_OPERAND1_RGB = 34193;
         GL_OPERAND2_RGB = 34194;
         GL_COMBINE_ALPHA = 34162;
         GL_SOURCE0_ALPHA = 34184;
         GL_SOURCE1_ALPHA = 34185;
         GL_SOURCE2_ALPHA = 34186;
         GL_OPERAND0_ALPHA = 34200;
         GL_OPERAND1_ALPHA = 34201;
         GL_OPERAND2_ALPHA = 34202;
      }

      useSeparateBlendExt = glcapabilities.GL_EXT_blend_func_separate && !glcapabilities.OpenGL14;
      separateBlend = glcapabilities.OpenGL14 || glcapabilities.GL_EXT_blend_func_separate;
      capsString = capsString + "Using framebuffer objects because ";
      if (glcapabilities.OpenGL30) {
         capsString = capsString + "OpenGL 3.0 is supported and separate blending is supported.\n";
         fboMode = GLX.FboMode.BASE;
         GL_FRAMEBUFFER = 36160;
         GL_RENDERBUFFER = 36161;
         GL_COLOR_ATTACHMENT0 = 36064;
         GL_DEPTH_ATTACHMENT = 36096;
         GL_FRAMEBUFFER_COMPLETE = 36053;
         GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 36054;
         GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 36055;
         GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER = 36059;
         GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER = 36060;
      } else if (glcapabilities.GL_ARB_framebuffer_object) {
         capsString = capsString + "ARB_framebuffer_object is supported and separate blending is supported.\n";
         fboMode = GLX.FboMode.ARB;
         GL_FRAMEBUFFER = 36160;
         GL_RENDERBUFFER = 36161;
         GL_COLOR_ATTACHMENT0 = 36064;
         GL_DEPTH_ATTACHMENT = 36096;
         GL_FRAMEBUFFER_COMPLETE = 36053;
         GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 36055;
         GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 36054;
         GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER = 36059;
         GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER = 36060;
      } else {
         if (!glcapabilities.GL_EXT_framebuffer_object) {
            throw new IllegalStateException("The driver does not appear to support framebuffer objects");
         }

         capsString = capsString + "EXT_framebuffer_object is supported.\n";
         fboMode = GLX.FboMode.EXT;
         GL_FRAMEBUFFER = 36160;
         GL_RENDERBUFFER = 36161;
         GL_COLOR_ATTACHMENT0 = 36064;
         GL_DEPTH_ATTACHMENT = 36096;
         GL_FRAMEBUFFER_COMPLETE = 36053;
         GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 36055;
         GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 36054;
         GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER = 36059;
         GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER = 36060;
      }

      isOpenGl21 = glcapabilities.OpenGL21;
      hasShaders = isOpenGl21 || glcapabilities.GL_ARB_vertex_shader && glcapabilities.GL_ARB_fragment_shader && glcapabilities.GL_ARB_shader_objects;
      capsString = capsString + "Shaders are " + (hasShaders ? "" : "not ") + "available because ";
      if (hasShaders) {
         if (glcapabilities.OpenGL21) {
            capsString = capsString + "OpenGL 2.1 is supported.\n";
            useShaderArb = false;
            GL_LINK_STATUS = 35714;
            GL_COMPILE_STATUS = 35713;
            GL_VERTEX_SHADER = 35633;
            GL_FRAGMENT_SHADER = 35632;
         } else {
            capsString = capsString + "ARB_shader_objects, ARB_vertex_shader, and ARB_fragment_shader are supported.\n";
            useShaderArb = true;
            GL_LINK_STATUS = 35714;
            GL_COMPILE_STATUS = 35713;
            GL_VERTEX_SHADER = 35633;
            GL_FRAGMENT_SHADER = 35632;
         }
      } else {
         capsString = capsString + "OpenGL 2.1 is " + (glcapabilities.OpenGL21 ? "" : "not ") + "supported, ";
         capsString = capsString + "ARB_shader_objects is " + (glcapabilities.GL_ARB_shader_objects ? "" : "not ") + "supported, ";
         capsString = capsString + "ARB_vertex_shader is " + (glcapabilities.GL_ARB_vertex_shader ? "" : "not ") + "supported, and ";
         capsString = capsString + "ARB_fragment_shader is " + (glcapabilities.GL_ARB_fragment_shader ? "" : "not ") + "supported.\n";
      }

      usePostProcess = hasShaders;
      String s = GL11.glGetString(7936).toLowerCase(Locale.ROOT);
      isNvidia = s.contains("nvidia");
      useVboArb = !glcapabilities.OpenGL15 && glcapabilities.GL_ARB_vertex_buffer_object;
      capsString = capsString + "VBOs are available because ";
      if (useVboArb) {
         capsString = capsString + "ARB_vertex_buffer_object is supported.\n";
         GL_STATIC_DRAW = 35044;
         GL_ARRAY_BUFFER = 34962;
      } else {
         capsString = capsString + "OpenGL 1.5 is supported.\n";
         GL_STATIC_DRAW = 35044;
         GL_ARRAY_BUFFER = 34962;
      }

      isAmd = s.contains("ati");
      if (isAmd) {
         needVbo = true;
      }

      try {
         Processor[] aprocessor = (new SystemInfo()).getHardware().getProcessors();
         cpuInfo = String.format("%dx %s", aprocessor.length, aprocessor[0]).replaceAll("\\s+", " ");
      } catch (Throwable var3) {
         ;
      }

   }

   public static boolean isNextGen() {
      return usePostProcess;
   }

   public static String getCapsString() {
      return capsString;
   }

   public static int glGetProgrami(int p_glGetProgrami_0_, int p_glGetProgrami_1_) {
      return useShaderArb ? ARBShaderObjects.glGetObjectParameteriARB(p_glGetProgrami_0_, p_glGetProgrami_1_) : GL20.glGetProgrami(p_glGetProgrami_0_, p_glGetProgrami_1_);
   }

   public static void glAttachShader(int p_glAttachShader_0_, int p_glAttachShader_1_) {
      if (useShaderArb) {
         ARBShaderObjects.glAttachObjectARB(p_glAttachShader_0_, p_glAttachShader_1_);
      } else {
         GL20.glAttachShader(p_glAttachShader_0_, p_glAttachShader_1_);
      }

   }

   public static void glDeleteShader(int p_glDeleteShader_0_) {
      if (useShaderArb) {
         ARBShaderObjects.glDeleteObjectARB(p_glDeleteShader_0_);
      } else {
         GL20.glDeleteShader(p_glDeleteShader_0_);
      }

   }

   public static int glCreateShader(int p_glCreateShader_0_) {
      return useShaderArb ? ARBShaderObjects.glCreateShaderObjectARB(p_glCreateShader_0_) : GL20.glCreateShader(p_glCreateShader_0_);
   }

   public static void glShaderSource(int p_glShaderSource_0_, CharSequence p_glShaderSource_1_) {
      if (useShaderArb) {
         ARBShaderObjects.glShaderSourceARB(p_glShaderSource_0_, p_glShaderSource_1_);
      } else {
         GL20.glShaderSource(p_glShaderSource_0_, p_glShaderSource_1_);
      }

   }

   public static void glCompileShader(int p_glCompileShader_0_) {
      if (useShaderArb) {
         ARBShaderObjects.glCompileShaderARB(p_glCompileShader_0_);
      } else {
         GL20.glCompileShader(p_glCompileShader_0_);
      }

   }

   public static int glGetShaderi(int p_glGetShaderi_0_, int p_glGetShaderi_1_) {
      return useShaderArb ? ARBShaderObjects.glGetObjectParameteriARB(p_glGetShaderi_0_, p_glGetShaderi_1_) : GL20.glGetShaderi(p_glGetShaderi_0_, p_glGetShaderi_1_);
   }

   public static String glGetShaderInfoLog(int p_glGetShaderInfoLog_0_, int p_glGetShaderInfoLog_1_) {
      return useShaderArb ? ARBShaderObjects.glGetInfoLogARB(p_glGetShaderInfoLog_0_, p_glGetShaderInfoLog_1_) : GL20.glGetShaderInfoLog(p_glGetShaderInfoLog_0_, p_glGetShaderInfoLog_1_);
   }

   public static String glGetProgramInfoLog(int p_glGetProgramInfoLog_0_, int p_glGetProgramInfoLog_1_) {
      return useShaderArb ? ARBShaderObjects.glGetInfoLogARB(p_glGetProgramInfoLog_0_, p_glGetProgramInfoLog_1_) : GL20.glGetProgramInfoLog(p_glGetProgramInfoLog_0_, p_glGetProgramInfoLog_1_);
   }

   public static void glUseProgram(int p_glUseProgram_0_) {
      if (useShaderArb) {
         ARBShaderObjects.glUseProgramObjectARB(p_glUseProgram_0_);
      } else {
         GL20.glUseProgram(p_glUseProgram_0_);
      }

   }

   public static int glCreateProgram() {
      return useShaderArb ? ARBShaderObjects.glCreateProgramObjectARB() : GL20.glCreateProgram();
   }

   public static void glDeleteProgram(int p_glDeleteProgram_0_) {
      if (useShaderArb) {
         ARBShaderObjects.glDeleteObjectARB(p_glDeleteProgram_0_);
      } else {
         GL20.glDeleteProgram(p_glDeleteProgram_0_);
      }

   }

   public static void glLinkProgram(int p_glLinkProgram_0_) {
      if (useShaderArb) {
         ARBShaderObjects.glLinkProgramARB(p_glLinkProgram_0_);
      } else {
         GL20.glLinkProgram(p_glLinkProgram_0_);
      }

   }

   public static int glGetUniformLocation(int p_glGetUniformLocation_0_, CharSequence p_glGetUniformLocation_1_) {
      return useShaderArb ? ARBShaderObjects.glGetUniformLocationARB(p_glGetUniformLocation_0_, p_glGetUniformLocation_1_) : GL20.glGetUniformLocation(p_glGetUniformLocation_0_, p_glGetUniformLocation_1_);
   }

   public static void glUniform1(int p_glUniform1_0_, IntBuffer p_glUniform1_1_) {
      if (useShaderArb) {
         ARBShaderObjects.glUniform1ivARB(p_glUniform1_0_, p_glUniform1_1_);
      } else {
         GL20.glUniform1iv(p_glUniform1_0_, p_glUniform1_1_);
      }

   }

   public static void glUniform1i(int p_glUniform1i_0_, int p_glUniform1i_1_) {
      if (useShaderArb) {
         ARBShaderObjects.glUniform1iARB(p_glUniform1i_0_, p_glUniform1i_1_);
      } else {
         GL20.glUniform1i(p_glUniform1i_0_, p_glUniform1i_1_);
      }

   }

   public static void glUniform1(int p_glUniform1_0_, FloatBuffer p_glUniform1_1_) {
      if (useShaderArb) {
         ARBShaderObjects.glUniform1fvARB(p_glUniform1_0_, p_glUniform1_1_);
      } else {
         GL20.glUniform1fv(p_glUniform1_0_, p_glUniform1_1_);
      }

   }

   public static void glUniform2(int p_glUniform2_0_, IntBuffer p_glUniform2_1_) {
      if (useShaderArb) {
         ARBShaderObjects.glUniform2ivARB(p_glUniform2_0_, p_glUniform2_1_);
      } else {
         GL20.glUniform2iv(p_glUniform2_0_, p_glUniform2_1_);
      }

   }

   public static void glUniform2(int p_glUniform2_0_, FloatBuffer p_glUniform2_1_) {
      if (useShaderArb) {
         ARBShaderObjects.glUniform2fvARB(p_glUniform2_0_, p_glUniform2_1_);
      } else {
         GL20.glUniform2fv(p_glUniform2_0_, p_glUniform2_1_);
      }

   }

   public static void glUniform3(int p_glUniform3_0_, IntBuffer p_glUniform3_1_) {
      if (useShaderArb) {
         ARBShaderObjects.glUniform3ivARB(p_glUniform3_0_, p_glUniform3_1_);
      } else {
         GL20.glUniform3iv(p_glUniform3_0_, p_glUniform3_1_);
      }

   }

   public static void glUniform3(int p_glUniform3_0_, FloatBuffer p_glUniform3_1_) {
      if (useShaderArb) {
         ARBShaderObjects.glUniform3fvARB(p_glUniform3_0_, p_glUniform3_1_);
      } else {
         GL20.glUniform3fv(p_glUniform3_0_, p_glUniform3_1_);
      }

   }

   public static void glUniform4(int p_glUniform4_0_, IntBuffer p_glUniform4_1_) {
      if (useShaderArb) {
         ARBShaderObjects.glUniform4ivARB(p_glUniform4_0_, p_glUniform4_1_);
      } else {
         GL20.glUniform4iv(p_glUniform4_0_, p_glUniform4_1_);
      }

   }

   public static void glUniform4(int p_glUniform4_0_, FloatBuffer p_glUniform4_1_) {
      if (useShaderArb) {
         ARBShaderObjects.glUniform4fvARB(p_glUniform4_0_, p_glUniform4_1_);
      } else {
         GL20.glUniform4fv(p_glUniform4_0_, p_glUniform4_1_);
      }

   }

   public static void glUniformMatrix2(int p_glUniformMatrix2_0_, boolean p_glUniformMatrix2_1_, FloatBuffer p_glUniformMatrix2_2_) {
      if (useShaderArb) {
         ARBShaderObjects.glUniformMatrix2fvARB(p_glUniformMatrix2_0_, p_glUniformMatrix2_1_, p_glUniformMatrix2_2_);
      } else {
         GL20.glUniformMatrix2fv(p_glUniformMatrix2_0_, p_glUniformMatrix2_1_, p_glUniformMatrix2_2_);
      }

   }

   public static void glUniformMatrix3(int p_glUniformMatrix3_0_, boolean p_glUniformMatrix3_1_, FloatBuffer p_glUniformMatrix3_2_) {
      if (useShaderArb) {
         ARBShaderObjects.glUniformMatrix3fvARB(p_glUniformMatrix3_0_, p_glUniformMatrix3_1_, p_glUniformMatrix3_2_);
      } else {
         GL20.glUniformMatrix3fv(p_glUniformMatrix3_0_, p_glUniformMatrix3_1_, p_glUniformMatrix3_2_);
      }

   }

   public static void glUniformMatrix4(int p_glUniformMatrix4_0_, boolean p_glUniformMatrix4_1_, FloatBuffer p_glUniformMatrix4_2_) {
      if (useShaderArb) {
         ARBShaderObjects.glUniformMatrix4fvARB(p_glUniformMatrix4_0_, p_glUniformMatrix4_1_, p_glUniformMatrix4_2_);
      } else {
         GL20.glUniformMatrix4fv(p_glUniformMatrix4_0_, p_glUniformMatrix4_1_, p_glUniformMatrix4_2_);
      }

   }

   public static int glGetAttribLocation(int p_glGetAttribLocation_0_, CharSequence p_glGetAttribLocation_1_) {
      return useShaderArb ? ARBVertexShader.glGetAttribLocationARB(p_glGetAttribLocation_0_, p_glGetAttribLocation_1_) : GL20.glGetAttribLocation(p_glGetAttribLocation_0_, p_glGetAttribLocation_1_);
   }

   public static int glGenBuffers() {
      return useVboArb ? ARBVertexBufferObject.glGenBuffersARB() : GL15.glGenBuffers();
   }

   public static void glGenBuffers(IntBuffer p_glGenBuffers_0_) {
      if (useVboArb) {
         ARBVertexBufferObject.glGenBuffersARB(p_glGenBuffers_0_);
      } else {
         GL15.glGenBuffers(p_glGenBuffers_0_);
      }

   }

   public static void glBindBuffer(int p_glBindBuffer_0_, int p_glBindBuffer_1_) {
      if (useVboArb) {
         ARBVertexBufferObject.glBindBufferARB(p_glBindBuffer_0_, p_glBindBuffer_1_);
      } else {
         GL15.glBindBuffer(p_glBindBuffer_0_, p_glBindBuffer_1_);
      }

   }

   public static void glBufferData(int p_glBufferData_0_, ByteBuffer p_glBufferData_1_, int p_glBufferData_2_) {
      if (useVboArb) {
         ARBVertexBufferObject.glBufferDataARB(p_glBufferData_0_, p_glBufferData_1_, p_glBufferData_2_);
      } else {
         GL15.glBufferData(p_glBufferData_0_, p_glBufferData_1_, p_glBufferData_2_);
      }

   }

   public static void glDeleteBuffers(int p_glDeleteBuffers_0_) {
      if (useVboArb) {
         ARBVertexBufferObject.glDeleteBuffersARB(p_glDeleteBuffers_0_);
      } else {
         GL15.glDeleteBuffers(p_glDeleteBuffers_0_);
      }

   }

   public static void glDeleteBuffers(IntBuffer p_glDeleteBuffers_0_) {
      if (useVboArb) {
         ARBVertexBufferObject.glDeleteBuffersARB(p_glDeleteBuffers_0_);
      } else {
         GL15.glDeleteBuffers(p_glDeleteBuffers_0_);
      }

   }

   public static boolean useVbo() {
      return true;
   }

   public static void glBindFramebuffer(int p_glBindFramebuffer_0_, int p_glBindFramebuffer_1_) {
      switch(fboMode) {
      case BASE:
         GL30.glBindFramebuffer(p_glBindFramebuffer_0_, p_glBindFramebuffer_1_);
         break;
      case ARB:
         ARBFramebufferObject.glBindFramebuffer(p_glBindFramebuffer_0_, p_glBindFramebuffer_1_);
         break;
      case EXT:
         EXTFramebufferObject.glBindFramebufferEXT(p_glBindFramebuffer_0_, p_glBindFramebuffer_1_);
      }

   }

   public static void glBindRenderbuffer(int p_glBindRenderbuffer_0_, int p_glBindRenderbuffer_1_) {
      switch(fboMode) {
      case BASE:
         GL30.glBindRenderbuffer(p_glBindRenderbuffer_0_, p_glBindRenderbuffer_1_);
         break;
      case ARB:
         ARBFramebufferObject.glBindRenderbuffer(p_glBindRenderbuffer_0_, p_glBindRenderbuffer_1_);
         break;
      case EXT:
         EXTFramebufferObject.glBindRenderbufferEXT(p_glBindRenderbuffer_0_, p_glBindRenderbuffer_1_);
      }

   }

   public static void glDeleteRenderbuffers(int p_glDeleteRenderbuffers_0_) {
      switch(fboMode) {
      case BASE:
         GL30.glDeleteRenderbuffers(p_glDeleteRenderbuffers_0_);
         break;
      case ARB:
         ARBFramebufferObject.glDeleteRenderbuffers(p_glDeleteRenderbuffers_0_);
         break;
      case EXT:
         EXTFramebufferObject.glDeleteRenderbuffersEXT(p_glDeleteRenderbuffers_0_);
      }

   }

   public static void glDeleteFramebuffers(int p_glDeleteFramebuffers_0_) {
      switch(fboMode) {
      case BASE:
         GL30.glDeleteFramebuffers(p_glDeleteFramebuffers_0_);
         break;
      case ARB:
         ARBFramebufferObject.glDeleteFramebuffers(p_glDeleteFramebuffers_0_);
         break;
      case EXT:
         EXTFramebufferObject.glDeleteFramebuffersEXT(p_glDeleteFramebuffers_0_);
      }

   }

   public static int glGenFramebuffers() {
      switch(fboMode) {
      case BASE:
         return GL30.glGenFramebuffers();
      case ARB:
         return ARBFramebufferObject.glGenFramebuffers();
      case EXT:
         return EXTFramebufferObject.glGenFramebuffersEXT();
      default:
         return -1;
      }
   }

   public static int glGenRenderbuffers() {
      switch(fboMode) {
      case BASE:
         return GL30.glGenRenderbuffers();
      case ARB:
         return ARBFramebufferObject.glGenRenderbuffers();
      case EXT:
         return EXTFramebufferObject.glGenRenderbuffersEXT();
      default:
         return -1;
      }
   }

   public static void glRenderbufferStorage(int p_glRenderbufferStorage_0_, int p_glRenderbufferStorage_1_, int p_glRenderbufferStorage_2_, int p_glRenderbufferStorage_3_) {
      switch(fboMode) {
      case BASE:
         GL30.glRenderbufferStorage(p_glRenderbufferStorage_0_, p_glRenderbufferStorage_1_, p_glRenderbufferStorage_2_, p_glRenderbufferStorage_3_);
         break;
      case ARB:
         ARBFramebufferObject.glRenderbufferStorage(p_glRenderbufferStorage_0_, p_glRenderbufferStorage_1_, p_glRenderbufferStorage_2_, p_glRenderbufferStorage_3_);
         break;
      case EXT:
         EXTFramebufferObject.glRenderbufferStorageEXT(p_glRenderbufferStorage_0_, p_glRenderbufferStorage_1_, p_glRenderbufferStorage_2_, p_glRenderbufferStorage_3_);
      }

   }

   public static void glFramebufferRenderbuffer(int p_glFramebufferRenderbuffer_0_, int p_glFramebufferRenderbuffer_1_, int p_glFramebufferRenderbuffer_2_, int p_glFramebufferRenderbuffer_3_) {
      switch(fboMode) {
      case BASE:
         GL30.glFramebufferRenderbuffer(p_glFramebufferRenderbuffer_0_, p_glFramebufferRenderbuffer_1_, p_glFramebufferRenderbuffer_2_, p_glFramebufferRenderbuffer_3_);
         break;
      case ARB:
         ARBFramebufferObject.glFramebufferRenderbuffer(p_glFramebufferRenderbuffer_0_, p_glFramebufferRenderbuffer_1_, p_glFramebufferRenderbuffer_2_, p_glFramebufferRenderbuffer_3_);
         break;
      case EXT:
         EXTFramebufferObject.glFramebufferRenderbufferEXT(p_glFramebufferRenderbuffer_0_, p_glFramebufferRenderbuffer_1_, p_glFramebufferRenderbuffer_2_, p_glFramebufferRenderbuffer_3_);
      }

   }

   public static int glCheckFramebufferStatus(int p_glCheckFramebufferStatus_0_) {
      switch(fboMode) {
      case BASE:
         return GL30.glCheckFramebufferStatus(p_glCheckFramebufferStatus_0_);
      case ARB:
         return ARBFramebufferObject.glCheckFramebufferStatus(p_glCheckFramebufferStatus_0_);
      case EXT:
         return EXTFramebufferObject.glCheckFramebufferStatusEXT(p_glCheckFramebufferStatus_0_);
      default:
         return -1;
      }
   }

   public static void glFramebufferTexture2D(int p_glFramebufferTexture2D_0_, int p_glFramebufferTexture2D_1_, int p_glFramebufferTexture2D_2_, int p_glFramebufferTexture2D_3_, int p_glFramebufferTexture2D_4_) {
      switch(fboMode) {
      case BASE:
         GL30.glFramebufferTexture2D(p_glFramebufferTexture2D_0_, p_glFramebufferTexture2D_1_, p_glFramebufferTexture2D_2_, p_glFramebufferTexture2D_3_, p_glFramebufferTexture2D_4_);
         break;
      case ARB:
         ARBFramebufferObject.glFramebufferTexture2D(p_glFramebufferTexture2D_0_, p_glFramebufferTexture2D_1_, p_glFramebufferTexture2D_2_, p_glFramebufferTexture2D_3_, p_glFramebufferTexture2D_4_);
         break;
      case EXT:
         EXTFramebufferObject.glFramebufferTexture2DEXT(p_glFramebufferTexture2D_0_, p_glFramebufferTexture2D_1_, p_glFramebufferTexture2D_2_, p_glFramebufferTexture2D_3_, p_glFramebufferTexture2D_4_);
      }

   }

   public static int getBoundFramebuffer() {
      switch(fboMode) {
      case BASE:
         return GlStateManager.getInteger(36006);
      case ARB:
         return GlStateManager.getInteger(36006);
      case EXT:
         return GlStateManager.getInteger(36006);
      default:
         return 0;
      }
   }

   public static void glActiveTexture(int p_glActiveTexture_0_) {
      if (useMultitextureArb) {
         ARBMultitexture.glActiveTextureARB(p_glActiveTexture_0_);
      } else {
         GL13.glActiveTexture(p_glActiveTexture_0_);
      }

   }

   public static void glClientActiveTexture(int p_glClientActiveTexture_0_) {
      if (useMultitextureArb) {
         ARBMultitexture.glClientActiveTextureARB(p_glClientActiveTexture_0_);
      } else {
         GL13.glClientActiveTexture(p_glClientActiveTexture_0_);
      }

   }

   public static void glMultiTexCoord2f(int p_glMultiTexCoord2f_0_, float p_glMultiTexCoord2f_1_, float p_glMultiTexCoord2f_2_) {
      if (useMultitextureArb) {
         ARBMultitexture.glMultiTexCoord2fARB(p_glMultiTexCoord2f_0_, p_glMultiTexCoord2f_1_, p_glMultiTexCoord2f_2_);
      } else {
         GL13.glMultiTexCoord2f(p_glMultiTexCoord2f_0_, p_glMultiTexCoord2f_1_, p_glMultiTexCoord2f_2_);
      }

      if (p_glMultiTexCoord2f_0_ == GL_TEXTURE1) {
         lastBrightnessX = p_glMultiTexCoord2f_1_;
         lastBrightnessY = p_glMultiTexCoord2f_2_;
      }
   }

   public static void glBlendFuncSeparate(int p_glBlendFuncSeparate_0_, int p_glBlendFuncSeparate_1_, int p_glBlendFuncSeparate_2_, int p_glBlendFuncSeparate_3_) {
      if (separateBlend) {
         if (useSeparateBlendExt) {
            EXTBlendFuncSeparate.glBlendFuncSeparateEXT(p_glBlendFuncSeparate_0_, p_glBlendFuncSeparate_1_, p_glBlendFuncSeparate_2_, p_glBlendFuncSeparate_3_);
         } else {
            GL14.glBlendFuncSeparate(p_glBlendFuncSeparate_0_, p_glBlendFuncSeparate_1_, p_glBlendFuncSeparate_2_, p_glBlendFuncSeparate_3_);
         }
      } else {
         GL11.glBlendFunc(p_glBlendFuncSeparate_0_, p_glBlendFuncSeparate_1_);
      }

   }

   public static boolean isUsingFBOs() {
      return true;
   }

   public static String getCpuInfo() {
      return cpuInfo == null ? "<unknown>" : cpuInfo;
   }

   public static void renderCrosshair(int p_renderCrosshair_0_) {
      renderCrosshair(p_renderCrosshair_0_, true, true, true);
   }

   public static void renderCrosshair(int p_renderCrosshair_0_, boolean p_renderCrosshair_1_, boolean p_renderCrosshair_2_, boolean p_renderCrosshair_3_) {
      GlStateManager.disableTexture();
      GlStateManager.depthMask(false);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      GL11.glLineWidth(4.0F);
      bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR);
      if (p_renderCrosshair_1_) {
         bufferbuilder.pos(0.0D, 0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
         bufferbuilder.pos((double)p_renderCrosshair_0_, 0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
      }

      if (p_renderCrosshair_2_) {
         bufferbuilder.pos(0.0D, 0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
         bufferbuilder.pos(0.0D, (double)p_renderCrosshair_0_, 0.0D).color(0, 0, 0, 255).endVertex();
      }

      if (p_renderCrosshair_3_) {
         bufferbuilder.pos(0.0D, 0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
         bufferbuilder.pos(0.0D, 0.0D, (double)p_renderCrosshair_0_).color(0, 0, 0, 255).endVertex();
      }

      tessellator.draw();
      GL11.glLineWidth(2.0F);
      bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR);
      if (p_renderCrosshair_1_) {
         bufferbuilder.pos(0.0D, 0.0D, 0.0D).color(255, 0, 0, 255).endVertex();
         bufferbuilder.pos((double)p_renderCrosshair_0_, 0.0D, 0.0D).color(255, 0, 0, 255).endVertex();
      }

      if (p_renderCrosshair_2_) {
         bufferbuilder.pos(0.0D, 0.0D, 0.0D).color(0, 255, 0, 255).endVertex();
         bufferbuilder.pos(0.0D, (double)p_renderCrosshair_0_, 0.0D).color(0, 255, 0, 255).endVertex();
      }

      if (p_renderCrosshair_3_) {
         bufferbuilder.pos(0.0D, 0.0D, 0.0D).color(127, 127, 255, 255).endVertex();
         bufferbuilder.pos(0.0D, 0.0D, (double)p_renderCrosshair_0_).color(127, 127, 255, 255).endVertex();
      }

      tessellator.draw();
      GL11.glLineWidth(1.0F);
      GlStateManager.depthMask(true);
      GlStateManager.enableTexture();
   }

   public static String getErrorString(int p_getErrorString_0_) {
      return LOOKUP_MAP.get(p_getErrorString_0_);
   }

   public static <T> T make(Supplier<T> p_make_0_) {
      return p_make_0_.get();
   }

   public static <T> T make(T p_make_0_, Consumer<T> p_make_1_) {
      p_make_1_.accept(p_make_0_);
      return p_make_0_;
   }

   @OnlyIn(Dist.CLIENT)
   static enum FboMode {
      BASE,
      ARB,
      EXT;
   }
}