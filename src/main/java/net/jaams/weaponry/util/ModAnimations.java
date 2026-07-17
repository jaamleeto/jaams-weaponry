package net.jaams.weaponry.util;

import java.util.WeakHashMap;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

/**
 * Per-player, time-smoothed blend values for the procedural gun aiming pose.
 *
 * <p>The aiming pose used to pop in and out in a single frame: it was applied
 * at full strength the moment every condition passed and released the instant
 * any of them failed (swinging, cooldown, out of ammo...). This holder instead
 * ramps the per-hand blend towards the active/inactive target exponentially,
 * keyed on game time so the transition looks the same at any frame rate. State
 * is discarded automatically when the player is garbage collected.
 */
public final class ModAnimations {

    private static final double FADE_RATE = 10.0D;
    private static final WeakHashMap<Player, Entry> CACHE = new WeakHashMap<>();

    private static final class Entry {
        float mainBlend;
        float offBlend;
        double lastTime;
    }

    private ModAnimations() {
    }

    /**
     * Advances the blend for one hand and returns the new value in [0, 1].
     *
     * @param player        the player being animated
     * @param isMainHand    whether this is the main-hand blend slot
     * @param active        whether the aiming pose should currently be shown
     * @param partialTick   frame partial tick, combined with the player tick
     *                      count to build a stable game-time source
     */
    public static float update(Player player, boolean isMainHand, boolean active, float partialTick) {
        double now = (double) player.tickCount + partialTick;
        Entry entry = CACHE.computeIfAbsent(player, p -> {
            Entry fresh = new Entry();
            fresh.lastTime = now;
            return fresh;
        });

        float current = isMainHand ? entry.mainBlend : entry.offBlend;
        double delta = Math.min(Math.max(now - entry.lastTime, 0.0D), 0.5D);
        entry.lastTime = now;

        float target = active ? 1.0F : 0.0F;
        float blended = current;
        if (current != target) {
            blended = target - (target - current) * (float) Math.exp(-delta * FADE_RATE);
            if (!active && blended < 0.01F) {
                blended = 0.0F;
            } else if (active && blended > 0.99F) {
                blended = 1.0F;
            }
            blended = Mth.clamp(blended, 0.0F, 1.0F);
        }

        if (isMainHand) {
            entry.mainBlend = blended;
        } else {
            entry.offBlend = blended;
        }
        return blended;
    }
}