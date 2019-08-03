package net.minecraft.data;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.ResourceLocation;

public class CustomRecipeBuilder {
   private final SpecialRecipeSerializer<?> serializer;

   public CustomRecipeBuilder(SpecialRecipeSerializer<?> p_i50786_1_) {
      this.serializer = p_i50786_1_;
   }

   public static CustomRecipeBuilder func_218656_a(SpecialRecipeSerializer<?> p_218656_0_) {
      return new CustomRecipeBuilder(p_218656_0_);
   }

   /**
    * Builds this recipe into an {@link IFinishedRecipe}.
    */
   public void build(Consumer<IFinishedRecipe> consumerIn, final String id) {
      consumerIn.accept(new IFinishedRecipe() {
         public void func_218610_a(JsonObject p_218610_1_) {
         }

         public IRecipeSerializer<?> func_218609_c() {
            return CustomRecipeBuilder.this.serializer;
         }

         /**
          * Gets the ID for the recipe.
          */
         public ResourceLocation getID() {
            return new ResourceLocation(id);
         }

         /**
          * Gets the JSON for the advancement that unlocks this recipe. Null if there is no advancement.
          */
         @Nullable
         public JsonObject getAdvancementJson() {
            return null;
         }

         /**
          * Gets the ID for the advancement associated with this recipe. Should not be null if {@link
          * #getAdvancementJson} is non-null.
          */
         public ResourceLocation getAdvancementID() {
            return new ResourceLocation("");
         }
      });
   }
}