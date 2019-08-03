package net.minecraft.world.gen;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;

public class DebugChunkGenerator extends ChunkGenerator<DebugGenerationSettings> {
   private static final List<BlockState> ALL_VALID_STATES = StreamSupport.stream(Registry.BLOCK.spliterator(), false).flatMap((p_199812_0_) -> {
      return p_199812_0_.getStateContainer().getValidStates().stream();
   }).collect(Collectors.toList());
   private static final int GRID_WIDTH = MathHelper.ceil(MathHelper.sqrt((float)ALL_VALID_STATES.size()));
   private static final int GRID_HEIGHT = MathHelper.ceil((float)ALL_VALID_STATES.size() / (float)GRID_WIDTH);
   protected static final BlockState AIR = Blocks.AIR.getDefaultState();
   protected static final BlockState BARRIER = Blocks.BARRIER.getDefaultState();

   public DebugChunkGenerator(IWorld p_i48959_1_, BiomeProvider p_i48959_2_, DebugGenerationSettings p_i48959_3_) {
      super(p_i48959_1_, p_i48959_2_, p_i48959_3_);
   }

   public void generateSurface(IChunk p_222535_1_) {
   }

   public void carve(IChunk p_222538_1_, GenerationStage.Carving p_222538_2_) {
   }

   public int getGroundHeight() {
      return this.world.getSeaLevel() + 1;
   }

   public void decorate(WorldGenRegion region) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
      int i = region.getMainChunkX();
      int j = region.getMainChunkZ();

      for(int k = 0; k < 16; ++k) {
         for(int l = 0; l < 16; ++l) {
            int i1 = (i << 4) + k;
            int j1 = (j << 4) + l;
            region.setBlockState(blockpos$mutableblockpos.setPos(i1, 60, j1), BARRIER, 2);
            BlockState blockstate = getBlockStateFor(i1, j1);
            if (blockstate != null) {
               region.setBlockState(blockpos$mutableblockpos.setPos(i1, 70, j1), blockstate, 2);
            }
         }
      }

   }

   public void func_222537_b(IWorld p_222537_1_, IChunk p_222537_2_) {
   }

   public int func_222529_a(int p_222529_1_, int p_222529_2_, Heightmap.Type p_222529_3_) {
      return 0;
   }

   public static BlockState getBlockStateFor(int p_177461_0_, int p_177461_1_) {
      BlockState blockstate = AIR;
      if (p_177461_0_ > 0 && p_177461_1_ > 0 && p_177461_0_ % 2 != 0 && p_177461_1_ % 2 != 0) {
         p_177461_0_ = p_177461_0_ / 2;
         p_177461_1_ = p_177461_1_ / 2;
         if (p_177461_0_ <= GRID_WIDTH && p_177461_1_ <= GRID_HEIGHT) {
            int i = MathHelper.abs(p_177461_0_ * GRID_WIDTH + p_177461_1_);
            if (i < ALL_VALID_STATES.size()) {
               blockstate = ALL_VALID_STATES.get(i);
            }
         }
      }

      return blockstate;
   }
}