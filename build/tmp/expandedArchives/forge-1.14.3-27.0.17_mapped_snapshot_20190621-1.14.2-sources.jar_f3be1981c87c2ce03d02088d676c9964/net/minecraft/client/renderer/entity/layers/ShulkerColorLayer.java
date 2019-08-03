package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.ShulkerRenderer;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.entity.model.ShulkerModel;
import net.minecraft.entity.monster.ShulkerEntity;
import net.minecraft.item.DyeColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ShulkerColorLayer extends LayerRenderer<ShulkerEntity, ShulkerModel<ShulkerEntity>> {
   public ShulkerColorLayer(IEntityRenderer<ShulkerEntity, ShulkerModel<ShulkerEntity>> p_i50924_1_) {
      super(p_i50924_1_);
   }

   public void render(ShulkerEntity entityIn, float p_212842_2_, float p_212842_3_, float p_212842_4_, float p_212842_5_, float p_212842_6_, float p_212842_7_, float p_212842_8_) {
      GlStateManager.pushMatrix();
      switch(entityIn.getAttachmentFacing()) {
      case DOWN:
      default:
         break;
      case EAST:
         GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.translatef(1.0F, -1.0F, 0.0F);
         GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
         break;
      case WEST:
         GlStateManager.rotatef(-90.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.translatef(-1.0F, -1.0F, 0.0F);
         GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
         break;
      case NORTH:
         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.translatef(0.0F, -1.0F, -1.0F);
         break;
      case SOUTH:
         GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.translatef(0.0F, -1.0F, 1.0F);
         break;
      case UP:
         GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.translatef(0.0F, -2.0F, 0.0F);
      }

      RendererModel renderermodel = this.getEntityModel().getHead();
      renderermodel.rotateAngleY = p_212842_6_ * ((float)Math.PI / 180F);
      renderermodel.rotateAngleX = p_212842_7_ * ((float)Math.PI / 180F);
      DyeColor dyecolor = entityIn.getColor();
      if (dyecolor == null) {
         this.bindTexture(ShulkerRenderer.field_204402_a);
      } else {
         this.bindTexture(ShulkerRenderer.SHULKER_ENDERGOLEM_TEXTURE[dyecolor.getId()]);
      }

      renderermodel.render(p_212842_8_);
      GlStateManager.popMatrix();
   }

   public boolean shouldCombineTextures() {
      return false;
   }
}