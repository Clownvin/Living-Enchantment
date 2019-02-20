package com.clownvin.livingenchantment.command;

import com.clownvin.livingenchantment.personality.Personality;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TextComponentTranslation;

public class CommandListPersonalities {

    public static int listPersonalities(CommandSource source) throws CommandException {
        StringBuilder sb = new StringBuilder();
        for (Personality p : Personality.getRegistry().getValues()) {
            if (sb.length() != 0)
                sb.append(", ");
            sb.append(p.name);
        }
        source.sendFeedback(new TextComponentTranslation("commands.listpersonalities.success", sb.toString()), true);
        return 0;
    }

    public static void register(CommandDispatcher<CommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)(((LiteralArgumentBuilder)
                Commands.literal("listpersonalities").requires((p_198359_0_) -> p_198359_0_.hasPermissionLevel(2)
                )).executes((source) -> listPersonalities((CommandSource)source.getSource()))));
    }
}
