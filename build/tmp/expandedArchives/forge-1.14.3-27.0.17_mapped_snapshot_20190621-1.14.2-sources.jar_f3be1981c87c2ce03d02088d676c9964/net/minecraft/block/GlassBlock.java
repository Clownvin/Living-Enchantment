package net.minecraft.block;

import net.minecraft.util.BlockRenderLayer;

public class GlassBlock extends AbstractGlassBlock {
   public GlassBlock(Block.Properties properties) {
      super(properties);
   }

   /**
    * Gets the render layer this block will render on. SOLID for solid blocks, CUTOUT or CUTOUT_MIPPED for on-off
    * transparency (glass, reeds), TRANSLUCENT for fully blended transparency (stained glass)
    */
   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }
}