package net.minecraft.client.renderer.tileentity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.model.ShulkerModel;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.tileentity.BannerTileEntity;
import net.minecraft.tileentity.BeaconTileEntity;
import net.minecraft.tileentity.BedTileEntity;
import net.minecraft.tileentity.BellTileEntity;
import net.minecraft.tileentity.CampfireTileEntity;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.ConduitTileEntity;
import net.minecraft.tileentity.EnchantingTableTileEntity;
import net.minecraft.tileentity.EndGatewayTileEntity;
import net.minecraft.tileentity.EndPortalTileEntity;
import net.minecraft.tileentity.EnderChestTileEntity;
import net.minecraft.tileentity.LecternTileEntity;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.PistonTileEntity;
import net.minecraft.tileentity.ShulkerBoxTileEntity;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TileEntityRendererDispatcher {
   private final Map<Class<? extends TileEntity>, TileEntityRenderer<? extends TileEntity>> renderers = Maps.newHashMap();
   public static final TileEntityRendererDispatcher instance = new TileEntityRendererDispatcher();
   public FontRenderer fontRenderer;
   public static double staticPlayerX;
   public static double staticPlayerY;
   public static double staticPlayerZ;
   public TextureManager textureManager;
   public World world;
   public ActiveRenderInfo renderInfo;
   public RayTraceResult cameraHitResult;

   private TileEntityRendererDispatcher() {
      this.renderers.put(SignTileEntity.class, new SignTileEntityRenderer());
      this.renderers.put(MobSpawnerTileEntity.class, new MobSpawnerTileEntityRenderer());
      this.renderers.put(PistonTileEntity.class, new PistonTileEntityRenderer());
      this.renderers.put(ChestTileEntity.class, new ChestTileEntityRenderer<>());
      this.renderers.put(EnderChestTileEntity.class, new ChestTileEntityRenderer<>());
      this.renderers.put(EnchantingTableTileEntity.class, new EnchantmentTableTileEntityRenderer());
      this.renderers.put(LecternTileEntity.class, new LecternTileEntityRenderer());
      this.renderers.put(EndPortalTileEntity.class, new EndPortalTileEntityRenderer());
      this.renderers.put(EndGatewayTileEntity.class, new EndGatewayTileEntityRenderer());
      this.renderers.put(BeaconTileEntity.class, new BeaconTileEntityRenderer());
      this.renderers.put(SkullTileEntity.class, new SkullTileEntityRenderer());
      this.renderers.put(BannerTileEntity.class, new BannerTileEntityRenderer());
      this.renderers.put(StructureBlockTileEntity.class, new StructureTileEntityRenderer());
      this.renderers.put(ShulkerBoxTileEntity.class, new ShulkerBoxTileEntityRenderer(new ShulkerModel()));
      this.renderers.put(BedTileEntity.class, new BedTileEntityRenderer());
      this.renderers.put(ConduitTileEntity.class, new ConduitTileEntityRenderer());
      this.renderers.put(BellTileEntity.class, new BellTileEntityRenderer());
      this.renderers.put(CampfireTileEntity.class, new CampfireTileEntityRenderer());

      for(TileEntityRenderer<?> tileentityrenderer : this.renderers.values()) {
         tileentityrenderer.setRendererDispatcher(this);
      }

   }

   public <T extends TileEntity> TileEntityRenderer<T> getRenderer(Class<? extends TileEntity> teClass) {
      TileEntityRenderer<? extends TileEntity> tileentityrenderer = this.renderers.get(teClass);
      if (tileentityrenderer == null && teClass != TileEntity.class) {
         tileentityrenderer = this.getRenderer((Class<? extends TileEntity>)teClass.getSuperclass());
         this.renderers.put(teClass, tileentityrenderer);
      }

      return (TileEntityRenderer<T>)tileentityrenderer;
   }

   @Nullable
   public <T extends TileEntity> TileEntityRenderer<T> getRenderer(@Nullable TileEntity tileEntityIn) {
      return tileEntityIn == null || tileEntityIn.isRemoved() ? null : this.getRenderer(tileEntityIn.getClass());
   }

   public void func_217665_a(World p_217665_1_, TextureManager p_217665_2_, FontRenderer p_217665_3_, ActiveRenderInfo p_217665_4_, RayTraceResult p_217665_5_) {
      if (this.world != p_217665_1_) {
         this.setWorld(p_217665_1_);
      }

      this.textureManager = p_217665_2_;
      this.renderInfo = p_217665_4_;
      this.fontRenderer = p_217665_3_;
      this.cameraHitResult = p_217665_5_;
   }

   public void render(TileEntity tileentityIn, float partialTicks, int destroyStage) {
      if (tileentityIn.getDistanceSq(this.renderInfo.getProjectedView().x, this.renderInfo.getProjectedView().y, this.renderInfo.getProjectedView().z) < tileentityIn.getMaxRenderDistanceSquared()) {
         if(!drawingBatch || !tileentityIn.hasFastRenderer()) {
         RenderHelper.enableStandardItemLighting();
         int i = this.world.getCombinedLight(tileentityIn.getPos(), 0);
         int j = i % 65536;
         int k = i / 65536;
         GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)j, (float)k);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         }
         BlockPos blockpos = tileentityIn.getPos();
         this.render(tileentityIn, (double)blockpos.getX() - staticPlayerX, (double)blockpos.getY() - staticPlayerY, (double)blockpos.getZ() - staticPlayerZ, partialTicks, destroyStage, false);
      }

   }

   /**
    * Render this TileEntity at a given set of coordinates
    */
   public void render(TileEntity tileEntityIn, double x, double y, double z, float partialTicks) {
      this.render(tileEntityIn, x, y, z, partialTicks, -1, false);
   }

   public void renderAsItem(TileEntity tileEntityIn) {
      this.render(tileEntityIn, 0.0D, 0.0D, 0.0D, 0.0F, -1, true);
   }

   public void render(TileEntity tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage, boolean hasNoBlock) {
      TileEntityRenderer<TileEntity> tileentityrenderer = this.getRenderer(tileEntityIn);
      if (tileentityrenderer != null) {
         try {
            if (hasNoBlock || tileEntityIn.hasWorld() && tileEntityIn.getType().isValidBlock(tileEntityIn.getBlockState().getBlock())) {

               if(drawingBatch && tileEntityIn.hasFastRenderer())
                  tileentityrenderer.renderTileEntityFast(tileEntityIn, x, y, z, partialTicks, destroyStage, batchBuffer.getBuffer());
               else
               tileentityrenderer.render(tileEntityIn, x, y, z, partialTicks, destroyStage);
            }
         } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering Block Entity");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Block Entity Details");
            tileEntityIn.addInfoToCrashReport(crashreportcategory);
            throw new ReportedException(crashreport);
         }
      }

   }

   public void setWorld(@Nullable World worldIn) {
      this.world = worldIn;
      if (worldIn == null) {
         this.renderInfo = null;
      }

   }

   public FontRenderer getFontRenderer() {
      return this.fontRenderer;
   }

   /**
    * Buffer used for batched TESRs
    */
   private net.minecraft.client.renderer.Tessellator batchBuffer = new net.minecraft.client.renderer.Tessellator(0x200000);
   private boolean drawingBatch = false;

   /**
    * Prepare for a batched TESR rendering.
    * You probably shouldn't call this manually.
    */
   public void preDrawBatch() {
       batchBuffer.getBuffer().begin(org.lwjgl.opengl.GL11.GL_QUADS, net.minecraft.client.renderer.vertex.DefaultVertexFormats.BLOCK);
       drawingBatch = true;
   }

   /**
    * Render all TESRs batched so far.
    * You probably shouldn't call this manually.
    */
   public void drawBatch() {
      textureManager.bindTexture(net.minecraft.client.renderer.texture.AtlasTexture.LOCATION_BLOCKS_TEXTURE);
      net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
      GlStateManager.blendFunc(org.lwjgl.opengl.GL11.GL_SRC_ALPHA, org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA);
      GlStateManager.enableBlend();
      GlStateManager.disableCull();

      if (net.minecraft.client.Minecraft.isAmbientOcclusionEnabled())
         GlStateManager.shadeModel(org.lwjgl.opengl.GL11.GL_SMOOTH);
      else
         GlStateManager.shadeModel(org.lwjgl.opengl.GL11.GL_FLAT);

      batchBuffer.draw();

      net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
      drawingBatch = false;
   }

   //Internal, Do not call Use ClientRegistry.
   public synchronized <T extends TileEntity> void setSpecialRenderer(Class<T> tileEntityClass, TileEntityRenderer<? super T> specialRenderer) {
      this.renderers.put(tileEntityClass, specialRenderer);
   }
}