package net.jaams.weaponry.packet;

import net.jaams.weaponry.JaamsWeaponryMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.Map;

public class SyncDataLoaderPacket implements CustomPacketPayload {
    public static final Type<SyncDataLoaderPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(JaamsWeaponryMod.MODID, "sync_data_loader"));
    public static final StreamCodec<FriendlyByteBuf, SyncDataLoaderPacket> STREAM_CODEC = StreamCodec.of(
            (buf, packet) -> encode(packet, buf), SyncDataLoaderPacket::decode);

    private final String syncId;
    private final Map<String, String> sources;

    public SyncDataLoaderPacket(String syncId, Map<String, String> sources) {
        this.syncId = syncId;
        this.sources = sources;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void encode(SyncDataLoaderPacket packet, FriendlyByteBuf buf) {
        buf.writeUtf(packet.syncId);
        buf.writeInt(packet.sources.size());
        for (Map.Entry<String, String> entry : packet.sources.entrySet()) {
            buf.writeUtf(entry.getKey());
            buf.writeUtf(entry.getValue());
        }
    }

    public static SyncDataLoaderPacket decode(FriendlyByteBuf buf) {
        String syncId = buf.readUtf();
        int size = buf.readInt();
        Map<String, String> sources = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            String key = buf.readUtf();
            String value = buf.readUtf();
            sources.put(key, value);
        }
        return new SyncDataLoaderPacket(syncId, sources);
    }

    public static void handle(SyncDataLoaderPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> ClientPacketHandler.handleSyncDataLoaderPacket(packet.syncId, packet.sources));
    }
}
