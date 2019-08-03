package net.minecraft.entity.merchant.villager;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.INPC;
import net.minecraft.entity.Pose;
import net.minecraft.entity.merchant.IMerchant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AbstractVillagerEntity extends AgeableEntity implements INPC, IMerchant {
   private static final DataParameter<Integer> SHAKE_HEAD_TICKS = EntityDataManager.createKey(AbstractVillagerEntity.class, DataSerializers.VARINT);
   @Nullable
   private PlayerEntity customer;
   @Nullable
   protected MerchantOffers offers;
   private final Inventory field_213722_bB = new Inventory(8);

   public AbstractVillagerEntity(EntityType<? extends AbstractVillagerEntity> p_i50185_1_, World p_i50185_2_) {
      super(p_i50185_1_, p_i50185_2_);
   }

   public int getShakeHeadTicks() {
      return this.dataManager.get(SHAKE_HEAD_TICKS);
   }

   public void setShakeHeadTicks(int p_213720_1_) {
      this.dataManager.set(SHAKE_HEAD_TICKS, p_213720_1_);
   }

   public int getXp() {
      return 0;
   }

   protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
      return this.isChild() ? 0.81F : 1.62F;
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(SHAKE_HEAD_TICKS, 0);
   }

   public void setCustomer(@Nullable PlayerEntity player) {
      this.customer = player;
   }

   @Nullable
   public PlayerEntity getCustomer() {
      return this.customer;
   }

   public boolean func_213716_dX() {
      return this.customer != null;
   }

   public MerchantOffers getOffers() {
      if (this.offers == null) {
         this.offers = new MerchantOffers();
         this.func_213712_ef();
      }

      return this.offers;
   }

   @OnlyIn(Dist.CLIENT)
   public void func_213703_a(@Nullable MerchantOffers p_213703_1_) {
   }

   public void func_213702_q(int p_213702_1_) {
   }

   public void onTrade(MerchantOffer p_213704_1_) {
      p_213704_1_.func_222219_j();
      this.livingSoundTime = -this.getTalkInterval();
      this.func_213713_b(p_213704_1_);
      if (this.customer instanceof ServerPlayerEntity) {
         CriteriaTriggers.VILLAGER_TRADE.func_215114_a((ServerPlayerEntity)this.customer, this, p_213704_1_.func_222200_d());
      }

   }

   protected abstract void func_213713_b(MerchantOffer p_213713_1_);

   public boolean func_213705_dZ() {
      return true;
   }

   /**
    * Notifies the merchant of a possible merchantrecipe being fulfilled or not. Usually, this is just a sound byte
    * being played depending if the suggested itemstack is not null.
    */
   public void verifySellingItem(ItemStack stack) {
      if (!this.world.isRemote && this.livingSoundTime > -this.getTalkInterval() + 20) {
         this.livingSoundTime = -this.getTalkInterval();
         this.playSound(this.func_213721_r(!stack.isEmpty()), this.getSoundVolume(), this.getSoundPitch());
      }

   }

   public SoundEvent func_213714_ea() {
      return SoundEvents.ENTITY_VILLAGER_YES;
   }

   protected SoundEvent func_213721_r(boolean p_213721_1_) {
      return p_213721_1_ ? SoundEvents.ENTITY_VILLAGER_YES : SoundEvents.ENTITY_VILLAGER_NO;
   }

   public void func_213711_eb() {
      this.playSound(SoundEvents.ENTITY_VILLAGER_CELEBRATE, this.getSoundVolume(), this.getSoundPitch());
   }

   public void writeAdditional(CompoundNBT compound) {
      super.writeAdditional(compound);
      MerchantOffers merchantoffers = this.getOffers();
      if (!merchantoffers.isEmpty()) {
         compound.put("Offers", merchantoffers.func_222199_a());
      }

      ListNBT listnbt = new ListNBT();

      for(int i = 0; i < this.field_213722_bB.getSizeInventory(); ++i) {
         ItemStack itemstack = this.field_213722_bB.getStackInSlot(i);
         if (!itemstack.isEmpty()) {
            listnbt.add(itemstack.write(new CompoundNBT()));
         }
      }

      compound.put("Inventory", listnbt);
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(CompoundNBT compound) {
      super.readAdditional(compound);
      if (compound.contains("Offers", 10)) {
         this.offers = new MerchantOffers(compound.getCompound("Offers"));
      }

      ListNBT listnbt = compound.getList("Inventory", 10);

      for(int i = 0; i < listnbt.size(); ++i) {
         ItemStack itemstack = ItemStack.read(listnbt.getCompound(i));
         if (!itemstack.isEmpty()) {
            this.field_213722_bB.addItem(itemstack);
         }
      }

   }

   @Nullable
   public Entity changeDimension(DimensionType destination) {
      this.func_213750_eg();
      return super.changeDimension(destination);
   }

   protected void func_213750_eg() {
      this.setCustomer((PlayerEntity)null);
   }

   /**
    * Called when the mob's health reaches 0.
    */
   public void onDeath(DamageSource cause) {
      super.onDeath(cause);
      this.func_213750_eg();
   }

   @OnlyIn(Dist.CLIENT)
   protected void func_213718_a(IParticleData p_213718_1_) {
      for(int i = 0; i < 5; ++i) {
         double d0 = this.rand.nextGaussian() * 0.02D;
         double d1 = this.rand.nextGaussian() * 0.02D;
         double d2 = this.rand.nextGaussian() * 0.02D;
         this.world.addParticle(p_213718_1_, this.posX + (double)(this.rand.nextFloat() * this.getWidth() * 2.0F) - (double)this.getWidth(), this.posY + 1.0D + (double)(this.rand.nextFloat() * this.getHeight()), this.posZ + (double)(this.rand.nextFloat() * this.getWidth() * 2.0F) - (double)this.getWidth(), d0, d1, d2);
      }

   }

   public boolean canBeLeashedTo(PlayerEntity player) {
      return false;
   }

   public Inventory func_213715_ed() {
      return this.field_213722_bB;
   }

   public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn) {
      if (super.replaceItemInInventory(inventorySlot, itemStackIn)) {
         return true;
      } else {
         int i = inventorySlot - 300;
         if (i >= 0 && i < this.field_213722_bB.getSizeInventory()) {
            this.field_213722_bB.setInventorySlotContents(i, itemStackIn);
            return true;
         } else {
            return false;
         }
      }
   }

   public World getWorld() {
      return this.world;
   }

   protected abstract void func_213712_ef();

   protected void func_213717_a(MerchantOffers p_213717_1_, VillagerTrades.ITrade[] p_213717_2_, int p_213717_3_) {
      Set<Integer> set = Sets.newHashSet();
      if (p_213717_2_.length > p_213717_3_) {
         while(set.size() < p_213717_3_) {
            set.add(this.rand.nextInt(p_213717_2_.length));
         }
      } else {
         for(int i = 0; i < p_213717_2_.length; ++i) {
            set.add(i);
         }
      }

      for(Integer integer : set) {
         VillagerTrades.ITrade villagertrades$itrade = p_213717_2_[integer];
         MerchantOffer merchantoffer = villagertrades$itrade.func_221182_a(this, this.rand);
         if (merchantoffer != null) {
            p_213717_1_.add(merchantoffer);
         }
      }

   }
}