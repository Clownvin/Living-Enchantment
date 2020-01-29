package com.clownvin.livingenchantment;

import com.clownvin.livingenchantment.command.*;
import com.clownvin.livingenchantment.config.LivingConfig;
import com.clownvin.livingenchantment.enchantment.EnchantmentLiving;
import com.clownvin.livingenchantment.entity.item.EntityLivingXPOrb;
import com.clownvin.livingenchantment.personality.Personality;
import com.clownvin.livingenchantment.proxy.CommonProxy;
import com.clownvin.livingenchantment.world.storage.loot.LootInjector;
import com.clownvin.livingenchantment.world.storage.loot.functions.EnchantLiving;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.libraries.ModList;

import java.util.ArrayList;
import java.util.List;

@Mod(name = LivingEnchantment.NAME, modid = LivingEnchantment.MODID, version = LivingEnchantment.VERSION, updateJSON = "https://raw.githubusercontent.com/Clownvin/Living-Enchantment/1.12.2/update.json")
@Mod.EventBusSubscriber(modid = LivingEnchantment.MODID)
public class LivingEnchantment {
    public static final String MODID = "livingenchantment";
    public static final String VERSION = "3.2.1";
    public static final String NAME = "Living Enchantment";

    public static final String PERSONALITY_NAME = "personalityName";
    public static final String PERSONALITY = "personality";
    public static final String LEVEL = "level";
    public static final String EFFECTIVENESS = "effectiveness";
    public static final String XP = "xp";
    public static final String LAST_TALK = "lasttalk";
    public static final String KILL_COUNT = "kills";
    public static final String HIT_COUNT = "hits";
    public static final String USAGE_COUNT = "uses";

    public static final int JUST_UNIQUES = 0;
    public static final int JUST_BOOKS = 2;
    public static final int GEN1 = 1;

    @Mod.Instance
    public static LivingEnchantment instance;

    @SidedProxy(clientSide = "com.clownvin.livingenchantment.proxy.ClientProxy", serverSide = "com.clownvin.livingenchantment.proxy.ServerProxy")
    public static CommonProxy proxy;

