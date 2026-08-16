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

import net.jaams.weaponry.data.CreativeTabData;

import java.util.Map;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

import com.google.gson.JsonElement;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;

@EventBusSubscriber
public class TabModifierLoader extends SimpleJsonResourceReloadListener {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	public static final TabModifierLoader INSTANCE = new TabModifierLoader();
	    private volatile List<CreativeTabData.Entry> allEntries = List.of();
	private static final Logger LOGGER = LogManager.getLogger(TabModifierLoader.class);

	private TabModifierLoader() {
		super(GSON, "jaams/tab_modifier");
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager resourceManager, ProfilerFiller profiler) {
		if (resources == null) {
			LOGGER.warn("TabModifierLoader apply called with null resources");
			return;
		}
		List<CreativeTabData.Entry> newEntries = new ArrayList<>();
		int filesLoaded = 0;
		int errors = 0;
		for (Map.Entry<ResourceLocation, JsonElement> entry : resources.entrySet()) {
		    if (entry == null || entry.getKey() == null || entry.getValue() == null) continue;
		    ResourceLocation fileId = entry.getKey();
		            try {
		                CreativeTabData data = GSON.fromJson(entry.getValue(), CreativeTabData.class);
		                if (data != null && data.entries != null && !data.entries.isEmpty()) {
		                    if (data.enabled != null && !data.enabled) {
		                        LOGGER.info("Tab modifier file {} is disabled, skipping", fileId);
		                        continue;
		                    }
		                    newEntries.addAll(data.entries);
		                    filesLoaded++;
		                }
		            } catch (Exception e) {
		                errors++;
		                LOGGER.error("Failed to load creative tab file: {}", fileId, e);
		            }
		        }
		        newEntries.sort((a, b) -> Integer.compare(b.weight, a.weight));
		        this.allEntries = List.copyOf(newEntries);
		        LOGGER.info("Loaded {} creative tab files with {} entries ({} errors)", filesLoaded, allEntries.size(), errors);
	}

	public List<CreativeTabData.Entry> getAllEntries() {
		return Collections.unmodifiableList(allEntries);
	}

	public boolean evaluateConditions(CreativeTabData.Entry entry) {
		if (entry == null) return false;
		if (entry.conditions == null || entry.conditions.isEmpty()) {
			return true;
		}
		boolean isAndMode = "and".equalsIgnoreCase(entry.condition_mode);
		for (CreativeTabData.Condition cond : entry.conditions) {
			if (cond.mod_id == null || cond.mod_id.isEmpty())
				continue;
			boolean modLoaded = ModList.get().isLoaded(cond.mod_id);
			if (isAndMode && !modLoaded)
				return false;
			if (!isAndMode && modLoaded)
				return true;
		}
		return isAndMode;
	}

	@SubscribeEvent
	public static void onAddReloadListener(AddReloadListenerEvent event) {
		event.addListener(INSTANCE);
	}
}
