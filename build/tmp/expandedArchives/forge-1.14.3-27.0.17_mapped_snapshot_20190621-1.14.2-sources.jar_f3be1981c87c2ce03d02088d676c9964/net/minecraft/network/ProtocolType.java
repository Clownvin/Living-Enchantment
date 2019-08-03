package net.minecraft.network;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.network.handshake.client.CHandshakePacket;
import net.minecraft.network.login.client.CCustomPayloadLoginPacket;
import net.minecraft.network.login.client.CEncryptionResponsePacket;
import net.minecraft.network.login.client.CLoginStartPacket;
import net.minecraft.network.login.server.SCustomPayloadLoginPacket;
import net.minecraft.network.login.server.SDisconnectLoginPacket;
import net.minecraft.network.login.server.SEnableCompressionPacket;
import net.minecraft.network.login.server.SEncryptionRequestPacket;
import net.minecraft.network.login.server.SLoginSuccessPacket;
import net.minecraft.network.play.client.CAnimateHandPacket;
import net.minecraft.network.play.client.CChatMessagePacket;
import net.minecraft.network.play.client.CClickWindowPacket;
import net.minecraft.network.play.client.CClientSettingsPacket;
import net.minecraft.network.play.client.CClientStatusPacket;
import net.minecraft.network.play.client.CCloseWindowPacket;
import net.minecraft.network.play.client.CConfirmTeleportPacket;
import net.minecraft.network.play.client.CConfirmTransactionPacket;
import net.minecraft.network.play.client.CCreativeInventoryActionPacket;
import net.minecraft.network.play.client.CCustomPayloadPacket;
import net.minecraft.network.play.client.CEditBookPacket;
import net.minecraft.network.play.client.CEnchantItemPacket;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CInputPacket;
import net.minecraft.network.play.client.CKeepAlivePacket;
import net.minecraft.network.play.client.CLockDifficultyPacket;
import net.minecraft.network.play.client.CMoveVehiclePacket;
import net.minecraft.network.play.client.CPickItemPacket;
import net.minecraft.network.play.client.CPlaceRecipePacket;
import net.minecraft.network.play.client.CPlayerAbilitiesPacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.network.play.client.CQueryEntityNBTPacket;
import net.minecraft.network.play.client.CQueryTileEntityNBTPacket;
import net.minecraft.network.play.client.CRecipeInfoPacket;
import net.minecraft.network.play.client.CRenameItemPacket;
import net.minecraft.network.play.client.CResourcePackStatusPacket;
import net.minecraft.network.play.client.CSeenAdvancementsPacket;
import net.minecraft.network.play.client.CSelectTradePacket;
import net.minecraft.network.play.client.CSetDifficultyPacket;
import net.minecraft.network.play.client.CSpectatePacket;
import net.minecraft.network.play.client.CSteerBoatPacket;
import net.minecraft.network.play.client.CTabCompletePacket;
import net.minecraft.network.play.client.CUpdateBeaconPacket;
import net.minecraft.network.play.client.CUpdateCommandBlockPacket;
import net.minecraft.network.play.client.CUpdateJigsawBlockPacket;
import net.minecraft.network.play.client.CUpdateMinecartCommandBlockPacket;
import net.minecraft.network.play.client.CUpdateSignPacket;
import net.minecraft.network.play.client.CUpdateStructureBlockPacket;
import net.minecraft.network.play.client.CUseEntityPacket;
import net.minecraft.network.play.server.SAdvancementInfoPacket;
import net.minecraft.network.play.server.SAnimateBlockBreakPacket;
import net.minecraft.network.play.server.SAnimateHandPacket;
import net.minecraft.network.play.server.SBlockActionPacket;
import net.minecraft.network.play.server.SCameraPacket;
import net.minecraft.network.play.server.SChangeBlockPacket;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.network.play.server.SChunkDataPacket;
import net.minecraft.network.play.server.SCloseWindowPacket;
import net.minecraft.network.play.server.SCollectItemPacket;
import net.minecraft.network.play.server.SCombatPacket;
import net.minecraft.network.play.server.SCommandListPacket;
import net.minecraft.network.play.server.SConfirmTransactionPacket;
import net.minecraft.network.play.server.SCooldownPacket;
import net.minecraft.network.play.server.SCustomPayloadPlayPacket;
import net.minecraft.network.play.server.SDestroyEntitiesPacket;
import net.minecraft.network.play.server.SDisconnectPacket;
import net.minecraft.network.play.server.SDisplayObjectivePacket;
import net.minecraft.network.play.server.SEntityEquipmentPacket;
import net.minecraft.network.play.server.SEntityHeadLookPacket;
import net.minecraft.network.play.server.SEntityMetadataPacket;
import net.minecraft.network.play.server.SEntityPacket;
import net.minecraft.network.play.server.SEntityPropertiesPacket;
import net.minecraft.network.play.server.SEntityStatusPacket;
import net.minecraft.network.play.server.SEntityTeleportPacket;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.network.play.server.SExplosionPacket;
import net.minecraft.network.play.server.SHeldItemChangePacket;
import net.minecraft.network.play.server.SJoinGamePacket;
import net.minecraft.network.play.server.SKeepAlivePacket;
import net.minecraft.network.play.server.SMapDataPacket;
import net.minecraft.network.play.server.SMerchantOffersPacket;
import net.minecraft.network.play.server.SMountEntityPacket;
import net.minecraft.network.play.server.SMoveVehiclePacket;
import net.minecraft.network.play.server.SMultiBlockChangePacket;
import net.minecraft.network.play.server.SOpenBookWindowPacket;
import net.minecraft.network.play.server.SOpenHorseWindowPacket;
import net.minecraft.network.play.server.SOpenSignMenuPacket;
import net.minecraft.network.play.server.SOpenWindowPacket;
import net.minecraft.network.play.server.SPlaceGhostRecipePacket;
import net.minecraft.network.play.server.SPlayEntityEffectPacket;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.network.play.server.SPlaySoundEventPacket;
import net.minecraft.network.play.server.SPlaySoundPacket;
import net.minecraft.network.play.server.SPlayerAbilitiesPacket;
import net.minecraft.network.play.server.SPlayerListHeaderFooterPacket;
import net.minecraft.network.play.server.SPlayerListItemPacket;
import net.minecraft.network.play.server.SPlayerLookPacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.network.play.server.SQueryNBTResponsePacket;
import net.minecraft.network.play.server.SRecipeBookPacket;
import net.minecraft.network.play.server.SRemoveEntityEffectPacket;
import net.minecraft.network.play.server.SRespawnPacket;
import net.minecraft.network.play.server.SScoreboardObjectivePacket;
import net.minecraft.network.play.server.SSelectAdvancementsTabPacket;
import net.minecraft.network.play.server.SSendResourcePackPacket;
import net.minecraft.network.play.server.SServerDifficultyPacket;
import net.minecraft.network.play.server.SSetExperiencePacket;
import net.minecraft.network.play.server.SSetPassengersPacket;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.network.play.server.SSpawnExperienceOrbPacket;
import net.minecraft.network.play.server.SSpawnGlobalEntityPacket;
import net.minecraft.network.play.server.SSpawnMobPacket;
import net.minecraft.network.play.server.SSpawnMovingSoundEffectPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.network.play.server.SSpawnPaintingPacket;
import net.minecraft.network.play.server.SSpawnParticlePacket;
import net.minecraft.network.play.server.SSpawnPlayerPacket;
import net.minecraft.network.play.server.SSpawnPositionPacket;
import net.minecraft.network.play.server.SStatisticsPacket;
import net.minecraft.network.play.server.SStopSoundPacket;
import net.minecraft.network.play.server.STabCompletePacket;
import net.minecraft.network.play.server.STagsListPacket;
import net.minecraft.network.play.server.STeamsPacket;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.network.play.server.SUnloadChunkPacket;
import net.minecraft.network.play.server.SUpdateBossInfoPacket;
import net.minecraft.network.play.server.SUpdateChunkPositionPacket;
import net.minecraft.network.play.server.SUpdateHealthPacket;
import net.minecraft.network.play.server.SUpdateLightPacket;
import net.minecraft.network.play.server.SUpdateRecipesPacket;
import net.minecraft.network.play.server.SUpdateScorePacket;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.network.play.server.SUpdateTimePacket;
import net.minecraft.network.play.server.SUpdateViewDistancePacket;
import net.minecraft.network.play.server.SWindowItemsPacket;
import net.minecraft.network.play.server.SWindowPropertyPacket;
import net.minecraft.network.play.server.SWorldBorderPacket;
import net.minecraft.network.status.client.CPingPacket;
import net.minecraft.network.status.client.CServerQueryPacket;
import net.minecraft.network.status.server.SPongPacket;
import net.minecraft.network.status.server.SServerInfoPacket;
import org.apache.logging.log4j.LogManager;

