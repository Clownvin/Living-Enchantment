package net.minecraft.client.audio;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.GameSettings;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

@OnlyIn(Dist.CLIENT)
public class SoundEngine {
   private static final Marker LOG_MARKER = MarkerManager.getMarker("SOUNDS");
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Set<ResourceLocation> UNABLE_TO_PLAY = Sets.newHashSet();
   public final SoundHandler sndHandler;
   private final GameSettings options;
   private boolean loaded;
   private final SoundSystem sndSystem = new SoundSystem();
   private final Listener listener = this.sndSystem.getListener();
   private final AudioStreamManager field_217939_i;
   private final SoundEngineExecutor executor = new SoundEngineExecutor();
   private final ChannelManager channelManager = new ChannelManager(this.sndSystem, this.executor);
   private int ticks;
   private final Map<ISound, ChannelManager.Entry> field_217942_m = Maps.newHashMap();
   private final Multimap<SoundCategory, ISound> field_217943_n = HashMultimap.create();
   private final List<ITickableSound> tickableSounds = Lists.newArrayList();
   private final Map<ISound, Integer> delayedSounds = Maps.newHashMap();
   private final Map<ISound, Integer> playingSoundsStopTime = Maps.newHashMap();
   private final List<ISoundEventListener> listeners = Lists.newArrayList();
   private final List<Sound> soundsToPreload = Lists.newArrayList();

