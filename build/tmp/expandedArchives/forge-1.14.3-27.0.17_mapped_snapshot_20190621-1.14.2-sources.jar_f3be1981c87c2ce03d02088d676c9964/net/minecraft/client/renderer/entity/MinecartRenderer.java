package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.MinecartModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MinecartRenderer<T extends AbstractMinecartEntity> extends EntityRenderer<T> {
   private static final ResourceLocation MINECART_TEXTURES = new ResourceLocation("textures/entity/minecart.png");
   protected final EntityModel<T> field_77013_a = new MinecartModel<>();

   public MinecartRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn);
      this.shadowSize = 0.7F;
   }

   /**
    * Renders the desired {@code T} type Entity.
    */
   public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
      GlStateManager.pushMatrix();
      this.bindEntityTexture(entity);
      long i = (long)entity.getEntityId() * 493286711L;
      i = i * i * 4392167121L + i * 98761L;
      float f = (((float)(i >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
      float f1 = (((float)(i >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
      float f2 = (((float)(i >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
      GlStateManager.translatef(f, f1, f2);
      double d0 = MathHelper.lerp((double)partialTicks, entity.lastTickPosX, entity.posX);
      double d1 = MathHelper.lerp((double)partialTicks, entity.lastTickPosY, entity.posY);
      double d2 = MathHelper.lerp((double)partialTicks, entity.lastTickPosZ, entity.posZ);
      double d3 = (double)0.3F;
      Vec3d vec3d = entity.getPos(d0, d1, d2);
      float f3 = MathHelper.lerp(partialTicks, entity.prevRotationPitch, entity.rotationPitch);
      if (vec3d != null) {
         Vec3d vec3d1 = entity.getPosOffset(d0, d1, d2, (double)0.3F);
         Vec3d vec3d2 = entity.getPosOffset(d0, d1, d2, (double)-0.3F);
         if (vec3d1 == null) {
            vec3d1 = vec3d;
         }

         if (vec3d2 == null) {
            vec3d2 = vec3d;
         }

         x += vec3d.x - d0;
         y += (vec3d1.y + vec3d2.y) / 2.0D - d1;
         z += vec3d.z - d2;
         Vec3d vec3d3 = vec3d2.add(-vec3d1.x, -vec3d1.y, -vec3d1.z);
         if (vec3d3.length() != 0.0D) {
            vec3d3 = vec3d3.normalize();
            entityYaw = (float)(Math.atan2(vec3d3.z, vec3d3.x) * 180.0D / Math.PI);
            f3 = (float)(Math.atan(vec3d3.y) * 73.0D);
         }
      }

      GlStateManager.translatef((float)x, (float)y + 0.375F, (float)z);
      GlStateManager.rotatef(180.0F - entityYaw, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(-f3, 0.0F, 0.0F, 1.0F);
      float f5 = (float)entity.getRollingAmplitude() - partialTicks;
      float f6 = entity.getDamage() - partialTicks;
      if (f6 < 0.0F) {
         f6 = 0.0F;
      }

      if (f5 > 0.0F) {
         GlStateManager.rotatef(MathHelper.sin(f5) * f5 * f6 / 10.0F * (float)entity.getRollingDirection(), 1.0F, 0.0F, 0.0F);
      }

      int j = entity.getDisplayTileOffset();
      if (this.renderOutlines) {
         GlStateManager.enableColorMaterial();
         GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(entity));
      }

      BlockState blockstate = entity.getDisplayTile();
      if (blockstate.getRenderType() != BlockRenderType.INVISIBLE) {
         GlStateManager.pushMatrix();
         this.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
         float f4 = 0.75F;
         GlStateManager.scalef(0.75F, 0.75F, 0.75F);
         GlStateManager.translatef(-0.5F, (float)(j - 8) / 16.0F, 0.5F);
         this.renderCartContents(entity, partialTicks, blockstate);
         GlStateManager.popMatrix();
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.bindEntityTexture(entity);
      }

      GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
      this.field_77013_a.render(entity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
      GlStateManager.popMatrix();
      if (this.renderOutlines) {
         GlStateManager.tearDownSolidRenderingTextureCombine();
         GlStateManager.disableColorMaterial();
      }

      super.doRender(entity, x, y, z, entityYaw, partialTicks);
   }

   /**
    * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
    */
   protected ResourceLocation getEntityTexture(T entity) {
      return MINECART_TEXTURES;
   }

   protected void renderCartContents(T cart, float partialTicks, BlockState contents) {
      GlStateManager.pushMatrix();
      Minecraft.getInstance().getBlockRendererDispatcher().renderBlockBrightness(contents, cart.getBrightness());
      GlStateManager.popMatrix();
   }
}