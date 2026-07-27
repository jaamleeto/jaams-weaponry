package net.jaams.weaponry.effect;

import java.util.UUID;
import net.jaams.weaponry.configuration.common.EffectsConfig;
import net.jaams.weaponry.util.ModUtils;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class DepletionMobEffect extends MobEffect implements RemovableAttributeEffect {

    private static final UUID ATTACK_SPEED_UUID = UUID.fromString("8a5f7071-04a5-4a1f-9420-b94d8b2e58f5");
    private static final UUID MOVEMENT_SPEED_UUID = UUID.fromString("c5f92ad3-68eb-4f76-9732-1e8d40a6de78");
    private static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("d3f4e6a2-9b7c-4c2e-a8f1-2c9b5d6e7f89");

    public DepletionMobEffect() {
        super(MobEffectCategory.HARMFUL, 0x8FACCF);
    }

    private static float getAttackSpeedMultiplier() {
        return EffectsConfig.DEPLETION_ATTACK_SPEED_ATTRIBUTE_MULTIPLIER.get().floatValue();
    }

    private static float getMovementSpeedMultiplier() {
        return EffectsConfig.DEPLETION_MOVEMENT_SPEED_ATTRIBUTE_MULTIPLIER.get().floatValue();
    }

    private static float getAttackDamageMultiplier() {
        return EffectsConfig.DEPLETION_ATTACK_DAMAGE_ATTRIBUTE_MULTIPLIER.get().floatValue();
    }

    private static float getSoundPitch(int amplifier) {
        int level = amplifier + 1;
        return 1.0F - (0.04F * level);
    }

    @Override
    public void onEffectStarted(LivingEntity entity, int amplifier) {
        if (entity == null)
            return;
        int level = amplifier + 1;
        double attackSpeedMod = getAttackSpeedMultiplier() * level;
        double movementSpeedMod = getMovementSpeedMultiplier() * level;
        double attackDamageMod = getAttackDamageMultiplier() * level;
        ModUtils.applyOrUpdateModifier(entity, Attributes.ATTACK_SPEED, ATTACK_SPEED_UUID, "Depletion Attack Speed",
                attackSpeedMod, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        ModUtils.applyOrUpdateModifier(entity, Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_UUID,
                "Depletion Movement Speed", movementSpeedMod, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        ModUtils.applyOrUpdateModifier(entity, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_UUID, "Depletion Attack Damage",
                attackDamageMod, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

        public void onEffectRemoved(LivingEntity entity, int amplifier) {
        if (entity == null)
            return;
        ModUtils.removeModifier(entity, Attributes.ATTACK_SPEED, ATTACK_SPEED_UUID);
        ModUtils.removeModifier(entity, Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_UUID);
        ModUtils.removeModifier(entity, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_UUID);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
