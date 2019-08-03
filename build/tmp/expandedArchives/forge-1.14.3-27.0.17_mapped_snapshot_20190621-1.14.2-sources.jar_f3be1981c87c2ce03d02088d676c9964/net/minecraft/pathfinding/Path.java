package net.minecraft.pathfinding;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Path {
   private final List<PathPoint> field_75884_a;
   private PathPoint[] openSet = new PathPoint[0];
   private PathPoint[] closedSet = new PathPoint[0];
   private PathPoint target;
   private int currentPathIndex;

   public Path(List<PathPoint> p_i51281_1_) {
      this.field_75884_a = p_i51281_1_;
   }

   /**
    * Directs this path to the next point in its array
    */
   public void incrementPathIndex() {
      ++this.currentPathIndex;
   }

   /**
    * Returns true if this path has reached the end
    */
   public boolean isFinished() {
      return this.currentPathIndex >= this.field_75884_a.size();
   }

   /**
    * returns the last PathPoint of the Array
    */
   @Nullable
   public PathPoint getFinalPathPoint() {
      return !this.field_75884_a.isEmpty() ? this.field_75884_a.get(this.field_75884_a.size() - 1) : null;
   }

   /**
    * return the PathPoint located at the specified PathIndex, usually the current one
    */
   public PathPoint getPathPointFromIndex(int index) {
      return this.field_75884_a.get(index);
   }

   public List<PathPoint> func_215746_d() {
      return this.field_75884_a;
   }

   public void func_215747_b(int p_215747_1_) {
      if (this.field_75884_a.size() > p_215747_1_) {
         this.field_75884_a.subList(p_215747_1_, this.field_75884_a.size()).clear();
      }

   }

   public void setPoint(int index, PathPoint point) {
      this.field_75884_a.set(index, point);
   }

   public int getCurrentPathLength() {
      return this.field_75884_a.size();
   }

   public int getCurrentPathIndex() {
      return this.currentPathIndex;
   }

   public void setCurrentPathIndex(int currentPathIndexIn) {
      this.currentPathIndex = currentPathIndexIn;
   }

   /**
    * Gets the vector of the PathPoint associated with the given index.
    */
   public Vec3d getVectorFromIndex(Entity entityIn, int index) {
      PathPoint pathpoint = this.field_75884_a.get(index);
      double d0 = (double)pathpoint.x + (double)((int)(entityIn.getWidth() + 1.0F)) * 0.5D;
      double d1 = (double)pathpoint.y;
      double d2 = (double)pathpoint.z + (double)((int)(entityIn.getWidth() + 1.0F)) * 0.5D;
      return new Vec3d(d0, d1, d2);
   }

   /**
    * returns the current PathEntity target node as Vec3D
    */
   public Vec3d getPosition(Entity entityIn) {
      return this.getVectorFromIndex(entityIn, this.currentPathIndex);
   }

   public Vec3d getCurrentPos() {
      PathPoint pathpoint = this.field_75884_a.get(this.currentPathIndex);
      return new Vec3d((double)pathpoint.x, (double)pathpoint.y, (double)pathpoint.z);
   }

   /**
    * Returns true if the EntityPath are the same. Non instance related equals.
    */
   public boolean isSamePath(@Nullable Path pathentityIn) {
      if (pathentityIn == null) {
         return false;
      } else if (pathentityIn.field_75884_a.size() != this.field_75884_a.size()) {
         return false;
      } else {
         for(int i = 0; i < this.field_75884_a.size(); ++i) {
            PathPoint pathpoint = this.field_75884_a.get(i);
            PathPoint pathpoint1 = pathentityIn.field_75884_a.get(i);
            if (pathpoint.x != pathpoint1.x || pathpoint.y != pathpoint1.y || pathpoint.z != pathpoint1.z) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean func_222862_a(BlockPos p_222862_1_) {
      PathPoint pathpoint = this.getFinalPathPoint();
      return pathpoint == null ? false : p_222862_1_.withinDistance(new Vec3i(pathpoint.x, pathpoint.y, pathpoint.z), 2.0D);
   }

   @OnlyIn(Dist.CLIENT)
   public PathPoint[] getOpenSet() {
      return this.openSet;
   }

   @OnlyIn(Dist.CLIENT)
   public PathPoint[] getClosedSet() {
      return this.closedSet;
   }

   @Nullable
   public PathPoint getTarget() {
      return this.target;
   }

   @OnlyIn(Dist.CLIENT)
   public static Path read(PacketBuffer buf) {
      int i = buf.readInt();
      PathPoint pathpoint = PathPoint.createFromBuffer(buf);
      List<PathPoint> list = Lists.newArrayList();
      int j = buf.readInt();

      for(int k = 0; k < j; ++k) {
         list.add(PathPoint.createFromBuffer(buf));
      }

      PathPoint[] apathpoint = new PathPoint[buf.readInt()];

      for(int l = 0; l < apathpoint.length; ++l) {
         apathpoint[l] = PathPoint.createFromBuffer(buf);
      }

      PathPoint[] apathpoint1 = new PathPoint[buf.readInt()];

      for(int i1 = 0; i1 < apathpoint1.length; ++i1) {
         apathpoint1[i1] = PathPoint.createFromBuffer(buf);
      }

      Path path = new Path(list);
      path.openSet = apathpoint;
      path.closedSet = apathpoint1;
      path.target = pathpoint;
      path.currentPathIndex = i;
      return path;
   }

   public String toString() {
      return "Path(length=" + this.field_75884_a.size() + ")";
   }
}