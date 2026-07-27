package net.jaams.weaponry.packet;

import net.jaams.weaponry.capability.aberration.AberrationProvider;
import net.jaams.weaponry.capability.amount.AmountProvider;
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
        if (player != null) {
            ItemStack itemStack = player.getItemInHand(hand);
            if (!player.getCooldowns().isOnCooldown(itemStack.getItem()) && cooldownTicks > 0) {
                player.getCooldowns().addCooldown(itemStack.getItem(), cooldownTicks);
            }
        }
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

}
