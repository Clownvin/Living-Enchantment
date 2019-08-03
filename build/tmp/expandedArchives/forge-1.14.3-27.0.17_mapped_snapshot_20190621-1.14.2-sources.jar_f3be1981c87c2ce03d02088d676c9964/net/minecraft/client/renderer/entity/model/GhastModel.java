package net.minecraft.client.renderer.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GhastModel<T extends Entity> extends EntityModel<T> {
   private final RendererModel field_78128_a;
   private final RendererModel[] field_78127_b = new RendererModel[9];

   public GhastModel() {
      int i = -16;
      this.field_78128_a = new RendererModel(this, 0, 0);
      this.field_78128_a.addBox(-8.0F, -8.0F, -8.0F, 16, 16, 16);
      this.field_78128_a.rotationPointY += 8.0F;
      Random random = new Random(1660L);

      for(int j = 0; j < this.field_78127_b.length; ++j) {
         this.field_78127_b[j] = new RendererModel(this, 0, 0);
         float f = (((float)(j % 3) - (float)(j / 3 % 2) * 0.5F + 0.25F) / 2.0F * 2.0F - 1.0F) * 5.0F;
         float f1 = ((float)(j / 3) / 2.0F * 2.0F - 1.0F) * 5.0F;
         int k = random.nextInt(7) + 8;
         this.field_78127_b[j].addBox(-1.0F, 0.0F, -1.0F, 2, k, 2);
         this.field_78127_b[j].rotationPointX = f;
         this.field_78127_b[j].rotationPointZ = f1;
         this.field_78127_b[j].rotationPointY = 15.0F;
      }

   }

   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
      for(int i = 0; i < this.field_78127_b.length; ++i) {
         this.field_78127_b[i].rotateAngleX = 0.2F * MathHelper.sin(ageInTicks * 0.3F + (float)i) + 0.4F;
      }

   }

   /**
    * Sets the models various rotation angles then renders the model.
    */
   public void render(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      GlStateManager.pushMatrix();
      GlStateManager.translatef(0.0F, 0.6F, 0.0F);
      this.field_78128_a.render(scale);

      for(RendererModel renderermodel : this.field_78127_b) {
         renderermodel.render(scale);
      }

      GlStateManager.popMatrix();
   }
}