package net.minecraft.util.math.shapes;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class EntitySelectionContext implements ISelectionContext {
   protected static final ISelectionContext DUMMY = new EntitySelectionContext(false, -Double.MAX_VALUE, Items.AIR) {
      public boolean func_216378_a(VoxelShape p_216378_1_, BlockPos p_216378_2_, boolean p_216378_3_) {
         return p_216378_3_;
      }
   };
   private final boolean field_216380_b;
   private final double field_216381_c;
   private final Item field_216382_d;

   protected EntitySelectionContext(boolean p_i51181_1_, double p_i51181_2_, Item p_i51181_4_) {
      this(null, p_i51181_1_, p_i51181_2_, p_i51181_4_);
   }

   protected EntitySelectionContext(@javax.annotation.Nullable Entity entityIn, boolean p_i51181_1_, double p_i51181_2_, Item p_i51181_4_) {
      this.entity = entityIn;
      this.field_216380_b = p_i51181_1_;
      this.field_216381_c = p_i51181_2_;
      this.field_216382_d = p_i51181_4_;
   }

   @Deprecated
   protected EntitySelectionContext(Entity p_i51182_1_) {
      this(p_i51182_1_, p_i51182_1_.isSneaking(), p_i51182_1_.getBoundingBox().minY, p_i51182_1_ instanceof LivingEntity ? ((LivingEntity)p_i51182_1_).getHeldItemMainhand().getItem() : Items.AIR);
   }

   public boolean hasItem(Item p_216375_1_) {
      return this.field_216382_d == p_216375_1_;
   }

   public boolean isSneaking() {
      return this.field_216380_b;
   }

   public boolean func_216378_a(VoxelShape p_216378_1_, BlockPos p_216378_2_, boolean p_216378_3_) {
      return this.field_216381_c > (double)p_216378_2_.getY() + p_216378_1_.getEnd(Direction.Axis.Y) - (double)1.0E-5F;
   }

   private final @javax.annotation.Nullable Entity entity;

   @Override
   public @javax.annotation.Nullable Entity getEntity() {
      return entity;
   }
}