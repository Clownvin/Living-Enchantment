package net.minecraft.world.gen;

import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.jigsaw.JigsawJunction;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.structure.AbstractVillagePiece;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.StructureStart;

public abstract class NoiseChunkGenerator<T extends GenerationSettings> extends ChunkGenerator<T> {
   private static final float[] field_222561_h = Util.make(new float[13824], (p_222557_0_) -> {
      for(int i = 0; i < 24; ++i) {
         for(int j = 0; j < 24; ++j) {
            for(int k = 0; k < 24; ++k) {
               p_222557_0_[i * 24 * 24 + j * 24 + k] = (float)func_222554_b(j - 12, k - 12, i - 12);
            }
         }
      }

   });
   private static final BlockState field_222562_i = Blocks.AIR.getDefaultState();
   private final int field_222563_j;
   private final int field_222564_k;
   private final int field_222565_l;
   private final int field_222566_m;
   private final int field_222567_n;
   protected final SharedSeedRandom field_222558_e;
   private final OctavesNoiseGenerator field_222568_o;
   private final OctavesNoiseGenerator field_222569_p;
   private final OctavesNoiseGenerator field_222570_q;
   private final INoiseGenerator field_222571_r;
   protected final BlockState field_222559_f;
   protected final BlockState field_222560_g;

   public NoiseChunkGenerator(IWorld p_i49931_1_, BiomeProvider p_i49931_2_, int p_i49931_3_, int p_i49931_4_, int p_i49931_5_, T p_i49931_6_, boolean p_i49931_7_) {
      super(p_i49931_1_, p_i49931_2_, p_i49931_6_);
      this.field_222563_j = p_i49931_4_;
      this.field_222564_k = p_i49931_3_;
      this.field_222559_f = p_i49931_6_.getDefaultBlock();
      this.field_222560_g = p_i49931_6_.getDefaultFluid();
      this.field_222565_l = 16 / this.field_222564_k;
      this.field_222566_m = p_i49931_5_ / this.field_222563_j;
      this.field_222567_n = 16 / this.field_222564_k;
      this.field_222558_e = new SharedSeedRandom(this.seed);
      this.field_222568_o = new OctavesNoiseGenerator(this.field_222558_e, 16);
      this.field_222569_p = new OctavesNoiseGenerator(this.field_222558_e, 16);
      this.field_222570_q = new OctavesNoiseGenerator(this.field_222558_e, 8);
      this.field_222571_r = (INoiseGenerator)(p_i49931_7_ ? new PerlinNoiseGenerator(this.field_222558_e, 4) : new OctavesNoiseGenerator(this.field_222558_e, 4));
   }

   private double func_222552_a(int p_222552_1_, int p_222552_2_, int p_222552_3_, double p_222552_4_, double p_222552_6_, double p_222552_8_, double p_222552_10_) {
      double d0 = 0.0D;
      double d1 = 0.0D;
      double d2 = 0.0D;
      double d3 = 1.0D;

      for(int i = 0; i < 16; ++i) {
         double d4 = OctavesNoiseGenerator.func_215461_a((double)p_222552_1_ * p_222552_4_ * d3);
         double d5 = OctavesNoiseGenerator.func_215461_a((double)p_222552_2_ * p_222552_6_ * d3);
         double d6 = OctavesNoiseGenerator.func_215461_a((double)p_222552_3_ * p_222552_4_ * d3);
         double d7 = p_222552_6_ * d3;
         d0 += this.field_222568_o.func_215463_a(i).func_215456_a(d4, d5, d6, d7, (double)p_222552_2_ * d7) / d3;
         d1 += this.field_222569_p.func_215463_a(i).func_215456_a(d4, d5, d6, d7, (double)p_222552_2_ * d7) / d3;
         if (i < 8) {
            d2 += this.field_222570_q.func_215463_a(i).func_215456_a(OctavesNoiseGenerator.func_215461_a((double)p_222552_1_ * p_222552_8_ * d3), OctavesNoiseGenerator.func_215461_a((double)p_222552_2_ * p_222552_10_ * d3), OctavesNoiseGenerator.func_215461_a((double)p_222552_3_ * p_222552_8_ * d3), p_222552_10_ * d3, (double)p_222552_2_ * p_222552_10_ * d3) / d3;
         }

         d3 /= 2.0D;
      }

      return MathHelper.clampedLerp(d0 / 512.0D, d1 / 512.0D, (d2 / 10.0D + 1.0D) / 2.0D);
   }

