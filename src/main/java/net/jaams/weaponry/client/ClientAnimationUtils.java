package net.jaams.weaponry.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import net.jaams.weaponry.JaamsWeaponryMod;
import net.jaams.weaponry.animation.AnimationAPI;
import net.jaams.weaponry.animation.AnimationAPI.PlayerAnimation;
import net.jaams.weaponry.compat.EpicFightCompat;
import net.jaams.weaponry.client.ClientAnimationSoundPlayer;
import net.jaams.weaponry.compat.PlayerAnimatorCompat;
import net.jaams.weaponry.util.ModAnimations;

public class ClientAnimationUtils {

    public static boolean isLocalPlayerInFirstPerson(Player player) {
        Minecraft mc = Minecraft.getInstance();
        if (player != mc.player)
            return false;
        if (!mc.options.getCameraType().isFirstPerson())
            return false;
        return true;
    }

    public static boolean isRenderingInventoryPanel = false;

    public static boolean shouldRenderInFirstPerson(Player player) {
        if (EpicFightCompat.isEpicFightMode(player) && EpicFightCompat.isFirstPersonModelActive())
            return false;
        return ModAnimations.isFirstPersonAnimation(player) && isLocalPlayerInFirstPerson(player)
                && isUsingCompatibleModel(player);
    }

