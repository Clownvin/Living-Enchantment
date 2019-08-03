package net.minecraft.world.biome;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.EndSpikeFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class TheEndBiome extends Biome {
   public TheEndBiome() {
      super((new Biome.Builder()).surfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.END_STONE_CONFIG).precipitation(Biome.RainType.NONE).category(Biome.Category.THEEND).depth(0.1F).scale(0.2F).temperature(0.5F).downfall(0.5F).waterColor(4159204).waterFogColor(329011).parent((String)null));
      this.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, createDecoratedFeature(Feature.END_SPIKE, new EndSpikeFeatureConfig(false, ImmutableList.of(), (BlockPos)null), Placement.NOPE, IPlacementConfig.NO_PLACEMENT_CONFIG));
      this.addSpawn(EntityClassification.MONSTER, new Biome.SpawnListEntry(EntityType.ENDERMAN, 10, 4, 4));
   }

   /**
    * takes temperature, returns color
    */
   @OnlyIn(Dist.CLIENT)
   public int getSkyColorByTemp(float currentTemperature) {
      return 0;
   }
}