package net.jaams.weaponry.packet;

import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

public class AmountPacket {
	private final int entityId;
	private final float damage;

	public AmountPacket(int entityId, float damage) {
		this.entityId = entityId;
		this.damage = damage;
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

	public static void handle(AmountPacket packet, Supplier<NetworkEvent.Context> ctx) {
		if (ctx.get().getDirection().getReceptionSide().isClient()) {
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handleAmountPacket(packet.entityId, packet.damage));
		}
		ctx.get().setPacketHandled(true);
	}
}
