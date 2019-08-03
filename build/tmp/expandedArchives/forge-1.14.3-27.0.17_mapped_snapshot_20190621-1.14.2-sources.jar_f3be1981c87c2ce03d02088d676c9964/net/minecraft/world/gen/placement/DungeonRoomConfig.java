package net.minecraft.world.gen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class DungeonRoomConfig implements IPlacementConfig {
   public final int chance;

   public DungeonRoomConfig(int chance) {
      this.chance = chance;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214719_1_) {
      return new Dynamic<>(p_214719_1_, p_214719_1_.createMap(ImmutableMap.of(p_214719_1_.createString("chance"), p_214719_1_.createInt(this.chance))));
   }

   public static DungeonRoomConfig deserialize(Dynamic<?> p_214731_0_) {
      int i = p_214731_0_.get("chance").asInt(0);
      return new DungeonRoomConfig(i);
   }
}