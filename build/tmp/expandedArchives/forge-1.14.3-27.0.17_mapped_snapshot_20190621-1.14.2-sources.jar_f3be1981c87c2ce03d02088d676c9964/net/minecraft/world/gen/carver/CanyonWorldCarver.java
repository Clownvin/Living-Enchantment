package net.minecraft.world.gen.carver;

import com.mojang.datafixers.Dynamic;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.feature.ProbabilityConfig;

public class CanyonWorldCarver extends WorldCarver<ProbabilityConfig> {
   private final float[] field_202536_i = new float[1024];

   public CanyonWorldCarver(Function<Dynamic<?>, ? extends ProbabilityConfig> p_i49930_1_) {
      super(p_i49930_1_, 256);
   }

   public boolean func_212868_a_(Random p_212868_1_, int p_212868_2_, int p_212868_3_, ProbabilityConfig p_212868_4_) {
      return p_212868_1_.nextFloat() <= p_212868_4_.probability;
   }

   public boolean func_212867_a_(IChunk p_212867_1_, Random rand, int p_212867_3_, int p_212867_4_, int p_212867_5_, int p_212867_6_, int p_212867_7_, BitSet p_212867_8_, ProbabilityConfig p_212867_9_) {
      int i = (this.func_222704_c() * 2 - 1) * 16;
      double d0 = (double)(p_212867_4_ * 16 + rand.nextInt(16));
      double d1 = (double)(rand.nextInt(rand.nextInt(40) + 8) + 20);
      double d2 = (double)(p_212867_5_ * 16 + rand.nextInt(16));
      float f = rand.nextFloat() * ((float)Math.PI * 2F);
      float f1 = (rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
      double d3 = 3.0D;
      float f2 = (rand.nextFloat() * 2.0F + rand.nextFloat()) * 2.0F;
      int j = i - rand.nextInt(i / 4);
      int k = 0;
      this.func_222729_a(p_212867_1_, rand.nextLong(), p_212867_3_, p_212867_6_, p_212867_7_, d0, d1, d2, f2, f, f1, 0, j, 3.0D, p_212867_8_);
      return true;
   }

   private void func_222729_a(IChunk p_222729_1_, long p_222729_2_, int p_222729_4_, int p_222729_5_, int p_222729_6_, double p_222729_7_, double p_222729_9_, double p_222729_11_, float p_222729_13_, float p_222729_14_, float p_222729_15_, int p_222729_16_, int p_222729_17_, double p_222729_18_, BitSet p_222729_20_) {
      Random random = new Random(p_222729_2_);
      float f = 1.0F;

      for(int i = 0; i < 256; ++i) {
         if (i == 0 || random.nextInt(3) == 0) {
            f = 1.0F + random.nextFloat() * random.nextFloat();
         }

         this.field_202536_i[i] = f * f;
      }

      float f4 = 0.0F;
      float f1 = 0.0F;

      for(int j = p_222729_16_; j < p_222729_17_; ++j) {
         double d0 = 1.5D + (double)(MathHelper.sin((float)j * (float)Math.PI / (float)p_222729_17_) * p_222729_13_);
         double d1 = d0 * p_222729_18_;
         d0 = d0 * ((double)random.nextFloat() * 0.25D + 0.75D);
         d1 = d1 * ((double)random.nextFloat() * 0.25D + 0.75D);
         float f2 = MathHelper.cos(p_222729_15_);
         float f3 = MathHelper.sin(p_222729_15_);
         p_222729_7_ += (double)(MathHelper.cos(p_222729_14_) * f2);
         p_222729_9_ += (double)f3;
         p_222729_11_ += (double)(MathHelper.sin(p_222729_14_) * f2);
         p_222729_15_ = p_222729_15_ * 0.7F;
         p_222729_15_ = p_222729_15_ + f1 * 0.05F;
         p_222729_14_ += f4 * 0.05F;
         f1 = f1 * 0.8F;
         f4 = f4 * 0.5F;
         f1 = f1 + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
         f4 = f4 + (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;
         if (random.nextInt(4) != 0) {
            if (!this.func_222702_a(p_222729_5_, p_222729_6_, p_222729_7_, p_222729_11_, j, p_222729_17_, p_222729_13_)) {
               return;
            }

            this.func_222705_a(p_222729_1_, p_222729_2_, p_222729_4_, p_222729_5_, p_222729_6_, p_222729_7_, p_222729_9_, p_222729_11_, d0, d1, p_222729_20_);
         }
      }

   }

   protected boolean func_222708_a(double p_222708_1_, double p_222708_3_, double p_222708_5_, int p_222708_7_) {
      return (p_222708_1_ * p_222708_1_ + p_222708_5_ * p_222708_5_) * (double)this.field_202536_i[p_222708_7_ - 1] + p_222708_3_ * p_222708_3_ / 6.0D >= 1.0D;
   }
}