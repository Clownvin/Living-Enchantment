package com.clownvin.livingenchantment.command;

import com.clownvin.livingenchantment.LivingEnchantment;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.TranslationTextComponent;

public class CommandResetItem {

    public static int resetItem(CommandSource source) throws CommandException {
        try {
            PlayerEntity player = source.asPlayer();
            ItemStack held = player.getHeldItemMainhand();
            ListNBT tag = LivingEnchantment.getEnchantmentNBTTag(held);
            if (tag == null) {
                source.sendErrorMessage(new TranslationTextComponent("commands.livingenchantment.mainhand_item_not_living"));
                return 2;
            }
            LivingEnchantment.resetItem(held);
            source.sendFeedback(new TranslationTextComponent("commands.resetitem.success", held.getDisplayName().getFormattedText()), true);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void register(CommandDispatcher<CommandSource> commandDispatcher) {
        commandDispatcher.register((LiteralArgumentBuilder)(((LiteralArgumentBuilder)
                Commands.literal("resetitem").requires((p_198359_0_) -> p_198359_0_.hasPermissionLevel(2)
        )).executes((source) -> resetItem((CommandSource)source.getSource()))));
    }
}
