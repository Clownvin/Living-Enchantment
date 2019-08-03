package net.minecraft.util;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public final class LongSerializable implements IDynamicSerializable {
   private final long field_223464_a;

   private LongSerializable(long p_i51540_1_) {
      this.field_223464_a = p_i51540_1_;
   }

   public long func_223461_a() {
      return this.field_223464_a;
   }

   public <T> T serialize(DynamicOps<T> p_218175_1_) {
      return p_218175_1_.createLong(this.field_223464_a);
   }

   public static LongSerializable func_223462_a(Dynamic<?> p_223462_0_) {
      return new LongSerializable(p_223462_0_.asNumber(Integer.valueOf(0)).longValue());
   }

   public static LongSerializable func_223463_a(long p_223463_0_) {
      return new LongSerializable(p_223463_0_);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         LongSerializable longserializable = (LongSerializable)p_equals_1_;
         return this.field_223464_a == longserializable.field_223464_a;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Long.hashCode(this.field_223464_a);
   }

   public String toString() {
      return Long.toString(this.field_223464_a);
   }
}