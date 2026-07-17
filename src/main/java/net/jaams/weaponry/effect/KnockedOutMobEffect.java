package net.jaams.weaponry.effect;

import java.util.UUID;
import net.jaams.weaponry.configuration.common.EffectsConfig;
import net.jaams.weaponry.particle.SmallWaveParticleData;
import net.jaams.weaponry.util.ModUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class KnockedOutMobEffect extends MobEffect {

    private static final UUID ATTACK_SPEED_UUID = UUID.fromString("8594af9b-10dd-4937-b797-58d3eb778908");
    private static final UUID MOVEMENT_SPEED_UUID = UUID.fromString("65607ab0-4fa4-41cc-9538-49a030c6319e");

    public KnockedOutMobEffect() {
        super(MobEffectCategory.HARMFUL, 0x999999);
    }

    private static float getAttackSpeedMultiplier() {
        return EffectsConfig.KNOCKED_OUT_ATTACK_SPEED_ATTRIBUTE_MULTIPLIER.get().floatValue();
    }

    private static float getMovementSpeedMultiplier() {
        return EffectsConfig.KNOCKED_OUT_MOVEMENT_SPEED_ATTRIBUTE_MULTIPLIER.get().floatValue();
    }

    private static float getSoundPitch(int amplifier) {
        int level = amplifier + 1;
        return 1.35F - (0.09F * level);
    }

    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        if (entity == null)
            return;
        int level = amplifier + 1;
        double attackSpeedMod = getAttackSpeedMultiplier() * level;
        double movementSpeedMod = getMovementSpeedMultiplier() * level;
        ModUtils.applyOrUpdateModifier(entity, Attributes.ATTACK_SPEED, ATTACK_SPEED_UUID, "Knocked Out Attack Speed",
                attackSpeedMod, AttributeModifier.Operation.MULTIPLY_TOTAL);
        ModUtils.applyOrUpdateModifier(entity, Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_UUID,
                "Knocked Out Movement Speed", movementSpeedMod, AttributeModifier.Operation.MULTIPLY_TOTAL);
        ModUtils.playSound(entity, "jaams_weaponry:knocked_out_started", SoundSource.PLAYERS, 1.0F,
                getSoundPitch(amplifier));
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        if (entity == null)
            return;
        ModUtils.removeModifier(entity, Attributes.ATTACK_SPEED, ATTACK_SPEED_UUID);
        ModUtils.removeModifier(entity, Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_UUID);
        ModUtils.playSound(entity, "jaams_weaponry:knocked_out_expires", SoundSource.PLAYERS, 1.0F,
                getSoundPitch(amplifier));
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity == null || !entity.isAlive() || entity.isRemoved()) {
            return;
        }

        if (entity.level() instanceof ServerLevel serverLevel) {
            if (entity.tickCount % 2 == 0) {
                double ticks = entity.tickCount;
                double radius = 0.38;
                double speed = 0.65;
                double baseHeight = entity.getY() + entity.getBbHeight() + 0.18;
                float size = 0.20F;
                int particleCount = 2;

                for (int i = 0; i < particleCount; i++) {
                    double angle = (ticks * speed) + (i * Math.PI);
                    double x = entity.getX() + (Math.cos(angle) * radius);
                    double z = entity.getZ() + (Math.sin(angle) * radius);
                    double heightVariation = (serverLevel.random.nextDouble() * 2.0 - 1.0) * 0.1;
                    double y = baseHeight + (Math.sin((ticks * 0.3) + i) * 0.015) + heightVariation;

                    float r = 0.48F + (serverLevel.random.nextFloat() * 0.05F);
                    float g = 0.52F + (serverLevel.random.nextFloat() * 0.05F);
                    float b = 0.58F + (serverLevel.random.nextFloat() * 0.05F);

                    SmallWaveParticleData particleData = new SmallWaveParticleData(r, g, b, size);
                    serverLevel.sendParticles(particleData, x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
                }
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
