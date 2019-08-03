package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.platform.GlStateManager;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LightDebugRenderer implements DebugRenderer.IDebugRenderer {
   private final Minecraft minecraft;

   public LightDebugRenderer(Minecraft minecraftIn) {
      this.minecraft = minecraftIn;
   }

   public void render(long p_217676_1_) {
      ActiveRenderInfo activerenderinfo = this.minecraft.gameRenderer.getActiveRenderInfo();
      World world = this.minecraft.world;
      GlStateManager.pushMatrix();
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.disableTexture();
      BlockPos blockpos = new BlockPos(activerenderinfo.getProjectedView());
      LongSet longset = new LongOpenHashSet();

      for(BlockPos blockpos1 : BlockPos.getAllInBoxMutable(blockpos.add(-10, -10, -10), blockpos.add(10, 10, 10))) {
         int i = world.getLightFor(LightType.SKY, blockpos1);
         float f = (float)(15 - i) / 15.0F * 0.5F + 0.16F;
         int j = MathHelper.hsvToRGB(f, 0.9F, 0.9F);
         long k = SectionPos.worldToSection(blockpos1.toLong());
         if (longset.add(k)) {
            DebugRenderer.func_217729_a(world.getChunkProvider().getLightManager().func_215572_a(LightType.SKY, SectionPos.from(k)), (double)(SectionPos.extractX(k) * 16 + 8), (double)(SectionPos.extractY(k) * 16 + 8), (double)(SectionPos.extractZ(k) * 16 + 8), 16711680, 0.3F);
         }

         if (i != 15) {
            DebugRenderer.func_217732_a(String.valueOf(i), (double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.25D, (double)blockpos1.getZ() + 0.5D, j);
         }
      }

      GlStateManager.enableTexture();
      GlStateManager.popMatrix();
   }
}