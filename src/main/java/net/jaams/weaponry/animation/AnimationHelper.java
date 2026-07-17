package net.jaams.weaponry.animation;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;

import net.jaams.weaponry.animation.AnimationAPI.PlayerAnimation;
import net.jaams.weaponry.animation.AnimationAPI.MobAnimationState;
import net.jaams.weaponry.util.ModAnimations;


public class AnimationHelper {

    
    public static float getSmoothProgress(LivingEntity entity, PlayerAnimation animation,
            float progress, float lastTick, float speed) {
        if (entity == null || animation == null || animation.length <= 0 || lastTick <= 0)
            return progress;

        float partialTick = Minecraft.getInstance().getFrameTime();
        float ageInTicks = entity.tickCount + partialTick;
        float tickDelta = ageInTicks - lastTick;

        
        if (tickDelta <= 0 || tickDelta > 3.0f)
            return progress;

        float effectiveSpeed = speed > 0 ? speed : 1.0f;
        float smoothProgress = progress + (tickDelta / 20.0f) * effectiveSpeed;

        
        if (smoothProgress >= animation.length) {
            if (animation.hold_on_last_frame) {
                smoothProgress = animation.length;
            } else if (animation.loop) {
                smoothProgress = smoothProgress % animation.length;
            } else {
                smoothProgress = animation.length;
            }
        }

        return Math.max(smoothProgress, 0);
    }

    
    public static float getSmoothProgressForPlayer(Player player, PlayerAnimation animation) {
        if (player == null || animation == null)
            return 0f;
        float progress = ModAnimations.getAnimationProgress(player);
        float lastTick = ModAnimations.getAnimationLastTick(player);
        float speed = ModAnimations.getAnimationSpeed(player);
        return getSmoothProgress(player, animation, progress, lastTick, speed);
    }

    
    public static float getSmoothProgressForMob(LivingEntity entity, PlayerAnimation animation) {
        if (entity == null || animation == null)
            return 0f;
        MobAnimationState state = AnimationAPI.mob_active_animations.get(entity.getId());
        if (state == null)
            return 0f;
        
        
        if (state.blendOutTicks > 0)
            return state.progress;
        return getSmoothProgress(entity, animation, state.progress, state.lastTick, state.speed);
    }

    public static void startAnimation(LivingEntity entity, String animationName) {
        startAnimation(entity, animationName, false, 1.0f, 0);
    }

    private static int resolveBlendOutDuration(MobAnimationState state) {
        if (state != null && state.animation != null && state.animation.blendOut > 0) {
            float speed = state.animation.blendSpeed > 0 ? state.animation.blendSpeed : 1.0f;
            return (int) Math.ceil(state.animation.blendOut / speed);
        }
        return AnimationAPI.BLEND_OUT_DURATION;
    }

    private static void startBlendOut(MobAnimationState state) {
        int duration = resolveBlendOutDuration(state);
        state.blendOutTicks = duration;
        state.blendOutDuration = duration;
        state.active = false;
    }

    public static void startAnimation(LivingEntity entity, String animationName, boolean override, float speed,
            int duration) {
        if (entity == null || animationName == null || animationName.isEmpty())
            return;
        int id = entity.getId();

        
        if (!override) {
            MobAnimationState existing = AnimationAPI.mob_active_animations.get(id);
            if (existing != null && existing.active && existing.animationName.equals(animationName)) {
                return;
            }
        }

        
        MobAnimationState existing = AnimationAPI.mob_active_animations.get(id);
        if (existing != null && existing.blendOutTicks > 0) {
            AnimationAPI.mob_active_animations.remove(id);
        }

        MobAnimationState state = new MobAnimationState();
        state.animationName = animationName;
        state.animation = AnimationAPI.animations.get(animationName);
        state.progress = 0f;
        state.lastTick = entity.tickCount;
        state.active = true;
        state.elapsedTicks = 0;
        state.override = override;
        state.speed = speed;
        state.duration = duration;
        state.blendOutTicks = 0;
        AnimationAPI.mob_active_animations.put(id, state);
    }

    
    public static void stopAnimation(LivingEntity entity) {
        if (entity == null)
            return;
        int id = entity.getId();
        MobAnimationState state = AnimationAPI.mob_active_animations.get(id);
        if (state != null && state.active && state.blendOutTicks == 0) {
            startBlendOut(state);
        } else {
            AnimationAPI.mob_active_animations.remove(id);
        }
    }

    public static boolean hasActiveAnimation(LivingEntity entity) {
        if (entity == null)
            return false;
        MobAnimationState state = AnimationAPI.mob_active_animations.get(entity.getId());
        return state != null && (state.active || state.blendOutTicks > 0);
    }

    
    public static float getMobBlendFactor(LivingEntity entity) {
        if (entity == null)
            return 0f;
        MobAnimationState state = AnimationAPI.mob_active_animations.get(entity.getId());
        if (state == null || state.blendOutTicks <= 0)
            return 0f;
        return 1.0f - (state.blendOutTicks / (float) state.blendOutDuration);
    }

