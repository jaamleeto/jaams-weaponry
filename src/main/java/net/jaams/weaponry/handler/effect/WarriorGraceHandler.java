package net.jaams.weaponry.handler.effect;

import net.jaams.weaponry.configuration.common.EffectsConfig;
import net.jaams.weaponry.init.ModMobEffects;
import net.jaams.weaponry.util.ModUtils;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "jaams_weaponry", bus = Mod.EventBusSubscriber.Bus.FORGE)
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
        if (player.hasEffect(ModMobEffects.WARRIORS_GRACE.get()) && ModUtils.isItemWeapon(player.getMainHandItem())) {
            MobEffectInstance effect = player.getEffect(ModMobEffects.WARRIORS_GRACE.get());
            int level = effect != null ? effect.getAmplifier() + 1 : 1;
            float pitch = getWarriorsGraceSoundPitch(level);
            ModUtils.playSound(player, "jaams_weaponry:warrior_grace_hit", SoundSource.PLAYERS, 1.0F, pitch);
        }
    }

    @SubscribeEvent
    public static void onWarriorGraceCriticalHit(CriticalHitEvent event) {
        if (!isWarriorsGraceEnabled()) return;
        Player player = event.getEntity();
        if (player.hasEffect(ModMobEffects.WARRIORS_GRACE.get())) {
            MobEffectInstance effectInstance = player.getEffect(ModMobEffects.WARRIORS_GRACE.get());
            int level = effectInstance.getAmplifier() + 1;
            if (event.isVanillaCritical() || event.getDamageModifier() > event.getOldDamageModifier()) {
                float extraDamageMultiplier = 1.0F + (getWarriorsGraceCritDamageMultiplier() * level);
                event.setDamageModifier(event.getDamageModifier() * extraDamageMultiplier);
            }
        }
    }

    @SubscribeEvent
    public static void onWarriorGraceLivingHurt(LivingHurtEvent event) {
        if (!isWarriorsGraceEnabled()) return;
        LivingEntity entity = event.getEntity();
        if (entity.hasEffect(ModMobEffects.WARRIORS_GRACE.get())) {
            MobEffectInstance effectInstance = entity.getEffect(ModMobEffects.WARRIORS_GRACE.get());
            int level = effectInstance.getAmplifier() + 1;
            float damageReduction = 1.0F - (getWarriorsGraceDamageReductionPerLevel() * level);
            event.setAmount(event.getAmount() * damageReduction);
        }
    }
}
