package net.jaams.weaponry.loader;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.AddReloadListenerEvent;

import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.resources.ResourceLocation;

import net.jaams.weaponry.data.TradeModifierData;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonElement;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TradeModifierLoader extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final TradeModifierLoader INSTANCE = new TradeModifierLoader();
    private volatile Map<ResourceLocation, TradeModifierData> modifiers = new ConcurrentHashMap<>();
    private static final Logger LOGGER = LogManager.getLogger(TradeModifierLoader.class);

    private TradeModifierLoader() {
        super(GSON, "jaams/trade_modifier");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager resourceManager,
            ProfilerFiller profiler) {
        if (resources == null) {
            LOGGER.warn("TradeModifierLoader apply called with null resources");
            return;
        }
        LOGGER.info("[TradeModifierLoader] apply called with {} resources", resources.size());
        for (ResourceLocation key : resources.keySet()) {
            LOGGER.info("[TradeModifierLoader] resource: {}", key);
        }
        Map<ResourceLocation, TradeModifierData> newModifiers = new ConcurrentHashMap<>();
        int count = 0;
        int errors = 0;
        for (Map.Entry<ResourceLocation, JsonElement> entry : resources.entrySet()) {
            if (entry == null || entry.getKey() == null || entry.getValue() == null) continue;
            ResourceLocation fileId = entry.getKey();
            try {
                TradeModifierData data = GSON.fromJson(entry.getValue(), TradeModifierData.class);
                if (data == null) {
                    LOGGER.warn("Trade modifier file {} returned null data", fileId);
                    errors++;
                    continue;
                }
                if (data.trades == null || data.trades.isEmpty()) {
                    LOGGER.warn("Trade modifier file {} has no trades defined", fileId);
                    errors++;
                    continue;
                }
                if (data.enabled != null && !data.enabled) {
                    LOGGER.info("Trade modifier file {} is disabled, skipping", fileId);
                    continue;
                }
                newModifiers.put(fileId, data);
                count++;
            } catch (Exception e) {
                errors++;
                LOGGER.error("Failed to load trade modifier file: {}", fileId, e);
            }
        }
        this.modifiers = newModifiers;
        LOGGER.info("Loaded {} trade modifiers ({} errors)", count, errors);
    }

    
    public List<TradeModifierData> getForProfession(ResourceLocation professionId) {
        if (professionId == null) {
            return List.of();
        }
        List<TradeModifierData> result = new ArrayList<>();
        String profId = professionId.toString();
        LOGGER.info("[TradeModifierLoader] getForProfession called with '{}', modifiers loaded={}", profId, modifiers.size());
        for (TradeModifierData data : modifiers.values()) {
            if (data == null) continue;
            LOGGER.info("[TradeModifierLoader] checking target={} against profId={}", data.target, profId);
            if (matchesTarget(data, profId)) {
                result.add(data);
            }
        }
        LOGGER.info("[TradeModifierLoader] getForProfession result count={}", result.size());
        return result;
    }

    public List<TradeModifierData> getAll() {
        return new ArrayList<>(modifiers.values());
    }

    

    public boolean evaluateConditions(TradeModifierData data, net.minecraft.world.entity.player.Player player) {
        if (data == null) return false;
        if (data.conditions == null || data.conditions.isEmpty()) return true;

        boolean isOrMode = "or".equalsIgnoreCase(data.condition_mode);
        for (TradeModifierData.Condition cond : data.conditions) {
            if (cond == null || cond.type == null) continue;
            boolean met = evaluateSingleCondition(cond, player);
            if (isOrMode && met) return true;
            if (!isOrMode && !met) return false;
        }
        return !isOrMode;
    }

    private boolean evaluateSingleCondition(TradeModifierData.Condition cond, net.minecraft.world.entity.player.Player player) {
        String type = cond.type.toLowerCase().trim();
        switch (type) {
            case "mod_loaded":
                return cond.mod_id != null && ModList.get().isLoaded(cond.mod_id);
            case "mod_not_loaded":
                return cond.mod_id != null && !ModList.get().isLoaded(cond.mod_id);
            case "advancement":
                if (cond.advancement == null || player == null) return false;
                if (!(player instanceof net.minecraft.server.level.ServerPlayer sp)) return false;
                ResourceLocation advId = ResourceLocation.tryParse(cond.advancement);
                if (advId == null) return false;
                var advancement = sp.getServer().getAdvancements().getAdvancement(advId);
                if (advancement == null) return false;
                var advs = sp.getAdvancements();
                var progress = advs.getOrStartProgress(advancement);
                return progress.isDone();
            case "difficulty":
                if (cond.difficulty == null || player == null) return false;
                return player.level().getDifficulty().name().equalsIgnoreCase(cond.difficulty);
            case "dimension":
                if (cond.dimension == null || player == null) return false;
                return player.level().dimension().location().toString().equals(cond.dimension);
            default:
                return true;
        }
    }

    

    private boolean matchesTarget(TradeModifierData data, String professionId) {
        if (data.target == null || data.target.isEmpty()) return false;
        for (String pattern : data.target) {
            if (evaluatePattern(pattern, professionId)) {
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
