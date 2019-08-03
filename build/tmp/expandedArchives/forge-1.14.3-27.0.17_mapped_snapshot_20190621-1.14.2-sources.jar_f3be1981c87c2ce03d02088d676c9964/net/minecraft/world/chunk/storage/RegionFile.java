package net.minecraft.world.chunk.storage;

import com.google.common.collect.Lists;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import javax.annotation.Nullable;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;

public class RegionFile implements AutoCloseable {
   // Minecraft is limited to 256 sections per chunk. So 1MB. This can easily be override.
   // So we extend this to use the REAL size when the count is maxed by seeking to that section and reading the length.
   private static final boolean FORGE_ENABLE_EXTENDED_SAVE = Boolean.parseBoolean(System.getProperty("forge.enableExtendedSave", "true"));
   private static final org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger();
   private static final byte[] EMPTY_SECTOR = new byte[4096];
   private final RandomAccessFile dataFile;
   private final int[] offsets = new int[1024];
   private final int[] chunkTimestamps = new int[1024];
   private final List<Boolean> sectorFree;
   private final File filepath;

   public RegionFile(File fileNameIn) throws IOException {
      this.filepath = fileNameIn;
      this.dataFile = new RandomAccessFile(fileNameIn, "rw");
      if (this.dataFile.length() < 4096L) {
         this.dataFile.write(EMPTY_SECTOR);
         this.dataFile.write(EMPTY_SECTOR);
      }

      if ((this.dataFile.length() & 4095L) != 0L) {
         for(int i = 0; (long)i < (this.dataFile.length() & 4095L); ++i) {
            this.dataFile.write(0);
         }
      }

      int i1 = (int)this.dataFile.length() / 4096;
      this.sectorFree = Lists.newArrayListWithCapacity(i1);

      for(int j = 0; j < i1; ++j) {
         this.sectorFree.add(true);
      }

      this.sectorFree.set(0, false);
      this.sectorFree.set(1, false);
      this.dataFile.seek(0L);

      for(int j1 = 0; j1 < 1024; ++j1) {
         int k = this.dataFile.readInt();
         this.offsets[j1] = k;
         int length = k & 255;
         if (length == 255) {
            if ((k >> 8) <= this.sectorFree.size()) { // We're maxed out, so we need to read the proper length from the section
               this.dataFile.seek((k >> 8) * 4096);
               length = (this.dataFile.readInt() + 4) / 4096 + 1;
               this.dataFile.seek(j1 * 4 + 4); //Go back to where we were
            }
         }
         if (k != 0 && (k >> 8) + length <= this.sectorFree.size()) {
            for (int l = 0; l < length; ++l) {
               this.sectorFree.set((k >> 8) + l, false);
            }
         }
         else if (length > 0)
            LOGGER.warn("Invalid chunk: ({}, {}) Offset: {} Length: {} runs off end file. {}", j1 % 32, (int)(j1 / 32), k >> 8, length, fileNameIn);
      }

      for(int k1 = 0; k1 < 1024; ++k1) {
         int l1 = this.dataFile.readInt();
         this.chunkTimestamps[k1] = l1;
      }

   }

