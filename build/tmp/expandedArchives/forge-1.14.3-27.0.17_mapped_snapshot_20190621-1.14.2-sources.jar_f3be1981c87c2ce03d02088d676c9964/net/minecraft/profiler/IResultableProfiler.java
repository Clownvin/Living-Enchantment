package net.minecraft.profiler;

import java.util.function.Supplier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IResultableProfiler extends IProfiler {
   /**
    * Start section
    */
   void startSection(String name);

   void startSection(Supplier<String> nameSupplier);

   /**
    * End section
    */
   void endSection();

   void endStartSection(String name);

   @OnlyIn(Dist.CLIENT)
   void endStartSection(Supplier<String> nameSupplier);

   IProfileResult getResults();
}