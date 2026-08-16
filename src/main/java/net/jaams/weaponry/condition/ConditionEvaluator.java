package net.jaams.weaponry.condition;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.google.gson.JsonElement;

import net.jaams.weaponry.util.ModComponents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

public final class ConditionEvaluator {

    public static final Set<String> SUPPORTED_TYPES = Set.of(
            "enchantment", "nbt", "tag", "item", "mod", "rarity",
            "has_component", "component_value",
            "is_damageable", "is_damaged", "is_enchanted", "is_enchantable",
            "is_edible", "is_stackable", "mod_loaded", "mod_not_loaded",
            "has_nbt_key", "has_int_tag", "has_boolean_tag", "has_short_nbt",
            "has_long_nbt", "has_string_nbt");

    public static final Set<String> NBT_TYPES = Set.of("boolean", "int", "short", "long", "string");
    public static final Set<String> RARITIES = Set.of("common", "uncommon", "rare", "epic");

    private ConditionEvaluator() {
    }

    public static boolean evaluateAll(List<? extends ConditionSource> conditions, String conditionMode,
            ItemStack stack) {
        if (conditions == null || conditions.isEmpty()) {
            return true;
        }
        boolean isAndMode = "and".equalsIgnoreCase(conditionMode);
        for (ConditionSource cond : conditions) {
            boolean conditionMet = evaluateSingle(cond, stack);
            if (isAndMode && !conditionMet) {
                return false;
            }
            if (!isAndMode && conditionMet) {
                return true;
            }
        }
        return isAndMode;
    }

    public static boolean evaluateSingle(ConditionSource cond, ItemStack stack) {
        return evaluateSingleDetailed(cond, stack).pass();
    }

    public static ConditionResult evaluateSingleDetailed(ConditionSource cond, ItemStack stack) {
        if (cond == null) {
            return ConditionResult.fail("null condition");
        }
        if (stack == null) {
            return ConditionResult.fail("null stack");
        }
        String type = cond.type() == null ? "" : cond.type().trim().toLowerCase(Locale.ROOT);
        return switch (type) {
            case "enchantment" -> checkEnchantment(cond, stack);
            case "nbt" -> checkNbt(cond, stack);
            case "tag" -> checkTag(cond, stack);
            case "item" -> checkItem(cond, stack);
            case "mod" -> checkMod(cond, stack);
            case "rarity" -> checkRarity(cond, stack);
            case "has_component" -> checkHasComponent(cond, stack);
            case "component_value" -> checkComponentValue(cond, stack);
            case "is_damageable" -> ConditionResult.of(stack.isDamageableItem(), "item is not damageable");
            case "is_damaged" -> ConditionResult.of(stack.isDamaged(), "item is not damaged");
            case "is_enchanted" -> ConditionResult.of(stack.isEnchanted(), "item is not enchanted");
            case "is_enchantable" -> ConditionResult.of(stack.isEnchantable(), "item is not enchantable");
            case "is_edible" -> ConditionResult.of(stack.has(DataComponents.FOOD), "item is not edible");
            case "is_stackable" -> ConditionResult.of(stack.getMaxStackSize() > 1, "item is not stackable");
            case "mod_loaded" -> {
                if (cond.modId() == null || cond.modId().isEmpty()) {
                    yield ConditionResult.fail("mod_id not set");
                }
                yield ConditionResult.of(ModList.get().isLoaded(cond.modId()),
                        "mod '" + cond.modId() + "' is not loaded");
            }
            case "mod_not_loaded" -> {
                if (cond.modId() == null || cond.modId().isEmpty()) {
                    yield ConditionResult.fail("mod_id not set");
                }
                yield ConditionResult.of(!ModList.get().isLoaded(cond.modId()),
                        "mod '" + cond.modId() + "' is loaded");
            }
            case "has_nbt_key" -> checkHasNbtKey(cond, stack);
            case "has_int_tag" -> checkHasNbtTyped(cond, stack, Tag.TAG_INT, "int", "an int");
            case "has_boolean_tag" -> checkHasNbtTyped(cond, stack, Tag.TAG_BYTE, "boolean", "a boolean");
            case "has_short_nbt" -> checkHasNbtTyped(cond, stack, Tag.TAG_SHORT, "short", "a short");
            case "has_long_nbt" -> checkHasNbtTyped(cond, stack, Tag.TAG_LONG, "long", "a long");
            case "has_string_nbt" -> checkHasNbtTyped(cond, stack, Tag.TAG_STRING, "string", "a string");
            default -> ConditionResult.fail("unknown condition type '" + type + "'");
        };
    }

