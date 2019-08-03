package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.storage.loot.LootTables;

public class BonusChestFeature extends Feature<NoFeatureConfig> {
   public BonusChestFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i49911_1_) {
      super(p_i49911_1_);
   }

   public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config) {
      for(BlockState blockstate = worldIn.getBlockState(pos); (blockstate.isAir(worldIn, pos) || blockstate.isIn(BlockTags.LEAVES)) && pos.getY() > 1; blockstate = worldIn.getBlockState(pos)) {
         pos = pos.down();
      }

      if (pos.getY() < 1) {
         return false;
      } else {
         pos = pos.up();

         for(int i = 0; i < 4; ++i) {
            BlockPos blockpos = pos.add(rand.nextInt(4) - rand.nextInt(4), rand.nextInt(3) - rand.nextInt(3), rand.nextInt(4) - rand.nextInt(4));
            if (worldIn.isAirBlock(blockpos)) {
               worldIn.setBlockState(blockpos, Blocks.CHEST.getDefaultState(), 2);
               LockableLootTileEntity.setLootTable(worldIn, rand, blockpos, LootTables.CHESTS_SPAWN_BONUS_CHEST);
               BlockState blockstate1 = Blocks.TORCH.getDefaultState();

               for(Direction direction : Direction.Plane.HORIZONTAL) {
                  BlockPos blockpos1 = blockpos.offset(direction);
                  if (blockstate1.isValidPosition(worldIn, blockpos1)) {
                     worldIn.setBlockState(blockpos1, blockstate1, 2);
                  }
               }

               return true;
            }
         }

         return false;
      }
   }
}