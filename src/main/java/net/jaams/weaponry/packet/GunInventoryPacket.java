package net.jaams.weaponry.packet;

import net.jaams.weaponry.JaamsWeaponryMod;
import net.jaams.weaponry.util.ModGuns;
import net.jaams.weaponry.util.ModUtils;
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

	public GunInventoryPacket(InteractionHand hand) {
		this.hand = hand;
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void encode(GunInventoryPacket packet, FriendlyByteBuf buf) {
		buf.writeEnum(packet.hand);
	}

	public static GunInventoryPacket decode(FriendlyByteBuf buf) {
		InteractionHand hand = buf.readEnum(InteractionHand.class);
		return new GunInventoryPacket(hand);
	}

	public static void handle(GunInventoryPacket packet, IPayloadContext ctx) {
		ctx.enqueueWork(() -> {
			if (!(ctx.player() instanceof ServerPlayer serverPlayer))
				return;
			ItemStack itemStack = serverPlayer.getItemInHand(packet.hand);
			if (!ModGuns.canOpenInventory(itemStack) || (packet.hand == InteractionHand.OFF_HAND && ModUtils.isEntityInBattleMode(serverPlayer))) {
				return;
			}
			ModGuns.openGunInventory(serverPlayer, itemStack, packet.hand);
		});
	}
}
