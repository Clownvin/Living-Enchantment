package net.minecraft.world.lighting;

import it.unimi.dsi.fastutil.longs.Long2ByteFunction;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import net.minecraft.util.math.MathHelper;

public abstract class LevelBasedGraph {
   private final int field_215486_a;
   private final LongLinkedOpenHashSet[] field_215487_b;
   private final Long2ByteFunction field_215488_c;
   private int field_215489_d;
   private volatile boolean field_215490_e;

   protected LevelBasedGraph(int p_i51298_1_, final int p_i51298_2_, final int p_i51298_3_) {
      if (p_i51298_1_ >= 254) {
         throw new IllegalArgumentException("Level count must be < 254.");
      } else {
         this.field_215486_a = p_i51298_1_;
         this.field_215487_b = new LongLinkedOpenHashSet[p_i51298_1_];

         for(int i = 0; i < p_i51298_1_; ++i) {
            this.field_215487_b[i] = new LongLinkedOpenHashSet(p_i51298_2_, 0.5F) {
               protected void rehash(int p_rehash_1_) {
                  if (p_rehash_1_ > p_i51298_2_) {
                     super.rehash(p_rehash_1_);
                  }

               }
            };
         }

         this.field_215488_c = new Long2ByteOpenHashMap(p_i51298_3_, 0.5F) {
            protected void rehash(int p_rehash_1_) {
               if (p_rehash_1_ > p_i51298_3_) {
                  super.rehash(p_rehash_1_);
               }

            }
         };
         this.field_215488_c.defaultReturnValue((byte)-1);
         this.field_215489_d = p_i51298_1_;
      }
   }

   private int func_215482_a(int p_215482_1_, int p_215482_2_) {
      int i = p_215482_1_;
      if (p_215482_1_ > p_215482_2_) {
         i = p_215482_2_;
      }

      if (i > this.field_215486_a - 1) {
         i = this.field_215486_a - 1;
      }

      return i;
   }

   private void func_215472_a(int p_215472_1_) {
      int i = this.field_215489_d;
      this.field_215489_d = p_215472_1_;

      for(int j = i + 1; j < p_215472_1_; ++j) {
         if (!this.field_215487_b[j].isEmpty()) {
            this.field_215489_d = j;
            break;
         }
      }

   }

   protected void func_215479_e(long p_215479_1_) {
      int i = this.field_215488_c.get(p_215479_1_) & 255;
      if (i != 255) {
         int j = this.func_215471_c(p_215479_1_);
         int k = this.func_215482_a(j, i);
         this.func_215484_a(p_215479_1_, k, this.field_215486_a, true);
         this.field_215490_e = this.field_215489_d < this.field_215486_a;
      }
   }

   private void func_215484_a(long p_215484_1_, int p_215484_3_, int p_215484_4_, boolean p_215484_5_) {
      if (p_215484_5_) {
         this.field_215488_c.remove(p_215484_1_);
      }

      this.field_215487_b[p_215484_3_].remove(p_215484_1_);
      if (this.field_215487_b[p_215484_3_].isEmpty() && this.field_215489_d == p_215484_3_) {
         this.func_215472_a(p_215484_4_);
      }

   }

   private void func_215470_a(long p_215470_1_, int p_215470_3_, int p_215470_4_) {
      this.field_215488_c.put(p_215470_1_, (byte)p_215470_3_);
      this.field_215487_b[p_215470_4_].add(p_215470_1_);
      if (this.field_215489_d > p_215470_4_) {
         this.field_215489_d = p_215470_4_;
      }

   }

   protected void func_215473_f(long worldPos) {
      this.func_215469_a(worldPos, worldPos, this.field_215486_a - 1, false);
   }

   protected void func_215469_a(long p_215469_1_, long p_215469_3_, int p_215469_5_, boolean p_215469_6_) {
      this.func_215474_a(p_215469_1_, p_215469_3_, p_215469_5_, this.func_215471_c(p_215469_3_), this.field_215488_c.get(p_215469_3_) & 255, p_215469_6_);
      this.field_215490_e = this.field_215489_d < this.field_215486_a;
   }

