package net.minecraft.client.renderer.debug;

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
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StructureDebugRenderer implements DebugRenderer.IDebugRenderer {
   private final Minecraft minecraft;
   private final Map<DimensionType, Map<String, MutableBoundingBox>> mainBoxes = Maps.newIdentityHashMap();
   private final Map<DimensionType, Map<String, MutableBoundingBox>> subBoxes = Maps.newIdentityHashMap();
   private final Map<DimensionType, Map<String, Boolean>> subBoxFlags = Maps.newIdentityHashMap();

   public StructureDebugRenderer(Minecraft minecraftIn) {
      this.minecraft = minecraftIn;
   }

   public void render(long p_217676_1_) {
      ActiveRenderInfo activerenderinfo = this.minecraft.gameRenderer.getActiveRenderInfo();
      IWorld iworld = this.minecraft.world;
      DimensionType dimensiontype = iworld.getDimension().getType();
      double d0 = activerenderinfo.getProjectedView().x;
      double d1 = activerenderinfo.getProjectedView().y;
      double d2 = activerenderinfo.getProjectedView().z;
      GlStateManager.pushMatrix();
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.disableTexture();
      GlStateManager.disableDepthTest();
      BlockPos blockpos = new BlockPos(activerenderinfo.getProjectedView().x, 0.0D, activerenderinfo.getProjectedView().z);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
      GlStateManager.lineWidth(1.0F);
      if (this.mainBoxes.containsKey(dimensiontype)) {
         for(MutableBoundingBox mutableboundingbox : this.mainBoxes.get(dimensiontype).values()) {
            if (blockpos.withinDistance(mutableboundingbox.func_215126_f(), 500.0D)) {
               WorldRenderer.drawBoundingBox(bufferbuilder, (double)mutableboundingbox.minX - d0, (double)mutableboundingbox.minY - d1, (double)mutableboundingbox.minZ - d2, (double)(mutableboundingbox.maxX + 1) - d0, (double)(mutableboundingbox.maxY + 1) - d1, (double)(mutableboundingbox.maxZ + 1) - d2, 1.0F, 1.0F, 1.0F, 1.0F);
            }
         }
      }

      if (this.subBoxes.containsKey(dimensiontype)) {
         for(Entry<String, MutableBoundingBox> entry : this.subBoxes.get(dimensiontype).entrySet()) {
            String s = entry.getKey();
            MutableBoundingBox mutableboundingbox1 = entry.getValue();
            Boolean obool = this.subBoxFlags.get(dimensiontype).get(s);
            if (blockpos.withinDistance(mutableboundingbox1.func_215126_f(), 500.0D)) {
               if (obool) {
                  WorldRenderer.drawBoundingBox(bufferbuilder, (double)mutableboundingbox1.minX - d0, (double)mutableboundingbox1.minY - d1, (double)mutableboundingbox1.minZ - d2, (double)(mutableboundingbox1.maxX + 1) - d0, (double)(mutableboundingbox1.maxY + 1) - d1, (double)(mutableboundingbox1.maxZ + 1) - d2, 0.0F, 1.0F, 0.0F, 1.0F);
               } else {
                  WorldRenderer.drawBoundingBox(bufferbuilder, (double)mutableboundingbox1.minX - d0, (double)mutableboundingbox1.minY - d1, (double)mutableboundingbox1.minZ - d2, (double)(mutableboundingbox1.maxX + 1) - d0, (double)(mutableboundingbox1.maxY + 1) - d1, (double)(mutableboundingbox1.maxZ + 1) - d2, 0.0F, 0.0F, 1.0F, 1.0F);
               }
            }
         }
      }

      tessellator.draw();
      GlStateManager.enableDepthTest();
      GlStateManager.enableTexture();
      GlStateManager.popMatrix();
   }

   public void func_223454_a(MutableBoundingBox p_223454_1_, List<MutableBoundingBox> p_223454_2_, List<Boolean> p_223454_3_, DimensionType p_223454_4_) {
      if (!this.mainBoxes.containsKey(p_223454_4_)) {
         this.mainBoxes.put(p_223454_4_, Maps.newHashMap());
      }

      if (!this.subBoxes.containsKey(p_223454_4_)) {
         this.subBoxes.put(p_223454_4_, Maps.newHashMap());
         this.subBoxFlags.put(p_223454_4_, Maps.newHashMap());
      }

      this.mainBoxes.get(p_223454_4_).put(p_223454_1_.toString(), p_223454_1_);

      for(int i = 0; i < p_223454_2_.size(); ++i) {
         MutableBoundingBox mutableboundingbox = p_223454_2_.get(i);
         Boolean obool = p_223454_3_.get(i);
         this.subBoxes.get(p_223454_4_).put(mutableboundingbox.toString(), mutableboundingbox);
         this.subBoxFlags.get(p_223454_4_).put(mutableboundingbox.toString(), obool);
      }

   }

   public void func_217675_a() {
      this.mainBoxes.clear();
      this.subBoxes.clear();
      this.subBoxFlags.clear();
   }
}