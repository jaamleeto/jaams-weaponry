package net.jaams.weaponry.handler.effect;

import net.jaams.weaponry.configuration.common.EffectsConfig;
import net.jaams.weaponry.init.ModMobEffects;
import net.jaams.weaponry.util.ModUtils;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = "jaams_weaponry")
public class WarriorGraceHandler {

    private static boolean isWarriorsGraceEnabled() {
        return EffectsConfig.WARRIORS_GRACE.get();
    }

    private static float getWarriorsGraceCritDamageMultiplier() {
        return EffectsConfig.WARRIORS_GRACE_CRIT_DAMAGE_MULTIPLIER.get().floatValue();
    }

    private static float getWarriorsGraceDamageReductionPerLevel() {
        return EffectsConfig.WARRIORS_GRACE_DAMAGE_REDUCTION_PER_LEVEL.get().floatValue();
    }

    private static float getWarriorsGraceSoundPitch(int level) {
        return 1.0F + (0.06F * level);
    }

    @SubscribeEvent
    public static void onWarriorGrace(AttackEntityEvent event) {
        if (!isWarriorsGraceEnabled()) return;
        Player player = event.getEntity();
        if (player.hasEffect(ModMobEffects.WARRIORS_GRACE) && ModUtils.isItemWeapon(player.getMainHandItem())) {
            MobEffectInstance effect = player.getEffect(ModMobEffects.WARRIORS_GRACE);
            int level = effect != null ? effect.getAmplifier() + 1 : 1;
            float pitch = getWarriorsGraceSoundPitch(level);
            ModUtils.playSound(player, "jaams_weaponry:warrior_grace_hit", SoundSource.PLAYERS, 1.0F, pitch);
        }
    }

    @SubscribeEvent
    public static void onWarriorGraceCriticalHit(CriticalHitEvent event) {
        if (!isWarriorsGraceEnabled()) return;
        Player player = event.getEntity();
        if (player.hasEffect(ModMobEffects.WARRIORS_GRACE)) {
            MobEffectInstance effectInstance = player.getEffect(ModMobEffects.WARRIORS_GRACE);
            int level = effectInstance.getAmplifier() + 1;
            if (event.isVanillaCritical() || event.isCriticalHit()) {
                float extraDamageMultiplier = 1.0F + (getWarriorsGraceCritDamageMultiplier() * level);
                event.setDamageMultiplier(event.getDamageMultiplier() * extraDamageMultiplier);
            }
        }
    }

    @SubscribeEvent
    public static void onWarriorGraceLivingHurt(LivingIncomingDamageEvent event) {
        if (!isWarriorsGraceEnabled()) return;
        LivingEntity entity = event.getEntity();
        if (entity.hasEffect(ModMobEffects.WARRIORS_GRACE)) {
            MobEffectInstance effectInstance = entity.getEffect(ModMobEffects.WARRIORS_GRACE);
            int level = effectInstance.getAmplifier() + 1;
            float damageReduction = 1.0F - (getWarriorsGraceDamageReductionPerLevel() * level);
            event.setAmount(event.getAmount() * damageReduction);
        }
    }
}
