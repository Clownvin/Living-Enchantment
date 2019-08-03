package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.model.ArmorStandArmorModel;
import net.minecraft.client.renderer.entity.model.ArmorStandModel;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ArmorStandRenderer extends LivingRenderer<ArmorStandEntity, ArmorStandArmorModel> {
   public static final ResourceLocation TEXTURE_ARMOR_STAND = new ResourceLocation("textures/entity/armorstand/wood.png");

   public ArmorStandRenderer(EntityRendererManager manager) {
      super(manager, new ArmorStandModel(), 0.0F);
      this.addLayer(new BipedArmorLayer<>(this, new ArmorStandArmorModel(0.5F), new ArmorStandArmorModel(1.0F)));
      this.addLayer(new HeldItemLayer<>(this));
      this.addLayer(new ElytraLayer<>(this));
      this.addLayer(new HeadLayer<>(this));
   }

   /**
    * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
    */
   protected ResourceLocation getEntityTexture(ArmorStandEntity entity) {
      return TEXTURE_ARMOR_STAND;
   }

   protected void applyRotations(ArmorStandEntity entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      GlStateManager.rotatef(180.0F - rotationYaw, 0.0F, 1.0F, 0.0F);
      float f = (float)(entityLiving.world.getGameTime() - entityLiving.punchCooldown) + partialTicks;
      if (f < 5.0F) {
         GlStateManager.rotatef(MathHelper.sin(f / 1.5F * (float)Math.PI) * 3.0F, 0.0F, 1.0F, 0.0F);
      }

   }

   protected boolean canRenderName(ArmorStandEntity entity) {
      return entity.isCustomNameVisible();
   }

   /**
    * Renders the desired {@code T} type Entity.
    */
   public void doRender(ArmorStandEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
      if (entity.hasMarker()) {
         this.renderMarker = true;
      }

      super.doRender(entity, x, y, z, entityYaw, partialTicks);
      if (entity.hasMarker()) {
         this.renderMarker = false;
      }

   }
}