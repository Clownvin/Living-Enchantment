package net.minecraft.client.renderer;

import java.util.Optional;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenSize {
   public final int width;
   public final int height;
   public final Optional<Integer> fullscreenWidth;
   public final Optional<Integer> fullscreenHeight;
   public final boolean fullscreen;

   public ScreenSize(int p_i51174_1_, int p_i51174_2_, Optional<Integer> p_i51174_3_, Optional<Integer> p_i51174_4_, boolean p_i51174_5_) {
      this.width = p_i51174_1_;
      this.height = p_i51174_2_;
      this.fullscreenWidth = p_i51174_3_;
      this.fullscreenHeight = p_i51174_4_;
      this.fullscreen = p_i51174_5_;
   }
}