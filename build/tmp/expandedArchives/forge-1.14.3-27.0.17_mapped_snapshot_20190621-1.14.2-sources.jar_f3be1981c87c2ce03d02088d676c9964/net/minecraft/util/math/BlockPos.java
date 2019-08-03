package net.minecraft.util.math;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Spliterator.OfInt;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.concurrent.Immutable;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisRotation;
import net.minecraft.util.Direction;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.Rotation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Immutable
public class BlockPos extends Vec3i implements IDynamicSerializable {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final BlockPos ZERO = new BlockPos(0, 0, 0);
   private static final int NUM_X_BITS = 1 + MathHelper.log2(MathHelper.smallestEncompassingPowerOfTwo(30000000));
   private static final int NUM_Z_BITS = NUM_X_BITS;
   private static final int NUM_Y_BITS = 64 - NUM_X_BITS - NUM_Z_BITS;
   private static final long X_MASK = (1L << NUM_X_BITS) - 1L;
   private static final long Y_MASK = (1L << NUM_Y_BITS) - 1L;
   private static final long Z_MASK = (1L << NUM_Z_BITS) - 1L;
   private static final int field_218292_j = NUM_Y_BITS;
   private static final int field_218293_k = NUM_Y_BITS + NUM_Z_BITS;

   public BlockPos(int x, int y, int z) {
      super(x, y, z);
   }

   public BlockPos(double x, double y, double z) {
      super(x, y, z);
   }

   public BlockPos(Entity source) {
      this(source.posX, source.posY, source.posZ);
   }

   public BlockPos(Vec3d vec) {
      this(vec.x, vec.y, vec.z);
   }

   public BlockPos(IPosition p_i50799_1_) {
      this(p_i50799_1_.getX(), p_i50799_1_.getY(), p_i50799_1_.getZ());
   }

   public BlockPos(Vec3i source) {
      this(source.getX(), source.getY(), source.getZ());
   }

   public static <T> BlockPos deserialize(Dynamic<T> p_218286_0_) {
      OfInt ofint = p_218286_0_.asIntStream().spliterator();
      int[] aint = new int[3];
      if (ofint.tryAdvance((Integer p_218285_1_) -> {
         aint[0] = p_218285_1_;
      }) && ofint.tryAdvance((Integer p_218280_1_) -> {
         aint[1] = p_218280_1_;
      })) {
         ofint.tryAdvance((Integer p_218284_1_) -> {
            aint[2] = p_218284_1_;
         });
      }

      return new BlockPos(aint[0], aint[1], aint[2]);
   }

   public <T> T serialize(DynamicOps<T> p_218175_1_) {
      return p_218175_1_.createIntList(IntStream.of(this.getX(), this.getY(), this.getZ()));
   }

   public static long offset(long pos, Direction p_218289_2_) {
      return offset(pos, p_218289_2_.getXOffset(), p_218289_2_.getYOffset(), p_218289_2_.getZOffset());
   }

   public static long offset(long pos, int dx, int dy, int dz) {
      return pack(unpackX(pos) + dx, unpackY(pos) + dy, unpackZ(pos) + dz);
   }

   public static int unpackX(long p_218290_0_) {
      return (int)(p_218290_0_ << 64 - field_218293_k - NUM_X_BITS >> 64 - NUM_X_BITS);
   }

   public static int unpackY(long p_218274_0_) {
      return (int)(p_218274_0_ << 64 - NUM_Y_BITS >> 64 - NUM_Y_BITS);
   }

   public static int unpackZ(long p_218282_0_) {
      return (int)(p_218282_0_ << 64 - field_218292_j - NUM_Z_BITS >> 64 - NUM_Z_BITS);
   }

   public static BlockPos fromLong(long p_218283_0_) {
      return new BlockPos(unpackX(p_218283_0_), unpackY(p_218283_0_), unpackZ(p_218283_0_));
   }

