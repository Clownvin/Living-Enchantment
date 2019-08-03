package net.minecraft.world.chunk;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.BitArray;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockStateContainer<T> implements IBlockStatePaletteResizer<T> {
   private final IBlockStatePalette<T> field_205521_b;
   private final IBlockStatePaletteResizer<T> field_205522_c = (p_205517_0_, p_205517_1_) -> {
      return 0;
   };
   private final ObjectIntIdentityMap<T> registry;
   private final Function<CompoundNBT, T> deserializer;
   private final Function<T, CompoundNBT> serializer;
   private final T defaultState;
   protected BitArray storage;
   private IBlockStatePalette<T> palette;
   private int bits;
   private final ReentrantLock lock = new ReentrantLock();

   public void lock() {
      if (this.lock.isLocked() && !this.lock.isHeldByCurrentThread()) {
         String s = Thread.getAllStackTraces().keySet().stream().filter(Objects::nonNull).map((p_210458_0_) -> {
            return p_210458_0_.getName() + ": \n\tat " + (String)Arrays.stream(p_210458_0_.getStackTrace()).map(Object::toString).collect(Collectors.joining("\n\tat "));
         }).collect(Collectors.joining("\n"));
         CrashReport crashreport = new CrashReport("Writing into PalettedContainer from multiple threads", new IllegalStateException());
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Thread dumps");
         crashreportcategory.addDetail("Thread dumps", s);
         throw new ReportedException(crashreport);
      } else {
         this.lock.lock();
      }
   }

   public void unlock() {
      this.lock.unlock();
   }

   public BlockStateContainer(IBlockStatePalette<T> p_i48961_1_, ObjectIntIdentityMap<T> p_i48961_2_, Function<CompoundNBT, T> p_i48961_3_, Function<T, CompoundNBT> p_i48961_4_, T p_i48961_5_) {
      this.field_205521_b = p_i48961_1_;
      this.registry = p_i48961_2_;
      this.deserializer = p_i48961_3_;
      this.serializer = p_i48961_4_;
      this.defaultState = p_i48961_5_;
      this.setBits(4);
   }

   private static int getIndex(int x, int y, int z) {
      return y << 8 | z << 4 | x;
   }

   private void setBits(int bitsIn) {
      setBits(bitsIn, false);
   }
   private void setBits(int bitsIn, boolean forceBits) {
      if (bitsIn != this.bits) {
         this.bits = bitsIn;
         if (this.bits <= 4) {
            this.bits = 4;
            this.palette = new BlockStatePaletteLinear<>(this.registry, this.bits, this, this.deserializer);
         } else if (this.bits < 9) {
            this.palette = new BlockStatePaletteHashMap<>(this.registry, this.bits, this, this.deserializer, this.serializer);
         } else {
            this.palette = this.field_205521_b;
            this.bits = MathHelper.log2DeBruijn(this.registry.size());
            if (forceBits)
               this.bits = bitsIn;
         }

         this.palette.idFor(this.defaultState);
         this.storage = new BitArray(this.bits, 4096);
      }
   }

   public int onResize(int p_onResize_1_, T p_onResize_2_) {
      this.lock();
      BitArray bitarray = this.storage;
      IBlockStatePalette<T> iblockstatepalette = this.palette;
      this.setBits(p_onResize_1_);

      for(int i = 0; i < bitarray.size(); ++i) {
         T t = iblockstatepalette.get(bitarray.getAt(i));
         if (t != null) {
            this.set(i, t);
         }
      }

      int j = this.palette.idFor(p_onResize_2_);
      this.unlock();
      return j;
   }

   public T func_222641_a(int p_222641_1_, int p_222641_2_, int p_222641_3_, T p_222641_4_) {
      this.lock();
      T t = (T)this.func_222643_a(getIndex(p_222641_1_, p_222641_2_, p_222641_3_), p_222641_4_);
      this.unlock();
      return t;
   }

   public T func_222639_b(int p_222639_1_, int p_222639_2_, int p_222639_3_, T p_222639_4_) {
      return (T)this.func_222643_a(getIndex(p_222639_1_, p_222639_2_, p_222639_3_), p_222639_4_);
   }

   protected T func_222643_a(int p_222643_1_, T p_222643_2_) {
      int i = this.palette.idFor(p_222643_2_);
      int j = this.storage.func_219789_a(p_222643_1_, i);
      T t = this.palette.get(j);
      return (T)(t == null ? this.defaultState : t);
   }

   protected void set(int index, T state) {
      int i = this.palette.idFor(state);
      this.storage.setAt(index, i);
   }

   public T get(int x, int y, int z) {
      return (T)this.get(getIndex(x, y, z));
   }

   protected T get(int index) {
      T t = this.palette.get(this.storage.getAt(index));
      return (T)(t == null ? this.defaultState : t);
   }

   @OnlyIn(Dist.CLIENT)
   public void read(PacketBuffer buf) {
      this.lock();
      int i = buf.readByte();
      if (this.bits != i) {
         this.setBits(i, true); //Forge, Force bit density to fix network issues, resize below if needed.
      }

      this.palette.read(buf);
      buf.readLongArray(this.storage.getBackingLongArray());
      this.unlock();

      int regSize = MathHelper.log2DeBruijn(this.registry.size());
      if (this.palette == field_205521_b && this.bits != regSize) // Resize bits to fit registry.
         this.onResize(regSize, defaultState);
   }

   public void write(PacketBuffer buf) {
      this.lock();
      buf.writeByte(this.bits);
      this.palette.write(buf);
      buf.writeLongArray(this.storage.getBackingLongArray());
      this.unlock();
   }

   public void readChunkPalette(ListNBT p_222642_1_, long[] p_222642_2_) {
      this.lock();
      int i = Math.max(4, MathHelper.log2DeBruijn(p_222642_1_.size()));
      if (i != this.bits) {
         this.setBits(i);
      }

      this.palette.read(p_222642_1_);
      int j = p_222642_2_.length * 64 / 4096;
      if (this.palette == this.field_205521_b) {
         IBlockStatePalette<T> iblockstatepalette = new BlockStatePaletteHashMap<>(this.registry, i, this.field_205522_c, this.deserializer, this.serializer);
         iblockstatepalette.read(p_222642_1_);
         BitArray bitarray = new BitArray(i, 4096, p_222642_2_);

         for(int k = 0; k < 4096; ++k) {
            this.storage.setAt(k, this.field_205521_b.idFor(iblockstatepalette.get(bitarray.getAt(k))));
         }
      } else if (j == this.bits) {
         System.arraycopy(p_222642_2_, 0, this.storage.getBackingLongArray(), 0, p_222642_2_.length);
      } else {
         BitArray bitarray1 = new BitArray(j, 4096, p_222642_2_);

         for(int l = 0; l < 4096; ++l) {
            this.storage.setAt(l, bitarray1.getAt(l));
         }
      }

      this.unlock();
   }

   public void writeChunkPalette(CompoundNBT compound, String paletteName, String paletteDataName) {
      this.lock();
      BlockStatePaletteHashMap<T> blockstatepalettehashmap = new BlockStatePaletteHashMap<>(this.registry, this.bits, this.field_205522_c, this.deserializer, this.serializer);
      blockstatepalettehashmap.idFor(this.defaultState);
      int[] aint = new int[4096];

      for(int i = 0; i < 4096; ++i) {
         aint[i] = blockstatepalettehashmap.idFor((T)this.get(i));
      }

      ListNBT listnbt = new ListNBT();
      blockstatepalettehashmap.writePaletteToList(listnbt);
      compound.put(paletteName, listnbt);
      int j = Math.max(4, MathHelper.log2DeBruijn(listnbt.size()));
      BitArray bitarray = new BitArray(j, 4096);

      for(int k = 0; k < aint.length; ++k) {
         bitarray.setAt(k, aint[k]);
      }

      compound.putLongArray(paletteDataName, bitarray.getBackingLongArray());
      this.unlock();
   }

   public int getSerializedSize() {
      return 1 + this.palette.getSerializedSize() + PacketBuffer.getVarIntSize(this.storage.size()) + this.storage.getBackingLongArray().length * 8;
   }

   public boolean contains(T p_222640_1_) {
      return this.palette.contains(p_222640_1_);
   }
}