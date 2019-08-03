package net.minecraft.client.renderer.entity.model;

import net.minecraft.util.HandSide;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IHasArm {
   void postRenderArm(float scale, HandSide side);
}