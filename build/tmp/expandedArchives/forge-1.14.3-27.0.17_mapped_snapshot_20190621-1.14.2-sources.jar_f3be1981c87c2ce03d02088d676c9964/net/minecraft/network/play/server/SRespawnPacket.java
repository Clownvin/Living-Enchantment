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

public class SRespawnPacket implements IPacket<IClientPlayNetHandler> {
   private DimensionType dimensionID;
   private GameType gameType;
   private WorldType worldType;

   public SRespawnPacket() {
   }

   public SRespawnPacket(DimensionType p_i50767_1_, WorldType p_i50767_2_, GameType p_i50767_3_) {
      this.dimensionID = p_i50767_1_;
      this.gameType = p_i50767_3_;
      this.worldType = p_i50767_2_;
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleRespawn(this);
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.dimensionID = DimensionType.getById(buf.readInt());
      this.gameType = GameType.getByID(buf.readUnsignedByte());
      this.worldType = WorldType.byName(buf.readString(16));
      if (this.worldType == null) {
         this.worldType = WorldType.DEFAULT;
      }

   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeInt(this.dimensionID.getId());
      buf.writeByte(this.gameType.getID());
      buf.writeString(this.worldType.getName());
   }

   @OnlyIn(Dist.CLIENT)
   public DimensionType getDimension() {
      return this.dimensionID;
   }

   @OnlyIn(Dist.CLIENT)
   public GameType getGameType() {
      return this.gameType;
   }

   @OnlyIn(Dist.CLIENT)
   public WorldType getWorldType() {
      return this.worldType;
   }
}