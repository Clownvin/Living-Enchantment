package net.minecraft.world.gen.feature;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Dynamic;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldWriter;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.structure.BuriedTreasureConfig;
import net.minecraft.world.gen.feature.structure.BuriedTreasureStructure;
import net.minecraft.world.gen.feature.structure.DesertPyramidStructure;
import net.minecraft.world.gen.feature.structure.EndCityStructure;
import net.minecraft.world.gen.feature.structure.FortressStructure;
import net.minecraft.world.gen.feature.structure.IglooStructure;
import net.minecraft.world.gen.feature.structure.JunglePyramidStructure;
import net.minecraft.world.gen.feature.structure.MineshaftConfig;
import net.minecraft.world.gen.feature.structure.MineshaftStructure;
import net.minecraft.world.gen.feature.structure.OceanMonumentStructure;
import net.minecraft.world.gen.feature.structure.OceanRuinConfig;
import net.minecraft.world.gen.feature.structure.OceanRuinStructure;
import net.minecraft.world.gen.feature.structure.PillagerOutpostConfig;
import net.minecraft.world.gen.feature.structure.PillagerOutpostStructure;
import net.minecraft.world.gen.feature.structure.ShipwreckConfig;
import net.minecraft.world.gen.feature.structure.ShipwreckStructure;
import net.minecraft.world.gen.feature.structure.StrongholdStructure;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.SwampHutStructure;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.feature.structure.VillageStructure;
import net.minecraft.world.gen.feature.structure.WoodlandMansionStructure;
import net.minecraft.world.gen.placement.CountConfig;