    public static List<String> validateConditions(List<? extends ConditionSource> conditions) {
        List<String> warnings = new ArrayList<>();
        if (conditions == null) {
            return warnings;
        }
        int idx = 0;
        for (ConditionSource c : conditions) {
            idx++;
            String label = "condition[" + idx + "]";
            if (c == null) {
                warnings.add(label + ": null entry");
                continue;
            }
            if (c.type() == null || c.type().trim().isEmpty()) {
                warnings.add(label + ": missing 'type'");
                continue;
            }
            String type = c.type().trim().toLowerCase(Locale.ROOT);
            if (!SUPPORTED_TYPES.contains(type)) {
                warnings.add(label + ": unknown type '" + c.type() + "'");
                continue;
            }
            switch (type) {
                case "nbt" -> {
                    String nbtType = c.nbtType();
                    if (nbtType == null || nbtType.trim().isEmpty()) {
                        warnings.add(label + ": nbt condition missing 'nbt_type' (or legacy 'nbt_key')");
                    } else if (!NBT_TYPES.contains(nbtType.trim().toLowerCase(Locale.ROOT))) {
                        warnings.add(label + ": invalid nbt_type '" + nbtType
                                + "' (expected boolean, int, short, long or string)");
                    }
                    if (c.key() == null || c.key().isEmpty()) {
                        warnings.add(label + ": nbt condition missing 'key'");
                    }
                }
                case "has_nbt_key", "has_int_tag", "has_boolean_tag", "has_short_nbt", "has_long_nbt",
                        "has_string_nbt" -> {
                    if (c.key() == null || c.key().isEmpty()) {
                        warnings.add(label + ": '" + type + "' condition missing 'key'");
                    }
                }
                case "enchantment" -> {
                    if (c.enchantment() == null || ResourceLocation.tryParse(c.enchantment()) == null) {
                        warnings.add(label + ": invalid enchantment '" + c.enchantment() + "'");
                    }
                }
                case "tag" -> {
                    String tagStr = c.tag();
                    if (tagStr == null || tagStr.isEmpty()) {
                        warnings.add(label + ": tag condition missing 'tag'");
                    } else {
                        String cleaned = tagStr.startsWith("!") ? tagStr.substring(1) : tagStr;
                        if (cleaned.startsWith("#")) {
                            cleaned = cleaned.substring(1);
                        }
                        if (ResourceLocation.tryParse(cleaned) == null) {
                            warnings.add(label + ": invalid tag '" + tagStr + "'");
                        }
                    }
                }
                case "item" -> {
                    if (c.item() == null || ResourceLocation.tryParse(c.item()) == null) {
                        warnings.add(label + ": invalid item '" + c.item() + "'");
                    }
                }
                case "mod", "mod_loaded", "mod_not_loaded" -> {
                    if (c.modId() == null || c.modId().isEmpty()) {
                        warnings.add(label + ": '" + type + "' condition missing 'mod_id'");
                    }
                }
                case "rarity" -> {
                    if (c.rarity() == null || !RARITIES.contains(c.rarity().trim().toLowerCase(Locale.ROOT))) {
                        warnings.add(label + ": invalid rarity '" + c.rarity() + "'");
                    }
                }
                case "has_component" -> {
                    if (c.component() == null || ResourceLocation.tryParse(c.component()) == null) {
                        warnings.add(label + ": invalid component '" + c.component() + "'");
                    }
                }
                case "component_value" -> {
                    if (c.component() == null || ResourceLocation.tryParse(c.component()) == null
                            || c.componentValue() == null) {
                        warnings.add(label + ": component_value needs valid 'component' and 'component_value'");
                    }
                }
                default -> {
                }
            }
        }
        return warnings;
    }

    private static ConditionResult checkEnchantment(ConditionSource cond, ItemStack stack) {
        if (cond.enchantment() == null) {
            return ConditionResult.fail("enchantment not set");
        }
        ResourceLocation enchId = ResourceLocation.tryParse(cond.enchantment());
        if (enchId == null) {
            return ConditionResult.fail("invalid enchantment id '" + cond.enchantment() + "'");
        }
        int level = 0;
        for (var e : net.minecraft.world.item.enchantment.EnchantmentHelper
                .getEnchantmentsForCrafting(stack).entrySet()) {
            if (e.getKey().is(enchId)) {
                level = e.getIntValue();
                break;
            }
        }
        int required = Math.max(1, cond.level());
        if (level <= 0) {
            return ConditionResult.fail("item is not enchanted with '" + cond.enchantment() + "'");
        }
        if (level < required) {
            return ConditionResult.fail("enchantment '" + cond.enchantment() + "' level " + level + " < required "
                    + required);
        }
        return ConditionResult.PASS;
    }

