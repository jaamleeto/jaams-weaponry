package net.jaams.weaponry.effect;

import net.minecraft.world.entity.LivingEntity;

/**
 * 1.21.1 removed MobEffect#removeAttributeModifiers; effects implementing this
 * get their cleanup invoked from EffectModifierBridgeHandler on Remove/Expired.
 */
public interface RemovableAttributeEffect {
    void onEffectRemoved(LivingEntity entity, int amplifier);
}
