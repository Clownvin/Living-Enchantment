package net.minecraft.client.renderer.model;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class BlockModel implements IUnbakedModel {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final FaceBakery field_217647_g = new FaceBakery();
   @VisibleForTesting
   static final Gson SERIALIZER = (new GsonBuilder()).registerTypeAdapter(BlockModel.class, new BlockModel.Deserializer()).registerTypeAdapter(BlockPart.class, new BlockPart.Deserializer()).registerTypeAdapter(BlockPartFace.class, new BlockPartFace.Deserializer()).registerTypeAdapter(BlockFaceUV.class, new BlockFaceUV.Deserializer()).registerTypeAdapter(ItemTransformVec3f.class, new ItemTransformVec3f.Deserializer()).registerTypeAdapter(ItemCameraTransforms.class, new ItemCameraTransforms.Deserializer()).registerTypeAdapter(ItemOverride.class, new ItemOverride.Deserializer()).create();
   private final List<BlockPart> elements;
   private final boolean gui3d;
   public final boolean ambientOcclusion;
   private final ItemCameraTransforms cameraTransforms;
   private final List<ItemOverride> overrides;
   public String name = "";
   @VisibleForTesting
   public final Map<String, String> textures;
   @Nullable
   public BlockModel parent;
   @Nullable
   protected ResourceLocation parentLocation;

   public static BlockModel deserialize(Reader readerIn) {
      return JSONUtils.fromJson(SERIALIZER, readerIn, BlockModel.class);
   }

   public static BlockModel deserialize(String jsonString) {
      return deserialize(new StringReader(jsonString));
   }

   public BlockModel(@Nullable ResourceLocation parentLocationIn, List<BlockPart> elementsIn, Map<String, String> texturesIn, boolean ambientOcclusionIn, boolean gui3dIn, ItemCameraTransforms cameraTransformsIn, List<ItemOverride> overridesIn) {
      this.elements = elementsIn;
      this.ambientOcclusion = ambientOcclusionIn;
      this.gui3d = gui3dIn;
      this.textures = texturesIn;
      this.parentLocation = parentLocationIn;
      this.cameraTransforms = cameraTransformsIn;
      this.overrides = overridesIn;
   }

   public List<BlockPart> getElements() {
      return this.elements.isEmpty() && this.parent != null ? this.parent.getElements() : this.elements;
   }

   @Nullable
   public ResourceLocation getParentLocation() { return parentLocation; }

   public boolean isAmbientOcclusion() {
      return this.parent != null ? this.parent.isAmbientOcclusion() : this.ambientOcclusion;
   }

   public boolean isGui3d() {
      return this.gui3d;
   }

   public List<ItemOverride> getOverrides() {
      return this.overrides;
   }

   private ItemOverrideList func_217646_a(ModelBakery p_217646_1_, BlockModel p_217646_2_) {
      return this.overrides.isEmpty() ? ItemOverrideList.EMPTY : new ItemOverrideList(p_217646_1_, p_217646_2_, p_217646_1_::getUnbakedModel, this.overrides);
   }
   
   public ItemOverrideList getOverrides(ModelBakery p_217646_1_, BlockModel p_217646_2_, Function<ResourceLocation, TextureAtlasSprite> textureGetter, net.minecraft.client.renderer.vertex.VertexFormat format) {
      return this.overrides.isEmpty() ? ItemOverrideList.EMPTY : new ItemOverrideList(p_217646_1_, p_217646_2_, p_217646_1_::getUnbakedModel, textureGetter, this.overrides, format);
   }

   public Collection<ResourceLocation> getDependencies() {
      Set<ResourceLocation> set = Sets.newHashSet();

      for(ItemOverride itemoverride : this.overrides) {
         set.add(itemoverride.getLocation());
      }

      if (this.parentLocation != null) {
         set.add(this.parentLocation);
      }

      return set;
   }

   public Collection<ResourceLocation> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors) {
      Set<IUnbakedModel> set = Sets.newLinkedHashSet();

      for(BlockModel blockmodel = this; blockmodel.parentLocation != null && blockmodel.parent == null; blockmodel = blockmodel.parent) {
         set.add(blockmodel);
         IUnbakedModel iunbakedmodel = modelGetter.apply(blockmodel.parentLocation);
         if (iunbakedmodel == null) {
            LOGGER.warn("No parent '{}' while loading model '{}'", this.parentLocation, blockmodel);
         }

         if (set.contains(iunbakedmodel)) {
            LOGGER.warn("Found 'parent' loop while loading model '{}' in chain: {} -> {}", blockmodel, set.stream().map(Object::toString).collect(Collectors.joining(" -> ")), this.parentLocation);
            iunbakedmodel = null;
         }

         if (iunbakedmodel == null) {
            blockmodel.parentLocation = ModelBakery.MODEL_MISSING;
            iunbakedmodel = modelGetter.apply(blockmodel.parentLocation);
         }

         if (!(iunbakedmodel instanceof BlockModel)) {
            throw new IllegalStateException("BlockModel parent has to be a block model.");
         }

         blockmodel.parent = (BlockModel)iunbakedmodel;
      }

      Set<ResourceLocation> set1 = Sets.newHashSet(new ResourceLocation(this.resolveTextureName("particle")));

      for(BlockPart blockpart : this.getElements()) {
         for(BlockPartFace blockpartface : blockpart.mapFaces.values()) {
            String s = this.resolveTextureName(blockpartface.texture);
            if (Objects.equals(s, MissingTextureSprite.getLocation().toString())) {
               missingTextureErrors.add(String.format("%s in %s", blockpartface.texture, this.name));
            }

            set1.add(new ResourceLocation(s));
         }
      }

      this.overrides.forEach((p_217643_4_) -> {
         IUnbakedModel iunbakedmodel1 = modelGetter.apply(p_217643_4_.getLocation());
         if (!Objects.equals(iunbakedmodel1, this)) {
            set1.addAll(iunbakedmodel1.getTextures(modelGetter, missingTextureErrors));
         }
      });
      if (this.getRootModel() == ModelBakery.MODEL_GENERATED) {
         ItemModelGenerator.LAYERS.forEach((p_217642_2_) -> {
            set1.add(new ResourceLocation(this.resolveTextureName(p_217642_2_)));
         });
      }

      return set1;
   }

   // FORGE: Override our interface method, binary compatible
   public IBakedModel bake(ModelBakery bakery, Function<ResourceLocation, TextureAtlasSprite> spriteGetter, ISprite sprite, net.minecraft.client.renderer.vertex.VertexFormat format) {
       if (!net.minecraftforge.client.model.Attributes.moreSpecific(format, net.minecraftforge.client.model.Attributes.DEFAULT_BAKED_FORMAT))
           throw new IllegalArgumentException("Cannot bake vanilla model to format other than BLOCK");
      return this.bake(bakery, this, spriteGetter, sprite, format);
   }

   @Deprecated
   public IBakedModel func_217644_a(ModelBakery p_217644_1_, BlockModel p_217644_2_, Function<ResourceLocation, TextureAtlasSprite> p_217644_3_, ISprite p_217644_4_) {
       return bake(p_217644_1_, p_217644_2_, p_217644_3_, p_217644_4_, net.minecraft.client.renderer.vertex.DefaultVertexFormats.ITEM);
   }
   
   public IBakedModel bake(ModelBakery p_217644_1_, BlockModel p_217644_2_, Function<ResourceLocation, TextureAtlasSprite> p_217644_3_, ISprite p_217644_4_, net.minecraft.client.renderer.vertex.VertexFormat format) {
      TextureAtlasSprite textureatlassprite = p_217644_3_.apply(new ResourceLocation(this.resolveTextureName("particle")));
      if (this.getRootModel() == ModelBakery.MODEL_ENTITY) {
         return new BuiltInModel(this.getAllTransforms(), this.getOverrides(p_217644_1_, p_217644_2_, p_217644_3_, format), textureatlassprite);
      } else {
         SimpleBakedModel.Builder simplebakedmodel$builder = (new SimpleBakedModel.Builder(this, this.getOverrides(p_217644_1_, p_217644_2_, p_217644_3_, format))).setTexture(textureatlassprite);

         for(BlockPart blockpart : this.getElements()) {
            for(Direction direction : blockpart.mapFaces.keySet()) {
               BlockPartFace blockpartface = blockpart.mapFaces.get(direction);
               TextureAtlasSprite textureatlassprite1 = p_217644_3_.apply(new ResourceLocation(this.resolveTextureName(blockpartface.texture)));
               if (blockpartface.cullFace == null) {
                  simplebakedmodel$builder.addGeneralQuad(makeBakedQuad(blockpart, blockpartface, textureatlassprite1, direction, p_217644_4_));
               } else {
                  simplebakedmodel$builder.addFaceQuad(p_217644_4_.getState().apply(java.util.Optional.empty()).map(trsr -> trsr.rotate(blockpartface.cullFace)).orElse(blockpartface.cullFace), makeBakedQuad(blockpart, blockpartface, textureatlassprite1, direction, p_217644_4_));
               }
            }
         }

         return simplebakedmodel$builder.build();
      }
   }

   private static BakedQuad func_217645_a(BlockPart p_217645_0_, BlockPartFace p_217645_1_, TextureAtlasSprite p_217645_2_, Direction p_217645_3_, ISprite p_217645_4_) {
      return field_217647_g.func_217648_a(p_217645_0_.positionFrom, p_217645_0_.positionTo, p_217645_1_, p_217645_2_, p_217645_3_, p_217645_4_, p_217645_0_.partRotation, p_217645_0_.shade);
   }

   public static BakedQuad makeBakedQuad(BlockPart p_209567_0, BlockPartFace partFace, TextureAtlasSprite sprite, Direction face, ISprite isprite) {
      return field_217647_g.makeBakedQuad(p_209567_0.positionFrom, p_209567_0.positionTo, partFace, sprite, face, new net.minecraftforge.client.model.BasicState(isprite.getState().apply(java.util.Optional.empty()).orElse(net.minecraftforge.common.model.TRSRTransformation.identity()), isprite.isUvLock()), p_209567_0.partRotation, p_209567_0.shade);
   }

   public boolean isTexturePresent(String textureName) {
      return !MissingTextureSprite.getLocation().toString().equals(this.resolveTextureName(textureName));
   }

   public String resolveTextureName(String textureName) {
      if (!this.startsWithHash(textureName)) {
         textureName = '#' + textureName;
      }

      return this.resolveTextureName(textureName, new BlockModel.Bookkeep(this));
   }

   private String resolveTextureName(String textureName, BlockModel.Bookkeep p_178302_2_) {
      if (this.startsWithHash(textureName)) {
         if (this == p_178302_2_.modelExt) {
            LOGGER.warn("Unable to resolve texture due to upward reference: {} in {}", textureName, this.name);
            return MissingTextureSprite.getLocation().toString();
         } else {
            String s = this.textures.get(textureName.substring(1));
            if (s == null && this.parent != null) {
               s = this.parent.resolveTextureName(textureName, p_178302_2_);
            }

            p_178302_2_.modelExt = this;
            if (s != null && this.startsWithHash(s)) {
               s = p_178302_2_.model.resolveTextureName(s, p_178302_2_);
            }

            return s != null && !this.startsWithHash(s) ? s : MissingTextureSprite.getLocation().toString();
         }
      } else {
         return textureName;
      }
   }

   private boolean startsWithHash(String hash) {
      return hash.charAt(0) == '#';
   }

   public BlockModel getRootModel() {
      return this.parent == null ? this : this.parent.getRootModel();
   }

   public ItemCameraTransforms getAllTransforms() {
      ItemTransformVec3f itemtransformvec3f = this.getTransform(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND);
      ItemTransformVec3f itemtransformvec3f1 = this.getTransform(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
      ItemTransformVec3f itemtransformvec3f2 = this.getTransform(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND);
      ItemTransformVec3f itemtransformvec3f3 = this.getTransform(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND);
      ItemTransformVec3f itemtransformvec3f4 = this.getTransform(ItemCameraTransforms.TransformType.HEAD);
      ItemTransformVec3f itemtransformvec3f5 = this.getTransform(ItemCameraTransforms.TransformType.GUI);
      ItemTransformVec3f itemtransformvec3f6 = this.getTransform(ItemCameraTransforms.TransformType.GROUND);
      ItemTransformVec3f itemtransformvec3f7 = this.getTransform(ItemCameraTransforms.TransformType.FIXED);
      return new ItemCameraTransforms(itemtransformvec3f, itemtransformvec3f1, itemtransformvec3f2, itemtransformvec3f3, itemtransformvec3f4, itemtransformvec3f5, itemtransformvec3f6, itemtransformvec3f7);
   }

   private ItemTransformVec3f getTransform(ItemCameraTransforms.TransformType type) {
      return this.parent != null && !this.cameraTransforms.hasCustomTransform(type) ? this.parent.getTransform(type) : this.cameraTransforms.getTransform(type);
   }

   public String toString() {
      return this.name;
   }

   @OnlyIn(Dist.CLIENT)
   static final class Bookkeep {
      public final BlockModel model;
      public BlockModel modelExt;

      private Bookkeep(BlockModel modelIn) {
         this.model = modelIn;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<BlockModel> {
      public BlockModel deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
         List<BlockPart> list = this.getModelElements(p_deserialize_3_, jsonobject);
         String s = this.getParent(jsonobject);
         Map<String, String> map = this.getTextures(jsonobject);
         boolean flag = this.getAmbientOcclusionEnabled(jsonobject);
         ItemCameraTransforms itemcameratransforms = ItemCameraTransforms.DEFAULT;
         if (jsonobject.has("display")) {
            JsonObject jsonobject1 = JSONUtils.getJsonObject(jsonobject, "display");
            itemcameratransforms = p_deserialize_3_.deserialize(jsonobject1, ItemCameraTransforms.class);
         }

         List<ItemOverride> list1 = this.getItemOverrides(p_deserialize_3_, jsonobject);
         ResourceLocation resourcelocation = s.isEmpty() ? null : new ResourceLocation(s);
         return new BlockModel(resourcelocation, list, map, flag, true, itemcameratransforms, list1);
      }

      protected List<ItemOverride> getItemOverrides(JsonDeserializationContext deserializationContext, JsonObject object) {
         List<ItemOverride> list = Lists.newArrayList();
         if (object.has("overrides")) {
            for(JsonElement jsonelement : JSONUtils.getJsonArray(object, "overrides")) {
               list.add(deserializationContext.deserialize(jsonelement, ItemOverride.class));
            }
         }

         return list;
      }

      private Map<String, String> getTextures(JsonObject object) {
         Map<String, String> map = Maps.newHashMap();
         if (object.has("textures")) {
            JsonObject jsonobject = object.getAsJsonObject("textures");

            for(Entry<String, JsonElement> entry : jsonobject.entrySet()) {
               map.put(entry.getKey(), entry.getValue().getAsString());
            }
         }

         return map;
      }

      private String getParent(JsonObject object) {
         return JSONUtils.getString(object, "parent", "");
      }

      protected boolean getAmbientOcclusionEnabled(JsonObject object) {
         return JSONUtils.getBoolean(object, "ambientocclusion", true);
      }

      protected List<BlockPart> getModelElements(JsonDeserializationContext deserializationContext, JsonObject object) {
         List<BlockPart> list = Lists.newArrayList();
         if (object.has("elements")) {
            for(JsonElement jsonelement : JSONUtils.getJsonArray(object, "elements")) {
               list.add(deserializationContext.deserialize(jsonelement, BlockPart.class));
            }
         }

         return list;
      }
   }
}