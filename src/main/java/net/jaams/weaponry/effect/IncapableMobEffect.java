package net.jaams.weaponry.effect;

import java.util.UUID;
import net.jaams.weaponry.configuration.common.EffectsConfig;
import net.jaams.weaponry.util.ModUtils;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class IncapableMobEffect extends MobEffect implements RemovableAttributeEffect {

    private static final UUID ATTACK_SPEED_MODIFIER_ID = UUID.fromString("ff55bb82-24a3-4b6d-90f6-75b22d54bfeb");
    private static final UUID ATTACK_DAMAGE_MODIFIER_ID = UUID.fromString("de5fc1e5-bff2-4956-9bf0-abbc511d5e2d");

    public IncapableMobEffect() {
        super(MobEffectCategory.HARMFUL, -12422244);
    }

    private static float getAttackSpeedMultiplier() {
        return EffectsConfig.INCAPABLE_ATTACK_SPEED_ATTRIBUTE_MULTIPLIER.get().floatValue();
    }

    private static float getAttackDamageMultiplier() {
        return EffectsConfig.INCAPABLE_ATTACK_DAMAGE_ATTRIBUTE_MULTIPLIER.get().floatValue();
    }

    @Override
    public void onEffectStarted(LivingEntity entity, int amplifier) {
        if (entity == null) return;
        int level = amplifier + 1;
        double attackSpeedPenalty = getAttackSpeedMultiplier() * level;
        double attackDamagePenalty = getAttackDamageMultiplier() * level;
        ModUtils.applyOrUpdateModifier(entity, Attributes.ATTACK_SPEED, ATTACK_SPEED_MODIFIER_ID, "Incapable Attack Speed", attackSpeedPenalty, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        ModUtils.applyOrUpdateModifier(entity, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_MODIFIER_ID, "Incapable Attack Damage", attackDamagePenalty, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

        public void onEffectRemoved(LivingEntity entity, int amplifier) {
        if (entity == null) return;
        ModUtils.removeModifier(entity, Attributes.ATTACK_SPEED, ATTACK_SPEED_MODIFIER_ID);
        ModUtils.removeModifier(entity, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_MODIFIER_ID);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
