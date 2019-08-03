package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.PandaModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PandaHeldItemLayer extends LayerRenderer<PandaEntity, PandaModel<PandaEntity>> {
   public PandaHeldItemLayer(IEntityRenderer<PandaEntity, PandaModel<PandaEntity>> p_i50930_1_) {
      super(p_i50930_1_);
   }

   public void render(PandaEntity entityIn, float p_212842_2_, float p_212842_3_, float p_212842_4_, float p_212842_5_, float p_212842_6_, float p_212842_7_, float p_212842_8_) {
      ItemStack itemstack = entityIn.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
      if (entityIn.func_213556_dX() && !itemstack.isEmpty() && !entityIn.func_213566_eo()) {
         float f = -0.6F;
         float f1 = 1.4F;
         if (entityIn.func_213578_dZ()) {
            f -= 0.2F * MathHelper.sin(p_212842_5_ * 0.6F) + 0.2F;
            f1 -= 0.09F * MathHelper.sin(p_212842_5_ * 0.6F);
         }

         GlStateManager.pushMatrix();
         GlStateManager.translatef(0.1F, f1, f);
         Minecraft.getInstance().getItemRenderer().renderItem(itemstack, entityIn, ItemCameraTransforms.TransformType.GROUND, false);
         GlStateManager.popMatrix();
      }
   }

   public boolean shouldCombineTextures() {
      return false;
   }
}