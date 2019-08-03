package net.minecraft.world.chunk.listener;

import javax.annotation.Nullable;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.chunk.ChunkStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggingChunkStatusListener implements IChunkStatusListener {
   private static final Logger field_219512_a = LogManager.getLogger();
   private final int field_219513_b;
   private int field_219514_c;
   private long field_219515_d;
   private long field_219516_e = Long.MAX_VALUE;

   public LoggingChunkStatusListener(int p_i50697_1_) {
      int i = p_i50697_1_ * 2 + 1;
      this.field_219513_b = i * i;
   }

   public void func_219509_a(ChunkPos p_219509_1_) {
      this.field_219516_e = Util.milliTime();
      this.field_219515_d = this.field_219516_e;
   }

   public void func_219508_a(ChunkPos p_219508_1_, @Nullable ChunkStatus p_219508_2_) {
      if (p_219508_2_ == ChunkStatus.FULL) {
         ++this.field_219514_c;
      }

      int i = this.func_219511_c();
      if (Util.milliTime() > this.field_219516_e) {
         this.field_219516_e += 500L;
         field_219512_a.info((new TranslationTextComponent("menu.preparingSpawn", MathHelper.clamp(i, 0, 100))).getString());
      }

   }

   public void func_219510_b() {
      field_219512_a.info("Time elapsed: {} ms", (long)(Util.milliTime() - this.field_219515_d));
      this.field_219516_e = Long.MAX_VALUE;
   }

   public int func_219511_c() {
      return MathHelper.floor((float)this.field_219514_c * 100.0F / (float)this.field_219513_b);
   }
}