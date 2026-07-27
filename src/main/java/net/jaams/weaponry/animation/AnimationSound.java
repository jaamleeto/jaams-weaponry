package net.jaams.weaponry.animation;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;


public class AnimationSound extends AbstractTickableSoundInstance {
    private final LivingEntity entity;

    public AnimationSound(SoundEvent soundEvent, SoundSource source,
            float volume, float pitch, LivingEntity entity) {
        super(soundEvent, source, RandomSource.create());
        this.entity = entity;
        this.volume = volume;
        this.pitch = pitch;
        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
        this.attenuation = SoundInstance.Attenuation.LINEAR;
    }

    @Override
    public void tick() {
        if (entity.isRemoved()) {
            this.stop();
        } else {
            this.x = entity.getX();
            this.y = entity.getY();
            this.z = entity.getZ();
        }
    }
}
