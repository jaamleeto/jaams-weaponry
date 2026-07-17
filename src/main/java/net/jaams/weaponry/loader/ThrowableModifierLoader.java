package net.jaams.weaponry.loader;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.AddReloadListenerEvent;

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

import net.jaams.weaponry.data.ThrowableItemData;

import java.util.Optional;
import java.util.Map;
import java.util.Locale;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonElement;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ThrowableModifierLoader extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final ThrowableModifierLoader INSTANCE = new ThrowableModifierLoader();
    private volatile Map<ResourceLocation, ThrowableItemData> throwables = new ConcurrentHashMap<>();
    private volatile Map<ResourceLocation, List<ThrowableItemData>> itemCache = new ConcurrentHashMap<>();
    private static final Logger LOGGER = LogManager.getLogger(ThrowableModifierLoader.class);

    private ThrowableModifierLoader() {
        super(GSON, "jaams/throw_modifier");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager resourceManager,
            ProfilerFiller profiler) {
        if (resources == null) {
            LOGGER.warn("ThrowableModifierLoader apply called with null resources");
            return;
        }
        Map<ResourceLocation, ThrowableItemData> newThrowables = new ConcurrentHashMap<>();
        int count = 0;
        int errors = 0;
        for (Map.Entry<ResourceLocation, JsonElement> entry : resources.entrySet()) {
            if (entry == null || entry.getKey() == null || entry.getValue() == null) continue;
            ResourceLocation fileId = entry.getKey();
            try {
                ThrowableItemData data = GSON.fromJson(entry.getValue(), ThrowableItemData.class);
                if (data == null) {
                    LOGGER.warn("Throwable modifier file {} returned null data", fileId);
                    errors++;
                    continue;
                }
                if (data.target == null || data.target.isEmpty()) {
                    LOGGER.warn("Throwable modifier file {} has no target defined", fileId);
                    errors++;
                    continue;
                }
                if (data.throwable == null || data.throwable.projectile == null || data.throwable.projectile.isEmpty()) {
                    LOGGER.warn("Throwable modifier file {} has no 'projectile' defined", fileId);
                    errors++;
                    continue;
                }
                if (data.enabled != null && !data.enabled) {
                    LOGGER.info("Throwable modifier file {} is disabled, skipping", fileId);
                    continue;
                }
                newThrowables.put(fileId, data);
                count++;
            } catch (Exception e) {
                errors++;
                LOGGER.error("Failed to load throwable modifier file: {}", fileId, e);
            }
        }
        this.throwables = newThrowables;
        this.itemCache = new ConcurrentHashMap<>();
        LOGGER.info("Loaded {} throwable modifiers ({} errors)", count, errors);
    }

    public Optional<ThrowableItemData> getDataForStack(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return Optional.empty();
        }
        List<ThrowableItemData> candidates = getForItem(stack.getItem());
        for (ThrowableItemData entry : candidates) {
            if (evaluateConditions(entry, stack)) {
                return Optional.of(entry);
            }
        }
        return Optional.empty();
    }

    public List<ThrowableItemData> getForItem(Item item) {
        if (item == null || item == Items.AIR) {
            return List.of();
        }
        try {
            ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(item);
            if (itemId == null || itemId.getPath().isEmpty()) {
                return List.of();
            }
            return itemCache.computeIfAbsent(itemId, this::computeThrowablesForItem);
        } catch (Exception e) {
            return List.of();
        }
    }

    private List<ThrowableItemData> computeThrowablesForItem(ResourceLocation itemId) {
        List<ThrowableItemData> result = new ArrayList<>();
        for (ThrowableItemData data : throwables.values()) {
            if (data == null) continue;
            if (data.throwable != null && matchesTarget(data.target, itemId) && data.throwable.throw_enabled) {
                result.add(data);
            }
        }
        result.sort((a, b) -> Integer.compare(b.priority, a.priority));
        return result;
    }

    public boolean evaluateConditions(ThrowableItemData data, ItemStack stack) {
        if (data == null || stack == null) return false;
        if (data.conditions == null || data.conditions.isEmpty()) {
            return true;
        }
        boolean isAndMode = "and".equalsIgnoreCase(data.condition_mode);
        for (ThrowableItemData.Condition cond : data.conditions) {
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

    private boolean evaluateSingleCondition(ThrowableItemData.Condition cond, ItemStack stack) {
        if (cond == null || cond.type == null)
            return false;
        return switch (cond.type.toLowerCase(Locale.ROOT)) {
            case "enchantment" -> checkEnchantment(cond, stack);
            case "nbt" -> checkNBT(cond, stack);
            case "tag" -> checkTag(cond, stack);
            case "item" -> checkItem(cond, stack);
            case "mod" -> checkMod(cond, stack);
            case "rarity" -> checkRarity(cond, stack);
            default -> false;
        };
    }

    private boolean checkEnchantment(ThrowableItemData.Condition cond, ItemStack stack) {
        if (stack == null || cond.enchantment == null)
            return false;
        ResourceLocation enchId = ResourceLocation.tryParse(cond.enchantment);
        if (enchId == null)
            return false;
        Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(enchId);
        if (enchantment == null)
            return false;
        int level = EnchantmentHelper.getTagEnchantmentLevel(enchantment, stack);
        return level >= cond.level;
    }

    private boolean checkNBT(ThrowableItemData.Condition cond, ItemStack stack) {
        if (stack == null || !stack.hasTag() || cond.key == null || cond.nbt_type == null)
            return false;
        CompoundTag tag = stack.getTag();
        if (tag == null)
            return false;
        return switch (cond.nbt_type.toLowerCase(Locale.ROOT)) {
            case "boolean" -> tag.contains(cond.key, 1) && tag.getBoolean(cond.key) == cond.nbt_boolean_value;
            case "int" -> tag.contains(cond.key, 3) && tag.getInt(cond.key) == cond.nbt_int_value;
            case "short" -> tag.contains(cond.key, 2) && tag.getShort(cond.key) == cond.nbt_short_value;
            case "long" -> tag.contains(cond.key, 4) && tag.getLong(cond.key) == long.class.cast(cond.nbt_long_value);
            case "string" -> tag.contains(cond.key, 8) && cond.nbt_string_value != null
                    && cond.nbt_string_value.equals(tag.getString(cond.key));
            default -> false;
        };
    }

    private boolean checkTag(ThrowableItemData.Condition cond, ItemStack stack) {
        if (cond.tag == null || stack == null)
            return false;
        ResourceLocation tagId = ResourceLocation.tryParse(cond.tag);
        if (tagId == null)
            return false;
        return stack.is(TagKey.create(Registries.ITEM, tagId));
    }

    private boolean checkItem(ThrowableItemData.Condition cond, ItemStack stack) {
        if (cond.item == null || stack == null)
            return false;
        ResourceLocation itemId = ResourceLocation.tryParse(cond.item);
        if (itemId == null || stack == null) return false;
        ResourceLocation stackId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        return stackId != null && stackId.equals(itemId);
    }

    private boolean checkMod(ThrowableItemData.Condition cond, ItemStack stack) {
        if (cond.mod_id == null || stack == null)
            return false;
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        return itemId != null && cond.mod_id.equalsIgnoreCase(itemId.getNamespace());
    }

    private boolean checkRarity(ThrowableItemData.Condition cond, ItemStack stack) {
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
        if (pattern.startsWith("regex:")) {
            return itemStr.matches(pattern.substring(6));
        }
        if (pattern.contains("*")) {
            String regex = "^" + pattern.replace("*", ".*") + "$";
            return itemStr.matches(regex);
        }
        if (pattern.startsWith("#")) {
            ResourceLocation tagId = ResourceLocation.tryParse(pattern.substring(1));
            if (tagId == null)
                return false;
            TagKey<Item> tagKey = TagKey.create(Registries.ITEM, tagId);
            Item item = ForgeRegistries.ITEMS.getValue(itemId);
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
