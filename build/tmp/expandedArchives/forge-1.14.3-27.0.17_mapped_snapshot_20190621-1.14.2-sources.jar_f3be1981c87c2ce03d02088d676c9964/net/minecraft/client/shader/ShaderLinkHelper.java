package net.minecraft.client.shader;

import com.mojang.blaze3d.platform.GLX;
import java.io.IOException;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ShaderLinkHelper {
   private static final Logger LOGGER = LogManager.getLogger();
   private static ShaderLinkHelper staticShaderLinkHelper;

   public static void setNewStaticShaderLinkHelper() {
      staticShaderLinkHelper = new ShaderLinkHelper();
   }

   public static ShaderLinkHelper getStaticShaderLinkHelper() {
      return staticShaderLinkHelper;
   }

   public void deleteShader(IShaderManager manager) {
      manager.getFragmentShaderLoader().detachShader();
      manager.getVertexShaderLoader().detachShader();
      GLX.glDeleteProgram(manager.getProgram());
   }

   public int createProgram() throws IOException {
      int i = GLX.glCreateProgram();
      if (i <= 0) {
         throw new IOException("Could not create shader program (returned program ID " + i + ")");
      } else {
         return i;
      }
   }

   public void linkProgram(IShaderManager manager) throws IOException {
      manager.getFragmentShaderLoader().attachShader(manager);
      manager.getVertexShaderLoader().attachShader(manager);
      GLX.glLinkProgram(manager.getProgram());
      int i = GLX.glGetProgrami(manager.getProgram(), GLX.GL_LINK_STATUS);
      if (i == 0) {
         LOGGER.warn("Error encountered when linking program containing VS {} and FS {}. Log output:", manager.getVertexShaderLoader().getShaderFilename(), manager.getFragmentShaderLoader().getShaderFilename());
         LOGGER.warn(GLX.glGetProgramInfoLog(manager.getProgram(), 32768));
      }

   }
}