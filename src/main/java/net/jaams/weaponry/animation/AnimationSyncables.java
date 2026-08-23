package net.jaams.weaponry.animation;

import net.jaams.weaponry.JaamsWeaponryMod;
import net.jaams.weaponry.loader.NetworkSyncable;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Bridges the static animation registries in {@link AnimationAPI} into the
 * generic loader sync system.
 */
public final class AnimationSyncables {
    private AnimationSyncables() {
    }

    public static class Animations implements NetworkSyncable {
        @Override
        public String getSyncId() {
            return "animation";
        }

        @Override
        public Map<String, String> getSourcesSnapshot() {
            Map<String, String> out = new HashMap<>();
            for (Map.Entry<String, JsonObject> entry : AnimationAPI.animationSources.entrySet()) {
                try {
                    out.put(entry.getKey(), JaamsWeaponryMod.GSON.toJson(entry.getValue()));
                } catch (Exception ignored) {
                }
            }
            return out;
        }

        @Override
        public void applyNetworkSync(Map<String, String> sources) {
            AnimationAPI.applyAnimationSync(sources);
        }
    }

    public static class RandomGroups implements NetworkSyncable {
        @Override
        public String getSyncId() {
            return "animation_group";
        }

        @Override
        public Map<String, String> getSourcesSnapshot() {
            Map<String, String> out = new HashMap<>();
            for (Map.Entry<String, JsonObject> entry : AnimationAPI.randomGroupSources.entrySet()) {
                try {
                    out.put(entry.getKey(), JaamsWeaponryMod.GSON.toJson(entry.getValue()));
                } catch (Exception ignored) {
                }
            }
            return out;
        }

        @Override
        public void applyNetworkSync(Map<String, String> sources) {
            AnimationAPI.applyRandomGroupSync(sources);
        }
    }
}