    private static ConditionResult checkNbt(ConditionSource cond, ItemStack stack) {
        String key = cond.key();
        String type = cond.nbtType() == null ? null : cond.nbtType().trim().toLowerCase(Locale.ROOT);
        if (key == null || key.isEmpty()) {
            return ConditionResult.fail("nbt condition missing 'key'");
        }
        if (type == null || type.isEmpty()) {
            return ConditionResult.fail("nbt condition missing 'nbt_type' (or legacy 'nbt_key')");
        }
        if (!NBT_TYPES.contains(type)) {
            return ConditionResult.fail("invalid nbt_type '" + cond.nbtType() + "'");
        }
        if (!ModComponents.has(stack)) {
            return ConditionResult.fail("item has no mod data");
        }
        CompoundTag tag = ModComponents.get(stack);
        if (tag == null) {
            return ConditionResult.fail("item has no mod data");
        }
        if (!tag.contains(key)) {
            return ConditionResult.fail("key '" + key + "' not found in mod data");
        }
        return switch (type) {
            case "boolean" -> {
                if (!tag.contains(key, Tag.TAG_BYTE)) {
                    yield ConditionResult.fail("key '" + key + "' is not a boolean");
                }
                boolean actual = tag.getBoolean(key);
                yield ConditionResult.of(actual == cond.nbtBooleanValue(),
                        "key '" + key + "' = " + actual + ", expected " + cond.nbtBooleanValue());
            }
            case "int" -> {
                if (!tag.contains(key, Tag.TAG_INT)) {
                    yield ConditionResult.fail("key '" + key + "' is not an int");
                }
                int actual = tag.getInt(key);
                yield ConditionResult.of(actual == cond.nbtIntValue(),
                        "key '" + key + "' = " + actual + ", expected " + cond.nbtIntValue());
            }
            case "short" -> {
                if (!tag.contains(key, Tag.TAG_SHORT)) {
                    yield ConditionResult.fail("key '" + key + "' is not a short");
                }
                short actual = tag.getShort(key);
                yield ConditionResult.of(actual == cond.nbtShortValue(),
                        "key '" + key + "' = " + actual + ", expected " + cond.nbtShortValue());
            }
            case "long" -> {
                if (!tag.contains(key, Tag.TAG_LONG)) {
                    yield ConditionResult.fail("key '" + key + "' is not a long");
                }
                long actual = tag.getLong(key);
                yield ConditionResult.of(actual == cond.nbtLongValue(),
                        "key '" + key + "' = " + actual + ", expected " + cond.nbtLongValue());
            }
            case "string" -> {
                if (!tag.contains(key, Tag.TAG_STRING)) {
                    yield ConditionResult.fail("key '" + key + "' is not a string");
                }
                String expected = cond.nbtStringValue();
                String actual = tag.getString(key);
                yield ConditionResult.of(expected != null && expected.equals(actual),
                        "key '" + key + "' = '" + actual + "', expected '" + expected + "'");
            }
            default -> ConditionResult.fail("unsupported nbt_type '" + type + "'");
        };
    }

    private static ConditionResult checkTag(ConditionSource cond, ItemStack stack) {
        if (cond.tag() == null || cond.tag().isEmpty()) {
            return ConditionResult.fail("tag not set");
        }
        boolean negate = cond.tag().startsWith("!");
        String tagStr = negate ? cond.tag().substring(1) : cond.tag();
        if (tagStr.startsWith("#")) {
            tagStr = tagStr.substring(1);
        }
        ResourceLocation tagId = ResourceLocation.tryParse(tagStr);
        if (tagId == null) {
            return ConditionResult.fail("invalid tag id '" + cond.tag() + "'");
        }
        boolean inTag = stack.is(TagKey.create(Registries.ITEM, tagId));
        if (negate != inTag) {
            return ConditionResult.PASS;
        }
        return ConditionResult.fail((negate ? "item is in tag '" : "item is not in tag '") + tagStr + "'");
    }

    private static ConditionResult checkItem(ConditionSource cond, ItemStack stack) {
        if (cond.item() == null) {
            return ConditionResult.fail("item not set");
        }
        ResourceLocation itemId = ResourceLocation.tryParse(cond.item());
        if (itemId == null) {
            return ConditionResult.fail("invalid item id '" + cond.item() + "'");
        }
        ResourceLocation stackId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (stackId == null || !stackId.equals(itemId)) {
            return ConditionResult.fail("item is '" + (stackId != null ? stackId : "?") + "', expected '" + itemId + "'");
        }
        return ConditionResult.PASS;
    }