public enum ProtocolType {
   HANDSHAKING(-1) {
      {
         this.registerPacket(PacketDirection.SERVERBOUND, CHandshakePacket.class);
      }
   },
   PLAY(0) {
      {
         this.registerPacket(PacketDirection.CLIENTBOUND, SSpawnObjectPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SSpawnExperienceOrbPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SSpawnGlobalEntityPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SSpawnMobPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SSpawnPaintingPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SSpawnPlayerPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SAnimateHandPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SStatisticsPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SAnimateBlockBreakPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SUpdateTileEntityPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SBlockActionPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SChangeBlockPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SUpdateBossInfoPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SServerDifficultyPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SChatPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SMultiBlockChangePacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, STabCompletePacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SCommandListPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SConfirmTransactionPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SCloseWindowPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SWindowItemsPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SWindowPropertyPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SSetSlotPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SCooldownPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SCustomPayloadPlayPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SPlaySoundPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SDisconnectPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SEntityStatusPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SExplosionPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SUnloadChunkPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SChangeGameStatePacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SOpenHorseWindowPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SKeepAlivePacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SChunkDataPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SPlaySoundEventPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SSpawnParticlePacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SUpdateLightPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SJoinGamePacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SMapDataPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SMerchantOffersPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SEntityPacket.RelativeMovePacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SEntityPacket.MovePacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SEntityPacket.LookPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SEntityPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SMoveVehiclePacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SOpenBookWindowPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SOpenWindowPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SOpenSignMenuPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SPlaceGhostRecipePacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SPlayerAbilitiesPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SCombatPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SPlayerListItemPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SPlayerLookPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SPlayerPositionLookPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SRecipeBookPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SDestroyEntitiesPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SRemoveEntityEffectPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SSendResourcePackPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SRespawnPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SEntityHeadLookPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SSelectAdvancementsTabPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SWorldBorderPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SCameraPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SHeldItemChangePacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SUpdateChunkPositionPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SUpdateViewDistancePacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SDisplayObjectivePacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SEntityMetadataPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SMountEntityPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SEntityVelocityPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SEntityEquipmentPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SSetExperiencePacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SUpdateHealthPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SScoreboardObjectivePacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SSetPassengersPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, STeamsPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SUpdateScorePacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SSpawnPositionPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SUpdateTimePacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, STitlePacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SSpawnMovingSoundEffectPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SPlaySoundEffectPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SStopSoundPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SPlayerListHeaderFooterPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SQueryNBTResponsePacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SCollectItemPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SEntityTeleportPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SAdvancementInfoPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SEntityPropertiesPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SPlayEntityEffectPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SUpdateRecipesPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, STagsListPacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CConfirmTeleportPacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CQueryTileEntityNBTPacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CSetDifficultyPacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CChatMessagePacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CClientStatusPacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CClientSettingsPacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CTabCompletePacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CConfirmTransactionPacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CEnchantItemPacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CClickWindowPacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CCloseWindowPacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CCustomPayloadPacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CEditBookPacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CQueryEntityNBTPacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CUseEntityPacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CKeepAlivePacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CLockDifficultyPacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CPlayerPacket.PositionPacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CPlayerPacket.PositionRotationPacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CPlayerPacket.RotationPacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CPlayerPacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CMoveVehiclePacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CSteerBoatPacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CPickItemPacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CPlaceRecipePacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CPlayerAbilitiesPacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CPlayerDiggingPacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CEntityActionPacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CInputPacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CRecipeInfoPacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CRenameItemPacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CResourcePackStatusPacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CSeenAdvancementsPacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CSelectTradePacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CUpdateBeaconPacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CHeldItemChangePacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CUpdateCommandBlockPacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CUpdateMinecartCommandBlockPacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CCreativeInventoryActionPacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CUpdateJigsawBlockPacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CUpdateStructureBlockPacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CUpdateSignPacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CAnimateHandPacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CSpectatePacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CPlayerTryUseItemOnBlockPacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CPlayerTryUseItemPacket.class);
      }
   },
   STATUS(1) {
      {
         this.registerPacket(PacketDirection.SERVERBOUND, CServerQueryPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SServerInfoPacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CPingPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SPongPacket.class);
      }
   },
   LOGIN(2) {
      {
         this.registerPacket(PacketDirection.CLIENTBOUND, SDisconnectLoginPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SEncryptionRequestPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SLoginSuccessPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SEnableCompressionPacket.class);
         this.registerPacket(PacketDirection.CLIENTBOUND, SCustomPayloadLoginPacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CLoginStartPacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CEncryptionResponsePacket.class);
         this.registerPacket(PacketDirection.SERVERBOUND, CCustomPayloadLoginPacket.class);
      }
   };

