package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.SmokerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SmokerBlock extends AbstractFurnaceBlock {
   protected SmokerBlock(Block.Properties p_i49973_1_) {
      super(p_i49973_1_);
   }

   public TileEntity createNewTileEntity(IBlockReader worldIn) {
      return new SmokerTileEntity();
   }

   protected void func_220089_a(World p_220089_1_, BlockPos p_220089_2_, PlayerEntity p_220089_3_) {
      TileEntity tileentity = p_220089_1_.getTileEntity(p_220089_2_);
      if (tileentity instanceof SmokerTileEntity) {
         p_220089_3_.openContainer((INamedContainerProvider)tileentity);
         p_220089_3_.addStat(Stats.INTERACT_WITH_SMOKER);
      }

   }

   /**
    * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
    * this method is unrelated to {@link randomTick} and {@link #needsRandomTick}, and will always be called regardless
    * of whether the block can receive random update ticks
    */
   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
      if (stateIn.get(field_220091_b)) {
         double d0 = (double)pos.getX() + 0.5D;
         double d1 = (double)pos.getY();
         double d2 = (double)pos.getZ() + 0.5D;
         if (rand.nextDouble() < 0.1D) {
            worldIn.playSound(d0, d1, d2, SoundEvents.BLOCK_SMOKER_SMOKE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
         }

         worldIn.addParticle(ParticleTypes.SMOKE, d0, d1 + 1.1D, d2, 0.0D, 0.0D, 0.0D);
      }
   }
}