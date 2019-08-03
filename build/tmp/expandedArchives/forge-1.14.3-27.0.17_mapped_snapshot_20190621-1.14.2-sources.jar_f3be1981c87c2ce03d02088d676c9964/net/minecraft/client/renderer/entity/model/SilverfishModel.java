package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SilverfishModel<T extends Entity> extends EntityModel<T> {
   private final RendererModel[] field_78171_a;
   private final RendererModel[] field_78169_b;
   private final float[] zPlacement = new float[7];
   private static final int[][] SILVERFISH_BOX_LENGTH = new int[][]{{3, 2, 2}, {4, 3, 2}, {6, 4, 3}, {3, 3, 3}, {2, 2, 3}, {2, 1, 2}, {1, 1, 2}};
   private static final int[][] SILVERFISH_TEXTURE_POSITIONS = new int[][]{{0, 0}, {0, 4}, {0, 9}, {0, 16}, {0, 22}, {11, 0}, {13, 4}};

   public SilverfishModel() {
      this.field_78171_a = new RendererModel[7];
      float f = -3.5F;

      for(int i = 0; i < this.field_78171_a.length; ++i) {
         this.field_78171_a[i] = new RendererModel(this, SILVERFISH_TEXTURE_POSITIONS[i][0], SILVERFISH_TEXTURE_POSITIONS[i][1]);
         this.field_78171_a[i].addBox((float)SILVERFISH_BOX_LENGTH[i][0] * -0.5F, 0.0F, (float)SILVERFISH_BOX_LENGTH[i][2] * -0.5F, SILVERFISH_BOX_LENGTH[i][0], SILVERFISH_BOX_LENGTH[i][1], SILVERFISH_BOX_LENGTH[i][2]);
         this.field_78171_a[i].setRotationPoint(0.0F, (float)(24 - SILVERFISH_BOX_LENGTH[i][1]), f);
         this.zPlacement[i] = f;
         if (i < this.field_78171_a.length - 1) {
            f += (float)(SILVERFISH_BOX_LENGTH[i][2] + SILVERFISH_BOX_LENGTH[i + 1][2]) * 0.5F;
         }
      }

      this.field_78169_b = new RendererModel[3];
      this.field_78169_b[0] = new RendererModel(this, 20, 0);
      this.field_78169_b[0].addBox(-5.0F, 0.0F, (float)SILVERFISH_BOX_LENGTH[2][2] * -0.5F, 10, 8, SILVERFISH_BOX_LENGTH[2][2]);
      this.field_78169_b[0].setRotationPoint(0.0F, 16.0F, this.zPlacement[2]);
      this.field_78169_b[1] = new RendererModel(this, 20, 11);
      this.field_78169_b[1].addBox(-3.0F, 0.0F, (float)SILVERFISH_BOX_LENGTH[4][2] * -0.5F, 6, 4, SILVERFISH_BOX_LENGTH[4][2]);
      this.field_78169_b[1].setRotationPoint(0.0F, 20.0F, this.zPlacement[4]);
      this.field_78169_b[2] = new RendererModel(this, 20, 18);
      this.field_78169_b[2].addBox(-3.0F, 0.0F, (float)SILVERFISH_BOX_LENGTH[4][2] * -0.5F, 6, 5, SILVERFISH_BOX_LENGTH[1][2]);
      this.field_78169_b[2].setRotationPoint(0.0F, 19.0F, this.zPlacement[1]);
   }

   /**
    * Sets the models various rotation angles then renders the model.
    */
   public void render(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

      for(RendererModel renderermodel : this.field_78171_a) {
         renderermodel.render(scale);
      }

      for(RendererModel renderermodel1 : this.field_78169_b) {
         renderermodel1.render(scale);
      }

   }

   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
      for(int i = 0; i < this.field_78171_a.length; ++i) {
         this.field_78171_a[i].rotateAngleY = MathHelper.cos(ageInTicks * 0.9F + (float)i * 0.15F * (float)Math.PI) * (float)Math.PI * 0.05F * (float)(1 + Math.abs(i - 2));
         this.field_78171_a[i].rotationPointX = MathHelper.sin(ageInTicks * 0.9F + (float)i * 0.15F * (float)Math.PI) * (float)Math.PI * 0.2F * (float)Math.abs(i - 2);
      }

      this.field_78169_b[0].rotateAngleY = this.field_78171_a[2].rotateAngleY;
      this.field_78169_b[1].rotateAngleY = this.field_78171_a[4].rotateAngleY;
      this.field_78169_b[1].rotationPointX = this.field_78171_a[4].rotationPointX;
      this.field_78169_b[2].rotateAngleY = this.field_78171_a[1].rotateAngleY;
      this.field_78169_b[2].rotationPointX = this.field_78171_a[1].rotationPointX;
   }
}