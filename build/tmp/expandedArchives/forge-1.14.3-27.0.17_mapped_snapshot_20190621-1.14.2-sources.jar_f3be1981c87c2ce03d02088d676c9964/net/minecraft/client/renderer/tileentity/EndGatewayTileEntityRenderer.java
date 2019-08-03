package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.EndGatewayTileEntity;
import net.minecraft.tileentity.EndPortalTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EndGatewayTileEntityRenderer extends EndPortalTileEntityRenderer {
   private static final ResourceLocation END_GATEWAY_BEAM_TEXTURE = new ResourceLocation("textures/entity/end_gateway_beam.png");

   public void render(EndPortalTileEntity tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
      GlStateManager.disableFog();
      EndGatewayTileEntity endgatewaytileentity = (EndGatewayTileEntity)tileEntityIn;
      if (endgatewaytileentity.isSpawning() || endgatewaytileentity.isCoolingDown()) {
         GlStateManager.alphaFunc(516, 0.1F);
         this.bindTexture(END_GATEWAY_BEAM_TEXTURE);
         float f = endgatewaytileentity.isSpawning() ? endgatewaytileentity.getSpawnPercent(partialTicks) : endgatewaytileentity.getCooldownPercent(partialTicks);
         double d0 = endgatewaytileentity.isSpawning() ? 256.0D - y : 50.0D;
         f = MathHelper.sin(f * (float)Math.PI);
         int i = MathHelper.floor((double)f * d0);
         float[] afloat = endgatewaytileentity.isSpawning() ? DyeColor.MAGENTA.getColorComponentValues() : DyeColor.PURPLE.getColorComponentValues();
         BeaconTileEntityRenderer.renderBeamSegment(x, y, z, (double)partialTicks, (double)f, endgatewaytileentity.getWorld().getGameTime(), 0, i, afloat, 0.15D, 0.175D);
         BeaconTileEntityRenderer.renderBeamSegment(x, y, z, (double)partialTicks, (double)f, endgatewaytileentity.getWorld().getGameTime(), 0, -i, afloat, 0.15D, 0.175D);
      }

      super.render(tileEntityIn, x, y, z, partialTicks, destroyStage);
      GlStateManager.enableFog();
   }

   protected int getPasses(double p_191286_1_) {
      return super.getPasses(p_191286_1_) + 1;
   }

   protected float getOffset() {
      return 1.0F;
   }
}