   public static long pack(int p_218276_0_, int p_218276_1_, int p_218276_2_) {
      long i = 0L;
      i = i | ((long)p_218276_0_ & X_MASK) << field_218293_k;
      i = i | ((long)p_218276_1_ & Y_MASK) << 0;
      i = i | ((long)p_218276_2_ & Z_MASK) << field_218292_j;
      return i;
   }

   public static long func_218288_f(long p_218288_0_) {
      return p_218288_0_ & -16L;
   }

   public long toLong() {
      return pack(this.getX(), this.getY(), this.getZ());
   }

   /**
    * Add the given coordinates to the coordinates of this BlockPos
    */
   public BlockPos add(double x, double y, double z) {
      return x == 0.0D && y == 0.0D && z == 0.0D ? this : new BlockPos((double)this.getX() + x, (double)this.getY() + y, (double)this.getZ() + z);
   }

   /**
    * Add the given coordinates to the coordinates of this BlockPos
    */
   public BlockPos add(int x, int y, int z) {
      return x == 0 && y == 0 && z == 0 ? this : new BlockPos(this.getX() + x, this.getY() + y, this.getZ() + z);
   }

   /**
    * Add the given Vector to this BlockPos
    */
   public BlockPos add(Vec3i vec) {
      return this.add(vec.getX(), vec.getY(), vec.getZ());
   }

   /**
    * Subtract the given Vector from this BlockPos
    */
   public BlockPos subtract(Vec3i vec) {
      return this.add(-vec.getX(), -vec.getY(), -vec.getZ());
   }

   /**
    * Offset this BlockPos 1 block up
    */
   public BlockPos up() {
      return this.up(1);
   }

   /**
    * Offset this BlockPos n blocks up
    */
   public BlockPos up(int n) {
      return this.offset(Direction.UP, n);
   }

   /**
    * Offset this BlockPos 1 block down
    */
   public BlockPos down() {
      return this.down(1);
   }

   /**
    * Offset this BlockPos n blocks down
    */
   public BlockPos down(int n) {
      return this.offset(Direction.DOWN, n);
   }

   /**
    * Offset this BlockPos 1 block in northern direction
    */
   public BlockPos north() {
      return this.north(1);
   }

   /**
    * Offset this BlockPos n blocks in northern direction
    */
   public BlockPos north(int n) {
      return this.offset(Direction.NORTH, n);
   }

   /**
    * Offset this BlockPos 1 block in southern direction
    */
   public BlockPos south() {
      return this.south(1);
   }

   /**
    * Offset this BlockPos n blocks in southern direction
    */
   public BlockPos south(int n) {
      return this.offset(Direction.SOUTH, n);
   }

   /**
    * Offset this BlockPos 1 block in western direction
    */
   public BlockPos west() {
      return this.west(1);
   }

   /**
    * Offset this BlockPos n blocks in western direction
    */
   public BlockPos west(int n) {
      return this.offset(Direction.WEST, n);
   }

   /**
    * Offset this BlockPos 1 block in eastern direction
    */
   public BlockPos east() {
      return this.east(1);
   }

   /**
    * Offset this BlockPos n blocks in eastern direction
    */
   public BlockPos east(int n) {
      return this.offset(Direction.EAST, n);
   }

   /**
    * Offset this BlockPos 1 block in the given direction
    */
   public BlockPos offset(Direction facing) {
      return this.offset(facing, 1);
   }

   /**
    * Offsets this BlockPos n blocks in the given direction
    */
   public BlockPos offset(Direction facing, int n) {
      return n == 0 ? this : new BlockPos(this.getX() + facing.getXOffset() * n, this.getY() + facing.getYOffset() * n, this.getZ() + facing.getZOffset() * n);
   }

