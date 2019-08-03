package net.minecraft.client.renderer.model;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IUnbakedModel extends net.minecraftforge.client.model.IModel<IUnbakedModel> {
   Collection<ResourceLocation> getDependencies();

   Collection<ResourceLocation> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors);

   /**
    * @deprecated Use {@link #bake(Function, Function, net.minecraftforge.common.model.IModelState, boolean, net.minecraft.client.renderer.vertex.VertexFormat)}.
    */
   @Nullable
   @Deprecated
   default IBakedModel func_217641_a(ModelBakery p_217641_1_, Function<ResourceLocation, TextureAtlasSprite> p_217641_2_, ISprite p_217641_3_) {
       return bake(p_217641_1_, p_217641_2_, p_217641_3_, net.minecraft.client.renderer.vertex.DefaultVertexFormats.ITEM);
   }
}