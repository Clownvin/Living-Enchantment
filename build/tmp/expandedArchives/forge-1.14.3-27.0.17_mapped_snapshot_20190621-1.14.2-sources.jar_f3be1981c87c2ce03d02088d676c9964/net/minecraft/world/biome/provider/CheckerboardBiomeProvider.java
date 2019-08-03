package net.minecraft.world.biome.provider;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.structure.Structure;

public class CheckerboardBiomeProvider extends BiomeProvider {
   private final Biome[] field_205320_b;
   private final int field_205321_c;

   public CheckerboardBiomeProvider(CheckerboardBiomeProviderSettings p_i48973_1_) {
      this.field_205320_b = p_i48973_1_.getBiomes();
      this.field_205321_c = p_i48973_1_.getSize() + 4;
   }

   /**
    * Gets the biome from the provided coordinates
    *  
    * @param x The x coordinate to get the biome from.
    * @param y The y coordinate to get the biome from.
    */
   public Biome getBiome(int x, int y) {
      return this.field_205320_b[Math.abs(((x >> this.field_205321_c) + (y >> this.field_205321_c)) % this.field_205320_b.length)];
   }

   public Biome[] getBiomes(int x, int z, int width, int length, boolean cacheFlag) {
      Biome[] abiome = new Biome[width * length];

      for(int i = 0; i < length; ++i) {
         for(int j = 0; j < width; ++j) {
            int k = Math.abs(((x + i >> this.field_205321_c) + (z + j >> this.field_205321_c)) % this.field_205320_b.length);
            Biome biome = this.field_205320_b[k];
            abiome[i * width + j] = biome;
         }
      }

      return abiome;
   }

   @Nullable
   public BlockPos findBiomePosition(int x, int z, int range, List<Biome> biomes, Random random) {
      return null;
   }

   public boolean hasStructure(Structure<?> structureIn) {
      return this.hasStructureCache.computeIfAbsent(structureIn, (p_205319_1_) -> {
         for(Biome biome : this.field_205320_b) {
            if (biome.hasStructure(p_205319_1_)) {
               return true;
            }
         }

         return false;
      });
   }

   public Set<BlockState> getSurfaceBlocks() {
      if (this.topBlocksCache.isEmpty()) {
         for(Biome biome : this.field_205320_b) {
            this.topBlocksCache.add(biome.getSurfaceBuilderConfig().getTop());
         }
      }

      return this.topBlocksCache;
   }

   public Set<Biome> getBiomesInSquare(int centerX, int centerZ, int sideLength) {
      return Sets.newHashSet(this.field_205320_b);
   }
}