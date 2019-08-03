package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Random;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.ModelBox;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ArrowLayer<T extends LivingEntity, M extends EntityModel<T>> extends LayerRenderer<T, M> {
   private final EntityRendererManager field_215336_a;

   public ArrowLayer(LivingRenderer<T, M> rendererIn) {
      super(rendererIn);
      this.field_215336_a = rendererIn.getRenderManager();
   }

   public void render(T entityIn, float p_212842_2_, float p_212842_3_, float p_212842_4_, float p_212842_5_, float p_212842_6_, float p_212842_7_, float p_212842_8_) {
      int i = entityIn.getArrowCountInEntity();
      if (i > 0) {
         Entity entity = new ArrowEntity(entityIn.world, entityIn.posX, entityIn.posY, entityIn.posZ);
         Random random = new Random((long)entityIn.getEntityId());
         RenderHelper.disableStandardItemLighting();

         for(int j = 0; j < i; ++j) {
            GlStateManager.pushMatrix();
            RendererModel renderermodel = this.getEntityModel().getRandomModelBox(random);
            ModelBox modelbox = renderermodel.cubeList.get(random.nextInt(renderermodel.cubeList.size()));
            renderermodel.postRender(0.0625F);
            float f = random.nextFloat();
            float f1 = random.nextFloat();
            float f2 = random.nextFloat();
            float f3 = MathHelper.lerp(f, modelbox.posX1, modelbox.posX2) / 16.0F;
            float f4 = MathHelper.lerp(f1, modelbox.posY1, modelbox.posY2) / 16.0F;
            float f5 = MathHelper.lerp(f2, modelbox.posZ1, modelbox.posZ2) / 16.0F;
            GlStateManager.translatef(f3, f4, f5);
            f = f * 2.0F - 1.0F;
            f1 = f1 * 2.0F - 1.0F;
            f2 = f2 * 2.0F - 1.0F;
            f = f * -1.0F;
            f1 = f1 * -1.0F;
            f2 = f2 * -1.0F;
            float f6 = MathHelper.sqrt(f * f + f2 * f2);
            entity.rotationYaw = (float)(Math.atan2((double)f, (double)f2) * (double)(180F / (float)Math.PI));
            entity.rotationPitch = (float)(Math.atan2((double)f1, (double)f6) * (double)(180F / (float)Math.PI));
            entity.prevRotationYaw = entity.rotationYaw;
            entity.prevRotationPitch = entity.rotationPitch;
            double d0 = 0.0D;
            double d1 = 0.0D;
            double d2 = 0.0D;
            this.field_215336_a.renderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, p_212842_4_, false);
            GlStateManager.popMatrix();
         }

         RenderHelper.enableStandardItemLighting();
      }
   }

   public boolean shouldCombineTextures() {
      return false;
   }
}