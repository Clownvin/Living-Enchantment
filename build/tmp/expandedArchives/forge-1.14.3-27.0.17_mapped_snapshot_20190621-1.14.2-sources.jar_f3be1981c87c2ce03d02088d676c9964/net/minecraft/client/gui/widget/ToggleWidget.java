package net.minecraft.client.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ToggleWidget extends Widget {
   protected ResourceLocation resourceLocation;
   protected boolean stateTriggered;
   protected int xTexStart;
   protected int yTexStart;
   protected int xDiffTex;
   protected int yDiffTex;

   public ToggleWidget(int p_i51128_1_, int p_i51128_2_, int p_i51128_3_, int p_i51128_4_, boolean p_i51128_5_) {
      super(p_i51128_1_, p_i51128_2_, p_i51128_3_, p_i51128_4_, "");
      this.stateTriggered = p_i51128_5_;
   }

   public void initTextureValues(int xTexStartIn, int yTexStartIn, int xDiffTexIn, int yDiffTexIn, ResourceLocation resourceLocationIn) {
      this.xTexStart = xTexStartIn;
      this.yTexStart = yTexStartIn;
      this.xDiffTex = xDiffTexIn;
      this.yDiffTex = yDiffTexIn;
      this.resourceLocation = resourceLocationIn;
   }

   public void setStateTriggered(boolean p_191753_1_) {
      this.stateTriggered = p_191753_1_;
   }

   public boolean isStateTriggered() {
      return this.stateTriggered;
   }

   public void setPosition(int xIn, int yIn) {
      this.x = xIn;
      this.y = yIn;
   }

   public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
      Minecraft minecraft = Minecraft.getInstance();
      minecraft.getTextureManager().bindTexture(this.resourceLocation);
      GlStateManager.disableDepthTest();
      int i = this.xTexStart;
      int j = this.yTexStart;
      if (this.stateTriggered) {
         i += this.xDiffTex;
      }

      if (this.isHovered()) {
         j += this.yDiffTex;
      }

      this.blit(this.x, this.y, i, j, this.width, this.height);
      GlStateManager.enableDepthTest();
   }
}