package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpinAttackEffectLayer<T extends LivingEntity> extends LayerRenderer<T, PlayerModel<T>> {
   public static final ResourceLocation field_204836_a = new ResourceLocation("textures/entity/trident_riptide.png");
   private final SpinAttackEffectLayer.Model model = new SpinAttackEffectLayer.Model();

   public SpinAttackEffectLayer(IEntityRenderer<T, PlayerModel<T>> p_i50920_1_) {
      super(p_i50920_1_);
   }

   public void render(T entityIn, float p_212842_2_, float p_212842_3_, float p_212842_4_, float p_212842_5_, float p_212842_6_, float p_212842_7_, float p_212842_8_) {
      if (entityIn.isSpinAttacking()) {
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.bindTexture(field_204836_a);

         for(int i = 0; i < 3; ++i) {
            GlStateManager.pushMatrix();
            GlStateManager.rotatef(p_212842_5_ * (float)(-(45 + i * 5)), 0.0F, 1.0F, 0.0F);
            float f = 0.75F * (float)i;
            GlStateManager.scalef(f, f, f);
            GlStateManager.translatef(0.0F, -0.2F + 0.6F * (float)i, 0.0F);
            this.model.func_217110_a(p_212842_2_, p_212842_3_, p_212842_5_, p_212842_6_, p_212842_7_, p_212842_8_);
            GlStateManager.popMatrix();
         }

      }
   }

   public boolean shouldCombineTextures() {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   static class Model extends net.minecraft.client.renderer.model.Model {
      private final RendererModel field_204834_a;

      public Model() {
         this.textureWidth = 64;
         this.textureHeight = 64;
         this.field_204834_a = new RendererModel(this, 0, 0);
         this.field_204834_a.addBox(-8.0F, -16.0F, -8.0F, 16, 32, 16);
      }

      public void func_217110_a(float p_217110_1_, float p_217110_2_, float p_217110_3_, float p_217110_4_, float p_217110_5_, float p_217110_6_) {
         this.field_204834_a.render(p_217110_6_);
      }
   }
}