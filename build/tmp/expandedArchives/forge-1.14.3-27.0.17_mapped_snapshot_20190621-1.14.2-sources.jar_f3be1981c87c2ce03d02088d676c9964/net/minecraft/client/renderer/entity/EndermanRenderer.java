package net.minecraft.client.renderer.entity;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.entity.layers.EndermanEyesLayer;
import net.minecraft.client.renderer.entity.layers.HeldBlockLayer;
import net.minecraft.client.renderer.entity.model.EndermanModel;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EndermanRenderer extends MobRenderer<EndermanEntity, EndermanModel<EndermanEntity>> {
   private static final ResourceLocation ENDERMAN_TEXTURES = new ResourceLocation("textures/entity/enderman/enderman.png");
   private final Random rnd = new Random();

   public EndermanRenderer(EntityRendererManager renderManagerIn) {
      super(renderManagerIn, new EndermanModel<>(0.0F), 0.5F);
      this.addLayer(new EndermanEyesLayer<>(this));
      this.addLayer(new HeldBlockLayer(this));
   }

   /**
    * Renders the desired {@code T} type Entity.
    */
   public void doRender(EndermanEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
      BlockState blockstate = entity.getHeldBlockState();
      EndermanModel<EndermanEntity> endermanmodel = this.getEntityModel();
      endermanmodel.isCarrying = blockstate != null;
      endermanmodel.isAttacking = entity.isScreaming();
      if (entity.isScreaming()) {
         double d0 = 0.02D;
         x += this.rnd.nextGaussian() * 0.02D;
         z += this.rnd.nextGaussian() * 0.02D;
      }

      super.doRender(entity, x, y, z, entityYaw, partialTicks);
   }

   /**
    * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
    */
   protected ResourceLocation getEntityTexture(EndermanEntity entity) {
      return ENDERMAN_TEXTURES;
   }
}