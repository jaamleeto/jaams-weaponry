package net.jaams.weaponry.handler.event;

import org.apache.commons.lang3.math.NumberUtils;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.ChatFormatting;

import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.Iterator;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Arrays;
import java.util.ArrayList;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class AttributeTooltipHandler {
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onAttributeTooltip(ItemTooltipEvent e) {
        ItemStack itemStack = e.getItemStack();
        ResourceLocation registryName = ForgeRegistries.ITEMS.getKey(itemStack.getItem());
        
        if (ModList.get().isLoaded("epicfight")) {
            return;
        }
        var lines = e.getToolTip();
        Map<String, Map<String, Double>> attributesBySlot = new HashMap<>();
        String currentSlot = null;
        Map<String, Integer> attributeOrder = new HashMap<>();
        attributeOrder.put("attribute.name.generic.attack_damage", 1);
        attributeOrder.put("attribute.name.generic.attack_speed", 2);
        attributeOrder.put("attribute.name.generic.attack_knockback", 3);
        attributeOrder.put("forge.entity_reach", 4);
        attributeOrder.put("attribute.name.generic.armor", 5);
        attributeOrder.put("attribute.name.generic.armor_toughness", 6);
        attributeOrder.put("attribute.name.generic.max_health", 7);
        attributeOrder.put("attribute.name.generic.knockback_resistance", 8);
        attributeOrder.put("attribute.name.generic.movement_speed", 9);
        attributeOrder.put("attribute.name.generic.flying_speed", 10);
        attributeOrder.put("attribute.name.generic.jump_strength", 11);
        attributeOrder.put("attribute.name.generic.luck", 12);
        attributeOrder.put("attribute.name.generic.follow_range", 13);
        Set<String> greenAttributes = new HashSet<>(
                Arrays.asList("attribute.name.generic.attack_damage", "attribute.name.generic.attack_speed"));
        for (Iterator<Component> it = lines.iterator(); it.hasNext();) {
            var line = it.next();
            var content = line.getContents();
            if (content instanceof TranslatableContents ttc) {
                if (ttc.getKey().startsWith("item.modifiers")) {
                    currentSlot = ttc.getKey().substring(ttc.getKey().lastIndexOf('.') + 1);
                    attributesBySlot.putIfAbsent(currentSlot, new HashMap<>());
                } else if (ttc.getKey().startsWith("attribute.modifier.plus.0") && currentSlot != null) {
                    Object[] args = ttc.getArgs();
                    if (args.length < 2 || !NumberUtils.isCreatable(args[0].toString()))
                        continue;
                    var attrName = ((TranslatableContents) ((MutableComponent) args[1]).getContents()).getKey();
                    double value = Double.parseDouble(args[0].toString());
                    attributesBySlot.get(currentSlot).merge(attrName, value, Double::sum);
                    it.remove();
                } else if (ttc.getKey().startsWith("attribute.modifier.take.0") && currentSlot != null) {
                    Object[] args = ttc.getArgs();
                    if (args.length < 2 || !NumberUtils.isCreatable(args[0].toString()))
                        continue;
                    var attrName = ((TranslatableContents) ((MutableComponent) args[1]).getContents()).getKey();
                    double value = -Double.parseDouble(args[0].toString());
                    attributesBySlot.get(currentSlot).merge(attrName, value, Double::sum);
                    it.remove();
                } else if (ttc.getKey().startsWith("attribute.modifier.plus.1") && currentSlot != null) {
                    Object[] args = ttc.getArgs();
                    if (args.length < 2 || !NumberUtils.isCreatable(args[0].toString()))
                        continue;
                    var attrName = ((TranslatableContents) ((MutableComponent) args[1]).getContents()).getKey();
                    double value = Double.parseDouble(args[0].toString()); 
                    attributesBySlot.get(currentSlot).merge(attrName + "_percent", value, Double::sum); 
                                                                                                        
                                                                                                        
                                                                                                        
                    it.remove();
                } else if (ttc.getKey().startsWith("attribute.modifier.take.1") && currentSlot != null) {
                    Object[] args = ttc.getArgs();
                    if (args.length < 2 || !NumberUtils.isCreatable(args[0].toString()))
                        continue;
                    var attrName = ((TranslatableContents) ((MutableComponent) args[1]).getContents()).getKey();
                    double value = -Double.parseDouble(args[0].toString()); 
                    attributesBySlot.get(currentSlot).merge(attrName + "_percent", value, Double::sum); 
                                                                                                        
                                                                                                        
                                                                                                        
                    it.remove();
                }
            } else {
                for (var part : line.getSiblings()) {
                    if (part.getContents() instanceof TranslatableContents ttc
                            && ttc.getKey().startsWith("attribute.modifier.equals.0") && currentSlot != null) {
                        Object[] args = ttc.getArgs();
                        if (args.length < 2 || !NumberUtils.isCreatable(args[0].toString()))
                            continue;
                        var attrName = ((TranslatableContents) ((MutableComponent) args[1]).getContents()).getKey();
                        double value = Double.parseDouble(args[0].toString());
                        attributesBySlot.get(currentSlot).merge(attrName, value, Double::sum);
                        it.remove();
                    }
                }
            }
        }
        for (var slotEntry : attributesBySlot.entrySet()) {
            String slot = slotEntry.getKey();
            Map<String, Double> slotAttributesMap = slotEntry.getValue();
            List<Component> greenAttrs = new ArrayList<>();
            List<Component> blueAttrs = new ArrayList<>();
            List<Component> redAttrs = new ArrayList<>();
            int insertIndex = -1;
            for (int j = 0; j < lines.size(); j++) {
                var line = lines.get(j);
                if (line.getContents() instanceof TranslatableContents ttc
                        && ttc.getKey().equals("item.modifiers." + slot)) {
                    insertIndex = j;
                    break;
                }
            }
            if (insertIndex == -1)
                continue;
            for (var attrEntry : slotAttributesMap.entrySet()) {
                String attrKey = attrEntry.getKey();
                double value = attrEntry.getValue();
                boolean isPercent = attrKey.endsWith("_percent");
                String attrName = isPercent ? attrKey.substring(0, attrKey.length() - "_percent".length()) : attrKey;
                String formattedValue = formatNumber(Math.abs(value)) + (isPercent ? "%" : ""); 
                                                                                                
                Component newLine;
                if (!isPercent && greenAttributes.contains(attrName)) {
                    newLine = Component.literal(" ")
                            .append(Component.literal(formattedValue).withStyle(ChatFormatting.DARK_GREEN)).append(" ")
                            .append(Component.translatable(attrName).withStyle(ChatFormatting.DARK_GREEN));
                    greenAttrs.add(newLine);
                } else if (value >= 0) {
                    String prefix = isPercent ? "+" : "+";
                    newLine = Component.literal(prefix + formattedValue).withStyle(ChatFormatting.BLUE).append(" ")
                            .append(Component.translatable(attrName).withStyle(ChatFormatting.BLUE));
                    blueAttrs.add(newLine);
                } else {
                    newLine = Component.literal("-" + formattedValue).withStyle(ChatFormatting.RED).append(" ")
                            .append(Component.translatable(attrName).withStyle(ChatFormatting.RED));
                    redAttrs.add(newLine);
                }
            }
            List<Component> orderedGreenAttrs = sortAttributes(greenAttrs, attributeOrder);
            List<Component> orderedBlueAttrs = sortAttributes(blueAttrs, attributeOrder);
            List<Component> orderedRedAttrs = sortAttributes(redAttrs, attributeOrder);
            List<Component> orderedAttributes = new ArrayList<>();
            orderedAttributes.addAll(orderedGreenAttrs);
            orderedAttributes.addAll(orderedBlueAttrs);
            orderedAttributes.addAll(orderedRedAttrs);
            for (int k = 0; k < orderedAttributes.size(); k++) {
                lines.add(insertIndex + 1 + k, orderedAttributes.get(k));
            }
        }
    }

    private static List<Component> sortAttributes(List<Component> attributes, Map<String, Integer> order) {
        Map<String, Integer> dynamicOrder = new HashMap<>(order);
        int nextPriority = order.values().stream().max(Integer::compare).orElse(12) + 1;
        for (Component attr : attributes) {
            String attrName = extractAttributeNameFromComponent(attr);
            if (!dynamicOrder.containsKey(attrName)) {
                dynamicOrder.put(attrName, nextPriority++);
            }
        }
        attributes.sort((a, b) -> {
            String attrNameA = extractAttributeNameFromComponent(a);
            String attrNameB = extractAttributeNameFromComponent(b);
            boolean isPercentA = a.toString().contains("%");
            boolean isPercentB = b.toString().contains("%");
            if (isPercentA != isPercentB) {
                return isPercentA ? 1 : -1;
            }
            Integer priorityA = dynamicOrder.getOrDefault(attrNameA, Integer.MAX_VALUE);
            Integer priorityB = dynamicOrder.getOrDefault(attrNameB, Integer.MAX_VALUE);
            return priorityA.compareTo(priorityB);
        });
        return attributes;
    }

    private static String extractAttributeNameFromComponent(Component component) {
        for (Component sibling : component.getSiblings()) {
            if (sibling.getContents() instanceof TranslatableContents ttc) {
                return ttc.getKey();
            }
        }
        if (component.getContents() instanceof TranslatableContents ttc) {
            return ttc.getKey();
        }
        return component.toString();
    }

    private static String formatNumber(double value) {
        if (value % 1 == 0) {
            return String.format("%.0f", value);
        } else {
            String formatted = String.format("%.2f", value);
            while (formatted.endsWith("0") && formatted.contains(".") && !formatted.endsWith(".0")) {
                formatted = formatted.substring(0, formatted.length() - 1);
            }
            if (formatted.endsWith(".")) {
                formatted = formatted.substring(0, formatted.length() - 1);
            }
            return formatted;
        }
    }
}