   @Nullable
   public synchronized DataInputStream func_222666_a(ChunkPos p_222666_1_) throws IOException {
      int i = this.getOffset(p_222666_1_);
      if (i == 0) {
         return null;
      } else {
         int j = i >> 8;
         int k = i & 255;
         if (k == 255) {
            this.dataFile.seek(j * 4096);
            k = (this.dataFile.readInt() + 4) / 4096 + 1;
         }
         if (j + k > this.sectorFree.size()) {
            return null;
         } else {
            this.dataFile.seek((long)(j * 4096));
            int l = this.dataFile.readInt();
            if (l > 4096 * k) {
               LOGGER.warn("Invalid chunk: ({}, {}) Offset: {} Invalid Size: {}>{} {}", p_222666_1_.x, p_222666_1_.z, j, l, k * 4096, filepath);
               return null;
            } else if (l <= 0) {
               LOGGER.warn("Invalid chunk: ({}, {}) Offset: {} Invalid Size: {} {}", p_222666_1_.x, p_222666_1_.z, j, l, filepath);
               return null;
            } else {
               byte b0 = this.dataFile.readByte();
               if (b0 == 1) {
                  byte[] abyte1 = new byte[l - 1];
                  this.dataFile.read(abyte1);
                  return new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(abyte1))));
               } else if (b0 == 2) {
                  byte[] abyte = new byte[l - 1];
                  this.dataFile.read(abyte);
                  return new DataInputStream(new BufferedInputStream(new InflaterInputStream(new ByteArrayInputStream(abyte))));
               } else {
                  return null;
               }
            }
         }
      }
   }

   public boolean func_222662_b(ChunkPos p_222662_1_) {
      int i = this.getOffset(p_222662_1_);
      if (i == 0) {
         return false;
      } else {
         int j = i >> 8;
         int k = i & 255;
         if (j + k > this.sectorFree.size()) {
            return false;
         } else {
            try {
               this.dataFile.seek((long)(j * 4096));
               int l = this.dataFile.readInt();
               if (l > 4096 * k) {
                  return false;
               } else {
                  return l > 0;
               }
            } catch (IOException var6) {
               return false;
            }
         }
      }
   }

   public DataOutputStream func_222661_c(ChunkPos p_222661_1_) {
      return new DataOutputStream(new BufferedOutputStream(new DeflaterOutputStream(new RegionFile.ChunkBuffer(p_222661_1_))));
   }

   protected synchronized void func_222664_a(ChunkPos p_222664_1_, byte[] p_222664_2_, int p_222664_3_) throws IOException {
      int i = this.getOffset(p_222664_1_);
      int j = i >> 8;
      int k = i & 255;
      if (k == 255) {
         this.dataFile.seek(j * 4096);
         k = (this.dataFile.readInt() + 4) / 4096 + 1;
      }
      int l = (p_222664_3_ + 5) / 4096 + 1;
      if (l >= 256 && !FORGE_ENABLE_EXTENDED_SAVE) {
         throw new RuntimeException(String.format("Too big to save, %d > 1048576", p_222664_3_));
      } else {
         if (l >= 256) LOGGER.warn("Large Chunk Detected: ({}, {}) Size: {} {}", p_222664_1_.x, p_222664_1_.z, l, filepath);

         if (j != 0 && k == l) {
            this.write(j, p_222664_2_, p_222664_3_);
         } else {
            for(int i1 = 0; i1 < k; ++i1) {
               this.sectorFree.set(j + i1, true);
            }

            int l1 = this.sectorFree.indexOf(true);
            int j1 = 0;
            if (l1 != -1) {
               for(int k1 = l1; k1 < this.sectorFree.size(); ++k1) {
                  if (j1 != 0) {
                     if (this.sectorFree.get(k1)) {
                        ++j1;
                     } else {
                        j1 = 0;
                     }
                  } else if (this.sectorFree.get(k1)) {
                     l1 = k1;
                     j1 = 1;
                  }

                  if (j1 >= l) {
                     break;
                  }
               }
            }

            if (j1 >= l) {
               j = l1;
               this.writeOffset(p_222664_1_, l1 << 8 | (l > 255 ? 255 : l));

               for(int i2 = 0; i2 < l; ++i2) {
                  this.sectorFree.set(j + i2, false);
               }

               this.write(j, p_222664_2_, p_222664_3_);
            } else {
               this.dataFile.seek(this.dataFile.length());
               j = this.sectorFree.size();

               for(int j2 = 0; j2 < l; ++j2) {
                  this.dataFile.write(EMPTY_SECTOR);
                  this.sectorFree.add(false);
               }

               this.write(j, p_222664_2_, p_222664_3_);
               this.writeOffset(p_222664_1_, j << 8 | (l > 255 ? 255 : l));
            }
         }

         this.writeTimestamp(p_222664_1_, (int)(Util.millisecondsSinceEpoch() / 1000L));
      }
   }

   /**
    * Writes the chunk data to this RegionFile.
    */
   private void write(int sectorNumber, byte[] data, int length) throws IOException {
      this.dataFile.seek((long)(sectorNumber * 4096));
      this.dataFile.writeInt(length + 1);
      this.dataFile.writeByte(2);
      this.dataFile.write(data, 0, length);
   }

   private int getOffset(ChunkPos p_222660_1_) {
      return this.offsets[this.getIndex(p_222660_1_)];
   }

   public boolean contains(ChunkPos p_222667_1_) {
      return this.getOffset(p_222667_1_) != 0;
   }

   private void writeOffset(ChunkPos p_222663_1_, int p_222663_2_) throws IOException {
      int i = this.getIndex(p_222663_1_);
      this.offsets[i] = p_222663_2_;
      this.dataFile.seek((long)(i * 4));
      this.dataFile.writeInt(p_222663_2_);
   }

   private int getIndex(ChunkPos p_222668_1_) {
      return p_222668_1_.getRegionPositionX() + p_222668_1_.getRegionPositionZ() * 32;
   }

   private void writeTimestamp(ChunkPos p_222665_1_, int p_222665_2_) throws IOException {
      int i = this.getIndex(p_222665_1_);
      this.chunkTimestamps[i] = p_222665_2_;
      this.dataFile.seek((long)(4096 + i * 4));
      this.dataFile.writeInt(p_222665_2_);
   }

   public void close() throws IOException {
      this.dataFile.close();
   }

   class ChunkBuffer extends ByteArrayOutputStream {
      private final ChunkPos pos;

      public ChunkBuffer(ChunkPos p_i50620_2_) {
         super(8096);
         this.pos = p_i50620_2_;
      }

      public void close() throws IOException {
         RegionFile.this.func_222664_a(this.pos, this.buf, this.count);
      }
   }
}