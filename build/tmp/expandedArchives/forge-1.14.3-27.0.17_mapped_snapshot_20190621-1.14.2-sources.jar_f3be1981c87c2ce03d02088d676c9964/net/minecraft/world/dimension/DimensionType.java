package net.minecraft.world.dimension;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.io.File;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class DimensionType extends net.minecraftforge.registries.ForgeRegistryEntry<DimensionType> implements IDynamicSerializable {
   public static final DimensionType field_223227_a_ = register("overworld", new DimensionType(1, "", "", OverworldDimension::new, true));
   public static final DimensionType field_223228_b_ = register("the_nether", new DimensionType(0, "_nether", "DIM-1", NetherDimension::new, false));
   public static final DimensionType field_223229_c_ = register("the_end", new DimensionType(2, "_end", "DIM1", EndDimension::new, false));
   private final int id;
   private final String suffix;
   private final String directory;
   private final BiFunction<World, DimensionType, ? extends Dimension> factory;
   private final boolean field_218273_h;
   private final boolean isVanilla;
   private final net.minecraftforge.common.ModDimension modType;
   private final net.minecraft.network.PacketBuffer data;

   private static DimensionType register(String key, DimensionType type) {
      return Registry.register(Registry.DIMENSION_TYPE, type.id, key, type);
   }

   //Forge, Internal use only. Use DimensionManager instead.
   @Deprecated
   protected DimensionType(int idIn, String suffixIn, String directoryIn, BiFunction<World, DimensionType, ? extends Dimension> p_i49935_4_, boolean p_i49935_5_) {
      this(idIn, suffixIn, directoryIn, p_i49935_4_, p_i49935_5_, null, null);
   }

   //Forge, Internal use only. Use DimensionManager instead.
   @Deprecated
   public DimensionType(int idIn, String suffixIn, String directoryIn, BiFunction<World, DimensionType, ? extends Dimension> p_i49935_4_, boolean p_i49935_5_, @Nullable net.minecraftforge.common.ModDimension modType, @Nullable net.minecraft.network.PacketBuffer data) {
      this.id = idIn;
      this.suffix = suffixIn;
      this.directory = directoryIn;
      this.factory = p_i49935_4_;
      this.field_218273_h = p_i49935_5_;
      this.isVanilla = this.id >= 0 && this.id <= 2;
      this.modType = modType;
      this.data = data;
   }

   public static DimensionType func_218271_a(Dynamic<?> p_218271_0_) {
      return Registry.DIMENSION_TYPE.getOrDefault(new ResourceLocation(p_218271_0_.asString("")));
   }

   public static Iterable<DimensionType> getAll() {
      return Registry.DIMENSION_TYPE;
   }

   public int getId() {
      return this.id + -1;
   }

   @Deprecated //Forge Do not use, only used for villages backwards compatibility
   public String getSuffix() {
      return isVanilla ? this.suffix : "";
   }

   public File getDirectory(File p_212679_1_) {
      return this.directory.isEmpty() ? p_212679_1_ : new File(p_212679_1_, this.directory);
   }

   public Dimension create(World worldIn) {
      return this.factory.apply(worldIn, this);
   }

   public String toString() {
      return "DimensionType{" + getKey(this) + "}";
   }

   @Nullable
   public static DimensionType getById(int id) {
      return Registry.DIMENSION_TYPE.getByValue(id - -1);
   }

   public boolean isVanilla() {
      return this.isVanilla;
   }

   @Nullable
   public net.minecraftforge.common.ModDimension getModType() {
      return this.modType;
   }

   @Nullable
   public net.minecraft.network.PacketBuffer getData() {
      return this.data;
   }

   @Nullable
   public static DimensionType byName(ResourceLocation nameIn) {
      return Registry.DIMENSION_TYPE.getOrDefault(nameIn);
   }

   @Nullable
   public static ResourceLocation getKey(DimensionType dim) {
      return Registry.DIMENSION_TYPE.getKey(dim);
   }

   public boolean func_218272_d() {
      return this.field_218273_h;
   }

   public <T> T serialize(DynamicOps<T> p_218175_1_) {
      return p_218175_1_.createString(Registry.DIMENSION_TYPE.getKey(this).toString());
   }
}