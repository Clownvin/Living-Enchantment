package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
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
public class WorldGenAttemptsDebugRenderer implements DebugRenderer.IDebugRenderer {
   private final Minecraft minecraft;
   private final List<BlockPos> locations = Lists.newArrayList();
   private final List<Float> sizes = Lists.newArrayList();
   private final List<Float> alphas = Lists.newArrayList();
   private final List<Float> reds = Lists.newArrayList();
   private final List<Float> greens = Lists.newArrayList();
   private final List<Float> blues = Lists.newArrayList();

   public WorldGenAttemptsDebugRenderer(Minecraft minecraftIn) {
      this.minecraft = minecraftIn;
   }

   public void addAttempt(BlockPos pos, float size, float red, float green, float blue, float alpha) {
      this.locations.add(pos);
      this.sizes.add(size);
      this.alphas.add(alpha);
      this.reds.add(red);
      this.greens.add(green);
      this.blues.add(blue);
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
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);

      for(int i = 0; i < this.locations.size(); ++i) {
         BlockPos blockpos = this.locations.get(i);
         Float f = this.sizes.get(i);
         float f1 = f / 2.0F;
         WorldRenderer.addChainedFilledBoxVertices(bufferbuilder, (double)((float)blockpos.getX() + 0.5F - f1) - d0, (double)((float)blockpos.getY() + 0.5F - f1) - d1, (double)((float)blockpos.getZ() + 0.5F - f1) - d2, (double)((float)blockpos.getX() + 0.5F + f1) - d0, (double)((float)blockpos.getY() + 0.5F + f1) - d1, (double)((float)blockpos.getZ() + 0.5F + f1) - d2, this.reds.get(i), this.greens.get(i), this.blues.get(i), this.alphas.get(i));
      }

      tessellator.draw();
      GlStateManager.enableTexture();
      GlStateManager.popMatrix();
   }
}