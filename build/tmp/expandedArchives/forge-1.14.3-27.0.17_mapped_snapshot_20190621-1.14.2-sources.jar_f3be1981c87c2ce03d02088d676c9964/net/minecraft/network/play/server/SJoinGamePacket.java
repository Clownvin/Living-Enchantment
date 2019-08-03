package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SJoinGamePacket implements IPacket<IClientPlayNetHandler> {
   private int playerId;
   private boolean hardcoreMode;
   private GameType gameType;
   private DimensionType dimension;
   private int maxPlayers;
   private WorldType worldType;
   private int field_218729_g;
   private boolean reducedDebugInfo;

   public SJoinGamePacket() {
   }

   public SJoinGamePacket(int p_i50773_1_, GameType p_i50773_2_, boolean p_i50773_3_, DimensionType p_i50773_4_, int p_i50773_5_, WorldType p_i50773_6_, int p_i50773_7_, boolean p_i50773_8_) {
      this.playerId = p_i50773_1_;
      this.dimension = p_i50773_4_;
      this.gameType = p_i50773_2_;
      this.maxPlayers = p_i50773_5_;
      this.hardcoreMode = p_i50773_3_;
      this.worldType = p_i50773_6_;
      this.field_218729_g = p_i50773_7_;
      this.reducedDebugInfo = p_i50773_8_;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.playerId = buf.readInt();
      int i = buf.readUnsignedByte();
      this.hardcoreMode = (i & 8) == 8;
      i = i & -9;
      this.gameType = GameType.getByID(i);
      this.dimension = DimensionType.getById(buf.readInt());
      this.maxPlayers = buf.readUnsignedByte();
      this.worldType = WorldType.byName(buf.readString(16));
      if (this.worldType == null) {
         this.worldType = WorldType.DEFAULT;
      }

      this.field_218729_g = buf.readVarInt();
      this.reducedDebugInfo = buf.readBoolean();
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeInt(this.playerId);
      int i = this.gameType.getID();
      if (this.hardcoreMode) {
         i |= 8;
      }

      buf.writeByte(i);
      buf.writeInt(this.dimension.getId());
      buf.writeByte(this.maxPlayers);
      buf.writeString(this.worldType.getName());
      buf.writeVarInt(this.field_218729_g);
      buf.writeBoolean(this.reducedDebugInfo);
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleJoinGame(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getPlayerId() {
      return this.playerId;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isHardcoreMode() {
      return this.hardcoreMode;
   }

   @OnlyIn(Dist.CLIENT)
   public GameType getGameType() {
      return this.gameType;
   }

   @OnlyIn(Dist.CLIENT)
   public DimensionType getDimension() {
      return this.dimension;
   }

   @OnlyIn(Dist.CLIENT)
   public WorldType getWorldType() {
      return this.worldType;
   }

   @OnlyIn(Dist.CLIENT)
   public int func_218728_h() {
      return this.field_218729_g;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isReducedDebugInfo() {
      return this.reducedDebugInfo;
   }
}