package net.minecraft.world.lighting;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import javax.annotation.Nullable;
import net.minecraft.util.Direction;
import net.minecraft.util.SectionDistanceGraph;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.IChunkLightProvider;
import net.minecraft.world.chunk.NibbleArray;

public abstract class SectionLightStorage<M extends LightDataMap<M>> extends SectionDistanceGraph {
   protected static final NibbleArray field_215534_a = new NibbleArray();
   private static final Direction[] DIRECTIONS = Direction.values();
   private final LightType type;
   private final IChunkLightProvider chunkProvider;
   protected final LongSet field_215535_b = new LongOpenHashSet();
   protected final LongSet field_215536_c = new LongOpenHashSet();
   protected final LongSet field_215537_d = new LongOpenHashSet();
   protected volatile M field_215538_e;
   protected final M field_215539_f;
   protected final LongSet field_215540_g = new LongOpenHashSet();
   protected final LongSet field_215541_h = new LongOpenHashSet();
   protected final Long2ObjectMap<NibbleArray> field_215542_i = new Long2ObjectOpenHashMap<>();
   private final LongSet field_223114_n = new LongOpenHashSet();
   private final LongSet field_215547_n = new LongOpenHashSet();
   protected volatile boolean field_215543_j;

   protected SectionLightStorage(LightType p_i51291_1_, IChunkLightProvider p_i51291_2_, M p_i51291_3_) {
      super(3, 16, 256);
      this.type = p_i51291_1_;
      this.chunkProvider = p_i51291_2_;
      this.field_215539_f = p_i51291_3_;
      this.field_215538_e = p_i51291_3_.copy();
      this.field_215538_e.disableCaching();
   }

   protected boolean func_215518_g(long p_215518_1_) {
      return this.func_215520_a(p_215518_1_, true) != null;
   }

   @Nullable
   protected NibbleArray func_215520_a(long sectionPosIn, boolean p_215520_3_) {
      return this.getArray((M)(p_215520_3_ ? this.field_215539_f : this.field_215538_e), sectionPosIn);
   }

   @Nullable
   protected NibbleArray getArray(M map, long sectionPosIn) {
      return map.getArray(sectionPosIn);
   }

   @Nullable
   public NibbleArray func_222858_h(long p_222858_1_) {
      NibbleArray nibblearray = this.field_215542_i.get(p_222858_1_);
      return nibblearray != null ? nibblearray : this.func_215520_a(p_222858_1_, false);
   }

   protected abstract int func_215525_d(long worldPos);

   protected int func_215521_h(long p_215521_1_) {
      long i = SectionPos.worldToSection(p_215521_1_);
      NibbleArray nibblearray = this.func_215520_a(i, true);
      return nibblearray.get(SectionPos.mask(BlockPos.unpackX(p_215521_1_)), SectionPos.mask(BlockPos.unpackY(p_215521_1_)), SectionPos.mask(BlockPos.unpackZ(p_215521_1_)));
   }

   protected void func_215517_b(long p_215517_1_, int p_215517_3_) {
      long i = SectionPos.worldToSection(p_215517_1_);
      if (this.field_215540_g.add(i)) {
         this.field_215539_f.copyArray(i);
      }

      NibbleArray nibblearray = this.func_215520_a(i, true);
      nibblearray.set(SectionPos.mask(BlockPos.unpackX(p_215517_1_)), SectionPos.mask(BlockPos.unpackY(p_215517_1_)), SectionPos.mask(BlockPos.unpackZ(p_215517_1_)), p_215517_3_);

      for(int j = -1; j <= 1; ++j) {
         for(int k = -1; k <= 1; ++k) {
            for(int l = -1; l <= 1; ++l) {
               this.field_215541_h.add(SectionPos.worldToSection(BlockPos.offset(p_215517_1_, k, l, j)));
            }
         }
      }

   }

   protected int func_215471_c(long sectionPosIn) {
      if (sectionPosIn == Long.MAX_VALUE) {
         return 2;
      } else if (this.field_215535_b.contains(sectionPosIn)) {
         return 0;
      } else {
         return !this.field_215547_n.contains(sectionPosIn) && this.field_215539_f.hasArray(sectionPosIn) ? 1 : 2;
      }
   }

