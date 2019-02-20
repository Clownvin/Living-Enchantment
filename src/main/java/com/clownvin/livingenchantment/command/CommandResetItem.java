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

public class CommandResetItem extends CommandBase {

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getName() {
        return "resetitem";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.resetitem.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        EntityPlayer player = getCommandSenderAsPlayer(sender);
        ItemStack held = player.getHeldItemMainhand();
        NBTTagCompound tag = LivingEnchantment.getEnchantmentNBTTag(held);
        if (tag == null)
            throw new WrongUsageException("commands.livingenchantment.mainhand_item_not_living", new Object[0]);
        LivingEnchantment.resetItem(held);
        notifyCommandListener(sender, this, "commands.resetitem.success", new Object[]{held.getDisplayName()});
    }
}
