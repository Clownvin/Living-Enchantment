package net.minecraft.fluid;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EmptyFluid extends Fluid {
   /**
    * Gets the render layer this block will render on. SOLID for solid blocks, CUTOUT or CUTOUT_MIPPED for on-off
    * transparency (glass, reeds), TRANSLUCENT for fully blended transparency (stained glass)
    */
   @OnlyIn(Dist.CLIENT)
   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.SOLID;
   }

   public Item getFilledBucket() {
      return Items.AIR;
   }

   public boolean func_215665_a(IFluidState p_215665_1_, IBlockReader p_215665_2_, BlockPos p_215665_3_, Fluid p_215665_4_, Direction p_215665_5_) {
      return true;
   }

   public Vec3d func_215663_a(IBlockReader p_215663_1_, BlockPos p_215663_2_, IFluidState p_215663_3_) {
      return Vec3d.ZERO;
   }

   public int getTickRate(IWorldReader p_205569_1_) {
      return 0;
   }

   protected boolean isEmpty() {
      return true;
   }

   protected float getExplosionResistance() {
      return 0.0F;
   }

   public float func_215662_a(IFluidState p_215662_1_, IBlockReader p_215662_2_, BlockPos p_215662_3_) {
      return 0.0F;
   }

   public float func_223407_a(IFluidState p_223407_1_) {
      return 0.0F;
   }

   protected BlockState getBlockState(IFluidState state) {
      return Blocks.AIR.getDefaultState();
   }

   public boolean isSource(IFluidState state) {
      return false;
   }

   public int getLevel(IFluidState p_207192_1_) {
      return 0;
   }

   public VoxelShape func_215664_b(IFluidState p_215664_1_, IBlockReader p_215664_2_, BlockPos p_215664_3_) {
      return VoxelShapes.empty();
   }
}