package net.minecraft.village;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.state.properties.BedPart;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.registry.Registry;

public class PointOfInterestType extends net.minecraftforge.registries.ForgeRegistryEntry<PointOfInterestType> {
   private static final Predicate<PointOfInterestType> field_221071_s = (p_221041_0_) -> {
      return Registry.VILLAGER_PROFESSION.stream().map(VillagerProfession::getPointOfInterest).collect(Collectors.toSet()).contains(p_221041_0_);
   };
   public static final Predicate<PointOfInterestType> field_221053_a = (p_221049_0_) -> {
      return true;
   };
   private static final Set<BlockState> field_221072_t = ImmutableList.of(Blocks.RED_BED, Blocks.BLACK_BED, Blocks.BLUE_BED, Blocks.BROWN_BED, Blocks.CYAN_BED, Blocks.GRAY_BED, Blocks.GREEN_BED, Blocks.LIGHT_BLUE_BED, Blocks.LIGHT_GRAY_BED, Blocks.LIME_BED, Blocks.MAGENTA_BED, Blocks.ORANGE_BED, Blocks.PINK_BED, Blocks.PURPLE_BED, Blocks.WHITE_BED, Blocks.YELLOW_BED).stream().flatMap((p_221043_0_) -> {
      return p_221043_0_.getStateContainer().getValidStates().stream();
   }).filter((p_221050_0_) -> {
      return p_221050_0_.get(BedBlock.PART) == BedPart.HEAD;
   }).collect(ImmutableSet.toImmutableSet());
   private static final Map<BlockState, PointOfInterestType> field_221073_u = Maps.newHashMap();
   public static final PointOfInterestType UNEMPLOYED = register("unemployed", ImmutableSet.of(), 1, (SoundEvent)null, field_221071_s);
   public static final PointOfInterestType ARMORER = register("armorer", func_221042_a(Blocks.BLAST_FURNACE), 1, SoundEvents.ENTITY_VILLAGER_WORK_ARMORER);
   public static final PointOfInterestType BUTCHER = register("butcher", func_221042_a(Blocks.SMOKER), 1, SoundEvents.ENTITY_VILLAGER_WORK_BUTCHER);
   public static final PointOfInterestType CARTOGRAPHER = register("cartographer", func_221042_a(Blocks.CARTOGRAPHY_TABLE), 1, SoundEvents.ENTITY_VILLAGER_WORK_CARTOGRAPHER);
   public static final PointOfInterestType CLERIC = register("cleric", func_221042_a(Blocks.BREWING_STAND), 1, SoundEvents.ENTITY_VILLAGER_WORK_CLERIC);
   public static final PointOfInterestType FARMER = register("farmer", func_221042_a(Blocks.COMPOSTER), 1, SoundEvents.ENTITY_VILLAGER_WORK_FARMER);
   public static final PointOfInterestType FISHERMAN = register("fisherman", func_221042_a(Blocks.BARREL), 1, SoundEvents.ENTITY_VILLAGER_WORK_FISHERMAN);
   public static final PointOfInterestType FLETCHER = register("fletcher", func_221042_a(Blocks.FLETCHING_TABLE), 1, SoundEvents.ENTITY_VILLAGER_WORK_FLETCHER);
   public static final PointOfInterestType LEATHERWORKER = register("leatherworker", func_221042_a(Blocks.CAULDRON), 1, SoundEvents.ENTITY_VILLAGER_WORK_LEATHERWORKER);
   public static final PointOfInterestType LIBRARIAN = register("librarian", func_221042_a(Blocks.LECTERN), 1, SoundEvents.ENTITY_VILLAGER_WORK_LIBRARIAN);
   public static final PointOfInterestType MASON = register("mason", func_221042_a(Blocks.STONECUTTER), 1, SoundEvents.ENTITY_VILLAGER_WORK_MASON);
   public static final PointOfInterestType NITWIT = register("nitwit", ImmutableSet.of(), 1, (SoundEvent)null);
   public static final PointOfInterestType SHEPHERD = register("shepherd", func_221042_a(Blocks.LOOM), 1, SoundEvents.ENTITY_VILLAGER_WORK_SHEPHERD);
   public static final PointOfInterestType TOOLSMITH = register("toolsmith", func_221042_a(Blocks.SMITHING_TABLE), 1, SoundEvents.ENTITY_VILLAGER_WORK_TOOLSMITH);
   public static final PointOfInterestType WEAPONSMITH = register("weaponsmith", func_221042_a(Blocks.GRINDSTONE), 1, SoundEvents.ENTITY_VILLAGER_WORK_WEAPONSMITH);
   public static final PointOfInterestType HOME = register("home", field_221072_t, 1, (SoundEvent)null);
   public static final PointOfInterestType MEETING = register("meeting", func_221042_a(Blocks.BELL), 32, (SoundEvent)null);
   private final String name;
   private final Set<BlockState> field_221075_w;
   private final int maxFreeTickets;
   @Nullable
   private final SoundEvent workSound;
   private final Predicate<PointOfInterestType> field_221078_z;

