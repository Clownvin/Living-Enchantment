package net.minecraft.block;

import net.minecraft.item.DyeColor;
import net.minecraft.util.BlockRenderLayer;

public class StainedGlassBlock extends AbstractGlassBlock implements IBeaconBeamColorProvider {
   private final DyeColor color;

   public StainedGlassBlock(DyeColor colorIn, Block.Properties properties) {
      super(properties);
      this.color = colorIn;
   }

   public DyeColor getColor() {
      return this.color;
   }

   /**
    * Gets the render layer this block will render on. SOLID for solid blocks, CUTOUT or CUTOUT_MIPPED for on-off
    * transparency (glass, reeds), TRANSLUCENT for fully blended transparency (stained glass)
    */
   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.TRANSLUCENT;
   }
}