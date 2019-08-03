package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.PufferFishBigModel;
import net.minecraft.client.renderer.entity.model.PufferFishMediumModel;
import net.minecraft.client.renderer.entity.model.PufferFishSmallModel;
import net.minecraft.entity.passive.fish.PufferfishEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PufferfishRenderer extends MobRenderer<PufferfishEntity, EntityModel<PufferfishEntity>> {
   private static final ResourceLocation field_203771_a = new ResourceLocation("textures/entity/fish/pufferfish.png");
   private int field_203772_j;
   private final PufferFishSmallModel<PufferfishEntity> field_203773_k = new PufferFishSmallModel<>();
   private final PufferFishMediumModel<PufferfishEntity> field_203774_l = new PufferFishMediumModel<>();
   private final PufferFishBigModel<PufferfishEntity> field_203775_m = new PufferFishBigModel<>();

   public PufferfishRenderer(EntityRendererManager p_i48863_1_) {
      super(p_i48863_1_, new PufferFishBigModel<>(), 0.2F);
      this.field_203772_j = 3;
   }

   /**
    * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
    */
   @Nullable
   protected ResourceLocation getEntityTexture(PufferfishEntity entity) {
      return field_203771_a;
   }

   /**
    * Renders the desired {@code T} type Entity.
    */
   public void doRender(PufferfishEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
      int i = entity.getPuffState();
      if (i != this.field_203772_j) {
         if (i == 0) {
            this.field_77045_g = this.field_203773_k;
         } else if (i == 1) {
            this.field_77045_g = this.field_203774_l;
         } else {
            this.field_77045_g = this.field_203775_m;
         }
      }

      this.field_203772_j = i;
      this.shadowSize = 0.1F + 0.1F * (float)i;
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
   }

   protected void applyRotations(PufferfishEntity entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      GlStateManager.translatef(0.0F, MathHelper.cos(ageInTicks * 0.05F) * 0.08F, 0.0F);
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}