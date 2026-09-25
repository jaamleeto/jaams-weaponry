package net.jaams.weaponry.packet;

import net.jaams.weaponry.JaamsWeaponryMod;
import net.jaams.weaponry.util.ModGuns;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class GunInventoryPacket implements CustomPacketPayload {
	public static final Type<GunInventoryPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(JaamsWeaponryMod.MODID, "gun_inventory"));
	public static final StreamCodec<FriendlyByteBuf, GunInventoryPacket> STREAM_CODEC = StreamCodec.of((buf, packet) -> encode(packet, buf), GunInventoryPacket::decode);

	private final InteractionHand hand;
	private final boolean fallbackToShoot;
	private final boolean searchOtherHand;

	/**
	 * Creates a request sent by the standalone inventory key. The server may use the
	 * other hand when the client could not identify the gun locally.
	 */
	public GunInventoryPacket(InteractionHand hand) {
		this(hand, false, true);
	}

	/**
	 * Creates a request originating from right-clicking a gun. It must stay bound to
	 * that hand, but can fall back to a normal shot when the server rejects opening
	 * the inventory (for example because the server-side modifier is disabled).
	 */
	public GunInventoryPacket(InteractionHand hand, boolean fallbackToShoot) {
		this(hand, fallbackToShoot, false);
	}

	private GunInventoryPacket(InteractionHand hand, boolean fallbackToShoot, boolean searchOtherHand) {
		this.hand = hand;
		this.fallbackToShoot = fallbackToShoot;
		this.searchOtherHand = searchOtherHand;
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void encode(GunInventoryPacket packet, FriendlyByteBuf buf) {
		buf.writeEnum(packet.hand);
		buf.writeBoolean(packet.fallbackToShoot);
		buf.writeBoolean(packet.searchOtherHand);
	}

	public static GunInventoryPacket decode(FriendlyByteBuf buf) {
		try {
			InteractionHand hand = buf.readEnum(InteractionHand.class);
			boolean fallbackToShoot = buf.readBoolean();
			boolean searchOtherHand = buf.readBoolean();
			return new GunInventoryPacket(hand, fallbackToShoot, searchOtherHand);
		} catch (Exception e) {
			JaamsWeaponryMod.LOGGER.error("[GunInventoryPacket] Failed to decode packet: {}", e.getMessage());
			return new GunInventoryPacket(InteractionHand.MAIN_HAND, false, false);
		}
	}

	public static void handle(GunInventoryPacket packet, IPayloadContext ctx) {
		if (!ctx.flow().isServerbound())
			return;
		ctx.enqueueWork(() -> {
			try {
				if (!(ctx.player() instanceof ServerPlayer serverPlayer) || !serverPlayer.isAlive()) {
					JaamsWeaponryMod.LOGGER.debug("[GunInventoryPacket] Invalid player or player not alive");
					return;
				}

				InteractionHand openHand = findOpenableHand(serverPlayer, packet.hand, packet.searchOtherHand);
				if (openHand != null) {
					ItemStack itemStack = serverPlayer.getItemInHand(openHand);
					// Double-check the stack is still a valid gun
					if (itemStack.isEmpty() || !ModGuns.isGun(itemStack) || !ModGuns.canOpenInventory(itemStack)) {
						JaamsWeaponryMod.LOGGER.debug("[GunInventoryPacket] Item in hand is no longer a valid gun: {}", itemStack);
						if (packet.fallbackToShoot) {
							tryShoot(serverPlayer, packet.hand);
						}
						return;
					}
					JaamsWeaponryMod.LOGGER.debug(
							"[GunInventory] Opening inventory: player={}, requestedHand={}, hand={}, item={}, type={}",
							serverPlayer.getName().getString(), packet.hand, openHand, itemStack.getItem(),
							ModGuns.getGunType(itemStack));
					ModGuns.openGunInventory(serverPlayer, itemStack, openHand);
					return;
				}

				// No openable hand found
				if (packet.fallbackToShoot) {
					tryShoot(serverPlayer, packet.hand);
				}
			} catch (Exception e) {
				JaamsWeaponryMod.LOGGER.error("[GunInventoryPacket] Error handling packet: {}", e.getMessage(), e);
			}
		});
	}

	private static void tryShoot(ServerPlayer serverPlayer, InteractionHand hand) {
		try {
			ItemStack itemStack = serverPlayer.getItemInHand(hand);
			if (ModGuns.isGun(itemStack) && !serverPlayer.getCooldowns().isOnCooldown(itemStack.getItem())) {
				ModGuns.shoot(serverPlayer.level(), serverPlayer, itemStack);
			}
		} catch (Exception e) {
			JaamsWeaponryMod.LOGGER.error("[GunInventoryPacket] Fallback shoot failed: {}", e.getMessage());
		}
	}

	private static InteractionHand findOpenableHand(ServerPlayer player, InteractionHand requestedHand,
			boolean searchOtherHand) {
		if (isOpenable(player.getItemInHand(requestedHand)))
			return requestedHand;
		if (searchOtherHand) {
			InteractionHand otherHand = requestedHand == InteractionHand.MAIN_HAND
					? InteractionHand.OFF_HAND
					: InteractionHand.MAIN_HAND;
			if (isOpenable(player.getItemInHand(otherHand)))
				return otherHand;
		}
		return null;
	}

	private static boolean isOpenable(ItemStack stack) {
		return !stack.isEmpty() && ModGuns.isGun(stack) && ModGuns.canOpenInventory(stack);
	}
}
