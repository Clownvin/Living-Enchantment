package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;

public class EndSpikeFeatureConfig implements IFeatureConfig {
   private final boolean crystalInvulnerable;
   private final List<EndSpikeFeature.EndSpike> spikes;
   @Nullable
   private final BlockPos crystalBeamTarget;

   public EndSpikeFeatureConfig(boolean crystalInvulnerable, List<EndSpikeFeature.EndSpike> spikes, @Nullable BlockPos crystalBeamTarget) {
      this.crystalInvulnerable = crystalInvulnerable;
      this.spikes = spikes;
      this.crystalBeamTarget = crystalBeamTarget;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> p_214634_1_) {
      return new Dynamic<>(p_214634_1_, p_214634_1_.createMap(ImmutableMap.of(p_214634_1_.createString("crystalInvulnerable"), p_214634_1_.createBoolean(this.crystalInvulnerable), p_214634_1_.createString("spikes"), p_214634_1_.createList(this.spikes.stream().map((p_214670_1_) -> {
         return p_214670_1_.func_214749_a(p_214634_1_).getValue();
      })), p_214634_1_.createString("crystalBeamTarget"), (T)(this.crystalBeamTarget == null ? p_214634_1_.createList(Stream.empty()) : p_214634_1_.createList(IntStream.of(this.crystalBeamTarget.getX(), this.crystalBeamTarget.getY(), this.crystalBeamTarget.getZ()).mapToObj(p_214634_1_::createInt))))));
   }

   public static <T> EndSpikeFeatureConfig deserialize(Dynamic<T> p_214673_0_) {
      List<EndSpikeFeature.EndSpike> list = p_214673_0_.get("spikes").asList(EndSpikeFeature.EndSpike::func_214747_a);
      List<Integer> list1 = p_214673_0_.get("crystalBeamTarget").asList((p_214672_0_) -> {
         return p_214672_0_.asInt(0);
      });
      BlockPos blockpos;
      if (list1.size() == 3) {
         blockpos = new BlockPos(list1.get(0), list1.get(1), list1.get(2));
      } else {
         blockpos = null;
      }

      return new EndSpikeFeatureConfig(p_214673_0_.get("crystalInvulnerable").asBoolean(false), list, blockpos);
   }

   public boolean func_214669_a() {
      return this.crystalInvulnerable;
   }

   public List<EndSpikeFeature.EndSpike> func_214671_b() {
      return this.spikes;
   }

   @Nullable
   public BlockPos func_214668_c() {
      return this.crystalBeamTarget;
   }
}