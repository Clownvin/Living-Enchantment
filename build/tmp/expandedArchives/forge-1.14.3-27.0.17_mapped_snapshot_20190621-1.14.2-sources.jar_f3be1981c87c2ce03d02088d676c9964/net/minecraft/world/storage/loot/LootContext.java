package net.minecraft.world.storage.loot;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ServerWorld;

public class LootContext {
   private final Random random;
   private final float luck;
   private final ServerWorld world;
   private final LootTableManager lootTableManager;
   private final Set<LootTable> lootTables = Sets.newLinkedHashSet();
   private final Map<LootParameter<?>, Object> parameters;
   private final Map<ResourceLocation, LootContext.IDynamicDropProvider> field_216037_g;

   private LootContext(Random randomIn, float luckIn, ServerWorld worldIn, LootTableManager lootTableManagerIn, Map<LootParameter<?>, Object> parametersIn, Map<ResourceLocation, LootContext.IDynamicDropProvider> p_i51271_6_) {
      this.random = randomIn;
      this.luck = luckIn;
      this.world = worldIn;
      this.lootTableManager = lootTableManagerIn;
      this.parameters = ImmutableMap.copyOf(parametersIn);
      this.field_216037_g = ImmutableMap.copyOf(p_i51271_6_);
   }

   public boolean has(LootParameter<?> parameter) {
      return this.parameters.containsKey(parameter);
   }

   public void func_216034_a(ResourceLocation p_216034_1_, Consumer<ItemStack> p_216034_2_) {
      LootContext.IDynamicDropProvider lootcontext$idynamicdropprovider = this.field_216037_g.get(p_216034_1_);
      if (lootcontext$idynamicdropprovider != null) {
         lootcontext$idynamicdropprovider.add(this, p_216034_2_);
      }

   }

   @Nullable
   public <T> T get(LootParameter<T> parameter) {
      return (T)this.parameters.get(parameter);
   }

   public boolean addLootTable(LootTable lootTableIn) {
      return this.lootTables.add(lootTableIn);
   }

   public void removeLootTable(LootTable lootTableIn) {
      this.lootTables.remove(lootTableIn);
   }

   public LootTableManager getLootTableManager() {
      return this.lootTableManager;
   }

   public Random getRandom() {
      return this.random;
   }

   public float getLuck() {
      return this.luck;
   }

   public ServerWorld getWorld() {
      return this.world;
   }

   public int getLootingModifier() {
      return net.minecraftforge.common.ForgeHooks.getLootingLevel(get(LootParameters.THIS_ENTITY), get(LootParameters.KILLER_ENTITY), get(LootParameters.DAMAGE_SOURCE));
   }

   public static class Builder {
      private final ServerWorld world;
      private final Map<LootParameter<?>, Object> field_216025_b = Maps.newIdentityHashMap();
      private final Map<ResourceLocation, LootContext.IDynamicDropProvider> field_216026_c = Maps.newHashMap();
      private Random rand;
      private float luck;

      public Builder(ServerWorld worldIn) {
         this.world = worldIn;
      }

      public LootContext.Builder withRandom(Random randomIn) {
         this.rand = randomIn;
         return this;
      }

      public LootContext.Builder withSeed(long seed) {
         if (seed != 0L) {
            this.rand = new Random(seed);
         }

         return this;
      }

      public LootContext.Builder withSeededRandom(long seed, Random p_216020_3_) {
         if (seed == 0L) {
            this.rand = p_216020_3_;
         } else {
            this.rand = new Random(seed);
         }

         return this;
      }

      public LootContext.Builder withLuck(float luckIn) {
         this.luck = luckIn;
         return this;
      }

      public <T> LootContext.Builder withParameter(LootParameter<T> parameter, T value) {
         this.field_216025_b.put(parameter, value);
         return this;
      }

      public <T> LootContext.Builder withNullableParameter(LootParameter<T> parameter, @Nullable T value) {
         if (value == null) {
            this.field_216025_b.remove(parameter);
         } else {
            this.field_216025_b.put(parameter, value);
         }

         return this;
      }

      public LootContext.Builder withDynamicDrop(ResourceLocation p_216017_1_, LootContext.IDynamicDropProvider p_216017_2_) {
         LootContext.IDynamicDropProvider lootcontext$idynamicdropprovider = this.field_216026_c.put(p_216017_1_, p_216017_2_);
         if (lootcontext$idynamicdropprovider != null) {
            throw new IllegalStateException("Duplicated dynamic drop '" + this.field_216026_c + "'");
         } else {
            return this;
         }
      }

      public ServerWorld getWorld() {
         return this.world;
      }

      public <T> T assertPresent(LootParameter<T> parameter) {
         T t = (T)this.field_216025_b.get(parameter);
         if (t == null) {
            throw new IllegalArgumentException("No parameter " + parameter);
         } else {
            return t;
         }
      }

      @Nullable
      public <T> T get(LootParameter<T> parameter) {
         return (T)this.field_216025_b.get(parameter);
      }

      public LootContext build(LootParameterSet parameterSet) {
         Set<LootParameter<?>> set = Sets.difference(this.field_216025_b.keySet(), parameterSet.getAllParameters());
         if (!set.isEmpty()) {
            throw new IllegalArgumentException("Parameters not allowed in this parameter set: " + set);
         } else {
            Set<LootParameter<?>> set1 = Sets.difference(parameterSet.getRequiredParameters(), this.field_216025_b.keySet());
            if (!set1.isEmpty()) {
               throw new IllegalArgumentException("Missing required parameters: " + set1);
            } else {
               Random random = this.rand;
               if (random == null) {
                  random = new Random();
               }

               return new LootContext(random, this.luck, this.world, this.world.getServer().getLootTableManager(), this.field_216025_b, this.field_216026_c);
            }
         }
      }
   }

   public static enum EntityTarget {
      THIS("this", LootParameters.THIS_ENTITY),
      KILLER("killer", LootParameters.KILLER_ENTITY),
      DIRECT_KILLER("direct_killer", LootParameters.DIRECT_KILLER_ENTITY),
      KILLER_PLAYER("killer_player", LootParameters.LAST_DAMAGE_PLAYER);

      private final String targetType;
      private final LootParameter<? extends Entity> parameter;

      private EntityTarget(String targetTypeIn, LootParameter<? extends Entity> parameterIn) {
         this.targetType = targetTypeIn;
         this.parameter = parameterIn;
      }

      public LootParameter<? extends Entity> getParameter() {
         return this.parameter;
      }

      public static LootContext.EntityTarget fromString(String type) {
         for(LootContext.EntityTarget lootcontext$entitytarget : values()) {
            if (lootcontext$entitytarget.targetType.equals(type)) {
               return lootcontext$entitytarget;
            }
         }

         throw new IllegalArgumentException("Invalid entity target " + type);
      }

      public static class Serializer extends TypeAdapter<LootContext.EntityTarget> {
         public void write(JsonWriter p_write_1_, LootContext.EntityTarget p_write_2_) throws IOException {
            p_write_1_.value(p_write_2_.targetType);
         }

         public LootContext.EntityTarget read(JsonReader p_read_1_) throws IOException {
            return LootContext.EntityTarget.fromString(p_read_1_.nextString());
         }
      }
   }

   @FunctionalInterface
   public interface IDynamicDropProvider {
      void add(LootContext p_add_1_, Consumer<ItemStack> p_add_2_);
   }
}