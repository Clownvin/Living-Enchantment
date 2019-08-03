package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TridentModel extends Model {
   public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/trident.png");
   private final RendererModel modelRenderer;

   public TridentModel() {
      this.textureWidth = 32;
      this.textureHeight = 32;
      this.modelRenderer = new RendererModel(this, 0, 0);
      this.modelRenderer.addBox(-0.5F, -4.0F, -0.5F, 1, 31, 1, 0.0F);
      RendererModel renderermodel = new RendererModel(this, 4, 0);
      renderermodel.addBox(-1.5F, 0.0F, -0.5F, 3, 2, 1);
      this.modelRenderer.addChild(renderermodel);
      RendererModel renderermodel1 = new RendererModel(this, 4, 3);
      renderermodel1.addBox(-2.5F, -3.0F, -0.5F, 1, 4, 1);
      this.modelRenderer.addChild(renderermodel1);
      RendererModel renderermodel2 = new RendererModel(this, 4, 3);
      renderermodel2.mirror = true;
      renderermodel2.addBox(1.5F, -3.0F, -0.5F, 1, 4, 1);
      this.modelRenderer.addChild(renderermodel2);
   }

   public void renderer() {
      this.modelRenderer.render(0.0625F);
   }
}