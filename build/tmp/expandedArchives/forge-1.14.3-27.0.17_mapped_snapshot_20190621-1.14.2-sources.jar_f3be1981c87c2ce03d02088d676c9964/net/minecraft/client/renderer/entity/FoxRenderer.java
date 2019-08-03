package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.entity.layers.FoxHeldItemLayer;
import net.minecraft.client.renderer.entity.model.FoxModel;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FoxRenderer extends MobRenderer<FoxEntity, FoxModel<FoxEntity>> {
   private static final ResourceLocation field_217767_a = new ResourceLocation("textures/entity/fox/fox.png");
   private static final ResourceLocation field_217768_j = new ResourceLocation("textures/entity/fox/fox_sleep.png");
   private static final ResourceLocation field_217769_k = new ResourceLocation("textures/entity/fox/snow_fox.png");
   private static final ResourceLocation field_217770_l = new ResourceLocation("textures/entity/fox/snow_fox_sleep.png");

   public FoxRenderer(EntityRendererManager p_i50969_1_) {
      super(p_i50969_1_, new FoxModel<>(), 0.4F);
      this.addLayer(new FoxHeldItemLayer(this));
   }

   protected void applyRotations(FoxEntity entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
      if (entityLiving.func_213480_dY() || entityLiving.func_213472_dX()) {
         GlStateManager.rotatef(-MathHelper.lerp(partialTicks, entityLiving.prevRotationPitch, entityLiving.rotationPitch), 1.0F, 0.0F, 0.0F);
      }

   }

   /**
    * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
    */
   @Nullable
   protected ResourceLocation getEntityTexture(FoxEntity entity) {
      if (entity.getVariantType() == FoxEntity.Type.RED) {
         return entity.isSleeping() ? field_217768_j : field_217767_a;
      } else {
         return entity.isSleeping() ? field_217770_l : field_217769_k;
      }
   }
}