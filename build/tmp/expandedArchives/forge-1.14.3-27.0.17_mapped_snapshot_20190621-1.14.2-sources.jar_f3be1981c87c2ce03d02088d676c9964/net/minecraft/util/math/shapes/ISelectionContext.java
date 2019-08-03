package net.minecraft.util.math.shapes;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;

public interface ISelectionContext extends net.minecraftforge.common.extensions.IForgeSelectionContext {
   static ISelectionContext dummy() {
      return EntitySelectionContext.DUMMY;
   }

   static ISelectionContext forEntity(Entity p_216374_0_) {
      return new EntitySelectionContext(p_216374_0_);
   }

   boolean isSneaking();

   boolean func_216378_a(VoxelShape p_216378_1_, BlockPos p_216378_2_, boolean p_216378_3_);

   boolean hasItem(Item p_216375_1_);
}