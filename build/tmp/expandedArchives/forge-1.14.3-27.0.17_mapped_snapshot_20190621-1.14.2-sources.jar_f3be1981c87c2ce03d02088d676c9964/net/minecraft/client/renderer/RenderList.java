package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.chunk.ChunkRender;
import net.minecraft.client.renderer.chunk.ListedChunkRender;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderList extends AbstractChunkRenderContainer {
   public void renderChunkLayer(BlockRenderLayer layer) {
      if (this.initialized) {
         for(ChunkRender chunkrender : this.renderChunks) {
            ListedChunkRender listedchunkrender = (ListedChunkRender)chunkrender;
            GlStateManager.pushMatrix();
            this.preRenderChunk(chunkrender);
            GlStateManager.callList(listedchunkrender.getDisplayList(layer, listedchunkrender.getCompiledChunk()));
            GlStateManager.popMatrix();
         }

         GlStateManager.clearCurrentColor();
         this.renderChunks.clear();
      }
   }
}