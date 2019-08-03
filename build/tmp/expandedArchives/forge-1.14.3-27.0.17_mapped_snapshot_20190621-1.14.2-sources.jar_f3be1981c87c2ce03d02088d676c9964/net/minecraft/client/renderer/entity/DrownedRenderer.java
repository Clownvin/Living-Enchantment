package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.entity.layers.DrownedOuterLayer;
import net.minecraft.client.renderer.entity.model.DrownedModel;
import net.minecraft.entity.monster.DrownedEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DrownedRenderer extends AbstractZombieRenderer<DrownedEntity, DrownedModel<DrownedEntity>> {
   private static final ResourceLocation DROWNED_LOCATION = new ResourceLocation("textures/entity/zombie/drowned.png");

   public DrownedRenderer(EntityRendererManager p_i48906_1_) {
      super(p_i48906_1_, new DrownedModel<>(0.0F, 0.0F, 64, 64), new DrownedModel<>(0.5F, true), new DrownedModel<>(1.0F, true));
      this.addLayer(new DrownedOuterLayer<>(this));
   }

   /**
    * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
    */
   @Nullable
   protected ResourceLocation getEntityTexture(ZombieEntity entity) {
      return DROWNED_LOCATION;
   }

   protected void applyRotations(DrownedEntity entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      float f = entityLiving.getSwimAnimation(partialTicks);
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
      if (f > 0.0F) {
         GlStateManager.rotatef(MathHelper.lerp(f, entityLiving.rotationPitch, -10.0F - entityLiving.rotationPitch), 1.0F, 0.0F, 0.0F);
      }

   }
}