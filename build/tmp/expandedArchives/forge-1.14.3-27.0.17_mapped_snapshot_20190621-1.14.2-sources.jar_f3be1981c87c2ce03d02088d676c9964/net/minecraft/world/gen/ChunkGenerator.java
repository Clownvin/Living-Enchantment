package net.minecraft.world.gen;

import java.util.BitSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityClassification;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;

public abstract class ChunkGenerator<C extends GenerationSettings> {
   protected final IWorld world;
   protected final long seed;
   protected final BiomeProvider biomeProvider;
   protected final C settings;

   public ChunkGenerator(IWorld p_i49954_1_, BiomeProvider p_i49954_2_, C p_i49954_3_) {
      this.world = p_i49954_1_;
      this.seed = p_i49954_1_.getSeed();
      this.biomeProvider = p_i49954_2_;
      this.settings = p_i49954_3_;
   }

   public void generateBiomes(IChunk chunkIn) {
      ChunkPos chunkpos = chunkIn.getPos();
      int i = chunkpos.x;
      int j = chunkpos.z;
      Biome[] abiome = this.biomeProvider.getBiomeBlock(i * 16, j * 16, 16, 16);
      chunkIn.setBiomes(abiome);
   }

   protected Biome getBiome(IChunk p_222534_1_) {
      return p_222534_1_.getBiome(BlockPos.ZERO);
   }

   protected Biome getBiome(WorldGenRegion p_222527_1_, BlockPos p_222527_2_) {
      return this.biomeProvider.getBiome(p_222527_2_);
   }

   public void carve(IChunk p_222538_1_, GenerationStage.Carving p_222538_2_) {
      SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
      int i = 8;
      ChunkPos chunkpos = p_222538_1_.getPos();
      int j = chunkpos.x;
      int k = chunkpos.z;
      BitSet bitset = p_222538_1_.getCarvingMask(p_222538_2_);

      for(int l = j - 8; l <= j + 8; ++l) {
         for(int i1 = k - 8; i1 <= k + 8; ++i1) {
            List<ConfiguredCarver<?>> list = this.getBiome(p_222538_1_).getCarvers(p_222538_2_);
            ListIterator<ConfiguredCarver<?>> listiterator = list.listIterator();

            while(listiterator.hasNext()) {
               int j1 = listiterator.nextIndex();
               ConfiguredCarver<?> configuredcarver = listiterator.next();
               sharedseedrandom.setLargeFeatureSeed(this.seed + (long)j1, l, i1);
               if (configuredcarver.func_222730_a(sharedseedrandom, l, i1)) {
                  configuredcarver.func_222731_a(p_222538_1_, sharedseedrandom, this.getSeaLevel(), l, i1, j, k, bitset);
               }
            }
         }
      }

   }

   @Nullable
   public BlockPos findNearestStructure(World worldIn, String name, BlockPos pos, int radius, boolean p_211403_5_) {
      Structure<?> structure = Feature.STRUCTURES.get(name.toLowerCase(Locale.ROOT));
      return structure != null ? structure.findNearest(worldIn, this, pos, radius, p_211403_5_) : null;
   }

   public void decorate(WorldGenRegion region) {
      int i = region.getMainChunkX();
      int j = region.getMainChunkZ();
      int k = i * 16;
      int l = j * 16;
      BlockPos blockpos = new BlockPos(k, 0, l);
      Biome biome = this.getBiome(region, blockpos.add(8, 8, 8));
      SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
      long i1 = sharedseedrandom.setDecorationSeed(region.getSeed(), k, l);

      for(GenerationStage.Decoration generationstage$decoration : GenerationStage.Decoration.values()) {
         biome.decorate(generationstage$decoration, this, region, i1, sharedseedrandom, blockpos);
      }

   }

   public abstract void generateSurface(IChunk p_222535_1_);

   public void spawnMobs(WorldGenRegion region) {
   }

