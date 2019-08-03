package net.minecraft.world.chunk.storage;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OptionalDynamic;
import com.mojang.datafixers.types.DynamicOps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegionSectionCache<R extends IDynamicSerializable> extends RegionFileCache {
   private static final Logger field_219120_a = LogManager.getLogger();
   private final Long2ObjectMap<Optional<R>> field_219121_b = new Long2ObjectOpenHashMap<>();
   private final LongLinkedOpenHashSet field_219122_d = new LongLinkedOpenHashSet();
   private final BiFunction<Runnable, Dynamic<?>, R> field_219123_e;
   private final Function<Runnable, R> field_219124_f;
   private final DataFixer field_219125_g;
   private final DefaultTypeReferences field_219126_h;

   public RegionSectionCache(File p_i49937_1_, BiFunction<Runnable, Dynamic<?>, R> p_i49937_2_, Function<Runnable, R> p_i49937_3_, DataFixer p_i49937_4_, DefaultTypeReferences p_i49937_5_) {
      super(p_i49937_1_);
      this.field_219123_e = p_i49937_2_;
      this.field_219124_f = p_i49937_3_;
      this.field_219125_g = p_i49937_4_;
      this.field_219126_h = p_i49937_5_;
   }

   protected void func_219115_a(BooleanSupplier p_219115_1_) {
      while(!this.field_219122_d.isEmpty() && p_219115_1_.getAsBoolean()) {
         ChunkPos chunkpos = SectionPos.from(this.field_219122_d.firstLong()).asChunkPos();
         this.func_219117_c(chunkpos);
      }

   }

   @Nullable
   protected Optional<R> func_219106_c(long p_219106_1_) {
      return this.field_219121_b.get(p_219106_1_);
   }

   protected Optional<R> func_219113_d(long p_219113_1_) {
      SectionPos sectionpos = SectionPos.from(p_219113_1_);
      if (this.func_219114_b(sectionpos)) {
         return Optional.empty();
      } else {
         Optional<R> optional = this.func_219106_c(p_219113_1_);
         if (optional != null) {
            return optional;
         } else {
            this.func_219107_b(sectionpos.asChunkPos());
            optional = this.func_219106_c(p_219113_1_);
            if (optional == null) {
               throw new IllegalStateException();
            } else {
               return optional;
            }
         }
      }
   }

   protected boolean func_219114_b(SectionPos p_219114_1_) {
      return World.isYOutOfBounds(SectionPos.toWorld(p_219114_1_.getSectionY()));
   }

   protected R func_219110_e(long p_219110_1_) {
      Optional<R> optional = this.func_219113_d(p_219110_1_);
      if (optional.isPresent()) {
         return (R)(optional.get());
      } else {
         R r = this.field_219124_f.apply(() -> {
            this.func_219116_a(p_219110_1_);
         });
         this.field_219121_b.put(p_219110_1_, Optional.of(r));
         return r;
      }
   }

   private void func_219107_b(ChunkPos p_219107_1_) {
      this.func_219119_a(p_219107_1_, NBTDynamicOps.INSTANCE, this.func_223138_c(p_219107_1_));
   }

   @Nullable
   private CompoundNBT func_223138_c(ChunkPos p_223138_1_) {
      try {
         return this.readChunk(p_223138_1_);
      } catch (IOException ioexception) {
         field_219120_a.error("Error reading chunk {} data from disk", p_223138_1_, ioexception);
         return null;
      }
   }

   private <T> void func_219119_a(ChunkPos p_219119_1_, DynamicOps<T> p_219119_2_, @Nullable T p_219119_3_) {
      if (p_219119_3_ == null) {
         for(int i = 0; i < 16; ++i) {
            this.field_219121_b.put(SectionPos.from(p_219119_1_, i).asLong(), Optional.empty());
         }
      } else {
         Dynamic<T> dynamic1 = new Dynamic<>(p_219119_2_, p_219119_3_);
         int j = func_219103_a(dynamic1);
         int k = SharedConstants.getVersion().getWorldVersion();
         boolean flag = j != k;
         Dynamic<T> dynamic = this.field_219125_g.update(this.field_219126_h.func_219816_a(), dynamic1, j, k);
         OptionalDynamic<T> optionaldynamic = dynamic.get("Sections");

         for(int l = 0; l < 16; ++l) {
            long i1 = SectionPos.from(p_219119_1_, l).asLong();
            Optional<R> optional = optionaldynamic.get(Integer.toString(l)).get().map((p_219105_3_) -> {
               return (R)(this.field_219123_e.apply(() -> {
                  this.func_219116_a(i1);
               }, p_219105_3_));
            });
            this.field_219121_b.put(i1, optional);
            optional.ifPresent((p_219118_4_) -> {
               this.func_219111_b(i1);
               if (flag) {
                  this.func_219116_a(i1);
               }

            });
         }
      }

   }

   private void func_219117_c(ChunkPos p_219117_1_) {
      Dynamic<INBT> dynamic = this.func_219108_a(p_219117_1_, NBTDynamicOps.INSTANCE);
      INBT inbt = dynamic.getValue();
      if (inbt instanceof CompoundNBT) {
         try {
            this.writeChunk(p_219117_1_, (CompoundNBT)inbt);
         } catch (IOException ioexception) {
            field_219120_a.error("Error writing data to disk", (Throwable)ioexception);
         }
      } else {
         field_219120_a.error("Expected compound tag, got {}", (Object)inbt);
      }

   }

   private <T> Dynamic<T> func_219108_a(ChunkPos p_219108_1_, DynamicOps<T> p_219108_2_) {
      Map<T, T> map = Maps.newHashMap();

      for(int i = 0; i < 16; ++i) {
         long j = SectionPos.from(p_219108_1_, i).asLong();
         this.field_219122_d.remove(j);
         Optional<R> optional = this.field_219121_b.get(j);
         if (optional != null && optional.isPresent()) {
            map.put(p_219108_2_.createString(Integer.toString(i)), ((IDynamicSerializable)optional.get()).serialize(p_219108_2_));
         }
      }

      return new Dynamic<>(p_219108_2_, p_219108_2_.createMap(ImmutableMap.of(p_219108_2_.createString("Sections"), p_219108_2_.createMap(map), p_219108_2_.createString("DataVersion"), p_219108_2_.createInt(SharedConstants.getVersion().getWorldVersion()))));
   }

   protected void func_219111_b(long p_219111_1_) {
   }

   protected void func_219116_a(long p_219116_1_) {
      Optional<R> optional = this.field_219121_b.get(p_219116_1_);
      if (optional != null && optional.isPresent()) {
         this.field_219122_d.add(p_219116_1_);
      } else {
         field_219120_a.warn("No data for position: {}", (Object)SectionPos.from(p_219116_1_));
      }
   }

   private static int func_219103_a(Dynamic<?> p_219103_0_) {
      return p_219103_0_.get("DataVersion").asNumber().orElse(1945).intValue();
   }

   public void func_219112_a(ChunkPos p_219112_1_) {
      if (!this.field_219122_d.isEmpty()) {
         for(int i = 0; i < 16; ++i) {
            long j = SectionPos.from(p_219112_1_, i).asLong();
            if (this.field_219122_d.contains(j)) {
               this.func_219117_c(p_219112_1_);
               return;
            }
         }
      }

   }
}