package net.minecraft.util.math;

import net.minecraft.nbt.FloatNBT;
import net.minecraft.nbt.ListNBT;

public class Rotations {
   protected final float x;
   protected final float y;
   protected final float z;

   public Rotations(float x, float y, float z) {
      this.x = !Float.isInfinite(x) && !Float.isNaN(x) ? x % 360.0F : 0.0F;
      this.y = !Float.isInfinite(y) && !Float.isNaN(y) ? y % 360.0F : 0.0F;
      this.z = !Float.isInfinite(z) && !Float.isNaN(z) ? z % 360.0F : 0.0F;
   }

   public Rotations(ListNBT nbt) {
      this(nbt.getFloat(0), nbt.getFloat(1), nbt.getFloat(2));
   }

   public ListNBT writeToNBT() {
      ListNBT listnbt = new ListNBT();
      listnbt.add(new FloatNBT(this.x));
      listnbt.add(new FloatNBT(this.y));
      listnbt.add(new FloatNBT(this.z));
      return listnbt;
   }

   public boolean equals(Object p_equals_1_) {
      if (!(p_equals_1_ instanceof Rotations)) {
         return false;
      } else {
         Rotations rotations = (Rotations)p_equals_1_;
         return this.x == rotations.x && this.y == rotations.y && this.z == rotations.z;
      }
   }

   /**
    * Gets the X axis rotation
    */
   public float getX() {
      return this.x;
   }

   /**
    * Gets the Y axis rotation
    */
   public float getY() {
      return this.y;
   }

   /**
    * Gets the Z axis rotation
    */
   public float getZ() {
      return this.z;
   }
}