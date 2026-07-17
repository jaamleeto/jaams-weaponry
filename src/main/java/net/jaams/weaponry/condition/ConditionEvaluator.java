package net.jaams.weaponry.condition;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.google.gson.JsonElement;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

public final class ConditionEvaluator {

    public static final Set<String> SUPPORTED_TYPES = Set.of(
            "enchantment", "nbt", "tag", "item", "mod", "rarity",
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
        ConditionResult result;
        switch (type) {
            case "enchantment":
                result = checkEnchantment(cond, stack);
                break;
            case "nbt":
                result = checkNbt(cond, stack);
                break;
            case "tag":
                result = checkTag(cond, stack);
                break;
            case "item":
                result = checkItem(cond, stack);
                break;
            case "mod":
                result = checkMod(cond, stack);
                break;
            case "rarity":
                result = checkRarity(cond, stack);
                break;
            case "is_damageable":
                result = ConditionResult.of(stack.isDamageableItem(), "item is not damageable");
                break;
            case "is_damaged":
                result = ConditionResult.of(stack.isDamaged(), "item is not damaged");
                break;
            case "is_enchanted":
                result = ConditionResult.of(stack.isEnchanted(), "item is not enchanted");
                break;
            case "is_enchantable":
                result = ConditionResult.of(stack.isEnchantable(), "item is not enchantable");
                break;
            case "is_edible":
                result = ConditionResult.of(stack.isEdible(), "item is not edible");
                break;
            case "is_stackable":
                result = ConditionResult.of(stack.getMaxStackSize() > 1, "item is not stackable");
                break;
            case "mod_loaded":
                if (cond.modId() == null || cond.modId().isEmpty()) {
                    result = ConditionResult.fail("mod_id not set");
                } else {
                    result = ConditionResult.of(ModList.get().isLoaded(cond.modId()),
                            "mod '" + cond.modId() + "' is not loaded");
                }
                break;
            case "mod_not_loaded":
                if (cond.modId() == null || cond.modId().isEmpty()) {
                    result = ConditionResult.fail("mod_id not set");
                } else {
                    result = ConditionResult.of(!ModList.get().isLoaded(cond.modId()),
                            "mod '" + cond.modId() + "' is loaded");
                }
                break;
            case "has_nbt_key":
                result = checkHasNbtKey(cond, stack);
                break;
            case "has_int_tag":
                result = checkHasNbtTyped(cond, stack, Tag.TAG_INT, "int", "an int");
                break;
            case "has_boolean_tag":
                result = checkHasNbtTyped(cond, stack, Tag.TAG_BYTE, "boolean", "a boolean");
                break;
            case "has_short_nbt":
                result = checkHasNbtTyped(cond, stack, Tag.TAG_SHORT, "short", "a short");
                break;
            case "has_long_nbt":
                result = checkHasNbtTyped(cond, stack, Tag.TAG_LONG, "long", "a long");
                break;
            case "has_string_nbt":
                result = checkHasNbtTyped(cond, stack, Tag.TAG_STRING, "string", "a string");
                break;
            default:
                result = ConditionResult.fail("unknown condition type '" + type + "'");
                break;
        }
        return result;
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
                case "nbt": {
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
                    break;
                }
                case "has_nbt_key": case "has_int_tag": case "has_boolean_tag": case "has_short_nbt": case "has_long_nbt":
                case "has_string_nbt": {
                    if (c.key() == null || c.key().isEmpty()) {
                        warnings.add(label + ": '" + type + "' condition missing 'key'");
                    }
                    break;
                }
                case "enchantment": {
                    if (c.enchantment() == null || ResourceLocation.tryParse(c.enchantment()) == null) {
                        warnings.add(label + ": invalid enchantment '" + c.enchantment() + "'");
                    }
                    break;
                }
                case "tag": {
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
                    break;
                }
                case "item": {
                    if (c.item() == null || ResourceLocation.tryParse(c.item()) == null) {
                        warnings.add(label + ": invalid item '" + c.item() + "'");
                    }
                    break;
                }
                case "mod": case "mod_loaded": case "mod_not_loaded": {
                    if (c.modId() == null || c.modId().isEmpty()) {
                        warnings.add(label + ": '" + type + "' condition missing 'mod_id'");
                    }
                    break;
                }
                case "rarity": {
                    if (c.rarity() == null || !RARITIES.contains(c.rarity().trim().toLowerCase(Locale.ROOT))) {
                        warnings.add(label + ": invalid rarity '" + c.rarity() + "'");
                    }
                    break;
                }
                default: {
                    break;
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
        Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(enchId);
        if (enchantment == null) {
            return ConditionResult.fail("unknown enchantment '" + cond.enchantment() + "'");
        }
        int level = EnchantmentHelper.getTagEnchantmentLevel(enchantment, stack);
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
        if (!stack.hasTag()) {
            return ConditionResult.fail("item has no tag data");
        }
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return ConditionResult.fail("item has no tag data");
        }
        if (!tag.contains(key)) {
            return ConditionResult.fail("key '" + key + "' not found in item tag");
        }
        ConditionResult result;
        switch (type) {
            case "boolean": {
                if (!tag.contains(key, Tag.TAG_BYTE)) {
                    result = ConditionResult.fail("key '" + key + "' is not a boolean");
                } else {
                    boolean actual = tag.getBoolean(key);
                    result = ConditionResult.of(actual == cond.nbtBooleanValue(),
                            "key '" + key + "' = " + actual + ", expected " + cond.nbtBooleanValue());
                }
                break;
            }
            case "int": {
                if (!tag.contains(key, Tag.TAG_INT)) {
                    result = ConditionResult.fail("key '" + key + "' is not an int");
                } else {
                    int actual = tag.getInt(key);
                    result = ConditionResult.of(actual == cond.nbtIntValue(),
                            "key '" + key + "' = " + actual + ", expected " + cond.nbtIntValue());
                }
                break;
            }
            case "short": {
                if (!tag.contains(key, Tag.TAG_SHORT)) {
                    result = ConditionResult.fail("key '" + key + "' is not a short");
                } else {
                    short actual = tag.getShort(key);
                    result = ConditionResult.of(actual == cond.nbtShortValue(),
                            "key '" + key + "' = " + actual + ", expected " + cond.nbtShortValue());
                }
                break;
            }
            case "long": {
                if (!tag.contains(key, Tag.TAG_LONG)) {
                    result = ConditionResult.fail("key '" + key + "' is not a long");
                } else {
                    long actual = tag.getLong(key);
                    result = ConditionResult.of(actual == cond.nbtLongValue(),
                            "key '" + key + "' = " + actual + ", expected " + cond.nbtLongValue());
                }
                break;
            }
            case "string": {
                if (!tag.contains(key, Tag.TAG_STRING)) {
                    result = ConditionResult.fail("key '" + key + "' is not a string");
                } else {
                    String expected = cond.nbtStringValue();
                    String actual = tag.getString(key);
                    result = ConditionResult.of(expected != null && expected.equals(actual),
                            "key '" + key + "' = '" + actual + "', expected '" + expected + "'");
                }
                break;
            }
            default:
                result = ConditionResult.fail("unsupported nbt_type '" + type + "'");
                break;
        }
        return result;
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
        ResourceLocation stackId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (stackId == null || !stackId.equals(itemId)) {
            return ConditionResult.fail("item is '" + (stackId != null ? stackId : "?") + "', expected '" + itemId + "'");
        }
        return ConditionResult.PASS;
    }

