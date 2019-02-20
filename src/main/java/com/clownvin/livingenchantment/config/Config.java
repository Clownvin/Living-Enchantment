package com.clownvin.livingenchantment.config;

import com.clownvin.livingenchantment.enchantment.EnchantmentLiving;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Config {

    public static Common COMMON;
    public static Client CLIENT;
    public static ForgeConfigSpec COMMON_SPEC;
    public static ForgeConfigSpec CLIENT_SPEC;

    public static void init() {
        Pair<Common, ForgeConfigSpec> commonPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON_SPEC = commonPair.getRight();
        COMMON = commonPair.getLeft();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_SPEC);
        Pair<Client, ForgeConfigSpec> clientPair = new ForgeConfigSpec.Builder().configure(Client::new);
        CLIENT_SPEC = clientPair.getRight();
        CLIENT = clientPair.getLeft();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_SPEC);
    }

    public static double getXPForBlock(World worldIn, BlockPos pos, IBlockState block) {
        if (COMMON.dynamicBlockXP.get()) {
            return block.getBlockHardness(worldIn, pos) * COMMON.blockXPMultiplier.get() * dynamicBlockModifier;
        }
        return COMMON.blockXPMultiplier.get() * 1;
    }

    public static double getXPForLiving(EntityLivingBase entity) {
        if (COMMON.dynamicKillXP.get()) {
            return entity.getMaxHealth() * COMMON.killXPMultiplier.get() * dynamicKillModifier;
        }
        return COMMON.killXPMultiplier.get() * 1;
    }

    public static void createEnderIOEnchantRecipe() {
        final File file = new File("./config/enderio/recipes/user/living_enchantment.xml");
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        if (file.exists())
            return;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(ENDERIO_ENCHANT_RECIPE.replace("$livingRegName", EnchantmentLiving.LIVING_ENCHANTMENT.getRegistryName().toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //---DEFAULTS---//
    public static final double DEFAULT_XP_MODIFIER = 1.0;
    public static final double DEFAULT_EFFECTIVENESS = 0.032;
    public static final double DEFAULT_ARMOR_EFFECTIVENESS = 0.285;
    public static final double dynamicKillModifier = .12D;
    public static final double dynamicBlockModifier = .75D;
    public static final double vanillaModifier = 3D;

    //---ENDERIO-RECIPE---//
    public static final String ENDERIO_ENCHANT_RECIPE =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<enderio:recipes xmlns:enderio=\"http://enderio.com/recipes\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://enderio.com/recipes recipes.xsd \">\n" +
                    "<recipe name=\"Enchanter: $livingRegName\" required=\"true\" disabled=\"false\"><!-- Not sure what exactly required does... -->\n" +
                    "   <enchanting>\n" +
                    "      <input name=\"minecraft:diamond\" amount=\"8\"/>\n" +
                    "      <enchantment name=\"$livingRegName\" costMultiplier=\"1\"/>\n" +
                    "   </enchanting>\n" +
                    "</recipe>\n" +
                    "\n</enderio:recipes>\n";


    public static class Client {

        //---GENERAL---//
        public ForgeConfigSpec.DoubleValue xpVolume;
        public ForgeConfigSpec.BooleanValue showDialogue;
        public ForgeConfigSpec.BooleanValue showPersonalities;
        public ForgeConfigSpec.IntValue minimumDialogueDelay;

        public Client(ForgeConfigSpec.Builder builder) {
            builder.push("General");
            xpVolume = builder
                    .comment("Changes how loud the XP added by this mod is.")
                    .translation("text.config.xp_volume")
                    .defineInRange("xp_volume", 0.15, 0, 2);
            showDialogue = builder
                    .comment("Changes whether or not living items will talk to you. On by default.")
                    .translation("text.config.show_dialogue")
                    .define("show_dialogue", true);
            showPersonalities = builder
                    .comment("Changes whether personalities show up in tooltip.")
                    .translation("text.config.show_personalities")
                    .define("show_personalities", true);
            minimumDialogueDelay = builder
                    .comment("Changes the minimum delay between (in Milliseconds) times the item will talk")
                    .translation("text.config.minimum_dialogue_delay")
                    .defineInRange("minimum_dialogue_delay", 9000, 0, 1000000);
            builder.pop();
        }
    }

    public static class Common {

        //---GENERAL---//
        public ForgeConfigSpec.DoubleValue levelExpModifier;
        public ForgeConfigSpec.DoubleValue toolEffectivenessPerLevel;
        public ForgeConfigSpec.DoubleValue weaponEffectivenessPerLevel;
        public ForgeConfigSpec.DoubleValue armorEffectivenessPerLEvel;
        public ForgeConfigSpec.DoubleValue killXPMultiplier;
        public ForgeConfigSpec.BooleanValue dynamicKillXP;
        public ForgeConfigSpec.DoubleValue blockXPMultiplier;
        public ForgeConfigSpec.BooleanValue dynamicBlockXP;
        public ForgeConfigSpec.BooleanValue allowForbiddenEnchantments;
        public ForgeConfigSpec.IntValue maxLevel;
        public ForgeConfigSpec.IntValue xpStyle;
        public ForgeConfigSpec.BooleanValue allowArmor;
        public ForgeConfigSpec.BooleanValue xpShare;
        public ForgeConfigSpec.BooleanValue hoeNames;
        public ForgeConfigSpec.IntValue xpFunction;
        public ForgeConfigSpec.BooleanValue checkCanHarvest;
        //public ForgeConfigSpec.BooleanValue effectivenessAffectsAllBlocks;

        //---LOOT---//
        public ForgeConfigSpec.BooleanValue fishingLoot;
        public ForgeConfigSpec.IntValue fishingLootType;
        public ForgeConfigSpec.IntValue fishingLootChance;
        public ForgeConfigSpec.BooleanValue chestLoot;
        public ForgeConfigSpec.IntValue chestLootType;
        public ForgeConfigSpec.IntValue chestLootChance;

        private Common(ForgeConfigSpec.Builder builder) {
            builder.push("General");
            levelExpModifier = builder
                    .comment("Changes how much XP is required for each level.\n0.005 is the min, and would be very fast leveling. 100 is the max, and would take forever to level.\nThe way this number is used is for scaling level xp amounts, as in this: actualXpToLevel = xpToLevel * levelXPModifier")
                    .translation("text.config.level_exp_modifier")
                    .defineInRange("level_exp_modifier", DEFAULT_XP_MODIFIER, 0.005, 1000);
            toolEffectivenessPerLevel = builder
                    .comment("Changes how much faster the tool mines/chops/digs per level. " + DEFAULT_EFFECTIVENESS + " (Roughly 3%) is the default.")
                    .translation("text.config.tool_effectiveness_per_level")
                    .defineInRange("tool_effectiveness_per_level", DEFAULT_EFFECTIVENESS, 0.005, 1000);
            weaponEffectivenessPerLevel = builder
                    .comment("Changes how large the damage increase per level is.  " + DEFAULT_EFFECTIVENESS + " (Roughly 3%) is the default.")
                    .translation("text.config.weapon_effectiveness_per_level")
                    .defineInRange("weapon_effectiveness_per_level", DEFAULT_EFFECTIVENESS, 0.005, 1000);
            armorEffectivenessPerLEvel = builder
                    .comment("Changes how much damage reduction you gain per level.  " + DEFAULT_ARMOR_EFFECTIVENESS + " is the default.\nThe default will reach 80% armor reduction around level 14.\nThis number is then divided by 4, since you can wear 4 pieces of armor.\nBecause of math, no matter what number you pick, you'll never exceed 100% damage reduction, except in very extreme circumstances.")
                    .translation("text.config.armor_effectiveness_per_level")
                    .defineInRange("armor_effectiveness_per_level", DEFAULT_ARMOR_EFFECTIVENESS, 0.005, 1000);
        /*
        builder
                .comment("")
                .translation("text.config.")
                .defineInRange("", DEFAULT_ARMOR_EFFECTIVENESS, 0.005, 1000);
         */
            killXPMultiplier = builder
                    .comment("Changes how much XP each kill gives.")
                    .translation("text.config.kill_xp_multiplier")
                    .defineInRange("kill_xp_multiplier", 3.0, 0.000, 1000);
            dynamicKillXP = builder
                    .comment("If true and not Mending-Style XP Handling, additional kill XP will scale with Mob HP.\nIf false and not Vanilla XP style, kill XP will always be 1 * killXPMultiplier")
                    .translation("text.config.dynamic_kill_xp")
                    .define("dynamic_kill_xp", true);
            blockXPMultiplier = builder
                    .comment("Changes how much XP block break gives.")
                    .translation("text.config.block_xp_multiplier")
                    .defineInRange("block_xp_multiplier", 1.0, 0, 1000);
            dynamicBlockXP = builder
                    .comment("If true, and not Mending-Style XP Handling, additional block break XP will scale with block hardness.\nIf false and not Vanilla XP style, block break XP will always be 1 * blockXPMultiplier")
                    .translation("text.config.dynamic_block_xp")
                    .define("dynamic_block_xp", true);
            allowForbiddenEnchantments = builder
                    .comment("Changes whether Living is incompatible with vanilla damage/efficiency/protection enchantments.")
                    .translation("text.config.allow_forbidden_enchantments")
                    .define("allow_forbidden_enchantments", false);
            maxLevel = builder
                    .comment("Controls the max level cap.")
                    .translation("text.config.max_level")
                    .defineInRange("max_level", 999, 0, 1000000);
            xpStyle = builder
                    .comment("Determines how Living XP is gained.\n0 - Mending-Style: Living works like mending, absorbing XP Orbs\n1 - Original: Gain XP on breaking blocks with tools or killing mobs, \n2 - Original with Orbs: Like original, but breaking blocks/killing drops XP orbs just for living enchantments.")
                    .translation("text.config.xp_style")
                    .defineInRange("xp_style", 2, 0, 2);
            allowArmor = builder
                    .comment("Whether or not you can enchant armor with Living.")
                    .translation("text.config.allow_armor")
                    .define("allow_armor", true);
            xpShare = builder
                    .comment("Determines whether or not living items will share gained XP (or if it will all just go to one)")
                    .translation("text.config.xp_share")
                    .define("xp_share", false);
            hoeNames = builder
                    .comment("Whether to give a custom name to generated hoes.\nThey're not sexual in nature, but some people might not like having their name considered a \"hoe\" name.")
                    .translation("text.config.hoe_names")
                    .define("hoe_names", true);
            checkCanHarvest = builder
                    .comment("Whether to check if player can harvest target block before increasing speed/dropping xp")
                    .translation("text.config.check_can_harvest")
                    .define("check_can_harvest", true);
            xpFunction = builder
                    .comment("Change the whole underlying XP function.\nOptions: 0 = D&D (Original), 1 = Gen 1 (From Pokemon).\nBoth reach level 14 around the same XP count.\nGen 1 has a much steeper curve after 26, but starts out faster.")
                    .translation("text.config.xp_function")
                    .defineInRange("xp_function", 0, 0, 1);
            builder.pop();
            builder.push("Loot");
            fishingLoot = builder
                    .comment("Changes whether fishing rewards living enchantment loot.\nRequires world restart to take affect.")
                    .translation("text.config.fishing_loot")
                    .define("fishing_loot", true);
            fishingLootType = builder
                    .comment("Changes what type of loot is rewarded when fishing. 0 - Just random unique items, 1 - Uniques and enchanted books, 2 - Just enchanted books.\nRequires world restart to take affect.")
                    .translation("text.config.fishing_loot_type")
                    .defineInRange("fishing_loot_type", 1, 0, 2);
            fishingLootChance = builder
                    .comment("Changes chance of getting living enchantment loot from fishing, 1 in [value]. Default is 1 in 1000.\nRequires world restart to take affect.")
                    .translation("text.config.fishing_loot_chance")
                    .defineInRange("fishing_loot_chance", 1000, 1, 100000000);
            chestLoot = builder
                    .comment("Changes whether spawned chests (dungeons, blacksmith, etc) can spawn living enchantment loot.\nRequires world restart to take affect.")
                    .translation("text.config.chest_loot")
                    .define("chest_loot", true);
            chestLootType = builder
                    .comment("Changes what type of loot can spawn in chests. 0 - Just random unique items, 1 - Uniques and enchanted books, 2 - Just enchanted books.\nRequires world restart to take affect.")
                    .translation("text.config.chest_loot_type")
                    .defineInRange("chest_loot_type", 1, 0, 2);
            chestLootChance = builder
                    .comment("Changes chance of getting living enchantment loot from chests, 1 in [value]. Default is 1 in 9.\nRequires world restart to take affect.")
                    .translation("text.config.chest_loot_chance")
                    .defineInRange("chest_loot_chance", 9, 1, 10000);
            builder.pop();
            //TODO See about adding config for every personality?
            //effectivenessAffectsAllBlocks = false;
        }
    }
}
