package net.minecraft.entity.monster;

import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShootableItem;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public abstract class MonsterEntity extends CreatureEntity implements IMob {
   protected MonsterEntity(EntityType<? extends MonsterEntity> type, World p_i48553_2_) {
      super(type, p_i48553_2_);
      this.experienceValue = 5;
   }

   public SoundCategory getSoundCategory() {
      return SoundCategory.HOSTILE;
   }

   /**
    * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
    * use this to react to sunlight and start to burn.
    */
   public void livingTick() {
      this.updateArmSwingProgress();
      this.func_213623_ec();
      super.livingTick();
   }

   protected void func_213623_ec() {
      float f = this.getBrightness();
      if (f > 0.5F) {
         this.idleTime += 2;
      }

   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      super.tick();
      if (!this.world.isRemote && this.world.getDifficulty() == Difficulty.PEACEFUL) {
         this.remove();
      }

   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.ENTITY_HOSTILE_SWIM;
   }

   protected SoundEvent getSplashSound() {
      return SoundEvents.ENTITY_HOSTILE_SPLASH;
   }

   /**
    * Called when the entity is attacked.
    */
   public boolean attackEntityFrom(DamageSource source, float amount) {
      return this.isInvulnerableTo(source) ? false : super.attackEntityFrom(source, amount);
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_HOSTILE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_HOSTILE_DEATH;
   }

   protected SoundEvent getFallSound(int heightIn) {
      return heightIn > 4 ? SoundEvents.ENTITY_HOSTILE_BIG_FALL : SoundEvents.ENTITY_HOSTILE_SMALL_FALL;
   }

   public float getBlockPathWeight(BlockPos pos, IWorldReader worldIn) {
      return 0.5F - worldIn.getBrightness(pos);
   }

   public static boolean func_223323_a(IWorld p_223323_0_, BlockPos p_223323_1_, Random p_223323_2_) {
      if (p_223323_0_.getLightFor(LightType.SKY, p_223323_1_) > p_223323_2_.nextInt(32)) {
         return false;
      } else {
         int i = p_223323_0_.getWorld().isThundering() ? p_223323_0_.getNeighborAwareLightSubtracted(p_223323_1_, 10) : p_223323_0_.getLight(p_223323_1_);
         return i <= p_223323_2_.nextInt(8);
      }
   }

   public static boolean func_223325_c(EntityType<? extends MonsterEntity> p_223325_0_, IWorld p_223325_1_, SpawnReason p_223325_2_, BlockPos p_223325_3_, Random p_223325_4_) {
      return p_223325_1_.getDifficulty() != Difficulty.PEACEFUL && func_223323_a(p_223325_1_, p_223325_3_, p_223325_4_) && func_223315_a(p_223325_0_, p_223325_1_, p_223325_2_, p_223325_3_, p_223325_4_);
   }

   public static boolean func_223324_d(EntityType<? extends MonsterEntity> p_223324_0_, IWorld p_223324_1_, SpawnReason p_223324_2_, BlockPos p_223324_3_, Random p_223324_4_) {
      return p_223324_1_.getDifficulty() != Difficulty.PEACEFUL && func_223315_a(p_223324_0_, p_223324_1_, p_223324_2_, p_223324_3_, p_223324_4_);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
   }

   /**
    * Entity won't drop items or experience points if this returns false
    */
   protected boolean canDropLoot() {
      return true;
   }

   public boolean isPreventingPlayerRest(PlayerEntity playerIn) {
      return true;
   }

   public ItemStack func_213356_f(ItemStack p_213356_1_) {
      if (p_213356_1_.getItem() instanceof ShootableItem) {
         Predicate<ItemStack> predicate = ((ShootableItem)p_213356_1_.getItem()).getAmmoPredicate();
         ItemStack itemstack = ShootableItem.getHeldAmmo(this, predicate);
         return itemstack.isEmpty() ? new ItemStack(Items.ARROW) : itemstack;
      } else {
         return ItemStack.EMPTY;
      }
   }
}