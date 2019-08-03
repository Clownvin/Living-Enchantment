package net.minecraft.util;

import com.google.common.collect.Iterators;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum Direction implements IStringSerializable {
   DOWN(0, 1, -1, "down", Direction.AxisDirection.NEGATIVE, Direction.Axis.Y, new Vec3i(0, -1, 0)),
   UP(1, 0, -1, "up", Direction.AxisDirection.POSITIVE, Direction.Axis.Y, new Vec3i(0, 1, 0)),
   NORTH(2, 3, 2, "north", Direction.AxisDirection.NEGATIVE, Direction.Axis.Z, new Vec3i(0, 0, -1)),
   SOUTH(3, 2, 0, "south", Direction.AxisDirection.POSITIVE, Direction.Axis.Z, new Vec3i(0, 0, 1)),
   WEST(4, 5, 1, "west", Direction.AxisDirection.NEGATIVE, Direction.Axis.X, new Vec3i(-1, 0, 0)),
   EAST(5, 4, 3, "east", Direction.AxisDirection.POSITIVE, Direction.Axis.X, new Vec3i(1, 0, 0));

   private final int index;
   private final int opposite;
   private final int horizontalIndex;
   private final String name;
   private final Direction.Axis axis;
   private final Direction.AxisDirection axisDirection;
   private final Vec3i directionVec;
   private static final Direction[] VALUES = values();
   private static final Map<String, Direction> NAME_LOOKUP = Arrays.stream(VALUES).collect(Collectors.toMap(Direction::getName2, (p_199787_0_) -> {
      return p_199787_0_;
   }));
   private static final Direction[] BY_INDEX = Arrays.stream(VALUES).sorted(Comparator.comparingInt((p_199790_0_) -> {
      return p_199790_0_.index;
   })).toArray((p_199788_0_) -> {
      return new Direction[p_199788_0_];
   });
   private static final Direction[] BY_HORIZONTAL_INDEX = Arrays.stream(VALUES).filter((p_199786_0_) -> {
      return p_199786_0_.getAxis().isHorizontal();
   }).sorted(Comparator.comparingInt((p_199789_0_) -> {
      return p_199789_0_.horizontalIndex;
   })).toArray((p_199791_0_) -> {
      return new Direction[p_199791_0_];
   });
   private static final Long2ObjectMap<Direction> field_218387_r = Arrays.stream(VALUES).collect(Collectors.toMap((p_218385_0_) -> {
      return (new BlockPos(p_218385_0_.getDirectionVec())).toLong();
   }, (p_218384_0_) -> {
      return p_218384_0_;
   }, (p_218386_0_, p_218386_1_) -> {
      throw new IllegalArgumentException("Duplicate keys");
   }, Long2ObjectOpenHashMap::new));

   private Direction(int indexIn, int oppositeIn, int horizontalIndexIn, String nameIn, Direction.AxisDirection axisDirectionIn, Direction.Axis axisIn, Vec3i directionVecIn) {
      this.index = indexIn;
      this.horizontalIndex = horizontalIndexIn;
      this.opposite = oppositeIn;
      this.name = nameIn;
      this.axis = axisIn;
      this.axisDirection = axisDirectionIn;
      this.directionVec = directionVecIn;
   }

   /**
    * Gets the {@link EnumFacing} values for the provided entity's
    *  looking direction. Dependent on yaw and pitch of entity looking.
    */
   public static Direction[] getFacingDirections(Entity entityIn) {
      float f = entityIn.getPitch(1.0F) * ((float)Math.PI / 180F);
      float f1 = -entityIn.getYaw(1.0F) * ((float)Math.PI / 180F);
      float f2 = MathHelper.sin(f);
      float f3 = MathHelper.cos(f);
      float f4 = MathHelper.sin(f1);
      float f5 = MathHelper.cos(f1);
      boolean flag = f4 > 0.0F;
      boolean flag1 = f2 < 0.0F;
      boolean flag2 = f5 > 0.0F;
      float f6 = flag ? f4 : -f4;
      float f7 = flag1 ? -f2 : f2;
      float f8 = flag2 ? f5 : -f5;
      float f9 = f6 * f3;
      float f10 = f8 * f3;
      Direction direction = flag ? EAST : WEST;
      Direction direction1 = flag1 ? UP : DOWN;
      Direction direction2 = flag2 ? SOUTH : NORTH;
      if (f6 > f8) {
         if (f7 > f9) {
            return compose(direction1, direction, direction2);
         } else {
            return f10 > f7 ? compose(direction, direction2, direction1) : compose(direction, direction1, direction2);
         }
      } else if (f7 > f10) {
         return compose(direction1, direction2, direction);
      } else {
         return f9 > f7 ? compose(direction2, direction, direction1) : compose(direction2, direction1, direction);
      }
   }

   /**
    * Creates an array of x y z equivalent facing values.
    */
   private static Direction[] compose(Direction first, Direction second, Direction third) {
      return new Direction[]{first, second, third, third.getOpposite(), second.getOpposite(), first.getOpposite()};
   }

   /**
    * Get the Index of this Facing (0-5). The order is D-U-N-S-W-E
    */
   public int getIndex() {
      return this.index;
   }

   /**
    * Get the index of this horizontal facing (0-3). The order is S-W-N-E
    */
   public int getHorizontalIndex() {
      return this.horizontalIndex;
   }

   /**
    * Get the AxisDirection of this Facing.
    */
   public Direction.AxisDirection getAxisDirection() {
      return this.axisDirection;
   }

   /**
    * Get the opposite Facing (e.g. DOWN => UP)
    */
   public Direction getOpposite() {
      return byIndex(this.opposite);
   }

   /**
    * Rotate this Facing around the given axis clockwise. If this facing cannot be rotated around the given axis,
    * returns this facing without rotating.
    */
   public Direction rotateAround(Direction.Axis axis) {
      switch(axis) {
      case X:
         if (this != WEST && this != EAST) {
            return this.rotateX();
         }

         return this;
      case Y:
         if (this != UP && this != DOWN) {
            return this.rotateY();
         }

         return this;
      case Z:
         if (this != NORTH && this != SOUTH) {
            return this.rotateZ();
         }

         return this;
      default:
         throw new IllegalStateException("Unable to get CW facing for axis " + axis);
      }
   }

   /**
    * Rotate this Facing around the Y axis clockwise (NORTH => EAST => SOUTH => WEST => NORTH)
    */
   public Direction rotateY() {
      switch(this) {
      case NORTH:
         return EAST;
      case EAST:
         return SOUTH;
      case SOUTH:
         return WEST;
      case WEST:
         return NORTH;
      default:
         throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
      }
   }

   /**
    * Rotate this Facing around the X axis (NORTH => DOWN => SOUTH => UP => NORTH)
    */
   private Direction rotateX() {
      switch(this) {
      case NORTH:
         return DOWN;
      case EAST:
      case WEST:
      default:
         throw new IllegalStateException("Unable to get X-rotated facing of " + this);
      case SOUTH:
         return UP;
      case UP:
         return NORTH;
      case DOWN:
         return SOUTH;
      }
   }

   /**
    * Rotate this Facing around the Z axis (EAST => DOWN => WEST => UP => EAST)
    */
   private Direction rotateZ() {
      switch(this) {
      case EAST:
         return DOWN;
      case SOUTH:
      default:
         throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
      case WEST:
         return UP;
      case UP:
         return EAST;
      case DOWN:
         return WEST;
      }
   }

   /**
    * Rotate this Facing around the Y axis counter-clockwise (NORTH => WEST => SOUTH => EAST => NORTH)
    */
   public Direction rotateYCCW() {
      switch(this) {
      case NORTH:
         return WEST;
      case EAST:
         return NORTH;
      case SOUTH:
         return EAST;
      case WEST:
         return SOUTH;
      default:
         throw new IllegalStateException("Unable to get CCW facing of " + this);
      }
   }

   /**
    * Gets the offset in the x direction to the block in front of this facing.
    */
   public int getXOffset() {
      return this.axis == Direction.Axis.X ? this.axisDirection.getOffset() : 0;
   }

   /**
    * Gets the offset in the y direction to the block in front of this facing.
    */
   public int getYOffset() {
      return this.axis == Direction.Axis.Y ? this.axisDirection.getOffset() : 0;
   }

   /**
    * Gets the offset in the z direction to the block in front of this facing.
    */
   public int getZOffset() {
      return this.axis == Direction.Axis.Z ? this.axisDirection.getOffset() : 0;
   }

   /**
    * Same as getName, but does not override the method from Enum.
    */
   public String getName2() {
      return this.name;
   }

   public Direction.Axis getAxis() {
      return this.axis;
   }

   /**
    * Get the facing specified by the given name
    */
   @Nullable
   public static Direction byName(@Nullable String name) {
      return name == null ? null : NAME_LOOKUP.get(name.toLowerCase(Locale.ROOT));
   }

   /**
    * Gets the EnumFacing corresponding to the given index (0-5). Out of bounds values are wrapped around. The order is
    * D-U-N-S-W-E.
    */
   public static Direction byIndex(int index) {
      return BY_INDEX[MathHelper.abs(index % BY_INDEX.length)];
   }

   /**
    * Gets the EnumFacing corresponding to the given horizontal index (0-3). Out of bounds values are wrapped around.
    * The order is S-W-N-E.
    */
   public static Direction byHorizontalIndex(int horizontalIndexIn) {
      return BY_HORIZONTAL_INDEX[MathHelper.abs(horizontalIndexIn % BY_HORIZONTAL_INDEX.length)];
   }

   @Nullable
   public static Direction func_218383_a(int p_218383_0_, int p_218383_1_, int p_218383_2_) {
      return field_218387_r.get(BlockPos.pack(p_218383_0_, p_218383_1_, p_218383_2_));
   }

   /**
    * Get the EnumFacing corresponding to the given angle in degrees (0-360). Out of bounds values are wrapped around.
    * An angle of 0 is SOUTH, an angle of 90 would be WEST.
    */
   public static Direction fromAngle(double angle) {
      return byHorizontalIndex(MathHelper.floor(angle / 90.0D + 0.5D) & 3);
   }

   public static Direction getFacingFromAxisDirection(Direction.Axis axisIn, Direction.AxisDirection axisDirectionIn) {
      switch(axisIn) {
      case X:
         return axisDirectionIn == Direction.AxisDirection.POSITIVE ? EAST : WEST;
      case Y:
         return axisDirectionIn == Direction.AxisDirection.POSITIVE ? UP : DOWN;
      case Z:
      default:
         return axisDirectionIn == Direction.AxisDirection.POSITIVE ? SOUTH : NORTH;
      }
   }

   /**
    * Gets the angle in degrees corresponding to this EnumFacing.
    */
   public float getHorizontalAngle() {
      return (float)((this.horizontalIndex & 3) * 90);
   }

   /**
    * Choose a random Facing using the given Random
    */
   public static Direction random(Random rand) {
      return values()[rand.nextInt(values().length)];
   }

   public static Direction getFacingFromVector(double x, double y, double z) {
      return getFacingFromVector((float)x, (float)y, (float)z);
   }

   public static Direction getFacingFromVector(float x, float y, float z) {
      Direction direction = NORTH;
      float f = Float.MIN_VALUE;

      for(Direction direction1 : VALUES) {
         float f1 = x * (float)direction1.directionVec.getX() + y * (float)direction1.directionVec.getY() + z * (float)direction1.directionVec.getZ();
         if (f1 > f) {
            f = f1;
            direction = direction1;
         }
      }

      return direction;
   }

   public String toString() {
      return this.name;
   }

   public String getName() {
      return this.name;
   }

   public static Direction getFacingFromAxis(Direction.AxisDirection axisDirectionIn, Direction.Axis axisIn) {
      for(Direction direction : values()) {
         if (direction.getAxisDirection() == axisDirectionIn && direction.getAxis() == axisIn) {
            return direction;
         }
      }

      throw new IllegalArgumentException("No such direction: " + axisDirectionIn + " " + axisIn);
   }

   /**
    * Get a normalized Vector that points in the direction of this Facing.
    */
   public Vec3i getDirectionVec() {
      return this.directionVec;
   }

   public static enum Axis implements IStringSerializable, Predicate<Direction> {
      X("x") {
         public int getCoordinate(int x, int y, int z) {
            return x;
         }

         public double getCoordinate(double x, double y, double z) {
            return x;
         }
      },
      Y("y") {
         public int getCoordinate(int x, int y, int z) {
            return y;
         }

         public double getCoordinate(double x, double y, double z) {
            return y;
         }
      },
      Z("z") {
         public int getCoordinate(int x, int y, int z) {
            return z;
         }

         public double getCoordinate(double x, double y, double z) {
            return z;
         }
      };

      private static final Map<String, Direction.Axis> NAME_LOOKUP = Arrays.stream(values()).collect(Collectors.toMap(Direction.Axis::getName2, (p_199785_0_) -> {
         return p_199785_0_;
      }));
      private final String name;

      private Axis(String nameIn) {
         this.name = nameIn;
      }

      /**
       * Get the axis specified by the given name
       */
      @Nullable
      public static Direction.Axis byName(String name) {
         return NAME_LOOKUP.get(name.toLowerCase(Locale.ROOT));
      }

      /**
       * Like getName but doesn't override the method from Enum.
       */
      public String getName2() {
         return this.name;
      }

      public boolean isVertical() {
         return this == Y;
      }

      /**
       * If this Axis is on the horizontal plane (true for X and Z)
       */
      public boolean isHorizontal() {
         return this == X || this == Z;
      }

      public String toString() {
         return this.name;
      }

      public static Direction.Axis func_218393_a(Random p_218393_0_) {
         return values()[p_218393_0_.nextInt(values().length)];
      }

      public boolean test(@Nullable Direction p_test_1_) {
         return p_test_1_ != null && p_test_1_.getAxis() == this;
      }

      /**
       * Get this Axis' Plane (VERTICAL for Y, HORIZONTAL for X and Z)
       */
      public Direction.Plane getPlane() {
         switch(this) {
         case X:
         case Z:
            return Direction.Plane.HORIZONTAL;
         case Y:
            return Direction.Plane.VERTICAL;
         default:
            throw new Error("Someone's been tampering with the universe!");
         }
      }

      public String getName() {
         return this.name;
      }

      public abstract int getCoordinate(int x, int y, int z);

      public abstract double getCoordinate(double x, double y, double z);
   }

   public static enum AxisDirection {
      POSITIVE(1, "Towards positive"),
      NEGATIVE(-1, "Towards negative");

      private final int offset;
      private final String description;

      private AxisDirection(int offset, String description) {
         this.offset = offset;
         this.description = description;
      }

      /**
       * Get the offset for this AxisDirection. 1 for POSITIVE, -1 for NEGATIVE
       */
      public int getOffset() {
         return this.offset;
      }

      public String toString() {
         return this.description;
      }
   }

   public static enum Plane implements Iterable<Direction>, Predicate<Direction> {
      HORIZONTAL(new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST}, new Direction.Axis[]{Direction.Axis.X, Direction.Axis.Z}),
      VERTICAL(new Direction[]{Direction.UP, Direction.DOWN}, new Direction.Axis[]{Direction.Axis.Y});

      private final Direction[] facingValues;
      private final Direction.Axis[] axisValues;

      private Plane(Direction[] facingValuesIn, Direction.Axis[] axisValuesIn) {
         this.facingValues = facingValuesIn;
         this.axisValues = axisValuesIn;
      }

      /**
       * Choose a random Facing from this Plane using the given Random
       */
      public Direction random(Random rand) {
         return this.facingValues[rand.nextInt(this.facingValues.length)];
      }

      public boolean test(@Nullable Direction p_test_1_) {
         return p_test_1_ != null && p_test_1_.getAxis().getPlane() == this;
      }

      public Iterator<Direction> iterator() {
         return Iterators.forArray(this.facingValues);
      }
   }
}