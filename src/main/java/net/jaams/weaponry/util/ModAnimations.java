package net.jaams.weaponry.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

import net.jaams.weaponry.JaamsWeaponryMod;
import net.jaams.weaponry.animation.AnimationAPI;
import net.jaams.weaponry.animation.AnimationAPI.PlayerAnimation;
import net.jaams.weaponry.network.PlayAnimationMessage;
import net.jaams.weaponry.network.PlayMobAnimationMessage;
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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;

public class ModAnimations {

    private static final String KEY_CURRENT = "AnimationCurrent";
    private static final String KEY_PROGRESS = "AnimationProgress";
    private static final String KEY_OVERRIDE = "AnimationOverride";
    private static final String KEY_FIRST_PERSON = "AnimationFirstPerson";
    private static final String KEY_RESET = "AnimationReset";
    private static final String KEY_LAST_PROGRESS = "AnimationLastProgress";
    private static final String KEY_PLAYED_SOUNDS = "AnimationPlayedSounds";
    private static final String KEY_LAST_TICK = "AnimationLastTick";
    private static final String KEY_NULL_RENDER = "AnimationNullRender";
    private static final String KEY_HIDE_ARMS = "AnimationHideArms";
    private static final String KEY_DURATION = "animation_duration";
    private static final String KEY_SPEED = "animation_speed";
    private static final String KEY_ELAPSED_TICKS = "animation_elapsed_ticks";
    private static final String KEY_BLEND_OUT = "AnimationBlendOut";
    private static final String KEY_BLEND_OUT_TOTAL = "AnimationBlendOutTotal";
    private static final String KEY_BLEND_OUT_TICK = "AnimationBlendOutTick";
    private static final String KEY_BLEND_IN_TOTAL = "AnimationBlendInTotal";
    private static final String KEY_RESTORE_ANIMATION = "AnimationRestore";
    private static final String KEY_RESTORE_FIRST_PERSON = "AnimationRestoreFirstPerson";
    private static final String KEY_RESTORE_REASON = "AnimationRestoreReason";
    private static final String KEY_BLEND_IN = "AnimationBlendIn";
    private static final String KEY_POSE = "AnimationPose";
    private static final String KEY_CUSTOM_BLEND_OUT = "AnimationCustomBlendOut";
    private static final String KEY_PLAYED_EVENTS = "AnimationPlayedEvents";
    private static final String KEY_COMBINABLE = "AnimationCombinable";

    private static final String REASON_ATTACK = "attack";
    private static final String REASON_PA = "pa";
    private static final String REASON_HURT = "hurt";
    private static final String REASON_SWING = "swing";
    private static final String REASON_ITEM_USE = "item_use";
    private static final String REASON_MOVE = "move";
    private static final String REASON_RUN = "run";
    private static final String REASON_SNEAKING = "sneaking";
    private static final String REASON_CRAWL = "crawl";

    public static final int BLEND_OUT_DURATION = 8;

    public static final int POSE_BLEND_DURATION = 8;

    private static int resolveBlendIn(PlayerAnimation anim) {
        if (anim != null && anim.blendIn > 0) {
            float speed = anim.blendSpeed > 0 ? anim.blendSpeed : 1.0f;
            return (int) Math.ceil(anim.blendIn / speed);
        }
        return POSE_BLEND_DURATION;
    }

    public static float getAnimationBlendFactor(Player player) {
        if (player == null)
            return 1.0f;
        if (hasBlendOut(player)) {
            int remaining = getBlendOut(player);
            if (remaining <= 0)
                return 1.0f;
            int total = getBlendOutTotal(player);
            if (total <= 0)
                total = BLEND_OUT_DURATION;

            return (float) (total - remaining + 1) / total;
        }

        if (hasBlendIn(player) && isAnimationPlaying(player)) {
            int remaining = getBlendIn(player);
            if (remaining <= 0) {
                removeBlendIn(player);
                return 0.0f;
            }
            int total = getBlendInTotal(player);
            if (total <= 0)
                total = POSE_BLEND_DURATION;
            return (float) remaining / (float) total;
        }
        if (isAnimationPlaying(player))
            return 0.0f;
        return 1.0f;
    }

    public static int getBlendOut(Player player) {
        if (player == null)
            return 0;
        return getData(player).getInt(KEY_BLEND_OUT);
    }

    public static void setBlendOut(Player player, int ticks) {
        if (player == null)
            return;
        getData(player).putInt(KEY_BLEND_OUT, Math.max(0, ticks));
    }

    public static void setBlendOut(Player player, int ticks, int total) {
        if (player == null)
            return;
        getData(player).putInt(KEY_BLEND_OUT, Math.max(0, ticks));
        getData(player).putInt(KEY_BLEND_OUT_TOTAL, Math.max(1, total));
    }

    private static int getBlendOutTotal(Player player) {
        if (player == null)
            return 0;
        return getData(player).getInt(KEY_BLEND_OUT_TOTAL);
    }

    public static boolean hasBlendOut(Player player) {
        if (player == null)
            return false;
        return getData(player).contains(KEY_BLEND_OUT);
    }

    public static void removeBlendOut(Player player) {
        if (player == null)
            return;
        getData(player).remove(KEY_BLEND_OUT);
        getData(player).remove(KEY_BLEND_OUT_TOTAL);
    }

    private static int getBlendOutTick(Player player) {
        if (player == null)
            return -1;
        return getData(player).getInt(KEY_BLEND_OUT_TICK);
    }

    private static void setBlendOutTick(Player player, int tick) {
        if (player == null)
            return;
        getData(player).putInt(KEY_BLEND_OUT_TICK, tick);
    }

    private static void removeBlendOutTick(Player player) {
        if (player == null)
            return;
        getData(player).remove(KEY_BLEND_OUT_TICK);
    }

    private static String getRestoreAnimation(Player player) {
        if (player == null)
            return "";
        return getData(player).getString(KEY_RESTORE_ANIMATION);
    }

    private static void setRestoreAnimation(Player player, String name) {
        if (player == null)
            return;
        getData(player).putString(KEY_RESTORE_ANIMATION, name);
    }

    public static boolean hasRestoreAnimation(Player player) {
        if (player == null)
            return false;
        String name = getData(player).getString(KEY_RESTORE_ANIMATION);
        return !name.isEmpty();
    }

    private static void removeRestoreAnimation(Player player) {
        if (player == null)
            return;
        getData(player).remove(KEY_RESTORE_ANIMATION);
    }

    private static boolean getRestoreFirstPerson(Player player) {
        if (player == null)
            return false;
        return getData(player).getBoolean(KEY_RESTORE_FIRST_PERSON);
    }

    private static void setRestoreFirstPerson(Player player, boolean firstPerson) {
        if (player == null)
            return;
        getData(player).putBoolean(KEY_RESTORE_FIRST_PERSON, firstPerson);
    }

