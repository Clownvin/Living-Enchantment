package net.minecraft.command;

import com.google.common.collect.Maps;
import com.google.common.primitives.UnsignedLong;
import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TimerCallbackManager<T> {
   private static final Logger LOGGER = LogManager.getLogger();
   private final TimerCallbackSerializers<T> field_216334_b;
   private final Queue<TimerCallbackManager.Entry<T>> entries = new PriorityQueue<>(sorter());
   private UnsignedLong nextUniqueId = UnsignedLong.ZERO;
   private final Map<String, TimerCallbackManager.Entry<T>> byName = Maps.newHashMap();

   private static <T> Comparator<TimerCallbackManager.Entry<T>> sorter() {
      return (p_216324_0_, p_216324_1_) -> {
         int i = Long.compare(p_216324_0_.triggerTime, p_216324_1_.triggerTime);
         return i != 0 ? i : p_216324_0_.uniqueId.compareTo(p_216324_1_.uniqueId);
      };
   }

   public TimerCallbackManager(TimerCallbackSerializers<T> p_i51188_1_) {
      this.field_216334_b = p_i51188_1_;
   }

   public void run(T p_216331_1_, long gameTime) {
      while(true) {
         TimerCallbackManager.Entry<T> entry = this.entries.peek();
         if (entry == null || entry.triggerTime > gameTime) {
            return;
         }

         this.entries.remove();
         this.byName.remove(entry.name);
         entry.callback.run(p_216331_1_, this, gameTime);
      }
   }

   private void schedule(String p_216328_1_, long p_216328_2_, ITimerCallback<T> p_216328_4_) {
      this.nextUniqueId = this.nextUniqueId.plus(UnsignedLong.ONE);
      TimerCallbackManager.Entry<T> entry = new TimerCallbackManager.Entry<>(p_216328_2_, this.nextUniqueId, p_216328_1_, p_216328_4_);
      this.byName.put(p_216328_1_, entry);
      this.entries.add(entry);
   }

   public boolean scheduleSkipDuplicate(String p_216325_1_, long p_216325_2_, ITimerCallback<T> p_216325_4_) {
      if (this.byName.containsKey(p_216325_1_)) {
         return false;
      } else {
         this.schedule(p_216325_1_, p_216325_2_, p_216325_4_);
         return true;
      }
   }

   public void scheduleReplaceDuplicate(String p_216326_1_, long p_216326_2_, ITimerCallback<T> p_216326_4_) {
      TimerCallbackManager.Entry<T> entry = this.byName.remove(p_216326_1_);
      if (entry != null) {
         this.entries.remove(entry);
      }

      this.schedule(p_216326_1_, p_216326_2_, p_216326_4_);
   }

   private void readEntry(CompoundNBT p_216329_1_) {
      CompoundNBT compoundnbt = p_216329_1_.getCompound("Callback");
      ITimerCallback<T> itimercallback = this.field_216334_b.func_216341_a(compoundnbt);
      if (itimercallback != null) {
         String s = p_216329_1_.getString("Name");
         long i = p_216329_1_.getLong("TriggerTime");
         this.scheduleSkipDuplicate(s, i, itimercallback);
      }

   }

   public void read(ListNBT p_216323_1_) {
      this.entries.clear();
      this.byName.clear();
      this.nextUniqueId = UnsignedLong.ZERO;
      if (!p_216323_1_.isEmpty()) {
         if (p_216323_1_.getTagType() != 10) {
            LOGGER.warn("Invalid format of events: " + p_216323_1_);
         } else {
            for(INBT inbt : p_216323_1_) {
               this.readEntry((CompoundNBT)inbt);
            }

         }
      }
   }

   private CompoundNBT writeEntry(TimerCallbackManager.Entry<T> p_216332_1_) {
      CompoundNBT compoundnbt = new CompoundNBT();
      compoundnbt.putString("Name", p_216332_1_.name);
      compoundnbt.putLong("TriggerTime", p_216332_1_.triggerTime);
      compoundnbt.put("Callback", this.field_216334_b.func_216339_a(p_216332_1_.callback));
      return compoundnbt;
   }

   public ListNBT write() {
      ListNBT listnbt = new ListNBT();
      this.entries.stream().sorted(sorter()).map(this::writeEntry).forEach(listnbt::add);
      return listnbt;
   }

   public static class Entry<T> {
      public final long triggerTime;
      public final UnsignedLong uniqueId;
      public final String name;
      public final ITimerCallback<T> callback;

      private Entry(long p_i50837_1_, UnsignedLong p_i50837_3_, String p_i50837_4_, ITimerCallback<T> p_i50837_5_) {
         this.triggerTime = p_i50837_1_;
         this.uniqueId = p_i50837_3_;
         this.name = p_i50837_4_;
         this.callback = p_i50837_5_;
      }
   }
}