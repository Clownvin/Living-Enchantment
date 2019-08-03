package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.TropicalFishAModel;
import net.minecraft.client.renderer.entity.model.TropicalFishBModel;
import net.minecraft.entity.passive.fish.TropicalFishEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TropicalFishPatternLayer extends LayerRenderer<TropicalFishEntity, EntityModel<TropicalFishEntity>> {
   private final TropicalFishAModel<TropicalFishEntity> modelA = new TropicalFishAModel<>(0.008F);
   private final TropicalFishBModel<TropicalFishEntity> modelB = new TropicalFishBModel<>(0.008F);

   public TropicalFishPatternLayer(IEntityRenderer<TropicalFishEntity, EntityModel<TropicalFishEntity>> p_i50918_1_) {
      super(p_i50918_1_);
   }

   public void render(TropicalFishEntity entityIn, float p_212842_2_, float p_212842_3_, float p_212842_4_, float p_212842_5_, float p_212842_6_, float p_212842_7_, float p_212842_8_) {
      if (!entityIn.isInvisible()) {
         EntityModel<TropicalFishEntity> entitymodel = (EntityModel<TropicalFishEntity>)(entityIn.getSize() == 0 ? this.modelA : this.modelB);
         this.bindTexture(entityIn.getPatternTexture());
         float[] afloat = entityIn.func_204222_dD();
         GlStateManager.color3f(afloat[0], afloat[1], afloat[2]);
         this.getEntityModel().setModelAttributes(entitymodel);
         entitymodel.setLivingAnimations(entityIn, p_212842_2_, p_212842_3_, p_212842_4_);
         entitymodel.render(entityIn, p_212842_2_, p_212842_3_, p_212842_5_, p_212842_6_, p_212842_7_, p_212842_8_);
      }
   }

   public boolean shouldCombineTextures() {
      return true;
   }
}