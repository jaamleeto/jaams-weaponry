package net.jaams.weaponry.packet;

import net.minecraftforge.network.NetworkEvent;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;

import net.jaams.weaponry.util.ModUtils;
import net.jaams.weaponry.util.ModGuns;

import java.util.function.Supplier;

public class GunInventoryPacket {
	private final InteractionHand hand;

	public GunInventoryPacket(InteractionHand hand) {
		this.hand = hand;
	}

	public static void encode(GunInventoryPacket packet, FriendlyByteBuf buf) {
		buf.writeEnum(packet.hand);
	}

	public static GunInventoryPacket decode(FriendlyByteBuf buf) {
		InteractionHand hand = buf.readEnum(InteractionHand.class);
		return new GunInventoryPacket(hand);
	}

	public static void handle(GunInventoryPacket packet, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (ctx.get().getDirection().getReceptionSide().isServer()) {
				ServerPlayer serverPlayer = ctx.get().getSender();
				if (serverPlayer == null)
					return;
				Level world = serverPlayer.level();
				ItemStack itemStack = serverPlayer.getItemInHand(packet.hand);
				if (!ModGuns.canOpenInventory(itemStack) || (packet.hand == InteractionHand.OFF_HAND && ModUtils.isEntityInBattleMode(serverPlayer))) {
					return;
				}
				ModGuns.openGunInventory(serverPlayer, itemStack, packet.hand);
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
