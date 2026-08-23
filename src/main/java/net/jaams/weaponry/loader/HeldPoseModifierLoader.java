package net.jaams.weaponry.loader;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.AddReloadListenerEvent;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.tags.TagKey;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.registries.ForgeRegistries;

import net.jaams.weaponry.JaamsWeaponryMod;
import net.jaams.weaponry.data.HeldPoseData;
import net.jaams.weaponry.data.GunItemData;
import net.jaams.weaponry.gun.helper.GunShootHelper;
import net.jaams.weaponry.util.ModGuns;
import net.jaams.weaponry.configuration.common.GunSystemCommonConfig;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonElement;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;

@Mod.EventBusSubscriber(modid = JaamsWeaponryMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HeldPoseModifierLoader extends SimpleJsonResourceReloadListener implements NetworkSyncable {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final HeldPoseModifierLoader INSTANCE = new HeldPoseModifierLoader();
    private volatile Map<ResourceLocation, HeldPoseData> poses = new ConcurrentHashMap<>();
    private volatile List<HeldPoseData> sortedCache = List.of();
    private volatile Map<String, String> poseSources = new ConcurrentHashMap<>();
    private static final Logger LOGGER = LogManager.getLogger(HeldPoseModifierLoader.class);

    private HeldPoseModifierLoader() {
        super(GSON, "jaams/pose_modifier");
    }

    @Override
    public String getSyncId() {
        return "held_pose";
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager resourceManager,
            ProfilerFiller profiler) {
        if (resources == null) {
            LOGGER.warn("HeldPoseModifierLoader apply called with null resources");
            return;
        }
        Map<String, String> sources = new ConcurrentHashMap<>();
        for (Map.Entry<ResourceLocation, JsonElement> entry : resources.entrySet()) {
            if (entry == null || entry.getKey() == null || entry.getValue() == null)
                continue;
            try {
                sources.put(entry.getKey().toString(), GSON.toJson(entry.getValue()));
            } catch (Exception ignored) {
            }
        }
        rebuild(sources);
    }

    
    public void applyNetworkSync(Map<String, String> sources) {
        if (sources == null)
            return;
        rebuild(sources);
    }

    public Map<String, String> getSourcesSnapshot() {
        return new HashMap<>(poseSources);
    }

    private void rebuild(Map<String, String> sources) {
        Map<ResourceLocation, HeldPoseData> newPoses = new ConcurrentHashMap<>();
        int count = 0;
        int errors = 0;
        for (Map.Entry<String, String> entry : sources.entrySet()) {
            String fileId = entry.getKey();
            if (!JaamsWeaponryMod.isOwnNamespace(fileId)) {
                continue;
            }
            try {
                JsonElement element = com.google.gson.JsonParser.parseString(entry.getValue());
                HeldPoseData data = GSON.fromJson(element, HeldPoseData.class);
                if (data == null) {
                    LOGGER.warn("Held pose file {} returned null data", fileId);
                    errors++;
                    continue;
                }
                if (data.target == null || data.target.isEmpty()) {
                    errors++;
                    LOGGER.warn("Held pose file {} has no target defined", fileId);
                    continue;
                }
                if (data.pose == null || data.pose.isEmpty()) {
                    errors++;
                    LOGGER.warn("Held pose file {} has no pose defined", fileId);
                    continue;
                }
                if (data.enabled != null && !data.enabled) {
                    LOGGER.info("Held pose file {} is disabled, skipping", fileId);
                    continue;
                }
                newPoses.put(new ResourceLocation(fileId), data);
                count++;
            } catch (Exception e) {
                errors++;
                LOGGER.error("Failed to load held pose file: {}", fileId, e);
            }
        }

        List<HeldPoseData> sorted = new ArrayList<>(newPoses.values());
        sorted.sort((a, b) -> Integer.compare(b.priority, a.priority));
        this.poses = newPoses;
        this.sortedCache = sorted;
        this.poseSources = new ConcurrentHashMap<>(sources);
        LOGGER.info("Loaded {} held pose entries ({} errors)", count, errors);
    }


    public List<HeldPoseData> getAll() {
        return sortedCache;
    }


    public HeldPoseData getForItem(Item item) {
        if (item == null || item == Items.AIR) {
            return null;
        }
        try {
            ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(item);
            if (itemId == null || itemId.getPath().isEmpty()) {
                return null;
            }
            for (HeldPoseData data : sortedCache) {
                if (data != null && matchesTarget(data.target, itemId)) {
                    return data;
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public boolean matchesTarget(List<String> targets, ResourceLocation itemId) {
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
        Item item = ForgeRegistries.ITEMS.getValue(itemId);
        return item != null && new ItemStack(item).is(tagKey);
    }

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(INSTANCE);
    }

    
    
    

    public boolean evaluateConditions(HeldPoseData data, ItemStack stack, Player player) {
        if (data == null || stack == null) return false;
        if (data.conditions == null || data.conditions.isEmpty()) {
            return true;
        }
        boolean isAndMode = "and".equalsIgnoreCase(data.condition_mode);
        for (HeldPoseData.Condition cond : data.conditions) {
            boolean conditionMet = evaluateSingleCondition(cond, stack, player);
            if (isAndMode && !conditionMet)
                return false;
            if (!isAndMode && conditionMet)
                return true;
        }
        return isAndMode;
    }

    private boolean evaluateSingleCondition(HeldPoseData.Condition cond, ItemStack stack, Player player) {
        if (cond == null || cond.type == null)
            return false;
        return switch (cond.type.toLowerCase()) {
            case "enchantment" -> checkEnchantment(cond, stack);
            case "tag" -> checkTag(cond, stack);
            case "has_gun_ammo" -> checkHasGunAmmo(stack, player);
            case "is_on_cooldown" -> checkIsOnCooldown(stack, player);
            case "is_using_item" -> checkIsUsingItem(player);
            case "is_not_using_item" -> !checkIsUsingItem(player);
            default -> false;
        };
    }

    private boolean checkEnchantment(HeldPoseData.Condition cond, ItemStack stack) {
        if (stack == null || cond.enchantment == null)
            return false;
        ResourceLocation enchId = ResourceLocation.tryParse(cond.enchantment);
        if (enchId == null)
            return false;
        net.minecraft.world.item.enchantment.Enchantment enchantment =
                ForgeRegistries.ENCHANTMENTS.getValue(enchId);
        if (enchantment == null)
            return false;
        int level = net.minecraft.world.item.enchantment.EnchantmentHelper
                .getTagEnchantmentLevel(enchantment, stack);
        return level >= cond.level;
    }

    private boolean checkTag(HeldPoseData.Condition cond, ItemStack stack) {
        if (cond.tag == null || stack == null)
            return false;
        ResourceLocation tagId = ResourceLocation.tryParse(cond.tag);
        if (tagId == null)
            return false;
        TagKey<Item> tagKey = TagKey.create(Registries.ITEM, tagId);
        return stack.is(tagKey);
    }

    
    private boolean checkHasGunAmmo(ItemStack stack, Player player) {
        if (stack == null || player == null || stack.isEmpty())
            return false;
        if (player.isCreative())
            return true;
        ModGuns.GunType gunType = ModGuns.getGunType(stack);
        if (gunType == null)
            return false;
        int ammoConsumption = getFinalAmmoConsumption(stack, gunType);
        GunItemData.GunEntry gunData = GunItemData.getGunData(stack);
        boolean useGunAmmo = GunShootHelper.getFinalAmmoSource(stack, "GunAmmoFromGun",
                GunSystemCommonConfig.GUN_AMMO_FROM_GUN::get,
                gunData != null ? gunData.ammo_from_gun : null);
        boolean useHandAmmo = GunShootHelper.getFinalAmmoSource(stack, "GunAmmoFromHand",
                GunSystemCommonConfig.GUN_AMMO_FROM_HAND::get,
                gunData != null ? gunData.ammo_from_hand : null);
        boolean useInventoryAmmo = GunShootHelper.getFinalAmmoSource(stack, "GunAmmoFromPlayerInventory",
                GunSystemCommonConfig.GUN_AMMO_FROM_PLAYER_INVENTORY::get,
                gunData != null ? gunData.ammo_from_player_inventory : null);
        GunShootHelper.SourceResult source = GunShootHelper.getPreferredSourceWithPriority(
                player, stack, useGunAmmo, useHandAmmo, useInventoryAmmo,
                ammoConsumption, gunType);
        return source.hasEnough();
    }

    private int getFinalAmmoConsumption(ItemStack gunStack, ModGuns.GunType type) {
        int nbtValue = net.jaams.weaponry.util.ModUtils.getConfigOrNbtInt(
                gunStack, "GunAmmoConsumption", () -> 0);
        if (nbtValue > 0)
            return nbtValue;
        GunItemData.ShootEntry shootData = GunItemData.getShootData(gunStack);
        if (shootData != null && shootData.ammo_consumption > 0)
            return shootData.ammo_consumption;
        return switch (type) {
            case PISTOL -> GunSystemCommonConfig.GUN_PISTOL_SHOOT_AMMO_CONSUMPTION.get();
            case SCATTERGUN -> GunSystemCommonConfig.GUN_SCATTERGUN_SHOOT_AMMO_CONSUMPTION.get();
            case SHOTGUN -> GunSystemCommonConfig.GUN_SHOTGUN_SHOOT_AMMO_CONSUMPTION.get();
            default -> GunSystemCommonConfig.GUN_PISTOL_SHOOT_AMMO_CONSUMPTION.get();
        };
    }

    
    private boolean checkIsOnCooldown(ItemStack stack, Player player) {
        if (stack == null || player == null || stack.isEmpty())
            return false;
        return player.getCooldowns().isOnCooldown(stack.getItem());
    }

    
    private boolean checkIsUsingItem(Player player) {
        if (player == null)
            return false;
        return player.isUsingItem();
    }
}
