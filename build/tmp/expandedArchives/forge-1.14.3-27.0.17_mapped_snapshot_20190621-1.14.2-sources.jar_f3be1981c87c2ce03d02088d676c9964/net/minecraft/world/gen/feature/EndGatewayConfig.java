package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Optional;
import net.minecraft.util.math.BlockPos;

public class EndGatewayConfig implements IFeatureConfig {
   private final Optional<BlockPos> exit;
   private final boolean exact;

   private EndGatewayConfig(Optional<BlockPos> exit, boolean exact) {
      this.exit = exit;
      this.exact = exact;
   }

   public static EndGatewayConfig func_214702_a(BlockPos p_214702_0_, boolean p_214702_1_) {
      return new EndGatewayConfig(Optional.of(p_214702_0_), p_214702_1_);
   }

   public static EndGatewayConfig func_214698_a() {
      return new EndGatewayConfig(Optional.empty(), false);
   }

   public Optional<BlockPos> func_214700_b() {
      return this.exit;
   }

   public boolean func_214701_c() {
      return this.exact;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214634_1_) {
      return new Dynamic<>(p_214634_1_, (T)this.exit.map((p_214703_2_) -> {
         return p_214634_1_.createMap(ImmutableMap.of(p_214634_1_.createString("exit_x"), p_214634_1_.createInt(p_214703_2_.getX()), p_214634_1_.createString("exit_y"), p_214634_1_.createInt(p_214703_2_.getY()), p_214634_1_.createString("exit_z"), p_214634_1_.createInt(p_214703_2_.getZ()), p_214634_1_.createString("exact"), p_214634_1_.createBoolean(this.exact)));
      }).orElse(p_214634_1_.emptyMap()));
   }

   public static <T> EndGatewayConfig deserialize(Dynamic<T> p_214697_0_) {
      Optional<BlockPos> optional = p_214697_0_.get("exit_x").asNumber().flatMap((p_214696_1_) -> {
         return p_214697_0_.get("exit_y").asNumber().flatMap((p_214695_2_) -> {
            return p_214697_0_.get("exit_z").asNumber().map((p_214699_2_) -> {
               return new BlockPos(p_214696_1_.intValue(), p_214695_2_.intValue(), p_214699_2_.intValue());
            });
         });
      });
      boolean flag = p_214697_0_.get("exact").asBoolean(false);
      return new EndGatewayConfig(optional, flag);
   }
}