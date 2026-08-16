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

import net.jaams.weaponry.data.EquipmentData;
import net.jaams.weaponry.data.EquipmentData.ItemEntry;
import net.jaams.weaponry.data.EquipmentData.EquipEntry;

import java.util.Map;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;

@EventBusSubscriber
public class EquipmentModifierLoader extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final EquipmentModifierLoader INSTANCE = new EquipmentModifierLoader();
    private volatile Map<ResourceLocation, EquipmentData> modifiers = new ConcurrentHashMap<>();
    private volatile Map<ResourceLocation, List<EquipmentData>> entityCache = new ConcurrentHashMap<>();
    private static final Logger LOGGER = LogManager.getLogger(EquipmentModifierLoader.class);

    private EquipmentModifierLoader() {
        super(GSON, "jaams/equipment_modifier");
    }

    

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager rm, ProfilerFiller prof) {
        if (resources == null) { LOGGER.warn("EquipmentModifierLoader apply called with null resources"); return; }
        Map<ResourceLocation, EquipmentData> map = new ConcurrentHashMap<>();
        int count = 0, errors = 0;
        for (var entry : resources.entrySet()) {
            if (entry == null || entry.getKey() == null || entry.getValue() == null) continue;
            ResourceLocation id = entry.getKey();
            try {
                EquipmentData data = GSON.fromJson(entry.getValue(), EquipmentData.class);
                if (data == null) { LOGGER.warn("Equipment modifier {} returned null", id); errors++; continue; }
                if (data.entity == null || data.entity.isEmpty()) { LOGGER.warn("Equipment modifier {} has no 'entity'", id); errors++; continue; }
                if (data.items == null || data.items.isEmpty()) { LOGGER.warn("Equipment modifier {} has no 'items'", id); errors++; continue; }
                if (data.enabled != null && !data.enabled) { LOGGER.info("Equipment modifier {} is disabled, skipping", id); continue; }
                boolean bad = false;
                for (ItemEntry ie : data.items) {
                    if (ie.item == null || ie.item.isEmpty()) { LOGGER.warn("Equipment modifier {} has item entry with no 'item'", id); bad = true; break; }
                }
                if (bad) { errors++; continue; }
                map.put(id, data);
                count++;
            } catch (Exception e) { errors++; LOGGER.error("Failed to load equipment modifier: {}", id, e); }
        }
        this.modifiers = map;
        this.entityCache = new ConcurrentHashMap<>();
        LOGGER.info("Loaded {} equipment modifiers ({} errors)", count, errors);
    }

    

    public List<EquipmentData> getForEntityType(EntityType<?> type) {
        if (type == null) return List.of();
        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(type);
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
        return resolveItem(entry.item, entry.enchantments, entry.random_enchantments, entry.nbt, entry.components, entry.count);
    }

    public java.util.Optional<ItemStack> resolveEquipItem(EquipEntry entry) {
        if (entry == null || entry.item == null || entry.item.isEmpty()) return java.util.Optional.empty();
        return resolveItem(entry.item, entry.enchantments, entry.random_enchantments, entry.nbt, entry.components, entry.count);
    }

    private java.util.Optional<ItemStack> resolveItem(String itemPattern, Map<String, Integer> enchantments,
            boolean randomEnch, Map<String, Object> nbt, Map<String, JsonElement> components, int count) {
        ResourceLocation itemId = resolveItemPattern(itemPattern);
        if (itemId == null) return java.util.Optional.empty();
        Item item = BuiltInRegistries.ITEM.get(itemId);
        if (item == null || item == Items.AIR) return java.util.Optional.empty();
        ItemStack stack = new ItemStack(item, Math.max(count, 1));
        applyEnchantments(stack, enchantments, randomEnch);
        applyNbt(stack, nbt);
        ModComponents.applyComponents(stack, components);
        return java.util.Optional.of(stack);
    }

    private ResourceLocation resolveItemPattern(String pattern) {
        if (pattern == null || pattern.isEmpty()) return null;
        if (pattern.startsWith("#")) {
            ResourceLocation tagId = ResourceLocation.tryParse(pattern.substring(1));
            if (tagId == null) return null;
            TagKey<Item> tagKey = TagKey.create(Registries.ITEM, tagId);
            for (Item item : BuiltInRegistries.ITEM) {
                ResourceLocation loc = BuiltInRegistries.ITEM.getKey(item);
                if (loc != null && new ItemStack(item).is(tagKey)) return loc;
            }
            return null;
        }
        if (pattern.contains("*")) {
            String regex = "^" + pattern.replace("*", ".*") + "$";
            for (Item item : BuiltInRegistries.ITEM) {
                ResourceLocation loc = BuiltInRegistries.ITEM.getKey(item);
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
            net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> ench = net.jaams.weaponry.init.ModEnchantments.holderFromId(eid);
            if (ench == null) continue;
            int level = Math.min(e.getValue(), ench.value().getMaxLevel());
            if (level < 1) continue;
            int finalLevel = random ? net.minecraft.util.RandomSource.create().nextInt(level) + 1 : level;
            stack.enchant(ench, finalLevel);
        }
    }

    private void applyNbt(ItemStack stack, Map<String, Object> nbt) {
        if (nbt == null || nbt.isEmpty()) return;
        CompoundTag tag = ModComponents.getOrCreate(stack);
        for (var e : nbt.entrySet()) {
            if (e.getKey() == null || e.getValue() == null) continue;
            tag.put(e.getKey(), convertToTag(e.getValue()));
        }
        ModComponents.set(stack, tag);
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
            EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(entityId);
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
        return switch (cond.type.toLowerCase(Locale.ROOT)) {
            case "enchantment" -> {
                if (cond.enchantment == null || stack == null) yield false;
                ResourceLocation eid = ResourceLocation.tryParse(cond.enchantment);
                if (eid == null) yield false;
                net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> ench = net.jaams.weaponry.init.ModEnchantments.holderFromId(eid);
                if (ench == null) yield false;
                yield EnchantmentHelper.getTagEnchantmentLevel(ench, stack) >= cond.level;
            }
            case "nbt" -> {
                if (cond.key == null || stack == null || !ModComponents.has(stack)) yield false;
                CompoundTag tag = ModComponents.get(stack);
                if (tag == null) yield false;
                yield switch (cond.nbt_key != null ? cond.nbt_key.toLowerCase(Locale.ROOT) : "") {
                    case "boolean" -> tag.contains(cond.key, net.minecraft.nbt.Tag.TAG_BYTE)
                            && tag.getBoolean(cond.key) == cond.nbt_boolean_value;
                    case "int" -> tag.contains(cond.key, net.minecraft.nbt.Tag.TAG_INT)
                            && tag.getInt(cond.key) == cond.nbt_int_value;
                    case "short" -> tag.contains(cond.key, net.minecraft.nbt.Tag.TAG_SHORT)
                            && tag.getShort(cond.key) == cond.nbt_short_value;
                    case "long" -> tag.contains(cond.key, net.minecraft.nbt.Tag.TAG_LONG)
                            && tag.getLong(cond.key) == cond.nbt_long_value;
                    case "string" -> tag.contains(cond.key, net.minecraft.nbt.Tag.TAG_STRING)
                            && cond.nbt_string_value != null
                            && cond.nbt_string_value.equals(tag.getString(cond.key));
                    default -> false;
                };
            }
            case "tag" -> {
                if (cond.tag == null || stack == null) yield false;
                ResourceLocation tid = ResourceLocation.tryParse(cond.tag);
                yield tid != null && stack.is(TagKey.create(Registries.ITEM, tid));
            }
            case "item" -> {
                if (cond.item == null || stack == null) yield false;
                ResourceLocation iid = ResourceLocation.tryParse(cond.item);
                ResourceLocation sid = BuiltInRegistries.ITEM.getKey(stack.getItem());
                yield iid != null && sid != null && sid.equals(iid);
            }
            case "mod" -> {
                if (cond.mod_id == null || stack == null) yield false;
                ResourceLocation sid = BuiltInRegistries.ITEM.getKey(stack.getItem());
                yield sid != null && cond.mod_id.equals(sid.getNamespace());
            }
            case "rarity" -> cond.rarity != null && stack != null && stack.getRarity().name().equalsIgnoreCase(cond.rarity);
            case "has_component" -> cond.component != null && ModComponents.hasComponent(stack, cond.component);
            case "component_value" -> ModComponents.componentValueMatches(stack, cond.component, cond.component_value);
            default -> false;
        };
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
        return switch (cond.type.toLowerCase(Locale.ROOT)) {
            case "difficulty" -> {
                if (!(mob.level() instanceof ServerLevel sl)) yield false;
                String cur = sl.getCurrentDifficultyAt(mob.blockPosition()).getDifficulty().name().toLowerCase(Locale.ROOT);
                yield cur.equals(cond.difficulty.toLowerCase(Locale.ROOT));
            }
            case "biome" -> {
                if (cond.biome == null || !(mob.level() instanceof ServerLevel sl)) yield false;
                var holder = sl.getBiome(mob.blockPosition());
                ResourceLocation key = holder.unwrapKey().map(k -> k.location()).orElse(null);
                if (key == null) yield false;
                if (cond.biome.startsWith("#")) {
                    ResourceLocation tid = ResourceLocation.tryParse(cond.biome.substring(1));
                    yield tid != null && holder.is(TagKey.create(Registries.BIOME, tid));
                }
                ResourceLocation bid = ResourceLocation.tryParse(cond.biome);
                yield bid != null && bid.equals(key);
            }
            case "time_of_day" -> { long t = mob.level().getDayTime() % 24000; yield t >= cond.time_min && t <= cond.time_max; }
            case "light_level" -> { int l = mob.level().getMaxLocalRawBrightness(mob.blockPosition()); yield l >= cond.light_level_min && l <= cond.light_level_max; }
            case "mod_loaded" -> cond.mod_id != null && net.neoforged.fml.ModList.get().isLoaded(cond.mod_id);
            case "entity_type" -> {
                if (cond.entity_type == null) yield false;
                ResourceLocation mid = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType());
                if (mid == null) yield false;
                if (cond.entity_type.startsWith("#")) {
                    ResourceLocation tid = ResourceLocation.tryParse(cond.entity_type.substring(1));
                    yield tid != null && mob.getType().is(TagKey.create(Registries.ENTITY_TYPE, tid));
                }
                ResourceLocation target = ResourceLocation.tryParse(cond.entity_type);
                yield target != null && target.equals(mid);
            }
            case "on_fire" -> mob.isOnFire();
            case "health_below" -> mob.getHealth() < cond.health_value;
            case "health_above" -> mob.getHealth() > cond.health_value;
            case "has_effect" -> {
                if (cond.effect == null) yield false;
                ResourceLocation eid = ResourceLocation.tryParse(cond.effect);
                if (eid == null) yield false;
                var eff = BuiltInRegistries.MOB_EFFECT.getHolder(eid).orElse(null);
                yield eff != null && mob.hasEffect(eff);
            }
            case "in_water" -> mob.isInWater();
            case "in_block" -> mob.isInWall();
            case "is_baby" -> {
                if (mob instanceof AgeableMob ageable) yield ageable.isBaby();
                if (mob instanceof Zombie zombie) yield zombie.isBaby();
                if (mob instanceof net.minecraft.world.entity.monster.piglin.Piglin piglin) yield piglin.isBaby();
                yield false;
            }
            case "moon_phase" -> { int p = mob.level().getMoonPhase(); yield p >= cond.moon_phase_min && p <= cond.moon_phase_max; }
            case "distance_to_player_below" -> distToPlayer(mob) <= cond.distance_value;
            case "distance_to_player_above" -> distToPlayer(mob) >= cond.distance_value;
            case "structure" -> {
                if (cond.structure == null || !(mob.level() instanceof ServerLevel sl)) yield false;
                ResourceLocation sid = ResourceLocation.tryParse(cond.structure);
                if (sid == null) yield false;
                var chunk = sl.getChunk(mob.blockPosition());
                boolean found = false;
                for (var ref : chunk.getAllReferences().entrySet()) {
                    ResourceLocation key = sl.registryAccess().registryOrThrow(Registries.STRUCTURE).getKey(ref.getKey());
                    if (sid.equals(key)) { found = true; break; }
                }
                yield found;
            }
            default -> false;
        };
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
