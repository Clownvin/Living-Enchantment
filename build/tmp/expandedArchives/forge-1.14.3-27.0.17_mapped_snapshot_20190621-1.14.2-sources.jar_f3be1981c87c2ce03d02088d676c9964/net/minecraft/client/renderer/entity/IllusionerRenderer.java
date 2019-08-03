package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.model.IllagerModel;
import net.minecraft.entity.monster.IllusionerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IllusionerRenderer extends IllagerRenderer<IllusionerEntity> {
   private static final ResourceLocation ILLUSIONIST = new ResourceLocation("textures/entity/illager/illusioner.png");

   public IllusionerRenderer(EntityRendererManager p_i47477_1_) {
      super(p_i47477_1_, new IllagerModel<>(0.0F, 0.0F, 64, 64), 0.5F);
      this.addLayer(new HeldItemLayer<IllusionerEntity, IllagerModel<IllusionerEntity>>(this) {
         public void render(IllusionerEntity entityIn, float p_212842_2_, float p_212842_3_, float p_212842_4_, float p_212842_5_, float p_212842_6_, float p_212842_7_, float p_212842_8_) {
            if (entityIn.isSpellcasting() || entityIn.isAggressive()) {
               super.render(entityIn, p_212842_2_, p_212842_3_, p_212842_4_, p_212842_5_, p_212842_6_, p_212842_7_, p_212842_8_);
            }

         }
      });
      this.field_77045_g.func_205062_a().showModel = true;
   }

   /**
    * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
    */
   protected ResourceLocation getEntityTexture(IllusionerEntity entity) {
      return ILLUSIONIST;
   }

   /**
    * Renders the desired {@code T} type Entity.
    */
   public void doRender(IllusionerEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
      if (entity.isInvisible()) {
         Vec3d[] avec3d = entity.getRenderLocations(partialTicks);
         float f = this.handleRotationFloat(entity, partialTicks);

         for(int i = 0; i < avec3d.length; ++i) {
            super.doRender(entity, x + avec3d[i].x + (double)MathHelper.cos((float)i + f * 0.5F) * 0.025D, y + avec3d[i].y + (double)MathHelper.cos((float)i + f * 0.75F) * 0.0125D, z + avec3d[i].z + (double)MathHelper.cos((float)i + f * 0.7F) * 0.025D, entityYaw, partialTicks);
         }
      } else {
         super.doRender(entity, x, y, z, entityYaw, partialTicks);
      }

   }

   protected boolean isVisible(IllusionerEntity p_193115_1_) {
      return true;
   }
}