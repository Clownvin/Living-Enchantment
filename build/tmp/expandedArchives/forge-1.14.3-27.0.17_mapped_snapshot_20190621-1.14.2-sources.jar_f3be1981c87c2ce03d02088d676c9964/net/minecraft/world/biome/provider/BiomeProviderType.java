package net.minecraft.world.biome.provider;

import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.registry.Registry;

public class BiomeProviderType<C extends IBiomeProviderSettings, T extends BiomeProvider> extends net.minecraftforge.registries.ForgeRegistryEntry<BiomeProviderType<?, ?>> {
   public static final BiomeProviderType<CheckerboardBiomeProviderSettings, CheckerboardBiomeProvider> CHECKERBOARD = register("checkerboard", CheckerboardBiomeProvider::new, CheckerboardBiomeProviderSettings::new);
   public static final BiomeProviderType<SingleBiomeProviderSettings, SingleBiomeProvider> FIXED = register("fixed", SingleBiomeProvider::new, SingleBiomeProviderSettings::new);
   public static final BiomeProviderType<OverworldBiomeProviderSettings, OverworldBiomeProvider> VANILLA_LAYERED = register("vanilla_layered", OverworldBiomeProvider::new, OverworldBiomeProviderSettings::new);
   public static final BiomeProviderType<EndBiomeProviderSettings, EndBiomeProvider> THE_END = register("the_end", EndBiomeProvider::new, EndBiomeProviderSettings::new);
   private final Function<C, T> factory;
   private final Supplier<C> settingsFactory;

   private static <C extends IBiomeProviderSettings, T extends BiomeProvider> BiomeProviderType<C, T> register(String key, Function<C, T> p_212581_1_, Supplier<C> p_212581_2_) {
      return Registry.register(Registry.BIOME_SOURCE_TYPE, key, new BiomeProviderType<>(p_212581_1_, p_212581_2_));
   }

   public BiomeProviderType(Function<C, T> p_i50002_1_, Supplier<C> p_i50002_2_) {
      this.factory = p_i50002_1_;
      this.settingsFactory = p_i50002_2_;
   }

   public T create(C settings) {
      return (T)(this.factory.apply(settings));
   }

   public C createSettings() {
      return (C)(this.settingsFactory.get());
   }
}