   protected int func_215516_b(long p_215516_1_) {
      if (this.field_215536_c.contains(p_215516_1_)) {
         return 2;
      } else {
         return !this.field_215535_b.contains(p_215516_1_) && !this.field_215537_d.contains(p_215516_1_) ? 2 : 0;
      }
   }

   protected void func_215476_a(long sectionPosIn, int p_215476_3_) {
      int i = this.func_215471_c(sectionPosIn);
      if (i != 0 && p_215476_3_ == 0) {
         this.field_215535_b.add(sectionPosIn);
         this.field_215537_d.remove(sectionPosIn);
      }

      if (i == 0 && p_215476_3_ != 0) {
         this.field_215535_b.remove(sectionPosIn);
         this.field_215536_c.remove(sectionPosIn);
      }

      if (i >= 2 && p_215476_3_ != 2) {
         if (this.field_215547_n.contains(sectionPosIn)) {
            this.field_215547_n.remove(sectionPosIn);
         } else {
            this.field_215539_f.setArray(sectionPosIn, this.func_215530_i(sectionPosIn));
            this.field_215540_g.add(sectionPosIn);
            this.func_215524_j(sectionPosIn);

            for(int j = -1; j <= 1; ++j) {
               for(int k = -1; k <= 1; ++k) {
                  for(int l = -1; l <= 1; ++l) {
                     this.field_215541_h.add(SectionPos.worldToSection(BlockPos.offset(sectionPosIn, k, l, j)));
                  }
               }
            }
         }
      }

      if (i != 2 && p_215476_3_ >= 2) {
         this.field_215547_n.add(sectionPosIn);
      }

      this.field_215543_j = !this.field_215547_n.isEmpty();
   }

   protected NibbleArray func_215530_i(long p_215530_1_) {
      NibbleArray nibblearray = this.field_215542_i.get(p_215530_1_);
      return nibblearray != null ? nibblearray : new NibbleArray();
   }

   protected void func_215528_a(LightEngine<?, ?> engine, long sectionPosIn) {
      int i = SectionPos.toWorld(SectionPos.extractX(sectionPosIn));
      int j = SectionPos.toWorld(SectionPos.extractY(sectionPosIn));
      int k = SectionPos.toWorld(SectionPos.extractZ(sectionPosIn));

      for(int l = 0; l < 16; ++l) {
         for(int i1 = 0; i1 < 16; ++i1) {
            for(int j1 = 0; j1 < 16; ++j1) {
               long k1 = BlockPos.pack(i + l, j + i1, k + j1);
               engine.func_215479_e(k1);
            }
         }
      }

   }

   protected boolean func_215527_a() {
      return this.field_215543_j;
   }

