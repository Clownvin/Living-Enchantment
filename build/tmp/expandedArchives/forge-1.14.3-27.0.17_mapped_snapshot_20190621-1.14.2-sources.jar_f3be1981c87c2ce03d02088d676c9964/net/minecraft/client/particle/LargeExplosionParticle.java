package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LargeExplosionParticle extends SpriteTexturedParticle {
   private final IAnimatedSprite field_217582_C;

   private LargeExplosionParticle(World p_i51028_1_, double p_i51028_2_, double p_i51028_4_, double p_i51028_6_, double p_i51028_8_, IAnimatedSprite p_i51028_10_) {
      super(p_i51028_1_, p_i51028_2_, p_i51028_4_, p_i51028_6_, 0.0D, 0.0D, 0.0D);
      this.maxAge = 6 + this.rand.nextInt(4);
      float f = this.rand.nextFloat() * 0.6F + 0.4F;
      this.particleRed = f;
      this.particleGreen = f;
      this.particleBlue = f;
      this.particleScale = 2.0F * (1.0F - (float)p_i51028_8_ * 0.5F);
      this.field_217582_C = p_i51028_10_;
      this.func_217566_b(p_i51028_10_);
   }

   public int getBrightnessForRender(float partialTick) {
      return 15728880;
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.age++ >= this.maxAge) {
         this.setExpired();
      } else {
         this.func_217566_b(this.field_217582_C);
      }
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_LIT;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite field_217531_a;

      public Factory(IAnimatedSprite p_i50634_1_) {
         this.field_217531_a = p_i50634_1_;
      }

      public Particle makeParticle(BasicParticleType typeIn, World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new LargeExplosionParticle(worldIn, x, y, z, xSpeed, this.field_217531_a);
      }
   }
}