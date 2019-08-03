package net.minecraft.potion;

import com.google.common.collect.ComparisonChain;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EffectInstance implements Comparable<EffectInstance>, net.minecraftforge.common.extensions.IForgeEffectInstance {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Effect potion;
   private int duration;
   private int amplifier;
   private boolean isSplashPotion;
   private boolean ambient;
   @OnlyIn(Dist.CLIENT)
   private boolean isPotionDurationMax;
   private boolean showParticles;
   private boolean showIcon;

   public EffectInstance(Effect potionIn) {
      this(potionIn, 0, 0);
   }

   public EffectInstance(Effect potionIn, int durationIn) {
      this(potionIn, durationIn, 0);
   }

   public EffectInstance(Effect potionIn, int durationIn, int amplifierIn) {
      this(potionIn, durationIn, amplifierIn, false, true);
   }

   public EffectInstance(Effect potionIn, int durationIn, int amplifierIn, boolean ambientIn, boolean showParticlesIn) {
      this(potionIn, durationIn, amplifierIn, ambientIn, showParticlesIn, showParticlesIn);
   }

   public EffectInstance(Effect p_i48980_1_, int p_i48980_2_, int p_i48980_3_, boolean p_i48980_4_, boolean p_i48980_5_, boolean p_i48980_6_) {
      this.potion = p_i48980_1_;
      this.duration = p_i48980_2_;
      this.amplifier = p_i48980_3_;
      this.ambient = p_i48980_4_;
      this.showParticles = p_i48980_5_;
      this.showIcon = p_i48980_6_;
   }

   public EffectInstance(EffectInstance other) {
      this.potion = other.potion;
      this.duration = other.duration;
      this.amplifier = other.amplifier;
      this.ambient = other.ambient;
      this.showParticles = other.showParticles;
      this.showIcon = other.showIcon;
      this.curativeItems = other.curativeItems == null ? null : new java.util.ArrayList<net.minecraft.item.ItemStack>(other.curativeItems);
   }

   public boolean combine(EffectInstance other) {
      if (this.potion != other.potion) {
         LOGGER.warn("This method should only be called for matching effects!");
      }

      boolean flag = false;
      if (other.amplifier > this.amplifier) {
         this.amplifier = other.amplifier;
         this.duration = other.duration;
         flag = true;
      } else if (other.amplifier == this.amplifier && this.duration < other.duration) {
         this.duration = other.duration;
         flag = true;
      }

      if (!other.ambient && this.ambient || flag) {
         this.ambient = other.ambient;
         flag = true;
      }

      if (other.showParticles != this.showParticles) {
         this.showParticles = other.showParticles;
         flag = true;
      }

      if (other.showIcon != this.showIcon) {
         this.showIcon = other.showIcon;
         flag = true;
      }

      return flag;
   }

   public Effect getPotion() {
      return this.getPotionRaw() == null ? null : this.getPotionRaw().delegate.get();
   }

   private Effect getPotionRaw() {
      return this.potion;
   }

   public int getDuration() {
      return this.duration;
   }

   public int getAmplifier() {
      return this.amplifier;
   }

   /**
    * Gets whether this potion effect originated from a beacon
    */
   public boolean isAmbient() {
      return this.ambient;
   }

   /**
    * Gets whether this potion effect will show ambient particles or not.
    */
   public boolean doesShowParticles() {
      return this.showParticles;
   }

   public boolean isShowIcon() {
      return this.showIcon;
   }

   public boolean tick(LivingEntity entityIn) {
      if (this.duration > 0) {
         if (this.potion.isReady(this.duration, this.amplifier)) {
            this.performEffect(entityIn);
         }

         this.deincrementDuration();
      }

      return this.duration > 0;
   }

   private int deincrementDuration() {
      return --this.duration;
   }

   public void performEffect(LivingEntity entityIn) {
      if (this.duration > 0) {
         this.potion.performEffect(entityIn, this.amplifier);
      }

   }

   public String getEffectName() {
      return this.potion.getName();
   }

   public String toString() {
      String s;
      if (this.amplifier > 0) {
         s = this.getEffectName() + " x " + (this.amplifier + 1) + ", Duration: " + this.duration;
      } else {
         s = this.getEffectName() + ", Duration: " + this.duration;
      }

      if (this.isSplashPotion) {
         s = s + ", Splash: true";
      }

      if (!this.showParticles) {
         s = s + ", Particles: false";
      }

      if (!this.showIcon) {
         s = s + ", Show Icon: false";
      }

      return s;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof EffectInstance)) {
         return false;
      } else {
         EffectInstance effectinstance = (EffectInstance)p_equals_1_;
         return this.duration == effectinstance.duration && this.amplifier == effectinstance.amplifier && this.isSplashPotion == effectinstance.isSplashPotion && this.ambient == effectinstance.ambient && this.potion.equals(effectinstance.potion);
      }
   }

   public int hashCode() {
      int i = this.potion.hashCode();
      i = 31 * i + this.duration;
      i = 31 * i + this.amplifier;
      i = 31 * i + (this.isSplashPotion ? 1 : 0);
      i = 31 * i + (this.ambient ? 1 : 0);
      return i;
   }

   /**
    * Write a custom potion effect to a potion item's NBT data.
    */
   public CompoundNBT write(CompoundNBT nbt) {
      nbt.putByte("Id", (byte)Effect.getId(this.getPotion()));
      nbt.putByte("Amplifier", (byte)this.getAmplifier());
      nbt.putInt("Duration", this.getDuration());
      nbt.putBoolean("Ambient", this.isAmbient());
      nbt.putBoolean("ShowParticles", this.doesShowParticles());
      nbt.putBoolean("ShowIcon", this.isShowIcon());
      writeCurativeItems(nbt);
      return nbt;
   }

   /**
    * Read a custom potion effect from a potion item's NBT data.
    */
   public static EffectInstance read(CompoundNBT nbt) {
      int i = nbt.getByte("Id") & 0xFF;
      Effect effect = Effect.get(i);
      if (effect == null) {
         return null;
      } else {
         int j = nbt.getByte("Amplifier");
         int k = nbt.getInt("Duration");
         boolean flag = nbt.getBoolean("Ambient");
         boolean flag1 = true;
         if (nbt.contains("ShowParticles", 1)) {
            flag1 = nbt.getBoolean("ShowParticles");
         }

         boolean flag2 = flag1;
         if (nbt.contains("ShowIcon", 1)) {
            flag2 = nbt.getBoolean("ShowIcon");
         }

         return readCurativeItems(new EffectInstance(effect, k, j < 0 ? 0 : j, flag, flag1, flag2), nbt);
      }
   }

   /**
    * Toggle the isPotionDurationMax field.
    */
   @OnlyIn(Dist.CLIENT)
   public void setPotionDurationMax(boolean maxDuration) {
      this.isPotionDurationMax = maxDuration;
   }

   /**
    * Get the value of the isPotionDurationMax field.
    */
   @OnlyIn(Dist.CLIENT)
   public boolean getIsPotionDurationMax() {
      return this.isPotionDurationMax;
   }

   public int compareTo(EffectInstance p_compareTo_1_) {
      int i = 32147;
      return (this.getDuration() <= 32147 || p_compareTo_1_.getDuration() <= 32147) && (!this.isAmbient() || !p_compareTo_1_.isAmbient()) ? ComparisonChain.start().compare(this.isAmbient(), p_compareTo_1_.isAmbient()).compare(this.getDuration(), p_compareTo_1_.getDuration()).compare(this.getPotion().getGuiSortColor(this), p_compareTo_1_.getPotion().getGuiSortColor(this)).result() : ComparisonChain.start().compare(this.isAmbient(), p_compareTo_1_.isAmbient()).compare(this.getPotion().getGuiSortColor(this), p_compareTo_1_.getPotion().getGuiSortColor(this)).result();
   }

   //======================= FORGE START ===========================
   private java.util.List<net.minecraft.item.ItemStack> curativeItems;

   @Override
   public java.util.List<net.minecraft.item.ItemStack> getCurativeItems() {
      if (this.curativeItems == null) //Lazy load this so that we don't create a circular dep on Items.
         this.curativeItems = getPotion().getCurativeItems();
      return this.curativeItems;
   }
   @Override
   public void setCurativeItems(java.util.List<net.minecraft.item.ItemStack> curativeItems) {
      this.curativeItems = curativeItems;
   }
   private static EffectInstance readCurativeItems(EffectInstance effect, CompoundNBT nbt) {
      if (nbt.contains("CurativeItems", net.minecraftforge.common.util.Constants.NBT.TAG_LIST)) {
         java.util.List<net.minecraft.item.ItemStack> items = new java.util.ArrayList<net.minecraft.item.ItemStack>();
         net.minecraft.nbt.ListNBT list = nbt.getList("CurativeItems", net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND);
         for (int i = 0; i < list.size(); i++) {
            items.add(net.minecraft.item.ItemStack.read(list.getCompound(i)));
         }
         effect.setCurativeItems(items);
      }

      return effect;
   }
}