    private static ConditionResult checkMod(ConditionSource cond, ItemStack stack) {
        if (cond.modId() == null) {
            return ConditionResult.fail("mod_id not set");
        }
        ResourceLocation stackId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (stackId == null || !cond.modId().equalsIgnoreCase(stackId.getNamespace())) {
            return ConditionResult.fail("item namespace '" + (stackId != null ? stackId.getNamespace() : "?")
                    + "' != mod_id '" + cond.modId() + "'");
        }
        return ConditionResult.PASS;
    }

    private static ConditionResult checkRarity(ConditionSource cond, ItemStack stack) {
        if (cond.rarity() == null) {
            return ConditionResult.fail("rarity not set");
        }
        String actual = stack.getRarity().name();
        if (!actual.equalsIgnoreCase(cond.rarity())) {
            return ConditionResult.fail("rarity is '" + actual + "', expected '" + cond.rarity() + "'");
        }
        return ConditionResult.PASS;
    }

    private static ConditionResult checkHasComponent(ConditionSource cond, ItemStack stack) {
        if (cond.component() == null) {
            return ConditionResult.fail("component not set");
        }
        if (!ModComponents.hasComponent(stack, cond.component())) {
            return ConditionResult.fail("item does not have component '" + cond.component() + "'");
        }
        return ConditionResult.PASS;
    }

    private static ConditionResult checkComponentValue(ConditionSource cond, ItemStack stack) {
        if (cond.component() == null || cond.componentValue() == null) {
            return ConditionResult.fail("component_value needs 'component' and 'component_value'");
        }
        if (!ModComponents.componentValueMatches(stack, cond.component(), cond.componentValue())) {
            return ConditionResult.fail("component '" + cond.component() + "' value does not match");
        }
        return ConditionResult.PASS;
    }

    private static ConditionResult checkHasNbtKey(ConditionSource cond, ItemStack stack) {
        String key = cond.key();
        if (key == null || key.isEmpty()) {
            return ConditionResult.fail("key not set");
        }
        CompoundTag tag = ModComponents.get(stack);
        if (tag == null) {
            return ConditionResult.fail("item has no mod data");
        }
        if (!tag.contains(key)) {
            return ConditionResult.fail("key '" + key + "' not found in mod data");
        }
        return ConditionResult.PASS;
    }

    private static ConditionResult checkHasNbtTyped(ConditionSource cond, ItemStack stack, int tagType, String type,
            String typeDesc) {
        String key = cond.key();
        if (key == null || key.isEmpty()) {
            return ConditionResult.fail("key not set");
        }
        CompoundTag tag = ModComponents.get(stack);
        if (tag == null) {
            return ConditionResult.fail("item has no mod data");
        }
        if (!tag.contains(key, tagType)) {
            return ConditionResult.fail("key '" + key + "' is not " + typeDesc);
        }
        boolean matches = switch (type) {
            case "boolean" -> tag.getBoolean(key) == cond.nbtBooleanValue();
            case "int" -> tag.getInt(key) == cond.nbtIntValue();
            case "short" -> tag.getShort(key) == cond.nbtShortValue();
            case "long" -> tag.getLong(key) == cond.nbtLongValue();
            case "string" -> cond.nbtStringValue() != null && cond.nbtStringValue().equals(tag.getString(key));
            default -> false;
        };
        if (!matches) {
            String expected = switch (type) {
                case "boolean" -> String.valueOf(cond.nbtBooleanValue());
                case "int" -> String.valueOf(cond.nbtIntValue());
                case "short" -> String.valueOf(cond.nbtShortValue());
                case "long" -> String.valueOf(cond.nbtLongValue());
                case "string" -> cond.nbtStringValue() != null ? "'" + cond.nbtStringValue() + "'" : "null";
                default -> "?";
            };
            String actual = switch (type) {
                case "boolean" -> String.valueOf(tag.getBoolean(key));
                case "int" -> String.valueOf(tag.getInt(key));
                case "short" -> String.valueOf(tag.getShort(key));
                case "long" -> String.valueOf(tag.getLong(key));
                case "string" -> "'" + tag.getString(key) + "'";
                default -> "?";
            };
            return ConditionResult.fail("key '" + key + "' = " + actual + ", expected " + expected);
        }
        return ConditionResult.PASS;
    }
}
