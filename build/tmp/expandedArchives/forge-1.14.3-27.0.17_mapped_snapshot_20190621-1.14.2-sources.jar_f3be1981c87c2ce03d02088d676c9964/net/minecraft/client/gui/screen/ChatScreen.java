package net.minecraft.client.gui.screen;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.SuggestionContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChatScreen extends Screen {
   private static final Pattern WHITESPACE_PATTERN = Pattern.compile("(\\s+)");
   private String historyBuffer = "";
   private int sentHistoryCursor = -1;
   protected TextFieldWidget inputField;
   private String defaultInputFieldText = "";
   protected final List<String> commandUsage = Lists.newArrayList();
   protected int commandUsagePosition;
   protected int commandUsageWidth;
   private ParseResults<ISuggestionProvider> currentParse;
   private CompletableFuture<Suggestions> pendingSuggestions;
   private ChatScreen.SuggestionsList suggestions;
   private boolean hasEdits;
   private boolean field_212338_z;

   public ChatScreen(String defaultText) {
      super(NarratorChatListener.field_216868_a);
      this.defaultInputFieldText = defaultText;
   }

   protected void init() {
      this.minecraft.keyboardListener.enableRepeatEvents(true);
      this.sentHistoryCursor = this.minecraft.ingameGUI.getChatGUI().getSentMessages().size();
      this.inputField = new TextFieldWidget(this.font, 4, this.height - 12, this.width - 4, 12, I18n.format("chat.editBox"));
      this.inputField.setMaxStringLength(256);
      this.inputField.setEnableBackgroundDrawing(false);
      this.inputField.setText(this.defaultInputFieldText);
      this.inputField.setTextFormatter(this::formatMessage);
      this.inputField.func_212954_a(this::func_212997_a);
      this.children.add(this.inputField);
      this.updateSuggestion();
      this.func_212928_a(this.inputField);
   }

   public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
      String s = this.inputField.getText();
      this.init(p_resize_1_, p_resize_2_, p_resize_3_);
      this.setChatLine(s);
      this.updateSuggestion();
   }

   public void removed() {
      this.minecraft.keyboardListener.enableRepeatEvents(false);
      this.minecraft.ingameGUI.getChatGUI().resetScroll();
   }

   public void tick() {
      this.inputField.tick();
   }

   private void func_212997_a(String p_212997_1_) {
      String s = this.inputField.getText();
      this.hasEdits = !s.equals(this.defaultInputFieldText);
      this.updateSuggestion();
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (this.suggestions != null && this.suggestions.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
         return true;
      } else {
         if (p_keyPressed_1_ == 258) {
            this.hasEdits = true;
            this.showSuggestions();
         }

         if (super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
            return true;
         } else if (p_keyPressed_1_ == 256) {
            this.minecraft.displayGuiScreen((Screen)null);
            return true;
         } else if (p_keyPressed_1_ != 257 && p_keyPressed_1_ != 335) {
            if (p_keyPressed_1_ == 265) {
               this.getSentHistory(-1);
               return true;
            } else if (p_keyPressed_1_ == 264) {
               this.getSentHistory(1);
               return true;
            } else if (p_keyPressed_1_ == 266) {
               this.minecraft.ingameGUI.getChatGUI().func_194813_a((double)(this.minecraft.ingameGUI.getChatGUI().getLineCount() - 1));
               return true;
            } else if (p_keyPressed_1_ == 267) {
               this.minecraft.ingameGUI.getChatGUI().func_194813_a((double)(-this.minecraft.ingameGUI.getChatGUI().getLineCount() + 1));
               return true;
            } else {
               return false;
            }
         } else {
            String s = this.inputField.getText().trim();
            if (!s.isEmpty()) {
               this.sendMessage(s);
            }

            this.minecraft.displayGuiScreen((Screen)null);
            return true;
         }
      }
   }

   public void showSuggestions() {
      if (this.pendingSuggestions != null && this.pendingSuggestions.isDone()) {
         int i = 0;
         Suggestions suggestions = this.pendingSuggestions.join();
         if (!suggestions.getList().isEmpty()) {
            for(Suggestion suggestion : suggestions.getList()) {
               i = Math.max(i, this.font.getStringWidth(suggestion.getText()));
            }

            int j = MathHelper.clamp(this.inputField.func_195611_j(suggestions.getRange().getStart()), 0, this.width - i);
            this.suggestions = new ChatScreen.SuggestionsList(j, this.height - 12, i, suggestions);
         }
      }

   }

   private static int getLastWordIndex(String p_208603_0_) {
      if (Strings.isNullOrEmpty(p_208603_0_)) {
         return 0;
      } else {
         int i = 0;

         for(Matcher matcher = WHITESPACE_PATTERN.matcher(p_208603_0_); matcher.find(); i = matcher.end()) {
            ;
         }

         return i;
      }
   }

   private void updateSuggestion() {
      String s = this.inputField.getText();
      if (this.currentParse != null && !this.currentParse.getReader().getString().equals(s)) {
         this.currentParse = null;
      }

      if (!this.field_212338_z) {
         this.inputField.setSuggestion((String)null);
         this.suggestions = null;
      }

      this.commandUsage.clear();
      StringReader stringreader = new StringReader(s);
      if (stringreader.canRead() && stringreader.peek() == '/') {
         stringreader.skip();
         CommandDispatcher<ISuggestionProvider> commanddispatcher = this.minecraft.player.connection.func_195515_i();
         if (this.currentParse == null) {
            this.currentParse = commanddispatcher.parse(stringreader, this.minecraft.player.connection.getSuggestionProvider());
         }

         int j = this.inputField.getCursorPosition();
         if (j >= 1 && (this.suggestions == null || !this.field_212338_z)) {
            this.pendingSuggestions = commanddispatcher.getCompletionSuggestions(this.currentParse, j);
            this.pendingSuggestions.thenRun(() -> {
               if (this.pendingSuggestions.isDone()) {
                  this.updateUsageInfo();
               }
            });
         }
      } else {
         int i = getLastWordIndex(s);
         Collection<String> collection = this.minecraft.player.connection.getSuggestionProvider().getPlayerNames();
         this.pendingSuggestions = ISuggestionProvider.suggest(collection, new SuggestionsBuilder(s, i));
      }

   }

   private void updateUsageInfo() {
      if (this.pendingSuggestions.join().isEmpty() && !this.currentParse.getExceptions().isEmpty() && this.inputField.getCursorPosition() == this.inputField.getText().length()) {
         int i = 0;

         for(Entry<CommandNode<ISuggestionProvider>, CommandSyntaxException> entry : this.currentParse.getExceptions().entrySet()) {
            CommandSyntaxException commandsyntaxexception = entry.getValue();
            if (commandsyntaxexception.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect()) {
               ++i;
            } else {
               this.commandUsage.add(commandsyntaxexception.getMessage());
            }
         }

         if (i > 0) {
            this.commandUsage.add(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create().getMessage());
         }
      }

      this.commandUsagePosition = 0;
      this.commandUsageWidth = this.width;
      if (this.commandUsage.isEmpty()) {
         this.fillNodeUsage(TextFormatting.GRAY);
      }

      this.suggestions = null;
      if (this.hasEdits && this.minecraft.gameSettings.autoSuggestCommands) {
         this.showSuggestions();
      }

   }

   private String formatMessage(String p_195130_1_, int p_195130_2_) {
      return this.currentParse != null ? func_212336_a(this.currentParse, p_195130_1_, p_195130_2_) : p_195130_1_;
   }

   public static String func_212336_a(ParseResults<ISuggestionProvider> p_212336_0_, String p_212336_1_, int p_212336_2_) {
      TextFormatting[] atextformatting = new TextFormatting[]{TextFormatting.AQUA, TextFormatting.YELLOW, TextFormatting.GREEN, TextFormatting.LIGHT_PURPLE, TextFormatting.GOLD};
      String s = TextFormatting.GRAY.toString();
      StringBuilder stringbuilder = new StringBuilder(s);
      int i = 0;
      int j = -1;
      CommandContextBuilder<ISuggestionProvider> commandcontextbuilder = p_212336_0_.getContext().getLastChild();

      for(ParsedArgument<ISuggestionProvider, ?> parsedargument : commandcontextbuilder.getArguments().values()) {
         ++j;
         if (j >= atextformatting.length) {
            j = 0;
         }

         int k = Math.max(parsedargument.getRange().getStart() - p_212336_2_, 0);
         if (k >= p_212336_1_.length()) {
            break;
         }

         int l = Math.min(parsedargument.getRange().getEnd() - p_212336_2_, p_212336_1_.length());
         if (l > 0) {
            stringbuilder.append((CharSequence)p_212336_1_, i, k);
            stringbuilder.append((Object)atextformatting[j]);
            stringbuilder.append((CharSequence)p_212336_1_, k, l);
            stringbuilder.append(s);
            i = l;
         }
      }

      if (p_212336_0_.getReader().canRead()) {
         int i1 = Math.max(p_212336_0_.getReader().getCursor() - p_212336_2_, 0);
         if (i1 < p_212336_1_.length()) {
            int j1 = Math.min(i1 + p_212336_0_.getReader().getRemainingLength(), p_212336_1_.length());
            stringbuilder.append((CharSequence)p_212336_1_, i, i1);
            stringbuilder.append((Object)TextFormatting.RED);
            stringbuilder.append((CharSequence)p_212336_1_, i1, j1);
            i = j1;
         }
      }

      stringbuilder.append((CharSequence)p_212336_1_, i, p_212336_1_.length());
      return stringbuilder.toString();
   }

   public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
      if (p_mouseScrolled_5_ > 1.0D) {
         p_mouseScrolled_5_ = 1.0D;
      }

      if (p_mouseScrolled_5_ < -1.0D) {
         p_mouseScrolled_5_ = -1.0D;
      }

      if (this.suggestions != null && this.suggestions.mouseScrolled(p_mouseScrolled_5_)) {
         return true;
      } else {
         if (!hasShiftDown()) {
            p_mouseScrolled_5_ *= 7.0D;
         }

         this.minecraft.ingameGUI.getChatGUI().func_194813_a(p_mouseScrolled_5_);
         return true;
      }
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (this.suggestions != null && this.suggestions.mouseClicked((int)p_mouseClicked_1_, (int)p_mouseClicked_3_, p_mouseClicked_5_)) {
         return true;
      } else {
         if (p_mouseClicked_5_ == 0) {
            ITextComponent itextcomponent = this.minecraft.ingameGUI.getChatGUI().getTextComponent(p_mouseClicked_1_, p_mouseClicked_3_);
            if (itextcomponent != null && this.handleComponentClicked(itextcomponent)) {
               return true;
            }
         }

         return this.inputField.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_) ? true : super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
      }
   }

   protected void insertText(String p_insertText_1_, boolean p_insertText_2_) {
      if (p_insertText_2_) {
         this.inputField.setText(p_insertText_1_);
      } else {
         this.inputField.writeText(p_insertText_1_);
      }

   }

   /**
    * input is relative and is applied directly to the sentHistoryCursor so -1 is the previous message, 1 is the next
    * message from the current cursor position
    */
   public void getSentHistory(int msgPos) {
      int i = this.sentHistoryCursor + msgPos;
      int j = this.minecraft.ingameGUI.getChatGUI().getSentMessages().size();
      i = MathHelper.clamp(i, 0, j);
      if (i != this.sentHistoryCursor) {
         if (i == j) {
            this.sentHistoryCursor = j;
            this.inputField.setText(this.historyBuffer);
         } else {
            if (this.sentHistoryCursor == j) {
               this.historyBuffer = this.inputField.getText();
            }

            this.inputField.setText(this.minecraft.ingameGUI.getChatGUI().getSentMessages().get(i));
            this.suggestions = null;
            this.sentHistoryCursor = i;
            this.hasEdits = false;
         }
      }
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.setFocused(this.inputField);
      this.inputField.setFocused2(true);
      fill(2, this.height - 14, this.width - 2, this.height - 2, this.minecraft.gameSettings.func_216839_a(Integer.MIN_VALUE));
      this.inputField.render(p_render_1_, p_render_2_, p_render_3_);
      if (this.suggestions != null) {
         this.suggestions.render(p_render_1_, p_render_2_);
      } else {
         int i = 0;

         for(String s : this.commandUsage) {
            fill(this.commandUsagePosition - 1, this.height - 14 - 13 - 12 * i, this.commandUsagePosition + this.commandUsageWidth + 1, this.height - 2 - 13 - 12 * i, -16777216);
            this.font.drawStringWithShadow(s, (float)this.commandUsagePosition, (float)(this.height - 14 - 13 + 2 - 12 * i), -1);
            ++i;
         }
      }

      ITextComponent itextcomponent = this.minecraft.ingameGUI.getChatGUI().getTextComponent((double)p_render_1_, (double)p_render_2_);
      if (itextcomponent != null && itextcomponent.getStyle().getHoverEvent() != null) {
         this.renderComponentHoverEffect(itextcomponent, p_render_1_, p_render_2_);
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   public boolean isPauseScreen() {
      return false;
   }

   private void fillNodeUsage(TextFormatting p_195132_1_) {
      CommandContextBuilder<ISuggestionProvider> commandcontextbuilder = this.currentParse.getContext();
      SuggestionContext<ISuggestionProvider> suggestioncontext = commandcontextbuilder.findSuggestionContext(this.inputField.getCursorPosition());
      Map<CommandNode<ISuggestionProvider>, String> map = this.minecraft.player.connection.func_195515_i().getSmartUsage(suggestioncontext.parent, this.minecraft.player.connection.getSuggestionProvider());
      List<String> list = Lists.newArrayList();
      int i = 0;

      for(Entry<CommandNode<ISuggestionProvider>, String> entry : map.entrySet()) {
         if (!(entry.getKey() instanceof LiteralCommandNode)) {
            list.add(p_195132_1_ + (String)entry.getValue());
            i = Math.max(i, this.font.getStringWidth(entry.getValue()));
         }
      }

      if (!list.isEmpty()) {
         this.commandUsage.addAll(list);
         this.commandUsagePosition = MathHelper.clamp(this.inputField.func_195611_j(suggestioncontext.startPos), 0, this.width - i);
         this.commandUsageWidth = i;
      }

   }

   @Nullable
   private static String calculateSuggestionSuffix(String p_208602_0_, String p_208602_1_) {
      return p_208602_1_.startsWith(p_208602_0_) ? p_208602_1_.substring(p_208602_0_.length()) : null;
   }

   private void setChatLine(String p_208604_1_) {
      this.inputField.setText(p_208604_1_);
   }

   @OnlyIn(Dist.CLIENT)
   class SuggestionsList {
      private final Rectangle2d field_198505_b;
      private final Suggestions suggestions;
      private final String field_212466_d;
      private int field_198507_d;
      private int field_198508_e;
      private Vec2f field_198509_f = Vec2f.ZERO;
      private boolean field_199880_h;

      private SuggestionsList(int p_i47700_2_, int p_i47700_3_, int p_i47700_4_, Suggestions p_i47700_5_) {
         this.field_198505_b = new Rectangle2d(p_i47700_2_ - 1, p_i47700_3_ - 3 - Math.min(p_i47700_5_.getList().size(), 10) * 12, p_i47700_4_ + 1, Math.min(p_i47700_5_.getList().size(), 10) * 12);
         this.suggestions = p_i47700_5_;
         this.field_212466_d = ChatScreen.this.inputField.getText();
         this.select(0);
      }

      public void render(int p_198500_1_, int p_198500_2_) {
         int i = Math.min(this.suggestions.getList().size(), 10);
         int j = -5592406;
         boolean flag = this.field_198507_d > 0;
         boolean flag1 = this.suggestions.getList().size() > this.field_198507_d + i;
         boolean flag2 = flag || flag1;
         boolean flag3 = this.field_198509_f.x != (float)p_198500_1_ || this.field_198509_f.y != (float)p_198500_2_;
         if (flag3) {
            this.field_198509_f = new Vec2f((float)p_198500_1_, (float)p_198500_2_);
         }

         if (flag2) {
            AbstractGui.fill(this.field_198505_b.getX(), this.field_198505_b.getY() - 1, this.field_198505_b.getX() + this.field_198505_b.getWidth(), this.field_198505_b.getY(), -805306368);
            AbstractGui.fill(this.field_198505_b.getX(), this.field_198505_b.getY() + this.field_198505_b.getHeight(), this.field_198505_b.getX() + this.field_198505_b.getWidth(), this.field_198505_b.getY() + this.field_198505_b.getHeight() + 1, -805306368);
            if (flag) {
               for(int k = 0; k < this.field_198505_b.getWidth(); ++k) {
                  if (k % 2 == 0) {
                     AbstractGui.fill(this.field_198505_b.getX() + k, this.field_198505_b.getY() - 1, this.field_198505_b.getX() + k + 1, this.field_198505_b.getY(), -1);
                  }
               }
            }

            if (flag1) {
               for(int i1 = 0; i1 < this.field_198505_b.getWidth(); ++i1) {
                  if (i1 % 2 == 0) {
                     AbstractGui.fill(this.field_198505_b.getX() + i1, this.field_198505_b.getY() + this.field_198505_b.getHeight(), this.field_198505_b.getX() + i1 + 1, this.field_198505_b.getY() + this.field_198505_b.getHeight() + 1, -1);
                  }
               }
            }
         }

         boolean flag4 = false;

         for(int l = 0; l < i; ++l) {
            Suggestion suggestion = this.suggestions.getList().get(l + this.field_198507_d);
            AbstractGui.fill(this.field_198505_b.getX(), this.field_198505_b.getY() + 12 * l, this.field_198505_b.getX() + this.field_198505_b.getWidth(), this.field_198505_b.getY() + 12 * l + 12, -805306368);
            if (p_198500_1_ > this.field_198505_b.getX() && p_198500_1_ < this.field_198505_b.getX() + this.field_198505_b.getWidth() && p_198500_2_ > this.field_198505_b.getY() + 12 * l && p_198500_2_ < this.field_198505_b.getY() + 12 * l + 12) {
               if (flag3) {
                  this.select(l + this.field_198507_d);
               }

               flag4 = true;
            }

            ChatScreen.this.font.drawStringWithShadow(suggestion.getText(), (float)(this.field_198505_b.getX() + 1), (float)(this.field_198505_b.getY() + 2 + 12 * l), l + this.field_198507_d == this.field_198508_e ? -256 : -5592406);
         }

         if (flag4) {
            Message message = this.suggestions.getList().get(this.field_198508_e).getTooltip();
            if (message != null) {
               ChatScreen.this.renderTooltip(TextComponentUtils.toTextComponent(message).getFormattedText(), p_198500_1_, p_198500_2_);
            }
         }

      }

      public boolean mouseClicked(int p_198499_1_, int p_198499_2_, int p_198499_3_) {
         if (!this.field_198505_b.contains(p_198499_1_, p_198499_2_)) {
            return false;
         } else {
            int i = (p_198499_2_ - this.field_198505_b.getY()) / 12 + this.field_198507_d;
            if (i >= 0 && i < this.suggestions.getList().size()) {
               this.select(i);
               this.useSuggestion();
            }

            return true;
         }
      }

      public boolean mouseScrolled(double p_198498_1_) {
         int i = (int)(ChatScreen.this.minecraft.mouseHelper.getMouseX() * (double)ChatScreen.this.minecraft.mainWindow.getScaledWidth() / (double)ChatScreen.this.minecraft.mainWindow.getWidth());
         int j = (int)(ChatScreen.this.minecraft.mouseHelper.getMouseY() * (double)ChatScreen.this.minecraft.mainWindow.getScaledHeight() / (double)ChatScreen.this.minecraft.mainWindow.getHeight());
         if (this.field_198505_b.contains(i, j)) {
            this.field_198507_d = MathHelper.clamp((int)((double)this.field_198507_d - p_198498_1_), 0, Math.max(this.suggestions.getList().size() - 10, 0));
            return true;
         } else {
            return false;
         }
      }

      public boolean keyPressed(int p_198503_1_, int p_198503_2_, int p_198503_3_) {
         if (p_198503_1_ == 265) {
            this.cycle(-1);
            this.field_199880_h = false;
            return true;
         } else if (p_198503_1_ == 264) {
            this.cycle(1);
            this.field_199880_h = false;
            return true;
         } else if (p_198503_1_ == 258) {
            if (this.field_199880_h) {
               this.cycle(Screen.hasShiftDown() ? -1 : 1);
            }

            this.useSuggestion();
            return true;
         } else if (p_198503_1_ == 256) {
            this.hide();
            return true;
         } else {
            return false;
         }
      }

      public void cycle(int p_199879_1_) {
         this.select(this.field_198508_e + p_199879_1_);
         int i = this.field_198507_d;
         int j = this.field_198507_d + 10 - 1;
         if (this.field_198508_e < i) {
            this.field_198507_d = MathHelper.clamp(this.field_198508_e, 0, Math.max(this.suggestions.getList().size() - 10, 0));
         } else if (this.field_198508_e > j) {
            this.field_198507_d = MathHelper.clamp(this.field_198508_e + 1 - 10, 0, Math.max(this.suggestions.getList().size() - 10, 0));
         }

      }

      public void select(int p_199675_1_) {
         this.field_198508_e = p_199675_1_;
         if (this.field_198508_e < 0) {
            this.field_198508_e += this.suggestions.getList().size();
         }

         if (this.field_198508_e >= this.suggestions.getList().size()) {
            this.field_198508_e -= this.suggestions.getList().size();
         }

         Suggestion suggestion = this.suggestions.getList().get(this.field_198508_e);
         ChatScreen.this.inputField.setSuggestion(ChatScreen.calculateSuggestionSuffix(ChatScreen.this.inputField.getText(), suggestion.apply(this.field_212466_d)));
      }

      public void useSuggestion() {
         Suggestion suggestion = this.suggestions.getList().get(this.field_198508_e);
         ChatScreen.this.field_212338_z = true;
         ChatScreen.this.setChatLine(suggestion.apply(this.field_212466_d));
         int i = suggestion.getRange().getStart() + suggestion.getText().length();
         ChatScreen.this.inputField.func_212422_f(i);
         ChatScreen.this.inputField.setSelectionPos(i);
         this.select(this.field_198508_e);
         ChatScreen.this.field_212338_z = false;
         this.field_199880_h = true;
      }

      public void hide() {
         ChatScreen.this.suggestions = null;
      }
   }
}