package net.jaams.weaponry.loader;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.tags.TagKey;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.resources.ResourceLocation;

import net.jaams.weaponry.condition.ConditionEvaluator;
import net.jaams.weaponry.data.RangedItemData;

import java.util.Locale;
import java.util.Optional;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonElement;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;

@EventBusSubscriber
public class RangedModifierLoader extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final RangedModifierLoader INSTANCE = new RangedModifierLoader();
    private static final Set<String> VALID_RANGED_TYPES = Set.of("SLINGSHOT");
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
                if (!VALID_RANGED_TYPES.contains(data.ranged.ranged_type.toUpperCase(Locale.ROOT))) {
                    LOGGER.warn("Ranged modifier file {}: invalid ranged_type '{}' (expected {})", fileId,
                            data.ranged.ranged_type, VALID_RANGED_TYPES);
                    errors++;
                }
                for (String warning : ConditionEvaluator.validateConditions(data.conditions)) {
                    LOGGER.warn("Ranged modifier file {}: {}", fileId, warning);
                    errors++;
                }
                data.id = fileId.toString();
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
        result.sort((a, b) -> {
            int byPriority = Integer.compare(b.priority, a.priority);
            if (byPriority != 0)
                return byPriority;
            return String.valueOf(a.id).compareTo(String.valueOf(b.id));
        });
        return result;
    }

    public boolean evaluateConditions(RangedItemData data, ItemStack stack) {
        if (data == null || stack == null)
            return false;
        return ConditionEvaluator.evaluateAll(data.conditions, data.condition_mode, stack);
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
