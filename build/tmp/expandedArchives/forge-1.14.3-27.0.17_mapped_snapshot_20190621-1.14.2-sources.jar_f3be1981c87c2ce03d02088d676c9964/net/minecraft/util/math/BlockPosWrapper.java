package net.minecraft.util.math;

import net.minecraft.entity.LivingEntity;

public class BlockPosWrapper implements IPosWrapper {
   private final BlockPos field_220612_a;
   private final Vec3d field_220613_b;

   public BlockPosWrapper(BlockPos p_i50371_1_) {
      this.field_220612_a = p_i50371_1_;
      this.field_220613_b = new Vec3d((double)p_i50371_1_.getX() + 0.5D, (double)p_i50371_1_.getY() + 0.5D, (double)p_i50371_1_.getZ() + 0.5D);
   }

   public BlockPos func_220608_a() {
      return this.field_220612_a;
   }

   public Vec3d func_220609_b() {
      return this.field_220613_b;
   }

   public boolean func_220610_a(LivingEntity p_220610_1_) {
      return true;
   }
}