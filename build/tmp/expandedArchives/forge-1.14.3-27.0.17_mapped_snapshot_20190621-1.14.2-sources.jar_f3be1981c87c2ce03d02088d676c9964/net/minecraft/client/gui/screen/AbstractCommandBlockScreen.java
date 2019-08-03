package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.SuggestionContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractCommandBlockScreen extends Screen {
   protected TextFieldWidget commandTextField;
   protected TextFieldWidget resultTextField;
   protected Button doneButton;
   protected Button cancelButton;
   protected Button trackOutputButton;
   protected boolean field_195238_s;
   protected final List<String> field_209111_t = Lists.newArrayList();
   protected int field_209112_u;
   protected int field_209113_v;
   protected ParseResults<ISuggestionProvider> field_209114_w;
   protected CompletableFuture<Suggestions> field_209115_x;
   protected AbstractCommandBlockScreen.SuggestionsList suggestionList;
   private boolean field_212342_z;

   public AbstractCommandBlockScreen() {
      super(NarratorChatListener.field_216868_a);
   }

   public void tick() {
      this.commandTextField.tick();
   }

   abstract CommandBlockLogic getLogic();

   abstract int func_195236_i();

   protected void init() {
      this.minecraft.keyboardListener.enableRepeatEvents(true);
      this.doneButton = this.addButton(new Button(this.width / 2 - 4 - 150, this.height / 4 + 120 + 12, 150, 20, I18n.format("gui.done"), (p_214187_1_) -> {
         this.func_195234_k();
      }));
      this.cancelButton = this.addButton(new Button(this.width / 2 + 4, this.height / 4 + 120 + 12, 150, 20, I18n.format("gui.cancel"), (p_214186_1_) -> {
         this.onClose();
      }));
      this.trackOutputButton = this.addButton(new Button(this.width / 2 + 150 - 20, this.func_195236_i(), 20, 20, "O", (p_214184_1_) -> {
         CommandBlockLogic commandblocklogic = this.getLogic();
         commandblocklogic.setTrackOutput(!commandblocklogic.shouldTrackOutput());
         this.updateTrackOutput();
      }));
      this.commandTextField = new TextFieldWidget(this.font, this.width / 2 - 150, 50, 300, 20, I18n.format("advMode.command"));
      this.commandTextField.setMaxStringLength(32500);
      this.commandTextField.setTextFormatter(this::formatCommand);
      this.commandTextField.func_212954_a(this::func_214185_b);
      this.children.add(this.commandTextField);
      this.resultTextField = new TextFieldWidget(this.font, this.width / 2 - 150, this.func_195236_i(), 276, 20, I18n.format("advMode.previousOutput"));
      this.resultTextField.setMaxStringLength(32500);
      this.resultTextField.setEnabled(false);
      this.resultTextField.setText("-");
      this.children.add(this.resultTextField);
      this.func_212928_a(this.commandTextField);
      this.commandTextField.setFocused2(true);
      this.computeSuggestions();
   }

   public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
      String s = this.commandTextField.getText();
      this.init(p_resize_1_, p_resize_2_, p_resize_3_);
      this.setCommand(s);
      this.computeSuggestions();
   }

   protected void updateTrackOutput() {
      if (this.getLogic().shouldTrackOutput()) {
         this.trackOutputButton.setMessage("O");
         this.resultTextField.setText(this.getLogic().getLastOutput().getString());
      } else {
         this.trackOutputButton.setMessage("X");
         this.resultTextField.setText("-");
      }

   }

   protected void func_195234_k() {
      CommandBlockLogic commandblocklogic = this.getLogic();
      this.func_195235_a(commandblocklogic);
      if (!commandblocklogic.shouldTrackOutput()) {
         commandblocklogic.setLastOutput((ITextComponent)null);
      }

      this.minecraft.displayGuiScreen((Screen)null);
   }

   public void removed() {
      this.minecraft.keyboardListener.enableRepeatEvents(false);
   }

   protected abstract void func_195235_a(CommandBlockLogic p_195235_1_);

   public void onClose() {
      this.getLogic().setTrackOutput(this.field_195238_s);
      this.minecraft.displayGuiScreen((Screen)null);
   }

   private void func_214185_b(String p_214185_1_) {
      this.computeSuggestions();
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (this.suggestionList != null && this.suggestionList.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
         return true;
      } else if (this.getFocused() == this.commandTextField && p_keyPressed_1_ == 258) {
         this.func_209109_s();
         return true;
      } else if (super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
         return true;
      } else if (p_keyPressed_1_ != 257 && p_keyPressed_1_ != 335) {
         if (p_keyPressed_1_ == 258 && this.getFocused() == this.commandTextField) {
            this.func_209109_s();
         }

         return false;
      } else {
         this.func_195234_k();
         return true;
      }
   }

   public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
      return this.suggestionList != null && this.suggestionList.mouseScrolled(MathHelper.clamp(p_mouseScrolled_5_, -1.0D, 1.0D)) ? true : super.mouseScrolled(p_mouseScrolled_1_, p_mouseScrolled_3_, p_mouseScrolled_5_);
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      return this.suggestionList != null && this.suggestionList.mouseClicked((int)p_mouseClicked_1_, (int)p_mouseClicked_3_, p_mouseClicked_5_) ? true : super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   protected void computeSuggestions() {
      String s = this.commandTextField.getText();
      if (this.field_209114_w != null && !this.field_209114_w.getReader().getString().equals(s)) {
         this.field_209114_w = null;
      }

      if (!this.field_212342_z) {
         this.commandTextField.setSuggestion((String)null);
         this.suggestionList = null;
      }

      this.field_209111_t.clear();
      CommandDispatcher<ISuggestionProvider> commanddispatcher = this.minecraft.player.connection.func_195515_i();
      StringReader stringreader = new StringReader(s);
      if (stringreader.canRead() && stringreader.peek() == '/') {
         stringreader.skip();
      }

      int i = stringreader.getCursor();
      if (this.field_209114_w == null) {
         this.field_209114_w = commanddispatcher.parse(stringreader, this.minecraft.player.connection.getSuggestionProvider());
      }

      int j = this.commandTextField.getCursorPosition();
      if (j >= i && (this.suggestionList == null || !this.field_212342_z)) {
         this.field_209115_x = commanddispatcher.getCompletionSuggestions(this.field_209114_w, j);
         this.field_209115_x.thenRun(() -> {
            if (this.field_209115_x.isDone()) {
               this.func_209107_u();
            }
         });
      }

   }

   private void func_209107_u() {
      if (this.field_209115_x.join().isEmpty() && !this.field_209114_w.getExceptions().isEmpty() && this.commandTextField.getCursorPosition() == this.commandTextField.getText().length()) {
         int i = 0;

         for(Entry<CommandNode<ISuggestionProvider>, CommandSyntaxException> entry : this.field_209114_w.getExceptions().entrySet()) {
            CommandSyntaxException commandsyntaxexception = entry.getValue();
            if (commandsyntaxexception.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect()) {
               ++i;
            } else {
               this.field_209111_t.add(commandsyntaxexception.getMessage());
            }
         }

         if (i > 0) {
            this.field_209111_t.add(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create().getMessage());
         }
      }

      this.field_209112_u = 0;
      this.field_209113_v = this.width;
      if (this.field_209111_t.isEmpty()) {
         this.func_209108_a(TextFormatting.GRAY);
      }

      this.suggestionList = null;
      if (this.minecraft.gameSettings.autoSuggestCommands) {
         this.func_209109_s();
      }

   }

   private String formatCommand(String p_209104_1_, int p_209104_2_) {
      return this.field_209114_w != null ? ChatScreen.func_212336_a(this.field_209114_w, p_209104_1_, p_209104_2_) : p_209104_1_;
   }

   private void func_209108_a(TextFormatting p_209108_1_) {
      CommandContextBuilder<ISuggestionProvider> commandcontextbuilder = this.field_209114_w.getContext();
      SuggestionContext<ISuggestionProvider> suggestioncontext = commandcontextbuilder.findSuggestionContext(this.commandTextField.getCursorPosition());
      Map<CommandNode<ISuggestionProvider>, String> map = this.minecraft.player.connection.func_195515_i().getSmartUsage(suggestioncontext.parent, this.minecraft.player.connection.getSuggestionProvider());
      List<String> list = Lists.newArrayList();
      int i = 0;

      for(Entry<CommandNode<ISuggestionProvider>, String> entry : map.entrySet()) {
         if (!(entry.getKey() instanceof LiteralCommandNode)) {
            list.add(p_209108_1_ + (String)entry.getValue());
            i = Math.max(i, this.font.getStringWidth(entry.getValue()));
         }
      }

      if (!list.isEmpty()) {
         this.field_209111_t.addAll(list);
         this.field_209112_u = MathHelper.clamp(this.commandTextField.func_195611_j(suggestioncontext.startPos), 0, this.commandTextField.func_195611_j(0) + this.commandTextField.getAdjustedWidth() - i);
         this.field_209113_v = i;
      }

   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString(this.font, I18n.format("advMode.setCommand"), this.width / 2, 20, 16777215);
      this.drawString(this.font, I18n.format("advMode.command"), this.width / 2 - 150, 40, 10526880);
      this.commandTextField.render(p_render_1_, p_render_2_, p_render_3_);
      int i = 75;
      if (!this.resultTextField.getText().isEmpty()) {
         i = i + (5 * 9 + 1 + this.func_195236_i() - 135);
         this.drawString(this.font, I18n.format("advMode.previousOutput"), this.width / 2 - 150, i + 4, 10526880);
         this.resultTextField.render(p_render_1_, p_render_2_, p_render_3_);
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
      if (this.suggestionList != null) {
         this.suggestionList.render(p_render_1_, p_render_2_);
      } else {
         i = 0;

         for(String s : this.field_209111_t) {
            fill(this.field_209112_u - 1, 72 + 12 * i, this.field_209112_u + this.field_209113_v + 1, 84 + 12 * i, Integer.MIN_VALUE);
            this.font.drawStringWithShadow(s, (float)this.field_209112_u, (float)(74 + 12 * i), -1);
            ++i;
         }
      }

   }

   public void func_209109_s() {
      if (this.field_209115_x != null && this.field_209115_x.isDone()) {
         Suggestions suggestions = this.field_209115_x.join();
         if (!suggestions.isEmpty()) {
            int i = 0;

            for(Suggestion suggestion : suggestions.getList()) {
               i = Math.max(i, this.font.getStringWidth(suggestion.getText()));
            }

            int j = MathHelper.clamp(this.commandTextField.func_195611_j(suggestions.getRange().getStart()), 0, this.commandTextField.func_195611_j(0) + this.commandTextField.getAdjustedWidth() - i);
            this.suggestionList = new AbstractCommandBlockScreen.SuggestionsList(j, 72, i, suggestions);
         }
      }

   }

   protected void setCommand(String p_209102_1_) {
      this.commandTextField.setText(p_209102_1_);
   }

   @Nullable
   private static String func_212339_b(String p_212339_0_, String p_212339_1_) {
      return p_212339_1_.startsWith(p_212339_0_) ? p_212339_1_.substring(p_212339_0_.length()) : null;
   }

   @OnlyIn(Dist.CLIENT)
   class SuggestionsList {
      private final Rectangle2d bounds;
      private final Suggestions suggestions;
      private final String field_212467_d;
      private int scrollAmount;
      private int selectedSuggestion;
      private Vec2f lastMousePosition = Vec2f.ZERO;
      private boolean field_209141_h;

      private SuggestionsList(int p_i49843_2_, int p_i49843_3_, int p_i49843_4_, Suggestions p_i49843_5_) {
         this.bounds = new Rectangle2d(p_i49843_2_ - 1, p_i49843_3_, p_i49843_4_ + 1, Math.min(p_i49843_5_.getList().size(), 7) * 12);
         this.suggestions = p_i49843_5_;
         this.field_212467_d = AbstractCommandBlockScreen.this.commandTextField.getText();
         this.suggest(0);
      }

      public void render(int mouseX, int mouseY) {
         int i = Math.min(this.suggestions.getList().size(), 7);
         int j = Integer.MIN_VALUE;
         int k = -5592406;
         boolean flag = this.scrollAmount > 0;
         boolean flag1 = this.suggestions.getList().size() > this.scrollAmount + i;
         boolean flag2 = flag || flag1;
         boolean flag3 = this.lastMousePosition.x != (float)mouseX || this.lastMousePosition.y != (float)mouseY;
         if (flag3) {
            this.lastMousePosition = new Vec2f((float)mouseX, (float)mouseY);
         }

         if (flag2) {
            AbstractGui.fill(this.bounds.getX(), this.bounds.getY() - 1, this.bounds.getX() + this.bounds.getWidth(), this.bounds.getY(), Integer.MIN_VALUE);
            AbstractGui.fill(this.bounds.getX(), this.bounds.getY() + this.bounds.getHeight(), this.bounds.getX() + this.bounds.getWidth(), this.bounds.getY() + this.bounds.getHeight() + 1, Integer.MIN_VALUE);
            if (flag) {
               for(int l = 0; l < this.bounds.getWidth(); ++l) {
                  if (l % 2 == 0) {
                     AbstractGui.fill(this.bounds.getX() + l, this.bounds.getY() - 1, this.bounds.getX() + l + 1, this.bounds.getY(), -1);
                  }
               }
            }

            if (flag1) {
               for(int j1 = 0; j1 < this.bounds.getWidth(); ++j1) {
                  if (j1 % 2 == 0) {
                     AbstractGui.fill(this.bounds.getX() + j1, this.bounds.getY() + this.bounds.getHeight(), this.bounds.getX() + j1 + 1, this.bounds.getY() + this.bounds.getHeight() + 1, -1);
                  }
               }
            }
         }

         boolean flag4 = false;

         for(int i1 = 0; i1 < i; ++i1) {
            Suggestion suggestion = this.suggestions.getList().get(i1 + this.scrollAmount);
            AbstractGui.fill(this.bounds.getX(), this.bounds.getY() + 12 * i1, this.bounds.getX() + this.bounds.getWidth(), this.bounds.getY() + 12 * i1 + 12, Integer.MIN_VALUE);
            if (mouseX > this.bounds.getX() && mouseX < this.bounds.getX() + this.bounds.getWidth() && mouseY > this.bounds.getY() + 12 * i1 && mouseY < this.bounds.getY() + 12 * i1 + 12) {
               if (flag3) {
                  this.suggest(i1 + this.scrollAmount);
               }

               flag4 = true;
            }

            AbstractCommandBlockScreen.this.font.drawStringWithShadow(suggestion.getText(), (float)(this.bounds.getX() + 1), (float)(this.bounds.getY() + 2 + 12 * i1), i1 + this.scrollAmount == this.selectedSuggestion ? -256 : -5592406);
         }

         if (flag4) {
            Message message = this.suggestions.getList().get(this.selectedSuggestion).getTooltip();
            if (message != null) {
               AbstractCommandBlockScreen.this.renderTooltip(TextComponentUtils.toTextComponent(message).getFormattedText(), mouseX, mouseY);
            }
         }

      }

      public boolean mouseClicked(int mouseX, int mouseY, int button) {
         if (!this.bounds.contains(mouseX, mouseY)) {
            return false;
         } else {
            int i = (mouseY - this.bounds.getY()) / 12 + this.scrollAmount;
            if (i >= 0 && i < this.suggestions.getList().size()) {
               this.suggest(i);
               this.applySuggestion();
            }

            return true;
         }
      }

      public boolean mouseScrolled(double amount) {
         int i = (int)(AbstractCommandBlockScreen.this.minecraft.mouseHelper.getMouseX() * (double)AbstractCommandBlockScreen.this.minecraft.mainWindow.getScaledWidth() / (double)AbstractCommandBlockScreen.this.minecraft.mainWindow.getWidth());
         int j = (int)(AbstractCommandBlockScreen.this.minecraft.mouseHelper.getMouseY() * (double)AbstractCommandBlockScreen.this.minecraft.mainWindow.getScaledHeight() / (double)AbstractCommandBlockScreen.this.minecraft.mainWindow.getHeight());
         if (this.bounds.contains(i, j)) {
            this.scrollAmount = MathHelper.clamp((int)((double)this.scrollAmount - amount), 0, Math.max(this.suggestions.getList().size() - 7, 0));
            return true;
         } else {
            return false;
         }
      }

      public boolean keyPressed(int p_209133_1_, int p_209133_2_, int p_209133_3_) {
         if (p_209133_1_ == 265) {
            this.func_209128_a(-1);
            this.field_209141_h = false;
            return true;
         } else if (p_209133_1_ == 264) {
            this.func_209128_a(1);
            this.field_209141_h = false;
            return true;
         } else if (p_209133_1_ == 258) {
            if (this.field_209141_h) {
               this.func_209128_a(Screen.hasShiftDown() ? -1 : 1);
            }

            this.applySuggestion();
            return true;
         } else if (p_209133_1_ == 256) {
            this.func_209132_b();
            return true;
         } else {
            return false;
         }
      }

      public void func_209128_a(int p_209128_1_) {
         this.suggest(this.selectedSuggestion + p_209128_1_);
         int i = this.scrollAmount;
         int j = this.scrollAmount + 7 - 1;
         if (this.selectedSuggestion < i) {
            this.scrollAmount = MathHelper.clamp(this.selectedSuggestion, 0, Math.max(this.suggestions.getList().size() - 7, 0));
         } else if (this.selectedSuggestion > j) {
            this.scrollAmount = MathHelper.clamp(this.selectedSuggestion - 7, 0, Math.max(this.suggestions.getList().size() - 7, 0));
         }

      }

      public void suggest(int suggestionIndex) {
         this.selectedSuggestion = suggestionIndex;
         if (this.selectedSuggestion < 0) {
            this.selectedSuggestion += this.suggestions.getList().size();
         }

         if (this.selectedSuggestion >= this.suggestions.getList().size()) {
            this.selectedSuggestion -= this.suggestions.getList().size();
         }

         Suggestion suggestion = this.suggestions.getList().get(this.selectedSuggestion);
         AbstractCommandBlockScreen.this.commandTextField.setSuggestion(AbstractCommandBlockScreen.func_212339_b(AbstractCommandBlockScreen.this.commandTextField.getText(), suggestion.apply(this.field_212467_d)));
      }

      public void applySuggestion() {
         Suggestion suggestion = this.suggestions.getList().get(this.selectedSuggestion);
         AbstractCommandBlockScreen.this.field_212342_z = true;
         AbstractCommandBlockScreen.this.setCommand(suggestion.apply(this.field_212467_d));
         int i = suggestion.getRange().getStart() + suggestion.getText().length();
         AbstractCommandBlockScreen.this.commandTextField.func_212422_f(i);
         AbstractCommandBlockScreen.this.commandTextField.setSelectionPos(i);
         this.suggest(this.selectedSuggestion);
         AbstractCommandBlockScreen.this.field_212342_z = false;
         this.field_209141_h = true;
      }

      public void func_209132_b() {
         AbstractCommandBlockScreen.this.suggestionList = null;
      }
   }
}