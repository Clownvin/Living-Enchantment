package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.ParrotRenderer;
import net.minecraft.client.renderer.entity.model.ParrotModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParrotVariantLayer<T extends PlayerEntity> extends LayerRenderer<T, PlayerModel<T>> {
   private final ParrotModel field_215346_a = new ParrotModel();

   public ParrotVariantLayer(IEntityRenderer<T, PlayerModel<T>> p_i50929_1_) {
      super(p_i50929_1_);
   }

   public void render(T entityIn, float p_212842_2_, float p_212842_3_, float p_212842_4_, float p_212842_5_, float p_212842_6_, float p_212842_7_, float p_212842_8_) {
      GlStateManager.enableRescaleNormal();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.func_215343_a(entityIn, p_212842_2_, p_212842_3_, p_212842_4_, p_212842_6_, p_212842_7_, p_212842_8_, true);
      this.func_215343_a(entityIn, p_212842_2_, p_212842_3_, p_212842_4_, p_212842_6_, p_212842_7_, p_212842_8_, false);
      GlStateManager.disableRescaleNormal();
   }

   private void func_215343_a(T p_215343_1_, float p_215343_2_, float p_215343_3_, float p_215343_4_, float p_215343_5_, float p_215343_6_, float p_215343_7_, boolean p_215343_8_) {
      CompoundNBT compoundnbt = p_215343_8_ ? p_215343_1_.getLeftShoulderEntity() : p_215343_1_.getRightShoulderEntity();
      EntityType.byKey(compoundnbt.getString("id")).filter((p_215344_0_) -> {
         return p_215344_0_ == EntityType.PARROT;
      }).ifPresent((p_215345_9_) -> {
         GlStateManager.pushMatrix();
         GlStateManager.translatef(p_215343_8_ ? 0.4F : -0.4F, p_215343_1_.func_213287_bg() ? -1.3F : -1.5F, 0.0F);
         this.bindTexture(ParrotRenderer.PARROT_TEXTURES[compoundnbt.getInt("Variant")]);
         this.field_215346_a.func_217161_a(p_215343_2_, p_215343_3_, p_215343_5_, p_215343_6_, p_215343_7_, p_215343_1_.ticksExisted);
         GlStateManager.popMatrix();
      });
   }

   public boolean shouldCombineTextures() {
      return false;
   }
}