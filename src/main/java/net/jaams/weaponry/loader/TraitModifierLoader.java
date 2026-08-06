package net.jaams.weaponry.loader;

import net.jaams.weaponry.util.ModComponents;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.tags.TagKey;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.core.registries.Registries;

import net.jaams.weaponry.data.TraitModifierData;

import java.util.Map;
import java.util.Locale;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonElement;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class TraitModifierLoader extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final TraitModifierLoader INSTANCE = new TraitModifierLoader();
    private static final Logger LOGGER = LogManager.getLogger(TraitModifierLoader.class);
    private volatile Map<ResourceLocation, TraitModifierData> modifiers = new ConcurrentHashMap<>();
    private volatile Map<ResourceLocation, List<TraitModifierData>> itemCache = new ConcurrentHashMap<>();

    private TraitModifierLoader() {
        super(GSON, "jaams/trait_modifier");
    }

    public static Gson getGson() {
        return GSON;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager resourceManager,
            ProfilerFiller profiler) {
        if (resources == null) {
            LOGGER.warn("TraitModifierLoader apply called with null resources");
            return;
        }
        Map<ResourceLocation, TraitModifierData> tempModifiers = new ConcurrentHashMap<>();
        int count = 0;
        int errors = 0;
        for (Map.Entry<ResourceLocation, JsonElement> entry : resources.entrySet()) {
            if (entry == null || entry.getKey() == null || entry.getValue() == null) continue;
            ResourceLocation fileId = entry.getKey();
            try {
                TraitModifierData data = GSON.fromJson(entry.getValue(), TraitModifierData.class);
                if (data == null) {
                    LOGGER.warn("Trait modifier file {} returned null data", fileId);
                    errors++;
                    continue;
                }
                if (data.target == null || data.target.isEmpty()) {
                    LOGGER.warn("Trait modifier file {} ignores loading: 'target' is missing or empty", fileId);
                    errors++;
                    continue;
                }
                if (data.enabled != null && !data.enabled) {
                    LOGGER.info("Trait modifier file {} is disabled, skipping", fileId);
                    continue;
                }
                tempModifiers.put(fileId, data);
                count++;
            } catch (Exception e) {
                errors++;
                LOGGER.error("Failed to load trait modifier file: {}", fileId, e);
            }
        }
        this.modifiers = tempModifiers;
        this.itemCache = new ConcurrentHashMap<>();
        LOGGER.info("Loaded {} custom trait modifiers ({} errors)", count, errors);
    }

    public void mergeFrom(TraitModifierData base, TraitModifierData other) {
        if (base == null || other == null)
            return;
        if (other.active_traits != null) {
            if (base.active_traits == null)
                base.active_traits = new ArrayList<>();
            for (String trait : other.active_traits) {
                if (!base.active_traits.contains(trait)) {
                    base.active_traits.add(trait);
                }
            }
        }
        mergeEntryObjects(base.traits, other.traits, TraitModifierData.TraitsEntry.class, base, "traits");
        mergeEntryObjects(base.projectile_traits, other.projectile_traits,
                TraitModifierData.ProjectileTraitsEntry.class, base, "projectile_traits");
    }

    private void mergeEntryObjects(Object baseParent, Object otherParent, Class<?> clazz, TraitModifierData baseRoot,
            String fieldName) {
        if (otherParent == null)
            return;
        try {
            if (baseParent == null) {
                baseParent = clazz.getDeclaredConstructor().newInstance();
                baseRoot.getClass().getField(fieldName).set(baseRoot, baseParent);
            }
            for (java.lang.reflect.Field subObjectField : clazz.getDeclaredFields()) {
                subObjectField.setAccessible(true);
                Object otherSubEntry = subObjectField.get(otherParent);
                if (otherSubEntry == null)
                    continue;
                Object baseSubEntry = subObjectField.get(baseParent);
                if (baseSubEntry == null) {
                    JsonElement clone = GSON.toJsonTree(otherSubEntry);
                    subObjectField.set(baseParent, GSON.fromJson(clone, subObjectField.getType()));
                } else {
                    for (java.lang.reflect.Field dataField : subObjectField.getType().getDeclaredFields()) {
                        dataField.setAccessible(true);
                        Object newValue = dataField.get(otherSubEntry);
                        if (newValue != null) {
                            dataField.set(baseSubEntry, newValue);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error haciendo merge profundo de rasgos", e);
        }
    }

    public List<TraitModifierData> getForItem(Item item) {
        if (item == null || item == Items.AIR)
            return List.of();
        try {
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
            if (itemId == null || itemId.getPath().isEmpty())
                return List.of();
            return itemCache.computeIfAbsent(itemId, this::computeModifiersForItem);
        } catch (Exception e) {
            return List.of();
        }
    }

    private List<TraitModifierData> computeModifiersForItem(ResourceLocation itemId) {
        List<TraitModifierData> result = new ArrayList<>();
        for (TraitModifierData data : modifiers.values()) {
            if (data == null) continue;
            if (matchesTarget(data.target, itemId)) {
                result.add(data);
            }
        }
        result.sort((a, b) -> Integer.compare(a.priority, b.priority));
        return result;
    }

    public boolean evaluateConditions(TraitModifierData data, ItemStack stack) {
        if (data == null || stack == null) return false;
        if (data.conditions == null || data.conditions.isEmpty())
            return true;
        boolean isAndMode = "and".equalsIgnoreCase(data.condition_mode);
        for (TraitModifierData.Condition cond : data.conditions) {
            boolean conditionMet = evaluateSingleCondition(cond, stack);
            if (isAndMode && !conditionMet)
                return false;
            if (!isAndMode && conditionMet)
                return true;
        }
        return isAndMode;
    }

    private boolean evaluateSingleCondition(TraitModifierData.Condition cond, ItemStack stack) {
        if (cond == null || cond.type == null)
            return false;
        return switch (cond.type.toLowerCase(Locale.ROOT)) {
            case "enchantment" -> checkEnchantment(cond, stack);
            case "nbt" -> checkNBT(cond, stack);
            case "tag" -> checkTag(cond, stack);
            case "item" -> checkItem(cond, stack);
            case "mod" -> checkMod(cond, stack);
            case "rarity" -> checkRarity(cond, stack);
            case "has_component" -> cond.component != null && ModComponents.hasComponent(stack, cond.component);
            case "component_value" -> ModComponents.componentValueMatches(stack, cond.component, cond.component_value);
            default -> false;
        };
    }

    private boolean checkEnchantment(TraitModifierData.Condition cond, ItemStack stack) {
        if (stack == null || cond.enchantment == null)
            return false;
        ResourceLocation enchId = ResourceLocation.tryParse(cond.enchantment);
        if (enchId == null)
            return false;
        int level = 0;
        for (var e : net.minecraft.world.item.enchantment.EnchantmentHelper.getEnchantmentsForCrafting(stack).entrySet()) {
            if (e.getKey().is(enchId)) { level = e.getIntValue(); break; }
        }
        return level >= cond.level;
    }

    private boolean checkNBT(TraitModifierData.Condition cond, ItemStack stack) {
        if (stack == null || !ModComponents.has(stack) || cond.key == null || cond.nbt_key == null)
            return false;
        CompoundTag tag = ModComponents.get(stack);
        if (tag == null)
            return false;
        return switch (cond.nbt_key.toLowerCase(Locale.ROOT)) {
            case "boolean" -> tag.contains(cond.key, Tag.TAG_BYTE) && tag.getBoolean(cond.key) == cond.nbt_boolean_value;
            case "int" -> tag.contains(cond.key, Tag.TAG_INT) && tag.getInt(cond.key) == cond.nbt_int_value;
            case "short" -> tag.contains(cond.key, Tag.TAG_SHORT) && tag.getShort(cond.key) == cond.nbt_short_value;
            case "long" -> tag.contains(cond.key, Tag.TAG_LONG) && tag.getLong(cond.key) == cond.nbt_long_value;
            case "string" -> tag.contains(cond.key, Tag.TAG_STRING) && cond.nbt_string_value != null
                    && cond.nbt_string_value.equals(tag.getString(cond.key));
            default -> false;
        };
    }

    private boolean checkTag(TraitModifierData.Condition cond, ItemStack stack) {
        if (cond.tag == null || stack == null)
            return false;
        ResourceLocation tagId = ResourceLocation.tryParse(cond.tag);
        return tagId != null && stack.is(TagKey.create(Registries.ITEM, tagId));
    }

    private boolean checkItem(TraitModifierData.Condition cond, ItemStack stack) {
        if (cond.item == null || stack == null)
            return false;
        ResourceLocation itemId = ResourceLocation.tryParse(cond.item);
        ResourceLocation stackId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return itemId != null && stackId != null && stackId.equals(itemId);
    }

    private boolean checkMod(TraitModifierData.Condition cond, ItemStack stack) {
        if (cond.mod_id == null || stack == null)
            return false;
        ResourceLocation stackId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return stackId != null && cond.mod_id.equals(stackId.getNamespace());
    }

    private boolean checkRarity(TraitModifierData.Condition cond, ItemStack stack) {
        return cond.rarity != null && stack != null && stack.getRarity().name().equalsIgnoreCase(cond.rarity);
    }

    private boolean matchesTarget(List<String> targets, ResourceLocation itemId) {
        if (targets == null || targets.isEmpty())
            return false;
        String itemStr = itemId.toString();
        boolean anyPositiveMatch = false;
        for (String t : targets) {
            boolean negate = t.startsWith("!");
            String pattern = negate ? t.substring(1) : t;
            boolean matches = evaluatePattern(pattern, itemStr, itemId);
            if (negate && matches)
                return false;
            if (!negate && matches)
                anyPositiveMatch = true;
        }
        return anyPositiveMatch;
    }

    private boolean evaluatePattern(String pattern, String itemStr, ResourceLocation itemId) {
        if (pattern.startsWith("regex:"))
            return itemStr.matches(pattern.substring(6));
        if (pattern.contains("*"))
            return itemStr.matches("^" + pattern.replace("*", ".*") + "$");
        if (pattern.startsWith("#")) {
            ResourceLocation tagId = ResourceLocation.tryParse(pattern.substring(1));
            return tagId != null && isItemInTag(itemId, tagId);
        }
        ResourceLocation loc = ResourceLocation.tryParse(pattern);
        return loc != null && loc.equals(itemId);
    }

    private boolean isItemInTag(ResourceLocation itemId, ResourceLocation tagId) {
        Item item = BuiltInRegistries.ITEM.get(itemId);
        if (item == null)
            return false;
        return new ItemStack(item).is(TagKey.create(Registries.ITEM, tagId));
    }

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(INSTANCE);
    }
}
