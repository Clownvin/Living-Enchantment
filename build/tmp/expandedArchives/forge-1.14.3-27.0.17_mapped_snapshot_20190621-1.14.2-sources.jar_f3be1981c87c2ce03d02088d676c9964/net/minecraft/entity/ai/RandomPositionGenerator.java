package net.minecraft.entity.ai;

import java.util.Random;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RandomPositionGenerator {
   /**
    * finds a random target within par1(x,z) and par2 (y) blocks
    */
   @Nullable
   public static Vec3d findRandomTarget(CreatureEntity entitycreatureIn, int xz, int y) {
      return findRandomTargetBlock(entitycreatureIn, xz, y, (Vec3d)null);
   }

   @Nullable
   public static Vec3d getLandPos(CreatureEntity creature, int maxXZ, int maxY) {
      return func_221024_a(creature, maxXZ, maxY, creature::getBlockPathWeight);
   }

   @Nullable
   public static Vec3d func_221024_a(CreatureEntity p_221024_0_, int p_221024_1_, int p_221024_2_, ToDoubleFunction<BlockPos> p_221024_3_) {
      return generateRandomPos(p_221024_0_, p_221024_1_, p_221024_2_, (Vec3d)null, false, 0.0D, p_221024_3_);
   }

   /**
    * finds a random target within par1(x,z) and par2 (y) blocks in the direction of the point par3
    */
   @Nullable
   public static Vec3d findRandomTargetBlockTowards(CreatureEntity entitycreatureIn, int xz, int y, Vec3d targetVec3) {
      Vec3d vec3d = targetVec3.subtract(entitycreatureIn.posX, entitycreatureIn.posY, entitycreatureIn.posZ);
      return findRandomTargetBlock(entitycreatureIn, xz, y, vec3d);
   }

   @Nullable
   public static Vec3d findRandomTargetTowardsScaled(CreatureEntity p_203155_0_, int xz, int p_203155_2_, Vec3d p_203155_3_, double p_203155_4_) {
      Vec3d vec3d = p_203155_3_.subtract(p_203155_0_.posX, p_203155_0_.posY, p_203155_0_.posZ);
      return generateRandomPos(p_203155_0_, xz, p_203155_2_, vec3d, true, p_203155_4_, p_203155_0_::getBlockPathWeight);
   }

   @Nullable
   public static Vec3d func_223548_b(CreatureEntity p_223548_0_, int p_223548_1_, int p_223548_2_, Vec3d p_223548_3_) {
      Vec3d vec3d = (new Vec3d(p_223548_0_.posX, p_223548_0_.posY, p_223548_0_.posZ)).subtract(p_223548_3_);
      return generateRandomPos(p_223548_0_, p_223548_1_, p_223548_2_, vec3d, false, (double)((float)Math.PI / 2F), p_223548_0_::getBlockPathWeight);
   }

   /**
    * finds a random target within par1(x,z) and par2 (y) blocks in the reverse direction of the point par3
    */
   @Nullable
   public static Vec3d findRandomTargetBlockAwayFrom(CreatureEntity entitycreatureIn, int xz, int y, Vec3d targetVec3) {
      Vec3d vec3d = (new Vec3d(entitycreatureIn.posX, entitycreatureIn.posY, entitycreatureIn.posZ)).subtract(targetVec3);
      return findRandomTargetBlock(entitycreatureIn, xz, y, vec3d);
   }

   /**
    * searches 10 blocks at random in a within par1(x,z) and par2 (y) distance, ignores those not in the direction of
    * par3Vec3, then points to the tile for which creature.getBlockPathWeight returns the highest number
    */
   @Nullable
   private static Vec3d findRandomTargetBlock(CreatureEntity entitycreatureIn, int xz, int y, @Nullable Vec3d targetVec3) {
      return generateRandomPos(entitycreatureIn, xz, y, targetVec3, true, (double)((float)Math.PI / 2F), entitycreatureIn::getBlockPathWeight);
   }

   @Nullable
   private static Vec3d generateRandomPos(CreatureEntity creature, int p_191379_1_, int p_191379_2_, @Nullable Vec3d p_191379_3_, boolean p_191379_4_, double p_191379_5_, ToDoubleFunction<BlockPos> p_191379_7_) {
      PathNavigator pathnavigator = creature.getNavigator();
      Random random = creature.getRNG();
      boolean flag;
      if (creature.detachHome()) {
         flag = creature.getHomePosition().withinDistance(creature.getPositionVec(), (double)(creature.getMaximumHomeDistance() + (float)p_191379_1_) + 1.0D);
      } else {
         flag = false;
      }

      boolean flag1 = false;
      double d0 = Double.NEGATIVE_INFINITY;
      BlockPos blockpos = new BlockPos(creature);

      for(int i = 0; i < 10; ++i) {
         BlockPos blockpos1 = getBlockPos(random, p_191379_1_, p_191379_2_, p_191379_3_, p_191379_5_);
         if (blockpos1 != null) {
            int j = blockpos1.getX();
            int k = blockpos1.getY();
            int l = blockpos1.getZ();
            if (creature.detachHome() && p_191379_1_ > 1) {
               BlockPos blockpos2 = creature.getHomePosition();
               if (creature.posX > (double)blockpos2.getX()) {
                  j -= random.nextInt(p_191379_1_ / 2);
               } else {
                  j += random.nextInt(p_191379_1_ / 2);
               }

               if (creature.posZ > (double)blockpos2.getZ()) {
                  l -= random.nextInt(p_191379_1_ / 2);
               } else {
                  l += random.nextInt(p_191379_1_ / 2);
               }
            }

            BlockPos blockpos3 = new BlockPos((double)j + creature.posX, (double)k + creature.posY, (double)l + creature.posZ);
            if ((!flag || creature.isWithinHomeDistanceFromPosition(blockpos3)) && pathnavigator.canEntityStandOnPos(blockpos3)) {
               if (!p_191379_4_) {
                  blockpos3 = moveAboveSolid(blockpos3, creature);
                  if (isWaterDestination(blockpos3, creature)) {
                     continue;
                  }
               }

               double d1 = p_191379_7_.applyAsDouble(blockpos3);
               if (d1 > d0) {
                  d0 = d1;
                  blockpos = blockpos3;
                  flag1 = true;
               }
            }
         }
      }

      if (flag1) {
         return new Vec3d(blockpos);
      } else {
         return null;
      }
   }

   @Nullable
   private static BlockPos getBlockPos(Random p_203156_0_, int p_203156_1_, int p_203156_2_, @Nullable Vec3d p_203156_3_, double p_203156_4_) {
      if (p_203156_3_ != null && !(p_203156_4_ >= Math.PI)) {
         double d3 = MathHelper.atan2(p_203156_3_.z, p_203156_3_.x) - (double)((float)Math.PI / 2F);
         double d4 = d3 + (double)(2.0F * p_203156_0_.nextFloat() - 1.0F) * p_203156_4_;
         double d0 = Math.sqrt(p_203156_0_.nextDouble()) * (double)MathHelper.SQRT_2 * (double)p_203156_1_;
         double d1 = -d0 * Math.sin(d4);
         double d2 = d0 * Math.cos(d4);
         if (!(Math.abs(d1) > (double)p_203156_1_) && !(Math.abs(d2) > (double)p_203156_1_)) {
            int l = p_203156_0_.nextInt(2 * p_203156_2_ + 1) - p_203156_2_;
            return new BlockPos(d1, (double)l, d2);
         } else {
            return null;
         }
      } else {
         int i = p_203156_0_.nextInt(2 * p_203156_1_ + 1) - p_203156_1_;
         int j = p_203156_0_.nextInt(2 * p_203156_2_ + 1) - p_203156_2_;
         int k = p_203156_0_.nextInt(2 * p_203156_1_ + 1) - p_203156_1_;
         return new BlockPos(i, j, k);
      }
   }

   private static BlockPos moveAboveSolid(BlockPos p_191378_0_, CreatureEntity p_191378_1_) {
      if (!p_191378_1_.world.getBlockState(p_191378_0_).getMaterial().isSolid()) {
         return p_191378_0_;
      } else {
         BlockPos blockpos;
         for(blockpos = p_191378_0_.up(); blockpos.getY() < p_191378_1_.world.getHeight() && p_191378_1_.world.getBlockState(blockpos).getMaterial().isSolid(); blockpos = blockpos.up()) {
            ;
         }

         return blockpos;
      }
   }

   private static boolean isWaterDestination(BlockPos p_191380_0_, CreatureEntity p_191380_1_) {
      return p_191380_1_.world.getFluidState(p_191380_0_).isTagged(FluidTags.WATER);
   }
}