   protected double[] func_222547_b(int p_222547_1_, int p_222547_2_) {
      double[] adouble = new double[this.field_222566_m + 1];
      this.func_222548_a(adouble, p_222547_1_, p_222547_2_);
      return adouble;
   }

   protected void func_222546_a(double[] p_222546_1_, int p_222546_2_, int p_222546_3_, double p_222546_4_, double p_222546_6_, double p_222546_8_, double p_222546_10_, int p_222546_12_, int p_222546_13_) {
      double[] adouble = this.func_222549_a(p_222546_2_, p_222546_3_);
      double d0 = adouble[0];
      double d1 = adouble[1];
      double d2 = this.func_222551_g();
      double d3 = this.func_222553_h();

      for(int i = 0; i < this.func_222550_i(); ++i) {
         double d4 = this.func_222552_a(p_222546_2_, i, p_222546_3_, p_222546_4_, p_222546_6_, p_222546_8_, p_222546_10_);
         d4 = d4 - this.func_222545_a(d0, d1, i);
         if ((double)i > d2) {
            d4 = MathHelper.clampedLerp(d4, (double)p_222546_13_, ((double)i - d2) / (double)p_222546_12_);
         } else if ((double)i < d3) {
            d4 = MathHelper.clampedLerp(d4, -30.0D, (d3 - (double)i) / (d3 - 1.0D));
         }

         p_222546_1_[i] = d4;
      }

   }

   protected abstract double[] func_222549_a(int p_222549_1_, int p_222549_2_);

   protected abstract double func_222545_a(double p_222545_1_, double p_222545_3_, int p_222545_5_);

   protected double func_222551_g() {
      return (double)(this.func_222550_i() - 4);
   }

   protected double func_222553_h() {
      return 0.0D;
   }

   public int func_222529_a(int p_222529_1_, int p_222529_2_, Heightmap.Type p_222529_3_) {
      int i = Math.floorDiv(p_222529_1_, this.field_222564_k);
      int j = Math.floorDiv(p_222529_2_, this.field_222564_k);
      int k = Math.floorMod(p_222529_1_, this.field_222564_k);
      int l = Math.floorMod(p_222529_2_, this.field_222564_k);
      double d0 = (double)k / (double)this.field_222564_k;
      double d1 = (double)l / (double)this.field_222564_k;
      double[][] adouble = new double[][]{this.func_222547_b(i, j), this.func_222547_b(i, j + 1), this.func_222547_b(i + 1, j), this.func_222547_b(i + 1, j + 1)};
      int i1 = this.getSeaLevel();

      for(int j1 = this.field_222566_m - 1; j1 >= 0; --j1) {
         double d2 = adouble[0][j1];
         double d3 = adouble[1][j1];
         double d4 = adouble[2][j1];
         double d5 = adouble[3][j1];
         double d6 = adouble[0][j1 + 1];
         double d7 = adouble[1][j1 + 1];
         double d8 = adouble[2][j1 + 1];
         double d9 = adouble[3][j1 + 1];

         for(int k1 = this.field_222563_j - 1; k1 >= 0; --k1) {
            double d10 = (double)k1 / (double)this.field_222563_j;
            double d11 = MathHelper.lerp3(d10, d0, d1, d2, d6, d4, d8, d3, d7, d5, d9);
            int l1 = j1 * this.field_222563_j + k1;
            if (d11 > 0.0D || l1 < i1) {
               BlockState blockstate;
               if (d11 > 0.0D) {
                  blockstate = this.field_222559_f;
               } else {
                  blockstate = this.field_222560_g;
               }

               if (p_222529_3_.func_222684_d().test(blockstate)) {
                  return l1 + 1;
               }
            }
         }
      }

      return 0;
   }

