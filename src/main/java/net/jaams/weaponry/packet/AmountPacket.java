package net.jaams.weaponry.packet;

import net.jaams.weaponry.JaamsWeaponryMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class AmountPacket implements CustomPacketPayload {
	public static final Type<AmountPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(JaamsWeaponryMod.MODID, "amount"));
	public static final StreamCodec<FriendlyByteBuf, AmountPacket> STREAM_CODEC = StreamCodec.of((buf, packet) -> encode(packet, buf), AmountPacket::decode);

	private final int entityId;
	private final float damage;

	public AmountPacket(int entityId, float damage) {
		this.entityId = entityId;
		this.damage = damage;
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void encode(AmountPacket packet, FriendlyByteBuf buf) {
		buf.writeInt(packet.entityId);
		buf.writeFloat(packet.damage);
	}

	public static AmountPacket decode(FriendlyByteBuf buf) {
		int entityId = buf.readInt();
		float damage = buf.readFloat();
		return new AmountPacket(entityId, damage);
	}

	public static void handle(AmountPacket packet, IPayloadContext ctx) {
		ctx.enqueueWork(() -> ClientPacketHandler.handleAmountPacket(packet.entityId, packet.damage));
	}
}
