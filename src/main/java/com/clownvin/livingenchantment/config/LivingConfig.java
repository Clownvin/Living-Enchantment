package com.clownvin.livingenchantment.config;

import com.clownvin.livingenchantment.LivingEnchantment;
import com.clownvin.livingenchantment.enchantment.EnchantmentLiving;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Config(modid = LivingEnchantment.MODID)
@Config.LangKey("livingenchantment.config.title")
public class LivingConfig {

    @Config.Ignore
    public static final double DEFAULT_XP_MODIFIER = 1.0;
    @Config.Ignore
    public static final double DEFAULT_EFFECTIVENESS = 0.032;
    @Config.Ignore
    public static final double DEFAULT_ARMOR_EFFECTIVENESS = 0.285;
    @Config.Ignore
    public static final double dynamicKillModifier = .12D;
    @Config.Ignore
    public static final double dynamicBlockModifier = .75D;
    @Config.Ignore
    public static final double vanillaModifier = 3D;
    @Config.Ignore
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
    public static General general = new General();
    public static Loot loot = new Loot();
    public static Personalities personalities = new Personalities();

    public static double getXPForBlock(World worldIn, BlockPos pos, IBlockState block) {
        if (general.dynamicBlockXP) {
            return block.getBlockHardness(worldIn, pos) * general.blockXPMultiplier * dynamicBlockModifier;
        }
        return general.blockXPMultiplier * 1;
    }

