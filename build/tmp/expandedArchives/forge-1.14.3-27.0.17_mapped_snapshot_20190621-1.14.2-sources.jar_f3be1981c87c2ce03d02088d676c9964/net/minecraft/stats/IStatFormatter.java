package net.minecraft.stats;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IStatFormatter {
   DecimalFormat field_223217_a_ = Util.make(new DecimalFormat("########0.00"), (p_223254_0_) -> {
      p_223254_0_.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
   });
   IStatFormatter field_223218_b_ = NumberFormat.getIntegerInstance(Locale.US)::format;
   IStatFormatter field_223219_c_ = (p_223256_0_) -> {
      return field_223217_a_.format((double)p_223256_0_ * 0.1D);
   };
   IStatFormatter field_223220_d_ = (p_223255_0_) -> {
      double d0 = (double)p_223255_0_ / 100.0D;
      double d1 = d0 / 1000.0D;
      if (d1 > 0.5D) {
         return field_223217_a_.format(d1) + " km";
      } else {
         return d0 > 0.5D ? field_223217_a_.format(d0) + " m" : p_223255_0_ + " cm";
      }
   };
   IStatFormatter field_223221_e_ = (p_223253_0_) -> {
      double d0 = (double)p_223253_0_ / 20.0D;
      double d1 = d0 / 60.0D;
      double d2 = d1 / 60.0D;
      double d3 = d2 / 24.0D;
      double d4 = d3 / 365.0D;
      if (d4 > 0.5D) {
         return field_223217_a_.format(d4) + " y";
      } else if (d3 > 0.5D) {
         return field_223217_a_.format(d3) + " d";
      } else if (d2 > 0.5D) {
         return field_223217_a_.format(d2) + " h";
      } else {
         return d1 > 0.5D ? field_223217_a_.format(d1) + " m" : d0 + " s";
      }
   };

   @OnlyIn(Dist.CLIENT)
   String format(int p_format_1_);
}