   private void func_215474_a(long p_215474_1_, long p_215474_3_, int p_215474_5_, int p_215474_6_, int p_215474_7_, boolean p_215474_8_) {
      if (!this.func_215485_a(p_215474_3_)) {
         p_215474_5_ = MathHelper.clamp(p_215474_5_, 0, this.field_215486_a - 1);
         p_215474_6_ = MathHelper.clamp(p_215474_6_, 0, this.field_215486_a - 1);
         boolean flag;
         if (p_215474_7_ == 255) {
            flag = true;
            p_215474_7_ = p_215474_6_;
         } else {
            flag = false;
         }

         int i;
         if (p_215474_8_) {
            i = Math.min(p_215474_7_, p_215474_5_);
         } else {
            i = MathHelper.clamp(this.func_215477_a(p_215474_3_, p_215474_1_, p_215474_5_), 0, this.field_215486_a - 1);
         }

         int j = this.func_215482_a(p_215474_6_, p_215474_7_);
         if (p_215474_6_ != i) {
            int k = this.func_215482_a(p_215474_6_, i);
            if (j != k && !flag) {
               this.func_215484_a(p_215474_3_, j, k, false);
            }

            this.func_215470_a(p_215474_3_, i, k);
         } else if (!flag) {
            this.func_215484_a(p_215474_3_, j, this.field_215486_a, true);
         }

      }
   }

   protected final void func_215475_b(long p_215475_1_, long p_215475_3_, int p_215475_5_, boolean p_215475_6_) {
      int i = this.field_215488_c.get(p_215475_3_) & 255;
      int j = MathHelper.clamp(this.func_215480_b(p_215475_1_, p_215475_3_, p_215475_5_), 0, this.field_215486_a - 1);
      if (p_215475_6_) {
         this.func_215474_a(p_215475_1_, p_215475_3_, j, this.func_215471_c(p_215475_3_), i, true);
      } else {
         int k;
         boolean flag;
         if (i == 255) {
            flag = true;
            k = MathHelper.clamp(this.func_215471_c(p_215475_3_), 0, this.field_215486_a - 1);
         } else {
            k = i;
            flag = false;
         }

         if (j == k) {
            this.func_215474_a(p_215475_1_, p_215475_3_, this.field_215486_a - 1, flag ? k : this.func_215471_c(p_215475_3_), i, false);
         }
      }

   }

   protected final boolean func_215481_b() {
      return this.field_215490_e;
   }

   protected final int func_215483_b(int p_215483_1_) {
      if (this.field_215489_d >= this.field_215486_a) {
         return p_215483_1_;
      } else {
         while(this.field_215489_d < this.field_215486_a && p_215483_1_ > 0) {
            --p_215483_1_;
            LongLinkedOpenHashSet longlinkedopenhashset = this.field_215487_b[this.field_215489_d];
            long i = longlinkedopenhashset.removeFirstLong();
            int j = MathHelper.clamp(this.func_215471_c(i), 0, this.field_215486_a - 1);
            if (longlinkedopenhashset.isEmpty()) {
               this.func_215472_a(this.field_215486_a);
            }

            int k = this.field_215488_c.remove(i) & 255;
            if (k < j) {
               this.func_215476_a(i, k);
               this.func_215478_a(i, k, true);
            } else if (k > j) {
               this.func_215470_a(i, k, this.func_215482_a(this.field_215486_a - 1, k));
               this.func_215476_a(i, this.field_215486_a - 1);
               this.func_215478_a(i, j, false);
            }
         }

         this.field_215490_e = this.field_215489_d < this.field_215486_a;
         return p_215483_1_;
      }
   }

   protected abstract boolean func_215485_a(long p_215485_1_);

   protected abstract int func_215477_a(long p_215477_1_, long p_215477_3_, int p_215477_5_);

   protected abstract void func_215478_a(long p_215478_1_, int p_215478_3_, boolean p_215478_4_);

   protected abstract int func_215471_c(long sectionPosIn);

   protected abstract void func_215476_a(long sectionPosIn, int p_215476_3_);

   protected abstract int func_215480_b(long p_215480_1_, long p_215480_3_, int p_215480_5_);
}