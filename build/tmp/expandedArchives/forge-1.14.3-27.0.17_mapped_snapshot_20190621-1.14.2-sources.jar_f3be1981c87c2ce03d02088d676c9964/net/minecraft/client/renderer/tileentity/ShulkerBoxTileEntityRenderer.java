package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.renderer.entity.ShulkerRenderer;
import net.minecraft.client.renderer.entity.model.ShulkerModel;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.ShulkerBoxTileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ShulkerBoxTileEntityRenderer extends TileEntityRenderer<ShulkerBoxTileEntity> {
   private final ShulkerModel<?> model;

   public ShulkerBoxTileEntityRenderer(ShulkerModel<?> modelIn) {
      this.model = modelIn;
   }

   public void render(ShulkerBoxTileEntity tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
      Direction direction = Direction.UP;
      if (tileEntityIn.hasWorld()) {
         BlockState blockstate = this.getWorld().getBlockState(tileEntityIn.getPos());
         if (blockstate.getBlock() instanceof ShulkerBoxBlock) {
            direction = blockstate.get(ShulkerBoxBlock.FACING);
         }
      }

      GlStateManager.enableDepthTest();
      GlStateManager.depthFunc(515);
      GlStateManager.depthMask(true);
      GlStateManager.disableCull();
      if (destroyStage >= 0) {
         this.bindTexture(DESTROY_STAGES[destroyStage]);
         GlStateManager.matrixMode(5890);
         GlStateManager.pushMatrix();
         GlStateManager.scalef(4.0F, 4.0F, 1.0F);
         GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
         GlStateManager.matrixMode(5888);
      } else {
         DyeColor dyecolor = tileEntityIn.getColor();
         if (dyecolor == null) {
            this.bindTexture(ShulkerRenderer.field_204402_a);
         } else {
            this.bindTexture(ShulkerRenderer.SHULKER_ENDERGOLEM_TEXTURE[dyecolor.getId()]);
         }
      }

      GlStateManager.pushMatrix();
      GlStateManager.enableRescaleNormal();
      if (destroyStage < 0) {
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      }

      GlStateManager.translatef((float)x + 0.5F, (float)y + 1.5F, (float)z + 0.5F);
      GlStateManager.scalef(1.0F, -1.0F, -1.0F);
      GlStateManager.translatef(0.0F, 1.0F, 0.0F);
      float f = 0.9995F;
      GlStateManager.scalef(0.9995F, 0.9995F, 0.9995F);
      GlStateManager.translatef(0.0F, -1.0F, 0.0F);
      switch(direction) {
      case DOWN:
         GlStateManager.translatef(0.0F, 2.0F, 0.0F);
         GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
      case UP:
      default:
         break;
      case NORTH:
         GlStateManager.translatef(0.0F, 1.0F, 1.0F);
         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
         break;
      case SOUTH:
         GlStateManager.translatef(0.0F, 1.0F, -1.0F);
         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         break;
      case WEST:
         GlStateManager.translatef(-1.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(-90.0F, 0.0F, 0.0F, 1.0F);
         break;
      case EAST:
         GlStateManager.translatef(1.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
      }

      this.model.getBase().render(0.0625F);
      GlStateManager.translatef(0.0F, -tileEntityIn.getProgress(partialTicks) * 0.5F, 0.0F);
      GlStateManager.rotatef(270.0F * tileEntityIn.getProgress(partialTicks), 0.0F, 1.0F, 0.0F);
      this.model.getLid().render(0.0625F);
      GlStateManager.enableCull();
      GlStateManager.disableRescaleNormal();
      GlStateManager.popMatrix();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      if (destroyStage >= 0) {
         GlStateManager.matrixMode(5890);
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5888);
      }

   }
}