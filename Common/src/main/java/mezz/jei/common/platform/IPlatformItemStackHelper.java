package mezz.jei.common.platform;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.FuelValues;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public interface IPlatformItemStackHelper {
	int getBurnTime(ItemStack itemStack, RecipeType<?> recipeType, FuelValues fuelValues);

	boolean isBookEnchantable(ItemStack stack, ItemStack book);

	Optional<String> getCreatorModId(ItemStack stack);

	List<Component> getTestTooltip(@Nullable Player player, ItemStack itemStack);

	ItemAttributeModifiers getItemAttributeModifiers(ItemStack stack);
}
