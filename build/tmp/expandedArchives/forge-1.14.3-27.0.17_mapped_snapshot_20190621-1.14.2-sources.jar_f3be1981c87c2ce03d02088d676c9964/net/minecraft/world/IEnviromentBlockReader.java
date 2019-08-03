package net.minecraft.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IEnviromentBlockReader extends IBlockReader {
   Biome getBiome(BlockPos pos);

   int getLightFor(LightType type, BlockPos pos);

   default boolean func_217337_f(BlockPos pos) {
      return this.getLightFor(LightType.SKY, pos) >= this.getMaxLightLevel();
   }

   @OnlyIn(Dist.CLIENT)
   default int getCombinedLight(BlockPos pos, int minLight) {
      int i = this.getLightFor(LightType.SKY, pos);
      int j = this.getLightFor(LightType.BLOCK, pos);
      if (j < minLight) {
         j = minLight;
      }

      return i << 20 | j << 4;
   }
}