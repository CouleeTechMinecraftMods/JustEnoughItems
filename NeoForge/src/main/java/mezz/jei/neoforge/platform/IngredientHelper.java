package mezz.jei.neoforge.platform;

import mezz.jei.common.platform.IPlatformIngredientHelper;
import mezz.jei.common.util.RegistryUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.ComposterBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class IngredientHelper implements IPlatformIngredientHelper {
	@Override
	public Ingredient createShulkerDyeIngredient(DyeColor color) {
		// In 1.21.2, Ingredient API has changed - Ingredient.Value and fromValues() no longer exist
		// The shulker box recipe accepts items from the dye color tag
		// We combine the tag items with the specific dye to ensure the dye is always included
		TagKey<Item> colorTag = color.getTag();
		DyeItem dye = DyeItem.byColor(color);

		// Create a list starting with the specific dye item
		List<Item> items = new ArrayList<>();
		items.add(dye);

		// Add all items from the tag (avoiding duplicates)
		Registry<Item> itemRegistry = RegistryUtil.getRegistry(Registries.ITEM);
		for (Holder<Item> holder : itemRegistry.getTagOrEmpty(colorTag)) {
			Item item = holder.value();
			if (item != dye) {
				items.add(item);
			}
		}

		// Create ingredient from the items using varargs
		return Ingredient.of(items.toArray(Item[]::new));
	}

	@Override
	public List<Ingredient> getPotionContainers(PotionBrewing potionBrewing) {
		return potionBrewing.containers;
	}

	@Override
	public Stream<Ingredient> getPotionIngredients(PotionBrewing potionBrewing) {
		return Stream.concat(
			potionBrewing.containerMixes.stream(),
			potionBrewing.potionMixes.stream()
		)
			.map(PotionBrewing.Mix::ingredient);
	}

	@Override
	public float getCompostValue(ItemStack itemStack) {
		return ComposterBlock.getValue(itemStack);
	}
}