   private static final ProtocolType[] STATES_BY_ID = new ProtocolType[4];
   private static final Map<Class<? extends IPacket<?>>, ProtocolType> STATES_BY_CLASS = Maps.newHashMap();
   private final int id;
   private final Map<PacketDirection, BiMap<Integer, Class<? extends IPacket<?>>>> directionMaps = Maps.newEnumMap(PacketDirection.class);

   private ProtocolType(int protocolId) {
      this.id = protocolId;
   }

   protected ProtocolType registerPacket(PacketDirection direction, Class<? extends IPacket<?>> packetClass) {
      BiMap<Integer, Class<? extends IPacket<?>>> bimap = this.directionMaps.get(direction);
      if (bimap == null) {
         bimap = HashBiMap.create();
         this.directionMaps.put(direction, bimap);
      }

      if (bimap.containsValue(packetClass)) {
         String s = direction + " packet " + packetClass + " is already known to ID " + bimap.inverse().get(packetClass);
         LogManager.getLogger().fatal(s);
         throw new IllegalArgumentException(s);
      } else {
         bimap.put(bimap.size(), packetClass);
         return this;
      }
   }

   public Integer getPacketId(PacketDirection direction, IPacket<?> packetIn) throws Exception {
      return this.directionMaps.get(direction).inverse().get(packetIn.getClass());
   }

