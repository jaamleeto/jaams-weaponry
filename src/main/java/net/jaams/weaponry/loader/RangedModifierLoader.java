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
import net.minecraft.core.registries.Registries;

import net.jaams.weaponry.data.RangedItemData;

import java.util.Optional;
import java.util.Map;
import java.util.Locale;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonElement;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class RangedModifierLoader extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final RangedModifierLoader INSTANCE = new RangedModifierLoader();
    private volatile Map<ResourceLocation, RangedItemData> modifiers = new ConcurrentHashMap<>();
    private volatile Map<ResourceLocation, List<RangedItemData>> itemCache = new ConcurrentHashMap<>();
    private static final Logger LOGGER = LogManager.getLogger(RangedModifierLoader.class);

    private RangedModifierLoader() {
        super(GSON, "jaams/ranged_modifier");
    }

    public static Gson getGson() {
        return GSON;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager resourceManager,
            ProfilerFiller profiler) {
        if (resources == null) {
            LOGGER.warn("RangedModifierLoader apply called with null resources");
            return;
        }
        Map<ResourceLocation, RangedItemData> newModifiers = new ConcurrentHashMap<>();
        int count = 0;
        int errors = 0;
        for (Map.Entry<ResourceLocation, JsonElement> entry : resources.entrySet()) {
            if (entry == null || entry.getKey() == null || entry.getValue() == null) continue;
            ResourceLocation fileId = entry.getKey();
            try {
                RangedItemData data = GSON.fromJson(entry.getValue(), RangedItemData.class);
                if (data == null) {
                    LOGGER.warn("Ranged modifier file {} returned null data", fileId);
                    errors++;
                    continue;
                }
                if (data.target == null || data.target.isEmpty()) {
                    LOGGER.warn("Ranged modifier file {} has no target defined", fileId);
                    errors++;
                    continue;
                }
                if (data.ranged == null || data.ranged.ranged_type == null || data.ranged.ranged_type.isEmpty()) {
                    LOGGER.warn("Ranged modifier file {} has no 'ranged_type' defined", fileId);
                    errors++;
                    continue;
                }
                if (data.enabled != null && !data.enabled) {
                    LOGGER.info("Ranged modifier file {} is disabled, skipping", fileId);
                    continue;
                }
                newModifiers.put(fileId, data);
                count++;
            } catch (Exception e) {
                errors++;
                LOGGER.error("Failed to load ranged modifier file: {}", fileId, e);
            }
        }
        this.modifiers = newModifiers;
        this.itemCache = new ConcurrentHashMap<>();
        LOGGER.info("Loaded {} ranged modifiers ({} errors)", count, errors);
    }

    public Optional<RangedItemData> getDataForStack(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return Optional.empty();
        }
        List<RangedItemData> candidates = getForItem(stack.getItem());
        for (RangedItemData entry : candidates) {
            if (evaluateConditions(entry, stack)) {
                return Optional.of(entry);
            }
        }
        return Optional.empty();
    }

    public List<RangedItemData> getForItem(Item item) {
        if (item == null || item == Items.AIR) {
            return List.of();
        }
        try {
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
            if (itemId == null || itemId.getPath().isEmpty()) {
                return List.of();
            }
            return itemCache.computeIfAbsent(itemId, this::computeRangedModifiersForItem);
        } catch (Exception e) {
            return List.of();
        }
    }

    private List<RangedItemData> computeRangedModifiersForItem(ResourceLocation itemId) {
        List<RangedItemData> result = new ArrayList<>();
        for (RangedItemData data : modifiers.values()) {
            if (data == null) continue;
            if (matchesTarget(data.target, itemId)) {
                result.add(data);
            }
        }
        result.sort((a, b) -> Integer.compare(b.priority, a.priority));
        return result;
    }

    public boolean evaluateConditions(RangedItemData data, ItemStack stack) {
        if (data == null || stack == null) return false;
        if (data.conditions == null || data.conditions.isEmpty()) {
            return true;
        }
        boolean isAndMode = "and".equalsIgnoreCase(data.condition_mode);
        for (RangedItemData.Condition cond : data.conditions) {
            boolean conditionMet = evaluateSingleCondition(cond, stack);
            if (isAndMode && !conditionMet) {
                return false;
            }
            if (!isAndMode && conditionMet) {
                return true;
            }
        }
        return isAndMode;
    }

    private boolean evaluateSingleCondition(RangedItemData.Condition cond, ItemStack stack) {
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

    private boolean checkEnchantment(RangedItemData.Condition cond, ItemStack stack) {
        if (stack == null || cond.enchantment == null)
            return false;
        ResourceLocation enchId = ResourceLocation.tryParse(cond.enchantment);
        if (enchId == null)
            return false;
        net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> enchantment = net.jaams.weaponry.init.ModEnchantments.holderFromId(enchId);
        if (enchantment == null)
            return false;
        int level = EnchantmentHelper.getTagEnchantmentLevel(enchantment, stack);
        return level >= cond.level;
    }

    private boolean checkNBT(RangedItemData.Condition cond, ItemStack stack) {
        if (stack == null || !ModComponents.has(stack) || cond.key == null || cond.nbt_key == null)
            return false;
        CompoundTag tag = ModComponents.get(stack);
        if (tag == null)
            return false;
        return switch (cond.nbt_key.toLowerCase(Locale.ROOT)) {
            case "boolean" -> tag.contains(cond.key, 1) && tag.getBoolean(cond.key) == cond.nbt_boolean_value;
            case "int" -> tag.contains(cond.key, 3) && tag.getInt(cond.key) == cond.nbt_int_value;
            case "short" -> tag.contains(cond.key, 2) && tag.getShort(cond.key) == cond.nbt_short_value;
            case "long" -> tag.contains(cond.key, 4) && tag.getLong(cond.key) == cond.nbt_long_value;
            case "string" -> tag.contains(cond.key, 8) && cond.nbt_string_value != null
                    && cond.nbt_string_value.equals(tag.getString(cond.key));
            default -> false;
        };
    }

    private boolean checkTag(RangedItemData.Condition cond, ItemStack stack) {
        if (cond.tag == null || stack == null)
            return false;
        ResourceLocation tagId = ResourceLocation.tryParse(cond.tag);
        if (tagId == null)
            return false;
        return stack.is(TagKey.create(Registries.ITEM, tagId));
    }

    private boolean checkItem(RangedItemData.Condition cond, ItemStack stack) {
        if (cond.item == null || stack == null)
            return false;
        ResourceLocation itemId = ResourceLocation.tryParse(cond.item);
        if (itemId == null) return false;
        ResourceLocation stackId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return stackId != null && stackId.equals(itemId);
    }

    private boolean checkMod(RangedItemData.Condition cond, ItemStack stack) {
        if (cond.mod_id == null || stack == null)
            return false;
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return itemId != null && cond.mod_id.equalsIgnoreCase(itemId.getNamespace());
    }

    private boolean checkRarity(RangedItemData.Condition cond, ItemStack stack) {
        if (cond.rarity == null || stack == null)
            return false;
        return stack.getRarity().name().equalsIgnoreCase(cond.rarity);
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
            if (negate) {
                if (matches)
                    return false;
            } else if (matches) {
                anyPositiveMatch = true;
            }
        }
        return anyPositiveMatch;
    }

    private boolean evaluatePattern(String pattern, String itemStr, ResourceLocation itemId) {
        if (pattern.startsWith("regex:"))
            return itemStr.matches(pattern.substring(6));
        if (pattern.contains("*")) {
            String regex = "^" + pattern.replace("*", ".*") + "$";
            return itemStr.matches(regex);
        }
        if (pattern.startsWith("#")) {
            ResourceLocation tagId = ResourceLocation.tryParse(pattern.substring(1));
            if (tagId == null)
                return false;
            TagKey<Item> tagKey = TagKey.create(Registries.ITEM, tagId);
            Item item = BuiltInRegistries.ITEM.get(itemId);
            return item != null && new ItemStack(item).is(tagKey);
        }
        ResourceLocation loc = ResourceLocation.tryParse(pattern);
        return loc != null && loc.equals(itemId);
    }

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(INSTANCE);
    }
}
