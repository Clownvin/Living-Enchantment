package net.minecraft.client.particle;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class TexturedParticle extends Particle {
   protected float particleScale = 0.1F * (this.rand.nextFloat() * 0.5F + 0.5F) * 2.0F;

   protected TexturedParticle(World p_i51011_1_, double p_i51011_2_, double p_i51011_4_, double p_i51011_6_) {
      super(p_i51011_1_, p_i51011_2_, p_i51011_4_, p_i51011_6_);
   }

   protected TexturedParticle(World p_i51012_1_, double p_i51012_2_, double p_i51012_4_, double p_i51012_6_, double p_i51012_8_, double p_i51012_10_, double p_i51012_12_) {
      super(p_i51012_1_, p_i51012_2_, p_i51012_4_, p_i51012_6_, p_i51012_8_, p_i51012_10_, p_i51012_12_);
   }

   /**
    * Renders the particle
    */
   public void renderParticle(BufferBuilder buffer, ActiveRenderInfo entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
      float f = this.func_217561_b(partialTicks);
      float f1 = this.func_217563_c();
      float f2 = this.func_217564_d();
      float f3 = this.func_217562_e();
      float f4 = this.func_217560_f();
      float f5 = (float)(MathHelper.lerp((double)partialTicks, this.prevPosX, this.posX) - interpPosX);
      float f6 = (float)(MathHelper.lerp((double)partialTicks, this.prevPosY, this.posY) - interpPosY);
      float f7 = (float)(MathHelper.lerp((double)partialTicks, this.prevPosZ, this.posZ) - interpPosZ);
      int i = this.getBrightnessForRender(partialTicks);
      int j = i >> 16 & '\uffff';
      int k = i & '\uffff';
      Vec3d[] avec3d = new Vec3d[]{new Vec3d((double)(-rotationX * f - rotationXY * f), (double)(-rotationZ * f), (double)(-rotationYZ * f - rotationXZ * f)), new Vec3d((double)(-rotationX * f + rotationXY * f), (double)(rotationZ * f), (double)(-rotationYZ * f + rotationXZ * f)), new Vec3d((double)(rotationX * f + rotationXY * f), (double)(rotationZ * f), (double)(rotationYZ * f + rotationXZ * f)), new Vec3d((double)(rotationX * f - rotationXY * f), (double)(-rotationZ * f), (double)(rotationYZ * f - rotationXZ * f))};
      if (this.particleAngle != 0.0F) {
         float f8 = MathHelper.lerp(partialTicks, this.prevParticleAngle, this.particleAngle);
         float f9 = MathHelper.cos(f8 * 0.5F);
         float f10 = (float)((double)MathHelper.sin(f8 * 0.5F) * entityIn.func_216787_l().x);
         float f11 = (float)((double)MathHelper.sin(f8 * 0.5F) * entityIn.func_216787_l().y);
         float f12 = (float)((double)MathHelper.sin(f8 * 0.5F) * entityIn.func_216787_l().z);
         Vec3d vec3d = new Vec3d((double)f10, (double)f11, (double)f12);

         for(int l = 0; l < 4; ++l) {
            avec3d[l] = vec3d.scale(2.0D * avec3d[l].dotProduct(vec3d)).add(avec3d[l].scale((double)(f9 * f9) - vec3d.dotProduct(vec3d))).add(vec3d.crossProduct(avec3d[l]).scale((double)(2.0F * f9)));
         }
      }

      buffer.pos((double)f5 + avec3d[0].x, (double)f6 + avec3d[0].y, (double)f7 + avec3d[0].z).tex((double)f2, (double)f4).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
      buffer.pos((double)f5 + avec3d[1].x, (double)f6 + avec3d[1].y, (double)f7 + avec3d[1].z).tex((double)f2, (double)f3).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
      buffer.pos((double)f5 + avec3d[2].x, (double)f6 + avec3d[2].y, (double)f7 + avec3d[2].z).tex((double)f1, (double)f3).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
      buffer.pos((double)f5 + avec3d[3].x, (double)f6 + avec3d[3].y, (double)f7 + avec3d[3].z).tex((double)f1, (double)f4).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
   }

   public float func_217561_b(float p_217561_1_) {
      return this.particleScale;
   }

   public Particle multipleParticleScaleBy(float scale) {
      this.particleScale *= scale;
      return super.multipleParticleScaleBy(scale);
   }

   protected abstract float func_217563_c();

   protected abstract float func_217564_d();

   protected abstract float func_217562_e();

   protected abstract float func_217560_f();
}