package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.tileentity.model.BellModel;
import net.minecraft.tileentity.BellTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BellTileEntityRenderer extends TileEntityRenderer<BellTileEntity> {
   private static final ResourceLocation field_217653_c = new ResourceLocation("textures/entity/bell/bell_body.png");
   private final BellModel field_217654_d = new BellModel();

   public void render(BellTileEntity tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
      GlStateManager.pushMatrix();
      GlStateManager.enableRescaleNormal();
      this.bindTexture(field_217653_c);
      GlStateManager.translatef((float)x, (float)y, (float)z);
      float f = (float)tileEntityIn.field_213943_a + partialTicks;
      float f1 = 0.0F;
      float f2 = 0.0F;
      if (tileEntityIn.field_213944_b) {
         float f3 = MathHelper.sin(f / (float)Math.PI) / (4.0F + f / 3.0F);
         if (tileEntityIn.field_213945_c == Direction.NORTH) {
            f1 = -f3;
         } else if (tileEntityIn.field_213945_c == Direction.SOUTH) {
            f1 = f3;
         } else if (tileEntityIn.field_213945_c == Direction.EAST) {
            f2 = -f3;
         } else if (tileEntityIn.field_213945_c == Direction.WEST) {
            f2 = f3;
         }
      }

      this.field_217654_d.func_217099_a(f1, f2, 0.0625F);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
   }
}