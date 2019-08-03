package net.minecraft.world.gen.feature.template;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorldReader;

public abstract class StructureProcessor {
   @Nullable
   public abstract Template.BlockInfo process(IWorldReader p_215194_1_, BlockPos p_215194_2_, Template.BlockInfo p_215194_3_, Template.BlockInfo p_215194_4_, PlacementSettings p_215194_5_);

   protected abstract IStructureProcessorType getType();

   protected abstract <T> Dynamic<T> serialize0(DynamicOps<T> p_215193_1_);

   public <T> Dynamic<T> serialize(DynamicOps<T> p_215191_1_) {
      return new Dynamic<>(p_215191_1_, p_215191_1_.mergeInto(this.serialize0(p_215191_1_).getValue(), p_215191_1_.createString("processor_type"), p_215191_1_.createString(Registry.STRUCTURE_PROCESSOR.getKey(this.getType()).toString())));
   }
}