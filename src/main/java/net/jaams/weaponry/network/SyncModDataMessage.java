package net.jaams.weaponry.network;

import net.minecraftforge.network.NetworkEvent;

import net.minecraft.network.FriendlyByteBuf;

import net.jaams.weaponry.JaamsWeaponryMod;
import net.jaams.weaponry.loader.ModLoaderSync;
import net.jaams.weaponry.loader.NetworkSyncable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Server -> client snapshot of every {@link NetworkSyncable} datapack-driven
 * loader so client-only systems (renderers, tooltips, poses, creative tabs,
 * gun GUIs...) resolve the exact same definitions the server loaded.
 */
public class SyncModDataMessage {
    private final Map<String, Map<String, String>> sections;

    public SyncModDataMessage(Map<String, Map<String, String>> sections) {
        this.sections = sections != null ? sections : new LinkedHashMap<>();
    }

    
    public static SyncModDataMessage fromServerState() {
        Map<String, Map<String, String>> sections = new LinkedHashMap<>();
        for (NetworkSyncable syncable : ModLoaderSync.all()) {
            try {
                Map<String, String> snapshot = syncable.getSourcesSnapshot();
                if (snapshot == null || snapshot.isEmpty())
                    continue;
                sections.put(syncable.getSyncId(), snapshot);
            } catch (Exception e) {
                JaamsWeaponryMod.LOGGER.error("Failed to snapshot loader '{}' for sync", syncable.getSyncId(), e);
            }
        }
        return new SyncModDataMessage(sections);
    }

    public static void encode(SyncModDataMessage msg, FriendlyByteBuf buffer) {
        buffer.writeVarInt(msg.sections.size());
        for (Map.Entry<String, Map<String, String>> section : msg.sections.entrySet()) {
            buffer.writeUtf(section.getKey());
            Map<String, String> entries = section.getValue();
            buffer.writeVarInt(entries.size());
            for (Map.Entry<String, String> entry : entries.entrySet()) {
                buffer.writeUtf(entry.getKey());
                buffer.writeUtf(entry.getValue());
            }
        }
    }

    public static SyncModDataMessage decode(FriendlyByteBuf buffer) {
        int sectionCount = buffer.readVarInt();
        Map<String, Map<String, String>> sections = new LinkedHashMap<>();
        for (int s = 0; s < sectionCount; s++) {
            String syncId = buffer.readUtf();
            int entryCount = buffer.readVarInt();
            Map<String, String> entries = new HashMap<>();
            for (int i = 0; i < entryCount; i++) {
                String key = buffer.readUtf();
                String json = buffer.readUtf();
                entries.put(key, json);
            }
            sections.put(syncId, entries);
        }
        return new SyncModDataMessage(sections);
    }

    public static void handle(SyncModDataMessage msg, Supplier<NetworkEvent.Context> ctx) {
        if (!ctx.get().getDirection().getReceptionSide().isClient()) {
            ctx.get().setPacketHandled(true);
            return;
        }
        ctx.get().enqueueWork(() -> {
            for (Map.Entry<String, Map<String, String>> section : msg.sections.entrySet()) {
                NetworkSyncable syncable = ModLoaderSync.get(section.getKey());
                if (syncable == null) {
                    JaamsWeaponryMod.LOGGER.warn("Received sync data for unknown loader '{}'", section.getKey());
                    continue;
                }
                try {
                    syncable.applyNetworkSync(section.getValue());
                } catch (Exception e) {
                    JaamsWeaponryMod.LOGGER.error("Failed to apply synced data for loader '{}'",
                            section.getKey(), e);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
