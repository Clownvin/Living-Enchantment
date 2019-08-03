package net.minecraft.client.audio;

import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChannelManager {
   private final Set<ChannelManager.Entry> channels = Sets.newIdentityHashSet();
   private final SoundSystem sndSystem;
   private final Executor soundExecutor;

   public ChannelManager(SoundSystem p_i50894_1_, Executor p_i50894_2_) {
      this.sndSystem = p_i50894_1_;
      this.soundExecutor = p_i50894_2_;
   }

   public ChannelManager.Entry createChannel(SoundSystem.Mode mode) {
      ChannelManager.Entry channelmanager$entry = new ChannelManager.Entry();
      this.soundExecutor.execute(() -> {
         SoundSource soundsource = this.sndSystem.func_216403_a(mode);
         if (soundsource != null) {
            channelmanager$entry.field_217893_b = soundsource;
            this.channels.add(channelmanager$entry);
         }

      });
      return channelmanager$entry;
   }

   public void func_217897_a(Consumer<Stream<SoundSource>> p_217897_1_) {
      this.soundExecutor.execute(() -> {
         p_217897_1_.accept(this.channels.stream().map((p_217896_0_) -> {
            return p_217896_0_.field_217893_b;
         }).filter(Objects::nonNull));
      });
   }

   public void tick() {
      this.soundExecutor.execute(() -> {
         Iterator<ChannelManager.Entry> iterator = this.channels.iterator();

         while(iterator.hasNext()) {
            ChannelManager.Entry channelmanager$entry = iterator.next();
            channelmanager$entry.field_217893_b.func_216434_i();
            if (channelmanager$entry.field_217893_b.func_216435_g()) {
               channelmanager$entry.release();
               iterator.remove();
            }
         }

      });
   }

   public void releaseAll() {
      this.channels.forEach(ChannelManager.Entry::release);
      this.channels.clear();
   }

   @OnlyIn(Dist.CLIENT)
   public class Entry {
      private SoundSource field_217893_b;
      private boolean field_217894_c;

      public boolean func_217889_a() {
         return this.field_217894_c;
      }

      public void func_217888_a(Consumer<SoundSource> p_217888_1_) {
         ChannelManager.this.soundExecutor.execute(() -> {
            if (this.field_217893_b != null) {
               p_217888_1_.accept(this.field_217893_b);
            }

         });
      }

      public void release() {
         this.field_217894_c = true;
         ChannelManager.this.sndSystem.release(this.field_217893_b);
         this.field_217893_b = null;
      }
   }
}