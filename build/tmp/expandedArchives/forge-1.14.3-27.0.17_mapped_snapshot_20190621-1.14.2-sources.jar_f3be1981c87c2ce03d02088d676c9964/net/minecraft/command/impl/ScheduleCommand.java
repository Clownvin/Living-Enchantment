package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.datafixers.util.Either;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.FunctionObject;
import net.minecraft.command.TimedFunction;
import net.minecraft.command.TimedFunctionTag;
import net.minecraft.command.arguments.FunctionArgument;
import net.minecraft.command.arguments.TimeArgument;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public class ScheduleCommand {
   private static final SimpleCommandExceptionType field_218913_a = new SimpleCommandExceptionType(new TranslationTextComponent("commands.schedule.same_tick"));

   public static void register(CommandDispatcher<CommandSource> p_218909_0_) {
      p_218909_0_.register(Commands.literal("schedule").requires((p_218912_0_) -> {
         return p_218912_0_.hasPermissionLevel(2);
      }).then(Commands.literal("function").then(Commands.argument("function", FunctionArgument.function()).suggests(FunctionCommand.FUNCTION_SUGGESTER).then(Commands.argument("time", TimeArgument.func_218091_a()).executes((p_218911_0_) -> {
         return func_218908_a(p_218911_0_.getSource(), FunctionArgument.func_218110_b(p_218911_0_, "function"), IntegerArgumentType.getInteger(p_218911_0_, "time"));
      })))));
   }

   private static int func_218908_a(CommandSource p_218908_0_, Either<FunctionObject, Tag<FunctionObject>> p_218908_1_, int p_218908_2_) throws CommandSyntaxException {
      if (p_218908_2_ == 0) {
         throw field_218913_a.create();
      } else {
         long i = p_218908_0_.getWorld().getGameTime() + (long)p_218908_2_;
         p_218908_1_.ifLeft((p_218910_4_) -> {
            ResourceLocation resourcelocation = p_218910_4_.getId();
            p_218908_0_.getWorld().getWorldInfo().getScheduledEvents().scheduleReplaceDuplicate(resourcelocation.toString(), i, new TimedFunction(resourcelocation));
            p_218908_0_.sendFeedback(new TranslationTextComponent("commands.schedule.created.function", resourcelocation, p_218908_2_, i), true);
         }).ifRight((p_218907_4_) -> {
            ResourceLocation resourcelocation = p_218907_4_.getId();
            p_218908_0_.getWorld().getWorldInfo().getScheduledEvents().scheduleReplaceDuplicate("#" + resourcelocation.toString(), i, new TimedFunctionTag(resourcelocation));
            p_218908_0_.sendFeedback(new TranslationTextComponent("commands.schedule.created.tag", resourcelocation, p_218908_2_, i), true);
         });
         return (int)Math.floorMod(i, 2147483647L);
      }
   }
}