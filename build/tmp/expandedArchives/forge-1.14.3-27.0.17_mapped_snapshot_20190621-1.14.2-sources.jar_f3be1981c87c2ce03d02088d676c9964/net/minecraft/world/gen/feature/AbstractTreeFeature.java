package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.shapes.BitSetVoxelShapePart;
import net.minecraft.util.math.shapes.VoxelShapePart;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldWriter;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.IWorldGenerationBaseReader;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.template.Template;

public abstract class AbstractTreeFeature<T extends IFeatureConfig> extends Feature<T> {
   protected net.minecraftforge.common.IPlantable sapling = (net.minecraftforge.common.IPlantable)net.minecraft.block.Blocks.OAK_SAPLING;

   public AbstractTreeFeature(Function<Dynamic<?>, ? extends T> p_i49920_1_, boolean doBlockNofityOnPlace) {
      super(p_i49920_1_, doBlockNofityOnPlace);
   }

   protected static boolean func_214587_a(IWorldGenerationBaseReader p_214587_0_, BlockPos p_214587_1_) {
      if (!(p_214587_0_ instanceof net.minecraft.world.IWorldReader)) // FORGE: Redirect to state method when possible
      return p_214587_0_.hasBlockState(p_214587_1_, (p_214573_0_) -> {
         Block block = p_214573_0_.getBlock();
         return p_214573_0_.isAir() || p_214573_0_.isIn(BlockTags.LEAVES) || block == Blocks.GRASS_BLOCK || Block.isDirt(block) || block.isIn(BlockTags.LOGS) || block.isIn(BlockTags.SAPLINGS) || block == Blocks.VINE;
      });
      else return p_214587_0_.hasBlockState(p_214587_1_, state -> state.canBeReplacedByLogs((net.minecraft.world.IWorldReader)p_214587_0_, p_214587_1_)); 
   }

   protected static boolean isAir(IWorldGenerationBaseReader p_214574_0_, BlockPos p_214574_1_) {
      if (!(p_214574_0_ instanceof net.minecraft.world.IBlockReader)) // FORGE: Redirect to state method when possible
      return p_214574_0_.hasBlockState(p_214574_1_, BlockState::isAir);
      else return p_214574_0_.hasBlockState(p_214574_1_, state -> state.isAir((net.minecraft.world.IBlockReader)p_214574_0_, p_214574_1_));
   }

   protected static boolean isDirt(IWorldGenerationBaseReader p_214578_0_, BlockPos p_214578_1_) {
      return p_214578_0_.hasBlockState(p_214578_1_, (p_214590_0_) -> {
         return Block.isDirt(p_214590_0_.getBlock());
      });
   }

   protected static boolean isWater(IWorldGenerationBaseReader p_214571_0_, BlockPos p_214571_1_) {
      return p_214571_0_.hasBlockState(p_214571_1_, (p_214583_0_) -> {
         return p_214583_0_.getBlock() == Blocks.WATER;
      });
   }

   protected static boolean isLeaves(IWorldGenerationBaseReader p_214570_0_, BlockPos p_214570_1_) {
      return p_214570_0_.hasBlockState(p_214570_1_, (p_214579_0_) -> {
         return p_214579_0_.isIn(BlockTags.LEAVES);
      });
   }

   protected static boolean isAirOrLeaves(IWorldGenerationBaseReader p_214572_0_, BlockPos p_214572_1_) {
      if (!(p_214572_0_ instanceof net.minecraft.world.IWorldReader)) // FORGE: Redirect to state method when possible
      return p_214572_0_.hasBlockState(p_214572_1_, (p_214581_0_) -> {
         return p_214581_0_.isAir() || p_214581_0_.isIn(BlockTags.LEAVES);
      });
      else return p_214572_0_.hasBlockState(p_214572_1_, state -> state.canBeReplacedByLeaves((net.minecraft.world.IWorldReader)p_214572_0_, p_214572_1_));
   }

   @Deprecated //Forge: moved to isSoil
   protected static boolean isDirtOrGrassBlock(IWorldGenerationBaseReader p_214589_0_, BlockPos p_214589_1_) {
      return p_214589_0_.hasBlockState(p_214589_1_, (p_214582_0_) -> {
         Block block = p_214582_0_.getBlock();
         return Block.isDirt(block) || block == Blocks.GRASS_BLOCK;
      });
   }

