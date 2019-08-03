package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import it.unimi.dsi.fastutil.objects.Object2FloatLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockModelRenderer {
   private final BlockColors blockColors;
   private static final ThreadLocal<BlockModelRenderer.Cache> CACHE_COMBINED_LIGHT = ThreadLocal.withInitial(() -> {
      return new BlockModelRenderer.Cache();
   });

   public BlockModelRenderer(BlockColors blockColorsIn) {
      this.blockColors = blockColorsIn;
   }

   @Deprecated //Forge: Model data argument
   public boolean renderModel(IEnviromentBlockReader p_217631_1_, IBakedModel p_217631_2_, BlockState p_217631_3_, BlockPos p_217631_4_, BufferBuilder p_217631_5_, boolean p_217631_6_, Random p_217631_7_, long p_217631_8_) {
      return renderModel(p_217631_1_, p_217631_2_, p_217631_3_, p_217631_4_, p_217631_5_, p_217631_6_, p_217631_7_, p_217631_8_, net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
   }

   public boolean renderModel(IEnviromentBlockReader p_217631_1_, IBakedModel p_217631_2_, BlockState p_217631_3_, BlockPos p_217631_4_, BufferBuilder p_217631_5_, boolean p_217631_6_, Random p_217631_7_, long p_217631_8_, net.minecraftforge.client.model.data.IModelData modelData) {
      boolean flag = Minecraft.isAmbientOcclusionEnabled() && p_217631_3_.getLightValue(p_217631_1_, p_217631_4_) == 0 && p_217631_2_.isAmbientOcclusion();
      modelData = p_217631_2_.getModelData(p_217631_1_, p_217631_4_, p_217631_3_, modelData);

      try {
         return flag ? this.renderModelSmooth(p_217631_1_, p_217631_2_, p_217631_3_, p_217631_4_, p_217631_5_, p_217631_6_, p_217631_7_, p_217631_8_, modelData) : this.renderModelFlat(p_217631_1_, p_217631_2_, p_217631_3_, p_217631_4_, p_217631_5_, p_217631_6_, p_217631_7_, p_217631_8_, modelData);
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Tesselating block model");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Block model being tesselated");
         CrashReportCategory.addBlockInfo(crashreportcategory, p_217631_4_, p_217631_3_);
         crashreportcategory.addDetail("Using AO", flag);
         throw new ReportedException(crashreport);
      }
   }

   @Deprecated //Forge: Model data argument
   public boolean renderModelSmooth(IEnviromentBlockReader p_217634_1_, IBakedModel p_217634_2_, BlockState p_217634_3_, BlockPos p_217634_4_, BufferBuilder p_217634_5_, boolean p_217634_6_, Random p_217634_7_, long p_217634_8_) {
      return renderModelSmooth(p_217634_1_, p_217634_2_, p_217634_3_, p_217634_4_, p_217634_5_, p_217634_6_, p_217634_7_, p_217634_8_, net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
   }

   public boolean renderModelSmooth(IEnviromentBlockReader p_217634_1_, IBakedModel p_217634_2_, BlockState p_217634_3_, BlockPos p_217634_4_, BufferBuilder p_217634_5_, boolean p_217634_6_, Random p_217634_7_, long p_217634_8_, net.minecraftforge.client.model.data.IModelData modelData) {
      boolean flag = false;
      float[] afloat = new float[Direction.values().length * 2];
      BitSet bitset = new BitSet(3);
      BlockModelRenderer.AmbientOcclusionFace blockmodelrenderer$ambientocclusionface = new BlockModelRenderer.AmbientOcclusionFace();

      for(Direction direction : Direction.values()) {
         p_217634_7_.setSeed(p_217634_8_);
         List<BakedQuad> list = p_217634_2_.getQuads(p_217634_3_, direction, p_217634_7_, modelData);
         if (!list.isEmpty() && (!p_217634_6_ || Block.shouldSideBeRendered(p_217634_3_, p_217634_1_, p_217634_4_, direction))) {
            this.renderQuadsSmooth(p_217634_1_, p_217634_3_, p_217634_4_, p_217634_5_, list, afloat, bitset, blockmodelrenderer$ambientocclusionface);
            flag = true;
         }
      }

      p_217634_7_.setSeed(p_217634_8_);
      List<BakedQuad> list1 = p_217634_2_.getQuads(p_217634_3_, (Direction)null, p_217634_7_, modelData);
      if (!list1.isEmpty()) {
         this.renderQuadsSmooth(p_217634_1_, p_217634_3_, p_217634_4_, p_217634_5_, list1, afloat, bitset, blockmodelrenderer$ambientocclusionface);
         flag = true;
      }

      return flag;
   }

   @Deprecated //Forge: Model data Argument
   public boolean renderModelFlat(IEnviromentBlockReader p_217635_1_, IBakedModel p_217635_2_, BlockState p_217635_3_, BlockPos p_217635_4_, BufferBuilder p_217635_5_, boolean p_217635_6_, Random p_217635_7_, long p_217635_8_) {
      return renderModelFlat(p_217635_1_, p_217635_2_, p_217635_3_, p_217635_4_, p_217635_5_, p_217635_6_, p_217635_7_, p_217635_8_, net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
   }

   public boolean renderModelFlat(IEnviromentBlockReader p_217635_1_, IBakedModel p_217635_2_, BlockState p_217635_3_, BlockPos p_217635_4_, BufferBuilder p_217635_5_, boolean p_217635_6_, Random p_217635_7_, long p_217635_8_, net.minecraftforge.client.model.data.IModelData modelData) {
      boolean flag = false;
      BitSet bitset = new BitSet(3);

      for(Direction direction : Direction.values()) {
         p_217635_7_.setSeed(p_217635_8_);
         List<BakedQuad> list = p_217635_2_.getQuads(p_217635_3_, direction, p_217635_7_, modelData);
         if (!list.isEmpty() && (!p_217635_6_ || Block.shouldSideBeRendered(p_217635_3_, p_217635_1_, p_217635_4_, direction))) {
            int i = p_217635_3_.getPackedLightmapCoords(p_217635_1_, p_217635_4_.offset(direction));
            this.renderQuadsFlat(p_217635_1_, p_217635_3_, p_217635_4_, i, false, p_217635_5_, list, bitset);
            flag = true;
         }
      }

      p_217635_7_.setSeed(p_217635_8_);
      List<BakedQuad> list1 = p_217635_2_.getQuads(p_217635_3_, (Direction)null, p_217635_7_, modelData);
      if (!list1.isEmpty()) {
         this.renderQuadsFlat(p_217635_1_, p_217635_3_, p_217635_4_, -1, true, p_217635_5_, list1, bitset);
         flag = true;
      }

      return flag;
   }

   private void renderQuadsSmooth(IEnviromentBlockReader p_217630_1_, BlockState p_217630_2_, BlockPos p_217630_3_, BufferBuilder p_217630_4_, List<BakedQuad> p_217630_5_, float[] p_217630_6_, BitSet p_217630_7_, BlockModelRenderer.AmbientOcclusionFace p_217630_8_) {
      Vec3d vec3d = p_217630_2_.getOffset(p_217630_1_, p_217630_3_);
      double d0 = (double)p_217630_3_.getX() + vec3d.x;
      double d1 = (double)p_217630_3_.getY() + vec3d.y;
      double d2 = (double)p_217630_3_.getZ() + vec3d.z;
      int i = 0;

      for(int j = p_217630_5_.size(); i < j; ++i) {
         BakedQuad bakedquad = p_217630_5_.get(i);
         this.fillQuadBounds(p_217630_1_, p_217630_2_, p_217630_3_, bakedquad.getVertexData(), bakedquad.getFace(), p_217630_6_, p_217630_7_);
         p_217630_8_.updateVertexBrightness(p_217630_1_, p_217630_2_, p_217630_3_, bakedquad.getFace(), p_217630_6_, p_217630_7_);
         p_217630_4_.addVertexData(bakedquad.getVertexData());
         p_217630_4_.putBrightness4(p_217630_8_.vertexBrightness[0], p_217630_8_.vertexBrightness[1], p_217630_8_.vertexBrightness[2], p_217630_8_.vertexBrightness[3]);
         if(bakedquad.shouldApplyDiffuseLighting()) {
            float diffuse = net.minecraftforge.client.model.pipeline.LightUtil.diffuseLight(bakedquad.getFace());
            p_217630_8_.vertexColorMultiplier[0] *= diffuse;
            p_217630_8_.vertexColorMultiplier[1] *= diffuse;
            p_217630_8_.vertexColorMultiplier[2] *= diffuse;
            p_217630_8_.vertexColorMultiplier[3] *= diffuse;
         }
         if (bakedquad.hasTintIndex()) {
            int k = this.blockColors.getColor(p_217630_2_, p_217630_1_, p_217630_3_, bakedquad.getTintIndex());
            float f = (float)(k >> 16 & 255) / 255.0F;
            float f1 = (float)(k >> 8 & 255) / 255.0F;
            float f2 = (float)(k & 255) / 255.0F;
            p_217630_4_.putColorMultiplier(p_217630_8_.vertexColorMultiplier[0] * f, p_217630_8_.vertexColorMultiplier[0] * f1, p_217630_8_.vertexColorMultiplier[0] * f2, 4);
            p_217630_4_.putColorMultiplier(p_217630_8_.vertexColorMultiplier[1] * f, p_217630_8_.vertexColorMultiplier[1] * f1, p_217630_8_.vertexColorMultiplier[1] * f2, 3);
            p_217630_4_.putColorMultiplier(p_217630_8_.vertexColorMultiplier[2] * f, p_217630_8_.vertexColorMultiplier[2] * f1, p_217630_8_.vertexColorMultiplier[2] * f2, 2);
            p_217630_4_.putColorMultiplier(p_217630_8_.vertexColorMultiplier[3] * f, p_217630_8_.vertexColorMultiplier[3] * f1, p_217630_8_.vertexColorMultiplier[3] * f2, 1);
         } else {
            p_217630_4_.putColorMultiplier(p_217630_8_.vertexColorMultiplier[0], p_217630_8_.vertexColorMultiplier[0], p_217630_8_.vertexColorMultiplier[0], 4);
            p_217630_4_.putColorMultiplier(p_217630_8_.vertexColorMultiplier[1], p_217630_8_.vertexColorMultiplier[1], p_217630_8_.vertexColorMultiplier[1], 3);
            p_217630_4_.putColorMultiplier(p_217630_8_.vertexColorMultiplier[2], p_217630_8_.vertexColorMultiplier[2], p_217630_8_.vertexColorMultiplier[2], 2);
            p_217630_4_.putColorMultiplier(p_217630_8_.vertexColorMultiplier[3], p_217630_8_.vertexColorMultiplier[3], p_217630_8_.vertexColorMultiplier[3], 1);
         }

         p_217630_4_.putPosition(d0, d1, d2);
      }

   }

   private void fillQuadBounds(IEnviromentBlockReader p_217633_1_, BlockState p_217633_2_, BlockPos p_217633_3_, int[] p_217633_4_, Direction p_217633_5_, @Nullable float[] p_217633_6_, BitSet p_217633_7_) {
      float f = 32.0F;
      float f1 = 32.0F;
      float f2 = 32.0F;
      float f3 = -32.0F;
      float f4 = -32.0F;
      float f5 = -32.0F;

      for(int i = 0; i < 4; ++i) {
         float f6 = Float.intBitsToFloat(p_217633_4_[i * 7]);
         float f7 = Float.intBitsToFloat(p_217633_4_[i * 7 + 1]);
         float f8 = Float.intBitsToFloat(p_217633_4_[i * 7 + 2]);
         f = Math.min(f, f6);
         f1 = Math.min(f1, f7);
         f2 = Math.min(f2, f8);
         f3 = Math.max(f3, f6);
         f4 = Math.max(f4, f7);
         f5 = Math.max(f5, f8);
      }

      if (p_217633_6_ != null) {
         p_217633_6_[Direction.WEST.getIndex()] = f;
         p_217633_6_[Direction.EAST.getIndex()] = f3;
         p_217633_6_[Direction.DOWN.getIndex()] = f1;
         p_217633_6_[Direction.UP.getIndex()] = f4;
         p_217633_6_[Direction.NORTH.getIndex()] = f2;
         p_217633_6_[Direction.SOUTH.getIndex()] = f5;
         int j = Direction.values().length;
         p_217633_6_[Direction.WEST.getIndex() + j] = 1.0F - f;
         p_217633_6_[Direction.EAST.getIndex() + j] = 1.0F - f3;
         p_217633_6_[Direction.DOWN.getIndex() + j] = 1.0F - f1;
         p_217633_6_[Direction.UP.getIndex() + j] = 1.0F - f4;
         p_217633_6_[Direction.NORTH.getIndex() + j] = 1.0F - f2;
         p_217633_6_[Direction.SOUTH.getIndex() + j] = 1.0F - f5;
      }

      float f9 = 1.0E-4F;
      float f10 = 0.9999F;
      switch(p_217633_5_) {
      case DOWN:
         p_217633_7_.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f3 <= 0.9999F || f5 <= 0.9999F);
         p_217633_7_.set(0, (f1 < 1.0E-4F || Block.isOpaque(p_217633_2_.getCollisionShape(p_217633_1_, p_217633_3_))) && f1 == f4);
         break;
      case UP:
         p_217633_7_.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f3 <= 0.9999F || f5 <= 0.9999F);
         p_217633_7_.set(0, (f4 > 0.9999F || Block.isOpaque(p_217633_2_.getCollisionShape(p_217633_1_, p_217633_3_))) && f1 == f4);
         break;
      case NORTH:
         p_217633_7_.set(1, f >= 1.0E-4F || f1 >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
         p_217633_7_.set(0, (f2 < 1.0E-4F || Block.isOpaque(p_217633_2_.getCollisionShape(p_217633_1_, p_217633_3_))) && f2 == f5);
         break;
      case SOUTH:
         p_217633_7_.set(1, f >= 1.0E-4F || f1 >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
         p_217633_7_.set(0, (f5 > 0.9999F || Block.isOpaque(p_217633_2_.getCollisionShape(p_217633_1_, p_217633_3_))) && f2 == f5);
         break;
      case WEST:
         p_217633_7_.set(1, f1 >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
         p_217633_7_.set(0, (f < 1.0E-4F || Block.isOpaque(p_217633_2_.getCollisionShape(p_217633_1_, p_217633_3_))) && f == f3);
         break;
      case EAST:
         p_217633_7_.set(1, f1 >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
         p_217633_7_.set(0, (f3 > 0.9999F || Block.isOpaque(p_217633_2_.getCollisionShape(p_217633_1_, p_217633_3_))) && f == f3);
      }

   }

   private void renderQuadsFlat(IEnviromentBlockReader p_217636_1_, BlockState p_217636_2_, BlockPos p_217636_3_, int p_217636_4_, boolean p_217636_5_, BufferBuilder p_217636_6_, List<BakedQuad> p_217636_7_, BitSet p_217636_8_) {
      Vec3d vec3d = p_217636_2_.getOffset(p_217636_1_, p_217636_3_);
      double d0 = (double)p_217636_3_.getX() + vec3d.x;
      double d1 = (double)p_217636_3_.getY() + vec3d.y;
      double d2 = (double)p_217636_3_.getZ() + vec3d.z;
      int i = 0;

      for(int j = p_217636_7_.size(); i < j; ++i) {
         BakedQuad bakedquad = p_217636_7_.get(i);
         if (p_217636_5_) {
            this.fillQuadBounds(p_217636_1_, p_217636_2_, p_217636_3_, bakedquad.getVertexData(), bakedquad.getFace(), (float[])null, p_217636_8_);
            BlockPos blockpos = p_217636_8_.get(0) ? p_217636_3_.offset(bakedquad.getFace()) : p_217636_3_;
            p_217636_4_ = p_217636_2_.getPackedLightmapCoords(p_217636_1_, blockpos);
         }

         p_217636_6_.addVertexData(bakedquad.getVertexData());
         p_217636_6_.putBrightness4(p_217636_4_, p_217636_4_, p_217636_4_, p_217636_4_);
         if (bakedquad.hasTintIndex()) {
            int k = this.blockColors.getColor(p_217636_2_, p_217636_1_, p_217636_3_, bakedquad.getTintIndex());
            float f = (float)(k >> 16 & 255) / 255.0F;
            float f1 = (float)(k >> 8 & 255) / 255.0F;
            float f2 = (float)(k & 255) / 255.0F;
            if(bakedquad.shouldApplyDiffuseLighting()) {
               float diffuse = net.minecraftforge.client.model.pipeline.LightUtil.diffuseLight(bakedquad.getFace());
               f *= diffuse;
               f1 *= diffuse;
               f2 *= diffuse;
            }
            p_217636_6_.putColorMultiplier(f, f1, f2, 4);
            p_217636_6_.putColorMultiplier(f, f1, f2, 3);
            p_217636_6_.putColorMultiplier(f, f1, f2, 2);
            p_217636_6_.putColorMultiplier(f, f1, f2, 1);
         } else if(bakedquad.shouldApplyDiffuseLighting()) {
            float diffuse = net.minecraftforge.client.model.pipeline.LightUtil.diffuseLight(bakedquad.getFace());
            p_217636_6_.putColorMultiplier(diffuse, diffuse, diffuse, 4);
            p_217636_6_.putColorMultiplier(diffuse, diffuse, diffuse, 3);
            p_217636_6_.putColorMultiplier(diffuse, diffuse, diffuse, 2);
            p_217636_6_.putColorMultiplier(diffuse, diffuse, diffuse, 1);
         }

         p_217636_6_.putPosition(d0, d1, d2);
      }

   }

   public void renderModelBrightnessColor(IBakedModel bakedModel, float brightness, float red, float green, float blue) {
      this.renderModelBrightnessColor((BlockState)null, bakedModel, brightness, red, green, blue);
   }

   public void renderModelBrightnessColor(@Nullable BlockState state, IBakedModel modelIn, float brightness, float red, float green, float blue) {
      Random random = new Random();
      long i = 42L;

      for(Direction direction : Direction.values()) {
         random.setSeed(42L);
         this.renderModelBrightnessColorQuads(brightness, red, green, blue, modelIn.getQuads(state, direction, random));
      }

      random.setSeed(42L);
      this.renderModelBrightnessColorQuads(brightness, red, green, blue, modelIn.getQuads(state, (Direction)null, random));
   }

   public void renderModelBrightness(IBakedModel model, BlockState state, float brightness, boolean glDisabled) {
      GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
      int i = this.blockColors.getColor(state, (IEnviromentBlockReader)null, (BlockPos)null, 0);
      float f = (float)(i >> 16 & 255) / 255.0F;
      float f1 = (float)(i >> 8 & 255) / 255.0F;
      float f2 = (float)(i & 255) / 255.0F;
      if (!glDisabled) {
         GlStateManager.color4f(brightness, brightness, brightness, 1.0F);
      }

      this.renderModelBrightnessColor(state, model, brightness, f, f1, f2);
   }

   private void renderModelBrightnessColorQuads(float brightness, float red, float green, float blue, List<BakedQuad> listQuads) {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      int i = 0;

      for(int j = listQuads.size(); i < j; ++i) {
         BakedQuad bakedquad = listQuads.get(i);
         bufferbuilder.begin(7, DefaultVertexFormats.ITEM);
         bufferbuilder.addVertexData(bakedquad.getVertexData());
         if (bakedquad.hasTintIndex()) {
            bufferbuilder.putColorRGB_F4(red * brightness, green * brightness, blue * brightness);
         } else {
            bufferbuilder.putColorRGB_F4(brightness, brightness, brightness);
         }

         Vec3i vec3i = bakedquad.getFace().getDirectionVec();
         bufferbuilder.putNormal((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
         tessellator.draw();
      }

   }

   public static void enableCache() {
      CACHE_COMBINED_LIGHT.get().func_222895_a();
   }

   public static void disableCache() {
      CACHE_COMBINED_LIGHT.get().func_222897_b();
   }

   @OnlyIn(Dist.CLIENT)
   class AmbientOcclusionFace {
      private final float[] vertexColorMultiplier = new float[4];
      private final int[] vertexBrightness = new int[4];

      public void updateVertexBrightness(IEnviromentBlockReader reader, BlockState state, BlockPos pos, Direction dir, float[] p_217629_5_, BitSet bits) {
         BlockPos blockpos = bits.get(0) ? pos.offset(dir) : pos;
         BlockModelRenderer.NeighborInfo blockmodelrenderer$neighborinfo = BlockModelRenderer.NeighborInfo.getNeighbourInfo(dir);
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
         BlockModelRenderer.Cache blockmodelrenderer$cache = BlockModelRenderer.CACHE_COMBINED_LIGHT.get();
         blockpos$mutableblockpos.setPos(blockpos).move(blockmodelrenderer$neighborinfo.corners[0]);
         BlockState blockstate = reader.getBlockState(blockpos$mutableblockpos);
         int i = blockmodelrenderer$cache.func_222893_a(blockstate, reader, blockpos$mutableblockpos);
         float f = blockmodelrenderer$cache.func_222896_b(blockstate, reader, blockpos$mutableblockpos);
         blockpos$mutableblockpos.setPos(blockpos).move(blockmodelrenderer$neighborinfo.corners[1]);
         BlockState blockstate1 = reader.getBlockState(blockpos$mutableblockpos);
         int j = blockmodelrenderer$cache.func_222893_a(blockstate1, reader, blockpos$mutableblockpos);
         float f1 = blockmodelrenderer$cache.func_222896_b(blockstate1, reader, blockpos$mutableblockpos);
         blockpos$mutableblockpos.setPos(blockpos).move(blockmodelrenderer$neighborinfo.corners[2]);
         BlockState blockstate2 = reader.getBlockState(blockpos$mutableblockpos);
         int k = blockmodelrenderer$cache.func_222893_a(blockstate2, reader, blockpos$mutableblockpos);
         float f2 = blockmodelrenderer$cache.func_222896_b(blockstate2, reader, blockpos$mutableblockpos);
         blockpos$mutableblockpos.setPos(blockpos).move(blockmodelrenderer$neighborinfo.corners[3]);
         BlockState blockstate3 = reader.getBlockState(blockpos$mutableblockpos);
         int l = blockmodelrenderer$cache.func_222893_a(blockstate3, reader, blockpos$mutableblockpos);
         float f3 = blockmodelrenderer$cache.func_222896_b(blockstate3, reader, blockpos$mutableblockpos);
         blockpos$mutableblockpos.setPos(blockpos).move(blockmodelrenderer$neighborinfo.corners[0]).move(dir);
         boolean flag = reader.getBlockState(blockpos$mutableblockpos).getOpacity(reader, blockpos$mutableblockpos) == 0;
         blockpos$mutableblockpos.setPos(blockpos).move(blockmodelrenderer$neighborinfo.corners[1]).move(dir);
         boolean flag1 = reader.getBlockState(blockpos$mutableblockpos).getOpacity(reader, blockpos$mutableblockpos) == 0;
         blockpos$mutableblockpos.setPos(blockpos).move(blockmodelrenderer$neighborinfo.corners[2]).move(dir);
         boolean flag2 = reader.getBlockState(blockpos$mutableblockpos).getOpacity(reader, blockpos$mutableblockpos) == 0;
         blockpos$mutableblockpos.setPos(blockpos).move(blockmodelrenderer$neighborinfo.corners[3]).move(dir);
         boolean flag3 = reader.getBlockState(blockpos$mutableblockpos).getOpacity(reader, blockpos$mutableblockpos) == 0;
         float f4;
         int i1;
         if (!flag2 && !flag) {
            f4 = f;
            i1 = i;
         } else {
            blockpos$mutableblockpos.setPos(blockpos).move(blockmodelrenderer$neighborinfo.corners[0]).move(blockmodelrenderer$neighborinfo.corners[2]);
            BlockState blockstate4 = reader.getBlockState(blockpos$mutableblockpos);
            f4 = blockmodelrenderer$cache.func_222896_b(blockstate4, reader, blockpos$mutableblockpos);
            i1 = blockmodelrenderer$cache.func_222893_a(blockstate4, reader, blockpos$mutableblockpos);
         }

         float f5;
         int j1;
         if (!flag3 && !flag) {
            f5 = f;
            j1 = i;
         } else {
            blockpos$mutableblockpos.setPos(blockpos).move(blockmodelrenderer$neighborinfo.corners[0]).move(blockmodelrenderer$neighborinfo.corners[3]);
            BlockState blockstate6 = reader.getBlockState(blockpos$mutableblockpos);
            f5 = blockmodelrenderer$cache.func_222896_b(blockstate6, reader, blockpos$mutableblockpos);
            j1 = blockmodelrenderer$cache.func_222893_a(blockstate6, reader, blockpos$mutableblockpos);
         }

         float f6;
         int k1;
         if (!flag2 && !flag1) {
            f6 = f;
            k1 = i;
         } else {
            blockpos$mutableblockpos.setPos(blockpos).move(blockmodelrenderer$neighborinfo.corners[1]).move(blockmodelrenderer$neighborinfo.corners[2]);
            BlockState blockstate7 = reader.getBlockState(blockpos$mutableblockpos);
            f6 = blockmodelrenderer$cache.func_222896_b(blockstate7, reader, blockpos$mutableblockpos);
            k1 = blockmodelrenderer$cache.func_222893_a(blockstate7, reader, blockpos$mutableblockpos);
         }

         float f7;
         int l1;
         if (!flag3 && !flag1) {
            f7 = f;
            l1 = i;
         } else {
            blockpos$mutableblockpos.setPos(blockpos).move(blockmodelrenderer$neighborinfo.corners[1]).move(blockmodelrenderer$neighborinfo.corners[3]);
            BlockState blockstate8 = reader.getBlockState(blockpos$mutableblockpos);
            f7 = blockmodelrenderer$cache.func_222896_b(blockstate8, reader, blockpos$mutableblockpos);
            l1 = blockmodelrenderer$cache.func_222893_a(blockstate8, reader, blockpos$mutableblockpos);
         }

         int i3 = blockmodelrenderer$cache.func_222893_a(state, reader, pos);
         blockpos$mutableblockpos.setPos(pos).move(dir);
         BlockState blockstate5 = reader.getBlockState(blockpos$mutableblockpos);
         if (bits.get(0) || !blockstate5.isOpaqueCube(reader, blockpos$mutableblockpos)) {
            i3 = blockmodelrenderer$cache.func_222893_a(blockstate5, reader, blockpos$mutableblockpos);
         }

         float f8 = bits.get(0) ? blockmodelrenderer$cache.func_222896_b(reader.getBlockState(blockpos), reader, blockpos) : blockmodelrenderer$cache.func_222896_b(reader.getBlockState(pos), reader, pos);
         BlockModelRenderer.VertexTranslations blockmodelrenderer$vertextranslations = BlockModelRenderer.VertexTranslations.getVertexTranslations(dir);
         if (bits.get(1) && blockmodelrenderer$neighborinfo.doNonCubicWeight) {
            float f29 = (f3 + f + f5 + f8) * 0.25F;
            float f30 = (f2 + f + f4 + f8) * 0.25F;
            float f31 = (f2 + f1 + f6 + f8) * 0.25F;
            float f32 = (f3 + f1 + f7 + f8) * 0.25F;
            float f13 = p_217629_5_[blockmodelrenderer$neighborinfo.vert0Weights[0].shape] * p_217629_5_[blockmodelrenderer$neighborinfo.vert0Weights[1].shape];
            float f14 = p_217629_5_[blockmodelrenderer$neighborinfo.vert0Weights[2].shape] * p_217629_5_[blockmodelrenderer$neighborinfo.vert0Weights[3].shape];
            float f15 = p_217629_5_[blockmodelrenderer$neighborinfo.vert0Weights[4].shape] * p_217629_5_[blockmodelrenderer$neighborinfo.vert0Weights[5].shape];
            float f16 = p_217629_5_[blockmodelrenderer$neighborinfo.vert0Weights[6].shape] * p_217629_5_[blockmodelrenderer$neighborinfo.vert0Weights[7].shape];
            float f17 = p_217629_5_[blockmodelrenderer$neighborinfo.vert1Weights[0].shape] * p_217629_5_[blockmodelrenderer$neighborinfo.vert1Weights[1].shape];
            float f18 = p_217629_5_[blockmodelrenderer$neighborinfo.vert1Weights[2].shape] * p_217629_5_[blockmodelrenderer$neighborinfo.vert1Weights[3].shape];
            float f19 = p_217629_5_[blockmodelrenderer$neighborinfo.vert1Weights[4].shape] * p_217629_5_[blockmodelrenderer$neighborinfo.vert1Weights[5].shape];
            float f20 = p_217629_5_[blockmodelrenderer$neighborinfo.vert1Weights[6].shape] * p_217629_5_[blockmodelrenderer$neighborinfo.vert1Weights[7].shape];
            float f21 = p_217629_5_[blockmodelrenderer$neighborinfo.vert2Weights[0].shape] * p_217629_5_[blockmodelrenderer$neighborinfo.vert2Weights[1].shape];
            float f22 = p_217629_5_[blockmodelrenderer$neighborinfo.vert2Weights[2].shape] * p_217629_5_[blockmodelrenderer$neighborinfo.vert2Weights[3].shape];
            float f23 = p_217629_5_[blockmodelrenderer$neighborinfo.vert2Weights[4].shape] * p_217629_5_[blockmodelrenderer$neighborinfo.vert2Weights[5].shape];
            float f24 = p_217629_5_[blockmodelrenderer$neighborinfo.vert2Weights[6].shape] * p_217629_5_[blockmodelrenderer$neighborinfo.vert2Weights[7].shape];
            float f25 = p_217629_5_[blockmodelrenderer$neighborinfo.vert3Weights[0].shape] * p_217629_5_[blockmodelrenderer$neighborinfo.vert3Weights[1].shape];
            float f26 = p_217629_5_[blockmodelrenderer$neighborinfo.vert3Weights[2].shape] * p_217629_5_[blockmodelrenderer$neighborinfo.vert3Weights[3].shape];
            float f27 = p_217629_5_[blockmodelrenderer$neighborinfo.vert3Weights[4].shape] * p_217629_5_[blockmodelrenderer$neighborinfo.vert3Weights[5].shape];
            float f28 = p_217629_5_[blockmodelrenderer$neighborinfo.vert3Weights[6].shape] * p_217629_5_[blockmodelrenderer$neighborinfo.vert3Weights[7].shape];
            this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert0] = f29 * f13 + f30 * f14 + f31 * f15 + f32 * f16;
            this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert1] = f29 * f17 + f30 * f18 + f31 * f19 + f32 * f20;
            this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert2] = f29 * f21 + f30 * f22 + f31 * f23 + f32 * f24;
            this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert3] = f29 * f25 + f30 * f26 + f31 * f27 + f32 * f28;
            int i2 = this.getAoBrightness(l, i, j1, i3);
            int j2 = this.getAoBrightness(k, i, i1, i3);
            int k2 = this.getAoBrightness(k, j, k1, i3);
            int l2 = this.getAoBrightness(l, j, l1, i3);
            this.vertexBrightness[blockmodelrenderer$vertextranslations.vert0] = this.getVertexBrightness(i2, j2, k2, l2, f13, f14, f15, f16);
            this.vertexBrightness[blockmodelrenderer$vertextranslations.vert1] = this.getVertexBrightness(i2, j2, k2, l2, f17, f18, f19, f20);
            this.vertexBrightness[blockmodelrenderer$vertextranslations.vert2] = this.getVertexBrightness(i2, j2, k2, l2, f21, f22, f23, f24);
            this.vertexBrightness[blockmodelrenderer$vertextranslations.vert3] = this.getVertexBrightness(i2, j2, k2, l2, f25, f26, f27, f28);
         } else {
            float f9 = (f3 + f + f5 + f8) * 0.25F;
            float f10 = (f2 + f + f4 + f8) * 0.25F;
            float f11 = (f2 + f1 + f6 + f8) * 0.25F;
            float f12 = (f3 + f1 + f7 + f8) * 0.25F;
            this.vertexBrightness[blockmodelrenderer$vertextranslations.vert0] = this.getAoBrightness(l, i, j1, i3);
            this.vertexBrightness[blockmodelrenderer$vertextranslations.vert1] = this.getAoBrightness(k, i, i1, i3);
            this.vertexBrightness[blockmodelrenderer$vertextranslations.vert2] = this.getAoBrightness(k, j, k1, i3);
            this.vertexBrightness[blockmodelrenderer$vertextranslations.vert3] = this.getAoBrightness(l, j, l1, i3);
            this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert0] = f9;
            this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert1] = f10;
            this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert2] = f11;
            this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert3] = f12;
         }

      }

      /**
       * Get ambient occlusion brightness
       */
      private int getAoBrightness(int br1, int br2, int br3, int br4) {
         if (br1 == 0) {
            br1 = br4;
         }

         if (br2 == 0) {
            br2 = br4;
         }

         if (br3 == 0) {
            br3 = br4;
         }

         return br1 + br2 + br3 + br4 >> 2 & 16711935;
      }

      private int getVertexBrightness(int b1, int b2, int b3, int b4, float w1, float w2, float w3, float w4) {
         int i = (int)((float)(b1 >> 16 & 255) * w1 + (float)(b2 >> 16 & 255) * w2 + (float)(b3 >> 16 & 255) * w3 + (float)(b4 >> 16 & 255) * w4) & 255;
         int j = (int)((float)(b1 & 255) * w1 + (float)(b2 & 255) * w2 + (float)(b3 & 255) * w3 + (float)(b4 & 255) * w4) & 255;
         return i << 16 | j;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class Cache {
      private boolean field_222898_a;
      private final Object2IntLinkedOpenHashMap<BlockPos> field_222899_b = Util.make(() -> {
         Object2IntLinkedOpenHashMap<BlockPos> object2intlinkedopenhashmap = new Object2IntLinkedOpenHashMap<BlockPos>(100, 0.25F) {
            protected void rehash(int p_rehash_1_) {
            }
         };
         object2intlinkedopenhashmap.defaultReturnValue(Integer.MAX_VALUE);
         return object2intlinkedopenhashmap;
      });
      private final Object2FloatLinkedOpenHashMap<BlockPos> field_222900_c = Util.make(() -> {
         Object2FloatLinkedOpenHashMap<BlockPos> object2floatlinkedopenhashmap = new Object2FloatLinkedOpenHashMap<BlockPos>(100, 0.25F) {
            protected void rehash(int p_rehash_1_) {
            }
         };
         object2floatlinkedopenhashmap.defaultReturnValue(Float.NaN);
         return object2floatlinkedopenhashmap;
      });

      private Cache() {
      }

      public void func_222895_a() {
         this.field_222898_a = true;
      }

      public void func_222897_b() {
         this.field_222898_a = false;
         this.field_222899_b.clear();
         this.field_222900_c.clear();
      }

      public int func_222893_a(BlockState p_222893_1_, IEnviromentBlockReader p_222893_2_, BlockPos p_222893_3_) {
         if (this.field_222898_a) {
            int i = this.field_222899_b.getInt(p_222893_3_);
            if (i != Integer.MAX_VALUE) {
               return i;
            }
         }

         int j = p_222893_1_.getPackedLightmapCoords(p_222893_2_, p_222893_3_);
         if (this.field_222898_a) {
            if (this.field_222899_b.size() == 100) {
               this.field_222899_b.removeFirstInt();
            }

            this.field_222899_b.put(p_222893_3_.toImmutable(), j);
         }

         return j;
      }

      public float func_222896_b(BlockState p_222896_1_, IEnviromentBlockReader p_222896_2_, BlockPos p_222896_3_) {
         if (this.field_222898_a) {
            float f = this.field_222900_c.getFloat(p_222896_3_);
            if (!Float.isNaN(f)) {
               return f;
            }
         }

         float f1 = p_222896_1_.func_215703_d(p_222896_2_, p_222896_3_);
         if (this.field_222898_a) {
            if (this.field_222900_c.size() == 100) {
               this.field_222900_c.removeFirstFloat();
            }

            this.field_222900_c.put(p_222896_3_.toImmutable(), f1);
         }

         return f1;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum NeighborInfo {
      DOWN(new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH}, 0.5F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.SOUTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.SOUTH}),
      UP(new Direction[]{Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH}, 1.0F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.SOUTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.SOUTH}),
      NORTH(new Direction[]{Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST}, 0.8F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_WEST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_EAST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_EAST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_WEST}),
      SOUTH(new Direction[]{Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP}, 0.8F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.WEST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.WEST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.EAST}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.EAST}),
      WEST(new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH}, 0.6F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.SOUTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.SOUTH}),
      EAST(new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH}, 0.6F, true, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.SOUTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.DOWN, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.NORTH}, new BlockModelRenderer.Orientation[]{BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.UP, BlockModelRenderer.Orientation.SOUTH});

      private final Direction[] corners;
      private final boolean doNonCubicWeight;
      private final BlockModelRenderer.Orientation[] vert0Weights;
      private final BlockModelRenderer.Orientation[] vert1Weights;
      private final BlockModelRenderer.Orientation[] vert2Weights;
      private final BlockModelRenderer.Orientation[] vert3Weights;
      private static final BlockModelRenderer.NeighborInfo[] VALUES = Util.make(new BlockModelRenderer.NeighborInfo[6], (p_209260_0_) -> {
         p_209260_0_[Direction.DOWN.getIndex()] = DOWN;
         p_209260_0_[Direction.UP.getIndex()] = UP;
         p_209260_0_[Direction.NORTH.getIndex()] = NORTH;
         p_209260_0_[Direction.SOUTH.getIndex()] = SOUTH;
         p_209260_0_[Direction.WEST.getIndex()] = WEST;
         p_209260_0_[Direction.EAST.getIndex()] = EAST;
      });

      private NeighborInfo(Direction[] cornersIn, float brightness, boolean doNonCubicWeightIn, BlockModelRenderer.Orientation[] vert0WeightsIn, BlockModelRenderer.Orientation[] vert1WeightsIn, BlockModelRenderer.Orientation[] vert2WeightsIn, BlockModelRenderer.Orientation[] vert3WeightsIn) {
         this.corners = cornersIn;
         this.doNonCubicWeight = doNonCubicWeightIn;
         this.vert0Weights = vert0WeightsIn;
         this.vert1Weights = vert1WeightsIn;
         this.vert2Weights = vert2WeightsIn;
         this.vert3Weights = vert3WeightsIn;
      }

      public static BlockModelRenderer.NeighborInfo getNeighbourInfo(Direction facing) {
         return VALUES[facing.getIndex()];
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Orientation {
      DOWN(Direction.DOWN, false),
      UP(Direction.UP, false),
      NORTH(Direction.NORTH, false),
      SOUTH(Direction.SOUTH, false),
      WEST(Direction.WEST, false),
      EAST(Direction.EAST, false),
      FLIP_DOWN(Direction.DOWN, true),
      FLIP_UP(Direction.UP, true),
      FLIP_NORTH(Direction.NORTH, true),
      FLIP_SOUTH(Direction.SOUTH, true),
      FLIP_WEST(Direction.WEST, true),
      FLIP_EAST(Direction.EAST, true);

      private final int shape;

      private Orientation(Direction facingIn, boolean flip) {
         this.shape = facingIn.getIndex() + (flip ? Direction.values().length : 0);
      }
   }

   @OnlyIn(Dist.CLIENT)
   static enum VertexTranslations {
      DOWN(0, 1, 2, 3),
      UP(2, 3, 0, 1),
      NORTH(3, 0, 1, 2),
      SOUTH(0, 1, 2, 3),
      WEST(3, 0, 1, 2),
      EAST(1, 2, 3, 0);

      private final int vert0;
      private final int vert1;
      private final int vert2;
      private final int vert3;
      private static final BlockModelRenderer.VertexTranslations[] VALUES = Util.make(new BlockModelRenderer.VertexTranslations[6], (p_209261_0_) -> {
         p_209261_0_[Direction.DOWN.getIndex()] = DOWN;
         p_209261_0_[Direction.UP.getIndex()] = UP;
         p_209261_0_[Direction.NORTH.getIndex()] = NORTH;
         p_209261_0_[Direction.SOUTH.getIndex()] = SOUTH;
         p_209261_0_[Direction.WEST.getIndex()] = WEST;
         p_209261_0_[Direction.EAST.getIndex()] = EAST;
      });

      private VertexTranslations(int vert0In, int vert1In, int vert2In, int vert3In) {
         this.vert0 = vert0In;
         this.vert1 = vert1In;
         this.vert2 = vert2In;
         this.vert3 = vert3In;
      }

      public static BlockModelRenderer.VertexTranslations getVertexTranslations(Direction facingIn) {
         return VALUES[facingIn.getIndex()];
      }
   }
}