package net.minecraft.entity.passive;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.DefendVillageTargetGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.entity.ai.goal.MoveTowardsVillageGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.ShowVillagerFlowerGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.spawner.WorldEntitySpawner;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class IronGolemEntity extends GolemEntity {
   protected static final DataParameter<Byte> PLAYER_CREATED = EntityDataManager.createKey(IronGolemEntity.class, DataSerializers.BYTE);
   private int attackTimer;
   private int holdRoseTick;

   public IronGolemEntity(EntityType<? extends IronGolemEntity> type, World worldIn) {
      super(type, worldIn);
      this.stepHeight = 1.0F;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
      this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.9D, 32.0F));
      this.goalSelector.addGoal(2, new MoveTowardsVillageGoal(this, 0.6D));
      this.goalSelector.addGoal(3, new MoveThroughVillageGoal(this, 0.6D, false, 4, () -> {
         return false;
      }));
      this.goalSelector.addGoal(5, new ShowVillagerFlowerGoal(this));
      this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 0.6D));
      this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
      this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
      this.targetSelector.addGoal(1, new DefendVillageTargetGoal(this));
      this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, MobEntity.class, 5, false, false, (p_213619_0_) -> {
         return p_213619_0_ instanceof IMob && !(p_213619_0_ instanceof CreeperEntity);
      }));
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(PLAYER_CREATED, (byte)0);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(100.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
      this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
   }

   /**
    * Decrements the entity's air supply when underwater
    */
   protected int decreaseAirSupply(int air) {
      return air;
   }

   protected void collideWithEntity(Entity entityIn) {
      if (entityIn instanceof IMob && !(entityIn instanceof CreeperEntity) && this.getRNG().nextInt(20) == 0) {
         this.setAttackTarget((LivingEntity)entityIn);
      }

      super.collideWithEntity(entityIn);
   }

   /**
    * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
    * use this to react to sunlight and start to burn.
    */
   public void livingTick() {
      super.livingTick();
      if (this.attackTimer > 0) {
         --this.attackTimer;
      }

      if (this.holdRoseTick > 0) {
         --this.holdRoseTick;
      }

      if (func_213296_b(this.getMotion()) > (double)2.5000003E-7F && this.rand.nextInt(5) == 0) {
         int i = MathHelper.floor(this.posX);
         int j = MathHelper.floor(this.posY - (double)0.2F);
         int k = MathHelper.floor(this.posZ);
         BlockState blockstate = this.world.getBlockState(new BlockPos(i, j, k));
         if (!blockstate.isAir()) {
            this.world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, blockstate), this.posX + ((double)this.rand.nextFloat() - 0.5D) * (double)this.getWidth(), this.getBoundingBox().minY + 0.1D, this.posZ + ((double)this.rand.nextFloat() - 0.5D) * (double)this.getWidth(), 4.0D * ((double)this.rand.nextFloat() - 0.5D), 0.5D, ((double)this.rand.nextFloat() - 0.5D) * 4.0D);
         }
      }

   }

   public boolean canAttack(EntityType<?> typeIn) {
      if (this.isPlayerCreated() && typeIn == EntityType.PLAYER) {
         return false;
      } else {
         return typeIn == EntityType.CREEPER ? false : super.canAttack(typeIn);
      }
   }

   public void writeAdditional(CompoundNBT compound) {
      super.writeAdditional(compound);
      compound.putBoolean("PlayerCreated", this.isPlayerCreated());
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(CompoundNBT compound) {
      super.readAdditional(compound);
      this.setPlayerCreated(compound.getBoolean("PlayerCreated"));
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      this.attackTimer = 10;
      this.world.setEntityState(this, (byte)4);
      boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), (float)(7 + this.rand.nextInt(15)));
      if (flag) {
         entityIn.setMotion(entityIn.getMotion().add(0.0D, (double)0.4F, 0.0D));
         this.applyEnchantments(this, entityIn);
      }

      this.playSound(SoundEvents.ENTITY_IRON_GOLEM_ATTACK, 1.0F, 1.0F);
      return flag;
   }

   /**
    * Handler for {@link World#setEntityState}
    */
   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte id) {
      if (id == 4) {
         this.attackTimer = 10;
         this.playSound(SoundEvents.ENTITY_IRON_GOLEM_ATTACK, 1.0F, 1.0F);
      } else if (id == 11) {
         this.holdRoseTick = 400;
      } else if (id == 34) {
         this.holdRoseTick = 0;
      } else {
         super.handleStatusUpdate(id);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public int getAttackTimer() {
      return this.attackTimer;
   }

   public void setHoldingRose(boolean holdingRose) {
      if (holdingRose) {
         this.holdRoseTick = 400;
         this.world.setEntityState(this, (byte)11);
      } else {
         this.holdRoseTick = 0;
         this.world.setEntityState(this, (byte)34);
      }

   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_IRON_GOLEM_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_IRON_GOLEM_DEATH;
   }

   protected void playStepSound(BlockPos pos, BlockState blockIn) {
      this.playSound(SoundEvents.ENTITY_IRON_GOLEM_STEP, 1.0F, 1.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public int getHoldRoseTick() {
      return this.holdRoseTick;
   }

   public boolean isPlayerCreated() {
      return (this.dataManager.get(PLAYER_CREATED) & 1) != 0;
   }

   public void setPlayerCreated(boolean playerCreated) {
      byte b0 = this.dataManager.get(PLAYER_CREATED);
      if (playerCreated) {
         this.dataManager.set(PLAYER_CREATED, (byte)(b0 | 1));
      } else {
         this.dataManager.set(PLAYER_CREATED, (byte)(b0 & -2));
      }

   }

   /**
    * Called when the mob's health reaches 0.
    */
   public void onDeath(DamageSource cause) {
      super.onDeath(cause);
   }

   public boolean isNotColliding(IWorldReader worldIn) {
      BlockPos blockpos = new BlockPos(this);
      BlockPos blockpos1 = blockpos.down();
      BlockState blockstate = worldIn.getBlockState(blockpos1);
      if (!blockstate.func_215682_a(worldIn, blockpos1, this)) {
         return false;
      } else {
         for(int i = 1; i < 3; ++i) {
            BlockPos blockpos2 = blockpos.up(i);
            BlockState blockstate1 = worldIn.getBlockState(blockpos2);
            if (!WorldEntitySpawner.isSpawnableSpace(worldIn, blockpos2, blockstate1, blockstate1.getFluidState())) {
               return false;
            }
         }

         return WorldEntitySpawner.isSpawnableSpace(worldIn, blockpos, worldIn.getBlockState(blockpos), Fluids.EMPTY.getDefaultState()) && worldIn.func_217346_i(this);
      }
   }
}