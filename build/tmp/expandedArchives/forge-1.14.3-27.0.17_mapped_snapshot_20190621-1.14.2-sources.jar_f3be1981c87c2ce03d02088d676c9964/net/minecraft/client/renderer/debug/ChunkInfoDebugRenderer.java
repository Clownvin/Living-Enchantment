package net.minecraft.client.renderer.debug;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkProvider;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ServerChunkProvider;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChunkInfoDebugRenderer implements DebugRenderer.IDebugRenderer {
   private final Minecraft client;
   private double field_217679_b = Double.MIN_VALUE;
   private final int field_217680_c = 12;
   @Nullable
   private ChunkInfoDebugRenderer.Entry field_217681_d;

   public ChunkInfoDebugRenderer(Minecraft client) {
      this.client = client;
   }

   public void render(long p_217676_1_) {
      double d0 = (double)Util.nanoTime();
      if (d0 - this.field_217679_b > 3.0E9D) {
         this.field_217679_b = d0;
         IntegratedServer integratedserver = this.client.getIntegratedServer();
         if (integratedserver != null) {
            this.field_217681_d = new ChunkInfoDebugRenderer.Entry(integratedserver);
         } else {
            this.field_217681_d = null;
         }
      }

      if (this.field_217681_d != null) {
         GlStateManager.disableFog();
         GlStateManager.enableBlend();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         GlStateManager.lineWidth(2.0F);
         GlStateManager.disableTexture();
         GlStateManager.depthMask(false);
         Map<ChunkPos, String> map = this.field_217681_d.field_217722_c.getNow((Map<ChunkPos, String>)null);
         double d1 = this.client.gameRenderer.getActiveRenderInfo().getProjectedView().y * 0.85D;

         for(Map.Entry<ChunkPos, String> entry : this.field_217681_d.field_217721_b.entrySet()) {
            ChunkPos chunkpos = entry.getKey();
            String s = entry.getValue();
            if (map != null) {
               s = s + (String)map.get(chunkpos);
            }

            String[] astring = s.split("\n");
            int i = 0;

            for(String s1 : astring) {
               DebugRenderer.func_217729_a(s1, (double)((chunkpos.x << 4) + 8), d1 + (double)i, (double)((chunkpos.z << 4) + 8), -1, 0.15F);
               i -= 2;
            }
         }

         GlStateManager.depthMask(true);
         GlStateManager.enableTexture();
         GlStateManager.disableBlend();
         GlStateManager.enableFog();
      }

   }

   @OnlyIn(Dist.CLIENT)
   final class Entry {
      private final Map<ChunkPos, String> field_217721_b;
      private final CompletableFuture<Map<ChunkPos, String>> field_217722_c;

      private Entry(IntegratedServer p_i49965_2_) {
         ClientWorld clientworld = ChunkInfoDebugRenderer.this.client.world;
         DimensionType dimensiontype = ChunkInfoDebugRenderer.this.client.world.dimension.getType();
         ServerWorld serverworld;
         if (p_i49965_2_.getWorld(dimensiontype) != null) {
            serverworld = p_i49965_2_.getWorld(dimensiontype);
         } else {
            serverworld = null;
         }

         ActiveRenderInfo activerenderinfo = ChunkInfoDebugRenderer.this.client.gameRenderer.getActiveRenderInfo();
         int i = (int)activerenderinfo.getProjectedView().x >> 4;
         int j = (int)activerenderinfo.getProjectedView().z >> 4;
         Builder<ChunkPos, String> builder = ImmutableMap.builder();
         ClientChunkProvider clientchunkprovider = clientworld.getChunkProvider();

         for(int k = i - 12; k <= i + 12; ++k) {
            for(int l = j - 12; l <= j + 12; ++l) {
               ChunkPos chunkpos = new ChunkPos(k, l);
               String s = "";
               Chunk chunk = clientchunkprovider.getChunk(k, l, false);
               s = s + "Client: ";
               if (chunk == null) {
                  s = s + "0n/a\n";
               } else {
                  s = s + (chunk.isEmpty() ? " E" : "");
                  s = s + "\n";
               }

               builder.put(chunkpos, s);
            }
         }

         this.field_217721_b = builder.build();
         this.field_217722_c = p_i49965_2_.supplyAsync(() -> {
            Builder<ChunkPos, String> builder1 = ImmutableMap.builder();
            ServerChunkProvider serverchunkprovider = serverworld.getChunkProvider();

            for(int i1 = i - 12; i1 <= i + 12; ++i1) {
               for(int j1 = j - 12; j1 <= j + 12; ++j1) {
                  ChunkPos chunkpos1 = new ChunkPos(i1, j1);
                  builder1.put(chunkpos1, "Server: " + serverchunkprovider.func_217208_a(chunkpos1));
               }
            }

            return builder1.build();
         });
      }
   }
}