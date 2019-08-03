package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.ByteBuffer;
import java.util.List;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WorldVertexBufferUploader {
   public void draw(BufferBuilder bufferBuilderIn) {
      if (bufferBuilderIn.getVertexCount() > 0) {
         VertexFormat vertexformat = bufferBuilderIn.getVertexFormat();
         int i = vertexformat.getSize();
         ByteBuffer bytebuffer = bufferBuilderIn.getByteBuffer();
         List<VertexFormatElement> list = vertexformat.getElements();

         for(int j = 0; j < list.size(); ++j) {
            VertexFormatElement vertexformatelement = list.get(j);
            vertexformatelement.getUsage().preDraw(vertexformat, j, i, bytebuffer); // moved to VertexFormatElement.preDraw
         }

         GlStateManager.drawArrays(bufferBuilderIn.getDrawMode(), 0, bufferBuilderIn.getVertexCount());
         int i1 = 0;

         for(int j1 = list.size(); i1 < j1; ++i1) {
            VertexFormatElement vertexformatelement1 = list.get(i1);
            vertexformatelement1.getUsage().postDraw(vertexformat, i1, i, bytebuffer); // moved to VertexFormatElement.postDraw
         }
      }

      bufferBuilderIn.reset();
   }
}