   protected abstract void func_222548_a(double[] p_222548_1_, int p_222548_2_, int p_222548_3_);

   public int func_222550_i() {
      return this.field_222566_m + 1;
   }

   public void generateSurface(IChunk p_222535_1_) {
      ChunkPos chunkpos = p_222535_1_.getPos();
      int i = chunkpos.x;
      int j = chunkpos.z;
      SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
      sharedseedrandom.setBaseChunkSeed(i, j);
      ChunkPos chunkpos1 = p_222535_1_.getPos();
      int k = chunkpos1.getXStart();
      int l = chunkpos1.getZStart();
      double d0 = 0.0625D;
      Biome[] abiome = p_222535_1_.getBiomes();

      for(int i1 = 0; i1 < 16; ++i1) {
         for(int j1 = 0; j1 < 16; ++j1) {
            int k1 = k + i1;
            int l1 = l + j1;
            int i2 = p_222535_1_.getTopBlockY(Heightmap.Type.WORLD_SURFACE_WG, i1, j1) + 1;
            double d1 = this.field_222571_r.func_215460_a((double)k1 * 0.0625D, (double)l1 * 0.0625D, 0.0625D, (double)i1 * 0.0625D);
            abiome[j1 * 16 + i1].buildSurface(sharedseedrandom, p_222535_1_, k1, l1, i2, d1, this.getSettings().getDefaultBlock(), this.getSettings().getDefaultFluid(), this.getSeaLevel(), this.world.getSeed());
         }
      }

      this.func_222555_a(p_222535_1_, sharedseedrandom);
   }

   protected void func_222555_a(IChunk p_222555_1_, Random p_222555_2_) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      int i = p_222555_1_.getPos().getXStart();
      int j = p_222555_1_.getPos().getZStart();
      T t = this.getSettings();
      int k = t.func_214968_u();
      int l = t.func_214967_t();

