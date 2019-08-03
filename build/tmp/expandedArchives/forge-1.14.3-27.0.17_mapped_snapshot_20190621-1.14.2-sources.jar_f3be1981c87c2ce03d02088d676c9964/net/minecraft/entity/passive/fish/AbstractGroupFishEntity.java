package net.minecraft.entity.passive.fish;

import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.FollowSchoolLeaderGoal;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public abstract class AbstractGroupFishEntity extends AbstractFishEntity {
   private AbstractGroupFishEntity field_212813_a;
   private int field_212814_b = 1;

   public AbstractGroupFishEntity(EntityType<? extends AbstractGroupFishEntity> p_i49856_1_, World p_i49856_2_) {
      super(p_i49856_1_, p_i49856_2_);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(5, new FollowSchoolLeaderGoal(this));
   }

   /**
    * Will return how many at most can spawn in a chunk at once.
    */
   public int getMaxSpawnedInChunk() {
      return this.getMaxGroupSize();
   }

   public int getMaxGroupSize() {
      return super.getMaxSpawnedInChunk();
   }

   protected boolean func_212800_dy() {
      return !this.func_212802_dB();
   }

   public boolean func_212802_dB() {
      return this.field_212813_a != null && this.field_212813_a.isAlive();
   }

   public AbstractGroupFishEntity func_212803_a(AbstractGroupFishEntity p_212803_1_) {
      this.field_212813_a = p_212803_1_;
      p_212803_1_.func_212807_dH();
      return p_212803_1_;
   }

   public void func_212808_dC() {
      this.field_212813_a.func_212806_dI();
      this.field_212813_a = null;
   }

   private void func_212807_dH() {
      ++this.field_212814_b;
   }

   private void func_212806_dI() {
      --this.field_212814_b;
   }

   public boolean func_212811_dD() {
      return this.func_212812_dE() && this.field_212814_b < this.getMaxGroupSize();
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      super.tick();
      if (this.func_212812_dE() && this.world.rand.nextInt(200) == 1) {
         List<AbstractFishEntity> list = this.world.getEntitiesWithinAABB(this.getClass(), this.getBoundingBox().grow(8.0D, 8.0D, 8.0D));
         if (list.size() <= 1) {
            this.field_212814_b = 1;
         }
      }

   }

   public boolean func_212812_dE() {
      return this.field_212814_b > 1;
   }

   public boolean func_212809_dF() {
      return this.getDistanceSq(this.field_212813_a) <= 121.0D;
   }

   public void func_212805_dG() {
      if (this.func_212802_dB()) {
         this.getNavigator().tryMoveToEntityLiving(this.field_212813_a, 1.0D);
      }

   }

   public void func_212810_a(Stream<AbstractGroupFishEntity> p_212810_1_) {
      p_212810_1_.limit((long)(this.getMaxGroupSize() - this.field_212814_b)).filter((p_212801_1_) -> {
         return p_212801_1_ != this;
      }).forEach((p_212804_1_) -> {
         p_212804_1_.func_212803_a(this);
      });
   }

   @Nullable
   public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
      super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
      if (spawnDataIn == null) {
         spawnDataIn = new AbstractGroupFishEntity.GroupData(this);
      } else {
         this.func_212803_a(((AbstractGroupFishEntity.GroupData)spawnDataIn).field_212822_a);
      }

      return spawnDataIn;
   }

   public static class GroupData implements ILivingEntityData {
      public final AbstractGroupFishEntity field_212822_a;

      public GroupData(AbstractGroupFishEntity p_i49858_1_) {
         this.field_212822_a = p_i49858_1_;
      }
   }
}