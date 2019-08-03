package net.minecraft.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.renderer.entity.model.HorseArmorChestsModel;
import net.minecraft.entity.passive.horse.AbstractChestedHorseEntity;
import net.minecraft.entity.passive.horse.DonkeyEntity;
import net.minecraft.entity.passive.horse.MuleEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChestedHorseRenderer<T extends AbstractChestedHorseEntity> extends AbstractHorseRenderer<T, HorseArmorChestsModel<T>> {
   private static final Map<Class<?>, ResourceLocation> field_195635_a = Maps.newHashMap(ImmutableMap.of(DonkeyEntity.class, new ResourceLocation("textures/entity/horse/donkey.png"), MuleEntity.class, new ResourceLocation("textures/entity/horse/mule.png")));

   public ChestedHorseRenderer(EntityRendererManager renderManagerIn, float p_i48144_2_) {
      super(renderManagerIn, new HorseArmorChestsModel<>(0.0F), p_i48144_2_);
   }

   /**
    * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
    */
   protected ResourceLocation getEntityTexture(T entity) {
      return field_195635_a.get(entity.getClass());
   }
}