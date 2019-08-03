package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.DolphinModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DolphinCarriedItemLayer extends LayerRenderer<DolphinEntity, DolphinModel<DolphinEntity>> {
   private final ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

   public DolphinCarriedItemLayer(IEntityRenderer<DolphinEntity, DolphinModel<DolphinEntity>> p_i50944_1_) {
      super(p_i50944_1_);
   }

   public void render(DolphinEntity entityIn, float p_212842_2_, float p_212842_3_, float p_212842_4_, float p_212842_5_, float p_212842_6_, float p_212842_7_, float p_212842_8_) {
      boolean flag = entityIn.getPrimaryHand() == HandSide.RIGHT;
      ItemStack itemstack = flag ? entityIn.getHeldItemOffhand() : entityIn.getHeldItemMainhand();
      ItemStack itemstack1 = flag ? entityIn.getHeldItemMainhand() : entityIn.getHeldItemOffhand();
      if (!itemstack.isEmpty() || !itemstack1.isEmpty()) {
         this.func_205129_a(entityIn, itemstack1);
      }
   }

   private void func_205129_a(LivingEntity p_205129_1_, ItemStack p_205129_2_) {
      if (!p_205129_2_.isEmpty()) {
         Item item = p_205129_2_.getItem();
         Block block = Block.getBlockFromItem(item);
         GlStateManager.pushMatrix();
         boolean flag = this.itemRenderer.shouldRenderItemIn3D(p_205129_2_) && block.getRenderLayer() == BlockRenderLayer.TRANSLUCENT;
         if (flag) {
            GlStateManager.depthMask(false);
         }

         float f = 1.0F;
         float f1 = -1.0F;
         float f2 = MathHelper.abs(p_205129_1_.rotationPitch) / 60.0F;
         if (p_205129_1_.rotationPitch < 0.0F) {
            GlStateManager.translatef(0.0F, 1.0F - f2 * 0.5F, -1.0F + f2 * 0.5F);
         } else {
            GlStateManager.translatef(0.0F, 1.0F + f2 * 0.8F, -1.0F + f2 * 0.2F);
         }

         this.itemRenderer.renderItem(p_205129_2_, p_205129_1_, ItemCameraTransforms.TransformType.GROUND, false);
         if (flag) {
            GlStateManager.depthMask(true);
         }

         GlStateManager.popMatrix();
      }
   }

   public boolean shouldCombineTextures() {
      return false;
   }
}