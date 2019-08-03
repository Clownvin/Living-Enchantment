package net.minecraft.data;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.CookingRecipeSerializer;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class CookingRecipeBuilder {
   private final Item field_218636_a;
   private final Ingredient field_218637_b;
   private final float field_218638_c;
   private final int field_218639_d;
   private final Advancement.Builder field_218640_e = Advancement.Builder.builder();
   private String field_218641_f;
   private final CookingRecipeSerializer<?> field_218642_g;

   private CookingRecipeBuilder(IItemProvider p_i50788_1_, Ingredient p_i50788_2_, float p_i50788_3_, int p_i50788_4_, CookingRecipeSerializer<?> p_i50788_5_) {
      this.field_218636_a = p_i50788_1_.asItem();
      this.field_218637_b = p_i50788_2_;
      this.field_218638_c = p_i50788_3_;
      this.field_218639_d = p_i50788_4_;
      this.field_218642_g = p_i50788_5_;
   }

   public static CookingRecipeBuilder func_218631_a(Ingredient p_218631_0_, IItemProvider p_218631_1_, float p_218631_2_, int p_218631_3_, CookingRecipeSerializer<?> p_218631_4_) {
      return new CookingRecipeBuilder(p_218631_1_, p_218631_0_, p_218631_2_, p_218631_3_, p_218631_4_);
   }

   public static CookingRecipeBuilder func_218633_b(Ingredient p_218633_0_, IItemProvider p_218633_1_, float p_218633_2_, int p_218633_3_) {
      return func_218631_a(p_218633_0_, p_218633_1_, p_218633_2_, p_218633_3_, IRecipeSerializer.BLASTING);
   }

   public static CookingRecipeBuilder func_218629_c(Ingredient p_218629_0_, IItemProvider p_218629_1_, float p_218629_2_, int p_218629_3_) {
      return func_218631_a(p_218629_0_, p_218629_1_, p_218629_2_, p_218629_3_, IRecipeSerializer.SMELTING);
   }

   public CookingRecipeBuilder func_218628_a(String p_218628_1_, ICriterionInstance p_218628_2_) {
      this.field_218640_e.withCriterion(p_218628_1_, p_218628_2_);
      return this;
   }

   public void func_218630_a(Consumer<IFinishedRecipe> p_218630_1_) {
      this.func_218635_a(p_218630_1_, Registry.ITEM.getKey(this.field_218636_a));
   }

   public void func_218632_a(Consumer<IFinishedRecipe> p_218632_1_, String p_218632_2_) {
      ResourceLocation resourcelocation = Registry.ITEM.getKey(this.field_218636_a);
      ResourceLocation resourcelocation1 = new ResourceLocation(p_218632_2_);
      if (resourcelocation1.equals(resourcelocation)) {
         throw new IllegalStateException("Recipe " + resourcelocation1 + " should remove its 'save' argument");
      } else {
         this.func_218635_a(p_218632_1_, resourcelocation1);
      }
   }

   public void func_218635_a(Consumer<IFinishedRecipe> p_218635_1_, ResourceLocation p_218635_2_) {
      this.func_218634_a(p_218635_2_);
      this.field_218640_e.withParentId(new ResourceLocation("recipes/root")).withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(p_218635_2_)).withRewards(AdvancementRewards.Builder.recipe(p_218635_2_)).withRequirementsStrategy(IRequirementsStrategy.field_223215_b_);
      p_218635_1_.accept(new CookingRecipeBuilder.Result(p_218635_2_, this.field_218641_f == null ? "" : this.field_218641_f, this.field_218637_b, this.field_218636_a, this.field_218638_c, this.field_218639_d, this.field_218640_e, new ResourceLocation(p_218635_2_.getNamespace(), "recipes/" + this.field_218636_a.getGroup().getPath() + "/" + p_218635_2_.getPath()), this.field_218642_g));
   }

   private void func_218634_a(ResourceLocation p_218634_1_) {
      if (this.field_218640_e.getCriteria().isEmpty()) {
         throw new IllegalStateException("No way of obtaining recipe " + p_218634_1_);
      }
   }

   public static class Result implements IFinishedRecipe {
      private final ResourceLocation field_218611_a;
      private final String field_218612_b;
      private final Ingredient field_218613_c;
      private final Item field_218614_d;
      private final float field_218615_e;
      private final int field_218616_f;
      private final Advancement.Builder field_218617_g;
      private final ResourceLocation field_218618_h;
      private final IRecipeSerializer<? extends AbstractCookingRecipe> field_218619_i;

      public Result(ResourceLocation p_i50605_1_, String p_i50605_2_, Ingredient p_i50605_3_, Item p_i50605_4_, float p_i50605_5_, int p_i50605_6_, Advancement.Builder p_i50605_7_, ResourceLocation p_i50605_8_, IRecipeSerializer<? extends AbstractCookingRecipe> p_i50605_9_) {
         this.field_218611_a = p_i50605_1_;
         this.field_218612_b = p_i50605_2_;
         this.field_218613_c = p_i50605_3_;
         this.field_218614_d = p_i50605_4_;
         this.field_218615_e = p_i50605_5_;
         this.field_218616_f = p_i50605_6_;
         this.field_218617_g = p_i50605_7_;
         this.field_218618_h = p_i50605_8_;
         this.field_218619_i = p_i50605_9_;
      }

      public void func_218610_a(JsonObject p_218610_1_) {
         if (!this.field_218612_b.isEmpty()) {
            p_218610_1_.addProperty("group", this.field_218612_b);
         }

         p_218610_1_.add("ingredient", this.field_218613_c.serialize());
         p_218610_1_.addProperty("result", Registry.ITEM.getKey(this.field_218614_d).toString());
         p_218610_1_.addProperty("experience", this.field_218615_e);
         p_218610_1_.addProperty("cookingtime", this.field_218616_f);
      }

      public IRecipeSerializer<?> func_218609_c() {
         return this.field_218619_i;
      }

      /**
       * Gets the ID for the recipe.
       */
      public ResourceLocation getID() {
         return this.field_218611_a;
      }

      /**
       * Gets the JSON for the advancement that unlocks this recipe. Null if there is no advancement.
       */
      @Nullable
      public JsonObject getAdvancementJson() {
         return this.field_218617_g.serialize();
      }

      /**
       * Gets the ID for the advancement associated with this recipe. Should not be null if {@link #getAdvancementJson}
       * is non-null.
       */
      @Nullable
      public ResourceLocation getAdvancementID() {
         return this.field_218618_h;
      }
   }
}