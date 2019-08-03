package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SSpawnParticlePacket implements IPacket<IClientPlayNetHandler> {
   private float xCoord;
   private float yCoord;
   private float zCoord;
   private float xOffset;
   private float yOffset;
   private float zOffset;
   private float particleSpeed;
   private int particleCount;
   private boolean longDistance;
   private IParticleData particle;

   public SSpawnParticlePacket() {
   }

   public <T extends IParticleData> SSpawnParticlePacket(T p_i47932_1_, boolean p_i47932_2_, float p_i47932_3_, float p_i47932_4_, float p_i47932_5_, float p_i47932_6_, float p_i47932_7_, float p_i47932_8_, float p_i47932_9_, int p_i47932_10_) {
      this.particle = p_i47932_1_;
      this.longDistance = p_i47932_2_;
      this.xCoord = p_i47932_3_;
      this.yCoord = p_i47932_4_;
      this.zCoord = p_i47932_5_;
      this.xOffset = p_i47932_6_;
      this.yOffset = p_i47932_7_;
      this.zOffset = p_i47932_8_;
      this.particleSpeed = p_i47932_9_;
      this.particleCount = p_i47932_10_;
   }

   /**
    * Reads the raw packet data from the data stream.
    */
   public void readPacketData(PacketBuffer buf) throws IOException {
      ParticleType<?> particletype = Registry.PARTICLE_TYPE.getByValue(buf.readInt());
      if (particletype == null) {
         particletype = ParticleTypes.BARRIER;
      }

      this.longDistance = buf.readBoolean();
      this.xCoord = buf.readFloat();
      this.yCoord = buf.readFloat();
      this.zCoord = buf.readFloat();
      this.xOffset = buf.readFloat();
      this.yOffset = buf.readFloat();
      this.zOffset = buf.readFloat();
      this.particleSpeed = buf.readFloat();
      this.particleCount = buf.readInt();
      this.particle = this.readParticle(buf, particletype);
   }

   private <T extends IParticleData> T readParticle(PacketBuffer p_199855_1_, ParticleType<T> p_199855_2_) {
      return p_199855_2_.getDeserializer().read(p_199855_2_, p_199855_1_);
   }

   /**
    * Writes the raw packet data to the data stream.
    */
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeInt(Registry.PARTICLE_TYPE.getId(this.particle.getType()));
      buf.writeBoolean(this.longDistance);
      buf.writeFloat(this.xCoord);
      buf.writeFloat(this.yCoord);
      buf.writeFloat(this.zCoord);
      buf.writeFloat(this.xOffset);
      buf.writeFloat(this.yOffset);
      buf.writeFloat(this.zOffset);
      buf.writeFloat(this.particleSpeed);
      buf.writeInt(this.particleCount);
      this.particle.write(buf);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isLongDistance() {
      return this.longDistance;
   }

   /**
    * Gets the x coordinate to spawn the particle.
    */
   @OnlyIn(Dist.CLIENT)
   public double getXCoordinate() {
      return (double)this.xCoord;
   }

   /**
    * Gets the y coordinate to spawn the particle.
    */
   @OnlyIn(Dist.CLIENT)
   public double getYCoordinate() {
      return (double)this.yCoord;
   }

   /**
    * Gets the z coordinate to spawn the particle.
    */
   @OnlyIn(Dist.CLIENT)
   public double getZCoordinate() {
      return (double)this.zCoord;
   }

   /**
    * Gets the x coordinate offset for the particle. The particle may use the offset for particle spread.
    */
   @OnlyIn(Dist.CLIENT)
   public float getXOffset() {
      return this.xOffset;
   }

   /**
    * Gets the y coordinate offset for the particle. The particle may use the offset for particle spread.
    */
   @OnlyIn(Dist.CLIENT)
   public float getYOffset() {
      return this.yOffset;
   }

   /**
    * Gets the z coordinate offset for the particle. The particle may use the offset for particle spread.
    */
   @OnlyIn(Dist.CLIENT)
   public float getZOffset() {
      return this.zOffset;
   }

   /**
    * Gets the speed of the particle animation (used in client side rendering).
    */
   @OnlyIn(Dist.CLIENT)
   public float getParticleSpeed() {
      return this.particleSpeed;
   }

   /**
    * Gets the amount of particles to spawn
    */
   @OnlyIn(Dist.CLIENT)
   public int getParticleCount() {
      return this.particleCount;
   }

   @OnlyIn(Dist.CLIENT)
   public IParticleData getParticle() {
      return this.particle;
   }

   /**
    * Passes this Packet on to the NetHandler for processing.
    */
   public void processPacket(IClientPlayNetHandler handler) {
      handler.handleParticles(this);
   }
}