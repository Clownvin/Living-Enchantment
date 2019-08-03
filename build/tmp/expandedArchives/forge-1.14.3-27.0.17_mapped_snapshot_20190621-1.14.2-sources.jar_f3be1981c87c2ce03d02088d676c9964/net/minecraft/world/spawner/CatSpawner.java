package net.minecraft.world.spawner;

import java.util.List;
import java.util.Random;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.GameRules;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Feature;

public class CatSpawner {
   private int field_221125_a;

   public int tick(ServerWorld p_221124_1_, boolean p_221124_2_, boolean p_221124_3_) {
      if (p_221124_3_ && p_221124_1_.getGameRules().func_223586_b(GameRules.field_223601_d)) {
         --this.field_221125_a;
         if (this.field_221125_a > 0) {
            return 0;
         } else {
            this.field_221125_a = 1200;
            PlayerEntity playerentity = p_221124_1_.getRandomPlayer();
            if (playerentity == null) {
               return 0;
            } else {
               Random random = p_221124_1_.rand;
               int i = (8 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
               int j = (8 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
               BlockPos blockpos = (new BlockPos(playerentity)).add(i, 0, j);
               if (!p_221124_1_.isAreaLoaded(blockpos.getX() - 10, blockpos.getY() - 10, blockpos.getZ() - 10, blockpos.getX() + 10, blockpos.getY() + 10, blockpos.getZ() + 10)) {
                  return 0;
               } else {
                  if (WorldEntitySpawner.canCreatureTypeSpawnAtLocation(EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, p_221124_1_, blockpos, EntityType.CAT)) {
                     if (p_221124_1_.func_217471_a(blockpos, 2)) {
                        return this.func_221121_a(p_221124_1_, blockpos);
                     }

                     if (Feature.SWAMP_HUT.isPositionInsideStructure(p_221124_1_, blockpos)) {
                        return this.func_221123_a(p_221124_1_, blockpos);
                     }
                  }

                  return 0;
               }
            }
         }
      } else {
         return 0;
      }
   }

   private int func_221121_a(ServerWorld p_221121_1_, BlockPos p_221121_2_) {
      int i = 48;
      if (p_221121_1_.func_217443_B().func_219145_a(PointOfInterestType.HOME.func_221045_c(), p_221121_2_, 48, PointOfInterestManager.Status.IS_OCCUPIED) > 4L) {
         List<CatEntity> list = p_221121_1_.getEntitiesWithinAABB(CatEntity.class, (new AxisAlignedBB(p_221121_2_)).grow(48.0D, 8.0D, 48.0D));
         if (list.size() < 5) {
            return this.func_221122_a(p_221121_2_, p_221121_1_);
         }
      }

      return 0;
   }

   private int func_221123_a(World p_221123_1_, BlockPos p_221123_2_) {
      int i = 16;
      List<CatEntity> list = p_221123_1_.getEntitiesWithinAABB(CatEntity.class, (new AxisAlignedBB(p_221123_2_)).grow(16.0D, 8.0D, 16.0D));
      return list.size() < 1 ? this.func_221122_a(p_221123_2_, p_221123_1_) : 0;
   }

   private int func_221122_a(BlockPos p_221122_1_, World p_221122_2_) {
      CatEntity catentity = EntityType.CAT.create(p_221122_2_);
      if (catentity == null) {
         return 0;
      } else {
         catentity.onInitialSpawn(p_221122_2_, p_221122_2_.getDifficultyForLocation(p_221122_1_), SpawnReason.NATURAL, (ILivingEntityData)null, (CompoundNBT)null);
         catentity.moveToBlockPosAndAngles(p_221122_1_, 0.0F, 0.0F);
         p_221122_2_.addEntity(catentity);
         return 1;
      }
   }
}