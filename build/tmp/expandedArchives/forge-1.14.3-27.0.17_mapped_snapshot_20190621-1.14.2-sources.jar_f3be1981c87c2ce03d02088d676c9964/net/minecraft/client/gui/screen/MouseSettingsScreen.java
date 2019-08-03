package net.minecraft.client.gui.screen;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.AbstractOption;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MouseSettingsScreen extends Screen {
   private final Screen field_213044_a;
   private OptionsRowList field_213045_b;
   private static final AbstractOption[] OPTIONS = new AbstractOption[]{AbstractOption.SENSITIVITY, AbstractOption.INVERT_MOUSE, AbstractOption.MOUSE_WHEEL_SENSITIVITY, AbstractOption.DISCRETE_MOUSE_SCROLL, AbstractOption.TOUCHSCREEN};

   public MouseSettingsScreen(Screen p_i51111_1_) {
      super(new TranslationTextComponent("options.mouse_settings.title"));
      this.field_213044_a = p_i51111_1_;
   }

   protected void init() {
      this.field_213045_b = new OptionsRowList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
      this.field_213045_b.func_214335_a(OPTIONS);
      this.children.add(this.field_213045_b);
      this.addButton(new Button(this.width / 2 - 100, this.height - 27, 200, 20, I18n.format("gui.done"), (p_213043_1_) -> {
         this.minecraft.gameSettings.saveOptions();
         this.minecraft.displayGuiScreen(this.field_213044_a);
      }));
   }

   public void removed() {
      this.minecraft.gameSettings.saveOptions();
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.field_213045_b.render(p_render_1_, p_render_2_, p_render_3_);
      this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 5, 16777215);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}