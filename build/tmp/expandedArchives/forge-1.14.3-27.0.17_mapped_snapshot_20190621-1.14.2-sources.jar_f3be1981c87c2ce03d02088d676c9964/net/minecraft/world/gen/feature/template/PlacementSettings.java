package net.minecraft.world.gen.feature.template;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;

public class PlacementSettings {
   private Mirror mirror = Mirror.NONE;
   private Rotation rotation = Rotation.NONE;
   private BlockPos centerOffset = BlockPos.ZERO;
   private boolean ignoreEntities;
   @Nullable
   private ChunkPos chunk;
   @Nullable
   private MutableBoundingBox boundingBox;
   private boolean field_204765_h = true;
   @Nullable
   private Random random;
   @Nullable
   private Integer field_204766_l;
   private int field_204767_m;
   private final List<StructureProcessor> processors = Lists.newArrayList();
   private boolean field_215225_l;

   public PlacementSettings copy() {
      PlacementSettings placementsettings = new PlacementSettings();
      placementsettings.mirror = this.mirror;
      placementsettings.rotation = this.rotation;
      placementsettings.centerOffset = this.centerOffset;
      placementsettings.ignoreEntities = this.ignoreEntities;
      placementsettings.chunk = this.chunk;
      placementsettings.boundingBox = this.boundingBox;
      placementsettings.field_204765_h = this.field_204765_h;
      placementsettings.random = this.random;
      placementsettings.field_204766_l = this.field_204766_l;
      placementsettings.field_204767_m = this.field_204767_m;
      placementsettings.processors.addAll(this.processors);
      placementsettings.field_215225_l = this.field_215225_l;
      return placementsettings;
   }

   public PlacementSettings setMirror(Mirror mirrorIn) {
      this.mirror = mirrorIn;
      return this;
   }

   public PlacementSettings setRotation(Rotation rotationIn) {
      this.rotation = rotationIn;
      return this;
   }

   public PlacementSettings setCenterOffset(BlockPos center) {
      this.centerOffset = center;
      return this;
   }

   public PlacementSettings setIgnoreEntities(boolean ignoreEntitiesIn) {
      this.ignoreEntities = ignoreEntitiesIn;
      return this;
   }

   public PlacementSettings setChunk(ChunkPos chunkPosIn) {
      this.chunk = chunkPosIn;
      return this;
   }

   public PlacementSettings setBoundingBox(MutableBoundingBox boundingBoxIn) {
      this.boundingBox = boundingBoxIn;
      return this;
   }

   public PlacementSettings setRandom(@Nullable Random randomIn) {
      this.random = randomIn;
      return this;
   }

   public PlacementSettings func_215223_c(boolean p_215223_1_) {
      this.field_215225_l = p_215223_1_;
      return this;
   }

   public PlacementSettings func_215219_b() {
      this.processors.clear();
      return this;
   }

   public PlacementSettings addProcessor(StructureProcessor p_215222_1_) {
      this.processors.add(p_215222_1_);
      return this;
   }

   public PlacementSettings func_215220_b(StructureProcessor p_215220_1_) {
      this.processors.remove(p_215220_1_);
      return this;
   }

   public Mirror getMirror() {
      return this.mirror;
   }

   public Rotation getRotation() {
      return this.rotation;
   }

   public BlockPos func_207664_d() {
      return this.centerOffset;
   }

   public Random getRandom(@Nullable BlockPos seed) {
      if (this.random != null) {
         return this.random;
      } else {
         return seed == null ? new Random(Util.milliTime()) : new Random(MathHelper.getPositionRandom(seed));
      }
   }

   public boolean getIgnoreEntities() {
      return this.ignoreEntities;
   }

   @Nullable
   public MutableBoundingBox getBoundingBox() {
      if (this.boundingBox == null && this.chunk != null) {
         this.setBoundingBoxFromChunk();
      }

      return this.boundingBox;
   }

   public boolean func_215218_i() {
      return this.field_215225_l;
   }

   public List<StructureProcessor> getProcessors() {
      return this.processors;
   }

   void setBoundingBoxFromChunk() {
      if (this.chunk != null) {
         this.boundingBox = this.getBoundingBoxFromChunk(this.chunk);
      }

   }

   public boolean func_204763_l() {
      return this.field_204765_h;
   }

   public List<Template.BlockInfo> func_204764_a(List<List<Template.BlockInfo>> p_204764_1_, @Nullable BlockPos p_204764_2_) {
      this.field_204766_l = 8;
      if (this.field_204766_l != null && this.field_204766_l >= 0 && this.field_204766_l < p_204764_1_.size()) {
         return p_204764_1_.get(this.field_204766_l);
      } else {
         this.field_204766_l = this.getRandom(p_204764_2_).nextInt(p_204764_1_.size());
         return p_204764_1_.get(this.field_204766_l);
      }
   }

   @Nullable
   private MutableBoundingBox getBoundingBoxFromChunk(@Nullable ChunkPos pos) {
      if (pos == null) {
         return this.boundingBox;
      } else {
         int i = pos.x * 16;
         int j = pos.z * 16;
         return new MutableBoundingBox(i, 0, j, i + 16 - 1, 255, j + 16 - 1);
      }
   }
}