package com.clownvin.livingenchantment.world.storage.loot.functions;

import com.clownvin.livingenchantment.LivingEnchantment;
import com.clownvin.livingenchantment.config.Config;
import com.clownvin.livingenchantment.enchantment.EnchantmentLiving;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

import java.util.Random;

public class EnchantLiving extends LootFunction {

    public static final String[] AXE_NAMES = {"Last Chance", "Forsaken Reaver", "Agatha", "Decimation", "Brutality", "Devotion", "Omega, Slayer of Thunder", "Hope's End", "Fate, Tribute of Insanity", "Guillotine", "Wolf", "Severance", "Nethersbane", "Last Laugh", "Ragnarok", "Fury's Gaze", "Axe of Perun", "Forseti's Axe", "Hephaestus's Labrys", "Lightning Axe", "Parashu", "Pangu's Axe", "Paul Bunyan's Axe", "Shango's Axe", "Zeus's Labrys"};
    public static final String[] SWORD_NAMES = {"Chrysaor", "Mmaagha Kamalu", "Thuận Thiên", "Kladenets", "Jokulsnaut", "Flaming Sword", "Cura Si Manjakini", "Kalevanmiekka", "Sword of Laban", "Sword of Victory", "Caladbolg", "Caledfwlch", "Ceard-nan Gallan", "Claiomh Solais", "Cosgarach Mhor", "Cruadh-Chosgarach", "Dyrnwyn", "Fragarach", "Mac an Luin", "Moralltach", "Beagalltach", "Singing Sword of Conaire Mór", "Cruaidin Catutchenn", "Orna", "Mimung", "Nagelring", "Eckesachs", "Balmung", "Nothung", "Blutgang", "Adylok", "Hatheloke", "Hrunting", "Nægling", "Sword of Saint Peter", "Wallace Sword", "Arondight", "Clarent", "Coreiseuse", "Excalibur", "Galatine", "Grail Sword", "Secace", "Sword in the Stone", "Sword with the Red Hilt", "Courtain", "Egeking", "Angurvadal", "Dáinsleif", "Sword of Freyr", "Gram", "Hǫfuð", "Hrotti", "Lævateinn", "Legbiter", "Mistilteinn", "Quern-biter", "Ridill", "Skofnung", "Tyrfing", "Dragvandil", "Gambantienn", "Almace", "Balisarda", "Corrougue", "Durendal", "Froberge", "Hauteclere", "Joyeuse", "Murgleys", "Précieuse", "Sauvagine", "Merveilleuse", "Joan of Arc's sword", "Tizona", "Colada", "Lobera", "Harpe", "Sword of Peleus", "Sword of Damocles", "Sword of justice", "Crocea Mors", "Sword of Attila", "Aruval", "Asi", "Chandrahas", "Girish", "Khanda", "Nandaka", "Nistrimsha", "Pattayadha", "Kusanagi-no-tsurugi", "Totsuka-no-Tsurugi", "Ame-no-Ohabari", "Futsu-no-mitama", "Juuchi Yosamu", "Yawarakai-Te", "Kogitsune Maru", "Kogarasu Maru", "Gan Jian and Mo Ye", "Glory of Ten Powers", "Lü Dongbin's sword", "Chandrahrasa", "Houken", "Khanda", "Szczerbiec", "Grus", "Morgelai", "Guy of Warwick's Sword", "Shamshir-e Zomorrodnegar", "Zulfiqar"};
    public static final String[] PICKAXE_NAMES = {"Excavator", "Chewie", "Dowsing Pick", "Luck", "Lucky", "Fortune", "Crescent Moon", "Zombie Brain Picker", "Earth", "Consumer", "Orefiend", "Oremonkey", "Loki", "Groundmover", "Powerhouse", "Pickers", "Not a Pickaxe", "Mining Artifact", "Old Pick", "New Pick", "Bloody Pick", "Lucky Pick"};
    public static final String[] SHOVEL_NAMES = {"Excavator", "Dirt", "Old Shovel", "Old Spade", "New Shovel", "New Spade", "Blood-soaked Shovel", "Earth", "Consumer", "Knight", "Lucky Shovel", "Hardened Shovel"};
    public static final String[] BOW_NAMES = {"Hornet", "Deliverance", "Steel Hail", "Snipe", "Pierce", "Hurricane", "Vixen", "Pique", "Siren's Cry", "Quintain", "Last Kiss", "Windtalker", "Hush", "Hooty Tooty Aim and Shooty", "Ballista", "Betrayal", "Windlass", "Starstruck", "Heartbeat", "Hell's Whistle", "Rain Maker", "Messenger", "Barrage", "Penetrator", "Arash's Bow", "Fail-not", "Houyi's Bow", "Apollo's Bow", "Artemis's Bow", "Cupid's Bow", "Heracles's Bow", "Eurytus's Bow", "Pinaka", "Vijaya", "Gandiva", "Kodandam", "Shiva Dhanush", "Sharanga", "Kaundinya's Bow", "Sharanga", "Indra's Bow"};
    //Slutty Hoe Names
    public static final String[] HOE_NAMES = {"Ashley", "Carly", "Carlie", "Haley", "Diamond", "Crystal", "Destinee", "Brandy", "Crystal", "Tiffany", "Candy", "Lexie", "Kassandra", "Bernadette", "Alexis", "Britney"};
    public static final String[] HELMET_NAMES = {"Helmet of Rostam", "Helm of Awe", "Tarnhelm", "Goswhit", "Crown of Immortality", "Huliðshjálmr", "Veil of Isis", "Kappa", "Crown of thorns", "Veil of Veronica", "Crown of Lombardy", "Ariadne's Diadem"};
    public static final String[] CHEST_NAMES = {"Chestplate of Achilles", "Chestplate of Beowulf", "Babr-e Bayan (Chestplate)", "Green Chestplate", "Kavacha (Chestplate)"};

