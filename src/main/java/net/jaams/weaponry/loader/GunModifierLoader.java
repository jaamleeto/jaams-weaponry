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
import net.jaams.weaponry.data.GunItemData;
import net.jaams.weaponry.util.ModGuns;

import java.util.Locale;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonElement;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;

@Mod.EventBusSubscriber(modid = JaamsWeaponryMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GunModifierLoader extends SimpleJsonResourceReloadListener implements NetworkSyncable {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final GunModifierLoader INSTANCE = new GunModifierLoader();
    private volatile Map<ResourceLocation, GunItemData> guns = new ConcurrentHashMap<>();
    private volatile Map<ResourceLocation, List<GunItemData>> itemCache = new ConcurrentHashMap<>();
    private volatile Map<String, String> sources = new ConcurrentHashMap<>();
    private static final Logger LOGGER = LogManager.getLogger(GunModifierLoader.class);

    private GunModifierLoader() {
        super(GSON, "jaams/gun_modifier");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager resourceManager,
            ProfilerFiller profiler) {
        if (resources == null) {
            LOGGER.warn("GunModifierLoader apply called with null resources");
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
        Map<ResourceLocation, GunItemData> newGuns = new ConcurrentHashMap<>();
        int count = 0;
        int errors = 0;
        for (Map.Entry<String, String> entry : srcs.entrySet()) {
            String fileId = entry.getKey();
            if (!JaamsWeaponryMod.isOwnNamespace(fileId)) {
                continue;
            }
            try {
                GunItemData data = GSON.fromJson(com.google.gson.JsonParser.parseString(entry.getValue()),
                        GunItemData.class);
                if (data == null) {
                    LOGGER.warn("Gun modifier file {} returned null data", fileId);
                    errors++;
                    continue;
                }
                if (data.target == null || data.target.isEmpty()) {
                    LOGGER.warn("Gun modifier file {} has no target defined", fileId);
                    errors++;
                    continue;
                }
                if (data.gun == null || data.gun.gun_type == null || data.gun.gun_type.isEmpty()) {
                    LOGGER.warn("Gun modifier file {} has no 'gun_type' defined", fileId);
                    errors++;
                    continue;
                }
                if (data.enabled != null && !data.enabled) {
                    LOGGER.info("Gun modifier file {} is disabled, skipping", fileId);
                    continue;
                }
                try {
                    ModGuns.GunType.valueOf(data.gun.gun_type.toUpperCase(Locale.ROOT));
                } catch (IllegalArgumentException e) {
                    LOGGER.warn("Gun modifier file {}: invalid gun_type '{}'", fileId, data.gun.gun_type);
                    errors++;
                }
                for (String warning : ConditionEvaluator.validateConditions(data.conditions)) {
                    LOGGER.warn("Gun modifier file {}: {}", fileId, warning);
                    errors++;
                }
                data.id = fileId;
                newGuns.put(ResourceLocation.parse(fileId), data);
                count++;
            } catch (Exception e) {
                errors++;
                LOGGER.error("Failed to load gun modifier file: {}", fileId, e);
            }
        }
        this.guns = newGuns;
        this.itemCache = new ConcurrentHashMap<>();
        this.sources = new ConcurrentHashMap<>(srcs);
        LOGGER.info("Loaded {} gun modifiers ({} errors)", count, errors);
    }

    @Override
    public String getSyncId() {
        return "gun_modifier";
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

    public List<GunItemData> getForItem(Item item) {
        if (item == null || item == Items.AIR) {
            return List.of();
        }
        try {
            ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(item);
            if (itemId == null || itemId.getPath().isEmpty()) {
                return List.of();
            }
            return itemCache.computeIfAbsent(itemId, this::computeGunModifiersForItem);
        } catch (Exception e) {
            return List.of();
        }
    }

    private List<GunItemData> computeGunModifiersForItem(ResourceLocation itemId) {
        List<GunItemData> result = new ArrayList<>();
        for (GunItemData data : guns.values()) {
            if (data == null) continue;
            if (data.gun != null && matchesTarget(data.target, itemId) && data.gun.gun_enabled) {
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

    public boolean evaluateConditions(GunItemData data, ItemStack stack) {
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
            return tagId != null && isItemInTag(itemId, tagId);
        }
        ResourceLocation loc = ResourceLocation.tryParse(pattern);
        return loc != null && loc.equals(itemId);
    }

    private boolean isItemInTag(ResourceLocation itemId, ResourceLocation tagId) {
        TagKey<Item> tagKey = TagKey.create(Registries.ITEM, tagId);
        Item item = ForgeRegistries.ITEMS.getValue(itemId);
        return item != null && new ItemStack(item).is(tagKey);
    }

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(INSTANCE);
    }
}
