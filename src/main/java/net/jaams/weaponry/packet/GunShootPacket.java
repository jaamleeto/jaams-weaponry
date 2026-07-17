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

public class GunShootPacket {
	private final InteractionHand hand;
	private final int cooldownTicks;

	public GunShootPacket(InteractionHand hand, int cooldownTicks) {
		this.hand = hand;
		this.cooldownTicks = cooldownTicks;
	}

	public static void encode(GunShootPacket packet, FriendlyByteBuf buf) {
		buf.writeEnum(packet.hand);
		buf.writeInt(packet.cooldownTicks);
	}

	public static GunShootPacket decode(FriendlyByteBuf buf) {
		InteractionHand hand = buf.readEnum(InteractionHand.class);
		int cooldownTicks = buf.readInt();
		return new GunShootPacket(hand, cooldownTicks);
	}

	public static void handle(GunShootPacket packet, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (ctx.get().getDirection().getReceptionSide().isServer()) {
				ServerPlayer serverPlayer = ctx.get().getSender();
				if (serverPlayer == null) {
					return;
				}
				Level world = serverPlayer.level();
				ItemStack itemStack = serverPlayer.getItemInHand(packet.hand);
				if (serverPlayer.getCooldowns().isOnCooldown(itemStack.getItem())) {
					return;
				}
				if (packet.hand == InteractionHand.OFF_HAND && ModUtils.isEntityInBattleMode(serverPlayer)) {
					return;
				}
				ModGuns.shoot(world, serverPlayer, itemStack);
			} else {
				ClientPacketHandler.handleGunCooldownPacket(packet.hand, packet.cooldownTicks);
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
