package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(
   value = Dist.CLIENT,
   _interface = IRendersAsItem.class
)
public class EyeOfEnderEntity extends Entity implements IRendersAsItem {
   private static final DataParameter<ItemStack> field_213864_b = EntityDataManager.createKey(EyeOfEnderEntity.class, DataSerializers.ITEMSTACK);
   private double targetX;
   private double targetY;
   private double targetZ;
   private int despawnTimer;
   private boolean shatterOrDrop;

   public EyeOfEnderEntity(EntityType<? extends EyeOfEnderEntity> p_i50169_1_, World p_i50169_2_) {
      super(p_i50169_1_, p_i50169_2_);
   }

   public EyeOfEnderEntity(World worldIn, double x, double y, double z) {
      this(EntityType.EYE_OF_ENDER, worldIn);
      this.despawnTimer = 0;
      this.setPosition(x, y, z);
   }

   public void func_213863_b(ItemStack p_213863_1_) {
      if (p_213863_1_.getItem() != Items.ENDER_EYE || p_213863_1_.hasTag()) {
         this.getDataManager().set(field_213864_b, Util.make(p_213863_1_.copy(), (p_213862_0_) -> {
            p_213862_0_.setCount(1);
         }));
      }

   }

   private ItemStack func_213861_i() {
      return this.getDataManager().get(field_213864_b);
   }

   public ItemStack getItem() {
      ItemStack itemstack = this.func_213861_i();
      return itemstack.isEmpty() ? new ItemStack(Items.ENDER_EYE) : itemstack;
   }

   protected void registerData() {
      this.getDataManager().register(field_213864_b, ItemStack.EMPTY);
   }

   /**
    * Checks if the entity is in range to render.
    */
   @OnlyIn(Dist.CLIENT)
   public boolean isInRangeToRenderDist(double distance) {
      double d0 = this.getBoundingBox().getAverageEdgeLength() * 4.0D;
      if (Double.isNaN(d0)) {
         d0 = 4.0D;
      }

      d0 = d0 * 64.0D;
      return distance < d0 * d0;
   }

   public void moveTowards(BlockPos pos) {
      double d0 = (double)pos.getX();
      int i = pos.getY();
      double d1 = (double)pos.getZ();
      double d2 = d0 - this.posX;
      double d3 = d1 - this.posZ;
      float f = MathHelper.sqrt(d2 * d2 + d3 * d3);
      if (f > 12.0F) {
         this.targetX = this.posX + d2 / (double)f * 12.0D;
         this.targetZ = this.posZ + d3 / (double)f * 12.0D;
         this.targetY = this.posY + 8.0D;
      } else {
         this.targetX = d0;
         this.targetY = (double)i;
         this.targetZ = d1;
      }

      this.despawnTimer = 0;
      this.shatterOrDrop = this.rand.nextInt(5) > 0;
   }