   public SoundEngine(SoundHandler p_i50892_1_, GameSettings p_i50892_2_, IResourceManager p_i50892_3_) {
      this.sndHandler = p_i50892_1_;
      this.options = p_i50892_2_;
      this.field_217939_i = new AudioStreamManager(p_i50892_3_);
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.sound.SoundSetupEvent(this));
   }

   public void reload() {
      UNABLE_TO_PLAY.clear();

      for(SoundEvent soundevent : Registry.SOUND_EVENT) {
         ResourceLocation resourcelocation = soundevent.getName();
         if (this.sndHandler.getAccessor(resourcelocation) == null) {
            LOGGER.warn("Missing sound for event: {}", (Object)Registry.SOUND_EVENT.getKey(soundevent));
            UNABLE_TO_PLAY.add(resourcelocation);
         }
      }

      this.unload();
      this.load();
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.sound.SoundLoadEvent(this));
   }

   /**
    * Tries to add the paulscode library and the relevant codecs. If it fails, the master volume  will be set to zero.
    */
   private synchronized void load() {
      if (!this.loaded) {
         try {
            this.sndSystem.func_216404_a();
            this.listener.init();
            this.listener.setGain(this.options.getSoundLevel(SoundCategory.MASTER));
            this.field_217939_i.func_217908_a(this.soundsToPreload).thenRun(this.soundsToPreload::clear);
            this.loaded = true;
            LOGGER.info(LOG_MARKER, "Sound engine started");
         } catch (RuntimeException runtimeexception) {
            LOGGER.error(LOG_MARKER, "Error starting SoundSystem. Turning off sounds & music", (Throwable)runtimeexception);
         }

      }
   }

   private float getVolume(SoundCategory category) {
      return category != null && category != SoundCategory.MASTER ? this.options.getSoundLevel(category) : 1.0F;
   }

   public void setVolume(SoundCategory category, float volume) {
      if (this.loaded) {
         if (category == SoundCategory.MASTER) {
            this.listener.setGain(volume);
         } else {
            this.field_217942_m.forEach((p_217926_1_, p_217926_2_) -> {
               float f = this.getClampedVolume(p_217926_1_);
               p_217926_2_.func_217888_a((p_217923_1_) -> {
                  if (f <= 0.0F) {
                     p_217923_1_.func_216418_f();
                  } else {
                     p_217923_1_.func_216430_b(f);
                  }

               });
            });
         }
      }
   }

   /**
    * Cleans up the Sound System
    */
   public void unload() {
      if (this.loaded) {
         this.stopAllSounds();
         this.field_217939_i.func_217912_a();
         this.sndSystem.func_216409_b();
         this.loaded = false;
      }

   }

   public void stop(ISound sound) {
      if (this.loaded) {
         ChannelManager.Entry channelmanager$entry = this.field_217942_m.get(sound);
         if (channelmanager$entry != null) {
            channelmanager$entry.func_217888_a(SoundSource::func_216418_f);
         }
      }

   }

   /**
    * Stops all currently playing sounds
    */
   public void stopAllSounds() {
      if (this.loaded) {
         this.executor.restart();
         this.field_217942_m.values().forEach((p_217922_0_) -> {
            p_217922_0_.func_217888_a(SoundSource::func_216418_f);
         });
         this.field_217942_m.clear();
         this.channelManager.releaseAll();
         this.delayedSounds.clear();
         this.tickableSounds.clear();
         this.field_217943_n.clear();
         this.playingSoundsStopTime.clear();
      }

   }

   public void addListener(ISoundEventListener listener) {
      this.listeners.add(listener);
   }

   public void removeListener(ISoundEventListener listener) {
      this.listeners.remove(listener);
   }

   public void tick(boolean p_217921_1_) {
      if (!p_217921_1_) {
         this.func_217927_h();
      }

      this.channelManager.tick();
   }

   private void func_217927_h() {
      ++this.ticks;

      for(ITickableSound itickablesound : this.tickableSounds) {
         itickablesound.tick();
         if (itickablesound.isDonePlaying()) {
            this.stop(itickablesound);
         } else {
            float f = this.getClampedVolume(itickablesound);
            float f1 = this.getClampedPitch(itickablesound);
            Vec3d vec3d = new Vec3d((double)itickablesound.getX(), (double)itickablesound.getY(), (double)itickablesound.getZ());
            ChannelManager.Entry channelmanager$entry = this.field_217942_m.get(itickablesound);
            if (channelmanager$entry != null) {
               channelmanager$entry.func_217888_a((p_217924_3_) -> {
                  p_217924_3_.func_216430_b(f);
                  p_217924_3_.func_216422_a(f1);
                  p_217924_3_.func_216420_a(vec3d);
               });
            }
         }
      }

      Iterator<Entry<ISound, ChannelManager.Entry>> iterator = this.field_217942_m.entrySet().iterator();

      while(iterator.hasNext()) {
         Entry<ISound, ChannelManager.Entry> entry = iterator.next();
         ChannelManager.Entry channelmanager$entry1 = entry.getValue();
         ISound isound = entry.getKey();
         float f2 = this.options.getSoundLevel(isound.getCategory());
         if (f2 <= 0.0F) {
            channelmanager$entry1.func_217888_a(SoundSource::func_216418_f);
            iterator.remove();
         } else if (channelmanager$entry1.func_217889_a()) {
            int j = this.playingSoundsStopTime.get(isound);
            if (j <= this.ticks) {
               int i = isound.getRepeatDelay();
               if (isound.canRepeat() && i > 0) {
                  this.delayedSounds.put(isound, this.ticks + i);
               }

               iterator.remove();
               LOGGER.debug(LOG_MARKER, "Removed channel {} because it's not playing anymore", (Object)channelmanager$entry1);
               this.playingSoundsStopTime.remove(isound);

               try {
                  this.field_217943_n.remove(isound.getCategory(), isound);
               } catch (RuntimeException var9) {
                  ;
               }

               if (isound instanceof ITickableSound) {
                  this.tickableSounds.remove(isound);
               }
            }
         }
      }

      Iterator<Entry<ISound, Integer>> iterator1 = this.delayedSounds.entrySet().iterator();

      while(iterator1.hasNext()) {
         Entry<ISound, Integer> entry1 = iterator1.next();
         if (this.ticks >= entry1.getValue()) {
            ISound isound1 = entry1.getKey();
            if (isound1 instanceof ITickableSound) {
               ((ITickableSound)isound1).tick();
            }

            this.play(isound1);
            iterator1.remove();
         }
      }

   }

   public boolean func_217933_b(ISound p_217933_1_) {
      if (!this.loaded) {
         return false;
      } else {
         return this.playingSoundsStopTime.containsKey(p_217933_1_) && this.playingSoundsStopTime.get(p_217933_1_) <= this.ticks ? true : this.field_217942_m.containsKey(p_217933_1_);
      }
   }

   public void play(ISound p_sound) {
      if (this.loaded) {
         p_sound = net.minecraftforge.client.ForgeHooksClient.playSound(this, p_sound);
         if (p_sound == null) return;
         SoundEventAccessor soundeventaccessor = p_sound.createAccessor(this.sndHandler);
         ResourceLocation resourcelocation = p_sound.getSoundLocation();
         if (soundeventaccessor == null) {
            if (UNABLE_TO_PLAY.add(resourcelocation)) {
               LOGGER.warn(LOG_MARKER, "Unable to play unknown soundEvent: {}", (Object)resourcelocation);
            }

         } else {
            if (!this.listeners.isEmpty()) {
               for(ISoundEventListener isoundeventlistener : this.listeners) {
                  isoundeventlistener.onPlaySound(p_sound, soundeventaccessor);
               }
            }

            if (this.listener.getGain() <= 0.0F) {
               LOGGER.debug(LOG_MARKER, "Skipped playing soundEvent: {}, master volume was zero", (Object)resourcelocation);
            } else {
               Sound sound = p_sound.getSound();
               if (sound == SoundHandler.MISSING_SOUND) {
                  if (UNABLE_TO_PLAY.add(resourcelocation)) {
                     LOGGER.warn(LOG_MARKER, "Unable to play empty soundEvent: {}", (Object)resourcelocation);
                  }

               } else {
                  float f3 = p_sound.getVolume();
                  float f = Math.max(f3, 1.0F) * (float)sound.getAttenuationDistance();
                  SoundCategory soundcategory = p_sound.getCategory();
                  float f1 = this.getClampedVolume(p_sound);
                  float f2 = this.getClampedPitch(p_sound);
                  ISound.AttenuationType isound$attenuationtype = p_sound.getAttenuationType();
                  boolean flag = p_sound.func_217861_m();
                  if (f1 == 0.0F && !p_sound.canBeSilent()) {
                     LOGGER.debug(LOG_MARKER, "Skipped playing sound {}, volume was zero.", (Object)sound.getSoundLocation());
                  } else {
                     boolean flag1 = p_sound.canRepeat() && p_sound.getRepeatDelay() == 0;
                     Vec3d vec3d = new Vec3d((double)p_sound.getX(), (double)p_sound.getY(), (double)p_sound.getZ());
                     ChannelManager.Entry channelmanager$entry = this.channelManager.createChannel(sound.isStreaming() ? SoundSystem.Mode.STREAMING : SoundSystem.Mode.STATIC);
                     LOGGER.debug(LOG_MARKER, "Playing sound {} for event {}", sound.getSoundLocation(), resourcelocation);
                     this.playingSoundsStopTime.put(p_sound, this.ticks + 20);
                     this.field_217942_m.put(p_sound, channelmanager$entry);
                     this.field_217943_n.put(soundcategory, p_sound);
                     channelmanager$entry.func_217888_a((p_217930_7_) -> {
                        p_217930_7_.func_216422_a(f2);
                        p_217930_7_.func_216430_b(f1);
                        if (isound$attenuationtype == ISound.AttenuationType.LINEAR) {
                           p_217930_7_.func_216423_c(f);
                        } else {
                           p_217930_7_.func_216419_h();
                        }

                        p_217930_7_.func_216425_a(flag1);
                        p_217930_7_.func_216420_a(vec3d);
                        p_217930_7_.func_216432_b(flag);
                     });
                     final ISound isound = p_sound;
                     if (!sound.isStreaming()) {
                        this.field_217939_i.func_217909_a(sound.getSoundAsOggLocation()).thenAccept((p_217934_1_) -> {
                           channelmanager$entry.func_217888_a((p_217925_1_) -> {
                              p_217925_1_.func_216429_a(p_217934_1_);
                              p_217925_1_.func_216438_c();
                              net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.sound.PlaySoundSourceEvent(this, isound, p_217925_1_));
                           });
                        });
                     } else {
                        this.field_217939_i.func_217917_b(sound.getSoundAsOggLocation()).thenAccept((p_217928_1_) -> {
                           channelmanager$entry.func_217888_a((p_217935_1_) -> {
                              p_217935_1_.func_216433_a(p_217928_1_);
                              p_217935_1_.func_216438_c();
                              net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.sound.PlayStreamingSourceEvent(this, isound, p_217935_1_));
                           });
                        });
                     }

                     if (p_sound instanceof ITickableSound) {
                        this.tickableSounds.add((ITickableSound)p_sound);
                     }

                  }
               }
            }
         }
      }
   }

   public void enqueuePreload(Sound soundIn) {
      this.soundsToPreload.add(soundIn);
   }

   private float getClampedPitch(ISound soundIn) {
      return MathHelper.clamp(soundIn.getPitch(), 0.5F, 2.0F);
   }

   private float getClampedVolume(ISound soundIn) {
      return MathHelper.clamp(soundIn.getVolume() * this.getVolume(soundIn.getCategory()), 0.0F, 1.0F);
   }

   /**
    * Pauses all currently playing sounds
    */
   public void pause() {
      if (this.loaded) {
         this.channelManager.func_217897_a((p_217929_0_) -> {
            p_217929_0_.forEach(SoundSource::func_216439_d);
         });
      }

   }

   /**
    * Resumes playing all currently playing sounds (after pauseAllSounds)
    */
   public void resume() {
      if (this.loaded) {
         this.channelManager.func_217897_a((p_217936_0_) -> {
            p_217936_0_.forEach(SoundSource::func_216437_e);
         });
      }

   }

   /**
    * Adds a sound to play in n tick
    */
   public void playDelayed(ISound sound, int delay) {
      this.delayedSounds.put(sound, this.ticks + delay);
   }

   public void func_217920_a(ActiveRenderInfo p_217920_1_) {
      if (this.loaded && p_217920_1_.func_216786_h()) {
         Vec3d vec3d = p_217920_1_.getProjectedView();
         Vec3d vec3d1 = p_217920_1_.func_216787_l();
         Vec3d vec3d2 = p_217920_1_.func_216788_m();
         this.executor.execute(() -> {
            this.listener.setPosition(vec3d);
            this.listener.setOrientation(vec3d1, vec3d2);
         });
      }
   }

   public void stop(@Nullable ResourceLocation soundName, @Nullable SoundCategory category) {
      if (category != null) {
         for(ISound isound : this.field_217943_n.get(category)) {
            if (soundName == null || isound.getSoundLocation().equals(soundName)) {
               this.stop(isound);
            }
         }
      } else if (soundName == null) {
         this.stopAllSounds();
      } else {
         for(ISound isound1 : this.field_217942_m.keySet()) {
            if (isound1.getSoundLocation().equals(soundName)) {
               this.stop(isound1);
            }
         }
      }

   }

   public String getDebugString() {
      return this.sndSystem.getDebugString();
   }
}