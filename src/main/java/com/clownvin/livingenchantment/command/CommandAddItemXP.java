package com.clownvin.livingenchantment.command;

import com.clownvin.livingenchantment.LivingEnchantment;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;

public class CommandAddItemXP extends CommandBase {

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getName() {
        return "additemxp";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.additemxp.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
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
            LivingEnchantment.addExp(player, held, tag, xp);
            notifyCommandListener(sender, this, "commands.additemxp.success", new Object[]{xp, held.getDisplayName()});
        } catch (NumberFormatException e) {
            throw new WrongUsageException("commands.livingenchantment.invalid_xp_number", new Object[0]);
        }
    }
}
