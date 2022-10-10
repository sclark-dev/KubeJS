package dev.latvian.mods.kubejs.platform.ingredient;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.core.ItemStackKJS;
import dev.latvian.mods.kubejs.item.ItemStackSet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

public class StrongNBTIngredient extends KubeJSIngredient {
	public static final KubeJSIngredientSerializer<StrongNBTIngredient> SERIALIZER = new KubeJSIngredientSerializer<>(StrongNBTIngredient::new, StrongNBTIngredient::new);

	private final ItemStack item;

	public StrongNBTIngredient(ItemStack item) {
		this.item = item;
	}

	public StrongNBTIngredient(FriendlyByteBuf buf) {
		this(buf.readItem());
	}

	public StrongNBTIngredient(JsonObject json) {
		this(ShapedRecipe.itemStackFromJson(json.get("item").getAsJsonObject()));
	}

	@Override
	public boolean test(@Nullable ItemStack stack) {
		if (stack != null && item.getItem() == stack.getItem() && item.hasTag() == stack.hasTag()) {
			if (item.hasTag()) {
				var t = item.getTag();

				for (var key : t.getAllKeys()) {
					if (!Objects.equals(t.get(key), stack.getTag().get(key))) {
						return false;
					}
				}
			}

			return true;
		}

		return false;
	}

	@Override
	public void kjs$gatherItemTypes(Set<Item> set) {
		set.add(item.getItem());
	}

	@Override
	public void kjs$gatherStacks(ItemStackSet set) {
		set.add(item);
	}

	@Override
	public IIngredientSerializer<? extends Ingredient> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public void toJson(JsonObject json) {
		json.add("item", item.kjs$toJson());
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeItem(item);
	}
}
