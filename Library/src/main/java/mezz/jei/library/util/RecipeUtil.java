package mezz.jei.library.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;

public class RecipeUtil {
	public static ItemStack getResultItem(Recipe<?> recipe) {
		// In 1.21.2+, Recipe.result() returns RecipeBookGroup or similar, not ItemStack
		// Most recipes implement ResultProvider which has result() method that returns ItemStack
		// Try to get result directly, fallback to EMPTY
		try {
			// Many recipe types have a result() method that returns ItemStack
			var method = recipe.getClass().getMethod("result");
			Object result = method.invoke(recipe);
			if (result instanceof ItemStack itemStack) {
				return itemStack;
			}
		} catch (Exception e) {
			// Fallback if method doesn't exist or fails
		}
		return ItemStack.EMPTY;
	}

	public static <I extends RecipeInput> ItemStack assembleResultItem(I input, Recipe<I> recipe) {
		Minecraft minecraft = Minecraft.getInstance();
		ClientLevel level = minecraft.level;
		if (level == null) {
			throw new NullPointerException("level must not be null.");
		}
		RegistryAccess registryAccess = level.registryAccess();
		return recipe.assemble(input, registryAccess);
	}
}
