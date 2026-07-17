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

public class VigorousRageMobEffect extends MobEffect {

    private static final UUID ATTACK_SPEED_MODIFIER_ID = UUID.fromString("7c7ba62d-c015-4b76-924f-5b6b200eac39");
    private static final UUID ATTACK_KNOCKBACK_MODIFIER_ID = UUID.fromString("d594fa72-d09b-4e2a-9f56-388ef4b5e279");
    private static final UUID MOVEMENT_SPEED_MODIFIER_ID = UUID.fromString("3176c3c5-487a-4657-92fb-dc66e9835290");

    public VigorousRageMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -26368);
    }

    private static float getAttackSpeedPerLevel() {
        return EffectsConfig.VIGOROUS_RAGE_ATTACK_SPEED_ATTRIBUTE_MULTIPLIER.get().floatValue();
    }

    private static float getAttackKnockbackPerLevel() {
        return EffectsConfig.VIGOROUS_RAGE_KNOCKBACK_ATTRIBUTE_MULTIPLIER.get().floatValue();
    }

    private static float getMovementSpeedPerLevel() {
        return EffectsConfig.VIGOROUS_RAGE_MOVEMENT_SPEED_ATTRIBUTE_MULTIPLIER.get().floatValue();
    }

    private static float getSoundPitch(int amplifier) {
        int level = amplifier + 1;
        return 1.0F + (0.06F * level);
    }

    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        if (entity == null) return;
        int level = amplifier + 1;
        double attackSpeedBonus = getAttackSpeedPerLevel() * level;
        double attackKnockbackBonus = getAttackKnockbackPerLevel() * level;
        double movementSpeedBonus = getMovementSpeedPerLevel() * level;
        ModUtils.applyOrUpdateModifier(entity, Attributes.ATTACK_SPEED, ATTACK_SPEED_MODIFIER_ID, "vigorous_rage_attack_speed", attackSpeedBonus, AttributeModifier.Operation.MULTIPLY_TOTAL);
        ModUtils.applyOrUpdateModifier(entity, Attributes.ATTACK_KNOCKBACK, ATTACK_KNOCKBACK_MODIFIER_ID, "vigorous_rage_knockback", attackKnockbackBonus, AttributeModifier.Operation.ADDITION);
        ModUtils.applyOrUpdateModifier(entity, Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_MODIFIER_ID, "vigorous_rage_movement_speed", movementSpeedBonus, AttributeModifier.Operation.MULTIPLY_TOTAL);
        ModUtils.playSound(entity, "jaams_weaponry:vigorous_rage_started", SoundSource.PLAYERS, 1.0F, getSoundPitch(amplifier));
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {
        if (entity == null) return;
        ModUtils.removeModifier(entity, Attributes.ATTACK_SPEED, ATTACK_SPEED_MODIFIER_ID);
        ModUtils.removeModifier(entity, Attributes.ATTACK_KNOCKBACK, ATTACK_KNOCKBACK_MODIFIER_ID);
        ModUtils.removeModifier(entity, Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_MODIFIER_ID);
        ModUtils.playSound(entity, "jaams_weaponry:vigorous_rage_expires", SoundSource.PLAYERS, 1.0F, getSoundPitch(amplifier));
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
