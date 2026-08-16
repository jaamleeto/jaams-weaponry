package net.jaams.weaponry.effect;

import net.minecraft.world.entity.LivingEntity;

public interface RemovableAttributeEffect {
    void onEffectRemoved(LivingEntity entity, int amplifier);
}
