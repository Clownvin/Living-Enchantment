package net.minecraft.world.gen.carver;

import java.util.BitSet;
import java.util.Random;
import net.minecraft.world.chunk.IChunk;

public class ConfiguredCarver<WC extends ICarverConfig> {
   public final WorldCarver<WC> field_222732_a;
   public final WC field_222733_b;

   public ConfiguredCarver(WorldCarver<WC> p_i49928_1_, WC p_i49928_2_) {
      this.field_222732_a = p_i49928_1_;
      this.field_222733_b = p_i49928_2_;
   }

   public boolean func_222730_a(Random p_222730_1_, int p_222730_2_, int p_222730_3_) {
      return this.field_222732_a.func_212868_a_(p_222730_1_, p_222730_2_, p_222730_3_, this.field_222733_b);
   }

   public boolean func_222731_a(IChunk p_222731_1_, Random p_222731_2_, int p_222731_3_, int p_222731_4_, int p_222731_5_, int p_222731_6_, int p_222731_7_, BitSet p_222731_8_) {
      return this.field_222732_a.func_212867_a_(p_222731_1_, p_222731_2_, p_222731_3_, p_222731_4_, p_222731_5_, p_222731_6_, p_222731_7_, p_222731_8_, this.field_222733_b);
   }
}