    @EventHandler
    public static void serverStartingEvent(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandAddItemXP());
        event.registerServerCommand(new CommandSetItemXP());
        event.registerServerCommand(new CommandSetItemLevel());
        event.registerServerCommand(new CommandResetItem());
        event.registerServerCommand(new CommandSetPersonality());
    }

    private static boolean isNewerVersion(String v1, String v2) {
        String[] v1s = v1.split("\\.");
        String[] v2s = v2.split("\\.");
        if (v2s.length > v1s.length)
            return true;
        System.out.println(v2s.length+", "+v1s.length);
        for (int i = 0; i < v2s.length; i++) {
            if (v2s[i].length() > v1s[i].length()) {
                return true;
            }
            if (v2s[i].compareTo(v1s[i]) > 0) {
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void onJoinGame(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
        if (!LivingConfig.general.showUpdateNotifications)
            return;
        ForgeVersion.CheckResult result = ForgeVersion.getResult(Loader.instance().activeModContainer());
        if (result.target == null || !isNewerVersion(Loader.instance().activeModContainer().getVersion(), result.target.toString())) {//result.target.compareTo(Loader.instance().activeModContainer().getVersion()) <= 0) {
            return;
        }
        event.player.sendMessage(new TextComponentTranslation("text.new_update_notification", MODID+", "+MODID+"-"+result.target.toString()));
    }

    public static int getWornLivingLevel(EntityLivingBase entity) {
        int cumulativeLivingLevel = 0;
        Iterable<ItemStack> gear = entity.getEquipmentAndArmor();
        for (ItemStack stack : gear) {
            if (!(stack.getItem() instanceof ItemArmor))
                continue;
            NBTTagCompound tag = getEnchantmentNBTTag(stack);
            if (tag == null)
                continue;
            cumulativeLivingLevel += tag.getInteger(LEVEL);
        }
        return cumulativeLivingLevel;
    }

    public static void addBlockCount(EntityLivingBase entity) {
        Iterable<ItemStack> gear = entity.getEquipmentAndArmor();
        for (ItemStack stack : gear) {
            if (!(stack.getItem() instanceof ItemArmor))
                continue;
            NBTTagCompound tag = getEnchantmentNBTTag(stack);
            if (tag == null)
                continue;
            tag.setInteger(HIT_COUNT, tag.getInteger(HIT_COUNT) + 1);
        }
    }

    public static String removeFormatting(String textIn) {
        String text = textIn;
        while (text.contains("§")) {
            int index = text.indexOf("§");
            text = text.replace(text.substring(index, index + 2), "");
        }
        return text;
    }

    public static int xpToLvl(double xp) {
        if (LivingConfig.general.xpFunction == GEN1)
            return (int) Math.pow((5 * (xp / LivingConfig.general.levelExpModifier)) / 4, 1 / 3.0f) + 1;
        return (int) ((Math.sqrt((9 * (xp / LivingConfig.general.levelExpModifier)) + 4) + 2) / 9.0);
    }

    public static double lvlToXp(int lvl) {
        if (LivingConfig.general.xpFunction == GEN1)
            return (Math.round((4 * (Math.pow(lvl - 1, 3)) / 5)) * LivingConfig.general.levelExpModifier);
        return ((9 * (lvl * lvl) - (4 * lvl)) * LivingConfig.general.levelExpModifier);
    }

    public static float getToolEffectivenessModifier(NBTTagCompound tag) {
        return 1 + (float) (tag.getInteger(LEVEL) * LivingConfig.general.toolEffectivenessPerLevel);
    }

    public static float getWeaponEffectivenessModifier(NBTTagCompound tag) {
        return 1 + (float) (tag.getInteger(LEVEL) * LivingConfig.general.weaponEffectivenessPerLevel);
    }

    public static float getArmorEffectivenessModifier(int level, float scale) {
        return 1 + (float) (level * LivingConfig.general.armorEffectivenessPerLEvel * scale);
    }

    public static NBTTagCompound getEnchantmentNBTTag(ItemStack stack) {
        NBTTagCompound tag = null;
        for (NBTBase base : stack.getEnchantmentTagList()) {
            NBTTagCompound other = (NBTTagCompound) base;
            if (Enchantment.getEnchantmentByID(other.getShort("id")) != EnchantmentLiving.LIVING_ENCHANTMENT)
                continue;
            tag = other;
            break;
        }
        return tag;
    }

    public static void doTalking(EntityPlayer player, ItemStack item, NBTTagCompound tag, Event reason) {
        if (!LivingConfig.personalities.showDialogue)
            return;
        Personality personality = Personality.getPersonality(tag);
        float damagePercent = item.getMaxDamage() <= 0 || item.getMaxDamage() <= 0 ? 0 : item.getItemDamage() / (float) item.getMaxDamage();
        if (damagePercent >= 0.90f) {
            talk(player, item, personality.getFivePercent(), 3000);
        } else if (damagePercent >= 0.75f) {
            talk(player, item, personality.getTwentyPercent(), 3000);
        }
        if (reason instanceof LivingDeathEvent && Math.random() * personality.killOdds <= 1.0) {
            if (((LivingDeathEvent) reason).getEntityLiving().equals(player))
                talk(player, item, personality.getOnDeath());
            else
                talk(player, item, personality.getOnKill());
        } else if ((reason instanceof BlockEvent.BreakEvent || reason instanceof UseHoeEvent) && Math.random() * personality.useOdds <= 1.0) {
            talk(player, item, personality.getOnUse());
        } else if (reason instanceof LivingHurtEvent && Math.random() * personality.hurtOdds <= 1.0) {
            Entity source = ((LivingHurtEvent) reason).getSource().getTrueSource();
            if (source != null && source.equals(player))
                talk(player, item, personality.getOnTargetHurt());
            //else send ouch message
        }
    }

    public static void talk(EntityPlayer player, ItemStack stack, String message) {
        talk(player, stack, message, LivingConfig.personalities.minimumDialogueDelay);
    }

    public static void talk(EntityPlayer player, ItemStack stack, String message, int minimumDialogueDelay) {
        NBTTagCompound tag = getEnchantmentNBTTag(stack);
        if (!LivingConfig.personalities.showDialogue) {
            return;
        }
        if (System.currentTimeMillis() - tag.getLong(LAST_TALK) < minimumDialogueDelay) {
            if (tag.getLong(LAST_TALK) > System.currentTimeMillis())
                tag.setLong(LAST_TALK, System.currentTimeMillis());
            return;
        }
        tag.setLong(LAST_TALK, System.currentTimeMillis());
        float durability = (1.0f - (stack.getItemDamage() / (float) stack.getMaxDamage())) * 100.0f;
        message = message.replace("$user", player.getName()).replace("$level", "" + tag.getInteger(LEVEL)).replace("$durability", String.format("%.1f", durability) + "%");
        player.sendMessage(new TextComponentString(stack.getDisplayName() + ": " + message));
    }

    public static boolean isMaxLevel(NBTTagCompound tag) {
        return tag.getInteger(LEVEL) == LivingConfig.general.maxLevel && xpToLvl(tag.getDouble(XP)) >= LivingConfig.general.maxLevel;
    }

    public static void resetItem(ItemStack item) {
        NBTTagCompound tag = getEnchantmentNBTTag(item);
        if (tag == null) {
            return; // It's not enchanted..
        }
        tag.setDouble(XP, 0.0);
        tag.setInteger(LEVEL, 0);
        tag.setFloat(EFFECTIVENESS, getWeaponEffectivenessModifier(tag));
        tag.setFloat(PERSONALITY, 0);
        tag.setString(PERSONALITY_NAME, "???");
        tag.setInteger(KILL_COUNT, 0);
        tag.setInteger(HIT_COUNT, 0);
        tag.setInteger(USAGE_COUNT, 0);
    }

    public static List<ItemStack> getAllEquipedLivingItems(EntityPlayer player) {
        List<ItemStack> items = new ArrayList<>(5);
        for (ItemStack i : player.getEquipmentAndArmor()) {
            if (getEnchantmentNBTTag(i) != null)
                items.add(i);
        }
        return items;
    }

    public static boolean isToolEffective(ItemStack item, IBlockState state) {
        return item.getItem().getToolClasses(item).contains(state.getBlock().getHarvestTool(state));
    }

    public static void doExpDrop(EntityPlayer player, BlockPos pos, double exp) {
        if (LivingConfig.general.xpStyle == 0)
            return;
        if (LivingConfig.general.xpStyle == 2) {
            player.world.spawnEntity(new EntityLivingXPOrb(player.world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, exp));
        } else if (LivingConfig.general.xpStyle == 1) {
            if (LivingConfig.general.xpShare)
                addExp(player, exp);
            else {
                ItemStack enchantedItem = EnchantmentHelper.getEnchantedItem(EnchantmentLiving.LIVING_ENCHANTMENT, player);
                addExp(player, enchantedItem, getEnchantmentNBTTag(enchantedItem), exp);
            }
        }
    }

    public static void addExp(EntityPlayer player, double exp) { //Adds EXP to all
        List<ItemStack> items = getAllEquipedLivingItems(player);
        for (ItemStack stack : getAllEquipedLivingItems(player)) {
            addExp(player, stack, getEnchantmentNBTTag(stack), exp / items.size());
        }
    }

    public static void addExp(EntityPlayer player, ItemStack stack, NBTTagCompound tag, double exp) {
        int currLevel = xpToLvl(tag.getDouble(XP));
        tag.setDouble(XP, tag.getDouble(XP) + exp);
        int newLevel = xpToLvl(tag.getDouble(XP));
        Personality personality = Personality.getPersonality(tag);
        tag.setString(PERSONALITY_NAME, personality.name);
        if (LivingConfig.general.maxLevel <= newLevel) {
            tag.setInteger(LEVEL, LivingConfig.general.maxLevel);
            tag.setFloat(EFFECTIVENESS, getWeaponEffectivenessModifier(tag));
        } else {
            tag.setInteger(LEVEL, newLevel);
            tag.setFloat(EFFECTIVENESS, getWeaponEffectivenessModifier(tag));
        }
        if (newLevel == currLevel) {
            return;
        }
        player.world.playSound(player, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_LEVELUP, player.getSoundCategory(), 0.75F, 0.9F + (float) (Math.random() * 0.2F));
        if (LivingConfig.personalities.showDialogue)
            talk(player, stack, personality.getOnLevelUp(), 0);
    }

    public static void setExp(EntityPlayer player, ItemStack stack, NBTTagCompound tag, double exp) {
        tag.setDouble(XP, 0);
        addExp(player, stack, tag, exp);
    }

    @SubscribeEvent
    public static void onPlayerPickedUpXP(PlayerPickupXpEvent event) { //Mending style XP
        if (event.getOrb().world.isRemote)
            return;
        if (LivingConfig.general.xpStyle != 0)
            return;
        ItemStack stack = EnchantmentHelper.getEnchantedItem(EnchantmentLiving.LIVING_ENCHANTMENT, event.getEntityPlayer());
        if (stack.isEmpty())
            return;
        NBTTagCompound tag = getEnchantmentNBTTag(stack);
        if (LivingEnchantment.isMaxLevel(tag))
            return;
        int xp = event.getOrb().xpValue == 1 ? 1 : event.getOrb().xpValue / 2;
        if (LivingConfig.general.xpShare)
            LivingEnchantment.addExp(event.getEntityPlayer(), xp * LivingConfig.vanillaModifier);
        else
            LivingEnchantment.addExp(event.getEntityPlayer(), stack, tag, xp * LivingConfig.vanillaModifier);
        event.getOrb().xpValue -= xp;
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        if (event.getEntityPlayer() == null)
            return;
        if (event.getItemStack().getItem() instanceof ItemArmor) {
            int livingLevel = getWornLivingLevel(event.getEntityPlayer());
            if (livingLevel != 0) {
                event.getToolTip().add(new TextComponentTranslation("tooltip.currently_worn").getUnformattedText());
                event.getToolTip().add(TextFormatting.BLUE + " " + new TextComponentTranslation("tooltip.damage_reduction", String.format("%.1f", (1 - (1.0F / getArmorEffectivenessModifier(livingLevel, 0.25f))) * 100) + "%" + TextFormatting.BLUE).getUnformattedText());
            }
        }
        NBTTagCompound tag = getEnchantmentNBTTag(event.getItemStack());
        if (tag == null) {
            return;
        }
        float multiplier = tag.getFloat(EFFECTIVENESS);
        if (!(event.getItemStack().getItem() instanceof ItemArmor) && multiplier > 0) {
            String attackDamageText = new TextComponentTranslation("tooltip.attack_damage").getUnformattedText();
            for (int i = event.getToolTip().size() - 1; i >= 0; i--) {
                if (event.getToolTip().get(i).contains(attackDamageText)) {
                    try {
                        String text = event.getToolTip().get(i).replace(attackDamageText, "").replace(" ", "");
                        text = removeFormatting(text);
                        float damage = multiplier * Float.parseFloat(text);
                        event.getToolTip().set(i, " " + String.format(damage % 1.0f == 0 ? "%.0f" : "%.1f", damage) + " " + attackDamageText);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
        event.getToolTip().add(1, TextFormatting.GOLD + new TextComponentTranslation("tooltip.lvl", TextFormatting.RESET.toString() + TextFormatting.GREEN.toString() + tag.getInteger(LEVEL) + TextFormatting.RESET).getUnformattedText());
        double xp = tag.getDouble(XP), nextLevelXp = lvlToXp(tag.getInteger(LEVEL) + 1);
        event.getToolTip().add(2, TextFormatting.GOLD + new TextComponentTranslation("tooltip.exp", TextFormatting.RESET.toString() + TextFormatting.GREEN.toString() + String.format("%.1f", xp) + TextFormatting.RESET + "/" + TextFormatting.GREEN.toString() + String.format("%.1f", nextLevelXp) + TextFormatting.RESET).getUnformattedText());
        int i = tag.getInteger(KILL_COUNT);
        if (i > 0) {
            event.getToolTip().add(i + new TextComponentTranslation("tooltip.things_killed").getUnformattedText());
        }
        i = tag.getInteger(USAGE_COUNT);
        if (i > 0) {
            Item item = event.getItemStack().getItem();
            if (item instanceof ItemPickaxe)
                event.getToolTip().add(i + new TextComponentTranslation("tooltip.blocks_pickaxed").getUnformattedText());
            else if (item instanceof ItemAxe)
                event.getToolTip().add(i + new TextComponentTranslation("tooltip.blocks_axed").getUnformattedText());
            else if (item instanceof ItemSpade)
                event.getToolTip().add(i + new TextComponentTranslation("tooltip.blocks_shoveled").getUnformattedText());
            else if (item instanceof ItemHoe)
                event.getToolTip().add(i + new TextComponentTranslation("tooltip.blocks_hoed").getUnformattedText());
            else
                event.getToolTip().add(i + new TextComponentTranslation("tooltip.blocks_tooled").getUnformattedText());
        }
        i = tag.getInteger(HIT_COUNT);
        if (i > 0) {
            event.getToolTip().add(i + new TextComponentTranslation("tooltip.hits_taken").getUnformattedText());
        }
        if (!LivingConfig.personalities.showPersonalities) {
            return;
        }
        event.getToolTip().add(3, TextFormatting.GOLD + new TextComponentTranslation("tooltip.personality", TextFormatting.RESET + "" + TextFormatting.GREEN + tag.getString(PERSONALITY_NAME)).getUnformattedText());
    }

    @SubscribeEvent
    public static void onLivingKilled(LivingDeathEvent event) {
        if (event.getEntityLiving().world.isRemote)
            return;
        if (LivingConfig.personalities.showDialogue && event.getEntityLiving() instanceof EntityPlayer && event.getEntityLiving().getHeldItemMainhand().isItemEnchanted()) {
            NBTTagCompound tag = getEnchantmentNBTTag(event.getEntityLiving().getHeldItemMainhand());
            if (tag != null && LivingConfig.personalities.showDialogue) {
                talk((EntityPlayer) event.getEntityLiving(), event.getEntityLiving().getHeldItemMainhand(), Personality.getPersonality(tag).getOnDeath());
            }
        }
        if (!(event.getSource().getTrueSource() instanceof EntityPlayer))
            return;
        EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
        NBTTagCompound tag = null;
        if (event.getSource().damageType.equals("arrow") && player.getHeldItemOffhand().getItem() instanceof ItemBow)
            tag = getEnchantmentNBTTag(player.getHeldItemOffhand());
        if (tag == null)
            tag = getEnchantmentNBTTag(player.getHeldItemMainhand());
        if (tag != null)
            tag.setInteger(KILL_COUNT, tag.getInteger(KILL_COUNT) + 1);
        ItemStack item = EnchantmentHelper.getEnchantedItem(EnchantmentLiving.LIVING_ENCHANTMENT, player);
        tag = getEnchantmentNBTTag(item);
        if (tag == null)
            return;
        doExpDrop(player, event.getEntityLiving().getPosition(), LivingConfig.getXPForLiving(event.getEntityLiving()));
        doTalking(player, item, tag, event);
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        int targetLivingLevel = getWornLivingLevel(event.getEntityLiving());
        if (targetLivingLevel > 0) {
            event.setAmount(event.getAmount() * (1.0F / getArmorEffectivenessModifier(targetLivingLevel, 0.25f)));
            addBlockCount(event.getEntityLiving());
            if (event.getEntityLiving() instanceof EntityPlayer) {
                ItemStack item = EnchantmentHelper.getEnchantedItem(EnchantmentLiving.LIVING_ENCHANTMENT, event.getEntityLiving());
                doTalking((EntityPlayer) event.getEntityLiving(), item, getEnchantmentNBTTag(item), event);
            }
        }
        if (!(event.getSource().getTrueSource() instanceof EntityPlayer))
            return;
        EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
        ItemStack weapon = player.getHeldItemMainhand();
        NBTTagCompound tag = getEnchantmentNBTTag(weapon);
        if (tag == null)
            return;
        float multiplier = getWeaponEffectivenessModifier(tag);
        event.setAmount(event.getAmount() * multiplier);
        doTalking(player, weapon, tag, event);

    }

    @SubscribeEvent
    public static void onAnvilRepair(AnvilRepairEvent event) {
        NBTTagCompound outputTag = getEnchantmentNBTTag(event.getItemResult());
        NBTTagCompound inputTag = getEnchantmentNBTTag(event.getItemInput());
        if (outputTag == null || inputTag == null)
            return;
        outputTag.setDouble(XP, inputTag.getDouble(XP));
        outputTag.setInteger(LEVEL, xpToLvl(outputTag.getDouble(XP)));
        outputTag.setFloat(EFFECTIVENESS, getWeaponEffectivenessModifier(outputTag));
        outputTag.setFloat(PERSONALITY, inputTag.getFloat(PERSONALITY));
        outputTag.setString(PERSONALITY_NAME, Personality.getPersonality(inputTag).name);
        outputTag.setInteger(KILL_COUNT, inputTag.getInteger(KILL_COUNT));
        outputTag.setInteger(HIT_COUNT, inputTag.getInteger(HIT_COUNT));
        outputTag.setInteger(USAGE_COUNT, inputTag.getInteger(USAGE_COUNT));
    }

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        NBTTagCompound outputTag = getEnchantmentNBTTag(event.getOutput());
        NBTTagCompound inputTag = getEnchantmentNBTTag(event.getLeft());
        if (outputTag == null || inputTag == null)
            return;
        outputTag.setDouble(XP, inputTag.getDouble(XP));
        outputTag.setInteger(LEVEL, xpToLvl(outputTag.getDouble(XP)));
        outputTag.setFloat(EFFECTIVENESS, getWeaponEffectivenessModifier(outputTag));
        outputTag.setFloat(PERSONALITY, inputTag.getFloat(PERSONALITY));
        outputTag.setString(PERSONALITY_NAME, Personality.getPersonality(inputTag).name);
        outputTag.setInteger(KILL_COUNT, inputTag.getInteger(KILL_COUNT));
        outputTag.setInteger(HIT_COUNT, inputTag.getInteger(HIT_COUNT));
        outputTag.setInteger(USAGE_COUNT, inputTag.getInteger(USAGE_COUNT));
    }

    @SubscribeEvent
    public static void onHoeUse(UseHoeEvent event) {
        if (event.getWorld().isRemote)
            return;
        EntityPlayer player = event.getEntityPlayer();
        ItemStack heldItem = player.getHeldItemMainhand();
        NBTTagCompound tag = getEnchantmentNBTTag(heldItem);
        if (tag == null)
            return;
        Block block = event.getWorld().getBlockState(event.getPos()).getBlock();
        if (!(block instanceof BlockGrass) && block != Blocks.DIRT)
            return;
        tag.setInteger(USAGE_COUNT, tag.getInteger(USAGE_COUNT) + 1);
        doExpDrop(player, event.getPos(), 1);
        doTalking(player, heldItem, tag, event);
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getWorld().isRemote)
            return;
        EntityPlayer player = event.getPlayer();
        ItemStack heldItem = player.getHeldItemMainhand();
        NBTTagCompound tag = getEnchantmentNBTTag(heldItem);
        if (tag == null)
            return;
        if (!isToolEffective(heldItem, event.getState()))
            return;
        tag.setInteger(USAGE_COUNT, tag.getInteger(USAGE_COUNT) + 1);
        doExpDrop(player, event.getPos(), LivingConfig.getXPForBlock(player.world, event.getPos(), event.getState()));
        doTalking(player, heldItem, tag, event);
    }

    @SubscribeEvent
    public static void breakSpeedEvent(PlayerEvent.BreakSpeed event) {
        ItemStack item = event.getEntityPlayer().getHeldItemMainhand();
        NBTTagCompound tag = getEnchantmentNBTTag(item);
        if (tag == null)
            return;
        if (!LivingConfig.general.effectivenessAffectsAllBlocks && !isToolEffective(item, event.getState()))
            return;
        float multiplier = getToolEffectivenessModifier(tag);
        event.setNewSpeed(event.getNewSpeed() * multiplier);
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        EnchantmentLiving.LIVING_ENCHANTMENT = new EnchantmentLiving(Enchantment.Rarity.VERY_RARE, EnumEnchantmentType.ALL, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.FEET, EntityEquipmentSlot.HEAD, EntityEquipmentSlot.LEGS});
        Personality.HEROBRINE = new Personality(0, "Herobrine",
                new String[]{ //Use
                        "Herobrine"
                },
                10,
                new String[]{ //Kill
                        "Herobrine"
                },
                10,
                new String[]{ //Death
                        "Herobrine"
                },
                new String[]{ //Level Up
                        "Herobrine"
                },
                new String[]{ //On Hurt
                        "Herobrine"
                },
                10,
                new String[]{ //Twenty percent
                        "Herobrine ($durability durability remaining)",
                },
                new String[]{ //Five percent
                        "Herobrine ($durability durability remaining)",
                });
        if (Loader.isModLoaded("enderio")) {
            LivingConfig.createEnderIOEnchantRecipe();
        }
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "livingXPOrb"), EntityLivingXPOrb.class, "livingXPOrb", 0, instance, 100, 1, true);
        LootInjector.init();
        LootFunctionManager.registerFunction(new EnchantLiving.Serializer());
        proxy.init(event);
        Blocks.BRICK_BLOCK.setHarvestLevel("pickaxe", 1);
        Blocks.END_STONE.setHarvestLevel("pickaxe", 1);
        Blocks.STICKY_PISTON.setHarvestLevel("pickaxe", 0);
        Blocks.PISTON.setHarvestLevel("pickaxe", 0);
        Blocks.FURNACE.setHarvestLevel("pickaxe", 0);
        Blocks.LIT_FURNACE.setHarvestLevel("pickaxe", 0);
        Blocks.STANDING_SIGN.setHarvestLevel("axe", 0);
        Blocks.STONE_STAIRS.setHarvestLevel("pickaxe", 1);
        Blocks.IRON_DOOR.setHarvestLevel("pickaxe", 1);
        Blocks.ACACIA_FENCE.setHarvestLevel("axe", 0);
        Blocks.BIRCH_FENCE.setHarvestLevel("axe", 0);
        Blocks.DARK_OAK_FENCE.setHarvestLevel("axe", 0);
        Blocks.JUNGLE_FENCE.setHarvestLevel("axe", 0);
        Blocks.NETHER_BRICK_FENCE.setHarvestLevel("pickaxe", 0);
        Blocks.OAK_FENCE.setHarvestLevel("axe", 0);
        Blocks.SPRUCE_FENCE.setHarvestLevel("axe", 0);
        Blocks.GLOWSTONE.setHarvestLevel("pickaxe", 0);
        Blocks.TRAPDOOR.setHarvestLevel("axe", 0);
        Blocks.STONEBRICK.setHarvestLevel("pickaxe", 1);
        Blocks.IRON_BARS.setHarvestLevel("pickaxe", 1);
        Blocks.STONE_BRICK_STAIRS.setHarvestLevel("pickaxe", 1);
        Blocks.BRICK_STAIRS.setHarvestLevel("pickaxe", 1);
        Blocks.NETHER_BRICK.setHarvestLevel("pickaxe", 1);
        Blocks.NETHER_BRICK_STAIRS.setHarvestLevel("pickaxe", 1);
        Blocks.WOODEN_SLAB.setHarvestLevel("axe", 0);
        Blocks.DOUBLE_WOODEN_SLAB.setHarvestLevel("axe", 0);
        Blocks.SANDSTONE_STAIRS.setHarvestLevel("pickaxe", 1);
        Blocks.ACACIA_STAIRS.setHarvestLevel("axe", 0);
        Blocks.COBBLESTONE_WALL.setHarvestLevel("pickaxe", 1);
        Blocks.ANVIL.setHarvestLevel("pickaxe", 1);
        Blocks.REDSTONE_BLOCK.setHarvestLevel("pickaxe", 1);
        Blocks.HOPPER.setHarvestLevel("pickaxe", 1);
        Blocks.QUARTZ_BLOCK.setHarvestLevel("pickaxe", 1);
        Blocks.QUARTZ_STAIRS.setHarvestLevel("pickaxe", 1);
        Blocks.DROPPER.setHarvestLevel("pickaxe", 1);
        Blocks.DISPENSER.setHarvestLevel("pickaxe", 1);
        Blocks.COAL_BLOCK.setHarvestLevel("pickaxe", 1);
        Blocks.OAK_STAIRS.setHarvestLevel("axe", 0);
        Blocks.CRAFTING_TABLE.setHarvestLevel("axe", 0);
        Blocks.ACACIA_FENCE_GATE.setHarvestLevel("axe", 0);
        Blocks.BIRCH_FENCE_GATE.setHarvestLevel("axe", 0);
        Blocks.DARK_OAK_FENCE_GATE.setHarvestLevel("axe", 0);
        Blocks.JUNGLE_FENCE_GATE.setHarvestLevel("axe", 0);
        Blocks.OAK_FENCE_GATE.setHarvestLevel("axe", 0);
        Blocks.SPRUCE_FENCE_GATE.setHarvestLevel("axe", 0);
        Blocks.CAULDRON.setHarvestLevel("pickaxe", 1);
        Blocks.COCOA.setHarvestLevel("axe", 0);
        Blocks.BIRCH_STAIRS.setHarvestLevel("axe", 0);
        Blocks.DARK_OAK_STAIRS.setHarvestLevel("axe", 0);
        Blocks.JUNGLE_STAIRS.setHarvestLevel("axe", 0);
        Blocks.HARDENED_CLAY.setHarvestLevel("pickaxe", 1);
        Blocks.STAINED_HARDENED_CLAY.setHarvestLevel("pickaxe", 1);
        Blocks.IRON_TRAPDOOR.setHarvestLevel("pickaxe", 1);
        Blocks.PRISMARINE.setHarvestLevel("pickaxe", 1);
        Blocks.RED_SANDSTONE_STAIRS.setHarvestLevel("pickaxe", 1);
        Blocks.DOUBLE_STONE_SLAB2.setHarvestLevel("pickaxe", 1);
        Blocks.STONE_SLAB2.setHarvestLevel("pickaxe", 1);
        Blocks.ACACIA_DOOR.setHarvestLevel("axe", 0);
        Blocks.BIRCH_DOOR.setHarvestLevel("axe", 0);
        Blocks.DARK_OAK_DOOR.setHarvestLevel("axe", 0);
        Blocks.JUNGLE_DOOR.setHarvestLevel("axe", 0);
        Blocks.OAK_DOOR.setHarvestLevel("axe", 0);
        Blocks.SPRUCE_DOOR.setHarvestLevel("axe", 0);
        Blocks.CHORUS_PLANT.setHarvestLevel("axe", 0);
        Blocks.PURPUR_BLOCK.setHarvestLevel("pickaxe", 1);
        Blocks.PURPUR_PILLAR.setHarvestLevel("pickaxe", 1);
        Blocks.PURPUR_STAIRS.setHarvestLevel("pickaxe", 1);
        Blocks.PURPUR_DOUBLE_SLAB.setHarvestLevel("pickaxe", 1);
        Blocks.SPRUCE_STAIRS.setHarvestLevel("axe", 0);
        Blocks.PURPUR_SLAB.setHarvestLevel("pickaxe", 1);
        Blocks.END_BRICKS.setHarvestLevel("pickaxe", 1);
        Blocks.RED_NETHER_BRICK.setHarvestLevel("pickaxe", 1);
        Blocks.BONE_BLOCK.setHarvestLevel("pickaxe", 1);
        Blocks.OBSERVER.setHarvestLevel("pickaxe", 1);
        Blocks.BLACK_SHULKER_BOX.setHarvestLevel("pickaxe", 0);
        Blocks.BLUE_SHULKER_BOX.setHarvestLevel("pickaxe", 0);
        Blocks.BROWN_SHULKER_BOX.setHarvestLevel("pickaxe", 0);
        Blocks.CYAN_SHULKER_BOX.setHarvestLevel("pickaxe", 0);
        Blocks.GRAY_SHULKER_BOX.setHarvestLevel("pickaxe", 0);
        Blocks.GREEN_SHULKER_BOX.setHarvestLevel("pickaxe", 0);
        Blocks.LIGHT_BLUE_SHULKER_BOX.setHarvestLevel("pickaxe", 0);
        Blocks.LIME_SHULKER_BOX.setHarvestLevel("pickaxe", 0);
        Blocks.MAGENTA_SHULKER_BOX.setHarvestLevel("pickaxe", 0);
        Blocks.ORANGE_SHULKER_BOX.setHarvestLevel("pickaxe", 0);
        Blocks.PINK_SHULKER_BOX.setHarvestLevel("pickaxe", 0);
        Blocks.PURPLE_SHULKER_BOX.setHarvestLevel("pickaxe", 0);
        Blocks.RED_SHULKER_BOX.setHarvestLevel("pickaxe", 0);
        Blocks.SILVER_SHULKER_BOX.setHarvestLevel("pickaxe", 0);
        Blocks.WHITE_SHULKER_BOX.setHarvestLevel("pickaxe", 0);
        Blocks.YELLOW_SHULKER_BOX.setHarvestLevel("pickaxe", 0);
        Blocks.BLACK_GLAZED_TERRACOTTA.setHarvestLevel("pickaxe", 1);
        Blocks.BLUE_GLAZED_TERRACOTTA.setHarvestLevel("pickaxe", 1);
        Blocks.BROWN_GLAZED_TERRACOTTA.setHarvestLevel("pickaxe", 1);
        Blocks.CYAN_GLAZED_TERRACOTTA.setHarvestLevel("pickaxe", 1);
        Blocks.GRAY_GLAZED_TERRACOTTA.setHarvestLevel("pickaxe", 1);
        Blocks.GREEN_GLAZED_TERRACOTTA.setHarvestLevel("pickaxe", 1);
        Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA.setHarvestLevel("pickaxe", 1);
        Blocks.LIME_GLAZED_TERRACOTTA.setHarvestLevel("pickaxe", 1);
        Blocks.MAGENTA_GLAZED_TERRACOTTA.setHarvestLevel("pickaxe", 1);
        Blocks.ORANGE_GLAZED_TERRACOTTA.setHarvestLevel("pickaxe", 1);
        Blocks.PINK_GLAZED_TERRACOTTA.setHarvestLevel("pickaxe", 1);
        Blocks.PURPLE_GLAZED_TERRACOTTA.setHarvestLevel("pickaxe", 1);
        Blocks.RED_GLAZED_TERRACOTTA.setHarvestLevel("pickaxe", 1);
        Blocks.SILVER_GLAZED_TERRACOTTA.setHarvestLevel("pickaxe", 1);
        Blocks.WHITE_GLAZED_TERRACOTTA.setHarvestLevel("pickaxe", 1);
        Blocks.YELLOW_GLAZED_TERRACOTTA.setHarvestLevel("pickaxe", 1);
        Blocks.CONCRETE.setHarvestLevel("pickaxe", 1);
        /*for (Block block : ForgeRegistries.BLOCKS.getValuesCollection()) {
            if (block.getHarvestTool(block.getDefaultState()) == null) {
                System.out.println(block+" has null harvest tool");
            }
        }*/
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
        Personality.fillWeightedList();
    }
}