   private static Set<BlockState> func_221042_a(Block p_221042_0_) {
      return ImmutableSet.copyOf(p_221042_0_.getStateContainer().getValidStates());
   }

   public PointOfInterestType(String nameIn, Set<BlockState> p_i50291_2_, int p_i50291_3_, @Nullable SoundEvent p_i50291_4_, Predicate<PointOfInterestType> p_i50291_5_) {
      this.name = nameIn;
      this.field_221075_w = ImmutableSet.copyOf(p_i50291_2_);
      this.maxFreeTickets = p_i50291_3_;
      this.workSound = p_i50291_4_;
      this.field_221078_z = p_i50291_5_;
   }

   public PointOfInterestType(String p_i50292_1_, Set<BlockState> p_i50292_2_, int p_i50292_3_, @Nullable SoundEvent p_i50292_4_) {
      this.name = p_i50292_1_;
      this.field_221075_w = ImmutableSet.copyOf(p_i50292_2_);
      this.maxFreeTickets = p_i50292_3_;
      this.workSound = p_i50292_4_;
      this.field_221078_z = (p_221046_1_) -> {
         return p_221046_1_ == this;
      };
   }

   public int getMaxFreeTickets() {
      return this.maxFreeTickets;
   }

   public Predicate<PointOfInterestType> func_221045_c() {
      return this.field_221078_z;
   }

   public String toString() {
      return this.name;
   }

   @Nullable
   public SoundEvent getWorkSound() {
      return this.workSound;
   }

   private static PointOfInterestType register(String key, Set<BlockState> p_221051_1_, int p_221051_2_, @Nullable SoundEvent p_221051_3_) {
      return func_221052_a(Registry.POINT_OF_INTEREST_TYPE.register(new ResourceLocation(key), new PointOfInterestType(key, p_221051_1_, p_221051_2_, p_221051_3_)));
   }

   private static PointOfInterestType register(String key, Set<BlockState> p_221039_1_, int p_221039_2_, @Nullable SoundEvent p_221039_3_, Predicate<PointOfInterestType> p_221039_4_) {
      return func_221052_a(Registry.POINT_OF_INTEREST_TYPE.register(new ResourceLocation(key), new PointOfInterestType(key, p_221039_1_, p_221039_2_, p_221039_3_, p_221039_4_)));
   }

   private static PointOfInterestType func_221052_a(PointOfInterestType p_221052_0_) {
      p_221052_0_.field_221075_w.forEach((p_221040_1_) -> {
         PointOfInterestType pointofinteresttype = field_221073_u.put(p_221040_1_, p_221052_0_);
         if (pointofinteresttype != null) {
            throw new IllegalStateException(String.format("%s is defined in too many tags", p_221040_1_));
         }
      });
      return p_221052_0_;
   }

   public static Optional<PointOfInterestType> forState(BlockState state) {
      return Optional.ofNullable(field_221073_u.get(state));
   }

   public static Stream<BlockState> getAllStates() {
      return field_221073_u.keySet().stream();
   }
}