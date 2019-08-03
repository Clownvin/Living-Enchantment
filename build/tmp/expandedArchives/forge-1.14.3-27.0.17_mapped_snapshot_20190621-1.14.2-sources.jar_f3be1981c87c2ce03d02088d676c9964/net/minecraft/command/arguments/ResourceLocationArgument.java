package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.advancements.Advancement;
import net.minecraft.command.CommandSource;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public class ResourceLocationArgument implements ArgumentType<ResourceLocation> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012");
   public static final DynamicCommandExceptionType UNKNOWN_ID = new DynamicCommandExceptionType((p_208676_0_) -> {
      return new TranslationTextComponent("argument.id.unknown", p_208676_0_);
   });
   public static final DynamicCommandExceptionType ADVANCEMENT_NOT_FOUND = new DynamicCommandExceptionType((p_208677_0_) -> {
      return new TranslationTextComponent("advancement.advancementNotFound", p_208677_0_);
   });
   public static final DynamicCommandExceptionType RECIPE_NOT_FOUND = new DynamicCommandExceptionType((p_208674_0_) -> {
      return new TranslationTextComponent("recipe.notFound", p_208674_0_);
   });

   public static ResourceLocationArgument resourceLocation() {
      return new ResourceLocationArgument();
   }

   public static Advancement getAdvancement(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
      ResourceLocation resourcelocation = context.getArgument(name, ResourceLocation.class);
      Advancement advancement = context.getSource().getServer().getAdvancementManager().getAdvancement(resourcelocation);
      if (advancement == null) {
         throw ADVANCEMENT_NOT_FOUND.create(resourcelocation);
      } else {
         return advancement;
      }
   }

   public static IRecipe<?> getRecipe(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
      RecipeManager recipemanager = context.getSource().getServer().getRecipeManager();
      ResourceLocation resourcelocation = context.getArgument(name, ResourceLocation.class);
      return recipemanager.getRecipe(resourcelocation).orElseThrow(() -> {
         return RECIPE_NOT_FOUND.create(resourcelocation);
      });
   }

   public static ResourceLocation getResourceLocation(CommandContext<CommandSource> context, String name) {
      return context.getArgument(name, ResourceLocation.class);
   }

   public ResourceLocation parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return ResourceLocation.read(p_parse_1_);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}