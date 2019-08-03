package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.util.math.MathHelper;

public class SimplexNoiseGenerator {
   protected static final int[][] field_215468_a = new int[][]{{1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0}, {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1}, {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}, {1, 1, 0}, {0, -1, 1}, {-1, 1, 0}, {0, -1, -1}};
   private static final double SQRT_3 = Math.sqrt(3.0D);
   private static final double F2 = 0.5D * (SQRT_3 - 1.0D);
   private static final double G2 = (3.0D - SQRT_3) / 6.0D;
   private final int[] p = new int[512];
   public final double xo;
   public final double yo;
   public final double zo;

   public SimplexNoiseGenerator(Random seed) {
      this.xo = seed.nextDouble() * 256.0D;
      this.yo = seed.nextDouble() * 256.0D;
      this.zo = seed.nextDouble() * 256.0D;

      for(int i = 0; i < 256; this.p[i] = i++) {
         ;
      }

      for(int l = 0; l < 256; ++l) {
         int j = seed.nextInt(256 - l);
         int k = this.p[l];
         this.p[l] = this.p[j + l];
         this.p[j + l] = k;
      }

   }

   private int func_215466_a(int p_215466_1_) {
      return this.p[p_215466_1_ & 255];
   }

   protected static double func_215467_a(int[] p_215467_0_, double p_215467_1_, double p_215467_3_, double p_215467_5_) {
      return (double)p_215467_0_[0] * p_215467_1_ + (double)p_215467_0_[1] * p_215467_3_ + (double)p_215467_0_[2] * p_215467_5_;
   }

   private double func_215465_a(int p_215465_1_, double p_215465_2_, double p_215465_4_, double p_215465_6_, double p_215465_8_) {
      double d1 = p_215465_8_ - p_215465_2_ * p_215465_2_ - p_215465_4_ * p_215465_4_ - p_215465_6_ * p_215465_6_;
      double d0;
      if (d1 < 0.0D) {
         d0 = 0.0D;
      } else {
         d1 = d1 * d1;
         d0 = d1 * d1 * func_215467_a(field_215468_a[p_215465_1_], p_215465_2_, p_215465_4_, p_215465_6_);
      }

      return d0;
   }

   public double getValue(double x, double y) {
      double d0 = (x + y) * F2;
      int i = MathHelper.floor(x + d0);
      int j = MathHelper.floor(y + d0);
      double d1 = (double)(i + j) * G2;
      double d2 = (double)i - d1;
      double d3 = (double)j - d1;
      double d4 = x - d2;
      double d5 = y - d3;
      int k;
      int l;
      if (d4 > d5) {
         k = 1;
         l = 0;
      } else {
         k = 0;
         l = 1;
      }

      double d6 = d4 - (double)k + G2;
      double d7 = d5 - (double)l + G2;
      double d8 = d4 - 1.0D + 2.0D * G2;
      double d9 = d5 - 1.0D + 2.0D * G2;
      int i1 = i & 255;
      int j1 = j & 255;
      int k1 = this.func_215466_a(i1 + this.func_215466_a(j1)) % 12;
      int l1 = this.func_215466_a(i1 + k + this.func_215466_a(j1 + l)) % 12;
      int i2 = this.func_215466_a(i1 + 1 + this.func_215466_a(j1 + 1)) % 12;
      double d10 = this.func_215465_a(k1, d4, d5, 0.0D, 0.5D);
      double d11 = this.func_215465_a(l1, d6, d7, 0.0D, 0.5D);
      double d12 = this.func_215465_a(i2, d8, d9, 0.0D, 0.5D);
      return 70.0D * (d10 + d11 + d12);
   }
}