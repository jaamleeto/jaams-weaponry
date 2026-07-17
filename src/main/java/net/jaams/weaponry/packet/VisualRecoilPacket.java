package net.jaams.weaponry.packet;

import net.minecraftforge.network.NetworkEvent;

import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

public class VisualRecoilPacket {
	private final float xRotRecoilIntensity;

	public VisualRecoilPacket(float xRotRecoilIntensity) {
		this.xRotRecoilIntensity = xRotRecoilIntensity;
	}

	public static void encode(VisualRecoilPacket packet, FriendlyByteBuf buf) {
		buf.writeFloat(packet.xRotRecoilIntensity);
	}

	public static VisualRecoilPacket decode(FriendlyByteBuf buf) {
		float xRotRecoilIntensity = buf.readFloat();
		return new VisualRecoilPacket(xRotRecoilIntensity);
	}

	public static void handle(VisualRecoilPacket packet, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (ctx.get().getDirection().getReceptionSide().isClient()) {
				ClientPacketHandler.handleVisualRecoilPacket(packet.xRotRecoilIntensity);
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
