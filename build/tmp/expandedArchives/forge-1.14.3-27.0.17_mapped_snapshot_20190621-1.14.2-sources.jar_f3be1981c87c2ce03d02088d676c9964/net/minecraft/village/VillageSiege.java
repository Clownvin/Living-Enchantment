package net.minecraft.village;

import javax.annotation.Nullable;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.spawner.WorldEntitySpawner;

public class VillageSiege {
   private final ServerWorld world;
   private boolean hasSetupSiege;
   private VillageSiege.State siegeState = VillageSiege.State.SIEGE_DONE;
   private int siegeCount;
   private int nextSpawnTime;
   private int spawnX;
   private int spawnY;
   private int spawnZ;

   public VillageSiege(ServerWorld p_i50299_1_) {
      this.world = p_i50299_1_;
   }

   /**
    * Runs a single tick for the village siege
    */
   public void tick() {
      if (this.world.isDaytime()) {
         this.siegeState = VillageSiege.State.SIEGE_DONE;
         this.hasSetupSiege = false;
      } else {
         float f = this.world.getCelestialAngle(0.0F);
         if ((double)f == 0.5D) {
            this.siegeState = this.world.rand.nextInt(10) == 0 ? VillageSiege.State.SIEGE_TONIGHT : VillageSiege.State.SIEGE_DONE;
         }

         if (this.siegeState != VillageSiege.State.SIEGE_DONE) {
            if (!this.hasSetupSiege) {
               if (!this.trySetupSiege()) {
                  return;
               }

               this.hasSetupSiege = true;
            }

            if (this.nextSpawnTime > 0) {
               --this.nextSpawnTime;
            } else {
               this.nextSpawnTime = 2;
               if (this.siegeCount > 0) {
                  this.spawnZombie();
                  --this.siegeCount;
               } else {
                  this.siegeState = VillageSiege.State.SIEGE_DONE;
               }

            }
         }
      }
   }

   private boolean trySetupSiege() {
      for(PlayerEntity playerentity : this.world.getPlayers()) {
         if (!playerentity.isSpectator()) {
            BlockPos blockpos = playerentity.getPosition();
            if (this.world.func_217483_b_(blockpos)) {
               for(int i = 0; i < 10; ++i) {
                  float f = this.world.rand.nextFloat() * ((float)Math.PI * 2F);
                  this.spawnX = blockpos.getX() + MathHelper.floor(MathHelper.cos(f) * 32.0F);
                  this.spawnY = blockpos.getY();
                  this.spawnZ = blockpos.getZ() + MathHelper.floor(MathHelper.sin(f) * 32.0F);
                  Vec3d siegeLocation = this.findRandomSpawnPos(new BlockPos(this.spawnX, this.spawnY, this.spawnZ));
                  if (siegeLocation != null) {
                     if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.village.VillageSiegeEvent(this, world, playerentity, siegeLocation))) return false;
                     this.nextSpawnTime = 0;
                     this.siegeCount = 20;
                     break;
                  }
               }

               return true;
            }
         }
      }

      return false;
   }

   private void spawnZombie() {
      Vec3d vec3d = this.findRandomSpawnPos(new BlockPos(this.spawnX, this.spawnY, this.spawnZ));
      if (vec3d != null) {
         ZombieEntity zombieentity;
         try {
            zombieentity = EntityType.ZOMBIE.create(this.world); //Forge: Direct Initialization is deprecated, use EntityType.
            zombieentity.onInitialSpawn(this.world, this.world.getDifficultyForLocation(new BlockPos(zombieentity)), SpawnReason.EVENT, (ILivingEntityData)null, (CompoundNBT)null);
         } catch (Exception exception) {
            exception.printStackTrace();
            return;
         }

         zombieentity.setLocationAndAngles(vec3d.x, vec3d.y, vec3d.z, this.world.rand.nextFloat() * 360.0F, 0.0F);
         this.world.addEntity(zombieentity);
      }
   }

   @Nullable
   private Vec3d findRandomSpawnPos(BlockPos pos) {
      for(int i = 0; i < 10; ++i) {
         int j = pos.getX() + this.world.rand.nextInt(16) - 8;
         int k = pos.getZ() + this.world.rand.nextInt(16) - 8;
         int l = this.world.getHeight(Heightmap.Type.WORLD_SURFACE, j, k);
         BlockPos blockpos = new BlockPos(j, l, k);
         if (this.world.func_217483_b_(blockpos) && WorldEntitySpawner.canCreatureTypeSpawnAtLocation(EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, this.world, blockpos, EntityType.ZOMBIE)) {
            return new Vec3d((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ());
         }
      }

      return null;
   }

   static enum State {
      SIEGE_CAN_ACTIVATE,
      SIEGE_TONIGHT,
      SIEGE_DONE;
   }
}