package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.List;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ConfirmScreen extends Screen {
   private final ITextComponent messageLine2;
   private final List<String> listLines = Lists.newArrayList();
   protected String confirmButtonText;
   protected String cancelButtonText;
   private int ticksUntilEnable;
   protected final BooleanConsumer field_213003_c;

   public ConfirmScreen(BooleanConsumer p_i51119_1_, ITextComponent p_i51119_2_, ITextComponent p_i51119_3_) {
      this(p_i51119_1_, p_i51119_2_, p_i51119_3_, I18n.format("gui.yes"), I18n.format("gui.no"));
   }

   public ConfirmScreen(BooleanConsumer p_i51120_1_, ITextComponent p_i51120_2_, ITextComponent p_i51120_3_, String p_i51120_4_, String p_i51120_5_) {
      super(p_i51120_2_);
      this.field_213003_c = p_i51120_1_;
      this.messageLine2 = p_i51120_3_;
      this.confirmButtonText = p_i51120_4_;
      this.cancelButtonText = p_i51120_5_;
   }

   public String getNarrationMessage() {
      return super.getNarrationMessage() + ". " + this.messageLine2.getString();
   }

   protected void init() {
      super.init();
      this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 96, 150, 20, this.confirmButtonText, (p_213002_1_) -> {
         this.field_213003_c.accept(true);
      }));
      this.addButton(new Button(this.width / 2 - 155 + 160, this.height / 6 + 96, 150, 20, this.cancelButtonText, (p_213001_1_) -> {
         this.field_213003_c.accept(false);
      }));
      this.listLines.clear();
      this.listLines.addAll(this.font.listFormattedStringToWidth(this.messageLine2.getFormattedText(), this.width - 50));
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 70, 16777215);
      int i = 90;

      for(String s : this.listLines) {
         this.drawCenteredString(this.font, s, this.width / 2, i, 16777215);
         i += 9;
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   /**
    * Sets the number of ticks to wait before enabling the buttons.
    */
   public void setButtonDelay(int ticksUntilEnableIn) {
      this.ticksUntilEnable = ticksUntilEnableIn;

      for(Widget widget : this.buttons) {
         widget.active = false;
      }

   }

   public void tick() {
      super.tick();
      if (--this.ticksUntilEnable == 0) {
         for(Widget widget : this.buttons) {
            widget.active = true;
         }
      }

   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 256) {
         this.field_213003_c.accept(false);
         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }
}