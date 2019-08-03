package net.minecraft.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StainedGlassBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FluidBlockRenderer {
   private final TextureAtlasSprite[] atlasSpritesLava = new TextureAtlasSprite[2];
   private final TextureAtlasSprite[] atlasSpritesWater = new TextureAtlasSprite[2];
   private TextureAtlasSprite atlasSpriteWaterOverlay;

   protected void initAtlasSprites() {
      AtlasTexture atlastexture = Minecraft.getInstance().getTextureMap();
      this.atlasSpritesLava[0] = Minecraft.getInstance().getModelManager().getBlockModelShapes().getModel(Blocks.LAVA.getDefaultState()).getParticleTexture();
      this.atlasSpritesLava[1] = atlastexture.getSprite(ModelBakery.LOCATION_LAVA_FLOW);
      this.atlasSpritesWater[0] = Minecraft.getInstance().getModelManager().getBlockModelShapes().getModel(Blocks.WATER.getDefaultState()).getParticleTexture();
      this.atlasSpritesWater[1] = atlastexture.getSprite(ModelBakery.LOCATION_WATER_FLOW);
      this.atlasSpriteWaterOverlay = atlastexture.getSprite(ModelBakery.LOCATION_WATER_OVERLAY);
   }

   private static boolean isAdjacentFluidSameAs(IBlockReader worldIn, BlockPos pos, Direction side, IFluidState state) {
      BlockPos blockpos = pos.offset(side);
      IFluidState ifluidstate = worldIn.getFluidState(blockpos);
      return ifluidstate.getFluid().isEquivalentTo(state.getFluid());
   }

   private static boolean func_209556_a(IBlockReader reader, BlockPos pos, Direction face, float heightIn) {
      BlockPos blockpos = pos.offset(face);
      BlockState blockstate = reader.getBlockState(blockpos);
      if (blockstate.isSolid()) {
         VoxelShape voxelshape = VoxelShapes.create(0.0D, 0.0D, 0.0D, 1.0D, (double)heightIn, 1.0D);
         VoxelShape voxelshape1 = blockstate.getRenderShape(reader, blockpos);
         return VoxelShapes.isCubeSideCovered(voxelshape, voxelshape1, face);
      } else {
         return false;
      }
   }

   public boolean render(IEnviromentBlockReader reader, BlockPos pos, BufferBuilder bufferBuilderIn, IFluidState fluidStateIn) {
      boolean flag = fluidStateIn.isTagged(FluidTags.LAVA);
      TextureAtlasSprite[] atextureatlassprite = flag ? this.atlasSpritesLava : this.atlasSpritesWater;
      int i = flag ? 16777215 : BiomeColors.getWaterColor(reader, pos);
      float f = (float)(i >> 16 & 255) / 255.0F;
      float f1 = (float)(i >> 8 & 255) / 255.0F;
      float f2 = (float)(i & 255) / 255.0F;
      boolean flag1 = !isAdjacentFluidSameAs(reader, pos, Direction.UP, fluidStateIn);
      boolean flag2 = !isAdjacentFluidSameAs(reader, pos, Direction.DOWN, fluidStateIn) && !func_209556_a(reader, pos, Direction.DOWN, 0.8888889F);
      boolean flag3 = !isAdjacentFluidSameAs(reader, pos, Direction.NORTH, fluidStateIn);
      boolean flag4 = !isAdjacentFluidSameAs(reader, pos, Direction.SOUTH, fluidStateIn);
      boolean flag5 = !isAdjacentFluidSameAs(reader, pos, Direction.WEST, fluidStateIn);
      boolean flag6 = !isAdjacentFluidSameAs(reader, pos, Direction.EAST, fluidStateIn);
      if (!flag1 && !flag2 && !flag6 && !flag5 && !flag3 && !flag4) {
         return false;
      } else {
         boolean flag7 = false;
         float f3 = 0.5F;
         float f4 = 1.0F;
         float f5 = 0.8F;
         float f6 = 0.6F;
         float f7 = this.getFluidHeight(reader, pos, fluidStateIn.getFluid());
         float f8 = this.getFluidHeight(reader, pos.south(), fluidStateIn.getFluid());
         float f9 = this.getFluidHeight(reader, pos.east().south(), fluidStateIn.getFluid());
         float f10 = this.getFluidHeight(reader, pos.east(), fluidStateIn.getFluid());
         double d0 = (double)pos.getX();
         double d1 = (double)pos.getY();
         double d2 = (double)pos.getZ();
         float f11 = 0.001F;
         if (flag1 && !func_209556_a(reader, pos, Direction.UP, Math.min(Math.min(f7, f8), Math.min(f9, f10)))) {
            flag7 = true;
            f7 -= 0.001F;
            f8 -= 0.001F;
            f9 -= 0.001F;
            f10 -= 0.001F;
            Vec3d vec3d = fluidStateIn.getFlow(reader, pos);
            float f12;
            float f13;
            float f14;
            float f15;
            float f16;
            float f17;
            float f18;
            float f19;
            if (vec3d.x == 0.0D && vec3d.z == 0.0D) {
               TextureAtlasSprite textureatlassprite1 = atextureatlassprite[0];
               f12 = textureatlassprite1.getInterpolatedU(0.0D);
               f16 = textureatlassprite1.getInterpolatedV(0.0D);
               f13 = f12;
               f17 = textureatlassprite1.getInterpolatedV(16.0D);
               f14 = textureatlassprite1.getInterpolatedU(16.0D);
               f18 = f17;
               f15 = f14;
               f19 = f16;
            } else {
               TextureAtlasSprite textureatlassprite = atextureatlassprite[1];
               float f20 = (float)MathHelper.atan2(vec3d.z, vec3d.x) - ((float)Math.PI / 2F);
               float f21 = MathHelper.sin(f20) * 0.25F;
               float f22 = MathHelper.cos(f20) * 0.25F;
               float f23 = 8.0F;
               f12 = textureatlassprite.getInterpolatedU((double)(8.0F + (-f22 - f21) * 16.0F));
               f16 = textureatlassprite.getInterpolatedV((double)(8.0F + (-f22 + f21) * 16.0F));
               f13 = textureatlassprite.getInterpolatedU((double)(8.0F + (-f22 + f21) * 16.0F));
               f17 = textureatlassprite.getInterpolatedV((double)(8.0F + (f22 + f21) * 16.0F));
               f14 = textureatlassprite.getInterpolatedU((double)(8.0F + (f22 + f21) * 16.0F));
               f18 = textureatlassprite.getInterpolatedV((double)(8.0F + (f22 - f21) * 16.0F));
               f15 = textureatlassprite.getInterpolatedU((double)(8.0F + (f22 - f21) * 16.0F));
               f19 = textureatlassprite.getInterpolatedV((double)(8.0F + (-f22 - f21) * 16.0F));
            }

            float f39 = (f12 + f13 + f14 + f15) / 4.0F;
            float f41 = (f16 + f17 + f18 + f19) / 4.0F;
            float f42 = (float)atextureatlassprite[0].getWidth() / (atextureatlassprite[0].getMaxU() - atextureatlassprite[0].getMinU());
            float f43 = (float)atextureatlassprite[0].getHeight() / (atextureatlassprite[0].getMaxV() - atextureatlassprite[0].getMinV());
            float f44 = 4.0F / Math.max(f43, f42);
            f12 = MathHelper.lerp(f44, f12, f39);
            f13 = MathHelper.lerp(f44, f13, f39);
            f14 = MathHelper.lerp(f44, f14, f39);
            f15 = MathHelper.lerp(f44, f15, f39);
            f16 = MathHelper.lerp(f44, f16, f41);
            f17 = MathHelper.lerp(f44, f17, f41);
            f18 = MathHelper.lerp(f44, f18, f41);
            f19 = MathHelper.lerp(f44, f19, f41);
            int j = this.getCombinedLightUpMax(reader, pos);
            int k = j >> 16 & '\uffff';
            int l = j & '\uffff';
            float f24 = 1.0F * f;
            float f25 = 1.0F * f1;
            float f26 = 1.0F * f2;
            bufferBuilderIn.pos(d0 + 0.0D, d1 + (double)f7, d2 + 0.0D).color(f24, f25, f26, 1.0F).tex((double)f12, (double)f16).lightmap(k, l).endVertex();
            bufferBuilderIn.pos(d0 + 0.0D, d1 + (double)f8, d2 + 1.0D).color(f24, f25, f26, 1.0F).tex((double)f13, (double)f17).lightmap(k, l).endVertex();
            bufferBuilderIn.pos(d0 + 1.0D, d1 + (double)f9, d2 + 1.0D).color(f24, f25, f26, 1.0F).tex((double)f14, (double)f18).lightmap(k, l).endVertex();
            bufferBuilderIn.pos(d0 + 1.0D, d1 + (double)f10, d2 + 0.0D).color(f24, f25, f26, 1.0F).tex((double)f15, (double)f19).lightmap(k, l).endVertex();
            if (fluidStateIn.shouldRenderSides(reader, pos.up())) {
               bufferBuilderIn.pos(d0 + 0.0D, d1 + (double)f7, d2 + 0.0D).color(f24, f25, f26, 1.0F).tex((double)f12, (double)f16).lightmap(k, l).endVertex();
               bufferBuilderIn.pos(d0 + 1.0D, d1 + (double)f10, d2 + 0.0D).color(f24, f25, f26, 1.0F).tex((double)f15, (double)f19).lightmap(k, l).endVertex();
               bufferBuilderIn.pos(d0 + 1.0D, d1 + (double)f9, d2 + 1.0D).color(f24, f25, f26, 1.0F).tex((double)f14, (double)f18).lightmap(k, l).endVertex();
               bufferBuilderIn.pos(d0 + 0.0D, d1 + (double)f8, d2 + 1.0D).color(f24, f25, f26, 1.0F).tex((double)f13, (double)f17).lightmap(k, l).endVertex();
            }
         }

         if (flag2) {
            float f31 = atextureatlassprite[0].getMinU();
            float f32 = atextureatlassprite[0].getMaxU();
            float f34 = atextureatlassprite[0].getMinV();
            float f36 = atextureatlassprite[0].getMaxV();
            int i2 = this.getCombinedLightUpMax(reader, pos.down());
            int j2 = i2 >> 16 & '\uffff';
            int k2 = i2 & '\uffff';
            float f37 = 0.5F * f;
            float f38 = 0.5F * f1;
            float f40 = 0.5F * f2;
            bufferBuilderIn.pos(d0, d1, d2 + 1.0D).color(f37, f38, f40, 1.0F).tex((double)f31, (double)f36).lightmap(j2, k2).endVertex();
            bufferBuilderIn.pos(d0, d1, d2).color(f37, f38, f40, 1.0F).tex((double)f31, (double)f34).lightmap(j2, k2).endVertex();
            bufferBuilderIn.pos(d0 + 1.0D, d1, d2).color(f37, f38, f40, 1.0F).tex((double)f32, (double)f34).lightmap(j2, k2).endVertex();
            bufferBuilderIn.pos(d0 + 1.0D, d1, d2 + 1.0D).color(f37, f38, f40, 1.0F).tex((double)f32, (double)f36).lightmap(j2, k2).endVertex();
            flag7 = true;
         }

         for(int l1 = 0; l1 < 4; ++l1) {
            float f33;
            float f35;
            double d3;
            double d4;
            double d5;
            double d6;
            Direction direction;
            boolean flag8;
            if (l1 == 0) {
               f33 = f7;
               f35 = f10;
               d3 = d0;
               d5 = d0 + 1.0D;
               d4 = d2 + (double)0.001F;
               d6 = d2 + (double)0.001F;
               direction = Direction.NORTH;
               flag8 = flag3;
            } else if (l1 == 1) {
               f33 = f9;
               f35 = f8;
               d3 = d0 + 1.0D;
               d5 = d0;
               d4 = d2 + 1.0D - (double)0.001F;
               d6 = d2 + 1.0D - (double)0.001F;
               direction = Direction.SOUTH;
               flag8 = flag4;
            } else if (l1 == 2) {
               f33 = f8;
               f35 = f7;
               d3 = d0 + (double)0.001F;
               d5 = d0 + (double)0.001F;
               d4 = d2 + 1.0D;
               d6 = d2;
               direction = Direction.WEST;
               flag8 = flag5;
            } else {
               f33 = f10;
               f35 = f9;
               d3 = d0 + 1.0D - (double)0.001F;
               d5 = d0 + 1.0D - (double)0.001F;
               d4 = d2;
               d6 = d2 + 1.0D;
               direction = Direction.EAST;
               flag8 = flag6;
            }

            if (flag8 && !func_209556_a(reader, pos, direction, Math.max(f33, f35))) {
               flag7 = true;
               BlockPos blockpos = pos.offset(direction);
               TextureAtlasSprite textureatlassprite2 = atextureatlassprite[1];
               if (!flag) {
                  Block block = reader.getBlockState(blockpos).getBlock();
                  if (block == Blocks.GLASS || block instanceof StainedGlassBlock) {
                     textureatlassprite2 = this.atlasSpriteWaterOverlay;
                  }
               }

               float f45 = textureatlassprite2.getInterpolatedU(0.0D);
               float f46 = textureatlassprite2.getInterpolatedU(8.0D);
               float f47 = textureatlassprite2.getInterpolatedV((double)((1.0F - f33) * 16.0F * 0.5F));
               float f48 = textureatlassprite2.getInterpolatedV((double)((1.0F - f35) * 16.0F * 0.5F));
               float f49 = textureatlassprite2.getInterpolatedV(8.0D);
               int i1 = this.getCombinedLightUpMax(reader, blockpos);
               int j1 = i1 >> 16 & '\uffff';
               int k1 = i1 & '\uffff';
               float f27 = l1 < 2 ? 0.8F : 0.6F;
               float f28 = 1.0F * f27 * f;
               float f29 = 1.0F * f27 * f1;
               float f30 = 1.0F * f27 * f2;
               bufferBuilderIn.pos(d3, d1 + (double)f33, d4).color(f28, f29, f30, 1.0F).tex((double)f45, (double)f47).lightmap(j1, k1).endVertex();
               bufferBuilderIn.pos(d5, d1 + (double)f35, d6).color(f28, f29, f30, 1.0F).tex((double)f46, (double)f48).lightmap(j1, k1).endVertex();
               bufferBuilderIn.pos(d5, d1 + 0.0D, d6).color(f28, f29, f30, 1.0F).tex((double)f46, (double)f49).lightmap(j1, k1).endVertex();
               bufferBuilderIn.pos(d3, d1 + 0.0D, d4).color(f28, f29, f30, 1.0F).tex((double)f45, (double)f49).lightmap(j1, k1).endVertex();
               if (textureatlassprite2 != this.atlasSpriteWaterOverlay) {
                  bufferBuilderIn.pos(d3, d1 + 0.0D, d4).color(f28, f29, f30, 1.0F).tex((double)f45, (double)f49).lightmap(j1, k1).endVertex();
                  bufferBuilderIn.pos(d5, d1 + 0.0D, d6).color(f28, f29, f30, 1.0F).tex((double)f46, (double)f49).lightmap(j1, k1).endVertex();
                  bufferBuilderIn.pos(d5, d1 + (double)f35, d6).color(f28, f29, f30, 1.0F).tex((double)f46, (double)f48).lightmap(j1, k1).endVertex();
                  bufferBuilderIn.pos(d3, d1 + (double)f33, d4).color(f28, f29, f30, 1.0F).tex((double)f45, (double)f47).lightmap(j1, k1).endVertex();
               }
            }
         }

         return flag7;
      }
   }

   private int getCombinedLightUpMax(IEnviromentBlockReader reader, BlockPos pos) {
      int i = reader.getCombinedLight(pos, 0);
      int j = reader.getCombinedLight(pos.up(), 0);
      int k = i & 255;
      int l = j & 255;
      int i1 = i >> 16 & 255;
      int j1 = j >> 16 & 255;
      return (k > l ? k : l) | (i1 > j1 ? i1 : j1) << 16;
   }

   private float getFluidHeight(IBlockReader reader, BlockPos pos, Fluid fluidIn) {
      int i = 0;
      float f = 0.0F;

      for(int j = 0; j < 4; ++j) {
         BlockPos blockpos = pos.add(-(j & 1), 0, -(j >> 1 & 1));
         if (reader.getFluidState(blockpos.up()).getFluid().isEquivalentTo(fluidIn)) {
            return 1.0F;
         }

         IFluidState ifluidstate = reader.getFluidState(blockpos);
         if (ifluidstate.getFluid().isEquivalentTo(fluidIn)) {
            float f1 = ifluidstate.func_215679_a(reader, blockpos);
            if (f1 >= 0.8F) {
               f += f1 * 10.0F;
               i += 10;
            } else {
               f += f1;
               ++i;
            }
         } else if (!reader.getBlockState(blockpos).getMaterial().isSolid()) {
            ++i;
         }
      }

      return f / (float)i;
   }
}