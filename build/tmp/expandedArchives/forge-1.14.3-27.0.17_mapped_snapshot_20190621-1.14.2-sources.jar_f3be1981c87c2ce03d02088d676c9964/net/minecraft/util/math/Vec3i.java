package net.minecraft.util.math;

import com.google.common.base.MoreObjects;
import javax.annotation.concurrent.Immutable;
import net.minecraft.dispenser.IPosition;

@Immutable
public class Vec3i implements Comparable<Vec3i> {
   public static final Vec3i NULL_VECTOR = new Vec3i(0, 0, 0);
   private final int x;
   private final int y;
   private final int z;

   public Vec3i(int xIn, int yIn, int zIn) {
      this.x = xIn;
      this.y = yIn;
      this.z = zIn;
   }

   public Vec3i(double xIn, double yIn, double zIn) {
      this(MathHelper.floor(xIn), MathHelper.floor(yIn), MathHelper.floor(zIn));
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof Vec3i)) {
         return false;
      } else {
         Vec3i vec3i = (Vec3i)p_equals_1_;
         if (this.getX() != vec3i.getX()) {
            return false;
         } else if (this.getY() != vec3i.getY()) {
            return false;
         } else {
            return this.getZ() == vec3i.getZ();
         }
      }
   }

   public int hashCode() {
      return (this.getY() + this.getZ() * 31) * 31 + this.getX();
   }

   public int compareTo(Vec3i p_compareTo_1_) {
      if (this.getY() == p_compareTo_1_.getY()) {
         return this.getZ() == p_compareTo_1_.getZ() ? this.getX() - p_compareTo_1_.getX() : this.getZ() - p_compareTo_1_.getZ();
      } else {
         return this.getY() - p_compareTo_1_.getY();
      }
   }

   /**
    * Gets the X coordinate.
    */
   public int getX() {
      return this.x;
   }

   /**
    * Gets the Y coordinate.
    */
   public int getY() {
      return this.y;
   }

   /**
    * Gets the Z coordinate.
    */
   public int getZ() {
      return this.z;
   }

   /**
    * Calculate the cross product of this and the given Vector
    */
   public Vec3i crossProduct(Vec3i vec) {
      return new Vec3i(this.getY() * vec.getZ() - this.getZ() * vec.getY(), this.getZ() * vec.getX() - this.getX() * vec.getZ(), this.getX() * vec.getY() - this.getY() * vec.getX());
   }

   public boolean withinDistance(Vec3i p_218141_1_, double p_218141_2_) {
      return this.distanceSq((double)p_218141_1_.x, (double)p_218141_1_.y, (double)p_218141_1_.z, false) < p_218141_2_ * p_218141_2_;
   }

   public boolean withinDistance(IPosition p_218137_1_, double p_218137_2_) {
      return this.distanceSq(p_218137_1_.getX(), p_218137_1_.getY(), p_218137_1_.getZ(), true) < p_218137_2_ * p_218137_2_;
   }

   /**
    * Calculate squared distance to the given Vector
    */
   public double distanceSq(Vec3i to) {
      return this.distanceSq((double)to.getX(), (double)to.getY(), (double)to.getZ(), true);
   }

   public double distanceSq(IPosition p_218138_1_, boolean p_218138_2_) {
      return this.distanceSq(p_218138_1_.getX(), p_218138_1_.getY(), p_218138_1_.getZ(), p_218138_2_);
   }

   public double distanceSq(double p_218140_1_, double p_218140_3_, double p_218140_5_, boolean useCenter) {
      double d0 = useCenter ? 0.5D : 0.0D;
      double d1 = (double)this.getX() + d0 - p_218140_1_;
      double d2 = (double)this.getY() + d0 - p_218140_3_;
      double d3 = (double)this.getZ() + d0 - p_218140_5_;
      return d1 * d1 + d2 * d2 + d3 * d3;
   }

   public int manhattanDistance(Vec3i p_218139_1_) {
      float f = (float)Math.abs(p_218139_1_.getX() - this.x);
      float f1 = (float)Math.abs(p_218139_1_.getY() - this.y);
      float f2 = (float)Math.abs(p_218139_1_.getZ() - this.z);
      return (int)(f + f1 + f2);
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("x", this.getX()).add("y", this.getY()).add("z", this.getZ()).toString();
   }
}