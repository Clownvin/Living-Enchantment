package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.gen.Heightmap;

public class Region implements IWorldReader {
   protected final int chunkX;
   protected final int chunkZ;
   protected final IChunk[][] chunks;
   protected boolean empty;
   protected final World world;

   public Region(World p_i50004_1_, BlockPos p_i50004_2_, BlockPos p_i50004_3_) {
      this.world = p_i50004_1_;
      this.chunkX = p_i50004_2_.getX() >> 4;
      this.chunkZ = p_i50004_2_.getZ() >> 4;
      int i = p_i50004_3_.getX() >> 4;
      int j = p_i50004_3_.getZ() >> 4;
      this.chunks = new IChunk[i - this.chunkX + 1][j - this.chunkZ + 1];
      this.empty = true;

      for(int k = this.chunkX; k <= i; ++k) {
         for(int l = this.chunkZ; l <= j; ++l) {
            this.chunks[k - this.chunkX][l - this.chunkZ] = p_i50004_1_.getChunk(k, l, ChunkStatus.FULL, false);
         }
      }

      for(int i1 = p_i50004_2_.getX() >> 4; i1 <= p_i50004_3_.getX() >> 4; ++i1) {
         for(int j1 = p_i50004_2_.getZ() >> 4; j1 <= p_i50004_3_.getZ() >> 4; ++j1) {
            IChunk ichunk = this.chunks[i1 - this.chunkX][j1 - this.chunkZ];
            if (ichunk != null && !ichunk.isEmptyBetween(p_i50004_2_.getY(), p_i50004_3_.getY())) {
               this.empty = false;
               return;
            }
         }
      }

   }

   public int getLightSubtracted(BlockPos pos, int amount) {
      return this.world.getLightSubtracted(pos, amount);
   }

   @Nullable
   public IChunk getChunk(int x, int z, ChunkStatus requiredStatus, boolean nonnull) {
      int i = x - this.chunkX;
      int j = z - this.chunkZ;
      if (i >= 0 && i < this.chunks.length && j >= 0 && j < this.chunks[i].length) {
         IChunk ichunk = this.chunks[i][j];
         return (IChunk)(ichunk != null ? ichunk : new EmptyChunk(this.world, new ChunkPos(x, z)));
      } else {
         return new EmptyChunk(this.world, new ChunkPos(x, z));
      }
   }

   public boolean chunkExists(int chunkX, int chunkZ) {
      int i = chunkX - this.chunkX;
      int j = chunkZ - this.chunkZ;
      return i >= 0 && i < this.chunks.length && j >= 0 && j < this.chunks[i].length;
   }

   public BlockPos getHeight(Heightmap.Type heightmapType, BlockPos pos) {
      return this.world.getHeight(heightmapType, pos);
   }

   public int getHeight(Heightmap.Type heightmapType, int x, int z) {
      return this.world.getHeight(heightmapType, x, z);
   }

   public int getSkylightSubtracted() {
      return this.world.getSkylightSubtracted();
   }

   public WorldBorder getWorldBorder() {
      return this.world.getWorldBorder();
   }

   public boolean checkNoEntityCollision(@Nullable Entity entityIn, VoxelShape shape) {
      return true;
   }

   public boolean isRemote() {
      return false;
   }

   public int getSeaLevel() {
      return this.world.getSeaLevel();
   }

   public Dimension getDimension() {
      return this.world.getDimension();
   }

   @Nullable
   public TileEntity getTileEntity(BlockPos pos) {
      IChunk ichunk = this.getChunk(pos);
      return ichunk.getTileEntity(pos);
   }

   public BlockState getBlockState(BlockPos pos) {
      if (World.isOutsideBuildHeight(pos)) {
         return Blocks.AIR.getDefaultState();
      } else {
         IChunk ichunk = this.getChunk(pos);
         return ichunk.getBlockState(pos);
      }
   }

   public IFluidState getFluidState(BlockPos pos) {
      if (World.isOutsideBuildHeight(pos)) {
         return Fluids.EMPTY.getDefaultState();
      } else {
         IChunk ichunk = this.getChunk(pos);
         return ichunk.getFluidState(pos);
      }
   }

   public Biome getBiome(BlockPos pos) {
      IChunk ichunk = this.getChunk(pos);
      return ichunk.getBiome(pos);
   }

   public int getLightFor(LightType type, BlockPos pos) {
      return this.world.getLightFor(type, pos);
   }
}