package net.jaams.weaponry.handler.event;

import org.checkerframework.checker.units.qual.s;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.ChatFormatting;

import net.jaams.weaponry.util.ModUtils;
import net.jaams.weaponry.configuration.client.TooltipsConfig;
import net.jaams.weaponry.compat.EpicFightCompat;

import java.util.stream.Collectors;
import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Collections;
import java.util.ArrayList;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public abstract class ItemTooltipHandler {
	private static final String SHIFT_HINT = "tooltip.jaams_weaponry.shift_details";
	private static final String ALT_HINT = "tooltip.jaams_weaponry.alt_desc";
	private static final String CTRL_HINT = "tooltip.jaams_weaponry.ctrl_info";
	private static final String MOD_ID = "jaams_weaponry";
	private static final String MOD_ID_STEM = "jaams";

	private static List<Component> capturedModTooltipLines = Collections.emptyList();

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void captureModTooltipLines(ItemTooltipEvent event) {
		capturedModTooltipLines = new ArrayList<>();
		for (Component line : event.getToolTip()) {
			String key = getKey(line);
			if (key != null && isModTooltipKey(key)) {
				capturedModTooltipLines.add(line);
			}
		}
	}

	private static boolean isModTooltipKey(String key) {
		if (!isOurKey(key))
			return false;
		return key.contains("trait.") || key.contains("properties") || key.endsWith(".long_desc") || key.contains("extra");
	}

	private static void addSpacingBetweenInfoCategories(ItemTooltipEvent event) {
		if (!Screen.hasControlDown()) {
			return;
		}
		List<Component> tooltip = event.getToolTip();
		if (tooltip.size() < 4) {
			return;
		}
		boolean isPropertiesTooltip = tooltip.stream().map(ItemTooltipHandler::getKey).anyMatch(key -> key != null && isOurKey(key) && (key.contains("properties") || key.contains("extra")));
		if (!isPropertiesTooltip) {
			return;
		}
		List<Integer> positionsToInsertSpace = new ArrayList<>();
		int previousCategoryIndex = -1;
		ChatFormatting previousColor = null;
		for (int i = 1; i < tooltip.size(); i++) {
			Component line = tooltip.get(i);
			String key = getKey(line);
			if (key == null || !isCategoryHeaderKey(key))
				continue;
			ChatFormatting currentColor = getDominantColor(line);
			if (currentColor == ChatFormatting.GOLD || currentColor == ChatFormatting.YELLOW || currentColor == ChatFormatting.RED) {
				if (previousCategoryIndex != -1) {
					boolean sameColor = currentColor == previousColor;
					if (sameColor) {
						if (hasGrayStatsBetween(tooltip, previousCategoryIndex, i)) {
							positionsToInsertSpace.add(i);
						}
					} else {
						positionsToInsertSpace.add(i);
					}
				}
				previousCategoryIndex = i;
				previousColor = currentColor;
			}
		}
		for (int i = positionsToInsertSpace.size() - 1; i >= 0; i--) {
			insertSingleEmptyLine(tooltip, positionsToInsertSpace.get(i));
		}
	}

	private static boolean hasGrayStatsBetween(List<Component> tooltip, int start, int end) {
		for (int j = start + 1; j < end; j++) {
			if (getDominantColor(tooltip.get(j)) == ChatFormatting.GRAY) {
				return true;
			}
		}
		return false;
	}

	private static ChatFormatting getDominantColor(Component component) {
		Style style = component.getStyle();
		if (style.getColor() != null) {
			TextColor color = style.getColor();
			if (color.equals(TextColor.fromLegacyFormat(ChatFormatting.GOLD))) {
				return ChatFormatting.GOLD;
			}
			if (color.equals(TextColor.fromLegacyFormat(ChatFormatting.YELLOW))) {
				return ChatFormatting.YELLOW;
			}
			if (color.equals(TextColor.fromLegacyFormat(ChatFormatting.RED))) {
				return ChatFormatting.RED;
			}
			if (color.equals(TextColor.fromLegacyFormat(ChatFormatting.GRAY))) {
				return ChatFormatting.GRAY;
			}
			return null;
		}
		for (Component sibling : component.getSiblings()) {
			Style s = sibling.getStyle();
			if (s.getColor() != null) {
				TextColor color = s.getColor();
				if (color.equals(TextColor.fromLegacyFormat(ChatFormatting.GOLD))) {
					return ChatFormatting.GOLD;
				}
				if (color.equals(TextColor.fromLegacyFormat(ChatFormatting.YELLOW))) {
					return ChatFormatting.YELLOW;
				}
				if (color.equals(TextColor.fromLegacyFormat(ChatFormatting.RED))) {
					return ChatFormatting.RED;
				}
				if (color.equals(TextColor.fromLegacyFormat(ChatFormatting.GRAY))) {
					return ChatFormatting.GRAY;
				}
			}
		}
		return null;
	}

	private static void addSpacingOnlyBetweenLongDescAndProperties(ItemTooltipEvent event) {
		List<Component> lines = event.getToolTip();
		if (lines.size() <= 1)
			return;
		for (int i = lines.size() - 2; i >= 1; i--) {
			Component current = lines.get(i);
			Component next = lines.get(i + 1);
			String keyCurrent = getKey(current);
			String keyNext = getKey(next);
			if (keyCurrent == null || keyNext == null)
				continue;
			if (!isOurKey(keyCurrent) && !isOurKey(keyNext))
				continue;
			boolean currentIsLongDesc = keyCurrent.endsWith(".long_desc");
			boolean currentIsPropExtra = keyCurrent.contains("properties") || keyCurrent.contains("extra");
			boolean nextIsLongDesc = keyNext.endsWith(".long_desc");
			boolean nextIsPropExtra = keyNext.contains("properties") || keyNext.contains("extra");
			boolean needsSpacing = false;
			if (currentIsLongDesc && nextIsPropExtra)
				needsSpacing = true;
			else if (currentIsPropExtra && nextIsLongDesc)
				needsSpacing = true;
			else if (currentIsLongDesc && nextIsLongDesc) {
				String mod1 = extractModId(keyCurrent);
				String mod2 = extractModId(keyNext);
				if (!mod1.equals(mod2))
					needsSpacing = true;
			} else if (currentIsPropExtra && nextIsPropExtra) {
				String mod1 = extractModId(keyCurrent);
				String mod2 = extractModId(keyNext);
				if (!mod1.equals(mod2))
					needsSpacing = true;
			}
			if (needsSpacing) {
				insertSingleEmptyLine(lines, i + 1);
			}
		}
	}

	private static void insertSingleEmptyLine(List<Component> lines, int index) {
		if (index < lines.size() && lines.get(index).getString().isEmpty()) {
			return;
		}
		if ((index > 0 && lines.get(index - 1).getString().isEmpty()) || (index < lines.size() && lines.get(index).getString().isEmpty())) {
			return;
		}
		lines.add(index, Component.literal(""));
	}

	private static String extractModId(String key) {
		if (!key.startsWith("tooltip."))
			return "unknown";
		int start = "tooltip.".length();
		int end = key.indexOf('.', start);
		if (end == -1)
			return key.substring(start);
		return key.substring(start, end);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onItemTooltip(ItemTooltipEvent event) {
		List<Component> lines = event.getToolTip();
		if (lines.isEmpty())
			return;
		restoreModTooltipsClearedByEpicFight(event, lines);
		Item item = event.getItemStack().getItem();
		ResourceLocation itemKey = ForgeRegistries.ITEMS.getKey(item);
		filterTooltipByConfig(lines, itemKey, item);
		filterControlTooltipByConfig(lines, itemKey, item);
		filterAltTooltipByConfig(lines, itemKey, item);
		applyDisabledFeatureFilters(lines);
		removeDuplicateTraits(lines);
		reorganizeTraitsByColor(lines);
		boolean altDown = Screen.hasAltDown();
		boolean ctrlDown = Screen.hasControlDown();
		boolean shiftDown = Screen.hasShiftDown();
		boolean altCtrl = altDown && ctrlDown;
		TooltipContent content = detectContent(lines);
		removeLinesByKeyState(lines, altDown, ctrlDown, shiftDown, altCtrl, content);
		if (altCtrl) {
			reorganizeAltCtrl(lines);
		}
		removeOurHints(lines);
		TooltipVisibility visibility = detectVisibility(lines);
		addSpacingAfterLastSpecialLine(lines);
		addHintsOnlyIfMissing(lines, content, visibility, shiftDown);
		addSpacingBetweenInfoCategories(event);
		addSpacingOnlyBetweenLongDescAndProperties(event);
	}

	private static void restoreModTooltipsClearedByEpicFight(ItemTooltipEvent event, List<Component> lines) {
		if (capturedModTooltipLines.isEmpty() || !EpicFightCompat.isEpicFightLoaded())
			return;
		if (hasModTooltipContent(lines))
			return;
		int insertIndex = Math.min(1, lines.size());
		lines.addAll(insertIndex, capturedModTooltipLines);
	}

	private static boolean hasModTooltipContent(List<Component> lines) {
		for (Component line : lines) {
			String key = getKey(line);
			if (key != null && isModTooltipKey(key)) {
				return true;
			}
		}
		return false;
	}

	private static void applyDisabledFeatureFilters(List<Component> lines) {
	}

	private static void filterTooltipByConfig(List<Component> lines, ResourceLocation itemKey, Item item) {
		if (!TooltipsConfig.TOOLTIPS.get()) {
			lines.removeIf(line -> {
				String key = getKey(line);
				return key != null && key.contains(MOD_ID) && (key.contains("trait.") || key.endsWith(".long_desc") || key.contains("properties") || key.contains("extra") || key.equals(SHIFT_HINT) || key.equals(ALT_HINT) || key.equals(CTRL_HINT));
			});
			return;
		}
		if (itemKey == null)
			return;
		List<? extends String> excludedRaw = TooltipsConfig.EXCLUDED_TOOLTIPS_ITEMS.get();
		Set<String> excludedSet = excludedRaw != null ? new HashSet<>(excludedRaw) : Collections.emptySet();
		boolean isExcluded = ModUtils.matchesList(excludedSet, itemKey, item, false);
		if (isExcluded) {
			lines.removeIf(line -> {
				String key = getKey(line);
				return key != null && (key.contains("trait.") || key.endsWith(".long_desc") || key.contains("properties") || key.contains("extra") || key.equals(SHIFT_HINT) || key.equals(ALT_HINT) || key.equals(CTRL_HINT));
			});
			return;
		}
	}

	private static void filterControlTooltipByConfig(List<Component> lines, ResourceLocation itemKey, Item item) {
		if (!TooltipsConfig.CONTROL_TOOLTIPS.get()) {
			lines.removeIf(line -> {
				String key = getKey(line);
				return key != null && key.contains(MOD_ID) && (key.contains("properties") || key.contains("extra") || key.equals(CTRL_HINT));
			});
			return;
		}
		if (itemKey == null)
			return;
		List<? extends String> excludedRaw = TooltipsConfig.EXCLUDED_CONTROL_TOOLTIPS_ITEMS.get();
		Set<String> excludedSet = excludedRaw != null ? new HashSet<>(excludedRaw) : Collections.emptySet();
		boolean isExcluded = ModUtils.matchesList(excludedSet, itemKey, item, false);
		if (isExcluded) {
			lines.removeIf(line -> {
				String key = getKey(line);
				return key != null && (key.contains("properties") || key.contains("extra") || key.equals(CTRL_HINT));
			});
			return;
		}
	}

	private static void filterAltTooltipByConfig(List<Component> lines, ResourceLocation itemKey, Item item) {
		if (!TooltipsConfig.ALT_TOOLTIPS.get()) {
			lines.removeIf(line -> {
				String key = getKey(line);
				return key != null && key.contains(MOD_ID) && (key.endsWith(".long_desc") || key.equals(ALT_HINT));
			});
			return;
		}
		if (itemKey == null)
			return;
		List<? extends String> excludedRaw = TooltipsConfig.EXCLUDED_ALT_TOOLTIPS_ITEMS.get();
		Set<String> excludedSet = excludedRaw != null ? new HashSet<>(excludedRaw) : Collections.emptySet();
		boolean isExcluded = ModUtils.matchesList(excludedSet, itemKey, item, false);
		if (isExcluded) {
			lines.removeIf(line -> {
				String key = getKey(line);
				return key != null && (key.endsWith(".long_desc") || key.equals(ALT_HINT));
			});
			return;
		}
	}

	private static void removeOurHints(List<Component> lines) {
		lines.removeIf(line -> {
			String key = getKey(line);
			return key != null && (key.equals(SHIFT_HINT) || key.equals(ALT_HINT) || key.equals(CTRL_HINT));
		});
	}

	private static void addHintsOnlyIfMissing(List<Component> lines, TooltipContent content, TooltipVisibility visibility, boolean shiftDown) {
		boolean advancedShown = visibility.visibleLongDesc || visibility.visiblePropertiesOrExtra;
		List<Component> hintsToAdd = new ArrayList<>();
		if (content.hasTrait && !shiftDown && !advancedShown && !hasAnyHintOfType(lines, "shift_details")) {
			hintsToAdd.add(Component.translatable(SHIFT_HINT).withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(true)));
		}
		if (content.hasLongDesc && !visibility.visibleLongDesc && !hasAnyHintOfType(lines, "alt_desc")) {
			hintsToAdd.add(Component.translatable(ALT_HINT).withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(true)));
		}
		if (content.hasPropertiesOrExtra && !visibility.visiblePropertiesOrExtra && !hasAnyHintOfType(lines, "ctrl_info")) {
			hintsToAdd.add(Component.translatable(CTRL_HINT).withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(true)));
		}
		if (!hintsToAdd.isEmpty()) {
			long realLines = lines.stream().skip(1).filter(line -> getKey(line) == null || !isHintKey(getKey(line))).count();
			if (realLines > 0) {
				lines.add(Component.literal(""));
			}
			lines.addAll(hintsToAdd);
		}
	}

	private static Set<String> getExcludedSet(List<? extends String> rawList) {
		return rawList != null ? new HashSet<>(rawList) : Collections.emptySet();
	}

	private static boolean hasAnyHintOfType(List<Component> lines, String type) {
		return lines.stream().anyMatch(line -> {
			String key = getKey(line);
			return key != null && isOurKey(key) && type.equals(getKeySuffix(key));
		});
	}

	private static String getKeySuffix(String key) {
		int lastDot = key.lastIndexOf('.');
		return lastDot >= 0 ? key.substring(lastDot + 1) : key;
	}

	private static boolean isHintKey(String key) {
		if (key == null)
			return false;
		String suffix = getKeySuffix(key);
		return "shift_details".equals(suffix) || "alt_desc".equals(suffix) || "ctrl_info".equals(suffix);
	}

	private static String getKey(Component c) {
		if (c.getContents() instanceof TranslatableContents tc) {
			return tc.getKey();
		}
		for (Component s : c.getSiblings()) {
			if (s.getContents() instanceof TranslatableContents tc) {
				return tc.getKey();
			}
		}
		return null;
	}

	private static boolean isOurKey(String key) {
		return key.contains(MOD_ID_STEM);
	}

	private static boolean isCategoryHeaderKey(String key) {
		if (!isOurKey(key))
			return false;
		if (key.endsWith(".desc"))
			return false;
		return key.contains("trait.") || key.contains("properties") || key.contains("extra");
	}

	private static TooltipContent detectContent(List<Component> lines) {
		boolean hasTrait = false, hasLongDesc = false, hasPropertiesOrExtra = false;
		for (Component line : lines) {
			String key = getKey(line);
			if (key == null || !isOurKey(key))
				continue;
			if (key.contains("trait."))
				hasTrait = true;
			if (key.endsWith(".long_desc"))
				hasLongDesc = true;
			if (key.contains("properties") || key.contains("extra"))
				hasPropertiesOrExtra = true;
		}
		return new TooltipContent(hasTrait, hasLongDesc, hasPropertiesOrExtra);
	}

	private static void removeLinesByKeyState(List<Component> lines, boolean altDown, boolean ctrlDown, boolean shiftDown, boolean altCtrl, TooltipContent content) {
		Iterator<Component> it = lines.iterator();
		while (it.hasNext()) {
			Component line = it.next();
			String key = getKey(line);
			if (key == null)
				continue;
			if (!isOurKey(key))
				continue;
			boolean isTrait = key.contains("trait.");
			boolean isLongDesc = key.endsWith(".long_desc");
			boolean isProperties = key.contains("properties");
			boolean isExtra = key.contains("extra");
			if (!isTrait && !isLongDesc && !isProperties && !isExtra)
				continue;
			boolean remove = false;
			if (!altDown && !ctrlDown) {
				if (!isTrait)
					remove = true;
			} else if (altDown && !ctrlDown) {
				if (isTrait && content.hasLongDesc)
					remove = true;
				if (isProperties || isExtra)
					remove = true;
			} else if (ctrlDown && !altDown) {
				if (isTrait && content.hasPropertiesOrExtra)
					remove = true;
				if (isLongDesc)
					remove = true;
			} else if (altCtrl) {
				if (isTrait && (content.hasLongDesc || content.hasPropertiesOrExtra)) {
					remove = true;
				}
			}
			if (remove)
				it.remove();
		}
	}

	private static void reorganizeAltCtrl(List<Component> lines) {
		List<Component> longDescs = new ArrayList<>();
		List<Component> extras = new ArrayList<>();
		for (Component line : lines) {
			String key = getKey(line);
			if (key == null || !isOurKey(key))
				continue;
			if (key.endsWith(".long_desc")) {
				longDescs.add(line);
			} else if (key.contains("properties") || key.contains("extra")) {
				extras.add(line);
			}
		}
		lines.removeAll(longDescs);
		lines.removeAll(extras);
		List<Component> block = new ArrayList<>();
		block.addAll(longDescs);
		block.addAll(extras);
		if (!block.isEmpty()) {
			lines.addAll(1, block);
		}
	}

	private static TooltipVisibility detectVisibility(List<Component> lines) {
		boolean visibleTrait = false, visibleLongDesc = false, visiblePropertiesOrExtra = false;
		for (Component line : lines) {
			String key = getKey(line);
			if (key == null || !isOurKey(key))
				continue;
			if (key.contains("trait."))
				visibleTrait = true;
			if (key.endsWith(".long_desc"))
				visibleLongDesc = true;
			if (key.contains("properties") || key.contains("extra"))
				visiblePropertiesOrExtra = true;
		}
		return new TooltipVisibility(visibleTrait, visibleLongDesc, visiblePropertiesOrExtra);
	}

	private static void addSpacingAfterLastSpecialLine(List<Component> lines) {
		if (lines.size() <= 1)
			return;
		int lastSpecialIndex = -1;
		for (int i = 0; i < lines.size(); i++) {
			Component line = lines.get(i);
			String key = getKey(line);
			if (key != null && isSpecialKey(key)) {
				lastSpecialIndex = i;
			}
		}
		if (lastSpecialIndex == -1)
			return;
		int nextIndex = lastSpecialIndex + 1;
		int blankCount = 0;
		while (nextIndex + blankCount < lines.size() && lines.get(nextIndex + blankCount).getString().isEmpty()) {
			blankCount++;
		}
		int contentAfterIndex = nextIndex + blankCount;
		if (contentAfterIndex >= lines.size())
			return;
		String nextKey = getKey(lines.get(contentAfterIndex));
		if (nextKey != null && isSpecialKey(nextKey))
			return;
		if (blankCount == 0) {
			lines.add(nextIndex, Component.literal(""));
		} else if (blankCount > 1) {
			for (int r = 0; r < blankCount - 1; r++) {
				lines.remove(nextIndex);
			}
		}
	}

	private static void removeDuplicateTraits(List<Component> lines) {
		Map<String, Component> latestTraits = new LinkedHashMap<>();
		Map<String, Component> latestTraitDescs = new LinkedHashMap<>();
		for (Component line : lines) {
			String key = getKey(line);
			if (key == null || !isOurKey(key))
				continue;
			if (key.contains("trait.") && !key.endsWith(".desc")) {
				String traitName = extractTraitName(key);
				if (traitName != null) {
					latestTraits.put(traitName, line);
				}
			} else if (key.endsWith(".desc")) {
				String baseKey = key.substring(0, key.length() - ".desc".length());
				String traitName = extractTraitName(baseKey);
				if (traitName != null) {
					latestTraitDescs.put(traitName, line);
				}
			}
		}
		List<Component> toRemove = new ArrayList<>();
		for (Component line : lines) {
			String key = getKey(line);
			if (key == null || !isOurKey(key))
				continue;
			if (key.contains("trait.") && !key.endsWith(".desc")) {
				String traitName = extractTraitName(key);
				if (traitName != null && latestTraits.get(traitName) != line) {
					toRemove.add(line);
				}
			} else if (key.endsWith(".desc")) {
				String baseKey = key.substring(0, key.length() - ".desc".length());
				String traitName = extractTraitName(baseKey);
				if (traitName != null) {
					Component latestDesc = latestTraitDescs.get(traitName);
					if (latestDesc != line || !latestTraits.containsKey(traitName)) {
						toRemove.add(line);
					}
				}
			}
		}
		lines.removeAll(toRemove);
	}

	private static String extractTraitName(String key) {
		if (!key.contains("trait."))
			return null;
		int traitIndex = key.indexOf("trait.");
		String afterTrait = key.substring(traitIndex + 6);
		int dotIndex = afterTrait.indexOf('.');
		if (dotIndex == -1) {
			return afterTrait;
		}
		return afterTrait.substring(0, dotIndex);
	}

	private static void reorganizeTraitsByColor(List<Component> lines) {
		List<Component> traits = new ArrayList<>();
		List<Component> nonTraits = new ArrayList<>();
		for (Component line : lines) {
			String key = getKey(line);
			if (key != null && isOurKey(key) && key.contains("trait.")) {
				traits.add(line);
			} else {
				nonTraits.add(line);
			}
		}
		if (traits.isEmpty()) {
			return;
		}
		Map<String, TraitEntry> traitMap = new LinkedHashMap<>();
		for (Component line : traits) {
			String key = getKey(line);
			if (key == null)
				continue;
			if (key.endsWith(".desc")) {
				String baseKey = key.substring(0, key.length() - ".desc".length());
				String traitName = extractTraitName(baseKey);
				if (traitName != null) {
					TraitEntry entry = traitMap.computeIfAbsent(traitName, k -> new TraitEntry());
					entry.desc = line;
				}
			} else {
				String traitName = extractTraitName(key);
				if (traitName != null) {
					TraitEntry entry = traitMap.computeIfAbsent(traitName, k -> new TraitEntry());
					entry.trait = line;
				}
			}
		}
		List<TraitEntry> validTraits = traitMap.values().stream().filter(e -> e.trait != null).collect(Collectors.toList());
		validTraits.sort((a, b) -> {
			int colorA = getTraitColor(a.trait);
			int colorB = getTraitColor(b.trait);
			int priorityA = getTraitPriority(colorA);
			int priorityB = getTraitPriority(colorB);
			return Integer.compare(priorityA, priorityB);
		});
		List<Component> orderedTraits = new ArrayList<>();
		for (TraitEntry entry : validTraits) {
			orderedTraits.add(entry.trait);
			if (entry.desc != null) {
				orderedTraits.add(entry.desc);
			}
		}
		lines.clear();
		lines.addAll(nonTraits);
		int insertIndex = 1;
		for (int i = 1; i < lines.size(); i++) {
			String key = getKey(lines.get(i));
			if (key != null && isOurKey(key) && key.contains("trait.")) {
				insertIndex = i;
				break;
			}
			if (key != null && isOurKey(key) && (key.contains("properties") || key.contains("extra") || key.endsWith(".long_desc"))) {
				insertIndex = i;
				break;
			}
		}
		lines.addAll(insertIndex, orderedTraits);
	}

	private static int getTraitPriority(int colorValue) {
		if (colorValue == -1)
			return 5;
		if (colorValue == ChatFormatting.GOLD.getColor()) {
			return 1;
		}
		if (colorValue == ChatFormatting.RED.getColor()) {
			return 2;
		}
		if (colorValue == ChatFormatting.YELLOW.getColor()) {
			return 3;
		}
		if (colorValue == ChatFormatting.AQUA.getColor()) {
			return 5;
		}
		return 4;
	}

	private static int getTraitColor(Component traitLine) {
		Style style = traitLine.getStyle();
		if (style.getColor() != null) {
			return style.getColor().getValue();
		}
		for (Component sibling : traitLine.getSiblings()) {
			Style s = sibling.getStyle();
			if (s.getColor() != null) {
				return s.getColor().getValue();
			}
		}
		return -1;
	}

	private static class TraitEntry {
		Component trait;
		Component desc;
	}

	private static boolean isSpecialKey(String key) {
		if (!isOurKey(key))
			return false;
		return key.contains("trait.") || key.endsWith(".long_desc") || key.contains("properties") || key.contains("extra");
	}

	private record TooltipContent(boolean hasTrait, boolean hasLongDesc, boolean hasPropertiesOrExtra) {
	}

	private record TooltipVisibility(boolean visibleTrait, boolean visibleLongDesc, boolean visiblePropertiesOrExtra) {
	}
}
