package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.VillagerLevelPendantLayer;
import net.minecraft.client.renderer.entity.model.ZombieVillagerModel;
import net.minecraft.entity.monster.ZombieVillagerEntity;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ZombieVillagerRenderer extends BipedRenderer<ZombieVillagerEntity, ZombieVillagerModel<ZombieVillagerEntity>> {
   private static final ResourceLocation ZOMBIE_VILLAGER_TEXTURES = new ResourceLocation("textures/entity/zombie_villager/zombie_villager.png");

   public ZombieVillagerRenderer(EntityRendererManager p_i50952_1_, IReloadableResourceManager p_i50952_2_) {
      super(p_i50952_1_, new ZombieVillagerModel<>(), 0.5F);
      this.addLayer(new BipedArmorLayer<>(this, new ZombieVillagerModel(0.5F, true), new ZombieVillagerModel(1.0F, true)));
      this.addLayer(new VillagerLevelPendantLayer<>(this, p_i50952_2_, "zombie_villager"));
   }

   /**
    * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
    */
   protected ResourceLocation getEntityTexture(ZombieVillagerEntity entity) {
      return ZOMBIE_VILLAGER_TEXTURES;
   }

   protected void applyRotations(ZombieVillagerEntity entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      if (entityLiving.isConverting()) {
         rotationYaw += (float)(Math.cos((double)entityLiving.ticksExisted * 3.25D) * Math.PI * 0.25D);
      }

      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}