package net.jaams.weaponry.client;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;

import net.jaams.weaponry.animation.AnimationSound;

public class ClientAnimationSoundPlayer {
    public static void play(SoundEvent soundEvent, SoundSource source, float volume, float pitch,
            LivingEntity entity) {
        net.minecraft.client.Minecraft.getInstance().getSoundManager().play(
                new AnimationSound(soundEvent, source, volume, pitch, entity));
    }
}