    private static boolean hasRestoreFirstPerson(Player player) {
        if (player == null)
            return false;
        return getData(player).contains(KEY_RESTORE_FIRST_PERSON);
    }

    private static void removeRestoreFirstPerson(Player player) {
        if (player == null)
            return;
        getData(player).remove(KEY_RESTORE_FIRST_PERSON);
    }

    private static String getRestoreReason(Player player) {
        if (player == null)
            return "";
        return getData(player).getString(KEY_RESTORE_REASON);
    }

    private static void setRestoreReason(Player player, String reason) {
        if (player == null)
            return;
        getData(player).putString(KEY_RESTORE_REASON, reason);
    }

    private static boolean hasRestoreReason(Player player) {
        if (player == null)
            return false;
        return getData(player).contains(KEY_RESTORE_REASON);
    }

    private static void removeRestoreReason(Player player) {
        if (player == null)
            return;
        getData(player).remove(KEY_RESTORE_REASON);
    }

    private static int getBlendIn(Player player) {
        if (player == null)
            return 0;
        return getData(player).getInt(KEY_BLEND_IN);
    }

    public static void setBlendIn(Player player, int ticks) {
        if (player == null)
            return;
        getData(player).putInt(KEY_BLEND_IN, Math.max(0, ticks));
    }

    public static void setBlendIn(Player player, int ticks, int total) {
        if (player == null)
            return;
        getData(player).putInt(KEY_BLEND_IN, Math.max(0, ticks));
        getData(player).putInt(KEY_BLEND_IN_TOTAL, Math.max(1, total));
    }

    private static int getBlendInTotal(Player player) {
        if (player == null)
            return 0;
        return getData(player).getInt(KEY_BLEND_IN_TOTAL);
    }

    private static boolean hasBlendIn(Player player) {
        if (player == null)
            return false;
        return getData(player).contains(KEY_BLEND_IN);
    }

    private static void removeBlendIn(Player player) {
        if (player == null)
            return;
        getData(player).remove(KEY_BLEND_IN);
        getData(player).remove(KEY_BLEND_IN_TOTAL);
    }

    public static String getPose(Player player) {
        if (player == null)
            return "";
        CompoundTag data = getData(player);
        if (data.contains(KEY_POSE))
            return data.getString(KEY_POSE);
        return "";
    }

    public static void setPose(Player player, String name) {
        if (player == null || name == null || name.isEmpty())
            return;
        getData(player).putString(KEY_POSE, name);
    }

    public static boolean hasPose(Player player) {
        if (player == null)
            return false;
        CompoundTag data = getData(player);
        return data.contains(KEY_POSE);
    }

    public static void removePose(Player player) {
        if (player == null)
            return;
        getData(player).remove(KEY_POSE);
    }

    private static void setCustomBlendOut(Player player, int ticks) {
        if (player == null)
            return;
        getData(player).putInt(KEY_CUSTOM_BLEND_OUT, ticks);
    }

    private static int getCustomBlendOut(Player player) {
        if (player == null)
            return -1;
        CompoundTag data = getData(player);
        if (!data.contains(KEY_CUSTOM_BLEND_OUT))
            return -1;
        int val = data.getInt(KEY_CUSTOM_BLEND_OUT);
        data.remove(KEY_CUSTOM_BLEND_OUT);
        return val;
    }

    private static ListTag getCombinableList(Player player) {
        if (player == null)
            return new ListTag();
        CompoundTag data = getData(player);
        if (data.contains(KEY_COMBINABLE))
            return data.getList(KEY_COMBINABLE, Tag.TAG_COMPOUND);
        return new ListTag();
    }

    private static void setCombinableList(Player player, ListTag list) {
        if (player == null)
            return;
        getData(player).put(KEY_COMBINABLE, list);
    }

    public static boolean hasCombinableAnimations(Player player) {
        if (player == null)
            return false;
        CompoundTag data = getData(player);
        if (!data.contains(KEY_COMBINABLE))
            return false;
        ListTag list = data.getList(KEY_COMBINABLE, Tag.TAG_COMPOUND);
        return !list.isEmpty();
    }

