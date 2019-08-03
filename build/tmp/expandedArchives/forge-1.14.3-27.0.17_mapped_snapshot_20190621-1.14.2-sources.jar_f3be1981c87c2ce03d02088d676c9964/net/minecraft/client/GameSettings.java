package net.minecraft.client;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.resources.ClientResourcePackInfo;
import net.minecraft.client.settings.AbstractOption;
import net.minecraft.client.settings.AmbientOcclusionStatus;
import net.minecraft.client.settings.AttackIndicatorStatus;
import net.minecraft.client.settings.CloudOption;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.settings.NarratorStatus;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.ChatVisibility;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.play.client.CClientSettingsPacket;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.util.HandSide;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.datafix.DefaultTypeReferences;
import net.minecraft.world.Difficulty;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class GameSettings {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = new Gson();
   private static final Type TYPE_LIST_STRING = new ParameterizedType() {
      public Type[] getActualTypeArguments() {
         return new Type[]{String.class};
      }

      public Type getRawType() {
         return List.class;
      }

      public Type getOwnerType() {
         return null;
      }
   };
   public static final Splitter COLON_SPLITTER = Splitter.on(':');
   public double mouseSensitivity = 0.5D;
   public int renderDistanceChunks = -1;
   public int framerateLimit = 120;
   public CloudOption cloudOption = CloudOption.FANCY;
   public boolean fancyGraphics = true;
   public AmbientOcclusionStatus ambientOcclusionStatus = AmbientOcclusionStatus.MAX;
   public List<String> resourcePacks = Lists.newArrayList();
   public List<String> incompatibleResourcePacks = Lists.newArrayList();
   public ChatVisibility chatVisibility = ChatVisibility.FULL;
   public double chatOpacity = 1.0D;
   public double accessibilityTextBackgroundOpacity = 0.5D;
   @Nullable
   public String fullscreenResolution;
   public boolean hideServerAddress;
   public boolean advancedItemTooltips;
   public boolean pauseOnLostFocus = true;
   private final Set<PlayerModelPart> setModelParts = Sets.newHashSet(PlayerModelPart.values());
   public HandSide mainHand = HandSide.RIGHT;
   public int overrideWidth;
   public int overrideHeight;
   public boolean heldItemTooltips = true;
   public double chatScale = 1.0D;
   public double chatWidth = 1.0D;
   public double chatHeightUnfocused = (double)0.44366196F;
   public double chatHeightFocused = 1.0D;
   public int mipmapLevels = 4;
   private final Map<SoundCategory, Float> soundLevels = Maps.newEnumMap(SoundCategory.class);
   public boolean useNativeTransport = true;
   public AttackIndicatorStatus attackIndicator = AttackIndicatorStatus.CROSSHAIR;
   public TutorialSteps tutorialStep = TutorialSteps.MOVEMENT;
   public int biomeBlendRadius = 2;
   public double mouseWheelSensitivity = 1.0D;
   public int glDebugVerbosity = 1;
   public boolean autoJump = true;
   public boolean autoSuggestCommands = true;
   public boolean chatColor = true;
   public boolean chatLinks = true;
   public boolean chatLinksPrompt = true;
   public boolean vsync = true;
   public boolean entityShadows = true;
   public boolean forceUnicodeFont;
   public boolean invertMouse;
   public boolean discreteMouseScroll;
   public boolean realmsNotifications = true;
   public boolean reducedDebugInfo;
   public boolean snooper = true;
   public boolean showSubtitles;
   public boolean accessibilityTextBackground = true;
   public boolean touchscreen;
   public boolean fullscreen;
   public boolean viewBobbing = true;
   public final KeyBinding keyBindForward = new KeyBinding("key.forward", 87, "key.categories.movement");
   public final KeyBinding keyBindLeft = new KeyBinding("key.left", 65, "key.categories.movement");
   public final KeyBinding keyBindBack = new KeyBinding("key.back", 83, "key.categories.movement");
   public final KeyBinding keyBindRight = new KeyBinding("key.right", 68, "key.categories.movement");
   public final KeyBinding keyBindJump = new KeyBinding("key.jump", 32, "key.categories.movement");
   public final KeyBinding keyBindSneak = new KeyBinding("key.sneak", 340, "key.categories.movement");
   public final KeyBinding keyBindSprint = new KeyBinding("key.sprint", 341, "key.categories.movement");
   public final KeyBinding keyBindInventory = new KeyBinding("key.inventory", 69, "key.categories.inventory");
   public final KeyBinding keyBindSwapHands = new KeyBinding("key.swapHands", 70, "key.categories.inventory");
   public final KeyBinding keyBindDrop = new KeyBinding("key.drop", 81, "key.categories.inventory");
   public final KeyBinding keyBindUseItem = new KeyBinding("key.use", InputMappings.Type.MOUSE, 1, "key.categories.gameplay");
   public final KeyBinding keyBindAttack = new KeyBinding("key.attack", InputMappings.Type.MOUSE, 0, "key.categories.gameplay");
   public final KeyBinding keyBindPickBlock = new KeyBinding("key.pickItem", InputMappings.Type.MOUSE, 2, "key.categories.gameplay");
   public final KeyBinding keyBindChat = new KeyBinding("key.chat", 84, "key.categories.multiplayer");
   public final KeyBinding keyBindPlayerList = new KeyBinding("key.playerlist", 258, "key.categories.multiplayer");
   public final KeyBinding keyBindCommand = new KeyBinding("key.command", 47, "key.categories.multiplayer");
   public final KeyBinding keyBindScreenshot = new KeyBinding("key.screenshot", 291, "key.categories.misc");
   public final KeyBinding keyBindTogglePerspective = new KeyBinding("key.togglePerspective", 294, "key.categories.misc");
   public final KeyBinding keyBindSmoothCamera = new KeyBinding("key.smoothCamera", InputMappings.INPUT_INVALID.getKeyCode(), "key.categories.misc");
   public final KeyBinding keyBindFullscreen = new KeyBinding("key.fullscreen", 300, "key.categories.misc");
   public final KeyBinding keyBindSpectatorOutlines = new KeyBinding("key.spectatorOutlines", InputMappings.INPUT_INVALID.getKeyCode(), "key.categories.misc");
   public final KeyBinding keyBindAdvancements = new KeyBinding("key.advancements", 76, "key.categories.misc");
   public final KeyBinding[] keyBindsHotbar = new KeyBinding[]{new KeyBinding("key.hotbar.1", 49, "key.categories.inventory"), new KeyBinding("key.hotbar.2", 50, "key.categories.inventory"), new KeyBinding("key.hotbar.3", 51, "key.categories.inventory"), new KeyBinding("key.hotbar.4", 52, "key.categories.inventory"), new KeyBinding("key.hotbar.5", 53, "key.categories.inventory"), new KeyBinding("key.hotbar.6", 54, "key.categories.inventory"), new KeyBinding("key.hotbar.7", 55, "key.categories.inventory"), new KeyBinding("key.hotbar.8", 56, "key.categories.inventory"), new KeyBinding("key.hotbar.9", 57, "key.categories.inventory")};
   public final KeyBinding keyBindSaveToolbar = new KeyBinding("key.saveToolbarActivator", 67, "key.categories.creative");
   public final KeyBinding keyBindLoadToolbar = new KeyBinding("key.loadToolbarActivator", 88, "key.categories.creative");
   public KeyBinding[] keyBindings = ArrayUtils.addAll(new KeyBinding[]{this.keyBindAttack, this.keyBindUseItem, this.keyBindForward, this.keyBindLeft, this.keyBindBack, this.keyBindRight, this.keyBindJump, this.keyBindSneak, this.keyBindSprint, this.keyBindDrop, this.keyBindInventory, this.keyBindChat, this.keyBindPlayerList, this.keyBindPickBlock, this.keyBindCommand, this.keyBindScreenshot, this.keyBindTogglePerspective, this.keyBindSmoothCamera, this.keyBindFullscreen, this.keyBindSpectatorOutlines, this.keyBindSwapHands, this.keyBindSaveToolbar, this.keyBindLoadToolbar, this.keyBindAdvancements}, this.keyBindsHotbar);
   protected Minecraft mc;
   private final File optionsFile;
   public Difficulty difficulty = Difficulty.NORMAL;
   public boolean hideGUI;
   public int thirdPersonView;
   public boolean showDebugInfo;
   public boolean showDebugProfilerChart;
   public boolean showLagometer;
   public String lastServer = "";
   public boolean smoothCamera;
   public double fov = 70.0D;
   public double gamma;
   public int guiScale;
   public ParticleStatus particles = ParticleStatus.ALL;
   public NarratorStatus narrator = NarratorStatus.OFF;
   public String language = "en_us";

   public GameSettings(Minecraft mcIn, File mcDataDir) {
      setForgeKeybindProperties();
      this.mc = mcIn;
      this.optionsFile = new File(mcDataDir, "options.txt");
      if (mcIn.isJava64bit() && Runtime.getRuntime().maxMemory() >= 1000000000L) {
         AbstractOption.RENDER_DISTANCE.func_216728_a(32.0F);
      } else {
         AbstractOption.RENDER_DISTANCE.func_216728_a(16.0F);
      }

      this.renderDistanceChunks = mcIn.isJava64bit() ? 12 : 8;
      this.loadOptions();
   }

   public float func_216840_a(float p_216840_1_) {
      return this.accessibilityTextBackground ? p_216840_1_ : (float)this.accessibilityTextBackgroundOpacity;
   }

   public int func_216841_b(float p_216841_1_) {
      return (int)(this.func_216840_a(p_216841_1_) * 255.0F) << 24 & -16777216;
   }

   public int func_216839_a(int p_216839_1_) {
      return this.accessibilityTextBackground ? p_216839_1_ : (int)(this.accessibilityTextBackgroundOpacity * 255.0D) << 24 & -16777216;
   }

   public void setKeyBindingCode(KeyBinding keyBindingIn, InputMappings.Input inputIn) {
      keyBindingIn.bind(inputIn);
      this.saveOptions();
   }

   /**
    * Loads the options from the options file. It appears that this has replaced the previous 'loadOptions'
    */
   public void loadOptions() {
      try {
         if (!this.optionsFile.exists()) {
            return;
         }

         this.soundLevels.clear();
         List<String> list = IOUtils.readLines(new FileInputStream(this.optionsFile));
         CompoundNBT compoundnbt = new CompoundNBT();

         for(String s : list) {
            try {
               Iterator<String> iterator = COLON_SPLITTER.omitEmptyStrings().limit(2).split(s).iterator();
               compoundnbt.putString(iterator.next(), iterator.next());
            } catch (Exception var10) {
               LOGGER.warn("Skipping bad option: {}", (Object)s);
            }
         }

         compoundnbt = this.dataFix(compoundnbt);

         for(String s1 : compoundnbt.keySet()) {
            String s2 = compoundnbt.getString(s1);

            try {
               if ("autoJump".equals(s1)) {
                  AbstractOption.AUTO_JUMP.set(this, s2);
               }

               if ("autoSuggestions".equals(s1)) {
                  AbstractOption.AUTO_SUGGEST_COMMANDS.set(this, s2);
               }

               if ("chatColors".equals(s1)) {
                  AbstractOption.CHAT_COLOR.set(this, s2);
               }

               if ("chatLinks".equals(s1)) {
                  AbstractOption.CHAT_LINKS.set(this, s2);
               }

               if ("chatLinksPrompt".equals(s1)) {
                  AbstractOption.CHAT_LINKS_PROMPT.set(this, s2);
               }

               if ("enableVsync".equals(s1)) {
                  AbstractOption.VSYNC.set(this, s2);
               }

               if ("entityShadows".equals(s1)) {
                  AbstractOption.ENTITY_SHADOWS.set(this, s2);
               }

               if ("forceUnicodeFont".equals(s1)) {
                  AbstractOption.FORCE_UNICODE_FONT.set(this, s2);
               }

               if ("discrete_mouse_scroll".equals(s1)) {
                  AbstractOption.DISCRETE_MOUSE_SCROLL.set(this, s2);
               }

               if ("invertYMouse".equals(s1)) {
                  AbstractOption.INVERT_MOUSE.set(this, s2);
               }

               if ("realmsNotifications".equals(s1)) {
                  AbstractOption.REALMS_NOTIFICATIONS.set(this, s2);
               }

               if ("reducedDebugInfo".equals(s1)) {
                  AbstractOption.REDUCED_DEBUG_INFO.set(this, s2);
               }

               if ("showSubtitles".equals(s1)) {
                  AbstractOption.SHOW_SUBTITLES.set(this, s2);
               }

               if ("snooperEnabled".equals(s1)) {
                  AbstractOption.SNOOPER.set(this, s2);
               }

               if ("touchscreen".equals(s1)) {
                  AbstractOption.TOUCHSCREEN.set(this, s2);
               }

               if ("fullscreen".equals(s1)) {
                  AbstractOption.FULLSCREEN.set(this, s2);
               }

               if ("bobView".equals(s1)) {
                  AbstractOption.VIEW_BOBBING.set(this, s2);
               }

               if ("mouseSensitivity".equals(s1)) {
                  this.mouseSensitivity = (double)parseFloat(s2);
               }

               if ("fov".equals(s1)) {
                  this.fov = (double)(parseFloat(s2) * 40.0F + 70.0F);
               }

               if ("gamma".equals(s1)) {
                  this.gamma = (double)parseFloat(s2);
               }

               if ("renderDistance".equals(s1)) {
                  this.renderDistanceChunks = Integer.parseInt(s2);
               }

               if ("guiScale".equals(s1)) {
                  this.guiScale = Integer.parseInt(s2);
               }

               if ("particles".equals(s1)) {
                  this.particles = ParticleStatus.byId(Integer.parseInt(s2));
               }

               if ("maxFps".equals(s1)) {
                  this.framerateLimit = Integer.parseInt(s2);
                  if (this.mc.mainWindow != null) {
                     this.mc.mainWindow.setFramerateLimit(this.framerateLimit);
                  }
               }

               if ("difficulty".equals(s1)) {
                  this.difficulty = Difficulty.byId(Integer.parseInt(s2));
               }

               if ("fancyGraphics".equals(s1)) {
                  this.fancyGraphics = "true".equals(s2);
               }

               if ("tutorialStep".equals(s1)) {
                  this.tutorialStep = TutorialSteps.byName(s2);
               }

               if ("ao".equals(s1)) {
                  if ("true".equals(s2)) {
                     this.ambientOcclusionStatus = AmbientOcclusionStatus.MAX;
                  } else if ("false".equals(s2)) {
                     this.ambientOcclusionStatus = AmbientOcclusionStatus.OFF;
                  } else {
                     this.ambientOcclusionStatus = AmbientOcclusionStatus.func_216570_a(Integer.parseInt(s2));
                  }
               }

               if ("renderClouds".equals(s1)) {
                  if ("true".equals(s2)) {
                     this.cloudOption = CloudOption.FANCY;
                  } else if ("false".equals(s2)) {
                     this.cloudOption = CloudOption.OFF;
                  } else if ("fast".equals(s2)) {
                     this.cloudOption = CloudOption.FAST;
                  }
               }

               if ("attackIndicator".equals(s1)) {
                  this.attackIndicator = AttackIndicatorStatus.byId(Integer.parseInt(s2));
               }

               if ("resourcePacks".equals(s1)) {
                  this.resourcePacks = JSONUtils.fromJson(GSON, s2, TYPE_LIST_STRING);
                  if (this.resourcePacks == null) {
                     this.resourcePacks = Lists.newArrayList();
                  }
               }

               if ("incompatibleResourcePacks".equals(s1)) {
                  this.incompatibleResourcePacks = JSONUtils.fromJson(GSON, s2, TYPE_LIST_STRING);
                  if (this.incompatibleResourcePacks == null) {
                     this.incompatibleResourcePacks = Lists.newArrayList();
                  }
               }

               if ("lastServer".equals(s1)) {
                  this.lastServer = s2;
               }

               if ("lang".equals(s1)) {
                  this.language = s2;
               }

               if ("chatVisibility".equals(s1)) {
                  this.chatVisibility = ChatVisibility.func_221252_a(Integer.parseInt(s2));
               }

               if ("chatOpacity".equals(s1)) {
                  this.chatOpacity = (double)parseFloat(s2);
               }

               if ("textBackgroundOpacity".equals(s1)) {
                  this.accessibilityTextBackgroundOpacity = (double)parseFloat(s2);
               }

               if ("backgroundForChatOnly".equals(s1)) {
                  this.accessibilityTextBackground = "true".equals(s2);
               }

               if ("fullscreenResolution".equals(s1)) {
                  this.fullscreenResolution = s2;
               }

               if ("hideServerAddress".equals(s1)) {
                  this.hideServerAddress = "true".equals(s2);
               }

               if ("advancedItemTooltips".equals(s1)) {
                  this.advancedItemTooltips = "true".equals(s2);
               }

               if ("pauseOnLostFocus".equals(s1)) {
                  this.pauseOnLostFocus = "true".equals(s2);
               }

               if ("overrideHeight".equals(s1)) {
                  this.overrideHeight = Integer.parseInt(s2);
               }

               if ("overrideWidth".equals(s1)) {
                  this.overrideWidth = Integer.parseInt(s2);
               }

               if ("heldItemTooltips".equals(s1)) {
                  this.heldItemTooltips = "true".equals(s2);
               }

               if ("chatHeightFocused".equals(s1)) {
                  this.chatHeightFocused = (double)parseFloat(s2);
               }

               if ("chatHeightUnfocused".equals(s1)) {
                  this.chatHeightUnfocused = (double)parseFloat(s2);
               }

               if ("chatScale".equals(s1)) {
                  this.chatScale = (double)parseFloat(s2);
               }

               if ("chatWidth".equals(s1)) {
                  this.chatWidth = (double)parseFloat(s2);
               }

               if ("mipmapLevels".equals(s1)) {
                  this.mipmapLevels = Integer.parseInt(s2);
               }

               if ("useNativeTransport".equals(s1)) {
                  this.useNativeTransport = "true".equals(s2);
               }

               if ("mainHand".equals(s1)) {
                  this.mainHand = "left".equals(s2) ? HandSide.LEFT : HandSide.RIGHT;
               }

               if ("narrator".equals(s1)) {
                  this.narrator = NarratorStatus.byId(Integer.parseInt(s2));
               }

               if ("biomeBlendRadius".equals(s1)) {
                  this.biomeBlendRadius = Integer.parseInt(s2);
               }

               if ("mouseWheelSensitivity".equals(s1)) {
                  this.mouseWheelSensitivity = (double)parseFloat(s2);
               }

               if ("glDebugVerbosity".equals(s1)) {
                  this.glDebugVerbosity = Integer.parseInt(s2);
               }

               for(KeyBinding keybinding : this.keyBindings) {
                  if (s1.equals("key_" + keybinding.getKeyDescription())) {
                     if (s2.indexOf(':') != -1) {
                        String[] pts = s2.split(":");
                        keybinding.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.valueFromString(pts[1]), InputMappings.getInputByName(pts[0]));
                     } else
                         keybinding.setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier.NONE, InputMappings.getInputByName(s2));
                  }
               }

               for(SoundCategory soundcategory : SoundCategory.values()) {
                  if (s1.equals("soundCategory_" + soundcategory.getName())) {
                     this.soundLevels.put(soundcategory, parseFloat(s2));
                  }
               }

               for(PlayerModelPart playermodelpart : PlayerModelPart.values()) {
                  if (s1.equals("modelPart_" + playermodelpart.getPartName())) {
                     this.setModelPartEnabled(playermodelpart, "true".equals(s2));
                  }
               }
            } catch (Exception var11) {
               LOGGER.warn("Skipping bad option: {}:{}", s1, s2);
            }
         }

         KeyBinding.resetKeyBindingArrayAndHash();
      } catch (Exception exception) {
         LOGGER.error("Failed to load options", (Throwable)exception);
      }

   }

   private CompoundNBT dataFix(CompoundNBT nbt) {
      int i = 0;

      try {
         i = Integer.parseInt(nbt.getString("version"));
      } catch (RuntimeException var4) {
         ;
      }

      return NBTUtil.update(this.mc.getDataFixer(), DefaultTypeReferences.OPTIONS, nbt, i);
   }

   /**
    * Parses a string into a float.
    */
   private static float parseFloat(String p_74305_0_) {
      if ("true".equals(p_74305_0_)) {
         return 1.0F;
      } else {
         return "false".equals(p_74305_0_) ? 0.0F : Float.parseFloat(p_74305_0_);
      }
   }

   /**
    * Saves the options to the options file.
    */
   public void saveOptions() {
      if (net.minecraftforge.fml.client.ClientModLoader.isLoading()) return; //Don't save settings before mods add keybindigns and the like to prevent them from being deleted.
      try (PrintWriter printwriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.optionsFile), StandardCharsets.UTF_8))) {
         printwriter.println("version:" + SharedConstants.getVersion().getWorldVersion());
         printwriter.println("autoJump:" + AbstractOption.AUTO_JUMP.get(this));
         printwriter.println("autoSuggestions:" + AbstractOption.AUTO_SUGGEST_COMMANDS.get(this));
         printwriter.println("chatColors:" + AbstractOption.CHAT_COLOR.get(this));
         printwriter.println("chatLinks:" + AbstractOption.CHAT_LINKS.get(this));
         printwriter.println("chatLinksPrompt:" + AbstractOption.CHAT_LINKS_PROMPT.get(this));
         printwriter.println("enableVsync:" + AbstractOption.VSYNC.get(this));
         printwriter.println("entityShadows:" + AbstractOption.ENTITY_SHADOWS.get(this));
         printwriter.println("forceUnicodeFont:" + AbstractOption.FORCE_UNICODE_FONT.get(this));
         printwriter.println("discrete_mouse_scroll:" + AbstractOption.DISCRETE_MOUSE_SCROLL.get(this));
         printwriter.println("invertYMouse:" + AbstractOption.INVERT_MOUSE.get(this));
         printwriter.println("realmsNotifications:" + AbstractOption.REALMS_NOTIFICATIONS.get(this));
         printwriter.println("reducedDebugInfo:" + AbstractOption.REDUCED_DEBUG_INFO.get(this));
         printwriter.println("snooperEnabled:" + AbstractOption.SNOOPER.get(this));
         printwriter.println("showSubtitles:" + AbstractOption.SHOW_SUBTITLES.get(this));
         printwriter.println("touchscreen:" + AbstractOption.TOUCHSCREEN.get(this));
         printwriter.println("fullscreen:" + AbstractOption.FULLSCREEN.get(this));
         printwriter.println("bobView:" + AbstractOption.VIEW_BOBBING.get(this));
         printwriter.println("mouseSensitivity:" + this.mouseSensitivity);
         printwriter.println("fov:" + (this.fov - 70.0D) / 40.0D);
         printwriter.println("gamma:" + this.gamma);
         printwriter.println("renderDistance:" + this.renderDistanceChunks);
         printwriter.println("guiScale:" + this.guiScale);
         printwriter.println("particles:" + this.particles.func_216832_b());
         printwriter.println("maxFps:" + this.framerateLimit);
         printwriter.println("difficulty:" + this.difficulty.getId());
         printwriter.println("fancyGraphics:" + this.fancyGraphics);
         printwriter.println("ao:" + this.ambientOcclusionStatus.func_216572_a());
         printwriter.println("biomeBlendRadius:" + this.biomeBlendRadius);
         switch(this.cloudOption) {
         case FANCY:
            printwriter.println("renderClouds:true");
            break;
         case FAST:
            printwriter.println("renderClouds:fast");
            break;
         case OFF:
            printwriter.println("renderClouds:false");
         }

         printwriter.println("resourcePacks:" + GSON.toJson(this.resourcePacks));
         printwriter.println("incompatibleResourcePacks:" + GSON.toJson(this.incompatibleResourcePacks));
         printwriter.println("lastServer:" + this.lastServer);
         printwriter.println("lang:" + this.language);
         printwriter.println("chatVisibility:" + this.chatVisibility.func_221254_a());
         printwriter.println("chatOpacity:" + this.chatOpacity);
         printwriter.println("textBackgroundOpacity:" + this.accessibilityTextBackgroundOpacity);
         printwriter.println("backgroundForChatOnly:" + this.accessibilityTextBackground);
         if (this.mc.mainWindow.getVideoMode().isPresent()) {
            printwriter.println("fullscreenResolution:" + this.mc.mainWindow.getVideoMode().get().getSettingsString());
         }

         printwriter.println("hideServerAddress:" + this.hideServerAddress);
         printwriter.println("advancedItemTooltips:" + this.advancedItemTooltips);
         printwriter.println("pauseOnLostFocus:" + this.pauseOnLostFocus);
         printwriter.println("overrideWidth:" + this.overrideWidth);
         printwriter.println("overrideHeight:" + this.overrideHeight);
         printwriter.println("heldItemTooltips:" + this.heldItemTooltips);
         printwriter.println("chatHeightFocused:" + this.chatHeightFocused);
         printwriter.println("chatHeightUnfocused:" + this.chatHeightUnfocused);
         printwriter.println("chatScale:" + this.chatScale);
         printwriter.println("chatWidth:" + this.chatWidth);
         printwriter.println("mipmapLevels:" + this.mipmapLevels);
         printwriter.println("useNativeTransport:" + this.useNativeTransport);
         printwriter.println("mainHand:" + (this.mainHand == HandSide.LEFT ? "left" : "right"));
         printwriter.println("attackIndicator:" + this.attackIndicator.func_216751_a());
         printwriter.println("narrator:" + this.narrator.func_216827_a());
         printwriter.println("tutorialStep:" + this.tutorialStep.getName());
         printwriter.println("mouseWheelSensitivity:" + this.mouseWheelSensitivity);
         printwriter.println("glDebugVerbosity:" + this.glDebugVerbosity);

         for(KeyBinding keybinding : this.keyBindings) {
            printwriter.println("key_" + keybinding.getKeyDescription() + ":" + keybinding.getTranslationKey() + (keybinding.getKeyModifier() != net.minecraftforge.client.settings.KeyModifier.NONE ? ":" + keybinding.getKeyModifier() : ""));
         }

         for(SoundCategory soundcategory : SoundCategory.values()) {
            printwriter.println("soundCategory_" + soundcategory.getName() + ":" + this.getSoundLevel(soundcategory));
         }

         for(PlayerModelPart playermodelpart : PlayerModelPart.values()) {
            printwriter.println("modelPart_" + playermodelpart.getPartName() + ":" + this.setModelParts.contains(playermodelpart));
         }
      } catch (Exception exception) {
         LOGGER.error("Failed to save options", (Throwable)exception);
      }

      this.sendSettingsToServer();
   }

   public float getSoundLevel(SoundCategory category) {
      return this.soundLevels.containsKey(category) ? this.soundLevels.get(category) : 1.0F;
   }

   public void setSoundLevel(SoundCategory category, float volume) {
      this.soundLevels.put(category, volume);
      this.mc.getSoundHandler().setSoundLevel(category, volume);
   }

   /**
    * Send a client info packet with settings information to the server
    */
   public void sendSettingsToServer() {
      if (this.mc.player != null) {
         int i = 0;

         for(PlayerModelPart playermodelpart : this.setModelParts) {
            i |= playermodelpart.getPartMask();
         }

         this.mc.player.connection.sendPacket(new CClientSettingsPacket(this.language, this.renderDistanceChunks, this.chatVisibility, this.chatColor, i, this.mainHand));
      }

   }

   public Set<PlayerModelPart> getModelParts() {
      return ImmutableSet.copyOf(this.setModelParts);
   }

   public void setModelPartEnabled(PlayerModelPart modelPart, boolean enable) {
      if (enable) {
         this.setModelParts.add(modelPart);
      } else {
         this.setModelParts.remove(modelPart);
      }

      this.sendSettingsToServer();
   }

   public void switchModelPartEnabled(PlayerModelPart modelPart) {
      if (this.getModelParts().contains(modelPart)) {
         this.setModelParts.remove(modelPart);
      } else {
         this.setModelParts.add(modelPart);
      }

      this.sendSettingsToServer();
   }

   public CloudOption getCloudOption() {
      return this.renderDistanceChunks >= 4 ? this.cloudOption : CloudOption.OFF;
   }

   /**
    * Return true if the client connect to a server using the native transport system
    */
   public boolean isUsingNativeTransport() {
      return this.useNativeTransport;
   }

   public void fillResourcePackList(ResourcePackList<ClientResourcePackInfo> resourcePackListIn) {
      resourcePackListIn.reloadPacksFromFinders();
      Set<ClientResourcePackInfo> set = Sets.newLinkedHashSet();
      Iterator<String> iterator = this.resourcePacks.iterator();

      while(iterator.hasNext()) {
         String s = iterator.next();
         ClientResourcePackInfo clientresourcepackinfo = resourcePackListIn.getPackInfo(s);
         if (clientresourcepackinfo == null && !s.startsWith("file/")) {
            clientresourcepackinfo = resourcePackListIn.getPackInfo("file/" + s);
         }

         if (clientresourcepackinfo == null) {
            LOGGER.warn("Removed resource pack {} from options because it doesn't seem to exist anymore", (Object)s);
            iterator.remove();
         } else if (!clientresourcepackinfo.getCompatibility().func_198968_a() && !this.incompatibleResourcePacks.contains(s)) {
            LOGGER.warn("Removed resource pack {} from options because it is no longer compatible", (Object)s);
            iterator.remove();
         } else if (clientresourcepackinfo.getCompatibility().func_198968_a() && this.incompatibleResourcePacks.contains(s)) {
            LOGGER.info("Removed resource pack {} from incompatibility list because it's now compatible", (Object)s);
            this.incompatibleResourcePacks.remove(s);
         } else {
            set.add(clientresourcepackinfo);
         }
      }

      if (net.minecraftforge.fml.client.ClientModLoader.isLoading()) return; //Don't save settings before mods add keybindigns and the like to prevent them from being deleted.
      resourcePackListIn.setEnabledPacks(set);
   }

   private void setForgeKeybindProperties() {
      net.minecraftforge.client.settings.KeyConflictContext inGame = net.minecraftforge.client.settings.KeyConflictContext.IN_GAME;
      keyBindForward.setKeyConflictContext(inGame);
      keyBindLeft.setKeyConflictContext(inGame);
      keyBindBack.setKeyConflictContext(inGame);
      keyBindRight.setKeyConflictContext(inGame);
      keyBindJump.setKeyConflictContext(inGame);
      keyBindSneak.setKeyConflictContext(inGame);
      keyBindSprint.setKeyConflictContext(inGame);
      keyBindAttack.setKeyConflictContext(inGame);
      keyBindChat.setKeyConflictContext(inGame);
      keyBindPlayerList.setKeyConflictContext(inGame);
      keyBindCommand.setKeyConflictContext(inGame);
      keyBindTogglePerspective.setKeyConflictContext(inGame);
      keyBindSmoothCamera.setKeyConflictContext(inGame);
      keyBindSwapHands.setKeyConflictContext(inGame);
   }
}