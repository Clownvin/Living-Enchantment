package net.minecraft.client.gui.screen;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import java.util.Random;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.FileUtil;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;

@OnlyIn(Dist.CLIENT)
public class CreateWorldScreen extends Screen {
   private final Screen parentScreen;
   private TextFieldWidget worldNameField;
   private TextFieldWidget worldSeedField;
   private String saveDirName;
   private String gameMode = "survival";
   private String savedGameMode;
   private boolean generateStructuresEnabled = true;
   private boolean allowCheats;
   private boolean allowCheatsWasSetByUser;
   private boolean bonusChestEnabled;
   private boolean hardCoreMode;
   private boolean alreadyGenerated;
   private boolean inMoreWorldOptionsDisplay;
   private Button btnCreateWorld;
   private Button btnGameMode;
   private Button btnMoreOptions;
   private Button btnMapFeatures;
   private Button btnBonusItems;
   private Button btnMapType;
   private Button btnAllowCommands;
   private Button btnCustomizeType;
   private String gameModeDesc1;
   private String gameModeDesc2;
   private String worldSeed;
   private String worldName;
   private int selectedIndex;
   public CompoundNBT chunkProviderSettingsJson = new CompoundNBT();

   public CreateWorldScreen(Screen p_i46320_1_) {
      super(new TranslationTextComponent("selectWorld.create"));
      this.parentScreen = p_i46320_1_;
      this.worldSeed = "";
      this.worldName = I18n.format("selectWorld.newWorld");
   }

   public void tick() {
      this.worldNameField.tick();
      this.worldSeedField.tick();
   }

