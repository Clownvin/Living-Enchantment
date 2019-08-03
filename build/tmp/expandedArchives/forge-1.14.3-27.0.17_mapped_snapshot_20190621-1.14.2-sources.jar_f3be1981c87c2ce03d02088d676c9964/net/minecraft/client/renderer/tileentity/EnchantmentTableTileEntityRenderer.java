package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.model.BookModel;
import net.minecraft.tileentity.EnchantingTableTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnchantmentTableTileEntityRenderer extends TileEntityRenderer<EnchantingTableTileEntity> {
   private static final ResourceLocation TEXTURE_BOOK = new ResourceLocation("textures/entity/enchanting_table_book.png");
   private final BookModel modelBook = new BookModel();

   public void render(EnchantingTableTileEntity tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)x + 0.5F, (float)y + 0.75F, (float)z + 0.5F);
      float f = (float)tileEntityIn.field_195522_a + partialTicks;
      GlStateManager.translatef(0.0F, 0.1F + MathHelper.sin(f * 0.1F) * 0.01F, 0.0F);

      float f1;
      for(f1 = tileEntityIn.field_195529_l - tileEntityIn.field_195530_m; f1 >= (float)Math.PI; f1 -= ((float)Math.PI * 2F)) {
         ;
      }

      while(f1 < -(float)Math.PI) {
         f1 += ((float)Math.PI * 2F);
      }

      float f2 = tileEntityIn.field_195530_m + f1 * partialTicks;
      GlStateManager.rotatef(-f2 * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(80.0F, 0.0F, 0.0F, 1.0F);
      this.bindTexture(TEXTURE_BOOK);
      float f3 = MathHelper.lerp(partialTicks, tileEntityIn.field_195524_g, tileEntityIn.field_195523_f) + 0.25F;
      float f4 = MathHelper.lerp(partialTicks, tileEntityIn.field_195524_g, tileEntityIn.field_195523_f) + 0.75F;
      f3 = (f3 - (float)MathHelper.fastFloor((double)f3)) * 1.6F - 0.3F;
      f4 = (f4 - (float)MathHelper.fastFloor((double)f4)) * 1.6F - 0.3F;
      if (f3 < 0.0F) {
         f3 = 0.0F;
      }

      if (f4 < 0.0F) {
         f4 = 0.0F;
      }

      if (f3 > 1.0F) {
         f3 = 1.0F;
      }

      if (f4 > 1.0F) {
         f4 = 1.0F;
      }

      float f5 = MathHelper.lerp(partialTicks, tileEntityIn.field_195528_k, tileEntityIn.field_195527_j);
      GlStateManager.enableCull();
      this.modelBook.func_217103_a(f, f3, f4, f5, 0.0F, 0.0625F);
      GlStateManager.popMatrix();
   }
}