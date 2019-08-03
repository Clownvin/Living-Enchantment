package net.minecraft.client.renderer;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Monitor;
import net.minecraft.client.settings.AbstractOption;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class VirtualScreen implements AutoCloseable {
   private final Minecraft mc;
   private final MonitorHandler field_217627_b;

   public VirtualScreen(Minecraft mcIn) {
      this.mc = mcIn;
      this.field_217627_b = new MonitorHandler(this::createMonitor);
   }

   public Monitor createMonitor(long p_217625_1_) {
      Monitor monitor = new Monitor(this.field_217627_b, p_217625_1_);
      AbstractOption.FULLSCREEN_RESOLUTION.func_216728_a((float)monitor.getVideoModeCount());
      return monitor;
   }

   public MainWindow create(ScreenSize p_217626_1_, String p_217626_2_, String p_217626_3_) {
      return new MainWindow(this.mc, this.field_217627_b, p_217626_1_, p_217626_2_, p_217626_3_);
   }

   public void close() {
      this.field_217627_b.func_216514_a();
   }
}