    public static void addCombinableAnimation(Player player, String name, float speed) {
        if (player == null || name == null || name.isEmpty())
            return;
        ListTag list = getCombinableList(player);

        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            if (entry.getString("name").equals(name)) {
                list.remove(i);
                break;
            }
        }
        CompoundTag entry = new CompoundTag();
        entry.putString("name", name);
        entry.putFloat("progress", 0f);
        entry.putFloat("lastTick", 0f);
        entry.putFloat("speed", speed);
        entry.put("playedSounds", new ListTag());
        list.add(entry);
        setCombinableList(player, list);
    }

    public static void removeCombinableAnimation(Player player, String name) {
        if (player == null || name == null || name.isEmpty())
            return;
        ListTag list = getCombinableList(player);
        for (int i = 0; i < list.size(); i++) {
            if (list.getCompound(i).getString("name").equals(name)) {
                list.remove(i);
                break;
            }
        }
        setCombinableList(player, list);
    }

    public static void clearCombinableAnimations(Player player) {
        if (player == null)
            return;
        getData(player).remove(KEY_COMBINABLE);
    }

    public static Map<String, Float> getCombinableRenderData(Player player) {
        Map<String, Float> result = new HashMap<>();
        if (player == null)
            return result;
        ListTag list = getCombinableList(player);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            String name = entry.getString("name");
            if (name.isEmpty())
                continue;
            PlayerAnimation anim = getAnimation(name);
            if (anim == null)
                continue;
            result.put(name, entry.getFloat("progress"));
        }
        return result;
    }

    public static void advanceCombinableAnimations(Player player, float ageInTicks) {
        if (player == null || player.level().isClientSide == false)
            return;
        ListTag list = getCombinableList(player);
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

            PlayerAnimation anim = getAnimation(animName);
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

                    if (shouldPlay && player.level() instanceof ClientLevel) {
                        final float st = soundTime;
                        BuiltInRegistries.SOUND_EVENT
                                .getOptional(new ResourceLocation(soundId))
                                .ifPresent(soundEvent -> {
                                    Minecraft.getInstance().getSoundManager().play(
                                            new net.jaams.weaponry.animation.AnimationSound(
                                                    soundEvent,
                                                    SoundSource.NEUTRAL, 1.0F, 1.0F,
                                                    player));
                                });
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
            setCombinableList(player, list);
        }
    }

    public static String resolveRandomGroup(String name) {
        if (name == null)
            return name;
        AnimationAPI.RandomAnimationGroup group = AnimationAPI.randomGroups.get(name);
        if (group != null) {
            String picked = group.pick();
            if (picked != null && !picked.isEmpty())
                return picked;
        }
        return name;
    }

    public static void playAnimation(ServerPlayer target, String animationName, boolean override, boolean firstPerson) {
        if (target == null || animationName == null || animationName.isEmpty())
            return;
        animationName = resolveRandomGroup(animationName);
        JaamsWeaponryMod.PACKET_HANDLER.send(
                PacketDistributor.PLAYER.with(() -> target),
                new PlayAnimationMessage(target.getId(), animationName, override, firstPerson));
    }

    public static void playAnimation(ServerPlayer target, String animationName) {
        playAnimation(target, animationName, false, false);
    }

    public static void stopAnimation(ServerPlayer target) {
        if (target == null)
            return;
        JaamsWeaponryMod.PACKET_HANDLER.send(
                PacketDistributor.PLAYER.with(() -> target),
                new PlayAnimationMessage(target.getId(), "", false, false));
    }

    public static void playAnimationForTracking(ServerPlayer target, String animationName, boolean override,
            boolean firstPerson) {
        if (target == null || animationName == null || animationName.isEmpty())
            return;
        animationName = resolveRandomGroup(animationName);
        JaamsWeaponryMod.PACKET_HANDLER.send(
                PacketDistributor.TRACKING_ENTITY.with(() -> target),
                new PlayAnimationMessage(target.getId(), animationName, override, firstPerson));
    }

    public static void stopAnimationForTracking(ServerPlayer target) {
        if (target == null)
            return;
        JaamsWeaponryMod.PACKET_HANDLER.send(
                PacketDistributor.TRACKING_ENTITY.with(() -> target),
                new PlayAnimationMessage(target.getId(), "", false, false));
    }

    public static void playAnimationToAll(ServerPlayer target, String animationName, boolean override,
            boolean firstPerson) {
        animationName = resolveRandomGroup(animationName);
        playAnimation(target, animationName, override, firstPerson);
        playAnimationForTracking(target, animationName, override, firstPerson);
    }

    public static void stopAnimationToAll(ServerPlayer target) {
        stopAnimation(target);
        stopAnimationForTracking(target);
    }

    private static CompoundTag getData(Player player) {
        return player.getPersistentData();
    }

    public static String getCurrentAnimationName(Player player) {
        if (player == null)
            return "";
        return getData(player).getString(KEY_CURRENT);
    }

    public static void setCurrentAnimationName(Player player, String name) {
        if (player == null)
            return;
        getData(player).putString(KEY_CURRENT, name);
    }

    public static void removeCurrentAnimationName(Player player) {
        if (player == null)
            return;
        getData(player).remove(KEY_CURRENT);
    }

    public static boolean hasActiveAnimationData(Player player) {
        if (player == null)
            return false;
        CompoundTag data = getData(player);
        return data.contains(KEY_CURRENT) && !data.getString(KEY_CURRENT).isEmpty();
    }

    public static float getAnimationProgress(Player player) {
        if (player == null)
            return 0f;
        return getData(player).getFloat(KEY_PROGRESS);
    }

    public static void setAnimationProgress(Player player, float progress) {
        if (player == null)
            return;
        getData(player).putFloat(KEY_PROGRESS, Math.max(0, progress));
    }

    public static void removeAnimationProgress(Player player) {
        if (player == null)
            return;
        getData(player).remove(KEY_PROGRESS);
    }

    public static boolean hasAnimationProgress(Player player) {
        if (player == null)
            return false;
        return getData(player).contains(KEY_PROGRESS);
    }

    public static boolean hasAnimationOverride(Player player) {
        if (player == null)
            return false;
        return getData(player).getBoolean(KEY_OVERRIDE);
    }

    public static void setAnimationOverride(Player player, boolean override) {
        if (player == null)
            return;
        getData(player).putBoolean(KEY_OVERRIDE, override);
    }

    public static void removeAnimationOverride(Player player) {
        if (player == null)
            return;
        getData(player).remove(KEY_OVERRIDE);
    }

    public static boolean hasHideArms(Player player) {
        if (player == null)
            return false;

        if (getData(player).getBoolean(KEY_HIDE_ARMS))
            return true;

        PlayerAnimation anim = getActiveAnimation(player);
        return anim != null && anim.hideArms;
    }

    public static void setHideArms(Player player, boolean hideArms) {
        if (player == null)
            return;
        getData(player).putBoolean(KEY_HIDE_ARMS, hideArms);
    }

    public static void removeHideArms(Player player) {
        if (player == null)
            return;
        getData(player).remove(KEY_HIDE_ARMS);
    }

    public static int getAnimationDuration(Player player) {
        if (player == null)
            return 0;
        CompoundTag data = getData(player);
        if (!data.contains(KEY_DURATION))
            return 0;
        return data.getInt(KEY_DURATION);
    }

    public static void setAnimationDuration(Player player, int ticks) {
        if (player == null)
            return;
        getData(player).putInt(KEY_DURATION, ticks);
    }

    public static void removeAnimationDuration(Player player) {
        if (player == null)
            return;
        getData(player).remove(KEY_DURATION);
    }

    public static float getAnimationSpeed(Player player) {
        if (player == null)
            return 1.0f;
        CompoundTag data = getData(player);
        if (!data.contains(KEY_SPEED))
            return 1.0f;
        return data.getFloat(KEY_SPEED);
    }

    public static void setAnimationSpeed(Player player, float speed) {
        if (player == null)
            return;
        getData(player).putFloat(KEY_SPEED, speed);
    }

    public static void removeAnimationSpeed(Player player) {
        if (player == null)
            return;
        getData(player).remove(KEY_SPEED);
    }

    public static float getAnimationElapsedTicks(Player player) {
        if (player == null)
            return 0f;
        CompoundTag data = getData(player);
        if (!data.contains(KEY_ELAPSED_TICKS))
            return 0f;
        return data.getFloat(KEY_ELAPSED_TICKS);
    }

    public static void setAnimationElapsedTicks(Player player, float ticks) {
        if (player == null)
            return;
        getData(player).putFloat(KEY_ELAPSED_TICKS, ticks);
    }

    public static void removeAnimationElapsedTicks(Player player) {
        if (player == null)
            return;
        getData(player).remove(KEY_ELAPSED_TICKS);
    }

    public static boolean isFirstPersonAnimation(Player player) {
        if (player == null)
            return false;
        return getData(player).getBoolean(KEY_FIRST_PERSON);
    }

    public static void setFirstPersonAnimation(Player player, boolean firstPerson) {
        if (player == null)
            return;
        getData(player).putBoolean(KEY_FIRST_PERSON, firstPerson);
    }

    public static boolean isAnimationReset(Player player) {
        if (player == null)
            return false;
        return getData(player).getBoolean(KEY_RESET);
    }

    public static void setAnimationReset(Player player, boolean reset) {
        if (player == null)
            return;
        getData(player).putBoolean(KEY_RESET, reset);
    }

    public static void removeAnimationReset(Player player) {
        if (player == null)
            return;
        getData(player).remove(KEY_RESET);
    }

    public static float getAnimationLastProgress(Player player) {
        if (player == null)
            return 0f;
        return getData(player).getFloat(KEY_LAST_PROGRESS);
    }

    public static void setAnimationLastProgress(Player player, float progress) {
        if (player == null)
            return;
        getData(player).putFloat(KEY_LAST_PROGRESS, progress);
    }

    public static void removeAnimationLastProgress(Player player) {
        if (player == null)
            return;
        getData(player).remove(KEY_LAST_PROGRESS);
    }

    public static float getAnimationLastTick(Player player) {
        if (player == null)
            return 0f;
        return getData(player).getFloat(KEY_LAST_TICK);
    }

    public static void setAnimationLastTick(Player player, float tick) {
        if (player == null)
            return;
        getData(player).putFloat(KEY_LAST_TICK, tick);
    }

    public static void removeAnimationLastTick(Player player) {
        if (player == null)
            return;
        getData(player).remove(KEY_LAST_TICK);
    }

    public static ListTag getAnimationPlayedSounds(Player player) {
        if (player == null)
            return new ListTag();
        return getData(player).getList(KEY_PLAYED_SOUNDS, Tag.TAG_FLOAT);
    }

    public static void setAnimationPlayedSounds(Player player, ListTag tag) {
        if (player == null)
            return;
        getData(player).put(KEY_PLAYED_SOUNDS, tag);
    }

    public static void removeAnimationPlayedSounds(Player player) {
        if (player == null)
            return;
        getData(player).remove(KEY_PLAYED_SOUNDS);
    }

    private static ListTag getAnimationPlayedEvents(Player player) {
        if (player == null)
            return new ListTag();
        CompoundTag data = getData(player);
        return data.contains(KEY_PLAYED_EVENTS) ? data.getList(KEY_PLAYED_EVENTS, Tag.TAG_FLOAT) : new ListTag();
    }

    private static void setAnimationPlayedEvents(Player player, ListTag tag) {
        if (player == null)
            return;
        getData(player).put(KEY_PLAYED_EVENTS, tag);
    }

    private static void removeAnimationPlayedEvents(Player player) {
        if (player == null)
            return;
        getData(player).remove(KEY_PLAYED_EVENTS);
    }

    public static int getAnimationNullRender(Player player) {
        if (player == null)
            return 0;
        return getData(player).getInt(KEY_NULL_RENDER);
    }

    public static void setAnimationNullRender(Player player, int value) {
        if (player == null)
            return;
        getData(player).putInt(KEY_NULL_RENDER, Math.max(0, value));
    }

    public static boolean hasAnimationNullRender(Player player) {
        if (player == null)
            return false;
        return getData(player).contains(KEY_NULL_RENDER);
    }

    public static void removeAnimationNullRender(Player player) {
        if (player == null)
            return;
        getData(player).remove(KEY_NULL_RENDER);
    }

    public static void resetAllAnimationData(Player player) {
            if (player == null)
                return;
            setAnimationReset(player, true);
            setFirstPersonAnimation(player, false);
            removeCurrentAnimationName(player);
            removeAnimationProgress(player);
            removeAnimationLastProgress(player);
            removeAnimationLastTick(player);
            removeAnimationPlayedSounds(player);
            removeAnimationPlayedEvents(player);
            removeAnimationOverride(player);
            removeAnimationDuration(player);
            removeAnimationSpeed(player);
            removeAnimationElapsedTicks(player);
            removeBlendOut(player);
            removeBlendOutTick(player);
            removeRestoreAnimation(player);
            removeRestoreFirstPerson(player);
            removeBlendIn(player);
            removePose(player);
            clearCombinableAnimations(player);
            clearActiveAnimation(player);
            player.noCulling = false;
        }

    public static void finishAnimation(Player player) {
        if (player == null)
            return;

        removeCurrentAnimationName(player);
        removeAnimationLastProgress(player);
        removeAnimationPlayedSounds(player);
        removeAnimationPlayedEvents(player);
        removeAnimationDuration(player);
        removeAnimationSpeed(player);
        removeAnimationElapsedTicks(player);
        removeRestoreAnimation(player);
        removeRestoreFirstPerson(player);
        removeBlendIn(player);
        setAnimationReset(player, false);
        setFirstPersonAnimation(player, false);
        int customBlendOut = getCustomBlendOut(player);
        int blendOut = customBlendOut > 0 ? customBlendOut : BLEND_OUT_DURATION;

        PlayerAnimation activeAnim = getActiveAnimation(player);
        if (activeAnim != null && activeAnim.blendSpeed > 0 && activeAnim.blendSpeed != 1.0f) {
            blendOut = (int) Math.ceil(blendOut / activeAnim.blendSpeed);
        }
        setBlendOut(player, blendOut, blendOut);

    }

    public static void clearAnimationState(Player player) {
            if (player == null)
                return;
            removeCurrentAnimationName(player);
            removeAnimationProgress(player);
            removeAnimationLastProgress(player);
            removeAnimationLastTick(player);
            removeAnimationPlayedSounds(player);
            removeAnimationPlayedEvents(player);
            removeAnimationDuration(player);
            removeAnimationSpeed(player);
            removeAnimationElapsedTicks(player);
            removeRestoreAnimation(player);
            removeRestoreFirstPerson(player);
            removeBlendIn(player);
            removeBlendOut(player);
            removeBlendOutTick(player);
            removeAnimationOverride(player);
            setAnimationReset(player, false);
            setFirstPersonAnimation(player, false);
            clearActiveAnimation(player);
            player.noCulling = false;
        }

    @Nullable
    public static PlayerAnimation getActiveAnimation(Player player) {
        if (player == null)
            return null;
        return AnimationAPI.active_animations.get(player);
    }

    public static void setActiveAnimation(Player player, @Nullable PlayerAnimation animation) {
        if (player == null)
            return;
        AnimationAPI.active_animations.put(player, animation);
    }

    public static void clearActiveAnimation(Player player) {
        if (player == null)
            return;
        AnimationAPI.active_animations.put(player, null);
    }

    public static boolean isAnimationPlaying(Player player) {
        if (player == null)
            return false;
        return AnimationAPI.active_animations.containsKey(player)
                && AnimationAPI.active_animations.get(player) != null;
    }

    @Nullable
    public static PlayerAnimation getAnimation(String name) {
        if (name == null)
            return null;
        return AnimationAPI.animations.get(name);
    }

    public static boolean isValidAnimation(String name) {
        return name != null && AnimationAPI.animations.containsKey(name);
    }

    public static Set<String> getLoadedAnimationNames() {
        return AnimationAPI.animations.keySet();
    }

    public static List<String> getLoadedAnimationNamesSorted() {
        return AnimationAPI.animations.keySet().stream().sorted().collect(Collectors.toList());
    }

    public static int getLoadedAnimationCount() {
        return AnimationAPI.animations.size();
    }

    public static boolean registerAnimation(String name, PlayerAnimation animation) {
        if (name == null || name.isEmpty() || animation == null)
            return false;
        if (AnimationAPI.animations.containsKey(name))
            return false;
        AnimationAPI.animations.put(name, animation);
        return true;
    }

    public static boolean unregisterAnimation(String name) {
        if (name == null)
            return false;
        return AnimationAPI.animations.remove(name) != null;
    }

    public static float getAnimationLength(Player player) {
        if (player == null)
            return 0f;
        PlayerAnimation anim = getActiveAnimation(player);
        return anim != null ? anim.length : 0f;
    }

    public static float getAnimationLength(String name) {
        PlayerAnimation anim = getAnimation(name);
        return anim != null ? anim.length : 0f;
    }

    public static boolean isLocalPlayerInFirstPerson(Player player) {
        Minecraft mc = Minecraft.getInstance();
        if (player != mc.player)
            return false;
        if (!mc.options.getCameraType().isFirstPerson())
            return false;
        return true;
    }

    public static boolean shouldRenderInFirstPerson(Player player) {
        if (ModUtils.isEntityInBattleMode(player))
            return false;
        return isFirstPersonAnimation(player) && isLocalPlayerInFirstPerson(player)
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

    public static class AnimationTickResult {
        @Nullable
        public final PlayerAnimation animation;
        public final float progress;
        public final boolean active;

        public AnimationTickResult(@Nullable PlayerAnimation animation, float progress, boolean active) {
            this.animation = animation;
            this.progress = progress;
            this.active = active;
        }
    }

    public static AnimationTickResult advanceAnimation(Player player, float ageInTicks) {
        if (player == null) {
            return new AnimationTickResult(null, 0f, false);
        }

        if (isAnimationReset(player)) {
            removeAnimationReset(player);
            removeAnimationLastProgress(player);
            removeAnimationPlayedSounds(player);
            removeAnimationPlayedEvents(player);
            clearActiveAnimation(player);
            player.noCulling = false;
        }

        // Remove first-person data if the player's model is not humanoid
        if (isFirstPersonAnimation(player) && !isUsingCompatibleModel(player)) {
            setFirstPersonAnimation(player, false);
            removeRestoreFirstPerson(player);
        }

        String playingAnimation = getCurrentAnimationName(player);
        if (playingAnimation.isEmpty()) {

            if (hasBlendOut(player)) {

                int currentIntTick = (int) Math.floor(ageInTicks);
                int lastDecrementTick = getBlendOutTick(player);
                if (currentIntTick != lastDecrementTick) {
                    setBlendOutTick(player, currentIntTick);
                    int remaining = getBlendOut(player);
                    if (remaining <= 1) {

                        removeBlendOut(player);
                        removeBlendOutTick(player);
                        removeAnimationProgress(player);
                        removeAnimationLastTick(player);
                        clearActiveAnimation(player);
                        player.noCulling = false;
                        return new AnimationTickResult(null, 0f, false);
                    } else {
                        setBlendOut(player, remaining - 1);
                    }
                }

                PlayerAnimation blendAnim = getActiveAnimation(player);
                float blendProgress = getAnimationProgress(player);
                if (blendAnim != null) {
                    return new AnimationTickResult(blendAnim, blendProgress, true);
                }
            }

            if (hasRestoreAnimation(player)) {
                String reason = getRestoreReason(player);
                boolean shouldRestore = false;

                if (reason == null || reason.isEmpty() || reason.equals(REASON_ATTACK)) {
                    float attackStrength = player.getAttackStrengthScale(0.0F);
                    shouldRestore = attackStrength >= 0.9F;
                } else if (reason.equals(REASON_PA)) {
                    shouldRestore = !net.jaams.weaponry.compat.PlayerAnimatorCompat.isPlayerAnimatorActive(player);
                } else if (reason.equals(REASON_HURT)) {
                    shouldRestore = player.hurtTime <= 0 && player.hurtDuration <= 0;
                } else if (reason.equals(REASON_SWING)) {
                    shouldRestore = !player.swinging;
                } else if (reason.equals(REASON_ITEM_USE)) {
                    shouldRestore = !player.isUsingItem();
                } else if (reason.equals(REASON_MOVE)) {
                    shouldRestore = player.getDeltaMovement().horizontalDistanceSqr() < 0.005;
                } else if (reason.equals(REASON_RUN)) {
                    shouldRestore = !player.isSprinting();
                } else if (reason.equals(REASON_SNEAKING)) {
                    shouldRestore = !player.isCrouching();
                } else if (reason.equals(REASON_CRAWL)) {
                    shouldRestore = !player.isVisuallyCrawling();
                }

                if (shouldRestore) {
                    String restoreName = getRestoreAnimation(player);
                    removeRestoreAnimation(player);
                    removeRestoreReason(player);
                    PlayerAnimation restoreAnim = getAnimation(restoreName);
                    if (restoreAnim != null) {

                        setCurrentAnimationName(player, restoreName);
                        int blendInDuration = resolveBlendIn(restoreAnim);
                        setBlendIn(player, blendInDuration, blendInDuration);

                        if (hasRestoreFirstPerson(player)) {
                            setFirstPersonAnimation(player, getRestoreFirstPerson(player));
                            removeRestoreFirstPerson(player);
                        }

                        removeAnimationProgress(player);
                        removeAnimationLastTick(player);
                        removeAnimationPlayedSounds(player);
                        removeAnimationPlayedEvents(player);

                        setActiveAnimation(player, restoreAnim);
                        return new AnimationTickResult(restoreAnim, 0f, true);
                    }
                }
            }

            if (hasPose(player) && !hasRestoreAnimation(player)) {
                String poseName = getPose(player);
                PlayerAnimation poseAnim = getAnimation(poseName);
                if (poseAnim != null) {
                    removeBlendOut(player);
                    removeBlendOutTick(player);
                    setCurrentAnimationName(player, poseName);
                    int blendInDuration = resolveBlendIn(poseAnim);
                    setBlendIn(player, blendInDuration, blendInDuration);
                    removeAnimationProgress(player);
                    removeAnimationLastTick(player);
                    removeAnimationPlayedSounds(player);
                    removeAnimationPlayedEvents(player);
                    setActiveAnimation(player, poseAnim);
                    return new AnimationTickResult(poseAnim, 0f, true);
                }
            }

            return new AnimationTickResult(null, 0f, false);
        }

        if (hasAnimationOverride(player)) {
            removeAnimationOverride(player);
            removeAnimationProgress(player);
            removeAnimationLastProgress(player);
            removeAnimationPlayedSounds(player);
            removeAnimationPlayedEvents(player);
            clearActiveAnimation(player);
        }

        PlayerAnimation animation = getActiveAnimation(player);
        if (animation == null) {
            animation = getAnimation(playingAnimation);
            if (animation == null) {
                JaamsWeaponryMod.LOGGER.info(
                        "Attempted to play null animation '{}', did animations fail to load?", playingAnimation);
                return new AnimationTickResult(null, 0f, false);
            }
            setActiveAnimation(player, animation);
        }

        if (animation.cancelOnAttack) {
            float attackStrength = player.getAttackStrengthScale(0.0F);
            if (attackStrength < 0.9F) {
                String animName = getCurrentAnimationName(player);
                boolean wasFirstPerson = isFirstPersonAnimation(player);

                if (animation.blendOut > 0) {
                    setCustomBlendOut(player, animation.blendOut);
                }
                finishAnimation(player);
                if (!animName.isEmpty()) {
                    setRestoreAnimation(player, animName);
                    setRestoreFirstPerson(player, wasFirstPerson);
                    setRestoreReason(player, REASON_ATTACK);
                }
                PlayerAnimation blendAnim = getActiveAnimation(player);
                float blendProgress = getAnimationProgress(player);
                if (blendAnim != null) {
                    return new AnimationTickResult(blendAnim, blendProgress, true);
                }
                return new AnimationTickResult(animation, 0f, false);
            }
        }

        if (animation.cancelOnPlayerAnimator) {
            if (net.jaams.weaponry.compat.PlayerAnimatorCompat.isPlayerAnimatorActive(player)) {
                String animName = getCurrentAnimationName(player);
                boolean wasFirstPerson = isFirstPersonAnimation(player);

                if (animation.blendOut > 0) {
                    setCustomBlendOut(player, animation.blendOut);
                }
                finishAnimation(player);
                if (!animName.isEmpty()) {
                    setRestoreAnimation(player, animName);
                    setRestoreFirstPerson(player, wasFirstPerson);
                    setRestoreReason(player, REASON_PA);
                }
                PlayerAnimation blendAnim = getActiveAnimation(player);
                float blendProgress = getAnimationProgress(player);
                if (blendAnim != null) {
                    return new AnimationTickResult(blendAnim, blendProgress, true);
                }
                return new AnimationTickResult(animation, 0f, false);
            }
        }

        if (animation.cancelOnHurt) {
            if (player.hurtTime > 0 || player.hurtDuration > 0) {
                String animName = getCurrentAnimationName(player);
                boolean wasFirstPerson = isFirstPersonAnimation(player);
                if (animation.blendOut > 0) {
                    setCustomBlendOut(player, animation.blendOut);
                }
                finishAnimation(player);
                if (!animName.isEmpty()) {
                    setRestoreAnimation(player, animName);
                    setRestoreFirstPerson(player, wasFirstPerson);
                    setRestoreReason(player, REASON_HURT);
                }
                PlayerAnimation blendAnim = getActiveAnimation(player);
                float blendProgress = getAnimationProgress(player);
                if (blendAnim != null) {
                    return new AnimationTickResult(blendAnim, blendProgress, true);
                }
                return new AnimationTickResult(animation, 0f, false);
            }
        }

        if (animation.cancelOnSwing) {
            if (player.swinging) {
                String animName = getCurrentAnimationName(player);
                boolean wasFirstPerson = isFirstPersonAnimation(player);
                if (animation.blendOut > 0) {
                    setCustomBlendOut(player, animation.blendOut);
                }
                finishAnimation(player);
                if (!animName.isEmpty()) {
                    setRestoreAnimation(player, animName);
                    setRestoreFirstPerson(player, wasFirstPerson);
                    setRestoreReason(player, REASON_SWING);
                }
                PlayerAnimation blendAnim = getActiveAnimation(player);
                float blendProgress = getAnimationProgress(player);
                if (blendAnim != null) {
                    return new AnimationTickResult(blendAnim, blendProgress, true);
                }
                return new AnimationTickResult(animation, 0f, false);
            }
        }

        if (animation.cancelOnItemUse) {
            if (player.isUsingItem()) {
                String animName = getCurrentAnimationName(player);
                boolean wasFirstPerson = isFirstPersonAnimation(player);
                if (animation.blendOut > 0) {
                    setCustomBlendOut(player, animation.blendOut);
                }
                finishAnimation(player);
                if (!animName.isEmpty()) {
                    setRestoreAnimation(player, animName);
                    setRestoreFirstPerson(player, wasFirstPerson);
                    setRestoreReason(player, REASON_ITEM_USE);
                }
                PlayerAnimation blendAnim = getActiveAnimation(player);
                float blendProgress = getAnimationProgress(player);
                if (blendAnim != null) {
                    return new AnimationTickResult(blendAnim, blendProgress, true);
                }
                return new AnimationTickResult(animation, 0f, false);
            }
        }

        if (animation.cancelOnMove) {
            if (player.getDeltaMovement().horizontalDistanceSqr() >= 0.005) {
                String animName = getCurrentAnimationName(player);
                boolean wasFirstPerson = isFirstPersonAnimation(player);
                if (animation.blendOut > 0) {
                    setCustomBlendOut(player, animation.blendOut);
                }
                finishAnimation(player);
                if (!animName.isEmpty()) {
                    setRestoreAnimation(player, animName);
                    setRestoreFirstPerson(player, wasFirstPerson);
                    setRestoreReason(player, REASON_MOVE);
                }
                PlayerAnimation blendAnim = getActiveAnimation(player);
                float blendProgress = getAnimationProgress(player);
                if (blendAnim != null) {
                    return new AnimationTickResult(blendAnim, blendProgress, true);
                }
                return new AnimationTickResult(animation, 0f, false);
            }
        }

        if (animation.cancelOnRun) {
            if (player.isSprinting()) {
                String animName = getCurrentAnimationName(player);
                boolean wasFirstPerson = isFirstPersonAnimation(player);
                if (animation.blendOut > 0) {
                    setCustomBlendOut(player, animation.blendOut);
                }
                finishAnimation(player);
                if (!animName.isEmpty()) {
                    setRestoreAnimation(player, animName);
                    setRestoreFirstPerson(player, wasFirstPerson);
                    setRestoreReason(player, REASON_RUN);
                }
                PlayerAnimation blendAnim = getActiveAnimation(player);
                float blendProgress = getAnimationProgress(player);
                if (blendAnim != null) {
                    return new AnimationTickResult(blendAnim, blendProgress, true);
                }
                return new AnimationTickResult(animation, 0f, false);
            }
        }

        if (animation.cancelOnSneaking) {
            if (player.isCrouching()) {
                String animName = getCurrentAnimationName(player);
                boolean wasFirstPerson = isFirstPersonAnimation(player);
                if (animation.blendOut > 0) {
                    setCustomBlendOut(player, animation.blendOut);
                }
                finishAnimation(player);
                if (!animName.isEmpty()) {
                    setRestoreAnimation(player, animName);
                    setRestoreFirstPerson(player, wasFirstPerson);
                    setRestoreReason(player, REASON_SNEAKING);
                }
                PlayerAnimation blendAnim = getActiveAnimation(player);
                float blendProgress = getAnimationProgress(player);
                if (blendAnim != null) {
                    return new AnimationTickResult(blendAnim, blendProgress, true);
                }
                return new AnimationTickResult(animation, 0f, false);
            }
        }

        if (animation.cancelOnCrawl) {
            if (player.isVisuallyCrawling()) {
                String animName = getCurrentAnimationName(player);
                boolean wasFirstPerson = isFirstPersonAnimation(player);
                if (animation.blendOut > 0) {
                    setCustomBlendOut(player, animation.blendOut);
                }
                finishAnimation(player);
                if (!animName.isEmpty()) {
                    setRestoreAnimation(player, animName);
                    setRestoreFirstPerson(player, wasFirstPerson);
                    setRestoreReason(player, REASON_CRAWL);
                }
                PlayerAnimation blendAnim = getActiveAnimation(player);
                float blendProgress = getAnimationProgress(player);
                if (blendAnim != null) {
                    return new AnimationTickResult(blendAnim, blendProgress, true);
                }
                return new AnimationTickResult(animation, 0f, false);
            }
        }

        float progress;
        float lastProgress;
        ListTag playedSounds;

        if (!hasAnimationProgress(player)) {
            progress = 0f;
            setAnimationProgress(player, 0f);
            setAnimationLastTick(player, ageInTicks);
            setAnimationLastProgress(player, 0f);
            setAnimationElapsedTicks(player, 0f);
            lastProgress = 0f;
            playedSounds = new ListTag();
        } else {
            progress = getAnimationProgress(player);
            lastProgress = getAnimationLastProgress(player);
            playedSounds = getAnimationPlayedSounds(player);

            float lastTickTime = getAnimationLastTick(player);
            float deltaTicks = ageInTicks - lastTickTime;

            float elapsedTicks = getAnimationElapsedTicks(player) + deltaTicks;
            setAnimationElapsedTicks(player, elapsedTicks);

            int maxDuration = getAnimationDuration(player);
            if (maxDuration > 0 && elapsedTicks >= maxDuration) {
                if (animation.blendOut > 0) {
                    setCustomBlendOut(player, animation.blendOut);
                }
                finishAnimation(player);
                return new AnimationTickResult(animation, progress, true);
            }

            float speed = getAnimationSpeed(player);
            if (speed <= 0)
                speed = 1.0f;
            float deltaTime = deltaTicks / 20.0f * speed;
            progress += deltaTime;

            setAnimationProgress(player, progress);
            setAnimationLastTick(player, ageInTicks);

            if (progress >= animation.length) {
                if (!animation.hold_on_last_frame && !animation.loop) {
                    boolean wasAnimationPose = animation.isPose;
                    if (animation.blendOut > 0) {
                        setCustomBlendOut(player, animation.blendOut);
                    }
                    finishAnimation(player);

                    if (!wasAnimationPose && hasPose(player)) {
                        String poseName = getPose(player);
                        PlayerAnimation poseAnim = getAnimation(poseName);
                        if (poseAnim != null) {
                            removeBlendOut(player);
                            removeBlendOutTick(player);
                            setCurrentAnimationName(player, poseName);

                            setFirstPersonAnimation(player, false);
                            int blendInDuration = resolveBlendIn(poseAnim);
                            setBlendIn(player, blendInDuration, blendInDuration);
                            removeAnimationProgress(player);
                            removeAnimationLastTick(player);
                            removeAnimationPlayedSounds(player);
                            removeAnimationPlayedEvents(player);
                            setActiveAnimation(player, poseAnim);
                            return new AnimationTickResult(poseAnim, 0f, true);
                        }
                    }
                    return new AnimationTickResult(animation, animation.length, true);
                } else if (animation.hold_on_last_frame) {
                    setAnimationProgress(player, animation.length);
                    progress = animation.length;
                } else if (animation.loop) {

                    float newProgress = progress % animation.length;

                    if (newProgress < lastProgress || newProgress < 0.001f) {
                        removeAnimationPlayedSounds(player);
                        removeAnimationPlayedEvents(player);
                        playedSounds = new ListTag();
                    }
                    removeAnimationProgress(player);
                    removeAnimationLastProgress(player);
                    setAnimationProgress(player, newProgress);
                    progress = newProgress;
                    lastProgress = 0f;
                }
            }
        }

        if (hasBlendIn(player)) {
            int currentIntTick = (int) Math.floor(ageInTicks);
            int lastDecrementTick = getBlendOutTick(player);
            if (currentIntTick != lastDecrementTick) {
                setBlendOutTick(player, currentIntTick);
                int remaining = getBlendIn(player);
                if (remaining <= 1) {
                    removeBlendIn(player);
                } else {
                    setBlendIn(player, remaining - 1);
                }
            }
        }

        if (!animation.soundEffects.isEmpty()) {
            Set<Float> playedSoundTimes = new HashSet<>();
            for (int i = 0; i < playedSounds.size(); i++) {
                playedSoundTimes.add(playedSounds.getFloat(i));
            }

            for (Map.Entry<Float, String> soundEntry : animation.soundEffects.entrySet()) {
                float soundTime = soundEntry.getKey();
                String soundId = soundEntry.getValue();

                if (playedSoundTimes.contains(soundTime)) {
                    continue;
                }

                boolean shouldPlay;
                if (lastProgress <= progress) {
                    shouldPlay = lastProgress <= soundTime && progress >= soundTime;
                } else {
                    shouldPlay = lastProgress <= soundTime || progress >= soundTime;
                }

                if (shouldPlay && player.level() instanceof ClientLevel) {
                    BuiltInRegistries.SOUND_EVENT
                            .getOptional(new ResourceLocation(soundId))
                            .ifPresent(soundEvent -> {
                                Minecraft.getInstance().getSoundManager().play(
                                        new net.jaams.weaponry.animation.AnimationSound(
                                                soundEvent,
                                                SoundSource.NEUTRAL, 1.0F, 1.0F,
                                                player));
                            });
                    playedSounds.add(FloatTag.valueOf(soundTime));
                }
            }

            setAnimationPlayedSounds(player, playedSounds);
            setAnimationLastProgress(player, progress);
        }

        if (!animation.events.isEmpty()) {
            ListTag playedEvents = getAnimationPlayedEvents(player);
            Set<Float> playedEventTimes = new HashSet<>();
            for (int i = 0; i < playedEvents.size(); i++) {
                playedEventTimes.add(playedEvents.getFloat(i));
            }
            boolean eventFired = false;
            for (Map.Entry<Float, List<AnimationAPI.AnimationEvent>> eventEntry : animation.events.entrySet()) {
                float eventTime = eventEntry.getKey();
                if (playedEventTimes.contains(eventTime))
                    continue;
                boolean shouldFire;
                if (lastProgress <= progress) {
                    shouldFire = lastProgress <= eventTime && progress >= eventTime;
                } else {
                    shouldFire = lastProgress <= eventTime || progress >= eventTime;
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
                setAnimationPlayedEvents(player, playedEvents);
            }
        }

        advanceCombinableAnimations(player, ageInTicks);

        return new AnimationTickResult(animation, progress, true);
    }

    public static float getEffectiveProgress(Player player) {
        return getAnimationProgress(player);
    }

    @Deprecated
    public static boolean hasActiveAnimationDataLegacy(Player player) {
        return hasActiveAnimationData(player);
    }

    public static void playMobAnimation(LivingEntity entity, String animationName) {
        playMobAnimation(entity, animationName, false, 1.0f, 0);
    }

    public static void playMobAnimation(LivingEntity entity, String animationName, boolean override, float speed,
            int duration) {
        if (entity == null || animationName == null || animationName.isEmpty())
            return;
        JaamsWeaponryMod.PACKET_HANDLER.send(
                PacketDistributor.TRACKING_ENTITY.with(() -> entity),
                new PlayMobAnimationMessage(entity.getId(), animationName, override, duration, speed));

        if (entity instanceof ServerPlayer player) {
            JaamsWeaponryMod.PACKET_HANDLER.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new PlayMobAnimationMessage(entity.getId(), animationName, override, duration, speed));
        }
    }

    public static void stopMobAnimation(LivingEntity entity) {
        if (entity == null)
            return;
        JaamsWeaponryMod.PACKET_HANDLER.send(
                PacketDistributor.TRACKING_ENTITY.with(() -> entity),
                new PlayMobAnimationMessage(entity.getId(), "", true));
        if (entity instanceof ServerPlayer player) {
            JaamsWeaponryMod.PACKET_HANDLER.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new PlayMobAnimationMessage(entity.getId(), "", true));
        }
    }

    public static void playMobAnimationFor(ServerPlayer receiver, LivingEntity target, String animationName) {
        playMobAnimationFor(receiver, target, animationName, false, 1.0f, 0);
    }

    public static void playMobAnimationFor(ServerPlayer receiver, LivingEntity target, String animationName,
            boolean override, float speed, int duration) {
        if (receiver == null || target == null || animationName == null || animationName.isEmpty())
            return;
        JaamsWeaponryMod.PACKET_HANDLER.send(
                PacketDistributor.PLAYER.with(() -> receiver),
                new PlayMobAnimationMessage(target.getId(), animationName, override, duration, speed));
    }

    public static float cameraShakeIntensity = 0f;
    public static int cameraShakeDuration = 0;

    public static void tickCameraShake() {
        if (cameraShakeDuration > 0) {
            cameraShakeDuration--;
            if (cameraShakeDuration <= 0) {
                cameraShakeIntensity = 0f;
            }
        }
    }

    private static final Map<String, net.minecraft.core.particles.SimpleParticleType> COMMON_PARTICLES = new HashMap<>();
    static {
        COMMON_PARTICLES.put("minecraft:crit", net.minecraft.core.particles.ParticleTypes.CRIT);
        COMMON_PARTICLES.put("minecraft:enchant", net.minecraft.core.particles.ParticleTypes.ENCHANT);
        COMMON_PARTICLES.put("minecraft:sweep_attack", net.minecraft.core.particles.ParticleTypes.SWEEP_ATTACK);
        COMMON_PARTICLES.put("minecraft:flame", net.minecraft.core.particles.ParticleTypes.FLAME);
        COMMON_PARTICLES.put("minecraft:smoke", net.minecraft.core.particles.ParticleTypes.SMOKE);
        COMMON_PARTICLES.put("minecraft:cloud", net.minecraft.core.particles.ParticleTypes.CLOUD);
        COMMON_PARTICLES.put("minecraft:spell", net.minecraft.core.particles.ParticleTypes.EFFECT);
        COMMON_PARTICLES.put("minecraft:damage_indicator", net.minecraft.core.particles.ParticleTypes.DAMAGE_INDICATOR);
        COMMON_PARTICLES.put("minecraft:heart", net.minecraft.core.particles.ParticleTypes.HEART);
        COMMON_PARTICLES.put("minecraft:angry", net.minecraft.core.particles.ParticleTypes.ANGRY_VILLAGER);
        COMMON_PARTICLES.put("minecraft:lava", net.minecraft.core.particles.ParticleTypes.LAVA);
        COMMON_PARTICLES.put("minecraft:rain", net.minecraft.core.particles.ParticleTypes.RAIN);
        COMMON_PARTICLES.put("minecraft:snowflake", net.minecraft.core.particles.ParticleTypes.SNOWFLAKE);
        COMMON_PARTICLES.put("minecraft:note", net.minecraft.core.particles.ParticleTypes.NOTE);
        COMMON_PARTICLES.put("minecraft:portal", net.minecraft.core.particles.ParticleTypes.PORTAL);
        COMMON_PARTICLES.put("minecraft:campfire_signal",
                net.minecraft.core.particles.ParticleTypes.CAMPFIRE_COSY_SMOKE);
    }

    public static void fireAnimationEvent(LivingEntity entity, AnimationAPI.AnimationEvent event) {
        if (entity == null || !(entity.level() instanceof ClientLevel))
            return;
        try {
            switch (event.type) {
                case "particle": {
                    String particleId = event.getString("particle", "");
                    if (!particleId.isEmpty()) {
                        int count = event.getInt("count", 1);
                        float speed = event.getFloat("speed", 0.0f);
                        net.minecraft.core.particles.ParticleOptions particle = COMMON_PARTICLES.get(particleId);
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

                    if (entity instanceof net.minecraft.world.entity.player.Player) {
                        cameraShakeIntensity = Math.max(cameraShakeIntensity, event.getFloat("intensity", 0.3f));
                        cameraShakeDuration = Math.max(cameraShakeDuration, event.getInt("duration", 5));
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
                                .ifPresent(soundEvent -> {
                                    Minecraft.getInstance().getSoundManager().play(
                                            new net.jaams.weaponry.animation.AnimationSound(
                                                    soundEvent,
                                                    SoundSource.NEUTRAL, volume, pitch,
                                                    entity));
                                });
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
