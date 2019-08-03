package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.Util;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CollisionBoxDebugRenderer implements DebugRenderer.IDebugRenderer {
   private final Minecraft minecraft;
   private double lastUpdate = Double.MIN_VALUE;
   private List<VoxelShape> collisionData = Collections.emptyList();

   public CollisionBoxDebugRenderer(Minecraft minecraftIn) {
      this.minecraft = minecraftIn;
   }

   public void render(long p_217676_1_) {
      ActiveRenderInfo activerenderinfo = this.minecraft.gameRenderer.getActiveRenderInfo();
      double d0 = (double)Util.nanoTime();
      if (d0 - this.lastUpdate > 1.0E8D) {
         this.lastUpdate = d0;
         this.collisionData = activerenderinfo.func_216773_g().world.getCollisionShapes(activerenderinfo.func_216773_g(), activerenderinfo.func_216773_g().getBoundingBox().grow(6.0D), Collections.emptySet()).collect(Collectors.toList());
      }

      double d1 = activerenderinfo.getProjectedView().x;
      double d2 = activerenderinfo.getProjectedView().y;
      double d3 = activerenderinfo.getProjectedView().z;
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.lineWidth(2.0F);
      GlStateManager.disableTexture();
      GlStateManager.depthMask(false);

      for(VoxelShape voxelshape : this.collisionData) {
         WorldRenderer.drawVoxelShapeParts(voxelshape, -d1, -d2, -d3, 1.0F, 1.0F, 1.0F, 1.0F);
      }

      GlStateManager.depthMask(true);
      GlStateManager.enableTexture();
      GlStateManager.disableBlend();
   }
}