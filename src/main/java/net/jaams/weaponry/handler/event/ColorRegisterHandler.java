package net.jaams.weaponry.handler.event;

import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.ModList;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.item.Item;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.core.cauldron.CauldronInteraction;

import net.jaams.weaponry.registry.TopItems;
import net.jaams.weaponry.registry.ShineriteItems;
import net.jaams.weaponry.dyeable.IDyeableItem;

@EventBusSubscriber(value = Dist.CLIENT)
public class ColorRegisterHandler {
	@SubscribeEvent
	public static void onItemColorShineriteRegister(RegisterColorHandlersEvent.Item event) {
		if (ModList.get().isLoaded("jaams_shinerite")) {
			event.register((stack, tintIndex) -> tintIndex > 0 ? -1 : ((IDyeableItem) stack.getItem()).getColor(stack), ShineriteItems.SHINERITE_DAGGER.get(), ShineriteItems.SHINERITE_KNUCKLE.get(), ShineriteItems.SHINERITE_CLEAVER.get(),
					ShineriteItems.SHINERITE_RING.get(), ShineriteItems.SHINERITE_KAMA.get(), ShineriteItems.SHINERITE_CLAW.get(), ShineriteItems.SHINERITE_MACHETE.get(), ShineriteItems.SHINERITE_KATAR.get(), ShineriteItems.SHINERITE_HAMMER.get(),
					ShineriteItems.SHINERITE_BATTLE_AXE.get(), ShineriteItems.SHINERITE_SICKLE.get(), ShineriteItems.SHINERITE_LONGSWORD.get(), ShineriteItems.SHINERITE_ZWEIHANDER.get(), ShineriteItems.SHINERITE_GREATSWORD.get(),
					ShineriteItems.SHINERITE_BROADSWORD.get(), ShineriteItems.SHINERITE_BUSTER_SWORD.get(), ShineriteItems.SHINERITE_KATANA.get(), ShineriteItems.SHINERITE_BUTTERFLY_SWORD.get(), ShineriteItems.SHINERITE_HOOK_SWORD.get(),
					ShineriteItems.SHINERITE_SCYTHE.get(), ShineriteItems.SHINERITE_GREATHAMMER.get(), ShineriteItems.SHINERITE_SPEAR.get(), ShineriteItems.SHINERITE_TWINBLADE.get(), ShineriteItems.SHINERITE_SAW_CLEAVER.get(),
					ShineriteItems.SHINERITE_DAGGER_REVERSE.get(), ShineriteItems.SHINERITE_SAW_CLEAVER_UNFOLDED.get(), ShineriteItems.SHINERITE_PISTOL.get());
		}
	}

	@SubscribeEvent
	public static void onItemColorRegister(RegisterColorHandlersEvent.Item event) {
		event.register((stack, tintIndex) -> tintIndex > 0 ? -1 : ((IDyeableItem) stack.getItem()).getColor(stack), TopItems.GAUNTLET.get());
		CauldronInteraction INTERACTION = (state, level, pos, player, hand, stack) -> {
			Item item = stack.getItem();
			if (!(item instanceof IDyeableItem dyeableItem))
				return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
			else if (!dyeableItem.hasColor(stack))
				return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
			else {
				if (!level.isClientSide) {
					dyeableItem.removeColor(stack);
					LayeredCauldronBlock.lowerFillLevel(state, level, pos);
				}
				return ItemInteractionResult.sidedSuccess(level.isClientSide);
			}
		};
		CauldronInteraction.WATER.map().put(TopItems.GAUNTLET.get(), INTERACTION);
	}
}
