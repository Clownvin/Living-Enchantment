package net.minecraft.entity.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LilyPadBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.client.CSteerBoatPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BoatEntity extends Entity {
   private static final DataParameter<Integer> TIME_SINCE_HIT = EntityDataManager.createKey(BoatEntity.class, DataSerializers.VARINT);
   private static final DataParameter<Integer> FORWARD_DIRECTION = EntityDataManager.createKey(BoatEntity.class, DataSerializers.VARINT);
   private static final DataParameter<Float> DAMAGE_TAKEN = EntityDataManager.createKey(BoatEntity.class, DataSerializers.FLOAT);
   private static final DataParameter<Integer> BOAT_TYPE = EntityDataManager.createKey(BoatEntity.class, DataSerializers.VARINT);
   private static final DataParameter<Boolean> field_199704_e = EntityDataManager.createKey(BoatEntity.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Boolean> field_199705_f = EntityDataManager.createKey(BoatEntity.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Integer> ROCKING_TICKS = EntityDataManager.createKey(BoatEntity.class, DataSerializers.VARINT);
   private final float[] paddlePositions = new float[2];
   private float momentum;
   private float outOfControlTicks;
   private float deltaRotation;
   private int lerpSteps;
   private double lerpX;
   private double lerpY;
   private double lerpZ;
   private double lerpYaw;
   private double lerpPitch;
   private boolean leftInputDown;
   private boolean rightInputDown;
   private boolean forwardInputDown;
   private boolean backInputDown;
   private double waterLevel;
   private float boatGlide;
   private BoatEntity.Status status;
   private BoatEntity.Status previousStatus;
   private double lastYd;
   private boolean rocking;
   private boolean field_203060_aN;
   private float rockingIntensity;
   private float rockingAngle;
   private float prevRockingAngle;

   public BoatEntity(EntityType<? extends BoatEntity> p_i50129_1_, World p_i50129_2_) {
      super(p_i50129_1_, p_i50129_2_);
      this.preventEntitySpawning = true;
   }

   public BoatEntity(World worldIn, double x, double y, double z) {
      this(EntityType.BOAT, worldIn);
      this.setPosition(x, y, z);
      this.setMotion(Vec3d.ZERO);
      this.prevPosX = x;
      this.prevPosY = y;
      this.prevPosZ = z;
   }

   /**
    * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
    * prevent them from trampling crops
    */
   protected boolean canTriggerWalking() {
      return false;
   }

   protected void registerData() {
      this.dataManager.register(TIME_SINCE_HIT, 0);
      this.dataManager.register(FORWARD_DIRECTION, 1);
      this.dataManager.register(DAMAGE_TAKEN, 0.0F);
      this.dataManager.register(BOAT_TYPE, BoatEntity.Type.OAK.ordinal());
      this.dataManager.register(field_199704_e, false);
      this.dataManager.register(field_199705_f, false);
      this.dataManager.register(ROCKING_TICKS, 0);
   }

   /**
    * Returns a boundingBox used to collide the entity with other entities and blocks. This enables the entity to be
    * pushable on contact, like boats or minecarts.
    */
   @Nullable
   public AxisAlignedBB getCollisionBox(Entity entityIn) {
      return entityIn.canBePushed() ? entityIn.getBoundingBox() : null;
   }

   /**
    * Returns the <b>solid</b> collision bounding box for this entity. Used to make (e.g.) boats solid. Return null if
    * this entity is not solid.
    *  
    * For general purposes, use {@link #width} and {@link #height}.
    *  
    * @see getEntityBoundingBox
    */
   @Nullable
   public AxisAlignedBB getCollisionBoundingBox() {
      return this.getBoundingBox();
   }

   /**
    * Returns true if this entity should push and be pushed by other entities when colliding.
    */
   public boolean canBePushed() {
      return true;
   }

   /**
    * Returns the Y offset from the entity's position for any entity riding this one.
    */
   public double getMountedYOffset() {
      return -0.1D;
   }

   /**
    * Called when the entity is attacked.
    */
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isInvulnerableTo(source)) {
         return false;
      } else if (!this.world.isRemote && !this.removed) {
         if (source instanceof IndirectEntityDamageSource && source.getTrueSource() != null && this.isPassenger(source.getTrueSource())) {
            return false;
         } else {
            this.setForwardDirection(-this.getForwardDirection());
            this.setTimeSinceHit(10);
            this.setDamageTaken(this.getDamageTaken() + amount * 10.0F);
            this.markVelocityChanged();
            boolean flag = source.getTrueSource() instanceof PlayerEntity && ((PlayerEntity)source.getTrueSource()).abilities.isCreativeMode;
            if (flag || this.getDamageTaken() > 40.0F) {
               if (!flag && this.world.getGameRules().func_223586_b(GameRules.field_223604_g)) {
                  this.entityDropItem(this.getItemBoat());
               }

               this.remove();
            }

            return true;
         }
      } else {
         return true;
      }
   }

   public void onEnterBubbleColumnWithAirAbove(boolean downwards) {
      if (!this.world.isRemote) {
         this.rocking = true;
         this.field_203060_aN = downwards;
         if (this.getRockingTicks() == 0) {
            this.setRockingTicks(60);
         }
      }

      this.world.addParticle(ParticleTypes.SPLASH, this.posX + (double)this.rand.nextFloat(), this.posY + 0.7D, this.posZ + (double)this.rand.nextFloat(), 0.0D, 0.0D, 0.0D);
      if (this.rand.nextInt(20) == 0) {
         this.world.playSound(this.posX, this.posY, this.posZ, this.getSplashSound(), this.getSoundCategory(), 1.0F, 0.8F + 0.4F * this.rand.nextFloat(), false);
      }

   }

   /**
    * Applies a velocity to the entities, to push them away from eachother.
    */
   public void applyEntityCollision(Entity entityIn) {
      if (entityIn instanceof BoatEntity) {
         if (entityIn.getBoundingBox().minY < this.getBoundingBox().maxY) {
            super.applyEntityCollision(entityIn);
         }
      } else if (entityIn.getBoundingBox().minY <= this.getBoundingBox().minY) {
         super.applyEntityCollision(entityIn);
      }

   }

   public Item getItemBoat() {
      switch(this.getBoatType()) {
      case OAK:
      default:
         return Items.OAK_BOAT;
      case SPRUCE:
         return Items.SPRUCE_BOAT;
      case BIRCH:
         return Items.BIRCH_BOAT;
      case JUNGLE:
         return Items.JUNGLE_BOAT;
      case ACACIA:
         return Items.ACACIA_BOAT;
      case DARK_OAK:
         return Items.DARK_OAK_BOAT;
      }
   }

   /**
    * Setups the entity to do the hurt animation. Only used by packets in multiplayer.
    */
   @OnlyIn(Dist.CLIENT)
   public void performHurtAnimation() {
      this.setForwardDirection(-this.getForwardDirection());
      this.setTimeSinceHit(10);
      this.setDamageTaken(this.getDamageTaken() * 11.0F);
   }

   /**
    * Returns true if other Entities should be prevented from moving through this Entity.
    */
   public boolean canBeCollidedWith() {
      return !this.removed;
   }

   /**
    * Sets a target for the client to interpolate towards over the next few ticks
    */
   @OnlyIn(Dist.CLIENT)
   public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
      this.lerpX = x;
      this.lerpY = y;
      this.lerpZ = z;
      this.lerpYaw = (double)yaw;
      this.lerpPitch = (double)pitch;
      this.lerpSteps = 10;
   }

   /**
    * Gets the horizontal facing direction of this Entity, adjusted to take specially-treated entity types into account.
    */
   public Direction getAdjustedHorizontalFacing() {
      return this.getHorizontalFacing().rotateY();
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      this.previousStatus = this.status;
      this.status = this.getBoatStatus();
      if (this.status != BoatEntity.Status.UNDER_WATER && this.status != BoatEntity.Status.UNDER_FLOWING_WATER) {
         this.outOfControlTicks = 0.0F;
      } else {
         ++this.outOfControlTicks;
      }

      if (!this.world.isRemote && this.outOfControlTicks >= 60.0F) {
         this.removePassengers();
      }

      if (this.getTimeSinceHit() > 0) {
         this.setTimeSinceHit(this.getTimeSinceHit() - 1);
      }

      if (this.getDamageTaken() > 0.0F) {
         this.setDamageTaken(this.getDamageTaken() - 1.0F);
      }

      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      super.tick();
      this.tickLerp();
      if (this.canPassengerSteer()) {
         if (this.getPassengers().isEmpty() || !(this.getPassengers().get(0) instanceof PlayerEntity)) {
            this.setPaddleState(false, false);
         }

         this.updateMotion();
         if (this.world.isRemote) {
            this.controlBoat();
            this.world.sendPacketToServer(new CSteerBoatPacket(this.getPaddleState(0), this.getPaddleState(1)));
         }

         this.move(MoverType.SELF, this.getMotion());
      } else {
         this.setMotion(Vec3d.ZERO);
      }

      this.updateRocking();

      for(int i = 0; i <= 1; ++i) {
         if (this.getPaddleState(i)) {
            if (!this.isSilent() && (double)(this.paddlePositions[i] % ((float)Math.PI * 2F)) <= (double)((float)Math.PI / 4F) && ((double)this.paddlePositions[i] + (double)((float)Math.PI / 8F)) % (double)((float)Math.PI * 2F) >= (double)((float)Math.PI / 4F)) {
               SoundEvent soundevent = this.getPaddleSound();
               if (soundevent != null) {
                  Vec3d vec3d = this.getLook(1.0F);
                  double d0 = i == 1 ? -vec3d.z : vec3d.z;
                  double d1 = i == 1 ? vec3d.x : -vec3d.x;
                  this.world.playSound((PlayerEntity)null, this.posX + d0, this.posY, this.posZ + d1, soundevent, this.getSoundCategory(), 1.0F, 0.8F + 0.4F * this.rand.nextFloat());
               }
            }

            this.paddlePositions[i] = (float)((double)this.paddlePositions[i] + (double)((float)Math.PI / 8F));
         } else {
            this.paddlePositions[i] = 0.0F;
         }
      }

      this.doBlockCollisions();
      List<Entity> list = this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox().grow((double)0.2F, (double)-0.01F, (double)0.2F), EntityPredicates.pushableBy(this));
      if (!list.isEmpty()) {
         boolean flag = !this.world.isRemote && !(this.getControllingPassenger() instanceof PlayerEntity);

         for(int j = 0; j < list.size(); ++j) {
            Entity entity = list.get(j);
            if (!entity.isPassenger(this)) {
               if (flag && this.getPassengers().size() < 2 && !entity.isPassenger() && entity.getWidth() < this.getWidth() && entity instanceof LivingEntity && !(entity instanceof WaterMobEntity) && !(entity instanceof PlayerEntity)) {
                  entity.startRiding(this);
               } else {
                  this.applyEntityCollision(entity);
               }
            }
         }
      }

   }

   private void updateRocking() {
      if (this.world.isRemote) {
         int i = this.getRockingTicks();
         if (i > 0) {
            this.rockingIntensity += 0.05F;
         } else {
            this.rockingIntensity -= 0.1F;
         }

         this.rockingIntensity = MathHelper.clamp(this.rockingIntensity, 0.0F, 1.0F);
         this.prevRockingAngle = this.rockingAngle;
         this.rockingAngle = 10.0F * (float)Math.sin((double)(0.5F * (float)this.world.getGameTime())) * this.rockingIntensity;
      } else {
         if (!this.rocking) {
            this.setRockingTicks(0);
         }

         int k = this.getRockingTicks();
         if (k > 0) {
            --k;
            this.setRockingTicks(k);
            int j = 60 - k - 1;
            if (j > 0 && k == 0) {
               this.setRockingTicks(0);
               Vec3d vec3d = this.getMotion();
               if (this.field_203060_aN) {
                  this.setMotion(vec3d.add(0.0D, -0.7D, 0.0D));
                  this.removePassengers();
               } else {
                  this.setMotion(vec3d.x, this.isPassenger(PlayerEntity.class) ? 2.7D : 0.6D, vec3d.z);
               }
            }

            this.rocking = false;
         }
      }

   }

   @Nullable
   protected SoundEvent getPaddleSound() {
      switch(this.getBoatStatus()) {
      case IN_WATER:
      case UNDER_WATER:
      case UNDER_FLOWING_WATER:
         return SoundEvents.ENTITY_BOAT_PADDLE_WATER;
      case ON_LAND:
         return SoundEvents.ENTITY_BOAT_PADDLE_LAND;
      case IN_AIR:
      default:
         return null;
      }
   }

   private void tickLerp() {
      if (this.lerpSteps > 0 && !this.canPassengerSteer()) {
         double d0 = this.posX + (this.lerpX - this.posX) / (double)this.lerpSteps;
         double d1 = this.posY + (this.lerpY - this.posY) / (double)this.lerpSteps;
         double d2 = this.posZ + (this.lerpZ - this.posZ) / (double)this.lerpSteps;
         double d3 = MathHelper.wrapDegrees(this.lerpYaw - (double)this.rotationYaw);
         this.rotationYaw = (float)((double)this.rotationYaw + d3 / (double)this.lerpSteps);
         this.rotationPitch = (float)((double)this.rotationPitch + (this.lerpPitch - (double)this.rotationPitch) / (double)this.lerpSteps);
         --this.lerpSteps;
         this.setPosition(d0, d1, d2);
         this.setRotation(this.rotationYaw, this.rotationPitch);
      }
   }

   public void setPaddleState(boolean left, boolean right) {
      this.dataManager.set(field_199704_e, left);
      this.dataManager.set(field_199705_f, right);
   }

   @OnlyIn(Dist.CLIENT)
   public float getRowingTime(int side, float limbSwing) {
      return this.getPaddleState(side) ? (float)MathHelper.clampedLerp((double)this.paddlePositions[side] - (double)((float)Math.PI / 8F), (double)this.paddlePositions[side], (double)limbSwing) : 0.0F;
   }

   /**
    * Determines whether the boat is in water, gliding on land, or in air
    */
   private BoatEntity.Status getBoatStatus() {
      BoatEntity.Status boatentity$status = this.getUnderwaterStatus();
      if (boatentity$status != null) {
         this.waterLevel = this.getBoundingBox().maxY;
         return boatentity$status;
      } else if (this.checkInWater()) {
         return BoatEntity.Status.IN_WATER;
      } else {
         float f = this.getBoatGlide();
         if (f > 0.0F) {
            this.boatGlide = f;
            return BoatEntity.Status.ON_LAND;
         } else {
            return BoatEntity.Status.IN_AIR;
         }
      }
   }

   public float getWaterLevelAbove() {
      AxisAlignedBB axisalignedbb = this.getBoundingBox();
      int i = MathHelper.floor(axisalignedbb.minX);
      int j = MathHelper.ceil(axisalignedbb.maxX);
      int k = MathHelper.floor(axisalignedbb.maxY);
      int l = MathHelper.ceil(axisalignedbb.maxY - this.lastYd);
      int i1 = MathHelper.floor(axisalignedbb.minZ);
      int j1 = MathHelper.ceil(axisalignedbb.maxZ);

      try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
         label161:
         for(int k1 = k; k1 < l; ++k1) {
            float f = 0.0F;

            for(int l1 = i; l1 < j; ++l1) {
               for(int i2 = i1; i2 < j1; ++i2) {
                  blockpos$pooledmutableblockpos.setPos(l1, k1, i2);
                  IFluidState ifluidstate = this.world.getFluidState(blockpos$pooledmutableblockpos);
                  if (ifluidstate.isTagged(FluidTags.WATER)) {
                     f = Math.max(f, ifluidstate.func_215679_a(this.world, blockpos$pooledmutableblockpos));
                  }

                  if (f >= 1.0F) {
                     continue label161;
                  }
               }
            }

            if (f < 1.0F) {
               float f2 = (float)blockpos$pooledmutableblockpos.getY() + f;
               return f2;
            }
         }

         float f1 = (float)(l + 1);
         return f1;
      }
   }

   /**
    * Decides how much the boat should be gliding on the land (based on any slippery blocks)
    */
   public float getBoatGlide() {
      AxisAlignedBB axisalignedbb = this.getBoundingBox();
      AxisAlignedBB axisalignedbb1 = new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY - 0.001D, axisalignedbb.minZ, axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
      int i = MathHelper.floor(axisalignedbb1.minX) - 1;
      int j = MathHelper.ceil(axisalignedbb1.maxX) + 1;
      int k = MathHelper.floor(axisalignedbb1.minY) - 1;
      int l = MathHelper.ceil(axisalignedbb1.maxY) + 1;
      int i1 = MathHelper.floor(axisalignedbb1.minZ) - 1;
      int j1 = MathHelper.ceil(axisalignedbb1.maxZ) + 1;
      VoxelShape voxelshape = VoxelShapes.create(axisalignedbb1);
      float f = 0.0F;
      int k1 = 0;

      try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
         for(int l1 = i; l1 < j; ++l1) {
            for(int i2 = i1; i2 < j1; ++i2) {
               int j2 = (l1 != i && l1 != j - 1 ? 0 : 1) + (i2 != i1 && i2 != j1 - 1 ? 0 : 1);
               if (j2 != 2) {
                  for(int k2 = k; k2 < l; ++k2) {
                     if (j2 <= 0 || k2 != k && k2 != l - 1) {
                        blockpos$pooledmutableblockpos.setPos(l1, k2, i2);
                        BlockState blockstate = this.world.getBlockState(blockpos$pooledmutableblockpos);
                        if (!(blockstate.getBlock() instanceof LilyPadBlock) && VoxelShapes.compare(blockstate.getCollisionShape(this.world, blockpos$pooledmutableblockpos).withOffset((double)l1, (double)k2, (double)i2), voxelshape, IBooleanFunction.field_223238_i_)) {
                           f += blockstate.getSlipperiness(this.world, blockpos$pooledmutableblockpos, this);
                           ++k1;
                        }
                     }
                  }
               }
            }
         }
      }

      return f / (float)k1;
   }

   private boolean checkInWater() {
      AxisAlignedBB axisalignedbb = this.getBoundingBox();
      int i = MathHelper.floor(axisalignedbb.minX);
      int j = MathHelper.ceil(axisalignedbb.maxX);
      int k = MathHelper.floor(axisalignedbb.minY);
      int l = MathHelper.ceil(axisalignedbb.minY + 0.001D);
      int i1 = MathHelper.floor(axisalignedbb.minZ);
      int j1 = MathHelper.ceil(axisalignedbb.maxZ);
      boolean flag = false;
      this.waterLevel = Double.MIN_VALUE;

      try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
         for(int k1 = i; k1 < j; ++k1) {
            for(int l1 = k; l1 < l; ++l1) {
               for(int i2 = i1; i2 < j1; ++i2) {
                  blockpos$pooledmutableblockpos.setPos(k1, l1, i2);
                  IFluidState ifluidstate = this.world.getFluidState(blockpos$pooledmutableblockpos);
                  if (ifluidstate.isTagged(FluidTags.WATER)) {
                     float f = (float)l1 + ifluidstate.func_215679_a(this.world, blockpos$pooledmutableblockpos);
                     this.waterLevel = Math.max((double)f, this.waterLevel);
                     flag |= axisalignedbb.minY < (double)f;
                  }
               }
            }
         }
      }

      return flag;
   }

   /**
    * Decides whether the boat is currently underwater.
    */
   @Nullable
   private BoatEntity.Status getUnderwaterStatus() {
      AxisAlignedBB axisalignedbb = this.getBoundingBox();
      double d0 = axisalignedbb.maxY + 0.001D;
      int i = MathHelper.floor(axisalignedbb.minX);
      int j = MathHelper.ceil(axisalignedbb.maxX);
      int k = MathHelper.floor(axisalignedbb.maxY);
      int l = MathHelper.ceil(d0);
      int i1 = MathHelper.floor(axisalignedbb.minZ);
      int j1 = MathHelper.ceil(axisalignedbb.maxZ);
      boolean flag = false;

      try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
         for(int k1 = i; k1 < j; ++k1) {
            for(int l1 = k; l1 < l; ++l1) {
               for(int i2 = i1; i2 < j1; ++i2) {
                  blockpos$pooledmutableblockpos.setPos(k1, l1, i2);
                  IFluidState ifluidstate = this.world.getFluidState(blockpos$pooledmutableblockpos);
                  if (ifluidstate.isTagged(FluidTags.WATER) && d0 < (double)((float)blockpos$pooledmutableblockpos.getY() + ifluidstate.func_215679_a(this.world, blockpos$pooledmutableblockpos))) {
                     if (!ifluidstate.isSource()) {
                        BoatEntity.Status boatentity$status = BoatEntity.Status.UNDER_FLOWING_WATER;
                        return boatentity$status;
                     }

                     flag = true;
                  }
               }
            }
         }
      }

      return flag ? BoatEntity.Status.UNDER_WATER : null;
   }

   /**
    * Update the boat's speed, based on momentum.
    */
   private void updateMotion() {
      double d0 = (double)-0.04F;
      double d1 = this.hasNoGravity() ? 0.0D : (double)-0.04F;
      double d2 = 0.0D;
      this.momentum = 0.05F;
      if (this.previousStatus == BoatEntity.Status.IN_AIR && this.status != BoatEntity.Status.IN_AIR && this.status != BoatEntity.Status.ON_LAND) {
         this.waterLevel = this.getBoundingBox().minY + (double)this.getHeight();
         this.setPosition(this.posX, (double)(this.getWaterLevelAbove() - this.getHeight()) + 0.101D, this.posZ);
         this.setMotion(this.getMotion().mul(1.0D, 0.0D, 1.0D));
         this.lastYd = 0.0D;
         this.status = BoatEntity.Status.IN_WATER;
      } else {
         if (this.status == BoatEntity.Status.IN_WATER) {
            d2 = (this.waterLevel - this.getBoundingBox().minY) / (double)this.getHeight();
            this.momentum = 0.9F;
         } else if (this.status == BoatEntity.Status.UNDER_FLOWING_WATER) {
            d1 = -7.0E-4D;
            this.momentum = 0.9F;
         } else if (this.status == BoatEntity.Status.UNDER_WATER) {
            d2 = (double)0.01F;
            this.momentum = 0.45F;
         } else if (this.status == BoatEntity.Status.IN_AIR) {
            this.momentum = 0.9F;
         } else if (this.status == BoatEntity.Status.ON_LAND) {
            this.momentum = this.boatGlide;
            if (this.getControllingPassenger() instanceof PlayerEntity) {
               this.boatGlide /= 2.0F;
            }
         }

         Vec3d vec3d = this.getMotion();
         this.setMotion(vec3d.x * (double)this.momentum, vec3d.y + d1, vec3d.z * (double)this.momentum);
         this.deltaRotation *= this.momentum;
         if (d2 > 0.0D) {
            Vec3d vec3d1 = this.getMotion();
            this.setMotion(vec3d1.x, (vec3d1.y + d2 * 0.06153846016296973D) * 0.75D, vec3d1.z);
         }
      }

   }

   private void controlBoat() {
      if (this.isBeingRidden()) {
         float f = 0.0F;
         if (this.leftInputDown) {
            --this.deltaRotation;
         }

         if (this.rightInputDown) {
            ++this.deltaRotation;
         }

         if (this.rightInputDown != this.leftInputDown && !this.forwardInputDown && !this.backInputDown) {
            f += 0.005F;
         }

         this.rotationYaw += this.deltaRotation;
         if (this.forwardInputDown) {
            f += 0.04F;
         }

         if (this.backInputDown) {
            f -= 0.005F;
         }

         this.setMotion(this.getMotion().add((double)(MathHelper.sin(-this.rotationYaw * ((float)Math.PI / 180F)) * f), 0.0D, (double)(MathHelper.cos(this.rotationYaw * ((float)Math.PI / 180F)) * f)));
         this.setPaddleState(this.rightInputDown && !this.leftInputDown || this.forwardInputDown, this.leftInputDown && !this.rightInputDown || this.forwardInputDown);
      }
   }

   public void updatePassenger(Entity passenger) {
      if (this.isPassenger(passenger)) {
         float f = 0.0F;
         float f1 = (float)((this.removed ? (double)0.01F : this.getMountedYOffset()) + passenger.getYOffset());
         if (this.getPassengers().size() > 1) {
            int i = this.getPassengers().indexOf(passenger);
            if (i == 0) {
               f = 0.2F;
            } else {
               f = -0.6F;
            }

            if (passenger instanceof AnimalEntity) {
               f = (float)((double)f + 0.2D);
            }
         }

         Vec3d vec3d = (new Vec3d((double)f, 0.0D, 0.0D)).rotateYaw(-this.rotationYaw * ((float)Math.PI / 180F) - ((float)Math.PI / 2F));
         passenger.setPosition(this.posX + vec3d.x, this.posY + (double)f1, this.posZ + vec3d.z);
         passenger.rotationYaw += this.deltaRotation;
         passenger.setRotationYawHead(passenger.getRotationYawHead() + this.deltaRotation);
         this.applyYawToEntity(passenger);
         if (passenger instanceof AnimalEntity && this.getPassengers().size() > 1) {
            int j = passenger.getEntityId() % 2 == 0 ? 90 : 270;
            passenger.setRenderYawOffset(((AnimalEntity)passenger).renderYawOffset + (float)j);
            passenger.setRotationYawHead(passenger.getRotationYawHead() + (float)j);
         }

      }
   }

   /**
    * Applies this boat's yaw to the given entity. Used to update the orientation of its passenger.
    */
   protected void applyYawToEntity(Entity entityToUpdate) {
      entityToUpdate.setRenderYawOffset(this.rotationYaw);
      float f = MathHelper.wrapDegrees(entityToUpdate.rotationYaw - this.rotationYaw);
      float f1 = MathHelper.clamp(f, -105.0F, 105.0F);
      entityToUpdate.prevRotationYaw += f1 - f;
      entityToUpdate.rotationYaw += f1 - f;
      entityToUpdate.setRotationYawHead(entityToUpdate.rotationYaw);
   }

   /**
    * Applies this entity's orientation (pitch/yaw) to another entity. Used to update passenger orientation.
    */
   @OnlyIn(Dist.CLIENT)
   public void applyOrientationToEntity(Entity entityToUpdate) {
      this.applyYawToEntity(entityToUpdate);
   }

   protected void writeAdditional(CompoundNBT compound) {
      compound.putString("Type", this.getBoatType().getName());
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   protected void readAdditional(CompoundNBT compound) {
      if (compound.contains("Type", 8)) {
         this.setBoatType(BoatEntity.Type.getTypeFromString(compound.getString("Type")));
      }

   }

   public boolean processInitialInteract(PlayerEntity player, Hand hand) {
      if (player.isSneaking()) {
         return false;
      } else {
         if (!this.world.isRemote && this.outOfControlTicks < 60.0F) {
            player.startRiding(this);
         }

         return true;
      }
   }

   protected void updateFallState(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
      this.lastYd = this.getMotion().y;
      if (!this.isPassenger()) {
         if (onGroundIn) {
            if (this.fallDistance > 3.0F) {
               if (this.status != BoatEntity.Status.ON_LAND) {
                  this.fallDistance = 0.0F;
                  return;
               }

               this.fall(this.fallDistance, 1.0F);
               if (!this.world.isRemote && !this.removed) {
                  this.remove();
                  if (this.world.getGameRules().func_223586_b(GameRules.field_223604_g)) {
                     for(int i = 0; i < 3; ++i) {
                        this.entityDropItem(this.getBoatType().asPlank());
                     }

                     for(int j = 0; j < 2; ++j) {
                        this.entityDropItem(Items.STICK);
                     }
                  }
               }
            }

            this.fallDistance = 0.0F;
         } else if (!this.world.getFluidState((new BlockPos(this)).down()).isTagged(FluidTags.WATER) && y < 0.0D) {
            this.fallDistance = (float)((double)this.fallDistance - y);
         }

      }
   }

   public boolean getPaddleState(int side) {
      return this.dataManager.<Boolean>get(side == 0 ? field_199704_e : field_199705_f) && this.getControllingPassenger() != null;
   }

   /**
    * Sets the damage taken from the last hit.
    */
   public void setDamageTaken(float damageTaken) {
      this.dataManager.set(DAMAGE_TAKEN, damageTaken);
   }

   /**
    * Gets the damage taken from the last hit.
    */
   public float getDamageTaken() {
      return this.dataManager.get(DAMAGE_TAKEN);
   }

   /**
    * Sets the time to count down from since the last time entity was hit.
    */
   public void setTimeSinceHit(int timeSinceHit) {
      this.dataManager.set(TIME_SINCE_HIT, timeSinceHit);
   }

   /**
    * Gets the time since the last hit.
    */
   public int getTimeSinceHit() {
      return this.dataManager.get(TIME_SINCE_HIT);
   }

   private void setRockingTicks(int p_203055_1_) {
      this.dataManager.set(ROCKING_TICKS, p_203055_1_);
   }

   private int getRockingTicks() {
      return this.dataManager.get(ROCKING_TICKS);
   }

   @OnlyIn(Dist.CLIENT)
   public float getRockingAngle(float partialTicks) {
      return MathHelper.lerp(partialTicks, this.prevRockingAngle, this.rockingAngle);
   }

   /**
    * Sets the forward direction of the entity.
    */
   public void setForwardDirection(int forwardDirection) {
      this.dataManager.set(FORWARD_DIRECTION, forwardDirection);
   }

   /**
    * Gets the forward direction of the entity.
    */
   public int getForwardDirection() {
      return this.dataManager.get(FORWARD_DIRECTION);
   }

   public void setBoatType(BoatEntity.Type boatType) {
      this.dataManager.set(BOAT_TYPE, boatType.ordinal());
   }

   public BoatEntity.Type getBoatType() {
      return BoatEntity.Type.byId(this.dataManager.get(BOAT_TYPE));
   }

   protected boolean canFitPassenger(Entity passenger) {
      return this.getPassengers().size() < 2 && !this.areEyesInFluid(FluidTags.WATER);
   }

   /**
    * For vehicles, the first passenger is generally considered the controller and "drives" the vehicle. For example,
    * Pigs, Horses, and Boats are generally "steered" by the controlling passenger.
    */
   @Nullable
   public Entity getControllingPassenger() {
      List<Entity> list = this.getPassengers();
      return list.isEmpty() ? null : list.get(0);
   }

   @OnlyIn(Dist.CLIENT)
   public void updateInputs(boolean p_184442_1_, boolean p_184442_2_, boolean p_184442_3_, boolean p_184442_4_) {
      this.leftInputDown = p_184442_1_;
      this.rightInputDown = p_184442_2_;
      this.forwardInputDown = p_184442_3_;
      this.backInputDown = p_184442_4_;
   }

   public IPacket<?> createSpawnPacket() {
      return new SSpawnObjectPacket(this);
   }

   // Forge: Fix MC-119811 by instantly completing lerp on board
   @Override
   protected void addPassenger(Entity passenger) {
      super.addPassenger(passenger);
      if (this.canPassengerSteer() && this.lerpSteps > 0) {
         this.lerpSteps = 0;
         this.posX = this.lerpX;
         this.posY = this.lerpY;
         this.posZ = this.lerpZ;
         this.rotationYaw = (float)this.lerpYaw;
         this.rotationPitch = (float)this.lerpPitch;
      }
   }

   public static enum Status {
      IN_WATER,
      UNDER_WATER,
      UNDER_FLOWING_WATER,
      ON_LAND,
      IN_AIR;
   }

   public static enum Type {
      OAK(Blocks.OAK_PLANKS, "oak"),
      SPRUCE(Blocks.SPRUCE_PLANKS, "spruce"),
      BIRCH(Blocks.BIRCH_PLANKS, "birch"),
      JUNGLE(Blocks.JUNGLE_PLANKS, "jungle"),
      ACACIA(Blocks.ACACIA_PLANKS, "acacia"),
      DARK_OAK(Blocks.DARK_OAK_PLANKS, "dark_oak");

      private final String name;
      private final Block block;

      private Type(Block p_i48146_3_, String p_i48146_4_) {
         this.name = p_i48146_4_;
         this.block = p_i48146_3_;
      }

      public String getName() {
         return this.name;
      }

      public Block asPlank() {
         return this.block;
      }

      public String toString() {
         return this.name;
      }

      /**
       * Get a boat type by it's enum ordinal
       */
      public static BoatEntity.Type byId(int id) {
         BoatEntity.Type[] aboatentity$type = values();
         if (id < 0 || id >= aboatentity$type.length) {
            id = 0;
         }

         return aboatentity$type[id];
      }

      public static BoatEntity.Type getTypeFromString(String nameIn) {
         BoatEntity.Type[] aboatentity$type = values();

         for(int i = 0; i < aboatentity$type.length; ++i) {
            if (aboatentity$type[i].getName().equals(nameIn)) {
               return aboatentity$type[i];
            }
         }

         return aboatentity$type[0];
      }
   }
}