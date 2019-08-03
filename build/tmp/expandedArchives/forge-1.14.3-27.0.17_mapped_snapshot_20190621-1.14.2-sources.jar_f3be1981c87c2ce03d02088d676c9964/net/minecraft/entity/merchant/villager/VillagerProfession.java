package net.minecraft.entity.merchant.villager;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.PointOfInterestType;

public class VillagerProfession extends net.minecraftforge.registries.ForgeRegistryEntry<VillagerProfession> {
   public static final VillagerProfession NONE = register("none", PointOfInterestType.UNEMPLOYED);
   public static final VillagerProfession ARMORER = register("armorer", PointOfInterestType.ARMORER);
   public static final VillagerProfession BUTCHER = register("butcher", PointOfInterestType.BUTCHER);
   public static final VillagerProfession CARTOGRAPHER = register("cartographer", PointOfInterestType.CARTOGRAPHER);
   public static final VillagerProfession CLERIC = register("cleric", PointOfInterestType.CLERIC);
   public static final VillagerProfession FARMER = register("farmer", PointOfInterestType.FARMER, ImmutableSet.of(Items.WHEAT, Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS), ImmutableSet.of(Blocks.FARMLAND));
   public static final VillagerProfession FISHERMAN = register("fisherman", PointOfInterestType.FISHERMAN);
   public static final VillagerProfession FLETCHER = register("fletcher", PointOfInterestType.FLETCHER);
   public static final VillagerProfession LEATHERWORKER = register("leatherworker", PointOfInterestType.LEATHERWORKER);
   public static final VillagerProfession LIBRARIAN = register("librarian", PointOfInterestType.LIBRARIAN);
   public static final VillagerProfession MASON = register("mason", PointOfInterestType.MASON);
   public static final VillagerProfession NITWIT = register("nitwit", PointOfInterestType.NITWIT);
   public static final VillagerProfession SHEPHERD = register("shepherd", PointOfInterestType.SHEPHERD);
   public static final VillagerProfession TOOLSMITH = register("toolsmith", PointOfInterestType.TOOLSMITH);
   public static final VillagerProfession WEAPONSMITH = register("weaponsmith", PointOfInterestType.WEAPONSMITH);
   private final String name;
   private final PointOfInterestType pointOfInterest;
   private final ImmutableSet<Item> field_221168_r;
   private final ImmutableSet<Block> field_221169_s;

   public VillagerProfession(String nameIn, PointOfInterestType pointOfInterestIn, ImmutableSet<Item> p_i50179_3_, ImmutableSet<Block> p_i50179_4_) {
      this.name = nameIn;
      this.pointOfInterest = pointOfInterestIn;
      this.field_221168_r = p_i50179_3_;
      this.field_221169_s = p_i50179_4_;
   }

   public PointOfInterestType getPointOfInterest() {
      return this.pointOfInterest;
   }

   public ImmutableSet<Item> func_221146_c() {
      return this.field_221168_r;
   }

   public ImmutableSet<Block> func_221150_d() {
      return this.field_221169_s;
   }

   public String toString() {
      return this.name;
   }

   static VillagerProfession register(String key, PointOfInterestType p_221147_1_) {
      return register(key, p_221147_1_, ImmutableSet.of(), ImmutableSet.of());
   }

   static VillagerProfession register(String key, PointOfInterestType p_221148_1_, ImmutableSet<Item> p_221148_2_, ImmutableSet<Block> p_221148_3_) {
      return Registry.register(Registry.VILLAGER_PROFESSION, new ResourceLocation(key), new VillagerProfession(key, p_221148_1_, p_221148_2_, p_221148_3_));
   }
}