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

import net.jaams.weaponry.data.GunItemData;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonElement;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GunModifierLoader extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final GunModifierLoader INSTANCE = new GunModifierLoader();
    private volatile Map<ResourceLocation, GunItemData> guns = new ConcurrentHashMap<>();
    private volatile Map<ResourceLocation, List<GunItemData>> itemCache = new ConcurrentHashMap<>();
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
        Map<ResourceLocation, GunItemData> newGuns = new ConcurrentHashMap<>();
        int count = 0;
        int errors = 0;
        for (Map.Entry<ResourceLocation, JsonElement> entry : resources.entrySet()) {
            if (entry == null || entry.getKey() == null || entry.getValue() == null) continue;
            ResourceLocation fileId = entry.getKey();
            try {
                GunItemData data = GSON.fromJson(entry.getValue(), GunItemData.class);
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
                newGuns.put(fileId, data);
                count++;
            } catch (Exception e) {
                errors++;
                LOGGER.error("Failed to load gun modifier file: {}", fileId, e);
            }
        }
        this.guns = newGuns;
        this.itemCache = new ConcurrentHashMap<>();
        LOGGER.info("Loaded {} gun modifiers ({} errors)", count, errors);
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
        result.sort((a, b) -> Integer.compare(b.priority, a.priority));
        return result;
    }

    public boolean evaluateConditions(GunItemData data, ItemStack stack) {
        if (data == null || stack == null) return false;
        if (data.conditions == null || data.conditions.isEmpty()) {
            return true;
        }
        boolean isAndMode = "and".equalsIgnoreCase(data.condition_mode);
        for (GunItemData.Condition cond : data.conditions) {
            boolean conditionMet = evaluateSingleCondition(cond, stack);
            if (isAndMode && !conditionMet)
                return false;
            if (!isAndMode && conditionMet)
                return true;
        }
        return isAndMode;
    }

    private boolean evaluateSingleCondition(GunItemData.Condition cond, ItemStack stack) {
        if (cond == null || cond.type == null)
            return false;
        return switch (cond.type.toLowerCase()) {
            case "enchantment" -> checkEnchantment(cond, stack);
            case "nbt" -> checkNBT(cond, stack);
            case "tag" -> checkTag(cond, stack);
            case "item" -> checkItem(cond, stack);
            case "mod" -> checkMod(cond, stack);
            case "rarity" -> checkRarity(cond, stack);
            default -> false;
        };
    }

    private boolean checkEnchantment(GunItemData.Condition cond, ItemStack stack) {
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

    private boolean checkNBT(GunItemData.Condition cond, ItemStack stack) {
        if (stack == null || !stack.hasTag() || cond.key == null)
            return false;
        CompoundTag tag = stack.getTag();
        return switch (cond.nbt_key != null ? cond.nbt_key.toLowerCase() : "") {
            case "boolean" -> tag.getBoolean(cond.key) == cond.nbt_boolean_value;
            case "int" -> tag.getInt(cond.key) == cond.nbt_int_value;
            case "string" -> cond.nbt_string_value != null && cond.nbt_string_value.equals(tag.getString(cond.key));
            default -> false;
        };
    }

    private boolean checkTag(GunItemData.Condition cond, ItemStack stack) {
        if (cond.tag == null || stack == null)
            return false;
        ResourceLocation tagId = ResourceLocation.tryParse(cond.tag);
        if (tagId == null)
            return false;
        TagKey<Item> tagKey = TagKey.create(Registries.ITEM, tagId);
        return stack.is(tagKey);
    }

    private boolean checkItem(GunItemData.Condition cond, ItemStack stack) {
        if (cond.item == null || stack == null)
            return false;
        ResourceLocation itemId = ResourceLocation.tryParse(cond.item);
        if (itemId == null || stack == null) return false;
        ResourceLocation stackId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        return stackId != null && stackId.equals(itemId);
    }

    private boolean checkMod(GunItemData.Condition cond, ItemStack stack) {
        if (cond.mod_id == null || stack == null)
            return false;
        ResourceLocation stackId = ForgeRegistries.ITEMS.getKey(stack.getItem());
        return stackId != null && cond.mod_id.equals(stackId.getNamespace());
    }

    private boolean checkRarity(GunItemData.Condition cond, ItemStack stack) {
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
