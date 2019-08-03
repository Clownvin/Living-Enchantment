package net.minecraft.client.gui.widget;

import net.minecraft.client.GameSettings;
import net.minecraft.client.settings.AbstractOption;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OptionSlider extends AbstractSlider {
   private final SliderPercentageOption option;

   public OptionSlider(GameSettings p_i51129_1_, int p_i51129_2_, int p_i51129_3_, int p_i51129_4_, int p_i51129_5_, SliderPercentageOption p_i51129_6_) {
      super(p_i51129_1_, p_i51129_2_, p_i51129_3_, p_i51129_4_, p_i51129_5_, (double)((float)p_i51129_6_.func_216726_a(p_i51129_6_.get(p_i51129_1_))));
      this.option = p_i51129_6_;
      this.updateMessage();
   }

   public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
      if (this.option == AbstractOption.FULLSCREEN_RESOLUTION) {
         this.updateMessage();
      }

      super.renderButton(p_renderButton_1_, p_renderButton_2_, p_renderButton_3_);
   }

   protected void applyValue() {
      this.option.set(this.options, this.option.func_216725_b(this.value));
      this.options.saveOptions();
   }

   protected void updateMessage() {
      this.setMessage(this.option.func_216730_c(this.options));
   }
}