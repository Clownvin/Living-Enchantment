package com.clownvin.livingenchantment;

import com.clownvin.livingenchantment.command.*;
import com.clownvin.livingenchantment.config.Config;
import com.clownvin.livingenchantment.enchantment.EnchantmentLiving;
import com.clownvin.livingenchantment.personality.Personality;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.GrassBlock;
import net.minecraft.block.material.Material;
import net.minecraft.command.CommandSource;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Mod(LivingEnchantment.MODID)
@Mod.EventBusSubscriber(modid = LivingEnchantment.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LivingEnchantment {

    public static final String MODID = "livingenchantment";

    private static final Logger LOGGER = LogManager.getLogger(LivingEnchantment.class);

    public static final int ID = 0;
    public static final int PERSONALITY_NAME = 1;
    public static final int PERSONALITY = 2;
    public static final int LEVEL = 3;
    public static final int EFFECTIVENESS = 4;
    public static final int XP = 5;
    public static final int LAST_TALK = 6;
    public static final int KILL_COUNT = 7;
    public static final int HIT_COUNT = 8;
    public static final int USAGE_COUNT = 9;

    public static final int JUST_UNIQUES = 0;
    public static final int JUST_BOOKS = 2;
    public static final int GEN1 = 1;

    public LivingEnchantment() {
        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(LivingEnchantment::postSetup);
        preinit();
    }

    protected void preinit() {
        EnchantmentLiving.LIVING_ENCHANTMENT = new EnchantmentLiving(Enchantment.Rarity.VERY_RARE, EnchantmentType.ALL, new EquipmentSlotType[]{
            EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND, EquipmentSlotType.CHEST, EquipmentSlotType.FEET, EquipmentSlotType.HEAD, EquipmentSlotType.LEGS});
        Personality.init();
        Config.init();
        if (ModList.get().isLoaded("enderio")) {
            Config.createEnderIOEnchantRecipe();
            LOGGER.debug("Created EnderIO Enchantment recipe.");
        }
        LOGGER.debug("Finished "+MODID+" init...");
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
        if (!Config.COMMON.showNewUpdateNotifications.get())
            return;
        IModInfo info = ModList.get().getModContainerById(MODID).get().getModInfo();
        VersionChecker.CheckResult result = VersionChecker.getResult(info);
        if (result.target == null || !isNewerVersion(info.getVersion().getQualifier(), result.target.getCanonical())) {//result.target.compareTo(Loader.instance().activeModContainer().getVersion()) <= 0) {
            return;
        }
        event.getPlayer().sendMessage(new TranslationTextComponent("text.new_update_notification", MODID+", "+MODID+"-"+result.target.toString()));
    }

    @SubscribeEvent
    public static void postSetup(FMLLoadCompleteEvent event) {
        Personality.fillWeightedList();
    }

    @SubscribeEvent
    public static void dediServerStartingEvent(FMLDedicatedServerSetupEvent event) {
        registerCommands(event.getServerSupplier().get().getCommandManager().getDispatcher());
    }

    @SubscribeEvent
    public static void serverStartingEvent(FMLServerStartingEvent event) {
        registerCommands(event.getCommandDispatcher());
    }

    private static void registerCommands(CommandDispatcher<CommandSource> dispatcher) {
        CommandAddItemXP.register(dispatcher);
        CommandAddItemXP.register(dispatcher);
        CommandListPersonalities.register(dispatcher);
        CommandResetItem.register(dispatcher);
        CommandSetItemLevel.register(dispatcher);
        CommandSetItemXP.register(dispatcher);
        CommandSetPersonality.register(dispatcher);
        LOGGER.debug("Registered commands...");
    }

    public static int getWornLivingLevel(LivingEntity entity) {
        int cumulativeLivingLevel = 0;
        Iterable<ItemStack> gear = entity.getEquipmentAndArmor();
        for (ItemStack stack : gear) {
            if (!(stack.getItem() instanceof ArmorItem))
                continue;
            ListNBT tag = getEnchantmentNBTTag(stack);
            if (tag == null)
                continue;
            cumulativeLivingLevel += tag.getInt(LEVEL);
        }
        return cumulativeLivingLevel;
    }

    public static void addBlockCount(LivingEntity entity) {
        Iterable<ItemStack> gear = entity.getEquipmentAndArmor();
        for (ItemStack stack : gear) {
            if (!(stack.getItem() instanceof ArmorItem))
                continue;
            ListNBT tag = getEnchantmentNBTTag(stack);
            if (tag == null)
                continue;
            tag.set(HIT_COUNT, new IntNBT(tag.getInt(HIT_COUNT) + 1));
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

    public static float getToolEffectivenessModifier(ListNBT tag) {
        return 1 + (float) (tag.getInt(LEVEL) * Config.COMMON.toolEffectivenessPerLevel.get());
    }

    public static float getWeaponEffectivenessModifier(ListNBT tag) {
        return 1 + (float) (tag.getInt(LEVEL) * Config.COMMON.weaponEffectivenessPerLevel.get());
    }

    public static float getArmorEffectivenessModifier(int level, float scale) {
        return 1 + (float) (level * Config.COMMON.armorEffectivenessPerLEvel.get() * scale);
    }

    public static ListNBT getEnchantmentNBTTag(ItemStack stack) {
        ListNBT tag = null;
        for (INBT base : stack.getEnchantmentTagList()) {
            ListNBT other = (ListNBT) base;
            if (!other.getString(ID).equals(EnchantmentLiving.LIVING_ENCHANTMENT.getRegistryName().toString()))
                continue;
            tag = other;
            break;
        }
        return tag;
    }

    public static void doTalking(PlayerEntity player, ItemStack item, ListNBT tag, Event reason) {
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

    public static void talk(PlayerEntity player, ItemStack stack, String message) {
        talk(player, stack, message, Config.CLIENT.minimumDialogueDelay.get());
    }

    public static void talk(PlayerEntity player, ItemStack stack, String message, int minimumDialogueDelay) {
        ListNBT tag = getEnchantmentNBTTag(stack);
        if (System.currentTimeMillis() - tag.getDouble(LAST_TALK) < minimumDialogueDelay) {
            if (tag.getDouble(LAST_TALK) > System.currentTimeMillis())
                tag.set(LAST_TALK, new DoubleNBT(System.currentTimeMillis()));
            return;
        }
        tag.set(LAST_TALK, new DoubleNBT(System.currentTimeMillis()));
        float durability = (1.0f - (stack.getDamage() / (float) stack.getMaxDamage())) * 100.0f;
        message = message.replace("$user", player.getName().getFormattedText()).replace("$level", "" + tag.getInt(LEVEL)).replace("$durability", String.format("%.1f", durability) + "%");
        player.sendMessage(new TranslationTextComponent(stack.getDisplayName().getFormattedText() + ": " + message));
    }

    public static boolean isMaxLevel(ListNBT tag) {
        return tag.getInt(LEVEL) == Config.COMMON.maxLevel.get() && xpToLvl(tag.getDouble(XP)) >= Config.COMMON.maxLevel.get();
    }

    public static void resetItem(ItemStack item) {
        ListNBT tag = getEnchantmentNBTTag(item);
        if (tag == null) {
            return; // It's not enchanted..
        }
        tag.set(XP, new DoubleNBT(0.0));
        tag.set(LEVEL, new IntNBT(0));
        tag.set(EFFECTIVENESS, new FloatNBT(getWeaponEffectivenessModifier(tag)));
        tag.set(PERSONALITY, new FloatNBT(0));
        tag.set(PERSONALITY_NAME, new StringNBT("???"));
        tag.set(KILL_COUNT, new IntNBT(0));
        tag.set(HIT_COUNT, new IntNBT(0));
        tag.set(USAGE_COUNT, new IntNBT(0));
    }

    public static List<ItemStack> getAllEquipedLivingItems(PlayerEntity player) {
        List<ItemStack> items = new ArrayList<>(5);
        for (ItemStack i : player.getEquipmentAndArmor()) {
            if (getEnchantmentNBTTag(i) != null)
                items.add(i);
        }
        return items;
    }

    public static ItemStack getRandomLivingItem(PlayerEntity player) {
        List<ItemStack> items = getAllEquipedLivingItems(player);
        return items.get((int) (Math.random() * items.size()));
    }

    public static void doExpDrop(PlayerEntity player, BlockPos pos, double exp) {
        if (Config.COMMON.xpStyle.get() == 0)
            return;
        //if (Config.COMMON.xpStyle.get() == 2) {
        //    player.getEntityWorld().addEntity(new EntityLivingXPOrb(player.world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, exp));
        //} else if (Config.COMMON.xpStyle.get() == 1) {
            if (Config.COMMON.xpShare.get())
                addExp(player, exp);
            else {
                ItemStack enchantedItem = getRandomLivingItem(player);
                addExp(player, enchantedItem, getEnchantmentNBTTag(enchantedItem), exp);
            }
        //}
    }

    public static void addExp(final PlayerEntity player, final double exp) { //Adds EXP to all
        List<ItemStack> items = getAllEquipedLivingItems(player);
        double itemExp = exp / items.size();
        items.forEach(item -> addExp(player, item, getEnchantmentNBTTag(item), itemExp));
    }

    public static void addExp(final PlayerEntity player, final ItemStack stack, final ListNBT tag, final double exp) {
        int currLevel = xpToLvl(tag.getDouble(XP));
        tag.set(XP, new DoubleNBT(tag.getDouble(XP) + exp));
        int newLevel = xpToLvl(tag.getDouble(XP));
        Personality personality = Personality.getPersonality(tag);
        tag.set(PERSONALITY_NAME, new StringNBT(personality.name));
        if (Config.COMMON.maxLevel.get() <= newLevel) {
            tag.set(LEVEL, new IntNBT(Config.COMMON.maxLevel.get()));
            tag.set(EFFECTIVENESS, new DoubleNBT(getWeaponEffectivenessModifier(tag)));
        } else {
            tag.set(LEVEL, new IntNBT(newLevel));
            tag.set(EFFECTIVENESS, new DoubleNBT(getWeaponEffectivenessModifier(tag)));
        }
        if (newLevel == currLevel) {
            return;
        }
        player.world.playSound(player, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_LEVELUP, player.getSoundCategory(), 0.75F, 0.9F + (float) (Math.random() * 0.2F));
        DistExecutor.runWhenOn(Dist.CLIENT, () -> (() -> {
            if (Config.CLIENT.showDialogue.get())
                talk(player, stack, personality.getOnLevelUp(), 0);
        }));
    }

    public static void setExp(PlayerEntity player, ItemStack stack, ListNBT tag, double exp) {
        tag.set(XP, new DoubleNBT(0));
        addExp(player, stack, tag, exp);
    }

    @SubscribeEvent
    public static void onPlayerPickedUpXP(PlayerPickupXpEvent event) { //Mending style XP
        if (event.getOrb().world.isRemote)
            return;
        if (Config.COMMON.xpStyle.get() != 0)
            return;
        ItemStack stack = getRandomLivingItem(event.getEntityPlayer());
        if (stack.isEmpty())
            return;
        ListNBT tag = getEnchantmentNBTTag(stack);
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
        if (event.getItemStack().getItem() instanceof ArmorItem) {
            int livingLevel = getWornLivingLevel(event.getEntityPlayer());
            if (livingLevel != 0) {
                event.getToolTip().add(new TranslationTextComponent("tooltip.currently_worn"));
                event.getToolTip().add(new TranslationTextComponent("tooltip.damage_reduction", String.format("%.1f", (1 - (1.0F / getArmorEffectivenessModifier(livingLevel, 0.25f))) * 100) + "%" + TextFormatting.BLUE));
            }
        }
        ListNBT tag = getEnchantmentNBTTag(event.getItemStack());
        if (tag == null) {
            return;
        }
        //float multiplier = tag.getFloat(EFFECTIVENESS);
        //TODO Attack Damage update
        //event.getToolTip()commands.debug.started
        event.getToolTip().add(1, new TranslationTextComponent(new TranslationTextComponent("tooltip.lvl", TextFormatting.RESET.toString() + TextFormatting.GREEN.toString() + tag.getInt(LEVEL) + TextFormatting.RESET).getFormattedText()));
        double xp = tag.getDouble(XP), nextLevelXp = lvlToXp(tag.getInt(LEVEL) + 1);
        event.getToolTip().add(2, new TranslationTextComponent("tooltip.exp", TextFormatting.RESET.toString() + TextFormatting.GREEN.toString() + String.format("%.1f", xp) + TextFormatting.RESET + "/" + TextFormatting.GREEN.toString() + String.format("%.1f", nextLevelXp) + TextFormatting.RESET));
        int i = tag.getInt(KILL_COUNT);
        if (i > 0) {
            event.getToolTip().add(new TranslationTextComponent("tooltip.things_killed", i));
        }
        i = tag.getInt(USAGE_COUNT);
        if (i > 0) {
            Item item = event.getItemStack().getItem();
            if (item instanceof PickaxeItem)
                event.getToolTip().add(new TranslationTextComponent("tooltip.blocks_picked", i));//LanguageMap.getInstance().translateKey("tooltip.blocks_picked").toString());
            else if (item instanceof AxeItem)
                event.getToolTip().add(new TranslationTextComponent("tooltip.blocks_axed", i));
            else if (item instanceof ShovelItem)
                event.getToolTip().add(new TranslationTextComponent("tooltip.blocks_shoveled", i));
            else if (item instanceof HoeItem)
                event.getToolTip().add(new TranslationTextComponent("tooltip.blocks_hoed", i));
            else
                event.getToolTip().add(new TranslationTextComponent("tooltip.blocks_tooled", i));
        }
        i = tag.getInt(HIT_COUNT);
        if (i > 0) {
            event.getToolTip().add(new TranslationTextComponent("tooltip.hits_taken", i));
        }
        if (!Config.CLIENT.showPersonalities.get()) {
            return;
        }
        event.getToolTip().add(3, new TranslationTextComponent("tooltip.personality", TextFormatting.RESET + "" + TextFormatting.GREEN + tag.getString(PERSONALITY_NAME)));
    }

    @SubscribeEvent
    public static void onLivingKilled(LivingDeathEvent event) {
        if (event.getEntityLiving().world.isRemote)
            return;
        DistExecutor.runWhenOn(Dist.CLIENT, () -> (() -> {
            if (Config.CLIENT.showDialogue.get() && event.getEntityLiving() instanceof PlayerEntity && event.getEntityLiving().getHeldItemMainhand().isEnchanted()) {
                ListNBT tag = getEnchantmentNBTTag(event.getEntityLiving().getHeldItemMainhand());
                if (tag != null && Config.CLIENT.showDialogue.get()) {
                    talk((PlayerEntity) event.getEntityLiving(), event.getEntityLiving().getHeldItemMainhand(), Personality.getPersonality(tag).getOnDeath());
                }
            }
        }));
        if (!(event.getSource().getTrueSource() instanceof PlayerEntity))
            return;
        PlayerEntity player = (PlayerEntity) event.getSource().getTrueSource();
        ListNBT tag = null;
        if (event.getSource().damageType.equals("arrow") && player.getHeldItemOffhand().getItem() instanceof BowItem)
            tag = getEnchantmentNBTTag(player.getHeldItemOffhand());
        if (tag == null)
            tag = getEnchantmentNBTTag(player.getHeldItemMainhand());
        if (tag != null)
            tag.set(KILL_COUNT, new IntNBT(tag.getInt(KILL_COUNT) + 1));
        ItemStack item = getRandomLivingItem(player);
        ListNBT talkingTag = getEnchantmentNBTTag(item);
        if (tag == null)
            return;
        doExpDrop(player, event.getEntityLiving().getPosition(), Config.getXPForLiving(event.getEntityLiving()));
        DistExecutor.runWhenOn(Dist.CLIENT, () -> (() -> doTalking(player, item, talkingTag, event)));
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        int targetLivingLevel = getWornLivingLevel(event.getEntityLiving());
        if (targetLivingLevel > 0) {
            event.setAmount(event.getAmount() * (1.0F / getArmorEffectivenessModifier(targetLivingLevel, 0.25f)));
            addBlockCount(event.getEntityLiving());
            if (event.getEntityLiving() instanceof PlayerEntity) {
                ItemStack item = getRandomLivingItem((PlayerEntity) event.getEntityLiving());
                DistExecutor.runWhenOn(Dist.CLIENT, () -> (() -> doTalking((PlayerEntity) event.getEntityLiving(), item, getEnchantmentNBTTag(item), event)));
            }
        }
        if (!(event.getSource().getTrueSource() instanceof PlayerEntity))
            return;
        PlayerEntity player = (PlayerEntity) event.getSource().getTrueSource();
        ItemStack weapon = player.getHeldItemMainhand();
        ListNBT tag = getEnchantmentNBTTag(weapon);
        if (tag == null)
            return;
        float multiplier = getWeaponEffectivenessModifier(tag);
        event.setAmount(event.getAmount() * multiplier);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> (() -> doTalking(player, weapon, tag, event)));
    }

    //@SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        LOGGER.info("On Anvil Update!");
        ListNBT inputTag = getEnchantmentNBTTag(event.getLeft());
        //ListNBT tag = getEnchantmentNBTTag(event.get)
        LOGGER.info(inputTag);
        if (inputTag == null)
            return;
        ItemStack output = event.getLeft().copy();
        LOGGER.info("Input:\n"+event.getLeft().getItem().getRegistryName()+"\n"+inputTag);
        //LOGGER.info("Output pre:\n"+event.getOutput().getItem().getRegistryName()+"\n"+outputTag);
        //Tuple<Tuple<Integer, Integer>, String> data = anvilUpdate(event.getLeft(), event.getRight(), output, event.getCost(), event.getMaterialCost(), event.getName());
        ListNBT outputTag = getEnchantmentNBTTag(output);
        outputTag.set(XP, inputTag.get(XP));
        outputTag.set(LEVEL, new IntNBT(xpToLvl(outputTag.getDouble(XP))));
        outputTag.set(EFFECTIVENESS, new FloatNBT(getWeaponEffectivenessModifier(outputTag)));
        outputTag.set(PERSONALITY, inputTag.get(PERSONALITY));
        outputTag.set(PERSONALITY_NAME, new StringNBT(Personality.getPersonality(inputTag).name));
        outputTag.set(KILL_COUNT, inputTag.get(KILL_COUNT));
        outputTag.set(HIT_COUNT, inputTag.get(HIT_COUNT));
        outputTag.set(USAGE_COUNT, inputTag.get(USAGE_COUNT));
        //output.setDisplayName(new TextComponentString(data.getB()));
        LOGGER.info("Output:\n"+event.getOutput().getItem().getRegistryName()+"\n"+outputTag);
        event.setOutput(output);
        //event.setCost(data.getA().getA());
        //event.setMaterialCost(data.getA().getB());
    }

    @SubscribeEvent
    public static void onAnvilRepair(AnvilRepairEvent event) {
        LOGGER.debug("On Anvil Repair!");
        ListNBT outputTag = getEnchantmentNBTTag(event.getItemResult());
        ListNBT inputTag = getEnchantmentNBTTag(event.getItemInput());
        if (outputTag == null || inputTag == null)
            return;
        LOGGER.debug("Input:\n"+event.getItemInput().getItem().getRegistryName()+"\n"+inputTag);
        LOGGER.debug("Output pre:\n"+event.getItemResult().getItem().getRegistryName()+"\n"+outputTag);
        outputTag.set(XP, inputTag.get(XP));
        outputTag.set(LEVEL, new IntNBT(xpToLvl(outputTag.getDouble(XP))));
        outputTag.set(EFFECTIVENESS, new FloatNBT(getWeaponEffectivenessModifier(outputTag)));
        outputTag.set(PERSONALITY, inputTag.get(PERSONALITY));
        outputTag.set(PERSONALITY_NAME, new StringNBT(Personality.getPersonality(inputTag).name));
        outputTag.set(KILL_COUNT, inputTag.get(KILL_COUNT));
        outputTag.set(HIT_COUNT, inputTag.get(HIT_COUNT));
        outputTag.set(USAGE_COUNT, inputTag.get(USAGE_COUNT));
        LOGGER.debug("Output post:\n"+event.getItemResult().getItem().getRegistryName()+"\n"+outputTag);
    }

    @SubscribeEvent
    public static void onHoeUse(UseHoeEvent event) {
        World world = event.getContext().getWorld();
        if (event.getContext().getWorld().isRemote)
            return;
        PlayerEntity player = event.getEntityPlayer();
        ItemStack heldItem = player.getHeldItemMainhand();
        ListNBT tag = getEnchantmentNBTTag(heldItem);
        if (tag == null)
            return;
        Block block = world.getBlockState(event.getContext().getPos()).getBlock();
        if (!(block instanceof GrassBlock) && block != Blocks.DIRT)
            return;
        tag.set(USAGE_COUNT, new IntNBT(tag.getInt(USAGE_COUNT) + 1));
        doExpDrop(player, event.getContext().getPos(), 1);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> (() -> doTalking(player, heldItem, tag, event)));
    }

    public static boolean isToolEffective(ItemStack stack, BlockState state) {
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
            return material == Material.WOOD || material == Material.PLANTS || material == Material.BAMBOO || material == Material.BAMBOO_SAPLING || material == Material.GOURD;
        }
        if (type == ToolType.SHOVEL) {
            return material == Material.EARTH || material == Material.SAND || material == Material.SNOW || material == Material.CLAY;
        }
        return false;
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getWorld().isRemote())
            return;
        PlayerEntity player = event.getPlayer();
        ItemStack heldItem = player.getHeldItemMainhand();
        if (event.getState().getBlockHardness(event.getWorld(), event.getPos()) <= 0 || (Config.COMMON.checkIsToolEffective.get() && !isToolEffective(heldItem, event.getState())))
            return;
        ListNBT tag = getEnchantmentNBTTag(heldItem);
        if (tag == null)
            return;
        tag.set(USAGE_COUNT, new IntNBT(tag.getInt(USAGE_COUNT) + 1));
        doExpDrop(player, event.getPos(), Config.getXPForBlock(player.world, event.getPos(), event.getState()));
        DistExecutor.runWhenOn(Dist.CLIENT, () -> (() -> doTalking(player, heldItem, tag, event)));
    }

    @SubscribeEvent
    public static void breakSpeedEvent(PlayerEvent.BreakSpeed event) {
        ItemStack heldItem = event.getEntityPlayer().getHeldItemMainhand();
        if (Config.COMMON.checkIsToolEffective.get() && !isToolEffective(heldItem, event.getState()))
            return;
        ListNBT tag = getEnchantmentNBTTag(heldItem);
        if (tag == null)
            return;
        float multiplier = getToolEffectivenessModifier(tag);
        event.setNewSpeed(event.getNewSpeed() * multiplier);
    }
}