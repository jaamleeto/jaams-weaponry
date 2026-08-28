package net.jaams.weaponry.loader;

import net.jaams.weaponry.sync.NetworkSyncable;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TagsUpdatedEvent;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.tags.TagKey;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;

import net.jaams.weaponry.JaamsWeaponryMod;
import net.jaams.weaponry.condition.ConditionEvaluator;
import net.jaams.weaponry.data.ThrowableItemData;
import net.jaams.weaponry.util.ModEnums;

import java.util.Optional;
import java.util.Map;
import java.util.Locale;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonElement;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;

@Mod.EventBusSubscriber(modid = JaamsWeaponryMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ThrowableModifierLoader extends SimpleJsonResourceReloadListener implements NetworkSyncable {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final ThrowableModifierLoader INSTANCE = new ThrowableModifierLoader();
    private volatile Map<ResourceLocation, ThrowableItemData> throwables = new ConcurrentHashMap<>();
    private volatile Map<ResourceLocation, List<ThrowableItemData>> itemCache = new ConcurrentHashMap<>();
    private volatile Map<String, String> sources = new ConcurrentHashMap<>();
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
        Map<String, String> srcs = new ConcurrentHashMap<>();
        for (Map.Entry<ResourceLocation, JsonElement> entry : resources.entrySet()) {
            if (entry == null || entry.getKey() == null || entry.getValue() == null)
                continue;
            try {
                srcs.put(entry.getKey().toString(), GSON.toJson(entry.getValue()));
            } catch (Exception ignored) {
            }
        }
        rebuild(srcs);
    }

    private void rebuild(Map<String, String> srcs) {
        Map<ResourceLocation, ThrowableItemData> newThrowables = new ConcurrentHashMap<>();
        int count = 0;
        int errors = 0;
        for (Map.Entry<String, String> entry : srcs.entrySet()) {
            String fileId = entry.getKey();
            if (!JaamsWeaponryMod.isOwnNamespace(fileId)) {
                continue;
            }
            try {
                ThrowableItemData data = GSON.fromJson(com.google.gson.JsonParser.parseString(entry.getValue()),
                        ThrowableItemData.class);
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
                if (data.throwable.throw_mode != null && !data.throwable.throw_mode.isEmpty()) {
                    try {
                        ModEnums.ThrowMode.valueOf(data.throwable.throw_mode.toUpperCase(Locale.ROOT));
                    } catch (IllegalArgumentException e) {
                        LOGGER.warn("Throwable modifier file {}: invalid throw_mode '{}'", fileId,
                                data.throwable.throw_mode);
                        errors++;
                    }
                }
                for (String warning : ConditionEvaluator.validateConditions(data.conditions)) {
                    LOGGER.warn("Throwable modifier file {}: {}", fileId, warning);
                    errors++;
                }
                data.id = fileId;
                newThrowables.put(ResourceLocation.parse(fileId), data);
                count++;
            } catch (Exception e) {
                errors++;
                LOGGER.error("Failed to load throwable modifier file: {}", fileId, e);
            }
        }
        this.throwables = newThrowables;
        this.itemCache = new ConcurrentHashMap<>();
        this.sources = new ConcurrentHashMap<>(srcs);
        LOGGER.info("Loaded {} throwable modifiers ({} errors)", count, errors);
    }

    @Override
    public String getSyncId() {
        return "throw_modifier";
    }

    @Override
    public Map<String, String> getSourcesSnapshot() {
        return new HashMap<>(sources);
    }

    @Override
    public void applyNetworkSync(Map<String, String> srcs) {
        if (srcs == null)
            return;
        rebuild(srcs);
    }

    @SubscribeEvent
    public static void onTagsUpdated(TagsUpdatedEvent event) {
        INSTANCE.itemCache.clear();
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
        result.sort((a, b) -> {
            int byPriority = Integer.compare(b.priority, a.priority);
            if (byPriority != 0)
                return byPriority;
            return String.valueOf(a.id).compareTo(String.valueOf(b.id));
        });
        return result;
    }

    public boolean evaluateConditions(ThrowableItemData data, ItemStack stack) {
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
