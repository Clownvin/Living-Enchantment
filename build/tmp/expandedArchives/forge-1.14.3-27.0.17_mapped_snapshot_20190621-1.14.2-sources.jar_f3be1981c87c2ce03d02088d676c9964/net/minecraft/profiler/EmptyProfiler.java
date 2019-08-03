package net.minecraft.profiler;

import java.util.function.Supplier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EmptyProfiler implements IResultableProfiler {
   public static final EmptyProfiler INSTANCE = new EmptyProfiler();

   public void startTick() {
   }

   public void endTick() {
   }

   /**
    * Start section
    */
   public void startSection(String name) {
   }

   public void startSection(Supplier<String> nameSupplier) {
   }

   /**
    * End section
    */
   public void endSection() {
   }

   public void endStartSection(String name) {
   }

   @OnlyIn(Dist.CLIENT)
   public void endStartSection(Supplier<String> nameSupplier) {
   }

   public IProfileResult getResults() {
      return EmptyProfileResult.field_219926_a;
   }
}