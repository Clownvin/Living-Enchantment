package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.tileentity.model.BedModel;
import net.minecraft.item.DyeColor;
import net.minecraft.state.properties.BedPart;
import net.minecraft.tileentity.BedTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BedTileEntityRenderer extends TileEntityRenderer<BedTileEntity> {
   private static final ResourceLocation[] TEXTURES = Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map((p_199742_0_) -> {
      return new ResourceLocation("textures/entity/bed/" + p_199742_0_.getTranslationKey() + ".png");
   }).toArray((p_199743_0_) -> {
      return new ResourceLocation[p_199743_0_];
   });
   private final BedModel model = new BedModel();

   public void render(BedTileEntity tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
      if (destroyStage >= 0) {
         this.bindTexture(DESTROY_STAGES[destroyStage]);
         GlStateManager.matrixMode(5890);
         GlStateManager.pushMatrix();
         GlStateManager.scalef(4.0F, 4.0F, 1.0F);
         GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
         GlStateManager.matrixMode(5888);
      } else {
         ResourceLocation resourcelocation = TEXTURES[tileEntityIn.getColor().getId()];
         if (resourcelocation != null) {
            this.bindTexture(resourcelocation);
         }
      }

      if (tileEntityIn.hasWorld()) {
         BlockState blockstate = tileEntityIn.getBlockState();
         this.func_199343_a(blockstate.get(BedBlock.PART) == BedPart.HEAD, x, y, z, blockstate.get(BedBlock.HORIZONTAL_FACING));
      } else {
         this.func_199343_a(true, x, y, z, Direction.SOUTH);
         this.func_199343_a(false, x, y, z - 1.0D, Direction.SOUTH);
      }

      if (destroyStage >= 0) {
         GlStateManager.matrixMode(5890);
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5888);
      }

   }

   private void func_199343_a(boolean p_199343_1_, double p_199343_2_, double p_199343_4_, double p_199343_6_, Direction p_199343_8_) {
      this.model.preparePiece(p_199343_1_);
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)p_199343_2_, (float)p_199343_4_ + 0.5625F, (float)p_199343_6_);
      GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.translatef(0.5F, 0.5F, 0.5F);
      GlStateManager.rotatef(180.0F + p_199343_8_.getHorizontalAngle(), 0.0F, 0.0F, 1.0F);
      GlStateManager.translatef(-0.5F, -0.5F, -0.5F);
      GlStateManager.enableRescaleNormal();
      this.model.render();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
   }
}