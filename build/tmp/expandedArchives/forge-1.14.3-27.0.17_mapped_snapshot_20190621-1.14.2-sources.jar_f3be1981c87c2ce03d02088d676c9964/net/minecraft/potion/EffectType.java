package net.minecraft.potion;

import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum EffectType {
   BENEFICIAL(TextFormatting.BLUE),
   HARMFUL(TextFormatting.RED),
   NEUTRAL(TextFormatting.BLUE);

   private final TextFormatting field_220307_d;

   private EffectType(TextFormatting p_i50390_3_) {
      this.field_220307_d = p_i50390_3_;
   }

   @OnlyIn(Dist.CLIENT)
   public TextFormatting func_220306_a() {
      return this.field_220307_d;
   }
}