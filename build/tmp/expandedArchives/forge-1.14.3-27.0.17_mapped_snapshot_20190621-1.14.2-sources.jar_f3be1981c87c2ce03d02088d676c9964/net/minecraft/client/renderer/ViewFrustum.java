package net.minecraft.client.renderer;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.chunk.ChunkRender;
import net.minecraft.client.renderer.chunk.IChunkRendererFactory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ViewFrustum {
   protected final WorldRenderer renderGlobal;
   protected final World world;
   protected int countChunksY;
   protected int countChunksX;
   protected int countChunksZ;
   public ChunkRender[] renderChunks;

   public ViewFrustum(World worldIn, int renderDistanceChunks, WorldRenderer renderGlobalIn, IChunkRendererFactory renderChunkFactory) {
      this.renderGlobal = renderGlobalIn;
      this.world = worldIn;
      this.setCountChunksXYZ(renderDistanceChunks);
      this.createRenderChunks(renderChunkFactory);
   }

   protected void createRenderChunks(IChunkRendererFactory renderChunkFactory) {
      int i = this.countChunksX * this.countChunksY * this.countChunksZ;
      this.renderChunks = new ChunkRender[i];

      for(int j = 0; j < this.countChunksX; ++j) {
         for(int k = 0; k < this.countChunksY; ++k) {
            for(int l = 0; l < this.countChunksZ; ++l) {
               int i1 = this.getIndex(j, k, l);
               this.renderChunks[i1] = renderChunkFactory.create(this.world, this.renderGlobal);
               this.renderChunks[i1].setPosition(j * 16, k * 16, l * 16);
            }
         }
      }

   }

   public void deleteGlResources() {
      for(ChunkRender chunkrender : this.renderChunks) {
         chunkrender.deleteGlResources();
      }

   }

   private int getIndex(int x, int y, int z) {
      return (z * this.countChunksY + y) * this.countChunksX + x;
   }

   protected void setCountChunksXYZ(int renderDistanceChunks) {
      int i = renderDistanceChunks * 2 + 1;
      this.countChunksX = i;
      this.countChunksY = 16;
      this.countChunksZ = i;
   }

   public void updateChunkPositions(double viewEntityX, double viewEntityZ) {
      int i = MathHelper.floor(viewEntityX) - 8;
      int j = MathHelper.floor(viewEntityZ) - 8;
      int k = this.countChunksX * 16;

      for(int l = 0; l < this.countChunksX; ++l) {
         int i1 = this.getBaseCoordinate(i, k, l);

         for(int j1 = 0; j1 < this.countChunksZ; ++j1) {
            int k1 = this.getBaseCoordinate(j, k, j1);

            for(int l1 = 0; l1 < this.countChunksY; ++l1) {
               int i2 = l1 * 16;
               ChunkRender chunkrender = this.renderChunks[this.getIndex(l, l1, j1)];
               chunkrender.setPosition(i1, i2, k1);
            }
         }
      }

   }

   private int getBaseCoordinate(int midBlocksIn, int countBlocksIn, int chunksIn) {
      int i = chunksIn * 16;
      int j = i - midBlocksIn + countBlocksIn / 2;
      if (j < 0) {
         j -= countBlocksIn - 1;
      }

      return i - j / countBlocksIn * countBlocksIn;
   }

   public void markForRerender(int sectionX, int sectionY, int sectionZ, boolean rerenderOnMainThread) {
      int i = Math.floorMod(sectionX, this.countChunksX);
      int j = Math.floorMod(sectionY, this.countChunksY);
      int k = Math.floorMod(sectionZ, this.countChunksZ);
      ChunkRender chunkrender = this.renderChunks[this.getIndex(i, j, k)];
      chunkrender.setNeedsUpdate(rerenderOnMainThread);
   }

   @Nullable
   protected ChunkRender getRenderChunk(BlockPos pos) {
      int i = MathHelper.intFloorDiv(pos.getX(), 16);
      int j = MathHelper.intFloorDiv(pos.getY(), 16);
      int k = MathHelper.intFloorDiv(pos.getZ(), 16);
      if (j >= 0 && j < this.countChunksY) {
         i = MathHelper.normalizeAngle(i, this.countChunksX);
         k = MathHelper.normalizeAngle(k, this.countChunksZ);
         return this.renderChunks[this.getIndex(i, j, k)];
      } else {
         return null;
      }
   }
}