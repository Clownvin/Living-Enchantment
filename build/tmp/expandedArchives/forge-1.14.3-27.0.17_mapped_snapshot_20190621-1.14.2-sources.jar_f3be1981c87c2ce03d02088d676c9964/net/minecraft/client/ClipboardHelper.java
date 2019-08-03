package net.minecraft.client;

import java.nio.ByteBuffer;
import net.minecraft.util.SharedConstants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.system.MemoryUtil;

@OnlyIn(Dist.CLIENT)
public class ClipboardHelper {
   private final ByteBuffer field_216490_a = ByteBuffer.allocateDirect(1024);

   public String func_216487_a(long p_216487_1_, GLFWErrorCallbackI p_216487_3_) {
      GLFWErrorCallback glfwerrorcallback = GLFW.glfwSetErrorCallback(p_216487_3_);
      String s = GLFW.glfwGetClipboardString(p_216487_1_);
      s = s != null ? SharedConstants.func_215070_b(s) : "";
      GLFWErrorCallback glfwerrorcallback1 = GLFW.glfwSetErrorCallback(glfwerrorcallback);
      if (glfwerrorcallback1 != null) {
         glfwerrorcallback1.free();
      }

      return s;
   }

   private void func_216488_a(long p_216488_1_, ByteBuffer p_216488_3_, String p_216488_4_) {
      MemoryUtil.memUTF8(p_216488_4_, true, p_216488_3_);
      GLFW.glfwSetClipboardString(p_216488_1_, p_216488_3_);
   }

   public void func_216489_a(long p_216489_1_, String p_216489_3_) {
      int i = MemoryUtil.memLengthUTF8(p_216489_3_, true);
      if (i < this.field_216490_a.capacity()) {
         this.func_216488_a(p_216489_1_, this.field_216490_a, p_216489_3_);
         this.field_216490_a.clear();
      } else {
         ByteBuffer bytebuffer = ByteBuffer.allocateDirect(i);
         this.func_216488_a(p_216489_1_, bytebuffer, p_216489_3_);
      }

   }
}