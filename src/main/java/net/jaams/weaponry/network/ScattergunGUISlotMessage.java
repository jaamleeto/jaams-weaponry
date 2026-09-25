
package net.jaams.weaponry.network;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;

import net.jaams.weaponry.world.inventory.ScattergunGUIMenu;
import net.jaams.weaponry.JaamsWeaponryMod;

import java.util.HashMap;

public class ScattergunGUISlotMessage implements CustomPacketPayload {
	public static final Type<ScattergunGUISlotMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(JaamsWeaponryMod.MODID, "scattergun_gui_slot"));
	public static final StreamCodec<FriendlyByteBuf, ScattergunGUISlotMessage> STREAM_CODEC = StreamCodec.of((buffer, message) -> buffer(message, buffer), ScattergunGUISlotMessage::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	private final int slotID, x, y, z, changeType, meta;

	public ScattergunGUISlotMessage(int slotID, int x, int y, int z, int changeType, int meta) {
		this.slotID = slotID;
		this.x = x;
		this.y = y;
		this.z = z;
		this.changeType = changeType;
		this.meta = meta;
	}

	public ScattergunGUISlotMessage(FriendlyByteBuf buffer) {
		this.slotID = buffer.readInt();
		this.x = buffer.readInt();
		this.y = buffer.readInt();
		this.z = buffer.readInt();
		this.changeType = buffer.readInt();
		this.meta = buffer.readInt();
	}

	public static void buffer(ScattergunGUISlotMessage message, FriendlyByteBuf buffer) {
		buffer.writeInt(message.slotID);
		buffer.writeInt(message.x);
		buffer.writeInt(message.y);
		buffer.writeInt(message.z);
		buffer.writeInt(message.changeType);
		buffer.writeInt(message.meta);
	}

	public static void handle(ScattergunGUISlotMessage message, IPayloadContext context) {
		context.enqueueWork(() -> {
			Player entity = context.player();
			int slotID = message.slotID;
			int changeType = message.changeType;
			int meta = message.meta;
			int x = message.x;
			int y = message.y;
			int z = message.z;
			handleSlotAction(entity, slotID, changeType, meta, x, y, z);
		});
	}

	public static void handleSlotAction(Player entity, int slot, int changeType, int meta, int x, int y, int z) {
		Level world = entity.level();
		HashMap<String, Object> guistate = ScattergunGUIMenu.guistate;
		if (!world.hasChunkAt(new BlockPos(x, y, z)))
			return;
	}

}
