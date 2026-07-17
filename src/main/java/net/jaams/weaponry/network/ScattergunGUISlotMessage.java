
package net.jaams.weaponry.network;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;

import net.jaams.weaponry.world.inventory.ScattergunGUIMenu;
import net.jaams.weaponry.configuration.client.GunSystemClientConfig;
import net.jaams.weaponry.JaamsWeaponryMod;

import java.util.function.Supplier;
import java.util.HashMap;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ScattergunGUISlotMessage {
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

	public static void handler(ScattergunGUISlotMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			Player entity = context.getSender();
			int slotID = message.slotID;
			int changeType = message.changeType;
			int meta = message.meta;
			int x = message.x;
			int y = message.y;
			int z = message.z;
			handleSlotAction(entity, slotID, changeType, meta, x, y, z);
		});
		context.setPacketHandled(true);
	}

	public static void handleSlotAction(Player entity, int slot, int changeType, int meta, int x, int y, int z) {
		Level world = entity.level();
		HashMap<String, Object> guistate = ScattergunGUIMenu.guistate;
		if (!world.hasChunkAt(new BlockPos(x, y, z)))
			return;
		if (slot == 0 && changeType == 0) {
			playClientSound(world, x, y, z, new ResourceLocation("jaams_weaponry:gun_system_scattergun_attachment"));
		}
		if (slot == 1 && changeType == 0) {
			playClientSound(world, x, y, z, new ResourceLocation("jaams_weaponry:gun_system_scattergun_bullet"));
		}
		if (slot == 2 && changeType == 0) {
			playClientSound(world, x, y, z, new ResourceLocation("jaams_weaponry:gun_system_scattergun_attachment"));
		}
	}

	private static void playClientSound(LevelAccessor world, double x, double y, double z, ResourceLocation sound) {
		if (GunSystemClientConfig.GUN_INV_SOUNDS.get() && world.isClientSide()) {
			((Level) world).playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(sound), SoundSource.PLAYERS, 1, 1, false);
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		JaamsWeaponryMod.addNetworkMessage(ScattergunGUISlotMessage.class, ScattergunGUISlotMessage::buffer, ScattergunGUISlotMessage::new, ScattergunGUISlotMessage::handler);
	}
}
