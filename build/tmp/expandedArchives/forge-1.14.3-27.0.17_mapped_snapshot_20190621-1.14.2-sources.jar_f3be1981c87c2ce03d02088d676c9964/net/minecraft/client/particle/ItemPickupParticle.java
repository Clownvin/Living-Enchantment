package net.minecraft.client.particle;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemPickupParticle extends Particle {
   private final Entity item;
   private final Entity target;
   private int age;
   private final int maxAge;
   private final float yOffset;
   private final EntityRendererManager renderManager = Minecraft.getInstance().getRenderManager();

   public ItemPickupParticle(World worldIn, Entity entityIn, Entity targetEntityIn, float yOffsetIn) {
      this(worldIn, entityIn, targetEntityIn, yOffsetIn, entityIn.getMotion());
   }

   private ItemPickupParticle(World p_i51025_1_, Entity p_i51025_2_, Entity p_i51025_3_, float p_i51025_4_, Vec3d p_i51025_5_) {
      super(p_i51025_1_, p_i51025_2_.posX, p_i51025_2_.posY, p_i51025_2_.posZ, p_i51025_5_.x, p_i51025_5_.y, p_i51025_5_.z);
      this.item = p_i51025_2_;
      this.target = p_i51025_3_;
      this.maxAge = 3;
      this.yOffset = p_i51025_4_;
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.CUSTOM;
   }

   /**
    * Renders the particle
    */
   public void renderParticle(BufferBuilder buffer, ActiveRenderInfo entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
      float f = ((float)this.age + partialTicks) / (float)this.maxAge;
      f = f * f;
      double d0 = this.item.posX;
      double d1 = this.item.posY;
      double d2 = this.item.posZ;
      double d3 = MathHelper.lerp((double)partialTicks, this.target.lastTickPosX, this.target.posX);
      double d4 = MathHelper.lerp((double)partialTicks, this.target.lastTickPosY, this.target.posY) + (double)this.yOffset;
      double d5 = MathHelper.lerp((double)partialTicks, this.target.lastTickPosZ, this.target.posZ);
      double d6 = MathHelper.lerp((double)f, d0, d3);
      double d7 = MathHelper.lerp((double)f, d1, d4);
      double d8 = MathHelper.lerp((double)f, d2, d5);
      int i = this.getBrightnessForRender(partialTicks);
      int j = i % 65536;
      int k = i / 65536;
      GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)j, (float)k);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      d6 = d6 - interpPosX;
      d7 = d7 - interpPosY;
      d8 = d8 - interpPosZ;
      GlStateManager.enableLighting();
      this.renderManager.renderEntity(this.item, d6, d7, d8, this.item.rotationYaw, partialTicks, false);
   }

   public void tick() {
      ++this.age;
      if (this.age == this.maxAge) {
         this.setExpired();
      }

   }
}