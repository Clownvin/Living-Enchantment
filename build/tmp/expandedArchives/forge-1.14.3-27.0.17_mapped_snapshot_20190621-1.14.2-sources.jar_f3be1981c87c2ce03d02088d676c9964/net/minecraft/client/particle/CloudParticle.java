package net.minecraft.client.particle;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CloudParticle extends SpriteTexturedParticle {
   private final IAnimatedSprite field_217583_C;

   private CloudParticle(World p_i51015_1_, double p_i51015_2_, double p_i51015_4_, double p_i51015_6_, double p_i51015_8_, double p_i51015_10_, double p_i51015_12_, IAnimatedSprite p_i51015_14_) {
      super(p_i51015_1_, p_i51015_2_, p_i51015_4_, p_i51015_6_, 0.0D, 0.0D, 0.0D);
      this.field_217583_C = p_i51015_14_;
      float f = 2.5F;
      this.motionX *= (double)0.1F;
      this.motionY *= (double)0.1F;
      this.motionZ *= (double)0.1F;
      this.motionX += p_i51015_8_;
      this.motionY += p_i51015_10_;
      this.motionZ += p_i51015_12_;
      float f1 = 1.0F - (float)(Math.random() * (double)0.3F);
      this.particleRed = f1;
      this.particleGreen = f1;
      this.particleBlue = f1;
      this.particleScale *= 1.875F;
      int i = (int)(8.0D / (Math.random() * 0.8D + 0.3D));
      this.maxAge = (int)Math.max((float)i * 2.5F, 1.0F);
      this.canCollide = false;
      this.func_217566_b(p_i51015_14_);
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public float func_217561_b(float p_217561_1_) {
      return this.particleScale * MathHelper.clamp(((float)this.age + p_217561_1_) / (float)this.maxAge * 32.0F, 0.0F, 1.0F);
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.age++ >= this.maxAge) {
         this.setExpired();
      } else {
         this.func_217566_b(this.field_217583_C);
         this.move(this.motionX, this.motionY, this.motionZ);
         this.motionX *= (double)0.96F;
         this.motionY *= (double)0.96F;
         this.motionZ *= (double)0.96F;
         PlayerEntity playerentity = this.world.getClosestPlayer(this.posX, this.posY, this.posZ, 2.0D, false);
         if (playerentity != null) {
            AxisAlignedBB axisalignedbb = playerentity.getBoundingBox();
            if (this.posY > axisalignedbb.minY) {
               this.posY += (axisalignedbb.minY - this.posY) * 0.2D;
               this.motionY += (playerentity.getMotion().y - this.motionY) * 0.2D;
               this.setPosition(this.posX, this.posY, this.posZ);
            }
         }

         if (this.onGround) {
            this.motionX *= (double)0.7F;
            this.motionZ *= (double)0.7F;
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite field_217538_a;

      public Factory(IAnimatedSprite p_i50630_1_) {
         this.field_217538_a = p_i50630_1_;
      }

      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new CloudParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.field_217538_a);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class SneezeFactory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite field_217539_a;

      public SneezeFactory(IAnimatedSprite p_i50629_1_) {
         this.field_217539_a = p_i50629_1_;
      }

      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         Particle particle = new CloudParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.field_217539_a);
         particle.setColor(200.0F, 50.0F, 120.0F);
         particle.setAlphaF(0.4F);
         return particle;
      }
   }
}