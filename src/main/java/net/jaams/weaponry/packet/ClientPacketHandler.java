package net.jaams.weaponry.packet;

import net.jaams.weaponry.JaamsWeaponryMod;
import net.jaams.weaponry.capability.aberration.AberrationProvider;
import net.jaams.weaponry.configuration.common.GunSystemCommonConfig;
import net.jaams.weaponry.capability.amount.AmountProvider;
import net.jaams.weaponry.sync.ModLoaderSync;
import net.jaams.weaponry.sync.NetworkSyncable;
import net.jaams.weaponry.util.ModEnums;
import net.jaams.weaponry.util.ModGuns;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class ClientPacketHandler {

    public static void handleAmountPacket(int entityId, float damage) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null) {
            Entity entity = minecraft.level.getEntity(entityId);
            if (entity instanceof LivingEntity livingEntity) {
                AmountProvider.get(livingEntity).ifPresent((amount) -> {
                    amount.setDamage(damage);
                });
            }
        }
    }

    public static void handleGunCooldownPacket(InteractionHand hand, int cooldownTicks) {
        Player player = Minecraft.getInstance().player;
        if (player == null || cooldownTicks <= 0)
            return;
        ItemStack firedStack = player.getItemInHand(hand);
        if (firedStack.isEmpty())
            return;

        // Replace local prediction with the authoritative server duration.
        // Keeping the old fixed client prediction caused drift when attachments
        // or data-driven modifiers changed the duration.
        if (GunSystemCommonConfig.GUN_COOLDOWN_GLOBAL.get()) {
            for (ItemStack stack : player.getInventory().items) {
                applyCooldown(stack, player, cooldownTicks, true);
            }
            for (ItemStack stack : player.getInventory().armor) {
                applyCooldown(stack, player, cooldownTicks, true);
            }
            for (ItemStack stack : player.getInventory().offhand) {
                applyCooldown(stack, player, cooldownTicks, true);
            }
            applyAuthoritativeCooldown(firedStack, player, cooldownTicks);
        } else if (GunSystemCommonConfig.GUN_COOLDOWN_BY_TYPE.get()) {
            ModGuns.GunType firedType = ModGuns.getGunType(firedStack);
            if (firedType != null) {
                for (ItemStack stack : player.getInventory().items) {
                    applyCooldown(stack, player, cooldownTicks, ModGuns.getGunType(stack) == firedType);
                }
                for (ItemStack stack : player.getInventory().armor) {
                    applyCooldown(stack, player, cooldownTicks, ModGuns.getGunType(stack) == firedType);
                }
                for (ItemStack stack : player.getInventory().offhand) {
                    applyCooldown(stack, player, cooldownTicks, ModGuns.getGunType(stack) == firedType);
                }
            }
            applyAuthoritativeCooldown(firedStack, player, cooldownTicks);
        } else {
            applyAuthoritativeCooldown(firedStack, player, cooldownTicks);
        }
    }

    private static void applyCooldown(ItemStack stack, Player player, int cooldownTicks, boolean applies) {
        if (!applies || stack.isEmpty() || !ModGuns.isGun(stack))
            return;
        applyAuthoritativeCooldown(stack, player, cooldownTicks);
    }

    private static void applyAuthoritativeCooldown(ItemStack stack, Player player, int cooldownTicks) {
        if (stack.isEmpty())
            return;
        player.getCooldowns().removeCooldown(stack.getItem());
        player.getCooldowns().addCooldown(stack.getItem(), cooldownTicks);
    }

    public static void handleVisualRecoilPacket(float xRotRecoilIntensity) {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            ModGuns.applyVisualRecoil(player, xRotRecoilIntensity);
        }
    }

    public static void handleAberrationPacket(int playerId, ModEnums.AberrationType effectType, double intensity,
            int duration) {
        Level level = Minecraft.getInstance().level;
        if (level != null) {
            Entity entity = level.getEntity(playerId);
            if (entity instanceof Player player) {
                AberrationProvider.get(player).ifPresent((aberration) -> {
                    aberration.setEffectType(effectType);
                    aberration.setIntensity(intensity);
                    aberration.setDuration(duration);
                });
            }
        }
    }

    public static void handleSyncDataLoaderPacket(String syncId, Map<String, String> sources) {
        NetworkSyncable syncable = ModLoaderSync.get(syncId);
        if (syncable != null) {
            syncable.applyNetworkSync(sources);
        } else {
            JaamsWeaponryMod.LOGGER.warn("Received sync packet for unknown loader: {}", syncId);
        }
    }

}
