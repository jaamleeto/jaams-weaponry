package net.jaams.weaponry.handler.event;

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
    private static final String MOD_ID = "jaams";

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void addSpacingBetweenInfoCategories(ItemTooltipEvent event) {
        if (!Screen.hasControlDown()) {
            return;
        }
        List<Component> tooltip = event.getToolTip();
        if (tooltip.size() < 4) {
            return;
        }
        boolean isPropertiesTooltip = tooltip.stream().map(ItemTooltipHandler::getKey)
                .anyMatch(key -> key != null && key.contains("properties"));
        if (!isPropertiesTooltip) {
            return;
        }
        List<Integer> positionsToInsertSpace = new ArrayList<>();
        int previousCategoryIndex = -1;
        ChatFormatting previousColor = null;
        for (int i = 0; i < tooltip.size(); i++) {
            String key = getKey(tooltip.get(i));

            if (key == null || !key.contains("jaams")
                    || !key.contains("properties"))
                continue;
            ChatFormatting currentColor = getDominantColor(tooltip.get(i));
            if (currentColor == ChatFormatting.GOLD || currentColor == ChatFormatting.YELLOW
                    || currentColor == ChatFormatting.RED) {
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

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void addSpacingBeforeAquaTraits(ItemTooltipEvent event) {
        List<Component> lines = event.getToolTip();
        if (lines.size() <= 1)
            return;
        int insertPosition = -1;
        boolean foundAquaBlock = false;
        for (int i = 0; i < lines.size(); i++) {
            Component line = lines.get(i);
            String key = getKey(line);
            if (key != null && key.contains("trait.")) {
                int traitColor = getTraitColorSmart(lines, i);
                boolean isAqua = (traitColor == ChatFormatting.AQUA.getColor());
                if (isAqua) {
                    foundAquaBlock = true;
                    break;
                } else {
                    insertPosition = i + 1;
                }
            }
        }
        if (foundAquaBlock && insertPosition != -1 && insertPosition < lines.size()) {
            insertSingleEmptyLine(lines, insertPosition);
        }
    }

    private static int getTraitColorSmart(List<Component> lines, int currentIndex) {
        Component currentLine = lines.get(currentIndex);
        String currentKey = getKey(currentLine);
        if (currentKey == null || !currentKey.contains("trait.")) {
            return -1;
        }
        if (!currentKey.endsWith(".desc")) {
            return getTraitColor(currentLine);
        }
        for (int j = currentIndex - 1; j >= 0; j--) {
            Component prevLine = lines.get(j);
            String prevKey = getKey(prevLine);
            if (prevKey != null && prevKey.contains("trait.") && !prevKey.endsWith(".desc")) {
                return getTraitColor(prevLine);
            }
        }
        return -1;
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void addSpacingOnlyBetweenLongDescAndProperties(ItemTooltipEvent event) {
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
            boolean currentIsLongDesc = keyCurrent.endsWith(".long_desc");
            boolean currentIsPropExtra = keyCurrent.contains("properties");
            boolean nextIsLongDesc = keyNext.endsWith(".long_desc");
            boolean nextIsPropExtra = keyNext.contains("properties");
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
        if ((index > 0 && lines.get(index - 1).getString().isEmpty())
                || (index < lines.size() && lines.get(index).getString().isEmpty())) {
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

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        List<Component> lines = event.getToolTip();
        if (lines.isEmpty())
            return;
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
    }

    private static void applyDisabledFeatureFilters(List<Component> lines) {
    }

    private static void filterTooltipByConfig(List<Component> lines, ResourceLocation itemKey, Item item) {
        if (!TooltipsConfig.TOOLTIPS.get()) {
            lines.removeIf(line -> {
                String key = getKey(line);
                return key != null && key.contains(MOD_ID)
                        && (key.contains("trait.") || key.endsWith(".long_desc") || key.contains("properties")
                                || key.equals(SHIFT_HINT) || key.equals(ALT_HINT)
                                || key.equals(CTRL_HINT));
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
                return key != null && key.contains(MOD_ID)
                        && (key.contains("trait.") || key.endsWith(".long_desc")
                                || key.contains("properties") || key.equals(SHIFT_HINT)
                                || key.equals(ALT_HINT) || key.equals(CTRL_HINT));
            });
            return;
        }
    }

    private static void filterControlTooltipByConfig(List<Component> lines, ResourceLocation itemKey, Item item) {
        if (!TooltipsConfig.CONTROL_TOOLTIPS.get()) {
            lines.removeIf(line -> {
                String key = getKey(line);
                return key != null && key.contains(MOD_ID)
                        && (key.contains("properties") || key.equals(CTRL_HINT));
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
                return key != null && key.contains(MOD_ID)
                        && (key.contains("properties") || key.equals(CTRL_HINT));
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
                return key != null && key.contains(MOD_ID) && (key.endsWith(".long_desc") || key.equals(ALT_HINT));
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

    private static void addHintsOnlyIfMissing(List<Component> lines, TooltipContent content,
            TooltipVisibility visibility, boolean shiftDown) {
        boolean advancedShown = visibility.visibleLongDesc || visibility.visibleProperties;
        List<Component> hintsToAdd = new ArrayList<>();
        if (content.hasTrait && !shiftDown && !advancedShown && !hasAnyHintOfType(lines, "shift_details")) {
            hintsToAdd.add(Component.translatable(SHIFT_HINT)
                    .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(true)));
        }
        if (content.hasLongDesc && !visibility.visibleLongDesc && !hasAnyHintOfType(lines, "alt_desc")) {
            hintsToAdd.add(Component.translatable(ALT_HINT)
                    .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(true)));
        }
        if (content.hasProperties && !visibility.visibleProperties
                && !hasAnyHintOfType(lines, "ctrl_info")) {
            hintsToAdd.add(Component.translatable(CTRL_HINT)
                    .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(true)));
        }
        if (!hintsToAdd.isEmpty()) {
            long realLines = lines.stream().skip(1).filter(line -> getKey(line) == null || !isHintKey(getKey(line)))
                    .count();
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
            return key != null && type.equals(getKeySuffix(key));
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

    private static TooltipContent detectContent(List<Component> lines) {
        boolean hasTrait = false, hasLongDesc = false, hasProperties = false;
        for (Component line : lines) {
            String key = getKey(line);
            if (key == null)
                continue;
            if (key.contains(MOD_ID) && key.contains("trait."))
                hasTrait = true;
            if (key.contains(MOD_ID) && key.endsWith(".long_desc"))
                hasLongDesc = true;
            if (key.contains(MOD_ID) && key.contains("properties"))
                hasProperties = true;
        }
        return new TooltipContent(hasTrait, hasLongDesc, hasProperties);
    }

    private static void removeLinesByKeyState(List<Component> lines, boolean altDown, boolean ctrlDown,
            boolean shiftDown, boolean altCtrl, TooltipContent content) {
        Iterator<Component> it = lines.iterator();
        while (it.hasNext()) {
            Component line = it.next();
            String key = getKey(line);
            if (key == null || !key.contains(MOD_ID))
                continue;
            boolean isTrait = key.contains("trait.");
            boolean isLongDesc = key.endsWith(".long_desc");
            boolean isProperties = key.contains("properties");
            if (!isTrait && !isLongDesc && !isProperties)
                continue;
            boolean remove = false;
            if (!altDown && !ctrlDown) {
                if (!isTrait)
                    remove = true;
            } else if (altDown && !ctrlDown) {
                if (isTrait && content.hasLongDesc)
                    remove = true;
                if (isProperties)
                    remove = true;
            } else if (ctrlDown && !altDown) {
                if (isTrait && content.hasProperties)
                    remove = true;
                if (isLongDesc)
                    remove = true;
            } else if (altCtrl) {
                if (isTrait && (content.hasLongDesc || content.hasProperties)) {
                    remove = true;
                }
            }
            if (remove)
                it.remove();
        }
    }

    private static void reorganizeAltCtrl(List<Component> lines) {
        List<Component> longDescs = new ArrayList<>();
        List<Component> properties = new ArrayList<>();
        for (Component line : lines) {
            String key = getKey(line);
            if (key == null || !key.contains(MOD_ID))
                continue;
            if (key.endsWith(".long_desc")) {
                longDescs.add(line);
            } else if (key.contains("properties")) {
                properties.add(line);
            }
        }
        lines.removeAll(longDescs);
        lines.removeAll(properties);
        List<Component> block = new ArrayList<>();
        block.addAll(longDescs);
        block.addAll(properties);
        if (!block.isEmpty()) {
            lines.addAll(1, block);
        }
    }

    private static TooltipVisibility detectVisibility(List<Component> lines) {
        boolean visibleTrait = false, visibleLongDesc = false, visibleProperties = false;
        for (Component line : lines) {
            String key = getKey(line);
            if (key == null)
                continue;
            if (key.contains(MOD_ID) && key.contains("trait."))
                visibleTrait = true;
            if (key.contains(MOD_ID) && key.endsWith(".long_desc"))
                visibleLongDesc = true;
            if (key.contains(MOD_ID) && key.contains("properties"))
                visibleProperties = true;
        }
        return new TooltipVisibility(visibleTrait, visibleLongDesc, visibleProperties);
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
        boolean hasEmptyAfter = lastSpecialIndex + 1 < lines.size()
                && lines.get(lastSpecialIndex + 1).getString().isEmpty();
        int nextNonEmptyIndex = lastSpecialIndex + 1;
        if (hasEmptyAfter)
            nextNonEmptyIndex++;
        boolean nextLineIsNormal = nextNonEmptyIndex < lines.size() && (getKey(lines.get(nextNonEmptyIndex)) == null
                || !isSpecialKey(getKey(lines.get(nextNonEmptyIndex))));
        if (!hasEmptyAfter && nextLineIsNormal) {
            lines.add(lastSpecialIndex + 1, Component.literal(""));
        }
    }

    private static void removeDuplicateTraits(List<Component> lines) {
        Map<String, Component> latestTraits = new LinkedHashMap<>();
        Map<String, Component> latestTraitDescs = new LinkedHashMap<>();
        for (Component line : lines) {
            String key = getKey(line);
            if (key == null || !key.contains(MOD_ID))
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
            if (key == null || !key.contains(MOD_ID))
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

        List<Component> normalLines = new ArrayList<>();
        List<Component> specialLines = new ArrayList<>();
        for (Component line : lines) {
            String key = getKey(line);
            if (key != null && key.contains(MOD_ID)
                    && (key.contains("trait.") || key.contains("properties")
                            || key.endsWith(".long_desc"))) {
                specialLines.add(line);
            } else {
                normalLines.add(line);
            }
        }
        if (specialLines.isEmpty()) {
            return;
        }

        List<List<Component>> sections = new ArrayList<>();
        List<Component> currentSection = new ArrayList<>();
        boolean inSection = false;
        for (Component line : specialLines) {
            ChatFormatting dominantColor = getDominantColor(line);
            boolean isGray = (dominantColor == ChatFormatting.GRAY);
            if (!isGray) {

                if (inSection && !currentSection.isEmpty()) {
                    sections.add(currentSection);
                }
                currentSection = new ArrayList<>();
                currentSection.add(line);
                inSection = true;
            } else if (inSection) {

                currentSection.add(line);
            } else {

                currentSection = new ArrayList<>();
                currentSection.add(line);
                inSection = true;
            }
        }
        if (inSection && !currentSection.isEmpty()) {
            sections.add(currentSection);
        }

        sections.sort((a, b) -> {
            int colorA = getTraitColor(a.get(0));
            int colorB = getTraitColor(b.get(0));
            int priorityA = getTraitPriority(colorA);
            int priorityB = getTraitPriority(colorB);
            return Integer.compare(priorityA, priorityB);
        });

        List<Component> orderedSpecials = new ArrayList<>();
        for (List<Component> section : sections) {
            orderedSpecials.addAll(section);
        }

        lines.clear();
        lines.addAll(normalLines);
        int insertIndex = 1;
        for (int i = 1; i < lines.size(); i++) {
            String key = getKey(lines.get(i));
            if (key != null && key.contains(MOD_ID)
                    && (key.contains("trait.") || key.contains("properties")
                            || key.endsWith(".long_desc"))) {
                insertIndex = i;
                break;
            }
        }
        lines.addAll(insertIndex, orderedSpecials);
    }

    private static int getTraitPriority(int colorValue) {
        if (colorValue == ChatFormatting.GOLD.getColor()) {
            return 1;
        }
        if (colorValue == ChatFormatting.YELLOW.getColor()) {
            return 2;
        }
        if (colorValue == ChatFormatting.RED.getColor()) {
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

    private static boolean isSpecialKey(String key) {
        return key.contains(MOD_ID)
                && (key.contains("trait.") || key.endsWith(".long_desc") || key.contains("properties"));
    }

    private record TooltipContent(boolean hasTrait, boolean hasLongDesc, boolean hasProperties) {
    }

    private record TooltipVisibility(boolean visibleTrait, boolean visibleLongDesc, boolean visibleProperties) {
    }
}
