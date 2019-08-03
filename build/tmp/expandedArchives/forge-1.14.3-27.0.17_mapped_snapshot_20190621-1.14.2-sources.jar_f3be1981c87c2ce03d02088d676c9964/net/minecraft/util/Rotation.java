package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum Rotation {
   NONE,
   CLOCKWISE_90,
   CLOCKWISE_180,
   COUNTERCLOCKWISE_90;

   public Rotation add(Rotation rotation) {
      switch(rotation) {
      case CLOCKWISE_180:
         switch(this) {
         case NONE:
            return CLOCKWISE_180;
         case CLOCKWISE_90:
            return COUNTERCLOCKWISE_90;
         case CLOCKWISE_180:
            return NONE;
         case COUNTERCLOCKWISE_90:
            return CLOCKWISE_90;
         }
      case COUNTERCLOCKWISE_90:
         switch(this) {
         case NONE:
            return COUNTERCLOCKWISE_90;
         case CLOCKWISE_90:
            return NONE;
         case CLOCKWISE_180:
            return CLOCKWISE_90;
         case COUNTERCLOCKWISE_90:
            return CLOCKWISE_180;
         }
      case CLOCKWISE_90:
         switch(this) {
         case NONE:
            return CLOCKWISE_90;
         case CLOCKWISE_90:
            return CLOCKWISE_180;
         case CLOCKWISE_180:
            return COUNTERCLOCKWISE_90;
         case COUNTERCLOCKWISE_90:
            return NONE;
         }
      default:
         return this;
      }
   }

   public Direction rotate(Direction facing) {
      if (facing.getAxis() == Direction.Axis.Y) {
         return facing;
      } else {
         switch(this) {
         case CLOCKWISE_90:
            return facing.rotateY();
         case CLOCKWISE_180:
            return facing.getOpposite();
         case COUNTERCLOCKWISE_90:
            return facing.rotateYCCW();
         default:
            return facing;
         }
      }
   }

   public int rotate(int p_185833_1_, int p_185833_2_) {
      switch(this) {
      case CLOCKWISE_90:
         return (p_185833_1_ + p_185833_2_ / 4) % p_185833_2_;
      case CLOCKWISE_180:
         return (p_185833_1_ + p_185833_2_ / 2) % p_185833_2_;
      case COUNTERCLOCKWISE_90:
         return (p_185833_1_ + p_185833_2_ * 3 / 4) % p_185833_2_;
      default:
         return p_185833_1_;
      }
   }

   public static Rotation func_222466_a(Random p_222466_0_) {
      Rotation[] arotation = values();
      return arotation[p_222466_0_.nextInt(arotation.length)];
   }

   public static List<Rotation> func_222467_b(Random p_222467_0_) {
      List<Rotation> list = Lists.newArrayList(values());
      Collections.shuffle(list, p_222467_0_);
      return list;
   }
}