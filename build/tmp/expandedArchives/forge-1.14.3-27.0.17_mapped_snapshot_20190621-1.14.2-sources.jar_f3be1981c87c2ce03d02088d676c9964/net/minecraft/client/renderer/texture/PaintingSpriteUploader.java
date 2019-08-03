package net.minecraft.client.renderer.texture;

import com.google.common.collect.Iterables;
import java.util.Collections;
import net.minecraft.entity.item.PaintingType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PaintingSpriteUploader extends SpriteUploader {
   private static final ResourceLocation field_215287_a = new ResourceLocation("back");

   public PaintingSpriteUploader(TextureManager textureManagerIn) {
      super(textureManagerIn, AtlasTexture.LOCATION_PAINTINGS_TEXTURE, "textures/painting");
   }

   protected Iterable<ResourceLocation> getKnownKeys() {
      return Iterables.concat(Registry.MOTIVE.keySet(), Collections.singleton(field_215287_a));
   }

   /**
    * Gets the sprite used for a specific painting type.
    *  
    * @param paintingTypeIn The painting type to look up.
    */
   public TextureAtlasSprite getSpriteForPainting(PaintingType paintingTypeIn) {
      return this.getSprite(Registry.MOTIVE.getKey(paintingTypeIn));
   }

   public TextureAtlasSprite func_215286_b() {
      return this.getSprite(field_215287_a);
   }
}