package net.minecraft.client.gui.screen;

import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.AbstractOption;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VideoSettingsScreen extends Screen {
   private final Screen parentGuiScreen;
   private final GameSettings guiGameSettings;
   private OptionsRowList optionsRowList;
   private static final AbstractOption[] OPTIONS = new AbstractOption[]{AbstractOption.GRAPHICS, AbstractOption.RENDER_DISTANCE, AbstractOption.AO, AbstractOption.FRAMERATE_LIMIT, AbstractOption.VSYNC, AbstractOption.VIEW_BOBBING, AbstractOption.GUI_SCALE, AbstractOption.ATTACK_INDICATOR, AbstractOption.GAMMA, AbstractOption.RENDER_CLOUDS, AbstractOption.FULLSCREEN, AbstractOption.PARTICLES, AbstractOption.MIPMAP_LEVELS, AbstractOption.ENTITY_SHADOWS, AbstractOption.BIOME_BLEND_RADIUS};
   private int field_213108_e;

   public VideoSettingsScreen(Screen parentScreenIn, GameSettings gameSettingsIn) {
      super(new TranslationTextComponent("options.videoTitle"));
      this.parentGuiScreen = parentScreenIn;
      this.guiGameSettings = gameSettingsIn;
   }

   protected void init() {
      this.field_213108_e = this.guiGameSettings.mipmapLevels;
      this.optionsRowList = new OptionsRowList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
      this.optionsRowList.func_214333_a(AbstractOption.FULLSCREEN_RESOLUTION);
      this.optionsRowList.func_214335_a(OPTIONS);
      this.children.add(this.optionsRowList);
      this.addButton(new Button(this.width / 2 - 100, this.height - 27, 200, 20, I18n.format("gui.done"), (p_213106_1_) -> {
         this.minecraft.gameSettings.saveOptions();
         this.minecraft.mainWindow.update();
         this.minecraft.displayGuiScreen(this.parentGuiScreen);
      }));
   }

   public void removed() {
      if (this.guiGameSettings.mipmapLevels != this.field_213108_e) {
         this.minecraft.getTextureMap().setMipmapLevels(this.guiGameSettings.mipmapLevels);
         this.minecraft.getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
         this.minecraft.getTextureMap().setBlurMipmapDirect(false, this.guiGameSettings.mipmapLevels > 0);
         this.minecraft.func_213245_w();
      }

      this.minecraft.gameSettings.saveOptions();
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      int i = this.guiGameSettings.guiScale;
      if (super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
         if (this.guiGameSettings.guiScale != i) {
            this.minecraft.func_213226_a();
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
      int i = this.guiGameSettings.guiScale;
      if (super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_)) {
         return true;
      } else if (this.optionsRowList.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_)) {
         if (this.guiGameSettings.guiScale != i) {
            this.minecraft.func_213226_a();
         }

         return true;
      } else {
         return false;
      }
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.optionsRowList.render(p_render_1_, p_render_2_, p_render_3_);
      this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 5, 16777215);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}