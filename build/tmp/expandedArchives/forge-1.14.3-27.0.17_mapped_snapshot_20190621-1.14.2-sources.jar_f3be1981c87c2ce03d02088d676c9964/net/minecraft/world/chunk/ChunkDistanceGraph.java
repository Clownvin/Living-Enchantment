package net.minecraft.world.chunk;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.lighting.LevelBasedGraph;

public abstract class ChunkDistanceGraph extends LevelBasedGraph {
   protected ChunkDistanceGraph(int p_i50712_1_, int p_i50712_2_, int p_i50712_3_) {
      super(p_i50712_1_, p_i50712_2_, p_i50712_3_);
   }

   protected boolean func_215485_a(long p_215485_1_) {
      return p_215485_1_ == ChunkPos.SENTINEL;
   }

   protected void func_215478_a(long p_215478_1_, int p_215478_3_, boolean p_215478_4_) {
      ChunkPos chunkpos = new ChunkPos(p_215478_1_);
      int i = chunkpos.x;
      int j = chunkpos.z;

      for(int k = -1; k <= 1; ++k) {
         for(int l = -1; l <= 1; ++l) {
            long i1 = ChunkPos.asLong(i + k, j + l);
            if (i1 != p_215478_1_) {
               this.func_215475_b(p_215478_1_, i1, p_215478_3_, p_215478_4_);
            }
         }
      }

   }

   protected int func_215477_a(long p_215477_1_, long p_215477_3_, int p_215477_5_) {
      int i = p_215477_5_;
      ChunkPos chunkpos = new ChunkPos(p_215477_1_);
      int j = chunkpos.x;
      int k = chunkpos.z;

      for(int l = -1; l <= 1; ++l) {
         for(int i1 = -1; i1 <= 1; ++i1) {
            long j1 = ChunkPos.asLong(j + l, k + i1);
            if (j1 == p_215477_1_) {
               j1 = ChunkPos.SENTINEL;
            }

            if (j1 != p_215477_3_) {
               int k1 = this.func_215480_b(j1, p_215477_1_, this.func_215471_c(j1));
               if (i > k1) {
                  i = k1;
               }

               if (i == 0) {
                  return i;
               }
            }
         }
      }

      return i;
   }

   protected int func_215480_b(long p_215480_1_, long p_215480_3_, int p_215480_5_) {
      return p_215480_1_ == ChunkPos.SENTINEL ? this.func_215492_b(p_215480_3_) : p_215480_5_ + 1;
   }

   protected abstract int func_215492_b(long p_215492_1_);

   public void func_215491_b(long p_215491_1_, int p_215491_3_, boolean p_215491_4_) {
      this.func_215469_a(ChunkPos.SENTINEL, p_215491_1_, p_215491_3_, p_215491_4_);
   }
}