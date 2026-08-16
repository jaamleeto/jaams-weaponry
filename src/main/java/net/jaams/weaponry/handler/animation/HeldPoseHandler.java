package net.jaams.weaponry.handler.animation;

import net.neoforged.neoforge.client.event.ClientTickEvent;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import net.jaams.weaponry.JaamsWeaponryMod;
import net.jaams.weaponry.animation.AnimationAPI.PlayerAnimation;
import net.jaams.weaponry.data.HeldPoseData;
import net.jaams.weaponry.loader.HeldPoseModifierLoader;
import net.jaams.weaponry.util.ModAnimations;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


@EventBusSubscriber(modid = JaamsWeaponryMod.MODID, value = Dist.CLIENT)
public class HeldPoseHandler {
    private static final Logger LOGGER = LogManager.getLogger(HeldPoseHandler.class);

    
    private static final Map<UUID, String> lastApplied = new ConcurrentHashMap<>();

    
    private static final Map<UUID, String> lastHoldFingerprint = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null)
            return;


        for (Player player : mc.level.players()) {
            updatePoseForPlayer(player);
        }
    }

    private static void updatePoseForPlayer(Player player) {
        UUID uuid = player.getUUID();
        String currentPose = lastApplied.get(uuid);

        HeldPoseData bestMatch = findBestMatch(player);

        if (bestMatch != null) {
            String newPose = bestMatch.pose;

            if (!newPose.equals(currentPose)) {
                ModAnimations.clearAnimationState(player);
                ModAnimations.setPose(player, newPose);
                ModAnimations.setFirstPersonAnimation(player, bestMatch.first_person);
                lastApplied.put(uuid, newPose);
            }

            playHoldAnimationIfNew(player, bestMatch, uuid);
        } else {
            if (currentPose != null && !currentPose.isEmpty()) {
                ModAnimations.finishAnimation(player);
                ModAnimations.removePose(player);
                ModAnimations.setFirstPersonAnimation(player, false);
                lastApplied.remove(uuid);
            }
            lastHoldFingerprint.remove(uuid);
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

    
    
    

    private static HeldPoseData findBestMatch(Player player) {
        String entityType = BuiltInRegistries.ENTITY_TYPE.getKey(player.getType()).toString();

        ItemStack mainhand = player.getMainHandItem();
        ItemStack offhand = player.getOffhandItem();

        for (HeldPoseData data : HeldPoseModifierLoader.INSTANCE.getAll()) {
            if (!data.appliesToEntity(entityType))
                continue;

            if (!mainhand.isEmpty() && matchesItem(data, mainhand) && data.appliesToHand("mainhand")) {
                if (HeldPoseModifierLoader.INSTANCE.evaluateConditions(data, mainhand, player))
                    return data;
            }

            if (!offhand.isEmpty() && matchesItem(data, offhand) && data.appliesToHand("offhand")) {
                if (HeldPoseModifierLoader.INSTANCE.evaluateConditions(data, offhand, player))
                    return data;
            }
        }

        return null;
    }

    private static boolean matchesItem(HeldPoseData data, ItemStack stack) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (itemId == null)
            return false;
        return HeldPoseModifierLoader.INSTANCE.matchesTarget(data.target, itemId);
    }
}