   public BlockPos rotate(Rotation rotationIn) {
      switch(rotationIn) {
      case NONE:
      default:
         return this;
      case CLOCKWISE_90:
         return new BlockPos(-this.getZ(), this.getY(), this.getX());
      case CLOCKWISE_180:
         return new BlockPos(-this.getX(), this.getY(), -this.getZ());
      case COUNTERCLOCKWISE_90:
         return new BlockPos(this.getZ(), this.getY(), -this.getX());
      }
   }

   /**
    * Calculate the cross product of this and the given Vector
    */
   public BlockPos crossProduct(Vec3i vec) {
      return new BlockPos(this.getY() * vec.getZ() - this.getZ() * vec.getY(), this.getZ() * vec.getX() - this.getX() * vec.getZ(), this.getX() * vec.getY() - this.getY() * vec.getX());
   }

   /**
    * Returns a version of this BlockPos that is guaranteed to be immutable.
    *  
    * <p>When storing a BlockPos given to you for an extended period of time, make sure you
    * use this in case the value is changed internally.</p>
    */
   public BlockPos toImmutable() {
      return this;
   }

   public static Iterable<BlockPos> getAllInBoxMutable(BlockPos p_218278_0_, BlockPos p_218278_1_) {
      return getAllInBoxMutable(Math.min(p_218278_0_.getX(), p_218278_1_.getX()), Math.min(p_218278_0_.getY(), p_218278_1_.getY()), Math.min(p_218278_0_.getZ(), p_218278_1_.getZ()), Math.max(p_218278_0_.getX(), p_218278_1_.getX()), Math.max(p_218278_0_.getY(), p_218278_1_.getY()), Math.max(p_218278_0_.getZ(), p_218278_1_.getZ()));
   }

   public static Stream<BlockPos> getAllInBox(BlockPos p_218281_0_, BlockPos p_218281_1_) {
      return getAllInBox(Math.min(p_218281_0_.getX(), p_218281_1_.getX()), Math.min(p_218281_0_.getY(), p_218281_1_.getY()), Math.min(p_218281_0_.getZ(), p_218281_1_.getZ()), Math.max(p_218281_0_.getX(), p_218281_1_.getX()), Math.max(p_218281_0_.getY(), p_218281_1_.getY()), Math.max(p_218281_0_.getZ(), p_218281_1_.getZ()));
   }

   public static Stream<BlockPos> getAllInBox(final int p_218287_0_, final int p_218287_1_, final int p_218287_2_, final int p_218287_3_, final int p_218287_4_, final int p_218287_5_) {
      return StreamSupport.stream(new AbstractSpliterator<BlockPos>((long)((p_218287_3_ - p_218287_0_ + 1) * (p_218287_4_ - p_218287_1_ + 1) * (p_218287_5_ - p_218287_2_ + 1)), 64) {
         final CubeCoordinateIterator iter = new CubeCoordinateIterator(p_218287_0_, p_218287_1_, p_218287_2_, p_218287_3_, p_218287_4_, p_218287_5_);
         final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

         public boolean tryAdvance(Consumer<? super BlockPos> p_tryAdvance_1_) {
            if (this.iter.hasNext()) {
               p_tryAdvance_1_.accept(this.pos.setPos(this.iter.getX(), this.iter.getY(), this.iter.getZ()));
               return true;
            } else {
               return false;
            }
         }
      }, false);
   }

