package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowyDirtBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;

public class IceAndSnowFeature extends Feature<NoFeatureConfig> {
   public IceAndSnowFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51435_1_) {
      super(p_i51435_1_);
   }

   public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      BlockPos.MutableBlockPos blockpos$mutableblockpos1 = new BlockPos.MutableBlockPos();

      for(int i = 0; i < 16; ++i) {
         for(int j = 0; j < 16; ++j) {
            int k = pos.getX() + i;
            int l = pos.getZ() + j;
            int i1 = worldIn.getHeight(Heightmap.Type.MOTION_BLOCKING, k, l);
            blockpos$mutableblockpos.setPos(k, i1, l);
            blockpos$mutableblockpos1.setPos(blockpos$mutableblockpos).move(Direction.DOWN, 1);
            Biome biome = worldIn.getBiome(blockpos$mutableblockpos);
            if (biome.doesWaterFreeze(worldIn, blockpos$mutableblockpos1, false)) {
               worldIn.setBlockState(blockpos$mutableblockpos1, Blocks.ICE.getDefaultState(), 2);
            }

            if (biome.doesSnowGenerate(worldIn, blockpos$mutableblockpos)) {
               worldIn.setBlockState(blockpos$mutableblockpos, Blocks.SNOW.getDefaultState(), 2);
               BlockState blockstate = worldIn.getBlockState(blockpos$mutableblockpos1);
               if (blockstate.has(SnowyDirtBlock.SNOWY)) {
                  worldIn.setBlockState(blockpos$mutableblockpos1, blockstate.with(SnowyDirtBlock.SNOWY, Boolean.valueOf(true)), 2);
               }
            }
         }
      }

      return true;
   }
}