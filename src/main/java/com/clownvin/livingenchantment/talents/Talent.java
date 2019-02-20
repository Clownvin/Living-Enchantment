package com.clownvin.livingenchantment.talents;

import com.clownvin.livingenchantment.LivingEnchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;

import javax.annotation.Nullable;
import java.util.Collection;

@Mod.EventBusSubscriber
public abstract class Talent extends IForgeRegistryEntry.Impl<Talent> {

    private static IForgeRegistry<Talent> registry;

    @SubscribeEvent
    public static void createRegistry(RegistryEvent.NewRegistry event) {
        RegistryBuilder<Talent> builder = new RegistryBuilder<>();
        registry = builder.setName(new ResourceLocation(LivingEnchantment.MODID, "TalentRegistry"))
                .setType(Talent.class).create();
    }

    @SubscribeEvent
    public static void registerTalents(RegistryEvent.Register<Talent> event) {
        event.getRegistry().registerAll(); //TODO TODO TODO Add talents
    }

    @Nullable
    public static Talent getTalent(ResourceLocation location) {
        return registry.getValue(location);
    }

    public static ResourceLocation getKey(Talent talent) {
        return registry.getKey(talent);
    }

    public static synchronized IForgeRegistry<Talent> getRegistry() {
        return registry;
    }

    public static Talent getTalent(String talent) {
        return registry.getValue(new ResourceLocation(talent.toLowerCase()));
    }

    public final String name;

    public Talent(String name) {
        setRegistryName(LivingEnchantment.MODID, name.toLowerCase());
        this.name = name;
    }

    public abstract boolean affects(ItemStack stack);

    protected abstract void perform(Event event, ItemStack performer, EntityLivingBase user);

    @Override
    public String toString() {
        return getRegistryName().toString();
    }
}