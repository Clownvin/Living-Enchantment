package net.minecraft.client.renderer.texture;

import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PotionSpriteUploader extends SpriteUploader {
   public PotionSpriteUploader(TextureManager p_i50908_1_) {
      super(p_i50908_1_, AtlasTexture.LOCATION_EFFECTS_TEXTURE, "textures/mob_effect");
   }

   protected Iterable<ResourceLocation> getKnownKeys() {
      return Registry.EFFECTS.keySet();
   }

   public TextureAtlasSprite func_215288_a(Effect p_215288_1_) {
      return this.getSprite(Registry.EFFECTS.getKey(p_215288_1_));
   }
}