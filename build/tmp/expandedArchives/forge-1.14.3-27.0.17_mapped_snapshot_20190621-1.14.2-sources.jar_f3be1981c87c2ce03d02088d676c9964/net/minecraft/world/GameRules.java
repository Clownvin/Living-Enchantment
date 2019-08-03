package net.minecraft.world;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SEntityStatusPacket;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameRules {
   private static final Logger field_223622_y = LogManager.getLogger();
   private static final Map<GameRules.RuleKey<?>, GameRules.RuleType<?>> field_223623_z = Maps.newTreeMap(Comparator.comparing((p_223597_0_) -> {
      return p_223597_0_.field_223578_a;
   }));
   public static final GameRules.RuleKey<GameRules.BooleanValue> field_223598_a = func_223595_a("doFireTick", GameRules.BooleanValue.func_223568_b(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> field_223599_b = func_223595_a("mobGriefing", GameRules.BooleanValue.func_223568_b(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> field_223600_c = func_223595_a("keepInventory", GameRules.BooleanValue.func_223568_b(false));
   public static final GameRules.RuleKey<GameRules.BooleanValue> field_223601_d = func_223595_a("doMobSpawning", GameRules.BooleanValue.func_223568_b(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> field_223602_e = func_223595_a("doMobLoot", GameRules.BooleanValue.func_223568_b(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> field_223603_f = func_223595_a("doTileDrops", GameRules.BooleanValue.func_223568_b(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> field_223604_g = func_223595_a("doEntityDrops", GameRules.BooleanValue.func_223568_b(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> field_223605_h = func_223595_a("commandBlockOutput", GameRules.BooleanValue.func_223568_b(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> field_223606_i = func_223595_a("naturalRegeneration", GameRules.BooleanValue.func_223568_b(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> field_223607_j = func_223595_a("doDaylightCycle", GameRules.BooleanValue.func_223568_b(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> field_223608_k = func_223595_a("logAdminCommands", GameRules.BooleanValue.func_223568_b(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> field_223609_l = func_223595_a("showDeathMessages", GameRules.BooleanValue.func_223568_b(true));
   public static final GameRules.RuleKey<GameRules.IntegerValue> field_223610_m = func_223595_a("randomTickSpeed", GameRules.IntegerValue.func_223559_b(3));
   public static final GameRules.RuleKey<GameRules.BooleanValue> field_223611_n = func_223595_a("sendCommandFeedback", GameRules.BooleanValue.func_223568_b(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> field_223612_o = func_223595_a("reducedDebugInfo", GameRules.BooleanValue.func_223567_b(false, (p_223589_0_, p_223589_1_) -> {
      byte b0 = (byte)(p_223589_1_.func_223572_a() ? 22 : 23);

      for(ServerPlayerEntity serverplayerentity : p_223589_0_.getPlayerList().getPlayers()) {
         serverplayerentity.connection.sendPacket(new SEntityStatusPacket(serverplayerentity, b0));
      }

   }));
   public static final GameRules.RuleKey<GameRules.BooleanValue> field_223613_p = func_223595_a("spectatorsGenerateChunks", GameRules.BooleanValue.func_223568_b(true));
   public static final GameRules.RuleKey<GameRules.IntegerValue> field_223614_q = func_223595_a("spawnRadius", GameRules.IntegerValue.func_223559_b(10));
   public static final GameRules.RuleKey<GameRules.BooleanValue> field_223615_r = func_223595_a("disableElytraMovementCheck", GameRules.BooleanValue.func_223568_b(false));
   public static final GameRules.RuleKey<GameRules.IntegerValue> field_223616_s = func_223595_a("maxEntityCramming", GameRules.IntegerValue.func_223559_b(24));
   public static final GameRules.RuleKey<GameRules.BooleanValue> field_223617_t = func_223595_a("doWeatherCycle", GameRules.BooleanValue.func_223568_b(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> field_223618_u = func_223595_a("doLimitedCrafting", GameRules.BooleanValue.func_223568_b(false));
   public static final GameRules.RuleKey<GameRules.IntegerValue> field_223619_v = func_223595_a("maxCommandChainLength", GameRules.IntegerValue.func_223559_b(65536));
   public static final GameRules.RuleKey<GameRules.BooleanValue> field_223620_w = func_223595_a("announceAdvancements", GameRules.BooleanValue.func_223568_b(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> field_223621_x = func_223595_a("disableRaids", GameRules.BooleanValue.func_223568_b(false));
   private final Map<GameRules.RuleKey<?>, GameRules.RuleValue<?>> rules = field_223623_z.entrySet().stream().collect(ImmutableMap.toImmutableMap(Entry::getKey, (p_223593_0_) -> {
      return p_223593_0_.getValue().func_223579_a();
   }));

   private static <T extends GameRules.RuleValue<T>> GameRules.RuleKey<T> func_223595_a(String p_223595_0_, GameRules.RuleType<T> p_223595_1_) {
      GameRules.RuleKey<T> rulekey = new GameRules.RuleKey<>(p_223595_0_);
      GameRules.RuleType<?> ruletype = field_223623_z.put(rulekey, p_223595_1_);
      if (ruletype != null) {
         throw new IllegalStateException("Duplicate game rule registration for " + p_223595_0_);
      } else {
         return rulekey;
      }
   }

   public <T extends GameRules.RuleValue<T>> T func_223585_a(GameRules.RuleKey<T> p_223585_1_) {
      return (T)(this.rules.get(p_223585_1_));
   }

   /**
    * Return the defined game rules as NBT.
    */
   public CompoundNBT write() {
      CompoundNBT compoundnbt = new CompoundNBT();
      this.rules.forEach((p_223594_1_, p_223594_2_) -> {
         compoundnbt.putString(p_223594_1_.field_223578_a, p_223594_2_.func_223552_b());
      });
      return compoundnbt;
   }

   /**
    * Set defined game rules from NBT.
    */
   public void read(CompoundNBT nbt) {
      this.rules.forEach((p_223591_1_, p_223591_2_) -> {
         p_223591_2_.func_223553_a(nbt.getString(p_223591_1_.field_223578_a));
      });
   }

   public static void func_223590_a(GameRules.IRuleEntryVisitor p_223590_0_) {
      field_223623_z.forEach((p_223587_1_, p_223587_2_) -> {
         func_223596_a(p_223590_0_, p_223587_1_, p_223587_2_);
      });
   }

   private static <T extends GameRules.RuleValue<T>> void func_223596_a(GameRules.IRuleEntryVisitor p_223596_0_, GameRules.RuleKey<?> p_223596_1_, GameRules.RuleType<?> p_223596_2_) {
      p_223596_0_.func_223481_a((GameRules.RuleKey)p_223596_1_, p_223596_2_);
   }

   public boolean func_223586_b(GameRules.RuleKey<GameRules.BooleanValue> p_223586_1_) {
      return this.func_223585_a(p_223586_1_).func_223572_a();
   }

   public int func_223592_c(GameRules.RuleKey<GameRules.IntegerValue> p_223592_1_) {
      return this.func_223585_a(p_223592_1_).func_223560_a();
   }

   public static class BooleanValue extends GameRules.RuleValue<GameRules.BooleanValue> {
      private boolean field_223575_a;

      private static GameRules.RuleType<GameRules.BooleanValue> func_223567_b(boolean p_223567_0_, BiConsumer<MinecraftServer, GameRules.BooleanValue> p_223567_1_) {
         return new GameRules.RuleType<>(BoolArgumentType::bool, (p_223574_1_) -> {
            return new GameRules.BooleanValue(p_223574_1_, p_223567_0_);
         }, p_223567_1_);
      }

      private static GameRules.RuleType<GameRules.BooleanValue> func_223568_b(boolean p_223568_0_) {
         return func_223567_b(p_223568_0_, (p_223569_0_, p_223569_1_) -> {
         });
      }

      public BooleanValue(GameRules.RuleType<GameRules.BooleanValue> p_i51535_1_, boolean p_i51535_2_) {
         super(p_i51535_1_);
         this.field_223575_a = p_i51535_2_;
      }

      protected void func_223555_a(CommandContext<CommandSource> p_223555_1_, String p_223555_2_) {
         this.field_223575_a = BoolArgumentType.getBool(p_223555_1_, p_223555_2_);
      }

      public boolean func_223572_a() {
         return this.field_223575_a;
      }

      public void func_223570_a(boolean p_223570_1_, @Nullable MinecraftServer p_223570_2_) {
         this.field_223575_a = p_223570_1_;
         this.func_223556_a(p_223570_2_);
      }

      protected String func_223552_b() {
         return Boolean.toString(this.field_223575_a);
      }

      protected void func_223553_a(String p_223553_1_) {
         this.field_223575_a = Boolean.parseBoolean(p_223553_1_);
      }

      public int func_223557_c() {
         return this.field_223575_a ? 1 : 0;
      }

      protected GameRules.BooleanValue func_223213_e_() {
         return this;
      }
   }

   public interface IRuleEntryVisitor {
      <T extends GameRules.RuleValue<T>> void func_223481_a(GameRules.RuleKey<T> p_223481_1_, GameRules.RuleType<T> p_223481_2_);
   }

   public static class IntegerValue extends GameRules.RuleValue<GameRules.IntegerValue> {
      private int field_223566_a;

      private static GameRules.RuleType<GameRules.IntegerValue> func_223564_a(int p_223564_0_, BiConsumer<MinecraftServer, GameRules.IntegerValue> p_223564_1_) {
         return new GameRules.RuleType<>(IntegerArgumentType::integer, (p_223565_1_) -> {
            return new GameRules.IntegerValue(p_223565_1_, p_223564_0_);
         }, p_223564_1_);
      }

      private static GameRules.RuleType<GameRules.IntegerValue> func_223559_b(int p_223559_0_) {
         return func_223564_a(p_223559_0_, (p_223561_0_, p_223561_1_) -> {
         });
      }

      public IntegerValue(GameRules.RuleType<GameRules.IntegerValue> p_i51534_1_, int p_i51534_2_) {
         super(p_i51534_1_);
         this.field_223566_a = p_i51534_2_;
      }

      protected void func_223555_a(CommandContext<CommandSource> p_223555_1_, String p_223555_2_) {
         this.field_223566_a = IntegerArgumentType.getInteger(p_223555_1_, p_223555_2_);
      }

      public int func_223560_a() {
         return this.field_223566_a;
      }

      protected String func_223552_b() {
         return Integer.toString(this.field_223566_a);
      }

      protected void func_223553_a(String p_223553_1_) {
         this.field_223566_a = func_223563_b(p_223553_1_);
      }

      private static int func_223563_b(String p_223563_0_) {
         if (!p_223563_0_.isEmpty()) {
            try {
               return Integer.parseInt(p_223563_0_);
            } catch (NumberFormatException var2) {
               GameRules.field_223622_y.warn("Failed to parse integer {}", (Object)p_223563_0_);
            }
         }

         return 0;
      }

      public int func_223557_c() {
         return this.field_223566_a;
      }

      protected GameRules.IntegerValue func_223213_e_() {
         return this;
      }
   }

   public static final class RuleKey<T extends GameRules.RuleValue<T>> {
      private final String field_223578_a;

      public RuleKey(String p_i51533_1_) {
         this.field_223578_a = p_i51533_1_;
      }

      public String toString() {
         return this.field_223578_a;
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else {
            return p_equals_1_ instanceof GameRules.RuleKey && ((GameRules.RuleKey)p_equals_1_).field_223578_a.equals(this.field_223578_a);
         }
      }

      public int hashCode() {
         return this.field_223578_a.hashCode();
      }

      public String func_223576_a() {
         return this.field_223578_a;
      }
   }

   public static class RuleType<T extends GameRules.RuleValue<T>> {
      private final Supplier<ArgumentType<?>> field_223582_a;
      private final Function<GameRules.RuleType<T>, T> field_223583_b;
      private final BiConsumer<MinecraftServer, T> field_223584_c;

      private RuleType(Supplier<ArgumentType<?>> p_i51531_1_, Function<GameRules.RuleType<T>, T> p_i51531_2_, BiConsumer<MinecraftServer, T> p_i51531_3_) {
         this.field_223582_a = p_i51531_1_;
         this.field_223583_b = p_i51531_2_;
         this.field_223584_c = p_i51531_3_;
      }

      public RequiredArgumentBuilder<CommandSource, ?> func_223581_a(String p_223581_1_) {
         return Commands.argument(p_223581_1_, this.field_223582_a.get());
      }

      public T func_223579_a() {
         return (T)(this.field_223583_b.apply(this));
      }
   }

   public abstract static class RuleValue<T extends GameRules.RuleValue<T>> {
      private final GameRules.RuleType<T> field_223558_a;

      public RuleValue(GameRules.RuleType<T> p_i51530_1_) {
         this.field_223558_a = p_i51530_1_;
      }

      protected abstract void func_223555_a(CommandContext<CommandSource> p_223555_1_, String p_223555_2_);

      public void func_223554_b(CommandContext<CommandSource> p_223554_1_, String p_223554_2_) {
         this.func_223555_a(p_223554_1_, p_223554_2_);
         this.func_223556_a(p_223554_1_.getSource().getServer());
      }

      protected void func_223556_a(@Nullable MinecraftServer p_223556_1_) {
         if (p_223556_1_ != null) {
            this.field_223558_a.field_223584_c.accept(p_223556_1_, (T)this.func_223213_e_());
         }

      }

      protected abstract void func_223553_a(String p_223553_1_);

      protected abstract String func_223552_b();

      public String toString() {
         return this.func_223552_b();
      }

      public abstract int func_223557_c();

      protected abstract T func_223213_e_();
   }
}