   /**
    * Updates the entity motion clientside, called by packets from the server
    */
   @OnlyIn(Dist.CLIENT)
   public void setVelocity(double x, double y, double z) {
      this.setMotion(x, y, z);
      if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
         float f = MathHelper.sqrt(x * x + z * z);
         this.rotationYaw = (float)(MathHelper.atan2(x, z) * (double)(180F / (float)Math.PI));
         this.rotationPitch = (float)(MathHelper.atan2(y, (double)f) * (double)(180F / (float)Math.PI));
         this.prevRotationYaw = this.rotationYaw;
         this.prevRotationPitch = this.rotationPitch;
      }

   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      this.lastTickPosX = this.posX;
      this.lastTickPosY = this.posY;
      this.lastTickPosZ = this.posZ;
      super.tick();
      Vec3d vec3d = this.getMotion();
      this.posX += vec3d.x;
      this.posY += vec3d.y;
      this.posZ += vec3d.z;
      float f = MathHelper.sqrt(func_213296_b(vec3d));
      this.rotationYaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * (double)(180F / (float)Math.PI));

      for(this.rotationPitch = (float)(MathHelper.atan2(vec3d.y, (double)f) * (double)(180F / (float)Math.PI)); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
         ;
      }

      while(this.rotationPitch - this.prevRotationPitch >= 180.0F) {
         this.prevRotationPitch += 360.0F;
      }

      while(this.rotationYaw - this.prevRotationYaw < -180.0F) {
         this.prevRotationYaw -= 360.0F;
      }

      while(this.rotationYaw - this.prevRotationYaw >= 180.0F) {
         this.prevRotationYaw += 360.0F;
      }

      this.rotationPitch = MathHelper.lerp(0.2F, this.prevRotationPitch, this.rotationPitch);
      this.rotationYaw = MathHelper.lerp(0.2F, this.prevRotationYaw, this.rotationYaw);
      if (!this.world.isRemote) {
         double d0 = this.targetX - this.posX;
         double d1 = this.targetZ - this.posZ;
         float f1 = (float)Math.sqrt(d0 * d0 + d1 * d1);
         float f2 = (float)MathHelper.atan2(d1, d0);
         double d2 = MathHelper.lerp(0.0025D, (double)f, (double)f1);
         double d3 = vec3d.y;
         if (f1 < 1.0F) {
            d2 *= 0.8D;
            d3 *= 0.8D;
         }

         int j = this.posY < this.targetY ? 1 : -1;
         vec3d = new Vec3d(Math.cos((double)f2) * d2, d3 + ((double)j - d3) * (double)0.015F, Math.sin((double)f2) * d2);
         this.setMotion(vec3d);
      }

      float f3 = 0.25F;
      if (this.isInWater()) {
         for(int i = 0; i < 4; ++i) {
            this.world.addParticle(ParticleTypes.BUBBLE, this.posX - vec3d.x * 0.25D, this.posY - vec3d.y * 0.25D, this.posZ - vec3d.z * 0.25D, vec3d.x, vec3d.y, vec3d.z);
         }
      } else {
         this.world.addParticle(ParticleTypes.PORTAL, this.posX - vec3d.x * 0.25D + this.rand.nextDouble() * 0.6D - 0.3D, this.posY - vec3d.y * 0.25D - 0.5D, this.posZ - vec3d.z * 0.25D + this.rand.nextDouble() * 0.6D - 0.3D, vec3d.x, vec3d.y, vec3d.z);
      }

      if (!this.world.isRemote) {
         this.setPosition(this.posX, this.posY, this.posZ);
         ++this.despawnTimer;
         if (this.despawnTimer > 80 && !this.world.isRemote) {
            this.playSound(SoundEvents.ENTITY_ENDER_EYE_DEATH, 1.0F, 1.0F);
            this.remove();
            if (this.shatterOrDrop) {
               this.world.addEntity(new ItemEntity(this.world, this.posX, this.posY, this.posZ, this.getItem()));
            } else {
               this.world.playEvent(2003, new BlockPos(this), 0);
            }
         }
      }

   }

   public void writeAdditional(CompoundNBT compound) {
      ItemStack itemstack = this.func_213861_i();
      if (!itemstack.isEmpty()) {
         compound.put("Item", itemstack.write(new CompoundNBT()));
      }

   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(CompoundNBT compound) {
      ItemStack itemstack = ItemStack.read(compound.getCompound("Item"));
      this.func_213863_b(itemstack);
   }

   /**
    * Gets how bright this entity is.
    */
   public float getBrightness() {
      return 1.0F;
   }

   @OnlyIn(Dist.CLIENT)
   public int getBrightnessForRender() {
      return 15728880;
   }

   /**
    * Returns true if it's possible to attack this entity with an item.
    */
   public boolean canBeAttackedWithItem() {
      return false;
   }

   public IPacket<?> createSpawnPacket() {
      return new SSpawnObjectPacket(this);
   }
}