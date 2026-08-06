package net.jaams.weaponry.util;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;

import net.jaams.weaponry.init.ModDataComponents;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
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

    private static final Logger LOGGER = LogManager.getLogger(ModComponents.class);

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

    // ── Data-component helpers (1.21.1) ───────────────────────────────────

    /**
     * Applies vanilla data components from a JSON map, exactly like the
     * {@code components} section of an {@code item_modifier} file: keys are
     * component IDs (e.g. {@code "minecraft:custom_name"}) and values are the
     * JSON representation of the component, decoded through its codec.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void applyComponents(ItemStack stack, Map<String, JsonElement> components) {
        if (stack == null || stack.isEmpty() || components == null || components.isEmpty())
            return;
        for (Map.Entry<String, JsonElement> entry : components.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null)
                continue;
            ResourceLocation compId = ResourceLocation.tryParse(entry.getKey());
            if (compId == null) {
                LOGGER.warn("Invalid component ID: {}", entry.getKey());
                continue;
            }
            DataComponentType type = BuiltInRegistries.DATA_COMPONENT_TYPE.get(compId);
            if (type == null) {
                LOGGER.warn("Unknown data component type: {}", compId);
                continue;
            }
            try {
                Codec codec = type.codec();
                DataResult result = codec.parse(JsonOps.INSTANCE, entry.getValue());
                if (result.isError()) {
                    LOGGER.error("Failed to parse data component {}: {}", compId,
                            result.error().orElse("unknown error"));
                    continue;
                }
                Object value = result.getOrThrow();
                Object current = stack.get(type);
                if (!Objects.equals(current, value)) {
                    stack.set(type, value);
                }
            } catch (Exception e) {
                LOGGER.error("Error applying data component {}", compId, e);
            }
        }
    }

    /**
     * Returns {@code true} when the stack carries the data component identified
     * by {@code componentId} (e.g. {@code "minecraft:unbreakable"}).
     */
    public static boolean hasComponent(ItemStack stack, String componentId) {
        if (stack == null || componentId == null)
            return false;
        ResourceLocation compId = ResourceLocation.tryParse(componentId);
        if (compId == null)
            return false;
        DataComponentType<?> compType = BuiltInRegistries.DATA_COMPONENT_TYPE.get(compId);
        return compType != null && stack.has(compType);
    }

    /**
     * Returns {@code true} when the stack's data component identified by
     * {@code componentId} equals the value parsed from {@code expectedJson}
     * through the component's codec.
     */
    public static boolean componentValueMatches(ItemStack stack, String componentId, JsonElement expectedJson) {
        if (stack == null || componentId == null || expectedJson == null)
            return false;
        ResourceLocation compId = ResourceLocation.tryParse(componentId);
        if (compId == null)
            return false;
        DataComponentType<?> compType = BuiltInRegistries.DATA_COMPONENT_TYPE.get(compId);
        if (compType == null)
            return false;
        try {
            DataResult<?> result = compType.codec().parse(JsonOps.INSTANCE, expectedJson);
            if (result.isError())
                return false;
            Object expected = result.getOrThrow();
            Object current = stack.get(compType);
            return Objects.equals(current, expected);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Parses an NBT string written either as SNBT (e.g. {@code {GunType:"PISTOL"}})
     * or as JSON (e.g. {@code {"GunType": "PISTOL"}}). JSON booleans and nested
     * objects/arrays are converted to their NBT equivalents, so both formats work
     * like the {@code nbt} section of an {@code item_modifier} file.
     */
    @Nullable
    public static CompoundTag parseNbtString(String nbtString) {
        if (nbtString == null || nbtString.isBlank())
            return null;
        try {
            return TagParser.parseTag(nbtString);
        } catch (Exception ignored) {
            // Not valid SNBT; try JSON below.
        }
        try {
            JsonElement element = JsonParser.parseString(nbtString);
            if (element.isJsonObject()) {
                return jsonToCompound(element.getAsJsonObject());
            }
            return null;
        } catch (Exception e) {
            LOGGER.warn("Failed to parse NBT string: {}", nbtString, e);
            return null;
        }
    }

    private static CompoundTag jsonToCompound(JsonObject obj) {
        CompoundTag tag = new CompoundTag();
        for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
            Tag value = jsonToTag(entry.getValue());
            if (value != null) {
                tag.put(entry.getKey(), value);
            }
        }
        return tag;
    }

    private static Tag jsonToTag(JsonElement json) {
        if (json == null || json.isJsonNull())
            return null;
        if (json.isJsonObject())
            return jsonToCompound(json.getAsJsonObject());
        if (json.isJsonArray()) {
            ListTag list = new ListTag();
            for (JsonElement element : json.getAsJsonArray()) {
                Tag item = jsonToTag(element);
                if (item != null) {
                    list.add(item);
                }
            }
            return list;
        }
        if (json.isJsonPrimitive()) {
            JsonPrimitive prim = json.getAsJsonPrimitive();
            if (prim.isBoolean())
                return ByteTag.valueOf(prim.getAsBoolean() ? (byte) 1 : (byte) 0);
            if (prim.isNumber()) {
                double d = prim.getAsDouble();
                if (d == Math.floor(d) && d >= Integer.MIN_VALUE && d <= Integer.MAX_VALUE)
                    return IntTag.valueOf((int) d);
                if (d == Math.floor(d) && d >= Long.MIN_VALUE && d <= Long.MAX_VALUE)
                    return LongTag.valueOf((long) d);
                return DoubleTag.valueOf(d);
            }
            if (prim.isString())
                return StringTag.valueOf(prim.getAsString());
        }
        return null;
    }
}
