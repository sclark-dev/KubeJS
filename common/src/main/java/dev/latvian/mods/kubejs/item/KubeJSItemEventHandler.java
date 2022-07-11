package dev.latvian.mods.kubejs.item;

import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.latvian.mods.kubejs.bindings.ItemWrapper;
import dev.latvian.mods.kubejs.player.InventoryChangedEventJS;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * @author LatvianModder
 */
public class KubeJSItemEventHandler {
	public static void init() {
		InteractionEvent.RIGHT_CLICK_ITEM.register(KubeJSItemEventHandler::rightClick);
		InteractionEvent.CLIENT_RIGHT_CLICK_AIR.register(KubeJSItemEventHandler::rightClickEmpty);
		InteractionEvent.CLIENT_LEFT_CLICK_AIR.register(KubeJSItemEventHandler::leftClickEmpty);
		PlayerEvent.PICKUP_ITEM_PRE.register(KubeJSItemEventHandler::pickup);
		PlayerEvent.DROP_ITEM.register(KubeJSItemEventHandler::drop);
		InteractionEvent.INTERACT_ENTITY.register(KubeJSItemEventHandler::entityInteract);
		PlayerEvent.CRAFT_ITEM.register(KubeJSItemEventHandler::crafted);
		PlayerEvent.SMELT_ITEM.register(KubeJSItemEventHandler::smelted);
	}

	private static String getItemId(ItemStack stack) {
		return String.valueOf(ItemWrapper.getId(stack.getItem()));
	}

	private static CompoundEventResult<ItemStack> rightClick(Player player, InteractionHand hand) {
		if (player != null && player.level instanceof ServerLevel && !player.getCooldowns().isOnCooldown(player.getItemInHand(hand).getItem()) && ItemRightClickedEventJS.EVENT.post(new ItemRightClickedEventJS(player, hand), getItemId(player.getItemInHand(hand)))) {
			return CompoundEventResult.interruptFalse(player.getItemInHand(hand));
		}

		return CompoundEventResult.pass();
	}

	private static void rightClickEmpty(Player player, InteractionHand hand) {
		if (player != null && player.level != null && player.level.isClientSide()) {
			ItemRightClickEmptyEventJS.EVENT.post(new ItemRightClickEmptyEventJS(player, hand), getItemId(player.getItemInHand(hand)));
		}
	}

	private static void leftClickEmpty(Player player, InteractionHand hand) {
		if (player != null && player.level != null && player.level.isClientSide()) {
			ItemLeftClickEventJS.EVENT.post(new ItemLeftClickEventJS(player, hand), getItemId(player.getItemInHand(hand)));
		}
	}

	private static EventResult pickup(Player player, ItemEntity entity, ItemStack stack) {
		if (player != null && entity != null && player.level instanceof ServerLevel && ItemPickedUpEventJS.EVENT.post(new ItemPickedUpEventJS(player, entity, stack), getItemId(stack))) {
			return EventResult.interruptFalse();
		}

		return EventResult.pass();
	}

	private static EventResult drop(Player player, ItemEntity entity) {
		if (player != null && entity != null && player.level instanceof ServerLevel && ItemDroppedEventJS.EVENT.post(new ItemDroppedEventJS(player, entity), getItemId(entity.getItem()))) {
			return EventResult.interruptFalse();
		}

		return EventResult.pass();
	}

	private static EventResult entityInteract(Player player, Entity entity, InteractionHand hand) {
		if (player != null && entity != null && player.level instanceof ServerLevel && ItemEntityInteractedEventJS.EVENT.post(new ItemEntityInteractedEventJS(player, entity, hand), getItemId(player.getItemInHand(hand)))) {
			return EventResult.interruptFalse();
		}

		return EventResult.pass();
	}

	private static void crafted(Player player, ItemStack stack, Container grid) {
		if (player instanceof ServerPlayer serverPlayer && !stack.isEmpty()) {
			ItemCraftedEventJS.EVENT.post(new ItemCraftedEventJS(player, stack, grid));
			InventoryChangedEventJS.EVENT.post(new InventoryChangedEventJS(serverPlayer, stack, -1), getItemId(stack));
		}
	}

	private static void smelted(Player player, ItemStack stack) {
		if (player instanceof ServerPlayer serverPlayer && !stack.isEmpty()) {
			ItemSmeltedEventJS.EVENT.post(new ItemSmeltedEventJS(player, stack));
			InventoryChangedEventJS.EVENT.post(new InventoryChangedEventJS(serverPlayer, stack, -1), getItemId(stack));
		}
	}
}