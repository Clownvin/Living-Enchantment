package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PathfindingDebugRenderer implements DebugRenderer.IDebugRenderer {
   private final Minecraft minecraft;
   private final Map<Integer, Path> pathMap = Maps.newHashMap();
   private final Map<Integer, Float> pathMaxDistance = Maps.newHashMap();
   private final Map<Integer, Long> creationMap = Maps.newHashMap();

   public PathfindingDebugRenderer(Minecraft minecraftIn) {
      this.minecraft = minecraftIn;
   }

   public void addPath(int eid, Path pathIn, float distance) {
      this.pathMap.put(eid, pathIn);
      this.creationMap.put(eid, Util.milliTime());
      this.pathMaxDistance.put(eid, distance);
   }

   public void render(long p_217676_1_) {
      if (!this.pathMap.isEmpty()) {
         long i = Util.milliTime();

         for(Integer integer : this.pathMap.keySet()) {
            Path path = this.pathMap.get(integer);
            float f = this.pathMaxDistance.get(integer);
            func_222911_a(this.func_222914_b(), path, f, true, true);
         }

         for(Integer integer1 : this.creationMap.keySet().toArray(new Integer[0])) {
            if (i - this.creationMap.get(integer1) > 20000L) {
               this.pathMap.remove(integer1);
               this.creationMap.remove(integer1);
            }
         }

      }
   }

   public static void func_222911_a(ActiveRenderInfo p_222911_0_, Path p_222911_1_, float p_222911_2_, boolean p_222911_3_, boolean p_222911_4_) {
      GlStateManager.pushMatrix();
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.color4f(0.0F, 1.0F, 0.0F, 0.75F);
      GlStateManager.disableTexture();
      GlStateManager.lineWidth(6.0F);
      func_222910_b(p_222911_0_, p_222911_1_, p_222911_2_, p_222911_3_, p_222911_4_);
      GlStateManager.enableTexture();
      GlStateManager.disableBlend();
      GlStateManager.popMatrix();
   }

   private static void func_222910_b(ActiveRenderInfo p_222910_0_, Path p_222910_1_, float p_222910_2_, boolean p_222910_3_, boolean p_222910_4_) {
      func_222912_a(p_222910_0_, p_222910_1_);
      double d0 = p_222910_0_.getProjectedView().x;
      double d1 = p_222910_0_.getProjectedView().y;
      double d2 = p_222910_0_.getProjectedView().z;
      PathPoint pathpoint = p_222910_1_.getTarget();
      if (func_222913_a(p_222910_0_, pathpoint) <= 40.0F) {
         DebugRenderer.func_217730_a((new AxisAlignedBB((double)((float)pathpoint.x + 0.25F), (double)((float)pathpoint.y + 0.25F), (double)pathpoint.z + 0.25D, (double)((float)pathpoint.x + 0.75F), (double)((float)pathpoint.y + 0.75F), (double)((float)pathpoint.z + 0.75F))).offset(-d0, -d1, -d2), 0.0F, 1.0F, 0.0F, 0.5F);

         for(int i = 0; i < p_222910_1_.getCurrentPathLength(); ++i) {
            PathPoint pathpoint1 = p_222910_1_.getPathPointFromIndex(i);
            if (func_222913_a(p_222910_0_, pathpoint1) <= 40.0F) {
               float f = i == p_222910_1_.getCurrentPathIndex() ? 1.0F : 0.0F;
               float f1 = i == p_222910_1_.getCurrentPathIndex() ? 0.0F : 1.0F;
               DebugRenderer.func_217730_a((new AxisAlignedBB((double)((float)pathpoint1.x + 0.5F - p_222910_2_), (double)((float)pathpoint1.y + 0.01F * (float)i), (double)((float)pathpoint1.z + 0.5F - p_222910_2_), (double)((float)pathpoint1.x + 0.5F + p_222910_2_), (double)((float)pathpoint1.y + 0.25F + 0.01F * (float)i), (double)((float)pathpoint1.z + 0.5F + p_222910_2_))).offset(-d0, -d1, -d2), f, 0.0F, f1, 0.5F);
            }
         }
      }

      if (p_222910_3_) {
         for(PathPoint pathpoint3 : p_222910_1_.getClosedSet()) {
            if (func_222913_a(p_222910_0_, pathpoint3) <= 40.0F) {
               DebugRenderer.func_217732_a(String.format("%s", pathpoint3.nodeType), (double)pathpoint3.x + 0.5D, (double)pathpoint3.y + 0.75D, (double)pathpoint3.z + 0.5D, -65536);
               DebugRenderer.func_217732_a(String.format(Locale.ROOT, "%.2f", pathpoint3.costMalus), (double)pathpoint3.x + 0.5D, (double)pathpoint3.y + 0.25D, (double)pathpoint3.z + 0.5D, -65536);
            }
         }

         for(PathPoint pathpoint4 : p_222910_1_.getOpenSet()) {
            if (func_222913_a(p_222910_0_, pathpoint4) <= 40.0F) {
               DebugRenderer.func_217732_a(String.format("%s", pathpoint4.nodeType), (double)pathpoint4.x + 0.5D, (double)pathpoint4.y + 0.75D, (double)pathpoint4.z + 0.5D, -16776961);
               DebugRenderer.func_217732_a(String.format(Locale.ROOT, "%.2f", pathpoint4.costMalus), (double)pathpoint4.x + 0.5D, (double)pathpoint4.y + 0.25D, (double)pathpoint4.z + 0.5D, -16776961);
            }
         }
      }

      if (p_222910_4_) {
         for(int j = 0; j < p_222910_1_.getCurrentPathLength(); ++j) {
            PathPoint pathpoint2 = p_222910_1_.getPathPointFromIndex(j);
            if (func_222913_a(p_222910_0_, pathpoint2) <= 40.0F) {
               DebugRenderer.func_217732_a(String.format("%s", pathpoint2.nodeType), (double)pathpoint2.x + 0.5D, (double)pathpoint2.y + 0.75D, (double)pathpoint2.z + 0.5D, -1);
               DebugRenderer.func_217732_a(String.format(Locale.ROOT, "%.2f", pathpoint2.costMalus), (double)pathpoint2.x + 0.5D, (double)pathpoint2.y + 0.25D, (double)pathpoint2.z + 0.5D, -1);
            }
         }
      }

   }

   public static void func_222912_a(ActiveRenderInfo p_222912_0_, Path p_222912_1_) {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      double d0 = p_222912_0_.getProjectedView().x;
      double d1 = p_222912_0_.getProjectedView().y;
      double d2 = p_222912_0_.getProjectedView().z;
      bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);

      for(int i = 0; i < p_222912_1_.getCurrentPathLength(); ++i) {
         PathPoint pathpoint = p_222912_1_.getPathPointFromIndex(i);
         if (!(func_222913_a(p_222912_0_, pathpoint) > 40.0F)) {
            float f = (float)i / (float)p_222912_1_.getCurrentPathLength() * 0.33F;
            int j = i == 0 ? 0 : MathHelper.hsvToRGB(f, 0.9F, 0.9F);
            int k = j >> 16 & 255;
            int l = j >> 8 & 255;
            int i1 = j & 255;
            bufferbuilder.pos((double)pathpoint.x - d0 + 0.5D, (double)pathpoint.y - d1 + 0.5D, (double)pathpoint.z - d2 + 0.5D).color(k, l, i1, 255).endVertex();
         }
      }

      tessellator.draw();
   }

   private static float func_222913_a(ActiveRenderInfo p_222913_0_, PathPoint p_222913_1_) {
      return (float)(Math.abs((double)p_222913_1_.x - p_222913_0_.getProjectedView().x) + Math.abs((double)p_222913_1_.y - p_222913_0_.getProjectedView().y) + Math.abs((double)p_222913_1_.z - p_222913_0_.getProjectedView().z));
   }

   private ActiveRenderInfo func_222914_b() {
      return this.minecraft.gameRenderer.getActiveRenderInfo();
   }
}