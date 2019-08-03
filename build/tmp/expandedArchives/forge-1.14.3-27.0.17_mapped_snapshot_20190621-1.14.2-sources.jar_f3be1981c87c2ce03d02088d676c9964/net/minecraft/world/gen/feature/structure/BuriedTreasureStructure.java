package net.minecraft.world.gen.feature.structure;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class BuriedTreasureStructure extends Structure<BuriedTreasureConfig> {
   public BuriedTreasureStructure(Function<Dynamic<?>, ? extends BuriedTreasureConfig> p_i49910_1_) {
      super(p_i49910_1_);
   }

   public boolean hasStartAt(ChunkGenerator<?> chunkGen, Random rand, int chunkPosX, int chunkPosZ) {
      Biome biome = chunkGen.getBiomeProvider().getBiome(new BlockPos((chunkPosX << 4) + 9, 0, (chunkPosZ << 4) + 9));
      if (chunkGen.hasStructure(biome, Feature.BURIED_TREASURE)) {
         ((SharedSeedRandom)rand).setLargeFeatureSeedWithSalt(chunkGen.getSeed(), chunkPosX, chunkPosZ, 10387320);
         BuriedTreasureConfig buriedtreasureconfig = (BuriedTreasureConfig)chunkGen.getStructureConfig(biome, Feature.BURIED_TREASURE);
         return rand.nextFloat() < buriedtreasureconfig.probability;
      } else {
         return false;
      }
   }

   public Structure.IStartFactory getStartFactory() {
      return BuriedTreasureStructure.Start::new;
   }

   public String getStructureName() {
      return "Buried_Treasure";
   }

   public int getSize() {
      return 1;
   }

   public static class Start extends StructureStart {
      public Start(Structure<?> p_i51165_1_, int p_i51165_2_, int p_i51165_3_, Biome p_i51165_4_, MutableBoundingBox p_i51165_5_, int p_i51165_6_, long p_i51165_7_) {
         super(p_i51165_1_, p_i51165_2_, p_i51165_3_, p_i51165_4_, p_i51165_5_, p_i51165_6_, p_i51165_7_);
      }

      public void init(ChunkGenerator<?> generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn) {
         int i = chunkX * 16;
         int j = chunkZ * 16;
         BlockPos blockpos = new BlockPos(i + 9, 90, j + 9);
         this.components.add(new BuriedTreasure.Piece(blockpos));
         this.recalculateStructureSize();
      }

      public BlockPos getPos() {
         return new BlockPos((this.getChunkPosX() << 4) + 9, 0, (this.getChunkPosZ() << 4) + 9);
      }
   }
}