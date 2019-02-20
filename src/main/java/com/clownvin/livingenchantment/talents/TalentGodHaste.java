package com.clownvin.livingenchantment.talents;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraftforge.fml.common.eventhandler.Event;

public class TalentGodHaste extends Talent {

    public TalentGodHaste(String name) {
        super(name);
    }

    @Override
    public boolean affects(ItemStack item) {
        return item.getItem() instanceof ItemTool || item.getItem().getToolClasses(item).parallelStream().anyMatch((toolclass) -> toolclass.equals("pickaxe") || toolclass.equals("axe") || toolclass.equals("shovel") || toolclass.equals("hoe"));
    }

    @Override
    public void perform(Event event, ItemStack performer, EntityLivingBase user) {

    }
}
