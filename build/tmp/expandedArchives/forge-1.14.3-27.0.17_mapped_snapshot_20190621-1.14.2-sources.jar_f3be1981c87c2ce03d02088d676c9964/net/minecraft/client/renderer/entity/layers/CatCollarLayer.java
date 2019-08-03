package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.CatModel;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CatCollarLayer extends LayerRenderer<CatEntity, CatModel<CatEntity>> {
   private static final ResourceLocation field_215339_a = new ResourceLocation("textures/entity/cat/cat_collar.png");
   private final CatModel<CatEntity> field_215340_b = new CatModel<>(0.01F);

   public CatCollarLayer(IEntityRenderer<CatEntity, CatModel<CatEntity>> p_i50948_1_) {
      super(p_i50948_1_);
   }

   public void render(CatEntity entityIn, float p_212842_2_, float p_212842_3_, float p_212842_4_, float p_212842_5_, float p_212842_6_, float p_212842_7_, float p_212842_8_) {
      if (entityIn.isTamed() && !entityIn.isInvisible()) {
         this.bindTexture(field_215339_a);
         float[] afloat = entityIn.getCollarColor().getColorComponentValues();
         GlStateManager.color3f(afloat[0], afloat[1], afloat[2]);
         this.getEntityModel().setModelAttributes(this.field_215340_b);
         this.field_215340_b.setLivingAnimations(entityIn, p_212842_2_, p_212842_3_, p_212842_4_);
         this.field_215340_b.render(entityIn, p_212842_2_, p_212842_3_, p_212842_5_, p_212842_6_, p_212842_7_, p_212842_8_);
      }
   }

   public boolean shouldCombineTextures() {
      return true;
   }
}