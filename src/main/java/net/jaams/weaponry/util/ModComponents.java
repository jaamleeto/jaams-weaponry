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

public final class ModComponents {

    private static final Logger LOGGER = LogManager.getLogger(ModComponents.class);

    private ModComponents() {
    }

    public static boolean has(ItemStack stack) {
        CompoundTag fresh = stack.get(ModDataComponents.MOD_DATA.get());
        if (fresh != null && !fresh.isEmpty()) {
            return true;
        }
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data != null && !data.isEmpty();
    }

    @Nullable
    public static CompoundTag get(ItemStack stack) {
        CompoundTag fresh = stack.get(ModDataComponents.MOD_DATA.get());
        if (fresh != null && !fresh.isEmpty()) {
            return fresh.copy();
        }
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null || data.isEmpty()) {
            return null;
        }
        return data.copyTag();
    }

    public static CompoundTag getOrCreate(ItemStack stack) {
        CompoundTag tag = get(stack);
        return tag != null ? tag : new CompoundTag();
    }

    public static void set(ItemStack stack, @Nullable CompoundTag tag) {
        if (tag == null || tag.isEmpty()) {
            stack.remove(ModDataComponents.MOD_DATA.get());
            stack.remove(DataComponents.CUSTOM_DATA);
        } else {
            stack.set(ModDataComponents.MOD_DATA.get(), tag.copy());
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
    }

    public static void update(ItemStack stack, Consumer<CompoundTag> mutator) {
        CompoundTag tag = getOrCreate(stack);
        mutator.accept(tag);
        set(stack, tag);
    }

    public static CompoundTag getOrCreateElement(ItemStack stack, String key) {
        CompoundTag tag = getOrCreate(stack);
        if (!tag.contains(key, 10)) {
            tag.put(key, new CompoundTag());
            set(stack, tag);
        }
        return tag.getCompound(key);
    }

    public static void setElement(ItemStack stack, String key, CompoundTag element) {
        update(stack, tag -> tag.put(key, element));
    }

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

    public static boolean hasComponent(ItemStack stack, String componentId) {
        if (stack == null || componentId == null)
            return false;
        ResourceLocation compId = ResourceLocation.tryParse(componentId);
        if (compId == null)
            return false;
        DataComponentType<?> compType = BuiltInRegistries.DATA_COMPONENT_TYPE.get(compId);
        return compType != null && stack.has(compType);
    }

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

    @Nullable
    public static CompoundTag jsonToNbt(JsonElement json) {
        if (json == null || json.isJsonNull())
            return null;
        if (json.isJsonObject())
            return jsonToCompound(json.getAsJsonObject());
        if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
            return parseNbtString(json.getAsString());
        }
        return null;
    }

    public static void applyJsonData(ItemStack stack, @Nullable JsonElement nbt,
            @Nullable Map<String, JsonElement> components) {
        if (stack == null || stack.isEmpty())
            return;
        if (nbt != null) {
            CompoundTag tag = jsonToNbt(nbt);
            if (tag != null && !tag.isEmpty()) {
                set(stack, tag);
            }
        }
        if (components != null && !components.isEmpty()) {
            applyComponents(stack, components);
        }
    }

    @Nullable
    public static CompoundTag parseNbtString(String nbtString) {
        if (nbtString == null || nbtString.isBlank())
            return null;
        try {
            return TagParser.parseTag(nbtString);
        } catch (Exception ignored) {
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
