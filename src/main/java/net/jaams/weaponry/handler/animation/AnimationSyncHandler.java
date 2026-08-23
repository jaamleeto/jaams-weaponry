package net.jaams.weaponry.handler.animation;

import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.minecraft.server.level.ServerPlayer;

import net.jaams.weaponry.JaamsWeaponryMod;
import net.jaams.weaponry.network.SyncModDataMessage;

@Mod.EventBusSubscriber(modid = JaamsWeaponryMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AnimationSyncHandler {

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        ServerPlayer joining = event.getPlayer();
        if (joining != null) {
            JaamsWeaponryMod.PACKET_HANDLER.send(
                    net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> joining),
                    SyncModDataMessage.fromServerState());
            return;
        }
        for (ServerPlayer player : event.getPlayerList().getPlayers()) {
            JaamsWeaponryMod.PACKET_HANDLER.send(
                    net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
                    SyncModDataMessage.fromServerState());
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            JaamsWeaponryMod.PACKET_HANDLER.send(
                    net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
                    SyncModDataMessage.fromServerState());
        }
    }
}