public abstract class Feature<FC extends IFeatureConfig> extends net.minecraftforge.registries.ForgeRegistryEntry<Feature<?>> {
   public static final Structure<PillagerOutpostConfig> PILLAGER_OUTPOST = register("pillager_outpost", new PillagerOutpostStructure(PillagerOutpostConfig::deserialize));
   public static final Structure<MineshaftConfig> MINESHAFT = register("mineshaft", new MineshaftStructure(MineshaftConfig::deserialize));
   public static final Structure<NoFeatureConfig> WOODLAND_MANSION = register("woodland_mansion", new WoodlandMansionStructure(NoFeatureConfig::deserialize));
   public static final Structure<NoFeatureConfig> JUNGLE_TEMPLE = register("jungle_temple", new JunglePyramidStructure(NoFeatureConfig::deserialize));
   public static final Structure<NoFeatureConfig> DESERT_PYRAMID = register("desert_pyramid", new DesertPyramidStructure(NoFeatureConfig::deserialize));
   public static final Structure<NoFeatureConfig> IGLOO = register("igloo", new IglooStructure(NoFeatureConfig::deserialize));
   public static final Structure<ShipwreckConfig> SHIPWRECK = register("shipwreck", new ShipwreckStructure(ShipwreckConfig::deserialize));
   public static final SwampHutStructure SWAMP_HUT = register("swamp_hut", new SwampHutStructure(NoFeatureConfig::deserialize));
   public static final Structure<NoFeatureConfig> STRONGHOLD = register("stronghold", new StrongholdStructure(NoFeatureConfig::deserialize));
   public static final Structure<NoFeatureConfig> OCEAN_MONUMENT = register("ocean_monument", new OceanMonumentStructure(NoFeatureConfig::deserialize));
   public static final Structure<OceanRuinConfig> OCEAN_RUIN = register("ocean_ruin", new OceanRuinStructure(OceanRuinConfig::deserialize));
   public static final Structure<NoFeatureConfig> NETHER_BRIDGE = register("nether_bridge", new FortressStructure(NoFeatureConfig::deserialize));
   public static final Structure<NoFeatureConfig> END_CITY = register("end_city", new EndCityStructure(NoFeatureConfig::deserialize));
   public static final Structure<BuriedTreasureConfig> BURIED_TREASURE = register("buried_treasure", new BuriedTreasureStructure(BuriedTreasureConfig::deserialize));
   public static final Structure<VillageConfig> VILLAGE = register("village", new VillageStructure(VillageConfig::deserialize));
   public static final Feature<NoFeatureConfig> FANCY_TREE = register("fancy_tree", new BigTreeFeature(NoFeatureConfig::deserialize, false));
   public static final Feature<NoFeatureConfig> BIRCH_TREE = register("birch_tree", new BirchTreeFeature(NoFeatureConfig::deserialize, false, false));
   public static final Feature<NoFeatureConfig> SUPER_BIRCH_TREE = register("super_birch_tree", new BirchTreeFeature(NoFeatureConfig::deserialize, false, true));
   public static final Feature<NoFeatureConfig> JUNGLE_GROUND_BUSH = register("jungle_ground_bush", new ShrubFeature(NoFeatureConfig::deserialize, Blocks.JUNGLE_LOG.getDefaultState(), Blocks.OAK_LEAVES.getDefaultState()));
   public static final Feature<NoFeatureConfig> JUNGLE_TREE = register("jungle_tree", new JungleTreeFeature(NoFeatureConfig::deserialize, false, 4, Blocks.JUNGLE_LOG.getDefaultState(), Blocks.JUNGLE_LEAVES.getDefaultState(), true));
   public static final Feature<NoFeatureConfig> PINE_TREE = register("pine_tree", new PointyTaigaTreeFeature(NoFeatureConfig::deserialize));
   public static final Feature<NoFeatureConfig> DARK_OAK_TREE = register("dark_oak_tree", new DarkOakTreeFeature(NoFeatureConfig::deserialize, false));
   public static final Feature<NoFeatureConfig> SAVANNA_TREE = register("savanna_tree", new SavannaTreeFeature(NoFeatureConfig::deserialize, false));
   public static final Feature<NoFeatureConfig> SPRUCE_TREE = register("spruce_tree", new TallTaigaTreeFeature(NoFeatureConfig::deserialize, false));
   public static final Feature<NoFeatureConfig> SWAMP_TREE = register("swamp_tree", new SwampTreeFeature(NoFeatureConfig::deserialize));
   public static final Feature<NoFeatureConfig> NORMAL_TREE = register("normal_tree", new TreeFeature(NoFeatureConfig::deserialize, false));
   public static final Feature<NoFeatureConfig> MEGA_JUNGLE_TREE = register("mega_jungle_tree", new MegaJungleFeature(NoFeatureConfig::deserialize, false, 10, 20, Blocks.JUNGLE_LOG.getDefaultState(), Blocks.JUNGLE_LEAVES.getDefaultState()));
   public static final Feature<NoFeatureConfig> MEGA_PINE_TREE = register("mega_pine_tree", new MegaPineTree(NoFeatureConfig::deserialize, false, false));
   public static final Feature<NoFeatureConfig> MEGA_SPRUCE_TREE = register("mega_spruce_tree", new MegaPineTree(NoFeatureConfig::deserialize, false, true));
   public static final FlowersFeature DEFAULT_FLOWER = register("default_flower", new DefaultFlowersFeature(NoFeatureConfig::deserialize));
   public static final FlowersFeature FOREST_FLOWER = register("forest_flower", new ForestFlowersFeature(NoFeatureConfig::deserialize));
   public static final FlowersFeature PLAIN_FLOWER = register("plain_flower", new PlainsFlowersFeature(NoFeatureConfig::deserialize));
   public static final FlowersFeature SWAMP_FLOWER = register("swamp_flower", new SwampFlowersFeature(NoFeatureConfig::deserialize));
   public static final FlowersFeature GENERAL_FOREST_FLOWER = register("general_forest_flower", new GeneralForestFlowerFeature(NoFeatureConfig::deserialize));
   public static final Feature<NoFeatureConfig> JUNGLE_GRASS = register("jungle_grass", new JungleGrassFeature(NoFeatureConfig::deserialize));
   public static final Feature<NoFeatureConfig> TAIGA_GRASS = register("taiga_grass", new TaigaGrassFeature(NoFeatureConfig::deserialize));
   public static final Feature<GrassFeatureConfig> GRASS = register("grass", new GrassFeature(GrassFeatureConfig::deserialize));
   public static final Feature<NoFeatureConfig> VOID_START_PLATFORM = register("void_start_platform", new VoidStartPlatformFeature(NoFeatureConfig::deserialize));
   public static final Feature<NoFeatureConfig> CACTUS = register("cactus", new CactusFeature(NoFeatureConfig::deserialize));
   public static final Feature<NoFeatureConfig> DEAD_BUSH = register("dead_bush", new DeadBushFeature(NoFeatureConfig::deserialize));
   public static final Feature<NoFeatureConfig> DESERT_WELL = register("desert_well", new DesertWellsFeature(NoFeatureConfig::deserialize));
   public static final Feature<NoFeatureConfig> FOSSIL = register("fossil", new FossilsFeature(NoFeatureConfig::deserialize));
   public static final Feature<NoFeatureConfig> HELL_FIRE = register("hell_fire", new FireFeature(NoFeatureConfig::deserialize));
   public static final Feature<BigMushroomFeatureConfig> HUGE_RED_MUSHROOM = register("huge_red_mushroom", new BigRedMushroomFeature(BigMushroomFeatureConfig::deserialize));
   public static final Feature<BigMushroomFeatureConfig> HUGE_BROWN_MUSHROOM = register("huge_brown_mushroom", new BigBrownMushroomFeature(BigMushroomFeatureConfig::deserialize));
   public static final Feature<NoFeatureConfig> ICE_SPIKE = register("ice_spike", new IceSpikeFeature(NoFeatureConfig::deserialize));
   public static final Feature<NoFeatureConfig> GLOWSTONE_BLOB = register("glowstone_blob", new GlowstoneBlobFeature(NoFeatureConfig::deserialize));
   public static final Feature<NoFeatureConfig> MELON = register("melon", new MelonFeature(NoFeatureConfig::deserialize));
   public static final Feature<NoFeatureConfig> PUMPKIN = register("pumpkin", new ScatteredPlantFeature(NoFeatureConfig::deserialize, Blocks.PUMPKIN.getDefaultState()));
   public static final Feature<NoFeatureConfig> REED = register("reed", new ReedFeature(NoFeatureConfig::deserialize));
   public static final Feature<NoFeatureConfig> FREEZE_TOP_LAYER = register("freeze_top_layer", new IceAndSnowFeature(NoFeatureConfig::deserialize));
   public static final Feature<NoFeatureConfig> VINES = register("vines", new VinesFeature(NoFeatureConfig::deserialize));
   public static final Feature<NoFeatureConfig> WATERLILY = register("waterlily", new WaterlilyFeature(NoFeatureConfig::deserialize));
   public static final Feature<NoFeatureConfig> MONSTER_ROOM = register("monster_room", new DungeonsFeature(NoFeatureConfig::deserialize));
   public static final Feature<NoFeatureConfig> BLUE_ICE = register("blue_ice", new BlueIceFeature(NoFeatureConfig::deserialize));
   public static final Feature<IcebergConfig> ICEBERG = register("iceberg", new IcebergFeature(IcebergConfig::deserialize));
   public static final Feature<BlockBlobConfig> FOREST_ROCK = register("forest_rock", new BlockBlobFeature(BlockBlobConfig::deserialize));
   public static final Feature<NoFeatureConfig> HAY_PILE = register("hay_pile", new HayBlockPileFeature(NoFeatureConfig::deserialize));
   public static final Feature<NoFeatureConfig> SNOW_PILE = register("snow_pile", new SnowBlockPileFeature(NoFeatureConfig::deserialize));
   public static final Feature<NoFeatureConfig> ICE_PILE = register("ice_pile", new IceBlockPileFeature(NoFeatureConfig::deserialize));
   public static final Feature<NoFeatureConfig> MELON_PILE = register("melon_pile", new MelonBlockPileFeature(NoFeatureConfig::deserialize));
   public static final Feature<NoFeatureConfig> PUMPKIN_PILE = register("pumpkin_pile", new PumpkinBlockPileFeature(NoFeatureConfig::deserialize));
   public static final Feature<BushConfig> BUSH = register("bush", new BushFeature(BushConfig::deserialize));
   public static final Feature<SphereReplaceConfig> DISK = register("disk", new SphereReplaceFeature(SphereReplaceConfig::deserialize));
   public static final Feature<DoublePlantConfig> DOUBLE_PLANT = register("double_plant", new DoublePlantFeature(DoublePlantConfig::deserialize));
   public static final Feature<HellLavaConfig> NETHER_SPRING = register("nether_spring", new NetherSpringFeature(HellLavaConfig::deserialize));
   public static final Feature<FeatureRadiusConfig> ICE_PATCH = register("ice_patch", new IcePathFeature(FeatureRadiusConfig::deserialize));
   public static final Feature<LakesConfig> LAKE = register("lake", new LakesFeature(LakesConfig::deserialize));
   public static final Feature<OreFeatureConfig> ORE = register("ore", new OreFeature(OreFeatureConfig::deserialize));
   public static final Feature<MultipleWithChanceRandomFeatureConfig> RANDOM_RANDOM_SELECTOR = register("random_random_selector", new MultipleRandomFeature(MultipleWithChanceRandomFeatureConfig::deserialize));
   public static final Feature<MultipleRandomFeatureConfig> RANDOM_SELECTOR = register("random_selector", new MultipleWithChanceRandomFeature(MultipleRandomFeatureConfig::deserialize));
   public static final Feature<SingleRandomFeature> SIMPLE_RANDOM_SELECTOR = register("simple_random_selector", new SingleRandomFeatureConfig(SingleRandomFeature::deserialize));
   public static final Feature<TwoFeatureChoiceConfig> RANDOM_BOOLEAN_SELECTOR = register("random_boolean_selector", new TwoFeatureChoiceFeature(TwoFeatureChoiceConfig::deserialize));
   public static final Feature<ReplaceBlockConfig> EMERALD_ORE = register("emerald_ore", new ReplaceBlockFeature(ReplaceBlockConfig::deserialize));
   public static final Feature<LiquidsConfig> SPRING_FEATURE = register("spring_feature", new SpringFeature(LiquidsConfig::deserialize));
   public static final Feature<EndSpikeFeatureConfig> END_SPIKE = register("end_spike", new EndSpikeFeature(EndSpikeFeatureConfig::deserialize));
   public static final Feature<NoFeatureConfig> END_ISLAND = register("end_island", new EndIslandFeature(NoFeatureConfig::deserialize));
   public static final Feature<NoFeatureConfig> CHORUS_PLANT = register("chorus_plant", new ChorusPlantFeature(NoFeatureConfig::deserialize));
   public static final Feature<EndGatewayConfig> END_GATEWAY = register("end_gateway", new EndGatewayFeature(EndGatewayConfig::deserialize));
   public static final Feature<SeaGrassConfig> SEAGRASS = register("seagrass", new SeaGrassFeature(SeaGrassConfig::deserialize));
   public static final Feature<NoFeatureConfig> KELP = register("kelp", new KelpFeature(NoFeatureConfig::deserialize));
   public static final Feature<NoFeatureConfig> CORAL_TREE = register("coral_tree", new CoralTreeFeature(NoFeatureConfig::deserialize));
   public static final Feature<NoFeatureConfig> CORAL_MUSHROOM = register("coral_mushroom", new CoralMushroomFeature(NoFeatureConfig::deserialize));
   public static final Feature<NoFeatureConfig> CORAL_CLAW = register("coral_claw", new CoralClawFeature(NoFeatureConfig::deserialize));
   public static final Feature<CountConfig> SEA_PICKLE = register("sea_pickle", new SeaPickleFeature(CountConfig::deserialize));
   public static final Feature<BlockWithContextConfig> SIMPLE_BLOCK = register("simple_block", new BlockWithContextFeature(BlockWithContextConfig::deserialize));
   public static final Feature<ProbabilityConfig> BAMBOO = register("bamboo", new BambooFeature(ProbabilityConfig::deserialize));
   public static final Feature<DecoratedFeatureConfig> DECORATED = register("decorated", new DecoratedFeature(DecoratedFeatureConfig::deserialize));
   public static final Feature<DecoratedFeatureConfig> DECORATED_FLOWER = register("decorated_flower", new DecoratedFlowerFeature(DecoratedFeatureConfig::deserialize));
   public static final Feature<NoFeatureConfig> SWEET_BERRY_BUSH = register("sweet_berry_bush", new ScatteredPlantFeature(NoFeatureConfig::deserialize, Blocks.SWEET_BERRY_BUSH.getDefaultState().with(SweetBerryBushBlock.AGE, Integer.valueOf(3))));
   public static final Feature<FillLayerConfig> FILL_LAYER = register("fill_layer", new FillLayerFeature(FillLayerConfig::deserialize));
   public static final BonusChestFeature BONUS_CHEST = register("bonus_chest", new BonusChestFeature(NoFeatureConfig::deserialize));
   public static final BiMap<String, Structure<?>> STRUCTURES = Util.make(HashBiMap.create(), (p_205170_0_) -> {
      p_205170_0_.put("Pillager_Outpost".toLowerCase(Locale.ROOT), PILLAGER_OUTPOST);
      p_205170_0_.put("Mineshaft".toLowerCase(Locale.ROOT), MINESHAFT);
      p_205170_0_.put("Mansion".toLowerCase(Locale.ROOT), WOODLAND_MANSION);
      p_205170_0_.put("Jungle_Pyramid".toLowerCase(Locale.ROOT), JUNGLE_TEMPLE);
      p_205170_0_.put("Desert_Pyramid".toLowerCase(Locale.ROOT), DESERT_PYRAMID);
      p_205170_0_.put("Igloo".toLowerCase(Locale.ROOT), IGLOO);
      p_205170_0_.put("Shipwreck".toLowerCase(Locale.ROOT), SHIPWRECK);
      p_205170_0_.put("Swamp_Hut".toLowerCase(Locale.ROOT), SWAMP_HUT);
      p_205170_0_.put("Stronghold".toLowerCase(Locale.ROOT), STRONGHOLD);
      p_205170_0_.put("Monument".toLowerCase(Locale.ROOT), OCEAN_MONUMENT);
      p_205170_0_.put("Ocean_Ruin".toLowerCase(Locale.ROOT), OCEAN_RUIN);
      p_205170_0_.put("Fortress".toLowerCase(Locale.ROOT), NETHER_BRIDGE);
      p_205170_0_.put("EndCity".toLowerCase(Locale.ROOT), END_CITY);
      p_205170_0_.put("Buried_Treasure".toLowerCase(Locale.ROOT), BURIED_TREASURE);
      p_205170_0_.put("Village".toLowerCase(Locale.ROOT), VILLAGE);
   });
   public static final List<Structure<?>> field_214488_aQ = ImmutableList.of(PILLAGER_OUTPOST, VILLAGE);
   private final Function<Dynamic<?>, ? extends FC> configFactory;
   protected final boolean doBlockNotify;

   private static <C extends IFeatureConfig, F extends Feature<C>> F register(String key, F value) {
      return (F)(Registry.<Feature<?>>register(Registry.FEATURE, key, value));
   }

   public Feature(Function<Dynamic<?>, ? extends FC> configFactoryIn) {
      this.configFactory = configFactoryIn;
      this.doBlockNotify = false;
   }

   public Feature(Function<Dynamic<?>, ? extends FC> configFactoryIn, boolean doBlockNotifyIn) {
      this.configFactory = configFactoryIn;
      this.doBlockNotify = doBlockNotifyIn;
   }

   public FC createConfig(Dynamic<?> p_214470_1_) {
      return (FC)(this.configFactory.apply(p_214470_1_));
   }

   protected void setBlockState(IWorldWriter worldIn, BlockPos pos, BlockState state) {
      if (this.doBlockNotify) {
         worldIn.setBlockState(pos, state, 3);
      } else {
         worldIn.setBlockState(pos, state, 2);
      }

   }

   public abstract boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, FC config);

   public List<Biome.SpawnListEntry> getSpawnList() {
      return Collections.emptyList();
   }

   public List<Biome.SpawnListEntry> getCreatureSpawnList() {
      return Collections.emptyList();
   }
}