package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CaveDebugRenderer implements DebugRenderer.IDebugRenderer {
   private final Minecraft minecraft;
   private final Map<BlockPos, BlockPos> subCaves = Maps.newHashMap();
   private final Map<BlockPos, Float> sizes = Maps.newHashMap();
   private final List<BlockPos> caves = Lists.newArrayList();

   public CaveDebugRenderer(Minecraft minecraftIn) {
      this.minecraft = minecraftIn;
   }

   public void addCave(BlockPos cavePos, List<BlockPos> subPositions, List<Float> sizes) {
      for(int i = 0; i < subPositions.size(); ++i) {
         this.subCaves.put(subPositions.get(i), cavePos);
         this.sizes.put(subPositions.get(i), sizes.get(i));
      }

      this.caves.add(cavePos);
   }

   public void render(long p_217676_1_) {
      ActiveRenderInfo activerenderinfo = this.minecraft.gameRenderer.getActiveRenderInfo();
      double d0 = activerenderinfo.getProjectedView().x;
      double d1 = activerenderinfo.getProjectedView().y;
      double d2 = activerenderinfo.getProjectedView().z;
      GlStateManager.pushMatrix();
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.disableTexture();
      BlockPos blockpos = new BlockPos(activerenderinfo.getProjectedView().x, 0.0D, activerenderinfo.getProjectedView().z);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);

      for(Entry<BlockPos, BlockPos> entry : this.subCaves.entrySet()) {
         BlockPos blockpos1 = entry.getKey();
         BlockPos blockpos2 = entry.getValue();
         float f = (float)(blockpos2.getX() * 128 % 256) / 256.0F;
         float f1 = (float)(blockpos2.getY() * 128 % 256) / 256.0F;
         float f2 = (float)(blockpos2.getZ() * 128 % 256) / 256.0F;
         float f3 = this.sizes.get(blockpos1);
         if (blockpos.withinDistance(blockpos1, 160.0D)) {
            WorldRenderer.addChainedFilledBoxVertices(bufferbuilder, (double)((float)blockpos1.getX() + 0.5F) - d0 - (double)f3, (double)((float)blockpos1.getY() + 0.5F) - d1 - (double)f3, (double)((float)blockpos1.getZ() + 0.5F) - d2 - (double)f3, (double)((float)blockpos1.getX() + 0.5F) - d0 + (double)f3, (double)((float)blockpos1.getY() + 0.5F) - d1 + (double)f3, (double)((float)blockpos1.getZ() + 0.5F) - d2 + (double)f3, f, f1, f2, 0.5F);
         }
      }

      for(BlockPos blockpos3 : this.caves) {
         if (blockpos.withinDistance(blockpos3, 160.0D)) {
            WorldRenderer.addChainedFilledBoxVertices(bufferbuilder, (double)blockpos3.getX() - d0, (double)blockpos3.getY() - d1, (double)blockpos3.getZ() - d2, (double)((float)blockpos3.getX() + 1.0F) - d0, (double)((float)blockpos3.getY() + 1.0F) - d1, (double)((float)blockpos3.getZ() + 1.0F) - d2, 1.0F, 1.0F, 1.0F, 1.0F);
         }
      }

      tessellator.draw();
      GlStateManager.enableDepthTest();
      GlStateManager.enableTexture();
      GlStateManager.popMatrix();
   }
}