package net.minecraft.client.renderer.model;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Model {
   public final List<RendererModel> boxList = Lists.newArrayList();
   public int textureWidth = 64;
   public int textureHeight = 32;

   public RendererModel getRandomModelBox(Random rand) {
      return this.boxList.get(rand.nextInt(this.boxList.size()));
   }
}