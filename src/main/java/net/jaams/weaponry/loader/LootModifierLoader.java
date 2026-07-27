package net.jaams.weaponry.loader;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.ModList;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.Difficulty;

import net.jaams.weaponry.data.LootModifierData;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonElement;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class LootModifierLoader extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final LootModifierLoader INSTANCE = new LootModifierLoader();
    private volatile Map<ResourceLocation, LootModifierData> modifiers = new ConcurrentHashMap<>();
    private static final Logger LOGGER = LogManager.getLogger(LootModifierLoader.class);

    private LootModifierLoader() {
        super(GSON, "jaams/loot_modifier");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager resourceManager,
            ProfilerFiller profiler) {
        if (resources == null) {
            LOGGER.warn("LootModifierLoader apply called with null resources");
            return;
        }
        Map<ResourceLocation, LootModifierData> newModifiers = new ConcurrentHashMap<>();
        int count = 0;
        int errors = 0;
        for (Map.Entry<ResourceLocation, JsonElement> entry : resources.entrySet()) {
            if (entry == null || entry.getKey() == null || entry.getValue() == null) continue;
            ResourceLocation fileId = entry.getKey();
            try {
                LootModifierData data = GSON.fromJson(entry.getValue(), LootModifierData.class);
                if (data == null) {
                    LOGGER.warn("Loot modifier file {} returned null data", fileId);
                    errors++;
                    continue;
                }
                if (data.entries == null || data.entries.isEmpty()) {
                    LOGGER.warn("Loot modifier file {} has no entries defined", fileId);
                    errors++;
                    continue;
                }
                if (data.enabled != null && !data.enabled) {
                    LOGGER.info("Loot modifier file {} is disabled, skipping", fileId);
                    continue;
                }
                newModifiers.put(fileId, data);
                count++;
            } catch (Exception e) {
                errors++;
                LOGGER.error("Failed to load loot modifier file: {}", fileId, e);
            }
        }
        this.modifiers = newModifiers;
        LOGGER.info("Loaded {} loot modifiers ({} errors)", count, errors);
    }

    
    public List<LootModifierData> getForLootTable(ResourceLocation lootTableId) {
        if (lootTableId == null) {
            return List.of();
        }
        List<LootModifierData> result = new ArrayList<>();
        String tableId = lootTableId.toString();
        for (LootModifierData data : modifiers.values()) {
            if (data == null) continue;
            if (matchesLootTable(data, tableId)) {
                result.add(data);
            }
        }
        return result;
    }

    
    public List<LootModifierData> getAll() {
        return new ArrayList<>(modifiers.values());
    }

    

    
    public boolean evaluateConditions(LootModifierData data, net.minecraft.world.level.storage.loot.LootContext context) {
        if (data == null) return false;
        if (data.conditions == null || data.conditions.isEmpty()) return true;

        boolean isOrMode = "or".equalsIgnoreCase(data.condition_mode);
        for (LootModifierData.Condition cond : data.conditions) {
            if (cond == null || cond.type == null) continue;
            boolean met = evaluateSingleCondition(cond, context);
            if (isOrMode && met) return true;
            if (!isOrMode && !met) return false;
        }
        return !isOrMode;
    }

    private boolean evaluateSingleCondition(LootModifierData.Condition cond, net.minecraft.world.level.storage.loot.LootContext context) {
        String type = cond.type.toLowerCase().trim();
        switch (type) {
            case "mod_loaded":
                return cond.mod_id != null && ModList.get().isLoaded(cond.mod_id);
            case "mod_not_loaded":
                return cond.mod_id != null && !ModList.get().isLoaded(cond.mod_id);
            case "advancement":
                if (cond.advancement == null) return false;
                ServerPlayer player = getPlayer(context);
                if (player == null) return false;
                ResourceLocation advId = ResourceLocation.tryParse(cond.advancement);
                if (advId == null) return false;
                var advancement = player.getServer().getAdvancements().get(advId);
                if (advancement == null) return false;
                var done = player.getAdvancements().getOrStartProgress(advancement);
                return done.isDone();
            case "difficulty":
                if (cond.difficulty == null) return false;
                ServerLevel level = context.getLevel();
                if (level == null) return false;
                Difficulty diff = level.getDifficulty();
                return diff.name().equalsIgnoreCase(cond.difficulty);
            case "dimension":
                if (cond.dimension == null) return false;
                ServerLevel dimLevel = context.getLevel();
                if (dimLevel == null) return false;
                ResourceLocation dimId = dimLevel.dimension().location();
                return dimId.toString().equals(cond.dimension);
            case "gamestage":
                
                if (cond.gamestage == null) return false;
                if (!ModList.get().isLoaded("gamestages")) return false;
                return false;
            default:
                return true;
        }
    }

    private ServerPlayer getPlayer(net.minecraft.world.level.storage.loot.LootContext context) {
        var killer = context.getParamOrNull(net.minecraft.world.level.storage.loot.parameters.LootContextParams.ATTACKING_ENTITY);
        if (killer instanceof ServerPlayer sp) return sp;
        var thisEntity = context.getParamOrNull(net.minecraft.world.level.storage.loot.parameters.LootContextParams.THIS_ENTITY);
        if (thisEntity instanceof ServerPlayer sp) return sp;
        return null;
    }

    

    private boolean matchesLootTable(LootModifierData data, String tableId) {
        if (data.loot_tables.isEmpty()) {
            return !isExcluded(data, tableId);
        }
        boolean matches = false;
        for (String pattern : data.loot_tables) {
            if (evaluatePattern(pattern, tableId)) {
                matches = true;
                break;
            }
        }
        if (!matches) return false;
        return !isExcluded(data, tableId);
    }

    private boolean isExcluded(LootModifierData data, String tableId) {
        if (data.exclude_loot_tables == null || data.exclude_loot_tables.isEmpty()) {
            return false;
        }
        for (String excluded : data.exclude_loot_tables) {
            if (evaluatePattern(excluded, tableId)) {
                return true;
            }
        }
        return false;
    }

    private boolean evaluatePattern(String pattern, String value) {
        if (pattern.startsWith("regex:")) {
            return value.matches(pattern.substring(6));
        }
        if (pattern.contains("*")) {
            String regex = "^" + pattern.replace("*", ".*") + "$";
            return value.matches(regex);
        }
        return value.equals(pattern);
    }

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(INSTANCE);
    }
}