    public static boolean isUsingCompatibleModel(Player player) {
        try {
            Minecraft mc = Minecraft.getInstance();
            EntityRenderer<?> renderer = mc.getEntityRenderDispatcher().getRenderer(player);
            if (renderer instanceof LivingEntityRenderer livingRenderer) {
                return livingRenderer.getModel() instanceof HumanoidModel;
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    public static boolean isPlayerAnimatorActive(Player player) {
        return PlayerAnimatorCompat.isPlayerAnimatorActive(player);
    }

    public static void advanceCombinableAnimations(Player player, float ageInTicks) {
        if (player == null || player.level().isClientSide == false)
            return;
        ListTag list = ModAnimations.getCombinableList(player);
        if (list.isEmpty())
            return;

        boolean changed = false;
        int i = 0;
        while (i < list.size()) {
            CompoundTag entry = list.getCompound(i);
            String animName = entry.getString("name");
            if (animName.isEmpty()) {
                list.remove(i);
                changed = true;
                continue;
            }

            PlayerAnimation anim = ModAnimations.getAnimation(animName);
            if (anim == null) {
                list.remove(i);
                changed = true;
                continue;
            }

            float lastTick = entry.getFloat("lastTick");
            float speed = entry.getFloat("speed");
            if (speed <= 0)
                speed = 1.0f;

            float deltaTicks = ageInTicks - lastTick;
            if (deltaTicks <= 0) {
                i++;
                continue;
            }

            entry.putFloat("lastTick", ageInTicks);
            float deltaTime = deltaTicks / 20.0f * speed;
            float progress = entry.getFloat("progress") + deltaTime;

            if (progress >= anim.length) {
                if (!anim.hold_on_last_frame && !anim.loop) {

                    list.remove(i);
                    changed = true;
                    continue;
                } else if (anim.hold_on_last_frame) {
                    progress = anim.length;
                } else if (anim.loop) {
                    progress = progress % anim.length;

                    entry.put("playedSounds", new ListTag());
                    entry.put("playedEvents", new ListTag());
                }
            }

            entry.putFloat("progress", progress);

            if (!anim.soundEffects.isEmpty()) {
                ListTag playedSounds = entry.getList("playedSounds", Tag.TAG_FLOAT);
                float effectiveLast = lastTick > 0 ? entry.getFloat("progress") - deltaTime : 0f;
                boolean soundPlayed = false;

                for (Map.Entry<Float, String> soundEntry : anim.soundEffects.entrySet()) {
                    float soundTime = soundEntry.getKey();
                    String soundId = soundEntry.getValue();

                    boolean alreadyPlayed = false;
                    for (int si = 0; si < playedSounds.size(); si++) {
                        if (Math.abs(playedSounds.getFloat(si) - soundTime) < 0.001f) {
                            alreadyPlayed = true;
                            break;
                        }
                    }
                    if (alreadyPlayed)
                        continue;

                    boolean shouldPlay;
                    if (effectiveLast <= progress) {
                        shouldPlay = effectiveLast <= soundTime && progress >= soundTime;
                    } else {
                        shouldPlay = effectiveLast <= soundTime || progress >= soundTime;
                    }

                    if (shouldPlay && player.level().isClientSide()) {
                        final float st = soundTime;
                        BuiltInRegistries.SOUND_EVENT
                                .getOptional(new ResourceLocation(soundId))
                                .ifPresent(soundEvent -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                                        () -> () -> ClientAnimationSoundPlayer.play(soundEvent, SoundSource.NEUTRAL,
                                                1.0F, 1.0F, player)));
                        playedSounds.add(FloatTag.valueOf(soundTime));
                        soundPlayed = true;
                    }
                }

                if (soundPlayed) {
                    entry.put("playedSounds", playedSounds);
                }
            }

            if (!anim.events.isEmpty()) {
                ListTag playedEvents = entry.contains("playedEvents") ? entry.getList("playedEvents", Tag.TAG_FLOAT)
                        : new ListTag();
                Set<Float> playedEventTimes = new HashSet<>();
                for (int ei = 0; ei < playedEvents.size(); ei++) {
                    playedEventTimes.add(playedEvents.getFloat(ei));
                }
                float effectiveLast = lastTick > 0 ? entry.getFloat("progress") - deltaTime : 0f;
                boolean eventFired = false;
                for (Map.Entry<Float, List<AnimationAPI.AnimationEvent>> eventEntry : anim.events.entrySet()) {
                    float eventTime = eventEntry.getKey();
                    if (playedEventTimes.contains(eventTime))
                        continue;
                    boolean shouldFire;
                    if (effectiveLast <= progress) {
                        shouldFire = effectiveLast <= eventTime && progress >= eventTime;
                    } else {
                        shouldFire = effectiveLast <= eventTime || progress >= eventTime;
                    }
                    if (shouldFire) {
                        for (AnimationAPI.AnimationEvent event : eventEntry.getValue()) {
                            fireAnimationEvent(player, event);
                        }
                        playedEvents.add(FloatTag.valueOf(eventTime));
                        eventFired = true;
                    }
                }
                if (eventFired) {
                    entry.put("playedEvents", playedEvents);
                }
            }

            i++;
        }

        if (changed) {
            ModAnimations.setCombinableList(player, list);
        }
    }

    public static void fireAnimationEvent(LivingEntity entity, AnimationAPI.AnimationEvent event) {
        if (entity == null || !entity.level().isClientSide())
            return;
        try {
            switch (event.type) {
                case "particle": {
                    String particleId = event.getString("particle", "");
                    if (!particleId.isEmpty()) {
                        int count = event.getInt("count", 1);
                        float speed = event.getFloat("speed", 0.0f);
                        net.minecraft.core.particles.ParticleOptions particle = ModAnimations.COMMON_PARTICLES.get(particleId);
                        if (particle == null) {

                            var opt = BuiltInRegistries.PARTICLE_TYPE
                                    .getOptional(new ResourceLocation(particleId));
                            if (opt.isPresent()
                                    && opt.get() instanceof net.minecraft.core.particles.SimpleParticleType spt) {
                                particle = spt;
                            }
                        }
                        if (particle != null) {
                            for (int i = 0; i < count; i++) {
                                entity.level().addParticle(particle,
                                        entity.getX() + (Math.random() - 0.5) * 0.5,
                                        entity.getY() + entity.getBbHeight() * 0.5 + (Math.random() - 0.5) * 0.5,
                                        entity.getZ() + (Math.random() - 0.5) * 0.5,
                                        (Math.random() - 0.5) * speed,
                                        (Math.random() - 0.5) * speed,
                                        (Math.random() - 0.5) * speed);
                            }
                        }
                    }
                    break;
                }
                case "camera_shake": {
                    if (!net.jaams.weaponry.configuration.client.CameraEffectsConfig.SHAKE.get())
                        break;
                    if (!(entity instanceof net.minecraft.world.entity.player.Player))
                        break;

                    String target = event.getString("target", "self").toLowerCase();
                    float radius = event.getFloat("radius", 16.0f);
                    Player localPlayer = Minecraft.getInstance().player;
                    if (localPlayer == null)
                        break;

                    boolean apply;
                    switch (target) {
                        case "all":
                            apply = true;
                            break;
                        case "nearby":
                            apply = entity.distanceTo(localPlayer) <= radius;
                            break;
                        case "self":
                        default:
                            apply = entity == localPlayer;
                            break;
                    }

                    if (apply) {
                        ModAnimations.cameraShakeIntensity = Math.max(ModAnimations.cameraShakeIntensity,
                                event.getFloat("intensity", 0.3f));
                        ModAnimations.cameraShakeDuration = Math.max(ModAnimations.cameraShakeDuration,
                                event.getInt("duration", 5));
                    }
                    break;
                }
                case "sound": {
                    String soundId = event.getString("sound", "");
                    if (!soundId.isEmpty()) {
                        float volume = event.getFloat("volume", 1.0f);
                        float pitch = event.getFloat("pitch", 1.0f);
                        BuiltInRegistries.SOUND_EVENT
                                .getOptional(new ResourceLocation(soundId))
                                .ifPresent(soundEvent -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                                        () -> () -> ClientAnimationSoundPlayer.play(soundEvent, SoundSource.NEUTRAL,
                                                volume, pitch, entity)));

                    }
                    break;
                }
                default:

                    break;
            }
        } catch (Exception e) {

        }
    }
}