   protected void func_215522_a(LightEngine<M, ?> engine, boolean p_215522_2_, boolean p_215522_3_) {
      if (this.func_215527_a() || !this.field_215542_i.isEmpty()) {
         for(long i : this.field_215547_n) {
            this.func_215528_a(engine, i);
            NibbleArray nibblearray = this.field_215542_i.remove(i);
            NibbleArray nibblearray1 = this.field_215539_f.removeArray(i);
            if (this.field_223114_n.contains(SectionPos.func_218169_f(i))) {
               if (nibblearray != null) {
                  this.field_215542_i.put(i, nibblearray);
               } else if (nibblearray1 != null) {
                  this.field_215542_i.put(i, nibblearray1);
               }
            }
         }

         this.field_215539_f.invalidateCaches();

         for(long i2 : this.field_215547_n) {
            this.func_215523_k(i2);
         }

         this.field_215547_n.clear();
         this.field_215543_j = false;

         for(Entry<NibbleArray> entry : this.field_215542_i.long2ObjectEntrySet()) {
            long j = entry.getLongKey();
            if (this.func_215518_g(j)) {
               NibbleArray nibblearray2 = entry.getValue();
               if (this.field_215539_f.getArray(j) != nibblearray2) {
                  this.func_215528_a(engine, j);
                  this.field_215539_f.setArray(j, nibblearray2);
                  this.field_215540_g.add(j);
               }
            }
         }

         this.field_215539_f.invalidateCaches();
         if (!p_215522_3_) {
            for(long j2 : this.field_215542_i.keySet()) {
               if (this.func_215518_g(j2)) {
                  int l2 = SectionPos.toWorld(SectionPos.extractX(j2));
                  int i3 = SectionPos.toWorld(SectionPos.extractY(j2));
                  int k = SectionPos.toWorld(SectionPos.extractZ(j2));

                  for(Direction direction : DIRECTIONS) {
                     long l = SectionPos.withOffset(j2, direction);
                     if (!this.field_215542_i.containsKey(l) && this.func_215518_g(l)) {
                        for(int i1 = 0; i1 < 16; ++i1) {
                           for(int j1 = 0; j1 < 16; ++j1) {
                              long k1;
                              long l1;
                              switch(direction) {
                              case DOWN:
                                 k1 = BlockPos.pack(l2 + j1, i3, k + i1);
                                 l1 = BlockPos.pack(l2 + j1, i3 - 1, k + i1);
                                 break;
                              case UP:
                                 k1 = BlockPos.pack(l2 + j1, i3 + 16 - 1, k + i1);
                                 l1 = BlockPos.pack(l2 + j1, i3 + 16, k + i1);
                                 break;
                              case NORTH:
                                 k1 = BlockPos.pack(l2 + i1, i3 + j1, k);
                                 l1 = BlockPos.pack(l2 + i1, i3 + j1, k - 1);
                                 break;
                              case SOUTH:
                                 k1 = BlockPos.pack(l2 + i1, i3 + j1, k + 16 - 1);
                                 l1 = BlockPos.pack(l2 + i1, i3 + j1, k + 16);
                                 break;
                              case WEST:
                                 k1 = BlockPos.pack(l2, i3 + i1, k + j1);
                                 l1 = BlockPos.pack(l2 - 1, i3 + i1, k + j1);
                                 break;
                              default:
                                 k1 = BlockPos.pack(l2 + 16 - 1, i3 + i1, k + j1);
                                 l1 = BlockPos.pack(l2 + 16, i3 + i1, k + j1);
                              }

                              engine.func_215469_a(k1, l1, engine.func_215480_b(k1, l1, engine.func_215471_c(k1)), false);
                              engine.func_215469_a(l1, k1, engine.func_215480_b(l1, k1, engine.func_215471_c(l1)), false);
                           }
                        }
                     }
                  }
               }
            }
         }

         ObjectIterator<Entry<NibbleArray>> objectiterator = this.field_215542_i.long2ObjectEntrySet().iterator();

         while(objectiterator.hasNext()) {
            Entry<NibbleArray> entry1 = objectiterator.next();
            long k2 = entry1.getLongKey();
            if (this.func_215518_g(k2)) {
               objectiterator.remove();
            }
         }

      }
   }

   protected void func_215524_j(long p_215524_1_) {
   }

   protected void func_215523_k(long p_215523_1_) {
   }

   protected void func_215526_b(long p_215526_1_, boolean p_215526_3_) {
   }

   public void func_223113_c(long p_223113_1_, boolean p_223113_3_) {
      if (p_223113_3_) {
         this.field_223114_n.add(p_223113_1_);
      } else {
         this.field_223114_n.remove(p_223113_1_);
      }

   }

   protected void setData(long sectionPosIn, @Nullable NibbleArray array) {
      if (array != null) {
         this.field_215542_i.put(sectionPosIn, array);
      } else {
         this.field_215542_i.remove(sectionPosIn);
      }

   }

   protected void func_215519_c(long sectionPosIn, boolean p_215519_3_) {
      boolean flag = this.field_215535_b.contains(sectionPosIn);
      if (!flag && !p_215519_3_) {
         this.field_215537_d.add(sectionPosIn);
         this.func_215469_a(Long.MAX_VALUE, sectionPosIn, 0, true);
      }

      if (flag && p_215519_3_) {
         this.field_215536_c.add(sectionPosIn);
         this.func_215469_a(Long.MAX_VALUE, sectionPosIn, 2, false);
      }

   }

   protected void func_215532_c() {
      if (this.func_215481_b()) {
         this.func_215483_b(Integer.MAX_VALUE);
      }

   }

   protected void func_215533_d() {
      if (!this.field_215540_g.isEmpty()) {
         M m = this.field_215539_f.copy();
         m.disableCaching();
         this.field_215538_e = m;
         this.field_215540_g.clear();
      }

      if (!this.field_215541_h.isEmpty()) {
         LongIterator longiterator = this.field_215541_h.iterator();

         while(longiterator.hasNext()) {
            long i = longiterator.nextLong();
            this.chunkProvider.func_217201_a(this.type, SectionPos.from(i));
         }

         this.field_215541_h.clear();
      }

   }
}