package net.minecraft.world.gen.feature.structure;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.storage.loot.LootTables;

public class BuriedTreasure {
   public static class Piece extends StructurePiece {
      public Piece(BlockPos p_i48882_1_) {
         super(IStructurePieceType.BTP, 0);
         this.boundingBox = new MutableBoundingBox(p_i48882_1_.getX(), p_i48882_1_.getY(), p_i48882_1_.getZ(), p_i48882_1_.getX(), p_i48882_1_.getY(), p_i48882_1_.getZ());
      }

      public Piece(TemplateManager p_i50677_1_, CompoundNBT p_i50677_2_) {
         super(IStructurePieceType.BTP, p_i50677_2_);
      }

      /**
       * (abstract) Helper method to read subclass data from NBT
       */
      protected void readAdditional(CompoundNBT tagCompound) {
      }

      /**
       * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at
       * the end, it adds Fences...
       */
      public boolean addComponentParts(IWorld worldIn, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos p_74875_4_) {
         int i = worldIn.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, this.boundingBox.minX, this.boundingBox.minZ);
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(this.boundingBox.minX, i, this.boundingBox.minZ);

         while(blockpos$mutableblockpos.getY() > 0) {
            BlockState blockstate = worldIn.getBlockState(blockpos$mutableblockpos);
            BlockState blockstate1 = worldIn.getBlockState(blockpos$mutableblockpos.down());
            if (blockstate1 == Blocks.SANDSTONE.getDefaultState() || blockstate1 == Blocks.STONE.getDefaultState() || blockstate1 == Blocks.ANDESITE.getDefaultState() || blockstate1 == Blocks.GRANITE.getDefaultState() || blockstate1 == Blocks.DIORITE.getDefaultState()) {
               BlockState blockstate2 = !blockstate.isAir() && !this.func_204295_a(blockstate) ? blockstate : Blocks.SAND.getDefaultState();

               for(Direction direction : Direction.values()) {
                  BlockPos blockpos = blockpos$mutableblockpos.offset(direction);
                  BlockState blockstate3 = worldIn.getBlockState(blockpos);
                  if (blockstate3.isAir() || this.func_204295_a(blockstate3)) {
                     BlockPos blockpos1 = blockpos.down();
                     BlockState blockstate4 = worldIn.getBlockState(blockpos1);
                     if ((blockstate4.isAir() || this.func_204295_a(blockstate4)) && direction != Direction.UP) {
                        worldIn.setBlockState(blockpos, blockstate1, 3);
                     } else {
                        worldIn.setBlockState(blockpos, blockstate2, 3);
                     }
                  }
               }

               this.boundingBox = new MutableBoundingBox(blockpos$mutableblockpos.getX(), blockpos$mutableblockpos.getY(), blockpos$mutableblockpos.getZ(), blockpos$mutableblockpos.getX(), blockpos$mutableblockpos.getY(), blockpos$mutableblockpos.getZ());
               return this.generateChest(worldIn, structureBoundingBoxIn, randomIn, blockpos$mutableblockpos, LootTables.CHESTS_BURIED_TREASURE, (BlockState)null);
            }

            blockpos$mutableblockpos.move(0, -1, 0);
         }

         return false;
      }

      private boolean func_204295_a(BlockState p_204295_1_) {
         return p_204295_1_ == Blocks.WATER.getDefaultState() || p_204295_1_ == Blocks.LAVA.getDefaultState();
      }
   }
}