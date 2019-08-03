package net.minecraft.world.chunk.storage;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.NibbleArray;

public class ChunkLoaderUtil {
   public static ChunkLoaderUtil.AnvilConverterData load(CompoundNBT nbt) {
      int i = nbt.getInt("xPos");
      int j = nbt.getInt("zPos");
      ChunkLoaderUtil.AnvilConverterData chunkloaderutil$anvilconverterdata = new ChunkLoaderUtil.AnvilConverterData(i, j);
      chunkloaderutil$anvilconverterdata.blocks = nbt.getByteArray("Blocks");
      chunkloaderutil$anvilconverterdata.data = new NibbleArrayReader(nbt.getByteArray("Data"), 7);
      chunkloaderutil$anvilconverterdata.skyLight = new NibbleArrayReader(nbt.getByteArray("SkyLight"), 7);
      chunkloaderutil$anvilconverterdata.blockLight = new NibbleArrayReader(nbt.getByteArray("BlockLight"), 7);
      chunkloaderutil$anvilconverterdata.heightmap = nbt.getByteArray("HeightMap");
      chunkloaderutil$anvilconverterdata.terrainPopulated = nbt.getBoolean("TerrainPopulated");
      chunkloaderutil$anvilconverterdata.field_76702_h = nbt.getList("Entities", 10);
      chunkloaderutil$anvilconverterdata.tileEntities = nbt.getList("TileEntities", 10);
      chunkloaderutil$anvilconverterdata.tileTicks = nbt.getList("TileTicks", 10);

      try {
         chunkloaderutil$anvilconverterdata.lastUpdated = nbt.getLong("LastUpdate");
      } catch (ClassCastException var5) {
         chunkloaderutil$anvilconverterdata.lastUpdated = (long)nbt.getInt("LastUpdate");
      }

      return chunkloaderutil$anvilconverterdata;
   }

   public static void convertToAnvilFormat(ChunkLoaderUtil.AnvilConverterData converterData, CompoundNBT compound, BiomeProvider provider) {
      compound.putInt("xPos", converterData.x);
      compound.putInt("zPos", converterData.z);
      compound.putLong("LastUpdate", converterData.lastUpdated);
      int[] aint = new int[converterData.heightmap.length];

      for(int i = 0; i < converterData.heightmap.length; ++i) {
         aint[i] = converterData.heightmap[i];
      }

      compound.putIntArray("HeightMap", aint);
      compound.putBoolean("TerrainPopulated", converterData.terrainPopulated);
      ListNBT listnbt = new ListNBT();

      for(int j = 0; j < 8; ++j) {
         boolean flag = true;

         for(int k = 0; k < 16 && flag; ++k) {
            for(int l = 0; l < 16 && flag; ++l) {
               for(int i1 = 0; i1 < 16; ++i1) {
                  int j1 = k << 11 | i1 << 7 | l + (j << 4);
                  int k1 = converterData.blocks[j1];
                  if (k1 != 0) {
                     flag = false;
                     break;
                  }
               }
            }
         }

         if (!flag) {
            byte[] abyte1 = new byte[4096];
            NibbleArray nibblearray = new NibbleArray();
            NibbleArray nibblearray1 = new NibbleArray();
            NibbleArray nibblearray2 = new NibbleArray();

            for(int j3 = 0; j3 < 16; ++j3) {
               for(int l1 = 0; l1 < 16; ++l1) {
                  for(int i2 = 0; i2 < 16; ++i2) {
                     int j2 = j3 << 11 | i2 << 7 | l1 + (j << 4);
                     int k2 = converterData.blocks[j2];
                     abyte1[l1 << 8 | i2 << 4 | j3] = (byte)(k2 & 255);
                     nibblearray.set(j3, l1, i2, converterData.data.get(j3, l1 + (j << 4), i2));
                     nibblearray1.set(j3, l1, i2, converterData.skyLight.get(j3, l1 + (j << 4), i2));
                     nibblearray2.set(j3, l1, i2, converterData.blockLight.get(j3, l1 + (j << 4), i2));
                  }
               }
            }

            CompoundNBT compoundnbt = new CompoundNBT();
            compoundnbt.putByte("Y", (byte)(j & 255));
            compoundnbt.putByteArray("Blocks", abyte1);
            compoundnbt.putByteArray("Data", nibblearray.getData());
            compoundnbt.putByteArray("SkyLight", nibblearray1.getData());
            compoundnbt.putByteArray("BlockLight", nibblearray2.getData());
            listnbt.add(compoundnbt);
         }
      }

      compound.put("Sections", listnbt);
      byte[] abyte = new byte[256];
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int l2 = 0; l2 < 16; ++l2) {
         for(int i3 = 0; i3 < 16; ++i3) {
            blockpos$mutableblockpos.setPos(converterData.x << 4 | l2, 0, converterData.z << 4 | i3);
            abyte[i3 << 4 | l2] = (byte)(Registry.BIOME.getId(provider.getBiome(blockpos$mutableblockpos)) & 255);
         }
      }

      compound.putByteArray("Biomes", abyte);
      compound.put("Entities", converterData.field_76702_h);
      compound.put("TileEntities", converterData.tileEntities);
      if (converterData.tileTicks != null) {
         compound.put("TileTicks", converterData.tileTicks);
      }

      compound.putBoolean("convertedFromAlphaFormat", true);
   }

   public static class AnvilConverterData {
      public long lastUpdated;
      public boolean terrainPopulated;
      public byte[] heightmap;
      public NibbleArrayReader blockLight;
      public NibbleArrayReader skyLight;
      public NibbleArrayReader data;
      public byte[] blocks;
      public ListNBT field_76702_h;
      public ListNBT tileEntities;
      public ListNBT tileTicks;
      public final int x;
      public final int z;

      public AnvilConverterData(int xIn, int zIn) {
         this.x = xIn;
         this.z = zIn;
      }
   }
}