package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPartEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.item.EnderPearlEntity;
import net.minecraft.entity.item.ExperienceBottleEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.EyeOfEnderEntity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.item.FireworkRocketEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.item.LeashKnotEntity;
import net.minecraft.entity.item.PaintingEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.item.minecart.SpawnerMinecartEntity;
import net.minecraft.entity.item.minecart.TNTMinecartEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.entity.monster.CaveSpiderEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.DrownedEntity;
import net.minecraft.entity.monster.ElderGuardianEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.EndermiteEntity;
import net.minecraft.entity.monster.EvokerEntity;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.monster.GiantEntity;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.entity.monster.HuskEntity;
import net.minecraft.entity.monster.IllusionerEntity;
import net.minecraft.entity.monster.MagmaCubeEntity;
import net.minecraft.entity.monster.PhantomEntity;
import net.minecraft.entity.monster.PillagerEntity;
import net.minecraft.entity.monster.RavagerEntity;
import net.minecraft.entity.monster.ShulkerEntity;
import net.minecraft.entity.monster.SilverfishEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.entity.monster.StrayEntity;
import net.minecraft.entity.monster.VexEntity;
import net.minecraft.entity.monster.VindicatorEntity;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.monster.ZombiePigmanEntity;
import net.minecraft.entity.monster.ZombieVillagerEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.passive.fish.CodEntity;
import net.minecraft.entity.passive.fish.PufferfishEntity;
import net.minecraft.entity.passive.fish.SalmonEntity;
import net.minecraft.entity.passive.fish.TropicalFishEntity;
import net.minecraft.entity.passive.horse.DonkeyEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraft.entity.passive.horse.MuleEntity;
import net.minecraft.entity.passive.horse.SkeletonHorseEntity;
import net.minecraft.entity.passive.horse.TraderLlamaEntity;
import net.minecraft.entity.passive.horse.ZombieHorseEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.EggEntity;
import net.minecraft.entity.projectile.EvokerFangsEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.SnowballEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EntityRendererManager {
   public final Map<Class<? extends Entity>, EntityRenderer<? extends Entity>> entityRenderMap = Maps.newHashMap();
   private final Map<String, PlayerRenderer> skinMap = Maps.newHashMap();
   private final PlayerRenderer playerRenderer;
   private FontRenderer textRenderer;
   private double renderPosX;
   private double renderPosY;
   private double renderPosZ;
   public final TextureManager textureManager;
   public World world;
   public ActiveRenderInfo info;
   public Entity pointedEntity;
   public float playerViewY;
   public float playerViewX;
   public GameSettings options;
   private boolean renderOutlines;
   private boolean renderShadow = true;
   private boolean debugBoundingBox;

   public <T extends Entity> void func_217782_a(Class<T> p_217782_1_, EntityRenderer<? super T> p_217782_2_) {
      this.entityRenderMap.put(p_217782_1_, p_217782_2_);
   }

   public EntityRendererManager(TextureManager p_i50971_1_, net.minecraft.client.renderer.ItemRenderer p_i50971_2_, IReloadableResourceManager p_i50971_3_) {
      this.textureManager = p_i50971_1_;
      this.func_217782_a(CaveSpiderEntity.class, new CaveSpiderRenderer(this));
      this.func_217782_a(SpiderEntity.class, new SpiderRenderer<>(this));
      this.func_217782_a(PigEntity.class, new PigRenderer(this));
      this.func_217782_a(SheepEntity.class, new SheepRenderer(this));
      this.func_217782_a(CowEntity.class, new CowRenderer(this));
      this.func_217782_a(MooshroomEntity.class, new MooshroomRenderer(this));
      this.func_217782_a(WolfEntity.class, new WolfRenderer(this));
      this.func_217782_a(ChickenEntity.class, new ChickenRenderer(this));
      this.func_217782_a(OcelotEntity.class, new OcelotRenderer(this));
      this.func_217782_a(RabbitEntity.class, new RabbitRenderer(this));
      this.func_217782_a(ParrotEntity.class, new ParrotRenderer(this));
      this.func_217782_a(TurtleEntity.class, new TurtleRenderer(this));
      this.func_217782_a(SilverfishEntity.class, new SilverfishRenderer(this));
      this.func_217782_a(EndermiteEntity.class, new EndermiteRenderer(this));
      this.func_217782_a(CreeperEntity.class, new CreeperRenderer(this));
      this.func_217782_a(EndermanEntity.class, new EndermanRenderer(this));
      this.func_217782_a(SnowGolemEntity.class, new SnowManRenderer(this));
      this.func_217782_a(SkeletonEntity.class, new SkeletonRenderer(this));
      this.func_217782_a(WitherSkeletonEntity.class, new WitherSkeletonRenderer(this));
      this.func_217782_a(StrayEntity.class, new StrayRenderer(this));
      this.func_217782_a(WitchEntity.class, new WitchRenderer(this));
      this.func_217782_a(BlazeEntity.class, new BlazeRenderer(this));
      this.func_217782_a(ZombiePigmanEntity.class, new PigZombieRenderer(this));
      this.func_217782_a(ZombieEntity.class, new ZombieRenderer(this));
      this.func_217782_a(ZombieVillagerEntity.class, new ZombieVillagerRenderer(this, p_i50971_3_));
      this.func_217782_a(HuskEntity.class, new HuskRenderer(this));
      this.func_217782_a(DrownedEntity.class, new DrownedRenderer(this));
      this.func_217782_a(SlimeEntity.class, new SlimeRenderer(this));
      this.func_217782_a(MagmaCubeEntity.class, new MagmaCubeRenderer(this));
      this.func_217782_a(GiantEntity.class, new GiantZombieRenderer(this, 6.0F));
      this.func_217782_a(GhastEntity.class, new GhastRenderer(this));
      this.func_217782_a(SquidEntity.class, new SquidRenderer(this));
      this.func_217782_a(VillagerEntity.class, new VillagerRenderer(this, p_i50971_3_));
      this.func_217782_a(WanderingTraderEntity.class, new WanderingTraderRenderer(this));
      this.func_217782_a(IronGolemEntity.class, new IronGolemRenderer(this));
      this.func_217782_a(BatEntity.class, new BatRenderer(this));
      this.func_217782_a(GuardianEntity.class, new GuardianRenderer(this));
      this.func_217782_a(ElderGuardianEntity.class, new ElderGuardianRenderer(this));
      this.func_217782_a(ShulkerEntity.class, new ShulkerRenderer(this));
      this.func_217782_a(PolarBearEntity.class, new PolarBearRenderer(this));
      this.func_217782_a(EvokerEntity.class, new EvokerRenderer<>(this));
      this.func_217782_a(VindicatorEntity.class, new VindicatorRenderer(this));
      this.func_217782_a(PillagerEntity.class, new PillagerRenderer(this));
      this.func_217782_a(RavagerEntity.class, new RavagerRenderer(this));
      this.func_217782_a(VexEntity.class, new VexRenderer(this));
      this.func_217782_a(IllusionerEntity.class, new IllusionerRenderer(this));
      this.func_217782_a(PhantomEntity.class, new PhantomRenderer(this));
      this.func_217782_a(PufferfishEntity.class, new PufferfishRenderer(this));
      this.func_217782_a(SalmonEntity.class, new SalmonRenderer(this));
      this.func_217782_a(CodEntity.class, new CodRenderer(this));
      this.func_217782_a(TropicalFishEntity.class, new TropicalFishRenderer(this));
      this.func_217782_a(DolphinEntity.class, new DolphinRenderer(this));
      this.func_217782_a(PandaEntity.class, new PandaRenderer(this));
      this.func_217782_a(CatEntity.class, new CatRenderer(this));
      this.func_217782_a(FoxEntity.class, new FoxRenderer(this));
      this.func_217782_a(EnderDragonEntity.class, new EnderDragonRenderer(this));
      this.func_217782_a(EnderCrystalEntity.class, new EnderCrystalRenderer(this));
      this.func_217782_a(WitherEntity.class, new WitherRenderer(this));
      this.func_217782_a(Entity.class, new DefaultRenderer(this));
      this.func_217782_a(PaintingEntity.class, new PaintingRenderer(this));
      this.func_217782_a(ItemFrameEntity.class, new ItemFrameRenderer(this, p_i50971_2_));
      this.func_217782_a(LeashKnotEntity.class, new LeashKnotRenderer(this));
      this.func_217782_a(ArrowEntity.class, new TippedArrowRenderer(this));
      this.func_217782_a(SpectralArrowEntity.class, new SpectralArrowRenderer(this));
      this.func_217782_a(TridentEntity.class, new TridentRenderer(this));
      this.func_217782_a(SnowballEntity.class, new SpriteRenderer<>(this, p_i50971_2_));
      this.func_217782_a(EnderPearlEntity.class, new SpriteRenderer<>(this, p_i50971_2_));
      this.func_217782_a(EyeOfEnderEntity.class, new SpriteRenderer<>(this, p_i50971_2_));
      this.func_217782_a(EggEntity.class, new SpriteRenderer<>(this, p_i50971_2_));
      this.func_217782_a(PotionEntity.class, new SpriteRenderer<>(this, p_i50971_2_));
      this.func_217782_a(ExperienceBottleEntity.class, new SpriteRenderer<>(this, p_i50971_2_));
      this.func_217782_a(FireworkRocketEntity.class, new FireworkRocketRenderer(this, p_i50971_2_));
      this.func_217782_a(FireballEntity.class, new SpriteRenderer<>(this, p_i50971_2_, 3.0F));
      this.func_217782_a(SmallFireballEntity.class, new SpriteRenderer<>(this, p_i50971_2_, 0.75F));
      this.func_217782_a(DragonFireballEntity.class, new DragonFireballRenderer(this));
      this.func_217782_a(WitherSkullEntity.class, new WitherSkullRenderer(this));
      this.func_217782_a(ShulkerBulletEntity.class, new ShulkerBulletRenderer(this));
      this.func_217782_a(ItemEntity.class, new ItemRenderer(this, p_i50971_2_));
      this.func_217782_a(ExperienceOrbEntity.class, new ExperienceOrbRenderer(this));
      this.func_217782_a(TNTEntity.class, new TNTRenderer(this));
      this.func_217782_a(FallingBlockEntity.class, new FallingBlockRenderer(this));
      this.func_217782_a(ArmorStandEntity.class, new ArmorStandRenderer(this));
      this.func_217782_a(EvokerFangsEntity.class, new EvokerFangsRenderer(this));
      this.func_217782_a(TNTMinecartEntity.class, new TNTMinecartRenderer(this));
      this.func_217782_a(SpawnerMinecartEntity.class, new MinecartRenderer<>(this));
      this.func_217782_a(AbstractMinecartEntity.class, new MinecartRenderer<>(this));
      this.func_217782_a(BoatEntity.class, new BoatRenderer(this));
      this.func_217782_a(FishingBobberEntity.class, new FishRenderer(this));
      this.func_217782_a(AreaEffectCloudEntity.class, new AreaEffectCloudRenderer(this));
      this.func_217782_a(HorseEntity.class, new HorseRenderer(this));
      this.func_217782_a(SkeletonHorseEntity.class, new UndeadHorseRenderer(this));
      this.func_217782_a(ZombieHorseEntity.class, new UndeadHorseRenderer(this));
      this.func_217782_a(MuleEntity.class, new ChestedHorseRenderer<>(this, 0.92F));
      this.func_217782_a(DonkeyEntity.class, new ChestedHorseRenderer<>(this, 0.87F));
      this.func_217782_a(LlamaEntity.class, new LlamaRenderer(this));
      this.func_217782_a(TraderLlamaEntity.class, new LlamaRenderer(this));
      this.func_217782_a(LlamaSpitEntity.class, new LlamaSpitRenderer(this));
      this.func_217782_a(LightningBoltEntity.class, new LightningBoltRenderer(this));
      this.playerRenderer = new PlayerRenderer(this);
      this.skinMap.put("default", this.playerRenderer);
      this.skinMap.put("slim", new PlayerRenderer(this, true));
   }

   public Map<String, PlayerRenderer> getSkinMap() {
       return (Map<String, PlayerRenderer>) java.util.Collections.unmodifiableMap(skinMap);
   }

   public void setRenderPosition(double renderPosXIn, double renderPosYIn, double renderPosZIn) {
      this.renderPosX = renderPosXIn;
      this.renderPosY = renderPosYIn;
      this.renderPosZ = renderPosZIn;
   }

   public <T extends Entity, U extends EntityRenderer<T>> U getEntityClassRenderObject(Class<? extends Entity> entityClass) {
      EntityRenderer<? extends Entity> entityrenderer = this.entityRenderMap.get(entityClass);
      if (entityrenderer == null && entityClass != Entity.class) {
         entityrenderer = this.getEntityClassRenderObject((Class<? extends Entity>) entityClass.getSuperclass());
         this.entityRenderMap.put(entityClass, entityrenderer);
      }

      return (U)entityrenderer;
   }

   @Nullable
   public <T extends Entity, U extends EntityRenderer<T>> U getEntityRenderObject(T entityIn) {
      if (entityIn instanceof AbstractClientPlayerEntity) {
         String s = ((AbstractClientPlayerEntity)entityIn).getSkinType();
         PlayerRenderer playerrenderer = this.skinMap.get(s);
         return (U)(playerrenderer != null ? playerrenderer : this.playerRenderer);
      } else {
         return this.getEntityClassRenderObject(entityIn.getClass());
      }
   }

   public void func_217781_a(World p_217781_1_, FontRenderer p_217781_2_, ActiveRenderInfo p_217781_3_, Entity p_217781_4_, GameSettings p_217781_5_) {
      this.world = p_217781_1_;
      this.options = p_217781_5_;
      this.info = p_217781_3_;
      this.pointedEntity = p_217781_4_;
      this.textRenderer = p_217781_2_;
      if (p_217781_3_.func_216773_g() instanceof LivingEntity && ((LivingEntity)p_217781_3_.func_216773_g()).isSleeping()) {
         Direction direction = ((LivingEntity)p_217781_3_.func_216773_g()).func_213376_dz();
         if (direction != null) {
            this.playerViewY = direction.getOpposite().getHorizontalAngle();
            this.playerViewX = 0.0F;
         }
      } else {
         this.playerViewY = p_217781_3_.getYaw();
         this.playerViewX = p_217781_3_.getPitch();
      }

   }

   public void setPlayerViewY(float playerViewYIn) {
      this.playerViewY = playerViewYIn;
   }

   public boolean isRenderShadow() {
      return this.renderShadow;
   }

   public void setRenderShadow(boolean renderShadowIn) {
      this.renderShadow = renderShadowIn;
   }

   public void setDebugBoundingBox(boolean debugBoundingBoxIn) {
      this.debugBoundingBox = debugBoundingBoxIn;
   }

   public boolean isDebugBoundingBox() {
      return this.debugBoundingBox;
   }

   public boolean isRenderMultipass(Entity entityIn) {
      return this.<Entity, EntityRenderer>getEntityRenderObject(entityIn).isMultipass();
   }

   public boolean shouldRender(Entity entityIn, ICamera camera, double camX, double camY, double camZ) {
      EntityRenderer<Entity> entityrenderer = this.getEntityRenderObject(entityIn);
      return entityrenderer != null && entityrenderer.shouldRender(entityIn, camera, camX, camY, camZ);
   }

   public void renderEntityStatic(Entity entityIn, float partialTicks, boolean p_188388_3_) {
      if (entityIn.ticksExisted == 0) {
         entityIn.lastTickPosX = entityIn.posX;
         entityIn.lastTickPosY = entityIn.posY;
         entityIn.lastTickPosZ = entityIn.posZ;
      }

      double d0 = MathHelper.lerp((double)partialTicks, entityIn.lastTickPosX, entityIn.posX);
      double d1 = MathHelper.lerp((double)partialTicks, entityIn.lastTickPosY, entityIn.posY);
      double d2 = MathHelper.lerp((double)partialTicks, entityIn.lastTickPosZ, entityIn.posZ);
      float f = MathHelper.lerp(partialTicks, entityIn.prevRotationYaw, entityIn.rotationYaw);
      int i = entityIn.getBrightnessForRender();
      if (entityIn.isBurning()) {
         i = 15728880;
      }

      int j = i % 65536;
      int k = i / 65536;
      GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)j, (float)k);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.renderEntity(entityIn, d0 - this.renderPosX, d1 - this.renderPosY, d2 - this.renderPosZ, f, partialTicks, p_188388_3_);
   }

   public void renderEntity(Entity entityIn, double x, double y, double z, float yaw, float partialTicks, boolean p_188391_10_) {
      EntityRenderer<Entity> entityrenderer = null;

      try {
         entityrenderer = this.getEntityRenderObject(entityIn);
         if (entityrenderer != null && this.textureManager != null) {
            try {
               entityrenderer.setRenderOutlines(this.renderOutlines);
               entityrenderer.doRender(entityIn, x, y, z, yaw, partialTicks);
            } catch (Throwable throwable1) {
               throw new ReportedException(CrashReport.makeCrashReport(throwable1, "Rendering entity in world"));
            }

            try {
               if (!this.renderOutlines) {
                  entityrenderer.doRenderShadowAndFire(entityIn, x, y, z, yaw, partialTicks);
               }
            } catch (Throwable throwable2) {
               throw new ReportedException(CrashReport.makeCrashReport(throwable2, "Post-rendering entity in world"));
            }

            if (this.debugBoundingBox && !entityIn.isInvisible() && !p_188391_10_ && !Minecraft.getInstance().isReducedDebug()) {
               try {
                  this.renderDebugBoundingBox(entityIn, x, y, z, yaw, partialTicks);
               } catch (Throwable throwable) {
                  throw new ReportedException(CrashReport.makeCrashReport(throwable, "Rendering entity hitbox in world"));
               }
            }
         }

      } catch (Throwable throwable3) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable3, "Rendering entity in world");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being rendered");
         entityIn.fillCrashReport(crashreportcategory);
         CrashReportCategory crashreportcategory1 = crashreport.makeCategory("Renderer details");
         crashreportcategory1.addDetail("Assigned renderer", entityrenderer);
         crashreportcategory1.addDetail("Location", CrashReportCategory.getCoordinateInfo(x, y, z));
         crashreportcategory1.addDetail("Rotation", yaw);
         crashreportcategory1.addDetail("Delta", partialTicks);
         throw new ReportedException(crashreport);
      }
   }

   public void renderMultipass(Entity entityIn, float partialTicks) {
      if (entityIn.ticksExisted == 0) {
         entityIn.lastTickPosX = entityIn.posX;
         entityIn.lastTickPosY = entityIn.posY;
         entityIn.lastTickPosZ = entityIn.posZ;
      }

      double d0 = MathHelper.lerp((double)partialTicks, entityIn.lastTickPosX, entityIn.posX);
      double d1 = MathHelper.lerp((double)partialTicks, entityIn.lastTickPosY, entityIn.posY);
      double d2 = MathHelper.lerp((double)partialTicks, entityIn.lastTickPosZ, entityIn.posZ);
      float f = MathHelper.lerp(partialTicks, entityIn.prevRotationYaw, entityIn.rotationYaw);
      int i = entityIn.getBrightnessForRender();
      if (entityIn.isBurning()) {
         i = 15728880;
      }

      int j = i % 65536;
      int k = i / 65536;
      GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)j, (float)k);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      EntityRenderer<Entity> entityrenderer = this.getEntityRenderObject(entityIn);
      if (entityrenderer != null && this.textureManager != null) {
         entityrenderer.renderMultipass(entityIn, d0 - this.renderPosX, d1 - this.renderPosY, d2 - this.renderPosZ, f, partialTicks);
      }

   }

   /**
    * Renders the bounding box around an entity when F3+B is pressed
    */
   private void renderDebugBoundingBox(Entity entityIn, double x, double y, double z, float entityYaw, float partialTicks) {
      GlStateManager.depthMask(false);
      GlStateManager.disableTexture();
      GlStateManager.disableLighting();
      GlStateManager.disableCull();
      GlStateManager.disableBlend();
      float f = entityIn.getWidth() / 2.0F;
      AxisAlignedBB axisalignedbb = entityIn.getBoundingBox();
      WorldRenderer.drawBoundingBox(axisalignedbb.minX - entityIn.posX + x, axisalignedbb.minY - entityIn.posY + y, axisalignedbb.minZ - entityIn.posZ + z, axisalignedbb.maxX - entityIn.posX + x, axisalignedbb.maxY - entityIn.posY + y, axisalignedbb.maxZ - entityIn.posZ + z, 1.0F, 1.0F, 1.0F, 1.0F);
      if (entityIn instanceof EnderDragonEntity) {
         for(EnderDragonPartEntity enderdragonpartentity : ((EnderDragonEntity)entityIn).func_213404_dT()) {
            double d0 = (enderdragonpartentity.posX - enderdragonpartentity.prevPosX) * (double)partialTicks;
            double d1 = (enderdragonpartentity.posY - enderdragonpartentity.prevPosY) * (double)partialTicks;
            double d2 = (enderdragonpartentity.posZ - enderdragonpartentity.prevPosZ) * (double)partialTicks;
            AxisAlignedBB axisalignedbb1 = enderdragonpartentity.getBoundingBox();
            WorldRenderer.drawBoundingBox(axisalignedbb1.minX - this.renderPosX + d0, axisalignedbb1.minY - this.renderPosY + d1, axisalignedbb1.minZ - this.renderPosZ + d2, axisalignedbb1.maxX - this.renderPosX + d0, axisalignedbb1.maxY - this.renderPosY + d1, axisalignedbb1.maxZ - this.renderPosZ + d2, 0.25F, 1.0F, 0.0F, 1.0F);
         }
      }

      if (entityIn instanceof LivingEntity) {
         float f1 = 0.01F;
         WorldRenderer.drawBoundingBox(x - (double)f, y + (double)entityIn.getEyeHeight() - (double)0.01F, z - (double)f, x + (double)f, y + (double)entityIn.getEyeHeight() + (double)0.01F, z + (double)f, 1.0F, 0.0F, 0.0F, 1.0F);
      }

      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      Vec3d vec3d = entityIn.getLook(partialTicks);
      bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
      bufferbuilder.pos(x, y + (double)entityIn.getEyeHeight(), z).color(0, 0, 255, 255).endVertex();
      bufferbuilder.pos(x + vec3d.x * 2.0D, y + (double)entityIn.getEyeHeight() + vec3d.y * 2.0D, z + vec3d.z * 2.0D).color(0, 0, 255, 255).endVertex();
      tessellator.draw();
      GlStateManager.enableTexture();
      GlStateManager.enableLighting();
      GlStateManager.enableCull();
      GlStateManager.disableBlend();
      GlStateManager.depthMask(true);
   }

   /**
    * World sets this RenderManager's worldObj to the world provided
    */
   public void setWorld(@Nullable World worldIn) {
      this.world = worldIn;
      if (worldIn == null) {
         this.info = null;
      }

   }

   public double getDistanceToCamera(double x, double y, double z) {
      return this.info.getProjectedView().squareDistanceTo(x, y, z);
   }

   /**
    * Returns the font renderer
    */
   public FontRenderer getFontRenderer() {
      return this.textRenderer;
   }

   public void setRenderOutlines(boolean renderOutlinesIn) {
      this.renderOutlines = renderOutlinesIn;
   }
}