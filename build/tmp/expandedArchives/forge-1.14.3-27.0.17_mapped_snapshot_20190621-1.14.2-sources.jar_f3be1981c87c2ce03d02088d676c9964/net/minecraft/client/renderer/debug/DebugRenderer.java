package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DebugRenderer {
   public final PathfindingDebugRenderer pathfinding;
   public final DebugRenderer.IDebugRenderer water;
   public final DebugRenderer.IDebugRenderer chunkBorder;
   public final DebugRenderer.IDebugRenderer heightMap;
   public final DebugRenderer.IDebugRenderer collisionBox;
   public final DebugRenderer.IDebugRenderer neighborsUpdate;
   public final CaveDebugRenderer cave;
   public final StructureDebugRenderer structure;
   public final DebugRenderer.IDebugRenderer light;
   public final DebugRenderer.IDebugRenderer worldGenAttempts;
   public final DebugRenderer.IDebugRenderer solidFace;
   public final DebugRenderer.IDebugRenderer field_217740_l;
   public final PointOfInterestDebugRenderer field_217741_m;
   public final RaidDebugRenderer field_222927_n;
   public final EntityAIDebugRenderer field_217742_n;
   private boolean chunkBorderEnabled;

   public DebugRenderer(Minecraft clientIn) {
      this.pathfinding = new PathfindingDebugRenderer(clientIn);
      this.water = new WaterDebugRenderer(clientIn);
      this.chunkBorder = new ChunkBorderDebugRenderer(clientIn);
      this.heightMap = new HeightMapDebugRenderer(clientIn);
      this.collisionBox = new CollisionBoxDebugRenderer(clientIn);
      this.neighborsUpdate = new NeighborsUpdateDebugRenderer(clientIn);
      this.cave = new CaveDebugRenderer(clientIn);
      this.structure = new StructureDebugRenderer(clientIn);
      this.light = new LightDebugRenderer(clientIn);
      this.worldGenAttempts = new WorldGenAttemptsDebugRenderer(clientIn);
      this.solidFace = new SolidFaceDebugRenderer(clientIn);
      this.field_217740_l = new ChunkInfoDebugRenderer(clientIn);
      this.field_217741_m = new PointOfInterestDebugRenderer(clientIn);
      this.field_222927_n = new RaidDebugRenderer(clientIn);
      this.field_217742_n = new EntityAIDebugRenderer(clientIn);
   }

   public void func_217737_a() {
      this.pathfinding.func_217675_a();
      this.water.func_217675_a();
      this.chunkBorder.func_217675_a();
      this.heightMap.func_217675_a();
      this.collisionBox.func_217675_a();
      this.neighborsUpdate.func_217675_a();
      this.cave.func_217675_a();
      this.structure.func_217675_a();
      this.light.func_217675_a();
      this.worldGenAttempts.func_217675_a();
      this.solidFace.func_217675_a();
      this.field_217740_l.func_217675_a();
      this.field_217741_m.func_217675_a();
      this.field_222927_n.func_217675_a();
      this.field_217742_n.func_217675_a();
   }

   public boolean shouldRender() {
      return this.chunkBorderEnabled;
   }

   /**
    * Toggles the debug screen's visibility.
    */
   public boolean toggleChunkBorders() {
      this.chunkBorderEnabled = !this.chunkBorderEnabled;
      return this.chunkBorderEnabled;
   }

   public void renderDebug(long partialTicks) {
      if (this.chunkBorderEnabled && !Minecraft.getInstance().isReducedDebug()) {
         this.chunkBorder.render(partialTicks);
      }

   }

   public static Optional<Entity> func_217728_a(@Nullable Entity p_217728_0_, int p_217728_1_) {
      if (p_217728_0_ == null) {
         return Optional.empty();
      } else {
         Vec3d vec3d = p_217728_0_.getEyePosition(1.0F);
         Vec3d vec3d1 = p_217728_0_.getLook(1.0F).scale((double)p_217728_1_);
         Vec3d vec3d2 = vec3d.add(vec3d1);
         AxisAlignedBB axisalignedbb = p_217728_0_.getBoundingBox().expand(vec3d1).grow(1.0D);
         int i = p_217728_1_ * p_217728_1_;
         Predicate<Entity> predicate = (p_217727_0_) -> {
            return !p_217727_0_.isSpectator() && p_217727_0_.canBeCollidedWith();
         };
         EntityRayTraceResult entityraytraceresult = ProjectileHelper.func_221273_a(p_217728_0_, vec3d, vec3d2, axisalignedbb, predicate, (double)i);
         if (entityraytraceresult == null) {
            return Optional.empty();
         } else {
            return vec3d.squareDistanceTo(entityraytraceresult.getHitVec()) > (double)i ? Optional.empty() : Optional.of(entityraytraceresult.getEntity());
         }
      }
   }

   public static void func_217735_a(BlockPos p_217735_0_, BlockPos p_217735_1_, float p_217735_2_, float p_217735_3_, float p_217735_4_, float p_217735_5_) {
      ActiveRenderInfo activerenderinfo = Minecraft.getInstance().gameRenderer.getActiveRenderInfo();
      if (activerenderinfo.func_216786_h()) {
         Vec3d vec3d = activerenderinfo.getProjectedView().func_216371_e();
         AxisAlignedBB axisalignedbb = (new AxisAlignedBB(p_217735_0_, p_217735_1_)).offset(vec3d);
         func_217730_a(axisalignedbb, p_217735_2_, p_217735_3_, p_217735_4_, p_217735_5_);
      }
   }

   public static void func_217736_a(BlockPos p_217736_0_, float p_217736_1_, float p_217736_2_, float p_217736_3_, float p_217736_4_, float p_217736_5_) {
      ActiveRenderInfo activerenderinfo = Minecraft.getInstance().gameRenderer.getActiveRenderInfo();
      if (activerenderinfo.func_216786_h()) {
         Vec3d vec3d = activerenderinfo.getProjectedView().func_216371_e();
         AxisAlignedBB axisalignedbb = (new AxisAlignedBB(p_217736_0_)).offset(vec3d).grow((double)p_217736_1_);
         func_217730_a(axisalignedbb, p_217736_2_, p_217736_3_, p_217736_4_, p_217736_5_);
      }
   }

   public static void func_217730_a(AxisAlignedBB p_217730_0_, float p_217730_1_, float p_217730_2_, float p_217730_3_, float p_217730_4_) {
      func_217733_a(p_217730_0_.minX, p_217730_0_.minY, p_217730_0_.minZ, p_217730_0_.maxX, p_217730_0_.maxY, p_217730_0_.maxZ, p_217730_1_, p_217730_2_, p_217730_3_, p_217730_4_);
   }

   public static void func_217733_a(double p_217733_0_, double p_217733_2_, double p_217733_4_, double p_217733_6_, double p_217733_8_, double p_217733_10_, float p_217733_12_, float p_217733_13_, float p_217733_14_, float p_217733_15_) {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);
      WorldRenderer.addChainedFilledBoxVertices(bufferbuilder, p_217733_0_, p_217733_2_, p_217733_4_, p_217733_6_, p_217733_8_, p_217733_10_, p_217733_12_, p_217733_13_, p_217733_14_, p_217733_15_);
      tessellator.draw();
   }

   public static void func_217731_a(String p_217731_0_, int p_217731_1_, int p_217731_2_, int p_217731_3_, int p_217731_4_) {
      func_217732_a(p_217731_0_, (double)p_217731_1_ + 0.5D, (double)p_217731_2_ + 0.5D, (double)p_217731_3_ + 0.5D, p_217731_4_);
   }

   public static void func_217732_a(String p_217732_0_, double p_217732_1_, double p_217732_3_, double p_217732_5_, int p_217732_7_) {
      func_217729_a(p_217732_0_, p_217732_1_, p_217732_3_, p_217732_5_, p_217732_7_, 0.02F);
   }

   public static void func_217729_a(String p_217729_0_, double p_217729_1_, double p_217729_3_, double p_217729_5_, int p_217729_7_, float p_217729_8_) {
      func_217734_a(p_217729_0_, p_217729_1_, p_217729_3_, p_217729_5_, p_217729_7_, p_217729_8_, true, 0.0F, false);
   }

   public static void func_217734_a(String p_217734_0_, double p_217734_1_, double p_217734_3_, double p_217734_5_, int p_217734_7_, float p_217734_8_, boolean p_217734_9_, float p_217734_10_, boolean p_217734_11_) {
      Minecraft minecraft = Minecraft.getInstance();
      ActiveRenderInfo activerenderinfo = minecraft.gameRenderer.getActiveRenderInfo();
      if (activerenderinfo.func_216786_h() && minecraft.getRenderManager().options != null) {
         FontRenderer fontrenderer = minecraft.fontRenderer;
         double d0 = activerenderinfo.getProjectedView().x;
         double d1 = activerenderinfo.getProjectedView().y;
         double d2 = activerenderinfo.getProjectedView().z;
         GlStateManager.pushMatrix();
         GlStateManager.translatef((float)(p_217734_1_ - d0), (float)(p_217734_3_ - d1) + 0.07F, (float)(p_217734_5_ - d2));
         GlStateManager.normal3f(0.0F, 1.0F, 0.0F);
         GlStateManager.scalef(p_217734_8_, -p_217734_8_, p_217734_8_);
         EntityRendererManager entityrenderermanager = minecraft.getRenderManager();
         GlStateManager.rotatef(-entityrenderermanager.playerViewY, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(-entityrenderermanager.playerViewX, 1.0F, 0.0F, 0.0F);
         GlStateManager.enableTexture();
         if (p_217734_11_) {
            GlStateManager.disableDepthTest();
         } else {
            GlStateManager.enableDepthTest();
         }

         GlStateManager.depthMask(true);
         GlStateManager.scalef(-1.0F, 1.0F, 1.0F);
         float f = p_217734_9_ ? (float)(-fontrenderer.getStringWidth(p_217734_0_)) / 2.0F : 0.0F;
         f = f - p_217734_10_ / p_217734_8_;
         fontrenderer.drawString(p_217734_0_, f, 0.0F, p_217734_7_);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.enableDepthTest();
         GlStateManager.popMatrix();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public interface IDebugRenderer {
      void render(long p_217676_1_);

      default void func_217675_a() {
      }
   }
}