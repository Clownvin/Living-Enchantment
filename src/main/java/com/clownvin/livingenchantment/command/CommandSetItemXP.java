package com.clownvin.livingenchantment.command;

import com.clownvin.livingenchantment.LivingEnchantment;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.TranslationTextComponent;

public class CommandSetItemXP {

    public static int setItemExp(CommandSource source, double exp) throws CommandException {
        if (exp < 0) {
            source.sendErrorMessage(new TranslationTextComponent("commands.livingenchantment.invalid_xp_number"));
            return 1;
        }
        try {
            PlayerEntity player = source.asPlayer();
            ItemStack held = player.getHeldItemMainhand();
            ListNBT tag = LivingEnchantment.getEnchantmentNBTTag(held);
            if (tag == null) {
                source.sendErrorMessage(new TranslationTextComponent("commands.livingenchantment.mainhand_item_not_living"));
                return 2;
            }
            LivingEnchantment.setExp(player, held, tag, exp);
            source.sendFeedback(new TranslationTextComponent("commands.setitemxp.success", held.getDisplayName().getFormattedText(), exp), true);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void register(CommandDispatcher<CommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)(((LiteralArgumentBuilder) Commands.literal("setitemxp").requires((p_198359_0_) -> p_198359_0_.hasPermissionLevel(2)
        )).then((Commands.argument("xp", DoubleArgumentType.doubleArg()).executes((p_198352_0_) ->
                setItemExp(p_198352_0_.getSource(), DoubleArgumentType.getDouble(p_198352_0_, "xp"))
        )))));
    }
}
