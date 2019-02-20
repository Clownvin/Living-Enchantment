package com.clownvin.livingenchantment.command;

import com.clownvin.livingenchantment.LivingEnchantment;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;

public class CommandSetItemXP {

    /*public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        EntityPlayer player = getCommandSenderAsPlayer(sender);
        if (args.length < 1)
            throw new WrongUsageException(getUsage(sender), new Object[0]);
        try {
            float xp = Float.parseFloat(args[0]);
            if (xp < 0)
                throw new WrongUsageException("commands.livingenchantment.invalid_xp_number", new Object[0]);
            ItemStack held = player.getHeldItemMainhand();
            NBTTagCompound tag = LivingEnchantment.getEnchantmentNBTTag(held);
            if (tag == null)
                throw new WrongUsageException("commands.livingenchantment.mainhand_item_not_living", new Object[0]);
            LivingEnchantment.setExp(player, held, tag, xp);
            notifyCommandListener(sender, this, "commands.setitemxp.success", new Object[]{held.getDisplayName(), xp});
        } catch (NumberFormatException e) {
            throw new WrongUsageException("commands.livingenchantment.invalid_xp_number", new Object[0]);
        }
    }*/

    public static int setItemExp(CommandSource source, double exp) throws CommandException {
        if (exp < 0) {
            source.sendErrorMessage(new TextComponentTranslation("commands.livingenchantment.invalid_xp_number"));
            return 1;
        }
        try {
            EntityPlayer player = source.asPlayer();
            ItemStack held = player.getHeldItemMainhand();
            NBTTagCompound tag = LivingEnchantment.getEnchantmentNBTTag(held);
            if (tag == null) {
                source.sendErrorMessage(new TextComponentTranslation("commands.livingenchantment.mainhand_item_not_living"));
                return 2;
            }
            LivingEnchantment.setExp(player, held, tag, exp);
            source.sendFeedback(new TextComponentTranslation("commands.setitemxp.success", held.getDisplayName().getFormattedText(), exp), true);
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
