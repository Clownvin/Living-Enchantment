package net.minecraft.world.spawner;

import java.util.Random;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.PatrollerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;

public class PatrolSpawner {
   private int field_222698_b;

   public int tick(ServerWorld p_222696_1_, boolean p_222696_2_, boolean p_222696_3_) {
      if (!p_222696_2_) {
         return 0;
      } else {
         Random random = p_222696_1_.rand;
         --this.field_222698_b;
         if (this.field_222698_b > 0) {
            return 0;
         } else {
            this.field_222698_b += 12000 + random.nextInt(1200);
            long i = p_222696_1_.getDayTime() / 24000L;
            if (i >= 5L && p_222696_1_.isDaytime()) {
               if (random.nextInt(5) != 0) {
                  return 0;
               } else {
                  int j = p_222696_1_.getPlayers().size();
                  if (j < 1) {
                     return 0;
                  } else {
                     PlayerEntity playerentity = p_222696_1_.getPlayers().get(random.nextInt(j));
                     if (playerentity.isSpectator()) {
                        return 0;
                     } else if (p_222696_1_.func_217483_b_(playerentity.getPosition())) {
                        return 0;
                     } else {
                        int k = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
                        int l = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
                        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
                        blockpos$mutableblockpos.setPos(playerentity.posX, playerentity.posY, playerentity.posZ).move(k, 0, l);
                        if (!p_222696_1_.isAreaLoaded(blockpos$mutableblockpos.getX() - 10, blockpos$mutableblockpos.getY() - 10, blockpos$mutableblockpos.getZ() - 10, blockpos$mutableblockpos.getX() + 10, blockpos$mutableblockpos.getY() + 10, blockpos$mutableblockpos.getZ() + 10)) {
                           return 0;
                        } else {
                           Biome biome = p_222696_1_.getBiome(blockpos$mutableblockpos);
                           Biome.Category biome$category = biome.getCategory();
                           if (biome$category == Biome.Category.MUSHROOM) {
                              return 0;
                           } else {
                              int i1 = 0;
                              int j1 = (int)Math.ceil((double)p_222696_1_.getDifficultyForLocation(blockpos$mutableblockpos).getAdditionalDifficulty()) + 1;

                              for(int k1 = 0; k1 < j1; ++k1) {
                                 ++i1;
                                 blockpos$mutableblockpos.setY(p_222696_1_.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockpos$mutableblockpos).getY());
                                 if (k1 == 0) {
                                    if (!this.func_222695_a(p_222696_1_, blockpos$mutableblockpos, random, true)) {
                                       break;
                                    }
                                 } else {
                                    this.func_222695_a(p_222696_1_, blockpos$mutableblockpos, random, false);
                                 }

                                 blockpos$mutableblockpos.func_223471_o(blockpos$mutableblockpos.getX() + random.nextInt(5) - random.nextInt(5));
                                 blockpos$mutableblockpos.func_223472_q(blockpos$mutableblockpos.getZ() + random.nextInt(5) - random.nextInt(5));
                              }

                              return i1;
                           }
                        }
                     }
                  }
               }
            } else {
               return 0;
            }
         }
      }
   }

   private boolean func_222695_a(World p_222695_1_, BlockPos p_222695_2_, Random p_222695_3_, boolean p_222695_4_) {
      if (!PatrollerEntity.func_223330_b(EntityType.PILLAGER, p_222695_1_, SpawnReason.PATROL, p_222695_2_, p_222695_3_)) {
         return false;
      } else {
         PatrollerEntity patrollerentity = EntityType.PILLAGER.create(p_222695_1_);
         if (patrollerentity != null) {
            if (p_222695_4_) {
               patrollerentity.setLeader(true);
               patrollerentity.resetPatrolTarget();
            }

            patrollerentity.setPosition((double)p_222695_2_.getX(), (double)p_222695_2_.getY(), (double)p_222695_2_.getZ());
            patrollerentity.onInitialSpawn(p_222695_1_, p_222695_1_.getDifficultyForLocation(p_222695_2_), SpawnReason.PATROL, (ILivingEntityData)null, (CompoundNBT)null);
            p_222695_1_.addEntity(patrollerentity);
            return true;
         } else {
            return false;
         }
      }
   }
}