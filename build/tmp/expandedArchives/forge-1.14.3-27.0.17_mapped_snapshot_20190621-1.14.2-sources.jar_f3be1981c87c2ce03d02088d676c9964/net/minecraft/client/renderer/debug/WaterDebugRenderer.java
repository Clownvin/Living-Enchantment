package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WaterDebugRenderer implements DebugRenderer.IDebugRenderer {
   private final Minecraft minecraft;

   public WaterDebugRenderer(Minecraft minecraftIn) {
      this.minecraft = minecraftIn;
   }

   public void render(long p_217676_1_) {
      ActiveRenderInfo activerenderinfo = this.minecraft.gameRenderer.getActiveRenderInfo();
      double d0 = activerenderinfo.getProjectedView().x;
      double d1 = activerenderinfo.getProjectedView().y;
      double d2 = activerenderinfo.getProjectedView().z;
      BlockPos blockpos = this.minecraft.player.getPosition();
      IWorldReader iworldreader = this.minecraft.player.world;
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.color4f(0.0F, 1.0F, 0.0F, 0.75F);
      GlStateManager.disableTexture();
      GlStateManager.lineWidth(6.0F);

      for(BlockPos blockpos1 : BlockPos.getAllInBoxMutable(blockpos.add(-10, -10, -10), blockpos.add(10, 10, 10))) {
         IFluidState ifluidstate = iworldreader.getFluidState(blockpos1);
         if (ifluidstate.isTagged(FluidTags.WATER)) {
            double d3 = (double)((float)blockpos1.getY() + ifluidstate.func_215679_a(iworldreader, blockpos1));
            DebugRenderer.func_217730_a((new AxisAlignedBB((double)((float)blockpos1.getX() + 0.01F), (double)((float)blockpos1.getY() + 0.01F), (double)((float)blockpos1.getZ() + 0.01F), (double)((float)blockpos1.getX() + 0.99F), d3, (double)((float)blockpos1.getZ() + 0.99F))).offset(-d0, -d1, -d2), 1.0F, 1.0F, 1.0F, 0.2F);
         }
      }

      for(BlockPos blockpos2 : BlockPos.getAllInBoxMutable(blockpos.add(-10, -10, -10), blockpos.add(10, 10, 10))) {
         IFluidState ifluidstate1 = iworldreader.getFluidState(blockpos2);
         if (ifluidstate1.isTagged(FluidTags.WATER)) {
            DebugRenderer.func_217732_a(String.valueOf(ifluidstate1.getLevel()), (double)blockpos2.getX() + 0.5D, (double)((float)blockpos2.getY() + ifluidstate1.func_215679_a(iworldreader, blockpos2)), (double)blockpos2.getZ() + 0.5D, -16777216);
         }
      }

      GlStateManager.enableTexture();
      GlStateManager.disableBlend();
   }
}