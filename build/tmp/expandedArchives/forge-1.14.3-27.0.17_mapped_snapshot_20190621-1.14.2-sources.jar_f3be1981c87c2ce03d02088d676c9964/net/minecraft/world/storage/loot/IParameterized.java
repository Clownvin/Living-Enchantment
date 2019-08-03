package net.minecraft.world.storage.loot;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.util.ResourceLocation;

public interface IParameterized {
   default Set<LootParameter<?>> getRequiredParameters() {
      return ImmutableSet.of();
   }

   default void func_215856_a(ValidationResults p_215856_1_, Function<ResourceLocation, LootTable> p_215856_2_, Set<ResourceLocation> p_215856_3_, LootParameterSet p_215856_4_) {
      p_215856_4_.func_216274_a(p_215856_1_, this);
   }
}