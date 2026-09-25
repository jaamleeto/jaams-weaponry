package net.jaams.weaponry.packet;

import net.jaams.weaponry.JaamsWeaponryMod;
import net.jaams.weaponry.util.ModEnums;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class AberrationPacket implements CustomPacketPayload {
	public static final Type<AberrationPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(JaamsWeaponryMod.MODID, "aberration"));
	public static final StreamCodec<FriendlyByteBuf, AberrationPacket> STREAM_CODEC = StreamCodec.of((buf, packet) -> encode(packet, buf), AberrationPacket::decode);

	private final int playerId;
	private final ModEnums.AberrationType effectType;
	private final double intensity;
	private final int duration;

	public AberrationPacket(int playerId, ModEnums.AberrationType effectType, double intensity, int duration) {
		this.playerId = playerId;
		this.effectType = effectType;
		this.intensity = intensity;
		this.duration = duration;
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void encode(AberrationPacket packet, FriendlyByteBuf buf) {
		buf.writeInt(packet.playerId);
		buf.writeEnum(packet.effectType);
		buf.writeDouble(packet.intensity);
		buf.writeInt(packet.duration);
	}

	public static AberrationPacket decode(FriendlyByteBuf buf) {
		int playerId = buf.readInt();
		ModEnums.AberrationType effectType = buf.readEnum(ModEnums.AberrationType.class);
		double intensity = buf.readDouble();
		int duration = buf.readInt();
		return new AberrationPacket(playerId, effectType, intensity, duration);
	}

	public static void handle(AberrationPacket packet, IPayloadContext ctx) {
		ctx.enqueueWork(() -> ClientPacketHandler.handleAberrationPacket(packet.playerId, packet.effectType, packet.intensity, packet.duration));
	}
}