    private static ConditionResult checkMod(ConditionSource cond, ItemStack stack) {
        if (cond.modId() == null) {
            return ConditionResult.fail("mod_id not set");
        }
        ResourceLocation stackId = ForgeRegistries.ITEMS.getKey(stack.getItem());
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

    private static ConditionResult checkHasNbtKey(ConditionSource cond, ItemStack stack) {
        String key = cond.key();
        if (key == null || key.isEmpty()) {
            return ConditionResult.fail("key not set");
        }
        if (!stack.hasTag()) {
            return ConditionResult.fail("item has no tag data");
        }
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return ConditionResult.fail("item has no tag data");
        }
        if (!tag.contains(key)) {
            return ConditionResult.fail("key '" + key + "' not found in item tag");
        }
        return ConditionResult.PASS;
    }

    private static ConditionResult checkHasNbtTyped(ConditionSource cond, ItemStack stack, int tagType, String type,
            String typeDesc) {
        String key = cond.key();
        if (key == null || key.isEmpty()) {
            return ConditionResult.fail("key not set");
        }
        if (!stack.hasTag()) {
            return ConditionResult.fail("item has no tag data");
        }
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return ConditionResult.fail("item has no tag data");
        }
        if (!tag.contains(key, tagType)) {
            return ConditionResult.fail("key '" + key + "' is not " + typeDesc);
        }
        boolean matches;
        switch (type) {
            case "boolean":
                matches = tag.getBoolean(key) == cond.nbtBooleanValue();
                break;
            case "int":
                matches = tag.getInt(key) == cond.nbtIntValue();
                break;
            case "short":
                matches = tag.getShort(key) == cond.nbtShortValue();
                break;
            case "long":
                matches = tag.getLong(key) == cond.nbtLongValue();
                break;
            case "string":
                matches = cond.nbtStringValue() != null && cond.nbtStringValue().equals(tag.getString(key));
                break;
            default:
                matches = false;
                break;
        }
        if (!matches) {
            String expected;
            switch (type) {
                case "boolean":
                    expected = String.valueOf(cond.nbtBooleanValue());
                    break;
                case "int":
                    expected = String.valueOf(cond.nbtIntValue());
                    break;
                case "short":
                    expected = String.valueOf(cond.nbtShortValue());
                    break;
                case "long":
                    expected = String.valueOf(cond.nbtLongValue());
                    break;
                case "string":
                    expected = cond.nbtStringValue() != null ? "'" + cond.nbtStringValue() + "'" : "null";
                    break;
                default:
                    expected = "?";
                    break;
            }
            String actual;
            switch (type) {
                case "boolean":
                    actual = String.valueOf(tag.getBoolean(key));
                    break;
                case "int":
                    actual = String.valueOf(tag.getInt(key));
                    break;
                case "short":
                    actual = String.valueOf(tag.getShort(key));
                    break;
                case "long":
                    actual = String.valueOf(tag.getLong(key));
                    break;
                case "string":
                    actual = "'" + tag.getString(key) + "'";
                    break;
                default:
                    actual = "?";
                    break;
            }
            return ConditionResult.fail("key '" + key + "' = " + actual + ", expected " + expected);
        }
        return ConditionResult.PASS;
    }
}