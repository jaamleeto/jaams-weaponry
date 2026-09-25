package net.jaams.weaponry.effect;

import java.util.UUID;
import net.jaams.weaponry.configuration.common.EffectsConfig;
import net.jaams.weaponry.util.ModUtils;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class WarriorsGraceMobEffect extends MobEffect implements RemovableAttributeEffect {

    private static final UUID ATTACK_DAMAGE_MODIFIER_ID = UUID.fromString("98d98663-b219-4cbc-b235-e08a690fe47a");
    private static final UUID KNOCKBACK_RESISTANCE_MODIFIER_ID = UUID.fromString("bee8e094-8892-49c8-873a-ca09234bbae9");

    public WarriorsGraceMobEffect() {
        super(MobEffectCategory.BENEFICIAL, -52480);
    }

    private static float getAttackDamagePerLevel() {
        return EffectsConfig.WARRIORS_GRACE_ATTACK_DAMAGE_ATTRIBUTE_MULTIPLIER.get().floatValue();
    }

    private static float getKnockbackResistancePerLevel() {
        return EffectsConfig.WARRIORS_GRACE_KNOCKBACK_RESISTANCE_ATTRIBUTE_MULTIPLIER.get().floatValue();
    }

    private static float getSoundPitch(int amplifier) {
        int level = amplifier + 1;
        return 1.0F + (0.06F * level);
    }

    @Override
    public void onEffectStarted(LivingEntity entity, int amplifier) {
        if (entity == null) return;
        int level = amplifier + 1;
        double attackBonus = getAttackDamagePerLevel() * level;
        double knockbackBonus = getKnockbackResistancePerLevel() * level;
        ModUtils.applyOrUpdateModifier(entity, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_MODIFIER_ID, "warrior1", attackBonus, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        if (entity instanceof Player) {
            ModUtils.applyOrUpdateModifier(entity, Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK_RESISTANCE_MODIFIER_ID, "warrior2", knockbackBonus, AttributeModifier.Operation.ADD_VALUE);
        }
        ModUtils.playSound(entity, "jaams_weaponry:warrior_grace_started", SoundSource.PLAYERS, 1.0F, getSoundPitch(amplifier));
    }

        public void onEffectRemoved(LivingEntity entity, int amplifier) {
        if (entity == null) return;
        ModUtils.removeModifier(entity, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_MODIFIER_ID);
        if (entity instanceof Player) {
            ModUtils.removeModifier(entity, Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK_RESISTANCE_MODIFIER_ID);
        }
        ModUtils.playSound(entity, "jaams_weaponry:warrior_grace_expires", SoundSource.PLAYERS, 1.0F, getSoundPitch(amplifier));
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
