package net.minecraft.world.chunk.storage;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.LongArrayNBT;
import net.minecraft.nbt.ShortNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.ITickList;
import net.minecraft.world.LightType;
import net.minecraft.world.SerializableTickList;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkPrimerTickList;
import net.minecraft.world.chunk.ChunkPrimerWrapper;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.structure.Structures;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.lighting.WorldLightManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkSerializer {
   private static final Logger LOGGER = LogManager.getLogger();

   public static ChunkPrimer read(ServerWorld worldIn, TemplateManager p_222656_1_, PointOfInterestManager p_222656_2_, ChunkPos pos, CompoundNBT compound) {
      ChunkGenerator<?> chunkgenerator = worldIn.getChunkProvider().getChunkGenerator();
      BiomeProvider biomeprovider = chunkgenerator.getBiomeProvider();
      CompoundNBT compoundnbt = compound.getCompound("Level");
      ChunkPos chunkpos = new ChunkPos(compoundnbt.getInt("xPos"), compoundnbt.getInt("zPos"));
      if (!Objects.equals(pos, chunkpos)) {
         LOGGER.error("Chunk file at {} is in the wrong location; relocating. (Expected {}, got {})", pos, pos, chunkpos);
      }

      Biome[] abiome = new Biome[256];
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      if (compoundnbt.contains("Biomes", 11)) {
         int[] aint = compoundnbt.getIntArray("Biomes");

         for(int i = 0; i < aint.length; ++i) {
            abiome[i] = Registry.BIOME.getByValue(aint[i]);
            if (abiome[i] == null) {
               abiome[i] = biomeprovider.getBiome(blockpos$mutableblockpos.setPos((i & 15) + pos.getXStart(), 0, (i >> 4 & 15) + pos.getZStart()));
            }
         }
      } else {
         for(int l1 = 0; l1 < abiome.length; ++l1) {
            abiome[l1] = biomeprovider.getBiome(blockpos$mutableblockpos.setPos((l1 & 15) + pos.getXStart(), 0, (l1 >> 4 & 15) + pos.getZStart()));
         }
      }

      UpgradeData upgradedata = compoundnbt.contains("UpgradeData", 10) ? new UpgradeData(compoundnbt.getCompound("UpgradeData")) : UpgradeData.EMPTY;
      ChunkPrimerTickList<Block> chunkprimerticklist1 = new ChunkPrimerTickList<>((p_222652_0_) -> {
         return p_222652_0_ == null || p_222652_0_.getDefaultState().isAir();
      }, pos, compoundnbt.getList("ToBeTicked", 9));
      ChunkPrimerTickList<Fluid> chunkprimerticklist = new ChunkPrimerTickList<>((p_222646_0_) -> {
         return p_222646_0_ == null || p_222646_0_ == Fluids.EMPTY;
      }, pos, compoundnbt.getList("LiquidsToBeTicked", 9));
      boolean flag = compoundnbt.getBoolean("isLightOn");
      ListNBT listnbt = compoundnbt.getList("Sections", 10);
      int j = 16;
      ChunkSection[] achunksection = new ChunkSection[16];
      boolean flag1 = worldIn.getDimension().hasSkyLight();
      AbstractChunkProvider abstractchunkprovider = worldIn.getChunkProvider();
      WorldLightManager worldlightmanager = abstractchunkprovider.getLightManager();
      if (flag) {
         worldlightmanager.retainData(pos, true);
      }

      for(int k = 0; k < listnbt.size(); ++k) {
         CompoundNBT compoundnbt1 = listnbt.getCompound(k);
         int l = compoundnbt1.getByte("Y");
         if (compoundnbt1.contains("Palette", 9) && compoundnbt1.contains("BlockStates", 12)) {
            ChunkSection chunksection = new ChunkSection(l << 4);
            chunksection.getData().readChunkPalette(compoundnbt1.getList("Palette", 10), compoundnbt1.getLongArray("BlockStates"));
            chunksection.recalculateRefCounts();
            if (!chunksection.isEmpty()) {
               achunksection[l] = chunksection;
            }

            p_222656_2_.func_219139_a(pos, chunksection);
         }

         if (flag) {
            if (compoundnbt1.contains("BlockLight", 7)) {
               worldlightmanager.setData(LightType.BLOCK, SectionPos.from(pos, l), new NibbleArray(compoundnbt1.getByteArray("BlockLight")));
            }

            if (flag1 && compoundnbt1.contains("SkyLight", 7)) {
               worldlightmanager.setData(LightType.SKY, SectionPos.from(pos, l), new NibbleArray(compoundnbt1.getByteArray("SkyLight")));
            }
         }
      }

      long i2 = compoundnbt.getLong("InhabitedTime");
      ChunkStatus.Type chunkstatus$type = getChunkStatus(compound);
      IChunk ichunk;
      if (chunkstatus$type == ChunkStatus.Type.LEVELCHUNK) {
         ITickList<Block> iticklist;
         if (compoundnbt.contains("TileTicks", 9)) {
            iticklist = SerializableTickList.func_222984_a(compoundnbt.getList("TileTicks", 10), Registry.BLOCK::getKey, Registry.BLOCK::getOrDefault);
         } else {
            iticklist = chunkprimerticklist1;
         }

         ITickList<Fluid> iticklist1;
         if (compoundnbt.contains("LiquidTicks", 9)) {
            iticklist1 = SerializableTickList.func_222984_a(compoundnbt.getList("LiquidTicks", 10), Registry.FLUID::getKey, Registry.FLUID::getOrDefault);
         } else {
            iticklist1 = chunkprimerticklist;
         }

         ichunk = new Chunk(worldIn.getWorld(), pos, abiome, upgradedata, iticklist, iticklist1, i2, achunksection, (p_222648_1_) -> {
            readEntities(compoundnbt, p_222648_1_);
         });
         if (compoundnbt.contains("ForgeCaps")) ((Chunk)ichunk).readCapsFromNBT(compoundnbt.getCompound("ForgeCaps"));
      } else {
         ChunkPrimer chunkprimer = new ChunkPrimer(pos, upgradedata, achunksection, chunkprimerticklist1, chunkprimerticklist);
         ichunk = chunkprimer;
         chunkprimer.setBiomes(abiome);
         chunkprimer.setInhabitedTime(i2);
         chunkprimer.setStatus(ChunkStatus.byName(compoundnbt.getString("Status")));
         if (chunkprimer.getStatus().isAtLeast(ChunkStatus.FEATURES)) {
            chunkprimer.setLightManager(worldlightmanager);
         }

         if (!flag && chunkprimer.getStatus().isAtLeast(ChunkStatus.LIGHT)) {
            for(BlockPos blockpos : BlockPos.getAllInBoxMutable(pos.getXStart(), 0, pos.getZStart(), pos.getXEnd(), 255, pos.getZEnd())) {
               if (ichunk.getBlockState(blockpos).getLightValue() != 0) {
                  chunkprimer.addLightPosition(blockpos);
               }
            }
         }
      }

      ichunk.setLight(flag);
      CompoundNBT compoundnbt3 = compoundnbt.getCompound("Heightmaps");
      EnumSet<Heightmap.Type> enumset = EnumSet.noneOf(Heightmap.Type.class);

      for(Heightmap.Type heightmap$type : ichunk.getStatus().getHeightMaps()) {
         String s = heightmap$type.getId();
         if (compoundnbt3.contains(s, 12)) {
            ichunk.setHeightmap(heightmap$type, compoundnbt3.getLongArray(s));
         } else {
            enumset.add(heightmap$type);
         }
      }

      Heightmap.func_222690_a(ichunk, enumset);
      CompoundNBT compoundnbt4 = compoundnbt.getCompound("Structures");
      ichunk.setStructureStarts(readStructureStarts(chunkgenerator, p_222656_1_, biomeprovider, compoundnbt4));
      ichunk.setStructureReferences(readStructureReferences(compoundnbt4));
      if (compoundnbt.getBoolean("shouldSave")) {
         ichunk.setModified(true);
      }

      ListNBT listnbt3 = compoundnbt.getList("PostProcessing", 9);

      for(int j2 = 0; j2 < listnbt3.size(); ++j2) {
         ListNBT listnbt1 = listnbt3.getList(j2);

         for(int i1 = 0; i1 < listnbt1.size(); ++i1) {
            ichunk.func_201636_b(listnbt1.getShort(i1), j2);
         }
      }

      if (chunkstatus$type == ChunkStatus.Type.LEVELCHUNK) {
         return new ChunkPrimerWrapper((Chunk)ichunk);
      } else {
         ChunkPrimer chunkprimer1 = (ChunkPrimer)ichunk;
         ListNBT listnbt4 = compoundnbt.getList("Entities", 10);

         for(int k2 = 0; k2 < listnbt4.size(); ++k2) {
            chunkprimer1.addEntity(listnbt4.getCompound(k2));
         }

         ListNBT listnbt5 = compoundnbt.getList("TileEntities", 10);

         for(int j1 = 0; j1 < listnbt5.size(); ++j1) {
            CompoundNBT compoundnbt2 = listnbt5.getCompound(j1);
            ichunk.addTileEntity(compoundnbt2);
         }

         ListNBT listnbt6 = compoundnbt.getList("Lights", 9);

         for(int l2 = 0; l2 < listnbt6.size(); ++l2) {
            ListNBT listnbt2 = listnbt6.getList(l2);

            for(int k1 = 0; k1 < listnbt2.size(); ++k1) {
               chunkprimer1.addLightValue(listnbt2.getShort(k1), l2);
            }
         }

         CompoundNBT compoundnbt5 = compoundnbt.getCompound("CarvingMasks");

         for(String s1 : compoundnbt5.keySet()) {
            GenerationStage.Carving generationstage$carving = GenerationStage.Carving.valueOf(s1);
            chunkprimer1.setCarvingMask(generationstage$carving, BitSet.valueOf(compoundnbt5.getByteArray(s1)));
         }

         return chunkprimer1;
      }
   }

   public static CompoundNBT write(ServerWorld worldIn, IChunk chunkIn) {
      ChunkPos chunkpos = chunkIn.getPos();
      CompoundNBT compoundnbt = new CompoundNBT();
      CompoundNBT compoundnbt1 = new CompoundNBT();
      compoundnbt.putInt("DataVersion", SharedConstants.getVersion().getWorldVersion());
      compoundnbt.put("Level", compoundnbt1);
      compoundnbt1.putInt("xPos", chunkpos.x);
      compoundnbt1.putInt("zPos", chunkpos.z);
      compoundnbt1.putLong("LastUpdate", worldIn.getGameTime());
      compoundnbt1.putLong("InhabitedTime", chunkIn.getInhabitedTime());
      compoundnbt1.putString("Status", chunkIn.getStatus().getName());
      UpgradeData upgradedata = chunkIn.getUpgradeData();
      if (!upgradedata.isEmpty()) {
         compoundnbt1.put("UpgradeData", upgradedata.write());
      }

      ChunkSection[] achunksection = chunkIn.getSections();
      ListNBT listnbt = new ListNBT();
      WorldLightManager worldlightmanager = worldIn.getChunkProvider().getLightManager();
      boolean flag = chunkIn.hasLight();

      for(int i = -1; i < 17; ++i) {
         final int i_f = i;
         ChunkSection chunksection = Arrays.stream(achunksection).filter((p_222657_1_) -> {
            return p_222657_1_ != null && p_222657_1_.getYLocation() >> 4 == i_f;
         }).findFirst().orElse(Chunk.EMPTY_SECTION);
         NibbleArray nibblearray = worldlightmanager.getLightEngine(LightType.BLOCK).getData(SectionPos.from(chunkpos, i));
         NibbleArray nibblearray1 = worldlightmanager.getLightEngine(LightType.SKY).getData(SectionPos.from(chunkpos, i));
         if (chunksection != Chunk.EMPTY_SECTION || nibblearray != null || nibblearray1 != null) {
            CompoundNBT compoundnbt2 = new CompoundNBT();
            compoundnbt2.putByte("Y", (byte)(i & 255));
            if (chunksection != Chunk.EMPTY_SECTION) {
               chunksection.getData().writeChunkPalette(compoundnbt2, "Palette", "BlockStates");
            }

            if (nibblearray != null && !nibblearray.isEmpty()) {
               compoundnbt2.putByteArray("BlockLight", nibblearray.getData());
            }

            if (nibblearray1 != null && !nibblearray1.isEmpty()) {
               compoundnbt2.putByteArray("SkyLight", nibblearray1.getData());
            }

            listnbt.add(compoundnbt2);
         }
      }

      compoundnbt1.put("Sections", listnbt);
      if (flag) {
         compoundnbt1.putBoolean("isLightOn", true);
      }

      Biome[] abiome = chunkIn.getBiomes();
      int[] aint = abiome != null ? new int[abiome.length] : new int[0];
      if (abiome != null) {
         for(int j = 0; j < abiome.length; ++j) {
            aint[j] = Registry.BIOME.getId(abiome[j]);
         }
      }

      compoundnbt1.putIntArray("Biomes", aint);
      ListNBT listnbt1 = new ListNBT();

      for(BlockPos blockpos : chunkIn.getTileEntitiesPos()) {
         CompoundNBT compoundnbt4 = chunkIn.func_223134_j(blockpos);
         if (compoundnbt4 != null) {
            listnbt1.add(compoundnbt4);
         }
      }

      compoundnbt1.put("TileEntities", listnbt1);
      ListNBT listnbt2 = new ListNBT();
      if (chunkIn.getStatus().getType() == ChunkStatus.Type.LEVELCHUNK) {
         Chunk chunk = (Chunk)chunkIn;
         chunk.setHasEntities(false);

         for(int k = 0; k < chunk.getEntityLists().length; ++k) {
            for(Entity entity : chunk.getEntityLists()[k]) {
               CompoundNBT compoundnbt3 = new CompoundNBT();
               try {
               if (entity.writeUnlessPassenger(compoundnbt3)) {
                  chunk.setHasEntities(true);
                  listnbt2.add(compoundnbt3);
               }
               } catch (Exception e) {
                  LogManager.getLogger().error("An Entity type {} has thrown an exception trying to write state. It will not persist. Report this to the mod author", entity.getType(), e);
               }
            }
         }
         try {
             final CompoundNBT capTag = chunk.writeCapsToNBT();
             if (capTag != null) compoundnbt1.put("ForgeCaps", capTag);
         } catch (Exception exception) {
             LogManager.getLogger().error("A capability provider has thrown an exception trying to write state. It will not persist. Report this to the mod author", exception);
         }
      } else {
         ChunkPrimer chunkprimer = (ChunkPrimer)chunkIn;
         listnbt2.addAll(chunkprimer.getEntities());
         compoundnbt1.put("Lights", toNbt(chunkprimer.getPackedLightPositions()));
         CompoundNBT compoundnbt5 = new CompoundNBT();

         for(GenerationStage.Carving generationstage$carving : GenerationStage.Carving.values()) {
            compoundnbt5.putByteArray(generationstage$carving.toString(), chunkIn.getCarvingMask(generationstage$carving).toByteArray());
         }

         compoundnbt1.put("CarvingMasks", compoundnbt5);
      }

      compoundnbt1.put("Entities", listnbt2);
      ITickList<Block> iticklist = chunkIn.getBlocksToBeTicked();
      if (iticklist instanceof ChunkPrimerTickList) {
         compoundnbt1.put("ToBeTicked", ((ChunkPrimerTickList)iticklist).write());
      } else if (iticklist instanceof SerializableTickList) {
         compoundnbt1.put("TileTicks", ((SerializableTickList)iticklist).func_219498_a(worldIn.getGameTime()));
      } else {
         compoundnbt1.put("TileTicks", worldIn.getPendingBlockTicks().func_219503_a(chunkpos));
      }

      ITickList<Fluid> iticklist1 = chunkIn.getFluidsToBeTicked();
      if (iticklist1 instanceof ChunkPrimerTickList) {
         compoundnbt1.put("LiquidsToBeTicked", ((ChunkPrimerTickList)iticklist1).write());
      } else if (iticklist1 instanceof SerializableTickList) {
         compoundnbt1.put("LiquidTicks", ((SerializableTickList)iticklist1).func_219498_a(worldIn.getGameTime()));
      } else {
         compoundnbt1.put("LiquidTicks", worldIn.getPendingFluidTicks().func_219503_a(chunkpos));
      }

      compoundnbt1.put("PostProcessing", toNbt(chunkIn.getPackedPositions()));
      CompoundNBT compoundnbt6 = new CompoundNBT();

      for(Entry<Heightmap.Type, Heightmap> entry : chunkIn.func_217311_f()) {
         if (chunkIn.getStatus().getHeightMaps().contains(entry.getKey())) {
            compoundnbt6.put(entry.getKey().getId(), new LongArrayNBT(entry.getValue().getDataArray()));
         }
      }

      compoundnbt1.put("Heightmaps", compoundnbt6);
      compoundnbt1.put("Structures", writeStructures(chunkpos, chunkIn.getStructureStarts(), chunkIn.getStructureReferences()));

      return compoundnbt;
   }

   public static ChunkStatus.Type getChunkStatus(@Nullable CompoundNBT chunkNBT) {
      if (chunkNBT != null) {
         ChunkStatus chunkstatus = ChunkStatus.byName(chunkNBT.getCompound("Level").getString("Status"));
         if (chunkstatus != null) {
            return chunkstatus.getType();
         }
      }

      return ChunkStatus.Type.PROTOCHUNK;
   }

   private static void readEntities(CompoundNBT compound, Chunk chunkIn) {
      ListNBT listnbt = compound.getList("Entities", 10);
      World world = chunkIn.getWorld();

      for(int i = 0; i < listnbt.size(); ++i) {
         CompoundNBT compoundnbt = listnbt.getCompound(i);
         EntityType.func_220335_a(compoundnbt, world, (p_222655_1_) -> {
            chunkIn.addEntity(p_222655_1_);
            return p_222655_1_;
         });
         chunkIn.setHasEntities(true);
      }

      ListNBT listnbt1 = compound.getList("TileEntities", 10);

      for(int j = 0; j < listnbt1.size(); ++j) {
         CompoundNBT compoundnbt1 = listnbt1.getCompound(j);
         boolean flag = compoundnbt1.getBoolean("keepPacked");
         if (flag) {
            chunkIn.addTileEntity(compoundnbt1);
         } else {
            TileEntity tileentity = TileEntity.create(compoundnbt1);
            if (tileentity != null) {
               chunkIn.addTileEntity(tileentity);
            }
         }
      }

   }

   private static CompoundNBT writeStructures(ChunkPos pos, Map<String, StructureStart> p_222649_1_, Map<String, LongSet> p_222649_2_) {
      CompoundNBT compoundnbt = new CompoundNBT();
      CompoundNBT compoundnbt1 = new CompoundNBT();

      for(Entry<String, StructureStart> entry : p_222649_1_.entrySet()) {
         compoundnbt1.put(entry.getKey(), entry.getValue().write(pos.x, pos.z));
      }

      compoundnbt.put("Starts", compoundnbt1);
      CompoundNBT compoundnbt2 = new CompoundNBT();

      for(Entry<String, LongSet> entry1 : p_222649_2_.entrySet()) {
         compoundnbt2.put(entry1.getKey(), new LongArrayNBT(entry1.getValue()));
      }

      compoundnbt.put("References", compoundnbt2);
      return compoundnbt;
   }

   private static Map<String, StructureStart> readStructureStarts(ChunkGenerator<?> p_222653_0_, TemplateManager p_222653_1_, BiomeProvider p_222653_2_, CompoundNBT compound) {
      Map<String, StructureStart> map = Maps.newHashMap();
      CompoundNBT compoundnbt = compound.getCompound("Starts");

      for(String s : compoundnbt.keySet()) {
         map.put(s, Structures.func_215142_a(p_222653_0_, p_222653_1_, p_222653_2_, compoundnbt.getCompound(s)));
      }

      return map;
   }

   private static Map<String, LongSet> readStructureReferences(CompoundNBT compound) {
      Map<String, LongSet> map = Maps.newHashMap();
      CompoundNBT compoundnbt = compound.getCompound("References");

      for(String s : compoundnbt.keySet()) {
         map.put(s, new LongOpenHashSet(compoundnbt.getLongArray(s)));
      }

      return map;
   }

   public static ListNBT toNbt(ShortList[] list) {
      ListNBT listnbt = new ListNBT();

      for(ShortList shortlist : list) {
         ListNBT listnbt1 = new ListNBT();
         if (shortlist != null) {
            for(Short oshort : shortlist) {
               listnbt1.add(new ShortNBT(oshort));
            }
         }

         listnbt.add(listnbt1);
      }

      return listnbt;
   }
}