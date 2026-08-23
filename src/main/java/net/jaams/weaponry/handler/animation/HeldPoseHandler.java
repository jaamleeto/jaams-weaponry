package net.jaams.weaponry.handler.animation;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.resources.ResourceLocation;

import net.jaams.weaponry.JaamsWeaponryMod;
import net.jaams.weaponry.animation.AnimationAPI.PlayerAnimation;
import net.jaams.weaponry.animation.AnimationHelper;
import net.jaams.weaponry.data.HeldPoseData;
import net.jaams.weaponry.loader.HeldPoseModifierLoader;
import net.jaams.weaponry.util.ModAnimations;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


@Mod.EventBusSubscriber(modid = JaamsWeaponryMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class HeldPoseHandler {
    private static final Logger LOGGER = LogManager.getLogger(HeldPoseHandler.class);

    
    private static final Map<UUID, String> lastApplied = new ConcurrentHashMap<>();

    
    private static final Map<UUID, String> lastHoldFingerprint = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null)
            return;


        for (Player player : mc.level.players()) {
            updatePoseForEntity(player);
        }

        if (mc.level != null) {
            for (Entity entity : mc.level.entitiesForRendering()) {
                if (entity instanceof LivingEntity living && !(entity instanceof Player)) {
                    updatePoseForEntity(living);
                }
            }
        }
    }

    private static void updatePoseForEntity(LivingEntity entity) {
        UUID uuid = entity.getUUID();
        String currentPose = lastApplied.get(uuid);

        HeldPoseData bestMatch = findBestMatch(entity);

        if (bestMatch != null) {
            String newPose = bestMatch.pose;

            if (!newPose.equals(currentPose)) {
                if (entity instanceof Player player) {
                    ModAnimations.clearAnimationState(player);
                    ModAnimations.setPose(player, newPose);
                    ModAnimations.setFirstPersonAnimation(player, bestMatch.first_person);
                } else {
                    applyMobPose(entity, bestMatch);
                }
                lastApplied.put(uuid, newPose);
            }

            if (entity instanceof Player player) {
                playHoldAnimationIfNew(player, bestMatch, uuid);
            }
        } else {
            if (currentPose != null && !currentPose.isEmpty()) {
                if (entity instanceof Player player) {
                    ModAnimations.finishAnimation(player);
                    ModAnimations.removePose(player);
                    ModAnimations.setFirstPersonAnimation(player, false);
                } else {
                    AnimationHelper.stopAnimation(entity);
                }
                lastApplied.remove(uuid);
            }
            lastHoldFingerprint.remove(uuid);
        }
    }

    private static void applyMobPose(LivingEntity mob, HeldPoseData data) {
        AnimationHelper.stopAnimation(mob);
        String pose = data.pose;
        if (pose != null && !pose.isEmpty() && ModAnimations.getAnimation(pose) != null) {
            AnimationHelper.startAnimation(mob, pose, false, 1.0f, 0);
            return;
        }
        String anim = data.animation;
        if (anim != null && !anim.isEmpty() && ModAnimations.getAnimation(anim) != null) {
            AnimationHelper.startAnimation(mob, anim, false, data.animation_speed, 0);
        }
    }

    
    
    

    
    private static void playHoldAnimationIfNew(Player player, HeldPoseData data, UUID uuid) {
        if (data.animation == null || data.animation.isEmpty())
            return;

        String fingerprint = buildFingerprint(data);
        String lastFp = lastHoldFingerprint.get(uuid);
        if (fingerprint.equals(lastFp))
            return;

        PlayerAnimation anim = ModAnimations.getAnimation(data.animation);
        if (anim == null)
            return;

        ModAnimations.setCurrentAnimationName(player, data.animation);
        ModAnimations.setActiveAnimation(player, anim);
        ModAnimations.setAnimationSpeed(player, data.animation_speed);
        ModAnimations.setFirstPersonAnimation(player, data.first_person);
        lastHoldFingerprint.put(uuid, fingerprint);
    }

    private static String buildFingerprint(HeldPoseData data) {
        StringBuilder sb = new StringBuilder();
        if (data.target != null) {
            for (String t : data.target) {
                sb.append(t).append(',');
            }
        }
        sb.append('|').append(data.pose).append('|').append(data.animation);
        return sb.toString();
    }

    
    
    

    private static HeldPoseData findBestMatch(LivingEntity entity) {
        String entityType = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()).toString();
        boolean isPlayer = entity instanceof Player;

        ItemStack mainhand = entity.getMainHandItem();
        ItemStack offhand = entity.getOffhandItem();

        for (HeldPoseData data : HeldPoseModifierLoader.INSTANCE.getAll()) {
            if (!data.appliesToEntity(entityType))
                continue;

            if (!mainhand.isEmpty() && matchesItem(data, mainhand) && data.appliesToHand("mainhand")) {
                if (isPlayer) {
                    if (HeldPoseModifierLoader.INSTANCE.evaluateConditions(data, mainhand, (Player) entity))
                        return data;
                } else {
                    return data;
                }
            }

            if (!offhand.isEmpty() && matchesItem(data, offhand) && data.appliesToHand("offhand")) {
                if (isPlayer) {
                    if (HeldPoseModifierLoader.INSTANCE.evaluateConditions(data, offhand, (Player) entity))
                        return data;
                } else {
                    return data;
                }
            }
        }

        return null;
    }

    private static boolean matchesItem(HeldPoseData data, ItemStack stack) {
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (itemId == null)
            return false;
        return HeldPoseModifierLoader.INSTANCE.matchesTarget(data.target, itemId);
    }
}
