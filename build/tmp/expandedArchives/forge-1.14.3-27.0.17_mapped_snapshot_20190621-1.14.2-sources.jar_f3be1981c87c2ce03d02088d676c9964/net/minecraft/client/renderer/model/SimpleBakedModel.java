package net.minecraft.client.renderer.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SimpleBakedModel implements IBakedModel {
   protected final List<BakedQuad> generalQuads;
   protected final Map<Direction, List<BakedQuad>> faceQuads;
   protected final boolean ambientOcclusion;
   protected final boolean gui3d;
   protected final TextureAtlasSprite texture;
   protected final ItemCameraTransforms cameraTransforms;
   protected final ItemOverrideList itemOverrideList;

   public SimpleBakedModel(List<BakedQuad> generalQuadsIn, Map<Direction, List<BakedQuad>> faceQuadsIn, boolean ambientOcclusionIn, boolean gui3dIn, TextureAtlasSprite textureIn, ItemCameraTransforms cameraTransformsIn, ItemOverrideList itemOverrideListIn) {
      this.generalQuads = generalQuadsIn;
      this.faceQuads = faceQuadsIn;
      this.ambientOcclusion = ambientOcclusionIn;
      this.gui3d = gui3dIn;
      this.texture = textureIn;
      this.cameraTransforms = cameraTransformsIn;
      this.itemOverrideList = itemOverrideListIn;
   }

   public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
      return side == null ? this.generalQuads : this.faceQuads.get(side);
   }

   public boolean isAmbientOcclusion() {
      return this.ambientOcclusion;
   }

   public boolean isGui3d() {
      return this.gui3d;
   }

   public boolean isBuiltInRenderer() {
      return false;
   }

   public TextureAtlasSprite getParticleTexture() {
      return this.texture;
   }

   public ItemCameraTransforms getItemCameraTransforms() {
      return this.cameraTransforms;
   }

   public ItemOverrideList getOverrides() {
      return this.itemOverrideList;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Builder {
      private final List<BakedQuad> builderGeneralQuads = Lists.newArrayList();
      private final Map<Direction, List<BakedQuad>> builderFaceQuads = Maps.newEnumMap(Direction.class);
      private final ItemOverrideList builderItemOverrideList;
      private final boolean builderAmbientOcclusion;
      private TextureAtlasSprite builderTexture;
      private final boolean builderGui3d;
      private final ItemCameraTransforms builderCameraTransforms;

      public Builder(BlockModel model, ItemOverrideList overrides) {
         this(model.isAmbientOcclusion(), model.isGui3d(), model.getAllTransforms(), overrides);
      }

      public Builder(BlockState p_i48189_1_, IBakedModel p_i48189_2_, TextureAtlasSprite p_i48189_3_, Random p_i48189_4_, long p_i48189_5_) {
         this(p_i48189_2_.isAmbientOcclusion(p_i48189_1_), p_i48189_2_.isGui3d(), p_i48189_2_.getItemCameraTransforms(), p_i48189_2_.getOverrides());
         this.builderTexture = p_i48189_2_.getParticleTexture();

         for(Direction direction : Direction.values()) {
            p_i48189_4_.setSeed(p_i48189_5_);

            for(BakedQuad bakedquad : p_i48189_2_.getQuads(p_i48189_1_, direction, p_i48189_4_)) {
               this.addFaceQuad(direction, new BakedQuadRetextured(bakedquad, p_i48189_3_));
            }
         }

         p_i48189_4_.setSeed(p_i48189_5_);

         for(BakedQuad bakedquad1 : p_i48189_2_.getQuads(p_i48189_1_, (Direction)null, p_i48189_4_)) {
            this.addGeneralQuad(new BakedQuadRetextured(bakedquad1, p_i48189_3_));
         }

      }

      private Builder(boolean ambientOcclusion, boolean gui3d, ItemCameraTransforms transforms, ItemOverrideList overrides) {
         for(Direction direction : Direction.values()) {
            this.builderFaceQuads.put(direction, Lists.newArrayList());
         }

         this.builderItemOverrideList = overrides;
         this.builderAmbientOcclusion = ambientOcclusion;
         this.builderGui3d = gui3d;
         this.builderCameraTransforms = transforms;
      }

      public SimpleBakedModel.Builder addFaceQuad(Direction facing, BakedQuad quad) {
         this.builderFaceQuads.get(facing).add(quad);
         return this;
      }

      public SimpleBakedModel.Builder addGeneralQuad(BakedQuad quad) {
         this.builderGeneralQuads.add(quad);
         return this;
      }

      public SimpleBakedModel.Builder setTexture(TextureAtlasSprite texture) {
         this.builderTexture = texture;
         return this;
      }

      public IBakedModel build() {
         if (this.builderTexture == null) {
            throw new RuntimeException("Missing particle!");
         } else {
            return new SimpleBakedModel(this.builderGeneralQuads, this.builderFaceQuads, this.builderAmbientOcclusion, this.builderGui3d, this.builderTexture, this.builderCameraTransforms, this.builderItemOverrideList);
         }
      }
   }
}