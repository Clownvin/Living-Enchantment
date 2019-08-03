package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.LlamaModel;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LlamaDecorLayer extends LayerRenderer<LlamaEntity, LlamaModel<LlamaEntity>> {
   private static final ResourceLocation[] LLAMA_DECOR_TEXTURES = new ResourceLocation[]{new ResourceLocation("textures/entity/llama/decor/white.png"), new ResourceLocation("textures/entity/llama/decor/orange.png"), new ResourceLocation("textures/entity/llama/decor/magenta.png"), new ResourceLocation("textures/entity/llama/decor/light_blue.png"), new ResourceLocation("textures/entity/llama/decor/yellow.png"), new ResourceLocation("textures/entity/llama/decor/lime.png"), new ResourceLocation("textures/entity/llama/decor/pink.png"), new ResourceLocation("textures/entity/llama/decor/gray.png"), new ResourceLocation("textures/entity/llama/decor/light_gray.png"), new ResourceLocation("textures/entity/llama/decor/cyan.png"), new ResourceLocation("textures/entity/llama/decor/purple.png"), new ResourceLocation("textures/entity/llama/decor/blue.png"), new ResourceLocation("textures/entity/llama/decor/brown.png"), new ResourceLocation("textures/entity/llama/decor/green.png"), new ResourceLocation("textures/entity/llama/decor/red.png"), new ResourceLocation("textures/entity/llama/decor/black.png")};
   private static final ResourceLocation field_215342_b = new ResourceLocation("textures/entity/llama/decor/trader_llama.png");
   private final LlamaModel<LlamaEntity> model = new LlamaModel<>(0.5F);

   public LlamaDecorLayer(IEntityRenderer<LlamaEntity, LlamaModel<LlamaEntity>> p_i50933_1_) {
      super(p_i50933_1_);
   }

   public void render(LlamaEntity entityIn, float p_212842_2_, float p_212842_3_, float p_212842_4_, float p_212842_5_, float p_212842_6_, float p_212842_7_, float p_212842_8_) {
      DyeColor dyecolor = entityIn.getColor();
      if (dyecolor != null) {
         this.bindTexture(LLAMA_DECOR_TEXTURES[dyecolor.getId()]);
      } else {
         if (!entityIn.func_213800_eB()) {
            return;
         }

         this.bindTexture(field_215342_b);
      }

      this.getEntityModel().setModelAttributes(this.model);
      this.model.render(entityIn, p_212842_2_, p_212842_3_, p_212842_5_, p_212842_6_, p_212842_7_, p_212842_8_);
   }

   public boolean shouldCombineTextures() {
      return false;
   }
}