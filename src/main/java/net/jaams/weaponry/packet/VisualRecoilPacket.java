package net.jaams.weaponry.packet;

import net.jaams.weaponry.JaamsWeaponryMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class VisualRecoilPacket implements CustomPacketPayload {
	public static final Type<VisualRecoilPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(JaamsWeaponryMod.MODID, "visual_recoil"));
	public static final StreamCodec<FriendlyByteBuf, VisualRecoilPacket> STREAM_CODEC = StreamCodec.of((buf, packet) -> encode(packet, buf), VisualRecoilPacket::decode);

	private final float xRotRecoilIntensity;

	public VisualRecoilPacket(float xRotRecoilIntensity) {
		this.xRotRecoilIntensity = xRotRecoilIntensity;
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void encode(VisualRecoilPacket packet, FriendlyByteBuf buf) {
		buf.writeFloat(packet.xRotRecoilIntensity);
	}

	public static VisualRecoilPacket decode(FriendlyByteBuf buf) {
		float xRotRecoilIntensity = buf.readFloat();
		return new VisualRecoilPacket(xRotRecoilIntensity);
	}

	public static void handle(VisualRecoilPacket packet, IPayloadContext ctx) {
		ctx.enqueueWork(() -> ClientPacketHandler.handleVisualRecoilPacket(packet.xRotRecoilIntensity));
	}
}
