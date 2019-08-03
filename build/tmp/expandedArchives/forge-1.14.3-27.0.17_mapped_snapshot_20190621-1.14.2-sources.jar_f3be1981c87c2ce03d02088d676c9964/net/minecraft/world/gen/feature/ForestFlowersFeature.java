package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;

public class ForestFlowersFeature extends FlowersFeature {
   private static final Block[] FLOWERS = new Block[]{Blocks.DANDELION, Blocks.POPPY, Blocks.BLUE_ORCHID, Blocks.ALLIUM, Blocks.AZURE_BLUET, Blocks.RED_TULIP, Blocks.ORANGE_TULIP, Blocks.WHITE_TULIP, Blocks.PINK_TULIP, Blocks.OXEYE_DAISY, Blocks.CORNFLOWER, Blocks.LILY_OF_THE_VALLEY};

   public ForestFlowersFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i49875_1_) {
      super(p_i49875_1_);
   }

   public BlockState getRandomFlower(Random p_202355_1_, BlockPos p_202355_2_) {
      double d0 = MathHelper.clamp((1.0D + Biome.INFO_NOISE.getValue((double)p_202355_2_.getX() / 48.0D, (double)p_202355_2_.getZ() / 48.0D)) / 2.0D, 0.0D, 0.9999D);
      Block block = FLOWERS[(int)(d0 * (double)FLOWERS.length)];
      return block == Blocks.BLUE_ORCHID ? Blocks.POPPY.getDefaultState() : block.getDefaultState();
   }
}