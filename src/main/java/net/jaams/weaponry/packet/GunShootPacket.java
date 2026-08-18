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
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class GunShootPacket implements CustomPacketPayload {
	public static final Type<GunShootPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(JaamsWeaponryMod.MODID, "gun_shoot"));
	public static final StreamCodec<FriendlyByteBuf, GunShootPacket> STREAM_CODEC = StreamCodec.of((buf, packet) -> encode(packet, buf), GunShootPacket::decode);

	private final InteractionHand hand;
	private final int cooldownTicks;

	public GunShootPacket(InteractionHand hand, int cooldownTicks) {
		this.hand = hand;
		this.cooldownTicks = cooldownTicks;
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
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

	public static void handle(GunShootPacket packet, IPayloadContext ctx) {
		ctx.enqueueWork(() -> {
			if (ctx.flow().isServerbound()) {
				if (!(ctx.player() instanceof ServerPlayer serverPlayer))
					return;
				Level world = serverPlayer.level();
				ItemStack itemStack = serverPlayer.getItemInHand(packet.hand);
				if (serverPlayer.getCooldowns().isOnCooldown(itemStack.getItem())) {
					return;
				}
				ModGuns.shoot(world, serverPlayer, itemStack);
			} else {
				ClientPacketHandler.handleGunCooldownPacket(packet.hand, packet.cooldownTicks);
			}
		});
	}
}
