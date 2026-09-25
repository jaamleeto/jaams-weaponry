package net.jaams.weaponry.sync;

import net.jaams.weaponry.packet.SyncDataLoaderPacket;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = "jaams_weaponry")
public class DataLoaderSyncEvents {

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && !player.level().isClientSide) {
            syncToPlayer(player);
        }
    }

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        ServerPlayer player = event.getPlayer();
        if (player != null) {
            syncToPlayer(player);
        } else {
            syncToAllPlayers();
        }
    }

    private static void syncToPlayer(ServerPlayer player) {
        for (NetworkSyncable syncable : ModLoaderSync.all()) {
            PacketDistributor.sendToPlayer(player,
                    new SyncDataLoaderPacket(syncable.getSyncId(), syncable.getSourcesSnapshot()));
        }
    }

    private static void syncToAllPlayers() {
        for (NetworkSyncable syncable : ModLoaderSync.all()) {
            PacketDistributor.sendToAllPlayers(
                    new SyncDataLoaderPacket(syncable.getSyncId(), syncable.getSourcesSnapshot()));
        }
    }
}
