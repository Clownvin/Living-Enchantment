package net.minecraft.world.gen.feature.structure;

import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.template.TemplateManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Structures {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final Structure<?> MINESHAFT = register("Mineshaft", Feature.MINESHAFT);
   public static final Structure<?> PILLAGER_OUTPOST = register("Pillager_Outpost", Feature.PILLAGER_OUTPOST);
   public static final Structure<?> FORTRESS = register("Fortress", Feature.NETHER_BRIDGE);
   public static final Structure<?> STRONGHOLD = register("Stronghold", Feature.STRONGHOLD);
   public static final Structure<?> JUNGLE_PYRAMID = register("Jungle_Pyramid", Feature.JUNGLE_TEMPLE);
   public static final Structure<?> OCEAN_RUIN = register("Ocean_Ruin", Feature.OCEAN_RUIN);
   public static final Structure<?> DESERT_PYRAMID = register("Desert_Pyramid", Feature.DESERT_PYRAMID);
   public static final Structure<?> IGLOO = register("Igloo", Feature.IGLOO);
   public static final Structure<?> SWAMP_HUT = register("Swamp_Hut", Feature.SWAMP_HUT);
   public static final Structure<?> MONUMENT = register("Monument", Feature.OCEAN_MONUMENT);
   public static final Structure<?> ENDCITY = register("EndCity", Feature.END_CITY);
   public static final Structure<?> MANSION = register("Mansion", Feature.WOODLAND_MANSION);
   public static final Structure<?> BURIED_TREASURE = register("Buried_Treasure", Feature.BURIED_TREASURE);
   public static final Structure<?> SHIPWRECK = register("Shipwreck", Feature.SHIPWRECK);
   public static final Structure<?> VILLAGE = register("Village", Feature.VILLAGE);

   private static Structure<?> register(String key, Structure<?> p_215141_1_) {
      return Registry.register(Registry.STRUCTURE_FEATURE, key.toLowerCase(Locale.ROOT), p_215141_1_);
   }

   public static void init() {
   }

   @Nullable
   public static StructureStart func_215142_a(ChunkGenerator<?> p_215142_0_, TemplateManager p_215142_1_, BiomeProvider p_215142_2_, CompoundNBT p_215142_3_) {
      String s = p_215142_3_.getString("id");
      if ("INVALID".equals(s)) {
         return StructureStart.DUMMY;
      } else {
         Structure<?> structure = Registry.STRUCTURE_FEATURE.getOrDefault(new ResourceLocation(s.toLowerCase(Locale.ROOT)));
         if (structure == null) {
            LOGGER.error("Unknown feature id: {}", (Object)s);
            return null;
         } else {
            int i = p_215142_3_.getInt("ChunkX");
            int j = p_215142_3_.getInt("ChunkZ");
            Biome biome = p_215142_3_.contains("biome") ? Registry.BIOME.getOrDefault(new ResourceLocation(p_215142_3_.getString("biome"))) : p_215142_2_.getBiome(new BlockPos((i << 4) + 9, 0, (j << 4) + 9));
            MutableBoundingBox mutableboundingbox = p_215142_3_.contains("BB") ? new MutableBoundingBox(p_215142_3_.getIntArray("BB")) : MutableBoundingBox.getNewBoundingBox();
            ListNBT listnbt = p_215142_3_.getList("Children", 10);

            try {
               StructureStart structurestart = structure.getStartFactory().create(structure, i, j, biome, mutableboundingbox, 0, p_215142_0_.getSeed());

               for(int k = 0; k < listnbt.size(); ++k) {
                  CompoundNBT compoundnbt = listnbt.getCompound(k);
                  String s1 = compoundnbt.getString("id");
                  IStructurePieceType istructurepiecetype = Registry.STRUCTURE_PIECE.getOrDefault(new ResourceLocation(s1.toLowerCase(Locale.ROOT)));
                  if (istructurepiecetype == null) {
                     LOGGER.error("Unknown structure piece id: {}", (Object)s1);
                  } else {
                     try {
                        StructurePiece structurepiece = istructurepiecetype.load(p_215142_1_, compoundnbt);
                        structurestart.components.add(structurepiece);
                     } catch (Exception exception) {
                        LOGGER.error("Exception loading structure piece with id {}", s1, exception);
                     }
                  }
               }

               return structurestart;
            } catch (Exception exception1) {
               LOGGER.error("Failed Start with id {}", s, exception1);
               return null;
            }
         }
      }
   }
}