   @Nullable
   public IPacket<?> getPacket(PacketDirection direction, int packetId) throws IllegalAccessException, InstantiationException {
      Class<? extends IPacket<?>> oclass = this.directionMaps.get(direction).get(Integer.valueOf(packetId));
      return oclass == null ? null : oclass.newInstance();
   }

   public int getId() {
      return this.id;
   }

   public static ProtocolType getById(int stateId) {
      return stateId >= -1 && stateId <= 2 ? STATES_BY_ID[stateId - -1] : null;
   }

   public static ProtocolType getFromPacket(IPacket<?> packetIn) {
      return STATES_BY_CLASS.get(packetIn.getClass());
   }

   static {
      for(ProtocolType protocoltype : values()) {
         int i = protocoltype.getId();
         if (i < -1 || i > 2) {
            throw new Error("Invalid protocol ID " + Integer.toString(i));
         }

         STATES_BY_ID[i - -1] = protocoltype;

         for(PacketDirection packetdirection : protocoltype.directionMaps.keySet()) {
            for(Class<? extends IPacket<?>> oclass : protocoltype.directionMaps.get(packetdirection).values()) {
               if (STATES_BY_CLASS.containsKey(oclass) && STATES_BY_CLASS.get(oclass) != protocoltype) {
                  throw new Error("Packet " + oclass + " is already assigned to protocol " + STATES_BY_CLASS.get(oclass) + " - can't reassign to " + protocoltype);
               }

               try {
                  oclass.newInstance();
               } catch (Throwable var10) {
                  throw new Error("Packet " + oclass + " fails instantiation checks! " + oclass);
               }

               STATES_BY_CLASS.put(oclass, protocoltype);
            }
         }
      }

   }
}