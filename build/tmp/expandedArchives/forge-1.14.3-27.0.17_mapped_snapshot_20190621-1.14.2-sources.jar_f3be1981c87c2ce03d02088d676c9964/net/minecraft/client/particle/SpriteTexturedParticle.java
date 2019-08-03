package net.minecraft.client.particle;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class SpriteTexturedParticle extends TexturedParticle {
   protected TextureAtlasSprite sprite;

   protected SpriteTexturedParticle(World p_i50998_1_, double p_i50998_2_, double p_i50998_4_, double p_i50998_6_) {
      super(p_i50998_1_, p_i50998_2_, p_i50998_4_, p_i50998_6_);
   }

   protected SpriteTexturedParticle(World p_i50999_1_, double p_i50999_2_, double p_i50999_4_, double p_i50999_6_, double p_i50999_8_, double p_i50999_10_, double p_i50999_12_) {
      super(p_i50999_1_, p_i50999_2_, p_i50999_4_, p_i50999_6_, p_i50999_8_, p_i50999_10_, p_i50999_12_);
   }

   protected void setSprite(TextureAtlasSprite sprite) {
      this.sprite = sprite;
   }

   protected float func_217563_c() {
      return this.sprite.getMinU();
   }

   protected float func_217564_d() {
      return this.sprite.getMaxU();
   }

   protected float func_217562_e() {
      return this.sprite.getMinV();
   }

   protected float func_217560_f() {
      return this.sprite.getMaxV();
   }

   public void func_217568_a(IAnimatedSprite p_217568_1_) {
      this.setSprite(p_217568_1_.func_217590_a(this.rand));
   }

   public void func_217566_b(IAnimatedSprite p_217566_1_) {
      this.setSprite(p_217566_1_.func_217591_a(this.age, this.maxAge));
   }
}