package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.CampfireBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.CampfireTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CampfireTileEntityRenderer extends TileEntityRenderer<CampfireTileEntity> {
   public void render(CampfireTileEntity tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
      Direction direction = tileEntityIn.getBlockState().get(CampfireBlock.FACING);
      NonNullList<ItemStack> nonnulllist = tileEntityIn.getInventory();

      for(int i = 0; i < nonnulllist.size(); ++i) {
         ItemStack itemstack = nonnulllist.get(i);
         if (itemstack != ItemStack.EMPTY) {
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)x + 0.5F, (float)y + 0.44921875F, (float)z + 0.5F);
            Direction direction1 = Direction.byHorizontalIndex((i + direction.getHorizontalIndex()) % 4);
            GlStateManager.rotatef(-direction1.getHorizontalAngle(), 0.0F, 1.0F, 0.0F);
            GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.translatef(-0.3125F, -0.3125F, 0.0F);
            GlStateManager.scalef(0.375F, 0.375F, 0.375F);
            Minecraft.getInstance().getItemRenderer().renderItem(itemstack, ItemCameraTransforms.TransformType.FIXED);
            GlStateManager.popMatrix();
         }
      }

   }
}