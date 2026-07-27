package net.jaams.weaponry.util;

import java.util.function.Consumer;

import net.jaams.weaponry.init.ModDataComponents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import org.jetbrains.annotations.Nullable;

/**
 * 1.21.1 data-component bridge for the mod's per-stack state.
 * <p>
 * Data is stored in two places that coexist:
 * <ul>
 *   <li>{@code jaams_weaponry:weapon_data} &ndash; the canonical 1.21.1
 *       {@link net.minecraft.world.item.component.DataComponentType}
 *       registered in {@link ModDataComponents}. This is the primary storage.</li>
 *   <li>{@code minecraft:custom_data} &ndash; the legacy vanilla
 *       {@link CustomData} blob kept for backward compatibility with items
 *       created by older versions of the mod.</li>
 * </ul>
 * <b>Reads</b> check {@code weapon_data} first, then fall back to
 * {@code custom_data}.  <b>Writes</b> set both components so that code
 * reading either path sees the latest values.
 * <p>
 * Returned tags are always copies &mdash; mutating a tag does NOT write
 * back; use {@link #set} or {@link #update} to persist changes.
 */
public final class ModComponents {

    private ModComponents() {
    }

    // ── Read ────────────────────────────────────────────────────────────

    /** Returns {@code true} when the stack carries mod data in either component. */
    public static boolean has(ItemStack stack) {
        // New component
        CompoundTag fresh = stack.get(ModDataComponents.WEAPONRY_DATA.get());
        if (fresh != null && !fresh.isEmpty()) {
            return true;
        }
        // Legacy fallback
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data != null && !data.isEmpty();
    }

    /**
     * Returns a <b>copy</b> of the mod data tag, or {@code null} when the
     * stack has no mod data in either component.
     */
    @Nullable
    public static CompoundTag get(ItemStack stack) {
        // Prefer the new component
        CompoundTag fresh = stack.get(ModDataComponents.WEAPONRY_DATA.get());
        if (fresh != null && !fresh.isEmpty()) {
            return fresh.copy();
        }
        // Fall back to legacy CustomData
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null || data.isEmpty()) {
            return null;
        }
        return data.copyTag();
    }

    /**
     * Returns a <b>copy</b> of the mod data tag, creating an empty one
     * when neither component is present. Commit with {@link #set}.
     */
    public static CompoundTag getOrCreate(ItemStack stack) {
        CompoundTag tag = get(stack);
        return tag != null ? tag : new CompoundTag();
    }

    // ── Write ───────────────────────────────────────────────────────────

    /**
     * Persists the given tag into both the new {@code weapon_data} component
     * and the legacy {@code custom_data} component.  A {@code null} or empty
     * tag removes both.
     */
    public static void set(ItemStack stack, @Nullable CompoundTag tag) {
        if (tag == null || tag.isEmpty()) {
            stack.remove(ModDataComponents.WEAPONRY_DATA.get());
            stack.remove(DataComponents.CUSTOM_DATA);
        } else {
            // Primary: the typed 1.21.1 component
            stack.set(ModDataComponents.WEAPONRY_DATA.get(), tag.copy());
            // Mirror: legacy CustomData for backward compat
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
    }

    /** Read-modify-write in one call. */
    public static void update(ItemStack stack, Consumer<CompoundTag> mutator) {
        CompoundTag tag = getOrCreate(stack);
        mutator.accept(tag);
        set(stack, tag);
    }

    // ── Element helpers ──────────────────────────────────────────────────

    /**
     * Returns a copy of the sub-tag stored under {@code key} (type 10 =
     * compound), creating and persisting an empty sub-tag if absent.
     */
    public static CompoundTag getOrCreateElement(ItemStack stack, String key) {
        CompoundTag tag = getOrCreate(stack);
        if (!tag.contains(key, 10)) {
            tag.put(key, new CompoundTag());
            set(stack, tag);
        }
        return tag.getCompound(key);
    }

    /** Writes a sub-tag under the given key. */
    public static void setElement(ItemStack stack, String key, CompoundTag element) {
        update(stack, tag -> tag.put(key, element));
    }
}