      for(BlockPos blockpos : BlockPos.getAllInBoxMutable(i, 0, j, i + 15, 0, j + 15)) {
         if (l > 0) {
            for(int i1 = l; i1 >= l - 4; --i1) {
               if (i1 >= l - p_222555_2_.nextInt(5)) {
                  p_222555_1_.setBlockState(blockpos$mutableblockpos.setPos(blockpos.getX(), i1, blockpos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
               }
            }
         }

         if (k < 256) {
            for(int j1 = k + 4; j1 >= k; --j1) {
               if (j1 <= k + p_222555_2_.nextInt(5)) {
                  p_222555_1_.setBlockState(blockpos$mutableblockpos.setPos(blockpos.getX(), j1, blockpos.getZ()), Blocks.BEDROCK.getDefaultState(), false);
               }
            }
         }
      }

   }

   public void func_222537_b(IWorld p_222537_1_, IChunk p_222537_2_) {
      int i = this.getSeaLevel();
      ObjectList<AbstractVillagePiece> objectlist = new ObjectArrayList<>(10);
      ObjectList<JigsawJunction> objectlist1 = new ObjectArrayList<>(32);
      ChunkPos chunkpos = p_222537_2_.getPos();
      int j = chunkpos.x;
      int k = chunkpos.z;
      int l = j << 4;
      int i1 = k << 4;

      for(Structure<?> structure : Feature.field_214488_aQ) {
         String s = structure.getStructureName();
         LongIterator longiterator = p_222537_2_.getStructureReferences(s).iterator();

         while(longiterator.hasNext()) {
            long j1 = longiterator.nextLong();
            ChunkPos chunkpos1 = new ChunkPos(j1);
            IChunk ichunk = p_222537_1_.getChunk(chunkpos1.x, chunkpos1.z);
            StructureStart structurestart = ichunk.getStructureStart(s);
            if (structurestart != null && structurestart.isValid()) {
               for(StructurePiece structurepiece : structurestart.getComponents()) {
                  if (structurepiece.func_214810_a(chunkpos, 12) && structurepiece instanceof AbstractVillagePiece) {
                     AbstractVillagePiece abstractvillagepiece = (AbstractVillagePiece)structurepiece;
                     JigsawPattern.PlacementBehaviour jigsawpattern$placementbehaviour = abstractvillagepiece.func_214826_b().getPlacementBehaviour();
                     if (jigsawpattern$placementbehaviour == JigsawPattern.PlacementBehaviour.RIGID) {
                        objectlist.add(abstractvillagepiece);
                     }

                     for(JigsawJunction jigsawjunction : abstractvillagepiece.getJunctions()) {
                        int k1 = jigsawjunction.getSourceX();
                        int l1 = jigsawjunction.getSourceZ();
                        if (k1 > l - 12 && l1 > i1 - 12 && k1 < l + 15 + 12 && l1 < i1 + 15 + 12) {
                           objectlist1.add(jigsawjunction);
                        }
                     }
                  }
               }
            }
         }
      }

      double[][][] adouble = new double[2][this.field_222567_n + 1][this.field_222566_m + 1];

      for(int j5 = 0; j5 < this.field_222567_n + 1; ++j5) {
         adouble[0][j5] = new double[this.field_222566_m + 1];
         this.func_222548_a(adouble[0][j5], j * this.field_222565_l, k * this.field_222567_n + j5);
         adouble[1][j5] = new double[this.field_222566_m + 1];
      }

      ChunkPrimer chunkprimer = (ChunkPrimer)p_222537_2_;
      Heightmap heightmap = chunkprimer.func_217303_b(Heightmap.Type.OCEAN_FLOOR_WG);
      Heightmap heightmap1 = chunkprimer.func_217303_b(Heightmap.Type.WORLD_SURFACE_WG);
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      ObjectListIterator<AbstractVillagePiece> objectlistiterator = objectlist.iterator();
      ObjectListIterator<JigsawJunction> objectlistiterator1 = objectlist1.iterator();

      for(int k5 = 0; k5 < this.field_222565_l; ++k5) {
         for(int l5 = 0; l5 < this.field_222567_n + 1; ++l5) {
            this.func_222548_a(adouble[1][l5], j * this.field_222565_l + k5 + 1, k * this.field_222567_n + l5);
         }

         for(int i6 = 0; i6 < this.field_222567_n; ++i6) {
            ChunkSection chunksection = chunkprimer.func_217332_a(15);
            chunksection.lock();

            for(int j6 = this.field_222566_m - 1; j6 >= 0; --j6) {
               double d16 = adouble[0][i6][j6];
               double d17 = adouble[0][i6 + 1][j6];
               double d18 = adouble[1][i6][j6];
               double d0 = adouble[1][i6 + 1][j6];
               double d1 = adouble[0][i6][j6 + 1];
               double d2 = adouble[0][i6 + 1][j6 + 1];
               double d3 = adouble[1][i6][j6 + 1];
               double d4 = adouble[1][i6 + 1][j6 + 1];

               for(int i2 = this.field_222563_j - 1; i2 >= 0; --i2) {
                  int j2 = j6 * this.field_222563_j + i2;
                  int k2 = j2 & 15;
                  int l2 = j2 >> 4;
                  if (chunksection.getYLocation() >> 4 != l2) {
                     chunksection.unlock();
                     chunksection = chunkprimer.func_217332_a(l2);
                     chunksection.lock();
                  }

                  double d5 = (double)i2 / (double)this.field_222563_j;
                  double d6 = MathHelper.lerp(d5, d16, d1);
                  double d7 = MathHelper.lerp(d5, d18, d3);
                  double d8 = MathHelper.lerp(d5, d17, d2);
                  double d9 = MathHelper.lerp(d5, d0, d4);

                  for(int i3 = 0; i3 < this.field_222564_k; ++i3) {
                     int j3 = l + k5 * this.field_222564_k + i3;
                     int k3 = j3 & 15;
                     double d10 = (double)i3 / (double)this.field_222564_k;
                     double d11 = MathHelper.lerp(d10, d6, d7);
                     double d12 = MathHelper.lerp(d10, d8, d9);

                     for(int l3 = 0; l3 < this.field_222564_k; ++l3) {
                        int i4 = i1 + i6 * this.field_222564_k + l3;
                        int j4 = i4 & 15;
                        double d13 = (double)l3 / (double)this.field_222564_k;
                        double d14 = MathHelper.lerp(d13, d11, d12);
                        double d15 = MathHelper.clamp(d14 / 200.0D, -1.0D, 1.0D);

                        int k4;
                        int l4;
                        int i5;
                        for(d15 = d15 / 2.0D - d15 * d15 * d15 / 24.0D; objectlistiterator.hasNext(); d15 += func_222556_a(k4, l4, i5) * 0.8D) {
                           AbstractVillagePiece abstractvillagepiece1 = objectlistiterator.next();
                           MutableBoundingBox mutableboundingbox = abstractvillagepiece1.getBoundingBox();
                           k4 = Math.max(0, Math.max(mutableboundingbox.minX - j3, j3 - mutableboundingbox.maxX));
                           l4 = j2 - (mutableboundingbox.minY + abstractvillagepiece1.getGroundLevelDelta());
                           i5 = Math.max(0, Math.max(mutableboundingbox.minZ - i4, i4 - mutableboundingbox.maxZ));
                        }

                        objectlistiterator.back(objectlist.size());

                        while(objectlistiterator1.hasNext()) {
                           JigsawJunction jigsawjunction1 = objectlistiterator1.next();
                           int k6 = j3 - jigsawjunction1.getSourceX();
                           k4 = j2 - jigsawjunction1.getSourceGroundY();
                           l4 = i4 - jigsawjunction1.getSourceZ();
                           d15 += func_222556_a(k6, k4, l4) * 0.4D;
                        }

                        objectlistiterator1.back(objectlist1.size());
                        BlockState blockstate;
                        if (d15 > 0.0D) {
                           blockstate = this.field_222559_f;
                        } else if (j2 < i) {
                           blockstate = this.field_222560_g;
                        } else {
                           blockstate = field_222562_i;
                        }

                        if (blockstate != field_222562_i) {
                           if (blockstate.getLightValue() != 0) {
                              blockpos$mutableblockpos.setPos(j3, j2, i4);
                              chunkprimer.addLightPosition(blockpos$mutableblockpos);
                           }

                           chunksection.set(k3, k2, j4, blockstate, false);
                           heightmap.update(k3, j2, j4, blockstate);
                           heightmap1.update(k3, j2, j4, blockstate);
                        }
                     }
                  }
               }
            }

            chunksection.unlock();
         }

         double[][] adouble1 = adouble[0];
         adouble[0] = adouble[1];
         adouble[1] = adouble1;
      }

   }

   private static double func_222556_a(int p_222556_0_, int p_222556_1_, int p_222556_2_) {
      int i = p_222556_0_ + 12;
      int j = p_222556_1_ + 12;
      int k = p_222556_2_ + 12;
      if (i >= 0 && i < 24) {
         if (j >= 0 && j < 24) {
            return k >= 0 && k < 24 ? (double)field_222561_h[k * 24 * 24 + i * 24 + j] : 0.0D;
         } else {
            return 0.0D;
         }
      } else {
         return 0.0D;
      }
   }

   private static double func_222554_b(int p_222554_0_, int p_222554_1_, int p_222554_2_) {
      double d0 = (double)(p_222554_0_ * p_222554_0_ + p_222554_2_ * p_222554_2_);
      double d1 = (double)p_222554_1_ + 0.5D;
      double d2 = d1 * d1;
      double d3 = Math.pow(Math.E, -(d2 / 16.0D + d0 / 16.0D));
      double d4 = -d1 * MathHelper.fastInvSqrt(d2 / 2.0D + d0 / 2.0D) / 2.0D;
      return d4 * d3;
   }
}