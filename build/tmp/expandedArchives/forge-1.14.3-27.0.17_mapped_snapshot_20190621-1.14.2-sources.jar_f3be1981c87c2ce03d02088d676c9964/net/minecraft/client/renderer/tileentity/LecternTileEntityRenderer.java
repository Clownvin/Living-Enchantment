package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.LecternBlock;
import net.minecraft.client.renderer.entity.model.BookModel;
import net.minecraft.tileentity.LecternTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LecternTileEntityRenderer extends TileEntityRenderer<LecternTileEntity> {
   private static final ResourceLocation field_217655_c = new ResourceLocation("textures/entity/enchanting_table_book.png");
   private final BookModel field_217656_d = new BookModel();

   public void render(LecternTileEntity tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
      BlockState blockstate = tileEntityIn.getBlockState();
      if (blockstate.get(LecternBlock.HAS_BOOK)) {
         GlStateManager.pushMatrix();
         GlStateManager.translatef((float)x + 0.5F, (float)y + 1.0F + 0.0625F, (float)z + 0.5F);
         float f = blockstate.get(LecternBlock.FACING).rotateY().getHorizontalAngle();
         GlStateManager.rotatef(-f, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(67.5F, 0.0F, 0.0F, 1.0F);
         GlStateManager.translatef(0.0F, -0.125F, 0.0F);
         this.bindTexture(field_217655_c);
         GlStateManager.enableCull();
         this.field_217656_d.func_217103_a(0.0F, 0.1F, 0.9F, 1.2F, 0.0F, 0.0625F);
         GlStateManager.popMatrix();
      }
   }
}