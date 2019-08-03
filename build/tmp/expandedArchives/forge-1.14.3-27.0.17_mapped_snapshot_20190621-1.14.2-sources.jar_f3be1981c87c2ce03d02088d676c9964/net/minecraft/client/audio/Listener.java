package net.minecraft.client.audio;

import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.openal.AL10;

@OnlyIn(Dist.CLIENT)
public class Listener {
   public static final Vec3d field_216470_a = new Vec3d(0.0D, 1.0D, 0.0D);
   private float gain = 1.0F;

   public void setPosition(Vec3d p_216465_1_) {
      AL10.alListener3f(4100, (float)p_216465_1_.x, (float)p_216465_1_.y, (float)p_216465_1_.z);
   }

   public void setOrientation(Vec3d p_216469_1_, Vec3d p_216469_2_) {
      AL10.alListenerfv(4111, new float[]{(float)p_216469_1_.x, (float)p_216469_1_.y, (float)p_216469_1_.z, (float)p_216469_2_.x, (float)p_216469_2_.y, (float)p_216469_2_.z});
   }

   public void setGain(float gainIn) {
      AL10.alListenerf(4106, gainIn);
      this.gain = gainIn;
   }

   public float getGain() {
      return this.gain;
   }

   public void init() {
      this.setPosition(Vec3d.ZERO);
      this.setOrientation(new Vec3d(0.0D, 0.0D, -1.0D), field_216470_a);
   }
}