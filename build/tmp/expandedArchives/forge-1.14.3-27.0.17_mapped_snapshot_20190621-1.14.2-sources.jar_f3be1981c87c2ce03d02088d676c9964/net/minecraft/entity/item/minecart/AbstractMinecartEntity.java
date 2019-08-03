package net.minecraft.entity.item.minecart;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AbstractMinecartEntity extends Entity implements net.minecraftforge.common.extensions.IForgeEntityMinecart {
   private static final DataParameter<Integer> ROLLING_AMPLITUDE = EntityDataManager.createKey(AbstractMinecartEntity.class, DataSerializers.VARINT);
   private static final DataParameter<Integer> ROLLING_DIRECTION = EntityDataManager.createKey(AbstractMinecartEntity.class, DataSerializers.VARINT);
   private static final DataParameter<Float> DAMAGE = EntityDataManager.createKey(AbstractMinecartEntity.class, DataSerializers.FLOAT);
   private static final DataParameter<Integer> DISPLAY_TILE = EntityDataManager.createKey(AbstractMinecartEntity.class, DataSerializers.VARINT);
   private static final DataParameter<Integer> DISPLAY_TILE_OFFSET = EntityDataManager.createKey(AbstractMinecartEntity.class, DataSerializers.VARINT);
   private static final DataParameter<Boolean> SHOW_BLOCK = EntityDataManager.createKey(AbstractMinecartEntity.class, DataSerializers.BOOLEAN);
   private boolean isInReverse;
   private static final int[][][] MATRIX = new int[][][]{{{0, 0, -1}, {0, 0, 1}}, {{-1, 0, 0}, {1, 0, 0}}, {{-1, -1, 0}, {1, 0, 0}}, {{-1, 0, 0}, {1, -1, 0}}, {{0, 0, -1}, {0, -1, 1}}, {{0, -1, -1}, {0, 0, 1}}, {{0, 0, 1}, {1, 0, 0}}, {{0, 0, 1}, {-1, 0, 0}}, {{0, 0, -1}, {-1, 0, 0}}, {{0, 0, -1}, {1, 0, 0}}};
   private int turnProgress;
   private double minecartX;
   private double minecartY;
   private double minecartZ;
   private double minecartYaw;
   private double minecartPitch;
   @OnlyIn(Dist.CLIENT)
   private double velocityX;
   @OnlyIn(Dist.CLIENT)
   private double velocityY;
   @OnlyIn(Dist.CLIENT)
   private double velocityZ;
   private boolean canBePushed = true;

   protected AbstractMinecartEntity(EntityType<?> type, World p_i48538_2_) {
      super(type, p_i48538_2_);
      this.preventEntitySpawning = true;
   }

   protected AbstractMinecartEntity(EntityType<?> type, World p_i48539_2_, double p_i48539_3_, double p_i48539_5_, double p_i48539_7_) {
      this(type, p_i48539_2_);
      this.setPosition(p_i48539_3_, p_i48539_5_, p_i48539_7_);
      this.setMotion(Vec3d.ZERO);
      this.prevPosX = p_i48539_3_;
      this.prevPosY = p_i48539_5_;
      this.prevPosZ = p_i48539_7_;
   }

   public static AbstractMinecartEntity create(World worldIn, double x, double y, double z, AbstractMinecartEntity.Type typeIn) {
      if (typeIn == AbstractMinecartEntity.Type.CHEST) {
         return new ChestMinecartEntity(worldIn, x, y, z);
      } else if (typeIn == AbstractMinecartEntity.Type.FURNACE) {
         return new FurnaceMinecartEntity(worldIn, x, y, z);
      } else if (typeIn == AbstractMinecartEntity.Type.TNT) {
         return new TNTMinecartEntity(worldIn, x, y, z);
      } else if (typeIn == AbstractMinecartEntity.Type.SPAWNER) {
         return new SpawnerMinecartEntity(worldIn, x, y, z);
      } else if (typeIn == AbstractMinecartEntity.Type.HOPPER) {
         return new HopperMinecartEntity(worldIn, x, y, z);
      } else {
         return (AbstractMinecartEntity)(typeIn == AbstractMinecartEntity.Type.COMMAND_BLOCK ? new MinecartCommandBlockEntity(worldIn, x, y, z) : new MinecartEntity(worldIn, x, y, z));
      }
   }

   /**
    * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
    * prevent them from trampling crops
    */
   protected boolean canTriggerWalking() {
      return false;
   }

   protected void registerData() {
      this.dataManager.register(ROLLING_AMPLITUDE, 0);
      this.dataManager.register(ROLLING_DIRECTION, 1);
      this.dataManager.register(DAMAGE, 0.0F);
      this.dataManager.register(DISPLAY_TILE, Block.getStateId(Blocks.AIR.getDefaultState()));
      this.dataManager.register(DISPLAY_TILE_OFFSET, 6);
      this.dataManager.register(SHOW_BLOCK, false);
   }

   /**
    * Returns a boundingBox used to collide the entity with other entities and blocks. This enables the entity to be
    * pushable on contact, like boats or minecarts.
    */
   @Nullable
   public AxisAlignedBB getCollisionBox(Entity entityIn) {
      if (getCollisionHandler() != null) return getCollisionHandler().getCollisionBox(this, entityIn);
      return entityIn.canBePushed() ? entityIn.getBoundingBox() : null;
   }

   /**
    * Returns true if this entity should push and be pushed by other entities when colliding.
    */
   public boolean canBePushed() {
      return canBePushed;
   }

   /**
    * Returns the Y offset from the entity's position for any entity riding this one.
    */
   public double getMountedYOffset() {
      return 0.0D;
   }

   /**
    * Called when the entity is attacked.
    */
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (!this.world.isRemote && !this.removed) {
         if (this.isInvulnerableTo(source)) {
            return false;
         } else {
            this.setRollingDirection(-this.getRollingDirection());
            this.setRollingAmplitude(10);
            this.markVelocityChanged();
            this.setDamage(this.getDamage() + amount * 10.0F);
            boolean flag = source.getTrueSource() instanceof PlayerEntity && ((PlayerEntity)source.getTrueSource()).abilities.isCreativeMode;
            if (flag || this.getDamage() > 40.0F) {
               this.removePassengers();
               if (flag && !this.hasCustomName()) {
                  this.remove();
               } else {
                  this.killMinecart(source);
               }
            }

            return true;
         }
      } else {
         return true;
      }
   }

   public void killMinecart(DamageSource source) {
      this.remove();
      if (this.world.getGameRules().func_223586_b(GameRules.field_223604_g)) {
         ItemStack itemstack = new ItemStack(Items.MINECART);
         if (this.hasCustomName()) {
            itemstack.setDisplayName(this.getCustomName());
         }

         this.entityDropItem(itemstack);
      }

   }

   /**
    * Setups the entity to do the hurt animation. Only used by packets in multiplayer.
    */
   @OnlyIn(Dist.CLIENT)
   public void performHurtAnimation() {
      this.setRollingDirection(-this.getRollingDirection());
      this.setRollingAmplitude(10);
      this.setDamage(this.getDamage() + this.getDamage() * 10.0F);
   }

   /**
    * Returns true if other Entities should be prevented from moving through this Entity.
    */
   public boolean canBeCollidedWith() {
      return !this.removed;
   }

   /**
    * Gets the horizontal facing direction of this Entity, adjusted to take specially-treated entity types into account.
    */
   public Direction getAdjustedHorizontalFacing() {
      return this.isInReverse ? this.getHorizontalFacing().getOpposite().rotateY() : this.getHorizontalFacing().rotateY();
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      if (this.getRollingAmplitude() > 0) {
         this.setRollingAmplitude(this.getRollingAmplitude() - 1);
      }

      if (this.getDamage() > 0.0F) {
         this.setDamage(this.getDamage() - 1.0F);
      }

      if (this.posY < -64.0D) {
         this.outOfWorld();
      }

      this.updatePortal();
      if (this.world.isRemote) {
         if (this.turnProgress > 0) {
            double d4 = this.posX + (this.minecartX - this.posX) / (double)this.turnProgress;
            double d5 = this.posY + (this.minecartY - this.posY) / (double)this.turnProgress;
            double d6 = this.posZ + (this.minecartZ - this.posZ) / (double)this.turnProgress;
            double d1 = MathHelper.wrapDegrees(this.minecartYaw - (double)this.rotationYaw);
            this.rotationYaw = (float)((double)this.rotationYaw + d1 / (double)this.turnProgress);
            this.rotationPitch = (float)((double)this.rotationPitch + (this.minecartPitch - (double)this.rotationPitch) / (double)this.turnProgress);
            --this.turnProgress;
            this.setPosition(d4, d5, d6);
            this.setRotation(this.rotationYaw, this.rotationPitch);
         } else {
            this.setPosition(this.posX, this.posY, this.posZ);
            this.setRotation(this.rotationYaw, this.rotationPitch);
         }

      } else {
         this.prevPosX = this.posX;
         this.prevPosY = this.posY;
         this.prevPosZ = this.posZ;
         if (!this.hasNoGravity()) {
            this.setMotion(this.getMotion().add(0.0D, -0.04D, 0.0D));
         }

         int i = MathHelper.floor(this.posX);
         int j = MathHelper.floor(this.posY);
         int k = MathHelper.floor(this.posZ);
         if (this.world.getBlockState(new BlockPos(i, j - 1, k)).isIn(BlockTags.RAILS)) {
            --j;
         }

         BlockPos blockpos = new BlockPos(i, j, k);
         BlockState blockstate = this.world.getBlockState(blockpos);
         if (canUseRail() && blockstate.isIn(BlockTags.RAILS)) {
            this.moveAlongTrack(blockpos, blockstate);
            if (blockstate.getBlock() == Blocks.ACTIVATOR_RAIL) {
               this.onActivatorRailPass(i, j, k, blockstate.get(PoweredRailBlock.POWERED));
            }
         } else {
            this.moveDerailedMinecart();
         }

         this.doBlockCollisions();
         this.rotationPitch = 0.0F;
         double d0 = this.prevPosX - this.posX;
         double d2 = this.prevPosZ - this.posZ;
         if (d0 * d0 + d2 * d2 > 0.001D) {
            this.rotationYaw = (float)(MathHelper.atan2(d2, d0) * 180.0D / Math.PI);
            if (this.isInReverse) {
               this.rotationYaw += 180.0F;
            }
         }

         double d3 = (double)MathHelper.wrapDegrees(this.rotationYaw - this.prevRotationYaw);
         if (d3 < -170.0D || d3 >= 170.0D) {
            this.rotationYaw += 180.0F;
            this.isInReverse = !this.isInReverse;
         }

         this.setRotation(this.rotationYaw, this.rotationPitch);
         AxisAlignedBB box;
         if (getCollisionHandler() != null) box = getCollisionHandler().getMinecartCollisionBox(this);
         else                               box = this.getBoundingBox().grow(0.2F, 0.0D, 0.2F);
         if (canBeRidden() && func_213296_b(this.getMotion()) > 0.01D) {
            List<Entity> list = this.world.getEntitiesInAABBexcluding(this, box, EntityPredicates.pushableBy(this));
            if (!list.isEmpty()) {
               for(int l = 0; l < list.size(); ++l) {
                  Entity entity1 = list.get(l);
                  if (!(entity1 instanceof PlayerEntity) && !(entity1 instanceof IronGolemEntity) && !(entity1 instanceof AbstractMinecartEntity) && !this.isBeingRidden() && !entity1.isPassenger()) {
                     entity1.startRiding(this);
                  } else {
                     entity1.applyEntityCollision(this);
                  }
               }
            }
         } else {
            for(Entity entity : this.world.getEntitiesWithinAABBExcludingEntity(this, box)) {
               if (!this.isPassenger(entity) && entity.canBePushed() && entity instanceof AbstractMinecartEntity) {
                  entity.applyEntityCollision(this);
               }
            }
         }

         this.handleWaterMovement();
         //net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.minecart.MinecartUpdateEvent(this, this.getCurrentRailPosition()));
      }
   }

   /**
    * Get's the maximum speed for a minecart
    */
   protected double getMaximumSpeed() {
      return 0.4D;
   }

   /**
    * Called every tick the minecart is on an activator rail.
    */
   public void onActivatorRailPass(int x, int y, int z, boolean receivingPower) {
   }

   /**
    * Moves a minecart that is not attached to a rail
    */
   protected void moveDerailedMinecart() {
      double d0 = onGround ? this.getMaximumSpeed() : getMaxSpeedAirLateral();
      Vec3d vec3d = this.getMotion();
      this.setMotion(MathHelper.clamp(vec3d.x, -d0, d0), vec3d.y, MathHelper.clamp(vec3d.z, -d0, d0));

      if(getMaxSpeedAirVertical() > 0 && getMotion().y > getMaxSpeedAirVertical()) {
          if(Math.abs(getMotion().x) < 0.3f && Math.abs(getMotion().z) < 0.3f)
              setMotion(new Vec3d(getMotion().x, 0.15f, getMotion().z));
          else
              setMotion(new Vec3d(getMotion().x, getMaxSpeedAirVertical(), getMotion().z));
      }

      if (this.onGround) {
         this.setMotion(this.getMotion().scale(0.5D));
      }

      this.move(MoverType.SELF, this.getMotion());
      if (!this.onGround) {
         this.setMotion(this.getMotion().scale(getDragAir()));
      }

   }

   protected void moveAlongTrack(BlockPos pos, BlockState state) {
      this.fallDistance = 0.0F;
      Vec3d vec3d = this.getPos(this.posX, this.posY, this.posZ);
      this.posY = (double)pos.getY();
      boolean flag = false;
      boolean flag1 = false;
      AbstractRailBlock abstractrailblock = (AbstractRailBlock)state.getBlock();
      if (abstractrailblock == Blocks.POWERED_RAIL) {
         flag = state.get(PoweredRailBlock.POWERED);
         flag1 = !flag;
      }

      double d0 = getSlopeAdjustment();
      Vec3d vec3d1 = this.getMotion();
      RailShape railshape = ((AbstractRailBlock)state.getBlock()).getRailDirection(state, this.world, pos, this);
      switch(railshape) {
      case ASCENDING_EAST:
         this.setMotion(vec3d1.add(-1*d0, 0.0D, 0.0D));
         ++this.posY;
         break;
      case ASCENDING_WEST:
         this.setMotion(vec3d1.add(d0, 0.0D, 0.0D));
         ++this.posY;
         break;
      case ASCENDING_NORTH:
         this.setMotion(vec3d1.add(0.0D, 0.0D, d0));
         ++this.posY;
         break;
      case ASCENDING_SOUTH:
         this.setMotion(vec3d1.add(0.0D, 0.0D, -1*d0));
         ++this.posY;
      }

      vec3d1 = this.getMotion();
      int[][] aint = MATRIX[railshape.getMeta()];
      double d1 = (double)(aint[1][0] - aint[0][0]);
      double d2 = (double)(aint[1][2] - aint[0][2]);
      double d3 = Math.sqrt(d1 * d1 + d2 * d2);
      double d4 = vec3d1.x * d1 + vec3d1.z * d2;
      if (d4 < 0.0D) {
         d1 = -d1;
         d2 = -d2;
      }

      double d5 = Math.min(2.0D, Math.sqrt(func_213296_b(vec3d1)));
      vec3d1 = new Vec3d(d5 * d1 / d3, vec3d1.y, d5 * d2 / d3);
      this.setMotion(vec3d1);
      Entity entity = this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
      if (entity instanceof PlayerEntity) {
         Vec3d vec3d2 = entity.getMotion();
         double d6 = func_213296_b(vec3d2);
         double d8 = func_213296_b(this.getMotion());
         if (d6 > 1.0E-4D && d8 < 0.01D) {
            this.setMotion(this.getMotion().add(vec3d2.x * 0.1D, 0.0D, vec3d2.z * 0.1D));
            flag1 = false;
         }
      }

      if (flag1 && shouldDoRailFunctions()) {
         double d19 = Math.sqrt(func_213296_b(this.getMotion()));
         if (d19 < 0.03D) {
            this.setMotion(Vec3d.ZERO);
         } else {
            this.setMotion(this.getMotion().mul(0.5D, 0.0D, 0.5D));
         }
      }

      double d20 = (double)pos.getX() + 0.5D + (double)aint[0][0] * 0.5D;
      double d7 = (double)pos.getZ() + 0.5D + (double)aint[0][2] * 0.5D;
      double d9 = (double)pos.getX() + 0.5D + (double)aint[1][0] * 0.5D;
      double d10 = (double)pos.getZ() + 0.5D + (double)aint[1][2] * 0.5D;
      d1 = d9 - d20;
      d2 = d10 - d7;
      double d11;
      if (d1 == 0.0D) {
         this.posX = (double)pos.getX() + 0.5D;
         d11 = this.posZ - (double)pos.getZ();
      } else if (d2 == 0.0D) {
         this.posZ = (double)pos.getZ() + 0.5D;
         d11 = this.posX - (double)pos.getX();
      } else {
         double d12 = this.posX - d20;
         double d13 = this.posZ - d7;
         d11 = (d12 * d1 + d13 * d2) * 2.0D;
      }

      this.posX = d20 + d1 * d11;
      this.posZ = d7 + d2 * d11;
      this.setPosition(this.posX, this.posY, this.posZ);
      this.moveMinecartOnRail(pos);
      if (aint[0][1] != 0 && MathHelper.floor(this.posX) - pos.getX() == aint[0][0] && MathHelper.floor(this.posZ) - pos.getZ() == aint[0][2]) {
         this.setPosition(this.posX, this.posY + (double)aint[0][1], this.posZ);
      } else if (aint[1][1] != 0 && MathHelper.floor(this.posX) - pos.getX() == aint[1][0] && MathHelper.floor(this.posZ) - pos.getZ() == aint[1][2]) {
         this.setPosition(this.posX, this.posY + (double)aint[1][1], this.posZ);
      }

      this.applyDrag();
      Vec3d vec3d3 = this.getPos(this.posX, this.posY, this.posZ);
      if (vec3d3 != null && vec3d != null) {
         double d14 = (vec3d.y - vec3d3.y) * 0.05D;
         Vec3d vec3d4 = this.getMotion();
         double d15 = Math.sqrt(func_213296_b(vec3d4));
         if (d15 > 0.0D) {
            this.setMotion(vec3d4.mul((d15 + d14) / d15, 1.0D, (d15 + d14) / d15));
         }

         this.setPosition(this.posX, vec3d3.y, this.posZ);
      }

      int j = MathHelper.floor(this.posX);
      int i = MathHelper.floor(this.posZ);
      if (j != pos.getX() || i != pos.getZ()) {
         Vec3d vec3d5 = this.getMotion();
         double d23 = Math.sqrt(func_213296_b(vec3d5));
         this.setMotion(d23 * (double)(j - pos.getX()), vec3d5.y, d23 * (double)(i - pos.getZ()));
      }

      if (shouldDoRailFunctions())
          ((AbstractRailBlock)state.getBlock()).onMinecartPass(state, world, pos, this);

      if (flag && shouldDoRailFunctions()) {
         Vec3d vec3d6 = this.getMotion();
         double d24 = Math.sqrt(func_213296_b(vec3d6));
         if (d24 > 0.01D) {
            double d16 = 0.06D;
            this.setMotion(vec3d6.add(vec3d6.x / d24 * 0.06D, 0.0D, vec3d6.z / d24 * 0.06D));
         } else {
            Vec3d vec3d7 = this.getMotion();
            double d17 = vec3d7.x;
            double d18 = vec3d7.z;
            if (railshape == RailShape.EAST_WEST) {
               if (this.func_213900_a(pos.west())) {
                  d17 = 0.02D;
               } else if (this.func_213900_a(pos.east())) {
                  d17 = -0.02D;
               }
            } else {
               if (railshape != RailShape.NORTH_SOUTH) {
                  return;
               }

               if (this.func_213900_a(pos.north())) {
                  d18 = 0.02D;
               } else if (this.func_213900_a(pos.south())) {
                  d18 = -0.02D;
               }
            }

            this.setMotion(d17, vec3d7.y, d18);
         }
      }

   }

   private boolean func_213900_a(BlockPos p_213900_1_) {
      return this.world.getBlockState(p_213900_1_).isNormalCube(this.world, p_213900_1_);
   }

   protected void applyDrag() {
      double d0 = this.isBeingRidden() ? 0.997D : 0.96D;
      this.setMotion(this.getMotion().mul(d0, 0.0D, d0));
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public Vec3d getPosOffset(double x, double y, double z, double offset) {
      int i = MathHelper.floor(x);
      int j = MathHelper.floor(y);
      int k = MathHelper.floor(z);
      if (this.world.getBlockState(new BlockPos(i, j - 1, k)).isIn(BlockTags.RAILS)) {
         --j;
      }

      BlockState blockstate = this.world.getBlockState(new BlockPos(i, j, k));
      if (blockstate.isIn(BlockTags.RAILS)) {
         RailShape railshape = ((AbstractRailBlock)blockstate.getBlock()).getRailDirection(blockstate, this.world, new BlockPos(i, j, k), this);
         y = (double)j;
         if (railshape.isAscending()) {
            y = (double)(j + 1);
         }

         int[][] aint = MATRIX[railshape.getMeta()];
         double d0 = (double)(aint[1][0] - aint[0][0]);
         double d1 = (double)(aint[1][2] - aint[0][2]);
         double d2 = Math.sqrt(d0 * d0 + d1 * d1);
         d0 = d0 / d2;
         d1 = d1 / d2;
         x = x + d0 * offset;
         z = z + d1 * offset;
         if (aint[0][1] != 0 && MathHelper.floor(x) - i == aint[0][0] && MathHelper.floor(z) - k == aint[0][2]) {
            y += (double)aint[0][1];
         } else if (aint[1][1] != 0 && MathHelper.floor(x) - i == aint[1][0] && MathHelper.floor(z) - k == aint[1][2]) {
            y += (double)aint[1][1];
         }

         return this.getPos(x, y, z);
      } else {
         return null;
      }
   }

   @Nullable
   public Vec3d getPos(double p_70489_1_, double p_70489_3_, double p_70489_5_) {
      int i = MathHelper.floor(p_70489_1_);
      int j = MathHelper.floor(p_70489_3_);
      int k = MathHelper.floor(p_70489_5_);
      if (this.world.getBlockState(new BlockPos(i, j - 1, k)).isIn(BlockTags.RAILS)) {
         --j;
      }

      BlockState blockstate = this.world.getBlockState(new BlockPos(i, j, k));
      if (blockstate.isIn(BlockTags.RAILS)) {
         RailShape railshape = ((AbstractRailBlock)blockstate.getBlock()).getRailDirection(blockstate, this.world, new BlockPos(i, j, k), this);
         int[][] aint = MATRIX[railshape.getMeta()];
         double d0 = (double)i + 0.5D + (double)aint[0][0] * 0.5D;
         double d1 = (double)j + 0.0625D + (double)aint[0][1] * 0.5D;
         double d2 = (double)k + 0.5D + (double)aint[0][2] * 0.5D;
         double d3 = (double)i + 0.5D + (double)aint[1][0] * 0.5D;
         double d4 = (double)j + 0.0625D + (double)aint[1][1] * 0.5D;
         double d5 = (double)k + 0.5D + (double)aint[1][2] * 0.5D;
         double d6 = d3 - d0;
         double d7 = (d4 - d1) * 2.0D;
         double d8 = d5 - d2;
         double d9;
         if (d6 == 0.0D) {
            d9 = p_70489_5_ - (double)k;
         } else if (d8 == 0.0D) {
            d9 = p_70489_1_ - (double)i;
         } else {
            double d10 = p_70489_1_ - d0;
            double d11 = p_70489_5_ - d2;
            d9 = (d10 * d6 + d11 * d8) * 2.0D;
         }

         p_70489_1_ = d0 + d6 * d9;
         p_70489_3_ = d1 + d7 * d9;
         p_70489_5_ = d2 + d8 * d9;
         if (d7 < 0.0D) {
            ++p_70489_3_;
         }

         if (d7 > 0.0D) {
            p_70489_3_ += 0.5D;
         }

         return new Vec3d(p_70489_1_, p_70489_3_, p_70489_5_);
      } else {
         return null;
      }
   }

   /**
    * Gets the bounding box of this Entity, adjusted to take auxiliary entities into account (e.g. the tile contained by
    * a minecart, such as a command block).
    */
   @OnlyIn(Dist.CLIENT)
   public AxisAlignedBB getRenderBoundingBox() {
      AxisAlignedBB axisalignedbb = this.getBoundingBox();
      return this.hasDisplayTile() ? axisalignedbb.grow((double)Math.abs(this.getDisplayTileOffset()) / 16.0D) : axisalignedbb;
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   protected void readAdditional(CompoundNBT compound) {
      if (compound.getBoolean("CustomDisplayTile")) {
         this.setDisplayTile(NBTUtil.readBlockState(compound.getCompound("DisplayState")));
         this.setDisplayTileOffset(compound.getInt("DisplayOffset"));
      }

   }

   protected void writeAdditional(CompoundNBT compound) {
      if (this.hasDisplayTile()) {
         compound.putBoolean("CustomDisplayTile", true);
         compound.put("DisplayState", NBTUtil.writeBlockState(this.getDisplayTile()));
         compound.putInt("DisplayOffset", this.getDisplayTileOffset());
      }

   }

   /**
    * Applies a velocity to the entities, to push them away from eachother.
    */
   public void applyEntityCollision(Entity entityIn) {
      //net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.minecart.MinecartCollisionEvent(this, entityIn));
      if (getCollisionHandler() != null) {
         getCollisionHandler().onEntityCollision(this, entityIn);
         return;
      }
      if (!this.world.isRemote) {
         if (!entityIn.noClip && !this.noClip) {
            if (!this.isPassenger(entityIn)) {
               double d0 = entityIn.posX - this.posX;
               double d1 = entityIn.posZ - this.posZ;
               double d2 = d0 * d0 + d1 * d1;
               if (d2 >= (double)1.0E-4F) {
                  d2 = (double)MathHelper.sqrt(d2);
                  d0 = d0 / d2;
                  d1 = d1 / d2;
                  double d3 = 1.0D / d2;
                  if (d3 > 1.0D) {
                     d3 = 1.0D;
                  }

                  d0 = d0 * d3;
                  d1 = d1 * d3;
                  d0 = d0 * (double)0.1F;
                  d1 = d1 * (double)0.1F;
                  d0 = d0 * (double)(1.0F - this.entityCollisionReduction);
                  d1 = d1 * (double)(1.0F - this.entityCollisionReduction);
                  d0 = d0 * 0.5D;
                  d1 = d1 * 0.5D;
                  if (entityIn instanceof AbstractMinecartEntity) {
                     double d4 = entityIn.posX - this.posX;
                     double d5 = entityIn.posZ - this.posZ;
                     Vec3d vec3d = (new Vec3d(d4, 0.0D, d5)).normalize();
                     Vec3d vec3d1 = (new Vec3d((double)MathHelper.cos(this.rotationYaw * ((float)Math.PI / 180F)), 0.0D, (double)MathHelper.sin(this.rotationYaw * ((float)Math.PI / 180F)))).normalize();
                     double d6 = Math.abs(vec3d.dotProduct(vec3d1));
                     if (d6 < (double)0.8F) {
                        return;
                     }

                     Vec3d vec3d2 = this.getMotion();
                     Vec3d vec3d3 = entityIn.getMotion();
                     if (((AbstractMinecartEntity)entityIn).isPoweredCart() && !this.isPoweredCart()) {
                        this.setMotion(vec3d2.mul(0.2D, 1.0D, 0.2D));
                        this.addVelocity(vec3d3.x - d0, 0.0D, vec3d3.z - d1);
                        entityIn.setMotion(vec3d3.mul(0.95D, 1.0D, 0.95D));
                     } else if (!((AbstractMinecartEntity)entityIn).isPoweredCart() && this.isPoweredCart()) {
                        entityIn.setMotion(vec3d3.mul(0.2D, 1.0D, 0.2D));
                        entityIn.addVelocity(vec3d2.x + d0, 0.0D, vec3d2.z + d1);
                        this.setMotion(vec3d2.mul(0.95D, 1.0D, 0.95D));
                     } else {
                        double d7 = (vec3d3.x + vec3d2.x) / 2.0D;
                        double d8 = (vec3d3.z + vec3d2.z) / 2.0D;
                        this.setMotion(vec3d2.mul(0.2D, 1.0D, 0.2D));
                        this.addVelocity(d7 - d0, 0.0D, d8 - d1);
                        entityIn.setMotion(vec3d3.mul(0.2D, 1.0D, 0.2D));
                        entityIn.addVelocity(d7 + d0, 0.0D, d8 + d1);
                     }
                  } else {
                     this.addVelocity(-d0, 0.0D, -d1);
                     entityIn.addVelocity(d0 / 4.0D, 0.0D, d1 / 4.0D);
                  }
               }

            }
         }
      }
   }

   /**
    * Sets a target for the client to interpolate towards over the next few ticks
    */
   @OnlyIn(Dist.CLIENT)
   public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
      this.minecartX = x;
      this.minecartY = y;
      this.minecartZ = z;
      this.minecartYaw = (double)yaw;
      this.minecartPitch = (double)pitch;
      this.turnProgress = posRotationIncrements + 2;
      this.setMotion(this.velocityX, this.velocityY, this.velocityZ);
   }

   /**
    * Updates the entity motion clientside, called by packets from the server
    */
   @OnlyIn(Dist.CLIENT)
   public void setVelocity(double x, double y, double z) {
      this.velocityX = x;
      this.velocityY = y;
      this.velocityZ = z;
      this.setMotion(this.velocityX, this.velocityY, this.velocityZ);
   }

   /**
    * Sets the current amount of damage the minecart has taken. Decreases over time. The cart breaks when this is over
    * 40.
    */
   public void setDamage(float damage) {
      this.dataManager.set(DAMAGE, damage);
   }

   /**
    * Gets the current amount of damage the minecart has taken. Decreases over time. The cart breaks when this is over
    * 40.
    */
   public float getDamage() {
      return this.dataManager.get(DAMAGE);
   }

   /**
    * Sets the rolling amplitude the cart rolls while being attacked.
    */
   public void setRollingAmplitude(int rollingAmplitude) {
      this.dataManager.set(ROLLING_AMPLITUDE, rollingAmplitude);
   }

   /**
    * Gets the rolling amplitude the cart rolls while being attacked.
    */
   public int getRollingAmplitude() {
      return this.dataManager.get(ROLLING_AMPLITUDE);
   }

   /**
    * Sets the rolling direction the cart rolls while being attacked. Can be 1 or -1.
    */
   public void setRollingDirection(int rollingDirection) {
      this.dataManager.set(ROLLING_DIRECTION, rollingDirection);
   }

   /**
    * Gets the rolling direction the cart rolls while being attacked. Can be 1 or -1.
    */
   public int getRollingDirection() {
      return this.dataManager.get(ROLLING_DIRECTION);
   }

   public abstract AbstractMinecartEntity.Type getMinecartType();

   public BlockState getDisplayTile() {
      return !this.hasDisplayTile() ? this.getDefaultDisplayTile() : Block.getStateById(this.getDataManager().get(DISPLAY_TILE));
   }

   public BlockState getDefaultDisplayTile() {
      return Blocks.AIR.getDefaultState();
   }

   public int getDisplayTileOffset() {
      return !this.hasDisplayTile() ? this.getDefaultDisplayTileOffset() : this.getDataManager().get(DISPLAY_TILE_OFFSET);
   }

   public int getDefaultDisplayTileOffset() {
      return 6;
   }

   public void setDisplayTile(BlockState displayTile) {
      this.getDataManager().set(DISPLAY_TILE, Block.getStateId(displayTile));
      this.setHasDisplayTile(true);
   }

   public void setDisplayTileOffset(int displayTileOffset) {
      this.getDataManager().set(DISPLAY_TILE_OFFSET, displayTileOffset);
      this.setHasDisplayTile(true);
   }

   public boolean hasDisplayTile() {
      return this.getDataManager().get(SHOW_BLOCK);
   }

   public void setHasDisplayTile(boolean showBlock) {
      this.getDataManager().set(SHOW_BLOCK, showBlock);
   }

   public IPacket<?> createSpawnPacket() {
      return new SSpawnObjectPacket(this);
   }

   private boolean canUseRail = true;
   @Override public boolean canUseRail() { return canUseRail; }
   @Override public void setCanUseRail(boolean value) { this.canUseRail = value; }
   private float currentSpeedOnRail;
   @Override public float getCurrentCartSpeedCapOnRail() { return currentSpeedOnRail; }
   @Override public void setCurrentCartSpeedCapOnRail(float value) { currentSpeedOnRail = Math.min(value, getMaxCartSpeedOnRail()); }
   private float maxSpeedAirLateral = DEFAULT_MAX_SPEED_AIR_LATERAL;
   @Override public float getMaxSpeedAirLateral() { return maxSpeedAirLateral; }
   @Override public void setMaxSpeedAirLateral(float value) { maxSpeedAirLateral = value; }
   private float maxSpeedAirVertical = DEFAULT_MAX_SPEED_AIR_VERTICAL;
   @Override public float getMaxSpeedAirVertical() { return maxSpeedAirVertical; }
   @Override public void setMaxSpeedAirVertical(float value) { maxSpeedAirVertical = value; }
   private double dragAir = DEFAULT_AIR_DRAG;
   @Override public double getDragAir() { return dragAir; }
   @Override public void setDragAir(double value) { dragAir = value; }
   @Override
   public double getMaxSpeed() { //Non-default because getMaximumSpeed is protected
      if (!canUseRail()) return getMaximumSpeed();
      BlockPos pos = this.getCurrentRailPosition();
      BlockState state = getMinecart().world.getBlockState(pos);
      if (!state.isIn(BlockTags.RAILS)) return getMaximumSpeed();

      float railMaxSpeed = ((AbstractRailBlock)state.getBlock()).getRailMaxSpeed(state, getMinecart().world, pos, getMinecart());
      return Math.min(railMaxSpeed, getCurrentCartSpeedCapOnRail());
   }
   @Override
   public void moveMinecartOnRail(BlockPos pos) { //Non-default because getMaximumSpeed is protected
      AbstractMinecartEntity mc = getMinecart();
      double d21 = mc.isBeingRidden() ? 0.75D : 1.0D;
      double d22 = mc.getMaximumSpeed();
      Vec3d vec3d1 = mc.getMotion();
      mc.move(MoverType.SELF, new Vec3d(MathHelper.clamp(d21 * vec3d1.x, -d22, d22), 0.0D, MathHelper.clamp(d21 * vec3d1.z, -d22, d22)));
   }

   public static enum Type {
      RIDEABLE,
      CHEST,
      FURNACE,
      TNT,
      SPAWNER,
      HOPPER,
      COMMAND_BLOCK;
   }
}