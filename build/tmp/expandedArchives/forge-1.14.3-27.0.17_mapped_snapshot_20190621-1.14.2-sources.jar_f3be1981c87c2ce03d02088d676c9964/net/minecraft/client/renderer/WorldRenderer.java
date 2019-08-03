package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.SoundType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.chunk.ChunkRender;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.IChunkRendererFactory;
import net.minecraft.client.renderer.chunk.ListedChunkRender;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.settings.CloudOption;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class WorldRenderer implements AutoCloseable, IResourceManagerReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ResourceLocation MOON_PHASES_TEXTURES = new ResourceLocation("textures/environment/moon_phases.png");
   private static final ResourceLocation SUN_TEXTURES = new ResourceLocation("textures/environment/sun.png");
   private static final ResourceLocation CLOUDS_TEXTURES = new ResourceLocation("textures/environment/clouds.png");
   private static final ResourceLocation END_SKY_TEXTURES = new ResourceLocation("textures/environment/end_sky.png");
   private static final ResourceLocation FORCEFIELD_TEXTURES = new ResourceLocation("textures/misc/forcefield.png");
   public static final Direction[] FACINGS = Direction.values();
   private final Minecraft mc;
   private final TextureManager textureManager;
   private final EntityRendererManager renderManager;
   private ClientWorld world;
   private Set<ChunkRender> chunksToUpdate = Sets.newLinkedHashSet();
   private List<WorldRenderer.LocalRenderInformationContainer> renderInfos = Lists.newArrayListWithCapacity(69696);
   private final Set<TileEntity> setTileEntities = Sets.newHashSet();
   private ViewFrustum viewFrustum;
   private int starGLCallList = -1;
   private int glSkyList = -1;
   private int glSkyList2 = -1;
   private final VertexFormat vertexBufferFormat;
   private VertexBuffer starVBO;
   private VertexBuffer skyVBO;
   private VertexBuffer sky2VBO;
   private final int cloudStride = 28;
   private boolean cloudsNeedUpdate = true;
   private int glCloudsList = -1;
   private VertexBuffer cloudsVBO;
   private int ticks;
   private final Map<Integer, DestroyBlockProgress> damagedBlocks = Maps.newHashMap();
   private final Map<BlockPos, ISound> mapSoundPositions = Maps.newHashMap();
   private final TextureAtlasSprite[] destroyBlockIcons = new TextureAtlasSprite[10];
   private Framebuffer entityOutlineFramebuffer;
   private ShaderGroup entityOutlineShader;
   private double frustumUpdatePosX = Double.MIN_VALUE;
   private double frustumUpdatePosY = Double.MIN_VALUE;
   private double frustumUpdatePosZ = Double.MIN_VALUE;
   private int frustumUpdatePosChunkX = Integer.MIN_VALUE;
   private int frustumUpdatePosChunkY = Integer.MIN_VALUE;
   private int frustumUpdatePosChunkZ = Integer.MIN_VALUE;
   private double lastViewEntityX = Double.MIN_VALUE;
   private double lastViewEntityY = Double.MIN_VALUE;
   private double lastViewEntityZ = Double.MIN_VALUE;
   private double lastViewEntityPitch = Double.MIN_VALUE;
   private double lastViewEntityYaw = Double.MIN_VALUE;
   private int cloudsCheckX = Integer.MIN_VALUE;
   private int cloudsCheckY = Integer.MIN_VALUE;
   private int cloudsCheckZ = Integer.MIN_VALUE;
   private Vec3d cloudsCheckColor = Vec3d.ZERO;
   private CloudOption cloudOption;
   private ChunkRenderDispatcher renderDispatcher;
   private AbstractChunkRenderContainer renderContainer;
   private int renderDistanceChunks = -1;
   private int renderEntitiesStartupCounter = 2;
   private int countEntitiesRendered;
   private int countEntitiesHidden;
   private boolean debugFixTerrainFrustum;
   private ClippingHelper debugFixedClippingHelper;
   private final Vector4f[] debugTerrainMatrix = new Vector4f[8];
   private final Vector3d debugTerrainFrustumPosition = new Vector3d();
   private boolean vboEnabled;
   private IChunkRendererFactory renderChunkFactory;
   private double prevRenderSortX;
   private double prevRenderSortY;
   private double prevRenderSortZ;
   private boolean displayListEntitiesDirty = true;
   private boolean entityOutlinesRendered;

   public WorldRenderer(Minecraft mcIn) {
      this.mc = mcIn;
      this.renderManager = mcIn.getRenderManager();
      this.textureManager = mcIn.getTextureManager();
      this.vboEnabled = GLX.useVbo();
      if (this.vboEnabled) {
         this.renderContainer = new VboRenderList();
         this.renderChunkFactory = ChunkRender::new;
      } else {
         this.renderContainer = new RenderList();
         this.renderChunkFactory = ListedChunkRender::new;
      }

      this.vertexBufferFormat = new VertexFormat();
      this.vertexBufferFormat.addElement(new VertexFormatElement(0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.POSITION, 3));
      this.generateStars();
      this.generateSky();
      this.generateSky2();
   }

   public void close() {
      if (this.entityOutlineShader != null) {
         this.entityOutlineShader.close();
      }

   }

   public void onResourceManagerReload(IResourceManager resourceManager) {
      this.textureManager.bindTexture(FORCEFIELD_TEXTURES);
      GlStateManager.texParameter(3553, 10242, 10497);
      GlStateManager.texParameter(3553, 10243, 10497);
      GlStateManager.bindTexture(0);
      this.updateDestroyBlockIcons();
      this.makeEntityOutlineShader();
   }

   private void updateDestroyBlockIcons() {
      AtlasTexture atlastexture = this.mc.getTextureMap();
      this.destroyBlockIcons[0] = atlastexture.getSprite(ModelBakery.LOCATION_DESTROY_STAGE_0);
      this.destroyBlockIcons[1] = atlastexture.getSprite(ModelBakery.LOCATION_DESTROY_STAGE_1);
      this.destroyBlockIcons[2] = atlastexture.getSprite(ModelBakery.LOCATION_DESTROY_STAGE_2);
      this.destroyBlockIcons[3] = atlastexture.getSprite(ModelBakery.LOCATION_DESTROY_STAGE_3);
      this.destroyBlockIcons[4] = atlastexture.getSprite(ModelBakery.LOCATION_DESTROY_STAGE_4);
      this.destroyBlockIcons[5] = atlastexture.getSprite(ModelBakery.LOCATION_DESTROY_STAGE_5);
      this.destroyBlockIcons[6] = atlastexture.getSprite(ModelBakery.LOCATION_DESTROY_STAGE_6);
      this.destroyBlockIcons[7] = atlastexture.getSprite(ModelBakery.LOCATION_DESTROY_STAGE_7);
      this.destroyBlockIcons[8] = atlastexture.getSprite(ModelBakery.LOCATION_DESTROY_STAGE_8);
      this.destroyBlockIcons[9] = atlastexture.getSprite(ModelBakery.LOCATION_DESTROY_STAGE_9);
   }

   /**
    * Creates the entity outline shader to be stored in RenderGlobal.entityOutlineShader
    */
   public void makeEntityOutlineShader() {
      if (GLX.usePostProcess) {
         if (ShaderLinkHelper.getStaticShaderLinkHelper() == null) {
            ShaderLinkHelper.setNewStaticShaderLinkHelper();
         }

         if (this.entityOutlineShader != null) {
            this.entityOutlineShader.close();
         }

         ResourceLocation resourcelocation = new ResourceLocation("shaders/post/entity_outline.json");

         try {
            this.entityOutlineShader = new ShaderGroup(this.mc.getTextureManager(), this.mc.getResourceManager(), this.mc.getFramebuffer(), resourcelocation);
            this.entityOutlineShader.createBindFramebuffers(this.mc.mainWindow.getFramebufferWidth(), this.mc.mainWindow.getFramebufferHeight());
            this.entityOutlineFramebuffer = this.entityOutlineShader.getFramebufferRaw("final");
         } catch (IOException ioexception) {
            LOGGER.warn("Failed to load shader: {}", resourcelocation, ioexception);
            this.entityOutlineShader = null;
            this.entityOutlineFramebuffer = null;
         } catch (JsonSyntaxException jsonsyntaxexception) {
            LOGGER.warn("Failed to load shader: {}", resourcelocation, jsonsyntaxexception);
            this.entityOutlineShader = null;
            this.entityOutlineFramebuffer = null;
         }
      } else {
         this.entityOutlineShader = null;
         this.entityOutlineFramebuffer = null;
      }

   }

   public void renderEntityOutlineFramebuffer() {
      if (this.isRenderEntityOutlines()) {
         GlStateManager.enableBlend();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
         this.entityOutlineFramebuffer.framebufferRenderExt(this.mc.mainWindow.getFramebufferWidth(), this.mc.mainWindow.getFramebufferHeight(), false);
         GlStateManager.disableBlend();
      }

   }

   protected boolean isRenderEntityOutlines() {
      return this.entityOutlineFramebuffer != null && this.entityOutlineShader != null && this.mc.player != null;
   }

   private void generateSky2() {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      if (this.sky2VBO != null) {
         this.sky2VBO.deleteGlBuffers();
      }

      if (this.glSkyList2 >= 0) {
         GLAllocation.deleteDisplayLists(this.glSkyList2);
         this.glSkyList2 = -1;
      }

      if (this.vboEnabled) {
         this.sky2VBO = new VertexBuffer(this.vertexBufferFormat);
         this.renderSky(bufferbuilder, -16.0F, true);
         bufferbuilder.finishDrawing();
         bufferbuilder.reset();
         this.sky2VBO.bufferData(bufferbuilder.getByteBuffer());
      } else {
         this.glSkyList2 = GLAllocation.generateDisplayLists(1);
         GlStateManager.newList(this.glSkyList2, 4864);
         this.renderSky(bufferbuilder, -16.0F, true);
         tessellator.draw();
         GlStateManager.endList();
      }

   }

   private void generateSky() {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      if (this.skyVBO != null) {
         this.skyVBO.deleteGlBuffers();
      }

      if (this.glSkyList >= 0) {
         GLAllocation.deleteDisplayLists(this.glSkyList);
         this.glSkyList = -1;
      }

      if (this.vboEnabled) {
         this.skyVBO = new VertexBuffer(this.vertexBufferFormat);
         this.renderSky(bufferbuilder, 16.0F, false);
         bufferbuilder.finishDrawing();
         bufferbuilder.reset();
         this.skyVBO.bufferData(bufferbuilder.getByteBuffer());
      } else {
         this.glSkyList = GLAllocation.generateDisplayLists(1);
         GlStateManager.newList(this.glSkyList, 4864);
         this.renderSky(bufferbuilder, 16.0F, false);
         tessellator.draw();
         GlStateManager.endList();
      }

   }

   private void renderSky(BufferBuilder bufferBuilderIn, float posY, boolean reverseX) {
      int i = 64;
      int j = 6;
      bufferBuilderIn.begin(7, DefaultVertexFormats.POSITION);

      for(int k = -384; k <= 384; k += 64) {
         for(int l = -384; l <= 384; l += 64) {
            float f = (float)k;
            float f1 = (float)(k + 64);
            if (reverseX) {
               f1 = (float)k;
               f = (float)(k + 64);
            }

            bufferBuilderIn.pos((double)f, (double)posY, (double)l).endVertex();
            bufferBuilderIn.pos((double)f1, (double)posY, (double)l).endVertex();
            bufferBuilderIn.pos((double)f1, (double)posY, (double)(l + 64)).endVertex();
            bufferBuilderIn.pos((double)f, (double)posY, (double)(l + 64)).endVertex();
         }
      }

   }

   private void generateStars() {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      if (this.starVBO != null) {
         this.starVBO.deleteGlBuffers();
      }

      if (this.starGLCallList >= 0) {
         GLAllocation.deleteDisplayLists(this.starGLCallList);
         this.starGLCallList = -1;
      }

      if (this.vboEnabled) {
         this.starVBO = new VertexBuffer(this.vertexBufferFormat);
         this.renderStars(bufferbuilder);
         bufferbuilder.finishDrawing();
         bufferbuilder.reset();
         this.starVBO.bufferData(bufferbuilder.getByteBuffer());
      } else {
         this.starGLCallList = GLAllocation.generateDisplayLists(1);
         GlStateManager.pushMatrix();
         GlStateManager.newList(this.starGLCallList, 4864);
         this.renderStars(bufferbuilder);
         tessellator.draw();
         GlStateManager.endList();
         GlStateManager.popMatrix();
      }

   }

   private void renderStars(BufferBuilder bufferBuilderIn) {
      Random random = new Random(10842L);
      bufferBuilderIn.begin(7, DefaultVertexFormats.POSITION);

      for(int i = 0; i < 1500; ++i) {
         double d0 = (double)(random.nextFloat() * 2.0F - 1.0F);
         double d1 = (double)(random.nextFloat() * 2.0F - 1.0F);
         double d2 = (double)(random.nextFloat() * 2.0F - 1.0F);
         double d3 = (double)(0.15F + random.nextFloat() * 0.1F);
         double d4 = d0 * d0 + d1 * d1 + d2 * d2;
         if (d4 < 1.0D && d4 > 0.01D) {
            d4 = 1.0D / Math.sqrt(d4);
            d0 = d0 * d4;
            d1 = d1 * d4;
            d2 = d2 * d4;
            double d5 = d0 * 100.0D;
            double d6 = d1 * 100.0D;
            double d7 = d2 * 100.0D;
            double d8 = Math.atan2(d0, d2);
            double d9 = Math.sin(d8);
            double d10 = Math.cos(d8);
            double d11 = Math.atan2(Math.sqrt(d0 * d0 + d2 * d2), d1);
            double d12 = Math.sin(d11);
            double d13 = Math.cos(d11);
            double d14 = random.nextDouble() * Math.PI * 2.0D;
            double d15 = Math.sin(d14);
            double d16 = Math.cos(d14);

            for(int j = 0; j < 4; ++j) {
               double d17 = 0.0D;
               double d18 = (double)((j & 2) - 1) * d3;
               double d19 = (double)((j + 1 & 2) - 1) * d3;
               double d20 = 0.0D;
               double d21 = d18 * d16 - d19 * d15;
               double d22 = d19 * d16 + d18 * d15;
               double d23 = d21 * d12 + 0.0D * d13;
               double d24 = 0.0D * d12 - d21 * d13;
               double d25 = d24 * d9 - d22 * d10;
               double d26 = d22 * d9 + d24 * d10;
               bufferBuilderIn.pos(d5 + d25, d6 + d23, d7 + d26).endVertex();
            }
         }
      }

   }

   /**
    * set null to clear
    */
   public void setWorldAndLoadRenderers(@Nullable ClientWorld worldClientIn) {
      this.frustumUpdatePosX = Double.MIN_VALUE;
      this.frustumUpdatePosY = Double.MIN_VALUE;
      this.frustumUpdatePosZ = Double.MIN_VALUE;
      this.frustumUpdatePosChunkX = Integer.MIN_VALUE;
      this.frustumUpdatePosChunkY = Integer.MIN_VALUE;
      this.frustumUpdatePosChunkZ = Integer.MIN_VALUE;
      this.renderManager.setWorld(worldClientIn);
      this.world = worldClientIn;
      if (worldClientIn != null) {
         this.loadRenderers();
      } else {
         this.chunksToUpdate.clear();
         this.renderInfos.clear();
         if (this.viewFrustum != null) {
            this.viewFrustum.deleteGlResources();
            this.viewFrustum = null;
         }

         if (this.renderDispatcher != null) {
            this.renderDispatcher.stopWorkerThreads();
         }

         this.renderDispatcher = null;
         this.setTileEntities.clear();
      }

   }

   /**
    * Loads all the renderers and sets up the basic settings usage
    */
   public void loadRenderers() {
      if (this.world != null) {
         if (this.renderDispatcher == null) {
            this.renderDispatcher = new ChunkRenderDispatcher(this.mc.isJava64bit());
         }

         this.displayListEntitiesDirty = true;
         this.cloudsNeedUpdate = true;
         LeavesBlock.setRenderTranslucent(this.mc.gameSettings.fancyGraphics);
         this.renderDistanceChunks = this.mc.gameSettings.renderDistanceChunks;
         boolean flag = this.vboEnabled;
         this.vboEnabled = GLX.useVbo();
         if (flag && !this.vboEnabled) {
            this.renderContainer = new RenderList();
            this.renderChunkFactory = ListedChunkRender::new;
         } else if (!flag && this.vboEnabled) {
            this.renderContainer = new VboRenderList();
            this.renderChunkFactory = ChunkRender::new;
         }

         if (flag != this.vboEnabled) {
            this.generateStars();
            this.generateSky();
            this.generateSky2();
         }

         if (this.viewFrustum != null) {
            this.viewFrustum.deleteGlResources();
         }

         this.stopChunkUpdates();
         synchronized(this.setTileEntities) {
            this.setTileEntities.clear();
         }

         this.viewFrustum = new ViewFrustum(this.world, this.mc.gameSettings.renderDistanceChunks, this, this.renderChunkFactory);
         if (this.world != null) {
            Entity entity = this.mc.getRenderViewEntity();
            if (entity != null) {
               this.viewFrustum.updateChunkPositions(entity.posX, entity.posZ);
            }
         }

         this.renderEntitiesStartupCounter = 2;
      }
   }

   protected void stopChunkUpdates() {
      this.chunksToUpdate.clear();
      this.renderDispatcher.stopChunkUpdates();
   }

   public void createBindEntityOutlineFbs(int width, int height) {
      this.setDisplayListEntitiesDirty();
      if (GLX.usePostProcess) {
         if (this.entityOutlineShader != null) {
            this.entityOutlineShader.createBindFramebuffers(width, height);
         }

      }
   }

   public void func_215326_a(ActiveRenderInfo p_215326_1_, ICamera p_215326_2_, float p_215326_3_) {
      if (this.renderEntitiesStartupCounter > 0) {
         --this.renderEntitiesStartupCounter;
      } else {
         double d0 = p_215326_1_.getProjectedView().x;
         double d1 = p_215326_1_.getProjectedView().y;
         double d2 = p_215326_1_.getProjectedView().z;
         this.world.getProfiler().startSection("prepare");
         TileEntityRendererDispatcher.instance.func_217665_a(this.world, this.mc.getTextureManager(), this.mc.fontRenderer, p_215326_1_, this.mc.objectMouseOver);
         this.renderManager.func_217781_a(this.world, this.mc.fontRenderer, p_215326_1_, this.mc.pointedEntity, this.mc.gameSettings);
         this.countEntitiesRendered = 0;
         this.countEntitiesHidden = 0;
         double d3 = p_215326_1_.getProjectedView().x;
         double d4 = p_215326_1_.getProjectedView().y;
         double d5 = p_215326_1_.getProjectedView().z;
         TileEntityRendererDispatcher.staticPlayerX = d3;
         TileEntityRendererDispatcher.staticPlayerY = d4;
         TileEntityRendererDispatcher.staticPlayerZ = d5;
         this.renderManager.setRenderPosition(d3, d4, d5);
         this.mc.gameRenderer.enableLightmap();
         this.world.getProfiler().endStartSection("entities");
         List<Entity> list = Lists.newArrayList();
         List<Entity> list1 = Lists.newArrayList();

         for(Entity entity : this.world.func_217416_b()) {
            if ((this.renderManager.shouldRender(entity, p_215326_2_, d0, d1, d2) || entity.isRidingOrBeingRiddenBy(this.mc.player)) && (entity != p_215326_1_.func_216773_g() || p_215326_1_.func_216770_i() || p_215326_1_.func_216773_g() instanceof LivingEntity && ((LivingEntity)p_215326_1_.func_216773_g()).isSleeping())) {
               ++this.countEntitiesRendered;
               this.renderManager.renderEntityStatic(entity, p_215326_3_, false);
               if (entity.isGlowing() || entity instanceof PlayerEntity && this.mc.player.isSpectator() && this.mc.gameSettings.keyBindSpectatorOutlines.isKeyDown()) {
                  list.add(entity);
               }

               if (this.renderManager.isRenderMultipass(entity)) {
                  list1.add(entity);
               }
            }
         }

         if (!list1.isEmpty()) {
            for(Entity entity1 : list1) {
               this.renderManager.renderMultipass(entity1, p_215326_3_);
            }
         }

         if (this.isRenderEntityOutlines() && (!list.isEmpty() || this.entityOutlinesRendered)) {
            this.world.getProfiler().endStartSection("entityOutlines");
            this.entityOutlineFramebuffer.func_216493_b(Minecraft.IS_RUNNING_ON_MAC);
            this.entityOutlinesRendered = !list.isEmpty();
            if (!list.isEmpty()) {
               GlStateManager.depthFunc(519);
               GlStateManager.disableFog();
               this.entityOutlineFramebuffer.bindFramebuffer(false);
               RenderHelper.disableStandardItemLighting();
               this.renderManager.setRenderOutlines(true);

               for(int i = 0; i < list.size(); ++i) {
                  this.renderManager.renderEntityStatic(list.get(i), p_215326_3_, false);
               }

               this.renderManager.setRenderOutlines(false);
               RenderHelper.enableStandardItemLighting();
               GlStateManager.depthMask(false);
               this.entityOutlineShader.render(p_215326_3_);
               GlStateManager.enableLighting();
               GlStateManager.depthMask(true);
               GlStateManager.enableFog();
               GlStateManager.enableBlend();
               GlStateManager.enableColorMaterial();
               GlStateManager.depthFunc(515);
               GlStateManager.enableDepthTest();
               GlStateManager.enableAlphaTest();
            }

            this.mc.getFramebuffer().bindFramebuffer(false);
         }

         this.world.getProfiler().endStartSection("blockentities");
         RenderHelper.enableStandardItemLighting();

         TileEntityRendererDispatcher.instance.preDrawBatch();
         for(WorldRenderer.LocalRenderInformationContainer worldrenderer$localrenderinformationcontainer : this.renderInfos) {
            List<TileEntity> list2 = worldrenderer$localrenderinformationcontainer.renderChunk.getCompiledChunk().getTileEntities();
            if (!list2.isEmpty()) {
               for(TileEntity tileentity : list2) {
                  if (!p_215326_2_.isBoundingBoxInFrustum(tileentity.getRenderBoundingBox())) continue;
                  TileEntityRendererDispatcher.instance.render(tileentity, p_215326_3_, -1);
               }
            }
         }

         synchronized(this.setTileEntities) {
            for(TileEntity tileentity1 : this.setTileEntities) {
               if (!p_215326_2_.isBoundingBoxInFrustum(tileentity1.getRenderBoundingBox())) continue;
               TileEntityRendererDispatcher.instance.render(tileentity1, p_215326_3_, -1);
            }
         }
         TileEntityRendererDispatcher.instance.drawBatch();

         this.preRenderDamagedBlocks();

         for(DestroyBlockProgress destroyblockprogress : this.damagedBlocks.values()) {
            BlockPos blockpos = destroyblockprogress.getPosition();
            BlockState blockstate = this.world.getBlockState(blockpos);
            if (blockstate.hasTileEntity()) {
               TileEntity tileentity2 = this.world.getTileEntity(blockpos);
               if (tileentity2 instanceof ChestTileEntity && blockstate.get(ChestBlock.TYPE) == ChestType.LEFT) {
                  blockpos = blockpos.offset(blockstate.get(ChestBlock.FACING).rotateY());
                  tileentity2 = this.world.getTileEntity(blockpos);
               }

               if (tileentity2 != null && blockstate.hasCustomBreakingProgress()) {
                  TileEntityRendererDispatcher.instance.render(tileentity2, p_215326_3_, destroyblockprogress.getPartialBlockDamage());
               }
            }
         }

         this.postRenderDamagedBlocks();
         this.mc.gameRenderer.disableLightmap();
         this.mc.getProfiler().endSection();
      }
   }

   /**
    * Gets the render info for use on the Debug screen
    */
   public String getDebugInfoRenders() {
      int i = this.viewFrustum.renderChunks.length;
      int j = this.getRenderedChunks();
      return String.format("C: %d/%d %sD: %d, %s", j, i, this.mc.renderChunksMany ? "(s) " : "", this.renderDistanceChunks, this.renderDispatcher == null ? "null" : this.renderDispatcher.getDebugInfo());
   }

   protected int getRenderedChunks() {
      int i = 0;

      for(WorldRenderer.LocalRenderInformationContainer worldrenderer$localrenderinformationcontainer : this.renderInfos) {
         CompiledChunk compiledchunk = worldrenderer$localrenderinformationcontainer.renderChunk.compiledChunk;
         if (compiledchunk != CompiledChunk.DUMMY && !compiledchunk.isEmpty()) {
            ++i;
         }
      }

      return i;
   }

   /**
    * Gets the entities info for use on the Debug screen
    */
   public String getDebugInfoEntities() {
      return "E: " + this.countEntitiesRendered + "/" + this.world.func_217425_f() + ", B: " + this.countEntitiesHidden;
   }

   public void func_215320_a(ActiveRenderInfo p_215320_1_, ICamera p_215320_2_, int p_215320_3_, boolean p_215320_4_) {
      if (this.mc.gameSettings.renderDistanceChunks != this.renderDistanceChunks) {
         this.loadRenderers();
      }

      this.world.getProfiler().startSection("camera");
      double d0 = this.mc.player.posX - this.frustumUpdatePosX;
      double d1 = this.mc.player.posY - this.frustumUpdatePosY;
      double d2 = this.mc.player.posZ - this.frustumUpdatePosZ;
      if (this.frustumUpdatePosChunkX != this.mc.player.chunkCoordX || this.frustumUpdatePosChunkY != this.mc.player.chunkCoordY || this.frustumUpdatePosChunkZ != this.mc.player.chunkCoordZ || d0 * d0 + d1 * d1 + d2 * d2 > 16.0D) {
         this.frustumUpdatePosX = this.mc.player.posX;
         this.frustumUpdatePosY = this.mc.player.posY;
         this.frustumUpdatePosZ = this.mc.player.posZ;
         this.frustumUpdatePosChunkX = this.mc.player.chunkCoordX;
         this.frustumUpdatePosChunkY = this.mc.player.chunkCoordY;
         this.frustumUpdatePosChunkZ = this.mc.player.chunkCoordZ;
         this.viewFrustum.updateChunkPositions(this.mc.player.posX, this.mc.player.posZ);
      }

      this.world.getProfiler().endStartSection("renderlistcamera");
      this.renderContainer.initialize(p_215320_1_.getProjectedView().x, p_215320_1_.getProjectedView().y, p_215320_1_.getProjectedView().z);
      this.renderDispatcher.func_217669_a(p_215320_1_.getProjectedView());
      this.world.getProfiler().endStartSection("cull");
      if (this.debugFixedClippingHelper != null) {
         Frustum frustum = new Frustum(this.debugFixedClippingHelper);
         frustum.setPosition(this.debugTerrainFrustumPosition.x, this.debugTerrainFrustumPosition.y, this.debugTerrainFrustumPosition.z);
         p_215320_2_ = frustum;
      }

      this.mc.getProfiler().endStartSection("culling");
      BlockPos blockpos1 = p_215320_1_.func_216780_d();
      ChunkRender chunkrender = this.viewFrustum.getRenderChunk(blockpos1);
      BlockPos blockpos = new BlockPos(MathHelper.floor(p_215320_1_.getProjectedView().x / 16.0D) * 16, MathHelper.floor(p_215320_1_.getProjectedView().y / 16.0D) * 16, MathHelper.floor(p_215320_1_.getProjectedView().z / 16.0D) * 16);
      float f = p_215320_1_.getPitch();
      float f1 = p_215320_1_.getYaw();
      this.displayListEntitiesDirty = this.displayListEntitiesDirty || !this.chunksToUpdate.isEmpty() || p_215320_1_.getProjectedView().x != this.lastViewEntityX || p_215320_1_.getProjectedView().y != this.lastViewEntityY || p_215320_1_.getProjectedView().z != this.lastViewEntityZ || (double)f != this.lastViewEntityPitch || (double)f1 != this.lastViewEntityYaw;
      this.lastViewEntityX = p_215320_1_.getProjectedView().x;
      this.lastViewEntityY = p_215320_1_.getProjectedView().y;
      this.lastViewEntityZ = p_215320_1_.getProjectedView().z;
      this.lastViewEntityPitch = (double)f;
      this.lastViewEntityYaw = (double)f1;
      boolean flag = this.debugFixedClippingHelper != null;
      this.mc.getProfiler().endStartSection("update");
      if (!flag && this.displayListEntitiesDirty) {
         this.displayListEntitiesDirty = false;
         this.renderInfos = Lists.newArrayList();
         Queue<WorldRenderer.LocalRenderInformationContainer> queue = Queues.newArrayDeque();
         Entity.setRenderDistanceWeight(MathHelper.clamp((double)this.mc.gameSettings.renderDistanceChunks / 8.0D, 1.0D, 2.5D));
         boolean flag1 = this.mc.renderChunksMany;
         if (chunkrender != null) {
            boolean flag2 = false;
            WorldRenderer.LocalRenderInformationContainer worldrenderer$localrenderinformationcontainer3 = new WorldRenderer.LocalRenderInformationContainer(chunkrender, (Direction)null, 0);
            Set<Direction> set1 = this.getVisibleFacings(blockpos1);
            if (set1.size() == 1) {
               Vec3d vec3d = p_215320_1_.func_216787_l();
               Direction direction = Direction.getFacingFromVector(vec3d.x, vec3d.y, vec3d.z).getOpposite();
               set1.remove(direction);
            }

            if (set1.isEmpty()) {
               flag2 = true;
            }

            if (flag2 && !p_215320_4_) {
               this.renderInfos.add(worldrenderer$localrenderinformationcontainer3);
            } else {
               if (p_215320_4_ && this.world.getBlockState(blockpos1).isOpaqueCube(this.world, blockpos1)) {
                  flag1 = false;
               }

               chunkrender.setFrameIndex(p_215320_3_);
               queue.add(worldrenderer$localrenderinformationcontainer3);
            }
         } else {
            int i = blockpos1.getY() > 0 ? 248 : 8;

            for(int j = -this.renderDistanceChunks; j <= this.renderDistanceChunks; ++j) {
               for(int k = -this.renderDistanceChunks; k <= this.renderDistanceChunks; ++k) {
                  ChunkRender chunkrender1 = this.viewFrustum.getRenderChunk(new BlockPos((j << 4) + 8, i, (k << 4) + 8));
                  if (chunkrender1 != null && p_215320_2_.isBoundingBoxInFrustum(chunkrender1.boundingBox.expand(0.0, blockpos1.getY() > 0 ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY, 0.0))) { // Forge: fix MC-73139
                     chunkrender1.setFrameIndex(p_215320_3_);
                     queue.add(new WorldRenderer.LocalRenderInformationContainer(chunkrender1, (Direction)null, 0));
                  }
               }
            }
         }

         this.mc.getProfiler().startSection("iteration");

         while(!queue.isEmpty()) {
            WorldRenderer.LocalRenderInformationContainer worldrenderer$localrenderinformationcontainer1 = queue.poll();
            ChunkRender chunkrender3 = worldrenderer$localrenderinformationcontainer1.renderChunk;
            Direction direction2 = worldrenderer$localrenderinformationcontainer1.facing;
            this.renderInfos.add(worldrenderer$localrenderinformationcontainer1);

            for(Direction direction1 : FACINGS) {
               ChunkRender chunkrender2 = this.getRenderChunkOffset(blockpos, chunkrender3, direction1);
               if ((!flag1 || !worldrenderer$localrenderinformationcontainer1.hasDirection(direction1.getOpposite())) && (!flag1 || direction2 == null || chunkrender3.getCompiledChunk().isVisible(direction2.getOpposite(), direction1)) && chunkrender2 != null && chunkrender2.func_217674_b() && chunkrender2.setFrameIndex(p_215320_3_) && p_215320_2_.isBoundingBoxInFrustum(chunkrender2.boundingBox)) {
                  WorldRenderer.LocalRenderInformationContainer worldrenderer$localrenderinformationcontainer = new WorldRenderer.LocalRenderInformationContainer(chunkrender2, direction1, worldrenderer$localrenderinformationcontainer1.counter + 1);
                  worldrenderer$localrenderinformationcontainer.setDirection(worldrenderer$localrenderinformationcontainer1.setFacing, direction1);
                  queue.add(worldrenderer$localrenderinformationcontainer);
               }
            }
         }

         this.mc.getProfiler().endSection();
      }

      this.mc.getProfiler().endStartSection("captureFrustum");
      if (this.debugFixTerrainFrustum) {
         this.fixTerrainFrustum(p_215320_1_.getProjectedView().x, p_215320_1_.getProjectedView().y, p_215320_1_.getProjectedView().z);
         this.debugFixTerrainFrustum = false;
      }

      this.mc.getProfiler().endStartSection("rebuildNear");
      Set<ChunkRender> set = this.chunksToUpdate;
      this.chunksToUpdate = Sets.newLinkedHashSet();

      for(WorldRenderer.LocalRenderInformationContainer worldrenderer$localrenderinformationcontainer2 : this.renderInfos) {
         ChunkRender chunkrender4 = worldrenderer$localrenderinformationcontainer2.renderChunk;
         if (chunkrender4.needsUpdate() || set.contains(chunkrender4)) {
            this.displayListEntitiesDirty = true;
            BlockPos blockpos2 = chunkrender4.getPosition().add(8, 8, 8);
            boolean flag3 = blockpos2.distanceSq(blockpos1) < 768.0D;
            if (net.minecraftforge.common.ForgeMod.alwaysSetupTerrainOffThread || !chunkrender4.needsImmediateUpdate() && !flag3) {
               this.chunksToUpdate.add(chunkrender4);
            } else {
               this.mc.getProfiler().startSection("build near");
               this.renderDispatcher.updateChunkNow(chunkrender4);
               chunkrender4.clearNeedsUpdate();
               this.mc.getProfiler().endSection();
            }
         }
      }

      this.chunksToUpdate.addAll(set);
      this.mc.getProfiler().endSection();
   }

   private Set<Direction> getVisibleFacings(BlockPos pos) {
      VisGraph visgraph = new VisGraph();
      BlockPos blockpos = new BlockPos(pos.getX() >> 4 << 4, pos.getY() >> 4 << 4, pos.getZ() >> 4 << 4);
      Chunk chunk = this.world.getChunkAt(blockpos);

      for(BlockPos blockpos1 : BlockPos.getAllInBoxMutable(blockpos, blockpos.add(15, 15, 15))) {
         if (chunk.getBlockState(blockpos1).isOpaqueCube(this.world, blockpos1)) {
            visgraph.setOpaqueCube(blockpos1);
         }
      }

      return visgraph.getVisibleFacings(pos);
   }

   /**
    * Returns RenderChunk offset from given RenderChunk in given direction, or null if it can't be seen by player at
    * given BlockPos.
    */
   @Nullable
   private ChunkRender getRenderChunkOffset(BlockPos playerPos, ChunkRender renderChunkBase, Direction facing) {
      BlockPos blockpos = renderChunkBase.getBlockPosOffset16(facing);
      if (MathHelper.abs(playerPos.getX() - blockpos.getX()) > this.renderDistanceChunks * 16) {
         return null;
      } else if (blockpos.getY() >= 0 && blockpos.getY() < 256) {
         return MathHelper.abs(playerPos.getZ() - blockpos.getZ()) > this.renderDistanceChunks * 16 ? null : this.viewFrustum.getRenderChunk(blockpos);
      } else {
         return null;
      }
   }

   private void fixTerrainFrustum(double x, double y, double z) {
   }

   public int renderBlockLayer(BlockRenderLayer p_215323_1_, ActiveRenderInfo p_215323_2_) {
      RenderHelper.disableStandardItemLighting();
      if (p_215323_1_ == BlockRenderLayer.TRANSLUCENT) {
         this.mc.getProfiler().startSection("translucent_sort");
         double d0 = p_215323_2_.getProjectedView().x - this.prevRenderSortX;
         double d1 = p_215323_2_.getProjectedView().y - this.prevRenderSortY;
         double d2 = p_215323_2_.getProjectedView().z - this.prevRenderSortZ;
         if (d0 * d0 + d1 * d1 + d2 * d2 > 1.0D) {
            this.prevRenderSortX = p_215323_2_.getProjectedView().x;
            this.prevRenderSortY = p_215323_2_.getProjectedView().y;
            this.prevRenderSortZ = p_215323_2_.getProjectedView().z;
            int k = 0;

            for(WorldRenderer.LocalRenderInformationContainer worldrenderer$localrenderinformationcontainer : this.renderInfos) {
               if (worldrenderer$localrenderinformationcontainer.renderChunk.compiledChunk.isLayerStarted(p_215323_1_) && k++ < 15) {
                  this.renderDispatcher.updateTransparencyLater(worldrenderer$localrenderinformationcontainer.renderChunk);
               }
            }
         }

         this.mc.getProfiler().endSection();
      }

      this.mc.getProfiler().startSection("filterempty");
      int l = 0;
      boolean flag = p_215323_1_ == BlockRenderLayer.TRANSLUCENT;
      int i1 = flag ? this.renderInfos.size() - 1 : 0;
      int i = flag ? -1 : this.renderInfos.size();
      int j1 = flag ? -1 : 1;

      for(int j = i1; j != i; j += j1) {
         ChunkRender chunkrender = (this.renderInfos.get(j)).renderChunk;
         if (!chunkrender.getCompiledChunk().isLayerEmpty(p_215323_1_)) {
            ++l;
            this.renderContainer.addRenderChunk(chunkrender, p_215323_1_);
         }
      }

      this.mc.getProfiler().endStartSection(() -> {
         return "render_" + p_215323_1_;
      });
      this.renderBlockLayer(p_215323_1_);
      this.mc.getProfiler().endSection();
      return l;
   }

   private void renderBlockLayer(BlockRenderLayer blockLayerIn) {
      this.mc.gameRenderer.enableLightmap();
      if (GLX.useVbo()) {
         GlStateManager.enableClientState(32884);
         GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
         GlStateManager.enableClientState(32888);
         GLX.glClientActiveTexture(GLX.GL_TEXTURE1);
         GlStateManager.enableClientState(32888);
         GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
         GlStateManager.enableClientState(32886);
      }

      this.renderContainer.renderChunkLayer(blockLayerIn);
      if (GLX.useVbo()) {
         for(VertexFormatElement vertexformatelement : DefaultVertexFormats.BLOCK.getElements()) {
            VertexFormatElement.Usage vertexformatelement$usage = vertexformatelement.getUsage();
            int i = vertexformatelement.getIndex();
            switch(vertexformatelement$usage) {
            case POSITION:
               GlStateManager.disableClientState(32884);
               break;
            case UV:
               GLX.glClientActiveTexture(GLX.GL_TEXTURE0 + i);
               GlStateManager.disableClientState(32888);
               GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
               break;
            case COLOR:
               GlStateManager.disableClientState(32886);
               GlStateManager.clearCurrentColor();
            }
         }
      }

      this.mc.gameRenderer.disableLightmap();
   }

   private void cleanupDamagedBlocks(Iterator<DestroyBlockProgress> iteratorIn) {
      while(iteratorIn.hasNext()) {
         DestroyBlockProgress destroyblockprogress = iteratorIn.next();
         int i = destroyblockprogress.getCreationCloudUpdateTick();
         if (this.ticks - i > 400) {
            iteratorIn.remove();
         }
      }

   }

   public void tick() {
      ++this.ticks;
      if (this.ticks % 20 == 0) {
         this.cleanupDamagedBlocks(this.damagedBlocks.values().iterator());
      }

   }

   private void renderSkyEnd() {
      GlStateManager.disableFog();
      GlStateManager.disableAlphaTest();
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      RenderHelper.disableStandardItemLighting();
      GlStateManager.depthMask(false);
      this.textureManager.bindTexture(END_SKY_TEXTURES);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();

      for(int i = 0; i < 6; ++i) {
         GlStateManager.pushMatrix();
         if (i == 1) {
            GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         }

         if (i == 2) {
            GlStateManager.rotatef(-90.0F, 1.0F, 0.0F, 0.0F);
         }

         if (i == 3) {
            GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
         }

         if (i == 4) {
            GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
         }

         if (i == 5) {
            GlStateManager.rotatef(-90.0F, 0.0F, 0.0F, 1.0F);
         }

         bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         bufferbuilder.pos(-100.0D, -100.0D, -100.0D).tex(0.0D, 0.0D).color(40, 40, 40, 255).endVertex();
         bufferbuilder.pos(-100.0D, -100.0D, 100.0D).tex(0.0D, 16.0D).color(40, 40, 40, 255).endVertex();
         bufferbuilder.pos(100.0D, -100.0D, 100.0D).tex(16.0D, 16.0D).color(40, 40, 40, 255).endVertex();
         bufferbuilder.pos(100.0D, -100.0D, -100.0D).tex(16.0D, 0.0D).color(40, 40, 40, 255).endVertex();
         tessellator.draw();
         GlStateManager.popMatrix();
      }

      GlStateManager.depthMask(true);
      GlStateManager.enableTexture();
      GlStateManager.disableBlend();
      GlStateManager.enableAlphaTest();
   }

   public void renderSky(float partialTicks) {
      net.minecraftforge.client.IRenderHandler renderer = this.world.getDimension().getSkyRenderer();
      if (renderer != null) {
         renderer.render(partialTicks, world, mc);
         return;
      }
      if (this.mc.world.dimension.getType() == DimensionType.field_223229_c_) {
         this.renderSkyEnd();
      } else if (this.mc.world.dimension.isSurfaceWorld()) {
         GlStateManager.disableTexture();
         Vec3d vec3d = this.world.func_217382_a(this.mc.gameRenderer.getActiveRenderInfo().func_216780_d(), partialTicks);
         float f = (float)vec3d.x;
         float f1 = (float)vec3d.y;
         float f2 = (float)vec3d.z;
         GlStateManager.color3f(f, f1, f2);
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder bufferbuilder = tessellator.getBuffer();
         GlStateManager.depthMask(false);
         GlStateManager.enableFog();
         GlStateManager.color3f(f, f1, f2);
         if (this.vboEnabled) {
            this.skyVBO.bindBuffer();
            GlStateManager.enableClientState(32884);
            GlStateManager.vertexPointer(3, 5126, 12, 0);
            this.skyVBO.drawArrays(7);
            VertexBuffer.unbindBuffer();
            GlStateManager.disableClientState(32884);
         } else {
            GlStateManager.callList(this.glSkyList);
         }

         GlStateManager.disableFog();
         GlStateManager.disableAlphaTest();
         GlStateManager.enableBlend();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         RenderHelper.disableStandardItemLighting();
         float[] afloat = this.world.dimension.calcSunriseSunsetColors(this.world.getCelestialAngle(partialTicks), partialTicks);
         if (afloat != null) {
            GlStateManager.disableTexture();
            GlStateManager.shadeModel(7425);
            GlStateManager.pushMatrix();
            GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(MathHelper.sin(this.world.getCelestialAngleRadians(partialTicks)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
            float f3 = afloat[0];
            float f4 = afloat[1];
            float f5 = afloat[2];
            bufferbuilder.begin(6, DefaultVertexFormats.POSITION_COLOR);
            bufferbuilder.pos(0.0D, 100.0D, 0.0D).color(f3, f4, f5, afloat[3]).endVertex();
            int i = 16;

            for(int j = 0; j <= 16; ++j) {
               float f6 = (float)j * ((float)Math.PI * 2F) / 16.0F;
               float f7 = MathHelper.sin(f6);
               float f8 = MathHelper.cos(f6);
               bufferbuilder.pos((double)(f7 * 120.0F), (double)(f8 * 120.0F), (double)(-f8 * 40.0F * afloat[3])).color(afloat[0], afloat[1], afloat[2], 0.0F).endVertex();
            }

            tessellator.draw();
            GlStateManager.popMatrix();
            GlStateManager.shadeModel(7424);
         }

         GlStateManager.enableTexture();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         GlStateManager.pushMatrix();
         float f11 = 1.0F - this.world.getRainStrength(partialTicks);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, f11);
         GlStateManager.rotatef(-90.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(this.world.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);
         float f12 = 30.0F;
         this.textureManager.bindTexture(SUN_TEXTURES);
         bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
         bufferbuilder.pos((double)(-f12), 100.0D, (double)(-f12)).tex(0.0D, 0.0D).endVertex();
         bufferbuilder.pos((double)f12, 100.0D, (double)(-f12)).tex(1.0D, 0.0D).endVertex();
         bufferbuilder.pos((double)f12, 100.0D, (double)f12).tex(1.0D, 1.0D).endVertex();
         bufferbuilder.pos((double)(-f12), 100.0D, (double)f12).tex(0.0D, 1.0D).endVertex();
         tessellator.draw();
         f12 = 20.0F;
         this.textureManager.bindTexture(MOON_PHASES_TEXTURES);
         int k = this.world.getMoonPhase();
         int l = k % 4;
         int i1 = k / 4 % 2;
         float f13 = (float)(l + 0) / 4.0F;
         float f14 = (float)(i1 + 0) / 2.0F;
         float f15 = (float)(l + 1) / 4.0F;
         float f9 = (float)(i1 + 1) / 2.0F;
         bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
         bufferbuilder.pos((double)(-f12), -100.0D, (double)f12).tex((double)f15, (double)f9).endVertex();
         bufferbuilder.pos((double)f12, -100.0D, (double)f12).tex((double)f13, (double)f9).endVertex();
         bufferbuilder.pos((double)f12, -100.0D, (double)(-f12)).tex((double)f13, (double)f14).endVertex();
         bufferbuilder.pos((double)(-f12), -100.0D, (double)(-f12)).tex((double)f15, (double)f14).endVertex();
         tessellator.draw();
         GlStateManager.disableTexture();
         float f10 = this.world.getStarBrightness(partialTicks) * f11;
         if (f10 > 0.0F) {
            GlStateManager.color4f(f10, f10, f10, f10);
            if (this.vboEnabled) {
               this.starVBO.bindBuffer();
               GlStateManager.enableClientState(32884);
               GlStateManager.vertexPointer(3, 5126, 12, 0);
               this.starVBO.drawArrays(7);
               VertexBuffer.unbindBuffer();
               GlStateManager.disableClientState(32884);
            } else {
               GlStateManager.callList(this.starGLCallList);
            }
         }

         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableBlend();
         GlStateManager.enableAlphaTest();
         GlStateManager.enableFog();
         GlStateManager.popMatrix();
         GlStateManager.disableTexture();
         GlStateManager.color3f(0.0F, 0.0F, 0.0F);
         double d0 = this.mc.player.getEyePosition(partialTicks).y - this.world.getHorizon();
         if (d0 < 0.0D) {
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0.0F, 12.0F, 0.0F);
            if (this.vboEnabled) {
               this.sky2VBO.bindBuffer();
               GlStateManager.enableClientState(32884);
               GlStateManager.vertexPointer(3, 5126, 12, 0);
               this.sky2VBO.drawArrays(7);
               VertexBuffer.unbindBuffer();
               GlStateManager.disableClientState(32884);
            } else {
               GlStateManager.callList(this.glSkyList2);
            }

            GlStateManager.popMatrix();
         }

         if (this.world.dimension.isSkyColored()) {
            GlStateManager.color3f(f * 0.2F + 0.04F, f1 * 0.2F + 0.04F, f2 * 0.6F + 0.1F);
         } else {
            GlStateManager.color3f(f, f1, f2);
         }

         GlStateManager.pushMatrix();
         GlStateManager.translatef(0.0F, -((float)(d0 - 16.0D)), 0.0F);
         GlStateManager.callList(this.glSkyList2);
         GlStateManager.popMatrix();
         GlStateManager.enableTexture();
         GlStateManager.depthMask(true);
      }
   }

   public void renderClouds(float partialTicks, double viewEntityX, double viewEntityY, double viewEntityZ) {
//      if (net.minecraftforge.client.CloudRenderer.renderClouds(this.ticks, partialTicks, this.world, mc)) return;
      if (this.mc.world.dimension.isSurfaceWorld()) {
         float f = 12.0F;
         float f1 = 4.0F;
         double d0 = 2.0E-4D;
         double d1 = (double)(((float)this.ticks + partialTicks) * 0.03F);
         double d2 = (viewEntityX + d1) / 12.0D;
         double d3 = (double)(this.world.dimension.getCloudHeight() - (float)viewEntityY + 0.33F);
         double d4 = viewEntityZ / 12.0D + (double)0.33F;
         d2 = d2 - (double)(MathHelper.floor(d2 / 2048.0D) * 2048);
         d4 = d4 - (double)(MathHelper.floor(d4 / 2048.0D) * 2048);
         float f2 = (float)(d2 - (double)MathHelper.floor(d2));
         float f3 = (float)(d3 / 4.0D - (double)MathHelper.floor(d3 / 4.0D)) * 4.0F;
         float f4 = (float)(d4 - (double)MathHelper.floor(d4));
         Vec3d vec3d = this.world.getCloudColour(partialTicks);
         int i = (int)Math.floor(d2);
         int j = (int)Math.floor(d3 / 4.0D);
         int k = (int)Math.floor(d4);
         if (i != this.cloudsCheckX || j != this.cloudsCheckY || k != this.cloudsCheckZ || this.mc.gameSettings.getCloudOption() != this.cloudOption || this.cloudsCheckColor.squareDistanceTo(vec3d) > 2.0E-4D) {
            this.cloudsCheckX = i;
            this.cloudsCheckY = j;
            this.cloudsCheckZ = k;
            this.cloudsCheckColor = vec3d;
            this.cloudOption = this.mc.gameSettings.getCloudOption();
            this.cloudsNeedUpdate = true;
         }

         if (this.cloudsNeedUpdate) {
            this.cloudsNeedUpdate = false;
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            if (this.cloudsVBO != null) {
               this.cloudsVBO.deleteGlBuffers();
            }

            if (this.glCloudsList >= 0) {
               GLAllocation.deleteDisplayLists(this.glCloudsList);
               this.glCloudsList = -1;
            }

            if (this.vboEnabled) {
               this.cloudsVBO = new VertexBuffer(DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
               this.drawClouds(bufferbuilder, d2, d3, d4, vec3d);
               bufferbuilder.finishDrawing();
               bufferbuilder.reset();
               this.cloudsVBO.bufferData(bufferbuilder.getByteBuffer());
            } else {
               this.glCloudsList = GLAllocation.generateDisplayLists(1);
               GlStateManager.newList(this.glCloudsList, 4864);
               this.drawClouds(bufferbuilder, d2, d3, d4, vec3d);
               tessellator.draw();
               GlStateManager.endList();
            }
         }

         GlStateManager.disableCull();
         this.textureManager.bindTexture(CLOUDS_TEXTURES);
         GlStateManager.enableBlend();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         GlStateManager.pushMatrix();
         GlStateManager.scalef(12.0F, 1.0F, 12.0F);
         GlStateManager.translatef(-f2, f3, -f4);
         if (this.vboEnabled && this.cloudsVBO != null) {
            this.cloudsVBO.bindBuffer();
            GlStateManager.enableClientState(32884);
            GlStateManager.enableClientState(32888);
            GLX.glClientActiveTexture(GLX.GL_TEXTURE0);
            GlStateManager.enableClientState(32886);
            GlStateManager.enableClientState(32885);
            GlStateManager.vertexPointer(3, 5126, 28, 0);
            GlStateManager.texCoordPointer(2, 5126, 28, 12);
            GlStateManager.colorPointer(4, 5121, 28, 20);
            GlStateManager.normalPointer(5120, 28, 24);
            int i1 = this.cloudOption == CloudOption.FANCY ? 0 : 1;

            for(int k1 = i1; k1 < 2; ++k1) {
               if (k1 == 0) {
                  GlStateManager.colorMask(false, false, false, false);
               } else {
                  GlStateManager.colorMask(true, true, true, true);
               }

               this.cloudsVBO.drawArrays(7);
            }

            VertexBuffer.unbindBuffer();
            GlStateManager.disableClientState(32884);
            GlStateManager.disableClientState(32888);
            GlStateManager.disableClientState(32886);
            GlStateManager.disableClientState(32885);
         } else if (this.glCloudsList >= 0) {
            int l = this.cloudOption == CloudOption.FANCY ? 0 : 1;

            for(int j1 = l; j1 < 2; ++j1) {
               if (j1 == 0) {
                  GlStateManager.colorMask(false, false, false, false);
               } else {
                  GlStateManager.colorMask(true, true, true, true);
               }

               GlStateManager.callList(this.glCloudsList);
            }
         }

         GlStateManager.popMatrix();
         GlStateManager.clearCurrentColor();
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableBlend();
         GlStateManager.enableCull();
      }
   }

   private void drawClouds(BufferBuilder bufferIn, double cloudsX, double cloudsY, double cloudsZ, Vec3d cloudsColor) {
      float f = 4.0F;
      float f1 = 0.00390625F;
      int i = 8;
      int j = 4;
      float f2 = 9.765625E-4F;
      float f3 = (float)MathHelper.floor(cloudsX) * 0.00390625F;
      float f4 = (float)MathHelper.floor(cloudsZ) * 0.00390625F;
      float f5 = (float)cloudsColor.x;
      float f6 = (float)cloudsColor.y;
      float f7 = (float)cloudsColor.z;
      float f8 = f5 * 0.9F;
      float f9 = f6 * 0.9F;
      float f10 = f7 * 0.9F;
      float f11 = f5 * 0.7F;
      float f12 = f6 * 0.7F;
      float f13 = f7 * 0.7F;
      float f14 = f5 * 0.8F;
      float f15 = f6 * 0.8F;
      float f16 = f7 * 0.8F;
      bufferIn.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
      float f17 = (float)Math.floor(cloudsY / 4.0D) * 4.0F;
      if (this.cloudOption == CloudOption.FANCY) {
         for(int k = -3; k <= 4; ++k) {
            for(int l = -3; l <= 4; ++l) {
               float f18 = (float)(k * 8);
               float f19 = (float)(l * 8);
               if (f17 > -5.0F) {
                  bufferIn.pos((double)(f18 + 0.0F), (double)(f17 + 0.0F), (double)(f19 + 8.0F)).tex((double)((f18 + 0.0F) * 0.00390625F + f3), (double)((f19 + 8.0F) * 0.00390625F + f4)).color(f11, f12, f13, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                  bufferIn.pos((double)(f18 + 8.0F), (double)(f17 + 0.0F), (double)(f19 + 8.0F)).tex((double)((f18 + 8.0F) * 0.00390625F + f3), (double)((f19 + 8.0F) * 0.00390625F + f4)).color(f11, f12, f13, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                  bufferIn.pos((double)(f18 + 8.0F), (double)(f17 + 0.0F), (double)(f19 + 0.0F)).tex((double)((f18 + 8.0F) * 0.00390625F + f3), (double)((f19 + 0.0F) * 0.00390625F + f4)).color(f11, f12, f13, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                  bufferIn.pos((double)(f18 + 0.0F), (double)(f17 + 0.0F), (double)(f19 + 0.0F)).tex((double)((f18 + 0.0F) * 0.00390625F + f3), (double)((f19 + 0.0F) * 0.00390625F + f4)).color(f11, f12, f13, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
               }

               if (f17 <= 5.0F) {
                  bufferIn.pos((double)(f18 + 0.0F), (double)(f17 + 4.0F - 9.765625E-4F), (double)(f19 + 8.0F)).tex((double)((f18 + 0.0F) * 0.00390625F + f3), (double)((f19 + 8.0F) * 0.00390625F + f4)).color(f5, f6, f7, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                  bufferIn.pos((double)(f18 + 8.0F), (double)(f17 + 4.0F - 9.765625E-4F), (double)(f19 + 8.0F)).tex((double)((f18 + 8.0F) * 0.00390625F + f3), (double)((f19 + 8.0F) * 0.00390625F + f4)).color(f5, f6, f7, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                  bufferIn.pos((double)(f18 + 8.0F), (double)(f17 + 4.0F - 9.765625E-4F), (double)(f19 + 0.0F)).tex((double)((f18 + 8.0F) * 0.00390625F + f3), (double)((f19 + 0.0F) * 0.00390625F + f4)).color(f5, f6, f7, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                  bufferIn.pos((double)(f18 + 0.0F), (double)(f17 + 4.0F - 9.765625E-4F), (double)(f19 + 0.0F)).tex((double)((f18 + 0.0F) * 0.00390625F + f3), (double)((f19 + 0.0F) * 0.00390625F + f4)).color(f5, f6, f7, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
               }

               if (k > -1) {
                  for(int i1 = 0; i1 < 8; ++i1) {
                     bufferIn.pos((double)(f18 + (float)i1 + 0.0F), (double)(f17 + 0.0F), (double)(f19 + 8.0F)).tex((double)((f18 + (float)i1 + 0.5F) * 0.00390625F + f3), (double)((f19 + 8.0F) * 0.00390625F + f4)).color(f8, f9, f10, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                     bufferIn.pos((double)(f18 + (float)i1 + 0.0F), (double)(f17 + 4.0F), (double)(f19 + 8.0F)).tex((double)((f18 + (float)i1 + 0.5F) * 0.00390625F + f3), (double)((f19 + 8.0F) * 0.00390625F + f4)).color(f8, f9, f10, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                     bufferIn.pos((double)(f18 + (float)i1 + 0.0F), (double)(f17 + 4.0F), (double)(f19 + 0.0F)).tex((double)((f18 + (float)i1 + 0.5F) * 0.00390625F + f3), (double)((f19 + 0.0F) * 0.00390625F + f4)).color(f8, f9, f10, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                     bufferIn.pos((double)(f18 + (float)i1 + 0.0F), (double)(f17 + 0.0F), (double)(f19 + 0.0F)).tex((double)((f18 + (float)i1 + 0.5F) * 0.00390625F + f3), (double)((f19 + 0.0F) * 0.00390625F + f4)).color(f8, f9, f10, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                  }
               }

               if (k <= 1) {
                  for(int j2 = 0; j2 < 8; ++j2) {
                     bufferIn.pos((double)(f18 + (float)j2 + 1.0F - 9.765625E-4F), (double)(f17 + 0.0F), (double)(f19 + 8.0F)).tex((double)((f18 + (float)j2 + 0.5F) * 0.00390625F + f3), (double)((f19 + 8.0F) * 0.00390625F + f4)).color(f8, f9, f10, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                     bufferIn.pos((double)(f18 + (float)j2 + 1.0F - 9.765625E-4F), (double)(f17 + 4.0F), (double)(f19 + 8.0F)).tex((double)((f18 + (float)j2 + 0.5F) * 0.00390625F + f3), (double)((f19 + 8.0F) * 0.00390625F + f4)).color(f8, f9, f10, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                     bufferIn.pos((double)(f18 + (float)j2 + 1.0F - 9.765625E-4F), (double)(f17 + 4.0F), (double)(f19 + 0.0F)).tex((double)((f18 + (float)j2 + 0.5F) * 0.00390625F + f3), (double)((f19 + 0.0F) * 0.00390625F + f4)).color(f8, f9, f10, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                     bufferIn.pos((double)(f18 + (float)j2 + 1.0F - 9.765625E-4F), (double)(f17 + 0.0F), (double)(f19 + 0.0F)).tex((double)((f18 + (float)j2 + 0.5F) * 0.00390625F + f3), (double)((f19 + 0.0F) * 0.00390625F + f4)).color(f8, f9, f10, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                  }
               }

               if (l > -1) {
                  for(int k2 = 0; k2 < 8; ++k2) {
                     bufferIn.pos((double)(f18 + 0.0F), (double)(f17 + 4.0F), (double)(f19 + (float)k2 + 0.0F)).tex((double)((f18 + 0.0F) * 0.00390625F + f3), (double)((f19 + (float)k2 + 0.5F) * 0.00390625F + f4)).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                     bufferIn.pos((double)(f18 + 8.0F), (double)(f17 + 4.0F), (double)(f19 + (float)k2 + 0.0F)).tex((double)((f18 + 8.0F) * 0.00390625F + f3), (double)((f19 + (float)k2 + 0.5F) * 0.00390625F + f4)).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                     bufferIn.pos((double)(f18 + 8.0F), (double)(f17 + 0.0F), (double)(f19 + (float)k2 + 0.0F)).tex((double)((f18 + 8.0F) * 0.00390625F + f3), (double)((f19 + (float)k2 + 0.5F) * 0.00390625F + f4)).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                     bufferIn.pos((double)(f18 + 0.0F), (double)(f17 + 0.0F), (double)(f19 + (float)k2 + 0.0F)).tex((double)((f18 + 0.0F) * 0.00390625F + f3), (double)((f19 + (float)k2 + 0.5F) * 0.00390625F + f4)).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                  }
               }

               if (l <= 1) {
                  for(int l2 = 0; l2 < 8; ++l2) {
                     bufferIn.pos((double)(f18 + 0.0F), (double)(f17 + 4.0F), (double)(f19 + (float)l2 + 1.0F - 9.765625E-4F)).tex((double)((f18 + 0.0F) * 0.00390625F + f3), (double)((f19 + (float)l2 + 0.5F) * 0.00390625F + f4)).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                     bufferIn.pos((double)(f18 + 8.0F), (double)(f17 + 4.0F), (double)(f19 + (float)l2 + 1.0F - 9.765625E-4F)).tex((double)((f18 + 8.0F) * 0.00390625F + f3), (double)((f19 + (float)l2 + 0.5F) * 0.00390625F + f4)).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                     bufferIn.pos((double)(f18 + 8.0F), (double)(f17 + 0.0F), (double)(f19 + (float)l2 + 1.0F - 9.765625E-4F)).tex((double)((f18 + 8.0F) * 0.00390625F + f3), (double)((f19 + (float)l2 + 0.5F) * 0.00390625F + f4)).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                     bufferIn.pos((double)(f18 + 0.0F), (double)(f17 + 0.0F), (double)(f19 + (float)l2 + 1.0F - 9.765625E-4F)).tex((double)((f18 + 0.0F) * 0.00390625F + f3), (double)((f19 + (float)l2 + 0.5F) * 0.00390625F + f4)).color(f14, f15, f16, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                  }
               }
            }
         }
      } else {
         int j1 = 1;
         int k1 = 32;

         for(int l1 = -32; l1 < 32; l1 += 32) {
            for(int i2 = -32; i2 < 32; i2 += 32) {
               bufferIn.pos((double)(l1 + 0), (double)f17, (double)(i2 + 32)).tex((double)((float)(l1 + 0) * 0.00390625F + f3), (double)((float)(i2 + 32) * 0.00390625F + f4)).color(f5, f6, f7, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
               bufferIn.pos((double)(l1 + 32), (double)f17, (double)(i2 + 32)).tex((double)((float)(l1 + 32) * 0.00390625F + f3), (double)((float)(i2 + 32) * 0.00390625F + f4)).color(f5, f6, f7, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
               bufferIn.pos((double)(l1 + 32), (double)f17, (double)(i2 + 0)).tex((double)((float)(l1 + 32) * 0.00390625F + f3), (double)((float)(i2 + 0) * 0.00390625F + f4)).color(f5, f6, f7, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
               bufferIn.pos((double)(l1 + 0), (double)f17, (double)(i2 + 0)).tex((double)((float)(l1 + 0) * 0.00390625F + f3), (double)((float)(i2 + 0) * 0.00390625F + f4)).color(f5, f6, f7, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
            }
         }
      }

   }

   public void updateChunks(long finishTimeNano) {
      this.displayListEntitiesDirty |= this.renderDispatcher.runChunkUploads(finishTimeNano);
      if (!this.chunksToUpdate.isEmpty()) {
         Iterator<ChunkRender> iterator = this.chunksToUpdate.iterator();

         while(iterator.hasNext()) {
            ChunkRender chunkrender = iterator.next();
            boolean flag;
            if (chunkrender.needsImmediateUpdate()) {
               flag = this.renderDispatcher.updateChunkNow(chunkrender);
            } else {
               flag = this.renderDispatcher.updateChunkLater(chunkrender);
            }

            if (!flag) {
               break;
            }

            chunkrender.clearNeedsUpdate();
            iterator.remove();
            long i = finishTimeNano - Util.nanoTime();
            if (i < 0L) {
               break;
            }
         }
      }

   }

   public void func_215322_a(ActiveRenderInfo p_215322_1_, float p_215322_2_) {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      WorldBorder worldborder = this.world.getWorldBorder();
      double d0 = (double)(this.mc.gameSettings.renderDistanceChunks * 16);
      if (!(p_215322_1_.getProjectedView().x < worldborder.maxX() - d0) || !(p_215322_1_.getProjectedView().x > worldborder.minX() + d0) || !(p_215322_1_.getProjectedView().z < worldborder.maxZ() - d0) || !(p_215322_1_.getProjectedView().z > worldborder.minZ() + d0)) {
         double d1 = 1.0D - worldborder.getClosestDistance(p_215322_1_.getProjectedView().x, p_215322_1_.getProjectedView().z) / d0;
         d1 = Math.pow(d1, 4.0D);
         double d2 = p_215322_1_.getProjectedView().x;
         double d3 = p_215322_1_.getProjectedView().y;
         double d4 = p_215322_1_.getProjectedView().z;
         GlStateManager.enableBlend();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         this.textureManager.bindTexture(FORCEFIELD_TEXTURES);
         GlStateManager.depthMask(false);
         GlStateManager.pushMatrix();
         int i = worldborder.getStatus().getColor();
         float f = (float)(i >> 16 & 255) / 255.0F;
         float f1 = (float)(i >> 8 & 255) / 255.0F;
         float f2 = (float)(i & 255) / 255.0F;
         GlStateManager.color4f(f, f1, f2, (float)d1);
         GlStateManager.polygonOffset(-3.0F, -3.0F);
         GlStateManager.enablePolygonOffset();
         GlStateManager.alphaFunc(516, 0.1F);
         GlStateManager.enableAlphaTest();
         GlStateManager.disableCull();
         float f3 = (float)(Util.milliTime() % 3000L) / 3000.0F;
         float f4 = 0.0F;
         float f5 = 0.0F;
         float f6 = 128.0F;
         bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
         bufferbuilder.setTranslation(-d2, -d3, -d4);
         double d5 = Math.max((double)MathHelper.floor(d4 - d0), worldborder.minZ());
         double d6 = Math.min((double)MathHelper.ceil(d4 + d0), worldborder.maxZ());
         if (d2 > worldborder.maxX() - d0) {
            float f7 = 0.0F;

            for(double d7 = d5; d7 < d6; f7 += 0.5F) {
               double d8 = Math.min(1.0D, d6 - d7);
               float f8 = (float)d8 * 0.5F;
               bufferbuilder.pos(worldborder.maxX(), 256.0D, d7).tex((double)(f3 + f7), (double)(f3 + 0.0F)).endVertex();
               bufferbuilder.pos(worldborder.maxX(), 256.0D, d7 + d8).tex((double)(f3 + f8 + f7), (double)(f3 + 0.0F)).endVertex();
               bufferbuilder.pos(worldborder.maxX(), 0.0D, d7 + d8).tex((double)(f3 + f8 + f7), (double)(f3 + 128.0F)).endVertex();
               bufferbuilder.pos(worldborder.maxX(), 0.0D, d7).tex((double)(f3 + f7), (double)(f3 + 128.0F)).endVertex();
               ++d7;
            }
         }

         if (d2 < worldborder.minX() + d0) {
            float f9 = 0.0F;

            for(double d9 = d5; d9 < d6; f9 += 0.5F) {
               double d12 = Math.min(1.0D, d6 - d9);
               float f12 = (float)d12 * 0.5F;
               bufferbuilder.pos(worldborder.minX(), 256.0D, d9).tex((double)(f3 + f9), (double)(f3 + 0.0F)).endVertex();
               bufferbuilder.pos(worldborder.minX(), 256.0D, d9 + d12).tex((double)(f3 + f12 + f9), (double)(f3 + 0.0F)).endVertex();
               bufferbuilder.pos(worldborder.minX(), 0.0D, d9 + d12).tex((double)(f3 + f12 + f9), (double)(f3 + 128.0F)).endVertex();
               bufferbuilder.pos(worldborder.minX(), 0.0D, d9).tex((double)(f3 + f9), (double)(f3 + 128.0F)).endVertex();
               ++d9;
            }
         }

         d5 = Math.max((double)MathHelper.floor(d2 - d0), worldborder.minX());
         d6 = Math.min((double)MathHelper.ceil(d2 + d0), worldborder.maxX());
         if (d4 > worldborder.maxZ() - d0) {
            float f10 = 0.0F;

            for(double d10 = d5; d10 < d6; f10 += 0.5F) {
               double d13 = Math.min(1.0D, d6 - d10);
               float f13 = (float)d13 * 0.5F;
               bufferbuilder.pos(d10, 256.0D, worldborder.maxZ()).tex((double)(f3 + f10), (double)(f3 + 0.0F)).endVertex();
               bufferbuilder.pos(d10 + d13, 256.0D, worldborder.maxZ()).tex((double)(f3 + f13 + f10), (double)(f3 + 0.0F)).endVertex();
               bufferbuilder.pos(d10 + d13, 0.0D, worldborder.maxZ()).tex((double)(f3 + f13 + f10), (double)(f3 + 128.0F)).endVertex();
               bufferbuilder.pos(d10, 0.0D, worldborder.maxZ()).tex((double)(f3 + f10), (double)(f3 + 128.0F)).endVertex();
               ++d10;
            }
         }

         if (d4 < worldborder.minZ() + d0) {
            float f11 = 0.0F;

            for(double d11 = d5; d11 < d6; f11 += 0.5F) {
               double d14 = Math.min(1.0D, d6 - d11);
               float f14 = (float)d14 * 0.5F;
               bufferbuilder.pos(d11, 256.0D, worldborder.minZ()).tex((double)(f3 + f11), (double)(f3 + 0.0F)).endVertex();
               bufferbuilder.pos(d11 + d14, 256.0D, worldborder.minZ()).tex((double)(f3 + f14 + f11), (double)(f3 + 0.0F)).endVertex();
               bufferbuilder.pos(d11 + d14, 0.0D, worldborder.minZ()).tex((double)(f3 + f14 + f11), (double)(f3 + 128.0F)).endVertex();
               bufferbuilder.pos(d11, 0.0D, worldborder.minZ()).tex((double)(f3 + f11), (double)(f3 + 128.0F)).endVertex();
               ++d11;
            }
         }

         tessellator.draw();
         bufferbuilder.setTranslation(0.0D, 0.0D, 0.0D);
         GlStateManager.enableCull();
         GlStateManager.disableAlphaTest();
         GlStateManager.polygonOffset(0.0F, 0.0F);
         GlStateManager.disablePolygonOffset();
         GlStateManager.enableAlphaTest();
         GlStateManager.disableBlend();
         GlStateManager.popMatrix();
         GlStateManager.depthMask(true);
      }
   }

   private void preRenderDamagedBlocks() {
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.enableBlend();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.5F);
      GlStateManager.polygonOffset(-1.0F, -10.0F);
      GlStateManager.enablePolygonOffset();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.enableAlphaTest();
      GlStateManager.pushMatrix();
   }

   private void postRenderDamagedBlocks() {
      GlStateManager.disableAlphaTest();
      GlStateManager.polygonOffset(0.0F, 0.0F);
      GlStateManager.disablePolygonOffset();
      GlStateManager.enableAlphaTest();
      GlStateManager.depthMask(true);
      GlStateManager.popMatrix();
   }

   public void func_215318_a(Tessellator p_215318_1_, BufferBuilder p_215318_2_, ActiveRenderInfo p_215318_3_) {
      double d0 = p_215318_3_.getProjectedView().x;
      double d1 = p_215318_3_.getProjectedView().y;
      double d2 = p_215318_3_.getProjectedView().z;
      if (!this.damagedBlocks.isEmpty()) {
         this.textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
         this.preRenderDamagedBlocks();
         p_215318_2_.begin(7, DefaultVertexFormats.BLOCK);
         p_215318_2_.setTranslation(-d0, -d1, -d2);
         p_215318_2_.noColor();
         Iterator<DestroyBlockProgress> iterator = this.damagedBlocks.values().iterator();

         while(iterator.hasNext()) {
            DestroyBlockProgress destroyblockprogress = iterator.next();
            BlockPos blockpos = destroyblockprogress.getPosition();
            Block block = this.world.getBlockState(blockpos).getBlock();
            TileEntity te = this.world.getTileEntity(blockpos);
            boolean hasBreak = block instanceof ChestBlock || block instanceof EnderChestBlock || block instanceof AbstractSignBlock || block instanceof AbstractSkullBlock;
            if (!hasBreak) hasBreak = te != null && te.canRenderBreaking();

            if (!hasBreak) {
               double d3 = (double)blockpos.getX() - d0;
               double d4 = (double)blockpos.getY() - d1;
               double d5 = (double)blockpos.getZ() - d2;
               if (d3 * d3 + d4 * d4 + d5 * d5 > 1024.0D) {
                  iterator.remove();
               } else {
                  BlockState blockstate = this.world.getBlockState(blockpos);
                  if (!blockstate.isAir(this.world, blockpos)) {
                     int i = destroyblockprogress.getPartialBlockDamage();
                     TextureAtlasSprite textureatlassprite = this.destroyBlockIcons[i];
                     BlockRendererDispatcher blockrendererdispatcher = this.mc.getBlockRendererDispatcher();
                     blockrendererdispatcher.renderBlockDamage(blockstate, blockpos, textureatlassprite, this.world);
                  }
               }
            }
         }

         p_215318_1_.draw();
         p_215318_2_.setTranslation(0.0D, 0.0D, 0.0D);
         this.postRenderDamagedBlocks();
      }

   }

   public void drawSelectionBox(ActiveRenderInfo p_215325_1_, RayTraceResult p_215325_2_, int p_215325_3_) {
      if (p_215325_3_ == 0 && p_215325_2_.getType() == RayTraceResult.Type.BLOCK) {
         BlockPos blockpos = ((BlockRayTraceResult)p_215325_2_).getPos();
         BlockState blockstate = this.world.getBlockState(blockpos);
         if (!blockstate.isAir(this.world, blockpos) && this.world.getWorldBorder().contains(blockpos)) {
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.lineWidth(Math.max(2.5F, (float)this.mc.mainWindow.getFramebufferWidth() / 1920.0F * 2.5F));
            GlStateManager.disableTexture();
            GlStateManager.depthMask(false);
            GlStateManager.matrixMode(5889);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(1.0F, 1.0F, 0.999F);
            double d0 = p_215325_1_.getProjectedView().x;
            double d1 = p_215325_1_.getProjectedView().y;
            double d2 = p_215325_1_.getProjectedView().z;
            drawShape(blockstate.getShape(this.world, blockpos, ISelectionContext.forEntity(p_215325_1_.func_216773_g())), (double)blockpos.getX() - d0, (double)blockpos.getY() - d1, (double)blockpos.getZ() - d2, 0.0F, 0.0F, 0.0F, 0.4F);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture();
            GlStateManager.disableBlend();
         }
      }

   }

   public static void drawVoxelShapeParts(VoxelShape voxelShapeIn, double xIn, double yIn, double zIn, float red, float green, float blue, float alpha) {
      List<AxisAlignedBB> list = voxelShapeIn.toBoundingBoxList();
      int i = MathHelper.ceil((double)list.size() / 3.0D);

      for(int j = 0; j < list.size(); ++j) {
         AxisAlignedBB axisalignedbb = list.get(j);
         float f = ((float)j % (float)i + 1.0F) / (float)i;
         float f1 = (float)(j / i);
         float f2 = f * (float)(f1 == 0.0F ? 1 : 0);
         float f3 = f * (float)(f1 == 1.0F ? 1 : 0);
         float f4 = f * (float)(f1 == 2.0F ? 1 : 0);
         drawShape(VoxelShapes.create(axisalignedbb.offset(0.0D, 0.0D, 0.0D)), xIn, yIn, zIn, f2, f3, f4, 1.0F);
      }

   }

   public static void drawShape(VoxelShape voxelShapeIn, double xIn, double yIn, double zIn, float red, float green, float blue, float alpha) {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR);
      voxelShapeIn.forEachEdge((p_195468_11_, p_195468_13_, p_195468_15_, p_195468_17_, p_195468_19_, p_195468_21_) -> {
         bufferbuilder.pos(p_195468_11_ + xIn, p_195468_13_ + yIn, p_195468_15_ + zIn).color(red, green, blue, alpha).endVertex();
         bufferbuilder.pos(p_195468_17_ + xIn, p_195468_19_ + yIn, p_195468_21_ + zIn).color(red, green, blue, alpha).endVertex();
      });
      tessellator.draw();
   }

   public static void drawSelectionBoundingBox(AxisAlignedBB box, float red, float green, float blue, float alpha) {
      drawBoundingBox(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, red, green, blue, alpha);
   }

   public static void drawBoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha) {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
      drawBoundingBox(bufferbuilder, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);
      tessellator.draw();
   }

   public static void drawBoundingBox(BufferBuilder buffer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha) {
      buffer.pos(minX, minY, minZ).color(red, green, blue, 0.0F).endVertex();
      buffer.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
      buffer.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
      buffer.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
      buffer.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
      buffer.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
      buffer.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
      buffer.pos(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
      buffer.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
      buffer.pos(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
      buffer.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
      buffer.pos(minX, maxY, maxZ).color(red, green, blue, 0.0F).endVertex();
      buffer.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
      buffer.pos(maxX, maxY, maxZ).color(red, green, blue, 0.0F).endVertex();
      buffer.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
      buffer.pos(maxX, maxY, minZ).color(red, green, blue, 0.0F).endVertex();
      buffer.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
      buffer.pos(maxX, minY, minZ).color(red, green, blue, 0.0F).endVertex();
   }

   public static void addChainedFilledBoxVertices(BufferBuilder builder, double x1, double y1, double z1, double x2, double y2, double z2, float red, float green, float blue, float alpha) {
      builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
      builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
      builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
      builder.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
      builder.pos(x1, y2, z1).color(red, green, blue, alpha).endVertex();
      builder.pos(x1, y2, z2).color(red, green, blue, alpha).endVertex();
      builder.pos(x1, y2, z2).color(red, green, blue, alpha).endVertex();
      builder.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
      builder.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
      builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
      builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
      builder.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
      builder.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
      builder.pos(x2, y2, z1).color(red, green, blue, alpha).endVertex();
      builder.pos(x2, y2, z1).color(red, green, blue, alpha).endVertex();
      builder.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
      builder.pos(x1, y2, z1).color(red, green, blue, alpha).endVertex();
      builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
      builder.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
      builder.pos(x2, y1, z1).color(red, green, blue, alpha).endVertex();
      builder.pos(x1, y1, z2).color(red, green, blue, alpha).endVertex();
      builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
      builder.pos(x2, y1, z2).color(red, green, blue, alpha).endVertex();
      builder.pos(x1, y2, z1).color(red, green, blue, alpha).endVertex();
      builder.pos(x1, y2, z1).color(red, green, blue, alpha).endVertex();
      builder.pos(x1, y2, z2).color(red, green, blue, alpha).endVertex();
      builder.pos(x2, y2, z1).color(red, green, blue, alpha).endVertex();
      builder.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
      builder.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
      builder.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
   }

   public void notifyBlockUpdate(IBlockReader worldIn, BlockPos pos, BlockState oldState, BlockState newState, int flags) {
      this.func_215324_a(pos, (flags & 8) != 0);
   }

   private void func_215324_a(BlockPos p_215324_1_, boolean p_215324_2_) {
      for(int i = p_215324_1_.getZ() - 1; i <= p_215324_1_.getZ() + 1; ++i) {
         for(int j = p_215324_1_.getX() - 1; j <= p_215324_1_.getX() + 1; ++j) {
            for(int k = p_215324_1_.getY() - 1; k <= p_215324_1_.getY() + 1; ++k) {
               this.markForRerender(j >> 4, k >> 4, i >> 4, p_215324_2_);
            }
         }
      }

   }

   /**
    * On the client, re-renders all blocks in this range, inclusive. On the server, does nothing.
    */
   public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
      for(int i = z1 - 1; i <= z2 + 1; ++i) {
         for(int j = x1 - 1; j <= x2 + 1; ++j) {
            for(int k = y1 - 1; k <= y2 + 1; ++k) {
               this.markForRerender(j >> 4, k >> 4, i >> 4);
            }
         }
      }

   }

   public void markSurroundingsForRerender(int sectionX, int sectionY, int sectionZ) {
      for(int i = sectionZ - 1; i <= sectionZ + 1; ++i) {
         for(int j = sectionX - 1; j <= sectionX + 1; ++j) {
            for(int k = sectionY - 1; k <= sectionY + 1; ++k) {
               this.markForRerender(j, k, i);
            }
         }
      }

   }

   public void markForRerender(int sectionX, int sectionY, int sectionZ) {
      this.markForRerender(sectionX, sectionY, sectionZ, false);
   }

   private void markForRerender(int sectionX, int sectionY, int sectionZ, boolean rerenderOnMainThread) {
      this.viewFrustum.markForRerender(sectionX, sectionY, sectionZ, rerenderOnMainThread);
   }

   public void playRecord(@Nullable SoundEvent soundIn, BlockPos pos) {
      ISound isound = this.mapSoundPositions.get(pos);
      if (isound != null) {
         this.mc.getSoundHandler().stop(isound);
         this.mapSoundPositions.remove(pos);
      }

      if (soundIn != null) {
         MusicDiscItem musicdiscitem = MusicDiscItem.getBySound(soundIn);
         if (musicdiscitem != null) {
            this.mc.ingameGUI.setRecordPlayingMessage(musicdiscitem.getRecordDescription().getFormattedText());
         }

         ISound simplesound = SimpleSound.record(soundIn, (float)pos.getX(), (float)pos.getY(), (float)pos.getZ());
         this.mapSoundPositions.put(pos, simplesound);
         this.mc.getSoundHandler().play(simplesound);
      }

      this.setPartying(this.world, pos, soundIn != null);
   }

   /**
    * Called when a record starts or stops playing. Used to make parrots start or stop partying.
    */
   private void setPartying(World worldIn, BlockPos pos, boolean isPartying) {
      for(LivingEntity livingentity : worldIn.getEntitiesWithinAABB(LivingEntity.class, (new AxisAlignedBB(pos)).grow(3.0D))) {
         livingentity.setPartying(pos, isPartying);
      }

   }

   public void addParticle(IParticleData particleData, boolean alwaysRender, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
      this.addParticle(particleData, alwaysRender, false, x, y, z, xSpeed, ySpeed, zSpeed);
   }

   public void addParticle(IParticleData particleData, boolean ignoreRange, boolean minimizeLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
      try {
         this.addParticleUnchecked(particleData, ignoreRange, minimizeLevel, x, y, z, xSpeed, ySpeed, zSpeed);
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception while adding particle");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Particle being added");
         crashreportcategory.addDetail("ID", Registry.PARTICLE_TYPE.getKey(particleData.getType()));
         crashreportcategory.addDetail("Parameters", particleData.getParameters());
         crashreportcategory.addDetail("Position", () -> {
            return CrashReportCategory.getCoordinateInfo(x, y, z);
         });
         throw new ReportedException(crashreport);
      }
   }

   private <T extends IParticleData> void addParticleUnchecked(T particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
      this.addParticle(particleData, particleData.getType().getAlwaysShow(), x, y, z, xSpeed, ySpeed, zSpeed);
   }

   @Nullable
   private Particle addParticleUnchecked(IParticleData particleData, boolean alwaysRender, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
      return this.addParticleUnchecked(particleData, alwaysRender, false, x, y, z, xSpeed, ySpeed, zSpeed);
   }

   @Nullable
   private Particle addParticleUnchecked(IParticleData particleData, boolean alwaysRender, boolean minimizeLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
      ActiveRenderInfo activerenderinfo = this.mc.gameRenderer.getActiveRenderInfo();
      if (this.mc != null && activerenderinfo.func_216786_h() && this.mc.particles != null) {
         ParticleStatus particlestatus = this.func_215327_a(minimizeLevel);
         if (alwaysRender) {
            return this.mc.particles.addParticle(particleData, x, y, z, xSpeed, ySpeed, zSpeed);
         } else if (activerenderinfo.getProjectedView().squareDistanceTo(x, y, z) > 1024.0D) {
            return null;
         } else {
            return particlestatus == ParticleStatus.MINIMAL ? null : this.mc.particles.addParticle(particleData, x, y, z, xSpeed, ySpeed, zSpeed);
         }
      } else {
         return null;
      }
   }

   private ParticleStatus func_215327_a(boolean p_215327_1_) {
      ParticleStatus particlestatus = this.mc.gameSettings.particles;
      if (p_215327_1_ && particlestatus == ParticleStatus.MINIMAL && this.world.rand.nextInt(10) == 0) {
         particlestatus = ParticleStatus.DECREASED;
      }

      if (particlestatus == ParticleStatus.DECREASED && this.world.rand.nextInt(3) == 0) {
         particlestatus = ParticleStatus.MINIMAL;
      }

      return particlestatus;
   }

   /**
    * Deletes all display lists
    */
   public void deleteAllDisplayLists() {
   }

   public void broadcastSound(int soundID, BlockPos pos, int data) {
      switch(soundID) {
      case 1023:
      case 1028:
      case 1038:
         ActiveRenderInfo activerenderinfo = this.mc.gameRenderer.getActiveRenderInfo();
         if (activerenderinfo.func_216786_h()) {
            double d0 = (double)pos.getX() - activerenderinfo.getProjectedView().x;
            double d1 = (double)pos.getY() - activerenderinfo.getProjectedView().y;
            double d2 = (double)pos.getZ() - activerenderinfo.getProjectedView().z;
            double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
            double d4 = activerenderinfo.getProjectedView().x;
            double d5 = activerenderinfo.getProjectedView().y;
            double d6 = activerenderinfo.getProjectedView().z;
            if (d3 > 0.0D) {
               d4 += d0 / d3 * 2.0D;
               d5 += d1 / d3 * 2.0D;
               d6 += d2 / d3 * 2.0D;
            }

            if (soundID == 1023) {
               this.world.playSound(d4, d5, d6, SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.HOSTILE, 1.0F, 1.0F, false);
            } else if (soundID == 1038) {
               this.world.playSound(d4, d5, d6, SoundEvents.BLOCK_END_PORTAL_SPAWN, SoundCategory.HOSTILE, 1.0F, 1.0F, false);
            } else {
               this.world.playSound(d4, d5, d6, SoundEvents.ENTITY_ENDER_DRAGON_DEATH, SoundCategory.HOSTILE, 5.0F, 1.0F, false);
            }
         }
      default:
      }
   }

   public void playEvent(PlayerEntity player, int type, BlockPos blockPosIn, int data) {
      Random random = this.world.rand;
      switch(type) {
      case 1000:
         this.world.playSound(blockPosIn, SoundEvents.BLOCK_DISPENSER_DISPENSE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
         break;
      case 1001:
         this.world.playSound(blockPosIn, SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.BLOCKS, 1.0F, 1.2F, false);
         break;
      case 1002:
         this.world.playSound(blockPosIn, SoundEvents.BLOCK_DISPENSER_LAUNCH, SoundCategory.BLOCKS, 1.0F, 1.2F, false);
         break;
      case 1003:
         this.world.playSound(blockPosIn, SoundEvents.ENTITY_ENDER_EYE_LAUNCH, SoundCategory.NEUTRAL, 1.0F, 1.2F, false);
         break;
      case 1004:
         this.world.playSound(blockPosIn, SoundEvents.ENTITY_FIREWORK_ROCKET_SHOOT, SoundCategory.NEUTRAL, 1.0F, 1.2F, false);
         break;
      case 1005:
         this.world.playSound(blockPosIn, SoundEvents.BLOCK_IRON_DOOR_OPEN, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1006:
         this.world.playSound(blockPosIn, SoundEvents.BLOCK_WOODEN_DOOR_OPEN, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1007:
         this.world.playSound(blockPosIn, SoundEvents.BLOCK_WOODEN_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1008:
         this.world.playSound(blockPosIn, SoundEvents.BLOCK_FENCE_GATE_OPEN, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1009:
         this.world.playSound(blockPosIn, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F, false);
         break;
      case 1010:
         if (Item.getItemById(data) instanceof MusicDiscItem) {
            this.playRecord(((MusicDiscItem)Item.getItemById(data)).getSound(), blockPosIn);
         } else {
            this.playRecord((SoundEvent)null, blockPosIn);
         }
         break;
      case 1011:
         this.world.playSound(blockPosIn, SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1012:
         this.world.playSound(blockPosIn, SoundEvents.BLOCK_WOODEN_DOOR_CLOSE, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1013:
         this.world.playSound(blockPosIn, SoundEvents.BLOCK_WOODEN_TRAPDOOR_CLOSE, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1014:
         this.world.playSound(blockPosIn, SoundEvents.BLOCK_FENCE_GATE_CLOSE, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1015:
         this.world.playSound(blockPosIn, SoundEvents.ENTITY_GHAST_WARN, SoundCategory.HOSTILE, 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1016:
         this.world.playSound(blockPosIn, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.HOSTILE, 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1017:
         this.world.playSound(blockPosIn, SoundEvents.ENTITY_ENDER_DRAGON_SHOOT, SoundCategory.HOSTILE, 10.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1018:
         this.world.playSound(blockPosIn, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1019:
         this.world.playSound(blockPosIn, SoundEvents.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1020:
         this.world.playSound(blockPosIn, SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1021:
         this.world.playSound(blockPosIn, SoundEvents.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1022:
         this.world.playSound(blockPosIn, SoundEvents.ENTITY_WITHER_BREAK_BLOCK, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1024:
         this.world.playSound(blockPosIn, SoundEvents.ENTITY_WITHER_SHOOT, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1025:
         this.world.playSound(blockPosIn, SoundEvents.ENTITY_BAT_TAKEOFF, SoundCategory.NEUTRAL, 0.05F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1026:
         this.world.playSound(blockPosIn, SoundEvents.ENTITY_ZOMBIE_INFECT, SoundCategory.HOSTILE, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1027:
         this.world.playSound(blockPosIn, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CONVERTED, SoundCategory.NEUTRAL, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1029:
         this.world.playSound(blockPosIn, SoundEvents.BLOCK_ANVIL_DESTROY, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1030:
         this.world.playSound(blockPosIn, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1031:
         this.world.playSound(blockPosIn, SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.BLOCKS, 0.3F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1032:
         this.mc.getSoundHandler().play(SimpleSound.master(SoundEvents.BLOCK_PORTAL_TRAVEL, random.nextFloat() * 0.4F + 0.8F));
         break;
      case 1033:
         this.world.playSound(blockPosIn, SoundEvents.BLOCK_CHORUS_FLOWER_GROW, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
         break;
      case 1034:
         this.world.playSound(blockPosIn, SoundEvents.BLOCK_CHORUS_FLOWER_DEATH, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
         break;
      case 1035:
         this.world.playSound(blockPosIn, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
         break;
      case 1036:
         this.world.playSound(blockPosIn, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1037:
         this.world.playSound(blockPosIn, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1039:
         this.world.playSound(blockPosIn, SoundEvents.ENTITY_PHANTOM_BITE, SoundCategory.HOSTILE, 0.3F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1040:
         this.world.playSound(blockPosIn, SoundEvents.ENTITY_ZOMBIE_CONVERTED_TO_DROWNED, SoundCategory.NEUTRAL, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1041:
         this.world.playSound(blockPosIn, SoundEvents.ENTITY_HUSK_CONVERTED_TO_ZOMBIE, SoundCategory.NEUTRAL, 2.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1042:
         this.world.playSound(blockPosIn, SoundEvents.BLOCK_GRINDSTONE_USE, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1043:
         this.world.playSound(blockPosIn, SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.BLOCKS, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1500:
         ComposterBlock.func_220292_a(this.world, blockPosIn, data > 0);
         break;
      case 1501:
         this.world.playSound(blockPosIn, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (this.world.getRandom().nextFloat() - this.world.getRandom().nextFloat()) * 0.8F, false);

         for(int j1 = 0; j1 < 8; ++j1) {
            this.world.addParticle(ParticleTypes.LARGE_SMOKE, (double)blockPosIn.getX() + Math.random(), (double)blockPosIn.getY() + 1.2D, (double)blockPosIn.getZ() + Math.random(), 0.0D, 0.0D, 0.0D);
         }
         break;
      case 1502:
         this.world.playSound(blockPosIn, SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.BLOCKS, 0.5F, 2.6F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.8F, false);

         for(int i1 = 0; i1 < 5; ++i1) {
            double d16 = (double)blockPosIn.getX() + random.nextDouble() * 0.6D + 0.2D;
            double d22 = (double)blockPosIn.getY() + random.nextDouble() * 0.6D + 0.2D;
            double d27 = (double)blockPosIn.getZ() + random.nextDouble() * 0.6D + 0.2D;
            this.world.addParticle(ParticleTypes.SMOKE, d16, d22, d27, 0.0D, 0.0D, 0.0D);
         }
         break;
      case 1503:
         this.world.playSound(blockPosIn, SoundEvents.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F, false);

         for(int l = 0; l < 16; ++l) {
            double d15 = (double)((float)blockPosIn.getX() + (5.0F + random.nextFloat() * 6.0F) / 16.0F);
            double d21 = (double)((float)blockPosIn.getY() + 0.8125F);
            double d26 = (double)((float)blockPosIn.getZ() + (5.0F + random.nextFloat() * 6.0F) / 16.0F);
            double d29 = 0.0D;
            double d30 = 0.0D;
            double d5 = 0.0D;
            this.world.addParticle(ParticleTypes.SMOKE, d15, d21, d26, 0.0D, 0.0D, 0.0D);
         }
         break;
      case 2000:
         Direction direction = Direction.byIndex(data);
         int k = direction.getXOffset();
         int k1 = direction.getYOffset();
         int l1 = direction.getZOffset();
         double d20 = (double)blockPosIn.getX() + (double)k * 0.6D + 0.5D;
         double d25 = (double)blockPosIn.getY() + (double)k1 * 0.6D + 0.5D;
         double d28 = (double)blockPosIn.getZ() + (double)l1 * 0.6D + 0.5D;

         for(int l2 = 0; l2 < 10; ++l2) {
            double d31 = random.nextDouble() * 0.2D + 0.01D;
            double d32 = d20 + (double)k * 0.01D + (random.nextDouble() - 0.5D) * (double)l1 * 0.5D;
            double d33 = d25 + (double)k1 * 0.01D + (random.nextDouble() - 0.5D) * (double)k1 * 0.5D;
            double d34 = d28 + (double)l1 * 0.01D + (random.nextDouble() - 0.5D) * (double)k * 0.5D;
            double d35 = (double)k * d31 + random.nextGaussian() * 0.01D;
            double d36 = (double)k1 * d31 + random.nextGaussian() * 0.01D;
            double d10 = (double)l1 * d31 + random.nextGaussian() * 0.01D;
            this.addParticleUnchecked(ParticleTypes.SMOKE, d32, d33, d34, d35, d36, d10);
         }
         break;
      case 2001:
         BlockState blockstate = Block.getStateById(data);
         if (!blockstate.isAir(this.world, blockPosIn)) {
            SoundType soundtype = blockstate.getSoundType();
            this.world.playSound(blockPosIn, soundtype.getBreakSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F, false);
         }

         this.mc.particles.addBlockDestroyEffects(blockPosIn, blockstate);
         break;
      case 2002:
      case 2007:
         double d11 = (double)blockPosIn.getX();
         double d14 = (double)blockPosIn.getY();
         double d19 = (double)blockPosIn.getZ();

         for(int j2 = 0; j2 < 8; ++j2) {
            this.addParticleUnchecked(new ItemParticleData(ParticleTypes.ITEM, new ItemStack(Items.SPLASH_POTION)), d11, d14, d19, random.nextGaussian() * 0.15D, random.nextDouble() * 0.2D, random.nextGaussian() * 0.15D);
         }

         float f4 = (float)(data >> 16 & 255) / 255.0F;
         float f2 = (float)(data >> 8 & 255) / 255.0F;
         float f5 = (float)(data >> 0 & 255) / 255.0F;
         IParticleData iparticledata = type == 2007 ? ParticleTypes.INSTANT_EFFECT : ParticleTypes.EFFECT;

         for(int k2 = 0; k2 < 100; ++k2) {
            double d4 = random.nextDouble() * 4.0D;
            double d6 = random.nextDouble() * Math.PI * 2.0D;
            double d7 = Math.cos(d6) * d4;
            double d8 = 0.01D + random.nextDouble() * 0.5D;
            double d9 = Math.sin(d6) * d4;
            Particle particle1 = this.addParticleUnchecked(iparticledata, iparticledata.getType().getAlwaysShow(), d11 + d7 * 0.1D, d14 + 0.3D, d19 + d9 * 0.1D, d7, d8, d9);
            if (particle1 != null) {
               float f3 = 0.75F + random.nextFloat() * 0.25F;
               particle1.setColor(f4 * f3, f2 * f3, f5 * f3);
               particle1.multiplyVelocity((float)d4);
            }
         }

         this.world.playSound(blockPosIn, SoundEvents.ENTITY_SPLASH_POTION_BREAK, SoundCategory.NEUTRAL, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 2003:
         double d0 = (double)blockPosIn.getX() + 0.5D;
         double d13 = (double)blockPosIn.getY();
         double d18 = (double)blockPosIn.getZ() + 0.5D;

         for(int i2 = 0; i2 < 8; ++i2) {
            this.addParticleUnchecked(new ItemParticleData(ParticleTypes.ITEM, new ItemStack(Items.ENDER_EYE)), d0, d13, d18, random.nextGaussian() * 0.15D, random.nextDouble() * 0.2D, random.nextGaussian() * 0.15D);
         }

         for(double d24 = 0.0D; d24 < (Math.PI * 2D); d24 += 0.15707963267948966D) {
            this.addParticleUnchecked(ParticleTypes.PORTAL, d0 + Math.cos(d24) * 5.0D, d13 - 0.4D, d18 + Math.sin(d24) * 5.0D, Math.cos(d24) * -5.0D, 0.0D, Math.sin(d24) * -5.0D);
            this.addParticleUnchecked(ParticleTypes.PORTAL, d0 + Math.cos(d24) * 5.0D, d13 - 0.4D, d18 + Math.sin(d24) * 5.0D, Math.cos(d24) * -7.0D, 0.0D, Math.sin(d24) * -7.0D);
         }
         break;
      case 2004:
         for(int j = 0; j < 20; ++j) {
            double d12 = (double)blockPosIn.getX() + 0.5D + ((double)this.world.rand.nextFloat() - 0.5D) * 2.0D;
            double d17 = (double)blockPosIn.getY() + 0.5D + ((double)this.world.rand.nextFloat() - 0.5D) * 2.0D;
            double d23 = (double)blockPosIn.getZ() + 0.5D + ((double)this.world.rand.nextFloat() - 0.5D) * 2.0D;
            this.world.addParticle(ParticleTypes.SMOKE, d12, d17, d23, 0.0D, 0.0D, 0.0D);
            this.world.addParticle(ParticleTypes.FLAME, d12, d17, d23, 0.0D, 0.0D, 0.0D);
         }
         break;
      case 2005:
         BoneMealItem.spawnBonemealParticles(this.world, blockPosIn, data);
         break;
      case 2006:
         for(int i = 0; i < 200; ++i) {
            float f = random.nextFloat() * 4.0F;
            float f1 = random.nextFloat() * ((float)Math.PI * 2F);
            double d1 = (double)(MathHelper.cos(f1) * f);
            double d2 = 0.01D + random.nextDouble() * 0.5D;
            double d3 = (double)(MathHelper.sin(f1) * f);
            Particle particle = this.addParticleUnchecked(ParticleTypes.DRAGON_BREATH, false, (double)blockPosIn.getX() + d1 * 0.1D, (double)blockPosIn.getY() + 0.3D, (double)blockPosIn.getZ() + d3 * 0.1D, d1, d2, d3);
            if (particle != null) {
               particle.multiplyVelocity(f);
            }
         }

         this.world.playSound(blockPosIn, SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.HOSTILE, 1.0F, this.world.rand.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 2008:
         this.world.addParticle(ParticleTypes.EXPLOSION, (double)blockPosIn.getX() + 0.5D, (double)blockPosIn.getY() + 0.5D, (double)blockPosIn.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
         break;
      case 3000:
         this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, true, (double)blockPosIn.getX() + 0.5D, (double)blockPosIn.getY() + 0.5D, (double)blockPosIn.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
         this.world.playSound(blockPosIn, SoundEvents.BLOCK_END_GATEWAY_SPAWN, SoundCategory.BLOCKS, 10.0F, (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F, false);
         break;
      case 3001:
         this.world.playSound(blockPosIn, SoundEvents.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.HOSTILE, 64.0F, 0.8F + this.world.rand.nextFloat() * 0.3F, false);
      }

   }

   public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
      if (progress >= 0 && progress < 10) {
         DestroyBlockProgress destroyblockprogress = this.damagedBlocks.get(breakerId);
         if (destroyblockprogress == null || destroyblockprogress.getPosition().getX() != pos.getX() || destroyblockprogress.getPosition().getY() != pos.getY() || destroyblockprogress.getPosition().getZ() != pos.getZ()) {
            destroyblockprogress = new DestroyBlockProgress(breakerId, pos);
            this.damagedBlocks.put(breakerId, destroyblockprogress);
         }

         destroyblockprogress.setPartialBlockDamage(progress);
         destroyblockprogress.setCloudUpdateTick(this.ticks);
      } else {
         this.damagedBlocks.remove(breakerId);
      }

   }

   public boolean hasNoChunkUpdates() {
      return this.chunksToUpdate.isEmpty() && this.renderDispatcher.hasNoChunkUpdates();
   }

   public void setDisplayListEntitiesDirty() {
      this.displayListEntitiesDirty = true;
      this.cloudsNeedUpdate = true;
   }

   public void updateTileEntities(Collection<TileEntity> tileEntitiesToRemove, Collection<TileEntity> tileEntitiesToAdd) {
      synchronized(this.setTileEntities) {
         this.setTileEntities.removeAll(tileEntitiesToRemove);
         this.setTileEntities.addAll(tileEntitiesToAdd);
      }
   }

   @Override
   public net.minecraftforge.resource.IResourceType getResourceType() {
      return net.minecraftforge.resource.VanillaResourceType.MODELS;
   }

   @OnlyIn(Dist.CLIENT)
   class LocalRenderInformationContainer {
      private final ChunkRender renderChunk;
      private final Direction facing;
      private byte setFacing;
      private final int counter;

      private LocalRenderInformationContainer(ChunkRender renderChunkIn, @Nullable Direction facingIn, int counterIn) {
         this.renderChunk = renderChunkIn;
         this.facing = facingIn;
         this.counter = counterIn;
      }

      public void setDirection(byte dir, Direction facingIn) {
         this.setFacing = (byte)(this.setFacing | dir | 1 << facingIn.ordinal());
      }

      public boolean hasDirection(Direction facingIn) {
         return (this.setFacing & 1 << facingIn.ordinal()) > 0;
      }
   }
}