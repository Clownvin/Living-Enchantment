package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.entity.model.SalmonModel;
import net.minecraft.entity.passive.fish.SalmonEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SalmonRenderer extends MobRenderer<SalmonEntity, SalmonModel<SalmonEntity>> {
   private static final ResourceLocation field_203776_a = new ResourceLocation("textures/entity/fish/salmon.png");

   public SalmonRenderer(EntityRendererManager p_i48862_1_) {
      super(p_i48862_1_, new SalmonModel<>(), 0.4F);
   }

   /**
    * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
    */
   @Nullable
   protected ResourceLocation getEntityTexture(SalmonEntity entity) {
      return field_203776_a;
   }

   protected void applyRotations(SalmonEntity entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
      float f = 1.0F;
      float f1 = 1.0F;
      if (!entityLiving.isInWater()) {
         f = 1.3F;
         f1 = 1.7F;
      }

      float f2 = f * 4.3F * MathHelper.sin(f1 * 0.6F * ageInTicks);
      GlStateManager.rotatef(f2, 0.0F, 1.0F, 0.0F);
      GlStateManager.translatef(0.0F, 0.0F, -0.4F);
      if (!entityLiving.isInWater()) {
         GlStateManager.translatef(0.2F, 0.1F, 0.0F);
         GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
      }

   }
}