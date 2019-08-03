package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.profiler.DebugProfiler;
import net.minecraft.profiler.IProfileResult;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;

public class DebugCommand {
   private static final SimpleCommandExceptionType NOT_RUNNING_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.debug.notRunning"));
   private static final SimpleCommandExceptionType ALREADY_RUNNING_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.debug.alreadyRunning"));

   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register(Commands.literal("debug").requires((p_198332_0_) -> {
         return p_198332_0_.hasPermissionLevel(3);
      }).then(Commands.literal("start").executes((p_198329_0_) -> {
         return startDebug(p_198329_0_.getSource());
      })).then(Commands.literal("stop").executes((p_198333_0_) -> {
         return stopDebug(p_198333_0_.getSource());
      })));
   }

   private static int startDebug(CommandSource source) throws CommandSyntaxException {
      MinecraftServer minecraftserver = source.getServer();
      DebugProfiler debugprofiler = minecraftserver.getProfiler();
      if (debugprofiler.func_219899_d().func_219936_a()) {
         throw ALREADY_RUNNING_EXCEPTION.create();
      } else {
         minecraftserver.enableProfiling();
         source.sendFeedback(new TranslationTextComponent("commands.debug.started", "Started the debug profiler. Type '/debug stop' to stop it."), true);
         return 0;
      }
   }

   private static int stopDebug(CommandSource source) throws CommandSyntaxException {
      MinecraftServer minecraftserver = source.getServer();
      DebugProfiler debugprofiler = minecraftserver.getProfiler();
      if (!debugprofiler.func_219899_d().func_219936_a()) {
         throw NOT_RUNNING_EXCEPTION.create();
      } else {
         IProfileResult iprofileresult = debugprofiler.func_219899_d().func_219938_b();
         File file1 = new File(minecraftserver.getFile("debug"), "profile-results-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + ".txt");
         iprofileresult.writeToFile(file1);
         float f = (float)iprofileresult.nanoTime() / 1.0E9F;
         float f1 = (float)iprofileresult.ticksSpend() / f;
         source.sendFeedback(new TranslationTextComponent("commands.debug.stopped", String.format(Locale.ROOT, "%.2f", f), iprofileresult.ticksSpend(), String.format("%.2f", f1)), true);
         return MathHelper.floor(f1);
      }
   }
}