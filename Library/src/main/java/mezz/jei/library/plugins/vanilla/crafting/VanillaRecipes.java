package mezz.jei.library.plugins.vanilla.crafting;

import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.Internal;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class VanillaRecipes {
	private final RecipeMap syncedRecipes;
	private final IIngredientManager ingredientManager;

	public VanillaRecipes(IIngredientManager ingredientManager) {
		// In 1.21.2+, get RecipeMap from Internal (synced from server via event)
		this.syncedRecipes = Internal.getClientSyncedRecipes();
		this.ingredientManager = ingredientManager;
	}

	public Map<Boolean, List<RecipeHolder<CraftingRecipe>>> getCraftingRecipes(IRecipeCategory<RecipeHolder<CraftingRecipe>> craftingCategory) {
		var validator = new CategoryRecipeValidator<>(craftingCategory, ingredientManager, 9);

		List<RecipeHolder<CraftingRecipe>> handled = new ArrayList<>();
		List<RecipeHolder<CraftingRecipe>> unhandled = new ArrayList<>();

		// In 1.21.2+, use RecipeMap.byType() which returns Collection<RecipeHolder<T>>
		Collection<RecipeHolder<CraftingRecipe>> allRecipes = syncedRecipes.byType(RecipeType.CRAFTING);
		for (RecipeHolder<CraftingRecipe> recipe : allRecipes) {
			if (validator.isRecipeValid(recipe)) {
				if (validator.isRecipeHandled(recipe)) {
					handled.add(recipe);
				} else {
					unhandled.add(recipe);
				}
			}
		}
		return Map.of(
			true, handled,
			false, unhandled
		);
	}

	public List<RecipeHolder<StonecutterRecipe>> getStonecuttingRecipes(IRecipeCategory<RecipeHolder<StonecutterRecipe>> stonecuttingCategory) {
		var validator = new CategoryRecipeValidator<>(stonecuttingCategory, ingredientManager, 1);
		return getValidHandledRecipes(syncedRecipes, RecipeType.STONECUTTING, validator);
	}

	public List<RecipeHolder<SmeltingRecipe>> getFurnaceRecipes(IRecipeCategory<RecipeHolder<SmeltingRecipe>> furnaceCategory) {
		CategoryRecipeValidator<SmeltingRecipe> validator = new CategoryRecipeValidator<>(furnaceCategory, ingredientManager, 1);
		return getValidHandledRecipes(syncedRecipes, RecipeType.SMELTING, validator);
	}

	public List<RecipeHolder<SmokingRecipe>> getSmokingRecipes(IRecipeCategory<RecipeHolder<SmokingRecipe>> smokingCategory) {
		CategoryRecipeValidator<SmokingRecipe> validator = new CategoryRecipeValidator<>(smokingCategory, ingredientManager, 1);
		return getValidHandledRecipes(syncedRecipes, RecipeType.SMOKING, validator);
	}

	public List<RecipeHolder<BlastingRecipe>> getBlastingRecipes(IRecipeCategory<RecipeHolder<BlastingRecipe>> blastingCategory) {
		CategoryRecipeValidator<BlastingRecipe> validator = new CategoryRecipeValidator<>(blastingCategory, ingredientManager, 1);
		return getValidHandledRecipes(syncedRecipes, RecipeType.BLASTING, validator);
	}

	public List<RecipeHolder<CampfireCookingRecipe>> getCampfireCookingRecipes(IRecipeCategory<RecipeHolder<CampfireCookingRecipe>> campfireCategory) {
		CategoryRecipeValidator<CampfireCookingRecipe> validator = new CategoryRecipeValidator<>(campfireCategory, ingredientManager, 1);
		return getValidHandledRecipes(syncedRecipes, RecipeType.CAMPFIRE_COOKING, validator);
	}

	public List<RecipeHolder<SmithingRecipe>> getSmithingRecipes(IRecipeCategory<RecipeHolder<SmithingRecipe>> smithingCategory) {
		CategoryRecipeValidator<SmithingRecipe> validator = new CategoryRecipeValidator<>(smithingCategory, ingredientManager, 0);
		return getValidHandledRecipes(syncedRecipes, RecipeType.SMITHING, validator);
	}

	private static <C extends RecipeInput, T extends Recipe<C>> List<RecipeHolder<T>> getValidHandledRecipes(
		RecipeMap syncedRecipes,
		RecipeType<T> recipeType,
		CategoryRecipeValidator<T> validator
	) {
		// In 1.21.2+, RecipeMap.byType() returns Collection<RecipeHolder<T>>
		return syncedRecipes.byType(recipeType)
			.stream()
			.filter(r -> validator.isRecipeValid(r) && validator.isRecipeHandled(r))
			.toList();
	}

}
