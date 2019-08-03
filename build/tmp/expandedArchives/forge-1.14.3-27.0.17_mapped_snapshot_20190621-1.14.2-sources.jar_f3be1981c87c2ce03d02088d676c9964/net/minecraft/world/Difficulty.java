package net.minecraft.world;

import java.util.Arrays;
import java.util.Comparator;
import javax.annotation.Nullable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public enum Difficulty {
   PEACEFUL(0, "peaceful"),
   EASY(1, "easy"),
   NORMAL(2, "normal"),
   HARD(3, "hard");

   private static final Difficulty[] ID_MAPPING = Arrays.stream(values()).sorted(Comparator.comparingInt(Difficulty::getId)).toArray((p_199928_0_) -> {
      return new Difficulty[p_199928_0_];
   });
   private final int id;
   private final String translationKey;

   private Difficulty(int difficultyIdIn, String difficultyResourceKeyIn) {
      this.id = difficultyIdIn;
      this.translationKey = difficultyResourceKeyIn;
   }

   public int getId() {
      return this.id;
   }

   public ITextComponent getDisplayName() {
      return new TranslationTextComponent("options.difficulty." + this.translationKey);
   }

   public static Difficulty byId(int id) {
      return ID_MAPPING[id % ID_MAPPING.length];
   }

   @Nullable
   public static Difficulty func_219963_a(String p_219963_0_) {
      for(Difficulty difficulty : values()) {
         if (difficulty.translationKey.equals(p_219963_0_)) {
            return difficulty;
         }
      }

      return null;
   }

   public String getTranslationKey() {
      return this.translationKey;
   }
}