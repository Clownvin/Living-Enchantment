package net.minecraft.world.gen.layer;

import net.minecraft.util.SharedConstants;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Layer {
   private static final Logger LOGGER = LogManager.getLogger();
   private final LazyArea field_215742_b;

   public Layer(IAreaFactory<LazyArea> lazyAreaFactoryIn) {
      this.field_215742_b = lazyAreaFactoryIn.make();
   }

   public Biome[] generateBiomes(int startX, int startZ, int xSize, int zSize) {
      Biome[] abiome = new Biome[xSize * zSize];

      for(int i = 0; i < zSize; ++i) {
         for(int j = 0; j < xSize; ++j) {
            int k = this.field_215742_b.getValue(startX + j, startZ + i);
            Biome biome = this.func_215739_a(k);
            abiome[j + i * xSize] = biome;
         }
      }

      return abiome;
   }

   private Biome func_215739_a(int p_215739_1_) {
      Biome biome = Registry.BIOME.getByValue(p_215739_1_);
      if (biome == null) {
         if (SharedConstants.developmentMode) {
            throw new IllegalStateException("Unknown biome id: " + p_215739_1_);
         } else {
            LOGGER.warn("Unknown biome id: ", (int)p_215739_1_);
            return Biomes.DEFAULT;
         }
      } else {
         return biome;
      }
   }

   public Biome func_215738_a(int p_215738_1_, int p_215738_2_) {
      return this.func_215739_a(this.field_215742_b.getValue(p_215738_1_, p_215738_2_));
   }
}