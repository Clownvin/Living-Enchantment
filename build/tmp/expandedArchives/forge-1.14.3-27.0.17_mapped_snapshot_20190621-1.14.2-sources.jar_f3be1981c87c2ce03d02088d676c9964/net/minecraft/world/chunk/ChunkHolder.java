package net.minecraft.world.chunk;

import com.mojang.datafixers.util.Either;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SChangeBlockPacket;
import net.minecraft.network.play.server.SChunkDataPacket;
import net.minecraft.network.play.server.SMultiBlockChangePacket;
import net.minecraft.network.play.server.SUpdateLightPacket;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ChunkHolder {
   public static final Either<IChunk, ChunkHolder.IChunkLoadingError> field_219306_a = Either.right(ChunkHolder.IChunkLoadingError.UNLOADED);
   public static final CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> field_219307_b = CompletableFuture.completedFuture(field_219306_a);
   public static final Either<Chunk, ChunkHolder.IChunkLoadingError> UNLOADED_CHUNK = Either.right(ChunkHolder.IChunkLoadingError.UNLOADED);
   private static final CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> UNLOADED_CHUNK_FUTURE = CompletableFuture.completedFuture(UNLOADED_CHUNK);
   private static final List<ChunkStatus> CHUNK_STATUS_LIST = ChunkStatus.getAll();
   private static final ChunkHolder.LocationType[] field_219311_f = ChunkHolder.LocationType.values();
   private final AtomicReferenceArray<CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>>> field_219312_g = new AtomicReferenceArray<>(CHUNK_STATUS_LIST.size());
   private volatile CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> field_222983_h = UNLOADED_CHUNK_FUTURE;
   private volatile CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> field_219313_h = UNLOADED_CHUNK_FUTURE;
   private volatile CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> field_219314_i = UNLOADED_CHUNK_FUTURE;
   private CompletableFuture<IChunk> field_219315_j = CompletableFuture.completedFuture((IChunk)null);
   private int field_219316_k;
   private int field_219317_l;
   private int field_219318_m;
   private final ChunkPos pos;
   private short[] changedBlockPositions = new short[64];
   private int changedBlocks;
   private int blockChangeMask;
   private int field_219323_r;
   private int blockLightChangeMask;
   private int skyLightChangeMask;
   private final WorldLightManager lightManager;
   private final ChunkHolder.IListener field_219327_v;
   private final ChunkHolder.IPlayerProvider playerProvider;
   private boolean field_219329_x;

   public ChunkHolder(ChunkPos p_i50716_1_, int p_i50716_2_, WorldLightManager p_i50716_3_, ChunkHolder.IListener p_i50716_4_, ChunkHolder.IPlayerProvider p_i50716_5_) {
      this.pos = p_i50716_1_;
      this.lightManager = p_i50716_3_;
      this.field_219327_v = p_i50716_4_;
      this.playerProvider = p_i50716_5_;
      this.field_219316_k = ChunkManager.field_219249_a + 1;
      this.field_219317_l = this.field_219316_k;
      this.field_219318_m = this.field_219316_k;
      this.func_219292_a(p_i50716_2_);
   }

   public CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> func_219301_a(ChunkStatus p_219301_1_) {
      CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture = this.field_219312_g.get(p_219301_1_.ordinal());
      return completablefuture == null ? field_219307_b : completablefuture;
   }

   public CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> func_219296_a() {
      return this.field_219313_h;
   }

   public CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> func_219297_b() {
      return this.field_219314_i;
   }

   public CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> func_223492_c() {
      return this.field_222983_h;
   }

   @Nullable
   public Chunk func_219298_c() {
      CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> completablefuture = this.func_219296_a();
      Either<Chunk, ChunkHolder.IChunkLoadingError> either = completablefuture.getNow((Either<Chunk, ChunkHolder.IChunkLoadingError>)null);
      return either == null ? null : either.left().orElse((Chunk)null);
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public ChunkStatus func_219285_d() {
      for(int i = CHUNK_STATUS_LIST.size() - 1; i >= 0; --i) {
         ChunkStatus chunkstatus = CHUNK_STATUS_LIST.get(i);
         CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture = this.func_219301_a(chunkstatus);
         if (completablefuture.getNow(field_219306_a).left().isPresent()) {
            return chunkstatus;
         }
      }

      return null;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public IChunk func_219287_e() {
      for(int i = CHUNK_STATUS_LIST.size() - 1; i >= 0; --i) {
         ChunkStatus chunkstatus = CHUNK_STATUS_LIST.get(i);
         CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture = this.func_219301_a(chunkstatus);
         if (!completablefuture.isCompletedExceptionally()) {
            Optional<IChunk> optional = completablefuture.getNow(field_219306_a).left();
            if (optional.isPresent()) {
               return optional.get();
            }
         }
      }

      return null;
   }

   public CompletableFuture<IChunk> func_219302_f() {
      return this.field_219315_j;
   }

   public void markBlockChanged(int x, int y, int z) {
      Chunk chunk = this.func_219298_c();
      if (chunk != null) {
         this.blockChangeMask |= 1 << (y >> 4);
         { //Forge; Cache everything, so always run
            short short1 = (short)(x << 12 | z << 8 | y);

            for(int i = 0; i < this.changedBlocks; ++i) {
               if (this.changedBlockPositions[i] == short1) {
                  return;
               }
            }

            if (this.changedBlocks == this.changedBlockPositions.length)
               this.changedBlockPositions = java.util.Arrays.copyOf(this.changedBlockPositions, this.changedBlockPositions.length << 1);
            this.changedBlockPositions[this.changedBlocks++] = short1;
         }

      }
   }

   public void markLightChanged(LightType type, int sectionY) {
      Chunk chunk = this.func_219298_c();
      if (chunk != null) {
         chunk.setModified(true);
         if (type == LightType.SKY) {
            this.skyLightChangeMask |= 1 << sectionY - -1;
         } else {
            this.blockLightChangeMask |= 1 << sectionY - -1;
         }

      }
   }

   public void sendChanges(Chunk chunkIn) {
      if (this.changedBlocks != 0 || this.skyLightChangeMask != 0 || this.blockLightChangeMask != 0) {
         World world = chunkIn.getWorld();
         if (this.changedBlocks >= net.minecraftforge.common.ForgeConfig.SERVER.clumpingThreshold.get()) {
            this.field_219323_r = -1;
         }

         if (this.skyLightChangeMask != 0 || this.blockLightChangeMask != 0) {
            this.sendToTracking(new SUpdateLightPacket(chunkIn.getPos(), this.lightManager, this.skyLightChangeMask & ~this.field_219323_r, this.blockLightChangeMask & ~this.field_219323_r), true);
            int i = this.skyLightChangeMask & this.field_219323_r;
            int j = this.blockLightChangeMask & this.field_219323_r;
            if (i != 0 || j != 0) {
               this.sendToTracking(new SUpdateLightPacket(chunkIn.getPos(), this.lightManager, i, j), false);
            }

            this.skyLightChangeMask = 0;
            this.blockLightChangeMask = 0;
            this.field_219323_r &= ~(this.skyLightChangeMask & this.blockLightChangeMask);
         }

         if (this.changedBlocks == 1) {
            int l = (this.changedBlockPositions[0] >> 12 & 15) + this.pos.x * 16;
            int j1 = this.changedBlockPositions[0] & 255;
            int k = (this.changedBlockPositions[0] >> 8 & 15) + this.pos.z * 16;
            BlockPos blockpos = new BlockPos(l, j1, k);
            this.sendToTracking(new SChangeBlockPacket(world, blockpos), false);
            if (world.getBlockState(blockpos).hasTileEntity()) {
               this.sendTileEntity(world, blockpos);
            }
         } else if (this.changedBlocks >= net.minecraftforge.common.ForgeConfig.SERVER.clumpingThreshold.get()) {
            this.sendToTracking(new SChunkDataPacket(chunkIn, this.blockChangeMask), false);
         } else if (this.changedBlocks != 0) {
            this.sendToTracking(new SMultiBlockChangePacket(this.changedBlocks, this.changedBlockPositions, chunkIn), false);
            for(int i1 = 0; i1 < this.changedBlocks; ++i1) {
               int k1 = (this.changedBlockPositions[i1] >> 12 & 15) + this.pos.x * 16;
               int l1 = this.changedBlockPositions[i1] & 255;
               int i2 = (this.changedBlockPositions[i1] >> 8 & 15) + this.pos.z * 16;
               BlockPos blockpos1 = new BlockPos(k1, l1, i2);
               if (world.getBlockState(blockpos1).hasTileEntity()) {
                  this.sendTileEntity(world, blockpos1);
               }
            }
         }

         this.changedBlocks = 0;
         this.blockChangeMask = 0;
      }
   }

   private void sendTileEntity(World worldIn, BlockPos posIn) {
      TileEntity tileentity = worldIn.getTileEntity(posIn);
      if (tileentity != null) {
         SUpdateTileEntityPacket supdatetileentitypacket = tileentity.getUpdatePacket();
         if (supdatetileentitypacket != null) {
            this.sendToTracking(supdatetileentitypacket, false);
         }
      }

   }

   private void sendToTracking(IPacket<?> packetIn, boolean boundaryOnly) {
      this.playerProvider.getTrackingPlayers(this.pos, boundaryOnly).forEach((p_219304_1_) -> {
         p_219304_1_.connection.sendPacket(packetIn);
      });
   }

   public CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> func_219276_a(ChunkStatus p_219276_1_, ChunkManager p_219276_2_) {
      int i = p_219276_1_.ordinal();
      CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture = this.field_219312_g.get(i);
      if (completablefuture != null) {
         Either<IChunk, ChunkHolder.IChunkLoadingError> either = completablefuture.getNow((Either<IChunk, ChunkHolder.IChunkLoadingError>)null);
         if (either == null || either.left().isPresent()) {
            return completablefuture;
         }
      }

      if (func_219278_b(this.field_219317_l).isAtLeast(p_219276_1_)) {
         CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture1 = p_219276_2_.func_219244_a(this, p_219276_1_);
         this.func_219284_a(completablefuture1);
         this.field_219312_g.set(i, completablefuture1);
         return completablefuture1;
      } else {
         return completablefuture == null ? field_219307_b : completablefuture;
      }
   }

   private void func_219284_a(CompletableFuture<? extends Either<? extends IChunk, ChunkHolder.IChunkLoadingError>> p_219284_1_) {
      this.field_219315_j = this.field_219315_j.thenCombine(p_219284_1_, (p_219295_0_, p_219295_1_) -> {
         return p_219295_1_.map((p_219283_0_) -> {
            return p_219283_0_;
         }, (p_219288_1_) -> {
            return p_219295_0_;
         });
      });
   }

   @OnlyIn(Dist.CLIENT)
   public ChunkHolder.LocationType func_219300_g() {
      return func_219286_c(this.field_219317_l);
   }

   public ChunkPos getPosition() {
      return this.pos;
   }

   public int func_219299_i() {
      return this.field_219317_l;
   }

   public int func_219281_j() {
      return this.field_219318_m;
   }

   private void func_219275_d(int p_219275_1_) {
      this.field_219318_m = p_219275_1_;
   }

   public void func_219292_a(int p_219292_1_) {
      this.field_219317_l = p_219292_1_;
   }

   protected void func_219291_a(ChunkManager p_219291_1_) {
      ChunkStatus chunkstatus = func_219278_b(this.field_219316_k);
      ChunkStatus chunkstatus1 = func_219278_b(this.field_219317_l);
      boolean flag = this.field_219316_k <= ChunkManager.field_219249_a;
      boolean flag1 = this.field_219317_l <= ChunkManager.field_219249_a;
      ChunkHolder.LocationType chunkholder$locationtype = func_219286_c(this.field_219316_k);
      ChunkHolder.LocationType chunkholder$locationtype1 = func_219286_c(this.field_219317_l);
      if (flag) {
         Either<IChunk, ChunkHolder.IChunkLoadingError> either = Either.right(new ChunkHolder.IChunkLoadingError() {
            public String toString() {
               return "Unloaded ticket level " + ChunkHolder.this.pos.toString();
            }
         });

         for(int i = flag1 ? chunkstatus1.ordinal() + 1 : 0; i <= chunkstatus.ordinal(); ++i) {
            CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture = this.field_219312_g.get(i);
            if (completablefuture != null) {
               completablefuture.complete(either);
            } else {
               this.field_219312_g.set(i, CompletableFuture.completedFuture(either));
            }
         }
      }

      boolean flag5 = chunkholder$locationtype.isAtLeast(ChunkHolder.LocationType.BORDER);
      boolean flag6 = chunkholder$locationtype1.isAtLeast(ChunkHolder.LocationType.BORDER);
      this.field_219329_x |= flag6;
      if (!flag5 && flag6) {
         this.field_222983_h = p_219291_1_.func_222961_b(this);
         this.func_219284_a(this.field_222983_h);
      }

      if (flag5 && !flag6) {
         CompletableFuture<Either<Chunk, ChunkHolder.IChunkLoadingError>> completablefuture1 = this.field_222983_h;
         this.field_222983_h = UNLOADED_CHUNK_FUTURE;
         this.func_219284_a(completablefuture1.thenApply((p_222982_1_) -> {
            return p_222982_1_.ifLeft(p_219291_1_::func_222973_a);
         }));
      }

      boolean flag7 = chunkholder$locationtype.isAtLeast(ChunkHolder.LocationType.TICKING);
      boolean flag2 = chunkholder$locationtype1.isAtLeast(ChunkHolder.LocationType.TICKING);
      if (!flag7 && flag2) {
         this.field_219313_h = p_219291_1_.func_219179_a(this);
         this.func_219284_a(this.field_219313_h);
      }

      if (flag7 && !flag2) {
         this.field_219313_h.complete(UNLOADED_CHUNK);
         this.field_219313_h = UNLOADED_CHUNK_FUTURE;
      }

      boolean flag3 = chunkholder$locationtype.isAtLeast(ChunkHolder.LocationType.ENTITY_TICKING);
      boolean flag4 = chunkholder$locationtype1.isAtLeast(ChunkHolder.LocationType.ENTITY_TICKING);
      if (!flag3 && flag4) {
         if (this.field_219314_i != UNLOADED_CHUNK_FUTURE) {
            throw new IllegalStateException();
         }

         this.field_219314_i = p_219291_1_.func_219188_b(this.pos);
         this.func_219284_a(this.field_219314_i);
      }

      if (flag3 && !flag4) {
         this.field_219314_i.complete(UNLOADED_CHUNK);
         this.field_219314_i = UNLOADED_CHUNK_FUTURE;
      }

      this.field_219327_v.func_219066_a(this.pos, this::func_219281_j, this.field_219317_l, this::func_219275_d);
      this.field_219316_k = this.field_219317_l;
   }

   public static ChunkStatus func_219278_b(int p_219278_0_) {
      return p_219278_0_ < 33 ? ChunkStatus.FULL : ChunkStatus.func_222581_a(p_219278_0_ - 33);
   }

   public static ChunkHolder.LocationType func_219286_c(int p_219286_0_) {
      return field_219311_f[MathHelper.clamp(33 - p_219286_0_ + 1, 0, field_219311_f.length - 1)];
   }

   public boolean func_219289_k() {
      return this.field_219329_x;
   }

   public void func_219303_l() {
      this.field_219329_x = func_219286_c(this.field_219317_l).isAtLeast(ChunkHolder.LocationType.BORDER);
   }

   public void func_219294_a(ChunkPrimerWrapper p_219294_1_) {
      for(int i = 0; i < this.field_219312_g.length(); ++i) {
         CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> completablefuture = this.field_219312_g.get(i);
         if (completablefuture != null) {
            Optional<IChunk> optional = completablefuture.getNow(field_219306_a).left();
            if (optional.isPresent() && optional.get() instanceof ChunkPrimer) {
               this.field_219312_g.set(i, CompletableFuture.completedFuture(Either.left(p_219294_1_)));
            }
         }
      }

      this.func_219284_a(CompletableFuture.completedFuture(Either.left(p_219294_1_.func_217336_u())));
   }

   public interface IChunkLoadingError {
      ChunkHolder.IChunkLoadingError UNLOADED = new ChunkHolder.IChunkLoadingError() {
         public String toString() {
            return "UNLOADED";
         }
      };
   }

   public interface IListener {
      void func_219066_a(ChunkPos pos, IntSupplier p_219066_2_, int p_219066_3_, IntConsumer p_219066_4_);
   }

   public interface IPlayerProvider {
      /**
       * Returns the players tracking the given chunk.
       *  
       * @param boundaryOnly If true, returns only players whose tracking area contains the chunk on its boundary.
       */
      Stream<ServerPlayerEntity> getTrackingPlayers(ChunkPos pos, boolean boundaryOnly);
   }

   public static enum LocationType {
      INACCESSIBLE,
      BORDER,
      TICKING,
      ENTITY_TICKING;

      public boolean isAtLeast(ChunkHolder.LocationType type) {
         return this.ordinal() >= type.ordinal();
      }
   }
}