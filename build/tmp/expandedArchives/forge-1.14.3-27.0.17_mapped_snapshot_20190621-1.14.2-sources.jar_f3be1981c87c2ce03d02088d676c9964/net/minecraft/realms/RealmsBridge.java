package net.minecraft.realms;

import com.mojang.datafixers.util.Either;
import java.lang.reflect.Constructor;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.AlertScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.realms.pluginapi.LoadedRealmsPlugin;
import net.minecraft.realms.pluginapi.RealmsPlugin;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsBridge extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private Screen previousScreen;

   public void switchToRealms(Screen p_switchToRealms_1_) {
      this.previousScreen = p_switchToRealms_1_;
      Optional<LoadedRealmsPlugin> optional = this.tryLoadRealms();
      if (optional.isPresent()) {
         Realms.setScreen(optional.get().getMainScreen(this));
      } else {
         this.showMissingRealmsErrorScreen();
      }

   }

   @Nullable
   public RealmsScreenProxy getNotificationScreen(Screen p_getNotificationScreen_1_) {
      this.previousScreen = p_getNotificationScreen_1_;
      return this.tryLoadRealms().map((p_214461_1_) -> {
         return p_214461_1_.getNotificationsScreen(this).getProxy();
      }).orElse((RealmsScreenProxy)null);
   }

   private Optional<LoadedRealmsPlugin> tryLoadRealms() {
      try {
         Class<?> oclass = Class.forName("com.mojang.realmsclient.plugin.RealmsPluginImpl");
         Constructor<?> constructor = oclass.getDeclaredConstructor();
         constructor.setAccessible(true);
         Object object = constructor.newInstance();
         RealmsPlugin realmsplugin = (RealmsPlugin)object;
         Either<LoadedRealmsPlugin, String> either = realmsplugin.tryLoad(Realms.getMinecraftVersionString());
         Optional<String> optional = either.right();
         if (optional.isPresent()) {
            LOGGER.error("Failed to load Realms module: {}", optional.get());
            return Optional.empty();
         }

         return either.left();
      } catch (ClassNotFoundException var7) {
         LOGGER.error("Realms module missing");
      } catch (Exception exception) {
         LOGGER.error("Failed to load Realms module", (Throwable)exception);
      }

      return Optional.empty();
   }

   public void init() {
      Minecraft.getInstance().displayGuiScreen(this.previousScreen);
   }

   private void showMissingRealmsErrorScreen() {
      Minecraft.getInstance().displayGuiScreen(new AlertScreen(() -> {
         Minecraft.getInstance().displayGuiScreen(this.previousScreen);
      }, new StringTextComponent(""), new TranslationTextComponent(SharedConstants.getVersion().isStable() ? "realms.missing.module.error.text" : "realms.missing.snapshot.error.text")));
   }
}