   public C getSettings() {
      return this.settings;
   }

   public abstract int getGroundHeight();

   public void spawnMobs(ServerWorld worldIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs) {
   }

   public boolean hasStructure(Biome biomeIn, Structure<? extends IFeatureConfig> structureIn) {
      return biomeIn.hasStructure(structureIn);
   }

   @Nullable
   public <C extends IFeatureConfig> C getStructureConfig(Biome biomeIn, Structure<C> structureIn) {
      return biomeIn.getStructureConfig(structureIn);
   }

   public BiomeProvider getBiomeProvider() {
      return this.biomeProvider;
   }

   public long getSeed() {
      return this.seed;
   }

   public int getMaxHeight() {
      return 256;
   }

   public List<Biome.SpawnListEntry> getPossibleCreatures(EntityClassification creatureType, BlockPos pos) {
      return this.world.getBiome(pos).getSpawns(creatureType);
   }

   public void initStructureStarts(IChunk p_222533_1_, ChunkGenerator<?> p_222533_2_, TemplateManager p_222533_3_) {
      for(Structure<?> structure : Feature.STRUCTURES.values()) {
         if (p_222533_2_.getBiomeProvider().hasStructure(structure)) {
            SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
            ChunkPos chunkpos = p_222533_1_.getPos();
            StructureStart structurestart = StructureStart.DUMMY;
            if (structure.hasStartAt(p_222533_2_, sharedseedrandom, chunkpos.x, chunkpos.z)) {
               Biome biome = this.getBiomeProvider().getBiome(new BlockPos(chunkpos.getXStart() + 9, 0, chunkpos.getZStart() + 9));
               StructureStart structurestart1 = structure.getStartFactory().create(structure, chunkpos.x, chunkpos.z, biome, MutableBoundingBox.getNewBoundingBox(), 0, p_222533_2_.getSeed());
               structurestart1.init(this, p_222533_3_, chunkpos.x, chunkpos.z, biome);
               structurestart = structurestart1.isValid() ? structurestart1 : StructureStart.DUMMY;
            }

            p_222533_1_.putStructureStart(structure.getStructureName(), structurestart);
         }
      }

   }

   public void func_222528_a(IWorld p_222528_1_, IChunk p_222528_2_) {
      int i = 8;
      int j = p_222528_2_.getPos().x;
      int k = p_222528_2_.getPos().z;
      int l = j << 4;
      int i1 = k << 4;

      for(int j1 = j - 8; j1 <= j + 8; ++j1) {
         for(int k1 = k - 8; k1 <= k + 8; ++k1) {
            long l1 = ChunkPos.asLong(j1, k1);

            for(Entry<String, StructureStart> entry : p_222528_1_.getChunk(j1, k1).getStructureStarts().entrySet()) {
               StructureStart structurestart = entry.getValue();
               if (structurestart != StructureStart.DUMMY && structurestart.getBoundingBox().intersectsWith(l, i1, l + 15, i1 + 15)) {
                  p_222528_2_.addStructureReference(entry.getKey(), l1);
                  DebugPacketSender.func_218804_a(p_222528_1_, structurestart);
               }
            }
         }
      }

   }

   public abstract void func_222537_b(IWorld p_222537_1_, IChunk p_222537_2_);

   public int getSeaLevel() {
      return 63;
   }

   public abstract int func_222529_a(int p_222529_1_, int p_222529_2_, Heightmap.Type p_222529_3_);

   public int func_222532_b(int p_222532_1_, int p_222532_2_, Heightmap.Type p_222532_3_) {
      return this.func_222529_a(p_222532_1_, p_222532_2_, p_222532_3_);
   }

   public int func_222531_c(int p_222531_1_, int p_222531_2_, Heightmap.Type p_222531_3_) {
      return this.func_222529_a(p_222531_1_, p_222531_2_, p_222531_3_) - 1;
   }
}