    public static double getXPForLiving(EntityLivingBase entity) {
        if (general.dynamicKillXP) {
            return entity.getMaxHealth() * general.killXPMultiplier * dynamicKillModifier;
        }
        return general.killXPMultiplier * 1;
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

    @Mod.EventBusSubscriber(modid = LivingEnchantment.MODID)
    private static class EventHandler {
        @SubscribeEvent
        public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(LivingEnchantment.MODID))
                ConfigManager.sync(LivingEnchantment.MODID, Config.Type.INSTANCE);
        }
    }

    public static class General {
        @Config.Name("Level XP Modifier (Larger is Slower)")
        @Config.Comment("Changes how much XP is required for each level.\n0.05 is the min, and would be very fast leveling. 100 is the max, and would take forever to level.\nThe way this number is used is for scaling level xp amounts, as in this: actualXpToLevel = xpToLevel * levelXPModifier")
        @Config.RangeDouble(min = 0.05, max = 100.0)
        public double levelExpModifier = DEFAULT_XP_MODIFIER;
        @Config.Name("Effectiveness Per Level (Tool)")
        @Config.Comment("Changes how much faster the tool mines/chops/digs per level. " + DEFAULT_EFFECTIVENESS + " (Roughly 3%) is the default.")
        @Config.RangeDouble(min = 0.0, max = 10.0)
        public double toolEffectivenessPerLevel = DEFAULT_EFFECTIVENESS;
        @Config.Name("Effectiveness Per Level (Weapon)")
        @Config.Comment("Changes how large the damage increase per level is.  " + DEFAULT_EFFECTIVENESS + " (Roughly 3%) is the default.")
        @Config.RangeDouble(min = 0.0, max = 10.0)
        public double weaponEffectivenessPerLevel = DEFAULT_EFFECTIVENESS;
        @Config.Name("Effectiveness Per Level (Armor)")
        @Config.Comment("Changes how much damage reduction you gain per level.  " + DEFAULT_ARMOR_EFFECTIVENESS + " is the default.\nThe default will reach 80% armor reduction around level 14.\nThis number is then divided by 4, since you can wear 4 pieces of armor.\nBecause of math, no matter what number you pick, you'll never exceed 100% damage reduction, ever.")
        @Config.RangeDouble(min = 0.0, max = 10.0)
        public double armorEffectivenessPerLEvel = DEFAULT_ARMOR_EFFECTIVENESS;
        @Config.Name("XP Multiplier (Kill)")
        @Config.Comment("Changes how much XP each kill gives.")
        @Config.RangeDouble(min = 0.0, max = 300.0)
        public double killXPMultiplier = 3.0;
        @Config.Name("Dynamic XP (Kill)")
        @Config.Comment("If true and not Mending-Style XP Handling, additional kill XP will scale with Mob HP.\nIf false and not Vanilla XP style, kill XP will always be 1 * killXPMultiplier")
        public boolean dynamicKillXP = true;
        @Config.Name("XP Multiplier (Block)")
        @Config.Comment("Changes how much XP block break gives.")
        @Config.RangeDouble(min = 0.0, max = 100.0)
        public double blockXPMultiplier = 1.0;
        @Config.Name("Dynamic XP (Block)")
        @Config.Comment("If true, and not Mending-Style XP Handling, additional block break XP will scale with block hardness.\nIf false and not Vanilla XP style, block break XP will always be 1 * blockXPMultiplier")
        public boolean dynamicBlockXP = true;
        @Config.Name("Allow Damage/Efficiency/Protection Enchantments")
        @Config.Comment("Changes whether Living is incompatible with vanilla damage/efficiency enchantments, like sharpness. Protection is also incompatible with living, for armor.")
        public boolean allowDamageEnchantments = false;
        @Config.Name("Max Level")
        @Config.Comment("Sets the max level cap.")
        @Config.RangeInt(min = 0, max = 999999)
        public int maxLevel = 999999;
        @Config.Name("XP Handling Style")
        @Config.Comment("Determines How XP is Gained. \n0 - Mending-Style: Living works like mending, absorbing XP Orbs\n1 - Original: Gain XP on breaking blocks with tools or killing mobs, \n2 - Original with Orbs: Like original, but breaking blocks/killing drops XP orbs just for living enchantments.")
        @Config.RangeInt(min = 0, max = 2)
        public int xpStyle = 2;
        @Config.Name("Allow Living Armor")
        @Config.Comment("Whether or not you can enchant armor with Living.")
        public boolean allowArmor = true;
        @Config.Name("XP Volume")
        @Config.Comment("Changes how loud the XP added by this mod is.")
        @Config.RangeDouble(min = 0.0, max = 200.0)
        public double xpVolume = 0.15;
        @Config.Name("XP Share")
        @Config.Comment("Determines whether or not living items will share gained XP (or if it will all just go to one)")
        public boolean xpShare = false;
        @Config.Name("Hoe Names")
        @Config.Comment("Whether to give a custom name to generated hoes.\nThey're not sexual in nature, but some people might not like having their name considered a \"hoe\" name.")
        public boolean hoeNames = false;
        @Config.Name("XP Function")
        @Config.Comment("Change the whole underlying XP function.\nOptions: 0 = D&D (Original), 1 = Gen 1 (From Pokemon).\nBoth reach level 14 around the same XP count.\nGen 1 has a much steeper curve after 26, but starts out faster.")
        @Config.RangeInt(min = 0, max = 1)
        public int xpFunction = 1;
        @Config.Name("Effectiveness Affects All Blocks")
        @Config.Comment("Changes whether or not Effectiveness affects non-tool-effective blocks. (Ex. Pickaxe vs. Dirt would have increased speed)")
        public boolean effectivenessAffectsAllBlocks = false;
        @Config.Name("Show Ingame Update Notifications")
        @Config.Comment("Changes whether or not the mod will alert you ingame to new updates for your version.")
        public boolean showUpdateNotifications = true;
    }

    public static class Loot {
        @Config.Name("Fishing Loot")
        @Config.Comment("Changes whether fishing rewards living enchantment loot.\nRequires minecraft restart to take affect.")
        public boolean fishingLoot = true;
        @Config.Name("Fishing Loot Type")
        @Config.Comment("Changes what type of loot is rewarded when fishing. 0 - Just random unique items, 1 - Uniques and enchanted books, 2 - Just enchanted books.\nRequires minecraft restart to take affect.")
        @Config.RangeInt(min = 0, max = 2)
        public int fishingLootType = 1;
        @Config.Name("Fishing Loot Chance")
        @Config.Comment("Changes chance of getting living enchantment loot from fishing, 1 in [value]. Default is 1 in 750.\nRequires minecraft restart to take affect.")
        @Config.RangeInt(min = 1, max = 10000)
        public int fishingLootChance = 1000;
        @Config.Name("Chest Loot")
        @Config.Comment("Changes whether spawned chests (dungeons, blacksmith, etc) can spawn living enchantment loot.\nRequires minecraft restart to take affect.")
        public boolean chestLoot = true;
        @Config.Name("Chest Loot Type")
        @Config.Comment("Changes what type of loot can spawn in chests. 0 - Just random unique items, 1 - Uniques and enchanted books, 2 - Just enchanted books.\nRequires minecraft restart to take affect.")
        @Config.RangeInt(min = 0, max = 2)
        public int chestLootType = 1;
        @Config.Name("Chest Loot Chance")
        @Config.Comment("Changes chance of getting living enchantment loot from chests, 1 in [value]. Default is 1 in 3.\nRequires minecraft restart to take affect.")
        @Config.RangeInt(min = 1, max = 10000)
        public int chestLootChance = 9;
    }

    public static class Personalities {
        @Config.Name("Show Dialogue")
        @Config.Comment("Changes whether or not living items will talk to you. On by default.")
        public boolean showDialogue = true;
        @Config.Name("Show Personality")
        @Config.Comment("Changes whether personalities show up in tooltip.")
        public boolean showPersonalities = true;
        @Config.Name("Minimum Dialogue Delay (MS)")
        @Config.Comment("Changes the minimum delay between (in Milliseconds) times the item will talk")
        @Config.RangeInt(min = 0, max = 900000)
        public int minimumDialogueDelay = 9000;
    }

}
