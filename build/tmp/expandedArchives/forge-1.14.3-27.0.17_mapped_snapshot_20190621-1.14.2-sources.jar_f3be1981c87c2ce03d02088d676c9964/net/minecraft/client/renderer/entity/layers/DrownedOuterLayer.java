package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.DrownedModel;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DrownedOuterLayer<T extends ZombieEntity> extends LayerRenderer<T, DrownedModel<T>> {
   private static final ResourceLocation field_204721_a = new ResourceLocation("textures/entity/zombie/drowned_outer_layer.png");
   private final DrownedModel<T> field_204723_c = new DrownedModel<>(0.25F, 0.0F, 64, 64);

   public DrownedOuterLayer(IEntityRenderer<T, DrownedModel<T>> p_i50943_1_) {
      super(p_i50943_1_);
   }

   public void render(T entityIn, float p_212842_2_, float p_212842_3_, float p_212842_4_, float p_212842_5_, float p_212842_6_, float p_212842_7_, float p_212842_8_) {
      if (!entityIn.isInvisible()) {
         this.getEntityModel().func_217148_a(this.field_204723_c);
         this.field_204723_c.setLivingAnimations(entityIn, p_212842_2_, p_212842_3_, p_212842_4_);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.bindTexture(field_204721_a);
         this.field_204723_c.render(entityIn, p_212842_2_, p_212842_3_, p_212842_5_, p_212842_6_, p_212842_7_, p_212842_8_);
      }
   }

   public boolean shouldCombineTextures() {
      return true;
   }
}