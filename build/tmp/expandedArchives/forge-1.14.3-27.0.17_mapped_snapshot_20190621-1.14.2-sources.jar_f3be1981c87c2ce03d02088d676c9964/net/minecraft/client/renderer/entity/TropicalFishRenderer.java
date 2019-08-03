package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.entity.layers.TropicalFishPatternLayer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.TropicalFishAModel;
import net.minecraft.client.renderer.entity.model.TropicalFishBModel;
import net.minecraft.entity.passive.fish.TropicalFishEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TropicalFishRenderer extends MobRenderer<TropicalFishEntity, EntityModel<TropicalFishEntity>> {
   private final TropicalFishAModel<TropicalFishEntity> field_204246_a = new TropicalFishAModel<>();
   private final TropicalFishBModel<TropicalFishEntity> field_204247_j = new TropicalFishBModel<>();

   public TropicalFishRenderer(EntityRendererManager p_i48889_1_) {
      super(p_i48889_1_, new TropicalFishAModel<>(), 0.15F);
      this.addLayer(new TropicalFishPatternLayer(this));
   }

   /**
    * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
    */
   @Nullable
   protected ResourceLocation getEntityTexture(TropicalFishEntity entity) {
      return entity.getBodyTexture();
   }

   /**
    * Renders the desired {@code T} type Entity.
    */
   public void doRender(TropicalFishEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
      this.field_77045_g = (EntityModel<TropicalFishEntity>)(entity.getSize() == 0 ? this.field_204246_a : this.field_204247_j);
      float[] afloat = entity.func_204219_dC();
      GlStateManager.color3f(afloat[0], afloat[1], afloat[2]);
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
   }

   protected void applyRotations(TropicalFishEntity entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
      float f = 4.3F * MathHelper.sin(0.6F * ageInTicks);
      GlStateManager.rotatef(f, 0.0F, 1.0F, 0.0F);
      if (!entityLiving.isInWater()) {
         GlStateManager.translatef(0.2F, 0.1F, 0.0F);
         GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
      }

   }
}