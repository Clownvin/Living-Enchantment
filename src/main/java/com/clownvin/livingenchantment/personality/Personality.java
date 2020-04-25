package com.clownvin.livingenchantment.personality;

import com.clownvin.livingenchantment.LivingEnchantment;
import com.clownvin.util.Weighted;
import com.clownvin.util.WeightedList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;

import javax.annotation.Nullable;
import java.util.List;


@Mod.EventBusSubscriber
public class Personality extends IForgeRegistryEntry.Impl<Personality> implements Weighted {

    public static Personality HEROBRINE;
    private static final WeightedList<Personality> weightedPersonalityList = new WeightedList<Personality>();
    private static IForgeRegistry<Personality> registry;

    public final int useOdds, killOdds, hurtOdds;
    public final float weight;
    public final String name;
    public final String[] onUse;
    public final String[] onKill;
    public final String[] onDeath;
    public final String[] onLevelUp;
    public final String[] onHurt;
    public final String[] twentyPercent;
    public final String[] fivePercent;

    public Personality(float weight, String name, String[] onUse, int useOdds, String[] onKill, int killOdds, String[] onDeath, String[] onLevelUp, String[] onHurt, int hurtOdds, String[] twentyPercent, String[] fivePercent) {
        //this.resourceLocation = new ResourceLocation(modid, name);
        setRegistryName(LivingEnchantment.MODID, name.toLowerCase());
        this.name = name;
        this.onUse = onUse;
        this.useOdds = useOdds;
        this.onKill = onKill;
        this.killOdds = killOdds;
        this.onDeath = onDeath;
        this.onLevelUp = onLevelUp;
        this.onHurt = onHurt;
        this.hurtOdds = hurtOdds;
        this.weight = weight;
        this.twentyPercent = twentyPercent;
        this.fivePercent = fivePercent;
    }

    @SubscribeEvent
    public static void createRegistry(RegistryEvent.NewRegistry event) {
        RegistryBuilder<Personality> builder = new RegistryBuilder<>();
        registry = builder.setName(new ResourceLocation(LivingEnchantment.MODID, "personalityRegistry"))
                .setType(Personality.class).create();
    }

    @SubscribeEvent
    public static void registerPersonalities(RegistryEvent.Register<Personality> event) {
        Personality[] personalityList = PersonalityLoader.getPersonalities();
        for (Personality p : personalityList) {
            event.getRegistry().register(p);
        }
        event.getRegistry().registerAll();
    }

    public static void fillWeightedList() {
        int i = 0;
        for (Personality p : registry.getValuesCollection()) {
            if (p.getWeight() <= 0)
                continue;
            weightedPersonalityList.add(p);
            i++;
        }
        System.out.println("Filled weighted personality list with "+i+" personalities.");
    }

    @Nullable
    public static Personality getPersonality(ResourceLocation location) {
        return registry.getValue(location);
    }

    public static ResourceLocation getKey(Personality personality) {
        return registry.getKey(personality);
    }

    public static synchronized IForgeRegistry<Personality> getRegistry() {
        return registry;
    }
    //public final ResourceLocation resourceLocation;

    //private Personality(String modid, int getWeight, String name, String[] onUse, int useOdds, String[] onKill, int killOdds, String[] onDeath, String[] onValuableItemPickup, int itemPickupOdds, String[] onLevelUp, String[] onHurt, int hurtOdds) {
    //   this(new ResourceLocation(modid, name.toUpperCase()) name, onUse, useOdds, onKill, killOdds, onDeath, onValuableItemPickup, itemPickupOdds, onLevelUp, onHurt, hurtOdds, getWeight);
    //  }

    private static Personality getPersonality(float personality) {
        //System.out.println("Getting personality for: "+personality);
        if (weightedPersonalityList.size() == 0)
            return HEROBRINE;
        return weightedPersonalityList.get(personality);
    }

    public static Personality getPersonality(NBTTagCompound tag) {
        float f = tag.getFloat(LivingEnchantment.PERSONALITY);
        while (f <= 0.0f || f > 1.0f) {
            f = (float) Math.random();
        }
        tag.setFloat(LivingEnchantment.PERSONALITY, f);
        return getPersonality(f);
    }

    public static Personality getPersonality(String personality) {
        return registry.getValue(new ResourceLocation(personality.toLowerCase()));
    }

    public static float getValue(Personality personality) {
        return weightedPersonalityList.getVal(personality);
    }

    @Override
    public float getWeight() {
        return weight;
    }

    private String getRandomPhrase(String[] phrases) {
        return phrases[(int) (Math.random() * (phrases.length - 1))];
    }

    public String getTwentyPercent() {
        return getRandomPhrase(twentyPercent);//[(int) (Math.random() * twentyPercent.length)];
    }

    public String getFivePercent() {
        return getRandomPhrase(fivePercent);//[(int) (Math.random() * fivePercent.length)];
    }

    public String getOnTargetHurt() {
        return getRandomPhrase(onHurt);//[(int) (Math.random() * onHurt.length)];
    }

    public String getOnUse() {
        return getRandomPhrase(onUse);//[(int) (Math.random() * onUse.length)];
    }

    public String getOnKill() {
        return getRandomPhrase(onKill);//[(int) (Math.random() * onKill.length)];
    }

    public String getOnDeath() {
        return getRandomPhrase(onDeath);//[(int) (Math.random() * onDeath.length)];
    }

    public String getOnLevelUp() {
        return getRandomPhrase(onLevelUp);//[(int) (Math.random() * onLevelUp.length)];
    }

    @Override
    public String toString() {
        return getRegistryName().toString();
    }
}
