package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.entity.item.FireworkRocketEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FireworkRocketRenderer extends EntityRenderer<FireworkRocketEntity> {
   private final net.minecraft.client.renderer.ItemRenderer field_217761_a;

   public FireworkRocketRenderer(EntityRendererManager p_i50970_1_, net.minecraft.client.renderer.ItemRenderer p_i50970_2_) {
      super(p_i50970_1_);
      this.field_217761_a = p_i50970_2_;
   }

   /**
    * Renders the desired {@code T} type Entity.
    */
   public void doRender(FireworkRocketEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)x, (float)y, (float)z);
      GlStateManager.enableRescaleNormal();
      GlStateManager.rotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef((float)(this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
      if (entity.func_213889_i()) {
         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
      } else {
         GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
      }

      this.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
      if (this.renderOutlines) {
         GlStateManager.enableColorMaterial();
         GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(entity));
      }

      this.field_217761_a.renderItem(entity.getItem(), ItemCameraTransforms.TransformType.GROUND);
      if (this.renderOutlines) {
         GlStateManager.tearDownSolidRenderingTextureCombine();
         GlStateManager.disableColorMaterial();
      }

      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
   }

   /**
    * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
    */
   protected ResourceLocation getEntityTexture(FireworkRocketEntity entity) {
      return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
   }
}