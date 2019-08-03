package net.minecraft.client.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IDataHolder {
   void setFixedData(String p_setFixedData_1_, Object p_setFixedData_2_);
}