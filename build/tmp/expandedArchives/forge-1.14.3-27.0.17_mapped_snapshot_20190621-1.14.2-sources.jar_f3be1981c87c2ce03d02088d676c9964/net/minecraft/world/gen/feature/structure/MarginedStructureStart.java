package net.minecraft.world.gen.feature.structure;

import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;

public abstract class MarginedStructureStart extends StructureStart {
   public MarginedStructureStart(Structure<?> p_i51352_1_, int p_i51352_2_, int p_i51352_3_, Biome p_i51352_4_, MutableBoundingBox p_i51352_5_, int p_i51352_6_, long p_i51352_7_) {
      super(p_i51352_1_, p_i51352_2_, p_i51352_3_, p_i51352_4_, p_i51352_5_, p_i51352_6_, p_i51352_7_);
   }

   protected void recalculateStructureSize() {
      super.recalculateStructureSize();
      int i = 12;
      this.bounds.minX -= 12;
      this.bounds.minY -= 12;
      this.bounds.minZ -= 12;
      this.bounds.maxX += 12;
      this.bounds.maxY += 12;
      this.bounds.maxZ += 12;
   }
}