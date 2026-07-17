package net.jaams.weaponry.loader;

import net.jaams.weaponry.sync.NetworkSyncable;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TagsUpdatedEvent;

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
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.biome.Biome;

import net.jaams.weaponry.JaamsWeaponryMod;
import net.jaams.weaponry.data.EquipmentData;
import net.jaams.weaponry.data.EquipmentData.ItemEntry;
import net.jaams.weaponry.data.EquipmentData.EquipEntry;

import java.util.Map;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;

@Mod.EventBusSubscriber(modid = JaamsWeaponryMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EquipmentModifierLoader extends SimpleJsonResourceReloadListener implements NetworkSyncable {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final EquipmentModifierLoader INSTANCE = new EquipmentModifierLoader();
    private volatile Map<ResourceLocation, EquipmentData> modifiers = new ConcurrentHashMap<>();
    private volatile Map<ResourceLocation, List<EquipmentData>> entityCache = new ConcurrentHashMap<>();
    private volatile Map<String, String> sources = new ConcurrentHashMap<>();
    private static final Logger LOGGER = LogManager.getLogger(EquipmentModifierLoader.class);

    private EquipmentModifierLoader() {
        super(GSON, "jaams/equipment_modifier");
    }

    

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager rm, ProfilerFiller prof) {
        if (resources == null) { LOGGER.warn("EquipmentModifierLoader apply called with null resources"); return; }
        Map<String, String> srcs = new ConcurrentHashMap<>();
        for (var entry : resources.entrySet()) {
            if (entry == null || entry.getKey() == null || entry.getValue() == null) continue;
            try {
                srcs.put(entry.getKey().toString(), GSON.toJson(entry.getValue()));
            } catch (Exception ignored) {
            }
        }
        rebuild(srcs);
    }

    private void rebuild(Map<String, String> srcs) {
        Map<ResourceLocation, EquipmentData> map = new ConcurrentHashMap<>();
        int count = 0, errors = 0;
        for (var entry : srcs.entrySet()) {
            String id = entry.getKey();
            if (!JaamsWeaponryMod.isOwnNamespace(id)) {
                continue;
            }
            try {
                EquipmentData data = GSON.fromJson(com.google.gson.JsonParser.parseString(entry.getValue()), EquipmentData.class);
                if (data == null) { LOGGER.warn("Equipment modifier {} returned null", id); errors++; continue; }
                if (data.enabled != null && !data.enabled) { continue; }
                if (data.entity == null || data.entity.isEmpty()) { LOGGER.warn("Equipment modifier {} has no 'entity'", id); errors++; continue; }
                if (data.items == null || data.items.isEmpty()) { LOGGER.warn("Equipment modifier {} has no 'items'", id); errors++; continue; }
                boolean bad = false;
                for (ItemEntry ie : data.items) {
                    if (ie.item == null || ie.item.isEmpty()) { LOGGER.warn("Equipment modifier {} has item entry with no 'item'", id); bad = true; break; }
                }
                if (bad) { errors++; continue; }
                map.put(ResourceLocation.parse(id), data);
                count++;
            } catch (Exception e) { errors++; LOGGER.error("Failed to load equipment modifier: {}", id, e); }
        }
        this.modifiers = map;
        this.entityCache = new ConcurrentHashMap<>();
        this.sources = new ConcurrentHashMap<>(srcs);
        LOGGER.info("Loaded {} equipment modifiers ({} errors)", count, errors);
    }

    @Override
    public String getSyncId() {
        return "equipment_modifier";
    }

    @Override
    public Map<String, String> getSourcesSnapshot() {
        return new HashMap<>(sources);
    }

    @Override
    public void applyNetworkSync(Map<String, String> srcs) {
        if (srcs == null) return;
        rebuild(srcs);
    }

    @SubscribeEvent
    public static void onTagsUpdated(TagsUpdatedEvent event) {
        INSTANCE.entityCache.clear();
    }

    

    public List<EquipmentData> getForEntityType(EntityType<?> type) {
        if (type == null) return List.of();
        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(type);
        if (id == null) return List.of();
        return entityCache.computeIfAbsent(id, this::computeForEntity);
    }

    private List<EquipmentData> computeForEntity(ResourceLocation entityId) {
        List<EquipmentData> result = new ArrayList<>();
        for (EquipmentData data : modifiers.values()) {
            if (data != null && matchesEntity(data.entity, entityId)) result.add(data);
        }
        result.sort((a, b) -> Integer.compare(b.priority, a.priority));
        return result;
    }

    

    public java.util.Optional<ItemStack> resolveItem(ItemEntry entry) {
        if (entry == null || entry.item == null || entry.item.isEmpty()) return java.util.Optional.empty();
        return resolveItem(entry.item, entry.enchantments, entry.random_enchantments, entry.nbt, entry.count);
    }

    public java.util.Optional<ItemStack> resolveEquipItem(EquipEntry entry) {
        if (entry == null || entry.item == null || entry.item.isEmpty()) return java.util.Optional.empty();
        return resolveItem(entry.item, entry.enchantments, entry.random_enchantments, entry.nbt, entry.count);
    }

    private java.util.Optional<ItemStack> resolveItem(String itemPattern, Map<String, Integer> enchantments,
            boolean randomEnch, Map<String, Object> nbt, int count) {
        ResourceLocation itemId = resolveItemPattern(itemPattern);
        if (itemId == null) return java.util.Optional.empty();
        Item item = ForgeRegistries.ITEMS.getValue(itemId);
        if (item == null || item == Items.AIR) return java.util.Optional.empty();
        ItemStack stack = new ItemStack(item, Math.max(count, 1));
        applyEnchantments(stack, enchantments, randomEnch);
        applyNbt(stack, nbt);
        return java.util.Optional.of(stack);
    }

    private ResourceLocation resolveItemPattern(String pattern) {
        if (pattern == null || pattern.isEmpty()) return null;
        if (pattern.startsWith("#")) {
            ResourceLocation tagId = ResourceLocation.tryParse(pattern.substring(1));
            if (tagId == null) return null;
            TagKey<Item> tagKey = TagKey.create(Registries.ITEM, tagId);
            for (Item item : ForgeRegistries.ITEMS.getValues()) {
                ResourceLocation loc = ForgeRegistries.ITEMS.getKey(item);
                if (loc != null && new ItemStack(item).is(tagKey)) return loc;
            }
            return null;
        }
        if (pattern.contains("*")) {
            String regex = "^" + pattern.replace("*", ".*") + "$";
            for (Item item : ForgeRegistries.ITEMS.getValues()) {
                ResourceLocation loc = ForgeRegistries.ITEMS.getKey(item);
                if (loc != null && loc.toString().matches(regex)) return loc;
            }
            return null;
        }
        return ResourceLocation.tryParse(pattern);
    }

    private void applyEnchantments(ItemStack stack, Map<String, Integer> enchantments, boolean random) {
        if (enchantments == null || enchantments.isEmpty()) return;
        for (var e : enchantments.entrySet()) {
            ResourceLocation eid = ResourceLocation.tryParse(e.getKey());
            if (eid == null) continue;
            Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(eid);
            if (ench == null) continue;
            int level = Math.min(e.getValue(), ench.getMaxLevel());
            if (level < 1) continue;
            int finalLevel = random ? net.minecraft.util.RandomSource.create().nextInt(level) + 1 : level;
            stack.enchant(ench, finalLevel);
        }
    }

    private void applyNbt(ItemStack stack, Map<String, Object> nbt) {
        if (nbt == null || nbt.isEmpty()) return;
        CompoundTag tag = stack.getOrCreateTag();
        for (var e : nbt.entrySet()) {
            if (e.getKey() == null || e.getValue() == null) continue;
            tag.put(e.getKey(), convertToTag(e.getValue()));
        }
        stack.setTag(tag);
    }

    private net.minecraft.nbt.Tag convertToTag(Object value) {
        if (value instanceof Number num) {
            double d = num.doubleValue();
            if (d == Math.floor(d) && d >= Integer.MIN_VALUE && d <= Integer.MAX_VALUE) return IntTag.valueOf(num.intValue());
            return DoubleTag.valueOf(d);
        } else if (value instanceof Boolean bool) {
            return ByteTag.valueOf(bool ? (byte) 1 : (byte) 0);
        } else if (value instanceof String str) {
            return StringTag.valueOf(str);
        } else if (value instanceof JsonObject obj) {
            CompoundTag c = new CompoundTag();
            for (var e : obj.entrySet()) {
                if (e.getValue().isJsonPrimitive()) {
                    JsonPrimitive p = e.getValue().getAsJsonPrimitive();
                    if (p.isBoolean()) c.putBoolean(e.getKey(), p.getAsBoolean());
                    else if (p.isNumber()) c.putDouble(e.getKey(), p.getAsDouble());
                    else if (p.isString()) c.putString(e.getKey(), p.getAsString());
                } else if (e.getValue().isJsonObject()) {
                    c.put(e.getKey(), convertToTag(e.getValue().getAsJsonObject()));
                }
            }
            return c;
        }
        return StringTag.valueOf(String.valueOf(value));
    }

    

    public boolean matchesEntity(List<String> patterns, ResourceLocation entityId) {
        if (patterns == null || patterns.isEmpty()) return false;
        String entityStr = entityId.toString();
        boolean anyPositive = false;
        for (String raw : patterns) {
            boolean negate = raw.startsWith("!");
            String p = negate ? raw.substring(1) : raw;
            boolean matches = evalEntityPattern(p, entityStr, entityId);
            if (negate) { if (matches) return false; }
            else if (matches) anyPositive = true;
        }
        return anyPositive;
    }

    private boolean evalEntityPattern(String pattern, String entityStr, ResourceLocation entityId) {
        if (pattern.startsWith("#")) {
            ResourceLocation tagId = ResourceLocation.tryParse(pattern.substring(1));
            if (tagId == null) return false;
            TagKey<EntityType<?>> tagKey = TagKey.create(Registries.ENTITY_TYPE, tagId);
            EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(entityId);
            return type != null && type.is(tagKey);
        }
        if (pattern.contains("*")) return entityStr.matches("^" + pattern.replace("*", ".*") + "$");
        ResourceLocation loc = ResourceLocation.tryParse(pattern);
        return loc != null && loc.equals(entityId);
    }

    

    public boolean evaluateItemConditions(List<EquipmentData.ItemCondition> conditions, String mode, ItemStack stack) {
        if (conditions == null || conditions.isEmpty()) return true;
        boolean and = "and".equalsIgnoreCase(mode);
        for (var cond : conditions) {
            boolean met = evalItemCondition(cond, stack);
            if (and && !met) return false;
            if (!and && met) return true;
        }
        return and;
    }

    private boolean evalItemCondition(EquipmentData.ItemCondition cond, ItemStack stack) {
        if (cond == null || cond.type == null) return false;
        boolean result;
        switch (cond.type.toLowerCase(Locale.ROOT)) {
            case "enchantment": {
                if (cond.enchantment == null || stack == null) { result = false; break; }
                ResourceLocation eid = ResourceLocation.tryParse(cond.enchantment);
                if (eid == null) { result = false; break; }
                Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(eid);
                if (ench == null) { result = false; break; }
                result = EnchantmentHelper.getTagEnchantmentLevel(ench, stack) >= cond.level;
                break;
            }
            case "nbt": {
                if (cond.key == null || stack == null || !stack.hasTag()) { result = false; break; }
                CompoundTag tag = stack.getTag();
                boolean nbtResult;
                switch (cond.nbt_key != null ? cond.nbt_key.toLowerCase(Locale.ROOT) : "") {
                    case "boolean": nbtResult = tag.getBoolean(cond.key) == cond.nbt_boolean_value; break;
                    case "int": nbtResult = tag.getInt(cond.key) == cond.nbt_int_value; break;
                    case "string": nbtResult = cond.nbt_string_value != null && cond.nbt_string_value.equals(tag.getString(cond.key)); break;
                    default: nbtResult = false; break;
                }
                result = nbtResult;
                break;
            }
            case "tag": {
                if (cond.tag == null || stack == null) { result = false; break; }
                ResourceLocation tid = ResourceLocation.tryParse(cond.tag);
                result = tid != null && stack.is(TagKey.create(Registries.ITEM, tid));
                break;
            }
            case "item": {
                if (cond.item == null || stack == null) { result = false; break; }
                ResourceLocation iid = ResourceLocation.tryParse(cond.item);
                ResourceLocation sid = ForgeRegistries.ITEMS.getKey(stack.getItem());
                result = iid != null && sid != null && sid.equals(iid);
                break;
            }
            case "mod": {
                if (cond.mod_id == null || stack == null) { result = false; break; }
                ResourceLocation sid = ForgeRegistries.ITEMS.getKey(stack.getItem());
                result = sid != null && cond.mod_id.equals(sid.getNamespace());
                break;
            }
            case "rarity": result = cond.rarity != null && stack != null && stack.getRarity().name().equalsIgnoreCase(cond.rarity); break;
            default: result = false; break;
        }
        return result;
    }

    

    public boolean evaluateEntityConditions(EquipmentData data, Mob mob) {
        if (data == null || mob == null) return false;
        if (data.conditions == null || data.conditions.isEmpty()) return true;
        boolean and = "and".equalsIgnoreCase(data.condition_mode);
        for (var cond : data.conditions) {
            boolean met = evalEntityCondition(cond, mob) ^ cond.negate;
            if (and && !met) return false;
            if (!and && met) return true;
        }
        return and;
    }

    private boolean evalEntityCondition(EquipmentData.EntityCondition cond, Mob mob) {
        if (cond == null || cond.type == null) return false;
        boolean result;
        switch (cond.type.toLowerCase(Locale.ROOT)) {
            case "difficulty": {
                if (!(mob.level() instanceof ServerLevel sl)) { result = false; break; }
                String cur = sl.getCurrentDifficultyAt(mob.blockPosition()).getDifficulty().name().toLowerCase(Locale.ROOT);
                result = cur.equals(cond.difficulty.toLowerCase(Locale.ROOT));
                break;
            }
            case "biome": {
                if (cond.biome == null || !(mob.level() instanceof ServerLevel sl)) { result = false; break; }
                var holder = sl.getBiome(mob.blockPosition());
                ResourceLocation key = holder.unwrapKey().map(k -> k.location()).orElse(null);
                if (key == null) { result = false; break; }
                if (cond.biome.startsWith("#")) {
                    ResourceLocation tid = ResourceLocation.tryParse(cond.biome.substring(1));
                    result = tid != null && holder.is(TagKey.create(Registries.BIOME, tid));
                    break;
                }
                ResourceLocation bid = ResourceLocation.tryParse(cond.biome);
                result = bid != null && bid.equals(key);
                break;
            }
            case "time_of_day": { long t = mob.level().getDayTime() % 24000; result = t >= cond.time_min && t <= cond.time_max; break; }
            case "light_level": { int l = mob.level().getMaxLocalRawBrightness(mob.blockPosition()); result = l >= cond.light_level_min && l <= cond.light_level_max; break; }
            case "mod_loaded": result = cond.mod_id != null && net.minecraftforge.fml.ModList.get().isLoaded(cond.mod_id); break;
            case "entity_type": {
                if (cond.entity_type == null) { result = false; break; }
                ResourceLocation mid = ForgeRegistries.ENTITY_TYPES.getKey(mob.getType());
                if (mid == null) { result = false; break; }
                if (cond.entity_type.startsWith("#")) {
                    ResourceLocation tid = ResourceLocation.tryParse(cond.entity_type.substring(1));
                    result = tid != null && mob.getType().is(TagKey.create(Registries.ENTITY_TYPE, tid));
                    break;
                }
                ResourceLocation target = ResourceLocation.tryParse(cond.entity_type);
                result = target != null && target.equals(mid);
                break;
            }
            case "on_fire": result = mob.isOnFire(); break;
            case "health_below": result = mob.getHealth() < cond.health_value; break;
            case "health_above": result = mob.getHealth() > cond.health_value; break;
            case "has_effect": {
                if (cond.effect == null) { result = false; break; }
                ResourceLocation eid = ResourceLocation.tryParse(cond.effect);
                if (eid == null) { result = false; break; }
                var eff = ForgeRegistries.MOB_EFFECTS.getValue(eid);
                result = eff != null && mob.hasEffect(eff);
                break;
            }
            case "in_water": result = mob.isInWater(); break;
            case "in_block": result = mob.isInWall(); break;
            case "is_baby": {
                if (mob instanceof AgeableMob ageable) { result = ageable.isBaby(); break; }
                if (mob instanceof Zombie zombie) { result = zombie.isBaby(); break; }
                if (mob instanceof net.minecraft.world.entity.monster.piglin.Piglin piglin) { result = piglin.isBaby(); break; }
                result = false;
                break;
            }
            case "moon_phase": { int p = mob.level().getMoonPhase(); result = p >= cond.moon_phase_min && p <= cond.moon_phase_max; break; }
            case "distance_to_player_below": result = distToPlayer(mob) <= cond.distance_value; break;
            case "distance_to_player_above": result = distToPlayer(mob) >= cond.distance_value; break;
            case "structure": {
                if (cond.structure == null || !(mob.level() instanceof ServerLevel sl)) { result = false; break; }
                ResourceLocation sid = ResourceLocation.tryParse(cond.structure);
                if (sid == null) { result = false; break; }
                var chunk = sl.getChunk(mob.blockPosition());
                boolean found = false;
                for (var ref : chunk.getAllReferences().entrySet()) {
                    ResourceLocation key = sl.registryAccess().registryOrThrow(Registries.STRUCTURE).getKey(ref.getKey());
                    if (sid.equals(key)) { found = true; break; }
                }
                result = found;
                break;
            }
            default: result = false; break;
        }
        return result;
    }

    private double distToPlayer(Mob mob) {
        if (!(mob.level() instanceof ServerLevel sl)) return Double.MAX_VALUE;
        double min = Double.MAX_VALUE;
        for (var player : sl.players()) {
            double d = mob.distanceTo(player);
            if (d < min) min = d;
        }
        return min;
    }

    
    
    

    
    public boolean applySpawnEquipment(Mob mob, net.minecraft.util.RandomSource random) {
        List<EquipmentData> mods = getForEntityType(mob.getType());
        if (mods.isEmpty()) return false;

        boolean anyApplied = false;
        for (EquipmentData data : mods) {
            try {
                if (!evaluateEntityConditions(data, mob)) continue;

                
                if (data.global_chance < 1.0 && random.nextDouble() >= data.global_chance) continue;

                for (ItemEntry entry : data.items) {
                    if (entry.chance < 1.0 && random.nextDouble() >= entry.chance) continue;

                    
                    java.util.Optional<ItemStack> itemOpt = resolveItem(entry);
                    if (itemOpt.isEmpty()) continue;

                    ItemStack stack = itemOpt.get();
                    if (!evaluateItemConditions(entry.conditions, data.condition_mode, stack)) continue;

                    net.minecraft.world.entity.EquipmentSlot slot = entry.getEquipmentSlot();
                    if (!entry.replace_existing && !mob.getItemBySlot(slot).isEmpty()) continue;

                    mob.setItemSlot(slot, stack);
                    mob.setDropChance(slot, 0.0F);
                    anyApplied = true;

                    
                    if (entry.equipment != null) {
                        for (var equip : entry.equipment) {
                            try {
                                java.util.Optional<ItemStack> equipOpt = resolveEquipItem(equip);
                                if (equipOpt.isEmpty()) continue;

                                ItemStack equipStack = equipOpt.get();
                                if (!evaluateItemConditions(equip.conditions, data.condition_mode, equipStack)) continue;

                                net.minecraft.world.entity.EquipmentSlot equipSlot = equip.getEquipmentSlot();
                                if (!equip.replace_existing && !mob.getItemBySlot(equipSlot).isEmpty()) continue;

                                mob.setItemSlot(equipSlot, equipStack);
                                mob.setDropChance(equipSlot, 0.0F);
                            } catch (Exception e) {
                                LOGGER.error("Error applying dual wield equipment to {}", mob.getType().toShortString(), e);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Error applying spawn equipment to {}", mob.getType().toShortString(), e);
            }
        }
        return anyApplied;
    }

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(INSTANCE);
    }
}
