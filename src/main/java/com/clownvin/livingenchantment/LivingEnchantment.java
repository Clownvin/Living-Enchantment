package com.clownvin.livingenchantment;

import com.clownvin.livingenchantment.command.*;
import com.clownvin.livingenchantment.config.Config;
import com.clownvin.livingenchantment.enchantment.EnchantmentLiving;
import com.clownvin.livingenchantment.entity.item.EntityLivingXPOrb;
import com.clownvin.livingenchantment.personality.Personality;
import com.clownvin.livingenchantment.world.storage.loot.LootInjector;
import com.clownvin.livingenchantment.world.storage.loot.functions.EnchantLiving;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.versions.forge.ForgeVersion;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mod(LivingEnchantment.MODID)
@Mod.EventBusSubscriber(modid = LivingEnchantment.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LivingEnchantment {

    public static final String MODID = "livingenchantment";
    public static final String CURSEFORGE_PAGE = "https://minecraft.curseforge.com/projects/living-enchantment";

    private static final Logger LOGGER = LogManager.getLogger(LivingEnchantment.class);

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

    public LivingEnchantment() {
        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        //FMLJavaModLoadingContext.get().getModEventBus().addListener(Personality);
        //FMLJavaModLoadingContext.get().getModEventBus().addListener(LivingEnchantment::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(LivingEnchantment::postSetup);
        preinit();
    }

    protected void preinit() {
        EnchantmentLiving.LIVING_ENCHANTMENT = new EnchantmentLiving(Enchantment.Rarity.VERY_RARE, EnumEnchantmentType.ALL, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.FEET, EntityEquipmentSlot.HEAD, EntityEquipmentSlot.LEGS});
        Personality.init();
        Config.init();
        //RenderingRegistry.registerEntityRenderingHandler(EntityLivingXPOrb.class, RenderLivingXPOrb::new);
        LootFunctionManager.registerFunction(new EnchantLiving.Serializer());
        LootInjector.init();
        EntityLivingXPOrb.init();
        if (ModList.get().isLoaded("enderio")) {
            Config.createEnderIOEnchantRecipe();
            LOGGER.debug("Created EnderIO Enchantment recipe.");
        }
        //LootTableList.
        LOGGER.debug("Finished "+MODID+" init...");
    }

    @SubscribeEvent
    public static void onJoinGame(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
        if (!Config.COMMON.showNewUpdateNotifications.get())
            return;
        IModInfo info = ModList.get().getModContainerById(LivingEnchantment.MODID).get().getModInfo();
        VersionChecker.CheckResult result = VersionChecker.getResult(info);
        if (result.target == null || result.target.getCanonical().compareTo(info.getVersion().getQualifier()) <= 0) {
            return;
        }
        event.getPlayer().sendMessage(new TextComponentTranslation("text.new_update_notification", "Living Enchantment: "+result.target.getCanonical()));
    }

    @SubscribeEvent
    public static void postSetup(FMLLoadCompleteEvent event) {
        Personality.fillWeightedList();
    }

    @SubscribeEvent
    public static void dediServerStartingEvent(FMLDedicatedServerSetupEvent event) {
        registerCommands(event.getServerSupplier().get().getCommandManager().getDispatcher());
        //registerReloadListeners(event.getServerSupplier().get().getResourceManager(), event.getServerSupplier().get().getLootTableManager());
    }

    @SubscribeEvent
    public static void serverStartingEvent(FMLServerStartingEvent event) {
        registerCommands(event.getCommandDispatcher());
        //registerReloadListeners(event.getServer().getResourceManager(), event.getServer().getLootTableManager());
    }

    /*
    private static class Looter implements IResourceManagerReloadListener {
        private LootTableManager lootTableManager;

        private Looter(LootTableManager lootTableManager) {
            this.lootTableManager = lootTableManager;
        }

        @Override
        public void onResourceManagerReload(IResourceManager iResourceManager) {
            System.out.println("Reloading resource manager");
            if (Config.COMMON.chestLoot.get()) {
                LootInjector.injectChestLoot(lootTableManager.getLootTableFromLocation(LootTableList.CHESTS_END_CITY_TREASURE));
                LootInjector.injectChestLoot(lootTableManager.getLootTableFromLocation(LootTableList.CHESTS_DESERT_PYRAMID));
                LootInjector.injectChestLoot(lootTableManager.getLootTableFromLocation(LootTableList.CHESTS_JUNGLE_TEMPLE));
                LootInjector.injectChestLoot(lootTableManager.getLootTableFromLocation(LootTableList.CHESTS_IGLOO_CHEST));
                LootInjector.injectChestLoot(lootTableManager.getLootTableFromLocation(LootTableList.CHESTS_SIMPLE_DUNGEON));
                LootInjector.injectChestLoot(lootTableManager.getLootTableFromLocation(LootTableList.CHESTS_STRONGHOLD_LIBRARY));
                LootInjector.injectChestLoot(lootTableManager.getLootTableFromLocation(LootTableList.CHESTS_NETHER_BRIDGE));
                LootInjector.injectChestLoot(lootTableManager.getLootTableFromLocation(LootTableList.CHESTS_WOODLAND_MANSION));
                //LootInjector.injectChestLoot(lootTableManager.getLootTableFromLocation(LootTableList.CHESTS_SPAWN_BONUS_CHEST));
            }
            if (Config.COMMON.fishingLoot.get()) {
                LootInjector.injectFishingLoot(lootTableManager.getLootTableFromLocation(LootTableList.GAMEPLAY_FISHING));
            }
        }
    }

    private static void registerReloadListeners(IReloadableResourceManager manager, LootTableManager lootTableManager) {
        manager.addReloadListener(new Looter(lootTableManager));
    }*/

    private static void registerCommands(CommandDispatcher<CommandSource> dispatcher) {
        CommandAddItemXP.register(dispatcher);
        CommandAddItemXP.register(dispatcher);
        CommandListPersonalities.register(dispatcher);
        CommandResetItem.register(dispatcher);
        CommandSetItemLevel.register(dispatcher);
        CommandSetItemXP.register(dispatcher);
        CommandSetPersonality.register(dispatcher);
        LOGGER.debug("Registered commands...");
        //IResourceManagerReloadListener;
        //Minecraft.getInstance().
        //Minecraft.getInstance().getResourceManager().
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
            cumulativeLivingLevel += tag.getInt(LEVEL);
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
            tag.setInt(HIT_COUNT, tag.getInt(HIT_COUNT) + 1);
        }
    }

    public static int xpToLvl(double xp) {
        if (Config.COMMON.xpFunction.get() == GEN1)
            return (int) Math.pow((5 * (xp / Config.COMMON.levelExpModifier.get())) / 4, 1 / 3.0f) + 1;
        return (int) ((Math.sqrt((9 * (xp / Config.COMMON.levelExpModifier.get())) + 4) + 2) / 9.0);
    }

    public static double lvlToXp(int lvl) {
        if (Config.COMMON.xpFunction.get() == GEN1)
            return (Math.round((4 * (Math.pow(lvl - 1, 3)) / 5)) * Config.COMMON.levelExpModifier.get());
        return ((9 * (lvl * lvl) - (4 * lvl)) * Config.COMMON.levelExpModifier.get());
    }

    public static float getToolEffectivenessModifier(NBTTagCompound tag) {
        return 1 + (float) (tag.getInt(LEVEL) * Config.COMMON.toolEffectivenessPerLevel.get());
    }

    public static float getWeaponEffectivenessModifier(NBTTagCompound tag) {
        return 1 + (float) (tag.getInt(LEVEL) * Config.COMMON.weaponEffectivenessPerLevel.get());
    }

    public static float getArmorEffectivenessModifier(int level, float scale) {
        return 1 + (float) (level * Config.COMMON.armorEffectivenessPerLEvel.get() * scale);
    }

    public static NBTTagCompound getEnchantmentNBTTag(ItemStack stack) {
        NBTTagCompound tag = null;
        for (INBTBase base : stack.getEnchantmentTagList()) {
            NBTTagCompound other = (NBTTagCompound) base;
            if (!other.getString("id").equals(EnchantmentLiving.LIVING_ENCHANTMENT.getRegistryName().toString()))
                continue;
            tag = other;
            break;
        }
        return tag;
    }

    public static void doTalking(EntityPlayer player, ItemStack item, NBTTagCompound tag, Event reason) {
        if (!Config.CLIENT.showDialogue.get())
            return;
        Personality personality = Personality.getPersonality(tag);
        float damagePercent = item.getDamage() / (float) item.getMaxDamage();
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
        talk(player, stack, message, Config.CLIENT.minimumDialogueDelay.get());
    }

    public static void talk(EntityPlayer player, ItemStack stack, String message, int minimumDialogueDelay) {
        NBTTagCompound tag = getEnchantmentNBTTag(stack);
        if (!Config.CLIENT.showDialogue.get()) {
            return;
        }
        if (System.currentTimeMillis() - tag.getLong(LAST_TALK) < minimumDialogueDelay) {
            if (tag.getLong(LAST_TALK) > System.currentTimeMillis())
                tag.setLong(LAST_TALK, System.currentTimeMillis());
            return;
        }
        tag.setLong(LAST_TALK, System.currentTimeMillis());
        float durability = (1.0f - (stack.getDamage() / (float) stack.getMaxDamage())) * 100.0f;
        message = message.replace("$user", player.getName().getFormattedText()).replace("$level", "" + tag.getInt(LEVEL)).replace("$durability", String.format("%.1f", durability) + "%");
        player.sendMessage(new TextComponentString(stack.getDisplayName().getFormattedText() + ": " + message));
    }

    public static boolean isMaxLevel(NBTTagCompound tag) {
        return tag.getInt(LEVEL) == Config.COMMON.maxLevel.get() && xpToLvl(tag.getDouble(XP)) >= Config.COMMON.maxLevel.get();
    }

    public static void resetItem(ItemStack item) {
        NBTTagCompound tag = getEnchantmentNBTTag(item);
        if (tag == null) {
            return; // It's not enchanted..
        }
        tag.setDouble(XP, 0.0);
        tag.setInt(LEVEL, 0);
        tag.setFloat(EFFECTIVENESS, getWeaponEffectivenessModifier(tag));
        tag.setFloat(PERSONALITY, 0);
        tag.setString(PERSONALITY_NAME, "???");
        tag.setInt(KILL_COUNT, 0);
        tag.setInt(HIT_COUNT, 0);
        tag.setInt(USAGE_COUNT, 0);
    }

    public static List<ItemStack> getAllEquipedLivingItems(EntityPlayer player) {
        List<ItemStack> items = new ArrayList<>(5);
        for (ItemStack i : player.getEquipmentAndArmor()) {
            if (getEnchantmentNBTTag(i) != null)
                items.add(i);
        }
        return items;
    }

    public static void doExpDrop(EntityPlayer player, BlockPos pos, double exp) {
        if (Config.COMMON.xpStyle.get() == 0)
            return;
        if (Config.COMMON.xpStyle.get() == 2) {
            player.world.spawnEntity(new EntityLivingXPOrb(player.world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, exp));
        } else if (Config.COMMON.xpStyle.get() == 1) {
            if (Config.COMMON.xpShare.get())
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
        if (Config.COMMON.maxLevel.get() <= newLevel) {
            tag.setInt(LEVEL, Config.COMMON.maxLevel.get());
            tag.setFloat(EFFECTIVENESS, getWeaponEffectivenessModifier(tag));
        } else {
            tag.setInt(LEVEL, newLevel);
            tag.setFloat(EFFECTIVENESS, getWeaponEffectivenessModifier(tag));
        }
        if (newLevel == currLevel) {
            return;
        }
        player.world.playSound(player, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_LEVELUP, player.getSoundCategory(), 0.75F, 0.9F + (float) (Math.random() * 0.2F));
        if (Config.CLIENT.showDialogue.get())
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
        if (Config.COMMON.xpStyle.get() != 0)
            return;
        ItemStack stack = EnchantmentHelper.getEnchantedItem(EnchantmentLiving.LIVING_ENCHANTMENT, event.getEntityPlayer());
        if (stack.isEmpty())
            return;
        NBTTagCompound tag = getEnchantmentNBTTag(stack);
        if (LivingEnchantment.isMaxLevel(tag))
            return;
        int xp = event.getOrb().xpValue == 1 ? 1 : event.getOrb().xpValue / 2;
        if (Config.COMMON.xpShare.get())
            LivingEnchantment.addExp(event.getEntityPlayer(), xp * Config.vanillaModifier);
        else
            LivingEnchantment.addExp(event.getEntityPlayer(), stack, tag, xp * Config.vanillaModifier);
        event.getOrb().xpValue -= xp;
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        //System.out.println("Tooltip");
        if (event.getEntityPlayer() == null)
            return;
        if (event.getItemStack().getItem() instanceof ItemArmor) {
            int livingLevel = getWornLivingLevel(event.getEntityPlayer());
            if (livingLevel != 0) {
                event.getToolTip().add(new TextComponentTranslation("tooltip.currently_worn"));
                event.getToolTip().add(new TextComponentTranslation("tooltip.damage_reduction", String.format("%.1f", (1 - (1.0F / getArmorEffectivenessModifier(livingLevel, 0.25f))) * 100) + "%" + TextFormatting.BLUE));
            }
        }
        NBTTagCompound tag = getEnchantmentNBTTag(event.getItemStack());
        if (tag == null) {
            return;
        }
        //float multiplier = tag.getFloat(EFFECTIVENESS);
        //TODO Attack Damage update
        //event.getToolTip()commands.debug.started
        event.getToolTip().add(1, new TextComponentString(new TextComponentTranslation("tooltip.lvl", TextFormatting.RESET.toString() + TextFormatting.GREEN.toString() + tag.getInt(LEVEL) + TextFormatting.RESET).getFormattedText()));
        double xp = tag.getDouble(XP), nextLevelXp = lvlToXp(tag.getInt(LEVEL) + 1);
        event.getToolTip().add(2, new TextComponentTranslation("tooltip.exp", TextFormatting.RESET.toString() + TextFormatting.GREEN.toString() + String.format("%.1f", xp) + TextFormatting.RESET + "/" + TextFormatting.GREEN.toString() + String.format("%.1f", nextLevelXp) + TextFormatting.RESET));
        int i = tag.getInt(KILL_COUNT);
        if (i > 0) {
            event.getToolTip().add(new TextComponentTranslation("tooltip.things_killed", i));
        }
        i = tag.getInt(USAGE_COUNT);
        if (i > 0) {
            Item item = event.getItemStack().getItem();
            if (item instanceof ItemPickaxe)
                event.getToolTip().add(new TextComponentTranslation("tooltip.blocks_picked", i));//LanguageMap.getInstance().translateKey("tooltip.blocks_picked").toString());
            else if (item instanceof ItemAxe)
                event.getToolTip().add(new TextComponentTranslation("tooltip.blocks_axed", i));
            else if (item instanceof ItemSpade)
                event.getToolTip().add(new TextComponentTranslation("tooltip.blocks_shoveled", i));
            else if (item instanceof ItemHoe)
                event.getToolTip().add(new TextComponentTranslation("tooltip.blocks_hoed", i));
            else
                event.getToolTip().add(new TextComponentTranslation("tooltip.blocks_tooled", i));
        }
        i = tag.getInt(HIT_COUNT);
        if (i > 0) {
            event.getToolTip().add(new TextComponentTranslation("tooltip.hits_taken", i));
        }
        if (!Config.CLIENT.showPersonalities.get()) {
            return;
        }
        event.getToolTip().add(3, new TextComponentTranslation("tooltip.personality", TextFormatting.RESET + "" + TextFormatting.GREEN + tag.getString(PERSONALITY_NAME)));
    }

    @SubscribeEvent
    public static void onLivingKilled(LivingDeathEvent event) {
        if (event.getEntityLiving().world.isRemote)
            return;
        if (Config.CLIENT.showDialogue.get() && event.getEntityLiving() instanceof EntityPlayer && event.getEntityLiving().getHeldItemMainhand().isEnchanted()) {
            NBTTagCompound tag = getEnchantmentNBTTag(event.getEntityLiving().getHeldItemMainhand());
            if (tag != null && Config.CLIENT.showDialogue.get()) {
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
            tag.setInt(KILL_COUNT, tag.getInt(KILL_COUNT) + 1);
        ItemStack item = EnchantmentHelper.getEnchantedItem(EnchantmentLiving.LIVING_ENCHANTMENT, player);
        tag = getEnchantmentNBTTag(item);
        if (tag == null)
            return;
        doExpDrop(player, event.getEntityLiving().getPosition(), Config.getXPForLiving(event.getEntityLiving()));
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

    //@SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        LOGGER.info("On Anvil Update!");
        NBTTagCompound inputTag = getEnchantmentNBTTag(event.getLeft());
        //NBTTagCompound tag = getEnchantmentNBTTag(event.get)
        LOGGER.info(inputTag);
        if (inputTag == null)
            return;
        ItemStack output = event.getLeft().copy();
        LOGGER.info("Input:\n"+event.getLeft().getItem().getRegistryName()+"\n"+inputTag);
        //LOGGER.info("Output pre:\n"+event.getOutput().getItem().getRegistryName()+"\n"+outputTag);
        //Tuple<Tuple<Integer, Integer>, String> data = anvilUpdate(event.getLeft(), event.getRight(), output, event.getCost(), event.getMaterialCost(), event.getName());
        NBTTagCompound outputTag = getEnchantmentNBTTag(output);
        outputTag.setDouble(XP, inputTag.getDouble(XP));
        outputTag.setInt(LEVEL, xpToLvl(outputTag.getDouble(XP)));
        outputTag.setFloat(EFFECTIVENESS, getWeaponEffectivenessModifier(outputTag));
        outputTag.setFloat(PERSONALITY, inputTag.getFloat(PERSONALITY));
        outputTag.setString(PERSONALITY_NAME, Personality.getPersonality(inputTag).name);
        outputTag.setInt(KILL_COUNT, inputTag.getInt(KILL_COUNT));
        outputTag.setInt(HIT_COUNT, inputTag.getInt(HIT_COUNT));
        outputTag.setInt(USAGE_COUNT, inputTag.getInt(USAGE_COUNT));
        //output.setDisplayName(new TextComponentString(data.getB()));
        LOGGER.info("Output:\n"+event.getOutput().getItem().getRegistryName()+"\n"+outputTag);
        event.setOutput(output);
        //event.setCost(data.getA().getA());
        //event.setMaterialCost(data.getA().getB());
    }

    @SubscribeEvent
    public static void onAnvilRepair(AnvilRepairEvent event) {
        LOGGER.debug("On Anvil Repair!");
        NBTTagCompound outputTag = getEnchantmentNBTTag(event.getItemResult());
        NBTTagCompound inputTag = getEnchantmentNBTTag(event.getItemInput());
        if (outputTag == null || inputTag == null)
            return;
        LOGGER.debug("Input:\n"+event.getItemInput().getItem().getRegistryName()+"\n"+inputTag);
        LOGGER.debug("Output pre:\n"+event.getItemResult().getItem().getRegistryName()+"\n"+outputTag);
        outputTag.setDouble(XP, inputTag.getDouble(XP));
        outputTag.setInt(LEVEL, xpToLvl(outputTag.getDouble(XP)));
        outputTag.setFloat(EFFECTIVENESS, getWeaponEffectivenessModifier(outputTag));
        outputTag.setFloat(PERSONALITY, inputTag.getFloat(PERSONALITY));
        outputTag.setString(PERSONALITY_NAME, Personality.getPersonality(inputTag).name);
        outputTag.setInt(KILL_COUNT, inputTag.getInt(KILL_COUNT));
        outputTag.setInt(HIT_COUNT, inputTag.getInt(HIT_COUNT));
        outputTag.setInt(USAGE_COUNT, inputTag.getInt(USAGE_COUNT));
        LOGGER.debug("Output post:\n"+event.getItemResult().getItem().getRegistryName()+"\n"+outputTag);
    }

    @SubscribeEvent
    public static void onHoeUse(UseHoeEvent event) {
        World world = event.getContext().getWorld();
        if (event.getContext().getWorld().isRemote)
            return;
        EntityPlayer player = event.getEntityPlayer();
        ItemStack heldItem = player.getHeldItemMainhand();
        NBTTagCompound tag = getEnchantmentNBTTag(heldItem);
        if (tag == null)
            return;
        Block block = world.getBlockState(event.getContext().getPos()).getBlock();
        if (!(block instanceof BlockGrass) && block != Blocks.DIRT)
            return;
        tag.setInt(USAGE_COUNT, tag.getInt(USAGE_COUNT) + 1);
        doExpDrop(player, event.getContext().getPos(), 1);
        doTalking(player, heldItem, tag, event);
    }

    public static boolean isToolEffective(ItemStack stack, IBlockState state) {
        Material material = state.getMaterial();
        if (stack.getToolTypes().parallelStream().anyMatch((type) -> typeMatchesMaterial(type, material))) {
            return true;
        }
        return stack.getItem().getToolTypes(stack).stream().anyMatch(e -> {LOGGER.debug(state.getBlock().getRegistryName()+", "+e.getName()+", "+state.isToolEffective(e)+", "+state.getHarvestTool()); return state.isToolEffective(e);});
    }

    public static boolean typeMatchesMaterial(ToolType type, Material material) {
        if (type == ToolType.PICKAXE) {
            return material == Material.ROCK || material == Material.IRON || material == Material.ANVIL;
        }
        if (type == ToolType.AXE) {
            return material == Material.WOOD || material == Material.PLANTS || material == Material.VINE;
        }
        if (type == ToolType.SHOVEL) {
            return material == Material.GROUND || material == Material.SAND || material == Material.GRASS || material == Material.SNOW || material == Material.CLAY || material == Material.CRAFTED_SNOW;
        }
        return false;
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getWorld().isRemote())
            return;
        EntityPlayer player = event.getPlayer();
        ItemStack heldItem = player.getHeldItemMainhand();
        if (event.getState().getBlockHardness(event.getWorld(), event.getPos()) <= 0 || (Config.COMMON.checkIsToolEffective.get() && !isToolEffective(heldItem, event.getState())))
            return;
        NBTTagCompound tag = getEnchantmentNBTTag(heldItem);
        if (tag == null)
            return;
        tag.setInt(USAGE_COUNT, tag.getInt(USAGE_COUNT) + 1);
        doExpDrop(player, event.getPos(), Config.getXPForBlock(player.world, event.getPos(), event.getState()));
        doTalking(player, heldItem, tag, event);
    }

    @SubscribeEvent
    public static void breakSpeedEvent(PlayerEvent.BreakSpeed event) {
        ItemStack heldItem = event.getEntityPlayer().getHeldItemMainhand();
        if (Config.COMMON.checkIsToolEffective.get() && !isToolEffective(heldItem, event.getState()))
            return;
        NBTTagCompound tag = getEnchantmentNBTTag(heldItem);
        if (tag == null)
            return;
        float multiplier = getToolEffectivenessModifier(tag);
        event.setNewSpeed(event.getNewSpeed() * multiplier);
    }
}