package net.minecraft.client.particle;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.ElderGuardianEntity;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MobAppearanceParticle extends Particle {
   private LivingEntity entity;

   private MobAppearanceParticle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn);
      this.particleGravity = 0.0F;
      this.maxAge = 30;
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.CUSTOM;
   }

   public void tick() {
      super.tick();
      if (this.entity == null) {
         ElderGuardianEntity elderguardianentity = EntityType.ELDER_GUARDIAN.create(this.world);
         elderguardianentity.setGhost();
         this.entity = elderguardianentity;
      }

   }

   /**
    * Renders the particle
    */
   public void renderParticle(BufferBuilder buffer, ActiveRenderInfo entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
      if (this.entity != null) {
         EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
         entityrenderermanager.setRenderPosition(Particle.interpPosX, Particle.interpPosY, Particle.interpPosZ);
         float f = 1.0F / ElderGuardianEntity.field_213629_b;
         float f1 = ((float)this.age + partialTicks) / (float)this.maxAge;
         GlStateManager.depthMask(true);
         GlStateManager.enableBlend();
         GlStateManager.enableDepthTest();
         GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
         float f2 = 240.0F;
         GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 240.0F, 240.0F);
         GlStateManager.pushMatrix();
         float f3 = 0.05F + 0.5F * MathHelper.sin(f1 * (float)Math.PI);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, f3);
         GlStateManager.translatef(0.0F, 1.8F, 0.0F);
         GlStateManager.rotatef(180.0F - entityIn.getYaw(), 0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(60.0F - 150.0F * f1 - entityIn.getPitch(), 1.0F, 0.0F, 0.0F);
         GlStateManager.translatef(0.0F, -0.4F, -1.5F);
         GlStateManager.scalef(f, f, f);
         this.entity.rotationYaw = 0.0F;
         this.entity.rotationYawHead = 0.0F;
         this.entity.prevRotationYaw = 0.0F;
         this.entity.prevRotationYawHead = 0.0F;
         entityrenderermanager.renderEntity(this.entity, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks, false);
         GlStateManager.popMatrix();
         GlStateManager.enableDepthTest();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new MobAppearanceParticle(worldIn, x, y, z);
      }
   }
}