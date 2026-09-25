package net.jaams.weaponry.handler.effect;

import net.jaams.weaponry.configuration.common.EffectsConfig;
import net.jaams.weaponry.init.ModMobEffects;
import net.jaams.weaponry.particle.CustomSkullParticleData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = "jaams_weaponry")
public class VigorousRageHandler {

    private static boolean isVigorousRageEnabled() {
        return EffectsConfig.VIGOROUS_RAGE.get();
    }

    private static float getBaseUpgradeChance() {
        return EffectsConfig.VIGOROUS_RAGE_BASE_UPGRADE_CHANCE.get().floatValue();
    }

    private static int getMaxLevel() {
        return EffectsConfig.VIGOROUS_RAGE_MAX_LEVEL.get();
    }

    private static int getMaxDurationTicks() {
        return EffectsConfig.VIGOROUS_RAGE_MAX_DURATION_TICKS.get();
    }

    private static int getMinExtraTicks() {
        return EffectsConfig.VIGOROUS_RAGE_MIN_EXTRA_TICKS.get();
    }

    private static int getMaxExtraTicks() {
        return EffectsConfig.VIGOROUS_RAGE_MAX_EXTRA_TICKS.get();
    }

    private static float getUpgradeChanceDecay() {
        return EffectsConfig.VIGOROUS_RAGE_UPGRADE_CHANCE_DECAY.get().floatValue();
    }

    private static float getHealthScalingFactor() {
        return EffectsConfig.VIGOROUS_RAGE_HEALTH_SCALING_FACTOR.get().floatValue();
    }

    private static float getMinHealthThreshold() {
        return EffectsConfig.VIGOROUS_RAGE_MIN_HEALTH_THRESHOLD.get().floatValue();
    }

    private static float getMaxHealthThreshold() {
        return EffectsConfig.VIGOROUS_RAGE_MAX_HEALTH_THRESHOLD.get().floatValue();
    }

    @SubscribeEvent
    public static void onVigorousRage(LivingDeathEvent event) {
        if (!isVigorousRageEnabled()) {
            return;
        }
        DamageSource source = event.getSource();
        if (!(source.getEntity() instanceof LivingEntity attacker)
                || source.is(net.minecraft.tags.DamageTypeTags.IS_PROJECTILE)) {
            return;
        }
        if (!attacker.hasEffect(ModMobEffects.VIGOROUS_RAGE)) {
            return;
        }
        LivingEntity target = event.getEntity();
        if (isValidTarget(target)) {
            MobEffectInstance effectInstance = attacker.getEffect(ModMobEffects.VIGOROUS_RAGE);
            if (effectInstance == null) {
                return;
            }
            int currentLevel = effectInstance.getAmplifier() + 1;
            RandomSource random = attacker.getRandom();
            int currentDuration = effectInstance.getDuration();
            float maxHealth = target.getMaxHealth();
            float clampedHealth = Math.min(Math.max(maxHealth, getMinHealthThreshold()), getMaxHealthThreshold());
            float healthFactor = Math
                    .min(Math.max((clampedHealth - getMinHealthThreshold()) * getHealthScalingFactor(), 0.0F), 1.0F);
            float upgradeChance = getBaseUpgradeChance() * (float) Math.pow(getUpgradeChanceDecay(), currentLevel - 1)
                    * (0.5F + healthFactor);
            int extraTicks = currentDuration < getMaxDurationTicks()
                    ? Math.round(getMinExtraTicks() + (getMaxExtraTicks() - getMinExtraTicks()) * healthFactor)
                    : 0;
            int newDuration = Math.min(currentDuration + extraTicks, getMaxDurationTicks());
            int newLevel = currentLevel;
            if (random.nextFloat() < upgradeChance) {
                newLevel = Math.min(currentLevel + 1, getMaxLevel());
            }
            MobEffectInstance newEffect = new MobEffectInstance(ModMobEffects.VIGOROUS_RAGE, newDuration,
                    newLevel - 1);
            attacker.addEffect(newEffect);
            if (!attacker.level().isClientSide() && attacker.level() instanceof ServerLevel serverLevel) {
                spawnSkullParticle(serverLevel, target);
            }
        }
    }

    private static void spawnSkullParticle(ServerLevel serverLevel, LivingEntity target) {
        float hitboxSize = Math.max(target.getBbWidth(), target.getBbHeight()) * 0.3F;
        float[] rgb = getRandomColor(serverLevel.getRandom());
        CustomSkullParticleData particleData = new CustomSkullParticleData(rgb[0], rgb[1], rgb[2], hitboxSize);
        serverLevel.sendParticles(particleData, target.getX(), target.getY() + target.getBbHeight() * 0.5F,
                target.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
    }

    private static float[] getRandomColor(RandomSource random) {
        float r = 0.8F + random.nextFloat() * 0.2F;
        float g = 0.3F + random.nextFloat() * 0.3F;
        float b = random.nextFloat() * 0.2F;
        return new float[] { r, g, b };
    }

    private static boolean isValidTarget(LivingEntity target) {
        return (target instanceof Mob && !(target instanceof Animal)) || target instanceof Player;
    }
}