   protected static boolean isSoil(IWorldGenerationBaseReader reader, BlockPos pos, net.minecraftforge.common.IPlantable sapling) {
      if (!(reader instanceof net.minecraft.world.IBlockReader) || sapling == null)
         return isDirtOrGrassBlock(reader, pos);
      return reader.hasBlockState(pos, state -> state.canSustainPlant((net.minecraft.world.IBlockReader)reader, pos, Direction.UP, sapling));
   }

   @Deprecated //Forge: moved to isSoilOrFarm
   protected static boolean isDirtOrGrassBlockOrFarmland(IWorldGenerationBaseReader p_214585_0_, BlockPos p_214585_1_) {
      return p_214585_0_.hasBlockState(p_214585_1_, (p_214586_0_) -> {
         Block block = p_214586_0_.getBlock();
         return Block.isDirt(block) || block == Blocks.GRASS_BLOCK || block == Blocks.FARMLAND;
      });
   }

   protected static boolean isSoilOrFarm(IWorldGenerationBaseReader reader, BlockPos pos, net.minecraftforge.common.IPlantable sapling) {
      if (!(reader instanceof net.minecraft.world.IBlockReader) || sapling == null)
         return isDirtOrGrassBlockOrFarmland(reader, pos);
      return reader.hasBlockState(pos, state -> state.canSustainPlant((net.minecraft.world.IBlockReader)reader, pos, Direction.UP, sapling));
   }

   protected static boolean func_214576_j(IWorldGenerationBaseReader p_214576_0_, BlockPos p_214576_1_) {
      return p_214576_0_.hasBlockState(p_214576_1_, (p_214588_0_) -> {
         Material material = p_214588_0_.getMaterial();
         return material == Material.TALL_PLANTS;
      });
   }

   @Deprecated //Forge: moved to setDirtAt
   protected void func_214584_a(IWorldGenerationReader p_214584_1_, BlockPos p_214584_2_) {
      if (!isDirt(p_214584_1_, p_214584_2_)) {
         this.setBlockState(p_214584_1_, p_214584_2_, Blocks.DIRT.getDefaultState());
      }

   }

   protected void setDirtAt(IWorldGenerationReader reader, BlockPos pos, BlockPos origin) {
      if (!(reader instanceof IWorld)) {
         func_214584_a(reader, pos);
         return;
      }
      ((IWorld)reader).getBlockState(pos).onPlantGrow((IWorld)reader, pos, origin);
   }

   protected void setBlockState(IWorldWriter worldIn, BlockPos pos, BlockState state) {
      this.func_208521_b(worldIn, pos, state);
   }

   protected final void setLogState(Set<BlockPos> changedBlocks, IWorldWriter worldIn, BlockPos p_208520_3_, BlockState p_208520_4_, MutableBoundingBox p_208520_5_) {
      this.func_208521_b(worldIn, p_208520_3_, p_208520_4_);
      p_208520_5_.expandTo(new MutableBoundingBox(p_208520_3_, p_208520_3_));
      if (BlockTags.LOGS.contains(p_208520_4_.getBlock())) {
         changedBlocks.add(p_208520_3_.toImmutable());
      }
   }

   private void func_208521_b(IWorldWriter p_208521_1_, BlockPos p_208521_2_, BlockState p_208521_3_) {
      if (this.doBlockNotify) {
         p_208521_1_.setBlockState(p_208521_2_, p_208521_3_, 19);
      } else {
         p_208521_1_.setBlockState(p_208521_2_, p_208521_3_, 18);
      }

   }

