package net.minecraft.network;

import net.minecraft.util.concurrent.ThreadTaskExecutor;
import net.minecraft.world.ServerWorld;

public class PacketThreadUtil {
   public static <T extends INetHandler> void checkThreadAndEnqueue(IPacket<T> packetIn, T processor, ServerWorld worldIn) throws ThreadQuickExitException {
      checkThreadAndEnqueue(packetIn, processor, worldIn.getServer());
   }

   public static <T extends INetHandler> void checkThreadAndEnqueue(IPacket<T> packetIn, T processor, ThreadTaskExecutor<?> executor) throws ThreadQuickExitException {
      if (!executor.isOnExecutionThread()) {
         executor.execute(() -> {
            packetIn.processPacket(processor);
         });
         throw ThreadQuickExitException.INSTANCE;
      }
   }
}