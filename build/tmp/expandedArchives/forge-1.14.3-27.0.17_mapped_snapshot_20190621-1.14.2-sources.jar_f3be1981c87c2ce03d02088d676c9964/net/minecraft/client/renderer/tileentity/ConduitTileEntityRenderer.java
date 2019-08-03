package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.tileentity.ConduitTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ConduitTileEntityRenderer extends TileEntityRenderer<ConduitTileEntity> {
   private static final ResourceLocation BASE_TEXTURE = new ResourceLocation("textures/entity/conduit/base.png");
   private static final ResourceLocation CAGE_TEXTURE = new ResourceLocation("textures/entity/conduit/cage.png");
   private static final ResourceLocation WIND_TEXTURE = new ResourceLocation("textures/entity/conduit/wind.png");
   private static final ResourceLocation VERTICAL_WIND_TEXTURE = new ResourceLocation("textures/entity/conduit/wind_vertical.png");
   private static final ResourceLocation OPEN_EYE_TEXTURE = new ResourceLocation("textures/entity/conduit/open_eye.png");
   private static final ResourceLocation CLOSED_EYE_TEXTURE = new ResourceLocation("textures/entity/conduit/closed_eye.png");
   private final ConduitTileEntityRenderer.ShellModel shellModel = new ConduitTileEntityRenderer.ShellModel();
   private final ConduitTileEntityRenderer.CageModel cageModel = new ConduitTileEntityRenderer.CageModel();
   private final ConduitTileEntityRenderer.WindModel windModel = new ConduitTileEntityRenderer.WindModel();
   private final ConduitTileEntityRenderer.EyeModel eyeModel = new ConduitTileEntityRenderer.EyeModel();

   public void render(ConduitTileEntity tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
      float f = (float)tileEntityIn.ticksExisted + partialTicks;
      if (!tileEntityIn.isActive()) {
         float f1 = tileEntityIn.getActiveRotation(0.0F);
         this.bindTexture(BASE_TEXTURE);
         GlStateManager.pushMatrix();
         GlStateManager.translatef((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
         GlStateManager.rotatef(f1, 0.0F, 1.0F, 0.0F);
         this.shellModel.func_217108_a(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
         GlStateManager.popMatrix();
      } else if (tileEntityIn.isActive()) {
         float f3 = tileEntityIn.getActiveRotation(partialTicks) * (180F / (float)Math.PI);
         float f2 = MathHelper.sin(f * 0.1F) / 2.0F + 0.5F;
         f2 = f2 * f2 + f2;
         this.bindTexture(CAGE_TEXTURE);
         GlStateManager.disableCull();
         GlStateManager.pushMatrix();
         GlStateManager.translatef((float)x + 0.5F, (float)y + 0.3F + f2 * 0.2F, (float)z + 0.5F);
         GlStateManager.rotatef(f3, 0.5F, 1.0F, 0.5F);
         this.cageModel.func_217106_a(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
         GlStateManager.popMatrix();
         int i = 3;
         int j = tileEntityIn.ticksExisted / 3 % 22;
         this.windModel.func_205077_a(j);
         int k = tileEntityIn.ticksExisted / 66 % 3;
         switch(k) {
         case 0:
            this.bindTexture(WIND_TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
            this.windModel.func_217109_a(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
            GlStateManager.scalef(0.875F, 0.875F, 0.875F);
            GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
            this.windModel.func_217109_a(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
            GlStateManager.popMatrix();
            break;
         case 1:
            this.bindTexture(VERTICAL_WIND_TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
            GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
            this.windModel.func_217109_a(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
            GlStateManager.scalef(0.875F, 0.875F, 0.875F);
            GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
            this.windModel.func_217109_a(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
            GlStateManager.popMatrix();
            break;
         case 2:
            this.bindTexture(WIND_TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
            GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
            this.windModel.func_217109_a(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
            GlStateManager.scalef(0.875F, 0.875F, 0.875F);
            GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
            this.windModel.func_217109_a(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
            GlStateManager.popMatrix();
         }

         ActiveRenderInfo activerenderinfo = this.rendererDispatcher.renderInfo;
         if (tileEntityIn.isEyeOpen()) {
            this.bindTexture(OPEN_EYE_TEXTURE);
         } else {
            this.bindTexture(CLOSED_EYE_TEXTURE);
         }

         GlStateManager.pushMatrix();
         GlStateManager.translatef((float)x + 0.5F, (float)y + 0.3F + f2 * 0.2F, (float)z + 0.5F);
         GlStateManager.scalef(0.5F, 0.5F, 0.5F);
         GlStateManager.rotatef(-activerenderinfo.getYaw(), 0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(activerenderinfo.getPitch(), 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
         this.eyeModel.func_217107_a(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.083333336F);
         GlStateManager.popMatrix();
      }

      super.render(tileEntityIn, x, y, z, partialTicks, destroyStage);
   }

   @OnlyIn(Dist.CLIENT)
   static class CageModel extends Model {
      private final RendererModel field_205075_a;

      public CageModel() {
         this.textureWidth = 32;
         this.textureHeight = 16;
         this.field_205075_a = new RendererModel(this, 0, 0);
         this.field_205075_a.addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8);
      }

      public void func_217106_a(float p_217106_1_, float p_217106_2_, float p_217106_3_, float p_217106_4_, float p_217106_5_, float p_217106_6_) {
         this.field_205075_a.render(p_217106_6_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class EyeModel extends Model {
      private final RendererModel field_207745_a;

      public EyeModel() {
         this.textureWidth = 8;
         this.textureHeight = 8;
         this.field_207745_a = new RendererModel(this, 0, 0);
         this.field_207745_a.addBox(-4.0F, -4.0F, 0.0F, 8, 8, 0, 0.01F);
      }

      public void func_217107_a(float p_217107_1_, float p_217107_2_, float p_217107_3_, float p_217107_4_, float p_217107_5_, float p_217107_6_) {
         this.field_207745_a.render(p_217107_6_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class ShellModel extends Model {
      private final RendererModel field_205076_a;

      public ShellModel() {
         this.textureWidth = 32;
         this.textureHeight = 16;
         this.field_205076_a = new RendererModel(this, 0, 0);
         this.field_205076_a.addBox(-3.0F, -3.0F, -3.0F, 6, 6, 6);
      }

      public void func_217108_a(float p_217108_1_, float p_217108_2_, float p_217108_3_, float p_217108_4_, float p_217108_5_, float p_217108_6_) {
         this.field_205076_a.render(p_217108_6_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class WindModel extends Model {
      private final RendererModel[] field_205079_b = new RendererModel[22];
      private int field_205080_c;

      public WindModel() {
         this.textureWidth = 64;
         this.textureHeight = 1024;

         for(int i = 0; i < 22; ++i) {
            this.field_205079_b[i] = new RendererModel(this, 0, 32 * i);
            this.field_205079_b[i].addBox(-8.0F, -8.0F, -8.0F, 16, 16, 16);
         }

      }

      public void func_217109_a(float p_217109_1_, float p_217109_2_, float p_217109_3_, float p_217109_4_, float p_217109_5_, float p_217109_6_) {
         this.field_205079_b[this.field_205080_c].render(p_217109_6_);
      }

      public void func_205077_a(int p_205077_1_) {
         this.field_205080_c = p_205077_1_;
      }
   }
}