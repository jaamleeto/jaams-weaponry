package net.jaams.weaponry.loader;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

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
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;

import net.jaams.weaponry.data.ItemModifierData;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonElement;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class ItemModifierLoader extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final ItemModifierLoader INSTANCE = new ItemModifierLoader();
    private volatile Map<ResourceLocation, ItemModifierData> modifiers = new ConcurrentHashMap<>();
    private volatile Map<ResourceLocation, List<ItemModifierData>> itemCache = new ConcurrentHashMap<>();
    private static final Logger LOGGER = LogManager.getLogger(ItemModifierLoader.class);

    private ItemModifierLoader() {
        super(GSON, "jaams/item_modifier");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager resourceManager,
            ProfilerFiller profiler) {
        if (resources == null) {
            LOGGER.warn("ItemModifierLoader apply called with null resources");
            return;
        }
        Map<ResourceLocation, ItemModifierData> newModifiers = new ConcurrentHashMap<>();
        int count = 0;
        int errors = 0;
        for (Map.Entry<ResourceLocation, JsonElement> entry : resources.entrySet()) {
            if (entry == null || entry.getKey() == null || entry.getValue() == null) continue;
            ResourceLocation fileId = entry.getKey();
            try {
                ItemModifierData data = GSON.fromJson(entry.getValue(), ItemModifierData.class);
                if (data == null) {
                    LOGGER.warn("Item modifier file {} returned null data", fileId);
                    errors++;
                    continue;
                }
                if (data.target == null || data.target.isEmpty()) {
                    errors++;
                    LOGGER.warn("Modifier file {} has no target defined", fileId);
                    continue;
                }
                if (data.enabled != null && !data.enabled) {
                    LOGGER.info("Modifier file {} is disabled, skipping", fileId);
                    continue;
                }
                newModifiers.put(fileId, data);
                count++;
            } catch (Exception e) {
                errors++;
                LOGGER.error("Failed to load modifier file: {}", fileId, e);
            }
        }
        this.modifiers = newModifiers;
        this.itemCache = new ConcurrentHashMap<>();
        LOGGER.info("Loaded {} item modifiers ({} errors)", count, errors);
    }

    public List<ItemModifierData> getForItem(Item item) {
        if (item == null || item == Items.AIR) {
            return List.of();
        }
        try {
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
            if (itemId == null || itemId.getPath().isEmpty()) {
                return List.of();
            }
            return itemCache.computeIfAbsent(itemId, this::computeModifiersForItem);
        } catch (Exception e) {
            return List.of();
        }
    }

    private List<ItemModifierData> computeModifiersForItem(ResourceLocation itemId) {
        List<ItemModifierData> result = new ArrayList<>();
        for (ItemModifierData data : modifiers.values()) {
            if (data == null) continue;
            if (matchesTarget(data.target, itemId)) {
                result.add(data);
            }
        }
        result.sort((a, b) -> Integer.compare(b.priority, a.priority));
        return result;
    }

    private boolean matchesTarget(List<String> targets, ResourceLocation itemId) {
        if (targets == null || targets.isEmpty())
            return false;
        String itemStr = itemId.toString();
        for (String t : targets) {
            boolean negate = t.startsWith("!");
            String pattern = negate ? t.substring(1) : t;
            boolean matches = evaluatePattern(pattern, itemStr, itemId);
            if (negate) {
                if (matches)
                    return false;
            } else if (matches) {
                return true;
            }
        }
        return false;
    }

    private boolean evaluatePattern(String pattern, String itemStr, ResourceLocation itemId) {
        if (pattern.startsWith("regex:")) {
            String regex = pattern.substring(6);
            return itemStr.matches(regex);
        }
        if (pattern.contains("*")) {
            String regex = "^" + pattern.replace("*", ".*") + "$";
            return itemStr.matches(regex);
        }
        if (pattern.startsWith("#")) {
            ResourceLocation tagId = ResourceLocation.tryParse(pattern.substring(1));
            return tagId != null && isItemInTag(itemId, tagId);
        }
        ResourceLocation loc = ResourceLocation.tryParse(pattern);
        return loc != null && loc.equals(itemId);
    }

    private boolean isItemInTag(ResourceLocation itemId, ResourceLocation tagId) {
        TagKey<Item> tagKey = TagKey.create(Registries.ITEM, tagId);
        Item item = BuiltInRegistries.ITEM.get(itemId);
        return item != null && new ItemStack(item).is(tagKey);
    }

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(INSTANCE);
    }
}