   /**
    * Creates an Iterable that returns all positions in the box specified by the given corners. <strong>Coordinates must
    * be in order</strong>; e.g. x1 <= x2.
    *  
    * This method uses {@link BlockPos.MutableBlockPos MutableBlockPos} instead of regular BlockPos, which grants better
    * performance. However, the resulting BlockPos instances can only be used inside the iteration loop (as otherwise
    * the value will change), unless {@link #toImmutable()} is called. This method is ideal for searching large areas
    * and only storing a few locations.
    *  
    * @see #getAllInBox(BlockPos, BlockPos)
    * @see #getAllInBox(int, int, int, int, int, int)
    * @see #getAllInBoxMutable(BlockPos, BlockPos)
    */
   public static Iterable<BlockPos> getAllInBoxMutable(int x1, int y1, int z1, int x2, int y2, int z2) {
      return () -> {
         return new AbstractIterator<BlockPos>() {
            final CubeCoordinateIterator field_218298_a = new CubeCoordinateIterator(x1, y1, z1, x2, y2, z2);
            final BlockPos.MutableBlockPos field_218299_b = new BlockPos.MutableBlockPos();

            protected BlockPos computeNext() {
               return (BlockPos)(this.field_218298_a.hasNext() ? this.field_218299_b.setPos(this.field_218298_a.getX(), this.field_218298_a.getY(), this.field_218298_a.getZ()) : this.endOfData());
            }
         };
      };
   }

   public static class MutableBlockPos extends BlockPos {
      protected int x;
      protected int y;
      protected int z;

      public MutableBlockPos() {
         this(0, 0, 0);
      }

      public MutableBlockPos(BlockPos pos) {
         this(pos.getX(), pos.getY(), pos.getZ());
      }

      public MutableBlockPos(int x_, int y_, int z_) {
         super(0, 0, 0);
         this.x = x_;
         this.y = y_;
         this.z = z_;
      }

      public MutableBlockPos(double p_i50824_1_, double p_i50824_3_, double p_i50824_5_) {
         this(MathHelper.floor(p_i50824_1_), MathHelper.floor(p_i50824_3_), MathHelper.floor(p_i50824_5_));
      }

      /**
       * Add the given coordinates to the coordinates of this BlockPos
       */
      public BlockPos add(double x, double y, double z) {
         return super.add(x, y, z).toImmutable();
      }

      /**
       * Add the given coordinates to the coordinates of this BlockPos
       */
      public BlockPos add(int x, int y, int z) {
         return super.add(x, y, z).toImmutable();
      }

      /**
       * Offsets this BlockPos n blocks in the given direction
       */
      public BlockPos offset(Direction facing, int n) {
         return super.offset(facing, n).toImmutable();
      }

      public BlockPos rotate(Rotation rotationIn) {
         return super.rotate(rotationIn).toImmutable();
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
       * None
       */
      public BlockPos.MutableBlockPos setPos(int xIn, int yIn, int zIn) {
         this.x = xIn;
         this.y = yIn;
         this.z = zIn;
         return this;
      }

      public BlockPos.MutableBlockPos setPos(Entity entityIn) {
         return this.setPos(entityIn.posX, entityIn.posY, entityIn.posZ);
      }

      public BlockPos.MutableBlockPos setPos(double xIn, double yIn, double zIn) {
         return this.setPos(MathHelper.floor(xIn), MathHelper.floor(yIn), MathHelper.floor(zIn));
      }

      public BlockPos.MutableBlockPos setPos(Vec3i vec) {
         return this.setPos(vec.getX(), vec.getY(), vec.getZ());
      }

      public BlockPos.MutableBlockPos setPos(long p_218294_1_) {
         return this.setPos(unpackX(p_218294_1_), unpackY(p_218294_1_), unpackZ(p_218294_1_));
      }

      public BlockPos.MutableBlockPos func_218295_a(AxisRotation p_218295_1_, int p_218295_2_, int p_218295_3_, int p_218295_4_) {
         return this.setPos(p_218295_1_.getCoordinate(p_218295_2_, p_218295_3_, p_218295_4_, Direction.Axis.X), p_218295_1_.getCoordinate(p_218295_2_, p_218295_3_, p_218295_4_, Direction.Axis.Y), p_218295_1_.getCoordinate(p_218295_2_, p_218295_3_, p_218295_4_, Direction.Axis.Z));
      }

      public BlockPos.MutableBlockPos move(Direction facing) {
         return this.move(facing, 1);
      }

      public BlockPos.MutableBlockPos move(Direction facing, int n) {
         return this.setPos(this.x + facing.getXOffset() * n, this.y + facing.getYOffset() * n, this.z + facing.getZOffset() * n);
      }

      public BlockPos.MutableBlockPos move(int xIn, int yIn, int zIn) {
         return this.setPos(this.x + xIn, this.y + yIn, this.z + zIn);
      }

      public void func_223471_o(int p_223471_1_) {
         this.x = p_223471_1_;
      }

      public void setY(int yIn) {
         this.y = yIn;
      }

      public void func_223472_q(int p_223472_1_) {
         this.z = p_223472_1_;
      }

      /**
       * Returns a version of this BlockPos that is guaranteed to be immutable.
       *  
       * <p>When storing a BlockPos given to you for an extended period of time, make sure you
       * use this in case the value is changed internally.</p>
       */
      public BlockPos toImmutable() {
         return new BlockPos(this);
      }
   }

