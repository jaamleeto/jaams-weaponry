package net.jaams.weaponry.handler.event;

import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.core.Holder;
import net.jaams.weaponry.util.ModComponents;

import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.ModList;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;

import net.jaams.weaponry.loader.ItemModifierLoader;
import net.jaams.weaponry.data.ItemModifierData;

import java.util.UUID;
import java.util.Objects;
import java.util.Map;
import java.util.List;

import java.nio.charset.StandardCharsets;

import com.google.gson.JsonPrimitive;
import com.google.gson.JsonElement;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class ItemModifierHandler {

    @SubscribeEvent
    public static void onItemDataAttributeModifier(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty())
            return;
        List<ItemModifierData> datas = ItemModifierLoader.INSTANCE.getForItem(stack.getItem());
        if (datas.isEmpty())
            return;
        for (ItemModifierData data : datas) {
            if (!checkConditions(stack, data))
                continue;
            applyNbt(stack, data);
            ModComponents.applyComponents(stack, data.components);
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if (data.appliesToSlot(slot)) {
                    applyAttributes(event, data, slot);
                }
            }
        }
    }

    private static boolean checkConditions(ItemStack stack, ItemModifierData data) {
        if (data.conditions.isEmpty())
            return true;
        boolean isOrMode = "or".equalsIgnoreCase(data.condition_mode);
        CompoundTag tag = ModComponents.get(stack);
        for (ItemModifierData.Condition cond : data.conditions) {
            if (cond.type == null)
                continue;
            boolean conditionMet = evaluateSingleCondition(stack, tag, cond);
            if (isOrMode && conditionMet) {
                return true;
            }
            if (!isOrMode && !conditionMet) {
                return false;
            }
        }
        return isOrMode ? false : true;
    }

    private static boolean evaluateSingleCondition(ItemStack stack, CompoundTag tag, ItemModifierData.Condition cond) {
        String type = cond.type.toLowerCase().trim();
        switch (type) {
            case "is_damageable":
            case "s_damageable":
                return stack.isDamageableItem();
            case "is_damaged":
                return stack.isDamaged();
            case "is_enchanted":
                return stack.isEnchanted();
            case "is_enchantable":
                return stack.isEnchantable();
            case "is_edible":
                return stack.has(net.minecraft.core.component.DataComponents.FOOD);
            case "is_stackable":
                return stack.getMaxStackSize() > 1;
            case "mod_loaded":
                return cond.mod_id != null && ModList.get().isLoaded(cond.mod_id);
            case "mod_not_loaded":
                return cond.mod_id != null && !ModList.get().isLoaded(cond.mod_id);
            case "mod_id":
                if (cond.mod_id != null) {
                    ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
                    return cond.mod_id.equals(itemId.getNamespace());
                }
                return false;
            case "is_item":
                if (cond.item != null) {
                    ResourceLocation wanted = ResourceLocation.tryParse(cond.item);
                    ResourceLocation current = BuiltInRegistries.ITEM.getKey(stack.getItem());
                    return current.equals(wanted);
                }
                return true;
            case "is_tagged":
                if (cond.tag != null) {
                    boolean negate = cond.tag.startsWith("!");
                    String tagStr = negate ? cond.tag.substring(1) : cond.tag;
                    if (tagStr.startsWith("#"))
                        tagStr = tagStr.substring(1);
                    ResourceLocation tagId = ResourceLocation.tryParse(tagStr);
                    if (tagId != null) {
                        boolean inTag = stack.is(TagKey.create(Registries.ITEM, tagId));
                        return negate != inTag;
                    }
                }
                return true;
            case "is_rarity":
                if (cond.rarity != null) {
                    String r = cond.rarity.toLowerCase().trim();
                    Rarity itemRarity = stack.getRarity();
                    return switch (r) {
                        case "common" -> itemRarity == Rarity.COMMON;
                        case "uncommon" -> itemRarity == Rarity.UNCOMMON;
                        case "rare" -> itemRarity == Rarity.RARE;
                        case "epic" -> itemRarity == Rarity.EPIC;
                        default -> false;
                    };
                }
                return true;
            case "has_nbt":
            case "has_nbt_key":
                String k = cond.nbt_key != null ? cond.nbt_key : cond.key;
                return k != null && tag != null && tag.contains(k);
            case "has_int_tag":
                return cond.nbt_key != null && tag != null && tag.contains(cond.nbt_key, Tag.TAG_INT)
                        && tag.getInt(cond.nbt_key) == cond.nbt_int_value;
            case "has_boolean_tag":
                return cond.nbt_key != null && tag != null && tag.contains(cond.nbt_key, Tag.TAG_BYTE)
                        && tag.getBoolean(cond.nbt_key) == cond.nbt_boolean_value;
            case "has_short_nbt":
                return cond.nbt_key != null && tag != null && tag.contains(cond.nbt_key, Tag.TAG_SHORT)
                        && tag.getShort(cond.nbt_key) == cond.nbt_short_value;
            case "has_long_nbt":
                return cond.nbt_key != null && tag != null && tag.contains(cond.nbt_key, Tag.TAG_LONG)
                        && tag.getLong(cond.nbt_key) == cond.nbt_long_value;
            case "has_string_nbt":
                return cond.nbt_key != null && tag != null && tag.contains(cond.nbt_key, Tag.TAG_STRING)
                        && Objects.equals(tag.getString(cond.nbt_key), cond.nbt_string_value);
            case "has_component":
                return cond.component != null && ModComponents.hasComponent(stack, cond.component);
            case "component_value":
                return ModComponents.componentValueMatches(stack, cond.component, cond.component_value);
            case "has_enchantment":
                if (cond.enchantment != null) {
                    ResourceLocation enchId = ResourceLocation.tryParse(cond.enchantment);
                    if (enchId == null)
                        return false;
                    int currentLevel = 0;
                    for (var enchEntry : EnchantmentHelper.getEnchantmentsForCrafting(stack).entrySet()) {
                        if (enchEntry.getKey().is(enchId)) {
                            currentLevel = enchEntry.getIntValue();
                            break;
                        }
                    }
                    return cond.level <= 0 ? currentLevel >= 1 : currentLevel >= cond.level;
                }
                return true;
            default:
                return true;
        }
    }

    private static void applyAttributes(ItemAttributeModifierEvent event, ItemModifierData data, EquipmentSlot slot) {
        for (ItemModifierData.AttributeEntry entry : data.modifiers.attributes) {
            ResourceLocation attrLoc = ResourceLocation.tryParse(entry.attribute);
            if (attrLoc == null)
                continue;
            Holder<Attribute> attribute = BuiltInRegistries.ATTRIBUTE.getHolder(attrLoc).orElse(null);
            if (attribute == null)
                continue;
            AttributeModifier.Operation operation = switch (entry.operation.toLowerCase()) {
                case "multiply_base" -> AttributeModifier.Operation.ADD_MULTIPLIED_BASE;
                case "multiply_total" -> AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL;
                default -> AttributeModifier.Operation.ADD_VALUE;
            };
            UUID uuid;
            if (entry.uuid != null) {
                uuid = UUID.nameUUIDFromBytes((entry.uuid.toString() + "|" + slot.getName())
                        .getBytes(StandardCharsets.UTF_8));
            } else {
                uuid = UUID.nameUUIDFromBytes((data.toString() + "|" + slot.getName() + "|" + entry.name)
                        .getBytes(StandardCharsets.UTF_8));
            }

            EquipmentSlotGroup slotGroup = EquipmentSlotGroup.bySlot(slot);

            if (operation == AttributeModifier.Operation.ADD_VALUE) {
                // Sum into existing modifiers instead of adding a new one
                boolean replaced = false;
                for (ItemAttributeModifiers.Entry existing : List.copyOf(event.getModifiers())) {
                    if (existing.attribute().is(attrLoc) && existing.slot() == slotGroup) {
                        AttributeModifier oldMod = existing.modifier();
                        AttributeModifier newMod = new AttributeModifier(
                                oldMod.id(),
                                oldMod.amount() + entry.amount,
                                oldMod.operation());
                        event.replaceModifier(attribute, newMod, existing.slot());
                        replaced = true;
                        break;
                    }
                }
                if (!replaced) {
                    AttributeModifier modifier = new AttributeModifier(
                            ResourceLocation.fromNamespaceAndPath("jaams_weaponry", uuid.toString()), entry.amount, operation);
                    event.addModifier(attribute, modifier, slotGroup);
                }
            } else {
                AttributeModifier modifier = new AttributeModifier(
                        ResourceLocation.fromNamespaceAndPath("jaams_weaponry", uuid.toString()), entry.amount, operation);
                event.addModifier(attribute, modifier, slotGroup);
            }
        }
    }

    private static void applyNbt(ItemStack stack, ItemModifierData data) {
        if (data.nbt.isEmpty())
            return;
        CompoundTag current = ModComponents.getOrCreate(stack);
        boolean changed = false;
        for (ItemModifierData.NbtEntry entry : data.nbt) {
            if (entry.key == null || entry.value == null)
                continue;
            Tag newTag = jsonToNbtTag(entry.value, entry.type);
            if (newTag == null)
                continue;
            if (entry.replace) {
                current.put(entry.key, newTag);
                changed = true;
            } else if (!current.contains(entry.key) || !current.get(entry.key).equals(newTag)) {
                current.put(entry.key, newTag);
                changed = true;
            }
        }
        if (changed) {
            ModComponents.set(stack, current);
        }
    }

    private static Tag jsonToNbtTag(JsonElement json, String type) {
        if (json == null)
            return null;
        type = type.toLowerCase().trim();
        try {
            switch (type) {
                case "byte":
                    return ByteTag.valueOf(json.getAsByte());
                case "short":
                    return ShortTag.valueOf(json.getAsShort());
                case "int":
                    return IntTag.valueOf(json.getAsInt());
                case "long":
                    return LongTag.valueOf(json.getAsLong());
                case "float":
                    return FloatTag.valueOf(json.getAsFloat());
                case "double":
                    return DoubleTag.valueOf(json.getAsDouble());
                case "string":
                    return StringTag.valueOf(json.getAsString());
                case "compound":
                case "auto":
                    if (json.isJsonObject()) {
                        CompoundTag compound = new CompoundTag();
                        for (Map.Entry<String, JsonElement> e : json.getAsJsonObject().entrySet()) {
                            Tag subTag = jsonToNbtTag(e.getValue(), "auto");
                            if (subTag != null)
                                compound.put(e.getKey(), subTag);
                        }
                        return compound;
                    }
                    if (json.isJsonPrimitive()) {
                        JsonPrimitive prim = json.getAsJsonPrimitive();
                        if (prim.isNumber()) {
                            double d = prim.getAsDouble();
                            if (d == Math.floor(d) && d >= Integer.MIN_VALUE && d <= Integer.MAX_VALUE) {
                                return IntTag.valueOf((int) d);
                            }
                            return DoubleTag.valueOf(d);
                        } else if (prim.isString()) {
                            return StringTag.valueOf(prim.getAsString());
                        } else if (prim.isBoolean()) {
                            return ByteTag.valueOf(prim.getAsBoolean() ? (byte) 1 : (byte) 0);
                        }
                    }
                    return null;
                case "list":
                case "array":
                    if (json.isJsonArray()) {
                        ListTag list = new ListTag();
                        for (JsonElement e : json.getAsJsonArray()) {
                            Tag elementTag = jsonToNbtTag(e, "auto");
                            if (elementTag != null)
                                list.add(elementTag);
                        }
                        return list;
                    }
                    return null;
                default:
                    return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}