    public EnchantLiving(ILootCondition[] lootConditions) {
        super(lootConditions);
    }

    public static LootFunction.Builder<?> getBuilder() {
        return builder((lootConditions -> new EnchantLiving(lootConditions)));
    }

    @Override
    protected ItemStack doApply(ItemStack stack, LootContext context) {
        if (stack.getItem() == Items.BOOK) {
            stack = new ItemStack(Items.ENCHANTED_BOOK);
            EnchantedBookItem.addEnchantment(stack, new EnchantmentData(EnchantmentLiving.LIVING_ENCHANTMENT, 1));
        } else {
            stack.addEnchantment(EnchantmentLiving.LIVING_ENCHANTMENT, 1);
        }
        Random rand = new Random();
        if (stack.getMaxDamage() >= 2)
            stack.setDamage(rand.nextInt((stack.getMaxDamage() / 2)));
        if (stack.getItem() instanceof AxeItem)
            stack.setDisplayName(new StringTextComponent(AXE_NAMES[rand.nextInt(AXE_NAMES.length)]));
        if (stack.getItem() instanceof SwordItem)
            stack.setDisplayName(new StringTextComponent(SWORD_NAMES[rand.nextInt(SWORD_NAMES.length)]));
        if (stack.getItem() instanceof PickaxeItem)
            stack.setDisplayName(new StringTextComponent(PICKAXE_NAMES[rand.nextInt(PICKAXE_NAMES.length)]));
        if (stack.getItem() instanceof ShovelItem)
            stack.setDisplayName(new StringTextComponent(SHOVEL_NAMES[rand.nextInt(SHOVEL_NAMES.length)]));
        if (stack.getItem() instanceof BowItem)
            stack.setDisplayName(new StringTextComponent(BOW_NAMES[rand.nextInt(BOW_NAMES.length)]));
        if (stack.getItem() instanceof HoeItem && Config.COMMON.hoeNames.get())
            stack.setDisplayName(new StringTextComponent(HOE_NAMES[rand.nextInt(HOE_NAMES.length)]));
        LivingEnchantment.resetItem(stack);
        return stack;
    }

    public static class Serializer extends LootFunction.Serializer<EnchantLiving> {
        public Serializer() {
            super(new ResourceLocation(LivingEnchantment.MODID, "make_living_loot"), EnchantLiving.class);
        }

        public void serialize(JsonObject object, EnchantLiving functionClazz, JsonSerializationContext serializationContext) {

        }

        public EnchantLiving deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn) {
            return new EnchantLiving(conditionsIn);
        }
    }
}