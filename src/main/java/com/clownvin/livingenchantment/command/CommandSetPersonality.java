package com.clownvin.livingenchantment.command;

import com.clownvin.livingenchantment.LivingEnchantment;
import com.clownvin.livingenchantment.personality.Personality;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;

public class CommandSetPersonality extends CommandBase {

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getName() {
        return "setpersonality";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.setpersonality.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length <= 0) {
            //throw new WrongUsageException(getUsage(sender), new Object[0]);
            StringBuilder sb = new StringBuilder();
            for (Personality p : Personality.getRegistry().getValuesCollection()) {
                if (sb.length() != 0)
                    sb.append(", ");
                sb.append(p.name);
            }
            notifyCommandListener(sender, this, "commands.setpersonality.listpersonalities", new Object[]{sb.toString()});
            return;
        }
        EntityPlayer player = getCommandSenderAsPlayer(sender);
        ItemStack held = player.getHeldItemMainhand();
        NBTTagCompound tag = LivingEnchantment.getEnchantmentNBTTag(held);
        String name = "???";
        if (tag == null)
            throw new WrongUsageException("commands.livingenchantment.mainhand_item_not_living", new Object[0]);
        try {
            float value = Float.parseFloat(args[0]);
            if (value <= 0 || value > 1.0f)
                throw new WrongUsageException("commands.setpersonality.not_in_range");
            tag.setFloat(LivingEnchantment.PERSONALITY, value);
            Personality personality = Personality.getPersonality(tag);
            tag.setString(LivingEnchantment.PERSONALITY_NAME, personality.name);
            name = personality.name;
        } catch (NumberFormatException e) {
            String arg = args[0];
            for (int i = 1; i < args.length; i++)
                arg += " " + args[i];
            Personality personality = Personality.getPersonality(LivingEnchantment.MODID + ":" + arg);
            if (personality == null)
                throw new WrongUsageException("commands.setpersonality.not_in_range", arg);
            tag.setFloat(LivingEnchantment.PERSONALITY, Personality.getValue(personality));
            tag.setString(LivingEnchantment.PERSONALITY_NAME, personality.name);
            name = personality.name;
        }
        notifyCommandListener(sender, this, "commands.setpersonality.success", new Object[]{held.getDisplayName(), name});
    }
}
