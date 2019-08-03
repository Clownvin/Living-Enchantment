package net.minecraft.profiler;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.minecraft.util.Util;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FilledProfileResult implements IProfileResult {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map<String, Long> timesMap;
   private final Map<String, Long> field_223508_c;
   private final long timeStop;
   private final int ticksStop;
   private final long timeStart;
   private final int ticksStart;
   private final int field_223509_h;

   public FilledProfileResult(Map<String, Long> p_i51528_1_, Map<String, Long> p_i51528_2_, long p_i51528_3_, int p_i51528_5_, long p_i51528_6_, int p_i51528_8_) {
      this.timesMap = p_i51528_1_;
      this.field_223508_c = p_i51528_2_;
      this.timeStop = p_i51528_3_;
      this.ticksStop = p_i51528_5_;
      this.timeStart = p_i51528_6_;
      this.ticksStart = p_i51528_8_;
      this.field_223509_h = p_i51528_8_ - p_i51528_5_;
   }

   public List<DataPoint> getDataPoints(String sectionPath) {
      long i = this.timesMap.containsKey("root") ? this.timesMap.get("root") : 0L;
      long j = this.timesMap.getOrDefault(sectionPath, -1L);
      long k = this.field_223508_c.getOrDefault(sectionPath, 0L);
      List<DataPoint> list = Lists.newArrayList();
      if (!sectionPath.isEmpty()) {
         sectionPath = sectionPath + ".";
      }

      long l = 0L;

      for(String s : this.timesMap.keySet()) {
         if (s.length() > sectionPath.length() && s.startsWith(sectionPath) && s.indexOf(".", sectionPath.length() + 1) < 0) {
            l += this.timesMap.get(s);
         }
      }

      float f = (float)l;
      if (l < j) {
         l = j;
      }

      if (i < l) {
         i = l;
      }

      Set<String> set = Sets.newHashSet(this.timesMap.keySet());
      set.addAll(this.field_223508_c.keySet());

      for(String s1 : set) {
         if (s1.length() > sectionPath.length() && s1.startsWith(sectionPath) && s1.indexOf(".", sectionPath.length() + 1) < 0) {
            long i1 = this.timesMap.getOrDefault(s1, 0L);
            double d0 = (double)i1 * 100.0D / (double)l;
            double d1 = (double)i1 * 100.0D / (double)i;
            String s2 = s1.substring(sectionPath.length());
            long j1 = this.field_223508_c.getOrDefault(s1, 0L);
            list.add(new DataPoint(s2, d0, d1, j1));
         }
      }

      for(String s3 : this.timesMap.keySet()) {
         this.timesMap.put(s3, this.timesMap.get(s3) * 999L / 1000L);
      }

      if ((float)l > f) {
         list.add(new DataPoint("unspecified", (double)((float)l - f) * 100.0D / (double)l, (double)((float)l - f) * 100.0D / (double)i, k));
      }

      Collections.sort(list);
      list.add(0, new DataPoint(sectionPath, 100.0D, (double)l * 100.0D / (double)i, k));
      return list;
   }

   public long timeStop() {
      return this.timeStop;
   }

   public int ticksStop() {
      return this.ticksStop;
   }

   public long timeStart() {
      return this.timeStart;
   }

   public int ticksStart() {
      return this.ticksStart;
   }

   public boolean writeToFile(File p_219919_1_) {
      p_219919_1_.getParentFile().mkdirs();
      Writer writer = null;

      boolean flag1;
      try {
         writer = new OutputStreamWriter(new FileOutputStream(p_219919_1_), StandardCharsets.UTF_8);
         writer.write(this.inlineIntoCrashReport(this.nanoTime(), this.ticksSpend()));
         boolean lvt_3_1_ = true;
         return lvt_3_1_;
      } catch (Throwable throwable) {
         LOGGER.error("Could not save profiler results to {}", p_219919_1_, throwable);
         flag1 = false;
      } finally {
         IOUtils.closeQuietly(writer);
      }

      return flag1;
   }

   protected String inlineIntoCrashReport(long p_219929_1_, int p_219929_3_) {
      StringBuilder stringbuilder = new StringBuilder();
      stringbuilder.append("---- Minecraft Profiler Results ----\n");
      stringbuilder.append("// ");
      stringbuilder.append(getWittyString());
      stringbuilder.append("\n\n");
      stringbuilder.append("Time span: ").append(p_219929_1_ / 1000000L).append(" ms\n");
      stringbuilder.append("Tick span: ").append(p_219929_3_).append(" ticks\n");
      stringbuilder.append("// This is approximately ").append(String.format(Locale.ROOT, "%.2f", (float)p_219929_3_ / ((float)p_219929_1_ / 1.0E9F))).append(" ticks per second. It should be ").append((int)20).append(" ticks per second\n\n");
      stringbuilder.append("--- BEGIN PROFILE DUMP ---\n\n");
      this.format(0, "root", stringbuilder);
      stringbuilder.append("--- END PROFILE DUMP ---\n\n");
      return stringbuilder.toString();
   }

   public String format() {
      StringBuilder stringbuilder = new StringBuilder();
      this.format(0, "root", stringbuilder);
      return stringbuilder.toString();
   }

   private void format(int p_219928_1_, String p_219928_2_, StringBuilder p_219928_3_) {
      List<DataPoint> list = this.getDataPoints(p_219928_2_);
      if (list.size() >= 3) {
         for(int i = 1; i < list.size(); ++i) {
            DataPoint datapoint = list.get(i);
            p_219928_3_.append(String.format("[%02d] ", p_219928_1_));

            for(int j = 0; j < p_219928_1_; ++j) {
               p_219928_3_.append("|   ");
            }

            p_219928_3_.append(datapoint.name).append('(').append(datapoint.field_223511_c).append('/').append(String.format(Locale.ROOT, "%.0f", (float)datapoint.field_223511_c / (float)this.field_223509_h)).append(')').append(" - ").append(String.format(Locale.ROOT, "%.2f", datapoint.relTime)).append("%/").append(String.format(Locale.ROOT, "%.2f", datapoint.rootRelTime)).append("%\n");
            if (!"unspecified".equals(datapoint.name)) {
               try {
                  this.format(p_219928_1_ + 1, p_219928_2_ + "." + datapoint.name, p_219928_3_);
               } catch (Exception exception) {
                  p_219928_3_.append("[[ EXCEPTION ").append((Object)exception).append(" ]]");
               }
            }
         }

      }
   }

   private static String getWittyString() {
      String[] astring = new String[]{"Shiny numbers!", "Am I not running fast enough? :(", "I'm working as hard as I can!", "Will I ever be good enough for you? :(", "Speedy. Zoooooom!", "Hello world", "40% better than a crash report.", "Now with extra numbers", "Now with less numbers", "Now with the same numbers", "You should add flames to things, it makes them go faster!", "Do you feel the need for... optimization?", "*cracks redstone whip*", "Maybe if you treated it better then it'll have more motivation to work faster! Poor server."};

      try {
         return astring[(int)(Util.nanoTime() % (long)astring.length)];
      } catch (Throwable var2) {
         return "Witty comment unavailable :(";
      }
   }

   public int ticksSpend() {
      return this.field_223509_h;
   }
}