   public final boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, T config) {
      Set<BlockPos> set = Sets.newHashSet();
      MutableBoundingBox mutableboundingbox = MutableBoundingBox.getNewBoundingBox();
      boolean flag = this.place(set, worldIn, rand, pos, mutableboundingbox);
      if (mutableboundingbox.minX > mutableboundingbox.maxX) {
         return false;
      } else {
         List<Set<BlockPos>> list = Lists.newArrayList();
         int i = 6;

         for(int j = 0; j < 6; ++j) {
            list.add(Sets.newHashSet());
         }

         VoxelShapePart voxelshapepart = new BitSetVoxelShapePart(mutableboundingbox.getXSize(), mutableboundingbox.getYSize(), mutableboundingbox.getZSize());

         try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
            if (flag && !set.isEmpty()) {
               for(BlockPos blockpos : Lists.newArrayList(set)) {
                  if (mutableboundingbox.isVecInside(blockpos)) {
                     voxelshapepart.setFilled(blockpos.getX() - mutableboundingbox.minX, blockpos.getY() - mutableboundingbox.minY, blockpos.getZ() - mutableboundingbox.minZ, true, true);
                  }

                  for(Direction direction : Direction.values()) {
                     blockpos$pooledmutableblockpos.setPos(blockpos).move(direction);
                     if (!set.contains(blockpos$pooledmutableblockpos)) {
                        BlockState blockstate = worldIn.getBlockState(blockpos$pooledmutableblockpos);
                        if (blockstate.has(BlockStateProperties.DISTANCE_1_7)) {
                           list.get(0).add(blockpos$pooledmutableblockpos.toImmutable());
                           this.func_208521_b(worldIn, blockpos$pooledmutableblockpos, blockstate.with(BlockStateProperties.DISTANCE_1_7, Integer.valueOf(1)));
                           if (mutableboundingbox.isVecInside(blockpos$pooledmutableblockpos)) {
                              voxelshapepart.setFilled(blockpos$pooledmutableblockpos.getX() - mutableboundingbox.minX, blockpos$pooledmutableblockpos.getY() - mutableboundingbox.minY, blockpos$pooledmutableblockpos.getZ() - mutableboundingbox.minZ, true, true);
                           }
                        }
                     }
                  }
               }
            }

            for(int l = 1; l < 6; ++l) {
               Set<BlockPos> set1 = list.get(l - 1);
               Set<BlockPos> set2 = list.get(l);

               for(BlockPos blockpos1 : set1) {
                  if (mutableboundingbox.isVecInside(blockpos1)) {
                     voxelshapepart.setFilled(blockpos1.getX() - mutableboundingbox.minX, blockpos1.getY() - mutableboundingbox.minY, blockpos1.getZ() - mutableboundingbox.minZ, true, true);
                  }

                  for(Direction direction1 : Direction.values()) {
                     blockpos$pooledmutableblockpos.setPos(blockpos1).move(direction1);
                     if (!set1.contains(blockpos$pooledmutableblockpos) && !set2.contains(blockpos$pooledmutableblockpos)) {
                        BlockState blockstate1 = worldIn.getBlockState(blockpos$pooledmutableblockpos);
                        if (blockstate1.has(BlockStateProperties.DISTANCE_1_7)) {
                           int k = blockstate1.get(BlockStateProperties.DISTANCE_1_7);
                           if (k > l + 1) {
                              BlockState blockstate2 = blockstate1.with(BlockStateProperties.DISTANCE_1_7, Integer.valueOf(l + 1));
                              this.func_208521_b(worldIn, blockpos$pooledmutableblockpos, blockstate2);
                              if (mutableboundingbox.isVecInside(blockpos$pooledmutableblockpos)) {
                                 voxelshapepart.setFilled(blockpos$pooledmutableblockpos.getX() - mutableboundingbox.minX, blockpos$pooledmutableblockpos.getY() - mutableboundingbox.minY, blockpos$pooledmutableblockpos.getZ() - mutableboundingbox.minZ, true, true);
                              }

                              set2.add(blockpos$pooledmutableblockpos.toImmutable());
                           }
                        }
                     }
                  }
               }
            }
         }

         Template.func_222857_a(worldIn, 3, voxelshapepart, mutableboundingbox.minX, mutableboundingbox.minY, mutableboundingbox.minZ);
         return flag;
      }
   }

   protected abstract boolean place(Set<BlockPos> changedBlocks, IWorldGenerationReader worldIn, Random rand, BlockPos position, MutableBoundingBox p_208519_5_);

   protected net.minecraftforge.common.IPlantable getSapling() {
      return sapling;
   }

   public AbstractTreeFeature<T> setSapling(net.minecraftforge.common.IPlantable sapling) {
      this.sapling = sapling;
      return this;
   }
}