package net.minecraft.world.chunk.listener;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.annotation.Nullable;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TrackingChunkStatusListener implements IChunkStatusListener {
   private final LoggingChunkStatusListener field_219526_a;
   private final Long2ObjectOpenHashMap<ChunkStatus> field_219527_b;
   private ChunkPos field_219528_c = new ChunkPos(0, 0);
   private final int field_219529_d;
   private final int field_219530_e;
   private final int field_219531_f;
   private boolean field_219532_g;

   public TrackingChunkStatusListener(int p_i50695_1_) {
      this.field_219526_a = new LoggingChunkStatusListener(p_i50695_1_);
      this.field_219529_d = p_i50695_1_ * 2 + 1;
      this.field_219530_e = p_i50695_1_ + ChunkStatus.func_222600_b();
      this.field_219531_f = this.field_219530_e * 2 + 1;
      this.field_219527_b = new Long2ObjectOpenHashMap<>();
   }

   public void func_219509_a(ChunkPos p_219509_1_) {
      if (this.field_219532_g) {
         this.field_219526_a.func_219509_a(p_219509_1_);
         this.field_219528_c = p_219509_1_;
      }
   }

   public void func_219508_a(ChunkPos p_219508_1_, @Nullable ChunkStatus p_219508_2_) {
      if (this.field_219532_g) {
         this.field_219526_a.func_219508_a(p_219508_1_, p_219508_2_);
         if (p_219508_2_ == null) {
            this.field_219527_b.remove(p_219508_1_.asLong());
         } else {
            this.field_219527_b.put(p_219508_1_.asLong(), p_219508_2_);
         }

      }
   }

   public void func_219521_a() {
      this.field_219532_g = true;
      this.field_219527_b.clear();
   }

   public void func_219510_b() {
      this.field_219532_g = false;
      this.field_219526_a.func_219510_b();
   }

   public int func_219522_c() {
      return this.field_219529_d;
   }

   public int func_219523_d() {
      return this.field_219531_f;
   }

   public int func_219524_e() {
      return this.field_219526_a.func_219511_c();
   }

   @Nullable
   public ChunkStatus func_219525_a(int p_219525_1_, int p_219525_2_) {
      return this.field_219527_b.get(ChunkPos.asLong(p_219525_1_ + this.field_219528_c.x - this.field_219530_e, p_219525_2_ + this.field_219528_c.z - this.field_219530_e));
   }
}