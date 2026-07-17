package net.jaams.weaponry.packet;

import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.network.FriendlyByteBuf;

import net.jaams.weaponry.util.ModEnums;

import java.util.function.Supplier;

public class AberrationPacket {
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

	public static void handle(AberrationPacket packet, Supplier<NetworkEvent.Context> ctx) {
		if (ctx.get().getDirection().getReceptionSide().isClient()) {
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handleAberrationPacket(packet.playerId, packet.effectType, packet.intensity, packet.duration));
		}
		ctx.get().setPacketHandled(true);
	}
}
