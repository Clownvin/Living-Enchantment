package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.Deadmau5HeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.layers.ParrotVariantLayer;
import net.minecraft.client.renderer.entity.layers.SpinAttackEffectLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlayerRenderer extends LivingRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {
   public PlayerRenderer(EntityRendererManager renderManager) {
      this(renderManager, false);
   }

   public PlayerRenderer(EntityRendererManager renderManager, boolean useSmallArms) {
      super(renderManager, new PlayerModel<>(0.0F, useSmallArms), 0.5F);
      this.addLayer(new BipedArmorLayer<>(this, new BipedModel(0.5F), new BipedModel(1.0F)));
      this.addLayer(new HeldItemLayer<>(this));
      this.addLayer(new ArrowLayer<>(this));
      this.addLayer(new Deadmau5HeadLayer(this));
      this.addLayer(new CapeLayer(this));
      this.addLayer(new HeadLayer<>(this));
      this.addLayer(new ElytraLayer<>(this));
      this.addLayer(new ParrotVariantLayer<>(this));
      this.addLayer(new SpinAttackEffectLayer<>(this));
   }

   /**
    * Renders the desired {@code T} type Entity.
    */
   public void doRender(AbstractClientPlayerEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
      if (!entity.isUser() || this.renderManager.info != null && this.renderManager.info.func_216773_g() == entity) {
         double d0 = y;
      if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderPlayerEvent.Pre(entity, this, partialTicks, x, y, z))) return;
         if (entity.func_213287_bg()) {
            d0 = y - 0.125D;
         }

         this.setModelVisibilities(entity);
         GlStateManager.setProfile(GlStateManager.Profile.PLAYER_SKIN);
         super.doRender(entity, x, d0, z, entityYaw, partialTicks);
         GlStateManager.unsetProfile(GlStateManager.Profile.PLAYER_SKIN);
      }
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderPlayerEvent.Post(entity, this, partialTicks, x, y, z));
   }

   private void setModelVisibilities(AbstractClientPlayerEntity clientPlayer) {
      PlayerModel<AbstractClientPlayerEntity> playermodel = this.getEntityModel();
      if (clientPlayer.isSpectator()) {
         playermodel.setVisible(false);
         playermodel.field_78116_c.showModel = true;
         playermodel.bipedHeadwear.showModel = true;
      } else {
         ItemStack itemstack = clientPlayer.getHeldItemMainhand();
         ItemStack itemstack1 = clientPlayer.getHeldItemOffhand();
         playermodel.setVisible(true);
         playermodel.bipedHeadwear.showModel = clientPlayer.isWearing(PlayerModelPart.HAT);
         playermodel.bipedBodyWear.showModel = clientPlayer.isWearing(PlayerModelPart.JACKET);
         playermodel.bipedLeftLegwear.showModel = clientPlayer.isWearing(PlayerModelPart.LEFT_PANTS_LEG);
         playermodel.bipedRightLegwear.showModel = clientPlayer.isWearing(PlayerModelPart.RIGHT_PANTS_LEG);
         playermodel.bipedLeftArmwear.showModel = clientPlayer.isWearing(PlayerModelPart.LEFT_SLEEVE);
         playermodel.bipedRightArmwear.showModel = clientPlayer.isWearing(PlayerModelPart.RIGHT_SLEEVE);
         playermodel.isSneak = clientPlayer.func_213287_bg();
         BipedModel.ArmPose bipedmodel$armpose = this.func_217766_a(clientPlayer, itemstack, itemstack1, Hand.MAIN_HAND);
         BipedModel.ArmPose bipedmodel$armpose1 = this.func_217766_a(clientPlayer, itemstack, itemstack1, Hand.OFF_HAND);
         if (clientPlayer.getPrimaryHand() == HandSide.RIGHT) {
            playermodel.rightArmPose = bipedmodel$armpose;
            playermodel.leftArmPose = bipedmodel$armpose1;
         } else {
            playermodel.rightArmPose = bipedmodel$armpose1;
            playermodel.leftArmPose = bipedmodel$armpose;
         }
      }

   }

   private BipedModel.ArmPose func_217766_a(AbstractClientPlayerEntity p_217766_1_, ItemStack p_217766_2_, ItemStack p_217766_3_, Hand p_217766_4_) {
      BipedModel.ArmPose bipedmodel$armpose = BipedModel.ArmPose.EMPTY;
      ItemStack itemstack = p_217766_4_ == Hand.MAIN_HAND ? p_217766_2_ : p_217766_3_;
      if (!itemstack.isEmpty()) {
         bipedmodel$armpose = BipedModel.ArmPose.ITEM;
         if (p_217766_1_.getItemInUseCount() > 0) {
            UseAction useaction = itemstack.getUseAction();
            if (useaction == UseAction.BLOCK) {
               bipedmodel$armpose = BipedModel.ArmPose.BLOCK;
            } else if (useaction == UseAction.BOW) {
               bipedmodel$armpose = BipedModel.ArmPose.BOW_AND_ARROW;
            } else if (useaction == UseAction.SPEAR) {
               bipedmodel$armpose = BipedModel.ArmPose.THROW_SPEAR;
            } else if (useaction == UseAction.CROSSBOW && p_217766_4_ == p_217766_1_.getActiveHand()) {
               bipedmodel$armpose = BipedModel.ArmPose.CROSSBOW_CHARGE;
            }
         } else {
            boolean flag3 = p_217766_2_.getItem() == Items.CROSSBOW;
            boolean flag = CrossbowItem.isCharged(p_217766_2_);
            boolean flag1 = p_217766_3_.getItem() == Items.CROSSBOW;
            boolean flag2 = CrossbowItem.isCharged(p_217766_3_);
            if (flag3 && flag) {
               bipedmodel$armpose = BipedModel.ArmPose.CROSSBOW_HOLD;
            }

            if (flag1 && flag2 && p_217766_2_.getItem().getUseAction(p_217766_2_) == UseAction.NONE) {
               bipedmodel$armpose = BipedModel.ArmPose.CROSSBOW_HOLD;
            }
         }
      }

      return bipedmodel$armpose;
   }

   /**
    * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
    */
   public ResourceLocation getEntityTexture(AbstractClientPlayerEntity entity) {
      return entity.getLocationSkin();
   }

   /**
    * Allows the render to do state modifications necessary before the model is rendered.
    */
   protected void preRenderCallback(AbstractClientPlayerEntity entitylivingbaseIn, float partialTickTime) {
      float f = 0.9375F;
      GlStateManager.scalef(0.9375F, 0.9375F, 0.9375F);
   }

   protected void renderEntityName(AbstractClientPlayerEntity entityIn, double x, double y, double z, String name, double distanceSq) {
      if (distanceSq < 100.0D) {
         Scoreboard scoreboard = entityIn.getWorldScoreboard();
         ScoreObjective scoreobjective = scoreboard.getObjectiveInDisplaySlot(2);
         if (scoreobjective != null) {
            Score score = scoreboard.getOrCreateScore(entityIn.getScoreboardName(), scoreobjective);
            this.renderLivingLabel(entityIn, score.getScorePoints() + " " + scoreobjective.getDisplayName().getFormattedText(), x, y, z, 64);
            y += (double)(9.0F * 1.15F * 0.025F);
         }
      }

      super.renderEntityName(entityIn, x, y, z, name, distanceSq);
   }

   public void renderRightArm(AbstractClientPlayerEntity clientPlayer) {
      float f = 1.0F;
      GlStateManager.color3f(1.0F, 1.0F, 1.0F);
      float f1 = 0.0625F;
      PlayerModel<AbstractClientPlayerEntity> playermodel = this.getEntityModel();
      this.setModelVisibilities(clientPlayer);
      GlStateManager.enableBlend();
      playermodel.swingProgress = 0.0F;
      playermodel.isSneak = false;
      playermodel.field_205061_a = 0.0F;
      playermodel.setRotationAngles(clientPlayer, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
      playermodel.bipedRightArm.rotateAngleX = 0.0F;
      playermodel.bipedRightArm.render(0.0625F);
      playermodel.bipedRightArmwear.rotateAngleX = 0.0F;
      playermodel.bipedRightArmwear.render(0.0625F);
      GlStateManager.disableBlend();
   }

   public void renderLeftArm(AbstractClientPlayerEntity clientPlayer) {
      float f = 1.0F;
      GlStateManager.color3f(1.0F, 1.0F, 1.0F);
      float f1 = 0.0625F;
      PlayerModel<AbstractClientPlayerEntity> playermodel = this.getEntityModel();
      this.setModelVisibilities(clientPlayer);
      GlStateManager.enableBlend();
      playermodel.isSneak = false;
      playermodel.swingProgress = 0.0F;
      playermodel.field_205061_a = 0.0F;
      playermodel.setRotationAngles(clientPlayer, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
      playermodel.bipedLeftArm.rotateAngleX = 0.0F;
      playermodel.bipedLeftArm.render(0.0625F);
      playermodel.bipedLeftArmwear.rotateAngleX = 0.0F;
      playermodel.bipedLeftArmwear.render(0.0625F);
      GlStateManager.disableBlend();
   }

   protected void applyRotations(AbstractClientPlayerEntity entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      float f = entityLiving.getSwimAnimation(partialTicks);
      if (entityLiving.isElytraFlying()) {
         super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
         float f1 = (float)entityLiving.getTicksElytraFlying() + partialTicks;
         float f2 = MathHelper.clamp(f1 * f1 / 100.0F, 0.0F, 1.0F);
         if (!entityLiving.isSpinAttacking()) {
            GlStateManager.rotatef(f2 * (-90.0F - entityLiving.rotationPitch), 1.0F, 0.0F, 0.0F);
         }

         Vec3d vec3d = entityLiving.getLook(partialTicks);
         Vec3d vec3d1 = entityLiving.getMotion();
         double d0 = Entity.func_213296_b(vec3d1);
         double d1 = Entity.func_213296_b(vec3d);
         if (d0 > 0.0D && d1 > 0.0D) {
            double d2 = (vec3d1.x * vec3d.x + vec3d1.z * vec3d.z) / (Math.sqrt(d0) * Math.sqrt(d1));
            double d3 = vec3d1.x * vec3d.z - vec3d1.z * vec3d.x;
            GlStateManager.rotatef((float)(Math.signum(d3) * Math.acos(d2)) * 180.0F / (float)Math.PI, 0.0F, 1.0F, 0.0F);
         }
      } else if (f > 0.0F) {
         super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
         float f3 = entityLiving.isInWater() ? -90.0F - entityLiving.rotationPitch : -90.0F;
         float f4 = MathHelper.lerp(f, 0.0F, f3);
         GlStateManager.rotatef(f4, 1.0F, 0.0F, 0.0F);
         if (entityLiving.func_213314_bj()) {
            GlStateManager.translatef(0.0F, -1.0F, 0.3F);
         }
      } else {
         super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
      }

   }
}