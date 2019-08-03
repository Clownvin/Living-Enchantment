package net.minecraft.client.gui.fonts;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IGlyph {
   float getAdvance();

   default float func_223274_a_(boolean p_223274_1_) {
      return this.getAdvance() + (p_223274_1_ ? this.func_223275_b_() : 0.0F);
   }

   default float func_223273_a_() {
      return 0.0F;
   }

   default float func_223275_b_() {
      return 1.0F;
   }

   default float func_223276_c_() {
      return 1.0F;
   }
}