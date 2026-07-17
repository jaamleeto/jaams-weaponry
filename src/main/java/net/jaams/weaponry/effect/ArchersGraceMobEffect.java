package net.jaams.weaponry.effect;

import java.util.UUID;
import net.jaams.weaponry.configuration.common.EffectsConfig;
import net.jaams.weaponry.util.ModUtils;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class ArchersGraceMobEffect extends MobEffect {

    private static final UUID ATTACK_KNOCKBACK_UUID = UUID.fromString("19711849-330d-43f8-9e95-f35e9cb17efc");
    private static final UUID ATTACK_SPEED_UUID = UUID.fromString("936a6b7f-d414-4869-928e-e5728658970b");

    public ArchersGraceMobEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF3300);
    }

    private static float getAttackSpeedPerLevel() {
        return EffectsConfig.ARCHERS_GRACE_ATTACK_SPEED_ATTRIBUTE_MULTIPLIER.get().floatValue();
    }

    private static float getKnockbackPerLevel() {
        return EffectsConfig.ARCHERS_GRACE_KNOCKBACK_ATTRIBUTE_MULTIPLIER.get().floatValue();
    }

    private static float getSoundPitch(int amplifier) {
        int level = amplifier + 1;
        return 1.0F + (0.05F * level);
    }

    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        if (entity == null) return;
        int level = amplifier + 1;
        double attackSpeedBonus = getAttackSpeedPerLevel() * level;
        double knockbackBonus = getKnockbackPerLevel() * level;
        ModUtils.applyOrUpdateModifier(entity, Attributes.ATTACK_SPEED, ATTACK_SPEED_UUID, "Archer Attack Speed", attackSpeedBonus, AttributeModifier.Operation.MULTIPLY_TOTAL);
        ModUtils.applyOrUpdateModifier(entity, Attributes.ATTACK_KNOCKBACK, ATTACK_KNOCKBACK_UUID, "Archer Knockback", knockbackBonus, AttributeModifier.Operation.ADDITION);
        ModUtils.playSound(entity, "jaams_weaponry:archers_grace_started", SoundSource.PLAYERS, 1.0F, getSoundPitch(amplifier));
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        if (entity == null) return;
        ModUtils.removeModifier(entity, Attributes.ATTACK_SPEED, ATTACK_SPEED_UUID);
        ModUtils.removeModifier(entity, Attributes.ATTACK_KNOCKBACK, ATTACK_KNOCKBACK_UUID);
        ModUtils.playSound(entity, "jaams_weaponry:archers_grace_expires", SoundSource.PLAYERS, 1.0F, getSoundPitch(amplifier));
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
