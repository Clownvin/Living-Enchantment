package net.minecraft.world.gen;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import java.util.Random;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.gen.layer.traits.IPixelTransformer;

public class LazyAreaLayerContext implements IExtendedNoiseRandom<LazyArea> {
   private final Long2IntLinkedOpenHashMap cache;
   private final int maxCacheSize;
   protected long field_215717_a;
   protected ImprovedNoiseGenerator field_215718_b;
   private long field_215719_e;
   private long field_215720_f;

   public LazyAreaLayerContext(int maxCacheSizeIn, long seedIn, long seedModifierIn) {
      this.field_215717_a = seedModifierIn;
      this.field_215717_a *= this.field_215717_a * 6364136223846793005L + 1442695040888963407L;
      this.field_215717_a += seedModifierIn;
      this.field_215717_a *= this.field_215717_a * 6364136223846793005L + 1442695040888963407L;
      this.field_215717_a += seedModifierIn;
      this.field_215717_a *= this.field_215717_a * 6364136223846793005L + 1442695040888963407L;
      this.field_215717_a += seedModifierIn;
      this.cache = new Long2IntLinkedOpenHashMap(16, 0.25F);
      this.cache.defaultReturnValue(Integer.MIN_VALUE);
      this.maxCacheSize = maxCacheSizeIn;
      this.func_215716_a(seedIn);
   }

   public LazyArea func_212861_a_(IPixelTransformer p_212861_1_) {
      return new LazyArea(this.cache, this.maxCacheSize, p_212861_1_);
   }

   public LazyArea func_212859_a_(IPixelTransformer p_212859_1_, LazyArea p_212859_2_) {
      return new LazyArea(this.cache, Math.min(1024, p_212859_2_.getmaxCacheSize() * 4), p_212859_1_);
   }

   public LazyArea makeArea(IPixelTransformer p_212860_1_, LazyArea p_212860_2_, LazyArea p_212860_3_) {
      return new LazyArea(this.cache, Math.min(1024, Math.max(p_212860_2_.getmaxCacheSize(), p_212860_3_.getmaxCacheSize()) * 4), p_212860_1_);
   }

   public void func_215716_a(long p_215716_1_) {
      this.field_215719_e = p_215716_1_;
      this.field_215719_e *= this.field_215719_e * 6364136223846793005L + 1442695040888963407L;
      this.field_215719_e += this.field_215717_a;
      this.field_215719_e *= this.field_215719_e * 6364136223846793005L + 1442695040888963407L;
      this.field_215719_e += this.field_215717_a;
      this.field_215719_e *= this.field_215719_e * 6364136223846793005L + 1442695040888963407L;
      this.field_215719_e += this.field_215717_a;
      this.field_215718_b = new ImprovedNoiseGenerator(new Random(p_215716_1_));
   }

   public void setPosition(long x, long z) {
      this.field_215720_f = this.field_215719_e;
      this.field_215720_f *= this.field_215720_f * 6364136223846793005L + 1442695040888963407L;
      this.field_215720_f += x;
      this.field_215720_f *= this.field_215720_f * 6364136223846793005L + 1442695040888963407L;
      this.field_215720_f += z;
      this.field_215720_f *= this.field_215720_f * 6364136223846793005L + 1442695040888963407L;
      this.field_215720_f += x;
      this.field_215720_f *= this.field_215720_f * 6364136223846793005L + 1442695040888963407L;
      this.field_215720_f += z;
   }

   public int random(int bound) {
      int i = (int)((this.field_215720_f >> 24) % (long)bound);
      if (i < 0) {
         i += bound;
      }

      this.field_215720_f *= this.field_215720_f * 6364136223846793005L + 1442695040888963407L;
      this.field_215720_f += this.field_215719_e;
      return i;
   }

   public ImprovedNoiseGenerator getNoiseGenerator() {
      return this.field_215718_b;
   }
}