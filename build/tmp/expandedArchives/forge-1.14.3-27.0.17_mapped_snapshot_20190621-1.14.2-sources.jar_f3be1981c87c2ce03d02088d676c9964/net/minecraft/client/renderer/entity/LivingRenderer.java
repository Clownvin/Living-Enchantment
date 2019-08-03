package net.minecraft.client.renderer.entity;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.FloatBuffer;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public abstract class LivingRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements IEntityRenderer<T, M> {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final DynamicTexture TEXTURE_BRIGHTNESS = Util.make(new DynamicTexture(16, 16, false), (p_203414_0_) -> {
      p_203414_0_.getTextureData().untrack();

      for(int i = 0; i < 16; ++i) {
         for(int j = 0; j < 16; ++j) {
            p_203414_0_.getTextureData().setPixelRGBA(j, i, -1);
         }
      }

      p_203414_0_.updateDynamicTexture();
   });
   protected M field_77045_g;
   protected final FloatBuffer brightnessBuffer = GLAllocation.createDirectFloatBuffer(4);
   protected final List<LayerRenderer<T, M>> layerRenderers = Lists.newArrayList();
   protected boolean renderMarker;

   public LivingRenderer(EntityRendererManager p_i50965_1_, M p_i50965_2_, float p_i50965_3_) {
      super(p_i50965_1_);
      this.field_77045_g = p_i50965_2_;
      this.shadowSize = p_i50965_3_;
   }

   public final boolean addLayer(LayerRenderer<T, M> layer) {
      return this.layerRenderers.add(layer);
   }

   public M getEntityModel() {
      return this.field_77045_g;
   }

   /**
    * Renders the desired {@code T} type Entity.
    */
   public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
      if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderLivingEvent.Pre<T, M>(entity, this, partialTicks, x, y, z))) return;
      GlStateManager.pushMatrix();
      GlStateManager.disableCull();
      this.field_77045_g.swingProgress = this.getSwingProgress(entity, partialTicks);
      boolean shouldSit = entity.isPassenger() && (entity.getRidingEntity() != null && entity.getRidingEntity().shouldRiderSit());
      this.field_77045_g.isSitting = shouldSit;
      this.field_77045_g.isChild = entity.isChild();

      try {
         float f = MathHelper.func_219805_h(partialTicks, entity.prevRenderYawOffset, entity.renderYawOffset);
         float f1 = MathHelper.func_219805_h(partialTicks, entity.prevRotationYawHead, entity.rotationYawHead);
         float f2 = f1 - f;
         if (shouldSit && entity.getRidingEntity() instanceof LivingEntity) {
            LivingEntity livingentity = (LivingEntity)entity.getRidingEntity();
            f = MathHelper.func_219805_h(partialTicks, livingentity.prevRenderYawOffset, livingentity.renderYawOffset);
            f2 = f1 - f;
            float f3 = MathHelper.wrapDegrees(f2);
            if (f3 < -85.0F) {
               f3 = -85.0F;
            }

            if (f3 >= 85.0F) {
               f3 = 85.0F;
            }

            f = f1 - f3;
            if (f3 * f3 > 2500.0F) {
               f += f3 * 0.2F;
            }

            f2 = f1 - f;
         }

         float f7 = MathHelper.lerp(partialTicks, entity.prevRotationPitch, entity.rotationPitch);
         this.renderLivingAt(entity, x, y, z);
         float f8 = this.handleRotationFloat(entity, partialTicks);
         this.applyRotations(entity, f8, f, partialTicks);
         float f4 = this.prepareScale(entity, partialTicks);
         float f5 = 0.0F;
         float f6 = 0.0F;
         if (!entity.isPassenger() && entity.isAlive()) {
            f5 = MathHelper.lerp(partialTicks, entity.prevLimbSwingAmount, entity.limbSwingAmount);
            f6 = entity.limbSwing - entity.limbSwingAmount * (1.0F - partialTicks);
            if (entity.isChild()) {
               f6 *= 3.0F;
            }

            if (f5 > 1.0F) {
               f5 = 1.0F;
            }
         }

         GlStateManager.enableAlphaTest();
         this.field_77045_g.setLivingAnimations(entity, f6, f5, partialTicks);
         this.field_77045_g.setRotationAngles(entity, f6, f5, f8, f2, f7, f4);
         if (this.renderOutlines) {
            boolean flag = this.setScoreTeamColor(entity);
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(entity));
            if (!this.renderMarker) {
               this.renderModel(entity, f6, f5, f8, f2, f7, f4);
            }

            if (!entity.isSpectator()) {
               this.renderLayers(entity, f6, f5, partialTicks, f8, f2, f7, f4);
            }

            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
            if (flag) {
               this.unsetScoreTeamColor();
            }
         } else {
            boolean flag1 = this.setDoRenderBrightness(entity, partialTicks);
            this.renderModel(entity, f6, f5, f8, f2, f7, f4);
            if (flag1) {
               this.unsetBrightness();
            }

            GlStateManager.depthMask(true);
            if (!entity.isSpectator()) {
               this.renderLayers(entity, f6, f5, partialTicks, f8, f2, f7, f4);
            }
         }

         GlStateManager.disableRescaleNormal();
      } catch (Exception exception) {
         LOGGER.error("Couldn't render entity", (Throwable)exception);
      }

      GlStateManager.activeTexture(GLX.GL_TEXTURE1);
      GlStateManager.enableTexture();
      GlStateManager.activeTexture(GLX.GL_TEXTURE0);
      GlStateManager.enableCull();
      GlStateManager.popMatrix();
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderLivingEvent.Post<T, M>(entity, this, partialTicks, x, y, z));
   }

   public float prepareScale(T entitylivingbaseIn, float partialTicks) {
      GlStateManager.enableRescaleNormal();
      GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
      this.preRenderCallback(entitylivingbaseIn, partialTicks);
      float f = 0.0625F;
      GlStateManager.translatef(0.0F, -1.501F, 0.0F);
      return 0.0625F;
   }

   protected boolean setScoreTeamColor(T entityLivingBaseIn) {
      GlStateManager.disableLighting();
      GlStateManager.activeTexture(GLX.GL_TEXTURE1);
      GlStateManager.disableTexture();
      GlStateManager.activeTexture(GLX.GL_TEXTURE0);
      return true;
   }

   protected void unsetScoreTeamColor() {
      GlStateManager.enableLighting();
      GlStateManager.activeTexture(GLX.GL_TEXTURE1);
      GlStateManager.enableTexture();
      GlStateManager.activeTexture(GLX.GL_TEXTURE0);
   }

   /**
    * Renders the model in RenderLiving
    */
   protected void renderModel(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
      boolean flag = this.isVisible(entitylivingbaseIn);
      boolean flag1 = !flag && !entitylivingbaseIn.isInvisibleToPlayer(Minecraft.getInstance().player);
      if (flag || flag1) {
         if (!this.bindEntityTexture(entitylivingbaseIn)) {
            return;
         }

         if (flag1) {
            GlStateManager.setProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
         }

         this.field_77045_g.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
         if (flag1) {
            GlStateManager.unsetProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
         }
      }

   }

   protected boolean isVisible(T p_193115_1_) {
      return !p_193115_1_.isInvisible() || this.renderOutlines;
   }

   protected boolean setDoRenderBrightness(T entityLivingBaseIn, float partialTicks) {
      return this.setBrightness(entityLivingBaseIn, partialTicks, true);
   }

   protected boolean setBrightness(T entitylivingbaseIn, float partialTicks, boolean combineTextures) {
      float f = entitylivingbaseIn.getBrightness();
      int i = this.getColorMultiplier(entitylivingbaseIn, f, partialTicks);
      boolean flag = (i >> 24 & 255) > 0;
      boolean flag1 = entitylivingbaseIn.hurtTime > 0 || entitylivingbaseIn.deathTime > 0;
      if (!flag && !flag1) {
         return false;
      } else if (!flag && !combineTextures) {
         return false;
      } else {
         GlStateManager.activeTexture(GLX.GL_TEXTURE0);
         GlStateManager.enableTexture();
         GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
         GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, 8448);
         GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, GLX.GL_TEXTURE0);
         GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PRIMARY_COLOR);
         GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
         GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
         GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 7681);
         GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, GLX.GL_TEXTURE0);
         GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
         GlStateManager.activeTexture(GLX.GL_TEXTURE1);
         GlStateManager.enableTexture();
         GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
         GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, GLX.GL_INTERPOLATE);
         GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, GLX.GL_CONSTANT);
         GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PREVIOUS);
         GlStateManager.texEnv(8960, GLX.GL_SOURCE2_RGB, GLX.GL_CONSTANT);
         GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
         GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
         GlStateManager.texEnv(8960, GLX.GL_OPERAND2_RGB, 770);
         GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 7681);
         GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, GLX.GL_PREVIOUS);
         GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
         this.brightnessBuffer.position(0);
         if (flag1) {
            this.brightnessBuffer.put(1.0F);
            this.brightnessBuffer.put(0.0F);
            this.brightnessBuffer.put(0.0F);
            this.brightnessBuffer.put(0.3F);
         } else {
            float f1 = (float)(i >> 24 & 255) / 255.0F;
            float f2 = (float)(i >> 16 & 255) / 255.0F;
            float f3 = (float)(i >> 8 & 255) / 255.0F;
            float f4 = (float)(i & 255) / 255.0F;
            this.brightnessBuffer.put(f2);
            this.brightnessBuffer.put(f3);
            this.brightnessBuffer.put(f4);
            this.brightnessBuffer.put(1.0F - f1);
         }

         this.brightnessBuffer.flip();
         GlStateManager.texEnv(8960, 8705, this.brightnessBuffer);
         GlStateManager.activeTexture(GLX.GL_TEXTURE2);
         GlStateManager.enableTexture();
         GlStateManager.bindTexture(TEXTURE_BRIGHTNESS.getGlTextureId());
         GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
         GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, 8448);
         GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, GLX.GL_PREVIOUS);
         GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_TEXTURE1);
         GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
         GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
         GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 7681);
         GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, GLX.GL_PREVIOUS);
         GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
         GlStateManager.activeTexture(GLX.GL_TEXTURE0);
         return true;
      }
   }

   protected void unsetBrightness() {
      GlStateManager.activeTexture(GLX.GL_TEXTURE0);
      GlStateManager.enableTexture();
      GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
      GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, 8448);
      GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, GLX.GL_TEXTURE0);
      GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PRIMARY_COLOR);
      GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
      GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
      GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 8448);
      GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, GLX.GL_TEXTURE0);
      GlStateManager.texEnv(8960, GLX.GL_SOURCE1_ALPHA, GLX.GL_PRIMARY_COLOR);
      GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
      GlStateManager.texEnv(8960, GLX.GL_OPERAND1_ALPHA, 770);
      GlStateManager.activeTexture(GLX.GL_TEXTURE1);
      GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
      GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, 8448);
      GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
      GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
      GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, 5890);
      GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PREVIOUS);
      GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 8448);
      GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
      GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, 5890);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.activeTexture(GLX.GL_TEXTURE2);
      GlStateManager.disableTexture();
      GlStateManager.bindTexture(0);
      GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
      GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, 8448);
      GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
      GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
      GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, 5890);
      GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PREVIOUS);
      GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 8448);
      GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
      GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, 5890);
      GlStateManager.activeTexture(GLX.GL_TEXTURE0);
   }

   /**
    * Sets a simple glTranslate on a LivingEntity.
    */
   protected void renderLivingAt(T entityLivingBaseIn, double x, double y, double z) {
      if (entityLivingBaseIn.getPose() == Pose.SLEEPING) {
         Direction direction = entityLivingBaseIn.func_213376_dz();
         if (direction != null) {
            float f = entityLivingBaseIn.getEyeHeight(Pose.STANDING) - 0.1F;
            GlStateManager.translatef((float)x - (float)direction.getXOffset() * f, (float)y, (float)z - (float)direction.getZOffset() * f);
            return;
         }
      }

      GlStateManager.translatef((float)x, (float)y, (float)z);
   }

   private static float func_217765_a(Direction p_217765_0_) {
      switch(p_217765_0_) {
      case SOUTH:
         return 90.0F;
      case WEST:
         return 0.0F;
      case NORTH:
         return 270.0F;
      case EAST:
         return 180.0F;
      default:
         return 0.0F;
      }
   }

   protected void applyRotations(T entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      Pose pose = entityLiving.getPose();
      if (pose != Pose.SLEEPING) {
         GlStateManager.rotatef(180.0F - rotationYaw, 0.0F, 1.0F, 0.0F);
      }

      if (entityLiving.deathTime > 0) {
         float f = ((float)entityLiving.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
         f = MathHelper.sqrt(f);
         if (f > 1.0F) {
            f = 1.0F;
         }

         GlStateManager.rotatef(f * this.getDeathMaxRotation(entityLiving), 0.0F, 0.0F, 1.0F);
      } else if (entityLiving.isSpinAttacking()) {
         GlStateManager.rotatef(-90.0F - entityLiving.rotationPitch, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(((float)entityLiving.ticksExisted + partialTicks) * -75.0F, 0.0F, 1.0F, 0.0F);
      } else if (pose == Pose.SLEEPING) {
         Direction direction = entityLiving.func_213376_dz();
         GlStateManager.rotatef(direction != null ? func_217765_a(direction) : rotationYaw, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(this.getDeathMaxRotation(entityLiving), 0.0F, 0.0F, 1.0F);
         GlStateManager.rotatef(270.0F, 0.0F, 1.0F, 0.0F);
      } else if (entityLiving.hasCustomName() || entityLiving instanceof PlayerEntity) {
         String s = TextFormatting.getTextWithoutFormattingCodes(entityLiving.getName().getString());
         if (s != null && ("Dinnerbone".equals(s) || "Grumm".equals(s)) && (!(entityLiving instanceof PlayerEntity) || ((PlayerEntity)entityLiving).isWearing(PlayerModelPart.CAPE))) {
            GlStateManager.translatef(0.0F, entityLiving.getHeight() + 0.1F, 0.0F);
            GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
         }
      }

   }

   /**
    * Returns where in the swing animation the living entity is (from 0 to 1).  Args : entity, partialTickTime
    */
   protected float getSwingProgress(T livingBase, float partialTickTime) {
      return livingBase.getSwingProgress(partialTickTime);
   }

   /**
    * Defines what float the third param in setRotationAngles of ModelBase is
    */
   protected float handleRotationFloat(T livingBase, float partialTicks) {
      return (float)livingBase.ticksExisted + partialTicks;
   }

   protected void renderLayers(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scaleIn) {
      for(LayerRenderer<T, M> layerrenderer : this.layerRenderers) {
         boolean flag = this.setBrightness(entitylivingbaseIn, partialTicks, layerrenderer.shouldCombineTextures());
         layerrenderer.render(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scaleIn);
         if (flag) {
            this.unsetBrightness();
         }
      }

   }

   protected float getDeathMaxRotation(T entityLivingBaseIn) {
      return 90.0F;
   }

   /**
    * Gets an RGBA int color multiplier to apply.
    */
   protected int getColorMultiplier(T entitylivingbaseIn, float lightBrightness, float partialTickTime) {
      return 0;
   }

   /**
    * Allows the render to do state modifications necessary before the model is rendered.
    */
   protected void preRenderCallback(T entitylivingbaseIn, float partialTickTime) {
   }

   public void renderName(T entity, double x, double y, double z) {
      if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderLivingEvent.Specials.Pre<T, M>(entity, this, x, y, z))) return;
      if (this.canRenderName(entity)) {
         double d0 = entity.getDistanceSq(this.renderManager.info.getProjectedView());
         float f = entity.func_213287_bg() ? 32.0F : 64.0F;
         if (!(d0 >= (double)(f * f))) {
            String s = entity.getDisplayName().getFormattedText();
            GlStateManager.alphaFunc(516, 0.1F);
            this.renderEntityName(entity, x, y, z, s, d0);
         }
      }
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderLivingEvent.Specials.Post<T, M>(entity, this, x, y, z));
   }

   protected boolean canRenderName(T entity) {
      ClientPlayerEntity clientplayerentity = Minecraft.getInstance().player;
      boolean flag = !entity.isInvisibleToPlayer(clientplayerentity);
      if (entity != clientplayerentity) {
         Team team = entity.getTeam();
         Team team1 = clientplayerentity.getTeam();
         if (team != null) {
            Team.Visible team$visible = team.getNameTagVisibility();
            switch(team$visible) {
            case ALWAYS:
               return flag;
            case NEVER:
               return false;
            case HIDE_FOR_OTHER_TEAMS:
               return team1 == null ? flag : team.isSameTeam(team1) && (team.getSeeFriendlyInvisiblesEnabled() || flag);
            case HIDE_FOR_OWN_TEAM:
               return team1 == null ? flag : !team.isSameTeam(team1) && flag;
            default:
               return true;
            }
         }
      }

      return Minecraft.isGuiEnabled() && entity != this.renderManager.info.func_216773_g() && flag && !entity.isBeingRidden();
   }
}