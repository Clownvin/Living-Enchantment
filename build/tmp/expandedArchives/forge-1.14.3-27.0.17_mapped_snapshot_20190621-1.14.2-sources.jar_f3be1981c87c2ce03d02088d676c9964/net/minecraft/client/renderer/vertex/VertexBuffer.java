package net.minecraft.client.renderer.vertex;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.ByteBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VertexBuffer {
   private int glBufferId;
   private final VertexFormat vertexFormat;
   private int count;

   public VertexBuffer(VertexFormat vertexFormatIn) {
      this.vertexFormat = vertexFormatIn;
      this.glBufferId = GLX.glGenBuffers();
   }

   public void bindBuffer() {
      GLX.glBindBuffer(GLX.GL_ARRAY_BUFFER, this.glBufferId);
   }

   public void bufferData(ByteBuffer data) {
      this.bindBuffer();
      GLX.glBufferData(GLX.GL_ARRAY_BUFFER, data, 35044);
      unbindBuffer();
      this.count = data.limit() / this.vertexFormat.getSize();
   }

   public void drawArrays(int mode) {
      GlStateManager.drawArrays(mode, 0, this.count);
   }

   public static void unbindBuffer() {
      GLX.glBindBuffer(GLX.GL_ARRAY_BUFFER, 0);
   }

   public void deleteGlBuffers() {
      if (this.glBufferId >= 0) {
         GLX.glDeleteBuffers(this.glBufferId);
         this.glBufferId = -1;
      }

   }
}