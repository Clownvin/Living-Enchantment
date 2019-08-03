package net.minecraft.profiler;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import java.time.Duration;
import java.util.List;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Profiler implements IResultableProfiler {
   private static final long WARN_TIME_THRESHOLD = Duration.ofMillis(100L).toNanos();
   private static final Logger LOGGER = LogManager.getLogger();
   private final List<String> sectionList = Lists.newArrayList();
   private final LongList timeStack = new LongArrayList();
   private final Object2LongMap<String> timesMap = new Object2LongOpenHashMap<>();
   private final Object2LongMap<String> field_223510_f = new Object2LongOpenHashMap<>();
   private final IntSupplier currentTicks;
   private final long startTime;
   private final int startTicks;
   private String currentSectionName = "";
   private boolean tickStarted;

   public Profiler(long time, IntSupplier currentTicks) {
      this.startTime = time;
      this.startTicks = currentTicks.getAsInt();
      this.currentTicks = currentTicks;
   }

   public void startTick() {
      if (this.tickStarted) {
         LOGGER.error("Profiler tick already started - missing endTick()?");
      } else {
         this.tickStarted = true;
         this.currentSectionName = "";
         this.sectionList.clear();
         this.startSection("root");
      }
   }

   public void endTick() {
      if (!this.tickStarted) {
         LOGGER.error("Profiler tick already ended - missing startTick()?");
      } else {
         this.endSection();
         this.tickStarted = false;
         if (!this.currentSectionName.isEmpty()) {
            LOGGER.error("Profiler tick ended before path was fully popped (remainder: '{}'). Mismatched push/pop?", (Object)this.currentSectionName);
         }

      }
   }

   /**
    * Start section
    */
   public void startSection(String name) {
      if (!this.tickStarted) {
         LOGGER.error("Cannot push '{}' to profiler if profiler tick hasn't started - missing startTick()?", (Object)name);
      } else {
         if (!this.currentSectionName.isEmpty()) {
            this.currentSectionName = this.currentSectionName + ".";
         }

         this.currentSectionName = this.currentSectionName + name;
         this.sectionList.add(this.currentSectionName);
         this.timeStack.add(Util.nanoTime());
      }
   }

   public void startSection(Supplier<String> nameSupplier) {
      this.startSection(nameSupplier.get());
   }

   /**
    * End section
    */
   public void endSection() {
      if (!this.tickStarted) {
         LOGGER.error("Cannot pop from profiler if profiler tick hasn't started - missing startTick()?");
      } else if (this.timeStack.isEmpty()) {
         LOGGER.error("Tried to pop one too many times! Mismatched push() and pop()?");
      } else {
         long i = Util.nanoTime();
         long j = this.timeStack.removeLong(this.timeStack.size() - 1);
         this.sectionList.remove(this.sectionList.size() - 1);
         long k = i - j;
         this.timesMap.put(this.currentSectionName, this.timesMap.getLong(this.currentSectionName) + k);
         this.field_223510_f.put(this.currentSectionName, this.field_223510_f.getLong(this.currentSectionName) + 1L);
         if (k > WARN_TIME_THRESHOLD) {
            LOGGER.warn("Something's taking too long! '{}' took aprox {} ms", this.currentSectionName, (double)k / 1000000.0D);
         }

         this.currentSectionName = this.sectionList.isEmpty() ? "" : this.sectionList.get(this.sectionList.size() - 1);
      }
   }

   public void endStartSection(String name) {
      this.endSection();
      this.startSection(name);
   }

   @OnlyIn(Dist.CLIENT)
   public void endStartSection(Supplier<String> nameSupplier) {
      this.endSection();
      this.startSection(nameSupplier);
   }

   public IProfileResult getResults() {
      return new FilledProfileResult(this.timesMap, this.field_223510_f, this.startTime, this.startTicks, Util.nanoTime(), this.currentTicks.getAsInt());
   }
}