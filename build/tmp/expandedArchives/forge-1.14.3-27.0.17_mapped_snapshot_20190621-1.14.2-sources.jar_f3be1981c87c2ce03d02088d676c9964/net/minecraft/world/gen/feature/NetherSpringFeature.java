package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class NetherSpringFeature extends Feature<HellLavaConfig> {
   private static final BlockState NETHERRACK = Blocks.NETHERRACK.getDefaultState();

   public NetherSpringFeature(Function<Dynamic<?>, ? extends HellLavaConfig> p_i51475_1_) {
      super(p_i51475_1_);
   }

   public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, HellLavaConfig config) {
      if (worldIn.getBlockState(pos.up()) != NETHERRACK) {
         return false;
      } else if (!worldIn.getBlockState(pos).isAir(worldIn, pos) && worldIn.getBlockState(pos) != NETHERRACK) {
         return false;
      } else {
         int i = 0;
         if (worldIn.getBlockState(pos.west()) == NETHERRACK) {
            ++i;
         }

         if (worldIn.getBlockState(pos.east()) == NETHERRACK) {
            ++i;
         }

         if (worldIn.getBlockState(pos.north()) == NETHERRACK) {
            ++i;
         }

         if (worldIn.getBlockState(pos.south()) == NETHERRACK) {
            ++i;
         }

         if (worldIn.getBlockState(pos.down()) == NETHERRACK) {
            ++i;
         }

         int j = 0;
         if (worldIn.isAirBlock(pos.west())) {
            ++j;
         }

         if (worldIn.isAirBlock(pos.east())) {
            ++j;
         }

         if (worldIn.isAirBlock(pos.north())) {
            ++j;
         }

         if (worldIn.isAirBlock(pos.south())) {
            ++j;
         }

         if (worldIn.isAirBlock(pos.down())) {
            ++j;
         }

         if (!config.insideRock && i == 4 && j == 1 || i == 5) {
            worldIn.setBlockState(pos, Blocks.LAVA.getDefaultState(), 2);
            worldIn.getPendingFluidTicks().scheduleTick(pos, Fluids.LAVA, 0);
         }

         return true;
      }
   }
}