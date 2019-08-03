package net.minecraft.world;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

public interface IEntityReader {
   /**
    * Gets all entities within the specified AABB excluding the one passed into it.
    */
   List<Entity> getEntitiesInAABBexcluding(@Nullable Entity entityIn, AxisAlignedBB boundingBox, @Nullable Predicate<? super Entity> predicate);

   <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> clazz, AxisAlignedBB aabb, @Nullable Predicate<? super T> filter);

   List<? extends PlayerEntity> getPlayers();

   /**
    * Will get all entities within the specified AABB excluding the one passed into it. Args: entityToExclude, aabb
    */
   default List<Entity> getEntitiesWithinAABBExcludingEntity(@Nullable Entity entityIn, AxisAlignedBB bb) {
      return this.getEntitiesInAABBexcluding(entityIn, bb, EntityPredicates.NOT_SPECTATING);
   }

   default boolean checkNoEntityCollision(@Nullable Entity entityIn, VoxelShape shape) {
      return shape.isEmpty() ? true : this.getEntitiesWithinAABBExcludingEntity(entityIn, shape.getBoundingBox()).stream().filter((p_217364_1_) -> {
         return !p_217364_1_.removed && p_217364_1_.preventEntitySpawning && (entityIn == null || !p_217364_1_.isRidingSameEntity(entityIn));
      }).noneMatch((p_217356_1_) -> {
         return VoxelShapes.compare(shape, VoxelShapes.create(p_217356_1_.getBoundingBox()), IBooleanFunction.field_223238_i_);
      });
   }

   default <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> p_217357_1_, AxisAlignedBB p_217357_2_) {
      return this.getEntitiesWithinAABB(p_217357_1_, p_217357_2_, EntityPredicates.NOT_SPECTATING);
   }

   default Stream<VoxelShape> func_223439_a(@Nullable Entity p_223439_1_, AxisAlignedBB p_223439_2_, Set<Entity> p_223439_3_) {
      if (p_223439_2_.getAverageEdgeLength() < 1.0E-7D) {
         return Stream.empty();
      } else {
         AxisAlignedBB axisalignedbb = p_223439_2_.grow(1.0E-7D);
         return this.getEntitiesWithinAABBExcludingEntity(p_223439_1_, axisalignedbb).stream().filter((p_217367_1_) -> {
            return !p_223439_3_.contains(p_217367_1_);
         }).filter((p_223442_1_) -> {
            return p_223439_1_ == null || !p_223439_1_.isRidingSameEntity(p_223442_1_);
         }).flatMap((p_217368_1_) -> {
            return Stream.of(p_217368_1_.getCollisionBoundingBox(), p_223439_1_ == null ? null : p_223439_1_.getCollisionBox(p_217368_1_));
         }).filter(Objects::nonNull).filter(axisalignedbb::intersects).map(VoxelShapes::create);
      }
   }

   @Nullable
   default PlayerEntity getClosestPlayer(double x, double y, double z, double distance, @Nullable Predicate<Entity> predicate) {
      double d0 = -1.0D;
      PlayerEntity playerentity = null;

      for(PlayerEntity playerentity1 : this.getPlayers()) {
         if (predicate == null || predicate.test(playerentity1)) {
            double d1 = playerentity1.getDistanceSq(x, y, z);
            if ((distance < 0.0D || d1 < distance * distance) && (d0 == -1.0D || d1 < d0)) {
               d0 = d1;
               playerentity = playerentity1;
            }
         }
      }

      return playerentity;
   }

   @Nullable
   default PlayerEntity getClosestPlayer(Entity p_217362_1_, double distance) {
      return this.getClosestPlayer(p_217362_1_.posX, p_217362_1_.posY, p_217362_1_.posZ, distance, false);
   }

   @Nullable
   default PlayerEntity getClosestPlayer(double x, double y, double z, double distance, boolean creativePlayers) {
      Predicate<Entity> predicate = creativePlayers ? EntityPredicates.CAN_AI_TARGET : EntityPredicates.NOT_SPECTATING;
      return this.getClosestPlayer(x, y, z, distance, predicate);
   }

   @Nullable
   default PlayerEntity func_217365_a(double p_217365_1_, double p_217365_3_, double p_217365_5_) {
      double d0 = -1.0D;
      PlayerEntity playerentity = null;

      for(PlayerEntity playerentity1 : this.getPlayers()) {
         if (EntityPredicates.NOT_SPECTATING.test(playerentity1)) {
            double d1 = playerentity1.getDistanceSq(p_217365_1_, playerentity1.posY, p_217365_3_);
            if ((p_217365_5_ < 0.0D || d1 < p_217365_5_ * p_217365_5_) && (d0 == -1.0D || d1 < d0)) {
               d0 = d1;
               playerentity = playerentity1;
            }
         }
      }

      return playerentity;
   }

   default boolean isPlayerWithin(double x, double y, double z, double distance) {
      for(PlayerEntity playerentity : this.getPlayers()) {
         if (EntityPredicates.NOT_SPECTATING.test(playerentity) && EntityPredicates.IS_LIVING_ALIVE.test(playerentity)) {
            double d0 = playerentity.getDistanceSq(x, y, z);
            if (distance < 0.0D || d0 < distance * distance) {
               return true;
            }
         }
      }

      return false;
   }

   @Nullable
   default PlayerEntity func_217370_a(EntityPredicate p_217370_1_, LivingEntity p_217370_2_) {
      return this.func_217361_a(this.getPlayers(), p_217370_1_, p_217370_2_, p_217370_2_.posX, p_217370_2_.posY, p_217370_2_.posZ);
   }

   @Nullable
   default PlayerEntity func_217372_a(EntityPredicate p_217372_1_, LivingEntity p_217372_2_, double p_217372_3_, double p_217372_5_, double p_217372_7_) {
      return this.func_217361_a(this.getPlayers(), p_217372_1_, p_217372_2_, p_217372_3_, p_217372_5_, p_217372_7_);
   }

   @Nullable
   default PlayerEntity func_217359_a(EntityPredicate p_217359_1_, double p_217359_2_, double p_217359_4_, double p_217359_6_) {
      return this.func_217361_a(this.getPlayers(), p_217359_1_, (LivingEntity)null, p_217359_2_, p_217359_4_, p_217359_6_);
   }

   @Nullable
   default <T extends LivingEntity> T func_217360_a(Class<? extends T> p_217360_1_, EntityPredicate p_217360_2_, @Nullable LivingEntity p_217360_3_, double p_217360_4_, double p_217360_6_, double p_217360_8_, AxisAlignedBB p_217360_10_) {
      return this.func_217361_a(this.getEntitiesWithinAABB(p_217360_1_, p_217360_10_, (Predicate<T>)null), p_217360_2_, p_217360_3_, p_217360_4_, p_217360_6_, p_217360_8_);
   }

   @Nullable
   default <T extends LivingEntity> T func_217361_a(List<? extends T> p_217361_1_, EntityPredicate p_217361_2_, @Nullable LivingEntity p_217361_3_, double p_217361_4_, double p_217361_6_, double p_217361_8_) {
      double d0 = -1.0D;
      T t = null;

      for(T t1 : p_217361_1_) {
         if (p_217361_2_.canTarget(p_217361_3_, t1)) {
            double d1 = t1.getDistanceSq(p_217361_4_, p_217361_6_, p_217361_8_);
            if (d0 == -1.0D || d1 < d0) {
               d0 = d1;
               t = t1;
            }
         }
      }

      return t;
   }

   default List<PlayerEntity> func_217373_a(EntityPredicate p_217373_1_, LivingEntity p_217373_2_, AxisAlignedBB p_217373_3_) {
      List<PlayerEntity> list = Lists.newArrayList();

      for(PlayerEntity playerentity : this.getPlayers()) {
         if (p_217373_3_.contains(playerentity.posX, playerentity.posY, playerentity.posZ) && p_217373_1_.canTarget(p_217373_2_, playerentity)) {
            list.add(playerentity);
         }
      }

      return list;
   }

   default <T extends LivingEntity> List<T> func_217374_a(Class<? extends T> p_217374_1_, EntityPredicate p_217374_2_, LivingEntity p_217374_3_, AxisAlignedBB p_217374_4_) {
      List<T> list = this.getEntitiesWithinAABB(p_217374_1_, p_217374_4_, (Predicate<T>)null);
      List<T> list1 = Lists.newArrayList();

      for(T t : list) {
         if (p_217374_2_.canTarget(p_217374_3_, t)) {
            list1.add(t);
         }
      }

      return list1;
   }

   @Nullable
   default PlayerEntity getPlayerByUuid(UUID uniqueIdIn) {
      for(int i = 0; i < this.getPlayers().size(); ++i) {
         PlayerEntity playerentity = this.getPlayers().get(i);
         if (uniqueIdIn.equals(playerentity.getUniqueID())) {
            return playerentity;
         }
      }

      return null;
   }
}