   protected void init() {
      this.minecraft.keyboardListener.enableRepeatEvents(true);
      this.worldNameField = new TextFieldWidget(this.font, this.width / 2 - 100, 60, 200, 20, I18n.format("selectWorld.enterName"));
      this.worldNameField.setText(this.worldName);
      this.worldNameField.func_212954_a((p_214319_1_) -> {
         this.worldName = p_214319_1_;
         this.btnCreateWorld.active = !this.worldNameField.getText().isEmpty();
         this.calcSaveDirName();
      });
      this.children.add(this.worldNameField);
      this.btnGameMode = this.addButton(new Button(this.width / 2 - 75, 115, 150, 20, I18n.format("selectWorld.gameMode"), (p_214316_1_) -> {
         if ("survival".equals(this.gameMode)) {
            if (!this.allowCheatsWasSetByUser) {
               this.allowCheats = false;
            }

            this.hardCoreMode = false;
            this.gameMode = "hardcore";
            this.hardCoreMode = true;
            this.btnAllowCommands.active = false;
            this.btnBonusItems.active = false;
            this.updateDisplayState();
         } else if ("hardcore".equals(this.gameMode)) {
            if (!this.allowCheatsWasSetByUser) {
               this.allowCheats = true;
            }

            this.hardCoreMode = false;
            this.gameMode = "creative";
            this.updateDisplayState();
            this.hardCoreMode = false;
            this.btnAllowCommands.active = true;
            this.btnBonusItems.active = true;
         } else {
            if (!this.allowCheatsWasSetByUser) {
               this.allowCheats = false;
            }

            this.gameMode = "survival";
            this.updateDisplayState();
            this.btnAllowCommands.active = true;
            this.btnBonusItems.active = true;
            this.hardCoreMode = false;
         }

         this.updateDisplayState();
      }));
      this.worldSeedField = new TextFieldWidget(this.font, this.width / 2 - 100, 60, 200, 20, I18n.format("selectWorld.enterSeed"));
      this.worldSeedField.setText(this.worldSeed);
      this.worldSeedField.func_212954_a((p_214313_1_) -> {
         this.worldSeed = this.worldSeedField.getText();
      });
      this.children.add(this.worldSeedField);
      this.btnMapFeatures = this.addButton(new Button(this.width / 2 - 155, 100, 150, 20, I18n.format("selectWorld.mapFeatures"), (p_214322_1_) -> {
         this.generateStructuresEnabled = !this.generateStructuresEnabled;
         this.updateDisplayState();
      }));
      this.btnMapFeatures.visible = false;
      this.btnMapType = this.addButton(new Button(this.width / 2 + 5, 100, 150, 20, I18n.format("selectWorld.mapType"), (p_214320_1_) -> {
         ++this.selectedIndex;
         if (this.selectedIndex >= WorldType.WORLD_TYPES.length) {
            this.selectedIndex = 0;
         }

         while(!this.canSelectCurWorldType()) {
            ++this.selectedIndex;
            if (this.selectedIndex >= WorldType.WORLD_TYPES.length) {
               this.selectedIndex = 0;
            }
         }

         this.chunkProviderSettingsJson = new CompoundNBT();
         this.updateDisplayState();
         this.showMoreWorldOptions(this.inMoreWorldOptionsDisplay);
      }));
      this.btnMapType.visible = false;
      this.btnCustomizeType = this.addButton(new Button(this.width / 2 + 5, 120, 150, 20, I18n.format("selectWorld.customizeType"), (p_214314_1_) -> {
         WorldType.WORLD_TYPES[this.selectedIndex].onCustomizeButton(this.minecraft, CreateWorldScreen.this);
      }));
      this.btnCustomizeType.visible = false;
      this.btnAllowCommands = this.addButton(new Button(this.width / 2 - 155, 151, 150, 20, I18n.format("selectWorld.allowCommands"), (p_214315_1_) -> {
         this.allowCheatsWasSetByUser = true;
         this.allowCheats = !this.allowCheats;
         this.updateDisplayState();
      }));
      this.btnAllowCommands.visible = false;
      this.btnBonusItems = this.addButton(new Button(this.width / 2 + 5, 151, 150, 20, I18n.format("selectWorld.bonusItems"), (p_214312_1_) -> {
         this.bonusChestEnabled = !this.bonusChestEnabled;
         this.updateDisplayState();
      }));
      this.btnBonusItems.visible = false;
      this.btnMoreOptions = this.addButton(new Button(this.width / 2 - 75, 187, 150, 20, I18n.format("selectWorld.moreWorldOptions"), (p_214321_1_) -> {
         this.toggleMoreWorldOptions();
      }));
      this.btnCreateWorld = this.addButton(new Button(this.width / 2 - 155, this.height - 28, 150, 20, I18n.format("selectWorld.create"), (p_214318_1_) -> {
         this.createWorld();
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height - 28, 150, 20, I18n.format("gui.cancel"), (p_214317_1_) -> {
         this.minecraft.displayGuiScreen(this.parentScreen);
      }));
      this.showMoreWorldOptions(this.inMoreWorldOptionsDisplay);
      this.func_212928_a(this.worldNameField);
      this.calcSaveDirName();
      this.updateDisplayState();
   }

   /**
    * Determine a save-directory name from the world name
    */
   private void calcSaveDirName() {
      this.saveDirName = this.worldNameField.getText().trim();
      if (this.saveDirName.length() == 0) {
         this.saveDirName = "World";
      }

      try {
         this.saveDirName = FileUtil.func_214992_a(this.minecraft.getSaveLoader().func_215781_c(), this.saveDirName, "");
      } catch (Exception var4) {
         this.saveDirName = "World";

         try {
            this.saveDirName = FileUtil.func_214992_a(this.minecraft.getSaveLoader().func_215781_c(), this.saveDirName, "");
         } catch (Exception exception) {
            throw new RuntimeException("Could not create save folder", exception);
         }
      }

   }

   /**
    * Sets displayed GUI elements according to the current settings state
    */
   private void updateDisplayState() {
      this.btnGameMode.setMessage(I18n.format("selectWorld.gameMode") + ": " + I18n.format("selectWorld.gameMode." + this.gameMode));
      this.gameModeDesc1 = I18n.format("selectWorld.gameMode." + this.gameMode + ".line1");
      this.gameModeDesc2 = I18n.format("selectWorld.gameMode." + this.gameMode + ".line2");
      this.btnMapFeatures.setMessage(I18n.format("selectWorld.mapFeatures") + ' ' + I18n.format(this.generateStructuresEnabled ? "options.on" : "options.off"));
      this.btnBonusItems.setMessage(I18n.format("selectWorld.bonusItems") + ' ' + I18n.format(this.bonusChestEnabled && !this.hardCoreMode ? "options.on" : "options.off"));
      this.btnMapType.setMessage(I18n.format("selectWorld.mapType") + ' ' + I18n.format(WorldType.WORLD_TYPES[this.selectedIndex].getTranslationKey()));
      this.btnAllowCommands.setMessage(I18n.format("selectWorld.allowCommands") + ' ' + I18n.format(this.allowCheats && !this.hardCoreMode ? "options.on" : "options.off"));
   }

   public void removed() {
      this.minecraft.keyboardListener.enableRepeatEvents(false);
   }

   private void createWorld() {
      this.minecraft.displayGuiScreen((Screen)null);
      if (!this.alreadyGenerated) {
         this.alreadyGenerated = true;
         long i = (new Random()).nextLong();
         String s = this.worldSeedField.getText();
         if (!StringUtils.isEmpty(s)) {
            try {
               long j = Long.parseLong(s);
               if (j != 0L) {
                  i = j;
               }
            } catch (NumberFormatException var6) {
               i = (long)s.hashCode();
            }
         }
         WorldType.WORLD_TYPES[this.selectedIndex].onGUICreateWorldPress();

         WorldSettings worldsettings = new WorldSettings(i, GameType.getByName(this.gameMode), this.generateStructuresEnabled, this.hardCoreMode, WorldType.WORLD_TYPES[this.selectedIndex]);
         worldsettings.setGeneratorOptions(Dynamic.convert(NBTDynamicOps.INSTANCE, JsonOps.INSTANCE, this.chunkProviderSettingsJson));
         if (this.bonusChestEnabled && !this.hardCoreMode) {
            worldsettings.enableBonusChest();
         }

         if (this.allowCheats && !this.hardCoreMode) {
            worldsettings.enableCommands();
         }

         this.minecraft.launchIntegratedServer(this.saveDirName, this.worldNameField.getText().trim(), worldsettings);
      }
   }

   /**
    * Returns whether the currently-selected world type is actually acceptable for selection
    * Used to hide the "debug" world type unless the shift key is depressed.
    */
   private boolean canSelectCurWorldType() {
      WorldType worldtype = WorldType.WORLD_TYPES[this.selectedIndex];
      if (worldtype != null && worldtype.canBeCreated()) {
         return worldtype == WorldType.DEBUG_ALL_BLOCK_STATES ? hasShiftDown() : true;
      } else {
         return false;
      }
   }

   /**
    * Toggles between initial world-creation display, and "more options" display.
    * Called when user clicks "More World Options..." or "Done" (same button, different labels depending on current
    * display).
    */
   private void toggleMoreWorldOptions() {
      this.showMoreWorldOptions(!this.inMoreWorldOptionsDisplay);
   }

   /**
    * Shows additional world-creation options if toggle is true, otherwise shows main world-creation elements
    */
   private void showMoreWorldOptions(boolean toggle) {
      this.inMoreWorldOptionsDisplay = toggle;
      if (WorldType.WORLD_TYPES[this.selectedIndex] == WorldType.DEBUG_ALL_BLOCK_STATES) {
         this.btnGameMode.visible = !this.inMoreWorldOptionsDisplay;
         this.btnGameMode.active = false;
         if (this.savedGameMode == null) {
            this.savedGameMode = this.gameMode;
         }

         this.gameMode = "spectator";
         this.btnMapFeatures.visible = false;
         this.btnBonusItems.visible = false;
         this.btnMapType.visible = this.inMoreWorldOptionsDisplay;
         this.btnAllowCommands.visible = false;
         this.btnCustomizeType.visible = false;
      } else {
         this.btnGameMode.visible = !this.inMoreWorldOptionsDisplay;
         this.btnGameMode.active = true;
         if (this.savedGameMode != null) {
            this.gameMode = this.savedGameMode;
            this.savedGameMode = null;
         }

         this.btnMapFeatures.visible = this.inMoreWorldOptionsDisplay && WorldType.WORLD_TYPES[this.selectedIndex] != WorldType.CUSTOMIZED;
         this.btnBonusItems.visible = this.inMoreWorldOptionsDisplay;
         this.btnMapType.visible = this.inMoreWorldOptionsDisplay;
         this.btnAllowCommands.visible = this.inMoreWorldOptionsDisplay;
         this.btnCustomizeType.visible = this.inMoreWorldOptionsDisplay && WorldType.WORLD_TYPES[this.selectedIndex].hasCustomOptions();
      }

      this.updateDisplayState();
      this.worldSeedField.setVisible(this.inMoreWorldOptionsDisplay);
      this.worldNameField.setVisible(!this.inMoreWorldOptionsDisplay);
      if (this.inMoreWorldOptionsDisplay) {
         this.btnMoreOptions.setMessage(I18n.format("gui.done"));
      } else {
         this.btnMoreOptions.setMessage(I18n.format("selectWorld.moreWorldOptions"));
      }

   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
         return true;
      } else if (p_keyPressed_1_ != 257 && p_keyPressed_1_ != 335) {
         return false;
      } else {
         this.createWorld();
         return true;
      }
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 20, -1);
      if (this.inMoreWorldOptionsDisplay) {
         this.drawString(this.font, I18n.format("selectWorld.enterSeed"), this.width / 2 - 100, 47, -6250336);
         this.drawString(this.font, I18n.format("selectWorld.seedInfo"), this.width / 2 - 100, 85, -6250336);
         if (this.btnMapFeatures.visible) {
            this.drawString(this.font, I18n.format("selectWorld.mapFeatures.info"), this.width / 2 - 150, 122, -6250336);
         }

         if (this.btnAllowCommands.visible) {
            this.drawString(this.font, I18n.format("selectWorld.allowCommands.info"), this.width / 2 - 150, 172, -6250336);
         }

         this.worldSeedField.render(p_render_1_, p_render_2_, p_render_3_);
         if (WorldType.WORLD_TYPES[this.selectedIndex].hasInfoNotice()) {
            this.font.drawSplitString(I18n.format(WorldType.WORLD_TYPES[this.selectedIndex].getInfoTranslationKey()), this.btnMapType.x + 2, this.btnMapType.y + 22, this.btnMapType.getWidth(), 10526880);
         }
      } else {
         this.drawString(this.font, I18n.format("selectWorld.enterName"), this.width / 2 - 100, 47, -6250336);
         this.drawString(this.font, I18n.format("selectWorld.resultFolder") + " " + this.saveDirName, this.width / 2 - 100, 85, -6250336);
         this.worldNameField.render(p_render_1_, p_render_2_, p_render_3_);
         this.drawCenteredString(this.font, this.gameModeDesc1, this.width / 2, 137, -6250336);
         this.drawCenteredString(this.font, this.gameModeDesc2, this.width / 2, 149, -6250336);
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   /**
    * Set the initial values of a new world to create, from the values from an existing world.
    *  
    * Called after construction when a user selects the "Recreate" button.
    */
   public void recreateFromExistingWorld(WorldInfo original) {
      this.worldName = original.getWorldName();
      this.worldSeed = original.getSeed() + "";
      WorldType worldtype = original.getGenerator() == WorldType.CUSTOMIZED ? WorldType.DEFAULT : original.getGenerator();
      this.selectedIndex = worldtype.getId();
      this.chunkProviderSettingsJson = original.getGeneratorOptions();
      this.generateStructuresEnabled = original.isMapFeaturesEnabled();
      this.allowCheats = original.areCommandsAllowed();
      if (original.isHardcore()) {
         this.gameMode = "hardcore";
      } else if (original.getGameType().isSurvivalOrAdventure()) {
         this.gameMode = "survival";
      } else if (original.getGameType().isCreative()) {
         this.gameMode = "creative";
      }

   }
}