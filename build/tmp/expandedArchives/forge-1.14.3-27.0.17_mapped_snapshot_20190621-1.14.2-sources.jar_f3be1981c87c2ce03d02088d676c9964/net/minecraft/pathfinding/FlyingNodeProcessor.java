package net.minecraft.pathfinding;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;

public class FlyingNodeProcessor extends WalkNodeProcessor {
   public void init(IWorldReader sourceIn, MobEntity mob) {
      super.init(sourceIn, mob);
      this.avoidsWater = mob.getPathPriority(PathNodeType.WATER);
   }

   /**
    * This method is called when all nodes have been processed and PathEntity is created.
    * {@link net.minecraft.world.pathfinder.WalkNodeProcessor WalkNodeProcessor} uses this to change its field {@link
    * net.minecraft.world.pathfinder.WalkNodeProcessor#avoidsWater avoidsWater}
    */
   public void postProcess() {
      this.entity.setPathPriority(PathNodeType.WATER, this.avoidsWater);
      super.postProcess();
   }

   public PathPoint getStart() {
      int i;
      if (this.getCanSwim() && this.entity.isInWater()) {
         i = MathHelper.floor(this.entity.getBoundingBox().minY);
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(this.entity.posX, (double)i, this.entity.posZ);

         for(Block block = this.blockaccess.getBlockState(blockpos$mutableblockpos).getBlock(); block == Blocks.WATER; block = this.blockaccess.getBlockState(blockpos$mutableblockpos).getBlock()) {
            ++i;
            blockpos$mutableblockpos.setPos(this.entity.posX, (double)i, this.entity.posZ);
         }
      } else {
         i = MathHelper.floor(this.entity.getBoundingBox().minY + 0.5D);
      }

      BlockPos blockpos1 = new BlockPos(this.entity);
      PathNodeType pathnodetype1 = this.getPathNodeType(this.entity, blockpos1.getX(), i, blockpos1.getZ());
      if (this.entity.getPathPriority(pathnodetype1) < 0.0F) {
         Set<BlockPos> set = Sets.newHashSet();
         set.add(new BlockPos(this.entity.getBoundingBox().minX, (double)i, this.entity.getBoundingBox().minZ));
         set.add(new BlockPos(this.entity.getBoundingBox().minX, (double)i, this.entity.getBoundingBox().maxZ));
         set.add(new BlockPos(this.entity.getBoundingBox().maxX, (double)i, this.entity.getBoundingBox().minZ));
         set.add(new BlockPos(this.entity.getBoundingBox().maxX, (double)i, this.entity.getBoundingBox().maxZ));

         for(BlockPos blockpos : set) {
            PathNodeType pathnodetype = this.getPathNodeType(this.entity, blockpos);
            if (this.entity.getPathPriority(pathnodetype) >= 0.0F) {
               return super.openPoint(blockpos.getX(), blockpos.getY(), blockpos.getZ());
            }
         }
      }

      return super.openPoint(blockpos1.getX(), i, blockpos1.getZ());
   }