   public static final class PooledMutableBlockPos extends BlockPos.MutableBlockPos implements AutoCloseable {
      private boolean released;
      private static final List<BlockPos.PooledMutableBlockPos> POOL = Lists.newArrayList();

      private PooledMutableBlockPos(int xIn, int yIn, int zIn) {
         super(xIn, yIn, zIn);
      }

      public static BlockPos.PooledMutableBlockPos retain() {
         return retain(0, 0, 0);
      }

      public static BlockPos.PooledMutableBlockPos retain(Entity entityIn) {
         return retain(entityIn.posX, entityIn.posY, entityIn.posZ);
      }

      public static BlockPos.PooledMutableBlockPos retain(double xIn, double yIn, double zIn) {
         return retain(MathHelper.floor(xIn), MathHelper.floor(yIn), MathHelper.floor(zIn));
      }

      public static BlockPos.PooledMutableBlockPos retain(int xIn, int yIn, int zIn) {
         synchronized(POOL) {
            if (!POOL.isEmpty()) {
               BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = POOL.remove(POOL.size() - 1);
               if (blockpos$pooledmutableblockpos != null && blockpos$pooledmutableblockpos.released) {
                  blockpos$pooledmutableblockpos.released = false;
                  blockpos$pooledmutableblockpos.setPos(xIn, yIn, zIn);
                  return blockpos$pooledmutableblockpos;
               }
            }
         }

         return new BlockPos.PooledMutableBlockPos(xIn, yIn, zIn);
      }

      /**
       * None
       */
      public BlockPos.PooledMutableBlockPos setPos(int xIn, int yIn, int zIn) {
         return (BlockPos.PooledMutableBlockPos)super.setPos(xIn, yIn, zIn);
      }

      public BlockPos.PooledMutableBlockPos setPos(Entity entityIn) {
         return (BlockPos.PooledMutableBlockPos)super.setPos(entityIn);
      }

      public BlockPos.PooledMutableBlockPos setPos(double xIn, double yIn, double zIn) {
         return (BlockPos.PooledMutableBlockPos)super.setPos(xIn, yIn, zIn);
      }

      public BlockPos.PooledMutableBlockPos setPos(Vec3i vec) {
         return (BlockPos.PooledMutableBlockPos)super.setPos(vec);
      }

      public BlockPos.PooledMutableBlockPos move(Direction facing) {
         return (BlockPos.PooledMutableBlockPos)super.move(facing);
      }

      public BlockPos.PooledMutableBlockPos move(Direction facing, int n) {
         return (BlockPos.PooledMutableBlockPos)super.move(facing, n);
      }

      public BlockPos.PooledMutableBlockPos move(int xIn, int yIn, int zIn) {
         return (BlockPos.PooledMutableBlockPos)super.move(xIn, yIn, zIn);
      }

      public void close() {
         synchronized(POOL) {
            if (POOL.size() < 100) {
               POOL.add(this);
            }

            this.released = true;
         }
      }
   }
}