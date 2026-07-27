package net.jaams.weaponry.capability;

import net.jaams.weaponry.capability.aberration.AberrationProvider;
import net.jaams.weaponry.capability.amount.AmountProvider;
import net.jaams.weaponry.packet.AberrationPacket;
import net.jaams.weaponry.packet.AmountPacket;
import net.jaams.weaponry.util.ModEnums;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = "jaams_weaponry", bus = EventBusSubscriber.Bus.GAME)
public class CapabilityEvents {

    private static void syncAll(ServerPlayer receiver, Entity target) {
        AberrationProvider.get(target).ifPresent((aberration) -> PacketDistributor.sendToPlayer(receiver,
                new AberrationPacket(target.getId(), aberration.getEffectType(), aberration.getIntensity(), aberration.getDuration())));
        AmountProvider.get(target).ifPresent((amount) -> PacketDistributor.sendToPlayer(receiver, new AmountPacket(target.getId(), amount.getDamage())));
    }

    @SubscribeEvent
    public static void onCapabilityPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player && !player.level().isClientSide) {
            AberrationProvider.get(player).ifPresent((aberration) -> {
                int duration = aberration.getDuration();
                if (duration > 0) {
                    aberration.setDuration(duration - 1);
                    if (duration - 1 <= 0) {
                        aberration.setEffectType(ModEnums.AberrationType.NONE);
                        aberration.setIntensity(0.0);
                    }
                    PacketDistributor.sendToPlayer(player, new AberrationPacket(player.getId(), aberration.getEffectType(), aberration.getIntensity(),
                            aberration.getDuration()));
                }
            });
        }
    }

    @SubscribeEvent
    public static void onCapabilityEntityJoinWorld(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && !player.level().isClientSide) {
            syncAll(player, player);
        }
    }

    @SubscribeEvent
    public static void onCapabilityStartTracking(PlayerEvent.StartTracking event) {
        Entity target = event.getTarget();
        if (!target.level().isClientSide && event.getEntity() instanceof ServerPlayer player) {
            if (target instanceof Player) {
                syncAll(player, target);
            }
        }
    }

    @SubscribeEvent
    public static void onCapabilityPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && !player.level().isClientSide) {
            syncAll(player, player);
        }
    }

    @SubscribeEvent
    public static void onCapabilityPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && !player.level().isClientSide) {
            syncAll(player, player);
        }
    }

    @SubscribeEvent
    public static void onCapabilityPlayerClone(PlayerEvent.Clone event) {
        // Data is carried over by AttachmentType#copyOnDeath; only re-sync to the new client entity.
        if (!event.getEntity().level().isClientSide && event.getEntity() instanceof ServerPlayer newPlayer) {
            syncAll(newPlayer, newPlayer);
        }
    }
}
