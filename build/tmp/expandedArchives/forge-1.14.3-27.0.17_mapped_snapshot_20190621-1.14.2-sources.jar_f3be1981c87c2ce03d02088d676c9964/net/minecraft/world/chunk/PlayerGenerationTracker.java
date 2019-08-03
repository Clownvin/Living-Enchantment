package net.minecraft.world.chunk;

import com.google.common.collect.Sets;
import com.google.common.collect.Streams;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.entity.player.ServerPlayerEntity;

public final class PlayerGenerationTracker {
   private final Set<ServerPlayerEntity> generatingPlayers = Sets.newHashSet();
   private final Set<ServerPlayerEntity> nonGeneratingPlayers = Sets.newHashSet();

   public Stream<ServerPlayerEntity> getGeneratingPlayers(long chunkPosIn) {
      return Streams.concat(this.generatingPlayers.stream(), this.nonGeneratingPlayers.stream());
   }

   public void addPlayer(long chunkPosIn, ServerPlayerEntity player, boolean canGenerateChunks) {
      (canGenerateChunks ? this.nonGeneratingPlayers : this.generatingPlayers).add(player);
   }

   public void removePlayer(long chunkPosIn, ServerPlayerEntity player) {
      this.generatingPlayers.remove(player);
      this.nonGeneratingPlayers.remove(player);
   }

   public void disableGeneration(ServerPlayerEntity player) {
      this.nonGeneratingPlayers.add(player);
      this.generatingPlayers.remove(player);
   }

   public void enableGeneration(ServerPlayerEntity player) {
      this.nonGeneratingPlayers.remove(player);
      this.generatingPlayers.add(player);
   }

   public boolean cannotGenerateChunks(ServerPlayerEntity player) {
      return !this.generatingPlayers.contains(player);
   }

   public void updatePlayerPosition(long oldChunkPos, long newChunkPos, ServerPlayerEntity player) {
   }
}