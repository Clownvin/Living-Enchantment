package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class WeightedList<U> {
   private final List<WeightedList<U>.Entry<? extends U>> field_220658_a = Lists.newArrayList();
   private final Random random;

   public WeightedList() {
      this(new Random());
   }

   public WeightedList(Random p_i50335_1_) {
      this.random = p_i50335_1_;
   }

   public void func_220656_a(U p_220656_1_, int p_220656_2_) {
      this.field_220658_a.add(new WeightedList.Entry(p_220656_1_, p_220656_2_));
   }

   public void func_220654_a() {
      this.field_220658_a.forEach((p_220657_1_) -> {
         p_220657_1_.func_220648_a(this.random.nextFloat());
      });
      this.field_220658_a.sort(Comparator.comparingDouble(WeightedList.Entry::func_220649_a));
   }

   public Stream<? extends U> func_220655_b() {
      return this.field_220658_a.stream().map(WeightedList.Entry::func_220647_b);
   }

   public String toString() {
      return "WeightedList[" + this.field_220658_a + "]";
   }

   class Entry<T> {
      private final T field_220651_b;
      private final int field_220652_c;
      private double field_220653_d;

      private Entry(T p_i50545_2_, int p_i50545_3_) {
         this.field_220652_c = p_i50545_3_;
         this.field_220651_b = p_i50545_2_;
      }

      public double func_220649_a() {
         return this.field_220653_d;
      }

      public void func_220648_a(float p_220648_1_) {
         this.field_220653_d = -Math.pow((double)p_220648_1_, (double)(1.0F / (float)this.field_220652_c));
      }

      public T func_220647_b() {
         return this.field_220651_b;
      }

      public String toString() {
         return "" + this.field_220652_c + ":" + this.field_220651_b;
      }
   }
}