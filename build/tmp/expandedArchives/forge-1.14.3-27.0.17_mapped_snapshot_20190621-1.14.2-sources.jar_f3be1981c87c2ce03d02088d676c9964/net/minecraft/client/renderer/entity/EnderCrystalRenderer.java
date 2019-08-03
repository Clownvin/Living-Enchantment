package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.model.EnderCrystalModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnderCrystalRenderer extends EntityRenderer<EnderCrystalEntity> {
   private static final ResourceLocation ENDER_CRYSTAL_TEXTURES = new ResourceLocation("textures/entity/end_crystal/end_crystal.png");
   private final EntityModel<EnderCrystalEntity> modelEnderCrystal = new EnderCrystalModel<>(0.0F, true);
   private final EntityModel<EnderCrystalEntity> modelEnderCrystalNoBase = new EnderCrystalModel<>(0.0F, false);

   public EnderCrystalRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn);
      this.shadowSize = 0.5F;
   }

   /**
    * Renders the desired {@code T} type Entity.
    */
   public void doRender(EnderCrystalEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
      float f = (float)entity.innerRotation + partialTicks;
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)x, (float)y, (float)z);
      this.bindTexture(ENDER_CRYSTAL_TEXTURES);
      float f1 = MathHelper.sin(f * 0.2F) / 2.0F + 0.5F;
      f1 = f1 * f1 + f1;
      if (this.renderOutlines) {
         GlStateManager.enableColorMaterial();
         GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(entity));
      }

      if (entity.shouldShowBottom()) {
         this.modelEnderCrystal.render(entity, 0.0F, f * 3.0F, f1 * 0.2F, 0.0F, 0.0F, 0.0625F);
      } else {
         this.modelEnderCrystalNoBase.render(entity, 0.0F, f * 3.0F, f1 * 0.2F, 0.0F, 0.0F, 0.0625F);
      }

      if (this.renderOutlines) {
         GlStateManager.tearDownSolidRenderingTextureCombine();
         GlStateManager.disableColorMaterial();
      }

      GlStateManager.popMatrix();
      BlockPos blockpos = entity.getBeamTarget();
      if (blockpos != null) {
         this.bindTexture(EnderDragonRenderer.ENDERCRYSTAL_BEAM_TEXTURES);
         float f2 = (float)blockpos.getX() + 0.5F;
         float f3 = (float)blockpos.getY() + 0.5F;
         float f4 = (float)blockpos.getZ() + 0.5F;
         double d0 = (double)f2 - entity.posX;
         double d1 = (double)f3 - entity.posY;
         double d2 = (double)f4 - entity.posZ;
         EnderDragonRenderer.renderCrystalBeams(x + d0, y - 0.3D + (double)(f1 * 0.4F) + d1, z + d2, partialTicks, (double)f2, (double)f3, (double)f4, entity.innerRotation, entity.posX, entity.posY, entity.posZ);
      }

      super.doRender(entity, x, y, z, entityYaw, partialTicks);
   }

   /**
    * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
    */
   protected ResourceLocation getEntityTexture(EnderCrystalEntity entity) {
      return ENDER_CRYSTAL_TEXTURES;
   }

   public boolean shouldRender(EnderCrystalEntity livingEntity, ICamera camera, double camX, double camY, double camZ) {
      return super.shouldRender(livingEntity, camera, camX, camY, camZ) || livingEntity.getBeamTarget() != null;
   }
}