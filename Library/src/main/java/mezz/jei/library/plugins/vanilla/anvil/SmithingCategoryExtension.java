package mezz.jei.library.plugins.vanilla.anvil;

import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.recipe.category.extensions.vanilla.smithing.ISmithingCategoryExtension;
import mezz.jei.common.platform.IPlatformRecipeHelper;
import mezz.jei.library.util.RecipeUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;

import java.util.List;

public abstract class SmithingCategoryExtension<R extends SmithingRecipe> implements ISmithingCategoryExtension<R> {
	private final IPlatformRecipeHelper recipeHelper;

	public SmithingCategoryExtension(IPlatformRecipeHelper recipeHelper) {
		this.recipeHelper = recipeHelper;
	}

	@Override
	public <T extends IIngredientAcceptor<T>> void setTemplate(R recipe, T ingredientAcceptor) {
		recipeHelper.getTemplate(recipe)
			.ifPresent(ingredientAcceptor::addIngredients);
	}

	@Override
	public <T extends IIngredientAcceptor<T>> void setBase(R recipe, T ingredientAcceptor) {
		recipeHelper.getBase(recipe)
			.ifPresent(ingredientAcceptor::addIngredients);
	}

	@Override
	public <T extends IIngredientAcceptor<T>> void setAddition(R recipe, T ingredientAcceptor) {
		recipeHelper.getAddition(recipe)
			.ifPresent(ingredientAcceptor::addIngredients);
	}

	@Override
	public <T extends IIngredientAcceptor<T>> void setOutput(R recipe, T ingredientAcceptor) {
		Ingredient templateIngredient = recipeHelper.getTemplate(recipe).orElse(Ingredient.of());
		Ingredient baseIngredient = recipeHelper.getBase(recipe).orElse(Ingredient.of());
		Ingredient additionIngredient = recipeHelper.getAddition(recipe).orElse(Ingredient.of());

		// In 1.21.2+, Ingredient.items() returns HolderSet<Item> which needs to be mapped to ItemStacks
		List<ItemStack> templateStacks = templateIngredient.items().stream()
			.map(ItemStack::new)
			.toList();
		if (templateStacks.isEmpty()) {
			templateStacks = List.of(ItemStack.EMPTY);
		}

		List<ItemStack> baseStacks = baseIngredient.items().stream()
			.map(ItemStack::new)
			.toList();
		if (baseStacks.isEmpty()) {
			baseStacks = List.of(ItemStack.EMPTY);
		}

		ItemStack addition = ItemStack.EMPTY;
		List<ItemStack> additions = additionIngredient.items().stream()
			.map(ItemStack::new)
			.toList();
		if (!additions.isEmpty()) {
			addition = additions.get(0);
		}

		for (ItemStack template : templateStacks) {
			for (ItemStack base : baseStacks) {
				SmithingRecipeInput recipeInput = new SmithingRecipeInput(template, base, addition);
				ItemStack output = RecipeUtil.assembleResultItem(recipeInput, recipe);
				ingredientAcceptor.addItemStack(output);
			}
		}
	}
}