    public static MobAnimationState getAnimationState(LivingEntity entity) {
        if (entity == null)
            return null;
        return AnimationAPI.mob_active_animations.get(entity.getId());
    }

    public static PlayerAnimation getActiveAnimation(LivingEntity entity) {
        if (entity == null)
            return null;
        MobAnimationState state = AnimationAPI.mob_active_animations.get(entity.getId());
        return state != null ? state.animation : null;
    }

    
    @SuppressWarnings("deprecation")
    public static float advanceAnimation(LivingEntity entity) {
        if (entity == null)
            return -1f;
        int id = entity.getId();
        MobAnimationState state = AnimationAPI.mob_active_animations.get(id);
        if (state == null)
            return -1f;

        
        if (state.blendOutTicks > 0) {
            state.blendOutTicks--;
            if (state.blendOutTicks <= 0) {
                AnimationAPI.mob_active_animations.remove(id);
                return -1f;
            }
            
            return state.progress;
        }

        if (!state.active || state.animation == null)
            return -1f;

        PlayerAnimation anim = state.animation;
        int currentTick = entity.tickCount;

        
        if (state.lastAdvanceTick < 0) {
            state.lastAdvanceTick = currentTick;
            state.lastTick = currentTick;
            state.progress = 0f;
            state.elapsedTicks = 0;
            return 0f;
        }

        
        if (currentTick <= state.lastAdvanceTick) {
            return state.progress;
        }

        int ticksElapsed = currentTick - state.lastAdvanceTick;
        state.lastAdvanceTick = currentTick;
        state.lastTick = currentTick;
        state.elapsedTicks += ticksElapsed;

        
        if (state.duration > 0 && state.elapsedTicks >= state.duration) {
            startBlendOut(state);
            return state.progress;
        }

        
        float effectiveSpeed = state.speed <= 0 ? 1.0f : state.speed;
        float deltaTime = ticksElapsed / 20.0f * effectiveSpeed;
        float prevProgress = state.progress;
        state.progress += deltaTime;

        
        if (state.progress >= anim.length) {
            if (anim.loop) {
                float loopPrevProgress = state.progress;
                state.progress = state.progress % anim.length;
                if (state.progress < loopPrevProgress || state.progress < 0.001f) {
                    state.playedSounds.clear();
                    state.playedEvents.clear();
                }
            } else if (anim.hold_on_last_frame) {
                state.progress = anim.length;
            } else {
                
                state.progress = anim.length;
                startBlendOut(state);
                return state.progress;
            }
        }

        
        if (!anim.soundEffects.isEmpty() && entity.level() instanceof ClientLevel) {
            for (var entry : anim.soundEffects.entrySet()) {
                float soundTime = entry.getKey();
                String soundId = entry.getValue();

                if (state.playedSounds.contains(soundTime))
                    continue;

                boolean shouldPlay;
                if (prevProgress <= state.progress) {
                    shouldPlay = prevProgress <= soundTime && state.progress >= soundTime;
                } else {
                    shouldPlay = prevProgress <= soundTime || state.progress >= soundTime;
                }

                if (shouldPlay) {
                    BuiltInRegistries.SOUND_EVENT
                            .getOptional(new ResourceLocation(soundId))
                            .ifPresent(soundEvent -> {
                                Minecraft.getInstance().getSoundManager().play(
                                        new AnimationSound(
                                                soundEvent,
                                                SoundSource.HOSTILE, 1.0F, 1.0F,
                                                entity));
                            });
                    state.playedSounds.add(soundTime);
                }
            }
        }

        
        if (!anim.events.isEmpty() && entity.level() instanceof ClientLevel) {
            for (var entry : anim.events.entrySet()) {
                float eventTime = entry.getKey();

                if (state.playedEvents.contains(eventTime))
                    continue;

                boolean shouldFire;
                if (prevProgress <= state.progress) {
                    shouldFire = prevProgress <= eventTime && state.progress >= eventTime;
                } else {
                    shouldFire = prevProgress <= eventTime || state.progress >= eventTime;
                }

                if (shouldFire) {
                    for (var event : entry.getValue()) {
                        ModAnimations.fireAnimationEvent(entity, event);
                    }
                    state.playedEvents.add(eventTime);
                }
            }
        }

        return state.progress;
    }

    
    public static void stopAll() {
        AnimationAPI.mob_active_animations.clear();
    }

    

    public static boolean isAnimationPlaying(LivingEntity entity) {
        return hasActiveAnimation(entity);
    }

    public static String getCurrentAnimationName(LivingEntity entity) {
        MobAnimationState state = getAnimationState(entity);
        return state != null ? state.animationName : "";
    }

    public static float getAnimationProgress(LivingEntity entity) {
        MobAnimationState state = getAnimationState(entity);
        return state != null ? state.progress : 0f;
    }

    public static float getAnimationLength(LivingEntity entity) {
        PlayerAnimation anim = getActiveAnimation(entity);
        return anim != null ? anim.length : 0f;
    }

    public static void resetAnimation(LivingEntity entity) {
        stopAnimation(entity);
    }

}
