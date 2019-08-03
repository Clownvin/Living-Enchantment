package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.FloatBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderHelper {
   private static final FloatBuffer COLOR_BUFFER = GLAllocation.createDirectFloatBuffer(4);
   private static final Vector3f LIGHT0_POS = func_216509_a(0.2F, 1.0F, -0.7F);
   private static final Vector3f LIGHT1_POS = func_216509_a(-0.2F, 1.0F, 0.7F);

   private static Vector3f func_216509_a(float p_216509_0_, float p_216509_1_, float p_216509_2_) {
      Vector3f vector3f = new Vector3f(p_216509_0_, p_216509_1_, p_216509_2_);
      vector3f.normalize();
      return vector3f;
   }

   /**
    * Disables the OpenGL lighting properties enabled by enableStandardItemLighting
    */
   public static void disableStandardItemLighting() {
      GlStateManager.disableLighting();
      GlStateManager.disableLight(0);
      GlStateManager.disableLight(1);
      GlStateManager.disableColorMaterial();
   }

   /**
    * Sets the OpenGL lighting properties to the values used when rendering blocks as items
    */
   public static void enableStandardItemLighting() {
      GlStateManager.enableLighting();
      GlStateManager.enableLight(0);
      GlStateManager.enableLight(1);
      GlStateManager.enableColorMaterial();
      GlStateManager.colorMaterial(1032, 5634);
      GlStateManager.light(16384, 4611, setColorBuffer(LIGHT0_POS.getX(), LIGHT0_POS.getY(), LIGHT0_POS.getZ(), 0.0F));
      float f = 0.6F;
      GlStateManager.light(16384, 4609, setColorBuffer(0.6F, 0.6F, 0.6F, 1.0F));
      GlStateManager.light(16384, 4608, setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
      GlStateManager.light(16384, 4610, setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
      GlStateManager.light(16385, 4611, setColorBuffer(LIGHT1_POS.getX(), LIGHT1_POS.getY(), LIGHT1_POS.getZ(), 0.0F));
      GlStateManager.light(16385, 4609, setColorBuffer(0.6F, 0.6F, 0.6F, 1.0F));
      GlStateManager.light(16385, 4608, setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
      GlStateManager.light(16385, 4610, setColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
      GlStateManager.shadeModel(7424);
      float f1 = 0.4F;
      GlStateManager.lightModel(2899, setColorBuffer(0.4F, 0.4F, 0.4F, 1.0F));
   }

   /**
    * Update and return colorBuffer with the RGBA values passed as arguments
    */
   public static FloatBuffer setColorBuffer(float red, float green, float blue, float alpha) {
      COLOR_BUFFER.clear();
      COLOR_BUFFER.put(red).put(green).put(blue).put(alpha);
      COLOR_BUFFER.flip();
      return COLOR_BUFFER;
   }

   /**
    * Sets OpenGL lighting for rendering blocks as items inside GUI screens (such as containers).
    */
   public static void enableGUIStandardItemLighting() {
      GlStateManager.pushMatrix();
      GlStateManager.rotatef(-30.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(165.0F, 1.0F, 0.0F, 0.0F);
      enableStandardItemLighting();
      GlStateManager.popMatrix();
   }
}