   /**
    * Returns PathPoint for given coordinates
    */
   public PathPoint getPathPointToCoords(double x, double y, double z) {
      return super.openPoint(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
   }

   public int func_222859_a(PathPoint[] p_222859_1_, PathPoint p_222859_2_) {
      int i = 0;
      PathPoint pathpoint = this.openPoint(p_222859_2_.x, p_222859_2_.y, p_222859_2_.z + 1);
      PathPoint pathpoint1 = this.openPoint(p_222859_2_.x - 1, p_222859_2_.y, p_222859_2_.z);
      PathPoint pathpoint2 = this.openPoint(p_222859_2_.x + 1, p_222859_2_.y, p_222859_2_.z);
      PathPoint pathpoint3 = this.openPoint(p_222859_2_.x, p_222859_2_.y, p_222859_2_.z - 1);
      PathPoint pathpoint4 = this.openPoint(p_222859_2_.x, p_222859_2_.y + 1, p_222859_2_.z);
      PathPoint pathpoint5 = this.openPoint(p_222859_2_.x, p_222859_2_.y - 1, p_222859_2_.z);
      if (pathpoint != null && !pathpoint.visited) {
         p_222859_1_[i++] = pathpoint;
      }

      if (pathpoint1 != null && !pathpoint1.visited) {
         p_222859_1_[i++] = pathpoint1;
      }

      if (pathpoint2 != null && !pathpoint2.visited) {
         p_222859_1_[i++] = pathpoint2;
      }

      if (pathpoint3 != null && !pathpoint3.visited) {
         p_222859_1_[i++] = pathpoint3;
      }

      if (pathpoint4 != null && !pathpoint4.visited) {
         p_222859_1_[i++] = pathpoint4;
      }

      if (pathpoint5 != null && !pathpoint5.visited) {
         p_222859_1_[i++] = pathpoint5;
      }

      boolean flag = pathpoint3 == null || pathpoint3.costMalus != 0.0F;
      boolean flag1 = pathpoint == null || pathpoint.costMalus != 0.0F;
      boolean flag2 = pathpoint2 == null || pathpoint2.costMalus != 0.0F;
      boolean flag3 = pathpoint1 == null || pathpoint1.costMalus != 0.0F;
      boolean flag4 = pathpoint4 == null || pathpoint4.costMalus != 0.0F;
      boolean flag5 = pathpoint5 == null || pathpoint5.costMalus != 0.0F;
      if (flag && flag3) {
         PathPoint pathpoint6 = this.openPoint(p_222859_2_.x - 1, p_222859_2_.y, p_222859_2_.z - 1);
         if (pathpoint6 != null && !pathpoint6.visited) {
            p_222859_1_[i++] = pathpoint6;
         }
      }

      if (flag && flag2) {
         PathPoint pathpoint7 = this.openPoint(p_222859_2_.x + 1, p_222859_2_.y, p_222859_2_.z - 1);
         if (pathpoint7 != null && !pathpoint7.visited) {
            p_222859_1_[i++] = pathpoint7;
         }
      }

      if (flag1 && flag3) {
         PathPoint pathpoint8 = this.openPoint(p_222859_2_.x - 1, p_222859_2_.y, p_222859_2_.z + 1);
         if (pathpoint8 != null && !pathpoint8.visited) {
            p_222859_1_[i++] = pathpoint8;
         }
      }

      if (flag1 && flag2) {
         PathPoint pathpoint9 = this.openPoint(p_222859_2_.x + 1, p_222859_2_.y, p_222859_2_.z + 1);
         if (pathpoint9 != null && !pathpoint9.visited) {
            p_222859_1_[i++] = pathpoint9;
         }
      }

      if (flag && flag4) {
         PathPoint pathpoint10 = this.openPoint(p_222859_2_.x, p_222859_2_.y + 1, p_222859_2_.z - 1);
         if (pathpoint10 != null && !pathpoint10.visited) {
            p_222859_1_[i++] = pathpoint10;
         }
      }

      if (flag1 && flag4) {
         PathPoint pathpoint11 = this.openPoint(p_222859_2_.x, p_222859_2_.y + 1, p_222859_2_.z + 1);
         if (pathpoint11 != null && !pathpoint11.visited) {
            p_222859_1_[i++] = pathpoint11;
         }
      }

      if (flag2 && flag4) {
         PathPoint pathpoint12 = this.openPoint(p_222859_2_.x + 1, p_222859_2_.y + 1, p_222859_2_.z);
         if (pathpoint12 != null && !pathpoint12.visited) {
            p_222859_1_[i++] = pathpoint12;
         }
      }

      if (flag3 && flag4) {
         PathPoint pathpoint13 = this.openPoint(p_222859_2_.x - 1, p_222859_2_.y + 1, p_222859_2_.z);
         if (pathpoint13 != null && !pathpoint13.visited) {
            p_222859_1_[i++] = pathpoint13;
         }
      }

      if (flag && flag5) {
         PathPoint pathpoint14 = this.openPoint(p_222859_2_.x, p_222859_2_.y - 1, p_222859_2_.z - 1);
         if (pathpoint14 != null && !pathpoint14.visited) {
            p_222859_1_[i++] = pathpoint14;
         }
      }

      if (flag1 && flag5) {
         PathPoint pathpoint15 = this.openPoint(p_222859_2_.x, p_222859_2_.y - 1, p_222859_2_.z + 1);
         if (pathpoint15 != null && !pathpoint15.visited) {
            p_222859_1_[i++] = pathpoint15;
         }
      }

      if (flag2 && flag5) {
         PathPoint pathpoint16 = this.openPoint(p_222859_2_.x + 1, p_222859_2_.y - 1, p_222859_2_.z);
         if (pathpoint16 != null && !pathpoint16.visited) {
            p_222859_1_[i++] = pathpoint16;
         }
      }

      if (flag3 && flag5) {
         PathPoint pathpoint17 = this.openPoint(p_222859_2_.x - 1, p_222859_2_.y - 1, p_222859_2_.z);
         if (pathpoint17 != null && !pathpoint17.visited) {
            p_222859_1_[i++] = pathpoint17;
         }
      }

      return i;
   }

   /**
    * Returns a mapped point or creates and adds one
    */
   @Nullable
   protected PathPoint openPoint(int x, int y, int z) {
      PathPoint pathpoint = null;
      PathNodeType pathnodetype = this.getPathNodeType(this.entity, x, y, z);
      float f = this.entity.getPathPriority(pathnodetype);
      if (f >= 0.0F) {
         pathpoint = super.openPoint(x, y, z);
         pathpoint.nodeType = pathnodetype;
         pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
         if (pathnodetype == PathNodeType.WALKABLE) {
            ++pathpoint.costMalus;
         }
      }

      return pathnodetype != PathNodeType.OPEN && pathnodetype != PathNodeType.WALKABLE ? pathpoint : pathpoint;
   }

   public PathNodeType getPathNodeType(IBlockReader blockaccessIn, int x, int y, int z, MobEntity entitylivingIn, int xSize, int ySize, int zSize, boolean canBreakDoorsIn, boolean canEnterDoorsIn) {
      EnumSet<PathNodeType> enumset = EnumSet.noneOf(PathNodeType.class);
      PathNodeType pathnodetype = PathNodeType.BLOCKED;
      BlockPos blockpos = new BlockPos(entitylivingIn);
      this.currentEntity = entitylivingIn;
      pathnodetype = this.getPathNodeType(blockaccessIn, x, y, z, xSize, ySize, zSize, canBreakDoorsIn, canEnterDoorsIn, enumset, pathnodetype, blockpos);
      this.currentEntity = null;
      if (enumset.contains(PathNodeType.FENCE)) {
         return PathNodeType.FENCE;
      } else {
         PathNodeType pathnodetype1 = PathNodeType.BLOCKED;

         for(PathNodeType pathnodetype2 : enumset) {
            if (entitylivingIn.getPathPriority(pathnodetype2) < 0.0F) {
               return pathnodetype2;
            }

            if (entitylivingIn.getPathPriority(pathnodetype2) >= entitylivingIn.getPathPriority(pathnodetype1)) {
               pathnodetype1 = pathnodetype2;
            }
         }

         if (pathnodetype == PathNodeType.OPEN && entitylivingIn.getPathPriority(pathnodetype1) == 0.0F) {
            return PathNodeType.OPEN;
         } else {
            return pathnodetype1;
         }
      }
   }

   public PathNodeType getPathNodeType(IBlockReader blockaccessIn, int x, int y, int z) {
      PathNodeType pathnodetype = this.getPathNodeTypeRaw(blockaccessIn, x, y, z);
      if (pathnodetype == PathNodeType.OPEN && y >= 1) {
         Block block = blockaccessIn.getBlockState(new BlockPos(x, y - 1, z)).getBlock();
         PathNodeType pathnodetype1 = this.getPathNodeTypeRaw(blockaccessIn, x, y - 1, z);
         if (pathnodetype1 != PathNodeType.DAMAGE_FIRE && block != Blocks.MAGMA_BLOCK && pathnodetype1 != PathNodeType.LAVA && block != Blocks.CAMPFIRE) {
            if (pathnodetype1 == PathNodeType.DAMAGE_CACTUS) {
               pathnodetype = PathNodeType.DAMAGE_CACTUS;
            } else if (pathnodetype1 == PathNodeType.DAMAGE_OTHER) { // Forge: consider modded damage types
               pathnodetype = PathNodeType.DAMAGE_OTHER;
            } else if (pathnodetype1 == PathNodeType.DAMAGE_OTHER) {
               pathnodetype = PathNodeType.DAMAGE_OTHER;
            } else {
               pathnodetype = pathnodetype1 != PathNodeType.WALKABLE && pathnodetype1 != PathNodeType.OPEN && pathnodetype1 != PathNodeType.WATER ? PathNodeType.WALKABLE : PathNodeType.OPEN;
            }
         } else {
            pathnodetype = PathNodeType.DAMAGE_FIRE;
         }
      }

      pathnodetype = this.checkNeighborBlocks(blockaccessIn, x, y, z, pathnodetype);
      return pathnodetype;
   }

   private PathNodeType getPathNodeType(MobEntity p_192559_1_, BlockPos p_192559_2_) {
      return this.getPathNodeType(p_192559_1_, p_192559_2_.getX(), p_192559_2_.getY(), p_192559_2_.getZ());
   }

   private PathNodeType getPathNodeType(MobEntity p_192558_1_, int p_192558_2_, int p_192558_3_, int p_192558_4_) {
      return this.getPathNodeType(this.blockaccess, p_192558_2_, p_192558_3_, p_192558_4_, p_192558_1_, this.entitySizeX, this.entitySizeY, this.entitySizeZ, this.getCanOpenDoors(), this.getCanEnterDoors());
   }
}