package net.minecraft.pathfinding;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.entity.MobEntity;
import net.minecraft.world.IWorldReader;

public class PathFinder {
   private final PathHeap path = new PathHeap();
   private final Set<PathPoint> closedSet = Sets.newHashSet();
   private final PathPoint[] pathOptions = new PathPoint[32];
   private final int field_215751_d;
   private NodeProcessor nodeProcessor;

   public PathFinder(NodeProcessor p_i51280_1_, int p_i51280_2_) {
      this.nodeProcessor = p_i51280_1_;
      this.field_215751_d = p_i51280_2_;
   }

   @Nullable
   public Path findPath(IWorldReader worldIn, MobEntity entitylivingIn, double x, double y, double z, float maxDistance) {
      this.path.clearPath();
      this.nodeProcessor.init(worldIn, entitylivingIn);
      PathPoint pathpoint = this.nodeProcessor.getStart();
      PathPoint pathpoint1 = this.nodeProcessor.getPathPointToCoords(x, y, z);
      Path path = this.findPath(pathpoint, pathpoint1, maxDistance);
      this.nodeProcessor.postProcess();
      return path;
   }

   @Nullable
   private Path findPath(PathPoint pathFrom, PathPoint pathTo, float maxDistance) {
      pathFrom.totalPathDistance = 0.0F;
      pathFrom.distanceToNext = pathFrom.distanceTo(pathTo);
      pathFrom.distanceToTarget = pathFrom.distanceToNext;
      this.path.clearPath();
      this.closedSet.clear();
      this.path.addPoint(pathFrom);
      PathPoint pathpoint = pathFrom;
      int i = 0;

      while(!this.path.isPathEmpty()) {
         ++i;
         if (i >= this.field_215751_d) {
            break;
         }

         PathPoint pathpoint1 = this.path.dequeue();
         pathpoint1.visited = true;
         if (pathpoint1.equals(pathTo)) {
            pathpoint = pathTo;
            break;
         }

         if (pathpoint1.distanceTo(pathTo) < pathpoint.distanceTo(pathTo)) {
            pathpoint = pathpoint1;
         }

         if (!(pathpoint1.distanceTo(pathTo) >= maxDistance)) {
            int j = this.nodeProcessor.func_222859_a(this.pathOptions, pathpoint1);

            for(int k = 0; k < j; ++k) {
               PathPoint pathpoint2 = this.pathOptions[k];
               float f = pathpoint1.distanceTo(pathpoint2);
               pathpoint2.field_222861_j = pathpoint1.field_222861_j + f;
               float f1 = pathpoint1.totalPathDistance + f + pathpoint2.costMalus;
               if (pathpoint2.field_222861_j < maxDistance && (!pathpoint2.isAssigned() || f1 < pathpoint2.totalPathDistance)) {
                  pathpoint2.previous = pathpoint1;
                  pathpoint2.totalPathDistance = f1;
                  pathpoint2.distanceToNext = pathpoint2.distanceTo(pathTo) * 1.5F + pathpoint2.costMalus;
                  if (pathpoint2.isAssigned()) {
                     this.path.changeDistance(pathpoint2, pathpoint2.totalPathDistance + pathpoint2.distanceToNext);
                  } else {
                     pathpoint2.distanceToTarget = pathpoint2.totalPathDistance + pathpoint2.distanceToNext;
                     this.path.addPoint(pathpoint2);
                  }
               }
            }
         }
      }

      if (pathpoint.equals(pathFrom)) {
         return null;
      } else {
         Path path = this.func_215750_a(pathpoint);
         return path;
      }
   }

   private Path func_215750_a(PathPoint p_215750_1_) {
      List<PathPoint> list = Lists.newArrayList();
      PathPoint pathpoint = p_215750_1_;
      list.add(0, p_215750_1_);

      while(pathpoint.previous != null) {
         pathpoint = pathpoint.previous;
         list.add(0, pathpoint);
      }

      return new Path(list);
   }
}