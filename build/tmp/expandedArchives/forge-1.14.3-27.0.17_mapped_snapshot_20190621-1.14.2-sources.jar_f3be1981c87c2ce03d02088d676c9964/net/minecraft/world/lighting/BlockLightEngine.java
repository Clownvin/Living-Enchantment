package net.minecraft.world.lighting;

import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.IChunkLightProvider;
import net.minecraft.world.chunk.NibbleArray;

public final class BlockLightEngine extends LightEngine<BlockLightStorage.StorageMap, BlockLightStorage> {
   private static final Direction[] DIRECTIONS = Direction.values();
   private final BlockPos.MutableBlockPos scratchPos = new BlockPos.MutableBlockPos();

   public BlockLightEngine(IChunkLightProvider p_i51301_1_) {
      super(p_i51301_1_, LightType.BLOCK, new BlockLightStorage(p_i51301_1_));
   }

   private int getLightValue(long worldPos) {
      int i = BlockPos.unpackX(worldPos);
      int j = BlockPos.unpackY(worldPos);
      int k = BlockPos.unpackZ(worldPos);
      IBlockReader iblockreader = this.chunkProvider.getChunkForLight(i >> 4, k >> 4);
      return iblockreader != null ? iblockreader.getLightValue(this.scratchPos.setPos(i, j, k)) : 0;
   }

   protected int func_215480_b(long p_215480_1_, long p_215480_3_, int p_215480_5_) {
      if (p_215480_3_ == Long.MAX_VALUE) {
         return 15;
      } else if (p_215480_1_ == Long.MAX_VALUE) {
         return p_215480_5_ + 15 - this.getLightValue(p_215480_3_);
      } else if (p_215480_5_ >= 15) {
         return p_215480_5_;
      } else {
         int i = Integer.signum(BlockPos.unpackX(p_215480_3_) - BlockPos.unpackX(p_215480_1_));
         int j = Integer.signum(BlockPos.unpackY(p_215480_3_) - BlockPos.unpackY(p_215480_1_));
         int k = Integer.signum(BlockPos.unpackZ(p_215480_3_) - BlockPos.unpackZ(p_215480_1_));
         Direction direction = Direction.func_218383_a(i, j, k);
         if (direction == null) {
            return 15;
         } else {
            AtomicInteger atomicinteger = new AtomicInteger();
            BlockState blockstate = this.func_223406_a(p_215480_3_, atomicinteger);
            if (atomicinteger.get() >= 15) {
               return 15;
            } else {
               BlockState blockstate1 = this.func_223406_a(p_215480_1_, (AtomicInteger)null);
               VoxelShape voxelshape = this.func_223405_a(blockstate1, p_215480_1_, direction);
               VoxelShape voxelshape1 = this.func_223405_a(blockstate, p_215480_3_, direction.getOpposite());
               return VoxelShapes.func_223416_b(voxelshape, voxelshape1) ? 15 : p_215480_5_ + Math.max(1, atomicinteger.get());
            }
         }
      }
   }

   protected void func_215478_a(long p_215478_1_, int p_215478_3_, boolean p_215478_4_) {
      long i = SectionPos.worldToSection(p_215478_1_);

      for(Direction direction : DIRECTIONS) {
         long j = BlockPos.offset(p_215478_1_, direction);
         long k = SectionPos.worldToSection(j);
         if (i == k || this.storage.func_215518_g(k)) {
            this.func_215475_b(p_215478_1_, j, p_215478_3_, p_215478_4_);
         }
      }

   }

   protected int func_215477_a(long p_215477_1_, long p_215477_3_, int p_215477_5_) {
      int i = p_215477_5_;
      if (Long.MAX_VALUE != p_215477_3_) {
         int j = this.func_215480_b(Long.MAX_VALUE, p_215477_1_, 0);
         if (p_215477_5_ > j) {
            i = j;
         }

         if (i == 0) {
            return i;
         }
      }

      long j1 = SectionPos.worldToSection(p_215477_1_);
      NibbleArray nibblearray = this.storage.func_215520_a(j1, true);

      for(Direction direction : DIRECTIONS) {
         long k = BlockPos.offset(p_215477_1_, direction);
         if (k != p_215477_3_) {
            long l = SectionPos.worldToSection(k);
            NibbleArray nibblearray1;
            if (j1 == l) {
               nibblearray1 = nibblearray;
            } else {
               nibblearray1 = this.storage.func_215520_a(l, true);
            }

            if (nibblearray1 != null) {
               int i1 = this.func_215480_b(k, p_215477_1_, this.func_215622_a(nibblearray1, k));
               if (i > i1) {
                  i = i1;
               }

               if (i == 0) {
                  return i;
               }
            }
         }
      }

      return i;
   }

   public void func_215623_a(BlockPos p_215623_1_, int p_215623_2_) {
      this.storage.func_215532_c();
      this.func_215469_a(Long.MAX_VALUE, p_215623_1_.toLong(), 15 - p_215623_2_, true);
   }
}