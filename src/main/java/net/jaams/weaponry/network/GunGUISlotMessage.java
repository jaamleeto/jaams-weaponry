
package net.jaams.weaponry.network;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;

import net.jaams.weaponry.world.inventory.GunGUIMenu;
import net.jaams.weaponry.configuration.client.GunSystemClientConfig;
import net.jaams.weaponry.JaamsWeaponryMod;

import java.util.HashMap;

public class GunGUISlotMessage implements CustomPacketPayload {
	public static final Type<GunGUISlotMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(JaamsWeaponryMod.MODID, "gun_gui_slot"));
	public static final StreamCodec<FriendlyByteBuf, GunGUISlotMessage> STREAM_CODEC = StreamCodec.of((buffer, message) -> buffer(message, buffer), GunGUISlotMessage::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	private final int slotID, x, y, z, changeType, meta;

	public GunGUISlotMessage(int slotID, int x, int y, int z, int changeType, int meta) {
		this.slotID = slotID;
		this.x = x;
		this.y = y;
		this.z = z;
		this.changeType = changeType;
		this.meta = meta;
	}

	public GunGUISlotMessage(FriendlyByteBuf buffer) {
		this.slotID = buffer.readInt();
		this.x = buffer.readInt();
		this.y = buffer.readInt();
		this.z = buffer.readInt();
		this.changeType = buffer.readInt();
		this.meta = buffer.readInt();
	}

	public static void buffer(GunGUISlotMessage message, FriendlyByteBuf buffer) {
		buffer.writeInt(message.slotID);
		buffer.writeInt(message.x);
		buffer.writeInt(message.y);
		buffer.writeInt(message.z);
		buffer.writeInt(message.changeType);
		buffer.writeInt(message.meta);
	}

	public static void handle(GunGUISlotMessage message, IPayloadContext context) {
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
		HashMap<String, Object> guistate = GunGUIMenu.guistate;
		if (!world.hasChunkAt(new BlockPos(x, y, z)))
			return;
		if (slot == 1 && changeType == 0) {
			playClientSound(world, x, y, z, ResourceLocation.parse("jaams_weaponry:gun_system_pistol_bullet"));
		}
	}

	private static void playClientSound(LevelAccessor world, double x, double y, double z, ResourceLocation sound) {
		if (GunSystemClientConfig.GUN_INV_SOUNDS.get() && world.isClientSide()) {
			((Level) world).playLocalSound(x, y, z, BuiltInRegistries.SOUND_EVENT.get(sound), SoundSource.PLAYERS, 1, 1, false);
		}
	}

}
